<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>商品出库</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/dealmanage/otherout.js");%> charset="UTF-8" type="text/javascript"></script>

</head>
<body>
	<div id="List_panel" class="wrapper" data-options="closed:false">
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
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="add()">新增</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="commit()">确认出库</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="doPrint()">打印</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="doExport()">导出</a>
				</div>
			</div>
		</div>
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:260px;">
			<s class="s"><i class="i"></i></s>
			<h3>
				<span>查询</span> <a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<input id="tddate" type="radio" name="seledate" checked />
				<label style="text-align:right; width: 70px;">领取日期：</label> 
				<input id="begdate" name="begdate" type="text" class="easyui-datebox" 
						 data-options="width:137,height:27" />  - 
				<input id="enddate" name="enddate" type="text" class="easyui-datebox" 
						data-options="width:137,height:27" />
			</div>
			<div class="time_col time_colp10">
				<input id="kkdate" type="radio" name="seledate" />
				<label style="text-align:right; width: 70px;">确认日期：</label> 
				<input id="bperiod" name="bperiod" type="text" class="easyui-datebox" 
						 data-options="width:137,height:27" />  - 
				<input id="eperiod" name="eperiod" type="text" class="easyui-datebox" 
						data-options="width:137,height:27" />
			</div>
			<div class="time_col time_colp10">
				<label style="text-align:right;width: 85px;">单据编码：</label> 
				<input id="ucode"  name="ucode" class="easyui-textbox" style="width: 286px; height: 28px;"/>  
			</div>
			<div class="time_col time_colp10">
				<label style="text-align:right;width:85px;">录入人：</label> 
				<input id="uid" name="uid" class="easyui-combobox" style="width:286px;height:28px;text-align:left"
					data-options="required:false,valueField:'id',textField:'name',panelHeight:200" />  
			</div>
			<div class="time_col time_colp10">
				<label style="text-align:right;width: 85px;">单据状态：</label> 
				<select id="qtype" name="qtype" class="easyui-combobox"  style="width: 286px; height: 28px;"
					data-options="panelHeight:'auto'" >
					<option value="-1">全部</option>
					<option value="0">待确认</option>
					<option value="1">已确认</option>
				</select> 
			</div>		
			<p>
				<a class="ui-btn save_input" id="cleanbtn" onclick="clearCondition();">清除</a> 
				<a class="ui-btn save_input" onclick="reloadData()">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		<div id="cardDialog" class="easyui-dialog" data-options="closed:true" modal=true>
			<form id="stockout" method="post" style="height:320px;width:880px;overflow:hidden;padding-top:18px;">
				<div class="time_col time_colp11" style="display:none">
					<input id="soutid" name="soutid" class="easyui-textbox"> 
					<input id="nmny" name="nmny" class="easyui-numberbox"> 
					<input id="updatets" name="updatets" class="easyui-textbox"> 
				</div>
				<div class="time_col time_colp11">
					<div style="width:30%;display: inline-block;">
						<label style="text-align:right;width:35%;">领取日期：</label> 
						<input id="getdate"  name="getdate" class="easyui-datebox" style="width:60%;height:28px;text-align:left"
							data-options="required:true"/>  
					</div>
					<div style="width:60%;display: inline-block;">
						<label style="text-align:right;width:35%;">事项：</label> 
						<input id='memo'  name="memo" class="easyui-textbox"   style="width:60%;height:28px;text-align:left"
							data-options="validType:'length[0,50]',required:true" ></input>
					</div>
				</div>
				<div class="grid-wrap" >
					<table id="cardGrid"></table>
				</div> 
				<div style="float:right;margin-top:20px;margin-right:20px;">
				    <a href="javascript:void(0)" id="addSave" class="ui-btn ui-btn-xz" onclick="addSave()">确定</a> 
					<a href="javascript:void(0)" id="addCancel" class="ui-btn ui-btn-xz"  onclick="addCancel()">取消</a>
				</div>
			</form>
		</div>
	</div>
</body>
</html>