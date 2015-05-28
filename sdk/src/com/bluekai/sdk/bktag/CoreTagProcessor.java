package com.bluekai.sdk.bktag;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bluekai.sdk.model.Params;
import com.bluekai.sdk.model.ParamsList;
import com.bluekai.sdk.utils.Logger;

/**
 * This class deduces the tags server url based upon the config object passed.
 * The logic is based on the core tag JavaScript file.
 * 
 * There's difference in behavior of the bkrid value. In this, it is generated
 * every time, while in the browser it's stored in a cookie.
 * 
 * @author moulimukherjee
 *
 */
public class CoreTagProcessor {

	public static final Map<String, String> configMapKeys = new HashMap<String, String>();

	static {
		configMapKeys.put("site", "site_id");
		configMapKeys.put("limit", "pixel_limit");
		configMapKeys.put("excludeBkParams", "ignore_meta");
		configMapKeys.put("excludeTitle", "exclude_title");
		configMapKeys.put("excludeKeywords", "exclude_keywords");
		configMapKeys.put("excludeReferrer", "exclude_referrer");
		configMapKeys.put("excludeLocation", "exclude_location");
		configMapKeys.put("partnerID", "partner_id");
		configMapKeys.put("allowMultipleCalls", "allow_multiple_calls");
		configMapKeys.put("suppressMultipleCalls", "suppress_multiple_calls");
		configMapKeys.put("callback", "callback");
		configMapKeys.put("useImage", "use_image");
		configMapKeys.put("useMultipleIframes", "use_multiple_iframes");
		configMapKeys.put("allData", "all_data");
		configMapKeys.put("timeOut", "timeout");
		configMapKeys.put("ignoreOutsideIframe", "ignore_outside_iframe");
		configMapKeys.put("eventScheduling", "event_scheduling");
		configMapKeys.put("suppressEventScheduling", "suppress_event_scheduling");
		configMapKeys.put("suppressCacheBusting", "suppress_cache_busting");
		configMapKeys.put("pixelUrl", "pixel_url");
		configMapKeys.put("pixelSecure", "pixel_secure");
		// Default rule for storing first party
		configMapKeys.put("useFirstParty", "use_first_party");
		configMapKeys.put("suppressFirstParty", "suppress_first_party");
		configMapKeys.put("sendStatidPayload", "send_statid_payload");
		configMapKeys.put("suppressStatidPayload", "suppress_statid_payload");
		// Globally masked
		configMapKeys.put("metaVars", "meta_vars");
		configMapKeys.put("jsList", "js_list");
		configMapKeys.put("paramList", "param_list");
		configMapKeys.put("useMobile", "use_mobile");
		configMapKeys.put("disableMobile", "disable_mobile");
		configMapKeys.put("isDebug", "is_debug");
		configMapKeys.put("limitGetLength", "limit_get_length");
	}

	private List<String> params;

	private CoreTagConfig config;

	private ParamsList inputParameters;

	private static final String PIXEL_URL = "http://tags.bluekai.com/";

	private static final String PIXEL_URL_SECURE = "https://stags.bluekai.com/";

	private static final String TAG = CoreTagProcessor.class.getSimpleName();

	public CoreTagProcessor(CoreTagConfig configuration, ParamsList parameters) {
		params = new ArrayList<String>();
		this.config = configuration;
		this.inputParameters = parameters;
		this.process();
	}

	private void process() {

		addParam("ret", "json", null);

		for (Params params : inputParameters) {
			addParam("phint", params.getKey(), params.getValue());
		}

		addParam("phint", "appVersion", config.getAppVersion());

		int bkrid = (int) Math.floor(Math.random() * Math.pow(2, 31));
		addParam("bkrid", String.valueOf(bkrid), null);

		int r = (int) (Math.random() * 9999999);
		addParam("r", String.valueOf(r), null);

		if (config.getAdvertisingId() != null) {
			addParam("adid", config.getAdvertisingId(), null);
		}

	}

	/**
	 * Returns the URL after the processing
	 * 
	 * @return
	 */
	public String getUrl() {
		StringBuilder url = new StringBuilder();

		if (config.isHttps()) {
			url.append(PIXEL_URL_SECURE);
		} else {
			url.append(PIXEL_URL);
		}
		if (config.getSite() != null) {
			url.append("site/").append(config.getSite());
		}
		url = url.append("?");

		for (String parameter : params) {
			url = url.append(parameter).append("&");
		}

		url = url.deleteCharAt(url.length() - 1);

		return url.toString();

	}

	private void addParam(String type, String key, String value) {
		if (type != null) {
			try {
				if (value != null) {
					params.add(type + "=" + URLEncoder.encode(key + "=" + value, "UTF-8"));

				} else {
					params.add(type + "=" + URLEncoder.encode(key, "UTF-8"));
				}
			} catch (UnsupportedEncodingException e) {
				Logger.warn(TAG, "Error encoding the parameters", e);
			}

		}
	}

	private void addBkParam(String key, String value) {
		this.addParam("phint", "__bk_" + key, value);
	}

	private void addHash(String key1, String key2, String value) {
		String md5Hash = value == null || value.equals("") ? "" : CoreTagUtil.hashString(value, "MD5");
		String sha256Hash = value == null || value.equals("") ? "" : CoreTagUtil.hashString(value, "SHA-256");
		this.addParam("phint", key1, md5Hash);
		this.addParam("phint", key2, sha256Hash);
	}

	private void addEmailHash(String value) {
		if (value == null) {
			value = "";
		}
		value = CoreTagUtil.normalizeEmail(value);
		this.addHash("e_id_m", "e_id_s", value);
	}

	private void addPhoneHash(String value) {
		if (value == null) {
			value = "";
		}
		value = CoreTagUtil.normalizePhone(value);
		this.addHash("p_id_m", "p_id_s", value);
	}

}
