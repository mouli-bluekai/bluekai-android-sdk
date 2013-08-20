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
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bluekai.sdk.listeners.BKViewListener;
import com.bluekai.sdk.model.ParamsList;
import com.bluekai.sdk.utils.Logger;

public class BlueKaiWebView extends WebView {

	private BKViewListener listener;
	private WebViewClient client;
	private boolean existingData = false;
	private ParamsList paramsList = null;
	private boolean errorOccured = false;
	
	private boolean isExistingData() {
		return existingData;
	}

	private ParamsList getParamsList() {
		return this.paramsList;
	}

	public BlueKaiWebView(Context context) {
		super(context);
	}

	public BlueKaiWebView(Context context, BKViewListener listener) {
		super(context);
		this.listener = listener;
	}

	public void setBKViewListerner(BKViewListener listener){
		this.listener = listener;
	}
	
	public synchronized void loadUrl(String url, boolean existingData, ParamsList paramsList) {
		Logger.debug("BlueKaiView", "loadUrl() called on BlueKaiWebView... " + paramsList.size());
		this.existingData = existingData;
		this.paramsList = paramsList;
		loadUrl(url);
	}

	public void setWebClient() {
		if (client == null) {
			client = new WebViewClient() {
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
		}
		setWebViewClient(client);
	}
}
