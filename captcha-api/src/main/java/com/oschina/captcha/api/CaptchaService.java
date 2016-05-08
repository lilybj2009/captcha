package com.oschina.captcha.api;
/**
 * 验证码服务
 *
 * @param <K>
 * @param <V>
 */
public interface CaptchaService<K extends CaptchaRequest, V extends CaptchaResponse> {
	/**
	 * 验证码生成服务
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public V service(K request) throws Exception;
}
