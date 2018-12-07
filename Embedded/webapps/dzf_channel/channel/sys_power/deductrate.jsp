<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>扣款率设置</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../jslib/ajaxfileupload.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/sys_power/deductrate.js");%> charset="UTF-8" type="text/javascript"></script>
<style type="text/css">
.selecticon {
    background: url(../../img/add_lan.png) no-repeat;
}
</style>
</head>
<body>
	<div id="List_panel" class="wrapper" data-options="closed:false">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="filter">
						<label class="mr5">加盟商：</label>
						<input style="height:28px;width:245px" class="easyui-textbox" id="filter_value" 
							prompt="请输入加盟商名称或编码,按Enter键 "/> 
					</div>
				</div>
				
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="onBatchSet()">批量设置</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="onImport()">导入</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="onExport()">导出</a>
				</div>
			</div>
		</div>
		
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		<!-- 导入对话框  begin-->
		<div id="impUpdDlg" class="easyui-dialog" style="overflow: auto;" data-options="closed:true">
			<form id="impUpdForm" method="post" enctype="multipart/form-data" style="text-align:center;font-size:14px;">
				<p>
				 	<p class="selecticon" style="cursor:pointer;border:1px solid #A5A5A5;width:78px;height:78px;
				 		margin:20px 175px 10px 175px;background-position:26px 26px;"
				 		onclick="$('#impufile').trigger('click')">
				 	</p>
				 	<p class="fileicon" style="margin:20px 175px 10px 175px;">
				 		<img src="../../img/fileicon.png">
				 	</p>
				</p>
				<p style="color:#333;">
			 		<input id="impufile" type="file" name="impufile" style="display:none"
			 			accept= "application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" 
			 			onchange="onFileUpSelected()">
					<span id="impufileName" class="notfeil">未选择任何文件</span>
				</p>
				<div style="margin:0px 10px 0px 10px;">没有导入模版，请点击[导出]，导出加盟商扣款率表，在对导出的模版信息进行更新后，重新回到这里进行更新导入。</div>
			</form>
			<div id="impUpd_msg" ></div>
		</div>
		<div id="impUpd-buttons" style="display:none;">
			<a href="javascript:void(0)" id="confirmBtn" class="easyui-linkbutton c6"
				 onclick="onUpload()" style="width:90px">上传</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" 
				onclick="javascript:$('#impUpdDlg').dialog('close')" style="width:90px">取消</a>
		</div>
		<!-- 导入对话框  end-->
		
		<!-- 修改对话框 begin -->
		<div id="editDlg" class="easyui-dialog" style="width:400px;height:260px;padding-top:30px" 
			data-options="closed:true" >
			<form id="editform" method="post" style="padding: 0px 30px;">
					<input type="hidden" name="rateid" id="rateid">
  					<input type="hidden" name="updatets" id="updatets">
  					<input type="hidden" name="corpid" id="corpid">
  					<input type="hidden" name="fcorpid" id="fcorpid">
				<div class="time_col time_colp11">
					<label style="text-align:right;width:100px;"><i class="bisu">*</i>新增：</label>
					<input class="easyui-numberbox" id ="enrate" name ="nrate" style="width:150px;height:25px;"
						data-options ="required:true,min:0, max:100"/>(%)
				</div>
				<div class="time_col time_colp11">
					<label style="text-align:right;width:100px;"><i class="bisu">*</i>续费：</label>
					<input class="easyui-numberbox" id ="ernrate" name ="rnrate" style="width:150px;height:25px;" 
						data-options ="required:true,min:0, max:100"/>(%)
				</div>
				<div style="text-align:center;margin-top:40px;">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onSave()">确定</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						onclick="javascript:$('#editDlg').dialog('close')">取消</a>
				</div>
			</form>
		 </div>
		 <!-- 修改对话框 end -->
		 
 		<!-- 批量设置对话框 begin -->
		<div id="setDlg" class="easyui-dialog" style="width:400px;height:260px;padding-top:30px" 
			data-options="closed:true" >
			<form id="setform" method="post" style="padding: 0px 30px;">
				<div class="time_col time_colp11">
					<label style="text-align:right;width:100px;"><i class="bisu">*</i>新增：</label>
					<input class="easyui-numberbox" id ="snrate" name ="nrate" style="width:150px;height:25px;"
						data-options ="required:true,min:0, max:100"/>(%)
				</div>
				<div class="time_col time_colp11">
					<label style="text-align:right;width:100px;"><i class="bisu">*</i>续费：</label>
					<input class="easyui-numberbox" id ="srnrate" name ="rnrate" style="width:150px;height:25px;" 
						data-options ="required:true,min:0, max:100"/>(%)
				</div>
				<div style="text-align:center;margin-top:40px;">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onBatchSave()">确定</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						onclick="javascript:$('#setDlg').dialog('close')">取消</a>
				</div>
			</form>
		</div>
		<!-- 批量设置对话框 end -->
		 
		<!-- 变更记录 begin -->
 		<div id="logDlg" class="easyui-dialog" style="width:460px;height:500px;" data-options="closed:true">
			<table id="loggrid" style="height:98%;"></table>
		</div>
		<!-- 变更记录 end -->
		
	</div>
</body>
</html>