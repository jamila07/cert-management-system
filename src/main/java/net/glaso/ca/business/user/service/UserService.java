package net.glaso.ca.business.user.service;

import net.glaso.ca.business.common.CommonConstants;
import net.glaso.ca.business.common.mail.MailSender;
import net.glaso.ca.business.common.mail.MailService;
import net.glaso.ca.business.group.dao.GroupDao;
import net.glaso.ca.business.group.vo.GroupSolutionVo;
import net.glaso.ca.business.group.vo.GroupVo;
import net.glaso.ca.business.user.dao.UserDao;
import net.glaso.ca.business.user.vo.AppliedUserInfoVo;
import net.glaso.ca.business.user.vo.UserVo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class UserService {

	private final UserDao userDao;

	private final GroupDao groupDao;

	private final MailService mailService;

	@Autowired
	public UserService( UserDao userDao, GroupDao groupDao, MailService mailSevice ) {
		this.userDao = userDao;
		this.groupDao = groupDao;
		this.mailService = mailSevice;
	}

	public void registerAppliedUser( HttpServletRequest request ) throws NoSuchAlgorithmException {
		JSONObject body = (JSONObject) request.getAttribute( "body" );
		AppliedUserInfoVo vo = AppliedUserInfoVo.deserialize( body );
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		
		vo.setAddDate( new Date() );
		vo.setState( 0 );
		vo.setPassword( DatatypeConverter.printHexBinary( messageDigest.digest( vo.getPassword().getBytes() ) ) );

		userDao.insertAplliedUser( vo );
		sendMail( vo );
	}

	private void sendMail( AppliedUserInfoVo vo ) {
		String text = new StringBuffer( "회원 가입 신청 메일이 왔습니다. 수락해주시기 바랍니다 \n\n" )
				.append( "ID : [ " )
				.append( vo.getUserId() )
				.append( " ] \n")
				.append( "이름 : [ " )
				.append( vo.getName() )
				.append( " ] \n\n")
				.append( "감사합니다.").toString();

		mailService.sendMail( new MailSender(), "[certGenerator] 회원 가입 요청 ", text, "ehdvudee" );
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
}
