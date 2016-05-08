<%@page contentType="image/JPEG" pageEncoding="UTF-8"
	import="java.awt.*,java.awt.image.*,java.util.*,javax.imageio.*" %>
<%@page import="com.oschina.captcha.image.*,com.oschina.captcha.image.impl.*" %>
	
<%-- 图片验证码
	[可变字体]
	[字符可旋转变换]
--%>
<%!
	//获取 客户端 IP
	String getClientIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip != null && !"".equals(ip) && ip.contains(",")) {
			ip = ip.split(",")[0];
		}
		if (ip == null || ("").equals(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ("").equals(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ("").equals(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	//随机验证码(不包括 数字[0,1] 大写字母[I] 小写字母[l])
	static String getRandomCaptchaCode(int len) {
		String code = "";
		String codeArray = "23456789ABCDEFGHJKLMNOPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
		int arrayLen = codeArray.length();
		if (len <= 0) {
			len = 4;
		}
		if (len > arrayLen) {
			len = arrayLen;
		}
		Random r = new Random();
		for (int i = 0; i < len; i++) {
			int index = r.nextInt(arrayLen);
			code += codeArray.substring(index, index + 1);
		}
		return code;
	}

	//取 验证码
	String getAuthenticode(HttpServletRequest request) {
		String authenticode = getRandomCaptchaCode(4);
		request.getSession().setAttribute("authenticode", authenticode);
		return authenticode;
	}
	
	static Font getRandomFont(int fontSize){
		Random r = new Random();
		Font font = new Font(defaultFontNams[r.nextInt(defaultFontNams.length)] ,Font.PLAIN ,fontSize);
		return font;
	}
	static String [] defaultFontNams = new String [] {
		"Georgia"
	};
%>
<%
	//解决了不显示验证码问题
	response.reset();

	//设置页面不缓存   
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0);

	//取参数
	String widthStr = request.getParameter("width");
	String heightStr = request.getParameter("height");
	int width = 60;
	int height = 24;
	try {
		width = (int)Float.parseFloat(widthStr);
		height = (int)Float.parseFloat(heightStr);
	} catch (Exception e) {
		//【request请求参数异常】
		String error = "【request请求参数异常】" + "IP：" + getClientIP(request)
				+ "，jsessionId：" + request.getSession().getId()
				+ "，参数异常信息：{ width=[" + widthStr + "] ,height=["
				+ heightStr + "] }";
		System.err.println(error);
	}

	//检查参数
	boolean hasWarning = false;
	String warningMsg = "[warning]: ";

	int maxWidth = 1000;
	int maxHeight = 1000;
	if (width > maxWidth) {
		hasWarning = true;
		warningMsg += " width(" + width + ") > maxWidth(" + maxWidth + ") ；";
		width = maxWidth;
	}
	if (height > maxHeight) {
		hasWarning = true;
		warningMsg += " height(" + height + ") > maxHeight(" + maxHeight + ") ；";
		height = maxHeight;
	}
	int minWidth = 55;
	int minHeight = 18;
	if (width < minWidth) {
		hasWarning = true;
		warningMsg += " width(" + width + ") < minWidth(" + minWidth + ") ；";
		width = minWidth;
	}
	if (height < minHeight) {
		hasWarning = true;
		warningMsg += " height(" + height + ") < minHeight(" + minHeight + ") ；";
		height = minHeight;
	}
	//异常时,打印日志
	if (hasWarning) {
		//【request请求参数异常】
		String warning = "【request请求参数警告】" + "IP："
				+ getClientIP(request) + "，jsessionId："
				+ request.getSession().getId() + "，参数警告信息：{ "
				+ warningMsg + " }";
		System.err.println(warning);
	}
	
	//图片验证码服务接口
	ImageCaptchaService service = ImageCaptchaServiceSingleton.getInstance();
	//接口参数
	ImageCaptchaRequest param = new ImageCaptchaRequest();
	param.setCaptcha(getAuthenticode(request));
	param.setHeight(height);
	param.setWidth(width);
	param.setDefaultFontSize(height-2);
	param.setBorderColor(Color.LIGHT_GRAY);
	param.setBorderThickness(0.1F);
	param.setAutoNoisePoint(false);
	param.setAutoNoiseLine(false);
	Font []fonts = new Font[param.getCaptcha().length()];
	for(int i=0 ; i< fonts.length ;i++){
		fonts[i] = getRandomFont(param.getDefaultFontSize());
	}
	param.setFonts(fonts);
	// 生成图象
	BufferedImage image = service.service(param).getImage();
	
	// 输出图象到页面   
	ServletOutputStream sos = response.getOutputStream();
	ImageIO.write(image, "jpeg", sos);
	sos.flush();
	sos.close();
	sos = null;
	
	//解决异常 java.lang.IllegalStateException: getOutputStream() has already been called for this response
	response.flushBuffer();
	out.clear();
	out = pageContext.pushBody();
%>