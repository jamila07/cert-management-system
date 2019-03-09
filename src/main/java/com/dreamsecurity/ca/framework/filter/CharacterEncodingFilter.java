package com.dreamsecurity.ca.framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.Logger;

public class CharacterEncodingFilter implements Filter {

	private FilterConfig config;
	private static final Logger logger = Logger.getLogger( CharacterEncodingFilter.class );
	
	@Override
	public void init( FilterConfig config ) throws ServletException {
		logger.info("-- CharacterEncodingFilter init performed... " );
		this.config = config;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain nextFilter)
			throws IOException, ServletException {
		request.setCharacterEncoding( config.getInitParameter("encoding") );
		response.setCharacterEncoding( config.getInitParameter("encoding") );

	
		nextFilter.doFilter( request, response );
		
	} 

	@Override
	public void destroy() {}

}
