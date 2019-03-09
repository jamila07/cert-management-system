package com.dreamsecurity.ca.business.login.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.dreamsecurity.ca.business.audit.service.WebAuditService;
import com.dreamsecurity.ca.business.common.domain.Criteria;
import com.dreamsecurity.ca.business.common.domain.PageMaker;
import com.dreamsecurity.ca.business.common.mvc.controller.CommonController;
import com.dreamsecurity.ca.business.group.service.GroupService;
import com.dreamsecurity.ca.business.login.common.LoginConstants;
import com.dreamsecurity.ca.business.login.service.LoginService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
public class LoginController extends CommonController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Inject
	private LoginService service;
	
	@Inject
	private WebAuditService webAuditService;
	
	@Inject
	private GroupService groupService;
	
	@GetMapping("/")
	public ModelAndView page( HttpServletRequest request ) {		
		ModelAndView mv = new ModelAndView();
		System.out.println( request.getSession().getId() );
		if ( request.getSession().getAttribute( LoginConstants.SESSION_ID ) != null ) {
			mv.setViewName( "redirect:/cert/home.do" );
			webAuditService.insertAudit( request );
		} else {
			mv.setViewName( "/index" );
		}
		
		return mv;
	}
	
	@PostMapping("/login.do")
	public ResponseEntity<?> login( HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException, NoSuchAlgorithmException {		
		
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		if ( service.login( request ) == true ) {
			entities.put( "redirect", "cert/home.do" );
			entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.CREATED);
			
			webAuditService.insertAudit( request );
		} else {
			entities.put( "redirect", "/" );
			entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.BAD_REQUEST);
		}
		
		return entity;
	}
	
	@GetMapping("/logout.do")
	public ResponseEntity<?> logout( HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException, NoSuchAlgorithmException {		
		
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		HttpSession session =  request.getSession();
		
		if( session != null ) {
			webAuditService.insertAudit( request );
			session.invalidate();
			
			entities.put( "redirect", "/" );
			entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		} else { 
			entities.put( "redirect", "/" );
			entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.BAD_REQUEST);
		}
		
		return entity;
	}
	
	@PostMapping("/showGroupInfo.do")
	public ResponseEntity<?> showGroupInfo( HttpServletRequest request, HttpServletResponse response ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		JSONObject jObj = new JSONObject( request.getAttribute( "body" ).toString() );
		
		PageMaker pageMaker = super.setPaging( jObj.getInt( "page" ), jObj.getInt( "perPageNum" ) );
		List<Map<String, Object>> list = groupService.showGroupList( request, pageMaker.getCri() );
		int listCnt = groupService.showGroupListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
}