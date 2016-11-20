<%@ page import="com.brokepal.utils.XmlUtil" %>
<%@ page import="com.brokepal.constant.Const" %>
<%--
  Created by IntelliJ IDEA.
  User: chenchao
  Date: 16/11/5
  Time: AM11:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String headNickname = (String) session.getAttribute("nickname");
    String headLogout = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.logout");
    String headLogin = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.login");
    String headRegister = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.register");
    String headChinese = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.Chinese");
    String headEnglish = XmlUtil.getNodeText((String) session.getAttribute("language"),"login.English");
%>
<div id="header" class="container_fluid">

    <script src="http://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
    <script type="text/javascript">
        $(function () {
            var tag = "";
            if ("<%= headNickname %>" != "null"){
                tag = "<img src=\"<%= request.getContextPath()%>/resource/images/icon.png\" class=\"img_icon col-sm-offset-1\"/>\n" +
                        "        <span class=\"icon-bell-alt col-sm-offset-6 col-md-offset-7\" style=\"margin-right: 20px;\"></span>\n" +
                        "        <span class=\"inlineBlock dropdown\">\n" +
                        "            <a href=\"##\" data-toggle=\"dropdown\" class=\"dropdown-toggle\">\n" +
                        "                <span class=\"icon-user\"> ${nickname} <span class=\"icon-sort-down\"></span> </span>\n" +
                        "            </a>\n" +
                        "            <ul class=\"dropdown-menu\">\n" +
                        "                <li><a href=\"##\" id=\"head_logout\" class=\"\"><%= headLogout %></a></li>\n" +
                        "            </ul>\n" +
                        "        </span>\n" +
                        "        <select id=\"head_select_language\" class=\"form-control inlineBlock col-sm-offset-1\" style=\"width: 100px;\">\n" +
                        "            <option selected value=\"<%= Const.Language.CHINESE %>\" style=\"padding: 20px;\"><%= headChinese %></option>\n" +
                        "            <option value=\"<%= Const.Language.ENGLISH %>\"><%= headEnglish %></option>\n" +
                        "        </select>";
            } else {
                tag = "<img src=\"<%= request.getContextPath()%>/resource/images/icon.png\" class=\"img_icon col-sm-offset-1\"/>\n" +
                        "        <button  id=\"head_btn_login\" class=\"btn btn-primary col-sm-offset-5 col-md-offset-6 \" style=\"margin-right: 10px;\"><%= headLogin %></button>\n" +
                        "        <button  id=\"head_btn_register\" class=\"btn btn-primary\"><%= headRegister %></button>\n" +
                        "        <select id=\"head_select_language\" class=\"form-control inlineBlock col-sm-offset-1\" style=\"width: 100px;\">\n" +
                        "            <option selected value=\"<%= Const.Language.CHINESE %>\" style=\"padding: 20px;\"><%= headChinese %></option>\n" +
                        "            <option value=\"<%= Const.Language.ENGLISH %>\"><%= headEnglish %></option>\n" +
                        "        </select>";
            }
            $("#header").html(tag);
            var language = $("#head_select_language").val();
            if (language != null && language != undefined && language != "" ){
                $("#head_select_language").val($.cookie("language"));
            }
            $("#head_select_language").change(function(){
                $.cookie("language",$("#head_select_language").val(),{ expires: 7, path: '/' });
                $.ajax({
                    type: 'POST',
                    url: "<%=request.getContextPath() %>/static/language",
                    data: {"language" : $("#head_select_language").val()},
                    success: function (result) {
                        location.reload();
                    }
                });
            });

            $("#head_btn_login").click(function () {
                window.location.href = "<%=request.getContextPath() %>/static/login";
            });
            $("#head_btn_register").click(function () {
                window.location.href = "<%=request.getContextPath() %>/static/register";
            });

            $("#head_logout").click(function () {
                var storage = window.localStorage;
                storage.removeItem('password');
                window.location.href = "<%=request.getContextPath() %>/static/login";
            });
        });
    </script>
</div>
