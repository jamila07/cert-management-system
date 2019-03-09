package com.dreamsecurity.ca.business.group.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.dreamsecurity.ca.business.common.domain.PageMaker;
import com.dreamsecurity.ca.business.common.mvc.controller.CommonController;
import com.dreamsecurity.ca.business.group.service.GroupService;
import com.dreamsecurity.ca.business.group.vo.UserGroupVo;
import com.dreamsecurity.ca.framework.utils.CaUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
@RequestMapping("/group")
public class GroupController extends CommonController {
	
	@Inject
	private GroupService service;
	
	@Inject
	private WebAuditService webAuditService;
	
	@GetMapping("home.do")
	public ModelAndView page( HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName( "group/view");
		webAuditService.insertAudit( request );
		return mv;
	}
	
	@PostMapping("showGroupList.do")
	public ResponseEntity<?> showGroupList( HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		JSONObject jObj = new JSONObject( request.getAttribute( "body" ).toString() );
		
		PageMaker pageMaker = super.setPaging( jObj.getInt( "page" ) );
		List<Map<String, Object>> list = service.showGroupList( request, pageMaker.getCri() );
		int listCnt = service.showGroupListCnt();
		
		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("showGroupApplyList.do")
	public ResponseEntity<?> showGroupApplyList( HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		JSONObject jObj = new JSONObject( request.getAttribute( "body" ).toString() );
		int groupId = jObj.getInt( "groupId" );
		
		PageMaker pageMaker = super.setPaging( jObj.getInt( "page" ) );
		List<Map<String, Object>> list = service.showUserGroupApplyList( request, pageMaker.getCri(), groupId );
		int listCnt = service.showUserGroupApplyListCnt( groupId );

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("register.do")
	public ResponseEntity<?> register( HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		service.registerGroup( request );
		entities.put( "redirect", "/group/home.do" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("showUserGroupList.do")
	public ResponseEntity<?> showUserList( HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException, JsonParseException, JsonMappingException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		List<Map<String, Object>> voListMap = new ArrayList<Map<String, Object>>();
		
		for ( UserGroupVo vo : service.showUserList( request ) ) {
			voListMap.add( CaUtils.voToMap( vo ) );
		}
		
		entities.put( "data", voListMap );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
	
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("appliedUser/{appliedUserId}")
	public ResponseEntity<?> registerAppliedUser( @PathVariable("appliedUserId") String userId, HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException, InvalidKeyException, NumberFormatException, NoSuchAlgorithmException, IllegalAccessException, InvalidKeySpecException, CertificateException, NoSuchProviderException, SignatureException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		JSONObject jObj = new JSONObject( request.getAttribute( "body" ).toString() );
		
		service.approveUserToJoinGroup( request, jObj, userId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>( entities, HttpStatus.OK );
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("addUserToGroup.do")
	public ResponseEntity<?> addUserToGroup( HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		this.service.addUserToGroup( request );
		
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
	
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("removeUserToGroup.do")
	public ResponseEntity<?> removeUserToGroup( HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		this.service.removeUserToGroup( request );
		
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
	
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("showJoinedGroup.do")
	public ResponseEntity<?> showJoinedGroup( HttpServletRequest request, HttpServletResponse response ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		entities.put( "data", service.showJoinedGroup( request ) );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("applyGroupSolution.do")
	public ResponseEntity<?> applyGroupSolution( HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		service.applyGroupSolution( request );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
}
