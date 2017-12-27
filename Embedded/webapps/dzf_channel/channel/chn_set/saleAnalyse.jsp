<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>销售数据分析</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>   
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../../css/periodext.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/chn_set/saleAnalyse.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/report/periodext.js");%> charset="UTF-8" type="text/javascript"></script>
<style type="text/css">
.panel-body{overflow:auto}
</style>
</head> 
<body>
	<div id="listPanel" class="wrapper" style="width: 100%;overflow:hidden; height: 100%;">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="kshu">
						<label style="text-align:right;width: 70px;">大区：</label> 
						<input id="aname"  name="aname" class="easyui-combobox" style="width: 180px; height: 28px;" 
							data-options="required:false,valueField:'name',textField:'name',panelHeight:150" editable="false" /> 
						<label style="text-align:right;width: 70px;">省（市）：</label> 
						<input id="ovince"  name="ovince" class="easyui-combobox" style="width: 150px; height: 28px;" 
							data-options="required:false,valueField:'id',textField:'name',panelHeight:150" editable="false" />  
						<label style="text-align:right;width: 70px;">期间：</label>
						<font> 
							<input id="bdate" type="text" class="easyui-datebox" style="width:110px;height:30px;" />
						</font> 
						<font>-</font> 
						<font> 
							<input id="edate" type="text"  class="easyui-datebox" style="width:110px;height:30px;" />
						</font>
						<a id="query" href="javascript:void(0)" style="margin-bottom: 0px;" class="ui-btn ui-btn-xz" onclick="load()">查询</a>
						<input style="height: 28px; width: 220px" class="easyui-textbox" id="quname" prompt="录入加盟商名称，按Enter过滤" />
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
 	</body>
</html>