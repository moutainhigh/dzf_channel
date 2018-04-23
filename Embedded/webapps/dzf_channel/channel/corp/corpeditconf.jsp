<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<title>付款单确认</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,request.getContextPath()+"/js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/corp/corpeditconf.js");%> charset="UTF-8" type="text/javascript"></script>
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
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; " onclick="qryData(1)">待审核</a>
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px;margin-right:15px; " onclick="qryData(2)">已审核</a>
					</div>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="audit()">审核</a>
				</div>
			</div>
		</div>
		<div class="qijian_box" id="qrydialog" style="display: none; width: 450px; height: 160px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<form id="query_form">
				<h3>
					<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="width: 80px;text-align:right">提交日期：</label>
					<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"  /></font>
					<font>-</font>
					<font><input name="edate" type="text" id="edate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"  /></font>
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">加盟商：</label>
					<input id="channel_select" class="easyui-textbox" style="width:290px;height:28px;" />
					<input id="pk_account" type="hidden">
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
					 <font size="3"><label style="text-align:left;width:17%">审核意见：</label></font> 
				 	 <input type="radio" style="margin-top: 6px;width:6%" name="seletype" id="confirm">
				 	<label style="text-align:left;width:12%">确认</label>
					 <input type="radio" style="margin-top: 6px;width:6%" name="seletype" id="reject">
					 <label style="text-align:left;width:12%">驳回</label>
				 </div>
				 <div class="time_col time_colp10" style="margin-top:20px;">
				   	<label style="text-align:left;width:18%; vertical-align: top;">驳回说明：</label>
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