package com.bluekai.sdk.model;

/**
 * Response object when making server calls using BKWebServiceRequestTask
 * 
 * @author moulimukherjee
 *
 */
public class BKResponse {
	// The response code, eg: 200, 201 etc.
	private int responseCode;

	// Flag if error occurred
	private boolean error;

	// The body of the response
	private String responseBody;

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

}
