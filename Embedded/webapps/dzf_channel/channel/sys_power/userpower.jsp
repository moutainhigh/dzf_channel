<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="../../inc/easyui.jsp"></jsp:include>
	
	<link rel="stylesheet" type="text/css" href=<%UpdateGradeVersion.outversion(out, "../../css/main.css");%> >
	<link rel="stylesheet" type="text/css" href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>> 
	<script type="text/javascript" src=<%UpdateGradeVersion.outversion(out, "../../js/sys_power/userpower.js" );%> charset="UTF-8"></script>
</head>
<body>
	<div class="mod-toolbar-content"  style="height: 5%;">
		<div class="left">
			<a class="ui-btn ui-btn-xz" id="editbn" onclick="edit();">分配</a> 
			<a class="ui-btn ui-btn-xz" id="savebn" onclick="save();" >保存</a>
			<a class="ui-btn ui-btn-xz" id="cancelbn" onclick="cancel();" >取消</a>
		</div>
	</div>
	<div class="easyui-layout" style="width: 100%;height: 90%;">
		<div data-options="region:'west',split:true" style="width:30%;" id="dataGrid">
			<table id="grid"></table>
		</div>
		<div data-options="region:'center'" id="dataTreeGrid" style="width:70%;">
			<table id="treeGrid"></table>
		</div>
	</div>
</body>
</html>