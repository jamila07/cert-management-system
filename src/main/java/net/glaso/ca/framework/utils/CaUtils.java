/**
 * KMIPUtils.java
 * -----------------------------------------------------------------
 *     __ __ __  ___________ 
 *    / //_//  |/  /  _/ __ \	  .--.
 *   / ,<  / /|_/ // // /_/ /	 /.-. '----------.
 *  / /| |/ /  / // // ____/ 	 \'-' .--"--""-"-'
 * /_/ |_/_/  /_/___/_/      	  '--'
 * 
 * -----------------------------------------------------------------
 * Description for class
 * This class is a collection of multiple used functions
 *
 * @author     Stefanie Meile <stefaniemeile@gmail.com>
 * @author     Michael Guster <michael.guster@gmail.com>
 * @org.       NTB - University of Applied Sciences Buchs, (CH)
 * @copyright  Copyright ï¿½ 2013, Stefanie Meile, Michael Guster
 * @license    Simplified BSD License (see LICENSE.TXT)
 * @version    1.0, 2013/08/09
 * @since      Class available since Release 1.0
 *
 * 
 */
package net.glaso.ca.framework.utils;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.glaso.ca.business.common.CommonConstants;

public class CaUtils {
	
	private CaUtils() {
		throw new AssertionError();
	}
	
	public static int byteArrayIndexOf( byte[] to, byte[] from ) {
		int toIdx=0;
		int result=-1;
		
		for ( int fromIdx=0; fromIdx<from.length; fromIdx++ ) {
			if ( to[toIdx] == from[fromIdx] ) {
				
				if ( result == -1 )	result = fromIdx;
				if ( to.length == ++toIdx ) return result;
				
			} else {
				toIdx = 0;
				result = -1;
			}
		}
		
		return -1;
	}

	public static X509Certificate bytesToX509Cert( byte[] bytes ) throws CertificateException {
		CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
		
		return (X509Certificate)certFactory.generateCertificate( new ByteArrayInputStream( bytes ) );
	}
	
	public static PrivateKey pkcs8bytesToPrivateKeyObj( byte[] bytes ) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance( "RSA" );
		return keyFactory.generatePrivate( new PKCS8EncodedKeySpec( bytes ) );
	}
	
	public static Map<String, Object> voToMap( Object vo ) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = vo.getClass().getDeclaredFields();
		Map<String, Object> voMap = new HashMap<>();
		for( Field f : fields ) {
			f.setAccessible( true );
			System.out.println( f.get(vo ) );
			if ( f.get( vo ) != null ) {
				if ( f.getType().isAssignableFrom( Date.class ) ) {
					Date date = (Date)f.get( vo );
					voMap.put( f.getName(), CommonConstants.dateFormat.format( date ) );
				} else {
					voMap.put( f.getName(), f.get( vo ) );
				}
			}
		}

		return voMap;
	}

	// upper the jdk 1.7
	public static byte[] generateSecureRandomString( int byteLength ) throws NoSuchAlgorithmException {
		byte[] rng;

		try{
			rng = generateSecureRandomString( "NativePRNGNonBlocking", byteLength );
		} catch ( NoSuchAlgorithmException e ) {
			System.out.println( "this server is not linux, while check create token.");
			rng = generateSecureRandomString( "Windows-PRNG", byteLength );
		}

		return rng;
	}

	private static byte[] generateSecureRandomString( String algo, int byteLength ) throws NoSuchAlgorithmException {
		byte[] seed;
		SecureRandom sRandom;
		byte[] random = new byte[byteLength];

		sRandom = SecureRandom.getInstance( algo );

		seed = SecureRandom.getInstance( algo ).generateSeed( 55 );

		sRandom.setSeed( seed );

		sRandom.nextBytes( random );

		return random;
	}
}

