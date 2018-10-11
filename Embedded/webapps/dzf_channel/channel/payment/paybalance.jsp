<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<%@page import="com.dzf.pub.DzfUtil"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%
	String nowDate = AdminDateUtil.getServerDate();
	String lastDate = AdminDateUtil.getPreviousDate();
	
	//获取当前月期间
	Calendar e = Calendar.getInstance();
	String ym = new SimpleDateFormat("yyyy-MM").format(e.getTime());
	
	e.setTime(new Date());
    e.add(Calendar.MONTH, -1);
    String pym = new SimpleDateFormat("yyyy-MM").format(e.getTime());
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>付款单余额查询</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../../css/periodext.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,request.getContextPath()+"/js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/payment/paybalance.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/report/periodext.js");%> charset="UTF-8" type="text/javascript"></script>
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
						<strong id="jqj"><%=lastDate%> 至 <%=nowDate%></strong> <span class="arrow-date"></span>
						<span class="arrow-date"></span>
					</div>
				</div>
					<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="filter">
						<input style="height:28px;width:220px" class="easyui-textbox" id="filter_value" prompt="请输入加盟商名称,按Enter键 "/> 
					</div>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onPrint()">打印</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onExport()">导出</a>
				</div>
			</div>
		</div>
		
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="grid"></table>
			</div>
		</div>
		
		<!-- 查询对话框 begin -->
		<div class="qijian_box" id="qrydialog" style="width:450px; height:330px;" data-options="closed:true">
			<s class="s"><i class="i"></i> </s>
			<h3> 
				<span>查询</span>
				<a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="sel_time">
				<div class="time_col">
					<input id="da" type="radio" name="seledate" checked value="da"/>
					<label for="da">日期：</label> 
					<font> 
						<input id="begdate" type="text" class="easyui-datebox" data-options="validType:'checkdate'"
							style="width:137px;height:30px;" value="<%=lastDate%>" />
					</font> 
					<font>-</font> 
					<font> 
						<input id="enddate" type="text"  class="easyui-datebox" data-options="validType:'checkdate'"
							style="width:137px;height:30px;" value="<%=nowDate%>"/>
					</font>
				</div>
			</div>
			<div class="time_col time_colp10">
				<input id="pe" type="radio"  name="seledate" value="pe"/>
				<label style="width:70px;" for="pe">期间：</label> 
				<font>
					<input type="text" id="begperiod" class="easyui-textbox" data-options="editable:false"
						style="width:137px;height:30px;" value=<%=pym%> />
				</font>
				<font>-</font>
				<font>
					<input type="text" id="endperiod" class="easyui-textbox" data-options="editable:false"
						style="width:137px;height:30px;" value=<%=ym%> />
				</font>
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;">付款类型：</label> 
				<input id="yfk" type="radio"  name="seletype" checked value="2"/>
				<label style="width:60px;" for='yfk'>预付款</label> 
				<input id="fd" type="radio"  name="seletype" value="3"/>
				<label style="width:45px;" for='fd'>返点</label> 
				<input id="bzj" type="radio"  name="seletype" value="1"/>
				<label style="width:60px;" for='bzj'>保证金</label> 
				<input id="all" type="radio"  name="seletype" value="-1"/>
				<label style="width:60px;" for='all'>全部</label> 
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">渠道经理：</label>
				<input id="manager" class="easyui-textbox" style="width:290px;height:28px;" />
				<input id="managerid" type="hidden">
			</div>
			<div class="time_col time_colp10">
				<label style="width:70px;text-align:right">加盟商：</label>
				<input id="channel_select" class="easyui-textbox" style="width:290px;height:28px;" />
				<input id="pk_account" type="hidden">
			</div>
			<div class="time_col time_colp10">
				<label style="text-align:right;width: 70px;">大区：</label> 
				<input id="aname"  name="aname" class="easyui-combobox" style="width: 290px; height: 28px;" 
					data-options="required:false,valueField:'name',textField:'name',panelHeight:100" editable="false" />  
			</div>
			<p style="border: none;margin-top: 5px">
				<a class="ui-btn save_input" onclick="clearParams()">清除</a>
				<a class="ui-btn save_input" onclick="reloadData()">确定</a> 
				<a class="ui-btn " onclick="closeCx()">取消</a>
			</p>
		</div>
		<!-- 查询对话框 end -->
		
		<!-- 加盟商参照begin -->
		<div id="kj_dialog"></div>
		<div id="kj_buttons" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#kj_dialog').dialog('close');" style="width:90px">取消</a>
		</div>
		<!-- 加盟商参照begin -->
		
		<!-- 付款单余额明细begin -->
		<div id="detail_dialog" class="easyui-dialog" title="付款单余额明细" 
			data-options="modal:true,closed:true" style="width:1140px;height:500px;">
			<div class="time_col" style="padding-top: 10px;width:96%;margin:0 auto;">
				<label style="text-align:right">查询：</label> 
				<span id ="qrydate" style="vertical-align: middle;font-size:14px;"></span>
				<label style="text-align:right">加盟商：</label> 
				<span id ="corpnm" style="vertical-align: middle;font-size:14px;"></span>
				<label style="text-align:right">付款类型：</label> 
				<span id ="ptypenm" style="vertical-align: middle;font-size:14px;"></span>
			<div class="right" style="float: right;display: inline-block;"> 
				<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onDetPrint()">打印</a>
				<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onDetExport()">导出</a>
		 	</div>
			</div>	
			
			<div data-options="region:'center'" style="overflow-x:auto; overflow-y:auto;margin: 0 auto;width:98%;height:380px;padding:10px">
				 <table id="gridh"></table>	
			</div>
		</div>
		<!-- 付款单余额明细end -->
		
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