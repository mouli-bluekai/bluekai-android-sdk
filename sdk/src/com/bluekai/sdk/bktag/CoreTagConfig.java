package com.bluekai.sdk.bktag;

public class CoreTagConfig {
	private String site;

	private String appVersion;

	private String limit;

	private Boolean excludeBkParams;

	private Integer partnerId;

	private Boolean allowMultipleCalls;

	private String callback;

	private Boolean allData;

	private Object timeOut;

	private Boolean ignoreOutsideIframe;

	private Boolean https = false;

	private String advertisingId;

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public Boolean getExcludeBkParams() {
		return excludeBkParams;
	}

	public void setExcludeBkParams(Boolean excludeBkParams) {
		this.excludeBkParams = excludeBkParams;
	}

	public Integer getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(Integer partnerId) {
		this.partnerId = partnerId;
	}

	public Boolean getAllowMultipleCalls() {
		return allowMultipleCalls;
	}

	public void setAllowMultipleCalls(Boolean allowMultipleCalls) {
		this.allowMultipleCalls = allowMultipleCalls;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public Boolean getAllData() {
		return allData;
	}

	public void setAllData(Boolean allData) {
		this.allData = allData;
	}

	public Object getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Object timeOut) {
		this.timeOut = timeOut;
	}

	public Boolean getIgnoreOutsideIframe() {
		return ignoreOutsideIframe;
	}

	public void setIgnoreOutsideIframe(Boolean ignoreOutsideIframe) {
		this.ignoreOutsideIframe = ignoreOutsideIframe;
	}

	public Boolean isHttps() {
		return https;
	}

	public void setHttps(Boolean https) {
		this.https = https;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public Boolean getHttps() {
		return https;
	}

	public String getAdvertisingId() {
		return advertisingId;
	}

	public void setAdvertisingId(String advertisingId) {
		this.advertisingId = advertisingId;
	}

}
