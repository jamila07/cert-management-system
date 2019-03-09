package com.dreamsecurity.ca.business.cert.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.dreamsecurity.ca.business.cert.vo.CertVo;
import com.dreamsecurity.ca.business.cert.vo.KeyVo;

@Repository
public class CertDao {
	
	@Inject
	private SqlSession session;
	
	private static String certNamespace="com.dreamsecurity.mapper.cert";
	private static String keyNamespace="com.dreamsecurity.mapper.key";
	
	public List<Map<String, Object>> selectCertList( String sessionId ) {
		Map<String, Object> paramMap = new HashMap<>();
		
		paramMap.put( "id", sessionId );
		return session.selectList( certNamespace + ".selectCertList", paramMap );
	}
	
	public CertVo selectCertBinary( CertVo vo ) {
		return session.selectOne( certNamespace + ".selectCertBinary", vo );
	}
	
	public KeyVo selectKeyBinary( CertVo vo ) {
		return session.selectOne( keyNamespace + ".selectKeyBinary", vo );
	}
	
	public CertVo selectCertUsingSubject( CertVo vo ) {
		return session.selectOne( certNamespace + ".selectCertUsingSubject", vo );
	}
	
	public CertVo selectCertInfoOneUsingDN( CertVo vo ) {
		return session.selectOne( certNamespace + ".selectCertInfoOneUsingDN", vo );
	}
	
	public List<CertVo> selectRootInterCertUsingOuType( CertVo vo ) {
		return session.selectList( certNamespace + ".selectRootInterCertUsingOuType",vo );
	}
	public int selectCertSerialNumber( CertVo vo ) {
		int maxCnt;
		
		try {
			maxCnt = session.selectOne( certNamespace + ".selectMaxSerialNumber", vo );
		} catch ( NullPointerException e ) {
			return 0;
		}
		
		return maxCnt;
	}
	
	public int insertKeyInfo( KeyVo vo ) {
		int val = session.insert( keyNamespace + ".insertKeyInfo", vo );
		return val;
	}
	
	public int insertCertInfo( CertVo vo ) {
		return session.insert( certNamespace + ".insertCertInfo", vo );
	}
	
	public CertVo selectRootCertAndKeyInfoOne() {
		return session.selectOne( certNamespace + ".selectRootCertAndKeyInfoOne" );
	}
	
	public CertVo selectIntermediateCertAndKeyInfoOne( CertVo vo ) {
		return session.selectOne( certNamespace + ".selectIntermediateCertAndKeyInfoOne", vo );
	}
	
	public boolean hasRootCa() {
		if ( session.selectOne( certNamespace + ".selectRootCaOne" ) == null ) {
			return true;
		} else return false;
	}
	
	public boolean hasIntermediateCa( CertVo vo ) {
		if ( session.selectOne( certNamespace + ".selectIntermediateCaOne", vo ) == null ) {
			return true;
		} else return false;
	}
	
}
