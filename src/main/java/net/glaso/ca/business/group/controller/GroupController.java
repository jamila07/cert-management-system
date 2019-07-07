package net.glaso.ca.business.group.controller;

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

import net.glaso.ca.business.group.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import net.glaso.ca.business.audit.service.WebAuditService;
import net.glaso.ca.business.common.domain.PageMaker;
import net.glaso.ca.business.common.mvc.controller.CommonController;
import net.glaso.ca.business.group.vo.UserGroupVo;
import net.glaso.ca.framework.utils.CaUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
@RequestMapping("/group")
public class GroupController extends CommonController {

	@Inject
	private GroupService service;

	@Inject
	private WebAuditService webAuditService;

	@SuppressWarnings("unchecked")
	@GetMapping("")
	public  <T>T listOrPage( HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException {
		if ( request.getParameterMap().isEmpty() ) {
			return (T) page( request );
		} else {
			return (T) showGroupList( request, response );
		}
	}

	private ModelAndView page( HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView();

		mv.setViewName( "group/view");
		webAuditService.insertAudit( request );

		return mv;
	}

	private ResponseEntity<?> showGroupList( HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = service.showGroupList( request, pageMaker.getCri() );
		int listCnt = service.showGroupListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("")
	public ResponseEntity<?> registerGroup( HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		service.registerGroup( request );
		entities.put( "redirect", "/group" );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@GetMapping("{groupId}/user")
	public ResponseEntity<?> showUserList( @PathVariable("groupId") int groupId, HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException, JsonParseException, JsonMappingException, IOException {
		String oper = request.getParameter( "oper" );

		if ( oper != null && oper.equals( "requested" ) ) {
			return showAppliedUserList( groupId, request, response );
		} else if ( oper != null && oper.equals( "registered" ) ) {
			return showRegisteredUserList( groupId, request, response );
		} else {
			throw new IllegalArgumentException( "operation is null." );
		}
	}


	private ResponseEntity<?> showAppliedUserList( int groupId, HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = service.showUserGroupApplyList( request, pageMaker.getCri(), groupId );
		int listCnt = service.showUserGroupApplyListCnt( groupId );

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	private ResponseEntity<?> showRegisteredUserList( int groupId, HttpServletRequest request, HttpServletResponse response ) throws IllegalArgumentException, IllegalAccessException, JsonParseException, JsonMappingException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();
		List<Map<String, Object>> voListMap = new ArrayList<Map<String, Object>>();

		for ( UserGroupVo vo : service.showUserList( groupId ) ) {
			voListMap.add( CaUtils.voToMap( vo ) );
		}

		entities.put( "data", voListMap );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("{groupId}/user/{userId}")
	public ResponseEntity<?> registerAppliedUser( @PathVariable("groupId") int groupId, @PathVariable("userId") String userId, HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException, InvalidKeyException, NumberFormatException, NoSuchAlgorithmException, IllegalAccessException, InvalidKeySpecException, CertificateException, NoSuchProviderException, SignatureException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		service.approveUserToJoinGroup( request, groupId, userId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>( entities, HttpStatus.OK );

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("{groupId}/user")
	public ResponseEntity<?> addUserToGroup( @PathVariable("groupId") int groupId, HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		this.service.addUserToGroup( request, groupId );

		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	// 덜 완성 view 단
	@DeleteMapping("{groupId}/user/{userId}")
	public ResponseEntity<?> removeUserToGroup( @PathVariable("groupId") int groupId, @PathVariable("userId") String userId, HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		this.service.removeUserToGroup( request, groupId, userId );

		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("{groupId}/solution")
	public ResponseEntity<?> applyGroupSolution( @PathVariable("groupId") int groupId, HttpServletRequest request, HttpServletResponse response ) throws JsonParseException, JsonMappingException, IOException {
		ResponseEntity<?> entity = null;
		Map<String, Object> entities = new HashMap<String, Object>();

		service.applyGroupSolution( request, groupId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<Map<String, Object>>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}
}
