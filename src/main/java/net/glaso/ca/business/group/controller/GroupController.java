package net.glaso.ca.business.group.controller;

import net.glaso.ca.business.audit.service.WebAuditService;
import net.glaso.ca.business.common.domain.PageMaker;
import net.glaso.ca.business.common.mvc.controller.CommonController;
import net.glaso.ca.business.group.service.GroupService;
import net.glaso.ca.business.group.vo.UserGroupVo;
import net.glaso.ca.framework.utils.CaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/group")
public class GroupController extends CommonController {

	private final GroupService service;

	private final WebAuditService webAuditService;

	@Autowired
	public GroupController( GroupService service, WebAuditService webAuditService ) {
		this.service = service;
		this.webAuditService = webAuditService;
	}

	@SuppressWarnings("unchecked")
	@GetMapping("")
	public  <T>T listOrPage( HttpServletRequest request ) throws IllegalArgumentException {
		if ( request.getParameterMap().isEmpty() ) {
			return (T) page( request );
		} else {
			return (T) showGroupList( request );
		}
	}

	private ModelAndView page( HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView();

		mv.setViewName( "group/view");
		webAuditService.insertAudit( request );

		return mv;
	}

	private ResponseEntity<?> showGroupList( HttpServletRequest request ) throws IllegalArgumentException {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = service.showGroupList( pageMaker.getCri() );
		int listCnt = service.showGroupListCnt();

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entities.put( "status", "success" );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("")
	public ResponseEntity<?> registerGroup( HttpServletRequest request ) {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		service.registerGroup( request );
		entities.put( "redirect", "/group" );
		entities.put( "status", "success" );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@GetMapping("{groupId}/user")
	public ResponseEntity<?> showUserList( @PathVariable("groupId") int groupId, HttpServletRequest request ) throws IllegalArgumentException, IllegalAccessException {
		String oper = request.getParameter( "oper" );

		if ( oper != null && oper.equals( "requested" ) ) {
			return showAppliedUserList( groupId, request );
		} else if ( oper != null && oper.equals( "registered" ) ) {
			return showRegisteredUserList( groupId, request );
		} else {
			throw new IllegalArgumentException( "operation is null." );
		}
	}


	private ResponseEntity<?> showAppliedUserList( int groupId, HttpServletRequest request ) throws IllegalArgumentException {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		int page = request.getParameter( "page" ) != null ? Integer.parseInt( request.getParameter( "page" ) ) : 10;

		PageMaker pageMaker = super.setPaging( page );
		List<Map<String, Object>> list = service.showUserGroupApplyList( pageMaker.getCri(), groupId );
		int listCnt = service.showUserGroupApplyListCnt( groupId );

		entities = super.commonListing( entities, list, listCnt, pageMaker );
		entities.put( "status", "success" );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	private ResponseEntity<?> showRegisteredUserList( int groupId, HttpServletRequest request ) throws IllegalArgumentException, IllegalAccessException {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();
		List<Map<String, Object>> voListMap = new ArrayList<>();

		for ( UserGroupVo vo : service.showUserList( groupId ) ) {
			voListMap.add( CaUtils.voToMap( vo ) );
		}

		entities.put( "data", voListMap );
		entities.put( "status", "success" );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("{groupId}/user/{userId}")
	public ResponseEntity<?> registerAppliedUser( @PathVariable("groupId") int groupId, @PathVariable("userId") String userId, HttpServletRequest request ) throws IllegalAccessException {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		service.approveUserToJoinGroup( request, groupId, userId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<>( entities, HttpStatus.OK );

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("{groupId}/user")
	public ResponseEntity<?> addUserToGroup( @PathVariable("groupId") int groupId, HttpServletRequest request ) {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		this.service.addUserToGroup( request, groupId );

		entities.put( "status", "success" );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	// 덜 완성 view 단
	@DeleteMapping("{groupId}/user/{userId}")
	public ResponseEntity<?> removeUserToGroup( @PathVariable("groupId") int groupId, @PathVariable("userId") String userId, HttpServletRequest request ) {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		this.service.removeUserToGroup( groupId, userId );

		entities.put( "status", "success" );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}

	@PostMapping("{groupId}/solution")
	public ResponseEntity<?> applyGroupSolution( @PathVariable("groupId") int groupId, HttpServletRequest request ) {
		ResponseEntity<?> entity;
		Map<String, Object> entities = new HashMap<>();

		service.applyGroupSolution( request, groupId );
		entities.put( "status", "success" );
		entity = new ResponseEntity<>(entities, HttpStatus.OK);

		webAuditService.insertAudit( request );

		return entity;
	}
}
