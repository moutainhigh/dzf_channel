<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>出入库汇总表</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/dealmanage/stocksum.js");%> charset="UTF-8" type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/jslib/jquery-easyui-1.4.3/datagrid-detailview.js"
	charset="UTF-8" type="text/javascript"></script>
</head>
<body>
	<!-- 列表界面 begin -->
	<div id="listPanel" class="wrapper" data-options="closed:false">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="cxjs">
						<label class="mr5">日期：</label>
						<input id="begdate" name="begdate" type="text" class="easyui-datebox" 
							 data-options="width:137,height:27" />  -
							 <input id="enddate" name="enddate" type="text" class="easyui-datebox" 
							data-options="width:137,height:27" />  
					   
					</div>
						<a class="ui-btn save_input" onclick="reloadData()" style="height:21px;margin-top:2px;margin-left:9px">查询</a>
				</div>
				<div class="left mod-crumb">
					<span class="cur"></span>
				</div>
				
			</div>
		</div>
		
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>

	</div>
	<!-- 列表界面 end -->
	
</body>
</html>