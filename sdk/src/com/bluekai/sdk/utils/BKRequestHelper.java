package com.bluekai.sdk.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

import com.bluekai.sdk.model.BKRequest;

/**
 * Created by moulimukherjee.
 */
public class BKRequestHelper {
	public final static String algoritmString = "HmacSHA256";

	public static final String TAG = BKRequestHelper.class.getSimpleName();

	/**
	 * The org.apache.commons.codec.binary.Base64 class isn't available in android to generate hmac.
	 * Hence generating the signature manually.
	 * Credit: http://stackoverflow.com/a/18895255/1632200
	 *
	 * @param stringToSign
	 * @param key
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static String signUrl(String stringToSign, String key) {
		String signature = null;
		try {
			Mac mac = Mac.getInstance(algoritmString);
			mac.init(new SecretKeySpec(key.getBytes(), algoritmString));
			byte[] bytes = mac.doFinal(stringToSign.getBytes());
			bytes = Base64.encode(bytes, Base64.NO_WRAP);
			signature = new String(bytes);
		} catch (Exception e) {
			Log.e(TAG, "Error signing: " + e.getMessage(), e);
		}

		return signature;
	}

	public static String getStringToSign(BKRequest request) {
		StringBuilder stringToSign = new StringBuilder();

		stringToSign.append(request.getType().toString());
		try {
			URL url = new URL(request.getUrl());
			if (url.getPath() != null) {
				stringToSign.append(url.getPath());
			}

			if (url.getQuery() != null) {
				String[] params = url.getQuery().split("&");
				if (params != null && params.length > 0) {
					for (String param : params) {
						if (!param.split("=")[0].equals("debug"))
							stringToSign.append(param.split("=")[1]);
					}
				}
			}
			if (request.getPayload() != null) {
				stringToSign.append(request.getPayload());
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, "Malformed URL: " + e.getMessage(), e);
		}

		return stringToSign.toString();
	}
}
