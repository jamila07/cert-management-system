package net.glaso.ca.business.admin.service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.glaso.ca.business.group.dao.GroupDao;
import net.glaso.ca.business.group.vo.GroupSolutionVo;
import net.glaso.ca.business.group.vo.GroupVo;
import net.glaso.ca.business.group.vo.UserGroupVo;
import net.glaso.ca.business.user.vo.AppliedUserInfoVo;
import net.glaso.ca.business.user.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.glaso.ca.business.cert.common.CertConstants;
import net.glaso.ca.business.cert.dao.CertDao;
import net.glaso.ca.business.cert.vo.CertVo;
import net.glaso.ca.business.cert.vo.KeyVo;
import net.glaso.ca.business.common.CommonConstants;
import net.glaso.ca.business.common.domain.Criteria;
import net.glaso.ca.business.login.common.LoginConstants;
import net.glaso.ca.business.user.dao.UserDao;
import net.glaso.ca.framework.cert.CaCertGenerator;
import net.glaso.ca.framework.cert.CertGeneratorFactory;
import net.glaso.ca.framework.cert.RootCertGenerator;
import net.glaso.ca.framework.init.CaSettings;
import net.glaso.ca.framework.utils.CaUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import sun.security.x509.KeyIdentifier;

@Service
public class AdminService {

	private final UserDao userDao;

	private final GroupDao groupDao;

	private final CertDao certDao;

	@Autowired
	public AdminService( UserDao userDao, GroupDao groupDao, CertDao certDao ) {
		this.userDao = userDao;
		this.groupDao = groupDao;
		this.certDao = certDao;
	}

	@Transactional(rollbackFor={Exception.class})
	public void registerUser( int seqId ) throws IOException, NoSuchAlgorithmException, IllegalAccessException, InvalidKeySpecException, InvalidKeyException, NumberFormatException, CertificateException, NoSuchProviderException, SignatureException {
		AppliedUserInfoVo appliedUserVo = userDao.selectAppliedUserInfoOne( seqId );

		if ( appliedUserVo.getGroupCreator() && appliedUserVo.getGroupId() ==0 ) {
			// 그룹장 트렌젝션
			UserVo userVo = addUser( appliedUserVo );

			GroupVo groupVo = insertMasterGroupInfo( appliedUserVo, userVo );

			registerIntermediateCa( userVo, groupVo, appliedUserVo.getDepartTeam(), appliedUserVo.getSolutionName() );

		} else if ( !appliedUserVo.getGroupCreator() && appliedUserVo.getGroupId() > 0 ) {
			// 그룹원 트렌젝션
			UserVo userVo = addUser( appliedUserVo );

			UserGroupVo userGroupVo = new UserGroupVo();

			userGroupVo.setUserId( userVo.getId() );
			userGroupVo.setGroupId( appliedUserVo.getGroupId() );
			userGroupVo.setJoinDate( new Date() );
			userGroupVo.setUserAuthority( 2 );
			userGroupVo.setState( 0 );

			groupDao.addUserToGroup( userGroupVo );

		} else if ( !appliedUserVo.getGroupCreator() && appliedUserVo.getGroupId() == 0 ) {
			// 일반 트렌젝션			
			addUser( appliedUserVo );
		}
	}

	@Transactional(rollbackFor={Exception.class})
	public void registerRootCa( HttpServletRequest request ) throws IOException, IllegalAccessException, NoSuchAlgorithmException, InvalidKeyException, CertificateException, NoSuchProviderException, SignatureException {
		CertVo certVo = new CertVo();
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance( CertConstants.KEY_ALGORITHM );
		keyPairGenerator.initialize( 2048 );

		KeyPair rootKeyPair = keyPairGenerator.generateKeyPair();
		String dn = new StringBuilder().append( "CN = " ).append( CaSettings.getInstance().get( "rootCaCn" ) )
				.append( " OU = " ).append( CaSettings.getInstance().get( "rootCaOrganizationUnit" ) )
				.append( " O = " ).append( CaSettings.getInstance().get( "organization" ) )
				.append( " C = " ).append( CaSettings.getInstance().get( "country" ) ).toString();

		if ( !certDao.hasRootCa() ) {
			throw new IllegalAccessException( "Server already has RootCA.");
		}

		BigInteger serialNumber = BigInteger.valueOf( certDao.selectCertSerialNumber( certVo ) + 1);

		X509Certificate rootCert = CertGeneratorFactory.building()
				.subject( rootKeyPair )
				.validity( Integer.parseInt( CaSettings.getInstance().get( "rootCertValidity" ) ) )
				.algorithm( CertConstants.CERT_ALGORITHM )
				.issuer( rootKeyPair.getPrivate() )
				.dn( dn )
				.serialNumber( serialNumber )
				.type( RootCertGenerator.class )
				.build()
				.generateCertificate();

		HttpSession session = request.getSession();
		certVo.setSubject( session.getAttribute( LoginConstants.SESSION_ID ).toString() );
		certVo.setIssuer( certVo.getSubject() );
		certVo = setKeyAndCertVo( certVo, rootKeyPair, serialNumber, rootCert );

		certDao.insertKeyInfo( certVo.getKeyVo() );
		certVo.setKeyId( certVo.getKeyVo().getId() );
		certDao.insertCertInfo( certVo );
	}

	public List<Map<String, Object>> showAppliedUserList( Criteria cri ) {
		List<Map<String,Object>> voMapList = userDao.selectAppliedUserList( cri );

		for ( Map<String, Object> voMap : voMapList ) {
			voMap.put( "adddate", CommonConstants.dateFormat.format( (Date)voMap.get( "adddate" ) ) );

			boolean gCreator = (boolean) voMap.get("groupcreator");
			int groupId = (int) voMap.get( "groupid" );

			if ( gCreator && groupId == 0 ) {
				voMap.put( "reqtype", "그룹장" );
			} else if ( !gCreator &&  groupId > 0 ) {
				voMap.put( "reqtype", "그룹원" );
			} else if ( !gCreator && groupId == 0 ) {
				voMap.put( "reqtype", "일반" );
			}
		}

		return voMapList;
	}

	public int showAppliedUserListCnt() {
		return userDao.selectAppliedUserListCnt();
	}

	public List<Map<String, Object>> showGroupApplyList( Criteria cri ) {
		List<Map<String,Object>> voMapList = groupDao.selectGroupApplyList( cri );

		for ( Map<String, Object> voMap : voMapList ) {
			voMap.put( "createdate", CommonConstants.dateFormat.format( (Date)voMap.get( "createdate" ) ) );
		}

		return voMapList;
	}

	public int showGroupApplyListCnt() {
		return groupDao.selectGroupApplyListCnt();
	}

	public List<Map<String, Object>> showGroupSolutionApplyList( Criteria cri ) {
		List<Map<String,Object>> voMapList = groupDao.selectGroupSolutionApplyList( cri );

		for ( Map<String, Object> voMap : voMapList ) {
			voMap.put( "createdate", CommonConstants.dateFormat.format( (Date)voMap.get( "createdate" ) ) );
		}

		return voMapList;
	}

	public int showGroupSolutionApplyListCnt() {
		return groupDao.selectGroupSolutionApplyListCnt();
	}

	@Transactional(rollbackFor={Exception.class})
	public void registerGroup( int seqId ) throws InvalidKeyException, NumberFormatException, IllegalAccessException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, NoSuchProviderException, SignatureException, IOException {
		UserGroupVo uGroupVo = new UserGroupVo();
		GroupVo groupVo = new GroupVo();
		GroupSolutionVo gSolutionVo = new GroupSolutionVo();

		uGroupVo.setGroupId( seqId );
		groupVo.setId( seqId );
		gSolutionVo.setGroupId( seqId );

		uGroupVo.setState( 1 );
		groupVo.setState( 0 );
		gSolutionVo.setState( 0 );

		groupDao.updateUserStateUsingGroupId( uGroupVo );
		groupDao.updateGroupStateUsingGroupId( groupVo );
		groupDao.updateGroupSolutionStateUsingGroupId( gSolutionVo );

		groupVo = groupDao.selectUserSolutionJoinUsingGroupId( groupVo );

		registerIntermediateCa( groupVo.getUserVo(), groupVo, groupVo.getUserVo().getDepartTeam(), groupVo.getGroupSolutionVo().get( 0 ).getSolutionName() );
	}

	@Transactional(rollbackFor={Exception.class})
	public void registerSolution( int seqId ) throws InvalidKeyException, NumberFormatException, IllegalAccessException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, NoSuchProviderException, SignatureException, IOException {
		GroupSolutionVo gSolutionVo = new GroupSolutionVo();

		gSolutionVo.setSeqId( seqId );
		gSolutionVo.setState( 0 );

		groupDao.updateGroupSolutionStateUsingSolutionId( gSolutionVo );

		GroupVo groupVo = groupDao.selectGroupSolutionJoinGroupUsingSolId( gSolutionVo );

		UserVo userVo = new UserVo();
		userVo.setLoginId( groupVo.getGroupSolutionVo().get(0).getCreator() );
		groupVo.setUserVo( userDao.selectOneUser( userVo ) );

		registerIntermediateCa( groupVo.getUserVo(), groupVo, groupVo.getUserVo().getDepartTeam(), groupVo.getGroupSolutionVo().get( 0 ).getSolutionName() );
	}

	public void refuseAppliedUser( int seqId ) {
		AppliedUserInfoVo vo = new AppliedUserInfoVo();

		vo.setSeqId( seqId );
		vo.setState( 2 );

		userDao.updateAppliedUserState( vo );
	}

	@Transactional(rollbackFor={Exception.class})
	public void refuseAppliedGroup( HttpServletRequest request, int seqId ) {

	}

	public void refuseAppliedSolution( HttpServletRequest request, int seqId ) {

	}

	private UserVo addUser( AppliedUserInfoVo appliedUserVo ) {
		UserVo userVo = new UserVo();

		userVo.setId( appliedUserVo.getUserId() );
		userVo.setName( appliedUserVo.getName() );
		userVo.setAddDate( new Date() );
		userVo.setDepartTeam( appliedUserVo.getDepartTeam() );
		userVo.setJobLevel( appliedUserVo.getJobLevel() );
		userVo.seteMail( appliedUserVo.geteMail() );
		userVo.setPassword( appliedUserVo.getPassword() );
		userVo.setState( 1 );

		appliedUserVo.setState( 1 );

		userDao.insertUser( userVo );
		userDao.updateAppliedUserState( appliedUserVo );

		return userVo;
	}

	private GroupVo insertMasterGroupInfo( AppliedUserInfoVo appliedUserVo, UserVo userVo ) {
		GroupVo groupVo = new GroupVo();
		Date currentTime = new Date();
		groupVo.setName( appliedUserVo.getGroupName() );
		groupVo.setCreateDate( currentTime );
		groupVo.setCreator( appliedUserVo.getUserId() );
		groupVo.setState( 0 );
		groupVo.setDescription( appliedUserVo.getGroupDescription() );

		groupDao.registerGroup( groupVo );

		GroupSolutionVo groupSolutionVo = new GroupSolutionVo();

		groupSolutionVo.setGroupId( groupVo.getId() );
		groupSolutionVo.setSolutionName( appliedUserVo.getSolutionName() );
		groupSolutionVo.setCreateDate( currentTime );
		groupSolutionVo.setCreator( appliedUserVo.getUserId() );
		groupSolutionVo.setState( 0 );

		groupDao.insertGroupSolution( groupSolutionVo );

		UserGroupVo userGroupVo = new UserGroupVo();

		userGroupVo.setUserId( userVo.getId() );
		userGroupVo.setGroupId( groupVo.getId() );
		userGroupVo.setJoinDate( new Date() );
		userGroupVo.setUserAuthority( 0 );
		userGroupVo.setState( 1 );

		groupDao.insertUserToGroup( userGroupVo );

		return groupVo;
	}

	private CertVo setKeyAndCertVo( CertVo certVo, KeyPair keyPair, BigInteger serialNumber, X509Certificate cert ) throws CertificateEncodingException, IOException {
		KeyVo keyVo = setKeyVo( new KeyVo(), keyPair, cert );

		certVo.setSerialNumber( serialNumber.intValue() );
		certVo.setFile( cert.getEncoded() );
		certVo.setIssuingRequestDate( new Date() );
		certVo.setStartDate( cert.getNotBefore() );
		certVo.setEndDate( cert.getNotAfter() );
		certVo.setSubjectDn( cert.getSubjectDN().getName().getBytes() );
		certVo.setKeyVo( keyVo );

		return certVo;
	}

	private KeyVo setKeyVo( KeyVo vo, KeyPair keyPair, X509Certificate cert ) throws IOException {
		vo.setPrivateKey( keyPair.getPrivate().getEncoded() );
		vo.setPublicKey( keyPair.getPublic().getEncoded() );
		vo.setPublicKeyIdentifier( new KeyIdentifier( cert.getPublicKey() ).getIdentifier() );

		return vo;
	}

	private synchronized void registerIntermediateCa( UserVo userVo, GroupVo groupVo, String departTeam, String solutionName ) throws IllegalAccessException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, InvalidKeyException, NumberFormatException, NoSuchProviderException, SignatureException, IOException {
		CertVo certVo = new CertVo();
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance( CertConstants.KEY_ALGORITHM );

		keyPairGenerator.initialize( 2048 );
		KeyPair caKeyPair = keyPairGenerator.generateKeyPair();
		String dn = new StringBuilder().append( "CN = " ).append( departTeam )
				.append( " OU = " ).append( solutionName )
				.append( " O = " ).append( CaSettings.getInstance().get( "organization" ) )
				.append( " C = " ).append( CaSettings.getInstance().get( "country" ) ).toString();

		CertVo rootCaVo = certDao.selectRootCertAndKeyInfoOne();

		if ( rootCaVo == null )
			throw new IllegalAccessException( "RootCA is not found");

		certVo.setType( 1 );
		certVo.setIssuer( rootCaVo.getSubject() );

		X509Certificate rootCert = CaUtils.bytesToX509Cert( rootCaVo.getFile() );
		PrivateKey rootPriKey = CaUtils.pkcs8bytesToPrivateKeyObj( rootCaVo.getKeyVo().getPrivateKey() );

		BigInteger serialNumber = BigInteger.valueOf( certDao.selectCertSerialNumber( certVo ) + 1 );

		X509Certificate intermediateCa = CertGeneratorFactory.building()
				.subject( caKeyPair )
				.validity( Integer.parseInt( CaSettings.getInstance().get( "interCertValidity" ) ) )
				.algorithm( CertConstants.CERT_ALGORITHM )
				.issuer( rootPriKey )
				.issuerCert( rootCert )
				.dn( dn )
				.serialNumber( serialNumber )
				.type( CaCertGenerator.class )
				.build()
				.generateCertificate();

		certVo.setSubject( userVo.getId() );
		certVo.setIssuer( rootCaVo.getSubject() );
		certVo.setOuType( groupVo.getId() );
		certVo = setKeyAndCertVo( certVo, caKeyPair, serialNumber, intermediateCa );

		certDao.insertKeyInfo( certVo.getKeyVo() );
		certVo.setKeyId( certVo.getKeyVo().getId() );
		certDao.insertCertInfo( certVo );
	}
}