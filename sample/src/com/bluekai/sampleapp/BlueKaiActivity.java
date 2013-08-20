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

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.bluekai.sdk.SettingsActivity;

public class BlueKaiActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		
		Intent intent = new Intent().setClass(this, BlueKaiTab.class);
		spec = tabHost.newTabSpec("bluekai").setIndicator("BlueKai").setContent(intent);
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec("settings").setIndicator("T&C")
				.setContent(new Intent().setClass(this, SettingsActivity.class));
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, DevSettingsTab.class);
		spec = tabHost.newTabSpec("devsettings").setIndicator("Settings")
				.setContent(intent);
		tabHost.addTab(spec);
	}
}
