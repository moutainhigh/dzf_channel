<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
<title>操作日志查询</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>  rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/chn_set/sys_opelog.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<body class="sys_opelog">
	<div class="wrapper">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
						<div class="h30 h30-arrow" id="cxjs">
							<label class="mr5">查询：</label><strong id="jqj"></strong><span
								class="arrow-date"></span>
					</div>
				</div>
				<div class="right">
					<a id="btn_save" href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options=" plain:true"   onclick="directPrint()">打印</a>
					<a id="btn_cancel" href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options=" plain:true"   onclick="expexcel()">导出</a>
				</div> 
			</div>
	    </div>
	    <div class="mod-inner" style="height:82%">
			<div id="dataGrid" class="grid-wrap">
				<table id="dgmsinfo" ></table>
			</div>
		</div>
		
		 <div class="qijian_box" id="contid1"
			style="display: none; width: 430px;height:250px; ">
			<s class="s"><i class="i"></i> </s>
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>

			<div class="time_col time_colp10">
				<div class="time_col">
					<label  style="text-align:right;width:70px;">操作日期：</label> 
					<font>
						<input name="begindate1" type="text" id="begindate1" class="easyui-datebox" data-options="width:130,height:27,editable:true" />
					</font>
					<font>-</font> 
					<font>
						<input name="enddate" type="text" id="enddate" class="easyui-datebox" data-options="width:130,height:27,editable:true" />
					</font>
				</div>
			</div>
			
			<div class="time_col time_colp10">
				<div class="time_col">
					<label  style="text-align:right; width:70px;">操作用户：</label> 
					<font>
						<input name="opeuser" type="text" id="opeuser" class="easyui-combobox" data-options="valueField: 'id', textField: 'name',editable:false " style="width:280px;height:30px;" />
					</font>
				</div>
			</div>
			
			<div class="time_col time_colp10">
				<div class="time_col">
					<label  style="text-align:right;width:70px;">操作类型：</label> 
					<font>
						<input name="opetype" type="text" id="opetype" class="easyui-combobox" data-options="valueField: 'id', textField: 'name',editable:false " style="width:280px;height:30px;" />
					</font>
				</div>
			</div>
			
			<div class="time_col time_colp10">
				<div class="time_col">
					<label  style="text-align:right;width:70px;">操作明细：</label> 
					<font>
						<input name="opemsg" 	type="text" id="opemsg" class="easyui-textbox" data-options="width:280,height:27,editable:true" />
					</font>
				</div>
			</div>
			<p>
				<a class="ui-btn save_input" onclick="reloadData()">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
    </div>
</body>
</html>
