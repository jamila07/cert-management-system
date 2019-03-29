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
	
	private static final String ENC_PRI_START = "-----BEGIN ENCRYPTED PRIVATE KEY-----\n";
	private static final String ENC_PRI_END = "\n-----END ENCRYPTED PRIVATE KEY-----\n";
	
	private static final String PRI_START = "-----BEGIN PRIVATE KEY-----\n";
	private static final String PRI_END = "\n-----END PRIVATE KEY-----\n";
	
	private static final String PUB_START = "-----BEGIN PUBLIC KEY-----\n";
	private static final String PUB_END = "\n-----END PUBLIC KEY-----\n";
	
	public static String rsaPriToPem( byte[] pri ) {
		String b64Key = DatatypeConverter.printBase64Binary( pri );
		String pemKey = new StringBuilder()
				.append( RSA_PRI_START )
				.append( slice64( b64Key ) )
				.append( RSA_PRI_END ).toString();
		
		return pemKey;
	}
	
	public static String certToPem( byte[] cert ) {
		String b64Cert = DatatypeConverter.printBase64Binary( cert );
		String pemCert = new StringBuilder()
				.append( CERT_START )
				.append( slice64( b64Cert ) )
				.append( CERT_END ).toString();
		
		return pemCert;
	}
	
	public static String priKeyToPem( byte[] key ) {
		String b64Key = DatatypeConverter.printBase64Binary( key );
		String pemKey = new StringBuilder()
				.append( PRI_START )
				.append( slice64( b64Key ) )
				.append( PRI_END ).toString();
		
		return pemKey;
	}
	
	public static String encryptedPriKeyToPem( byte[] key ) {
		String b64Key = DatatypeConverter.printBase64Binary( key );
		String pemKey = new StringBuilder()
				.append( ENC_PRI_START )
				.append( slice64( b64Key ) )
				.append( ENC_PRI_END ).toString();
		
		return pemKey;
	}	
	
	public static String publicKeyToPem( byte[] key ) {
		String b64Key = DatatypeConverter.printBase64Binary( key );
		String pemKey = new StringBuilder()
				.append( PUB_START )
				.append( slice64( b64Key ) )
				.append( PUB_END ).toString();
		
		return pemKey;
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
