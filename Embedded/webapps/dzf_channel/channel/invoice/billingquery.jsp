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
<title>加盟商开票查询</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/invoice/billingquery.js");%> charset="UTF-8" type="text/javascript"></script>
<style type="text/css">
.pos-background {
	background-color: yellow;
}
.search-rs {
	background: #2c9dd8;
	color: #fff;
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
						<strong id="querydate"></strong>
						<span class="arrow-date"></span>
					</div>
				</div>
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="filter">
						<input style="height:28px;width:220px" class="easyui-textbox" id="quname" prompt="录入加盟商名称或编码回车 "/> 
					</div>
				</div>
				
				<div class=right>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onBilling()">开票</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onExport()">导出</a>
				</div>
			</div>
		</div>
		<div class="qijian_box" id="qrydialog" style="display: none; width:420px; height:230px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<form id="query_form">
				<h3>
					<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="width: 80px;text-align:right">截止日期：</label>
					<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" style="width:105px;height:28px;" /></font>
					<label style="text-align:right;width:55px;">大区：</label> 
					<input id="aname"  name="aname" class="easyui-combobox" style="width:105px; height: 28px;" 
						data-options="required:false,valueField:'name',textField:'name',panelHeight:100" editable="false" /> 
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">加盟商：</label>
					<input id="channel_select" class="easyui-textbox" style="width:282px;height:28px;" />
					<input id="pk_account" type="hidden">
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">渠道经理：</label>
					<input id="manager" class="easyui-textbox" style="width:282px;height:28px;" />
					<input id="managerid" type="hidden">
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">渠道运营：</label>
					<input id="operater" class="easyui-textbox" style="width:282px;height:28px;" />
					<input id="operaterid" type="hidden">
				</div>
				
			</form>
			<p>
				<a class="ui-btn save_input" onclick="clearParams()">清除</a>
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
		
		<div id="payDetail" class="easyui-dialog" title="扣款明细" 
			data-options="modal:true,closed:true" style="width:940px;height:500px;">
			<div class="time_col" style="padding-top: 10px;width:96%;margin:0 auto;">
				<label style="text-align:right">查询：</label> 
				<span id ="qrydate" style="vertical-align: middle;font-size:14px;"></span>
				<label style="text-align:right">加盟商：</label> 
				<span id ="corpnm" style="vertical-align: middle;font-size:14px;"></span>
				<!-- <label style="text-align:right">付款类型：</label> 
				<span id ="ptypenm" style="vertical-align: middle;font-size:14px;"></span> -->
			<div class="right" style="float: right;display: inline-block;"> 
				<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onRecPrint()">打印</a>
				<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onRecExport()">导出</a>
		 	</div>
			</div>	
			
			<div data-options="region:'center'" style="overflow-x:auto; overflow-y:auto;margin: 0 auto;width:90%;height:380px;padding:10px">
				 <table id="gridp"></table>	
			</div>
		</div>
		
		 <!-- 渠道经理参照对话框及按钮 begin -->
		<div id="manDlg"></div>
		<div id="manBtn" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectMans()" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" 
				onclick="javascript:$('#manDlg').dialog('close');" style="width:90px">取消</a>
		</div>
		<!-- 渠道经理参照对话框及按钮 end -->
		
		<!-- 渠道运营参照对话框及按钮 begin -->
		<div id="operDlg"></div>
		<div id="operBtn" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectOpers()" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" 
				onclick="javascript:$('#operDlg').dialog('close');" style="width:90px">取消</a>
		</div>
		<!-- 渠道运营参照对话框及按钮 end -->
		
	</div>
</body>
</html>