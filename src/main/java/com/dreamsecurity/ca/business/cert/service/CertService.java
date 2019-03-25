package com.dreamsecurity.ca.business.cert.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dreamsecurity.ca.business.cert.common.CertConstants;
import com.dreamsecurity.ca.business.cert.dao.CertDao;
import com.dreamsecurity.ca.business.cert.vo.CertVo;
import com.dreamsecurity.ca.business.cert.vo.KeyVo;
import com.dreamsecurity.ca.business.common.CommonConstants;
import com.dreamsecurity.ca.business.login.common.LoginConstants;
import com.dreamsecurity.ca.framework.cert.CertGeneratorFactory;
import com.dreamsecurity.ca.framework.cert.KmsTrustManagerFactory;
import com.dreamsecurity.ca.framework.cert.UserCertGenerator;
import com.dreamsecurity.ca.framework.init.CaSettings;
import com.dreamsecurity.ca.framework.utils.CaUtils;
import com.dreamsecurity.ca.framework.utils.PemUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sun.security.x509.KeyIdentifier;

@Service
public class CertService {
	
	@Resource
	private CertDao certDao;
	
	public String downloadCertPemFile( int certId, HttpServletRequest request ) {
		CertVo vo = selectCertUsingCertId( certId, request );
		return DatatypeConverter.printBase64Binary( PemUtil.certToPem( vo.getFile() ).getBytes() );
	}
	
	public String downloadCertBinaryFile( int certId, HttpServletRequest request ) throws IOException {
		CertVo vo = selectCertUsingCertId( certId, request );
		return DatatypeConverter.printBase64Binary( vo.getFile() );
	}
	
	private CertVo selectCertUsingCertId( int certId, HttpServletRequest request ) {
		CertVo vo = new CertVo();
		vo.setId( certId );
		vo = certDao.selectCertBinary( vo );
		
		return vo;
	}
	
	public String downloadPkcs8KeyFile( int certId, HttpServletRequest request ) {
		KeyVo kVo = selectCertPrivateKeyUsingCertId( certId, request );
		
		return DatatypeConverter.printBase64Binary( kVo.getPrivateKey() );
	}
	
	public String downloadPkcs8PemFile( int certId, HttpServletRequest request ) {
		KeyVo kVo = selectCertPrivateKeyUsingCertId( certId, request );
		
		return DatatypeConverter.printBase64Binary( PemUtil.pkcs8ToPem( kVo.getPrivateKey() ).getBytes() );
	}
	
	private KeyVo selectCertPrivateKeyUsingCertId( int certId, HttpServletRequest request ) {
		String userId = request.getSession().getAttribute( LoginConstants.SESSION_ID ).toString();
		CertVo cVo = new CertVo();
		
		cVo.setId( certId );
		cVo.setSubject( userId );
		
		if ( certDao.selectCertUsingSubject( cVo ) == null ) 
			throw new IllegalAccessError( "there is no authorization for download it.");
		
		KeyVo kVo = certDao.selectKeyBinary( cVo );
		
		return kVo;
	}
	
	public boolean verifyCertificate( byte[] cert ) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CertPathBuilderException {
		CertificateFactory certFactory = CertificateFactory.getInstance( "X.509" );
		X509Certificate eeCert = (X509Certificate) certFactory.generateCertificate( new ByteArrayInputStream( cert ) );
		
		
		CertVo eeVo = new CertVo();
		List<CertVo> rootInterVoList = null;
		
		eeVo.setSubjectDn( eeCert.getSubjectDN().getName().getBytes() );
		
		eeVo = certDao.selectCertInfoOneUsingDN( eeVo );
		
		if ( eeVo != null ) {
			rootInterVoList = certDao.selectRootInterCertUsingOuType( eeVo );
		} else {
			// throw Exception 없음요
			System.out.println( " throw exception 처리 해야함 null 요맨" );
		}
		
		Set<X509Certificate> certChain = new HashSet<>();
		for ( int i=0; i<rootInterVoList.size(); i++ ) {
			certChain.add( (X509Certificate) certFactory.generateCertificate( new ByteArrayInputStream( rootInterVoList.get( i ).getFile() ) ) ) ;
		}
		
		KmsTrustManagerFactory.verifyCertificate( eeCert, certChain, false );
		
		return true;
	}
	
	public List<Map<String, Object>> showList( HttpServletRequest request ) {
		HttpSession session = request.getSession();
		String sessionId = session.getAttribute( LoginConstants.SESSION_ID ).toString();
		
		List<Map<String,Object>> voMapList = certDao.selectCertList( sessionId );
		
		for ( Map<String, Object> voMap : voMapList ) {
			voMap.put( "subjectdn", new String( (byte[]) voMap.get( "subjectdn" ) ) );
			voMap.put( "enddate", CommonConstants.dateFormat.format( (Date)voMap.get( "enddate" ) ) );
			voMap.put( "startdate", CommonConstants.dateFormat.format( (Date)voMap.get( "startdate" ) ) );
		}
		
		return voMapList;
	}
	
	public CertVo mappingObject( HttpServletRequest request ) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		String body = null;
		
		if ( request.getAttribute( "body" ) != null ) {
			body = request.getAttribute( "body" ).toString();
		}
		
		return mapper.readValue( body, CertVo.class );
	}
	
	@Transactional(rollbackFor={Exception.class})
	public void register( HttpServletRequest request, CertVo vo ) throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IllegalAccessException, IOException, InvalidKeySpecException {
		HttpSession session = request.getSession();
		vo.setSubject( session.getAttribute( LoginConstants.SESSION_ID ).toString() );

		this.registerEeCert( vo );
	}
		
	private synchronized X509Certificate registerEeCert( CertVo certVo ) throws NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, InvalidKeyException, NoSuchProviderException, SignatureException, IOException, IllegalAccessException {
		KeyPairGenerator keyPGen = KeyPairGenerator.getInstance( CertConstants.KEY_ALGORITHM );
		keyPGen.initialize( 2048 );
		KeyPair eeCertKeyPair = keyPGen.generateKeyPair();
		String dn = new StringBuilder().append( "CN = " ).append( certVo.getCiteDomain() ) // domain 여러개 올 떄 CN 처리 어떻게 할지 2.7 ehdvudee
				.append( " O = " ).append( certVo.getCiteName() )
				.append( " L = " ).append( certVo.getCiteLocality() )
				.append( " S = " ).append( certVo.getCiteProvince() )
				.append( " C = " ).append( CaSettings.getInstance().get( "country" ) ).toString();
		
		CertVo intermediateCaVo = certDao.selectIntermediateCertAndKeyInfoOne( certVo ); 
		
		if ( intermediateCaVo == null ) {
			throw new IllegalAccessException( certVo.getOuType() + " 's IntermediateCa is not found." ); 
		}
		
		List<String> domainList = new ArrayList<>();
		domainList.add( certVo.getCiteDomain() );
		
		certVo.setIssuer( intermediateCaVo.getSubject() );
		X509Certificate ca = CaUtils.bytesToX509Cert( intermediateCaVo.getFile() );
		PrivateKey caPriKey = CaUtils.pkcs8bytesToPrivateKeyObj( intermediateCaVo.getKeyVo().getPrivateKey() );
		BigInteger serialNumber = BigInteger.valueOf( certDao.selectCertSerialNumber( certVo ) + 1);
		
		X509Certificate eeCert = CertGeneratorFactory.building()
				.subject( eeCertKeyPair )
				.validity( Integer.parseInt( CaSettings.getInstance().get( "entityCertValidity" ) ) )
				.algorithm( CertConstants.CERT_ALGORITHM )
				.issuer( caPriKey )
				.issuerCert( ca )
				.dn( dn )
				.serialNumber( serialNumber )
				.subjectAltName( domainList )
				.type( UserCertGenerator.class )
				.build()
				.generateCertificate();
		
		certVo.setIssuer( intermediateCaVo.getSubject() );
		certVo = setKeyAndCertVo( certVo, eeCertKeyPair, serialNumber, eeCert );
		certVo.setOuType( certVo.getGroupName() );
		
		certDao.insertKeyInfo( certVo.getKeyVo() );
		certVo.setKeyId( certVo.getKeyVo().getId() );
		certDao.insertCertInfo( certVo );
		
		return eeCert;
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
}	
