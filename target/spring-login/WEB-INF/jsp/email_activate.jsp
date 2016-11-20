<%@ page import="com.brokepal.utils.XmlUtil" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2016/11/7
  Time: 18:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String fromPage = request.getParameter("from");
    String sendAgain = XmlUtil.getNodeText((String) session.getAttribute("language"), "login.sendAgain");
    String login = XmlUtil.getNodeText((String) session.getAttribute("language"), "login.login");

    boolean ifPleaseActivate = false;
    boolean ifValidateCodeExpire = false;
    boolean ifActivated = false;
    String registerStatus = (String) session.getAttribute("registerStatus");
    String infoPleaseActivate = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.pleaseActivate");
    String infoValidateCodeExpire = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.activateCodeExpire");
    String infoActivated = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.activated");
    if (infoPleaseActivate.equals(registerStatus))  ifPleaseActivate = true;
    if (infoValidateCodeExpire.equals(registerStatus))  ifValidateCodeExpire = true;
    if (infoActivated.equals(registerStatus))  ifActivated = true;
%>
<html>
<head>
    <title>Register status</title>
    <jsp:include  page="common/head.jsp"/>
    <style type="text/css">
        #div_main{
            text-align: center;
            padding-top: 40px;
            font-size: 30px;
        }
    </style>
</head>
<body>
<jsp:include  page="common/header.jsp"/>
<div id="div_main" class="container" >
    <p style="display: <%= ifPleaseActivate ? "block" : "none"%>">
        <%= infoPleaseActivate %>
        <button class="sendAgain btn btn-success" disabled><span class="countdown"></span><%= sendAgain %></button>
    </p>
    <p style="display: <%= ifValidateCodeExpire ? "block" : "none"%>">
        <%= infoValidateCodeExpire %>
        <button class="sendAgain btn btn-success" disabled><span class="countdown"></span><%= sendAgain %></button>
    </p>
    <p style="display: <%= ifActivated ? "block" : "none"%>">
        <%= infoActivated %>
        <a href="<%=request.getContextPath()%>/static/login<%= fromPage == null ? "" : "?from=" + fromPage %>"><%= login %></a>
    </p>
</div>
<script src="<%= request.getContextPath()%>/resource/script/countdown.js"></script>
<script type="text/javascript">
    $(function(){
        window.localStorage.removeItem('password');
        $('.countdown').countDown({
            startNumber: 10,
            callBack: function () {
                $(".sendAgain").attr("disabled",false);
                $(".countdown").hide();
            }
        });
        $(".sendAgain").click(function () {
            $.ajax({
                type: 'POST',
                url: "<%=request.getContextPath() %>/static/sendActivateEmail",
                data: {"email" : "${email}"}
            });
        });
    });
</script>
</body>
</html>
