package net.glaso.ca.business.user.service;

import net.glaso.ca.business.admin.service.AdminService;
import net.glaso.ca.business.common.CommonConstants;
import net.glaso.ca.business.common.mail.MailSender;
import net.glaso.ca.business.common.mail.MailService;
import net.glaso.ca.business.group.dao.GroupDao;
import net.glaso.ca.business.group.vo.GroupSolutionVo;
import net.glaso.ca.business.group.vo.GroupVo;
import net.glaso.ca.business.user.dao.UserDao;
import net.glaso.ca.business.user.vo.AppliedUserInfoVo;
import net.glaso.ca.business.user.vo.AppliedUserMailVo;
import net.glaso.ca.business.user.vo.UserVo;
import net.glaso.ca.framework.utils.CaUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@Service
public class UserService {
	private final AdminService adminService;

	private final UserDao userDao;

	private final GroupDao groupDao;

	private final MailService mailService;

	@Autowired
	public UserService( AdminService adminService, UserDao userDao, GroupDao groupDao, MailService mailSevice ) {
		this.adminService = adminService;
		this.userDao = userDao;
		this.groupDao = groupDao;
		this.mailService = mailSevice;
	}

	@Transactional(rollbackFor={Exception.class})
	public void registerAppliedUser( HttpServletRequest request ) throws NoSuchAlgorithmException {
		JSONObject body = (JSONObject) request.getAttribute( "body" );
		AppliedUserInfoVo vo = AppliedUserInfoVo.deserialize( body );
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		
		vo.setAddDate( new Date() );
		vo.setState( 0 );
		vo.setPassword( DatatypeConverter.printHexBinary( messageDigest.digest( vo.getPassword().getBytes() ) ) );

		userDao.insertAplliedUser( vo );

		AppliedUserMailVo appliedUserMailVo = generateAuthMail( vo );
		userDao.insertAppliedUserMail( appliedUserMailVo );

		sendMailToAdmin( vo );
		sendMailToUser( request, appliedUserMailVo, vo.geteMail() );
	}

	public UserVo selectOneUser(UserVo vo ) {
		return userDao.selectOneUser( vo );
	}
	
	public List<Map<String, Object>> showList( HttpServletRequest request ) {
		List<Map<String, Object>> voMapList = userDao.selectUserList();
		
		for ( Map<String, Object> voMap : voMapList ) {
			voMap.put( "adddate", CommonConstants.dateFormat.format( (Date)voMap.get( "adddate" ) ) );
		}
		
		return voMapList;
	}
	
	public boolean chkOverlapUser( String userId ) {
		UserVo vo = new UserVo();
		
		vo.setId( userId );
		
		if ( userDao.selectUserOne4ChkOverlap( vo ) != null ) return false;
		if ( userDao.selectAppliedUserOne4ChkOverlap( vo ) != null ) return false;
		
		return true;
	}
	
	public Map<Integer, Map<String, List<String>>> showJoinedGroup( HttpServletRequest request, String userId ) {
		List<GroupVo> groupVoList = groupDao.selectJoinedGroupList( userId );
		
//		"data":{
//			1:{	"dev2":["magickms","magicdb"]},
//			2:{ "dev1":["sso", "tsa"]}
//		}
		Map<Integer, Map<String, List<String>>> groupInfo = new HashMap<>();
		Map<String, List<String>> solutionInfo;
		for ( GroupVo groupVo : groupVoList ) {
			solutionInfo = new HashMap<>();
			
			for ( GroupSolutionVo groupSoltuionVo : groupVo.getGroupSolutionVo() ) {
				if ( groupInfo.get( groupVo.getId() ) == null ) {
					List<String> solutionNameList = new ArrayList<>();
					solutionNameList.add( groupSoltuionVo.getSolutionName() );
					
					solutionInfo.put( groupVo.getName(), solutionNameList );
					groupInfo.put( groupVo.getId(), solutionInfo );
				} else {
					groupInfo.get( groupVo.getId() ).get( groupVo.getName() ).add( groupSoltuionVo.getSolutionName() );
				}
			}
		}
		
		return groupInfo;
	}

	@Transactional(rollbackFor={Exception.class})
	public String isUserAuthenticated( String authVal ) throws IllegalAccessException, SignatureException, NoSuchAlgorithmException, IOException, CertificateException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException {
		AppliedUserInfoVo appliedUserInfoVo = userDao.selectAppliedUserMailUsingAuthVal( authVal );
		if ( appliedUserInfoVo != null  ) {
			appliedUserInfoVo.getAppliedUserMailVo().setActivatedState( 0 );
			userDao.updateAppliedUserMailActState( appliedUserInfoVo.getAppliedUserMailVo() );

			if ( appliedUserInfoVo.getState() == 3 ) {
				adminService.registerUser( appliedUserInfoVo );

				return "인증이 완료되었습니다. 회원 가입 축하드립니다.";
			}

			return "인증이 완료되었습니다. 관리자 승인 후 회원 가입이 완료됩니다.";
		} else return null;
	}

	private AppliedUserMailVo generateAuthMail( AppliedUserInfoVo appliedUserInfoVo ) throws NoSuchAlgorithmException {
		AppliedUserMailVo vo = new AppliedUserMailVo();

		byte[] bytes = CaUtils.generateSecureRandomString( 128 );

		vo.setAppliedUserInfoSeqId( appliedUserInfoVo.getSeqId() );
		vo.setSendDate( new Date() );
		vo.setExpiredDate( new Date( vo.getSendDate().getTime() +  172800000L/* 60 * 60 * 24 * 2 * 1000 */ ) );
		vo.setActivatedState( 1 );
		vo.setState( 0 );
		vo.setAuthUri( DatatypeConverter.printHexBinary( bytes ) );

		return vo;
	}

	private void sendMailToUser( HttpServletRequest request, AppliedUserMailVo vo, String recepient ) {
		String title = "[certGenerator] 회원가입 이메일 인증을 진행합니다.";
		String text = new StringBuilder( "안녕하세요. CertGenerator 입니다. <br><br>" )
				.append( "회원가입을 위해 이메일 인증을 진행합니다.<br>" )
				.append( "회원가입 완료를 위해 이메일 인증을 꼭 받아주시기 바랍니다.<br>" )
				.append( "아래의 링크를 클릭하세요.<br>" )
				.append( "<a href=\'http://" )
				.append( request.getServerName() )
				.append( ":" )
				.append( request.getServerPort() )
				.append( "/user/mail/" )
				.append( vo.getAuthUri() )
				.append( "\'> 클릭하기 </a><br><br>")
				.append( "감사합니다." ).toString();

		mailService.sendMail( new MailSender(), title, text, recepient );

	}

	private void sendMailToAdmin( AppliedUserInfoVo vo ) {
		String title = "[certGenerator] 회원 가입 요청 ";
		String recipient = "ehdvudee@naver.com";
		String text = new StringBuilder( "회원 가입 신청 메일이 왔습니다. 수락해주시기 바랍니다 <br><br>" )
				.append( "ID : [ " )
				.append( vo.getUserId() )
				.append( " ] <br>")
				.append( "이름 : [ " )
				.append( vo.getName() )
				.append( " ] <br><br>")
				.append( "감사합니다.").toString();

		mailService.sendMail( new MailSender(), title, text, recipient );
	}

}
