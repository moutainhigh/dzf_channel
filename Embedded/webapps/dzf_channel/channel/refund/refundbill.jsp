<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.DzfUtil"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<%
	String date = AdminDateUtil.getServerDate();
	String btdate = AdminDateUtil.getPreviousNMonth(3);
%>
<title>退款单</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>
	rel="stylesheet">
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/rebate/public.css");%>
	rel="stylesheet">
<script
	src=<%UpdateGradeVersion.outversion(out, request.getContextPath() + "/js/easyuiext.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/refund/refundbill.js");%>
	charset="UTF-8" type="text/javascript"></script>
</head>

<body>
	<!-- 列表界面begin -->
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
				
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="onAdd()">新增</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="onEdit()">修改</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="onOperat(1)">确认</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="onOperat(2)">取消确认</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="onDelete()">删除</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="onExport()">导出</a>
				</div>
			</div>
		</div>
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		<!-- 新增对话框 begin -->
		<div id="addDlg" class="easyui-dialog" style="width:1000px;height:360px;padding:20px 20px;" 
			data-options="resizable:true,closed:true">
			<form id="addForm" method="post" style="margin-top:0px;">
				<div id="tableDiv" style="height:280px;overflow-y: auto;">
				  	<div class="time_col time_colp11">
			          	<div style="width:32%;display: inline-block">
							<label style="width:90px;text-align:right;">&emsp;退款单号: </label>
							<input id="vcode" name="vcode" class="easyui-textbox" style="width:200px;height:26px;"
								data-options="validType:'length[0,30]'" />
					 	</div>
					 	<div style="display: inline-block;width:33%;">
							<label style="width:90px;text-align: right;"><i class="bisu">*</i>加盟商:</label>
						    <input id="corp" name="corp" class="easyui-textbox" style="width:200px;height:26px;"
								data-options="required:true,validType:'length[0,100]'" />
							<input id="corpid" name="corpid" type="hidden">
						</div>
						<div style="display: inline-block;width:33%;">
							<label style="width:90px;text-align: right;"><i class="bisu">*</i>退款日期:</label>
							<input id="refdate" name="refdate" class="easyui-datebox" style="width:200px;height:26px;"
								data-options="required:true" />
						</div>
					</div>
				 	<div class="time_col time_colp11">
						<div style="width:32%;display: inline-block">
							<label style="width:90px;text-align: right;"><i class="bisu">*</i>预付款退款:</label>
							<input id="yfktk" name="yfktk" class="easyui-numberbox" style="width:200px;height:26px;"
								data-options="required:true,validType:'length[0,12]',min:0,precision:2,groupSeparator:','" />
						</div>	
						<div style="width:33%;display: inline-block">
							<label style="width:90px;text-align: right;"><i class="bisu">*</i>保证金退款:</label>
							<input id="bzjtk" name="bzjtk" class="easyui-numberbox" style="width:200px;height:26px;" 
								data-options="required:true,validType:'length[0,12]',min:0,precision:2,groupSeparator:','"/>
						</div>
					</div>
					<div class="time_col time_colp11">
						<div style="display: inline-block; margin-top: 5px;">
							<label style="width:90px;text-align: right;vertical-align: top;">备注:</label>
							<textarea id="memo" name="memo" class="easyui-textbox" style="width:830px;height:100px;"
								data-options="validType:'length[0,50]',multiline:true" ></textarea>
						</div>
					</div>
					<div style="text-align: center;margin-top:30px;">
				        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="onSave()">保存</a> 
				        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="$('#addDlg').dialog('close');">取消</a>
			        </div>
			 	</div>
		   </form>
		</div>
		<!-- 新增对话框 end -->
		
		<!-- 修改对话框 begin -->
		<div id="editDlg" class="easyui-dialog" style="width:1000px;height:360px;padding:20px 20px;background:#FFF;" 
			data-options="resizable:true,closed:true">
			<form id="editForm" method="post" style="margin-top:0px;">
				<input type="hidden" id="erefid" name="refid" />
				<input type="hidden" id="eupdatets" name="updatets" />
				<input type="hidden" id="estat" name="stat" />
				<div id="tableDiv" style="overflow-y: auto;">
				  	<div class="time_col time_colp11">
			          	<div style="width:32%;display: inline-block">
							<label style="width:90px;text-align:right;">&emsp;退款单号: </label>
							<input id="evcode" name="vcode" class="easyui-textbox" style="width:200px;height:26px;"
								data-options="readonly:true,required:true,validType:'length[0,30]'" />
					 	</div>
					 	<div style="display: inline-block;width:33%;">
							<label style="width:90px;text-align: right;"><i class="bisu">*</i>加盟商:</label>
						    <input id="ecorp" name="corp" class="easyui-textbox" style="width:200px;height:26px;"
								data-options="readonly:true,required:true,validType:'length[0,100]'" />
							<input id="ecorpid" name="corpid" type="hidden">
						</div>
						<div style="display: inline-block;width:33%;">
							<label style="width:90px;text-align: right;"><i class="bisu">*</i>退款日期:</label>
							<input id="erefdate" name="refdate" class="easyui-datebox" style="width:200px;height:26px;"
								data-options="required:true" />
						</div>
					</div>
				 	<div class="time_col time_colp11">
						<div style="width:32%;display: inline-block">
							<label style="width:90px;text-align: right;"><i class="bisu">*</i>预付款退款:</label>
							<input id="eyfktk" name="yfktk" class="easyui-numberbox" style="width:200px;height:26px;"
								data-options="required:true,validType:'length[0,12]',min:0,precision:2,groupSeparator:','" />
						</div>	
						<div style="width:33%;display: inline-block">
							<label style="width:90px;text-align: right;"><i class="bisu">*</i>保证金退款:</label>
							<input id="ebzjtk" name="bzjtk" class="easyui-numberbox" style="width:200px;height:26px;" 
								data-options="required:true,validType:'length[0,12]',min:0,precision:2,groupSeparator:','"/>
						</div>
					</div>
					<div class="time_col time_colp11">
						<div style="display: inline-block; margin-top: 5px;">
							<label style="width:90px;text-align: right;vertical-align: top;">备注:</label>
							<textarea id="ememo" name="memo" class="easyui-textbox" style="width:830px;height:100px;"
								data-options="validType:'length[0,50]',multiline:true" ></textarea>
						</div>
					</div>
					<div style="text-align: center;margin-top:30px;">
				        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="onEditSave()">保存</a> 
				        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="$('#editDlg').dialog('close');">取消</a>
			        </div>
			 	</div>
		   </form>
		</div>
		<!-- 修改对话框end -->
		
	</div>
	<!-- 列表界面end -->
	
	<!-- 查询对话框 begin -->
	<div id="qrydialog" class="qijian_box" style="display:none; width:450px; height:260px">
		<s class="s" style="left: 25px;"><i class="i"></i> </s>
		<form id="query_form">
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<label style="width: 90px;text-align:right">退款期间：</label>
				<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" value=<%= btdate %>
					data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
				<font>-</font>
				<font><input name="edate" type="text" id="edate" class="easyui-datebox" value=<%= date %>
					data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
			</div>
			<div class="time_col time_colp10">
				<label style="width:90px;text-align:right">加盟商：</label>
				<input id="qcorp" class="easyui-textbox" style="width:281px;height:28px;" />
				<input id="qcorpid" type="hidden">
			</div>
			<div class="time_col time_colp10">
				<label style="width:90px;text-align:right">退款单号：</label>
				<input id="vcode" class="easyui-textbox" style="width:281px;height:28px;" />
			</div>
			<div class="time_col time_colp10">
				<label style="width:90px;text-align:right">单据状态：</label>
				<select id="qstatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
					style="width:100px;height:28px;">
					<option value="-1">全部</option>
					<option value="0">待确认</option>
					<option value="1">已确认</option>
				</select>
			</div>
		</form>
		<p>
			<a class="ui-btn save_input" onclick="clearParams()">清空</a>
			<a class="ui-btn save_input" onclick="reloadData()">确认</a>
			<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
		</p>
	</div>
	<!-- 查询对话框 end -->
	
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