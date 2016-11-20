<%@ page import="com.brokepal.utils.XmlUtil" %><%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2016/11/9
  Time: 14:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String fromPage = request.getParameter("from");
    String sendValidateCode = XmlUtil.getNodeText((String) session.getAttribute("language"), "login.sendValidateCode");
    String resetPassword = XmlUtil.getNodeText((String) session.getAttribute("language"), "login.resetPassword");
    String resetPasswordPageTitle = XmlUtil.getNodeText((String) session.getAttribute("language"), "login.resetPassword");
    String passwordPlaceholder = XmlUtil.getNodeText((String) session.getAttribute("language"), "login.passwordPlaceholder");
    String passwordConfirmPlaceholder = XmlUtil.getNodeText((String) session.getAttribute("language"), "login.passwordConfirmPlaceholder");
    String emailPlaceholder = XmlUtil.getNodeText((String) session.getAttribute("language"), "login.emailPlaceholder");
    String validateCodePlaceholder = XmlUtil.getNodeText((String) session.getAttribute("language"), "login.validateCodePlaceholder");
%>
<html>
<head>
    <title><%= resetPasswordPageTitle %></title>
    <jsp:include  page="common/head.jsp"/>
    <link href="<%= request.getContextPath()%>/resource/css/reset-password.css" rel="stylesheet">
</head>
<body>
<jsp:include  page="common/header.jsp"/>
<div id="div_main" class="container" >
    <h1 id="title"><%= resetPasswordPageTitle %></h1>
    <form id="reset_password_form" class="col-md-6 col-md-offset-3 col-lg-4 col-lg-offset-4"
          action="<%=request.getContextPath() %>/static/resetPassword<%= fromPage == null ? "" : "?from=" + fromPage %>"
          role="form" method="post">
        <input type="hidden" id="resetPasswordTimestamp" name="resetPasswordTimestamp"/><%-- 时间戳，用来处理F5刷新页面时的重复提交表单 --%>
        <div id="resetPasswordFailInfo" style="color: red;font-size: 15px; line-height: 20px;margin-bottom: 5px;">${resetPasswordFailInfo}</div><%-- 登录失败时的提醒信息 --%>
        <div class="form-group">
            <input type="email" class="form-control inlineBlock width-55" name="email" id="email" value="${email}" placeholder="<%= emailPlaceholder %>"  required />
            <button type="button" class="btn btn-success width-40 right" id="btn_send_validate_code"><span class="countdown"></span><%= sendValidateCode %></button><br>
        </div>

        <div class="form-group">
            <input type="password" class="form-control inlineBlock width-95" name="password" id="password" placeholder="<%= passwordPlaceholder %>" required/>
            <span class="icon-ok-sign gray" id="passwordPass"></span>
        </div>

        <div class="form-group">
            <input type="password" class="form-control inlineBlock width-95" name="passwordConfirm" id="passwordConfirm" placeholder="<%= passwordConfirmPlaceholder %>" required/>
            <span class="icon-ok-sign gray" id="passwordConfirmPass"></span>
        </div>

        <div class="form-group">
            <input type="text" class="form-control inlineBlock width-95" name="validateCode" id="validateCode" placeholder="<%= validateCodePlaceholder %>" required/>
        </div>

        <div class="form-group noMargin">
            <button type="submit" id="btn_reset" class="btn btn-primary"><%= resetPassword %></button>
        </div>
    </form>
    <script src="<%= request.getContextPath()%>/resource/script/jquery.jcryption.3.1.0.js"></script>
    <script src="<%= request.getContextPath()%>/resource/script/countdown.js"></script>
    <script type="text/javascript">
        $(function () {
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

            $("#btn_send_validate_code").click(function () {
                $(this).attr("disabled", true);
                $.ajax({
                    type: 'POST',
                    url: "<%=request.getContextPath() %>/static/sendValidateEmail",
                    data: {"email": $("#email").val()},
                    dataType: 'json',
                    contentType: "application/x-www-form-urlencoded; charset=utf-8",
                    success: function (data) {
                        if (data.result == "succeed"){
                            $("#resetPasswordFailInfo").text("");
                            $('.countdown').countDown({
                                startNumber: 10,
                                callBack: function () {
                                    $("#btn_send_validate_code").attr("disabled", false);
                                    $(".countdown").hide();
                                }
                            });
                        } else {
                            $("#resetPasswordFailInfo").text(data.errorInfo);
                            $("#btn_send_validate_code").attr("disabled", false);
                        }
                    }
                });
            });

            $("#reset_password_form").submit(function () {
                $("#btn_reset").attr("disabled", "disabled");
                var timestamp = new Date().getTime();
                $("#resetPasswordTimestamp").val(timestamp);
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
