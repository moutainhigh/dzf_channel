<%@ page import="java.util.Calendar"%>
<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
	<title>加盟商客户管理</title>
	<jsp:include page="../../inc/easyui.jsp"></jsp:include>   
	<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
	<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/report/custManageRep.js");%> charset="UTF-8" type="text/javascript"></script>
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
						<%-- <div class="h30 h30-arrow" id="cxjs">
							<label class="mr5">查询期间：</label>
							<input id="qddate" name="ddate" type="text" class="easyui-datebox" 
										style="width:137px;height:30px;" value="<%=today%>" />
							<a id="query" href="javascript:void(0)" style="margin-bottom: 0px;" class="ui-btn ui-btn-xz" onclick="reloadData()">查询</a> 
						</div> --%>
				    </div>
				  	<div class="right">
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz conn"
							data-options=" plain:true" onclick="reloadData()" id="">刷新</a>
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
