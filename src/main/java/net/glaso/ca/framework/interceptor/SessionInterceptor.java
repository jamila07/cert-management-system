package net.glaso.ca.framework.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.glaso.ca.business.login.common.LoginConstants;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SessionInterceptor extends HandlerInterceptorAdapter {

	Logger logger = Logger.getLogger( SessionInterceptor.class );

	private String redirectPage = "/";
	private long sessionTimeOut = 300000;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception { 
		HttpSession session = request.getSession( false );
		
		System.out.println( request.getRequestURI());
		if( this.hasSession( session ) ) {
			if ( !isSessionExpired( session ) ) {
				session.invalidate();
				this.sendRedirect( response, "잘못된 접근 : 세션 만료." );
				
				return false;
			}
			
			return true;
		}
		else {
			this.sendRedirect( response, "잘못된 접근 : 로그인하세요." );
			
			return false;
		}
	}
	
	private void sendRedirect( HttpServletResponse response, String msg ) throws IOException {
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter printWriter = response.getWriter();
		printWriter.println("<script>alert('" + msg + "');location.replace('" + this.redirectPage + "');</script>");
		printWriter.flush();
		printWriter.close();
	}
	
	private boolean hasSession( HttpSession session ) {
		

		if( session == null ) {
			return false;
		}
		
		if( session.getAttribute( LoginConstants.SESSION_ID ) == null) {
			return false;
		}
		else {
			String operId = (String)session.getAttribute( LoginConstants.SESSION_ID );
			operId = operId.trim();
			
			if(operId.equals("") || operId.toLowerCase().equals("null") ) {
				return false;
			}
		}
		

		return true;
	}
	
	private boolean isSessionExpired( HttpSession session ) {
		long currentTime = new Date().getTime();
		long sessionCreTime = (Long)session.getAttribute( LoginConstants.SESSION_TIME );
		
		if ( currentTime > ( sessionTimeOut + sessionCreTime ) ) {
			return false;
		}
		
		return true;
	}
	
	private void authFailProcess( HttpServletResponse response, String errMsg ) throws IOException {
		JSONObject jObj = new JSONObject();
		
		jObj.put( "status", "fail" );
		jObj.put( "errMsg", errMsg );
		
		response.setStatus( 401 );
		response.setContentType( "application/json" );
		response.setCharacterEncoding( "UTF-8" );
		response.getWriter().write( jObj.toString() );
		response.getWriter().flush();
		response.getWriter().close();
	}
	
}