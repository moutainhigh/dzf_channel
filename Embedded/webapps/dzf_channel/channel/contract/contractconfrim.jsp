<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<title>合同确认</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/contract/contractconfrim.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/contract/showfile.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<%
	String logincorp = (String) session.getAttribute(IGlobalConstants.login_corp);
	String login_user = (String) session.getAttribute(IGlobalConstants.login_user);
	UserVO userVo = UserCache.getInstance().get(login_user, logincorp);
%>
<body>
	<input id="unm" name="unm" type="hidden" value=<%= userVo.getUser_name() %>> 
	<input id="uid" name="uid" type="hidden" value=<%= userVo.getCuserid() %>> 
	<div id="List_panel" class="wrapper" data-options="closed:false">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<span class="cur"></span>
				</div>
				
				<div class="left mod-crumb">
				
					<input id="begdate" name="begdate" class="easyui-datebox" style="width:110px;height:28px;text-align:left">
					--
					<input id="enddate" name="enddate" class="easyui-datebox" style="width:110px;height:28px;text-align:left">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" data-options="plain:true" onclick="query()">查询</a>
				</div>
				
				<div class="left mod-crumb">
					<div style="margin:4px 0px 0px 10px;float:right;font-size:14px;">
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; margin-right:15px;" onclick="qryData(1)">待审核</a>
					</div>
				</div>
				
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="filter">
						<input style="height:28px;width:220px" class="easyui-textbox" id="filter_value" prompt="请输入渠道商名称,按Enter键 "/> 
					</div>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="audit()">合同审核</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="bathAudit()">批量审核</a>
				</div>
			</div>
		</div>

		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		<!-- 单个审核  begin  -->
		<div id="deduct_Dialog" class="easyui-dialog" style="width:1160px;height:90%;overflow: auto;" data-options="closed:true">
			<div>
				<div class="time_col time_colp11 " style="margin-top: 6px;">
					<div style="width:28%;display: inline-block;">
						<label style="width:27%;text-align: right;">预付款余额：</label>
						<input id="balmny" name="balmny" class="easyui-numberbox" data-options="readonly:true,precision:2,groupSeparator:','"
							 style="width:60%;height:28px;text-align:left; ">
					</div>
					<div style="width:50%;display: inline-block;">
						<label style="width:20%;text-align: right;">促销活动：</label>
						<input id="salespromot" name="salespromot" class="easyui-textbox" style="width:60%;height:28px;text-align:left; ">
					</div>
				</div>
				<div class="time_col time_colp11 " style="margin-top: 6px;">
					<div style="width:28%;display: inline-block;">
						<label style="width:27%;text-align: right;">渠道商：</label>
						<input id="corpnm" name="corpnm" class="easyui-textbox" style="width:60%;height:28px;text-align:left; ">
					</div>
					<div style="width:22%;display: inline-block;">
						<label style="width:30%;text-align: right;">合同金额：</label>
						<input id="hntlmny" name="hntlmny" class="easyui-numberbox" data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:60%;height:28px;text-align:left; ">
					</div>
					<div style="width:18%;display: inline-block;float: right;">
						<a class="ui-btn save_input" onclick="deductConfri()">确定</a>&emsp;
						<a class="ui-btn cancel_input" onclick="deductCancel()">取消</a>
					</div>
				</div>
			</div>
		
			<form id = "deductfrom" style="border-top:2px solid #4292c1;  margin-top:30px;"  method="post">
				<div class="time_col time_colp11 ">
					<label style="width: 100px;text-align:center;color:blue;">扣款</label>
				</div>
				<div class="time_col time_colp11 " style="margin-top:10px;">
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
					<div style="width:24%;display: inline-block;">
						<label style="width:35%;text-align: right;">扣款比例：</label>
						<input id="propor" name="propor" class="easyui-numberbox" data-options="min:0,max:100,required:true,"
							style="width:50%;height:28px;text-align:left; ">%
					</div>
					<div style="width:24%;display: inline-block;">
						<label style="width:35%;text-align: right;">扣款金额：</label>
						<input id="ndemny" name="ndemny" class="easyui-numberbox"  data-options="readonly:true,precision:2,groupSeparator:','"
						 style="width:60%;height:28px;text-align:left; ">
					</div>
					<div style="width:24%;display: inline-block;">
						<label style="width:35%;text-align: right;">扣费日期：</label>
						<input id="dedate" name="dedate" class="easyui-datebox"  data-options="readonly:true" 
						style="width:60%;height:28px;text-align:left"></input>
					</div>
					<div style="width:24%;display: inline-block;">
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
				</div>
				<div class="time_col time_colp11 ">
					<label style="vertical-align: top;text-align: right;">驳回原因：&nbsp;&nbsp;</label>
					<textarea id="confreason" name="confreason" class="easyui-textbox"  data-options="multiline:true,validType:'length[0,200]'" 
						 style="height:50px; width:88%;border-radius: 5px;"></textarea>
				</div>      	               
			
				<!-- 合同信息 begin -->
				<div>
					<div class="time_col time_colp11 ">
						<label style="width: 100px;text-align:center;color:blue;">合同信息</label>
					</div>
					<div class="time_col time_colp11 ">
						<div style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">客户名称：</label>
							<input id="corpkna" name="corpkna" class="easyui-textbox" data-options="readonly:true" 
								style="width:56%;height:28px;text-align:left;">
						</div>
						<div style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">合同编号：</label>
							<input id="vccode" name="vccode"  class="easyui-textbox" data-options="readonly:true" 
								style="width:56%;height:28px;text-align:left;">
						</div>
						<div style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">签订日期：</label>
							<input id="signdate" name="signdate" class="easyui-datebox"  data-options="readonly:true" 
								style="width:56%;height:28px;text-align:left">
						</div>
						<div style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">业务类型：</label>
							<input id="typeminm" name="typeminm" class="easyui-textbox" data-options="readonly:true"
								style="width:56%;height:28px;text-align:left;">
						</div>
					</div>
					<div class="time_col time_colp11 ">
						<div style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">纳税人资格：</label>
							<input id="chname" name="chname" class="easyui-textbox" data-options="readonly:true"
								style="width:56%;height:28px;text-align:left;">
						</div>
						<div style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">代账费（元/月）：</label>
							<input id="nmsmny" name="nmsmny" class="easyui-numberbox"  data-options="readonly:true,precision:2,groupSeparator:','"
								 style="width:56%;height:28px;text-align:left;">
						</div>
						<div style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">账本费：</label>
							<input id="nbmny" name="nbmny" class="easyui-numberbox"  data-options="readonly:true,precision:2,groupSeparator:','"
								 style="width:56%;height:28px;text-align:left"></input>
						</div>
						<div style="width:24%;display: inline-block;">
							<label style="width:40%;text-align: right;">合同金额：</label>
							<input id="ntlmny" name="ntlmny" class="easyui-numberbox"  data-options="readonly:true,precision:2,groupSeparator:','"
							style="width:56%;height:28px;text-align:left;">
						</div>
					</div>
					<div class="time_col time_colp11 ">
						<div style="width:24%;display:inline-block;">
							<label style="width:40%;text-align: right;">合同周期（月）：</label>
							<input id="contcycle" name="contcycle" class="easyui-textbox" data-options="readonly:true" 
								style="width:56%; height: 28px; text-align:left;">
						</div>
						<div style="width:24%;display:inline-block;">
							<label style="width:40%;text-align: right;">收款周期（月）：</label>
							<input id="chgcycle" name="chgcycle" class="easyui-textbox" data-options="readonly:true" 
								style="width: 56%; height: 28px; text-align:left;">
						</div>
						<div style="width: 48%;display:inline-block;">
							<div class="time_col">
								<label style="width: 118px;text-align: right;">服务期限：</label> 
								<input type="text" id="bperiod" name="bperiod" class="easyui-textbox" data-options="readonly:true"
									style="width:137px; height: 28px; " >-
								<input type="text" id="eperiod" name="eperiod" class="easyui-textbox" data-options="readonly:true"
								 	style="width:137px; height: 28px;">
							</div>
						</div>
					</div>
					<!-- 附件信息begin -->
					<div class="menu_entrance menu_entrances" style="margin-top:0;margin-right:5px;">
						<div class="entrance_block_tu" id="tpght" style="height:60%;width:99%;">
							<ul class="tu_block" id="filedocs"></ul>
						</div>
					</div>
					<!-- 附件信息end -->
				</div>
			</form>
			
			<div id="filedoc"></div>
			<!-- 合同信息 end -->
		</div>
		<!-- 单个审核  end  -->
		
		<!-- 查看附件begin -->
		<div id="attachViewDlg" style="display: none;">	
		  	<div class="menu_entrance menu_entrances" style="margin-top:0;margin-right:5px;">
				<div class="entrance_block_tu" id="tpght" style="overflow-y:auto;height:98%;width:99%;">
					<ul class="tu_block" id="attachs"></ul>
				</div>
			</div>
		</div>
		<div id="tpfd"></div>
		<!-- 查看附件end -->
		
		<!-- 批量审核  begin-->
		<div id="bdeduct_Dialog" class="easyui-dialog" style="width:900px;height:360px;overflow: auto;" data-options="closed:true">
			<div>
				<div class="time_col time_colp11" style="margin-top:20px;">
					<div style="width:18%;display: inline-block;float: right;">
						<a class="ui-btn save_input" onclick="bathconf()">确定</a>&emsp;
						<a class="ui-btn cancel_input" onclick="bathcanc()">取消</a>
					</div>
				</div>
			</div>
			<form id="bdeductfrom" style="border-top:2px solid #4292c1; width:94%;margin:0 auto;   margin-top:60px;" method="post">
				<div class="time_col time_colp11" style="margin-top:10px;">
					<div style="width:24%;display: inline-block;">
						<label style="width:35%;text-align: right;">扣款比例：</label>
						<input id="bpropor" name="propor" class="easyui-numberbox" data-options="min:0,max:100,required:true,"
							style="width:50%;height:28px;text-align:left; ">%
					</div>
		
					<div style="width:24%;display: inline-block;">
						<label style="width:35%;text-align: right;">扣费日期：</label>
						<input id="bdedate" name="dedate" class="easyui-datebox"  data-options="readonly:true" 
						style="width:60%;height:28px;text-align:left"></input>
					</div>
					<div style="width:24%;display: inline-block;">
						<label style="width:35%;text-align: right;">经办人：</label>
						<input id="bvopernm" name="vopernm" class="easyui-textbox" data-options="readonly:true" 
							style="width:60%;height:28px;text-align:left; ">
						<input id="bvoper" name="voper" type="hidden">
					</div>
				</div>
				<div class="time_col time_colp11 ">
					&emsp;<input id="bdebit" name="bopertype" type="radio" value="1" checked />
					<label>扣款</label>
					<input name="bopertype" type="radio" value="2" />
					<label>驳回</label>
				</div>
				<div class="time_col time_colp11 ">
					<label style="vertical-align: top;text-align: right;width:76px;">驳回原因：&nbsp;</label>
					<textarea id="confreason" name="confreason" class="easyui-textbox"  data-options="multiline:true,validType:'length[0,200]'" 
						 style="height:50px; width:84%;border-radius: 5px;"></textarea>
				</div>  
			</form>
		</div>
		<!-- 批量审核  end-->
	</div>
</body>
</html>