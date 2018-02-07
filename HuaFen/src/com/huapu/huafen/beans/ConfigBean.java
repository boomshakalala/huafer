package com.huapu.huafen.beans;


/**
 * 同步信息
 * @author liang_xs
 *
 */
public class ConfigBean extends BaseResult{
	private String splashScreen;
	private String serverTime;
	private String splashLink;
	private int splashType;
	private int splashId;
	private String token;
	private String useTerms;
	private String appVersion;
	/**
	 * 更新等级
	 * 		1不更新
	 * 		2可更新
	 * 		3必须更新
	 */
	private int appLevel; 
	/**
	 * app更新地址
	 */
	private String appDownloadUrl;
	/**
	 * app更新内容
	 */
	private String appContent;
	
	
	
	public String getAppDownloadUrl() {
		return appDownloadUrl;
	}
	public void setAppDownloadUrl(String appDownloadUrl) {
		this.appDownloadUrl = appDownloadUrl;
	}
	public String getAppContent() {
		return appContent;
	}
	public void setAppContent(String appContent) {
		this.appContent = appContent;
	}
	public int getAppLevel() {
		return appLevel;
	}
	public void setAppLevel(int appLevel) {
		this.appLevel = appLevel;
	}
	public String getSplashScreen() {
		return splashScreen;
	}
	public void setSplashScreen(String splashScreen) {
		this.splashScreen = splashScreen;
	}
	public String getServerTime() {
		return serverTime;
	}
	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}
	public String getSplashLink() {
		return splashLink;
	}
	public void setSplashLink(String splashLink) {
		this.splashLink = splashLink;
	}
	public int getSplashType() {
		return splashType;
	}
	public void setSplashType(int splashType) {
		this.splashType = splashType;
	}
	public int getSplashId() {
		return splashId;
	}
	public void setSplashId(int splashId) {
		this.splashId = splashId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUseTerms() {
		return useTerms;
	}
	public void setUseTerms(String useTerms) {
		this.useTerms = useTerms;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	
}
