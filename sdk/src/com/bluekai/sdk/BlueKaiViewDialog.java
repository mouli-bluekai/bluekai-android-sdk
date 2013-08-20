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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.bluekai.sdk.listeners.BKViewListener;
import com.bluekai.sdk.model.ParamsList;
import com.bluekai.sdk.utils.Logger;

public class BlueKaiViewDialog extends DialogFragment {
	private final String TAG = "BlueKaiView";

	private WebView blueKaiView = null;
	private BKViewListener listener = null;
	private String url = null;
	private boolean existingData = false;
	private boolean errorOccured = false;
	private ParamsList paramsList = null;
	
	public BlueKaiViewDialog() {

	}

	private boolean isExistingData() {
		return this.existingData;
	}
	
	private ParamsList getParamsList(){
		return this.paramsList;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		if (blueKaiView == null) {
			blueKaiView = new WebView(getActivity());
			WebViewClient client = new WebViewClient() {
				@Override
				public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
					super.onReceivedError(view, errorCode, description, failingUrl);
					Logger.debug("BlueKaiView", "Error loading BK URL in webview -- " + errorCode + " -- "
							+ description);
					errorOccured = true;
					if (listener != null) {
						listener.onViewLoaded(false, isExistingData(), getParamsList());
					}
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					if (!errorOccured && listener != null) {
						errorOccured = false;
						listener.onViewLoaded(true, isExistingData(), getParamsList());
					}
				}
			};
			blueKaiView.setWebViewClient(client);
			WebSettings webSettings = blueKaiView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			int height = 1, width = 1;
			// if (devMode) {
			height = width = 300;
			// }
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			params.setMargins(10, 10, 10, 10);
			blueKaiView.setLayoutParams(params);
			blueKaiView.setBackgroundColor(Color.LTGRAY);
			alertDialogBuilder.setNegativeButton("Close", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		blueKaiView.loadUrl(url);
		alertDialogBuilder.setView(blueKaiView);
		return alertDialogBuilder.create();
	}

	public void setBKViewListener(BKViewListener listener) {
		this.listener = listener;
	}

	public void setLoadURL(String url, boolean existingData, ParamsList paramsList) {
		// blueKaiView.loadUrl(url);
		this.existingData = existingData;
		this.url = url;
		this.paramsList = paramsList;
	}

}
