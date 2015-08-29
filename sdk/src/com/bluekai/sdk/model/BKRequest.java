package com.bluekai.sdk.model;

import java.util.ArrayList;
import java.util.List;

import android.util.Pair;

/**
 * Request object for making server calls using BKWebServiceRequestTask
 *
 * @author moulimukherjee
 */
public class BKRequest {
	public enum Type {
		GET, POST, PUT, DELETE
	}

	// The type of request, GET or POST
	private Type type;

	// The body of the request in case of POST/PUT requests
	private String payload;

	// The URL of the request
	private String url;

	private List<Pair<String, String>> headers;

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

	public List<Pair<String, String>> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Pair<String, String>> headers) {
		this.headers = headers;
	}

	public void addHeader(String name, String value) {
		if (headers == null) {
			headers = new ArrayList<Pair<String, String>>();
		}
		headers.add(new Pair<String, String>(name, value));
	}

	private String toString(List<Pair<String, String>> headers) {
		StringBuilder output = new StringBuilder();
		if (headers != null) {
			output.append("{");
			for (Pair<String, String> header : headers) {
				output.append("[" + header.first + ", " + header.second + "]");
			}
			output.append("}");
		}
		return output.toString();
	}

	@Override
	public String toString() {
		return "BKRequest{" +
				"type=" + type +
				", payload='" + payload + '\'' +
				", url='" + url + '\'' +
				", headers=" + toString(headers) +
				'}';
	}
}
