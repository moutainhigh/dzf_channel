<%@ page import="java.util.Calendar"%>
<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
<title>账务处理分析</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>
	rel="stylesheet">
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/report/repcommon.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/report/financeDealStateRep.js");%>
	charset="UTF-8" type="text/javascript"></script>
</head>
<%
	//获取当前月期间
	Calendar e = Calendar.getInstance();
	String ym = new SimpleDateFormat("yyyy-MM").format(e.getTime());
%>
<body class="dzf-skin">
	<div class="wrapper">
		<div id="List_panel" class="wrapper" style="height: 100%;" data-options="closed:false">
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
				  	<div class="right">
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz conn"
							data-options=" plain:true" onclick="reloadData()" id="">刷新</a> 
					</div>
		        </div>
		    </div>
		    
		    <div class="mod-inner">
				<div id="dataGrid" class="grid-wrap">
					<table id="grid"></table>
				</div>
			</div>
			
			<!-- 查询对话框 begin -->
		    <div class="qijian_box" id="qrydialog" style="display:none; width:480px; height:270px;">
				<s class="s"><i class="i"></i></s>
				<h3>
					<span>查询</span> <a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="text-align:right;width:100px;">截止日期：</label>
					<input type="text" id="qryperiod" class="easyui-textbox" data-options="editable:false"
						style="width:137px;height:30px;" value=<%=ym%> />
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width:100px;">大区：</label> 
					<input id="aname"  name="aname" class="easyui-combobox" style="width: 285px; height: 28px;" 
						data-options="required:false,valueField:'name',textField:'name',panelHeight:100" editable="false" />  
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width:100px;">省（市）：</label> 
					<input id="ovince"  name="ovince" class="easyui-combobox" style="width: 285px; height: 28px;" 
						data-options="required:false,valueField:'id',textField:'name',panelHeight:100" editable="false" />  
				</div>		
				<div class="time_col time_colp10">
					<label style="text-align:left;width:100px;">会计运营经理：</label> 
					<input id="uid" name="uid" class="easyui-combobox" style="width:285px;height:28px;text-align:left"
						data-options="required:false,valueField:'id',textField:'name',panelHeight:100" editable="false" />  
				</div>
				<div class="time_col time_colp10">
					<label style="text-align:right;width:100px">加盟商：</label>
					<input id="channel_select" class="easyui-textbox" style="width:285px;height:28px;" />
					<input id="pk_account" type="hidden">
				</div>
				<p>
					<a class="ui-btn save_input" id="cleanbtn" onclick="clearCondition();">清除</a> 
					<a class="ui-btn save_input" onclick="reloadData()">确定</a>
					<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
				</p>
			</div>
			<!-- 查询对话框 end -->
			
			<!-- 加盟商参照 begin -->
			<div id="kj_dialog"></div>
			<div id="kj_buttons" style="display:none;">
				<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
				<a href="javascript:void(0)" class="easyui-linkbutton" style="width:90px" 
					onclick="javascript:$('#kj_dialog').dialog('close');">取消</a>
			</div>
			<!-- 加盟商参照 end -->
			
			<!-- 明细begin -->
			<div id="detail_dialog" class="easyui-dialog" title="客户明细" 
				data-options="modal:true,closed:true" style="width:1000px;height:500px;">
				<div class="time_col" style="padding-top: 10px;width:96%;margin:0 auto;">
					<div style="display:inline">
						<label style="text-align:right">截止日期：</label> 
						<span id ="qrydate" style="vertical-align: middle;font-size:14px;"></span>
						<label style="text-align:right">加盟商：</label> 
						<span id ="corpnm" style="vertical-align: middle;font-size:14px;"></span>
						<input id="corpid" name="corpid" type="hidden">
					</div>
					
					<div style="display:inline;float:right;">
						<label style="width:85px;text-align:right;">记账状态：</label>
						<select id="jzstatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
							style="width:80px;height:28px;">
							<option value="-1">全部</option>
							<option value="1">未开始</option>
							<option value="2">进行中</option>
							<option value="3">已完成</option>
						</select>
						<label style="width:85px;text-align:right;">账务检查：</label>
						<select id="zwstatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
							style="width:80px;height:28px;">
							<option value="-1">全部</option>
							<option value="1">已关账</option>
							<option value="2">未关账</option>
						</select>
						
						<a class="ui-btn save_input" style="margin-bottom:0px;" onclick="queryDetail()">查询</a>
					</div>
				</div>	
				
				<div data-options="region:'center'" 
					style="overflow-x:auto; overflow-y:auto;margin: 0 auto;width:90%;height:380px;padding:10px">
					 <table id="gridh"></table>	
				</div>
			</div>
			<!-- 明细end -->
			
		</div>
	</div>
</body>
</html>
