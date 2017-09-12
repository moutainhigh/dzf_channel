<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
<title>服务套餐定义</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>  rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/packagedef/packagedef.js");%>  charset="UTF-8" type="text/javascript"> </script>
</head>
<body>
	<div id="types" class="wrapper">
		<div class="mod-toolbar-top">
			
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<label>查询期间：</label> 
					<input id="begdate" name="begdate" class="easyui-datebox" style="width:110px;height:28px;text-align:left">
					--
					<input id="enddate" name="enddate" class="easyui-datebox" style="width:110px;height:28px;text-align:left">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" data-options="plain:true" onclick="reload()">查询</a>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="addBtn" onclick="addType()">新增</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="editBtn" onclick="modify()">修改</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="publishBtn" onclick="publish()">发布</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="offBtn" onclick="updateOff()">下架</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="delBtn" onclick="del()">删除</a>
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
