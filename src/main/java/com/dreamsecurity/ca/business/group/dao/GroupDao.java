package com.dreamsecurity.ca.business.group.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.dreamsecurity.ca.business.common.domain.Criteria;
import com.dreamsecurity.ca.business.group.vo.GroupSolutionVo;
import com.dreamsecurity.ca.business.group.vo.GroupVo;
import com.dreamsecurity.ca.business.group.vo.UserGroupVo;

@Repository
public class GroupDao {

	@Inject
	private SqlSession session;
	
	private static String namespace="com.dreamsecurity.mapper.group";
	
	public int registerGroup( GroupVo vo ) {
		return session.insert( namespace + ".registerGroup", vo );
	}
	
	public int addUserToGroup( UserGroupVo vo ) {
		return session.insert( namespace + ".addUserToGroup",vo );
	}
	
	public List<Map<String, Object>> selectGroupList( Criteria cri ) {
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put( "cri", cri );
		
		return session.selectList( namespace + ".selectGroupList", paramMap );
	}
	
	public int selectGroupListCnt() {
		return session.selectOne( namespace + ".selectGroupListCnt" );
	}
	
	public List<Map<String, Object>> selectUserGroupApplyList( Criteria cri, int groupId ) {
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put( "cri", cri );
		paramMap.put( "groupId", groupId );
		
		return session.selectList( namespace + ".selectUserGroupApplyList", paramMap );
	}
	
	public int selectUserGroupApplyListCnt( int groupId ) {
		return session.selectOne( namespace + ".selectUserGroupApplyListCnt", groupId );
	}
	
	public List<Map<String, Object>> selectGroupApplyList( Criteria cri ) {
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put( "cri", cri );
		
		return session.selectList( namespace + ".selectGroupApplyList" , paramMap );
	}
	
	public int selectGroupApplyListCnt() {
		return session.selectOne( namespace + ".selectGroupApplyListCnt" );
	}
	
	public List<Map<String, Object>> selectGroupSolutionApplyList( Criteria cri ) {
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put( "cri", cri );
		
		return session.selectList( namespace + ".selectGroupSolutionApplyList" , paramMap );
	}
	
	public int selectGroupSolutionApplyListCnt() {
		return session.selectOne( namespace + ".selectGroupSolutionApplyListCnt" );
	}
	
	public int updateUserState( UserGroupVo vo ) {
		return session.update( namespace + ".updateUserState", vo );
	}
	
	public int updateUserStateUsingGroupId( UserGroupVo vo ) {
		return session.update( namespace + ".updateUserStateUsingGroupId", vo );
	}
	
	public int updateGroupStateUsingGroupId( GroupVo vo ) {
		return session.update( namespace + ".updateGroupStateUsingGroupId", vo );
	}
	
	public int updateGroupSolutionStateUsingGroupId( GroupSolutionVo vo ) {
		return session.update( namespace + ".updateGroupSolutionStateUsingGroupId", vo );
	}
	
	public UserGroupVo selectGroupMaster( UserGroupVo vo ) {
		return session.selectOne( namespace + ".selectGroupMaster", vo );
	}
	
	public List<UserGroupVo> selectUserGroupList( UserGroupVo userGroupVo ) {
		return session.selectList( namespace + ".selectUserGroupList", userGroupVo );
	}
	
	public void insertUserToGroup( UserGroupVo vo ) {
		session.insert( namespace + ".insertUserToGroup", vo );
	}
	
	public void deleteUserToGroup( UserGroupVo vo ) {
		session.delete( namespace + ".deleteUserToGroup", vo );
	}
	
	public void insertGroupSolution( GroupSolutionVo vo ) {
		session.insert( namespace + ".insertGroupSolution", vo );
	}
	
	public List<GroupVo> selectJoinedGroupList( String userId ) {
		return session.selectList( namespace + ".selectJoinedGroupList", userId );
	}
	
	public GroupVo selectUserSolutionJoinUsingGroupId( GroupVo vo ) {
		return session.selectOne( namespace + ".selectUserSolutionJoinUsingGroupId", vo );
	}
}
