package com.bluekai.sdk.bktag;

import java.security.MessageDigest;

import com.bluekai.sdk.utils.Logger;

/**
 * Util methods for core tag
 * 
 * @author moulimukherjee
 *
 */
public class CoreTagUtil {

	private static final String TAG = CoreTagUtil.class.getSimpleName();

	/**
	 * 
	 * @param email
	 * @return
	 */
	public static String normalizeEmail(String email) {
		return email.trim().toLowerCase().replaceFirst("\\+[^@]*@", "@");
	}

	/**
	 * 
	 * @param phone
	 * @return
	 */
	public static String normalizePhone(String phone) {
		String result = phone;
		try {
			result = result.trim().replaceAll("/^[0]+", "").replaceAll("\\D", "");
		} catch (Exception e) {

		}
		return result;
	}

	/**
	 * 
	 * @param message
	 * @param algorithm
	 * @return
	 */
	public static String hashString(String message, String algorithm) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance(algorithm);
			byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));
			return new String(hashedBytes);

		} catch (Exception e) {
			Logger.warn(TAG, "Error hashing string", e);
			return null;
		}

	}
}
