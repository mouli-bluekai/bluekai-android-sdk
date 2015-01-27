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
import com.bluekai.sdk.listeners.DataPostedListener;

public class BlueKaiTab extends FragmentActivity implements DataPostedListener, OnSharedPreferenceChangeListener {

	private Button sendButton = null;

	private Button clearButton = null;

	// private Button pushButton = null;
	private EditText keyText = null;

	private EditText valueText = null;

<<<<<<< HEAD
	private boolean devMode = false;
	private boolean useHttps = false;
=======
	// private EditText pairsCountText = null;

	private Boolean devMode = false;

>>>>>>> df0d9fe... Refactored code to use SharedPreferences instead of SQLite DB in the Sample App
	private String siteId = null;

	private String appVersion = "4.1.6";

	private BlueKai bk = null;

	protected SharedPreferences preferences;

	private Boolean useWebView;

	private String customUserAgent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_blue_kai);
<<<<<<< HEAD
			this.context = getApplicationContext();

			database = DataSource.getInstance(context);
			DevSettings devSettings = database.getDevSettings();
			if (devSettings == null) {
				try {
					Resources resources = this.getResources();
					AssetManager assetManager = resources.getAssets();
					InputStream inputStream = assetManager.open("settings.properties");
					Properties properties = new Properties();
					properties.load(inputStream);
					devMode = Boolean.parseBoolean(properties.getProperty("devmode"));
					useHttps = Boolean.parseBoolean(properties.getProperty("useHttps"));
					siteId = properties.getProperty("siteid");
				} catch (IOException e) {
					Log.e("BlueKaiSampleApp", "Error loading properties. Default values will be loaded from SDK", e);
				}
			} else {
				siteId = devSettings.getBkurl();
				devMode = devSettings.isDevMode();
				useHttps = devSettings.isHttpsEnabled();
			}
=======

			readPreferences();
			preferences.registerOnSharedPreferenceChangeListener(this);

			siteId = preferences.getString("siteId", "2");
			devMode = preferences.getBoolean("enableDevMode", false);
>>>>>>> df0d9fe... Refactored code to use SharedPreferences instead of SQLite DB in the Sample App

			bk = BlueKai.getInstance(this, this, devMode, useHttps, siteId, appVersion, this, new Handler());
			bk.setFragmentManager(getSupportFragmentManager());

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
						bk.put(key, value);
					}
				}
			});
<<<<<<< HEAD
=======

			/*
			 * pushButton = (Button) findViewById(R.id.push);
			 * pushButton.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View arg0) { int count =
			 * Integer.parseInt(pairsCountText.getText().toString()); if (count
			 * < 1 || count > 2000) { Toast.makeText(context,
			 * "Out of range. Enter a number between 1 and 2000",
			 * Toast.LENGTH_LONG) .show(); } else { Map<String, String>
			 * paramsMap = new HashMap<String, String>(); for (int i = 0; i <
			 * count; i++) { paramsMap.put("test" + i, "value" + i); }
			 * bk.put(paramsMap); } } });
			 */
>>>>>>> df0d9fe... Refactored code to use SharedPreferences instead of SQLite DB in the Sample App
		} catch (Exception ex) {
			Log.e("BlueKaiTab", "Error while creating", ex);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
<<<<<<< HEAD
		database = DataSource.getInstance(context);
		DevSettings devSettings = database.getDevSettings();
		if (devSettings == null) {
			try {
				Resources resources = this.getResources();
				AssetManager assetManager = resources.getAssets();
				InputStream inputStream = assetManager.open("settings.properties");
				Properties properties = new Properties();
				properties.load(inputStream);
				useHttps = Boolean.parseBoolean(properties.getProperty("useHttps"));
				devMode = Boolean.parseBoolean(properties.getProperty("devmode"));
				siteId = properties.getProperty("siteid");
			} catch (IOException e) {
				Log.e("BlueKaiSampleApp", "Error loading properties. Default values will be loaded from SDK", e);
			}
		} else {
			siteId = devSettings.getBkurl();
			devMode = devSettings.isDevMode();
			useHttps = devSettings.isHttpsEnabled();
		}
		Log.d("BlueKaiSampleApp", "On Resume --> DevMode ---> " + devMode + " -- Site ID --> " + siteId + " -- Use Https --> " + useHttps);
		bk = BlueKai.getInstance(this, this, devMode, useHttps, siteId, appVersion, this, new Handler());
=======
		Log.d("BlueKaiSampleApp", "On Resume --> DevMode ---> " + devMode + " -- Site ID --> " + siteId);
		bk = BlueKai.getInstance(this, this, devMode, siteId, appVersion, this, new Handler());
>>>>>>> df0d9fe... Refactored code to use SharedPreferences instead of SQLite DB in the Sample App
		bk.resume();
	}

	@Override
	public void onDataPosted(boolean success, String message) {
		Log.d("BlueKaiSampleApp", String.valueOf(success) + " :: " + message);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("enableDevMode")) {
			Boolean oldDevMode = devMode;
			devMode = sharedPreferences.getBoolean(key, oldDevMode);
		} else if (key.equals("siteId")) {
			String oldSiteId = siteId;
			siteId = sharedPreferences.getString(key, oldSiteId);
		} else if (key.equals("useWebView")) {
			Boolean oldUseWebView = useWebView;
			useWebView = sharedPreferences.getBoolean(key, oldUseWebView);
		} else if (key.equals("customUserAgent")) {
			String oldUserAgent = customUserAgent;
			customUserAgent = sharedPreferences.getString(key, oldUserAgent);
		}

	}

	// Reads the preferences from settings
	private void readPreferences() {
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		// Loading the default shared preferences
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
	}
}
