package com.bluekai.sdk.task;

import android.content.Context;
import android.os.AsyncTask;

import com.bluekai.sdk.helpers.BKAdvertisingIdClient;
import com.bluekai.sdk.helpers.BKAdvertisingIdClient.AdInfo;
import com.bluekai.sdk.utils.Logger;

/**
 * AsyncTask to fetch the Advertising ID in background. Reading of advertising ID is a
 * blocking task, so it should always run on a background thread.
 * 
 * @author moulimukherjee
 *
 */
public class GetAdvertisingIdTask extends AsyncTask<Void, Integer, AdInfo> {

	private final String TAG = GetAdvertisingIdTask.class.getSimpleName();

	private Context context;

	private BKAdvertisingIdListener listener;

	public GetAdvertisingIdTask(Context context, BKAdvertisingIdListener listener) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	protected AdInfo doInBackground(Void... params) {
		AdInfo adInfo = null;
		try {
			adInfo = BKAdvertisingIdClient.getAdvertisingIdInfo(context);
		} catch (Exception e) {
			Logger.warn(TAG, e.getMessage(), e);
		}
		return adInfo;

	}

	@Override
	protected void onPostExecute(AdInfo result) {
		super.onPostExecute(result);
		listener.onReceivedAdvertisingId(result);

	}

}
