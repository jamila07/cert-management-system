package com.dreamsecurity.ca.business.user.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.dreamsecurity.ca.business.audit.service.WebAuditService;
import com.dreamsecurity.ca.business.user.service.UserService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
@RequestMapping("/user")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Inject
	private UserService service;
	
	@Inject
	private WebAuditService wAuditService;
	
	@GetMapping("home.do")
	public ModelAndView page( HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName( "user/view");
		
		wAuditService.insertAudit( request );
		return mv;
	}
	
	@PostMapping("/register.do")
	public void register( HttpServletRequest request, HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException, NoSuchAlgorithmException {		
		service.appliedUserRegister( request );
		wAuditService.insertAudit( request );
	}
	
	@PostMapping(value="/showUserList.do")
	public ResponseEntity<?> showUserList( HttpServletRequest request, HttpServletResponse response ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		entities.put( "data", service.showList( request ) );
		
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		wAuditService.insertAudit( request );
		return entity;
	}
	
	@PostMapping(value="/chkOverlapUser/{userId}")
	public ResponseEntity<?> chkOverlapUser( @PathVariable("userId") String userId, HttpServletRequest request, HttpServletResponse response ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		if ( service.chkOverlapUser( userId ) ) entities.put( "data", true );
		else entities.put( "data", false );
		
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		return entity;
	}
}