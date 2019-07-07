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
	
	public static byte[] convertBigIntegerToByteArray( BigInteger b ) {
		byte[] array = b.toByteArray();
		
		if (array[0] == 0) {
		    byte[] tmp = new byte[array.length - 1];
		    System.arraycopy(array, 1, tmp, 0, tmp.length);
		    array = tmp;
		}
		
		return array;
	}
	
	public static byte[] convertHexStringToByteArray( String hexString ) {
		int len = hexString.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ( (Character.digit( hexString.charAt(i), 16 ) << 4 )
					+ Character.digit( hexString.charAt(i+1), 16 ) );
		}
		
		return data;
	}
	
	public static char[] convertBytearrayToCharArray( byte[] bytes ) {
		char[] chars = new char[bytes.length/2];
		
		for ( int i=0; i< chars.length; i++ ) {
			chars[i] = (char) ( ( ( bytes[i*2] & 0xff ) << 8 ) + ( bytes[i * 2 + 1] & 0xff ) );
		}
		
		return chars;

	}
	
	public static ArrayList<Byte> convertHexStringToArrayList(String s) {
		ArrayList al = new ArrayList();
		for (int i = 0; i < s.length() - 1; i += 2) {
			al.add(Byte.valueOf((byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16))));
		}
		return al;
	}
	
	public static String convertByteArrayToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for(byte b : bytes) {
			sb.append(String.format("%02x", b&0xff));
		}
		return sb.toString();
	}
	
	public static byte[] toByteArray(List<Byte> in) {
		int n = in.size();
		byte[] ret = new byte[n];
		for (int i = 0; i < n; ++i) {
			ret[i] = ((Byte) in.get(i)).byteValue();
		}
		return ret;
	}
	
	public static ArrayList<Byte> convertByteArrayToArrayList(byte[] bytes) {
		ArrayList al = new ArrayList();
		for (int i = 0; i < bytes.length; ++i) {
			al.add(Byte.valueOf(bytes[i]));
		}
		return al;
	}
	
	public static String convertArrayListToHexString(ArrayList<Byte> al) {
		StringBuffer buf = new StringBuffer();
		String tmpStr = null;
		for (Byte b : al) {
			tmpStr = String.format("%02X", new Object[]{b});
			buf.append(tmpStr);
		}

		return buf.toString();
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
	
	public static byte[] convertMultiPartFileToBody( byte[] bytes ) {
		byte[] padding = { 0x30, (byte)0xef, (byte)0xbf};
		
		System.out.println( byteArrayIndexOf( padding, bytes ) );
		return null;
	}
}

