<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.DzfUtil"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>

<!DOCTYPE html>
<html>
<head>
<title>渠道商客户审批</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script type="text/javascript" src=<%UpdateGradeVersion.outversion(out, "../../js/channel/custapprove/customer_approve.js");%> charset="UTF-8"></script>
<style type="text/css">
.pos-background {
	background-color: yellow;
}
</style>
</head>
<body>
	<div class="wrapper" style="overflow:hidden">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<div id="cxjs" class="h30 h30-arrow">
						<label class="mr5">查询：</label><strong id="query_period" >&nbsp;</strong><span class="arrow-date"></span>
						<input id="quick_query" class="easyui-textbox" style="height:30px;width:200px">
					</div>
				</div>
				<div class=right>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="refresh()">刷新</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="approve()">审批</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="abandonApprove()">弃审</a>
				</div>
			</div>
		</div>
		<div class="qijian_box" id="qrydialog" style="display: none; width: 420px; height: 250px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<form id="query_form">
				<h3>
					<span>查询条件</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="width: 70px;text-align:right">录入日期：</label>
					<font><input name="begindate" type="text" id="startDate" class="easyui-datebox" data-options="width:137,height:27,editable:true" value="" /></font>
					<font>-</font>
					<font><input type="text" id="endDate" class="easyui-datebox" name="enddate"  data-options="width:137,height:27,editable:true"  value="" /></font>
				</div>
				<div class="time_col time_colp10">
					<label style="width:70px;">所属区域：</label>
					<input id="area_select" class="easyui-textbox" style="width:219px;height:28px;" />
				</div>
				<div class="time_col time_colp10">
					<label style="width:70px;">渠道商：</label>
					<input id="channel_select" class="easyui-textbox" value="" style="width:219px;height:28px;" />
					<input id="pk_account" type="hidden">
				</div>
				<div class="time_col time_colp10">
					<label style="width:70px;">审批状态：</label>
					<select id="approve_status" class="easyui-combobox" data-options="panelHeight:'auto'" style="width:219px;height:28px;">
						<option value="">全部</option>
						<option value="0">未审批</option>
						<option value="1">已审批</option>
						<option value="2">未通过</option>
					</select>
				</div>
			</form>
			<p>
				<a class="ui-btn save_input" onclick="clearParams()">清空</a>
				<a class="ui-btn save_input" onclick="reloadData()">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="grid"></table>
			</div>
		</div>
		
		<div id="approve_dialog">
			<div style="margin-top: 10px;margin-left: 10px">
				<form id="approve_form" method="post">
					<p style="text-align: center;margin-bottom: 5px;">
						通过：<input name="pass" type="radio" value="Y" checked>
						&nbsp;&nbsp;&nbsp;未通过：<input name="pass" type="radio" value="N">
					</p>
					<div>
						<label>审批意见：</label>
					</div>
					<div>
						<textarea style="resize:none;width: 270px" name="comment" rows="3" style="width: 270px"></textarea>
					</div>
				</form>
			</div>
		</div>
		<div id="approve_buttons" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="doApprove();" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#approve_dialog').dialog('close');" style="width:90px">取消</a>
		</div>
		
		<div id="kj_dialog"></div>
		<div id="kj_buttons" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectgs();" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#kj_dialog').dialog('close');" style="width:90px">取消</a>
		</div>
		<div id="area_dialog" style="overflow: auto;"></div>
		<div id="area_buttons" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectArea();" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#area_dialog').dialog('close');" style="width:90px">取消</a>
		</div>
	</div>
</body>
</html>
