<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<title>TreeSoft数据库管理系统</title>
	<meta name="Keywords" content="TreeSoft数据库管理系统,treeNMS">
    <meta name="Description" content="TreeSoft数据库管理系统,treeNMS">
    <meta name="viewport" content="width=device-width, initial-scale=0.85">
	<script src="${ctx}/static/plugins/easyui/jquery/jquery-1.11.1.min.js"></script>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/css/bglogin.css" />
	<link rel="icon" href="${ctx}/favicon.ico" mce_href="${ctx}/favicon.ico" type="image/x-icon">  
    <link rel="shortcut icon" href="${ctx}/favicon.ico" mce_href="${ctx}/favicon.ico" type="image/x-icon">
	<script>
	var captcha;
	function refreshCaptcha(){  
		document.getElementById("img_captcha").src="${ctx}/static/images/securityCode.jpg?t=" + Math.random();  
	}  
	
	function check(){
     if( $("#username").val()=="" ){
    	 $("#login_main_errortip").html("请输入用户名!");
    	  return false;
     }
     if( $("#password").val()=="" ){
    	  $("#login_main_errortip").html("请输入密码!");
    	  return false;
     }
      if( $("#captcha").val()=="" ){
    	   $("#login_main_errortip").html("请输入验证码!");
    	  return false;
     }
      
     if( $("#username").val().length>15 ){
    	  $("#login_main_errortip").html("请输入用户名!");
    	  return false;
     }
     return true;
    }

	</script>
</head>
<body>
	<div>
	<form id="loginForm" action="${ctx}/treesoft/loginVaildate" method="post" >
		<div class="login_top" >
			<div class="login_title"   >
				<span style="margin-left:10px; margin-top:10px; color:#fff;  font-size: 20px;"> <img src="${ctx}/static/images/logo.png" >TreeSoft数据库管理系统  <span style="color: #00824D;font-size:16px; font-weight:bold;">&nbsp;TreeNMS</span>  <span style="color: #fffl;font-size:12px;">V1.7.2</span> </span>
			</div>
		</div>
		<div style="float:left;width:100%;">
			<div class="login_main">
				<div class="login_main_top"></div>
				<div class="login_main_errortip" id="login_main_errortip">&nbsp; ${message} </div>
				<div class="login_main_ln">
					<input type="text" id="username" name="username" />
				</div>
				<div class="login_main_pw">
					<input type="password" id="password" name="password" />
				</div>
				<div class="login_main_yzm"    >
					  
					<input type="text" id="captcha" name="captcha"/> 
					<img alt="验证码" src="${ctx}/static/images/securityCode.jpg" title="点击更换" id="img_captcha" onclick="javascript:refreshCaptcha();"   />
					 
				</div>
				<div class="login_main_remb">
					<input id="rm" name="rememberMe" type="hidden"/>  <!-- <label for="rm"><span>记住我</span></label> -->
				</div>
				
				
				<div class="login_main_submit">
				    <input type="submit"  value=""  onclick="return check()" />
				</div>
			</div>
		</div>
		
		<div style="text-align:center" >
				&nbsp; &nbsp;适用于Redis, Memcached, 推荐使用Chrome, FireFox, IE9+ 浏览器
		 </div>
		
	</form>
  </div>
	 
</body>
</html>
