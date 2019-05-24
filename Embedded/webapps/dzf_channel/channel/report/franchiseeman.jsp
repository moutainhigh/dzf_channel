<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>加盟商管理-加盟商总经理</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/report/franchiseeman.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/report/mancommon.js");%> charset="UTF-8" type="text/javascript"></script>
<style type="text/css">
.panel-body{overflow:auto}
</style>
</head>
<body>
	<div class="wrapper">
		<div id="listPanel" class="wrapper" style="width: 100%;overflow:hidden; height: 100%;">
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
					<div class="h30 h30-arrow" id="kshu">
							<input style="height: 28px; width: 280px" class="easyui-textbox" id="filter_value" prompt="录入加盟商名称关键字回车可过滤" />
					</div>
					<div class="right">
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="doExport(3)">导出</a>
					</div>
				</div>
			</div>
			<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:300px;">
				<s class="s"><i class="i"></i></s>
				<h3>
					<span>查询</span> <a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="sel_time time_col">
					<label style="text-align:right">查询期间：</label> 
					<input id="bdate" name="bdate"  class="easyui-datebox" data-options="width:137,height:28,validType:'checkdate'" />
					<font>-</font> 
					<input id="edate" name="edate"  class="easyui-datebox" data-options="width:137,height:28,validType:'checkdate'" />
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width: 70px;">大区：</label> 
					<input id="aname"  name="aname" class="easyui-combobox" style="width: 295px; height: 28px;" 
						data-options="required:false,valueField:'name',textField:'name',panelHeight:100"/>  
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width: 70px;">省（市）：</label> 
					<input id="ovince"  name="ovince" class="easyui-combobox" style="width: 295px; height: 28px;" 
						data-options="required:false,valueField:'id',textField:'name',panelHeight:100" />  
				</div>		
				<div class="time_col time_colp10">
					<label style="text-align:right;width:70px;">渠道经理：</label> 
					<input id="cuid" name="cuid" class="easyui-combobox" style="width:295px;height:28px;text-align:left"
						data-options="required:false,valueField:'id',textField:'name',panelHeight:100"/>  
				</div>
				<p>
					<a class="ui-btn save_input" id="cleanbtn" onclick="clearCondition();">清除</a> 
					<a class="ui-btn save_input" onclick="reloadData()">确定</a>
					<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
				</p>
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
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onDetExport(1)">导出</a>
				 	</div>
				</div>	
				<div class="easyui-tabs" style="padding: 0px 0px 0px 18px;width:96%;" id="detail">
					<div title="已审核" style="border: none; margin-top: -2px;width:90%;height:380px;padding:10px">
						<div data-options="region:'center'" style="overflow-x:auto; overflow-y:auto;margin: 0 auto;">
							 <table id="gridh"></table>	
						</div>
					</div>
					<div title="未审核" style="border: none; margin-top: -2px;width:90%;height:380px;padding:10px">
						<div data-options="region:'center'" style="overflow-x:auto; overflow-y:auto;margin: 0 auto;">
							 <table id="gridw"></table>	
						</div>
					</div>
					<div title="已驳回" style="border: none; margin-top: -2px;width:90%;height:380px;padding:10px">
						<div data-options="region:'center'" style="overflow-x:auto; overflow-y:auto;margin: 0 auto;">
							 <table id="gridb"></table>	
						</div>
					</div>
				</div>
			</div>
	 	</div>	
	</div>
</body>
</html>
