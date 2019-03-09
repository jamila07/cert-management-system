package com.dreamsecurity.ca.business.admin.controller;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
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

import com.dreamsecurity.ca.business.admin.service.AdminService;
import com.dreamsecurity.ca.business.audit.service.WebAuditService;
import com.dreamsecurity.ca.business.common.domain.PageMaker;
import com.dreamsecurity.ca.business.common.mvc.controller.CommonController;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
@RequestMapping("/admin")
public class AdminController extends CommonController {
	
	@Inject
	private AdminService adminService;
	
	@Inject
	private WebAuditService webAuditService;
	
	@GetMapping("home.do")
	public ModelAndView page( HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView();
		
		mv.setViewName( "admin/view");
		webAuditService.insertAudit( request );
		return mv;
	}
	
	@PostMapping("registerRootCa.do")
	public ResponseEntity<?> registerRootCa( HttpServletRequest request, HttpServletResponse response ) throws InvalidKeyException, IllegalAccessException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, SignatureException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		adminService.registerRootCa( request );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>( entities, HttpStatus.CREATED );
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("appliedUserList.do")
	public ResponseEntity<?> showAppliedUserList( HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		JSONObject jObj = new JSONObject( request.getAttribute( "body" ).toString() );
		
		PageMaker pageMaker = super.setPaging( jObj.getInt( "page" ) );
		List<Map<String, Object>> list = adminService.showAppliedUserList( pageMaker.getCri() );
		int listCnt = adminService.showAppliedUserListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("appliedUser/{AppliedUserSeqId}")
	public ResponseEntity<?> registerAppliedUser( @PathVariable("AppliedUserSeqId") int seqId, HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException, InvalidKeyException, NumberFormatException, NoSuchAlgorithmException, IllegalAccessException, InvalidKeySpecException, CertificateException, NoSuchProviderException, SignatureException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		adminService.registerUser( request, seqId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>( entities, HttpStatus.CREATED );
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("appliedGroupList.do")
	public ResponseEntity<?> showAppliedGroupList( HttpServletRequest request, HttpServletResponse response ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		JSONObject jObj = new JSONObject( request.getAttribute( "body" ).toString() );
		
		PageMaker pageMaker = super.setPaging( jObj.getInt( "page" ) );
		List<Map<String, Object>> list = adminService.showGroupApplyList( pageMaker.getCri() );
		int listCnt = adminService.showGroupApplyListCnt();
		
		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("appliedGroupSolutionList.do")
	public ResponseEntity<?> showAppliedGroupSolutionList( HttpServletRequest request, HttpServletResponse response ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		JSONObject jObj = new JSONObject( request.getAttribute( "body" ).toString() );
		
		PageMaker pageMaker = super.setPaging( jObj.getInt( "page" ) );
		List<Map<String, Object>> list = adminService.showGroupSolutionApplyList( pageMaker.getCri() );
		int listCnt = adminService.showGroupSolutionApplyListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("appliedGroup/{AppliedGroupSeqId}")
	public ResponseEntity<?> registerAppliedGroup( @PathVariable("AppliedGroupSeqId") int seqId, HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException, InvalidKeyException, NumberFormatException, NoSuchAlgorithmException, IllegalAccessException, InvalidKeySpecException, CertificateException, NoSuchProviderException, SignatureException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		adminService.registerGroup( request, seqId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>( entities, HttpStatus.CREATED );
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
	
	@PostMapping("appliedSolution/{AppliedSolutionSeqId}")
	public ResponseEntity<?> registerAppliedSolution( @PathVariable("AppliedSolutionSeqId") int seqId, HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException, InvalidKeyException, NumberFormatException, NoSuchAlgorithmException, IllegalAccessException, InvalidKeySpecException, CertificateException, NoSuchProviderException, SignatureException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		
		adminService.registerSolution( request, seqId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>( entities, HttpStatus.CREATED );
		
		webAuditService.insertAudit( request );
		
		return entity;
	}
}
