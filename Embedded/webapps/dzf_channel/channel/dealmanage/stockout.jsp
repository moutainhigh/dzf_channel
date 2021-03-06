<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>商品出库</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/dealmanage/stockout.js");%> charset="UTF-8" type="text/javascript"></script>

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
				<div class="left mod-crumb">
					<div style="margin:6px 0px 0px 10px;float:right;font-size:14px;">
						<a href="javascript:void(0)"  style="font-size:14;color:blue;" onclick="loadData(0)">待确认</a>
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; " onclick="loadData(1)">待发货</a>
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px;margin-right:15px; " onclick="loadData(2)">已发货</a>
					</div>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="add()">新增</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="commit()">确认出库</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="doPrint()">打印</a>
				</div>
			</div>
		</div>
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:280px;">
			<s class="s"><i class="i"></i></s>
			<h3>
				<span>查询</span> <a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="sel_time time_col">
				<label style="text-align:right">录入日期：</label> 
				<input id="begdate" name="begdate"  class="easyui-datebox" data-options="width:137,height:28,validType:'checkdate'" />
				<font>-</font> 
				<input id="enddate" name="enddate"  class="easyui-datebox" data-options="width:137,height:28,validType:'checkdate'" />
			</div>
			<div class="time_col time_colp10">
				<label style="text-align:right;width: 70px;">加盟商：</label> 
				<input id="cpid"  name="cpid" class="easyui-combobox" style="width: 295px; height: 28px;" 
					data-options="required:false,valueField:'pk_gs',textField:'uname',panelHeight:200"/>  
			</div>
			<div class="time_col time_colp10">
				<label style="text-align:right;width: 70px;">单据编码：</label> 
				<input id="ucode"  name="ucode" class="easyui-textbox" style="width: 295px; height: 28px;" />  
			</div>		
			<div class="time_col time_colp10">
				<label style="text-align:right;width:70px;">录入人：</label> 
				<input id="uid" name="uid" class="easyui-combobox" style="width:295px;height:28px;text-align:left"
					data-options="required:false,valueField:'id',textField:'name',panelHeight:200" />  
			</div>
			<div class="time_col time_colp10">
				<label style="text-align:right;width:70px;">大区：</label> 
				<input id="aname"  name="aname" class="easyui-combobox" style="width:90px; height: 28px;" 
					data-options="required:false,valueField:'name',textField:'name',panelHeight:100" editable="false" /> 
				<label style="width: 80px;text-align:right">渠道运营：</label>
				<input id="operater" class="easyui-textbox" style="width:110px;height:28px;" />
				<input id="operaterid" type="hidden">
			</div>
			<p>
				<a class="ui-btn save_input" id="cleanbtn" onclick="clearCondition();">清除</a> 
				<a class="ui-btn save_input" onclick="reloadData()">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		
		<div id="cardDialog" class="easyui-dialog" data-options="closed:true" modal=true >
			<form id="stockout" method="post" style="height:440px;width:1100px;overflow:hidden;padding-top:18px;">
				<div class="time_col time_colp11" style="display:none">
					<input id="soutid" name="soutid" class="easyui-textbox"> 
					<input id="nmny" name="nmny" class="easyui-numberbox"> 
					<input id="updatets" name="updatets" class="easyui-textbox"> 
				</div>
				<div class="time_col time_colp11">
					<div style="width:32%;display: inline-block;">
						<label style="text-align:right;width:35%;">加盟商：</label> 
						<input id="corpid"  name="corpid" class="easyui-combobox" style="width:60%;height:28px;text-align:left"
							data-options="required:true,panelHeight:200"/>  
					</div>
					<div style="width:32%;display: inline-block;">
						<label style="text-align:right;width:35%;">物流公司：</label> 
						<input id='logunit'  name="logunit" class="easyui-textbox"   style="width:60%;height:28px;text-align:left"
							data-options="validType:'length[0,70]',readonly:true" ></input>
					</div>
					<div style="width:32%;display: inline-block;" >
						<label style="text-align:right;width:35%;">物流单号：</label> 
						<input id="fcode" name="fcode" class="easyui-textbox" style="width:60%;height:28px;text-align:left;"
							data-options="validType:'length[0,50]',readonly:true"> 
					</div>
				</div>
				<div class="time_col time_colp11">
					<div style="width:100%;display: inline-block;">
						<label style="text-align:right;width:11.2%;vertical-align:middle;">备注：</label>
						<textarea id="memo" class="easyui-textbox" name="memo" data-options="multiline:true,validType:'length[0,50]'" style="width:84%;height:40px;"></textarea>
					</div>
				</div>
				<div class="time_col time_colp11">
					<div style="width:32%;display: inline-block;">
						<label style="text-align:right;width:35%;">收货人：</label> 
						<input id="rename"  name="rename" class="easyui-textbox" style="width:60%;height:28px;text-align:left"
							data-options="required:true"/>  
					</div>
					<div style="width:32%;display: inline-block;">
						<label style="text-align:right;width:35%;">联系方式：</label> 
						<input id='phone'  name="phone" class="easyui-textbox"   style="width:60%;height:28px;text-align:left"
							data-options="validType:'phone[\'#p1\']',required:true" ></input>
					</div>
					<div style="width:32%;display: inline-block;" >
						<label style="text-align:right;width:35%;">邮政编码：</label> 
						<input id="recode" name="recode" class="easyui-textbox" style="width:60%;height:28px;text-align:left;"
							data-options="validType:'length[0,10]'"> 
					</div>
				</div>
				<div class="time_col time_colp11">
					<div style="width:100%;display: inline-block;">
						<label style="text-align:right;width:11.2%;vertical-align:middle;">收货地址：</label>
						<textarea id="readdress" class="easyui-textbox" name="readdress" data-options="multiline:true,validType:'length[0,100]',required:true" style="width:84%;height:40px;"></textarea>
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
		
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		<div id="logDialog" class="easyui-dialog" style="width:400px;height:240px;padding-top:30px;" data-options="closed:true" modal=true>
			<form id="logUpdate" method="post">
				<div class="time_col time_colp11">
					<div style="display: inline-block;">
						<label style="text-align:right;width:140px;">物流公司</label>
						<input id="slogid"  name="logid" class="easyui-combobox" style="width:150px;height:25px;" 
							data-options="valueField:'id',textField:'name',panelHeight:150"/>  
					</div>
				</div>
				<div class="time_col time_colp11">
					<label style="text-align:right;width:140px;">物流单号</label>
						<input id="sfcode" name="fcode" class="easyui-textbox" 
							data-options="validType:'length[0,20]'" style="width:150px;height:25px;"/>
				</div>
				<div class="time_col time_colp11">
					<label style="text-align:right;width:140px;">金额</label>
						<input id="sfcost" name="fcost" class="easyui-numberbox" 
							data-options="precision:2,groupSeparator:',',min:0" style="width:150px;height:25px;"/>
				</div>
			</form>
			<div style="text-align:center;margin-top:30px;">
			    <a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="logCommit()">确定</a> 
				<a href="javascript:void(0)"  class="ui-btn ui-btn-xz" onclick="logCancel()">取消</a>
			</div>
		</div>
		<div id="billDialog"></div>
		
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