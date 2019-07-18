package net.glaso.ca.business.admin.controller;

import net.glaso.ca.business.admin.service.AdminService;
import net.glaso.ca.business.audit.service.WebAuditService;
import net.glaso.ca.business.common.domain.PageMaker;
import net.glaso.ca.business.common.mvc.controller.CommonController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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

@RestController
@RequestMapping("/admin")
public class AdminController extends CommonController {

	private final AdminService adminService;

	private final WebAuditService webAuditService;

	@Autowired
	public AdminController( AdminService adminService, WebAuditService webAuditService ) {
		this.adminService = adminService;
		this.webAuditService = webAuditService;
	}

	@GetMapping("")
	public ModelAndView page( HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView();

		mv.setViewName( "admin/view");
		webAuditService.insertAudit( request );
		return mv;
	}

	@PostMapping("")
	public ResponseEntity<?> registerRootCa( HttpServletRequest request ) throws InvalidKeyException, IllegalAccessException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, SignatureException, IOException {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		adminService.registerRootCa( request );
		entities.put( "status", "success" );
		entity = new ResponseEntity<>( entities, HttpStatus.CREATED );

		webAuditService.insertAudit( request );

		return entity;
	}

	@GetMapping("applied-user")
	public ResponseEntity<?> showAppliedUserList( HttpServletRequest request ) throws IllegalArgumentException {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();
		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = adminService.showAppliedUserList( pageMaker.getCri() );
		int listCnt = adminService.showAppliedUserListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("applied-user/{appliedUserSeqId}")
	public ResponseEntity<?> registerAppliedUser( @PathVariable("appliedUserSeqId") int seqId, HttpServletRequest request ) throws IOException, InvalidKeyException, NumberFormatException, NoSuchAlgorithmException, IllegalAccessException, InvalidKeySpecException, CertificateException, NoSuchProviderException, SignatureException {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		adminService.registerUser( request, seqId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<>( entities, HttpStatus.CREATED );

		webAuditService.insertAudit( request );

		return entity;
	}

	@GetMapping("applied-group")
	public ResponseEntity<?> showAppliedGroupList( HttpServletRequest request ) {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();
		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = adminService.showGroupApplyList( pageMaker.getCri() );
		int listCnt = adminService.showGroupApplyListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@GetMapping("applied-group/solution")
	public ResponseEntity<?> showAppliedGroupSolutionList( HttpServletRequest request ) {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();
		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = adminService.showGroupSolutionApplyList( pageMaker.getCri() );
		int listCnt = adminService.showGroupSolutionApplyListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("applied-group/{appliedGroupSeqId}")
	public ResponseEntity<?> registerAppliedGroup( @PathVariable("appliedGroupSeqId") int seqId, HttpServletRequest request ) throws IOException, InvalidKeyException, NumberFormatException, NoSuchAlgorithmException, IllegalAccessException, InvalidKeySpecException, CertificateException, NoSuchProviderException, SignatureException {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		adminService.registerGroup( seqId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<>( entities, HttpStatus.CREATED );

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("applied-group/{groupId}/solution/{appliedSolutionSeqId}")
	public ResponseEntity<?> registerAppliedSolution( @PathVariable("appliedSolutionSeqId") int seqId, HttpServletRequest request ) throws IOException, InvalidKeyException, NumberFormatException, NoSuchAlgorithmException, IllegalAccessException, InvalidKeySpecException, CertificateException, NoSuchProviderException, SignatureException {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		adminService.registerSolution( seqId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<>( entities, HttpStatus.CREATED );

		webAuditService.insertAudit( request );

		return entity;
	}
}
