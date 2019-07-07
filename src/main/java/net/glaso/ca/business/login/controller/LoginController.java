package net.glaso.ca.business.login.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.glaso.ca.business.group.service.GroupService;
import net.glaso.ca.business.login.service.LoginService;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import net.glaso.ca.business.audit.service.WebAuditService;
import net.glaso.ca.business.common.domain.PageMaker;
import net.glaso.ca.business.common.mvc.controller.CommonController;
import net.glaso.ca.business.login.common.LoginConstants;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
public class LoginController extends CommonController {
	
	@Inject
	private LoginService service;
	
	@Inject
	private WebAuditService webAuditService;
	
	@Inject
	private GroupService groupService;
	
	@GetMapping("/")
	public ModelAndView page( HttpServletRequest request ) {		
		ModelAndView mv = new ModelAndView();
		if ( request.getSession().getAttribute( LoginConstants.SESSION_ID ) != null ) {
			mv.setViewName( "redirect:/cert" );
			webAuditService.insertAudit( request );
		} else {
			mv.setViewName( "/index" );
		}
		
		return mv;
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login( HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException, NoSuchAlgorithmException {		
		
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		if ( service.login( request ) == true ) {
			entities.put( "redirect", "cert" );
			entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.CREATED);
			
			webAuditService.insertAudit( request );
		} else {
 			entities.put( "redirect", "/" );
			entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.BAD_REQUEST);
		}
		
		return entity;
	}
	
	@GetMapping("/logout")
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
		JSONObject jObj = (JSONObject) request.getAttribute( "body" );
		
		PageMaker pageMaker = super.setPaging( jObj.getInt( "page" ), jObj.getInt( "perPageNum" ) );
		List<Map<String, Object>> list = groupService.showGroupList( request, pageMaker.getCri() );
		int listCnt = groupService.showGroupListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
}