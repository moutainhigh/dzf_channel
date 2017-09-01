<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>

<%
	String errorMessage = (String)session.getAttribute("errorMsg");
%>
<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>在线会计</title>
<jsp:include page="./inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, request.getContextPath()+"/css/index.css");%> rel="stylesheet" />

</head>
<body>
<div class="wrapper">
	<div class="error_div">
		<h4><%=errorMessage != null ?errorMessage:""%></h4>
		<!-- <div class="error_but"><a href="javascript:CloseTab(this, 'close')" class="ui-btn ui-btn-xz">返回</a></div> -->
	</div>
</div>
</body>
</html>