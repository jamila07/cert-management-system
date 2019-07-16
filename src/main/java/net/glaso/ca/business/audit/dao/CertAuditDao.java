package net.glaso.ca.business.audit.dao;

import net.glaso.ca.business.audit.vo.CertAuditVo;
import net.glaso.ca.business.common.domain.Criteria;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CertAuditDao {

	private final SqlSession session;

	@Autowired
	public CertAuditDao( SqlSession session ) {
		this.session = session;
	}

	private static String namespace="net.glaso.mapper.audit";

	public List<Map<String, Object>> selectCertAuditList( Criteria cri ) {
		Map<String, Object> paramMap = new HashMap<>();

		paramMap.put( "cri", cri );

		return session.selectList( namespace + ".selectCertAuditList", paramMap );
	}

	public int selectCertAuditListCnt() {
		return session.selectOne( namespace + ".selectCertAuditListCnt" );
	}

	public void insertCertAudit( CertAuditVo vo ) {
		session.insert( namespace + ".insertCertAudit", vo );
	}
}
