package com.bluekai.sdk.bktag;

import java.security.MessageDigest;

/**
 * Util methods for core tag
 * 
 * @author moulimukherjee
 *
 */
public class CoreTagUtil {

	/**
	 * 
	 * @param email
	 * @return
	 */
	public static String normalizeEmail(String email) {
		String result = email;
		try {
			result = email.trim().toLowerCase();
			String[] tmp = result.split("@");
			String pre = tmp[0];
			if (pre.indexOf("+") > -1) {
				pre = pre.substring(0, pre.indexOf("+"));
			}
			result = pre + "@" + tmp[1];
		} catch (Exception e) {

		}
		return result;
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
			e.printStackTrace();
			return null;
		}

	}
}
