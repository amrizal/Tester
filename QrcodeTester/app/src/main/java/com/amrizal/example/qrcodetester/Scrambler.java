package com.amrizal.example.qrcodetester;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;




/**
 * <code>Scrambler</code> is used to encrypt, encode and decrypt, decode strings.<br>
 * 
 * @since 2008
 * @author chee hoo
 */

public class Scrambler
{
	/**
	 * Encryption scheme
	 */
	public static final String 	DEFAULT_ENCRYPTION_SCHEME = "AES";
	
	/**
	 * Encryption key
	 */
	public static final String	DEFAULT_ENCRYPTION_KEY	= "d09d778ce61bef10";
	
	/**
	 * Unicode format
	 */
	private static final String	UNICODE_FORMAT	= "UTF8";

	/**
	 * An object used to log messages for debugging purposes.
	 */
	private static final String TAG = "Scrambler";
	
	
	/**
	 * Encrypts and encodes (base64) a string. 
	 * 
	 * @param encryptionKey Represents encryption key (merchant's secret). 
	 * @param unencryptedString Represents a string. 
	 * @return Encrypted and encoded string. 
	 */
	public static String encrypt( String encryptionKey, String unencryptedString )
	{
		try
		{			
	        String newKey = null;
	        if (encryptionKey.length()<16)
	        	newKey = DEFAULT_ENCRYPTION_KEY;
	        else
	        	newKey = encryptionKey.substring(0, 16); // uses 128bit key length (256bit is not used in case customer's server does not support)
	        byte[] keyAsBytes = newKey.getBytes( UNICODE_FORMAT );
	
	        SecretKeySpec keySpec = new SecretKeySpec(keyAsBytes, "AES");
	
	        Cipher cipher = Cipher.getInstance( DEFAULT_ENCRYPTION_SCHEME );

			cipher.init( Cipher.ENCRYPT_MODE, keySpec );
			byte[] cleartext = unencryptedString.getBytes( UNICODE_FORMAT );
			byte[] ciphertext = cipher.doFinal( cleartext );
			
			String strEncode = new String(Base64Coder.encode( ciphertext ));
        
			//debugPrint("Encoded = " + strEncode);			
			
			//return base64encoder.encode( ciphertext );
			return strEncode;
		}
		catch (Exception e)
		{
			Log.e(TAG, "Scrambler encryptino exception: " + e.getMessage());
			return "";
		}
	}

	/**
	 * Decrypts and decodes a string. 
	 * 
	 * @param encryptionKey Represents encryption key (merchant's secret). 
	 * @param encryptedString Represents encrypted and encoded string. 
	 * @return Decrypted and decoded string. 
	 */
	public static String decrypt(String encryptionKey, String encryptedString )
	{
		try
		{
	        String newKey = null;
	        if (encryptionKey.length()<16)
	        	newKey = DEFAULT_ENCRYPTION_KEY;
	        else
	        	newKey = encryptionKey.substring(0, 16);  // uses 128bit key length (256bit is not used in case customer's server does not support)
            byte[] keyAsBytes = newKey.getBytes( UNICODE_FORMAT );

            SecretKeySpec keySpec = new SecretKeySpec(keyAsBytes, "AES");

            Cipher cipher = Cipher.getInstance( DEFAULT_ENCRYPTION_SCHEME );

			cipher.init( Cipher.DECRYPT_MODE, keySpec );
		
			byte[] cleartext = Base64Coder.decode( encryptedString );
			byte[] ciphertext = cipher.doFinal( cleartext );

			return bytes2String( ciphertext );
		}
		catch (Exception e)
		{
            return "";
		}
	}

	/**
	 * Converts an array of bytes to string. 
	 * 
	 * @param bytes Represents an array of byte.
	 * @return A string.
	 */
	private static String bytes2String( byte[] bytes )
	{
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++)
		{
			stringBuffer.append( (char) bytes[i] );
		}
		return stringBuffer.toString();
	}

}
