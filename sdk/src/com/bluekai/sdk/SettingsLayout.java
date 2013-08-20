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
package com.bluekai.sdk;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluekai.sdk.model.Settings;

public class SettingsLayout extends RelativeLayout {

	private BlueKaiDataSource dataSource = null;
	private Button save;
	private Button cancel;
	private CheckBox dataPostCheck;
	private TextView termsCheck;

	public SettingsLayout(Context context) {
		super(context);
		dataSource = BlueKaiDataSource.getInstance(context);
		Settings settings = dataSource.getSettings();
		initView(context, settings);
	}

	private void initView(Context context, Settings settings) {
		setBackgroundColor(Color.BLACK);
		if (settings == null) {
			settings = dataSource.getSettings();
		}

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		params.setMargins(25, 0, 0, 10);
		dataPostCheck = new CheckBox(context);
		dataPostCheck.setText("Allow BlueKai to receive my data");
		dataPostCheck.setId(1);
		dataPostCheck.setLayoutParams(params);
		dataPostCheck.setChecked(settings.isAllowDataPosting());
		dataPostCheck.setTextColor(Color.WHITE);
		addView(dataPostCheck);

		save = new Button(context);
		save.setText("Save");
		save.setId(6);
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params.setMargins(45, 0, 0, 10);
		save.setLayoutParams(params);
		addView(save);

		cancel = new Button(context);
		cancel.setText("Cancel");
		cancel.setId(7);
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.RIGHT_OF, save.getId());
		params.setMargins(10, 0, 0, 10);
		cancel.setLayoutParams(params);
		addView(cancel);

		SpannableString ss = new SpannableString("The BlueKai privacy policy is available here");
		ss.setSpan(new URLSpan("http://www.bluekai.com/consumers_privacyguidelines.php"), 40, 44,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		termsCheck = new TextView(context);
		termsCheck.setTextColor(Color.WHITE);
		termsCheck.setText(ss);
		termsCheck.setMovementMethod(LinkMovementMethod.getInstance());
		termsCheck.setId(5);
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.ABOVE, save.getId());
		params.setMargins(25, 0, 0, 10);
		termsCheck.setLayoutParams(params);
		addView(termsCheck);
	}

	public boolean saveSettings() {
		Settings settings = new Settings();
		settings.setAllowDataPosting(dataPostCheck.isChecked());
		return dataSource != null ? dataSource.createSettings(settings) : false;
	}

	public Button getSave() {
		return save;
	}

	public Button getCancel() {
		return cancel;
	}
}
