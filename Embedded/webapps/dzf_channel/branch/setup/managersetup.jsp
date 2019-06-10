<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>
	rel="stylesheet">
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script type="text/javascript"
	src=<%UpdateGradeVersion.outversion(out, "../../js/branch/setup/managersetup.js");%>
	charset="UTF-8"></script>
</head>
<body class="easyui-layout">
	<div class="mod-toolbar-top">
		<div class="mod-toolbar-content">
			 <div id="condtpn" class="left mod-crumb">
          <div class="h30 h30-arrow"> 
			<label class="mr5">用户名称：</label>
				<input id="quname" class="easyui-textbox" style="width:236px;height:27px" prompt="输入用户名称或账号,按Enter键过滤 "/>
		  	<label style="width:70px;">锁定用户：</label>
				<input id="qrylock" class="easyui-combobox" style="width:80px;height:27px"
						data-options="valueField: 'value', textField: 'label', panelHeight:'60',
									data: [{label: '是',value: 'Y'},	{label: '否',value: 'N',selected:true}]" />
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
		
	<div id="cbDialog" class="easyui-dialog" style="width:600px;height:350px;padding:10px 20px;" 
		data-options="closed:true,buttons:'#dlg-buttons'">
		<form id="bill" method="post">
			<div id="tableDiv" style="height:400px;overflow-y: auto">
				<table class="tableForm">
				<tr>
					<td><label style="width:70px;">登录账号：</label></td>
					<td>
						<input id="ucode" name="ucode" class="easyui-textbox" style="width:160px;height:27px;" 
							data-options="required:true,validType:['minLimit[6]']"/>
					</td>
					<td><div id="ckcode" style="display:none;"></div></td>
					
					<td><label style="width:70px;">用户名称：</label></td>
					<td >
						<input id="uname" name="uname" class="easyui-textbox" style="width:160px;height:27px;" data-options="required:true">
					</td>
					<td><div id="ckname" style="display:none;"></div></td>
				</tr>
				<tr>
					<td><label style="width:70px;">密码：</label></td>
					<td ><input id="u_pwd" name="u_pwd" type="password" class="easyui-textbox" style="width:160px;height:27px;"
						data-options="required:true,validType:['minLength[8]','pwdrule']"/> 
					</td>
					<td><div id="u_pwd_ck" style="display:none;"></div></td>
					
					<td><label style="width:70px;">确认密码：</label></td>
					<td>
						<input id="uc_pwd" name="uc_pwd" type="password" class="easyui-textbox" style="width:160px;height:27px;" 
							data-options="required:true,validType:['equals[\'#u_pwd\']','minLength[8]']">
					</td>
					<td><div id="uc_pwd_ck" style="display:none;"></div></td>
				</tr>
				
				<tr>
					<td><label style="width:70px;">生效时间：</label></td>
					<td colspan = 2>
						<input id="en_time" name="en_time" class="easyui-datebox" style="width:160px;height:27px;" 
							data-options="required:true"/> 
					</td>
					<td><label style="width:70px;">失效时间：</label></td>
					<td colspan = 2>
						<input id="dis_time" name="dis_time" class="easyui-datebox" style="width:160px;height:27px;">
					</td>
				</tr>
				<tr>
					<td><label style="width:70px;">用户描述：</label></td>
					<td colspan = 5><input id="u_note" name=u_note class="easyui-textbox" style="width:420px;height:27px;"></td>
				</tr>
				<tr>
					<td><label style="width:70px;">机构：</label></td>
					<td colspan = 5>
						<div id = "branch">
						</div>
					</td>
				</tr>
				</table>
			</div>
			<input id="uid" name="uid" type="hidden">
			<input id="corp_id" name="corp_id" type="hidden">
			<input id="crtcorp_id" name="crtcorp_id" type="hidden">
			<input id="b_mng" name="b_mng" type="hidden">
		</form>
	</div>
	<div id="dlg-buttons">
		<a href="javascript:void(0)" class="easyui-linkbutton c6" onclick="onSave()">保存</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="cancel()">取消</a>
	</div>
</body>
</html>