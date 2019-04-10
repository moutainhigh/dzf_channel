<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>合同审核</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>
	rel="stylesheet">
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/contract/contractaudit.js");%>
	charset="UTF-8" type="text/javascript"></script>
<style type="text/css">
</style>
</head>
<% 
	String cuserid = (String) session.getAttribute(IGlobalConstants.login_user);
%>
<body>
	<input id="uid" name="uid" type="hidden" value=<%= cuserid %>> 
	<div id="List_panel" class="wrapper" data-options="closed:false">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<div id="cxjs" class="h30 h30-arrow">
						<label class="mr5">查询：</label>
						<strong id="querydate"></strong>
						<span class="arrow-date"></span>
					</div>
				</div>
				<div class="left mod-crumb">
					<span class="cur"></span>
				</div>
				<div class="left mod-crumb">
					<div style="margin:4px 0px 0px 10px;float:left;font-size:14px;">
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; margin-right:15px;" 
							onclick="qryData()">待审核</a>
					</div>
				</div>
				
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="audit()">合同审核</a>
				</div>
			</div>
		</div>
		
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
	</div>
	
	<!-- 查询对话框 begin -->
	<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:290px">
		<s class="s" style="left: 25px;"><i class="i"></i> </s>
		<form id="query_form">
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">申请日期：</label>
				<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" 
					data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
				<font>-</font>
				<font><input name="edate" type="text" id="edate" class="easyui-datebox" 
					data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">申请类型：</label>
				<input id="qnullify" type="checkbox" checked 
					style="width:20px;height:28px;text-align:left;margin-left:2px;"/>
				<label style="width:40px;text-align:left">作废</label> 
				<input id="qstop" type="checkbox" checked 
					style="width:20px;height:28px;text-align:left;margin-left:10px;"/>
				<label style="width:40px;text-align:left">终止</label> 
				<input id="unroutine" type="checkbox" checked 
					style="width:20px;height:28px;text-align:left;margin-left:20px;"/>
				<label style="width:80px;text-align:left">非常规套餐</label>
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">申请状态：</label>
				<select id="qapstatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
					style="width:105px;height:28px;">
					<!-- 申请状态  1：渠道待审（未处理）；2： 区总待审（处理中）；3：总经理待审（处理中）；4：运营待审（处理中）；5：已处理；6：已拒绝； -->
					<option value="-1">全部</option>
					<option value="1">渠道待审</option>
					<option value="2">区总待审</option>
					<option value="3">总经理待审</option>
					<option value="4">运营待审</option>
					<option value="5">已处理</option>
					<option value="6">已拒绝</option>
				</select>
				<label style="width:88px;text-align:right">纳税人资格：</label>
				<select id="qchname" class="easyui-combobox" data-options="panelHeight:'auto'" 
					style="width:80px;height:28px;">
					<option value="-1">全部</option>
					<option value="1">小规模</option>
					<option value="2">一般人</option>
				</select>
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">加盟商：</label>
				<input id="channel_select" class="easyui-textbox" style="width:284px;height:28px;"/>
				<input id="pk_account" type="hidden">
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">客户：</label>
				<input id="corpkna_ae" class="easyui-textbox" style="width:284px;height:28px;"/>
				<input id="corpkid_ae" name="corpkid" type="hidden"> 
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">渠道经理：</label>
				<input id="manager" class="easyui-textbox" style="width:284px;height:28px;" />
				<input id="managerid" type="hidden">
			</div>
		</form>
		<p>
			<a class="ui-btn save_input" onclick="clearParams()">清除</a>
			<a class="ui-btn save_input" onclick="reloadData()">确定</a>
			<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
		</p>
	</div>
	<!-- 查询对话框end -->
	
	<!-- 查询-加盟商参照 begin -->
	<div id="chnDlg"></div>
	<div id="chnBtn" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#chnDlg').dialog('close');" style="width:90px">取消</a>
		</div>
	<!-- 查询-加盟商参照 end -->
	
	<!-- 查询-客户参照begin -->
	<div id="gs_dialog"></div>
	<!-- 查询-客户参照end -->
	
	<!-- 渠道经理参照对话框及按钮 begin -->
	<div id="manDlg"></div>
	<div id="manBtn" style="display:none;">
		<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectMans()" style="width:90px">确认</a> 
		<a href="javascript:void(0)" class="easyui-linkbutton" 
			onclick="javascript:$('#manDlg').dialog('close');" style="width:90px">取消</a>
	</div>
	<!-- 渠道经理参照对话框及按钮 end -->
	
</body>
</html>