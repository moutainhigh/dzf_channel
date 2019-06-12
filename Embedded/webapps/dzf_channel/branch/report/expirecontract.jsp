<%@page import="com.dzf.pub.DzfUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.lang.DZFDate"%>
<%
	String month = new DZFDate().getStrMonth();
%>
<!DOCTYPE html>
<html>
<head>
<title>到期合同统计</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/branch/report/expirecontract.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<style>
 
</style>
</head>
<body> 
   <div id="List_panel" class="wrapper" data-options="closed:false">
   <input type="hidden" value="<%=month%>" id="month" >
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<span class="cur"></span>
				</div>
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="cxjs">
						<label class="mr5">查询：</label>
						<strong id="jqj"></strong>
						<span class="arrow-date"></span>
					</div>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="doExport()">导出</a>
				</div>
			</div>
		</div>
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:258px;">
			<s class="s"><i class="i"></i></s>
			<h3>
				<span>查询</span> <a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="sel_time">
				<div class="time_col">
					<label style="text-align:right;width:102px;">到期月份：</label> 
					<select id="startYear" class="easyui-combobox" data-options="editable:false" name="startYear" 
					style="width:70px;height:27px;">
					<% DzfUtil.WriteYearOption(out);%>
					</select>
					<span id="monthField">
					<select id="startMonth" class="easyui-combobox" name="startMonth" data-options="editable:false" 
					style="width:52px;height:27px;">
					<% DzfUtil.WriteMonthOption(out);%>
					</select>
				</span>
				</div>
		    </div>
		    <div class="time_col time_colp10">
				<label style="text-align:right;width:85px;">机构名称：</label> 
				<input id="pk_bset" class="easyui-combobox" style="width:286px;height:28px;"
				    editable="false" data-options="valueField:'pk_bset', textField:'name', panelHeight:'200'" />
			</div>
			<div class="time_col time_colp10">
				<label style="text-align:right;width: 85px;">公司编码：</label> 
				<input id="ucode"  name="ucode" class="easyui-textbox" style="width: 286px; height: 28px;"/>  
			</div>
			<div class="time_col time_colp10">
				<label style="text-align:right;width:85px;">公司名称：</label> 
				<input id="uname" name="uname" class="easyui-textbox" style="width:286px;height:28px;"/>  
			</div>
			<p>
				<a class="ui-btn save_input" id="cleanbtn" onclick="clearQuery();">清除</a> 
				<a class="ui-btn save_input" onclick="reloadData()">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
	</div>
</body>
</html>
