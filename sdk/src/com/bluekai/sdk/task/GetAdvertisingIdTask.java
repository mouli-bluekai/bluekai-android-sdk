package com.bluekai.sdk.task;

import android.content.Context;
import android.os.AsyncTask;

import com.bluekai.sdk.helpers.BKAdvertisingIdClient;
import com.bluekai.sdk.helpers.BKAdvertisingIdClient.AdInfo;

public class GetAdvertisingIdTask extends AsyncTask<Void, Integer, AdInfo> {

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
			e.printStackTrace();
		}
		return adInfo;

	}

	@Override
	protected void onPostExecute(AdInfo result) {
		super.onPostExecute(result);
		listener.onReceivedAdvertisingId(result);

	}

}
