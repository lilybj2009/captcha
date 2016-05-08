package com.oschina.captcha.image;

import com.oschina.captcha.image.impl.ImageCaptchaService;
/**
 * 图片验证码生成服务 单例
 *
 */
public class ImageCaptchaServiceSingleton {
	private static ImageCaptchaService instance = new ImageCaptchaService();

	public static ImageCaptchaService getInstance() {
		return instance;
	}
}