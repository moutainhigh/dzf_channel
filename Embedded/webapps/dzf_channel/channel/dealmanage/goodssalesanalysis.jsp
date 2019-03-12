<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>商品销售分析</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/report/goodssalesanalysis.js");%> charset="UTF-8" type="text/javascript"></script>
<body>
	<!-- 列表界面 begin -->
	<div id="listPanel" class="wrapper" data-options="closed:false">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="cxjs">
						<label class="mr5">查询：</label>
						<strong id="jqj"></strong>
						<span class="arrow-date"></span>
					</div>
				</div>
				<div class="left mod-crumb">
					<span class="cur"></span>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="onExport()">导出</a>
				</div>
			</div>
		</div>
		
		<!-- 查询对话框 begin -->
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:170px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">期间：</label> 
				<input id="qryperiod" class="easyui-datebox" data-options="editable:false"
					style="width:140px;height:28px;text-align:left">
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">加盟商：</label>
				<input id="channel_select" class="easyui-textbox" style="width:280px;height:28px;"/>
				<input id="pk_account" type="hidden">
			</div>
			<p>
				<a class="ui-btn save_input" onclick="clearParams()">清除</a>
				<a class="ui-btn save_input" onclick="reloadData()">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		<!-- 查询对话框end -->
		
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>

	</div>
	<!-- 列表界面 end -->
	
	<!-- 加盟商参照 begin -->
	<div id="chnDlg"></div>
	<div id="chnBtn" style="display:none;">
		<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
		<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#chnDlg').dialog('close');" style="width:90px">取消</a>
	</div>
	<!-- 加盟商参照 end -->
	
</body>
</html>