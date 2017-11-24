<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="../../inc/easyui.jsp"></jsp:include>
	<link href=<%UpdateGradeVersion.outversion(out,"../../css/index.css");%>  rel="stylesheet">
	<script type="text/javascript" src=<%UpdateGradeVersion.outversion(out,"../../js/sys_power/usermng.js");%>  charset="UTF-8"></script>
</head>
<body class="easyui-layout">
	<!-- <div class="wrapper"> -->
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				 <div id="condtpn" class="left mod-crumb">
	          <div class="h30 h30-arrow"> 
				<label class="mr5">用户名称：</label>
					<input id="quname" class="easyui-textbox" style="width:120px"/>
					
			  	<label style="width:70px;">锁定用户：</label>
					<input id="qrylock" class="easyui-combobox" 
							data-options="valueField: 'value',
										textField: 'label',
										panelHeight:'60',
										data: [
										{label: ' ',value: '',selected:true},
										{label: '是',value: 'Y'},
										{label: '否',value: 'N'}]" 
						style="width:80px;height:27px" />
			  </div>
	        </div>
				<div class="right">
					<a class="ui-btn ui-btn-xz" onclick="add();" plain="true" href="javascript:void(0);">增加</a> 
					<a class="ui-btn ui-btn-xz" onclick="edit();" plain="true" href="javascript:void(0);">修改</a>
					<!-- <a class="ui-btn ui-btn-xz" onclick="del();" plain="true" href="javascript:void(0);">删除</a> -->
					<a class="ui-btn ui-btn-xz" onclick="load();" plain="true" href="javascript:void(0);">刷新</a>
					<a class="ui-btn ui-btn-xz" onclick="lock();" plain="true" href="javascript:void(0);">锁定</a>
					<a class="ui-btn ui-btn-xz" onclick="unlock();" plain="true" href="javascript:void(0);">解锁</a>
				</div>
			</div>
		</div>
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="grid"></table><!--  data-options="fit:true" -->
			</div>
		</div>
	<!-- </div> -->
		<div id="cbDialog" class="easyui-dialog" style="width:620px;height:350px;padding:10px 20px;" data-options="closed:true,buttons:'#dlg-buttons'">
		<form id="bill" method="post">
		<div id="tableDiv" style="height:400px;overflow-y: auto">
			<table class="tableForm">
				<tr>
					<td><label style="width:70px;">用户编码</label></td>
					<td colspan=2><input id="ucode" name="ucode" class="easyui-validatebox" style="width:260px" data-options="required:true"/>
					</td>
					<td><div id="ckcode" style="display:none;"></div></td>
				</tr>
				<tr>
					<td><label  style="width:70px;">用户名称</label></td>
					<td  colspan=2><input id="uname" name="uname" class="easyui-validatebox" style="width:260px" data-options="required:true"></td>
					<td><div id="ckname" style="display:none;"></div></td>
				</tr>
				<tr>
					<td><label>密码</label></td>
					<td colspan=2><input id="u_pwd" name="u_pwd" class="easyui-validatebox" 
						type="password"  style="width:260px" 
						data-options="required:true,validType:['minLength[8]','pwdrule']"/> 
					</td>
					<td><div id="u_pwd_ck" style="display:none;"></div></td>
				</tr>
				<tr>
					<td><label>确认密码</label></td>
					<td colspan=2><input id="uc_pwd" name="uc_pwd" class="easyui-validatebox"  
							type="password" style="width:260px" 
							data-options="required:true,validType:['equals[\'#u_pwd\']','minLength[6]']"></td>
					<td><div id="uc_pwd_ck" style="display:none;"></div></td>
				</tr>
				
				<tr>
					<td><label>生效时间</label></td>
					<td><input id="en_time" name="en_time" class="easyui-datebox" style="width:170px" data-options="required:true"/> 
					</td>
					<td><label>失效时间</label></td>
					<td><input id="dis_time" name="dis_time" class="easyui-datebox" style="width:170px"></td>
				</tr>
				
				
				<tr>
					<td><label>用户描述</label></td>
					<td colspan=3><input id="u_note" name=u_note class="easyui-textbox" style="width:440px"></td>
				</tr>
				</table>
				</div>
				<input type="text" name="role_id" style="display:none;">
				<input type="text" name="uid" style="display:none;">
				<input type="text" name="src_type" style="display:none;">
				<input id="corp_id" name="corp_id" type="hidden"  >
				<input id="crtcorp_id" name="crtcorp_id" type="hidden"  >
				
			</form>
	</div>
	<div id="dlg-buttons">
		<a href="javascript:void(0)" id="addNew" class="easyui-linkbutton c6" onclick="onSave(false)">保存</a>
		<a href="javascript:void(0)" id="editOne" class="easyui-linkbutton c6" onclick="onSave(true)">保存</a>
        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="cancel()">取消</a>
	</div>
</body>
</html>