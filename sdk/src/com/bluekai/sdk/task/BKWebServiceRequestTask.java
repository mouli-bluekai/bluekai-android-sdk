package com.bluekai.sdk.task;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.bluekai.sdk.model.BKRequest;
import com.bluekai.sdk.model.BKResponse;

/**
 * The async task to make http get or post calls in the background
 *
 * @author moulimukherjee
 */
public class BKWebServiceRequestTask extends AsyncTask<BKRequest, Integer, BKResponse> {

	private BKWebServiceListener listener;

	public BKWebServiceRequestTask(BKWebServiceListener listener) {
		this.listener = listener;
	}

	@Override
	protected BKResponse doInBackground(BKRequest... params) {

		BKRequest request = params[0];
		HttpClient client = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpResponse response;
		BKResponse bkResponse = new BKResponse();

		String url = request.getUrl();
		Log.d("BKWebServiceRequestTask", request.toString());
		try {
			switch (request.getType()) {
				case POST:
					HttpPost post = new HttpPost(url);
					post.setEntity(new StringEntity(request.getPayload(), "UTF-8"));
					if (request.getHeaders() != null) {
						for (Pair<String, String> header : request.getHeaders()) {
							post.setHeader(header.first, header.second);
						}
					}
					response = client.execute(post, localContext);
					break;
				case PUT:
					HttpPut put = new HttpPut(url);
					put.setEntity(new StringEntity(request.getPayload(), "UTF-8"));
					if (request.getHeaders() != null) {
						for (Pair<String, String> header : request.getHeaders()) {
							put.setHeader(header.first, header.second);
						}
					}
					response = client.execute(put, localContext);
					break;

				case DELETE:
					HttpDelete delete = new HttpDelete(url);
					if (request.getHeaders() != null) {
						for (Pair<String, String> header : request.getHeaders()) {
							delete.setHeader(header.first, header.second);
						}
					}
					response = client.execute(delete, localContext);
					break;

				case GET:
				default:
					HttpGet httpGet = new HttpGet(url);
					if (request.getHeaders() != null) {
						for (Pair<String, String> header : request.getHeaders()) {
							httpGet.setHeader(header.first, header.second);
						}
					}
					response = client.execute(httpGet, localContext);
					break;
			}
			if (response != null) {
				HttpEntity httpEntity = response.getEntity();
				String body = EntityUtils.toString(httpEntity);

				bkResponse.setResponseBody(body);
				bkResponse.setResponseCode(response.getStatusLine().getStatusCode());
			} else {
				bkResponse.setError(true);
			}

		} catch (Exception e) {
			bkResponse.setError(true);
		}

		return bkResponse;
	}

	@Override
	protected void onPostExecute(BKResponse result) {
		if (listener != null) {
			listener.afterReceivingResponse(result);
		}
	}

	@Override
	protected final void onPreExecute() {
		if (listener != null) {
			listener.beforeSendingRequest();
		}
	}

}