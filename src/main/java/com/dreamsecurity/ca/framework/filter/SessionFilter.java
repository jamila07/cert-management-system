package com.dreamsecurity.ca.framework.filter;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.dreamsecurity.ca.business.login.common.LoginConstants;

public class SessionFilter implements Filter {

	private String redirectPage;
 
	private String excludeURL ;

	private String[] excludeURLs;

	Logger logger = Logger.getLogger( SessionFilter.class );

	@Override
	public void init( FilterConfig filterConfig ) {
		logger.info("SessionFilter init with parameters...");
		this.redirectPage = ( String )filterConfig.getInitParameter( "redirectPage" );
		this.excludeURL = ( String )filterConfig.getInitParameter( "excludeURL" );

		if ( excludeURL != null ) {
			StringTokenizer token = new StringTokenizer(excludeURL, ",");
			this.excludeURLs = new String[token.countTokens()];
			for (int i = 0; token.hasMoreTokens(); i++) {
				this.excludeURLs[i] = token.nextToken().trim();
				logger.debug( excludeURLs[i] );
			}
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest)request;
		HttpServletResponse servletResponse = (HttpServletResponse)response;
		String requestURI = servletRequest.getRequestURI();
	
		if( this.isExcludeURL(requestURI) || !this.isRedirect( servletRequest ) ) {
			chain.doFilter(request, response);
		}
		else {
			servletResponse.sendRedirect( this.redirectPage );
		}
	}

	private boolean isRedirect( HttpServletRequest request ) {
		boolean result = false;
		HttpSession session = request.getSession(false);

		if(session == null) {
			result = true;
		}

		if( ! result ) {
			if( request.getSession().getAttribute( LoginConstants.SESSION_ID ) == null) {
				result = true;
			}
			else {
				String operId = (String)request.getSession().getAttribute( LoginConstants.SESSION_ID );
				operId = operId.trim();
				if(operId.equals("") || operId.toLowerCase().equals("null") || operId == null ) {
					result = true;
				}
			}
		}

		return result;
	}
	
	private boolean isExcludeURL(String url) {
		boolean result = false;
		
		if(url == null) {
			return true;
		}
		url = url.toLowerCase();
		this.logger.debug(url);
		for (int i = 0; i < this.excludeURLs.length; i++) {		
			//this.logger.debug(excludeURLs[i]);
			if (url.indexOf(  this.excludeURLs[i].toLowerCase() ) < 0) {
				continue;
			}
			result = true;
			break;
		}
		return result;
	}
}