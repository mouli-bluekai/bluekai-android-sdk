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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bluekai.sdk.bktag.CoreTagConfig;
import com.bluekai.sdk.bktag.CoreTagProcessor;
import com.bluekai.sdk.helpers.BKAdvertisingIdClient.AdInfo;
import com.bluekai.sdk.listeners.BKViewListener;
import com.bluekai.sdk.listeners.DataPostedListener;
import com.bluekai.sdk.listeners.SettingsChangedListener;
import com.bluekai.sdk.model.BKRequest;
import com.bluekai.sdk.model.BKRequest.Type;
import com.bluekai.sdk.model.BKResponse;
import com.bluekai.sdk.model.Params;
import com.bluekai.sdk.model.ParamsList;
import com.bluekai.sdk.model.Settings;
import com.bluekai.sdk.task.BKAdvertisingIdListener;
import com.bluekai.sdk.task.BKWebServiceListener;
import com.bluekai.sdk.task.BKWebServiceRequestTask;
import com.bluekai.sdk.task.GetAdvertisingIdTask;
import com.bluekai.sdk.utils.BKRequestHelper;
import com.bluekai.sdk.utils.Logger;

public class BlueKai implements SettingsChangedListener, BKViewListener {

	private final static String TAG = "BlueKai";

	private static BlueKai instance = null;

	private boolean devMode = false;

	private Activity activity = null;

	private Context context = null;

	private boolean httpsEnabled = false;

	private final String HTTP = "http://";

	private final String HTTPS = "https://";

	private final String BASE_URL = "mobileproxy.bluekai.com/m.html";

	private final String SANDBOX_URL = "mobileproxy.bluekai.com/m-sandbox.html";

	private final String DEFAULT_USER_AGENT = "Android Mobile BlueKaiSDK";

	private final String OPT_OUT_BASE_URL = "https://api.tags.bluekai.com/api/v2.0/users";

	private String siteId = "2";

	private String appVersion = "1.0";

	private BlueKaiWebView blueKaiView;

	private BlueKaiDataSource database = null;

	private Settings settings;

	private DataPostedListener listener;

	private Handler handler;

	// Flag to determine if calls should be made using web view or if it should
	// be a direct call
	private boolean useWebView = false;

	private String userAgent = null;

	// Google Advertising ID
	private String advertisingId = null;

	// Value for Google Setting to opt out of interest based ads. If this value
	// is true, then advertising ID won't be sent
	private boolean optOutPrivacy = false;

	private boolean advertisingIdRetrieved = false;

	private BlueKaiData blueKaiData;

	private BlueKai() {
		database = BlueKaiDataSource.getInstance(context);
		database.setSettingsChangedListener(this);
		settings = database.getSettings();
	}

	private BlueKai(Activity activity, Context context, boolean devMode, boolean useHttps, String siteId, String appVersion, DataPostedListener listener,
					Handler handler, boolean useWebView, BlueKaiData data) {
		this.activity = activity;
		this.context = context;
		this.devMode = devMode;
		this.appVersion = appVersion;
		if (!TextUtils.isEmpty(siteId) && !this.siteId.equals(siteId)) {
			this.siteId = siteId;
		}
		this.listener = listener;
		this.handler = handler;
		this.httpsEnabled = useHttps;
		this.useWebView = useWebView;
		this.userAgent = DEFAULT_USER_AGENT;
		this.blueKaiData = data;
		Logger.debug(TAG, " onCreate Dev Mode ? " + devMode);
		Logger.debug(TAG, " onCreate BK URL --> " + (useHttps ? HTTPS : HTTP) + (devMode ? SANDBOX_URL : BASE_URL));
		database = BlueKaiDataSource.getInstance(context);
		database.setSettingsChangedListener(this);
		settings = database.getSettings();
		populateAdvertisingId();
	}

	/**
	 * Method to resume BlueKai process after calling application resumes. To
	 * use in onResume() of the calling activity
	 */
	public void resume() {
		Logger.debug(TAG, " resume Dev Mode ? " + devMode);
		if (!devMode && useWebView && blueKaiView == null) {
			addBlueKaiWebView(context);
		}

		populateAdvertisingId();

		if (settings.isAllowDataPosting()) {
			checkForExistingData();
		}
	}

	/**
	 * Method to get BlueKai instance
	 *
	 * @param activity   Calling application activity reference
	 * @param context    Calling application context
	 * @param devMode    Developer mode. Set to enable webview to popup in a dialog.
	 *                   Strictly for developer purposes only
	 * @param siteId     BlueKai site id
	 * @param appVersion Version of the calling application
	 * @param listener   DataPostedListener. Calling activity should implement this
	 *                   interface
	 * @param handler    Handler. Android os handler.
	 * @return BlueKai instance
	 */
	@Deprecated
	public static BlueKai getInstance(Activity activity, Context context, boolean devMode, String siteId, String appVersion, DataPostedListener listener,
									  Handler handler) {
		return getInstance(activity, context, devMode, false, siteId, appVersion, listener, handler);
	}

	/**
	 * Method to get BlueKai instance
	 *
	 * @param activity     Calling application activity reference
	 * @param context      Calling application context
	 * @param devMode      Developer mode. Set to enable webview to popup in a dialog. Strictly for developer purposes only
	 * @param httpsEnabled Secure mode. Set to enable data transfer to BlueKai over https.
	 * @param siteId       BlueKai site id
	 * @param appVersion   Version of the calling application
	 * @param listener     DataPostedListener. Calling activity should implement this interface
	 * @param handler      Handler. Android os handler.
	 * @return BlueKai instance
	 */
	@Deprecated
	public static BlueKai getInstance(Activity activity, Context context, boolean devMode, boolean httpsEnabled, String siteId, String appVersion,
									  DataPostedListener listener, Handler handler) {
		return getInstance(activity, context, devMode, httpsEnabled, siteId, appVersion, listener, handler, true);
	}

	/**
	 * @param activity     Calling application activity reference
	 * @param context      Calling application context
	 * @param devMode      Developer mode. Set to enable webview to popup in a dialog. Strictly for developer purposes only
	 * @param httpsEnabled Secure mode. Set to enable data transfer to BlueKai over https.
	 * @param siteId       BlueKai site id
	 * @param appVersion   Version of the calling application
	 * @param listener     DataPostedListener. Calling activity should implement this interface
	 * @param handler      Handler. Android os handler.
	 * @param useWebView   Flag if set to true, then the calls will be made using webview else native calls would be made to send phints
	 * @return
	 */
	public static BlueKai getInstance(Activity activity, Context context, boolean devMode, boolean httpsEnabled, String siteId, String appVersion,
									  DataPostedListener listener, Handler handler, boolean useWebView) {
		return getInstance(activity, context, devMode, httpsEnabled, siteId, appVersion, listener, handler, useWebView, null);
	}

	/**
	 * @param activity
	 * @param context
	 * @param devMode
	 * @param httpsEnabled
	 * @param siteId
	 * @param appVersion
	 * @param listener
	 * @param handler
	 * @param useWebView
	 * @param data
	 * @return
	 */
	public static BlueKai getInstance(Activity activity, Context context, boolean devMode, boolean httpsEnabled, String siteId, String appVersion,
									  DataPostedListener listener, Handler handler, boolean useWebView, BlueKaiData data) {
		Logger.debug(TAG, "Called get instance...");
		if (instance == null) {
			instance = new BlueKai(activity, context, devMode, httpsEnabled, siteId, appVersion, listener, handler, useWebView, data);
		} else {
			instance.setActivity(activity);
			instance.setAppContext(context);
			instance.setDevMode(devMode);
			instance.setHttpsEnabled(httpsEnabled);
			instance.setSiteId(siteId);
			instance.setAppVersion(appVersion);
			instance.setDataPostedListener(listener);
			instance.setHandler(handler);
			instance.setUseWebView(useWebView);
			instance.setBlueKaiData(data);
		}
		return instance;
	}

	/**
	 * Convenience method to initialize and get instance of BlueKai without
	 * arguments. This method returns the previously created instance, if any,
	 * or returns an instance with bare minimum settings initialized. In ideal
	 * cases, first create a BlueKai instance using the other two getInstance()
	 * methods that take arguments and then subsequently use this method to get
	 * previously created instance.
	 *
	 * @return BlueKai instance
	 */
	public static BlueKai getInstance() {
		Logger.debug(TAG, "Called get instance...");
		if (instance == null) {
			Logger.debug(TAG, "Creating new instance...");
			instance = new BlueKai();
		}
		return instance;
	}

	/**
	 * Set the calling activity reference
	 *
	 * @param activity Calling activity reference
	 */
	public void setActivity(Activity activity) {
		this.activity = activity;
	}


	public void setBlueKaiData(BlueKaiData blueKaiData) {
		this.blueKaiData = blueKaiData;
	}

	/**
	 * Get calling activity reference.
	 *
	 * @return activity Activity
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * Set the calling application context
	 *
	 * @param context Context
	 */
	public void setAppContext(Context context) {
		this.context = context;
		database = BlueKaiDataSource.getInstance(context);
		database.setSettingsChangedListener(this);
		settings = database.getSettings();
	}

	/**
	 * Get calling application context.
	 *
	 * @return context Context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Set developer mode (True or False)
	 *
	 * @param devMode Developer mode
	 */
	public void setDevMode(boolean devMode) {
		this.devMode = devMode;
	}

	/**
	 * Get Developer mode setting.
	 *
	 * @return devMode boolean
	 */
	public boolean isDevMode() {
		return devMode;
	}

	/**
	 * Set BlueKai site id
	 *
	 * @param siteId Site ID
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	/**
	 * Get BlueKai Site ID
	 *
	 * @return siteId String
	 */
	public String getSiteId() {
		return this.siteId;
	}

	/**
	 * Set the calling application's version
	 *
	 * @param appVersion Application version
	 */
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	/**
	 * Get calling application's version
	 *
	 * @return appVersion String
	 */
	public String getAppVersion() {
		return appVersion;
	}

	/**
	 * Set the DataPostedListener to get notifications about status of a data
	 * posting. Calling activity should implement this interface
	 *
	 * @param listener Listener implementation
	 */
	public void setDataPostedListener(DataPostedListener listener) {
		this.listener = listener;
	}

	/**
	 * Get the listener that is configured to get notifications about status of
	 * data posting.
	 *
	 * @return listener DataPostedListener
	 */
	public DataPostedListener getDataPostedListener() {
		return listener;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/**
	 * Method to send data to BlueKai. Accepts a single key-value pair
	 *
	 * @param key   Key
	 * @param value Value
	 */
	public void put(String key, String value) {
		sendData(key, value);
	}

	/**
	 * Method to send data to BlueKai. This makes a direct call instead of using
	 * web view. Accepts a single key-value pair Returns the campaign details
	 *
	 * @param key   Key
	 * @param value Value
	 */
	public String putSync(String key, String value) {
		return sendDataSync(key, value);
	}

	/**
	 * Convenience method to send a bunch of key-value pairs to BlueKai
	 *
	 * @param map Map with keys and values
	 * @deprecated as of release v1.0.3. Replaced by
	 * {@link #putAll(java.util.Map)}
	 */
	@Deprecated
	public void put(Map<String, String> map) {
		sendData(map);
	}

	/**
	 * Convenience method to send a bunch of key-value pairs to BlueKai
	 *
	 * @param map Map with keys and values
	 */
	public void putAll(Map<String, String> map) {
		sendData(map);
	}

	/**
	 * Method to show BlueKai in-build opt-in screen
	 *
	 * @param listener Listener to get callback on settings change
	 */
	public void showSettingsScreen(SettingsChangedListener listener) {
		if (activity == null || context == null) {
			Logger.error(TAG, "Activity or context reference is null. Cannot show settings page");
		} else {
			Logger.debug(TAG, "Settings activity called...");
			Intent intent = new Intent(context, SettingsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(intent);
		}
	}

	/**
	 * Method to set user opt-in or opt-out preference
	 *
	 * @param optIn Opt-in (true or false)
	 * @Deprecated as of release v1.0.3. Replaced by
	 * {@link #setOptInPreference(boolean)}
	 */
	@Deprecated
	public void setOptIn(boolean optIn) {
		setOptInPreference(optIn);
	}

	/**
	 * Method to set user opt-in or opt-out preference
	 *
	 * @param optIn Opt-in (true or false)
	 */
	public void setOptInPreference(boolean optIn) {
		this.settings.setAllowDataPosting(optIn);
		if (database != null) {
			database.createSettings(this.settings);
		}
	}

	/**
	 * Method to get user opt-in or opt-out preference
	 *
	 * @return user's opt-in or opt-out preference
	 */
	public boolean getOptInPreference() {
		return this.settings.isAllowDataPosting();
	}

	/**
	 * Method to check if httpsEnabled is true. If httpsEnabled is set then data
	 * is sent to BlueKai over https
	 *
	 * @return httpsEnabled flag that enables/disables data being sent to
	 * BlueKai over https.
	 */
	public boolean isHttpsEnabled() {
		return httpsEnabled;
	}

	/**
	 * Method to change httpsEnabled settings. If httpsEnabled is set then data
	 * is sent to BlueKai over https
	 *
	 * @param httpsEnabled
	 */
	public void setHttpsEnabled(boolean httpsEnabled) {
		this.httpsEnabled = httpsEnabled;
	}

	/**
	 * @return
	 */
	public boolean useWebView() {
		return useWebView;
	}

	/**
	 * @param useWebView
	 */
	public void setUseWebView(boolean useWebView) {
		this.useWebView = useWebView;
	}

	/**
	 * @return
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * @param userAgent
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	private void addBlueKaiWebView(Context context) {
		try {
			blueKaiView = new BlueKaiWebView(context, this);
			blueKaiView.setWebClient();
			WebSettings webSettings = blueKaiView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			int height = 1, width = 1;
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			params.setMargins(10, 10, 10, 10);
			blueKaiView.setBackgroundColor(Color.LTGRAY);
			if (activity != null) {
				activity.addContentView(blueKaiView, params);
			} else {
				Logger.warn(TAG, "Activity is null. Cannot add webview");
			}
		} catch (Exception ex) {
			Logger.error(TAG, "Error while adding BlueKai webview", ex);
		}
	}

	private void checkForExistingData() {
		if (database != null) {
			ParamsList paramsList = database.getParams();
			if (paramsList != null && !paramsList.isEmpty()) {
				sendExistingData(paramsList);
			}
		}
	}

	private void sendExistingData(ParamsList paramsList) {
		Logger.debug(TAG, "IsAllowDataPosting --> " + settings.isAllowDataPosting());
		if (settings.isAllowDataPosting()) {
			if (useWebView) {
				SendData sendData = new SendData(paramsList, handler, true);
				Thread thread = new Thread(sendData);
				thread.start();
			} else {
				sendDataWithoutWebView(paramsList, true);
			}
		}

	}

	private void sendData(String key, String value) {
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put(key, value);
		sendData(paramsMap);
	}

	private void sendData(Map<String, String> paramsMap) {
		Logger.debug(TAG, "IsAllowDataPosting --> " + settings.isAllowDataPosting());
		ParamsList paramsList = new ParamsList();
		Iterator<String> it = paramsMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = paramsMap.get(key);
			Params params = new Params();
			params.setKey(key);
			params.setValue(value);
			paramsList.add(params);
		}

		if (settings.isAllowDataPosting()) {
			if (useWebView) {
				if (blueKaiView == null) {
					addBlueKaiWebView(context);
				}
				SendData sendData = new SendData(paramsList, handler, false);
				Thread thread = new Thread(sendData);
				thread.start();
			} else {
				sendDataWithoutWebView(paramsList, false);
			}

		}
	}

	/**
	 * Makes an async call to tags server using BKWebServiceRequestTask
	 *
	 * @param paramsList
	 * @param existingData
	 */
	private void sendDataWithoutWebView(final ParamsList paramsList, final boolean existingData) {

		BKRequest request = getBkRequestObject(paramsList, existingData);

		// Not making the actual call if dev mode is on
		if (!devMode) {
			BKWebServiceRequestTask webServiceTask = getBKWebServiceRequestTaskObject(paramsList, existingData);
			webServiceTask.execute(request);
		}
	}

	private String sendDataSync(String key, String value) {
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put(key, value);
		return sendDataSync(paramsMap);
	}

	private String sendDataSync(Map<String, String> paramsMap) {
		ParamsList paramsList = new ParamsList();
		Iterator<String> it = paramsMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = paramsMap.get(key);
			Params params = new Params();
			params.setKey(key);
			params.setValue(value);
			paramsList.add(params);
		}

		return sendDataWithoutWebViewSync(paramsList, false);

	}

	/**
	 * Returns the BKRequest object for making the async call to tags server
	 *
	 * @param paramsList
	 * @param existingData
	 * @return
	 */
	private BKRequest getBkRequestObject(final ParamsList paramsList, final boolean existingData) {
		CoreTagConfig config = new CoreTagConfig();
		config.setSite(siteId);
		config.setAppVersion(appVersion);
		config.setHttps(httpsEnabled);
		if (!optOutPrivacy) {
			config.setAdvertisingId(advertisingId);
		}

		CoreTagProcessor coreTagProcessor = new CoreTagProcessor(config, paramsList);
		final String tagUrl = coreTagProcessor.getUrl();
		Logger.debug(TAG, "URL: " + tagUrl);
		if (devMode) {
			Toast.makeText(context, "URL: " + tagUrl, Toast.LENGTH_LONG).show();
		}

		BKRequest request = new BKRequest();
		request.setUrl(tagUrl);
		request.addHeader("User-Agent", userAgent);
		request.setType(Type.GET);
		return request;
	}

	/**
	 * Returns the BKWebServiceRequestTask object for making calls to the tags
	 * server
	 *
	 * @param paramsList
	 * @param existingData
	 * @return
	 */
	private BKWebServiceRequestTask getBKWebServiceRequestTaskObject(final ParamsList paramsList, final boolean existingData) {
		BKWebServiceRequestTask webServiceTask = new BKWebServiceRequestTask(new BKWebServiceListener() {

			@Override
			public void beforeSendingRequest() {
			}

			@Override
			public void afterReceivingResponse(BKResponse response) {
				Logger.debug(TAG, "Received response: " + response.getResponseBody());
				if (response.isError()) {
					onDataPosted(!response.isError(), "Problem posting data", existingData, paramsList);
				} else {
					onDataPosted(!response.isError(), "Data posted successfully. Response: " + response.getResponseBody(), existingData, paramsList);
				}
			}
		});
		return webServiceTask;
	}

	/**
	 * Makes a sync direct call to tags server using BKWebServiceRequestTask
	 *
	 * @param paramsList
	 * @param existingData
	 * @return
	 */
	private String sendDataWithoutWebViewSync(final ParamsList paramsList, final boolean existingData) {

		String returnedMessage = "Test string";
		BKRequest request = getBkRequestObject(paramsList, existingData);

		// Not making the actual call if dev mode is on
		if (!devMode) {
			BKWebServiceRequestTask webServiceTask = getBKWebServiceRequestTaskObject(paramsList, existingData);
			BKResponse response;
			try {
				response = webServiceTask.execute(request).get();
				returnedMessage = response.getResponseBody();
			} catch (Exception e) {
				Logger.warn(TAG, "Error sending request", e);
			}

		}
		return returnedMessage;
	}

	private class SendData implements Runnable, BKViewListener {

		private ParamsList paramsList = null, backupList = null;

		private Handler handler = null;

		private boolean existingData = false;

		private ParamsList currentList = null;

		public SendData(ParamsList paramsList, Handler handler, boolean existingData) {
			this.paramsList = paramsList;
			this.backupList = new ParamsList(paramsList);
			this.handler = handler;
			this.existingData = existingData;
			if (blueKaiView != null) {
				blueKaiView.setBKViewListerner(this);
			}
		}

		@Override
		public void run() {
			try {
				final String url = getURL();
				Logger.debug(TAG, "URL: " + url);
				if (devMode && handler != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(context, "URL: " + url, Toast.LENGTH_LONG).show();
						}
					});
				}
				if (url != null && !url.trim().equals("")) {
					try {
						currentList = new ParamsList(backupList.subList(0, backupList.size() - paramsList.size()));
						backupList.removeAll(currentList);
						Logger.debug(TAG, "Lists size :: " + paramsList.size() + " :: " + backupList.size());
						Logger.debug(TAG, "Sending list to be loaded in loadUrl() " + currentList.size());
						if (handler != null) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									showBlueKaiDialog(url, existingData, currentList, SendData.this);
								}
							});
						}
					} catch (Exception ex) {
						Logger.error(TAG, "Error while posting data", ex);
						currentList = new ParamsList(backupList.subList(0, backupList.size() - paramsList.size()));
						backupList.removeAll(currentList);
						onDataPosted(false, "Error posting data -- " + ex.getMessage(), existingData, currentList);
					}
				} else {
					onDataPosted(false, "Nothing to post", existingData, null);
				}

			} catch (Exception ex) {
				final String message = ex.getMessage();
				Logger.error(TAG, "Error while sending data", ex);
				currentList = new ParamsList(backupList.subList(0, backupList.size() - paramsList.size()));
				backupList.removeAll(currentList);
				onDataPosted(false, message, existingData, currentList);
			}
		}

		private String getURL() throws UnsupportedEncodingException {
			StringBuffer buffer = null;
			String url = (httpsEnabled ? HTTPS : HTTP) + (devMode ? SANDBOX_URL : BASE_URL) + "?site=" + getSiteId() + "&";
			String queryPart = "";
			Iterator<Params> it = paramsList.iterator();
			buffer = new StringBuffer();
			String tailString = "&appVersion=" + appVersion;
			if (!optOutPrivacy) {
				tailString = tailString + "&adid=" + advertisingId;
			}
			int tailLength = tailString.length();
			while (it.hasNext()) {
				Params params = it.next();
				String key = URLEncoder.encode(params.getKey(), "UTF-8");
				String value = URLEncoder.encode(params.getValue(), "UTF-8");
				if (buffer.length() + tailLength + key.length() + value.length() + 2 > 255) {
					break;
				} else {
					buffer.append(key + "=" + value);
					if (it.hasNext()) {
						buffer.append("&");
					}
					it.remove();
				}
			}
			buffer.append(tailString);
			queryPart = buffer.toString();
			return url + queryPart;
		}

		private void onDataPosted(boolean success, String message, boolean existingData, ParamsList paramsList) {
			Logger.debug(TAG, "OnDataPosted called ... status -> " + success + " this.ParamsList size --> " + this.paramsList.size());
			if (paramsList != null) {
				Logger.debug(TAG, "ParamsList --> " + paramsList.size());
			}
			if (!success) {
				if (existingData) {
					// updateData
					updateData(paramsList);
				} else {
					// persist
					persistData(paramsList);
				}
			} else {
				if (existingData) {
					// Clear data
					clearData(paramsList);
				}
			}
			if (BlueKai.this.listener != null) {
				BlueKai.this.listener.onDataPosted(success, message);
			}
			if (!this.paramsList.isEmpty()) {
				run();
			}
		}

		@Override
		public void onViewLoaded(boolean success, boolean existingData, ParamsList paramsList) {
			Logger.debug(TAG, "OnViewLoaded() called ... Status --> " + success);
			if (success) {
				onDataPosted(success, "Data posted successfully", existingData, paramsList);
			} else {
				onDataPosted(success, "Problem posting data", existingData, paramsList);
			}
		}
	}

	private void onDataPosted(boolean success, String message, boolean existingData, ParamsList paramsList) {
		Logger.debug(TAG, "OnDataPosted called ... status -> " + success);
		if (paramsList != null) {
			Logger.debug(TAG, "ParamsList --> " + paramsList.size());
		}
		if (!success) {
			if (existingData) {
				// updateData
				updateData(paramsList);
			} else {
				// persist
				persistData(paramsList);
			}
		} else {
			if (existingData) {
				// Clear data
				clearData(paramsList);
			}
		}
		if (this.listener != null) {
			this.listener.onDataPosted(success, message);
		}
	}

	private void clearData(ParamsList paramsList) {
		if (database != null && paramsList != null && !paramsList.isEmpty()) {
			database.clearParams(paramsList);
		}

	}

	private void persistData(ParamsList paramsList) {
		if (database != null && paramsList != null && !paramsList.isEmpty()) {
			database.persistData(paramsList);
		}
	}

	private void updateData(ParamsList paramsList) {
		if (database != null && paramsList != null && !paramsList.isEmpty()) {
			database.updateData(paramsList);
		}
	}

	@Override
	public void onSettingsChanged(Settings settings) {
		Logger.debug(TAG, "On Settings changed");
		this.settings = settings;
		if (settings.isAllowDataPosting()) {
			checkForExistingData();
		} else {
			universalOptOut();
		}
	}

	/**
	 * Method makes a call to a BlueKai endpoint to delete the user profile created at BlueKai and opt out the user universally.
	 * This will try to use the android advertising ID from the device. If the user has limited the ad tracking
	 * (i.e. preventing the use of adveritising ID), then the user Identifier if provided (in BlueKaiData object)
	 * while initializing the BlueKai instance would be used. If even that is not provided, then this call will not do anything,
	 * and opting out would just mean no further data would be sent to BlueKai.
	 */
	public void universalOptOut() {
		String userIdParamName = null;
		String userIdParamValue = null;

		if (!optOutPrivacy && advertisingId != null) {
			userIdParamName = "adid";
			userIdParamValue = advertisingId;
		} else if (blueKaiData != null && blueKaiData.getUserIdentifier() != null) {
			userIdParamName = blueKaiData.getUserIdentifier().first;
			userIdParamValue = blueKaiData.getUserIdentifier().second;
		}

		if (userIdParamName != null && userIdParamValue != null) {
			String url = OPT_OUT_BASE_URL + "/" + userIdParamName + "/" + userIdParamValue;
			BKRequest optOutRequest = new BKRequest();
			optOutRequest.setType(Type.DELETE);
			optOutRequest.addHeader("Content-Type", "application/json");
			optOutRequest.addHeader("User-Agent", userAgent);
			optOutRequest.addHeader("X-SiteID", siteId);
			optOutRequest.addHeader("X-Public", blueKaiData.getBkKey());
			optOutRequest.setUrl(url);

			String stringToSign = BKRequestHelper.getStringToSign(optOutRequest);
			Log.d(TAG, "String to sign: " + stringToSign);
			String signature = BKRequestHelper.signUrl(stringToSign, blueKaiData.getBkSecretKey());
			Log.d(TAG, "Signature is: " + signature);
			optOutRequest.addHeader("X-Hash", signature);

			BKWebServiceRequestTask task = new BKWebServiceRequestTask(null);

			try {
				BKResponse response = task.execute(optOutRequest).get();
				Log.d(TAG, response.toString());
				if (response.getResponseCode() != 200) {
					throw new RuntimeException("Dummy dummy dummy");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

	}

	private synchronized void showBlueKaiDialog(String url, boolean existingData, ParamsList paramsList, BKViewListener listener) {
		if (devMode) {
			AlertDialog.Builder alert = new AlertDialog.Builder(activity);
			WebView wv = new BlueKaiWebView(context);
			wv.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}
			});
			wv.getSettings().setJavaScriptEnabled(true);
			wv.loadUrl(url);
			alert.setView(wv);
			alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
			alert.show().getWindow().setLayout(600, 600);
		} else {
			blueKaiView.loadUrl(url, existingData, paramsList);
		}
	}

	@Override
	public void onViewLoaded(boolean success, boolean existingData, ParamsList paramsList) {
		Logger.debug(TAG, "OnViewLoaded() called ... Status --> " + success);
		if (success) {
			onDataPosted(success, "Data posted successfully", existingData, null);
		} else {
			onDataPosted(success, "Problem posting data", existingData, paramsList);
		}
	}

	private void populateAdvertisingId() {
		advertisingIdRetrieved = false;
		GetAdvertisingIdTask task = new GetAdvertisingIdTask(context, new BKAdvertisingIdListener() {

			@Override
			public void onReceivedAdvertisingId(AdInfo adInfo) {
				if (adInfo != null) {
					advertisingIdRetrieved = true;
					advertisingId = adInfo.getId();
					optOutPrivacy = adInfo.isLimitAdTrackingEnabled();
				}

			}

		});
		task.execute();

	}

}
