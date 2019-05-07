<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>渠道合同审核</title>
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
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/contract/showimage.js");%>
	charset="UTF-8" type="text/javascript"></script>
<style type="text/css">
    .right_menu {
        display:inline-block;position:relative;
    }
    .right_menu .more_div a {
        padding: 1px 5px;
        text-align: center;
    }
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
						<strong id="jqj"></strong>
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
				<label style="width:70px;text-align:right">处理状态：</label>
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
	<div id="change_Dialog" class="easyui-dialog" style="width:1160px;height:90%;background:#FFF" data-options="closed:true">
		<div class="time_col time_colp11  Marketing" style="margin:0 auto;height:50px;line-height:50px;background:#eff1f2;">
			<div style="width:14%;display: inline-block;float: right;">
				<a class="ui-btn ui-btn-xz" onclick="changeConfri()">确定</a>
				<a class="ui-btn ui-btn-xz" onclick="changeCancel()">取消</a>
			</div>
		</div>
		<div style="height:92%; overflow:auto; margin-top:10px;">
		
			<form id = "changefrom" method="post">
				
				<input type="hidden" id="applyid" name="applyid">
				<input type="hidden" id="conid" name="conid">
				<input type="hidden" id="hisid" name="hisid">
				<input type="hidden" id="corpid" name="corpid">
				<input type="hidden" id="corpkid" name="corpkid">
				<input type="hidden" id="apstatus" name="apstatus">
				<input type="hidden" id="updatets" name="updatets">
				<input type="hidden" id="changetype" name="changetype">
			
				<div class="time_col time_colp11 ">
					&emsp;&emsp;<input id="debit" name="opertype" type="radio" value="1" checked />
					<label>通过</label>
					<div id="audit" style="width:24%;display:none;">
						<label style="text-align: right;width:90px;">下一审核人：</label>
						<input id="auditer" name="auditer" class="easyui-combobox" editable="false"
							data-options="panelHeight:'auto',valueField:'id',textField:'name'"
							style="width:110px;height:28px;text-align:left">
					</div>
					<div id="oper" style="width:24%;display:none;font-size:14px;">提交渠道运营人员处理</div>
				</div>
					
				<div class="time_col time_colp11 " style="margin-top:10px;">
					&emsp;&emsp;<input name="opertype" type="radio" value="2" />
					<label>驳回</label>
					<label style="text-align: right;width:90px;">驳回原因：</label>
					<input id="confreason" name="confreason" class="easyui-textbox"  
						data-options="readonly:true,validType:'length[0,200]'" 
						style="height:33px; width:60%; border-radius:5px;"/>
				</div>
			
				<!-- 变更信息begin -->
				<div class="time_col time_colp11 heading">
					<label style="width: 68px;text-align:center;color:#FFF;font-weight: bold;">变更信息</label>
				</div>
				<div class="time_col time_colp11 " style="margin-top:10px;">
					<div class="time_col time_colp11 " style="margin-top: 10px;">
						<div class="decan" style="width:22%; display: inline-block">
							<label style="width: 110px; text-align:right;">变更原因：</label>
							<input id="ichangereason" name="changereason" class="easyui-textbox" data-options="readonly:true"
								style="width:40%;height:28px;text-align:left">
						</div>
						<div class="decan" style="width:46%; display: inline-block">
							<label style="text-align: right; width:65px;">备注：</label>
							<input id="ichangememo" name="changememo" class="easyui-textbox" data-options="readonly:true"
								style="width:80%;height:28px;text-align:left">
						</div>
					</div>
					<div class="time_col time_colp11">
						<div id = "addclass">
							<div class="decan" style="width:24%; display:inline-block">
								<label style="text-align: right; width:40%;">终止期间：</label> 
								<input id="istperiod" name="stperiod" class="easyui-textbox" style="width:40%;height:28px;text-align:left"
									data-options="readonly:true" >
							</div>
							<div class="decan" style="width:24%; display:inline-block">
								<div class="time_col">
									<label style="width: 112px;text-align: right;">变更后服务期限：</label> 
									<input type="text" id="ibcgeperiod" name="bcgeperiod" class="easyui-textbox" data-options="readonly:true"
										style="width:70px; height: 28px; " >-
									<input type="text" id="iecgeperiod" name="ecgeperiod" class="easyui-textbox" data-options="readonly:true"
									 	style="width:70px; height: 28px;">
								</div>
							</div>
							<div class="decan" style="width:24%; display:inline-block">
								<label style="text-align: right; width:40%; white-space: nowrap;">变更后合同金额：</label> 
								<input id="inchtlmny" name="nchtlmny" class="easyui-numberbox" 
									data-options="readonly:true,precision:2,groupSeparator:','"
									style="width:56%; height: 28px; text-align: left">
							</div>
						</div>
					</div>
				</div>
				
				<!-- 变更信息end -->
			
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
						<label style="width:40%;text-align: right;">合同编码：</label>
						<input id="vccode" name="vccode" class="easyui-textbox" data-options="readonly:true"
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
						<label style="width:40%;text-align: right;">账本费：</label>
						<input id="nbmny" name="nbmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:30%;height:28px;text-align:left"></input>
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">合同金额：</label>
						<input id="ntlmny" name="ntlmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:56%;height:28px;text-align:left;">
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
							<label style="width:111px;text-align: right;">服务期限：</label> 
							<input type="text" id="bperiod" name="bperiod" class="easyui-textbox" data-options="readonly:true"
								style="width:70px; height: 28px; " >-
							<input type="text" id="eperiod" name="eperiod" class="easyui-textbox" data-options="readonly:true"
							 	style="width:70px; height: 28px;">
						</div>
					</div>
					<div class="decan" style="width: 24%;display:inline-block;">
						<div class="time_col">
							<label style="width:40%;text-align: right;">提单日期：</label>
							<input id="isubmitdate" name="submitdate" class="easyui-textbox" data-options="readonly:true" 
								style="width: 56%; height: 28px; text-align:left;">
						</div>
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
			
		</div>
	</div>
	<!-- 变更审核 end -->
	
	<!-- 非常规套餐审核 begin -->
	<div id="audit_Dialog" class="easyui-dialog" style="width:1160px;height:90%;background:#FFF" data-options="closed:true">
		<div class="time_col time_colp11  Marketing" style="margin:0 auto;height:50px;line-height:50px;background:#eff1f2;">
			<div style="width:14%;display: inline-block;float: right;">
				<a class="ui-btn ui-btn-xz" onclick="auditConfri()">确定</a>
				<a class="ui-btn ui-btn-xz" onclick="auditCancel()">取消</a>
			</div>
		</div>
		<div style="height:85%; overflow:auto; margin-top:10px;">
		
			<form id = "auditfrom" method="post">
				
				<input type="hidden" id="aapplyid" name="applyid">
				<input type="hidden" id="aconid" name="conid">
				<input type="hidden" id="ahisid" name="hisid">
				<input type="hidden" id="aapstatus" name="apstatus">
				<input type="hidden" id="aupdatets" name="updatets">
				<input type="hidden" id="achangetype" name="changetype">
				<input type="hidden" id="acorpid" name="corpid">
				<input type="hidden" id="acorpkid" name="corpkid">
				<input type="hidden" id="auopertype" name="opertype">
			
				<div class="time_col time_colp11 ">
					&emsp;&emsp;<input id="adebit" name="aopertype" type="radio" value="1" checked />
					<label>通过</label>
					<div id="aaudit" style="width:24%;display:none;">
						<label style="text-align: right;width:90px;">下一审核人：</label>
						<input id="aauditer" name="auditer" class="easyui-combobox" editable="false"
							data-options="panelHeight:'auto',valueField:'id',textField:'name'"
							style="width:110px;height:28px;text-align:left">
					</div>
					<div id="aoper" style="width:24%;display:none;font-size:14px;">提交渠道运营人员处理</div>
				</div>
					
				<div class="time_col time_colp11 " style="margin-top:10px;">
					&emsp;&emsp;<input name="aopertype" type="radio" value="2" />
					<label>驳回</label>
					<label style="text-align: right;width:90px;">驳回原因：</label>
					<input id="aconfreason" name="confreason" class="easyui-textbox"  
						data-options="readonly:true,validType:'length[0,200]'" 
						style="height:33px; width:60%; border-radius:5px;"/>
				</div>
			
				<!-- 合同信息 begin -->
				<div class="time_col time_colp11 heading">
					<label style="width: 68px;text-align:center;color:#FFF;font-weight: bold;">合同信息</label>
				</div>
				<div class="time_col time_colp11 ">
					<div class="decan" style="width:48.4%;display: inline-block;">
						<label style="width:20%;text-align: right;">客户名称：</label>
						<input id="acorpkna" name="corpkna" class="easyui-textbox" data-options="readonly:true" 
							style="width:76%;height:28px;text-align:left;">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">签订日期：</label>
						<input id="asigndate" name="signdate" class="easyui-datebox"  data-options="readonly:true" 
							style="width:56%;height:28px;text-align:left">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">合同编码：</label>
						<input id="avccode" name="vccode" class="easyui-textbox" data-options="readonly:true"
							style="width:56%;height:28px;text-align:left;">
					</div>
				</div>
				<div class="time_col time_colp11 ">
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">纳税人资格：</label>
						<input id="achname" name="chname" class="easyui-textbox" data-options="readonly:true"
							style="width:56%;height:28px;text-align:left;">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;    text-indent: 15px;">代账费(元/月):</label>
						<input id="anmsmny" name="nmsmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:56%;height:28px;text-align:left;">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">账本费：</label>
						<input id="anbmny" name="nbmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:30%;height:28px;text-align:left"></input>
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">合同金额：</label>
						<input id="antlmny" name="ntlmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:56%;height:28px;text-align:left;">
					</div>
				</div>
				<div class="time_col time_colp11 ">
					<div class="decan" style="width:24%;display:inline-block;">
						<label style="width:40%;text-align: right;">合同周期(月)：</label>
						<input id="acontcycle" name="contcycle" class="easyui-textbox" data-options="readonly:true" 
							style="width:56%; height: 28px; text-align:left;">
					</div>
					<div class="decan" style="width:24%;display:inline-block;">
						<label style="width:40%;text-align: right;">收款周期(月)：</label>
						<input id="arecycle" name="recycle" class="easyui-textbox" data-options="readonly:true" 
							style="width: 56%; height: 28px; text-align:left;">
					</div>
					<div class="decan" style="width:24%;display:inline-block;">
						<div class="time_col">
							<label style="width:111px;text-align: right;">服务期限：</label> 
							<input type="text" id="abperiod" name="bperiod" class="easyui-textbox" data-options="readonly:true"
								style="width:70px; height: 28px; " >-
							<input type="text" id="aeperiod" name="eperiod" class="easyui-textbox" data-options="readonly:true"
							 	style="width:70px; height: 28px;">
						</div>
					</div>
					<div class="decan" style="width: 24%;display:inline-block;">
						<div class="time_col">
							<label style="width:40%;text-align: right;">提单日期：</label>
							<input id="aisubmitdate" name="submitdate" class="easyui-textbox" data-options="readonly:true" 
								style="width: 56%; height: 28px; text-align:left;">
						</div>
					</div>
				</div>
				<!-- 合同信息 end -->
			</form>
			
			<!-- 附件信息begin -->
			<div id ="afileshow" class="menu_entrance menu_entrances" style="margin-top:0;margin-right:5px;display:none;">
				<div class="entrance_block_tu" id="tpght" style="height:auto;width:99%;">
					<ul class="tu_block" id="afiledocs"></ul>
				</div>
			</div>
			<!-- 附件信息end -->
			
			<!-- 驳回历史begin -->
            <div id="rejereson"></div>
			<!-- 驳回历史 -->
				
		</div>
	</div>
	<!-- 非常规套餐审核 end -->
	
	<!-- 变更详情 begin -->
	<div id="ichange_Dialog" class="easyui-dialog" style="width:1160px;height:90%;background:#FFF" data-options="closed:true">
		<div style="height:92%; overflow:auto; margin-top:10px;">
		
			<form id = "ichangefrom" method="post">
			 	<!-- 进度展示 begin -->
				<div class="time_col time_colp11">
					<label style="width: 110px; text-align:right;">申请状态：</label>
					<div id = "ihistory" style="display:inline-block"></div>
				</div>
				<!-- 进度展示 end -->
			
				<!-- 变更信息begin -->
				<div class="time_col time_colp11 heading">
					<label style="width: 68px;text-align:center;color:#FFF;font-weight: bold;">变更信息</label>
				</div>
				<div class="time_col time_colp11 " style="margin-top:10px;">
					<div class="time_col time_colp11 " style="margin-top: 10px;">
						<div class="decan" style="width:22%; display: inline-block">
							<label style="width: 110px; text-align:right;">变更原因：</label>
							<input id="iichangereason" name="changereason" class="easyui-textbox" data-options="readonly:true"
								style="width:40%;height:28px;text-align:left">
						</div>
						<div class="decan" style="width:46%; display: inline-block">
							<label style="text-align: right; width:65px;">备注：</label>
							<input id="iichangememo" name="changememo" class="easyui-textbox" data-options="readonly:true"
								style="width:80%;height:28px;text-align:left">
						</div>
					</div>
					<div class="time_col time_colp11">
						<div id = "addclass">
							<div class="decan" style="width:24%; display:inline-block">
								<label style="text-align: right; width:40%;">终止期间：</label> 
								<input id="iistperiod" name="stperiod" class="easyui-textbox" style="width:40%;height:28px;text-align:left"
									data-options="readonly:true" >
							</div>
							<div class="decan" style="width:24%; display:inline-block">
								<div class="time_col">
									<label style="width: 112px;text-align: right;">变更后服务期限：</label> 
									<input type="text" id="iibcgeperiod" name="bcgeperiod" class="easyui-textbox" data-options="readonly:true"
										style="width:70px; height: 28px; " >-
									<input type="text" id="iiecgeperiod" name="ecgeperiod" class="easyui-textbox" data-options="readonly:true"
									 	style="width:70px; height: 28px;">
								</div>
							</div>
							<div class="decan" style="width:24%; display:inline-block">
								<label style="text-align: right; width:40%; white-space: nowrap;">变更后合同金额：</label> 
								<input id="iinchtlmny" name="nchtlmny" class="easyui-numberbox" 
									data-options="readonly:true,precision:2,groupSeparator:','"
									style="width:56%; height: 28px; text-align: left">
							</div>
						</div>
					</div>
				</div>
				
				<!-- 变更信息end -->
			
				<!-- 合同信息 begin -->
				<div class="time_col time_colp11 heading">
					<label style="width: 68px;text-align:center;color:#FFF;font-weight: bold;">合同信息</label>
				</div>
				<div class="time_col time_colp11 ">
					<div class="decan" style="width:48.4%;display: inline-block;">
						<label style="width:20%;text-align: right;">客户名称：</label>
						<input id="icorpkna" name="corpkna" class="easyui-textbox" data-options="readonly:true" 
							style="width:76%;height:28px;text-align:left;">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">签订日期：</label>
						<input id="isigndate" name="signdate" class="easyui-datebox"  data-options="readonly:true" 
							style="width:56%;height:28px;text-align:left">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">合同编码：</label>
						<input id="ivccode" name="vccode" class="easyui-textbox" data-options="readonly:true"
							style="width:56%;height:28px;text-align:left;">
					</div>
				</div>
				<div class="time_col time_colp11 ">
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">纳税人资格：</label>
						<input id="ichname" name="chname" class="easyui-textbox" data-options="readonly:true"
							style="width:56%;height:28px;text-align:left;">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;    text-indent: 15px;">代账费(元/月):</label>
						<input id="inmsmny" name="nmsmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:56%;height:28px;text-align:left;">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">账本费：</label>
						<input id="inbmny" name="nbmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:30%;height:28px;text-align:left"></input>
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">合同金额：</label>
						<input id="intlmny" name="ntlmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:56%;height:28px;text-align:left;">
					</div>
				</div>
				<div class="time_col time_colp11 ">
					<div class="decan" style="width:24%;display:inline-block;">
						<label style="width:40%;text-align: right;">合同周期(月)：</label>
						<input id="icontcycle" name="contcycle" class="easyui-textbox" data-options="readonly:true" 
							style="width:56%; height: 28px; text-align:left;">
					</div>
					<div class="decan" style="width:24%;display:inline-block;">
						<label style="width:40%;text-align: right;">收款周期(月)：</label>
						<input id="irecycle" name="recycle" class="easyui-textbox" data-options="readonly:true" 
							style="width: 56%; height: 28px; text-align:left;">
					</div>
					<div class="decan" style="width:24%;display:inline-block;">
						<div class="time_col">
							<label style="width:111px;text-align: right;">服务期限：</label> 
							<input type="text" id="ibperiod" name="bperiod" class="easyui-textbox" data-options="readonly:true"
								style="width:70px; height: 28px; " >-
							<input type="text" id="ieperiod" name="eperiod" class="easyui-textbox" data-options="readonly:true"
							 	style="width:70px; height: 28px;">
						</div>
					</div>
					<div class="decan" style="width: 24%;display:inline-block;">
						<div class="time_col">
							<label style="width:40%;text-align: right;">提单日期：</label>
							<input id="iisubmitdate" name="submitdate" class="easyui-textbox" data-options="readonly:true" 
								style="width: 56%; height: 28px; text-align:left;">
						</div>
					</div>
				</div>
				<!-- 合同信息 end -->
			</form>
				
			<!-- 附件信息begin -->
			<div id ="ifileshow" class="menu_entrance menu_entrances" style="margin-top:0;margin-right:5px;display:none;">
				<div class="entrance_block_tu" id="tpght" style="height:auto;width:99%;">
					<ul class="tu_block" id="ifiledocs"></ul>
				</div>
			</div>
			<!-- 附件信息end -->
		
		</div>
	</div>
	<!-- 变更详情 end -->
	
	<!-- 非常规套餐详情 begin -->
	<div id="iaudit_Dialog" class="easyui-dialog" style="width:1160px;height:80%;background:#FFF" data-options="closed:true">
		<div style="height:92%; overflow:auto; margin-top:10px;">
		
			<form id = "iauditfrom" method="post">
				<!-- 进度展示 begin -->
				<div class="time_col time_colp11">
					<label style="width: 110px; text-align:right;">申请状态：</label>
					<div id = "ahistory" style="display:inline-block"></div>
				</div>
				<!-- 进度展示 end -->
			
				<!-- 合同信息 begin -->
				<div class="time_col time_colp11 heading">
					<label style="width: 68px;text-align:center;color:#FFF;font-weight: bold;">合同信息</label>
				</div>
				<div class="time_col time_colp11 ">
					<div class="decan" style="width:48.4%;display: inline-block;">
						<label style="width:20%;text-align: right;">客户名称：</label>
						<input id="iacorpkna" name="corpkna" class="easyui-textbox" data-options="readonly:true" 
							style="width:76%;height:28px;text-align:left;">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">签订日期：</label>
						<input id="iasigndate" name="signdate" class="easyui-datebox"  data-options="readonly:true" 
							style="width:56%;height:28px;text-align:left">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">合同编码：</label>
						<input id="iavccode" name="vccode" class="easyui-textbox" data-options="readonly:true"
							style="width:56%;height:28px;text-align:left;">
					</div>
				</div>
				<div class="time_col time_colp11 ">
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">纳税人资格：</label>
						<input id="iachname" name="chname" class="easyui-textbox" data-options="readonly:true"
							style="width:56%;height:28px;text-align:left;">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;    text-indent: 15px;">代账费(元/月):</label>
						<input id="ianmsmny" name="nmsmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:56%;height:28px;text-align:left;">
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">账本费：</label>
						<input id="ianbmny" name="nbmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:30%;height:28px;text-align:left"></input>
					</div>
					<div class="decan" style="width:24%;display: inline-block;">
						<label style="width:40%;text-align: right;">合同金额：</label>
						<input id="iantlmny" name="ntlmny" class="easyui-numberbox"  
							data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:56%;height:28px;text-align:left;">
					</div>
				</div>
				<div class="time_col time_colp11 ">
					<div class="decan" style="width:24%;display:inline-block;">
						<label style="width:40%;text-align: right;">合同周期(月)：</label>
						<input id="iacontcycle" name="contcycle" class="easyui-textbox" data-options="readonly:true" 
							style="width:56%; height: 28px; text-align:left;">
					</div>
					<div class="decan" style="width:24%;display:inline-block;">
						<label style="width:40%;text-align: right;">收款周期(月)：</label>
						<input id="iarecycle" name="recycle" class="easyui-textbox" data-options="readonly:true" 
							style="width: 56%; height: 28px; text-align:left;">
					</div>
					<div class="decan" style="width:24%;display:inline-block;">
						<div class="time_col">
							<label style="width:111px;text-align: right;">服务期限：</label> 
							<input type="text" id="iabperiod" name="bperiod" class="easyui-textbox" data-options="readonly:true"
								style="width:70px; height: 28px; " >-
							<input type="text" id="iaeperiod" name="eperiod" class="easyui-textbox" data-options="readonly:true"
							 	style="width:70px; height: 28px;">
						</div>
					</div>
					<div class="decan" style="width: 24%;display:inline-block;">
						<div class="time_col">
							<label style="width:40%;text-align: right;">提单日期：</label>
							<input id="iaisubmitdate" name="submitdate" class="easyui-textbox" data-options="readonly:true" 
								style="width: 56%; height: 28px; text-align:left;">
						</div>
					</div>
				</div>
				<!-- 合同信息 end -->
			</form>
			
			<!-- 附件信息begin -->
			<div id ="iafileshow" class="menu_entrance menu_entrances" style="margin-top:0;margin-right:5px;display:none;">
				<div class="entrance_block_tu" id="tpght" style="height:auto;width:99%;">
					<ul class="tu_block" id="iafiledocs"></ul>
				</div>
			</div>
			<!-- 附件信息end -->
				
		</div>
	</div>
	<!-- 非常规套餐审核 end -->
	
	<!-- 批量审核begin -->
	<div id="batch_Dialog" class="easyui-dialog" style="width:500px;height:238px;background:#FFF" data-options="closed:true">
		<div style="height:85%; overflow:auto; margin-top:10px;">
		 	<form id = "batchfrom" method="post">
		 		<input type="hidden" id="buopertype" name="opertype">
		 		<div class="time_col time_colp11 ">
					&emsp;&emsp;<input id="bdebit" name="bopertype" type="radio" value="1" checked />
					<label style="width:28px;">通过</label>
					<div id="baudit" style="width:220px;display:none;">
						<label style="text-align: right;width:90px;">下一审核人：</label>
						<input id="bauditer" name="auditer" class="easyui-combobox" editable="false"
							data-options="panelHeight:'auto',valueField:'id',textField:'name'"
							style="width:110px;height:28px;text-align:left">
					</div>
					<div id="boper" style="width:220px;display:none;font-size:14px;">提交渠道运营人员处理</div>
				</div>
					
				<div class="time_col time_colp11 " style="margin-top:10px;">
					&emsp;&emsp;<input name="bopertype" type="radio" value="2" />
					<label style="width:28px;">驳回</label>
					<label style="text-align: right;width:90px;">驳回原因：</label>
					<input id="bconfreason" name="confreason" class="easyui-textbox"  
						data-options="multiline:true,readonly:true,validType:'length[0,200]'" 
						style="height:66px; width:286px; border-radius:5px;"/>
				</div>
		 	</form>
			<div style="text-align: center;margin-top:20px;">
				<a class="ui-btn ui-btn-xz" onclick="batchConfri()">确定</a>
				<a class="ui-btn ui-btn-xz" onclick="batchCancel()">取消</a>
			</div>
		</div>
	</div>
	<!-- 批量审核end -->
	
</body>
</html>