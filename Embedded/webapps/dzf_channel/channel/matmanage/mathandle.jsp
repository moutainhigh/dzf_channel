<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>物料处理</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/matmanage/mathandle.js");%> charset="UTF-8" type="text/javascript"></script>
<script src="<%=request.getContextPath()%>/jslib/jquery-easyui-1.4.3/datagrid-detailview.js" charset="UTF-8" type="text/javascript"></script>
<style>
#mat_add div.panel.datagrid {
	margin-left: 132px;
	margin-top: -21px;
}
#infoform div.panel.datagrid {
	margin-left: 132px;
	margin-top: -21px;
}
</style>	
</head>
<body>
	<!-- 列表界面 begin -->
	
	<div id="listPanel" class="wrapper" style="width: 100%;overflow:hidden; height: 100%;">
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
				<div class="left mod-crumb">
					<div style="margin:6px 0px 0px 10px;float:right;font-size:14px;">
						<a href="javascript:void(0)"  style="font-size:14;color:blue;" onclick="load(3)">待发货</a>
					</div>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="send()">发货</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="doExport()">导出</a>
				</div>
			</div>
		</div>
		
		<!-- 查询对话框 begin -->
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:263px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<input id="rq" type="radio"  name="seledate" checked="true" value="lr" />
				<label style="text-align:right; width: 70px;">录入日期：</label> 
				<input id="begdate" name="begdate" type="text" class="easyui-datebox" 
						 data-options="width:137,height:27" />  - 
				<input id="enddate" name="enddate" type="text" class="easyui-datebox" 
						data-options="width:137,height:27" />
			</div>
			<div class="time_col time_colp10">
				<input id="qj" type="radio" name="seledate" value="app" />
				<label style="text-align:right; width: 70px;">申请日期：</label> 
				<input id="bperiod" name="bperiod" type="text" class="easyui-datebox" 
						 data-options="width:137,height:27" />  - 
				<input id="eperiod" name="eperiod" type="text" class="easyui-datebox" 
						data-options="width:137,height:27" />
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">加盟商：</label>
				<input id="qcorpname" class="easyui-textbox" style="width:286px;height:28px;"/>
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">单据状态：</label>
				<!-- 单据状态：全部、待审核、待发货、已发货、已驳回 -->
				<select id="status" class="easyui-combobox" data-options="panelHeight:'auto'" style="width:282px;height:28px;">
					<option value="0">全部</option>
					<!-- <option value="1">待审核</option> -->
					<option value="2">待发货</option>
					<option value="3">已发货</option>
					<!-- <option value="4">已驳回</option> -->
				</select>
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">渠道经理：</label>
				<input id="uid" class="easyui-combobox" editable="false" style="width:286px;height:28px;text-align:left"
			       data-options="required:false,valueField:'id',textField:'name',multiple:false,panelHeight:200" /> 
			</div>
			<p>
				<a class="ui-btn save_input" onclick="clearParams()">清除</a>
				<a class="ui-btn save_input" onclick="load(1)">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		<!-- 查询对话框end -->
		
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		<!-- 新增对话框  begin-->
		<div id="cbDialog" class="easyui-dialog" style="height:440px;width:680px;overflow:auto;padding-top:18px;"
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<form id="mat_add" method="post">
				<input id="matbillid" name="matbillid" type="hidden">
				<input id="updatets" name="updatets" type="hidden">
				<input id="type" name="type" type="hidden">
				<input id="debegdate" name="debegdate" type="hidden">
				<input id="deenddate" name="deenddate" type="hidden">
				<div class="time_col time_colp10">
						<label style="text-align:right; width: 124px;">合同编号：</label> 
						<input id="code" name="code" class="easyui-textbox" style="width:168px;height:28px;"/>
			            <span class="hid">
			            <label style="text-align:right; width: 85px;">状态：</label> 
						<input id="stat" class="easyui-textbox" style="width:163px;height:28px;"/>
			            </span>
			    </div>
			    <div class="time_col time_colp10">
						<label style="width:124px;text-align:right"><i class="bisu">*</i>加盟商：</label> 
						<input id="corpnm" name="corpname" class="easyui-textbox" style="width:431px;height:28px;" data-options="required:true"/>
						<input id="fcorp" name="fcorp" type="hidden">
				</div>
				<div class="time_col time_colp10">
						<label style="width:124px;text-align:right"><i class="bisu">*</i>所在地区：</label> 
						<input id="pname" name="pname" class="easyui-combobox" style="width:141px;height:28px;"
				           editable="false" data-options="valueField:'vprovince', textField:'pname', panelHeight:'200',prompt:'请选择省'" /> 
				        <input id="vprovince" name="vprovince" type="hidden">
				        <input id="cityname" name="cityname" class="easyui-combobox" style="width:141px;height:28px;"
				           editable="false" data-options="valueField:'vcity', textField:'cityname', panelHeight:'200',prompt:'请选择市'" />
				        <input id="vcity" name="vcity" type="hidden">
				        <input id="countryname" name="countryname" class="easyui-combobox" style="width:141px;height:28px;"
				           editable="false" data-options="valueField:'varea', textField:'countryname', panelHeight:'200',prompt:'请选择区/县'" />
				         <input id="varea" name="varea" type="hidden">
				</div>
				<div class="time_col time_colp10">
				       <label style="text-align:right; width: 124px;"><i class="bisu">*</i>详细地址：</label> 
				       <!-- <textarea id="address" name="address" class="easyui-validatebox" style="width: 432px;height:51px;"  data-options="required:true"></textarea> -->
			           <input id="address" name="address" class="easyui-textbox" style="width:431px;height:28px;" data-options="required:true" />
				</div>
				<div class="time_col time_colp10">
					   <label style="width:124px;text-align:right"><i class="bisu">*</i>收货人：</label> 
					   <input id="receiver" name="receiver" class="easyui-textbox" style="width:167px;height:28px;" data-options="required:true"/>
					   <label style="width:85px;text-align:right"><i class="bisu">*</i>联系电话：</label> 
					   <input id="phone" name="phone" class="easyui-textbox" style="width:167px;height:28px;" data-options="required:true"/>
				</div>
				<div class="time_col time_colp10">
						<label style="width:124px;text-align:right"><i class="bisu">*</i>物料选择：</label>
					    <div id="cardGrid" style="width:432px;height:70px;
							margin-left:94px;margin-top:-21px;">
						</div>
				</div>
               	<div class="time_col time_colp10">
						<label style="text-align:right; width: 124px;">备注：</label>
						<!-- <textarea id="memo" name="memo" class="easyui-validatebox" style="width: 432px;height:51px;"  placeholder="最多可输入200个字"></textarea> -->
				        <input id="memo" name="memo" class="easyui-textbox" style="width:431px;height:28px;"  data-options="prompt:'最多可输入200个字'"/>
				</div>
				<div class="time_col time_colp10">
					   <label style="width:124px;text-align:right">申请人：</label> 
					   <input id="applyname" name="applyname" class="easyui-textbox" style="width:168px;height:28px;"/>
					   <label style="width:87px;text-align:right">申请时间：</label> 
					   <input id="adate" name="adate" type="text" class="easyui-datebox" 
						 data-options="width:163,height:27" />  
				</div>
				<div class="time_col time_colp10">
						<label style="width:124px;text-align:right">审核人：</label>
						<input id="audname" name="audname" class="easyui-textbox" style="width:168px;height:28px;"/>
						<label style="width:87px;text-align:right">审核时间：</label>
						<input id="audate" name="audate" type="text" class="easyui-datebox"
							   data-options="width:163,height:27" />
					</div>
				<div class="time_col time_colp10">
					 	<label style="text-align:right; width: 124px;"><i class="bisu">*</i>快递公司：</label>
					 	<input id="logname" name="logname" class="easyui-combobox" style="width:166px;height:28px;"
				           editable="false" data-options="required:true,valueField:'logid', textField:'logname', panelHeight:'200'" />  
						<input id="logid" name="logid" type="hidden">
					
			            <label style="text-align:right; width: 85px;"><i class="bisu">*</i>金额：</label> 
						<input id="fcost" name="fcost" class="easyui-numberbox" style="width:166px;height:28px;" 
							data-options="required:true"/>
				</div>
				<div class="time_col time_colp10">
					   <label style="width:124px;text-align:right"><i class="bisu">*</i>快递单号：</label> 
					   <input id="fcode" name="fcode" class="easyui-textbox" style="width:166px;height:28px;" 
					 	   data-options="required:true"/>
					   <label style="width:85px;text-align:right">发货时间：</label> 
					   <input id="dedate" name="dedate" type="text" class="easyui-datebox" 
						   data-options="width:166,height:27" />  
				</div>
				<div class="time_col time_colp10">
					  <label style="width:124px;text-align:right">发货人：</label> 
				      <input id="dename" name="dename" class="easyui-textbox" style="width:166px;height:28px;"/>
				</div>
				  
				<div style="float:right;margin-top:2px;margin-right:293px;">
				    <span class="sid">
				    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onSave()">保存</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onCancel()">取消</a>
				    </span>
				</div>
			</form>
		</div>
		<!-- 新增对话框  end-->
		
		
		<!-- 详情对话框  begin-->
		<div id="infoDialog" class="easyui-dialog" style="height:530px;width:680px;overflow:auto;padding-top:18px;"
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<form id="infoform" method="post">
				<input id="imatbillid" name="matbillid" type="hidden">
				<input id="iupdatets" name="updatets" type="hidden">
				<input id="itype" name="type" type="hidden">
				<input id="idebegdate" name="debegdate" type="hidden">
				<input id="ideenddate" name="deenddate" type="hidden">
				<div class="time_col time_colp10">
						<label style="text-align:right; width: 124px;">合同编号：</label> 
						<input id="icode" name="code" class="easyui-textbox" style="width:168px;height:28px;"
							data-options="readonly:true"/>
			            <label style="text-align:right; width: 85px;">状态：</label> 
						<input id="istat" class="easyui-textbox" style="width:163px;height:28px;"
							data-options="readonly:true" />
			    </div>
			    <div class="time_col time_colp10">
						<label style="width:124px;text-align:right"><i class="bisu">*</i>加盟商：</label> 
						<input id="icorpnm" name="corpname" class="easyui-textbox" style="width:431px;height:28px;" 
							data-options="readonly:true" />
						<input id="ifcorp" name="fcorp" type="hidden">
				</div>
				<div class="time_col time_colp10">
						<label style="width:124px;text-align:right"><i class="bisu">*</i>所在地区：</label> 
						<input id="ipname" name="pname" class="easyui-textbox" style="width:141px;height:28px;"
				           data-options="readonly:true" /> 
				        <input id="ivprovince" name="vprovince" type="hidden">
				        <input id="icityname" name="cityname" class="easyui-textbox" style="width:141px;height:28px;"
				           data-options="readonly:true" />
				        <input id="ivcity" name="vcity" type="hidden">
				        <input id="icountryname" name="countryname" class="easyui-textbox" style="width:141px;height:28px;"
				           data-options="readonly:true" />
				        <input id="ivarea" name="varea" type="hidden">
				</div>
				<div class="time_col time_colp10">
				       <label style="text-align:right; width: 124px;"><i class="bisu">*</i>详细地址：</label> 
			           <input id="iaddress" name="address" class="easyui-textbox" style="width:431px;height:28px;" 
			           	data-options="readonly:true" />
				</div>
				<div class="time_col time_colp10">
					   <label style="width:124px;text-align:right"><i class="bisu">*</i>收货人：</label> 
					   <input id="ireceiver" name="receiver" class="easyui-textbox" style="width:167px;height:28px;" 
					   	data-options="readonly:true"/>
					   <label style="width:85px;text-align:right"><i class="bisu">*</i>联系电话：</label> 
					   <input id="iphone" name="phone" class="easyui-textbox" style="width:167px;height:28px;" 
					   	data-options="readonly:true"/>
				</div>
				<div class="time_col time_colp10">
						<label style="width:124px;text-align:right"><i class="bisu">*</i>物料选择：</label>
					    <div id="icardGrid" style="width:432px;height:70px;display:inline-block;"></div>
				</div>
               	<div class="time_col time_colp10">
						<label style="text-align:right; width: 124px;">备注：</label>
				        <input id="imemo" name="memo" class="easyui-textbox" style="width:431px;height:28px;"  
				         data-options="readonly:true"/>
				</div>
				<div class="time_col time_colp10">
					   <label style="width:124px;text-align:right">申请人：</label> 
					   <input id="iapplyname" name="applyname" class="easyui-textbox" style="width:168px;height:28px;"
					   		data-options="readonly:true"/>
					   <label style="width:87px;text-align:right">申请时间：</label> 
					   <input id="iadate" name="adate" type="text" class="easyui-datebox" style="width:168px;height:27px;"
						 data-options="readonly:true" />  
				</div>
				<div class="time_col time_colp10">
						<label style="width:124px;text-align:right">审核人：</label>
						<input id="audname" name="audname" class="easyui-textbox" style="width:168px;height:28px;"/>
						<label style="width:87px;text-align:right">审核时间：</label>
						<input id="audate" name="audate" type="text" class="easyui-datebox"
							   data-options="width:163,height:27" />
				</div>
				<div class="time_col time_colp10">
					 	<label style="text-align:right; width: 124px;"><i class="bisu">*</i>快递公司：</label>
					 	<input id="ilogname" name="logname" class="easyui-textbox" style="width:166px;height:28px;"
				           data-options="readonly:true" />  
						<input id="ilogid" name="logid" type="hidden">
					
			            <label style="text-align:right; width: 85px;"><i class="bisu">*</i>金额：</label> 
						<input id="ifcost" name="fcost" class="easyui-numberbox" style="width:166px;height:28px;" 
							data-options="readonly:true"/>
				</div>
				<div class="time_col time_colp10">
					   <label style="width:124px;text-align:right"><i class="bisu">*</i>快递单号：</label> 
					   <input id="ifcode" name="fcode" class="easyui-textbox" style="width:166px;height:28px;" 
					 	   data-options="readonly:true"/>
					   <label style="width:85px;text-align:right">发货时间：</label> 
					   <input id="idedate" name="dedate" type="text" class="easyui-datebox" style="width:166px;height:27px;" 
						   data-options="readonly:true" />  
				</div>
				<div class="time_col time_colp10">
					  <label style="width:124px;text-align:right">发货人：</label> 
				      <input id="idename" name="dename" class="easyui-textbox" style="width:166px;height:28px;"
				      	data-options="readonly:true"/>
				</div>
			</form>
		</div>
		<!-- 详情对话框  end-->
		
	</div>
	<!-- 列表界面 end -->
	
</body>
</html>