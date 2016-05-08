package com.oschina.captcha.api;

public abstract class CaptchaResponse {
	private String captcha;
	private Object responseObj;
	
	public String getCaptcha() {
		return captcha;
	}
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
	public Object getResponseObj() {
		return responseObj;
	}
	public void setResponseObj(Object responseObj) {
		this.responseObj = responseObj;
	}
}
