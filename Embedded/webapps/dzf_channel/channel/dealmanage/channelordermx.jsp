<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>订单明细表</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../../css/channelorder.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/dealmanage/channelordermx.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<body>
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
				
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="doExport()">导出</a>
				</div>
			</div>
		</div>
		
		<!-- 查询对话框 begin -->
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:285px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
			    <input id="tjdate" type="radio" name="seledate" checked />
				<label style="text-align:right; width: 85px;">提交日期：</label> 
				<input id="submitbegdate" name="submitbegdate" type="text" class="easyui-datebox" 
						 data-options="width:137,height:27" />  - 
				<input id="submitenddate" name="submitenddate" type="text" class="easyui-datebox" 
						data-options="width:137,height:27" />
			</div>
			<div class="time_col time_colp10">
			    <input id="kkdate" type="radio" name="seledate" />
				<label style="text-align:right; width: 85px;">扣款日期：</label> 
				<input id="kbegdate" name="kbegdate" type="text" class="easyui-datebox" 
						 data-options="width:137,height:27" />  - 
				<input id="kenddate" name="kenddate" type="text" class="easyui-datebox" 
						data-options="width:137,height:27" />
			</div>
			
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">订单状态：</label>
				<select id="qstatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
					editable="false" style="width:290px;height:28px;">
					<!-- 状态  0：待确认；1：待发货；2：已发货；3：已收货；4：已取消； -->
					<option value="-1">全部</option>
					<option value="0">待确认</option>
					<option value="1">待发货</option>
					<option value="2">已发货</option>
					<option value="3">已收货</option>
					<option value="4">已取消</option>
				</select>
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">加盟商：</label>
				<input id="qcpname" class="easyui-textbox" style="width:290px;height:28px;"/>
				<input id="qcpid" type="hidden">
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">订单编码：</label>
				<input id="qbcode" class="easyui-textbox" style="width:290px;height:28px;"/>
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">商品：</label>
				<input id="goodsname" name="goodsname" class="easyui-combobox" style="width:286px;height:28px;text-align:left"
					data-options="required:false,valueField:'id',textField:'name',multiple:true,panelHeight:200" /> 
			</div>
			<p>
				<a class="ui-btn save_input" onclick="clearParams()">清除</a>
				<a class="ui-btn save_input" onclick="reloadData()">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		<!-- 查询对话框end -->
		
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		<!-- 加盟商参照对话框begin -->
		<div id="chnDlg"></div>
		<!-- 加盟商参照对话框end -->
	
	</div>
</body>
</html>