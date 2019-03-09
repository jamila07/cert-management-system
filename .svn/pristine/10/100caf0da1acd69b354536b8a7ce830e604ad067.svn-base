package com.dreamsecurity.ca.business.login.service;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.dreamsecurity.ca.business.login.common.LoginConstants;
import com.dreamsecurity.ca.business.user.dao.UserDao;
import com.dreamsecurity.ca.business.user.vo.UserVo;
import com.dreamsecurity.ca.framework.utils.CaUtils;
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
		UserVo requestedVo = mapper.readValue( request.getAttribute( "body" ).toString(), UserVo.class );
		
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		UserVo selectedVo = userDao.selectOneUser( requestedVo );
		String computedPw = CaUtils.convertByteArrayToHexString( messageDigest.digest( ( session.getId() + selectedVo.getPassword() ).getBytes() ) );
		
		if ( requestedVo.getSha256Pw().equals( computedPw ) ) {
			session.setAttribute( LoginConstants.SESSION_ID, selectedVo.getId() );
			session.setAttribute( LoginConstants.SESSION_NAME, selectedVo.getName() );
			session.setAttribute( LoginConstants.SESSION_JOBLEVEL, selectedVo.getJobLevel() );
			session.setAttribute( LoginConstants.SESSION_JOBLEVEL, selectedVo.getDepartTeam() );
			session.setAttribute( LoginConstants.SESSION_TIME, new Date().getTime() );
			session.setAttribute( LoginConstants.SESSION_AUTH, selectedVo.getState() );
			
			return true;
		} else {
			return false;
		}
	}
}
