<%@page import="java.util.Map"%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.IGlobalConstants"%>
<%
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Date nowDate = new Date();//当天日期
	Calendar c=Calendar.getInstance();
	c.setTime(nowDate);
	c.add(Calendar.MONTH, -1);//向前推一个月
	Date beforeDate=c.getTime();

%>
<!DOCTYPE html>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>发票管理</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/invoice/sys_fpmng.js");%> charset="UTF-8" type="text/javascript"></script>
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
						<label class="mr5">查询：</label>
						<strong id="querydate"><%=sdf.format(beforeDate)%> 至 <%=sdf.format(nowDate)%></strong>
						<span class="arrow-date"></span>
					</div>
				</div>
				<div class=right>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onBilling()">开票</a>
				</div>
			</div>
		</div>
		<div class="qijian_box" id="qrydialog" style="display: none; width: 420px; height: 210px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<form id="query_form">
				<h3>
					<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="width: 80px;text-align:right">日期：</label>
					<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" data-options="width:130,height:27,editable:true" value="<%=sdf.format(beforeDate)%>" /></font>
					<font>-</font>
					<font><input name="edate" type="text" id="edate" class="easyui-datebox" data-options="width:130,height:27,editable:true"  value="<%=sdf.format(nowDate)%>" /></font>
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">渠道商：</label>
					<input id="channel_select" class="easyui-textbox" style="width:219px;height:28px;" />
					<input id="pk_account" type="hidden">
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">发票状态：</label>
					<select id="istatus" class="easyui-combobox" data-options="panelHeight:'auto'" style="width:219px;height:28px;">
						<option value="-1">全部</option>
						<option value="1">待开票</option>
						<option value="2">已开票</option>
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
		
		<div id="kj_dialog"></div>
		<div id="kj_buttons" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#kj_dialog').dialog('close');" style="width:90px">取消</a>
		</div>
	</div>
</body>
</html>