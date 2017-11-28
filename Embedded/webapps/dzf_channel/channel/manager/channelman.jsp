<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>加盟商管理-渠道经理 </title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/manager/channelman.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<body>
	<div class="wrapper">
		<div id="listPanel" class="wrapper" style="width: 100%;overflow:hidden; height: 100%;">
			<div class="mod-toolbar-top">
				<div class="mod-toolbar-content">
					<div class="left mod-crumb">
						<div class="h30 h30-arrow" id="cxjs">
							<label class="mr5">查询期间：</label>
							<input id="bdate" name="bdate"  class="easyui-datebox" style="width:137px;height:30px;" />
							<input id="edate"  name="edate"  class="easyui-datebox" style="width:137px;height:30px;"/>
							<a id="query" href="javascript:void(0)" style="margin-bottom: 0px;" class="ui-btn ui-btn-xz" onclick="reloadData()">查询</a> 
						</div>
						<div class="h30 h30-arrow" id="kshu">
							<input style="height: 28px; width: 300px" class="easyui-textbox" id="filter_value" prompt="录入加盟商名称关键字回车可过滤" />
						</div>
					</div>
					<div class="right">
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="doExport()">导出</a>
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
