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

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bluekai.sdk.model.DevSettings;

public class DevSettingsTab extends Activity {

	private Properties properties = null;
	private Context context;

	private DataSource database;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.devsettings);
		this.context = getApplicationContext();

		String siteId = "2";
		boolean devMode = false;
		boolean useHttps = false;

		database = DataSource.getInstance(context);
		DevSettings devSettings = database.getDevSettings();
		if (devSettings == null) {
			try {
				Resources resources = this.getResources();
				AssetManager assetManager = resources.getAssets();
				InputStream inputStream = assetManager.open("settings.properties");
				properties = new Properties();
				properties.load(inputStream);
				devMode = Boolean.parseBoolean(properties.getProperty("devmode"));
				useHttps = Boolean.parseBoolean(properties.getProperty("useHttps"));
				siteId = properties.getProperty("siteid");
				Log.d("BlueKaiSampleApp", "DevSettings tab. DevMode ---> " + devMode + " -- Site ID --> " + siteId + " -- Use Https --> " + useHttps);
			} catch (IOException e) {
				Log.e("BlueKaiSampleApp", "Error loading properties. Default values will be loaded from SDK", e);
			}
		} else {
			siteId = devSettings.getBkurl();
			devMode = devSettings.isDevMode();
			useHttps = devSettings.isHttpsEnabled();
		}

		final CheckBox devModeCheck = (CheckBox) findViewById(R.id.devmode);
		devModeCheck.setChecked(devMode);

		final CheckBox useHttpsCheck = (CheckBox) findViewById(R.id.useHttps);
		useHttpsCheck.setChecked(useHttps);

		final EditText bkUrlText = (EditText) findViewById(R.id.serverurl);
		bkUrlText.setText(siteId);

		Button save = (Button) findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DevSettings devSettings = new DevSettings();
				devSettings.setDevMode(devModeCheck.isChecked());
				devSettings.setBkurl(bkUrlText.getText().toString());
				devSettings.setHttpsEnabled(useHttpsCheck.isChecked());
				database.writeDevSettings(devSettings);
			}
		});
	}
}
