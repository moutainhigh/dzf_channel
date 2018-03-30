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
.panel-body{overflow:auto}
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
						<div class="h30 h30-arrow" id="kshu">
							<label style="text-align:right;width: 70px;">大区：</label> 
							<input id="aname"  name="aname" class="easyui-combobox" style="width: 180px; height: 28px;" 
								data-options="required:false,valueField:'name',textField:'name',panelHeight:100" editable="false" />  
							<label style="text-align:right;width: 70px;">省（市）：</label> 
							<input id="ovince"  name="ovince" class="easyui-combobox" style="width: 160px; height: 28px;" 
								data-options="required:false,valueField:'id',textField:'name',panelHeight:100" editable="false" />  
							<label style="text-align:right;width:70px;">培训师：</label> 
							<input id="uid" name="uid" class="easyui-combobox" style="width:160px;height:28px;text-align:left"
								data-options="required:false,valueField:'id',textField:'name',panelHeight:100" editable="false" />  
							<a href="javascript:void(0)" class="ui-btn ui-btn-xz conn"
							data-options=" plain:true" style="margin-bottom:0px;" onclick="load()" id="">查询</a>
							<a href="javascript:void(0)" class="ui-btn ui-btn-xz conn"
							data-options=" plain:true" style="margin-bottom:0px;" onclick="clearCondition()" id="">清空</a>
						</div>
					</div>
		        </div>
		    </div>
		    <div class="mod-inner">
				<div id="dataGrid" class="grid-wrap">
					<table id="grid"></table>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
