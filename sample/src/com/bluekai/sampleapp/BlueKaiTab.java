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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bluekai.sdk.BlueKai;
import com.bluekai.sdk.listeners.DataPostedListener;
import com.bluekai.sdk.model.DevSettings;

public class BlueKaiTab extends FragmentActivity implements DataPostedListener {

	private Button sendButton = null;
	private Button clearButton = null;
	//private Button pushButton = null;
	private EditText keyText = null;
	private EditText valueText = null;
	//private EditText pairsCountText = null;

	private boolean devMode = false;
	private boolean useHttps = false;
	private String siteId = null;
	private String appVersion = "4.1.6";

	private BlueKai bk = null;

	private Context context;
	private DataSource database;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_blue_kai);
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

			bk = BlueKai.getInstance(this, this, devMode, useHttps, siteId, appVersion, this, new Handler());
			bk.setFragmentManager(getSupportFragmentManager());

			keyText = (EditText) findViewById(R.id.keyText);
			valueText = (EditText) findViewById(R.id.valueText);
			//pairsCountText = (EditText) findViewById(R.id.pairs_count);

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
						Toast.makeText(context, "Key is empty. Please enter a value", Toast.LENGTH_LONG).show();
					} else if (value == null || value.trim().equals("")) {
						valueText.requestFocus();
						Toast.makeText(context, "Value is empty. Please enter a value", Toast.LENGTH_LONG).show();
					} else {
						bk.put(key, value);
					}
				}
			});
		} catch (Exception ex) {
			Log.e("BlueKaiTab", "Error while creating", ex);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
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
		bk.resume();
	}

	@Override
	public void onDataPosted(boolean success, String message) {
		Log.d("BlueKaiSampleApp", String.valueOf(success) + " :: " + message);
	}
}
