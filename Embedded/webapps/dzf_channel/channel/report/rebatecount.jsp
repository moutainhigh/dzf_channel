<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.DzfUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<title>返点金额统计</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>
	rel="stylesheet">
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/report/rebatecount.js");%>
	charset="UTF-8" type="text/javascript"></script>
</head>

<body>
	<div id="List_panel" class="wrapper" data-options="closed:false">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<div id="cxjs" class="h30 h30-arrow" style="margin-top: -2px;">
						<label style="width: 90px;text-align:right">期间：</label>
						<font>
							<select id="qyear" name="qyear" class="easyui-combobox" 
								data-options="editable:false"  style="width:130px;height:28px;">
								<% DzfUtil.WriteYearOption(out);%>
					 		</select>
					 	</font>
					</div>
				</div>
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="filter">
						<!-- <label class="mr5">加盟商：</label> -->
						<input id="filter_value" style="height:28px;width:250px" class="easyui-textbox"  
						prompt="请输入加盟商名称,按Enter键 "/> 
					</div>
				</div>
			</div>
		</div>
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
	</div>
</body>

</html>