package com.bluekai.sdk.task;

import com.bluekai.sdk.helpers.BKAdvertisingIdClient.AdInfo;

/**
 * Listener called by advertising ID async task onPostExecute()
 * 
 * @author moulimukherjee
 *
 */
public interface BKAdvertisingIdListener {

	public void onReceivedAdvertisingId(AdInfo adInfo);

}
