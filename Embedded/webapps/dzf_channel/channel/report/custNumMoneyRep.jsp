<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
<title>加盟商客户数量金额统计</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>
	rel="stylesheet">
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/periodext.css");%>
	rel="stylesheet">
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/report/custNumMoneyRep.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/report/periodext.js");%>
	charset="UTF-8" type="text/javascript"></script>
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
						<div class="h30 h30-arrow" id="cxjs">
							<label class="mr5">查询期间：</label>
							<input type="text" id="qryperiod" class="easyui-textbox" data-options="editable:false"
								style="width:137px;height:30px;" value=<%=ym%> />
							<a id="query" href="javascript:void(0)" style="margin-bottom: 0px;" 
								class="ui-btn ui-btn-xz" onclick="reloadData()">查询</a> 
						</div>
				    </div>
				  	<div class="right">
						<!-- <a href="javascript:void(0)" class="ui-btn ui-btn-xz conn"
							data-options=" plain:true" onclick="reloadData(2)" id="">刷新</a> -->
					</div>
		        </div>
		    </div>
			<div id="dataGrid" class="grid-wrap">
				<table id="grid"></table>
			</div>
		</div>
	</div>
</body>
</html>
