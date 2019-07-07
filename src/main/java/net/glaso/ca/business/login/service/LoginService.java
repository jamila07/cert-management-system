package net.glaso.ca.business.login.service;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import net.glaso.ca.business.user.dao.UserDao;
import net.glaso.ca.business.user.vo.UserVo;
import org.springframework.stereotype.Service;

import net.glaso.ca.business.login.common.LoginConstants;
import net.glaso.ca.framework.utils.CaUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LoginService {
	
	@Resource
	private UserDao userDao;
	
	public boolean login( HttpServletRequest request ) throws JsonParseException, JsonMappingException, IOException, NoSuchAlgorithmException {
		HttpSession session = request.getSession();
		
		ObjectMapper mapper = new ObjectMapper();
		// 여기 확인
		UserVo requestedVo = mapper.readValue( request.getAttribute( "body" ).toString(), UserVo.class );
		
		UserVo selectedVo = userDao.selectOneUser( requestedVo );
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		md.update( DatatypeConverter.parseHexBinary( selectedVo.getPassword() ) );
		md.update( DatatypeConverter.parseHexBinary( session.getId() ) );

		System.out.println( session.getId() );
		System.out.println( selectedVo.getPassword() );

		String computedPw = CaUtils.convertByteArrayToHexString( md.digest( ));

		System.out.println( computedPw );

		if ( requestedVo.getSha256Pw().toLowerCase().equals( computedPw.toLowerCase() ) ) {
			session.setAttribute( LoginConstants.SESSION_ID, selectedVo.getId() );
			session.setAttribute( LoginConstants.SESSION_NAME, selectedVo.getName() );
			session.setAttribute( LoginConstants.SESSION_JOBLEVEL, selectedVo.getJobLevel() );
			session.setAttribute( LoginConstants.SESSION_DEPART, selectedVo.getDepartTeam() );
			session.setAttribute( LoginConstants.SESSION_AUTH, selectedVo.getState() );
			session.setAttribute( LoginConstants.SESSION_TIME, new Date().getTime() );
			
			return true;
		} else {
			return false;
		}
	}
}
