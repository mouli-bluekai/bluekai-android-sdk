package com.bluekai.sdk.task;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.bluekai.sdk.model.BKRequest;
import com.bluekai.sdk.model.BKResponse;

import android.os.AsyncTask;

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

		try {
			switch (request.getType()) {
				case POST: {
					HttpPost post = new HttpPost(url);
					post.setHeader("Content-type", request.getContentType());
					post.setEntity(new StringEntity(request.getPayload(), "UTF-8"));
					response = client.execute(post, localContext);

					break;
				}

				case GET:
				default: {

					String urlWithParams = url + ((url.indexOf("?") == -1) ? "?" : "&");
					urlWithParams = urlWithParams + request.getPayload();
					HttpGet httpGet = new HttpGet(urlWithParams);
					response = client.execute(httpGet);

					break;
				}
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
		super.onPostExecute(result);
		listener.afterReceivingResponse(result);
	}

	@Override
	protected final void onPreExecute() {
		super.onPreExecute();
		listener.beforeSendingRequest();
	}

}