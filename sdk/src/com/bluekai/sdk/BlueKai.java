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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.widget.RelativeLayout;

import com.bluekai.sdk.listeners.BKViewListener;
import com.bluekai.sdk.listeners.DataPostedListener;
import com.bluekai.sdk.listeners.SettingsChangedListener;
import com.bluekai.sdk.model.Params;
import com.bluekai.sdk.model.ParamsList;
import com.bluekai.sdk.model.Settings;
import com.bluekai.sdk.utils.Logger;

public class BlueKai implements SettingsChangedListener, BKViewListener {
	private final String TAG = "BlueKai";
	private static BlueKai instance = null;

	private boolean devMode = false;
	private Activity activity = null;
	private Context context = null;
	private boolean useHttps = false;
	private final String HTTP = "http://";
	private final String HTTPS = "https://";
	private final String BASE_URL = "mobileproxy.bluekai.com/m.html";
	private final String SANDBOX_URL = "mobileproxy.bluekai.com/m-sandbox.html";
	private String siteId = "2";
	private String appVersion = "1.0";
	private String imei = "";
	private BlueKaiWebView blueKaiView;
	private BlueKaiDataSource database = null;
	private Settings settings;
	private FragmentManager fManager = null;
	private DataPostedListener listener;
	private Handler handler;

	private BlueKai() {
		this.devMode = false;
	}

	private BlueKai(Activity activity, Context context, boolean devMode, String siteId, String appVersion,
                    DataPostedListener listener, Handler handler) {
		this(activity, context, devMode, false, siteId, appVersion, listener, handler);
    }

	private BlueKai(Activity activity, Context context, boolean devMode, boolean useHttps, String siteId, String appVersion,
			DataPostedListener listener, Handler handler) {
		this.activity = activity;
		this.context = context;
		this.devMode = devMode;
		this.appVersion = appVersion;
		this.imei = getImei(context);
		if (!TextUtils.isEmpty(siteId) && !this.siteId.equals(siteId)) {
			this.siteId = siteId;
		}
		this.listener = listener;
		this.handler = handler;
		this.useHttps = useHttps;
		Logger.debug(TAG, " onCreate Dev Mode ? " + devMode);
		Logger.debug(TAG, " onCreate BK URL --> " + (useHttps ? HTTPS : HTTP) + (devMode ? SANDBOX_URL : BASE_URL));
		database = BlueKaiDataSource.getInstance(context);
		database.setSettingsChangedListener(this);
		settings = database.getSettings();
	}

	/**
	 * Set the fragment manager. Used when devMode is enabled to show webview in
	 * a popup dialog
	 * 
	 * @param fm FragmentManager
	 */
	public void setFragmentManager(FragmentManager fm) {
		this.fManager = fm;
	}

	/**
	 * Method to resume BlueKai process after calling application resumes. To
	 * use in onResume() of the calling activity
	 */
	public void resume() {
		Logger.debug(TAG, " resume Dev Mode ? " + devMode);
		if (!devMode) {
			addBlueKaiWebView(context);
		}
		if (settings.isAllowDataPosting()) {
			checkForExistingData();
		}
	}

	/**
	 * Method to get BlueKai instance
	 * 
	 * @param activity
	 *            Calling application activity reference
	 * @param context
	 *            Calling application context
	 * @param devMode
	 *            Developer mode. Set to enable webview to popup in a dialog.
	 *            Strictly for developer purposes only
	 * @param siteId
	 *            BlueKai site id
	 * @param appVersion
	 *            Version of the calling application
	 * @param listener
	 *            DataPostedListener. Calling activity should implement this
	 *            interface
	 * @param handler
	 *          Handler. Android os handler.
	 *
	 * @return BlueKai instance
	 */
	public static BlueKai getInstance(Activity activity, Context context, boolean devMode, String siteId,
			String appVersion, DataPostedListener listener, Handler handler) {
		Logger.debug("BlueKai", "Called get instance...");
		if (instance == null) {
			instance = new BlueKai(activity, context, devMode, siteId, appVersion, listener, handler);
		} else {
			instance.setActivity(activity);
			instance.setAppContext(context);
			instance.setDevMode(devMode);
			instance.setSiteId(siteId);
			instance.setAppVersion(appVersion);
			instance.setDataPostedListener(listener);
			instance.setHandler(handler);
		}
		return instance;
	}

	/**
	 * Method to get BlueKai instance
	 *
	 * @param activity
	 *          Calling application activity reference
	 * @param context
	 *          Calling application context
	 * @param devMode
	 *          Developer mode. Set to enable webview to popup in a dialog.
	 *          Strictly for developer purposes only
	 * @param useHttps
	 *          Secure mode. Set to enable data transfer to BlueKai over https.
	 * @param siteId
	 *          BlueKai site id
	 * @param appVersion
	 *          Version of the calling application
	 * @param listener
	 *          DataPostedListener. Calling activity should implement this
	 *          interface
	 * @param handler
	 *          Handler. Android os handler.
	 *
	 * @return BlueKai instance
	 */
    public static BlueKai getInstance(Activity activity, Context context, boolean devMode, boolean useHttps, String siteId,
                                      String appVersion, DataPostedListener listener, Handler handler) {
        Logger.debug("BlueKai", "Called get instance...");
        if (instance == null) {
            instance = new BlueKai(activity, context, devMode, useHttps, siteId, appVersion, listener, handler);
        } else {
			instance.setActivity(activity);
			instance.setAppContext(context);
			instance.setDevMode(devMode);
			instance.setUseHttps(useHttps);
			instance.setSiteId(siteId);
			instance.setAppVersion(appVersion);
			instance.setDataPostedListener(listener);
			instance.setHandler(handler);
		}
		return instance;
	}

	/**
	 * Convenience method to initialize and get instance of BlueKai without
	 * arguments
	 * 
	 * @return BlueKai instance
	 */
	public static BlueKai getInstance() {
		Logger.debug("BlueKai", "Called get instance...");
		if (instance == null) {
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
		instance.activity = activity;
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
		instance.context = context;
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
	 * @param devMode
	 *            Developer mode
	 */
	public void setDevMode(boolean devMode) {
		this.devMode = devMode;
		instance.devMode = devMode;
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
		instance.siteId = siteId;
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
	 * @param appVersion
	 *            Application version
	 */
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
		instance.appVersion = appVersion;
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
	 * @param listener
	 *            Listener implementation
	 */
	public void setDataPostedListener(DataPostedListener listener) {
		this.listener = listener;
		instance.listener = listener;
	}

	/**
	 * Get the listener that is configured to get notifications about status of
	 * data posting.
	 *
	 * @return listener DataPostedListener
	 */
	public DataPostedListener getListener() {
		return listener;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
		instance.handler = handler;
	}

	public Handler getHandler() {
		return handler;
	}

    /**
	 * Method to send data to BlueKai. Accepts a single key-value pair
	 *

	 * @param key
	 *            Key
	 * @param value
	 *            Value
	 */
	public void put(String key, String value) {
		sendData(key, value);
	}

	/**
	 * Convenience method to send a bunch of key-value pairs to BlueKai
	 * 
	 * @param map
	 *            Map with keys and values
	 * @deprecated as of release v1.0.3. Replaced by {@link #putAll(java.util.Map)}
	 */
	@Deprecated
	public void put(Map<String, String> map) {
		sendData(map);
	}

	/**
	 * Convenience method to send a bunch of key-value pairs to BlueKai
	 *
	 * @param map
	 *            Map with keys and values
	 */
	public void putAll(Map<String, String> map) {
		sendData(map);
	}

	/**
	 * Method to show BlueKai in-build opt-in screen
	 * 
	 * @param listener
	 *            Listener to get callback on settings change
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
	 * @param optIn
	 *            Opt-in (true or false)
	 * @Deprecated as of release v1.0.3. Replaced by {@link #setOptInPreference(boolean)}
	 */
	@Deprecated
	public void setOptIn(boolean optIn) {
		setOptInPreference(optIn);
	}

	/**
	 * Method to set user opt-in or opt-out preference
	 *
	 * @param optIn
	 *            Opt-in (true or false)
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
	 * Method to check if useHttps is enabled. If useHttps is set then data is sent to BlueKai over https
	 *
	 * @return useHttps flag that enables/disables data being sent to BlueKai over https.
	 */
	public boolean isUseHttps() {
		return useHttps;
	}

	/**
	 * Method to change useHttps settings. If useHttps is set then data is sent to BlueKai over https
	 * @param useHttps
	 */
	public void setUseHttps(boolean useHttps) {
		this.useHttps = useHttps;
	}


	private void addBlueKaiWebView(Context context) {
		try {
			blueKaiView = new BlueKaiWebView(context, this);
			blueKaiView.setWebClient();
			WebSettings webSettings = blueKaiView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			int height = 1, width = 1;
			if (devMode) {
				height = width = RelativeLayout.LayoutParams.MATCH_PARENT;
			}
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
			SendData sendData = new SendData(paramsList, handler, true);
			Thread thread = new Thread(sendData);
			thread.start();
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
			SendData sendData = new SendData(paramsList, handler, false);
			Thread thread = new Thread(sendData);
			thread.start();
		}
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
				Logger.debug(TAG, "URL to call ---> " + url);
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
			String url = (useHttps ? HTTPS : HTTP) + (devMode ? SANDBOX_URL : BASE_URL) + "?site=" + getSiteId() + "&";
			String queryPart = "";
			Iterator<Params> it = paramsList.iterator();
			buffer = new StringBuffer();
			String tailString = "&appVersion=" + appVersion;
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
			Logger.debug(TAG, "OnDataPosted called ... status -> " + success + " this.ParamsList size --> "
					+ this.paramsList.size());
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
		}
	}

	private synchronized void showBlueKaiDialog(String url, boolean existingData, ParamsList paramsList,
			BKViewListener listener) {
		if (devMode) {
			BlueKaiViewDialog dialog = new BlueKaiViewDialog();
			dialog.setLoadURL(url, existingData, paramsList);
			dialog.setBKViewListener(listener);
			dialog.show(fManager, "bkdialog");
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

	private String getImei(Context ctx) {
		String androidId = "";
		try {
			androidId = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
			Logger.debug(TAG, "Android ID --> " + androidId);
		} catch (Exception e1) {
			Logger.error(TAG, "IMEIManager :: Exception in processData" + e1.getMessage());
		}

		if ((androidId == null) || (androidId.equals("")) || (androidId.equals("0") || (androidId.startsWith("00000")))) {
			androidId = getPsudeoIMEI();
		}
		return androidId;
	}

	private String getPsudeoIMEI() {
		String imei = "";
		String format = "yyyyMMddHHmmssSSSS";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		imei = sdf.format(new Date());
		return imei;
	}
}

