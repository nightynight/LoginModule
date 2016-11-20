<%@ page import="com.brokepal.utils.XmlUtil" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2016/11/4
  Time: 14:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String fromPage = request.getParameter("from");
    String title = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.registerPageTitle");
    String nicknamePlaceholder = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.nicknamePlaceholder");
    String usernamePlaceholder = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.usernamePlaceholder");
    String passwordPlaceholder = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.passwordPlaceholder");
    String passwordConfirmPlaceholder = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.passwordConfirmPlaceholder");
    String emailPlaceholder = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.emailPlaceholder");
    String phonePlaceholder = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.phonePlaceholder");
    String register = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.register");
%>
<html>
<head>
    <title><%= title %></title>
    <jsp:include  page="common/head.jsp"/>
    <link href="<%= request.getContextPath()%>/resource/css/register.css" rel="stylesheet">
</head>
<body>
<jsp:include  page="common/header.jsp"/>
<div id="div_main" class="container" >
    <h1 id="title"><%= title %></h1>
    <form id="register_form" class="col-md-6 col-md-offset-3 col-lg-4 col-lg-offset-4"
          action="<%=request.getContextPath() %>/static/register<%= fromPage == null ? "" : "?from=" + fromPage%>"
          role="form" method="post">
        <input type="hidden" id="registerTimestamp" name="registerTimestamp"/><%-- 时间戳，用来处理F5刷新页面时的重复提交表单 --%>
        <div id="registerFailInfo" style="color: red;font-size: 15px; line-height: 20px;margin-bottom: 5px;">${registerFailInfo}</div><%-- 登录失败时的提醒信息 --%>

        <div class="form-group">
            <input type="text" class="form-control inlineBlock width-90" name="nickname" id="nickname" value="${nickname}" placeholder="<%= nicknamePlaceholder %>"  required />
            <span class="icon-asterisk red small"></span>
        </div>

        <div class="form-group">
            <input type="text" class="form-control inlineBlock width-90" name="username" id="username" placeholder="<%= usernamePlaceholder %>" required />
            <span class="icon-asterisk red small">&nbsp;</span>
            <span class="icon-ok-sign gray" id="usernamePass"></span>
        </div>

        <div class="form-group">
            <input type="password" class="form-control inlineBlock width-90" name="password" id="password" placeholder="<%= passwordPlaceholder %>" required/>
            <span class="icon-asterisk red small">&nbsp;</span>
            <span class="icon-ok-sign gray" id="passwordPass"></span>
        </div>

        <div class="form-group">
            <input type="password" class="form-control inlineBlock width-90" name="passwordConfirm" id="passwordConfirm" placeholder="<%= passwordConfirmPlaceholder %>" required/>
            <span class="icon-asterisk red small">&nbsp;</span>
            <span class="icon-ok-sign gray" id="passwordConfirmPass"></span>
        </div>

        <div class="form-group">
            <input type="email" class="form-control inlineBlock width-90" name="email" id="email" placeholder="<%= emailPlaceholder %>"  required />
            <span class="icon-asterisk red small"> </span>
        </div>

        <div class="form-group">
            <input type="text" class="form-control inlineBlock width-90" name="phone" id="phone" placeholder="<%= phonePlaceholder %>" />
        </div>

        <div class="form-group noMargin">
            <button type="submit" id="btn_register" class="btn btn-primary"><%= register %></button>
        </div>
    </form>
</div>

<script src="<%= request.getContextPath()%>/resource/script/jquery.jcryption.3.1.0.js"></script>
<script type="text/javascript">
    $(function(){
        $("#username").blur(function () {
            $.ajax({
                type: 'POST',
                url: "<%=request.getContextPath() %>/static/isUsernameExist",
                data: {"username" : $("#username").val()},
                success: function (result) {
                    if (result == "false") {
                        //提示通过验证
                        $("#usernamePass").removeClass("gray");
                        $("#usernamePass").addClass("green");
                    } else {
                        $("#usernamePass").removeClass("green");
                        $("#usernamePass").addClass("gray");
                    }
                }
            });
        });

        $("#password").blur(function () {
            var checkNum = /.+/;
            if (checkNum.test($(this).val())) {
                $("#passwordPass").removeClass("gray");
                $("#passwordPass").addClass("green");
            } else {
                $("#passwordPass").removeClass("green");
                $("#passwordPass").addClass("gray");
            }
        });

        $("#passwordConfirm").blur(function () {
            if ($(this).val() == $("#password").val()) {
                $("#passwordConfirmPass").removeClass("gray");
                $("#passwordConfirmPass").addClass("green");
            } else {
                $("#passwordConfirmPass").removeClass("green");
                $("#passwordConfirmPass").addClass("gray");
            }
        });

        $("#passwordConfirm").blur(function () {

        });

        $("#register_form").submit(function(){
            $("#btn_register").attr("disabled","disabled");
            var registerTimestamp = new Date().getTime();
            $("#registerTimestamp").val(registerTimestamp);
            var encrypt = new JSEncrypt();
            var publickKey = "${publicKey}";
            encrypt.setPublicKey(publickKey);
            var encryptedPasswd = encrypt.encrypt($('#password').val());
            $('#password').val(encryptedPasswd);
            var encryptedPasswdConfirm = encrypt.encrypt($('#passwordConfirm').val());
            $('#passwordConfirm').val(encryptedPasswdConfirm);
        });
    });
</script>
</body>
</html>
