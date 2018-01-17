<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<title>付款单确认</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,request.getContextPath()+"/js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/payment/payconfirm.js");%> charset="UTF-8" type="text/javascript"></script>
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
				<div class="left mod-crumb">
					<div style="margin:6px 0px 0px 10px;float:right;font-size:14px;">
						单据状态：
						<a href="javascript:void(0)"  style="font-size:14;color:blue;" onclick="qryData(-1)">全部</a>
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; " onclick="qryData(2)">待确认</a>
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px;margin-right:15px; " onclick="qryData(3)">已确认</a>
					</div>
				</div>
				
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="filter">
						<label class="mr5">加盟商：</label>
						<input style="height:28px;width:250px" class="easyui-textbox" id="filter_value" prompt="请输入加盟商名称,按Enter键 "/> 
					</div>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="certain()" >收款确认</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="operate(2)" >取消确认</a>
				</div>
			</div>
		</div>
		<div class="qijian_box" id="qrydialog" style="display: none; width: 450px; height: 230px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<form id="query_form">
				<h3>
					<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="width: 80px;text-align:right">付款日期：</label>
					<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"  /></font>
					<font>-</font>
					<font><input name="edate" type="text" id="edate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"  /></font>
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">单据状态：</label>
					<select id="status" class="easyui-combobox" data-options="panelHeight:'auto'" style="width:100px;height:28px;">
						<option value="-1">全部</option>
						<option value="2">待确认</option>
						<option value="3">已确认</option>
						<option value="4">已驳回</option>
					</select>
					<label style="width:80px;text-align:right">付款类型：</label>
					<select id="iptype" class="easyui-combobox" data-options="panelHeight:'auto'" style="width:100px;height:28px;">
						<option value="-1">全部</option>
						<option value="1">保证金</option>
						<option value="2">预付款</option>
					</select>
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">加盟商：</label>
					<input id="channel_select" class="easyui-textbox" style="width:290px;height:28px;" />
					<input id="pk_account" type="hidden">
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">支付方式：</label>
					<select id="ipmode" class="easyui-combobox" data-options="panelHeight:'auto'" style="width:150px;height:28px;">
						<option value="-1">全部</option>
						<option value="1">银行转账</option>
						<option value="2">支付宝</option>
						<option value="3">微信</option>
						<option value="4">其他</option>
					</select>
				</div>
			</form>
			<p>
				<a class="ui-btn save_input" onclick="clearParams()">清除</a>
				<a class="ui-btn save_input" onclick="reloadData()">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		<div id="hlDialog" class="easyui-dialog" style="width:500px;height:300px;padding:20px 20px 10px;" data-options="closed:true">
			<form id="commit">
				<div class="time_col time_colp10">
					 <font size="3"><label style="text-align:left;width:40%">你确定要收款确认吗?</label></font> 
				 	 <input type="radio" style="margin-top: 6px;width:8%" name="seletype" id="confirm" value>
				 	<label style="text-align:left;width:12%">确认</label>
					 <input type="radio" style="margin-top: 6px;width:6%" name="seletype" id="reject">
					 <label style="text-align:left;width:12%">驳回</label>
				 </div>
				 <div class="time_col time_colp10" style="margin-top:20px;">
				   	<label style="text-align:left;width:18%;    vertical-align: top;">驳回说明：</label>
					<textarea id="vreason" name="vreason" class="easyui-textbox" data-options="multiline:true,validType:'length[0,50]'" 
						style="width:74%;height:50px;"></textarea>
				  </div>
			</form>
			 <p style="margin-top:90px; text-align: right;">
				<a id="ok" href="#" class="ui-btn" onclick="confirm()">确定</a> 
				<a id="cancel" href="#" class="ui-btn" onclick="canConfirm()">取消</a>
			 </p>
		</div>
		<!-- 查看附件begin -->
		<div id="tpfd"></div>
		<!-- 查看附件end -->
		<div id="chnDlg"></div>
		<div id="chnBtn" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#chnDlg').dialog('close');" style="width:90px">取消</a>
		</div>
	</div>
</body>

</html>