<%@ page language="java"  pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.text.SimpleDateFormat"%>
<!DOCTYPE html>
<html>
<head>
	<title>流失客户明细</title>
	<jsp:include page="../../inc/easyui.jsp"></jsp:include>   
	<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
	<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/report/lossCustomer.js");%> charset="UTF-8" type="text/javascript"></script>	
	<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/report/repcommon.js");%> charset="UTF-8" type="text/javascript"></script>  
	<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<%
	//获取当前月期间
	Calendar e = Calendar.getInstance();
	String ym = new SimpleDateFormat("yyyy-MM").format(e.getTime());
	e.add(Calendar.MONTH, +1);
	String bym = new SimpleDateFormat("yyyy-MM").format(e.getTime());//当前日期（往后推1个月）年-月
%>

<body class="dzf-skin">
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
					<div class="left mod-crumb">
					<div style="margin:6px 0px 0px 10px;float:right;font-size:14px;">
						<a href="javascript:void(0)"  style="font-size:14;color:blue;" onclick="qryByType(0)">全部</a>
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; " onclick="qryByType(1)">正常流失</a>
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; " onclick="qryByType(2)">非正常流失</a>
					</div>
				</div>
				  	<div class="right">
					    <a href="javascript:void(0)" class="ui-btn ui-btn-xz conn"
							data-options=" plain:true" onclick="doExport()">导出</a> 	
					</div>
		        </div>
		    </div>
		    <div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:265px;">
				<s class="s"><i class="i"></i></s>
				<h3>
					<span>查询</span> <a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="text-align:right;width:100px;">停用日期：</label>
					<input type="text" id="sbegdate" class="easyui-textbox" data-options="editable:false"
						style="width:125px;height:30px;" value=<%=ym%> />
					-
					<input type="text" id="senddate" class="easyui-textbox" data-options="editable:false"
						style="width:125px;height:30px;" value=<%=bym%> />
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width: 100px;">大区：</label> 
					<input id="aname"  name="aname" class="easyui-combobox" style="width: 90px; height: 28px;" 
						data-options="required:false,valueField:'name',textField:'name',panelHeight:100" editable="false" />
					<label style="text-align:right;width: 70px;">会计运营：</label> 
					<input id="uid" name="uid" class="easyui-combobox" style="width:90px;height:28px;text-align:left"
						data-options="required:false,valueField:'id',textField:'name',panelHeight:100" editable="false" />  
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width: 100px;">省（市）：</label> 
					<input id="ovince"  name="ovince" class="easyui-combobox" style="width: 260px; height: 28px;" 
						data-options="required:false,valueField:'id',textField:'name',panelHeight:100" editable="false" />  
				</div>		
				<div class="time_col time_colp10">
					<label style="text-align:right;width:100px">加盟商：</label>
					<input id="channel_select" class="easyui-textbox" style="width:260px;height:28px;" />
					<input id="pk_account" type="hidden">
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width:100px">停用原因：</label>
					<input id="sreason" class="easyui-textbox" style="width:260px;height:28px;"/>
				</div>
				<p>
					<a class="ui-btn save_input" onclick="clearCondition();">清除</a> 
					<a class="ui-btn save_input" onclick="reloadData()">确定</a>
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
	</div>
</body>
</html>
