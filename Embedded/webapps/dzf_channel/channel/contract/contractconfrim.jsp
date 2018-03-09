<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<title>合同确认</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../../css/contractconfrim.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/contract/contractconfrim.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/contract/showfile.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<%
	String logincorp = (String) session.getAttribute(IGlobalConstants.login_corp);
	String login_user = (String) session.getAttribute(IGlobalConstants.login_user);
	UserVO userVo = UserCache.getInstance().get(login_user, logincorp);
	String period = AdminDateUtil.getPeriod();
%>
<body>
	<input id="unm" name="unm" type="hidden" value=<%= userVo.getUser_name() %>> 
	<input id="uid" name="uid" type="hidden" value=<%= userVo.getCuserid() %>> 
	<input id="period" name="period" type="hidden" value=<%= period %>> 
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
							onclick="qryData(1)">待审核</a>
					</div>
					<div style="margin:4px 0px 0px 10px;float:right;font-size:14px;">
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; margin-right:15px;" 
							onclick="qryData(2)">存量待审</a>
					</div>
				</div>
				
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="filter">
						<input style="height:28px;width:220px" class="easyui-textbox" id="filter_value" 
							prompt="请输入加盟商名称,按Enter键 "/> 
					</div>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="audit()">合同审核</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="bathAudit()">批量审核</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="change()">合同变更</a>
				</div>
			</div>
		</div>
		
		<!-- 查询对话框 begin -->
		<div class="qijian_box" id="qrydialog" style="display: none; width: 450px; height: 320px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<form id="query_form">
				<h3>
					<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="width: 85px;text-align:right">提单日期：</label>
					<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
					<font>-</font>
					<font><input name="edate" type="text" id="edate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
				</div>
				<div class="time_col time_colp10">
					<label style="width: 85px;text-align:right">合同类型：</label>
					<input id="normal" name="normal" type="checkbox" checked 
						style="width:20px;height:28px;text-align:left;margin-left:2px;"/>
					<label style="width:100px;text-align:left">正常提单</label> 
					<input id="supple" name="supple" type="checkbox" checked 
						style="width:20px;height:28px;text-align:left;margin-left:20px;"/>
					<label style="width:100px;text-align:left">补提单</label> 
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">合同状态：</label>
					<select id="destatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:100px;height:28px;">
						<option value="-1">全部</option>
						<option value="5">待审核</option>
						<option value="1">已审核</option>
						<option value="9">已终止</option>
						<option value="10">已作废</option>
						<option value="8">服务到期</option>
					</select>
					<label style="width:80px;text-align:right">客户类型：</label>
					<select id="isncust" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:100px;height:28px;">
						<option value="">全部</option>
						<option value="N">非存量客户</option>
						<option value="Y">存量客户</option>
					</select>
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">加盟商：</label>
					<input id="channel_select" class="easyui-textbox" style="width:290px;height:28px;"/>
					<input id="pk_account" type="hidden">
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">客户：</label>
					<input id="corpkna_ae" class="easyui-textbox" style="width:290px;height:28px;"/>
					<input id="corpkid_ae" name="corpkid" type="hidden"> 
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">渠道经理：</label>
					<input id="manager" class="easyui-textbox" style="width:290px;height:28px;" />
					<input id="managerid" type="hidden">
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">纳税人资格：</label>
					<select id="corptype" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:150px;height:28px;">
						<option value="-1">全部</option>
						<option value="1">小规模纳税人</option>
						<option value="2">一般纳税人</option>
					</select>
				</div>
			</form>
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
		
		<!-- 单个审核  begin  -->
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
			<div style="height:88%; overflow: auto;">
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
					<div class="decan strong" style="width:30%;display: inline-block;">
						<label style="width:27%;text-align: right;font-weight: bold;">加盟商：</label>
						<input id="corpnm" name="corpnm" class="easyui-textbox" data-options="readonly:true" 
							style="width:60%;height:28px;text-align:left; ">
					</div>
					<div class="decan strong" style="width:20%;display: inline-block;">
						<label style="width:32%;text-align: right;font-weight: bold;">合同金额：</label>
						<input id="hntlmny" name="hntlmny" class="easyui-numberbox" style="width:58%;height:28px;text-align:left;"
							data-options="readonly:true,precision:2,groupSeparator:','" >
					</div>
					<div id="issupple" class="decan strong" style="width:24%;display:inline-block;font-weight:bold;">
						<label style="width:40%;text-align: right;">变更日期：</label>
						<input id="scperiod" name="cperiod" class="easyui-textbox" data-options="readonly:true" 
							style="width: 56%; height: 28px; text-align:left;">
					</div>
				</div>
				<form id = "deductfrom" method="post">
					<div class="time_col time_colp11 ">
						<label style="width: 100px;text-align:center;color:#1b8cf2;font-weight: bold;">扣款</label>
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
						<input id="adviser" name="adviser" type="hidden">
						<input id="submitime" name="submitime" type="hidden">
						<input id="isncust" name="isncust" type="hidden">
						<input id="sourid" name="sourid" type="hidden">
						<input id="pstatus" name="pstatus" type="hidden">
						<input id="cperiod" name="cperiod" type="hidden">
						<div style="width:24%;display: inline-block;">
							<label style="width:35%;text-align: right;">扣款比例：</label>
							<input id="propor" name="propor" class="easyui-numberbox" data-options="min:0,max:100,required:true,"
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
						<label style="text-align: right;width:90px;">驳回原因：</label>
						<textarea id="confreason" name="confreason" class="easyui-textbox"  
							data-options="readonly:true,multiline:true,validType:'length[0,200]'" 
							 style="height:33px; width:60%;border-radius: 5px;"></textarea>
					</div>
				
					<!-- 合同信息 begin -->
					<div>
						<div class="time_col time_colp11 ">
							<label style="width: 100px;text-align:center;color:#1b8cf2;font-weight: bold;">合同信息</label>
						</div>
						<div class="time_col time_colp11 ">
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">客户名称：</label>
								<input id="corpkna" name="corpkna" class="easyui-textbox" data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left;">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">合同编号：</label>
								<input id="vccode" name="vccode"  class="easyui-textbox" data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left;">
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
								<label style="width:40%;text-align: right;">账本费：</label>
								<input id="nbmny" name="nbmny" class="easyui-numberbox"  
									data-options="readonly:true,precision:2,groupSeparator:','"
									style="width:56%;height:28px;text-align:left"></input>
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
									<label style="width: 109px;text-align: right;">服务期限：</label> 
									<input type="text" id="bperiod" name="bperiod" class="easyui-textbox" data-options="readonly:true"
										style="width:70px; height: 28px; " >-
									<input type="text" id="eperiod" name="eperiod" class="easyui-textbox" data-options="readonly:true"
									 	style="width:70px; height: 28px;">
								</div>
							</div>
						</div>
						<!-- 附件信息begin -->
						<div id ="fileshow" class="menu_entrance menu_entrances" style="margin-top:0;margin-right:5px;display:none;">
							<div class="entrance_block_tu" id="tpght" style="height:auto;width:99%;">
								<ul class="tu_block" id="filedocs"></ul>
							</div>
						</div>
						<!-- 附件信息end -->
					</div>
				</form>
				<div id="filedoc"></div>
			<!-- 合同信息 end -->
			</div>
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
		
		<!-- 查看加盟商 -->
		<div id="chnDlg"></div>
		<!-- 公司列表 -->
		<div id="gs_dialog"></div>
		<!-- 批量审核  end-->
		<div id="chnBtn" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#chnDlg').dialog('close');" style="width:90px">取消</a>
		</div>
		
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
					<textarea id="bconfreason" name="confreason" class="easyui-textbox"  
						data-options="readonly:true,multiline:true,validType:'length[0,200]'" 
						 style="height:50px; width:84%;border-radius: 5px;"></textarea>
				</div>  
			</form>
		</div>
		<!-- 批量审核  end-->
		
		<!-- 合同变更 begin  -->
		<div id="change_Dialog" class="easyui-dialog" style="width:1160px;height:90%;background:#FFF" data-options="closed:true">
			<div class="time_col time_colp11 Marketing" style="margin:0 auto;height:50px;line-height:50px;background:#eff1f2;">
				<div style="width:14%;display: inline-block;float: right;">
					<a class="ui-btn ui-btn-xz" onclick="changeConfri()">确定</a>
					<a class="ui-btn ui-btn-xz" onclick="changeCancel()">取消</a>
				</div>
			</div>
			<div style="height:92%; overflow: auto;">
				<form id = "changefrom" method="post" enctype="multipart/form-data">
					<div class="time_col time_colp11 " style="margin-top:10px;">
						<div class="time_col time_colp11 " style="margin-top: 10px;">
							<div style="width: 34%; display: inline-block">
								<label style="width: 100px; text-align:right;">变更原因：</label>
								<input id="end"  type="radio" name="chtype" value="1" style="width:30px">
								<label style="text-align:left; width:60%;">C端客户终止，变更合同</label>
							</div>
							<div style="width:20%; display: inline-block">
								<input id="nullify"  type=radio name="chtype" value="2" style="width:30px">
								<label style="text-align:left; width: 60%;">合同作废</label> 
								<input id="changetype" name="changetype" type="hidden">
							</div>
							<div style="width: 44%; display: inline-block">
								<label style="text-align: right; width: 30%;">备注：</label>
								<input id="changememo" name="changememo" class="easyui-textbox" style="width:40%;height:28px;text-align:left">
							</div>
						</div>
						<div class="time_col time_colp11">
							<div id = "addclass">
								<div style="width:25%; display:inline-block">
									<label style="text-align: right; width: 35%;">终止期间：</label> 
									<input id="stperiod" name="stperiod" class="easyui-datebox" data-options="editable:false"
										style="width:60%;height:28px;text-align:left">
								</div>
								<div style="width:20%; display:inline-block">
									<label style="text-align: right; width: 35%;">退回扣款：</label> 
									<input id="remny" name="remny" class="easyui-numberbox" 
										data-options="precision:2,groupSeparator:','"
										style="width:40%;height:28px;text-align:left">
								</div>
								<div style="width:20%; display:inline-block">
									<label style="text-align: right; width: 50%;">变更后合同金额：</label> 
									<input id="nchtlmny" name="nchtlmny" class="easyui-numberbox" 
										data-options="readonly:true,precision:2,groupSeparator:','"
										style="width: 40%; height: 28px; text-align: left">
								</div>
								<div style="width:20%; display:inline-block">
									<label style="text-align: right; width: 50%;">变更后扣款金额：</label> 
									<input id="nchsumny" name="nchsumny" class="easyui-numberbox" 
										data-options="readonly:true,precision:2,groupSeparator:','"
										style="width:40%;height:28px;text-align:left"> 
								</div>
							</div>
						</div>
					</div>
					<div class="time_col time_colp11 ">
						<label style="width: 100px;text-align:center;color:#1b8cf2;font-weight: bold;">扣款</label>
					</div>
					<div class="time_col time_colp11 " style="margin-top:10px;">
						<input id="id" name="id" type="hidden">
						<input id="scontractid" name="contractid" type="hidden">
						<input id="ststp" name="tstp" type="hidden">
						<input id="sarea" name="area" type="hidden">
						<input id="scorpid" name="corpid" type="hidden">
						<input id="spid" name="pid" type="hidden">
						<input id="scorpkid" name="corpkid" type="hidden">
						<input id="scorpkna" name="corpkna" type="hidden">
						<input id="sbdate" name="bdate" type="hidden">
						<input id="sedate" name="edate" type="hidden">
						<input id="stypemin" name="typemin" type="hidden">
						<input id="scorpnm" name="corpnm" type="hidden">
						<input id="sadviser" name="adviser" type="hidden">
						<input id="ssubmitime" name="submitime" type="hidden">
						<input id="sisncust" name="isncust" type="hidden">
						
						<input id="sndemny" name="ndemny" type="hidden"><!-- 预付款扣款金额 -->
						<input id="snderebmny" name="nderebmny" type="hidden"><!-- 返点款扣款金额 -->
						<div class="decan" style="width:24%;display: inline-block;">
							<label style="width:35%;text-align: right;">扣款比例：</label>
							<input id="spropor" name="propor" class="easyui-numberbox" data-options="readonly:true"
								style="width:50%;height:28px;text-align:right; ">%
						</div>
						<div class="decan" style="width:24%;display: inline-block;">
							<label style="width:35%;text-align: right;">扣款金额：</label>
							<input id="sndesummny" name="ndesummny" class="easyui-numberbox" style="width:60%;height:28px;text-align:left;"
								data-options="readonly:true,precision:2,groupSeparator:','" >
						</div>
						<div class="decan" style="width:24%;display: inline-block;">
							<label style="width:35%;text-align: right;">扣费日期：</label>
							<input id="sdedate" name="dedate" class="easyui-datebox"  data-options="readonly:true" 
								style="width:60%;height:28px;text-align:left"></input>
						</div>
						<div class="decan" style="width:24%;display: inline-block;">
							<label style="width:35%;text-align: right;">经办人：</label>
							<input id="svopernm" name="vopernm" class="easyui-textbox" data-options="readonly:true" 
								style="width:60%;height:28px;text-align:left; ">
							<input id="svoper" name="voper" type="hidden">
						</div>
					</div>
				
					<!-- 合同信息 begin -->
					<div>
						<div class="time_col time_colp11 ">
							<label style="width: 100px;text-align:center;color:#1b8cf2;font-weight: bold;">合同信息</label>
						</div>
						<div class="time_col time_colp11 ">
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">客户名称：</label>
								<input id="scorpkna" name="corpkna" class="easyui-textbox" data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left;">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">合同编号：</label>
								<input id="svccode" name="vccode"  class="easyui-textbox" data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left;">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">签订日期：</label>
								<input id="ssigndate" name="signdate" class="easyui-datebox"  data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">业务类型：</label>
								<input id="stypeminm" name="typeminm" class="easyui-textbox" data-options="readonly:true"
									style="width:56%;height:28px;text-align:left;">
							</div>
						</div>
						<div class="time_col time_colp11 ">
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">纳税人资格：</label>
								<input id="schname" name="chname" class="easyui-textbox" data-options="readonly:true"
									style="width:56%;height:28px;text-align:left;">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-indent: 15px;">代账费(元/月):</label>
								<input id="snmsmny" name="nmsmny" class="easyui-numberbox"  data-options="readonly:true,precision:2,groupSeparator:','"
									 style="width:56%;height:28px;text-align:left;">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">账本费：</label>
								<input id="snbmny" name="nbmny" class="easyui-numberbox"  data-options="readonly:true,precision:2,groupSeparator:','"
									 style="width:56%;height:28px;text-align:left"></input>
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">合同金额：</label>
								<input id="sntlmny" name="ntlmny" class="easyui-numberbox"  data-options="readonly:true,precision:2,groupSeparator:','"
								style="width:56%;height:28px;text-align:left;">
							</div>
						</div>
						<div class="time_col time_colp11 ">
							<div class="decan" style="width:24%;display:inline-block;">
								<label style="width:40%;text-align: right;">合同周期(月)：</label>
								<input id="scontcycle" name="contcycle" class="easyui-textbox" data-options="readonly:true" 
									style="width:56%; height: 28px; text-align:left;">
							</div>
							<div class="decan" style="width:24%;display:inline-block;">
								<label style="width:40%;text-align: right;">收款周期(月)：</label>
								<input id="srecycle" name="recycle" class="easyui-textbox" data-options="readonly:true" 
									style="width: 56%; height: 28px; text-align:left;">
							</div>
							<div class="decan" style="width: 48%;display:inline-block;">
								<div class="time_col">
									<label style="width: 109px;text-align: right;">服务期限：</label> 
									<input type="text" id="sbperiod" name="bperiod" class="easyui-textbox" data-options="readonly:true"
										style="width:70px; height: 28px; " >-
									<input type="text" id="seperiod" name="eperiod" class="easyui-textbox" data-options="readonly:true"
									 	style="width:70px; height: 28px;">
								</div>
							</div>
						</div>
						<!-- 附件信息begin -->
						<div id ="sfileshow" class="menu_entrance menu_entrances" style="margin-top:0;margin-right:5px;display:none;">
							<div class="entrance_block_tu" id="stpght" style="height:auto;width:99%;">
								<ul class="tu_block" id="sfiledocs"></ul>
							</div>
						</div>
						<!-- 附件信息end -->
						<!-- 变更附件 begin -->
						<div style="margin:20px;">
						<label style="text-align:right;width:8.52%; display: inline-block;   vertical-align: top;">变更附件：</label>
						<div style="display: inline-block;white-space: nowrap;">
							 <div class="uploadImg"  style="display: inline-block;">
								<div style="overflow: auto;" id="image1"></div> 
							</div>
						</div>
						</div>
						<!-- 变更附件 end -->
					</div>
				</form>
				<div id="sfiledoc"></div>
			<!-- 合同信息 end -->
			</div>
		</div>
		<!-- 合同变更  end  -->
		
		<!-- 合同详情 begin  -->
		<div id="info_Dialog" class="easyui-dialog" style="width:1160px;height:90%;background:#FFF" data-options="closed:true">
			<div class="time_col time_colp11 Marketing" style="margin:0 auto;height:50px;line-height:50px;background:#eff1f2;">
				<div style="width:14%;display: inline-block;float: right;margin-top:10px;">
					<a class="ui-btn ui-btn-xz" onclick="$('#info_Dialog').dialog('close');">返回</a>
				</div>
			</div>
			<div style="height:92%; overflow: auto;">
				<form id = "infofrom" method="post" enctype="multipart/form-data">
				
					<!-- 变更信息 begin -->
					<div id = "changeinfo" style="height:0;">
						<div class="time_col time_colp11 ">
							<label style="width: 100px;text-align:center;color:#1b8cf2;font-weight: bold;">变更信息</label>
						</div>
						<div class="time_col time_colp11 " style="margin-top:10px;">
							<div class="decan" style="margin-top: 10px;">
								<div style="width: 30%; display: inline-block">
									<label style="width:32%; text-align:right;">变更原因：</label>
									<input id="ichangereason" name="changereason" class="easyui-textbox" data-options="readonly:true"
										style="width:60%;height:28px;text-align:left">
								</div>
								<div style="width: 44%; display: inline-block">
									<label style="text-align: right; width: 30%;">备注：</label>
									<input id="ichangememo" name="changememo" class="easyui-textbox" data-options="readonly:true"
										style="width:40%;height:28px;text-align:left">
								</div>
							</div>
							<div class="time_col time_colp11">
								<div id = "addclass" class="decan">
									<div style="width:24%; display:inline-block">
										<label style="text-align: right; width: 40%;">终止期间：</label> 
										<input id="istperiod" name="stperiod" class="easyui-textbox" data-options="readonly:true"
											style="width:56%;height:28px;text-align:left">
									</div>
									<div style="width:24%; display:inline-block">
										<label style="text-align: right; width: 40%;">退回扣款：</label> 
										<input id="iremny" name="remny" class="easyui-numberbox" 
											data-options="readonly:true,precision:2,groupSeparator:','"
											style="width:40%;height:28px;text-align:left">
									</div>
									<div style="width:24%; display:inline-block">
										<label style="text-align: right; width: 40%;white-space: nowrap;">变更后合同金额：</label> 
										<input id="inchtlmny" name="nchtlmny" class="easyui-numberbox" 
											data-options="readonly:true,precision:2,groupSeparator:','"
											style="width: 40%; height: 28px; text-align: left">
									</div>
									<div style="width:24%; display:inline-block">
										<label style="text-align: right; width: 40%;white-space: nowrap;">变更后扣款金额：</label> 
										<input id="inchsumny" name="nchsumny" class="easyui-numberbox" 
											data-options="readonly:true,precision:2,groupSeparator:','"
											style="width:40%;height:28px;text-align:left"> 
									</div>
								</div>
							</div>
						</div>
					</div>
					<!-- 变更信息 end -->
					
					<!-- 扣款信息begin -->
					<div id = "dedinfo" style="height:0;">
						<div class="time_col time_colp11 ">
							<label style="width: 100px;text-align:center;color:#1b8cf2;font-weight: bold;">扣款信息</label>
						</div>
						<div class="time_col time_colp11 " style="margin-top:10px;">
							<input id="iid" name="id" type="hidden">
							<input id="icontractid" name="contractid" type="hidden">
							<input id="itstp" name="tstp" type="hidden">
							<input id="iarea" name="area" type="hidden">
							<input id="icorpid" name="corpid" type="hidden">
							<input id="ipid" name="pid" type="hidden">
							<input id="icorpkid" name="corpkid" type="hidden">
							<input id="icorpkna" name="corpkna" type="hidden">
							<input id="ibdate" name="bdate" type="hidden">
							<input id="iedate" name="edate" type="hidden">
							<input id="itypemin" name="typemin" type="hidden">
							<input id="icorpnm" name="corpnm" type="hidden">
							<input id="iadviser" name="adviser" type="hidden">
							<input id="isubmitime" name="submitime" type="hidden">
							<input id="iisncust" name="isncust" type="hidden">
							
							<input id="indemny" name="ndemny" type="hidden"><!-- 预付款扣款金额 -->
							<input id="inderebmny" name="nderebmny" type="hidden"><!-- 返点款扣款金额 -->
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">扣款比例：</label>
								<input id="ipropor" name="propor" class="easyui-numberbox" data-options="readonly:true"
									style="width:20%;height:28px;text-align:right; ">%
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">扣款金额：</label>
								<input id="indesummny" name="ndesummny" class="easyui-numberbox" 
									style="width:56%;height:28px;text-align:left;"
									data-options="readonly:true,precision:2,groupSeparator:','" >
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">扣费日期：</label>
								<input id="idedate" name="dedate" class="easyui-textbox"  data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left"></input>
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">经办人：</label>
								<input id="ivopernm" name="vopernm" class="easyui-textbox" data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left; ">
								<input id="ivoper" name="voper" type="hidden">
							</div>
						</div>
					</div>
					<!-- 扣款信息end -->
				
					<!-- 合同信息 begin -->
					<div>
						<div class="time_col time_colp11 ">
							<label style="width: 100px;text-align:center;color:#1b8cf2;font-weight: bold;">合同信息</label>
						</div>
						<div class="time_col time_colp11 ">
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">客户名称：</label>
								<input id="icorpkna" name="corpkna" class="easyui-textbox" data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left;">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">合同编号：</label>
								<input id="ivccode" name="vccode"  class="easyui-textbox" data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left;">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">签订日期：</label>
								<input id="isigndate" name="signdate" class="easyui-textbox"  data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">业务类型：</label>
								<input id="itypeminm" name="typeminm" class="easyui-textbox" data-options="readonly:true"
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
								<label style="width:40%;text-indent: 15px;">代账费(元/月):</label>
								<input id="inmsmny" name="nmsmny" class="easyui-numberbox"  
									data-options="readonly:true,precision:2,groupSeparator:','"
									style="width:56%;height:28px;text-align:left;">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">账本费：</label>
								<input id="inbmny" name="nbmny" class="easyui-numberbox"  
									data-options="readonly:true,precision:2,groupSeparator:','"
									style="width:56%;height:28px;text-align:left"></input>
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
							<div class="decan" style="width: 24%;display:inline-block;">
								<div class="time_col">
									<label style="width: 109px;text-align: right;">服务期限：</label> 
									<input type="text" id="ibperiod" name="bperiod" class="easyui-textbox" data-options="readonly:true"
										style="width:70px; height: 28px; " >-
									<input type="text" id="ieperiod" name="eperiod" class="easyui-textbox" data-options="readonly:true"
									 	style="width:70px; height: 28px;">
								</div>
							</div>
							<div class="decan" style="width:24%;display:inline-block;">
								<label style="width:40%;text-align: right;">合同状态：</label>
								<input id="istatusname" name="statusname" class="easyui-textbox" data-options="readonly:true" 
									style="width:56%; height: 28px; text-align:left;">
							</div>
						</div>
						<!-- 附件信息begin -->
						<div id ="ifileshow" class="menu_entrance menu_entrances" style="margin-top:0;margin-right:5px;display:none;">
							<div class="entrance_block_tu" id="stpght" style="height:auto;width:99%;">
								<ul class="tu_block" id="ifiledocs"></ul>
							</div>
						</div>
						<!-- 附件信息end -->
					</div>
				</form>
				<div id="ifiledoc"></div>
			<!-- 合同信息 end -->
			</div>
		</div>
		<!-- 合同变更  end  -->
		
		<!-- 渠道经理参照对话框及按钮 begin -->
		<div id="manDlg"></div>
		<div id="manBtn" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectMans()" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" 
				onclick="javascript:$('#manDlg').dialog('close');" style="width:90px">取消</a>
		</div>
		<!-- 渠道经理参照对话框及按钮 end -->
		
	</div>
	
</body>
</html>