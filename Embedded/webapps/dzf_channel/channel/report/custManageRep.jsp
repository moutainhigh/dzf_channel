<%@ page import="java.util.Calendar"%>
<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
	<title>行业分析</title>
	<jsp:include page="../../inc/easyui.jsp"></jsp:include>   
	<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
	<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/report/custManageRep.js");%> charset="UTF-8" type="text/javascript"></script>
	<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/report/repcommon.js");%> charset="UTF-8" type="text/javascript"></script>
	<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<style type="text/css">
.datagrid-header .datagrid-cell span {
    font-weight: bold;
    font-size: 12px;
}
</style>
</head>
<%
	//获取当天日期
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Date nowDate = new Date();
	String today = sdf.format(new Date());
	//获取前一周日期
	int nowDay = nowDate.getDate();
	int lastWeakDays = nowDate.getDate() - 7;
	Date lastDate = new Date();
	lastDate.setDate(lastWeakDays);
	String lastWeakDay = sdf.format(lastDate);
%>

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
							data-options=" plain:true" onclick="load()" id="">刷新</a> 
					</div>
		        </div>
		    </div>
		    <div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:250px;">
				<s class="s"><i class="i"></i></s>
				<h3>
					<span>查询</span> <a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="text-align:right;width: 70px;">大区：</label> 
					<input id="aname"  name="aname" class="easyui-combobox" style="width: 295px; height: 28px;" 
						data-options="required:false,valueField:'name',textField:'name',panelHeight:100" editable="false" />  
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width: 70px;">省（市）：</label> 
					<input id="ovince"  name="ovince" class="easyui-combobox" style="width: 295px; height: 28px;" 
						data-options="required:false,valueField:'id',textField:'name',panelHeight:100" editable="false" />  
				</div>		
				<div class="time_col time_colp10">
					<label style="text-align:right;width:70px;">会计运营经理：</label> 
					<input id="uid" name="uid" class="easyui-combobox" style="width:295px;height:28px;text-align:left"
						data-options="required:false,valueField:'id',textField:'name',panelHeight:100" editable="false" />  
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width:70px">加盟商：</label>
					<input id="channel_select" class="easyui-textbox" style="width:295px;height:28px;" />
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
		</div>
	</div>
</body>
</html>
