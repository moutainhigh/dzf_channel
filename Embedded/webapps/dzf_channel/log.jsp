<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.chat.CryptUtil"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<%
String cuserid = request.getParameter("id");
String touserid = CryptUtil.getInstance().encryptAES(cuserid);
%>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>Index</title>
    <jsp:include page="./inc/easyui.jsp"></jsp:include>
    <script src="./layui/layui.js"></script>
    <link href="./layui/css/layui.css" rel="stylesheet" />
<!--     <style type="text/css">
        .layim-chat-main {
            height: 300px;
        }</style> -->
    <script type="text/javascript">
		var touserid = '<%=touserid%>';
    </script>
</head>
<body>
    <div class="layim-chat-main" style="width:70%; height:100%">
        <ul>

        </ul>
    </div>
    <div id="page" style="text-align:center">
    </div>
    <script type="text/javascript" src="./layui/log.js"></script>
</body>
</html>