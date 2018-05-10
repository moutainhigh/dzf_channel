<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>加盟商管理-渠道经理 </title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/report/channelman.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/report/mancommon.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<body>
	<div class="wrapper">
		<div id="listPanel" class="wrapper" style="width: 100%;overflow:hidden; height: 100%;">
			<div class="mod-toolbar-top">
				<div class="mod-toolbar-content">
					<div class="left mod-crumb">
						<div class="h30 h30-arrow" id="cxjs">
							<label class="mr5">查询期间：</label>
							<input id="bdate" name="bdate"  class="easyui-datebox" style="width:137px;height:30px;" data-options="validType:'checkdate'"/>
							<input id="edate"  name="edate"  class="easyui-datebox" style="width:137px;height:30px;" data-options="validType:'checkdate'"/>
							<a id="query" href="javascript:void(0)" style="margin-bottom: 0px;" class="ui-btn ui-btn-xz" onclick="reloadData()">查询</a> 
						</div>
						<div class="h30 h30-arrow" id="kshu">
							<input style="height: 28px; width: 300px" class="easyui-textbox" id="filter_value" prompt="录入加盟商名称关键字回车可过滤" />
						</div>
					</div>
					<div class="right">
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="doExport(1)">导出</a>
					</div>
				</div>
			</div>
			<div class="mod-inner">
				<div id="dataGrid" class="grid-wrap">
					<table id="grid"></table>
				</div>
			</div> 
			<div id="detail_dialog" class="easyui-dialog" title="合同金额明细" data-options="modal:true,closed:true" style="width:730px;height:500px;">
				<div class="time_col" style="padding-top: 10px;width:96%;margin:0 auto;">
					<label style="text-align:right">查询：</label> 
					<span id ="qrydate" style="vertical-align: middle;font-size:14px;"></span>
					<label style="text-align:right">加盟商：</label> 
					<span id ="corpnm" style="vertical-align: middle;font-size:14px;"></span>
					<div class="right" style="float: right;display: inline-block;"> 
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onDetPrint()">打印</a>
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onDetExport()">导出</a>
				 	</div>
				</div>	
				<div data-options="region:'center'" style="overflow-x:auto; overflow-y:auto;margin: 0 auto;width:90%;height:380px;padding:10px">
					 <table id="gridh"></table>	
				</div>
			</div>
	 	</div>	
	</div>
</body>
</html>
