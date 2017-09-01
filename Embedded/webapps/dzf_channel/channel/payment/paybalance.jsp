<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<title>付款单余额查询</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,request.getContextPath()+"/js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/payment/paybalance.js");%> charset="UTF-8" type="text/javascript"></script>
</head>

<body>
	<div id="List_panel" class="wrapper" data-options="closed:false">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<span class="cur"></span>
				</div>
				<div class="left mod-crumb">
					<div style="margin:6px 0px 0px 10px;float:right;font-size:14px;">
						付款类型：
						<a href="javascript:void(0)"  style="font-size:14;color:blue;" onclick="qryData(-1)">全部</a>
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; " onclick="qryData(2)">预付款</a>
						<a href="javascript:void(0)"  
							style="font-size:14;color:blue;margin-left:15px;margin-right:15px; " onclick="qryData(1)">加盟费</a>
					</div>
				</div>
				
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="filter">
						<label class="mr5">渠道商：</label>
						<input style="height:28px;width:250px" class="easyui-textbox" id="filter_value" prompt="请输入渠道商名称,按Enter键 "/> 
					</div>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="qryDetail()">查询明细</a>
				</div>
			</div>
		</div>

		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		<div id="detail_dialog" class="easyui-dialog" title="付款单余额明细" data-options="modal:true,closed:true" style="width:880px;height:500px;">
			<div class="time_col" style="padding-top: 10px;;width:90%;margin:0 auto;">
				<label style="text-align:right">渠道商：</label> 
				<input id="corpnm" name="corpnm" class="easyui-textbox" 
					data-options="readonly:true" style="width:150px;height:28px;text-align:left">
				<label style="text-align:right">付款类型：</label> 
				<input id="ptypenm" name="ptypenm" class="easyui-textbox" 
					data-options="readonly:true" style="width:150px;height:28px;text-align:left">
			</div>		
			<div data-options="region:'center'" style="overflow-x:auto; overflow-y:auto;margin: 0 auto;width:90%;height:380px;padding:10px">
				 <table id="gridh"></table>	
			</div>
		</div>
	</div>
	
</body>

</html>