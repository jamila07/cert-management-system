package com.dreamsecurity.ca.business.user.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.dreamsecurity.ca.business.audit.service.WebAuditService;
import com.dreamsecurity.ca.business.user.service.UserService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Inject
	private UserService service;
	
	@Inject
	private WebAuditService wAuditService;
	
	// 2019.4.2 - ehdvudee
	// return: ModelAndView or ResponseEntitiy
	// 위의 것만 리턴하면 경고에 대해 안전하다.
	@SuppressWarnings("unchecked")
	@GetMapping("")
	public <T>T listOrPage( HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException {
		if ( request.getParameterMap().isEmpty() ) {
			return (T) page( request );
		} else {
			return (T) showUserList( request, response );
		}
	}
	
	private ModelAndView page( HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName( "user/view");
		
		wAuditService.insertAudit( request );
		return mv;
	}
	
	private ResponseEntity<?> showUserList( HttpServletRequest request, HttpServletResponse response ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		entities.put( "data", service.showList( request ) );
		entities.put( "status", "success" );
		
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		wAuditService.insertAudit( request );
		return entity;
	}
	
	@PostMapping("")
	public ResponseEntity<?> register( HttpServletRequest request, HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException, NoSuchAlgorithmException {		
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		service.registerAppliedUser( request );
		wAuditService.insertAudit( request );
		
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.CREATED);
		
		return entity;
	}

	@PostMapping(value="/{userId}")
	public ResponseEntity<?> userIdPost( @PathVariable("userId") String userId, HttpServletRequest request, HttpServletResponse response ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		JSONObject body = (JSONObject)request.getAttribute( "body" );
		
		if ( !body.has( "oper" ) ) throw new IllegalArgumentException( "oper is null." );
		
		if ( body.getString( "oper" ).equals( "chkOverlap" ) ) { 
			if ( service.chkOverlapUser( userId ) ) entities.put( "data", true );
			else entities.put( "data", false );
		} else {
			throw new IllegalArgumentException( "oper is invalid." );
		}
		
		entities.put( "status" , "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		return entity;
	}
	
	@GetMapping("{userId}/applied-groups")
	public ResponseEntity<?> showJoinedGroup( @PathVariable("userId") String userId, HttpServletRequest request, HttpServletResponse response ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		entities.put( "data", service.showJoinedGroup( request, userId ) );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		wAuditService.insertAudit( request );
		
		return entity;
	}
}
