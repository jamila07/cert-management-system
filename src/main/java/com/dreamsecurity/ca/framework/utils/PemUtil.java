package com.dreamsecurity.ca.framework.utils;

import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

public class PemUtil {
	private static final Pattern KEY_PATTERN = Pattern.compile(
            "-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+" + // Header
                    "([a-z0-9+/=\\r\\n]+)" +                       // Base64 text
                    "-+END\\s+.*PRIVATE\\s+KEY[^-]*-+"           // Footer
    );
	
	private static final String RSA_PRI_START = "-----BEGIN RSA PRIVATE KEY-----\n";
	private static final String RSA_PRI_END = "\n-----END RSA PRIVATE KEY-----\n";
	private static final String CERT_START = "-----BEGIN CERTIFICATE-----\n";
	private static final String CERT_END = "\n-----END CERTIFICATE-----\n";
	
	public static String pkcs8ToPem( byte[] pri ) {
		String b64Key = DatatypeConverter.printBase64Binary( pri );
		String pemKey = new StringBuilder()
				.append( RSA_PRI_START )
				.append( slice64( b64Key ) )
				.append( RSA_PRI_END ).toString();
		
		System.out.println( pemKey );
		return pemKey;
	}
	
	public static String certToPem( byte[] cert ) {
		String b64Cert = DatatypeConverter.printBase64Binary( cert );
		String pemCert = new StringBuilder()
				.append( CERT_START )
				.append( slice64( b64Cert ) )
				.append( CERT_END ).toString();
		
		System.out.println( pemCert );
		
		return pemCert;
	}

	private static StringBuilder slice64( String str ) {
		StringBuilder sb = new StringBuilder();
		for ( int i=1; i<=str.length(); i++ ) {
			if ( (i % 64) != 0 )
				sb.append( str.charAt( i-1 ) );
			else {
				sb.append( str.charAt( i-1 ) );
				if ( !(i == str.length()) ) 
					sb.append("\n");
			}
		}
		
		return sb;
	}
	
}
