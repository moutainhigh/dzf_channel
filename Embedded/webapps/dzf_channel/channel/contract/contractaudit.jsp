<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>合同审核</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>
	rel="stylesheet">
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/contractaudit.css");%>
	rel="stylesheet">
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/contract/contractaudit.js");%>
	charset="UTF-8" type="text/javascript"></script>
<style type="text/css">
</style>
</head>
<% 
	String cuserid = (String) session.getAttribute(IGlobalConstants.login_user);
%>
<body>
	<input id="uid" name="uid" type="hidden" value=<%= cuserid %>> 
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
							onclick="qryData()">待审核</a>
					</div>
				</div>
				
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="audit()">审核</a>
				</div>
			</div>
		</div>
		
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
	</div>
	
	<!-- 查询对话框 begin -->
	<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:290px">
		<s class="s" style="left: 25px;"><i class="i"></i> </s>
		<form id="query_form">
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">申请日期：</label>
				<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" 
					data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
				<font>-</font>
				<font><input name="edate" type="text" id="edate" class="easyui-datebox" 
					data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">申请类型：</label>
				<input id="qnullify" type="checkbox" checked 
					style="width:20px;height:28px;text-align:left;margin-left:2px;"/>
				<label style="width:40px;text-align:left">作废</label> 
				<input id="qstop" type="checkbox" checked 
					style="width:20px;height:28px;text-align:left;margin-left:10px;"/>
				<label style="width:40px;text-align:left">终止</label> 
				<input id="unroutine" type="checkbox" checked 
					style="width:20px;height:28px;text-align:left;margin-left:20px;"/>
				<label style="width:80px;text-align:left">非常规套餐</label>
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">申请状态：</label>
				<select id="qapstatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
					style="width:105px;height:28px;">
					<!-- 申请状态  1：渠道待审（未处理）；2： 区总待审（处理中）；3：总经理待审（处理中）；4：运营待审（处理中）；5：已处理；6：已拒绝； -->
					<option value="-1">全部</option>
					<option value="1">渠道待审</option>
					<option value="2">区总待审</option>
					<option value="3">总经理待审</option>
					<option value="4">运营待审</option>
					<option value="5">已处理</option>
					<option value="6">已拒绝</option>
				</select>
				<label style="width:88px;text-align:right">纳税人资格：</label>
				<select id="qchname" class="easyui-combobox" data-options="panelHeight:'auto'" 
					style="width:80px;height:28px;">
					<option value="-1">全部</option>
					<option value="1">小规模</option>
					<option value="2">一般人</option>
				</select>
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">加盟商：</label>
				<input id="channel_select" class="easyui-textbox" style="width:284px;height:28px;"/>
				<input id="pk_account" type="hidden">
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">客户：</label>
				<input id="corpkna_ae" class="easyui-textbox" style="width:284px;height:28px;"/>
				<input id="corpkid_ae" name="corpkid" type="hidden"> 
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">渠道经理：</label>
				<input id="manager" class="easyui-textbox" style="width:284px;height:28px;" />
				<input id="managerid" type="hidden">
			</div>
		</form>
		<p>
			<a class="ui-btn save_input" onclick="clearParams()">清除</a>
			<a class="ui-btn save_input" onclick="reloadData()">确定</a>
			<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
		</p>
	</div>
	<!-- 查询对话框end -->
	
	<!-- 查询-加盟商参照 begin -->
	<div id="chnDlg"></div>
	<div id="chnBtn" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#chnDlg').dialog('close');" style="width:90px">取消</a>
		</div>
	<!-- 查询-加盟商参照 end -->
	
	<!-- 查询-客户参照begin -->
	<div id="gs_dialog"></div>
	<!-- 查询-客户参照end -->
	
	<!-- 渠道经理参照对话框及按钮 begin -->
	<div id="manDlg"></div>
	<div id="manBtn" style="display:none;">
		<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectMans()" style="width:90px">确认</a> 
		<a href="javascript:void(0)" class="easyui-linkbutton" 
			onclick="javascript:$('#manDlg').dialog('close');" style="width:90px">取消</a>
	</div>
	<!-- 渠道经理参照对话框及按钮 end -->
	
	<!-- 变更审核 begin -->
	<div id="deduct_Dialog" class="easyui-dialog" style="width:1160px;height:90%;background:#FFF" data-options="closed:true">
		<div class="time_col time_colp11  Marketing" style="margin:0 auto;height:50px;line-height:50px;background:#eff1f2;">
			<div class="decan decan-top" style="width:50%;display: inline-block;margin-left:16px;">
				<input id="salespromot" name="salespromot" class="easyui-textbox" data-options="readonly:true" 
					style="width:68%;height:50px;text-align:left; ">
			</div>
			<div style="width:14%;display: inline-block;float: right;">
				<a class="ui-btn ui-btn-xz" onclick="deductConfri()">确定</a>
				<a class="ui-btn ui-btn-xz" onclick="deductCancel()">取消</a>
			</div>
		</div>
		<div style="height:92%; overflow: auto;">
			<div class="time_col time_colp11 " style="margin-top:10px;">
				<div class="decan strong Marketing" style="width:22%;display: inline-block;">
					<label style="width:40%;text-align: right;font-weight: bold;">预付款余额：</label>
					<input id="balmny" name="balmny" class="easyui-numberbox" style="width:50%;height:28px;text-align:left;"
						data-options="readonly:true,precision:2,groupSeparator:','" >
				</div>
				<div class="decan strong Marketing" style="width:22%;display: inline-block;">
					<label style="width:40%;text-align: right;font-weight: bold;">返点余额：</label>
					<input id="rebbalmny" name="rebbalmny" class="easyui-numberbox" style="width:50%;height:28px;text-align:left;"
						data-options="readonly:true,precision:2,groupSeparator:','" >
				</div>
				<div class="decan strong" style="width:28%;display: inline-block;">
					<label style="width:27%;text-align: right;font-weight: bold;">加盟商：</label>
					<input id="corpnm" name="corpnm" class="easyui-textbox" data-options="readonly:true" 
						style="width:60%;height:28px;text-align:left; ">
				</div>
				<div class="decan strong" style="width:25%;display: inline-block;">
					<label style="width:35%;text-align: right;font-weight: bold;">加盟商类型：</label>
					<input id="corptp" name="corptp" class="easyui-textbox" data-options="readonly:true" 
						style="width:60%;height:28px;text-align:left; ">
				</div>
			</div>
			<div class="time_col time_colp11 " style="margin-top:10px;">
				<div class="decan strong" style="width:22%;display: inline-block;">
					<label style="width:40%;text-align: right;font-weight: bold;">合同金额：</label>
					<input id="hntlmny" name="hntlmny" class="easyui-numberbox" style="width:50%;height:28px;text-align:left;"
						data-options="readonly:true,precision:2,groupSeparator:','" >
				</div>
				<div id="issupple" class="decan strong" style="width:22%;display:inline-block;font-weight:bold;">
					<label style="width:40%;text-align: right;">变更日期：</label>
					<input id="scperiod" name="cperiod" class="easyui-textbox" data-options="readonly:true" 
						style="width:55%; height:28px; text-align:left;">
				</div>
			</div>
			
			<!-- 原合同信息begin -->
			<div id = "oldinfo" style="height:0;">
				<form id = "oldfrom" method="post">
					<div class="time_col time_colp11 heading">
						<label style="width: 70px;text-align:center;color:#FFF;font-weight: bold;">原合同信息</label>
					</div>
					<div class="time_col time_colp11 ">
						<div class="decan" style="width:48.4%;display: inline-block;">
							<label style="width:20%;text-align: right;">客户名称：</label>
							<input id="ocorpkna" name="corpkna" class="easyui-textbox" data-options="readonly:true" 
								style="width:76%;height:28px;text-align:left;">
						</div>
						<div class="decan" style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">签订日期：</label>
							<input id="osigndate" name="signdate" class="easyui-datebox"  data-options="readonly:true" 
								style="width:56%;height:28px;text-align:left">
						</div>
						<div class="decan" style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">业务类型：</label>
							<input id="otypeminm" name="typeminm" class="easyui-textbox" data-options="readonly:true"
								style="width:56%;height:28px;text-align:left;">
						</div>
					</div>
					<div class="time_col time_colp11 ">
						<div class="decan" style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">纳税人资格：</label>
							<input id="ochname" name="chname" class="easyui-textbox" data-options="readonly:true"
								style="width:56%;height:28px;text-align:left;">
						</div>
						<div class="decan" style="width:24%;display: inline-block;">
							<label style="width:40%;    text-indent: 15px;">代账费(元/月):</label>
							<input id="onmsmny" name="nmsmny" class="easyui-numberbox"  
								data-options="readonly:true,precision:2,groupSeparator:','"
								style="width:56%;height:28px;text-align:left;">
						</div>
						<div class="decan" style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">合同金额：</label>
							<input id="ontlmny" name="ntlmny" class="easyui-numberbox"  
								data-options="readonly:true,precision:2,groupSeparator:','"
								style="width:56%;height:28px;text-align:left;">
						</div>
						<div class="decan" style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">账本费：</label>
							<input id="onbmny" name="nbmny" class="easyui-numberbox"  
								data-options="readonly:true,precision:2,groupSeparator:','"
								style="width:30%;height:28px;text-align:left"></input>
						</div>
					</div>
					<div class="time_col time_colp11 ">
						<div class="decan" style="width:24%;display:inline-block;">
							<label style="width:40%;text-align: right;">合同周期(月)：</label>
							<input id="ocontcycle" name="contcycle" class="easyui-textbox" data-options="readonly:true" 
								style="width:56%; height: 28px; text-align:left;">
						</div>
						<div class="decan" style="width:24%;display:inline-block;">
							<label style="width:40%;text-align: right;">收款周期(月)：</label>
							<input id="orecycle" name="recycle" class="easyui-textbox" data-options="readonly:true" 
								style="width: 56%; height: 28px; text-align:left;">
						</div>
							<div class="decan" style="width:24%;display:inline-block;">
							<div class="time_col">
								<label style="width: 109px;text-align: right;">服务期限：</label> 
								<input type="text" id="obperiod" name="bperiod" class="easyui-textbox" data-options="readonly:true"
									style="width:70px; height: 28px; " >-
								<input type="text" id="oeperiod" name="eperiod" class="easyui-textbox" data-options="readonly:true"
								 	style="width:70px; height: 28px;">
							</div>
						</div>
						<!-- <div class="decan" style="width:24%;display:inline-block;">
							<label for="isnconfirm" style="text-align:right;width:57%;">
								<input type="checkbox" id="oisnconfirm" name="isnconfirm" value="是" onclick="return false;">&nbsp;未确定服务期限
							</label>
						</div> -->
					</div>
					<div class="time_col time_colp11 ">
						<div class="decan" style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">合同编号：</label>
							<input id="ovccode" name="vccode"  class="easyui-textbox" data-options="readonly:true" 
								style="width:56%;height:28px;text-align:left;">
						</div>
						<div class="decan" style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">存量客户：</label>
							<input type="checkbox" id="oisncust" name="isncust" value="是" onclick="return false;">
						</div>
						<div class="decan" style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">提单日期：</label>
							<input id="osigndate" name="signdate" class="easyui-datebox"  data-options="readonly:true" 
								style="width:56%;height:28px;text-align:left">
							
						</div>
					</div>
				</form>
			</div>
			<!-- 原合同信息end -->
			
			<form id = "deductfrom" method="post">
				<!-- 扣款信息begin -->
				<div class="time_col time_colp11 heading">
					<label style="width: 68px;text-align:center;color:#FFF;font-weight: bold;">扣款</label>
				</div>
				<div class="time_col time_colp11" style="margin-top:10px;">
					<input id="contractid" name="contractid" type="hidden">
					<input id="tstp" name="tstp" type="hidden">
					<input id="area" name="area" type="hidden">
					<input id="corpid" name="corpid" type="hidden">
					<input id="pid" name="pid" type="hidden">
					<input id="corpkid" name="corpkid" type="hidden">
					<input id="corpkna" name="corpkna" type="hidden">
					<input id="bdate" name="bdate" type="hidden">
					<input id="edate" name="edate" type="hidden">
					<input id="typemin" name="typemin" type="hidden">
					<input id="corpnm" name="corpnm" type="hidden">
					<input id="adviser" name="adviser" type="hidden">
					<input id="submitime" name="submitime" type="hidden">
					<input id="isncust" name="isncust" type="hidden">
					<input id="sourid" name="sourid" type="hidden">
					<input id="pstatus" name="pstatus" type="hidden">
					<input id="cperiod" name="cperiod" type="hidden">
					<input id="ictype" name="ictype" type="hidden">
					<input id="comptype" name="comptype" type="hidden">
					<div style="width:24%;display: inline-block;">
						<label style="width:35%;text-align: right;">扣款比例：</label>
						<input id="propor" name="propor" class="easyui-numberbox" data-options="min:0,max:100,required:true,readonly:false"
							style="width:50%;height:28px;text-align:left; ">%
					</div>
					<div  style="width:24%;display: inline-block;">
						<label style="width:35%;text-align: right;">扣款金额：</label>
						<input id="ndesummny" name="ndesummny" class="easyui-numberbox" style="width:60%;height:28px;text-align:left;"
							data-options="readonly:true,precision:2,groupSeparator:','" >
					</div>
					<div style="width:24%;display: inline-block;">
						<label style="width:35%;text-align: right;">扣费日期：</label>
						<input id="dedate" name="dedate" class="easyui-datebox"  data-options="readonly:true" 
							style="width:60%;height:28px;text-align:left"></input>
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:35%;text-align: right;">经办人：</label>
						<input id="vopernm" name="vopernm" class="easyui-textbox" data-options="readonly:true" 
							style="width:60%;height:28px;text-align:left; ">
						<input id="voper" name="voper" type="hidden">
					</div>
				</div>
				<div class="time_col time_colp11 ">
					&emsp;&emsp;<input id="debit" name="opertype" type="radio" value="1" checked />
					<label>扣款</label>
					<input name="opertype" type="radio" value="2" />
					<label>驳回</label>
					<label style="text-align: right;width:94px;">驳回原因：</label>
					<textarea id="confreason" name="confreason" class="easyui-textbox"  
						data-options="readonly:true,multiline:true,validType:'length[0,200]'" 
						style="height:33px; width:60%;border-radius: 5px;">
					</textarea>
					<input id="confreasonid" name="confreasonid" type="hidden">
				</div>
				<!-- 扣款信息end -->
			
				<!-- 合同信息 begin -->
				<div class="time_col time_colp11 heading">
					<label style="width: 68px;text-align:center;color:#FFF;font-weight: bold;">合同信息</label>
				</div>
				<div class="time_col time_colp11 ">
					<div class="decan" style="width:48.4%;display: inline-block;">
						<label style="width:20%;text-align: right;">客户名称：</label>
						<input id="corpkna" name="corpkna" class="easyui-textbox" data-options="readonly:true" 
							style="width:76%;height:28px;text-align:left;">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">签订日期：</label>
						<input id="signdate" name="signdate" class="easyui-datebox"  data-options="readonly:true" 
							style="width:56%;height:28px;text-align:left">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">业务类型：</label>
						<input id="typeminm" name="typeminm" class="easyui-textbox" data-options="readonly:true"
							style="width:56%;height:28px;text-align:left;">
					</div>
				</div>
				<div class="time_col time_colp11 ">
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">纳税人资格：</label>
						<input id="chname" name="chname" class="easyui-textbox" data-options="readonly:true"
							style="width:56%;height:28px;text-align:left;">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;    text-indent: 15px;">代账费(元/月):</label>
						<input id="nmsmny" name="nmsmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:56%;height:28px;text-align:left;">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">合同金额：</label>
						<input id="ntlmny" name="ntlmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:56%;height:28px;text-align:left;">
					</div>
					<div id = "nbook" class="decan" style="width:24%;display:none;">
						<label style="width:40%;text-align: right;">账本费：</label>
						<input id="nbmny" name="nbmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:30%;height:28px;text-align:left"></input>
					</div>
				</div>
				<div class="time_col time_colp11 ">
					<div class="decan" style="width:24%;display:inline-block;">
						<label style="width:40%;text-align: right;">合同周期(月)：</label>
						<input id="contcycle" name="contcycle" class="easyui-textbox" data-options="readonly:true" 
							style="width:56%; height: 28px; text-align:left;">
					</div>
					<div class="decan" style="width:24%;display:inline-block;">
						<label style="width:40%;text-align: right;">收款周期(月)：</label>
						<input id="recycle" name="recycle" class="easyui-textbox" data-options="readonly:true" 
							style="width: 56%; height: 28px; text-align:left;">
					</div>
						<div class="decan" style="width:24%;display:inline-block;">
						<div class="time_col">
							<label style="width: 109px;text-align: right;">服务期限：</label> 
							<input type="text" id="bperiod" name="bperiod" class="easyui-textbox" data-options="readonly:true"
								style="width:70px; height: 28px; " >-
							<input type="text" id="eperiod" name="eperiod" class="easyui-textbox" data-options="readonly:true"
							 	style="width:70px; height: 28px;">
						</div>
					</div>
					<!-- <div class="decan" style="width:24%;display:inline-block;">
						<label for="isnconfirm" style="text-align:right;width:57%;">
							<input type="checkbox" id="isnconfirm" name="isnconfirm" value="是" onclick="return false;">&nbsp;未确定服务期限
						</label>
					</div> -->
				</div>
				<div class="time_col time_colp11 ">
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:39.6%;text-align: right;">合同编号：</label>
						<input id="vccode" name="vccode"  class="easyui-textbox" data-options="readonly:true" 
							style="width:56%;height:28px;text-align:left;">
					</div>
					<div class="decan" style="width:25%;display: inline-block;">
						<label style="width:38.6%;text-align: right;">存量客户：</label>
						<input type="checkbox" id="isncust" name="isncust" value="是" onclick="return false;">
					</div>
					<div class="decan" style="width:25%;display: inline-block;">
						<label style="width:38.6%;text-align: right;">续费合同：</label>
						<input type="checkbox" id="chargetype" name="chargetype" value="是" onclick="return false;">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:31.6%;text-align: right;">公司类型：</label>
						<input id="comptypenm" name="comptypenm" class="easyui-textbox"data-options="readonly:true"
							style="text-align:left"></input>
					</div>
				</div>
				<!-- 合同信息 end -->
			</form>
				
			<!-- 附件信息begin -->
			<div id ="fileshow" class="menu_entrance menu_entrances" style="margin-top:0;margin-right:5px;display:none;">
				<div class="entrance_block_tu" id="tpght" style="height:auto;width:99%;">
					<ul class="tu_block" id="filedocs"></ul>
				</div>
			</div>
			<!-- 附件信息end -->
			
               <!-- 驳回历史begin -->
               <div id="rejereson"></div>
			<!-- 驳回历史 -->
			
			<!-- 附件信息begin -->
			<div id="filedoc"></div>
			<!-- 附件信息end -->
		
		</div>
	</div>
	<!-- 变更审核 end -->
	
</body>
</html>