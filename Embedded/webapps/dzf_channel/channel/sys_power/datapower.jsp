<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="../../inc/easyui.jsp"></jsp:include>
	<link rel="stylesheet" type="text/css" href=<%UpdateGradeVersion.outversion(out, "../../css/main.css");%> >
	<link rel="stylesheet" type="text/css" href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>> 
	<script type="text/javascript" src=<%UpdateGradeVersion.outversion(out, "../../js/sys_power/datapower.js" );%> charset="UTF-8"></script>
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
			<form id="dataPower" method="post">
				<div class="time_col time_colp11" style="display:none">
					<input id="roleid" name="roleid" type='text'> 
				</div>
				<div class="time_col time_colp10">
					<input id="sy" type="radio" name="level" value="1"/>
					<label for='sy' style='width:250px'>所有数据权限</label> 
				</div>
				<div class="time_col time_colp10">
					<input id="dq" type="radio" name="level" value="2"/>
					<label for='dq' style='width:250px'>大区数据权限</label> 
				</div>
				<div class="time_col time_colp10">
					<input id="kh" type="radio" name="level" value="3"/>
					<label for='kh' style='width:250px'>加盟商数据权限</label> 
				</div>
			</form>
		</div>
	</div>
</body>
</html>