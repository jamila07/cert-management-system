package com.dreamsecurity.ca.framework.interceptor;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.dreamsecurity.ca.business.login.common.LoginConstants;
import com.dreamsecurity.ca.business.user.dao.UserDao;
import com.dreamsecurity.ca.business.user.vo.UserVo;

public class AdminAuthInterceptor  extends HandlerInterceptorAdapter {

	@Inject
	private UserDao userDao;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		UserVo vo = new UserVo();
		vo.setLoginId( request.getSession().getAttribute( LoginConstants.SESSION_ID).toString() );
		vo = userDao.selectOneUser( vo );

		if ( vo.getState() == 0 ) return true;
		else {
			setErr( response, "꺼지세요" );
			return false;
		}
	}

	private void setErr( HttpServletResponse response, String errMsg ) throws IOException {
		response.sendError( 401, "권한없어욤");
	}
}
