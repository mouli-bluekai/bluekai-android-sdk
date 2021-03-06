package com.bluekai.sdk.model;

/**
 * Request object for making server calls using BKWebServiceRequestTask
 * 
 * @author moulimukherjee
 *
 */
public class BKRequest {
	public enum Type {
		GET, POST
	};

	// The type of request, GET or POST
	private Type type;

	// The body of the request in case of POST or the URL parameters in case of
	// GET requests
	private String payload;

	// The URL of the request
	private String url;

	// The content type in case of a POST request
	private String contentType;

	private String userAgent;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

}
