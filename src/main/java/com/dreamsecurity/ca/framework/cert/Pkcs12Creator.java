package com.dreamsecurity.ca.framework.cert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.security.pkcs12.PKCS12KeyStore;


public class Pkcs12Creator {
	
	private static String PBE_ALG = "PBEWithSHA1AndDESede";
	
	public static byte[] enc( byte[] encodedprivkey, String password ) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, InvalidParameterSpecException, IOException {
		byte[] salt;
		int count = 1020;// hash iteration count;
		
		SecureRandom random = new SecureRandom();
		salt = new byte[8];
		random.nextBytes( salt );

		// Create PBE parameter set
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec( salt, count );
		PBEKeySpec pbeKeySpec = new PBEKeySpec( password.toCharArray() );
		SecretKeyFactory keyFac = SecretKeyFactory.getInstance( PBE_ALG );
		SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);
		
		Cipher pbeCipher = Cipher.getInstance( PBE_ALG );
		
		// Initialize PBE Cipher with key and parameters
		pbeCipher.init( Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec );
		
		// Encrypt the encoded Private Key with the PBE key
		byte[] ciphertext = pbeCipher.doFinal( encodedprivkey );
		
		// Now construct  PKCS #8 EncryptedPrivateKeyInfo object
		AlgorithmParameters algParams = AlgorithmParameters.getInstance( PBE_ALG );
		algParams.init( pbeParamSpec );
		
		EncryptedPrivateKeyInfo encInfo = new EncryptedPrivateKeyInfo( algParams, ciphertext);
		
		// and here we have it! a DER encoded PKCS#8 encrypted key!
		return encInfo.getEncoded();
	}
	
	public static byte[] generatePkcs12( String pin, String alias, byte[] priKey, Certificate[] certChain ) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		PKCS12KeyStore pkcs12kStr = new PKCS12KeyStore();
		pkcs12kStr.engineSetKeyEntry( alias, priKey, certChain );

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		pkcs12kStr.engineStore( baos, pin.toCharArray() );
		
		return baos.toByteArray();
	}
}
