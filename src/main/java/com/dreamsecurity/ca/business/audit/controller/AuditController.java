package com.dreamsecurity.ca.business.audit.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.dreamsecurity.ca.business.audit.service.CertAuditService;
import com.dreamsecurity.ca.business.audit.service.WebAuditService;
import com.dreamsecurity.ca.business.common.domain.PageMaker;
import com.dreamsecurity.ca.business.common.mvc.controller.CommonController;

@RestController
@RequestMapping("/audit")
public class AuditController extends CommonController {

	@Inject
	private CertAuditService cAuditService;

	@Inject
	private WebAuditService wAuditService;

	@GetMapping("")
	public ModelAndView page( HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView();

		mv.setViewName( "audit/view");
		wAuditService.insertAudit( request );
		return mv;
	}

	@GetMapping("web")
	public ResponseEntity<?> showWebAuditList( HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = wAuditService.showList( request, pageMaker.getCri() );
		int listCnt = wAuditService.showListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		wAuditService.insertAudit( request );

		return entity;
	}

	@GetMapping("cert")
	public ResponseEntity<?> showCertAuditList( HttpServletRequest request, HttpServletResponse response ) {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = cAuditService.showList( request, pageMaker.getCri() );
		int listCnt = cAuditService.showListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		wAuditService.insertAudit( request );

		return entity;
	}
}
