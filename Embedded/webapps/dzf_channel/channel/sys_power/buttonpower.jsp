<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="../../inc/easyui.jsp"></jsp:include>
	<link rel="stylesheet" type="text/css" href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>> 
	<script type="text/javascript" src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8"></script>
	<script type="text/javascript" src=<%UpdateGradeVersion.outversion(out, "../../js/sys_power/buttonpower.js" );%> charset="UTF-8"></script>
</head>
<body>
	<div id="types" class="wrapper">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="editBtn" onclick="edit()">修改</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="saveBtn" onclick="save()">保存</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="cancelBtn" onclick="cancel()">取消</a> 
				 </div> 
			</div>
	    </div>
	    <div class="mod-inner" style="height:auto;">
			<div id="dataGrid" class="grid-wrap">
				<table id="grid" ></table>
			</div>
		</div>
    </div>
</body>
</html>