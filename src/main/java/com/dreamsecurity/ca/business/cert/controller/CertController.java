package com.dreamsecurity.ca.business.cert.controller;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.dreamsecurity.ca.business.audit.service.CertAuditService;
import com.dreamsecurity.ca.business.audit.service.WebAuditService;
import com.dreamsecurity.ca.business.cert.service.CertService;
import com.dreamsecurity.ca.business.cert.vo.CertVo;
import com.dreamsecurity.ca.framework.utils.CaUtils;

@RestController
@RequestMapping("/cert")
public class CertController {
	
	private static final Logger logger = LoggerFactory.getLogger(CertController.class);
	
	@Inject
	private CertService service;
	
	@Inject
	private CertAuditService certAuditService;
	
	@Inject
	private WebAuditService webAuditService;
	
	@GetMapping("home.do")
	public ModelAndView page( HttpServletRequest request ) {		
		ModelAndView mv = new ModelAndView();

		mv.setViewName( "/cert/view" );
		
		webAuditService.insertAudit( request );
		
		return mv;
	}
	
	@PostMapping("/register.do")
	public ResponseEntity<?> register( HttpServletRequest request, HttpServletResponse response ) throws InvalidKeyException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, IllegalAccessException, InvalidKeySpecException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		CertVo vo = null;
		try {
			vo = service.mappingObject( request );
			service.register( request, vo );
			entities.put( "redirect", "/cert/home.do" );
			entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
			
			certAuditService.insertAudit( request, vo );
			webAuditService.insertAudit( request );
		} catch ( Exception e ) {
			e.printStackTrace();
			certAuditService.insertAudit( request, vo, e );
			throw e;
		}
		
		return entity;
	}
	
	@PostMapping("showList.do")
	public ResponseEntity<?> showList( HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
			
		entities.put( "data", service.showList( request ) );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		return entity;
	}
	
	@PostMapping("certBinDownload/{certId}")
	public ResponseEntity<?>downloadCertBinFile( @PathVariable("certId") int id, HttpServletRequest request, HttpServletResponse response ) throws IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		entities.put( "fileB64", service.downloadCertBinaryFile( id, request ) );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		return entity;
	}
	
	@PostMapping("certPemDownload/{certId}")
	public ResponseEntity<?>downloadCertPemFile( @PathVariable("certId") int id, HttpServletRequest request, HttpServletResponse response ) throws IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		entities.put( "fileB64", service.downloadCertPemFile( id, request ) );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		return entity;
	}
	
	@PostMapping("pkcs8KeyDownload/{certId}")
	public ResponseEntity<?>downloadPkcs8KeyFile( @PathVariable("certId") int id, HttpServletRequest request, HttpServletResponse response ) throws IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		entities.put( "fileB64", service.downloadPkcs8KeyFile( id, request ) );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		return entity;
	}
	
	@PostMapping("pkcs8PemDownload/{certId}")
	public ResponseEntity<?>downloadPkcs8PemFile( @PathVariable("certId") int id, HttpServletRequest request, HttpServletResponse response ) throws IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		entities.put( "fileB64", service.downloadPkcs8PemFile( id, request ) );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		return entity;
	}
	
	@PostMapping("upload")
	public ResponseEntity<?> uploadFile( HttpServletRequest request ) throws CertificateException, IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CertPathBuilderException, JSONException{
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		JSONObject jObj = CaUtils.getJSONObject( request );
		
		service.verifyCertificate( 
				DatatypeConverter.parseBase64Binary( 
						jObj.getString( "data" ) 
				)
		);
		entities.put( "status", "success" ); 
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		return entity;
	}
}
