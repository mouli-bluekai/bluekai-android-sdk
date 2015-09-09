/*
 * Copyright 2013-present BlueKai, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bluekai.sampleapp;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluekai.sdk.BlueKai;
import com.bluekai.sdk.BlueKaiData;
import com.bluekai.sdk.listeners.DataPostedListener;

public class BlueKaiTab extends FragmentActivity implements DataPostedListener, OnSharedPreferenceChangeListener {

	private Button sendButton = null;

	private Button clearButton = null;

	// private Button pushButton = null;
	private EditText keyText = null;

	private EditText valueText = null;

	// private EditText pairsCountText = null;

	private Boolean devMode = false;

	private String siteId = null;

	private String appVersion = "4.1.6";

	private BlueKai bk = null;

	protected SharedPreferences preferences;

	private Boolean useWebView = false;

	private boolean useHttps = false;

	private boolean sync = false;

	private Integer partnerId = null;

	private String wsPublicKey = null;

	private String wsPrivateKey = null;

	BlueKaiData blueKaiData = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_blue_kai);

			readPreferences();
			preferences.registerOnSharedPreferenceChangeListener(this);

			siteId = preferences.getString("siteId", "2");
			devMode = preferences.getBoolean("enableDevMode", false);
			useWebView = preferences.getBoolean("useWebView", false);
			useHttps = preferences.getBoolean("useHttps", false);
			sync = preferences.getBoolean("sync", false);
			partnerId = Integer.parseInt(preferences.getString("partnerId", "486"));
			wsPublicKey = preferences.getString("wsPublicKey", null);
			wsPrivateKey = preferences.getString("wsPrivateKey", null);

			blueKaiData = new BlueKaiData();
			blueKaiData.setBkKey(wsPublicKey);
			blueKaiData.setBkSecretKey(wsPrivateKey);
			// An external user identifier can also be used for universal opt out
			// By default the android advertising ID would be used
			//blueKaiData.setUserIdentifier(new Pair<String, String>("e_id_s", "<hash_value>"));

			bk = BlueKai.getInstance(this, this, devMode, useHttps, siteId, appVersion, this, new Handler(), useWebView);

			keyText = (EditText) findViewById(R.id.keyText);
			valueText = (EditText) findViewById(R.id.valueText);
			// pairsCountText = (EditText) findViewById(R.id.pairs_count);

			clearButton = (Button) findViewById(R.id.clear);
			clearButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					keyText.setText("");
					valueText.setText("");
					keyText.requestFocus();
				}
			});

			sendButton = (Button) findViewById(R.id.send);
			sendButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String key = keyText.getText().toString();
					String value = valueText.getText().toString();
					if (key == null || key.trim().equals("")) {
						keyText.requestFocus();
						Toast.makeText(BlueKaiTab.this, "Key is empty. Please enter a value", Toast.LENGTH_LONG).show();
					} else if (value == null || value.trim().equals("")) {
						valueText.requestFocus();
						Toast.makeText(BlueKaiTab.this, "Value is empty. Please enter a value", Toast.LENGTH_LONG).show();
					} else {
						if (sync) {
							String response = bk.putSync(key, value);
							Toast.makeText(BlueKaiTab.this, response, Toast.LENGTH_SHORT).show();
						} else {
							bk.put(key, value);
						}

					}
				}
			});

		} catch (Exception ex) {
			Log.e("BlueKaiTab", "Error while creating", ex);
		}
	}

	/**
	 * Example method which prevents bluekai from sending any more data and also deletes the user profile at bluekai
	 * {@link BlueKai#universalOptOut(BlueKaiData, DataPostedListener)} method can also be used for an async call instead
	 * of the {@link BlueKai#universalOptOutSync(BlueKaiData)} method as shown below
	 */
	public void optOut() {
		bk.setOptInPreference(false);

		// Making a sync call to bluekai for opting out
		if (bk.universalOptOutSync(blueKaiData)) {
			Toast.makeText(this, "User successfully opted out universally", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Error while opting out", Toast.LENGTH_SHORT).show();
		}

		/*
		For making an async call instead

		bk.universalOptOut(blueKaiData, new DataPostedListener() {

			@Override
			public void onDataPosted(boolean success, String message) {
				if (success) {
					Toast.makeText(BlueKaiTab.this, "User successfully opted out universally", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(BlueKaiTab.this, "Error while opting out", Toast.LENGTH_SHORT).show();
				}
			}
		});
		 */
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.d("BlueKaiSampleApp", "On Resume --> DevMode ---> " + devMode + " -- Site ID --> " + siteId);
		bk = BlueKai.getInstance(this, this, devMode, useHttps, siteId, appVersion, this, new Handler(), useWebView);
		bk.resume();
	}

	@Override
	public void onDataPosted(boolean success, String message) {
		Log.d("BlueKaiSampleApp", String.valueOf(success) + " :: " + message);
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if ("enableDevMode".equals(key)) {
			Boolean oldDevMode = devMode;
			devMode = sharedPreferences.getBoolean(key, oldDevMode);
		} else if ("siteId".equals(key)) {
			String oldSiteId = siteId;
			siteId = sharedPreferences.getString(key, oldSiteId);
		} else if ("useWebView".equals(key)) {
			Boolean oldUseWebView = useWebView;
			useWebView = sharedPreferences.getBoolean(key, oldUseWebView);
		} else if ("useHttps".equals(key)) {
			Boolean oldUseHttps = useHttps;
			useHttps = sharedPreferences.getBoolean(key, oldUseHttps);
		} else if ("sync".equals(key)) {
			Boolean oldSync = sync;
			sync = sharedPreferences.getBoolean(key, oldSync);
		} else if ("partnerId".equals(key)) {
			Integer oldPartnerId = partnerId;
			partnerId = Integer.parseInt(sharedPreferences.getString("partnerId", String.valueOf(oldPartnerId)));
		} else if ("wsPublicKey".equals(key)) {
			String oldWsPublicKey = wsPublicKey;
			wsPublicKey = sharedPreferences.getString("wsPublicKey", oldWsPublicKey);
		} else if ("wsPrivateKey".equals(key)) {
			String oldWsPrivateKey = wsPrivateKey;
			wsPrivateKey = sharedPreferences.getString("wsPrivateKey", oldWsPrivateKey);
		}

	}

	// Reads the preferences from settings
	private void readPreferences() {
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		// Loading the default shared preferences
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
	}
}
