package jp.co.smbc.gridbeacon;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * <code>Crypto</code> is used to for encryption, hashing.<br>
 *
 * @author Lau Chee hoo
 * @since 2011
 */
public class Crypto {

	static char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	/**
	 * Encrypts and encodes (base64) a string.
	 *
	 * @param encryptionKey     Represents encryption key (merchant's secret).
	 * @param unencryptedString Represents a string.
	 * @return Encrypted and encoded string.
	 */
	public static String encrypt(String encryptionKey, String unencryptedString) {
		MessageDigest digest;
		String encryptedText = null;
		byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,};
		try {
			digest = MessageDigest.getInstance("SHA-256");

			digest.update(encryptionKey.getBytes("UTF-8"));
			byte[] keyBytes = new byte[32];
			System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

			AlgorithmParameterSpec spec = new IvParameterSpec(iv);

			cipher.init(Cipher.ENCRYPT_MODE, key, spec);
			byte[] encrypted = cipher.doFinal(unencryptedString.getBytes("UTF-8"));
			char[] encodedByte = Base64Coder.encode(encrypted);
			encryptedText = chars2String(encodedByte);

			return encryptedText;
		} catch (NoSuchAlgorithmException e) {
			encryptedText = "NoSuchAlgorithmException";
		} catch (InvalidKeyException e) {
			encryptedText = "InvalidKeyException";
		} catch (InvalidAlgorithmParameterException e) {
			encryptedText = "InvalidAlgorithmParameterException";
		} catch (IllegalBlockSizeException e) {
			encryptedText = "IllegalBlockSizeException";
		} catch (BadPaddingException e) {
			encryptedText = "BadPaddingException";
		} catch (UnsupportedEncodingException e) {
			encryptedText = "UnsupportedEncodingException";
		} catch (NoSuchPaddingException e) {
			encryptedText = "NoSuchPaddingException";
		} catch (IllegalStateException e) {
			encryptedText = "IllegalStateException";
		}

		return encryptedText;
	}

	/**
	 * Decrypts and decodes a string.
	 *
	 * @param encryptionKey   Represents encryption key (merchant's secret).
	 * @param encryptedString Represents encrypted and encoded string.
	 * @return Decrypted and decoded string.
	 */
	public static String decrypt(String encryptionKey, String encryptedString) {
		MessageDigest digest;
		String decryptedText = null;
		byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,};
		try {
			digest = MessageDigest.getInstance("SHA-256");

			digest.update(encryptionKey.getBytes("UTF-8"));
			byte[] keyBytes = new byte[32];
			System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

			AlgorithmParameterSpec spec = new IvParameterSpec(iv);

			cipher.init(Cipher.DECRYPT_MODE, key, spec);
			byte[] bytes = Base64Coder.decode(encryptedString);
			byte[] decrypted = cipher.doFinal(bytes);
			decryptedText = new String(decrypted, "UTF-8");

		} catch (IllegalArgumentException e) {
			decryptedText = "IllegalArgumentException";
		} catch (NoSuchAlgorithmException e) {
			decryptedText = "NoSuchAlgorithmException";
		} catch (InvalidKeyException e) {
			decryptedText = "InvalidKeyException";
		} catch (InvalidAlgorithmParameterException e) {
			decryptedText = "InvalidAlgorithmParameterException";
		} catch (IllegalBlockSizeException e) {
			decryptedText = "IllegalBlockSizeException";
		} catch (BadPaddingException e) {
			decryptedText = "BadPaddingException";
		} catch (UnsupportedEncodingException e) {
			decryptedText = "UnsupportedEncodingException";
		} catch (NoSuchPaddingException e) {
			decryptedText = "NoSuchPaddingException";
		} catch (IllegalStateException e) {
			decryptedText = "IllegalStateException";
		}
		return decryptedText;
	}

	/**
	 * Converts an array of char to string.
	 *
	 * @param chars Represents an array of byte.
	 * @return A string.
	 */
	private static String chars2String(char[] chars) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			stringBuffer.append(chars[i]);
		}
		return stringBuffer.toString();
	}

	/**
	 * Converts an array of byte data to hexadecimal format.
	 *
	 * @param b Represents data in byte form.
	 * @return Hexadecimal format.
	 */
	public static String toHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			// look up high nibble char
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			// look up low nibble char
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	/**
	 * Generates the hash value (signature) for email link.
	 *
	 * @param sec     Represents merchant's secret.
	 * @param combine Represents a string.
	 * @return Hash value.
	 */
	public static String generateSIG(String sec, String combine) {

		MessageDigest md = null;
		byte digest[] = null;
		String result = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			// md.update(sec.getBytes());
			// md.update(combine.getBytes());
			md.update(sec.getBytes("UTF-8"));
			md.update(combine.getBytes("UTF-8"));

			digest = md.digest();
			result = toHexString(digest);

		} catch (Exception e) {
		}
		return result;
	}

}

class Base64Coder {
	/**
	 * Mapping table from 6-bit nibbles to Base64 characters.
	 */
	private static char[] map1 = new char[64];

	static {
		int i = 0;
		for (char c = 'A'; c <= 'Z'; c++) {
			map1[i++] = c;
		}
		for (char c = 'a'; c <= 'z'; c++) {
			map1[i++] = c;
		}
		for (char c = '0'; c <= '9'; c++) {
			map1[i++] = c;
		}
		map1[i++] = '+';
		map1[i++] = '/';
	}

	/**
	 * Mapping table from Base64 characters to 6-bit nibbles.
	 */
	private static byte[] map2 = new byte[128];

	static {
		for (int i = 0; i < map2.length; i++) {
			map2[i] = -1;
		}
		for (int i = 0; i < 64; i++) {
			map2[map1[i]] = (byte) i;
		}
	}

	/**
	 * Encodes a string into Base64 format. No blanks or line breaks are inserted.
	 *
	 * @param s a String to be encoded.
	 * @return A String with the Base64 encoded data.
	 */
	public static String encodeString(String s) {
		return new String(encode(s.getBytes()));
	}

	/**
	 * Encodes a byte array into Base64 format. No blanks or line breaks are inserted.
	 *
	 * @param in an array containing the data bytes to be encoded.
	 * @return A character array with the Base64 encoded data.
	 */
	public static char[] encode(byte[] in) {
		return encode(in, in.length);
	}

	/**
	 * Encodes a byte array into Base64 format. No blanks or line breaks are inserted.
	 *
	 * @param in   an array containing the data bytes to be encoded.
	 * @param iLen number of bytes to process in <code>in</code>.
	 * @return A character array with the Base64 encoded data.
	 */
	public static char[] encode(byte[] in, int iLen) {
		int oDataLen = (iLen * 4 + 2) / 3; // output length without padding
		int oLen = ((iLen + 2) / 3) * 4; // output length including padding
		char[] out = new char[oLen];
		int ip = 0;
		int op = 0;
		while (ip < iLen) {
			int i0 = in[ip++] & 0xff;
			int i1 = ip < iLen ? in[ip++] & 0xff : 0;
			int i2 = ip < iLen ? in[ip++] & 0xff : 0;
			int o0 = i0 >>> 2;
			int o1 = ((i0 & 3) << 4) | (i1 >>> 4);
			int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
			int o3 = i2 & 0x3F;
			out[op++] = map1[o0];
			out[op++] = map1[o1];
			out[op] = op < oDataLen ? map1[o2] : '=';
			op++;
			out[op] = op < oDataLen ? map1[o3] : '=';
			op++;
		}
		return out;
	}

	/**
	 * Decodes a string from Base64 format.
	 *
	 * @param s a Base64 String to be decoded.
	 * @return A String containing the decoded data.
	 * @throws IllegalArgumentException if the input is not valid Base64 encoded data.
	 */
	public static String decodeString(String s) {
		return new String(decode(s));
	}

	/**
	 * Decodes a byte array from Base64 format.
	 *
	 * @param s a Base64 String to be decoded.
	 * @return An array containing the decoded data bytes.
	 * @throws IllegalArgumentException if the input is not valid Base64 encoded data.
	 */
	public static byte[] decode(String s) {
		return decode(s.toCharArray());
	}

	/**
	 * Decodes a byte array from Base64 format. No blanks or line breaks are allowed within the Base64 encoded data.
	 *
	 * @param in a character array containing the Base64 encoded data.
	 * @return An array containing the decoded data bytes.
	 * @throws IllegalArgumentException if the input is not valid Base64 encoded data.
	 */
	public static byte[] decode(char[] in) {
		int iLen = in.length;
		if (iLen % 4 != 0)
			throw new IllegalArgumentException("Length of Base64 encoded input string is not a multiple of 4.");
		while (iLen > 0 && in[iLen - 1] == '=')
			iLen--;
		int oLen = (iLen * 3) / 4;
		byte[] out = new byte[oLen];
		int ip = 0;
		int op = 0;
		while (ip < iLen) {
			int i0 = in[ip++];
			int i1 = in[ip++];
			int i2 = ip < iLen ? in[ip++] : 'A';
			int i3 = ip < iLen ? in[ip++] : 'A';
			if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127)
				throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
			int b0 = map2[i0];
			int b1 = map2[i1];
			int b2 = map2[i2];
			int b3 = map2[i3];
			if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0)
				throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
			int o0 = (b0 << 2) | (b1 >>> 4);
			int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
			int o2 = ((b2 & 3) << 6) | b3;
			out[op++] = (byte) o0;
			if (op < oLen)
				out[op++] = (byte) o1;
			if (op < oLen)
				out[op++] = (byte) o2;
		}
		return out;
	}

	/**
	 * Dummy constructor.
	 */
	public Base64Coder() {
	}

}
