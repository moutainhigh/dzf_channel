<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.DzfUtil"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<%
	String date = AdminDateUtil.getServerDate();
	String btdate = AdminDateUtil.getPreviousNMonth(1);
%>
<title>退款单</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>
	rel="stylesheet">
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/rebate/public.css");%>
	rel="stylesheet">
<script
	src=<%UpdateGradeVersion.outversion(out, request.getContextPath() + "/js/easyuiext.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/refund/refunddetail.js");%>
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
				
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="onExport()">导出</a>
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
				<label style="width: 70px;text-align:right">日期：</label>
				<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" value=<%= btdate %>
					data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
				<font>-</font>
				<font><input name="edate" type="text" id="edate" class="easyui-datebox" value=<%= date %>
					data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">加盟商：</label>
				<input id="qcorp" class="easyui-textbox" style="width:281px;height:28px;" />
				<input id="qcorpid" type="hidden">
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right;">大区：</label> 
				<input id="aname"  name="aname" class="easyui-combobox" style="width:281px; height: 28px;" 
					data-options="required:false,valueField:'name',textField:'name',panelHeight:100" editable="false" />  
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">渠道经理：</label>
				<input id="manager" class="easyui-textbox" style="width:281px;height:28px;" />
				<input id="managerid" type="hidden">
			</div>
		</form>
		<p>
			<a class="ui-btn save_input" onclick="clearParams()">清空</a>
			<a class="ui-btn save_input" onclick="reloadData()">确认</a>
			<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
		</p>
	</div>
	<!-- 查询对话框 end -->
	
	<!-- 加盟商参照对话框及按钮 begin -->
	<div id="chnDlg"></div>
	<div id="chnBtn" style="display:none;">
		<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
		<a href="javascript:void(0)" class="easyui-linkbutton" 
			onclick="javascript:$('#chnDlg').dialog('close');" style="width:90px">取消</a>
	</div>
	<!-- 加盟商参照对话框及按钮 end -->
	
	<!-- 渠道经理参照对话框及按钮 begin -->
	<div id="manDlg"></div>
	<div id="manBtn" style="display:none;">
		<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectMans()" style="width:90px">确认</a> 
		<a href="javascript:void(0)" class="easyui-linkbutton" 
			onclick="javascript:$('#manDlg').dialog('close');" style="width:90px">取消</a>
	</div>
	<!-- 渠道经理参照对话框及按钮 end -->
	
	<!-- 付款单余额明细begin -->
	<div id="detail_dialog" class="easyui-dialog" title="退款明细" 
		data-options="modal:true,closed:true" style="width:828px;height:500px;">
		<div class="time_col" style="padding-top: 10px;width:96%;margin:0 auto;">
			<label style="text-align:right">查询：</label> 
			<span id ="qrydate" style="vertical-align: middle;font-size:14px;"></span>
			<label style="text-align:right">加盟商：</label> 
			<span id ="corpnm" style="vertical-align: middle;font-size:14px;"></span>
		</div>	
		
		<div data-options="region:'center'" 
			style="overflow-x:auto; overflow-y:auto;margin: 0 auto;width:90%;height:380px;padding:10px">
			 <table id="gridh"></table>	
		</div>
	</div>
	<!-- 付款单余额明细end -->
	
</body>

</html>