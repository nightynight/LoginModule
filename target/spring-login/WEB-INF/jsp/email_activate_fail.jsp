<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2016/11/8
  Time: 11:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Activate fail</title>
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
    ${activateError}
</div>
</body>
<script type="text/javascript">
    $(function(){

    });
</script>
</html>
