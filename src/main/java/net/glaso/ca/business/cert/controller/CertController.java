package net.glaso.ca.business.cert.controller;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import net.glaso.ca.business.audit.service.CertAuditService;
import net.glaso.ca.business.audit.service.WebAuditService;
import net.glaso.ca.business.cert.service.CertService;
import net.glaso.ca.business.cert.vo.CertVo;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/cert")
public class CertController {

	@Inject
	private CertService service;

	@Inject
	private CertAuditService certAuditService;

	@Inject
	private WebAuditService webAuditService;

	// 2019.4.2 - ehdvudee
	// return: ModelAndView or ResponseEntitiy
	// 위의 것만 리턴하면 경고에 대해 안전하다.
	@SuppressWarnings("unchecked")
	@GetMapping("")
	public <T>T listOrPage( HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException {
		if ( request.getParameterMap().isEmpty() ) {
			return (T) page( request );
		} else {
			return (T) showList( request, response );
		}
	}

	private ModelAndView page( HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView();

		mv.setViewName( "/cert/view" );

		webAuditService.insertAudit( request );

		return mv;
	}

	private ResponseEntity<?> showList( HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		entities.put( "data", service.showList( request ) );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		return entity;
	}


	@PostMapping("")
	public ResponseEntity<?> registerAndVerifyCert( HttpServletRequest request, HttpServletResponse response ) throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IllegalAccessException, InvalidKeySpecException, IOException, InvalidAlgorithmParameterException, CertPathBuilderException, JSONException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		JSONObject body = (JSONObject) request.getAttribute( "body" );

		if ( body.has( "oper" ) && body.getString( "oper" ).equals( "verify" ) ) {

			return uploadFile( entity, entities, body );

		} else if ( body.has( "oper" ) && body.getString( "oper" ).equals( "register" ) ){
			CertVo vo = CertVo.deserialize( body );

			try {
				service.register( request, vo );
				entities.put( "redirect", "/cert" );
				entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

				certAuditService.insertAudit( request, vo );
				webAuditService.insertAudit( request );
			} catch ( Exception e ) {
				e.printStackTrace();
				certAuditService.insertAudit( request, vo, e );
				throw e;
			}

			return entity;
		} else {
			throw new IllegalArgumentException( "operation is null." );
		}
	}

	private ResponseEntity<?> uploadFile( ResponseEntity<?> entity, Map<String, Object> entities, JSONObject body ) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CertPathBuilderException, JSONException {

		service.verifyCertificate( DatatypeConverter.parseBase64Binary( body.getString( "data" ) ) );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		return entity;
	}

	@PostMapping("{certId}")
	public ResponseEntity<?> downloadCertAndUpload( @PathVariable("certId") int certId, HttpServletRequest request, HttpServletResponse response ) throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, JSONException, KeyStoreException, IOException, NoSuchProviderException, CertPathBuilderException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		JSONObject body = (JSONObject) request.getAttribute( "body" );

		if ( body.has( "oper" ) && body.getString( "oper" ).equals( "download" ) ) {
			entities.put( "fileB64", service.downloadCert( request, certId ) );
			entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		}

		return entity;
	}
}
