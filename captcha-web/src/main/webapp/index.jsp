<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript">
		function reflushCaptcha(){
			var captchaImage = document.getElementById('captchaImage');
			var height = 40;
			var width = 140;
			var param = "height="+height+"&width="+width+"&_time="+ (new Date().getTime());
			captchaImage.src = "<%=path %>/captcha/captcha.jsp?"+ param;
		}
	</script>
  </head>
  
  <body>
    <h3>验证码</h3>
    <hr/>
    
    <div align="center">
    	<img src="<%=path %>/captcha/captcha.jsp?width=140&height=40" id="captchaImage"/>&nbsp;&nbsp;<input type="button" value="点击获取验证码" onclick="reflushCaptcha();" />
    </div>
    <hr/>
    
  </body>
</html>
