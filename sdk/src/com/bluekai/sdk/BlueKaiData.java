package com.bluekai.sdk;

import android.util.Pair;

/**
 * Created by moulimukherjee.
 */
public class BlueKaiData {

	private Pair<String, String> userIdentifier;
	private String bkKey;
	private String bkSecretKey;

	public Pair<String, String> getUserIdentifier() {
		return userIdentifier;
	}

	public void setUserIdentifier(Pair<String, String> userIdentifier) {
		this.userIdentifier = userIdentifier;
	}

	public String getBkKey() {
		return bkKey;
	}

	public void setBkKey(String bkKey) {
		this.bkKey = bkKey;
	}

	public String getBkSecretKey() {
		return bkSecretKey;
	}

	public void setBkSecretKey(String bkSecretKey) {
		this.bkSecretKey = bkSecretKey;
	}
}
