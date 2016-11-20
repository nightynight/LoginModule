<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
<%@ page import="java.security.interfaces.RSAPublicKey" %>
<%@ page import="com.brokepal.utils.RSACryptoUtil" %>
<%@ page import="com.brokepal.utils.XmlUtil" %>
<%
    String fromPage = request.getParameter("from");
    String usernameOrEmailPlaceholder = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.usernameOrEmailPlaceholder");
    String title = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.loginPageTitle");
    String passwordPlaceholder = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.passwordPlaceholder");
    String validateCodePlaceholder = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.validateCodePlaceholder");
    String login = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.login");
    String register = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.register");
    String forgetPassword = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.forgetPassword");
    String keepPassword = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.keepPassword");
    String changeVerifyCode = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.changeVerifyCode");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title><%= title %></title>
    <jsp:include  page="common/head.jsp"/>
    <link href="<%= request.getContextPath()%>/resource/css/login.css" rel="stylesheet">
    <script src="<%= request.getContextPath()%>/resource/script/jquery.jcryption.3.1.0.js"></script>
    <script src="<%= request.getContextPath()%>/resource/script/jbase64.js"></script>
</head>
<body>
<div id="div_main" class="container" >
    <img src="<%= request.getContextPath()%>/resource/images/icon.png" id="img_icon"/>
    <h1 id="title"><%= title %></h1>

    <form id="login_form" class="col-md-6 col-md-offset-3 col-lg-4 col-lg-offset-4"
          role="form" action="<%=request.getContextPath() %>/static/login<%= fromPage == null ? "" : "?from=" + fromPage %>"
          method="post">
        <input type="hidden" id="loginTimestamp" name="loginTimestamp"/><%-- 时间戳，用来处理F5刷新页面时的重复提交表单 --%>
        <div id="loginFailInfo" style="color: red;font-size: 15px; line-height: 20px;margin-bottom: 5px;">${loginFailInfo}</div><%-- 登录失败时的提醒信息 --%>

        <div class="form-group">
            <input type="text" class="form-control" name="username" id="username" placeholder="<%= usernameOrEmailPlaceholder %>" required />
        </div>

        <div class="form-group">
            <input type="password" class="form-control" name="password" id="password" placeholder="<%= passwordPlaceholder %>" required/>
        </div>

        <div id="div_verify"></div><%-- 登录失败多次后使用验证码，动态添加相关标签 --%>

        <div class="form-group" style="text-align: left;">
            <input type="checkbox" id="keepPassword" checked/><label for="keepPassword"><%= keepPassword %></label>
            <a href="<%=request.getContextPath()%>/static/register<%= fromPage == null ? "" : "?from=" + fromPage%>" style="float: right;display: inline-block;margin-left: 20px;"><%= register %></a>
            <a href="<%=request.getContextPath()%>/static/resetPassword" style="float: right;"><%= forgetPassword %></a>
            <div style="clear: both"></div>
        </div>

        <div class="form-group noMargin">
            <button type="submit" id="btn_login" class="btn btn-primary btn-block"><%= login %></button>
        </div>
    </form>
</div>


<script type="text/javascript">
    $(function () {
        $("#login_form").submit(function () {
            $("#btn_login").attr("disabled","disabled");
            var loginTimestamp = new Date().getTime();
            $("#loginTimestamp").val(loginTimestamp);//加个时间戳，为了解决F5刷新导致重复提交表单的一些问题（在服务器判断两个时间戳是否相等，相等则是重复提交）
            storage.setItem('username',$("#username").val());
            if ($("#keepPassword").is(':checked')){
                var passwordCrypt = BASE64.encoder($("#password").val());//返回编码后的字符
                storage.setItem('password',passwordCrypt);
            } else {
                storage.removeItem('password');
            }
            var encrypt = new JSEncrypt();  //使用该对象来实现加密
            var publickKey = "${publicKey}";
            if ($('#password') != null && $('#password') != undefined) {
                encrypt.setPublicKey(publickKey);//设置密钥
                var encryptedUsername = encrypt.encrypt($('#username').val());//加密用户名
                var encryptedPasswd = encrypt.encrypt($('#password').val());//加密密码
                $('#username').val(encryptedUsername);  //将用户名的文本框的值设置为加密后的用户名
                $('#password').val(encryptedPasswd);    //将密码的文本框的值设置为加密后的密码
            }
        });

        var failedCount = ${failedCount};
        //如果登录失败次数超过某个值，则开始使用验证码
        if (failedCount > 2) {
            $("#div_verify").html("<div class=\"form-group\" style=\"text-align: left;\">\n" +
                    "<input type=\"text\" class=\"form-control\" style=\"width: 35%;display: inline\" name=\"verifyCode\" id=\"verifyCode\" placeholder=\"<%= validateCodePlaceholder %>\" required/>\n" +
                    "<span class=\"icon-ok-sign green hideWithPos\" id=\"passVerify\"></span>\n" +
                    "<img id=\"verifyCodeImage\" src=\"<%=request.getContextPath() %>/static/verifyCodeImage\"/>\n" +
                    "<span id=\"changeVerifyCode\" class=\"clickSpan\" style=\"vertical-align: bottom\"><%= changeVerifyCode %></span>\n" +
                    "</div>");
            $("#verifyCode").bind('input propertychange',function () { //验证码输入框中的内容改变时，触发该事件
                //当验证码的字母个数到达四个时，验证一下是否输入正确
                if ($(this).val().length == 4) {
                    $.ajax({
                        type: 'POST',
                        url: "<%=request.getContextPath() %>/static/verifyCode",
                        data: {"verifyCode" : $("#verifyCode").val()},
                        success: function (result) {
                            if (result == "succeed") {
                                //提示通过验证
                                $("#passVerify").css("visibility","visible");
                            } else {
                                $("#passVerify").css("visibility","hidden");
                            }
                        }
                    });
                } else {
                    $("#passVerify").css("visibility","hidden");
                }
            });

            $("#changeVerifyCode").click(function () {
                $("#passVerify").prop("checked",false);
                $("#verifyCodeImage").attr("src","<%= request.getContextPath() %>/static/verifyCodeImage?timestamp=" + new Date().getTime());
                //加个时间戳，防止浏览器从缓存中读取验证码图片
            });
        }

        var storage = window.localStorage;
        var username = storage.getItem('username'); //从localStorage中获取username
        var password = storage.getItem('password'); //从localStorage中获取加密后的password

        if (username != null && username != "null" && username != ""){
            $("#username").val(username); //将从localStorage中获取的username填入username文本框
        }
        if (password != null && password != "null" && password != "") {
            var unicode= BASE64.decoder(password);//返回解码后的unicode码数组。
            //可由下面的代码编码为string，生成原始密码
            var password = '';
            for(var i = 0; i < unicode.length; ++i){
                password += String.fromCharCode(unicode[i]);
            }
            $("#password").val(password); //将密码填入对应文本框
            if (failedCount == 0){
                $("#login_form").submit();
            }
        }
        <%
            boolean isFailed = false;
            String loginFailInfo = (String)request.getSession().getAttribute("loginFailInfo");
            if (loginFailInfo != null  &&
            (loginFailInfo.equals(XmlUtil.getNodeText((String) session.getAttribute("language"),"login.accountNotExist"))
            || loginFailInfo.equals(XmlUtil.getNodeText((String) session.getAttribute("language"),"login.wrongPassword"))))
                isFailed =true;
        %>
        if (<%= isFailed %>) {
            storage.removeItem('password'); //登录失败时，将password从localStorage中删除
            $("#password").val(""); //将对应的文本框清空
        }
    });
</script>
</body>
</html>