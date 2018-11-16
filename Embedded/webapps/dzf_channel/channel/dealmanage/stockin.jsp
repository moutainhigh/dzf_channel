<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>入库单</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/dealmanage/stockin.js");%> charset="UTF-8" type="text/javascript"></script>

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
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="confirm()">确认入库</a>
				</div>
			</div>
		</div>
		
		<!-- 查询对话框 begin -->
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:190px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<label style="text-align:right; width: 70px;">入库日期：</label> 
				<input id="begdate" name="begdate" type="text" class="easyui-datebox" 
						 data-options="width:137,height:27" />  - 
				<input id="enddate" name="enddate" type="text" class="easyui-datebox" 
						data-options="width:137,height:27" />
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">单据编码：</label>
				<input id="qvcode" class="easyui-textbox" style="width:290px;height:28px;"/>
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">录入人：</label>
				<input id="uid" name="uid" class="easyui-combobox" style="width:295px;height:28px;text-align:left"
					data-options="required:false,valueField:'uid',textField:'uname',panelHeight:200" /> 
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
	
	<!-- 卡片界面 begin -->
	<div id="cardPanel" style="height:100%; width: 100%; overflow:auto;">
		<div class="mod-toolbar-top">
	        <div class="mod-toolbar-content">
		        <div class="right">
					<a href="javascript:void(0)" id="savebtn" class="ui-btn ui-btn-xz" onclick="onSave()">保存</a>
		        	<a href="javascript:void(0)" id="cancbtn" class="ui-btn ui-btn-xz" onclick="showList()">返回</a>
		        </div> 
	        </div>
  		</div>
  		
  		<div class="mod-inner">
  			<div id="mainId" style="height: 81%; width:100%;">
  				<form id="stform" method="post" enctype="multipart/form-data" style="min-width:1150px;padding-top:18px;">
  					<input type="hidden" name="stid" id="stid">
  					<input type="hidden" name="updatets" id="updatets">
  					<input type="hidden" name="vcode" id="vcode">
  					
  					<input type="hidden" name="operid" id="operid">
  					<input type="hidden" name="opertime" id="opertime">
  					<input type="hidden" name="status" id="status">
  					<input type="hidden" name="corpid" id="corpid">
					<div class="time_col time_colp11">
					    <div style="width: 25%;display: inline-block;">
							<label style="text-align:right;width:35%;">入库日期：</label>
							<input type="text" id="stdate" name="stdate" class="easyui-datebox" 
								style="width:40%;height:28px;"  data-options="required:true" />
					    </div>
					    <div style="width: 25%;display: inline-block;">
							<label style="text-align:right;width: 35%;">总金额：</label>
							<input id="totalmny" name="totalmny" class="easyui-numberbox" 
								data-options ="readonly:true,min:0,precision:2,groupSeparator:',' "  
								style="width:40%;height:28px;text-align:left"   />
					    </div>
					</div>
					<div id="dataGrid" style="width:100%;display:inline-block;">
						<table id="stgrid"></table>
				    </div>
					
				</form>
  			</div>
  		</div>
		
	</div>
	<!-- 卡片界面 end -->
	
	<!-- 供应商参照 -->
	<div id = "refdiv"></div> 
	
	<!-- 计量单位对话框  begin-->
	<div id="supDlg" class="easyui-dialog" style="width:400px;height:220px;padding-top:30px;" 
		data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
		<form id="supForm" method="post">
			<div class="time_col time_colp11">
				<div style="display: inline-block;">
					<label style="text-align:right;width:140px;">供应商名称：</label>
					<input id="vmemo" name="memo" class="easyui-textbox" 
						data-options="validType:'length[0,50]'" style="width:150px;height:25px;"/>
				</div>
			</div>
		</form>
		<div style="text-align:center;margin-top:40px;">
		    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="supSave()">保存</a> 
			<a href="javascript:void(0)"  class="ui-btn ui-btn-xz" onclick="supCancel()">取消</a>
		</div>
	</div>
	<!-- 计量单位对话框  end-->
	
</body>
</html>