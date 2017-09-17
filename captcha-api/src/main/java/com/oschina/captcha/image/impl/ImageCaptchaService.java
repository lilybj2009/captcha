package com.oschina.captcha.image.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oschina.captcha.api.CaptchaService;
import com.oschina.captcha.image.util.ImageCaptchaUtils;
/**
 * 图片验证码生成服务
 *
 */
public class ImageCaptchaService implements CaptchaService<ImageCaptchaRequest, ImageCaptchaResponse> {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected int timeout = 20;//存取缓存操作响应超时时间(毫秒数)
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.oscn.captcha.icaptcha.Icaptcha#service(com.oscn.captcha.icaptcha.CaptchaRequest)
	 */
	public ImageCaptchaResponse service(ImageCaptchaRequest request)
			throws Exception {
		// 性能计算
		long st = System.currentTimeMillis();

		// request参数校正
		request = this.checkRequest(request);

		// 生成图片验证码
		BufferedImage image = this.generateImage(request);

		// 封装response
		ImageCaptchaResponse response = new ImageCaptchaResponse();
		response.setCaptcha(request.getCaptcha());
		response.setImage(image);
		response.setResponseObj(image);

		// 性能计算
		long consumingTime = System.currentTimeMillis() - st;
		if (logger.isDebugEnabled()) {
			String message = "性能分析 ==> 生成图片验证码[" + request.getCaptcha() + "]耗时[" + consumingTime + "]毫秒" +
					" ==> width=[" + request.getWidth() + "] ,height=[" + request.getHeight() + "]";
			logger.debug(message);
		}
		
		if((consumingTime)>timeout){
			if(this.logger.isWarnEnabled()){
				this.logger.warn("service_timeout_" + (consumingTime) + "_for[" + request + "]");
			}
		}

		return response;
	}
	
	/**request参数校正
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected ImageCaptchaRequest checkRequest(ImageCaptchaRequest request)
			throws Exception {
		/*
		 * ================== 参数准备 ==================
		 */
		// captcha
		if (null == request || request.getCaptcha() == null
				|| request.getCaptcha().trim().isEmpty()) {
			throw new Exception("验证码为空!");
		}
		char[] codeChars = request.getCaptcha().toCharArray();
		// height 图片高
		int height = request.getHeight();
		// width 图片宽
		int width = request.getWidth();

		// fonts 字体集
		Font[] fonts = request.getFonts();
		// fontColors
		Color[] fontColors = request.getFontColors();
		// bgColor
		Color bgColor = request.getBgColor();
		//borderColor
		Color borderColor = request.getBorderColor();
		//borderThickness
		float borderThickness = request.getBorderThickness();
		/*
		 * ================== 参数校正 ==================
		 */
		int defaultFontSize = request.getDefaultFontSize();
		if(defaultFontSize < 2){
			defaultFontSize = 2;
		}
		int minHeight = defaultFontSize + 2;
		if (height < minHeight) {
			height = minHeight;
		}
		int minWidth = height * codeChars.length /2;
		if (width < minWidth) {
			width = minWidth;
		}
		if (null == fonts || fonts.length != codeChars.length) {
			fonts = new Font[codeChars.length];
		}
		Font randomFont = ImageCaptchaUtils.getRandomFont(defaultFontSize, Font.PLAIN);
		for (int i=0 ; i< fonts.length ; i++) {
			if (null == fonts[i]) {
				fonts[i] = randomFont;
			}
		}
		if (null == fontColors || fontColors.length != codeChars.length) {
			fontColors = new Color[codeChars.length];
		}
		Color randomColor = ImageCaptchaUtils.getRandomColor(20, 130);
		for (int i=0 ; i< fontColors.length ; i++) {
			if (null == fontColors[i]) {
				fontColors[i] = randomColor;
			}
		}
		if (null == bgColor) {
			bgColor = new Color(255,255,255);
		}
		if(null != borderColor){
			if(borderThickness <= 0){
				borderThickness = 1;
			}
		}
		
		request.setHeight(height);
		request.setWidth(width);
		request.setFonts(fonts);
		request.setFontColors(fontColors);
		request.setBgColor(bgColor);
		request.setBorderColor(borderColor);
		request.setBorderThickness(borderThickness);
		
		return request;
	}
	
	/**
	 * 生成图片验证码
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected BufferedImage generateImage(ImageCaptchaRequest request)
			throws Exception {
		/*
		 * ================== 参数准备 ==================
		 */
		// captcha
		String captcha = request.getCaptcha();
		if (captcha == null || captcha.trim().isEmpty()) {
			throw new Exception("验证码不能为空!");
		}
		char[] codeChars = captcha.toCharArray();
		// height 图片高
		int height = request.getHeight();
		// width 图片宽
		int width = request.getWidth();
		// fonts 字体集
		Font[] fonts = request.getFonts();
		// fontColors
		Color[] fontColors = request.getFontColors();
		// bgColor
		Color bgColor = request.getBgColor();
		/*
		 * ================== 迭代生成字符图片 ==================
		 */
		//统计字符总的宽度(width - countFontWidth < height/3以决定第一个图片是否左移)
		int countFontWidth = 0;
		
		/* 待处理的字符图片 */
		BufferedImage[] images = new BufferedImage[codeChars.length];
		for (int i = 0; i < codeChars.length; i++) {
			char codeChar = codeChars[i];
			/* 首先创建一个 height * height 的图片 */
			images[i] = new BufferedImage(height, height, BufferedImage.TYPE_INT_RGB);

			/* 使得绘制的图片背景透明（必须的步骤） */
			Graphics2D g2d = images[i].createGraphics();
			images[i] = g2d.getDeviceConfiguration().createCompatibleImage(height ,height, Transparency.TRANSLUCENT);
			g2d.dispose();
			g2d = images[i].createGraphics();

			/* 设置字体及颜色 */
			Font font = fonts[i];
			Color fontColor = fontColors[i];
			g2d.setFont(font);
			g2d.setColor(fontColor);

			/* 变换矩阵，对字符分别进行scale,rotate,shear变换 */
			AffineTransform affineTransform = new AffineTransform();
			// 缩放变换
			double scale_x = ImageCaptchaUtils.getRandomOffset(1.0, 1.0);
			double scale_y = ImageCaptchaUtils.getRandomOffset(1.0, 1.0);
			affineTransform.scale(scale_x, scale_y);
			// 旋转变换
			double radian = Math.PI/2.0-0.5;// PI=3.14=180度
			double theta = ImageCaptchaUtils.getRandomOffset(-radian , radian);
			double rotate_x = height / 2.0;
			double rotate_y = height / 2.0;
			affineTransform.rotate(theta, rotate_x, rotate_y);
			// 剪切变换
			affineTransform.shear(0, 0);
			g2d.setTransform(affineTransform);

			/* 计算绘制字符时的基线位置，使字符在图片中央处 */
			FontRenderContext fontRenderContext = g2d.getFontRenderContext();
			LineMetrics lineMetrics = font.getLineMetrics(Character.toString(codeChar), fontRenderContext);
			FontMetrics fontMetrics = g2d.getFontMetrics();
			int charWidth = fontMetrics.charWidth(codeChar);//字符占用宽度
			float charHeight = lineMetrics.getHeight();//文本高度
			float ascent  = lineMetrics.getAscent();//基线到 ascender 线
			float descent = lineMetrics.getDescent();//基线到下降线的距离
			float leading = lineMetrics.getLeading();//下降线的底部到下一行顶部的建议距离
			
			int fontX = (int) ((height - charWidth) / 2);
			int fontY = (int) ((height + ascent) / 2  - descent/2);
			/* 绘制字符 */
			g2d.drawString(Character.toString(codeChar), fontX, fontY);
			g2d.dispose();
			
			countFontWidth += charWidth;
			if(logger.isDebugEnabled()){
				String debug = "字符[ "+codeChar+" ]" 
						+ " ==> charWidth=[" + charWidth + "]" 
						+ " ==> charHeight=[" + charHeight +"] ,ascent=[" + ascent + "] ,descent=[" + descent + "] ,leading=[" + leading + "]"
						 ;
				logger.debug( debug );
			}
			if(logger.isDebugEnabled()){
				String debug = "generateImage ==> " 
						+ " g2d.drawString("+ codeChar + " ,"+ fontX +" ,"+ fontY +") " 
						+ " ==> 字体信息{ fontName=[" +font.getFontName()+ "] ,fontSize=[" + font.getSize()  +"]"
						+ " ==> 旋转参数 radian=["+theta+"] ,rotate_x=[" + rotate_x + "] ,rotate_y=[" + rotate_y + "] "
						+ " ==> 缩放参数 scale_x=[" + scale_x + "] ,scale_y=[" + scale_y + "] " ;
				logger.debug( debug );
			}
		}
		/*
		 * ================== 合成多个字符图片为一图 ==================
		 */
		int startX = (width - countFontWidth)*3/4;
		startX = ImageCaptchaUtils.getRandomOffset(0, startX-2);
		BufferedImage captchaImage = appendImages(images, request ,startX);
		
		return captchaImage;
	}

	/**
	 * 拼接验证码图片
	 * @param images
	 * @param request
	 * @param startX 第一张图的X起始座标
	 * @return
	 */
	private BufferedImage appendImages(BufferedImage[] images,ImageCaptchaRequest request ,int startX) {
		// height 图片高
		int height = request.getHeight();
		// width 图片宽
		int width = request.getWidth();

		BufferedImage bgImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_BGR);
		Graphics2D g2d = bgImage.createGraphics();
		
		/* 背景 */
		Color bgColor = request.getBgColor();
		g2d.setColor(bgColor);
		g2d.fillRect(0, 0, width, height);
		
		/* 噪点  */
		if(request.isAutoNoisePoint()){
			g2d.setColor(ImageCaptchaUtils.getRandomColor(160, 200));
			for (int i = 0; i < width/2; i++) {
				int x = ImageCaptchaUtils.getRandomOffset(1, width-1);
				int y = ImageCaptchaUtils.getRandomOffset(1, height-1);
				g2d.fillRect(x, y, 1, 1);
			}
		}
		/* 干扰线  */
		if(request.isAutoNoiseLine()){
			g2d.setColor(ImageCaptchaUtils.getRandomColor(160, 200));
			for (int i = 0; i < width/2; i++) {
				int x = ImageCaptchaUtils.getRandomOffset(0, width);
				int y = ImageCaptchaUtils.getRandomOffset(0, height);
				int xl = ImageCaptchaUtils.getRandomOffset(0, width);
				int yl = ImageCaptchaUtils.getRandomOffset(0, height);
				g2d.drawLine(x, y, x + xl, y + yl);
			}
		}
		
		/* 边框 */
		Color borderColor = request.getBorderColor();
		if(null != borderColor){
			g2d.setColor(borderColor);
			Stroke stroke = new BasicStroke(request.getBorderThickness());//设置线宽
			g2d.setStroke(stroke);
			g2d.drawRect(0, 0, width-1, height-1);
		}
		String debug = null;
		if(logger.isDebugEnabled()){
			debug = "appendImages ==> ";
		}

		/* 拼接第一张图 */
		int index = 0;
		int drawX = 0;
		if(startX >= 0){
			drawX = startX;
		}
		if (images != null && images.length != 0) {
			g2d.drawImage(images[index], drawX, 0, images[index].getWidth(),images[0].getHeight(), null);
			if(logger.isDebugEnabled()){
				debug += " 图（" + (index) + "）X=[" + drawX + "] ";
			}
			drawX += images[index].getWidth();
			index++;
		}
		/* 拼接第一张以后的图 */
		while (index < images.length) {
			//计算两图文字间距
			int distance = this.calculateDistance(images[index - 1] ,images[index]);
			//System.out.println("第["+(index - 1)+"]图文字 与 第["+(index)+"]张图文字 距离：" + distance);
			g2d.drawImage(images[index], drawX - distance, 0, images[index].getWidth(), images[0].getHeight(), null);
			if(logger.isDebugEnabled()){
				debug += "， 图（" + (index) + "）X=[" + (drawX - distance) + "] ";
			}
			drawX += images[index].getWidth() - distance;
			index++;
		}
		g2d.dispose();
		
		if(logger.isDebugEnabled()){
			logger.debug(debug);
		}
		return bgImage;
	}

	/**计算两图文字间距
	 * <li>按行扫描</li>
	 * 
	 * @param leftImage
	 * @param rightImage
	 * @return
	 */
	private int calculateDistance(BufferedImage leftImage, BufferedImage rightImage) {

		// 左图每行右侧空白字符个数列表
		int[][] leftBlank = calculateBlankNum(leftImage);
		// 右图每行左侧空白字符个数列表
		int[][] rightBlank = calculateBlankNum(rightImage);

		int[] tempArray = new int[leftImage.getHeight()];
		for (int row = 0; row < leftBlank.length; row++) {
			if (rightBlank[row][0] == 0) {
				//右图空白行
				tempArray[row] = leftBlank[row][1] + leftImage.getWidth();
			} else {
				//右图文字与左图文字 间隔
				tempArray[row] = leftBlank[row][1] + rightBlank[row][0];
			}
		}
		return min(tempArray);
	}

	/**
	 * 计算每个图片每行的左右空白像素个数. 
	 * <li>int[row][0]存储左边空白像素个数</li>
	 * <li>int[row][1]存储右边空白像素个数</li>
	 * 
	 * @param image
	 * @return
	 */
	private int[][] calculateBlankNum(BufferedImage image) {

		int width = image.getWidth();
		int height = image.getHeight();
		//初始化
		int[][] result = new int[height][2];
		for (int row = 0; row < result.length; row++) {
			result[row][0] = 0;
			result[row][1] = width;
		}
		//空白像素点
		int[] blankPixel = new int[] { 0, 0, 0, 0 };//Red Green Blue max
		int[] colorArray = new int[4];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				colorArray = image.getRaster().getPixel(col, row, colorArray);
				//不是空白像素点
				if (!equalArray(colorArray, blankPixel)) {
					if (result[row][0] == 0) {
						//左边空白像素个数
						result[row][0] = col;
					}
					//右边空白像素个数
					result[row][1] = width - col - 1;
				}
			}
		}
		return result;
	}

	/**
	 * 判断两个数组是否相等，要求长度相等，每个元素的值也相等
	 * 
	 * @param colorArray
	 * @param colorArray2
	 * @return
	 */
	private boolean equalArray(int[] arrayA, int[] arrayB) {
		if (arrayA == null || arrayB == null) {
			return false;
		}
		if (arrayA.length != arrayB.length) {
			return false;
		}
		for (int i = 0; i < arrayA.length; i++) {
			if (arrayA[i] != arrayB[i]) {
				return false;
			}
		}
		return true;
	}
	/**
	 * 取数组中最小值
	 * @param array
	 * @return
	 */
	private int min(int[] array) {
		int result = Integer.MAX_VALUE;
		for (int item : array) {
			if (item < result) {
				result = item;
			}
		}
		return result;
	}
	//====================== 扭曲图片 ======================  
	private BufferedImage warpImage(BufferedImage srcImage) {
		Random random = new Random();
		double dMultValue = random.nextInt(7) + 3;// 波形的幅度倍数，越大扭曲的程序越高，一般为3  
		double dPhase = random.nextInt(6);// 波形的起始相位，取值区间（0-2＊PI）  

		BufferedImage destImage = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < destImage.getWidth(); i++) {
			for (int j = 0; j < destImage.getHeight(); j++) {
				int nOldX = getXwarp(dPhase, dMultValue, destImage.getHeight(), i, j);
				int nOldY = j;
				if (nOldX >= 0 && nOldX < destImage.getWidth() && nOldY >= 0
						&& nOldY < destImage.getHeight()) {
					destImage.setRGB(nOldX, nOldY, srcImage.getRGB(i, j));
				}
			}
		}
		return destImage;
	}
	private int getXwarp(double dPhase, double dMultValue,
			int height, int xPosition, int yPosition) {
		double dx = (double) (Math.PI * yPosition) / height + dPhase;
		double dy = Math.sin(dx);
		return xPosition + (int) (dy * dMultValue);
	} 
}
