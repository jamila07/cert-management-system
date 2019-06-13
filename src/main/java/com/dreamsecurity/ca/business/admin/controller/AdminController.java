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

	@GetMapping("")
	public ModelAndView page( HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView();

		mv.setViewName( "admin/view");
		webAuditService.insertAudit( request );
		return mv;
	}

	@PostMapping("")
	public ResponseEntity<?> registerRootCa( HttpServletRequest request, HttpServletResponse response ) throws InvalidKeyException, IllegalAccessException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, SignatureException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		adminService.registerRootCa( request );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>( entities, HttpStatus.CREATED );

		webAuditService.insertAudit( request );

		return entity;
	}

	@GetMapping("applied-user")
	public ResponseEntity<?> showAppliedUserList( HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = adminService.showAppliedUserList( pageMaker.getCri() );
		int listCnt = adminService.showAppliedUserListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("applied-user/{appliedUserSeqId}")
	public ResponseEntity<?> registerAppliedUser( @PathVariable("appliedUserSeqId") int seqId, HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException, InvalidKeyException, NumberFormatException, NoSuchAlgorithmException, IllegalAccessException, InvalidKeySpecException, CertificateException, NoSuchProviderException, SignatureException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		adminService.registerUser( request, seqId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>( entities, HttpStatus.CREATED );

		webAuditService.insertAudit( request );

		return entity;
	}

	@GetMapping("applied-group")
	public ResponseEntity<?> showAppliedGroupList( HttpServletRequest request, HttpServletResponse response ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = adminService.showGroupApplyList( pageMaker.getCri() );
		int listCnt = adminService.showGroupApplyListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@GetMapping("applied-group/solution")
	public ResponseEntity<?> showAppliedGroupSolutionList( HttpServletRequest request, HttpServletResponse response ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = adminService.showGroupSolutionApplyList( pageMaker.getCri() );
		int listCnt = adminService.showGroupSolutionApplyListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("applied-group/{appliedGroupSeqId}")
	public ResponseEntity<?> registerAppliedGroup( @PathVariable("appliedGroupSeqId") int seqId, HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException, InvalidKeyException, NumberFormatException, NoSuchAlgorithmException, IllegalAccessException, InvalidKeySpecException, CertificateException, NoSuchProviderException, SignatureException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		adminService.registerGroup( request, seqId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>( entities, HttpStatus.CREATED );

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("applied-group/{groupId}/solution/{appliedSolutionSeqId}")
	public ResponseEntity<?> registerAppliedSolution( @PathVariable("appliedSolutionSeqId") int seqId, HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException, InvalidKeyException, NumberFormatException, NoSuchAlgorithmException, IllegalAccessException, InvalidKeySpecException, CertificateException, NoSuchProviderException, SignatureException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		adminService.registerSolution( request, seqId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>( entities, HttpStatus.CREATED );

		webAuditService.insertAudit( request );

		return entity;
	}
}
