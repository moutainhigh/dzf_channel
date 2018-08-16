<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>商品管理</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../../css/channelorder.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/dealmanage/channelorder.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<body>
	<div id="List_panel" class="wrapper" data-options="closed:false">
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
					<span class="cur"></span>
				</div>
				
				<div class="left mod-crumb">
					<div style="margin:4px 0px 0px 10px;float:left;font-size:14px;">
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; margin-right:15px;" 
							onclick="qryData()">待确认</a>
					</div>
				</div>
				
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="confirm()">确认</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="cancOrder()">取消订单</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="sendOut()">商品发货</a>
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
				<label style="width:85px;text-align:right">订单编码：</label>
				<input id="qbcode" class="easyui-textbox" style="width:290px;height:28px;"/>
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">加盟商：</label>
				<input id="qcpname" class="easyui-textbox" style="width:290px;height:28px;"/>
				<input id="qcpid" type="hidden">
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">订单状态：</label>
				<select id="qstatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
					editable="false" style="width:290px;height:28px;">
					<!-- 状态  0：待确认；1：待发货；2：已发货；3：已收货；4：已取消； -->
					<option value="-1">全部</option>
					<option value="0">待确认</option>
					<option value="1">待发货</option>
					<option value="2">已发货</option>
					<option value="3">已收货</option>
					<option value="4">已取消</option>
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
		
		<!-- 加盟商参照对话框begin -->
		<div id="chnDlg"></div>
		<!-- 加盟商参照对话框end -->
		
		<!-- 取消订单begin -->
		<div id="cancelDlg" class="easyui-dialog" style="width: 500px; height: 300px; padding-top: 20px; font-size: 14px;"
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
        	<div style="width: 80%; margin: 0 auto;">
				<div class="time_col time_colp11">
					<input id="reason1" name="reatype" type="radio" value="1" checked />
					<label style="width: 250px;" for="reason1">加盟商账户预付款余额不足</label>
				</div>
				<div class="time_col time_colp11">
					<input id="reason2" name="reatype" type="radio" value="2" />
					<label style="width: 250px;" for="reason2">商品缺货</label>
				</div>
				<div class="time_col time_colp11">
					<input id="reason3" name="reatype" type="radio" value="3" />
					<label style="width: 250px;" for="reason3">其它原因</label>
				</div>	
			   	<div class="time_col time_colp11">
			   		<form id="cancfrom" method="post">
						<div style="display: inline-block; margin-top: 5px;">
							<input type="text" class="easyui-textbox" id="reason" name="reason"
								data-options="multiline:true,validType:'length[0,50]',"
								style="width:380px; height:70px; display:none;">
						</div>
					</form>
				</div>
				<div style="text-align:center;margin-top:20px;">
				    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="cancSave()">保存</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="cancCancel()">取消</a>
				</div>
			</div>
		</div>
		<!-- 取消订单end -->
		
		<!-- 发货列表begin -->
		<div id="setOutDlg" class="easyui-dialog" style="width:1000px;height:450px;" data-options="closed:true">
			<table id="sgrid" style="height:85%;"></table>
			<div style="text-align:center;margin-top:20px;">
			    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="sendSave()">保存</a> 
				<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="sendCancel()">取消</a>
			</div>
		</div>
		<!-- 发货列表end -->

		<!-- 订单详情begin -->
		<div id="infoDlg" class="easyui-dialog" style="width: 830px; height: 530px; padding-top: 10px; font-size: 14px;"
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<div class="shift">
				<div style="margin-bottom: 20px;">
					<div style="width: 10%; display: inline-block">
						<div class="shop_pick">订单信息</div>
					</div>
					<div class="order">
						<label>订单编号：</label> <span id="billcode"></span>
					</div>
					<div class="order">
						<label>加盟商：</label> <span id="pname"></span>
					</div>
				</div>
				<!-- 订单详情 begin -->
				<div id="detail"></div>
				<!-- 订单详情 end -->
				
				<!-- 快递信息begin -->
				<div id="fast"></div>
				<!-- 快递信息end -->
				
				<div class="icon">
					<div class="shop_pick">收货信息</div>
				</div>
				<div style="margin-left: 40px;">
					<span id="receinfo"></span>
				</div>
				<div class="icon">
					<div class="shop_pick">已选商品</div>
				</div>
				<!-- 商品详情 begin -->
				<div id="goods"></div>
				<!-- 商品详情 end -->
			</div>
			<div class="goodsbuy_foot">
				<div class="shop_cut_left">
				    实付款：¥<span id="ndesummny"></span>
				</div>
				<div class="shop_cut_right">
					(包含返点¥<span id="nderebmny"></span>)
				</div>
			</div>
		</div>
		<!-- 订单详情end -->

	</div>
</body>
</html>