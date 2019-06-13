package com.dreamsecurity.ca.business.cert.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
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
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dreamsecurity.ca.business.cert.common.CertConstants;
import com.dreamsecurity.ca.business.cert.dao.CertDao;
import com.dreamsecurity.ca.business.cert.dto.CertDto;
import com.dreamsecurity.ca.business.cert.vo.CertVo;
import com.dreamsecurity.ca.business.cert.vo.KeyVo;
import com.dreamsecurity.ca.business.common.CommonConstants;
import com.dreamsecurity.ca.business.login.common.LoginConstants;
import com.dreamsecurity.ca.framework.cert.CertGeneratorFactory;
import com.dreamsecurity.ca.framework.cert.KmsTrustManagerFactory;
import com.dreamsecurity.ca.framework.cert.Pkcs12Creator;
import com.dreamsecurity.ca.framework.cert.UserCertGenerator;
import com.dreamsecurity.ca.framework.init.CaSettings;
import com.dreamsecurity.ca.framework.utils.CaUtils;
import com.dreamsecurity.ca.framework.utils.KeyStoreUtil;
import com.dreamsecurity.ca.framework.utils.PemUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import sun.security.x509.KeyIdentifier;

@Service
public class CertService {

	private static final Logger logger = Logger.getLogger( CertService.class );

	@Resource
	private CertDao certDao;

	public String downloadCert( HttpServletRequest request, int certId ) throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, JSONException, KeyStoreException, IOException {
		JSONObject body = (JSONObject) request.getAttribute( "body" );

		if ( !body.has( "exportWhat" ) ) throw new IllegalArgumentException( "exportWhat is null." );
		int exportWhat = body.getInt( "exportWhat" );

		if ( exportWhat == 1 /*KeyPair*/ ) {
			return DatatypeConverter.printBase64Binary( exportKeyPair( request, body, certId ) );
		} else if ( exportWhat == 2 /*PrivateKey*/ ) {
			return DatatypeConverter.printBase64Binary( exportPrivateKey( request, body, certId ) );
		} else if ( exportWhat == 3 /*PublicKey*/ ) {
			return DatatypeConverter.printBase64Binary( exportPublicKey( body, certId ) );
		} else if ( exportWhat == 4 /*CertChain*/ ) {
			return DatatypeConverter.printBase64Binary( exportCertChain( request, body, certId ) );
		}

		throw new IllegalArgumentException( "exportWhat value is null." );
	}

	private byte[] exportKeyPair( HttpServletRequest request, JSONObject body, int certId ) throws CertificateException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, JSONException, IOException, KeyStoreException{

		if ( !body.has( "exportType") ) throw new IllegalArgumentException( "exportType is null. " );

		KeyVo kVo = selectCertKeyPairUsingCertId( certId, request );
		CertDto cDto = getCertChain( certId );

		byte[] retKey;
		boolean isEncrypted = false;
		if ( !body.has( "password" ) || body.getString( "password" ).equals( "" ) || body.getInt( "exportType" ) == 3 )
			retKey = kVo.getPrivateKey();
		else {
			retKey = Pkcs12Creator.enc( kVo.getPrivateKey(), body.getString( "password" ) );
			isEncrypted = true;
		}

		if ( body.getInt( "exportType" ) == 1 /*PKCS12*/ ) {
			logger.info( "export PKCS Key Pair");

			String pin = "";
			if ( body.has( "pin" ) || !body.getString( "pin" ).equals( "" ) )
				pin = body.getString( "pin" );

			return Pkcs12Creator.generatePkcs12( pin, cDto.getEeCertVo().getSubject(), retKey, cDto.getCertChain() );

		} else if ( body.getInt("exportType" ) == 3 /*JKS*/ ) {
			logger.info( "export JKS Key Pair");

			String pin = "";
			if ( body.has( "pin" ) || !body.getString( "pin" ).equals( "" ) )
				pin = body.getString( "pin" );

			KeyStoreUtil kStore = KeyStoreUtil.getInstance( null, null );
			kStore.setKeyPair( retKey,
					kVo.getPublicKey(),
					cDto.getCertChain(),
					"RSA",
					cDto.getEeCertVo().getSubject(),
					body.has( "password") ? body.getString( "password" ).toCharArray() : "".toCharArray() );

			return kStore.convertKeyStoreToByteArray( pin.toCharArray() );

		} else if ( body.getInt( "exportType" ) == 2 /*PEM*/ ) {
			logger.info( "export PEM Key Pair");

			StringBuilder pemBuilder;

			pemBuilder = getPrivateKeyPem( retKey, isEncrypted );
			for ( X509Certificate cert : cDto.getCertChain() ) pemBuilder.append( PemUtil.certToPem( cert.getEncoded() ) );

			return pemBuilder.toString().getBytes();

		} else {
			throw new IllegalArgumentException( "export type value is invalid." );
		}
	}

	private byte[] exportPrivateKey( HttpServletRequest request, JSONObject body, int certId ) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, JSONException, IOException {

		if ( !body.has( "isPem" ) ) throw new IllegalArgumentException( "isPem is null." );

		KeyVo kVo = selectCertKeyPairUsingCertId( certId, request );
		byte[] retKey;
		boolean isEncrypted = false;


		if ( !body.has( "password" ) || body.getString( "password" ).equals( "" ) ) {
			logger.info( "export non-cryptedKey.");
			retKey = kVo.getPrivateKey();
		} else {
			logger.info( "export encryptedKey.");
			retKey = Pkcs12Creator.enc( kVo.getPrivateKey(), body.getString( "password" ) );
			isEncrypted = true;
		}

		if ( body.getBoolean( "isPem" ) ) {
			return getPrivateKeyPem( retKey, isEncrypted ).toString().getBytes();
		}

		return retKey;
	}

	private byte[] exportPublicKey( JSONObject body, int certId ) {
		logger.info( "export publickey.");
		if ( !body.has( "isPem" ) ) throw new IllegalArgumentException( "isPem is null." );

		KeyVo kVo = selectCertPublicKeyUsingCertId( certId );

		if ( body.getBoolean( "isPem" ) ) {
			return PemUtil.publicKeyToPem( kVo.getPublicKey() ).getBytes();
		}

		return kVo.getPublicKey();
	}

	private byte[] exportCertChain( HttpServletRequest request, JSONObject body, int certId ) throws CertificateException {
		if ( !body.has( "exportType") || !body.has( "isPem" ) ) throw new IllegalArgumentException( "exportType or isPem is null." );

		CertDto cDto = getCertChain( certId );
		byte[] retCert;

		if ( body.getInt( "exportType" ) == 1 /*HEAD*/ ) {
			logger.info( "export one certificate");
			retCert = cDto.getEeCertVo().getFile();

			if ( body.getBoolean( "isPem") ) return PemUtil.certToPem( retCert ).getBytes();
			else return retCert;

		} else if ( body.getInt( "exportType" ) == 2 /*ENTIRE*/ ) {
			logger.info( "export entire certificates");
			StringBuilder certSb = new StringBuilder();

			for ( X509Certificate cert : cDto.getCertChain() )  {
				certSb.append( PemUtil.certToPem( cert.getEncoded() ) );
			}

			return certSb.toString().getBytes();

		} else throw new IllegalArgumentException( "export type value is invalid." );
	}

	private CertDto getCertChain( int certId ) throws CertificateException {
		CertVo eeCertVo = new CertVo();
		CertDto cDto = new CertDto();

		eeCertVo.setId( certId );
		eeCertVo = certDao.selectCertOneUsingCertId( eeCertVo );
		eeCertVo.setGroupId( eeCertVo.getOuType() ); // 수정해야함 ㅡㅡ

		CertVo rootCertVo;

		if ( eeCertVo.getType() == 0 /*Root CA*/ ) {
			logger.info( "make certificate Chain...: current Cert is RootCA.");
			X509Certificate eeCert = (X509Certificate) CertificateFactory.getInstance( "X.509" ).generateCertificate( new ByteArrayInputStream( eeCertVo.getFile() ) );

			cDto.setCertChain( new X509Certificate[]{ eeCert } );

		} else if ( eeCertVo.getType() == 1 /*Intermediate CA*/ ) {
			logger.info( "make certificate Chain...: current Cert is IntermedicateCA.");
			rootCertVo = certDao.selectRootCertAndKeyInfoOne();

			X509Certificate eeCert = (X509Certificate) CertificateFactory.getInstance( "X.509" ).generateCertificate( new ByteArrayInputStream( eeCertVo.getFile() ) );
			X509Certificate rootCert = (X509Certificate) CertificateFactory.getInstance( "X.509" ).generateCertificate( new ByteArrayInputStream( rootCertVo.getFile() ) );

			cDto.setCertChain( new X509Certificate[]{ eeCert, rootCert } );

		} else if ( eeCertVo.getType() == 2 /*End Entity Cert*/) {
			logger.info( "make certificate Chain...: current Cert is End Entyity Certificate.");
			rootCertVo = certDao.selectRootCertAndKeyInfoOne();

			X509Certificate eeCert = (X509Certificate) CertificateFactory.getInstance( "X.509" ).generateCertificate( new ByteArrayInputStream( eeCertVo.getFile() ) );
			X509Certificate rootCert = (X509Certificate) CertificateFactory.getInstance( "X.509" ).generateCertificate( new ByteArrayInputStream( rootCertVo.getFile() ) );

			String dn = eeCert.getIssuerDN().getName();
			eeCertVo.setGroupSolutionName( dn.substring( dn.indexOf( "OU = ") + 5, dn.indexOf( "O = Dreamsecurity") - 1 ) );

			CertVo interCertVo = certDao.selectIntermediateCertAndKeyInfoOne( eeCertVo );

			X509Certificate interCert = (X509Certificate) CertificateFactory.getInstance( "X.509" ).generateCertificate( new ByteArrayInputStream( interCertVo.getFile() ) );

			cDto.setCertChain( new X509Certificate[]{ eeCert, interCert, rootCert } );

		} else {
			throw new IllegalArgumentException( "CertType(certLevel) is null." );
		}

		cDto.setEeCertVo( eeCertVo );

		return cDto;
	}

	private StringBuilder getPrivateKeyPem( byte[] privateKey, boolean isEncrypted ) {
		StringBuilder pemBuilder = new StringBuilder();

		if ( isEncrypted )
			pemBuilder.append( PemUtil.encryptedPriKeyToPem( privateKey ) );
		else
			pemBuilder.append( PemUtil.priKeyToPem( privateKey ) );

		return pemBuilder;
	}

	private KeyVo selectCertPublicKeyUsingCertId( int certId ) {
		CertVo cVo = new CertVo();

		cVo.setId( certId );
		KeyVo kVo = certDao.selectPublicKeyBinary( cVo );

		return kVo;
	}

	private KeyVo selectCertKeyPairUsingCertId( int certId, HttpServletRequest request ) {
		String userId = request.getSession().getAttribute( LoginConstants.SESSION_ID ).toString();
		CertVo cVo = new CertVo();

		cVo.setId( certId );
		cVo.setSubject( userId );

		if ( certDao.selectCertUsingSubject( cVo ) == null )
			throw new IllegalAccessError( "there is no authorization for download it.");

		KeyVo kVo = certDao.selectKeyPairBinary( cVo );

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

	@Deprecated
	public CertVo mappingObject( HttpServletRequest request ) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JSONObject body = null;

		if ( request.getAttribute( "body" ) != null ) {
			body = (JSONObject)request.getAttribute( "body" );
		}

		return mapper.readValue( body.toString(), CertVo.class );
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
		certVo.setOuType( certVo.getGroupId() );

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
