<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
<title>业绩统计</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/report/repcommon.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/report/custNumMoneyRep.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<%
	//获取当前月期间
	Calendar e = Calendar.getInstance();
	String ym = new SimpleDateFormat("yyyy-MM").format(e.getTime());
%>
<body>
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
							data-options=" plain:true" onclick="reloadData()" id="">刷新</a> 
					</div>
		        </div>
		    </div>
		    <div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:250px;">
				<s class="s"><i class="i"></i></s>
				<h3>
					<span>查询</span> <a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="text-align:right;width: 70px;">查询期间：</label>
					<input type="text" id="qryperiod" class="easyui-textbox" data-options="editable:false"
						style="width:137px;height:30px;" value=<%=ym%> />
<!-- 							<a id="query" href="javascript:void(0)" style="margin-bottom: 0px;"  -->
<!-- 								class="ui-btn ui-btn-xz" onclick="reloadData()">查询</a>  -->
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width: 70px;">大区：</label> 
					<input id="aname"  name="aname" class="easyui-combobox" style="width: 295px; height: 28px;" 
						data-options="required:false,valueField:'name',textField:'name',panelHeight:150" editable="false" />  
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width: 70px;">省（市）：</label> 
					<input id="ovince"  name="ovince" class="easyui-combobox" style="width: 295px; height: 28px;" 
						data-options="required:false,valueField:'id',textField:'name',panelHeight:150" editable="false" />  
				</div>		
				<div class="time_col time_colp10">
					<label style="text-align:right;width:70px;">渠道经理：</label> 
					<input id="uid" name="uid" class="easyui-combobox" style="width:295px;height:28px;text-align:left"
						data-options="required:false,valueField:'id',textField:'name',panelHeight:80" editable="false" />  
				</div>
				<p>
					<a class="ui-btn save_input" id="cleanbtn" onclick="clearCondition();">清除</a> 
					<a class="ui-btn save_input" onclick="reloadData()">确定</a>
					<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
				</p>
			</div>
			<div id="dataGrid" class="grid-wrap">
				<table id="grid"></table>
			</div>
		</div>
	</div>
</body>
</html>
