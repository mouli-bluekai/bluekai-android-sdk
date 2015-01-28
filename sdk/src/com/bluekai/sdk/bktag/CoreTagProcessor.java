package com.bluekai.sdk.bktag;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private Config config;

	public CoreTagProcessor(Config configuration) {
		params = new ArrayList<String>();
		this.config = configuration;
	}

	public void process() {
		//

	}

	private void addParam(String type, String key, String value) {
		if (type != null) {
			if (value != null) {
				try {
					params.add(type + "=" + URLEncoder.encode(key + "=" + value, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				params.add(type + "=" + key);
			}
		}
	}

	private void addBkParam(String key, String value) {
		this.addParam("phint", "__bk_" + key, value);
	}

	private void addHash(String key1, String key2, String value) {
		// TODO hashing function
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
