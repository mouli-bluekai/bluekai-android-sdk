package com.bluekai.sdk.task;

import com.bluekai.sdk.model.BKResponse;

public interface BKWebServiceListener {

	/**
	 * This method would be called on the UI Thread from
	 * BKWebServiceRequestTask.onPreExecute()
	 */
	public void beforeSendingRequest();

	/**
	 * This method would be called on the UI Thread from
	 * BKWebServiceRequestTask.onPostExecute()
	 * 
	 * @param response
	 */
	public void afterReceivingResponse(BKResponse response);

}
