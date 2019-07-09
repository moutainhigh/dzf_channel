<%@ page language="java"  pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
	<title>加盟商人员统计</title>
	<jsp:include page="../../inc/easyui.jsp"></jsp:include>   
	<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
	<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/report/personStatis.js");%> charset="UTF-8" type="text/javascript"></script>	
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
							data-options=" plain:true" onclick="load()">刷新</a> 
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz conn"
							data-options=" plain:true" onclick="edit()">修改</a> 
					    <a href="javascript:void(0)" class="ui-btn ui-btn-xz conn"
							data-options=" plain:true" onclick="doExport()">导出</a> 	
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
			
			<!-- 编辑对话框  begin-->
		 <div id="cbDialog" class="easyui-dialog" style="height:300px;width:400px;overflow:hidden;padding-top:18px;" 
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<form id="market_edit" method="post">
				<input id="marketid" name="marketid" type="hidden">
				<input id="updatets" name="updatets" type="hidden">
				<input id="corpid" name="corpid" type="hidden">
				<input id="code" name="code" type="hidden">
				<div class="time_col time_colp10">
						<label style="text-align:right; width: 124px;">加盟商：</label> 
						<input id="corpname" class="easyui-textbox" data-options="readonly:true" style="width:168px;height:28px;"/>
			    </div>
			    <div class="time_col time_colp10">
						<label style="text-align:right; width: 124px;">销售经理：</label> 
						<input id="mnum" name="mnum" class="easyui-numberbox" data-options="min:1" style="width:168px;height:28px;"/>
			    </div>
			    <div class="time_col time_colp10">
						<label style="text-align:right; width: 124px;">销售主管：</label> 
						<input id="dnum" name="dnum" class="easyui-numberbox" data-options="min:1" style="width:168px;height:28px;"/>
			    </div>
			    <div class="time_col time_colp10">
						<label style="text-align:right; width: 124px;">销售：</label> 
						<input id="snum" name="snum" class="easyui-numberbox" data-options="min:1" style="width:168px;height:28px;"/>
			    </div>
				<div style="float:right;margin-top:25px;margin-right:118px;">
				    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onSave()">保存</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onCancel()">取消</a>
				</div>
			</form>
		</div>
		<!-- 新增对话框  end-->
			
		</div>
		<div id="userDetail" class="easyui-dialog" title="加盟商人员明细表" 
			data-options="modal:true,closed:true" style="width:940px;height:500px;">
			<div data-options="region:'center'" style="overflow-x:auto; overflow-y:auto;margin: 0 auto;width:90%;height:380px;padding:10px">
				 <table id="userGrid"></table>	
			</div>
		</div>
	</div>
</body>
</html>
