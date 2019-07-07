package net.glaso.ca.framework.wrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestWrapper extends HttpServletRequestWrapper {

	private final String body;
	
	public RequestWrapper( HttpServletRequest request ) throws IOException {
		super(request);
		
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		 
		try {
			InputStream iStream = request.getInputStream();
			
			if ( iStream != null ) {
				br = new BufferedReader( new InputStreamReader( iStream ) );
			
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ( (bytesRead = br.read( charBuffer ) ) > 0 ) {
					sb.append( charBuffer, 0, bytesRead );
				}
			} else {
				sb.append( "" );
			}
		} finally {
			if ( br != null ) { try { br.close(); } catch( IOException e ) {} }
		}
		
		body = sb.toString();
	}
	
	@Override
	public ServletInputStream getInputStream() {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( body.getBytes() );
		
		ServletInputStream servletInputStream = new ServletInputStream() {
			public int read() {
				return byteArrayInputStream.read();
			}

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
			}
		};
		
		return servletInputStream;
	}
	
	@Override
	public BufferedReader getReader() {
		return new BufferedReader( new InputStreamReader( this.getInputStream() ) );
	}
	
	public String getBody() {
		return this.body;
	}
}
