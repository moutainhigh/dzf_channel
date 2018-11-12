<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>商品分类</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/dealmanage/goodstype.js");%> charset="UTF-8" type="text/javascript"></script>

</head>
<body>
	<div id="List_panel" class="wrapper" data-options="closed:false">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="add()">新增</a>
				</div>
			</div>
		</div>
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		<div id="addDialog" class="easyui-dialog" style="width:400px;height:220px;padding-top:30px;" 
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<form id="addForm" method="post">
				<div class="time_col time_colp11">
					<div style="display: inline-block;">
						<label style="text-align:right;width:140px;"><i class="bisu">*</i>分类名称</label>
						<input id="vname" name="vname" class="easyui-textbox" 
							data-options="required:true,validType:'length[0,20]'" style="width:150px;height:25px;"/>
						<input type="hidden" id="pk_goodstype" name="pk_goodstype" >
					</div>
				</div>
			</form>
			<div style="text-align:center;margin-top:40px;">
			    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" title="Ctrl+S" onclick="save()">保存</a> 
				<a href="javascript:void(0)"  class="ui-btn ui-btn-xz" title="CTRL+Z" onclick="cancel()">取消</a>
			</div>
		</div>
		
	</div>
</body>
</html>