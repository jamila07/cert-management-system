package net.glaso.ca.business.user.controller;

import net.glaso.ca.business.audit.service.WebAuditService;
import net.glaso.ca.business.user.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

	private final UserService service;

	private final WebAuditService wAuditService;

	@Autowired
	public UserController( UserService service, WebAuditService wAuditService ) {
		this.service = service;
		this.wAuditService = wAuditService;
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
			return (T) showUserList( request );
		}
	}
	
	private ModelAndView page( HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName( "user/view");
		
		wAuditService.insertAudit( request );
		return mv;
	}
	
	private ResponseEntity<?> showUserList( HttpServletRequest request ) {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();
		
		entities.put( "data", service.showList( request ) );
		entities.put( "status", "success" );
		
		entity = new ResponseEntity<>(entities, HttpStatus.OK);
		
		wAuditService.insertAudit( request );
		return entity;
	}
	
	@PostMapping("")
	public ResponseEntity<?> register( HttpServletRequest request ) throws NoSuchAlgorithmException {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();
		
		service.registerAppliedUser( request );
		wAuditService.insertAudit( request );
		
		entities.put( "status", "success" );
		entity = new ResponseEntity<>(entities, HttpStatus.CREATED);
		
		return entity;
	}

	@PostMapping(value="/{userId}")
	public ResponseEntity<?> userIdPost( @PathVariable("userId") String userId, HttpServletRequest request ) {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();
		JSONObject body = (JSONObject)request.getAttribute( "body" );
		
		if ( !body.has( "oper" ) ) throw new IllegalArgumentException( "oper is null." );
		
		if ( body.getString( "oper" ).equals( "chkOverlap" ) ) { 
			if ( service.chkOverlapUser( userId ) ) entities.put( "data", true );
			else entities.put( "data", false );
		} else {
			throw new IllegalArgumentException( "oper is invalid." );
		}
		
		entities.put( "status" , "success" );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);
		
		return entity;
	}
	
	@GetMapping("{userId}/applied-groups")
	public ResponseEntity<?> showJoinedGroup( @PathVariable("userId") String userId, HttpServletRequest request ) {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();
		
		entities.put( "data", service.showJoinedGroup( request, userId ) );
		entities.put( "status", "success" );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);
		
		wAuditService.insertAudit( request );
		
		return entity;
	}
}
