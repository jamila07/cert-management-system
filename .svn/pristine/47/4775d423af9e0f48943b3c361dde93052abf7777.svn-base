package com.dreamsecurity.ca.framework.handler;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.dreamsecurity.ca.business.audit.service.WebAuditService;
import com.dreamsecurity.ca.business.cert.controller.CertController;
import com.dreamsecurity.ca.business.group.controller.GroupController;
import com.dreamsecurity.ca.business.login.controller.LoginController;
import com.dreamsecurity.ca.business.user.controller.UserController;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@ControllerAdvice( assignableTypes = {CertController.class, GroupController.class, LoginController.class, UserController.class } )
public class CaExceptionHandler {
	
	@Inject
	private WebAuditService webAuditService;
	
	// HTTP Response Code : 400
	@ExceptionHandler(value = {JsonParseException.class, IllegalAccessException.class, IllegalArgumentException.class})
	public ResponseEntity<?> numberHandler( HttpServletRequest request, Exception e ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		entities = this.failProcess(entities, e);
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.BAD_REQUEST);

		insertfailWebAudit(request, e, HttpStatus.BAD_REQUEST.value() );
		
		e.printStackTrace();
		
		return entity;		
	}

	// HTTP Response Code : 500
	@ExceptionHandler(value={ JsonMappingException.class, IOException.class, NoSuchAlgorithmException.class,
			InvalidKeyException.class, CertificateException.class, NoSuchProviderException.class,
			SignatureException.class, InvalidKeySpecException.class, NullPointerException.class })
	public ResponseEntity<?> cryptoHandler( HttpServletRequest request, Exception e ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		entities = this.failProcess(entities, e);
		entity = new ResponseEntity<Map<String, Object>>( entities, HttpStatus.INTERNAL_SERVER_ERROR );

		insertfailWebAudit(request, e, HttpStatus.INTERNAL_SERVER_ERROR.value() );
		
		e.printStackTrace();

		return entity;	
	}	

	// HTTP Response Code : 500
	// 분류되지 않는 모든 Rest 관련 Exception
	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<?> defaultErrorHandler( HttpServletRequest request, Exception e ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		entities = this.failProcess(entities, e);
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.INTERNAL_SERVER_ERROR);

		insertfailWebAudit(request, e, HttpStatus.INTERNAL_SERVER_ERROR.value() );
		
		e.printStackTrace();

		return entity;		
	}

	private Map<String, Object> failProcess(Map<String, Object> map, Throwable e) {
		map.put("status", "fail");
		map.put("errMsg", ExceptionUtils.getRootCauseMessage(e) );
		return map;
	}
	
	private void insertfailWebAudit( HttpServletRequest request, Exception e, int repCode ) {
		webAuditService.insertAudit(request, e, repCode);
	}
}
