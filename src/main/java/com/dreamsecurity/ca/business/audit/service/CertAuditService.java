package com.dreamsecurity.ca.business.audit.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.dreamsecurity.ca.business.audit.dao.CertAuditDao;
import com.dreamsecurity.ca.business.audit.vo.CertAuditVo;
import com.dreamsecurity.ca.business.cert.vo.CertVo;
import com.dreamsecurity.ca.business.common.CommonConstants;
import com.dreamsecurity.ca.business.common.domain.Criteria;
import com.dreamsecurity.ca.business.login.common.LoginConstants;

@Service
public class CertAuditService {

	@Resource
	private CertAuditDao certAuditDao;
	
	public List<Map<String, Object>> showList( HttpServletRequest request, Criteria cri ) {
		List<Map<String,Object>> voMapList = certAuditDao.selectCertAduitList( cri );
		
		for ( Map<String, Object> voMap : voMapList ) {
			voMap.put( "date", CommonConstants.dateFormat.format( (Date)voMap.get( "date" ) ) );
		}
		
		return voMapList;
	}
	
	public int showListCnt() {
		return certAuditDao.selectCertAuditListCnt();
	}
	
	public void insertAudit( HttpServletRequest request, CertVo certVo ) throws NoSuchAlgorithmException {
		CertAuditVo vo = new CertAuditVo();
		
		vo.setResult( 0 );
		
		insertAudit( request, certVo, vo );
	}
	
	public void insertAudit( HttpServletRequest request, CertVo certVo, Exception e ) throws NoSuchAlgorithmException {
		CertAuditVo vo = new CertAuditVo();
		
		vo.setResult( 1 );
		vo.setErrMsg( e.getMessage() );
		
		insertAudit( request, certVo, vo );
	}
	
	private void insertAudit( HttpServletRequest request, CertVo certVo, CertAuditVo vo ) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		
		vo.setClientIp( request.getRemoteAddr() );
		vo.setServerIp( request.getServerName() );
		vo.setDate( new Date() );
		vo.setUserId( request.getSession().getAttribute( LoginConstants.SESSION_ID ).toString() );
		vo.setAction( certVo.getType() );
		
		String requestParam = new StringBuilder().append( "type: ").append( certVo.getType() )
				.append( ", ouType: " ).append( certVo.getOuType() )
				.append( ", citeName: " ).append( certVo.getCiteName() )
//				.append( ", validity: " ).append( certVo.getValidity() )
				.append( ", description: ").append( certVo.getDescription() ).toString();
		
		String hashVal = new StringBuilder().append( vo.getClientIp() )
				.append( vo.getUserId() )
				.append( CommonConstants.dateFormat.format( vo.getDate() ) ).toString();
		
		vo.setRequestParam( requestParam );
		vo.setHash( messageDigest.digest( hashVal.getBytes() ) );
	
		certAuditDao.insertCertAudit( vo );
	}
}