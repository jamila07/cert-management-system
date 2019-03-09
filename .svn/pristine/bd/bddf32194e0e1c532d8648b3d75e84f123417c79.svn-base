package com.dreamsecurity.ca.framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.dreamsecurity.ca.framework.utils.CaUtils;
import com.dreamsecurity.ca.framework.wrapper.RequestWrapper;

public class RequestBodyFilter  implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		request = new RequestWrapper((HttpServletRequest) request);
		
		HttpServletRequest hReq = new RequestWrapper((HttpServletRequest) request);
		request.setAttribute( "body", CaUtils.getHTTPBody( hReq ) );
		
		chain.doFilter( request, response );
	}
}
