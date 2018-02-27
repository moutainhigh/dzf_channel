<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.DzfUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<title>返点单确认</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>
	rel="stylesheet">
<script
	src=<%UpdateGradeVersion.outversion(out, request.getContextPath() + "/js/easyuiext.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/rebate/rebateconf.js");%>
	charset="UTF-8" type="text/javascript"></script>
</head>

<body>
	<!-- 列表界面begin -->
	<div id="List_panel" class="wrapper" data-options="closed:false">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<div id="cxjs" class="h30 h30-arrow">
						<label class="mr5">查询：</label>
						<strong id="jqj"></strong>
						<span class="arrow-date"></span>
					</div>
				</div>
				<div class="left mod-crumb">
					<span class="cur"></span>
				</div>
				<div class="left mod-crumb">
					<div style="margin:6px 0px 0px 10px;float:right;font-size:14px;">
						<a href="javascript:void(0)" style="font-size:14;color:blue;margin-left:15px;margin-right:15px;" 
							onclick="qryData(1)">待确认</a>
					</div>
				</div>
				
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="filter">
						<label class="mr5">加盟商：</label>
						<input id="filter_value" style="height:28px;width:250px" class="easyui-textbox"  
						prompt="请输入加盟商名称,按Enter键 "/> 
					</div>
				</div>
				<div class="right">
					<!-- <a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onBatchConf">批量确认</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onAdd()">取消确认</a> -->
				</div>
			</div>
		</div>
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		
	</div>
	<!-- 列表界面end -->
	
	<!-- 查询对话框 begin -->
	<div id="qrydialog" class="qijian_box" style="display:none; width:450px; height:230px">
		<s class="s" style="left: 25px;"><i class="i"></i> </s>
		<form id="query_form">
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<label style="width: 90px;text-align:right">返点期间：</label>
				<font>
					<select id="qyear" name="qyear" class="easyui-combobox" 
						data-options="editable:false"  style="width:130px;height:28px;">
						<% DzfUtil.WriteYearOption(out);%>
			 		</select>
			 	</font>
				<font>-</font>
				<font>
					 <select id="qjd" name="qjd" class="easyui-combobox" data-options="editable:false,panelHeight:'auto'" 
					 	style="width:130px;height:28px;text-align:left">
						<option value="1">第一季度</option>
						<option value="2">第二季度</option>
						<option value="3">第三季度</option>
						<option value="4">第四季度</option>	
					</select>
				</font>
			</div>
			<div class="time_col time_colp10">
				<label style="width:90px;text-align:right">返点单状态：</label>
				<!-- 状态   0：待提交；1：待确认；2：待审批；3：审批通过；4：待确认驳回；5：待审批驳回； -->
				<select id="qstatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
					style="width:100px;height:28px;">
					<option value="1">待确认</option>
					<option value="2">待审批</option>
					<option value="3">审批通过</option>
				</select>
			</div>
			<div class="time_col time_colp10">
				<label style="width:90px;text-align:right">渠道经理：</label>
				<input id="manager" class="easyui-textbox" style="width:290px;height:28px;" />
				<input id="managerid" type="hidden">
			</div>
			<div class="time_col time_colp10">
				<label style="width:90px;text-align:right">加盟商：</label>
				<input id="qcorp" class="easyui-textbox" style="width:290px;height:28px;" />
				<input id="qcorpid" type="hidden">
			</div>
		</form>
		<p>
			<a class="ui-btn save_input" onclick="clearParams()">清空</a>
			<a class="ui-btn save_input" onclick="reloadData()">确认</a>
			<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
		</p>
	</div>
	<!-- 查询对话框 end -->
	
	<!-- 渠道经理参照对话框及按钮 begin -->
	<div id="manDlg"></div>
	<div id="manBtn" style="display:none;">
		<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectMans()" style="width:90px">确认</a> 
		<a href="javascript:void(0)" class="easyui-linkbutton" 
			onclick="javascript:$('#manDlg').dialog('close');" style="width:90px">取消</a>
	</div>
	<!-- 渠道经理参照对话框及按钮 end -->
	
	<!-- 加盟商参照对话框及按钮 begin -->
	<div id="chnDlg"></div>
	<div id="chnBtn" style="display:none;">
		<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
		<a href="javascript:void(0)" class="easyui-linkbutton" 
			onclick="javascript:$('#chnDlg').dialog('close');" style="width:90px">取消</a>
	</div>
	<!-- 加盟商参照对话框及按钮 end -->
	
	
</body>

</html>