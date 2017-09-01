<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<title>合同确认</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,request.getContextPath()+"/js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/contract/contractconfrim.js");%> charset="UTF-8" type="text/javascript"></script>
</head>

<body>
	<div id="List_panel" class="wrapper" data-options="closed:false">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<span class="cur"></span>
				</div>
				
				<div class="left mod-crumb">
					<label style="text-align:right">查询期间：</label> 
					<input id="corpnm" name="corpnm" class="easyui-datebox" style="width:150px;height:28px;text-align:left">
					---- 
					<input id="ptypenm" name="ptypenm" class="easyui-datebox" style="width:150px;height:28px;text-align:left">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="query()">查询</a>
				</div>
				
				<div class="left mod-crumb">
					<div style="margin:6px 0px 0px 10px;float:right;font-size:14px;">
						<a href="javascript:void(0)"  style="font-size:14;color:blue;" onclick="qryData(-1)">全部</a>
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; " onclick="qryData(1)">待确认</a>
						<a href="javascript:void(0)"  
							style="font-size:14;color:blue;margin-left:15px;" onclick="qryData(2)">代扣款</a>
						<a href="javascript:void(0)"  
							style="font-size:14;color:blue;margin-left:15px;margin-right:15px; " onclick="qryData(3)">已扣款</a>
					</div>
				</div>
				
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="filter">
						<label class="mr5">渠道商：</label>
						<input style="height:28px;width:250px" class="easyui-textbox" id="filter_value" prompt="请输入渠道商名称,按Enter键 "/> 
					</div>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="doConfrim()">合同确认</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="doCancel()">取消确认</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="deduct()">扣款处理</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="dedancel()">取消扣款</a>
				</div>
			</div>
		</div>

		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
	</div>
	
</body>

</html>