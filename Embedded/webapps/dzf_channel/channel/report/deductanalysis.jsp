<%@page import="com.dzf.pub.lang.DZFDate"%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<%@page import="com.dzf.pub.DzfUtil"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%
	String now = AdminDateUtil.getServerDate();
    String last = AdminDateUtil.getPreviousDate();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>加盟商扣款分析</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../../css/periodext.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/report/deductanalysis.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<body>
	<div class="wrapper">
		<div id="listPanel" class="wrapper" style="width: 100%;overflow:hidden; height: 100%;">
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
					<!-- <div class="h30 h30-arrow" id="filter">
						<input style="height:28px;width:220px" class="easyui-textbox" id="quname" prompt="录入加盟商名称或编码回车定位 "/> 
					</div> -->
					<div class="right">
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="load(2)">刷新</a>
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onExport()">导出</a>
					</div>
				</div>
			</div>
			
			<div class="mod-inner">
				<div id="dataGrid" class="grid-wrap">
					<table id="grid"></table>
				</div>
			</div>
			
			<!-- 查询对话框 begin -->
			<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:250px;">
				<s class="s"><i class="i"></i></s>
				<h3>
					<span>查询</span> <a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<form id = "qryfrom">
				<div class="sel_time">
					<div class="time_col">
						<input id="rq" type="radio"  name="seledate" checked="true" value="rq"/>
						<label style="width:70px;text-align:right" for='rq'>扣款日期：</label> 
						<font>
							<input id="bdate" name="bdate"  class="easyui-datebox" 
								data-options="required:true,width:130,height:28,validType:'checkdate'" value=<%=last%>/>
						</font>
						<font>-</font>
						<font>
							<input id="edate" name="edate"  class="easyui-datebox" 
								data-options="required:true,width:130,height:28,validType:'checkdate'" value=<%=now%>/>
						</font>
					</div>
				</div>
				<div class="time_col time_colp10">
					<input id="qj" type="radio" name="seledate" value="qj"/>
					<label style="width:68px;text-align:right" for='qj'>期间：</label> 
					<font> 
						<input type="text" id="begperiod" class="easyui-datebox" 
							data-options="editable:false,width:130,height:28"  />
					</font> 
					<font>-</font> 
					<font> 
						<input type="text" id="endperiod" class="easyui-datebox" 
							data-options="editable:false,width:130,height:28" />
					</font>
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">加盟商：</label>
					<input id="qcorp" class="easyui-textbox" style="width:280px;height:28px;" />
					<input id="qcorpid" type="hidden">	
				</div>
				</form>
				<p>
					<a class="ui-btn save_input" onclick="clearParams();">清除</a> 
					<a class="ui-btn save_input" onclick="load(1)">确定</a>
					<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
				</p>
			</div>
			<!-- 查询对话框 begin -->
			
	 	</div>	
	</div>
	
	<!-- 加盟商参照对话框及按钮 begin -->
	<div id="chnDlg"></div>
	<div id="chnBtn" style="display:none;">
		<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
		<a href="javascript:void(0)" class="easyui-linkbutton" 
			onclick="javascript:$('#chnDlg').dialog('close');" style="width:90px">取消</a>
	</div>
	<!-- 加盟商参照对话框及按钮 end -->
</body>
</html>
