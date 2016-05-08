package com.oschina.captcha.api;

public abstract class CaptchaRequest {
	private String captcha;
	private Object requestObj;
	
	public String getCaptcha() {
		return captcha;
	}
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
	public Object getRequestObj() {
		return requestObj;
	}
	public void setRequestObj(Object requestObj) {
		this.requestObj = requestObj;
	}
}
