package net.glaso.ca.framework.interceptor;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.glaso.ca.business.login.common.LoginConstants;
import net.glaso.ca.business.user.dao.UserDao;
import net.glaso.ca.business.user.vo.UserVo;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AdminAuthInterceptor  extends HandlerInterceptorAdapter {

	@Inject
	private UserDao userDao;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		UserVo vo = new UserVo();
		Object sessionId = request.getSession().getAttribute( LoginConstants.SESSION_ID);
		
		if ( sessionId == null ) {
			setErr( response, "꺼지세요" );
			return false;
		}
		
		vo.setLoginId( sessionId.toString() );
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
