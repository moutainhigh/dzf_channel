<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<title>合同确认</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,request.getContextPath()+"/js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/contract/contractconfrim.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<%
	String logincorp = (String) session.getAttribute(IGlobalConstants.login_corp);
	String login_user = (String) session.getAttribute(IGlobalConstants.login_user);
	UserVO userVo = UserCache.getInstance().get(login_user, logincorp);
%>
<body>
	<input id="unm" name="unm" type="hidden" value=<%= userVo.getUser_name() %>> 
	<input id="uid" name="uid" type="hidden" value=<%= userVo.getCuserid() %>> 
	<div id="List_panel" class="wrapper" data-options="closed:false">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<span class="cur"></span>
				</div>
				
				<div class="left mod-crumb">
					<input id="begdate" name="begdate" class="easyui-datebox" style="width:110px;height:28px;text-align:left">
					--
					<input id="enddate" name="enddate" class="easyui-datebox" style="width:110px;height:28px;text-align:left">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="query()">查询</a>
				</div>
				
				<div class="left mod-crumb">
					<div style="margin:6px 0px 0px 10px;float:right;font-size:14px;">
						<a href="javascript:void(0)"  style="font-size:14;color:blue;" onclick="qryData(0)">全部</a>
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; " onclick="qryData(1)">待确认</a>
						<a href="javascript:void(0)"  
							style="font-size:14;color:blue;margin-left:15px;" onclick="qryData(2)">代扣款</a>
						<a href="javascript:void(0)"  
							style="font-size:14;color:blue;margin-left:15px;margin-right:15px; " onclick="qryData(3)">已扣款</a>
					</div>
				</div>
				
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="filter">
						<input style="height:28px;width:220px" class="easyui-textbox" id="filter_value" prompt="请输入渠道商名称,按Enter键 "/> 
					</div>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="doConfrim()">合同确认</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="confrim(3)">取消确认</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="deduct()">扣款处理</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="cancelDeduct()">取消扣款</a>
				</div>
			</div>
		</div>

		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		<div id="confrim_Dialog" class="easyui-dialog" style="width:500px;height:300px;padding:10px;" data-options="closed:true">
			<form id="conform">
				<textarea id="confreason" name="confreason" class="easyui-textbox" data-options="multiline:true,validType:'length[0,200]'" 
					style="width:400px;height:120px;">
				</textarea>
			 </form>
			 <p style="margin-top:60px;">
				<a id="" href="#" class="ui-btn" onclick="confrim(1)" style="margin-right:10%;margin-left:46%;">确认成功</a> 
				<a id="" href="#" class="ui-btn" onclick="confrim(2)">确认失败</a>
			 </p>
		</div>
		
		<div id="deduct_Dialog" class="easyui-dialog" title="扣款" data-options="modal:true,closed:true" 
				style="width:930px;height:50%;overflow:auto;">
				<div class=right style="float:right !important;height:40px;padding-bottom:0px;padding-top:10px;">
					<a id="" href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="deductConfri()">确认</a> 
					<a id="" href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="deductCancel()">取消</a> 
				</div>
			<form id="deductfrom" method="post" enctype="multipart/form-data">
				<input id="id" name="id" type="hidden">
				<input id="corpid" name="corpid" type="hidden">
				<div class="time_col time_colp11">
					<label style="text-align:right">渠道商：</label> 
					<input id="corpnm" name="corpnm" class="easyui-textbox" 
						data-options="readonly:true" style="width:150px;height:28px;text-align:left"> 
					<label style="text-align:right">剩余金额：</label> 
					<input id="balmny" name="balmny" class="easyui-numberbox" 
						data-options="readonly:true,precision:2,groupSeparator:','" style="width:150px;height:28px;text-align:left">
					<label	style="text-align:right">合同金额：</label> 
					<input id="ntlmny" name="ntlmny" class="easyui-numberbox" 
						data-options="readonly:true,precision:2,groupSeparator:','" style="width:150px;height:28px;text-align:left"> 
				</div>
				<div class="time_col time_colp11">
					<label style="text-align:right">扣款比例：</label> 
					<input id="propor" name="propor" class="easyui-numberbox" 
						data-options="min:0,max:100," style="width:135px;height:28px;text-align:left">% 
					<label style="text-align:right">扣款金额：</label> 
					<input id="ndemny" name="ndemny" class="easyui-numberbox" 
						data-options="readonly:true,precision:2,groupSeparator:','" style="width:150px;height:28px;text-align:left">
				</div>
				<div class="time_col time_colp11">
					<label style="text-align:right">扣费日期：</label> 
					<input id="dedate" name="dedate" class="easyui-datebox" 
						data-options="readonly:true" style="width:150px;height:28px;text-align:left"> 
					<label style="text-align:right">经办人：</label> 
					<input id="vopernm" name="vopernm" class="easyui-textbox" 
						data-options="readonly:true" style="width:150px;height:28px;text-align:left">
					<input id="voper" name="voper" type="hidden">
				</div>
				<div class="time_col time_colp11">
					<label style="text-align:right">备注：</label> 
					<textarea id="conmemo" name="conmemo" class="easyui-textbox" 
						data-options="multiline:true,validType:'length[0,200]'" style="width:600px;height:80px;">
					</textarea>
				</div>
			</form>
		</div>
	</div>
	
</body>

</html>