<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>出入库明细表</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/dealmanage/stockoutin.js");%> charset="UTF-8" type="text/javascript"></script>
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
						<label class="mr5">查询：</label>
						<strong id="jqj"></strong>
						<span class="arrow-date"></span>
					</div>
				</div>
				<div class="left mod-crumb">
					<span class="cur"></span>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="doExport()">导出</a>
				</div>
			</div>
		</div>
		
		<!-- 查询对话框 begin -->
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:218px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<label style="text-align:right; width: 70px;">时间：</label> 
				<input id="begdate" name="begdate" type="text" class="easyui-datebox" 
						 data-options="width:137,height:27" />  - 
				<input id="enddate" name="enddate" type="text" class="easyui-datebox" 
						data-options="width:137,height:27" />
			</div>
			
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">类型：</label>
			<!--  	<input id="itype" name="itype" class="easyui-combobox" style="width:286px;height:28px;text-align:left"
					data-options="required:false,valueField:'id',textField:'name',panelHeight:200" /> -->
					<select id="itype" name="itype" class="easyui-combobox" data-options="panelHeight:'auto'" 
					editable="false" style="width:286px;height:28px;">
					<option value="0">全部</option>
					<option value="1">入库</option>
					<option value="2">出库</option>
				</select>
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">商品：</label>
				<input id="goodsname" name="goodsname" class="easyui-combobox" style="width:286px;height:28px;text-align:left"
					data-options="required:false,valueField:'id',textField:'name',multiple:true,panelHeight:200" /> 
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">单据编码：</label>
				<input id="qvcode" class="easyui-textbox" style="width:286px;height:28px;"/>
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
	
</body>
</html>