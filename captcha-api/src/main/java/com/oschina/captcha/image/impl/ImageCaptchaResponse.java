package com.oschina.captcha.image.impl;

import java.awt.image.BufferedImage;

import com.oschina.captcha.api.CaptchaResponse;

public class ImageCaptchaResponse extends CaptchaResponse {
	private BufferedImage image;

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
}
