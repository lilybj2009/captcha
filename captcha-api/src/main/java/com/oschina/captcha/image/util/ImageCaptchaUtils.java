package com.oschina.captcha.image.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Random;

public class ImageCaptchaUtils {
	public final static Random random = new Random();
	/**
	 * 取 随机的偏移量
	 * @param minOffset
	 * @param maxOffset
	 * @return
	 */
	public static double getRandomOffset(double minOffset, double maxOffset) {
		if (maxOffset < minOffset) {
			double temp = maxOffset;
			maxOffset = minOffset;
			minOffset = temp;
		}
		if (maxOffset == minOffset) {
			return minOffset;
		}
		double randomOffset = minOffset + Math.random() * (maxOffset - minOffset);
		return  randomOffset;
	}

	/**
	 * 取 随机的偏移量
	 * @param minOffset
	 * @param maxOffset
	 * @return [minOffset , maxOffset]
	 */
	public static int getRandomOffset(int minOffset, int maxOffset) {
		if (maxOffset < minOffset) {
			int temp = maxOffset;
			maxOffset = minOffset;
			minOffset = temp;
		}
		if (maxOffset == minOffset) {
			return minOffset;
		}
		int index = random.nextInt(maxOffset+1 - minOffset);
		return minOffset + index;
	}

	/**
	 * 给定范围获得随机颜色
	 * @param minColor
	 * @param maxColor
	 * @return
	 */
	public static Color getRandomColor(int minColor, int maxColor) {
		if (minColor > 255) {
			minColor = 255;
		}
		if (maxColor > 255) {
			maxColor = 255;
		}
		int r = getRandomOffset(minColor, maxColor);
		int g = getRandomOffset(minColor, maxColor);
		int b = getRandomOffset(minColor, maxColor);
		return new Color(r, g, b);
	}
	
	public static Font getRandomFont(){
		return getRandomFont(20 , Font.PLAIN);
	}
	public static Font getRandomFont(Integer fontSize ,Integer fontStyle){
		String fontName = localFonts[random.nextInt(localFonts.length)].getFontName();
		if(null == fontSize || fontSize <=0 ){
			fontSize = 20;
		}
		if(null == fontStyle || fontStyle < 0){
			fontStyle = Font.PLAIN;
		}
		Font font = new Font(fontName, fontStyle, fontSize);
		return font;
	}
	
	/**可用的本地字体集
	 * 
	 * @return
	 */
	public static Font[] getLocalFonts(){
		return localFonts;
	}
	static Font[] localFonts = getLocalEnvironmentFonts();
	private static  Font[] getLocalEnvironmentFonts() {
		java.util.List<Font> localFonts = new ArrayList<Font>();
		Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		//过滤不可用的字体
		String[] unAvailableFontNames = new String[] { "Bookshelf Symbol 7",
				"Estrangelo Edessa", "Gautami", "Latha", "MS Outlook",
				"MS Reference Specialty", "MT Extra", "MV Boli", "Mangal",
				"Marlett", "Raavi", "Shruti", "宋体-PUA", "Symbol", "Tunga",
				"Webdings", "Wingdings", "Wingdings 2", "Wingdings 3",
				"Blackadder ITC", "Bodoni MT Poster Compressed",
				"Brush Script MT Italic", "Chiller", "Edwardian Script ITC",
				"Forte", "Freestyle Script", "French Script MT", "Gigi",
				"Gill Sans MT Ext Condensed Bold", "Jokerman",
				"Kunstler Script", "Magneto Bold", "Matura MT Script Capitals",
				"Mistral", "Niagara Engraved", "Vivaldi Italic",
				"Old English Text MT", "Palace Script MT", "Parchment", "华文行楷",
				"Vladimir Script", "Monotype Corsiva", "Informal Roman",
				"Playbill", "Goudy Stout", "Curlz MT", "Elephant Italic",
				"Wide Latin" };
		for (Font font : fonts) {
			String fontName = font.getFontName();
			boolean available = true;
			if (null != fontName) {
				for (String name : unAvailableFontNames) {
					if (fontName.equalsIgnoreCase(name)) {
						available = false;
						break;
					}
				}
			}
			if (available) {
				localFonts.add(font);
			}
		}
		return localFonts.toArray(new Font[0]);
	}
}
