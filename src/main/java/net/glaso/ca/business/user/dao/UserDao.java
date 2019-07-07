package net.glaso.ca.business.user.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import net.glaso.ca.business.user.vo.AppliedUserInfoVo;
import net.glaso.ca.business.user.vo.UserVo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import net.glaso.ca.business.common.domain.Criteria;

@Repository
public class UserDao {
	
	@Inject
	private SqlSession session;
	
	private static String namespace="net.glaso.mapper.user";
	
	public void insertUser( UserVo vo ) {
		session.insert( namespace + ".insertUser", vo );
	}
	
	public void insertAplliedUser( AppliedUserInfoVo vo ) {
		session.insert( namespace + ".insertAppliedUser", vo );
	}
	
	public List<Map<String, Object>> selectAppliedUserList( Criteria cri ) {
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put( "cri", cri );
		
		return session.selectList( namespace + ".selectAppliedUserList", paramMap );
	}
	
	public int selectAppliedUserListCnt() {
		return session.selectOne( namespace + ".selectAppliedUserListCnt" );
	}
	
	public AppliedUserInfoVo selectAppliedUserInfoOne( int seqId ) {
		return session.selectOne( namespace + ".selectAppliedUserInfoOne", seqId );
	}
	
	public void updateAppliedUserState( AppliedUserInfoVo vo ) {
		session.update( namespace + ".updateAppliedUserState", vo );
	}
	
	public UserVo selectOneUser( UserVo vo ) {
		UserVo returnVo = session.selectOne( namespace + ".selectOneUser", vo );
		if ( returnVo == null ) {
			throw new NullPointerException( "User Id does not found." );
		}
		
		return returnVo;
	}
	
	public List<Map<String, Object>> selectUserList() { 
		return session.selectList( namespace + ".selectUserList" );
	}
	
	public UserVo selectUserOne4ChkOverlap( UserVo vo ) {
		return session.selectOne( namespace + ".selectUserOne4ChkOverlap", vo );
	}
	
	public UserVo selectAppliedUserOne4ChkOverlap( UserVo vo ) {
		return session.selectOne( namespace + ".selectAppliedUserOne4ChkOverlap", vo );
	}
}
