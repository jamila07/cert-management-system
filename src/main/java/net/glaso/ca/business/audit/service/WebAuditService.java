package net.glaso.ca.business.audit.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.glaso.ca.business.audit.dao.WebAuditDao;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.glaso.ca.business.audit.vo.WebAuditVo;
import net.glaso.ca.business.common.CommonConstants;
import net.glaso.ca.business.common.domain.Criteria;
import net.glaso.ca.business.login.common.LoginConstants;

@Service
public class WebAuditService {

	@Resource
	private WebAuditDao webAuditDao;

	private static final Logger logger = LoggerFactory.getLogger(WebAuditService.class);

	public List<Map<String,Object>> showList( Criteria cri ) {
		List<Map<String,Object>> voMapList = webAuditDao.selectWebAuditList( cri );

		for ( Map<String, Object> voMap : voMapList ) {
			voMap.put( "date", CommonConstants.dateFormat.format( (Date)voMap.get( "date" ) ) );
			voMap.putIfAbsent( "param", "없음" );
		}

		return voMapList;
	}

	public int showListCnt() {
		return webAuditDao.selectWebAuditListCnt();
	}

	public void insertAudit( HttpServletRequest request ) {
		WebAuditVo vo = new WebAuditVo();

		vo.setRepCode( 200 );

		insertAudit( request, vo );
	}

	public void insertAudit( HttpServletRequest request, Exception e, int repCode ) {
		WebAuditVo vo = new WebAuditVo();

		vo.setRepCode( repCode );
		vo.setErrMsg( e.getMessage() );

		insertAudit( request, vo );
	}

	private void insertAudit( HttpServletRequest request, WebAuditVo vo ) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance( "SHA-256" );

			vo.setClientIp( request.getRemoteAddr() );
			vo.setUrl( request.getRequestURI() );
			vo.setServerIp( request.getServerName() );
			vo.setDate( new Date() );

			try {
				vo.setUserId( request.getSession().getAttribute( LoginConstants.SESSION_ID ).toString() );
			} catch ( NullPointerException e ) {
				vo.setUserId( null );
			}

			JSONObject param;
			if ( request.getAttribute( "body" ) != null ) {
				param = (JSONObject) request.getAttribute( "body" );

				vo.setParam( param.toString() );
			}


			if ( vo.getParam().length() > 512 ) {
				vo.setParam( vo.getParam().substring(0, 511 ) );
			}

			String hashVal = new StringBuilder().append( vo.getClientIp() )
					.append( vo.getUserId() )
					.append( CommonConstants.dateFormat.format( vo.getDate() ) )
					.append( vo.getUrl() ).toString();

			vo.setHash( messageDigest.digest( hashVal.getBytes() ) );

			webAuditDao.insertWebAudit( vo );
		} catch ( NoSuchAlgorithmException ex ) {
			logger.warn( "error while insert failWebAudit, errMsg: " + ex.getMessage() );
		}
	}
}
