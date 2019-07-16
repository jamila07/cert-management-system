package net.glaso.ca.business.cert.controller;

import net.glaso.ca.business.audit.service.CertAuditService;
import net.glaso.ca.business.audit.service.WebAuditService;
import net.glaso.ca.business.cert.service.CertService;
import net.glaso.ca.business.cert.vo.CertVo;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cert")
public class CertController {

	private final CertService service;

	private final CertAuditService certAuditService;

	private final WebAuditService webAuditService;

	@Autowired
	public CertController( CertService service, CertAuditService certAuditService, WebAuditService webAuditService ) {
		this.service = service;
		this.certAuditService = certAuditService;
		this.webAuditService = webAuditService;
	}

	// 2019.4.2 - ehdvudee
	// return: ModelAndView or ResponseEntitiy
	// 위의 것만 리턴하면 경고에 대해 안전하다.
	@SuppressWarnings("unchecked")
	@GetMapping("")
	public <T>T listOrPage( HttpServletRequest request ) throws IllegalArgumentException {
		if ( request.getParameterMap().isEmpty() ) {
			return (T) page( request );
		} else {
			return (T) showList( request );
		}
	}

	private ModelAndView page( HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView();

		mv.setViewName( "/cert/view" );

		webAuditService.insertAudit( request );

		return mv;
	}

	private ResponseEntity<?> showList( HttpServletRequest request ) throws IllegalArgumentException {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		entities.put( "data", service.showList( request ) );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		return entity;
	}


	@PostMapping("")
	public ResponseEntity<?> registerAndVerifyCert( HttpServletRequest request ) throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IllegalAccessException, InvalidKeySpecException, IOException, InvalidAlgorithmParameterException, CertPathBuilderException, JSONException {
		Map<String, Object> entities = new HashMap<>();
		JSONObject body = (JSONObject) request.getAttribute( "body" );

		if ( body.has( "oper" ) && body.getString( "oper" ).equals( "verify" ) ) {

			return uploadFile( entities, body );

		} else if ( body.has( "oper" ) && body.getString( "oper" ).equals( "register" ) ){
			CertVo vo = CertVo.deserialize( body );
			ResponseEntity<?> entity;

			try {
				service.register( request, vo );
				entities.put( "redirect", "/cert" );
				entity = new ResponseEntity<>(entities, HttpStatus.OK);

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

	private ResponseEntity<?> uploadFile( Map<String, Object> entities, JSONObject body ) throws CertificateException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CertPathBuilderException, JSONException {
		ResponseEntity<?> entity;

		service.verifyCertificate( DatatypeConverter.parseBase64Binary( body.getString( "data" ) ) );
		entities.put( "status", "success" );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		return entity;
	}

	@PostMapping("{certId}")
	public ResponseEntity<?> downloadCertAndUpload( @PathVariable("certId") int certId, HttpServletRequest request ) throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, JSONException, KeyStoreException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<>();

		JSONObject body = (JSONObject) request.getAttribute( "body" );

		if ( body.has( "oper" ) && body.getString( "oper" ).equals( "download" ) ) {
			entities.put( "fileB64", service.downloadCert( request, certId ) );
			entity = new ResponseEntity<>(entities, HttpStatus.OK);
		}

		return entity;
	}
}
