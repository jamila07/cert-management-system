package net.glaso.ca.business.audit.controller;

import net.glaso.ca.business.audit.service.CertAuditService;
import net.glaso.ca.business.audit.service.WebAuditService;
import net.glaso.ca.business.common.domain.PageMaker;
import net.glaso.ca.business.common.mvc.controller.CommonController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/audit")
public class AuditController extends CommonController {

	private final CertAuditService cAuditService;

	private final WebAuditService wAuditService;

	@Autowired
	public AuditController( CertAuditService cAuditService, WebAuditService wAuditService ) {
		this.cAuditService = cAuditService;
		this.wAuditService = wAuditService;
	}

	@GetMapping("")
	public ModelAndView page( HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView();

		mv.setViewName( "audit/view");
		wAuditService.insertAudit( request );
		return mv;
	}

	@GetMapping("web")
	public ResponseEntity<?> showWebAuditList( HttpServletRequest request ) throws IllegalArgumentException {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = wAuditService.showList( pageMaker.getCri() );
		int listCnt = wAuditService.showListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		wAuditService.insertAudit( request );

		return entity;
	}

	@GetMapping("cert")
	public ResponseEntity<?> showCertAuditList( HttpServletRequest request ) {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = cAuditService.showList( pageMaker.getCri() );
		int listCnt = cAuditService.showListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		wAuditService.insertAudit( request );

		return entity;
	}
}
