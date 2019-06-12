<%@page import="com.dzf.pub.DzfUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.lang.DZFDate"%>
<!DOCTYPE html>
<html>
<head>
<title>客户数据统计</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/branch/report/salecorpdata.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<style>
 
</style>
</head>
<body> 
	<div id="List_panel" class="wrapper" data-options="closed:false">
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
				<!-- <div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="doExport()">导出</a>
				</div> -->
			</div>
		</div>
		
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		<!-- 查询对话框 begin -->
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:290px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
				<h3>
					<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="width:90px;text-align:right">录入日期：</label>
					<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
					<font>-</font>
					<font><input name="edate" type="text" id="edate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width:90px;">公司名称：</label> 
					<input id="cpname"  name="cpname" class="easyui-combobox" style="width:284px; height: 28px;" 
						data-options="valueField:'id',textField:'name',panelHeight:100" editable="false" />  
				</div>
				<div class="time_col time_colp10">
					<label style="width:90px;text-align:right">客户编码：</label>
					<input id="cpkcode" class="easyui-textbox" style="width:284px;height:28px;"/>
				</div>
				<div class="time_col time_colp10">
					<label style="width:90px;text-align:right">客户名称：</label>
					<input id="cpkname" class="easyui-textbox" style="width:284px;height:28px;"/>
				</div>
				<div class="time_col time_colp10">
					<label style="width:90px;text-align:right">记账状态：</label>
					<select id="jzzt" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:102px;height:28px;">
						<option value="全部">全部</option>
						<option value="未开始">未开始</option>
						<option value="进行中">进行中</option>
						<option value="已完成">已完成</option>
					</select>
					
					<label style="width:70px;text-align:right">报税状态：</label>
					<select id="bszt" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:102px;height:28px;">
						<option value="全部">全部</option>
						<option value="未完成">未完成</option>
						<option value="已完成">已完成</option>
					</select>
				</div>
				<div class="time_col time_colp10">
					<label style="width:90px;text-align:right">服务余额/月：</label>
					<input id="qmonth" class="easyui-numberbox" style="width:102px;height:28px;" />
				</div>
			<p>
				<a class="ui-btn save_input" id="cleanbtn" onclick="clearQuery()">清除</a> 
				<a class="ui-btn save_input" onclick="reloadData()">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		<!-- 查询对话框end -->
		
		<div id="kjdlg"></div>
		
	</div>
</body>
</html>
