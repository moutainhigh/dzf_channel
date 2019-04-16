<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>物料档案</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/matmanage/matfile.js");%> charset="UTF-8" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/jslib/jquery-easyui-1.4.3/datagrid-detailview.js"
	charset="UTF-8" type="text/javascript"></script>
<script>
	
</script>
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
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="add()">新增</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="dele()">删除</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="closed()">封存</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="opened()">启用</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="doExport()">导出</a>
				</div>
			</div>
		</div>
		
		<!-- 查询对话框 begin -->
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:194px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<label style="text-align:right; width: 85px;">录入日期：</label> 
				<input id="begdate" name="begdate" type="text" class="easyui-datebox" 
						 data-options="width:137,height:27" />  - 
				<input id="enddate" name="enddate" type="text" class="easyui-datebox" 
						data-options="width:137,height:27" />
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">物料名称：</label>
				<input id="wlname" class="easyui-textbox" style="width:286px;height:28px;"/>
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">封存状态：</label>
				<select id="sseal" class="easyui-combobox" data-options="panelHeight:'auto'" style="width:282px;height:28px;">
					<option value="0">全部</option>
					<option value="1">启用</option>
					<option value="2">封存</option>
				</select>
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
		
		<!-- 新增对话框  begin-->
		<div id="cbDialog" class="easyui-dialog" style="height:275px;width:500px;overflow:hidden;padding-top:18px;" 
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<form id="mat_add" method="post">
				<input name="matfileid" type="hidden">
				<input name="updatets" type="hidden">
				<div class="time_col time_colp10">
						<label style="width:85px;text-align:right"><i class="bisu">*</i>物料名称：</label> 
						<span class="hid">
							<input class="easyui-textbox" id="nname" name="wlname" class="hid" style="width:300px;height:28px;"
						      data-options="required:true"  validType= "remote['<%=request.getContextPath() %>/matmanage/matfile!queryMatname.action','wlname' ]" 
						      invalidMessage= "此物料已存在"  /> 
						</span>
						<span class="show">
						   <input class="easyui-textbox" id="name" name="wlname" style="width:300px;height:28px;"
						      data-options="required:true" /> 
						</span>
				</div>
				<div class="time_col time_colp10">
						<label style="width:85px;text-align:right"><i class="bisu">*</i>单位：</label>
						<input class="easyui-textbox" id="unit" name="unit" style="width:100px;height:28px;" data-options="required:true" />
						<!-- <label style="width:85px;text-align:right"><i class="bisu">*</i>成本价：</label> 
						<input class="easyui-textbox" id="cost" name="cost" style="width:100px;height:28px;" data-options="required:true" /> -->
				</div>
               	<div class="time_col time_colp10">
						<label style="width:85px;text-align:right">申请条件：</label>
						<input type="checkbox" id="apply" name="apply" style="margin-left: -10px;" value="1" onclick="this.value=(1==1)?1:0" />
						<span style="text-align: right;width:200px;font-size:14;">上季度合同审核通过数≥上季度申请数量的 70%</span>
				</div>
				<div style="float:right;margin-top:40px;margin-right:187px;">
				    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onSave()">保存</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onCancel()">取消</a>
				</div>
			</form>
		</div>
		<!-- 新增对话框  end-->
		
	</div>
	<!-- 列表界面 end -->
	
</body>
</html>