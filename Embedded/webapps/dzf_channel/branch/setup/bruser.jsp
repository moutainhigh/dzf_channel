<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="../../inc/easyui.jsp"></jsp:include>
	<link href=<%UpdateGradeVersion.outversion(out,"../../css/index.css");%>  rel="stylesheet">
	<script type="text/javascript" src=<%UpdateGradeVersion.outversion(out,"../../js/branch/setup/bruser.js");%>  charset="UTF-8"></script>
</head>
<body class="easyui-layout">
	<!-- <div class="wrapper"> -->
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				 <div id="condtpn" class="left mod-crumb">
	          <div class="h30 h30-arrow"> 
				<label class="mr5">用户名称：</label>
					<input id="quname" class="easyui-textbox" prompt="输入用户名称或编码，按Enter键过滤" style="width:280px;height:27px"/>
			  	<label style="width:70px;">锁定用户：</label>
					<input id="qrylock" class="easyui-combobox" 
							data-options="valueField: 'value',
										textField: 'label',
										panelHeight:'60',
										data: [
										{label: '是',value: 'Y'},
										{label: '否',value: 'N',selected:true}]" 
						style="width:80px;height:27px" />
			  </div>
	        </div>
				<div class="right">
					<a class="ui-btn ui-btn-xz" onclick="add();" plain="true" href="javascript:void(0);">新增</a> 
				</div>
			</div>
		</div>
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="grid"></table>
			</div>
		</div>
		
		
		<div id="addDialog" class="easyui-dialog" style="width:800px;height:400px;padding-top:30px;" data-options="closed:true" modal=true>
			<form id="addForm" method="post">
				<div class="time_col time_colp11">
					<div style="display: inline-block;">
						<label style="width:140px;text-align: right;"><i class="bisu">*</i>登录账号:</label>
						<input id="ucode" name="ucode" class="easyui-textbox"  style="width:200px;height:28px;" data-options="required:true"/>
						<input id="uid" name="uid" type="hidden">
						<input id="updatets" name="updatets" type="hidden">
						
						<label style="width:140px;text-align: right;"><i class="bisu">*</i>用户名称:</label>
						<input id="uname" name="uname" class="easyui-textbox"  style="width:200px;height:28px;" data-options="required:true"/>
					</div>
				</div>
				<div class="time_col time_colp11">
					<label style="text-align:right;width:140px;"><i class="bisu">*</i>密码:</label>
					<input id="u_pwd" name="u_pwd" class="easyui-textbox" type="password"  style="width:200px;height:28px;" 
						data-options="required:true,validType:['minLength[8]','pwdrule']"/>
						
					<label style="text-align:right;width:140px;"><i class="bisu">*</i>确认密码:</label>
					<input id="uc_pwd" name="uc_pwd" class="easyui-textbox" type="password" style="width:200px;height:28px;" 
							data-options="required:true,validType:['equals[\'#u_pwd\']','minLength[6]']">
				</div>
				<div class="time_col time_colp11">
					<label style="text-align:right;width:140px;">生效时间:</label>
					<input id="en_time" name="en_time" class="easyui-datebox" style="width:200px;height:28px;" data-options="required:true"/>
					
					<label style="text-align:right;width:140px;">失效时间:</label>
					<input id="dis_time" name="dis_time" class="easyui-datebox" style="width:200px;height:28px;" >
				</div>
				<div class="time_col time_colp11">
					<label style="text-align:right;width:140px;">用户描述:</label>
					<input id="u_note" name=u_note class="easyui-textbox" style="width:560px;height:40px;">
				</div>
				<div class="time_col time_colp11" id="branch"></div>
				<div class="time_col time_colp11" id = "roles"></div>
			</form>
			<div style="text-align:center;margin-top:30px;">
			    <a href="javascript:void(0)" id="saveNew" class="ui-btn ui-btn-xz"  onclick="onSave(true)">保存</a> 
			    <a href="javascript:void(0)" id="saveEdit" class="ui-btn ui-btn-xz"  onclick="onSave(false)">保存</a> 
				<a href="javascript:void(0)"   class="ui-btn ui-btn-xz" onclick="cancel()">取消</a>
			</div>
		</div>
		
</body>
</html>