<%@ page language="java"  pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
	<title>账务设置</title>
	<jsp:include page="../../inc/easyui.jsp"></jsp:include>   
	<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
	<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/chn_set/accountset.js");%> charset="UTF-8" type="text/javascript"></script>
	<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/report/repcommon.js");%> charset="UTF-8" type="text/javascript"></script>
	<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
</head>

<body class="dzf-skin">
	<div class="wrapper">
		<div id="List_panel" class="wrapper" style="width: 100%;/* overflow:hidden; */ height: 100%;" data-options="closed:false">
		     <div class="mod-toolbar-top">
		        <div class="mod-toolbar-content">
				    <div class="left mod-crumb">
						<span class="cur"></span>
					</div>
					<div class="left mod-crumb">
						<div class="h30 h30-arrow" id="cxjs">
							<label class="mr5">查询：</label>
							<strong id="jqj"></strong>
							<span class="arrow-date"></span>
						</div>
					</div>
				  	<div class="right">
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz conn"
							data-options=" plain:true" onclick="add()" id="">新增</a> 
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz conn"
							data-options=" plain:true" onclick="delete()" id="">删除</a> 
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz conn"
							data-options=" plain:true" onclick="load()" id="">导出</a> 
					</div>
		        </div>
		    </div>
		    <div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:250px;">
				<s class="s"><i class="i"></i></s>
				<h3>
					<span>查询</span> <a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="text-align:right;width: 100px;">大区：</label> 
					<input id="aname"  name="aname" class="easyui-combobox" style="width: 260px; height: 28px;" 
						data-options="required:false,valueField:'name',textField:'name',panelHeight:100" editable="false" />  
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width: 100px;">省（市）：</label> 
					<input id="ovince"  name="ovince" class="easyui-combobox" style="width: 260px; height: 28px;" 
						data-options="required:false,valueField:'id',textField:'name',panelHeight:100" editable="false" />  
				</div>		
				<div class="time_col time_colp10">
					<label style="text-align:right;width:100px;">会计运营经理：</label> 
					<input id="uid" name="uid" class="easyui-combobox" style="width:260px;height:28px;text-align:left"
						data-options="required:false,valueField:'id',textField:'name',panelHeight:100" editable="false" />  
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width:100px">加盟商：</label>
					<input id="channel_select" class="easyui-textbox" style="width:260px;height:28px;" />
					<input id="pk_account" type="hidden">
				</div>
				<p>
					<a class="ui-btn save_input" id="cleanbtn" onclick="clearCondition();">清除</a> 
					<a class="ui-btn save_input" onclick="load()">确定</a>
					<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
				</p>
			</div>
		    <div class="mod-inner">
				<div id="dataGrid" class="grid-wrap">
					<table id="grid"></table>
				</div>
			</div>
			
			<div id="kj_dialog"></div>
			<div id="kj_buttons" style="display:none;">
				<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
				<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#kj_dialog').dialog('close');" style="width:90px">取消</a>
			</div>
			<div id="gs_dialog"></div>
		</div>
		
		<div id="addDialog" class="easyui-dialog" style="width:460px;height:300px;padding-top:30px;" data-options="closed:true" modal=true>
			<form id="addForm" method="post">
				<div class="time_col time_colp11">
					<div style="display: inline-block;">
						<label style="width:140px;text-align: right;"><i class="bisu">*</i>加盟商:</label>
					    <input id="c_corpnm" name="corpnm" class="easyui-textbox" style="width:200px;height:25px;" data-options="required:true" />
						<input id="c_corpid" name="corpid" type="hidden">
					</div>
				</div>
				<div class="time_col time_colp11">
					<label style="text-align:right;width:140px;"><i class="bisu">*</i>客户:</label>
					<input id="c_corpkname" name="corpkname" class="easyui-textbox" style="width:200px;height:28px;" data-options="required:true"/>
					<input id="c_corpkid" name="corpkid" type="hidden"> 
					<input id="vccode" name="vccode" type="hidden"> 
					<input id="contractid" name="contractid" type="hidden"> 
				</div>
				<div class="time_col time_colp11">
					<label style="text-align:right;width:140px;">服务期限</label>
					<font>
						<input type="text" id="bperiod" name='bperiod' class="easyui-textbox" data-options="readonly:true" style="width: 100px; height: 28px; display: none;" >
					</font> 
					<font>-</font> 
					<font> 
						<input type="text" id="eperiod" name='eperiod'class="easyui-textbox" data-options="readonly:true" style="width: 100px; height: 28px; display: none;" >
					</font> 
				</div>
				<div class="time_col time_colp11">
					<label style="text-align:right;width:140px;">调整日期</label>
					<font> 
						<input type="text" id="tperiod" name='tperiod' class="easyui-textbox" data-options="readonly:true" style="width: 100px; height: 28px; display: none;" >
					</font> 
					<font>-</font> 
					<font>
						<input id="cperiod" name='cperiod'  class="easyui-textbox" data-options="editable:false,required:true" 
							style="width:100px; height: 28px; display: none;" >
					</font> 
				</div>
			</form>
			<div style="text-align:center;margin-top:30px;">
			    <a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="save()">确定</a> 
				<a href="javascript:void(0)"  class="ui-btn ui-btn-xz" onclick="cancel()">取消</a>
			</div>
		</div>
		
	</div>
</body>
</html>
