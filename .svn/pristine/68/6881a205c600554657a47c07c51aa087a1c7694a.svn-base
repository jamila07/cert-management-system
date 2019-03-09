package com.dreamsecurity.ca.business.audit.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.dreamsecurity.ca.business.audit.vo.WebAuditVo;
import com.dreamsecurity.ca.business.common.domain.Criteria;

@Repository
public class WebAuditDao {
	
	@Inject
	private SqlSession session;
	
	private static String namespace="com.dreamsecurity.mapper.audit";
	
	public List<Map<String, Object>> selectWebAduitList( Criteria cri ) {
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put( "cri", cri );
		
		return session.selectList( namespace + ".selectWebAduitList", paramMap );
	}
	
	public int selectWebAuditListCnt() {
		return session.selectOne( namespace + ".selectWebAuditListCnt" );
	}
	
	public void insertWebAudit( WebAuditVo vo ) {
		session.insert( namespace + ".insertWebAudit", vo );
	}
}
