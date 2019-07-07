package net.glaso.ca.business.audit.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import net.glaso.ca.business.audit.vo.CertAuditVo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import net.glaso.ca.business.common.domain.Criteria;

@Repository
public class CertAuditDao {

	@Inject
	private SqlSession session;

	private static String namespace="com.dreamsecurity.mapper.audit";

	public List<Map<String, Object>> selectCertAduitList( Criteria cri ) {
		Map<String, Object> paramMap = new HashMap<>();

		paramMap.put( "cri", cri );

		return session.selectList( namespace + ".selectCertAduitList", paramMap );
	}

	public int selectCertAuditListCnt() {
		return session.selectOne( namespace + ".selectCertAuditListCnt" );
	}

	public void insertCertAudit( CertAuditVo vo ) {
		session.insert( namespace + ".insertCertAudit", vo );
	}
}
