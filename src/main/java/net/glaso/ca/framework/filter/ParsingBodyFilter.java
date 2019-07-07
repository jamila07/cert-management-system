package net.glaso.ca.framework.filter;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class ParsingBodyFilter  implements Filter {

	private static final Logger logger = Logger.getLogger( ParsingBodyFilter.class );
	
	@Override
	public void init( FilterConfig config ) throws ServletException {
		logger.info("-- ParsingBodyFilter init performed... " );
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest alteredRequest = (HttpServletRequest) request;
		if ( !alteredRequest.getMethod().equals( "GET" ) ) {

			StringBuffer jb = new StringBuffer();
			String line = null;
			BufferedReader body = null;

			try {
				body = request.getReader();

				while ( ( line = body.readLine() ) != null ) {
					jb.append(line);
				}

				if ( jb.toString().length() > 0 ) {
					request.setAttribute( "body", new JSONObject( jb.toString() ) );
				}
			} catch ( JSONException e ) {
				throw new JSONException( "invalid Request Body - HINT: JSON FORMAT" );
			} finally {
				body.close();
			}
		}

		chain.doFilter( request, response );
	}
}
