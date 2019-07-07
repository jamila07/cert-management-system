package net.glaso.ca.business.cert.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import net.glaso.ca.business.cert.vo.CertVo;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import net.glaso.ca.business.cert.vo.KeyVo;

@Repository
public class CertDao {

	@Inject
	private SqlSession session;

	private static String certNamespace="com.dreamsecurity.mapper.cert";
	private static String keyNamespace="com.dreamsecurity.mapper.key";

	public CertVo selectCertOneUsingCertId(CertVo vo ) {
		return session.selectOne( certNamespace + ".selectCertOneUsingCertId", vo );
	}

	public List<Map<String, Object>> selectCertList( String sessionId ) {
		Map<String, Object> paramMap = new HashMap<>();

		paramMap.put( "id", sessionId );
		return session.selectList( certNamespace + ".selectCertList", paramMap );
	}

	public CertVo selectCertBinary( CertVo vo ) {
		return session.selectOne( certNamespace + ".selectCertBinary", vo );
	}

	public KeyVo selectKeyPairBinary( CertVo vo ) {
		return session.selectOne( keyNamespace + ".selectKeyPairBinary", vo );
	}

	public KeyVo selectPublicKeyBinary( CertVo vo ) {
		return session.selectOne( keyNamespace + ".selectPublicKeyBinary", vo );
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
		vo.setGroupSolutionName( "OU = " + vo.getGroupSolutionName() );

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

	//현재 사용안함 상황보고 지워야함
	@Deprecated
	public CertVo selectCertAndGroupUsingOuType( CertVo vo ) {
		return session.selectOne( certNamespace + ".selectCertAndGroupUsingOuType", vo );
	}

}
