package com.oschina.captcha.image.impl;

import java.awt.Color;
import java.awt.Font;

import com.oschina.captcha.api.CaptchaRequest;

public class ImageCaptchaRequest extends CaptchaRequest {
	private int width;
	private int height;
	
	private Color[] fontColors;//字体颜色集
	private Font[] fonts;//字体集
	private int defaultFontSize = 24;//默认字体-大小
	
	private Color bgColor;//图片背景-颜色
	
	private Color borderColor;//图片边框-颜色
	private float borderThickness = 1;//图片边框线-粗细
	
	private boolean autoNoisePoint;//自动噪点
	private boolean autoNoiseLine;//自动干扰线
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[ width=").append(width).append(" ,height=").append(height)
			.append(" ,captcha=").append(this.getCaptcha())
			.append(" ,autoNoisePoint=").append(autoNoisePoint)
			.append(" ,autoNoiseLine=").append(autoNoiseLine).append(" ]");
		return sb.toString();
	}
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public Color[] getFontColors() {
		return fontColors;
	}
	public void setFontColors(Color[] fontColors) {
		this.fontColors = fontColors;
	}
	public Font[] getFonts() {
		return fonts;
	}
	public void setFonts(Font[] fonts) {
		this.fonts = fonts;
	}
	public Color getBgColor() {
		return bgColor;
	}
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}
	public int getDefaultFontSize() {
		return defaultFontSize;
	}
	public void setDefaultFontSize(int defaultFontSize) {
		this.defaultFontSize = defaultFontSize;
	}
	public Color getBorderColor() {
		return borderColor;
	}
	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}
	public float getBorderThickness() {
		return borderThickness;
	}
	public void setBorderThickness(float borderThickness) {
		this.borderThickness = borderThickness;
	}
	public boolean isAutoNoisePoint() {
		return autoNoisePoint;
	}
	public void setAutoNoisePoint(boolean autoNoisePoint) {
		this.autoNoisePoint = autoNoisePoint;
	}
	public boolean isAutoNoiseLine() {
		return autoNoiseLine;
	}
	public void setAutoNoiseLine(boolean autoNoiseLine) {
		this.autoNoiseLine = autoNoiseLine;
	}
}
