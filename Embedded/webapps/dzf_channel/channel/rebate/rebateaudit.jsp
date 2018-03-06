<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.DzfUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<title>返点单审核</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>
	rel="stylesheet">
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/rebate/rebate.css");%>
	rel="stylesheet">
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/rebate/public.css");%>
	rel="stylesheet">
<script
	src=<%UpdateGradeVersion.outversion(out, request.getContextPath() + "/js/easyuiext.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/rebate/rebateaudit.js");%>
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
				<div class="left mod-crumb">
					<div style="margin:6px 0px 0px 10px;float:right;font-size:14px;">
						<a href="javascript:void(0)" style="font-size:14;color:blue;margin-left:15px;margin-right:15px;" 
							onclick="qryData(2)">待审批</a>
					</div>
				</div>
				
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="filter">
						<label class="mr5">加盟商：</label>
						<input id="filter_value" style="height:28px;width:250px" class="easyui-textbox"  
						prompt="请输入加盟商名称,按Enter键 "/> 
					</div>
				</div>
				<div class="right">
					<!-- <a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onBatchAudit()">批量审批</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onBatchReject()">批量驳回</a> -->
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onExport()">导出</a>
				</div>
			</div>
		</div>
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		<!-- 审批对话框 begin -->
		<div id="auditDlg" class="easyui-dialog" style="width:1000px;height:530px;padding:20px 20px;background:#FFF;overflow:auto;" 
			data-options="resizable:true,closed:true">
	       	<div style="border-bottom: 1px solid;padding: 10px 0px 10px;">
		        <div style="text-align:right;margin-top:-10px;margin-right:40px;">
			        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="onAudit()">确认</a> 
			        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="$('#auditDlg').dialog('close');">取消</a>
		        </div>
		        <!-- 确认提交表单 begin -->
		        <form id="commitForm" method="post" style="margin-top:10px;" >
		        	<input type="hidden" id="crebid" name="rebid" />
		        	<input type="hidden" id="ctstp" name="tstp" />
		        	<input type="hidden" id="cistatus" name="istatus" />
		        	<input type="hidden" id="cvcode" name="vcode" />
		        	<input type="hidden" id="ccorpid" name="corpid" />
		        	<input type="hidden" id="cdebitmny" name="debitmny"/>
					<input type="hidden" id="cbasemny" name="basemny" />
					<input type="hidden" id="crebatemny" name="rebatemny" />
					
					<input type="hidden" id="cyear" name="year" />
					<input type="hidden" id="cseason" name="season" />
					
			      	<div class="time_col time_colp11">
					 	<div style="width:60%;display: inline-block">
							<label style="width:100px;text-align: right;">确认状态：</label>
							<input id ="conf" name ="confstatus" type = radio value = "1" checked 
								style="margin:0px 0px 0px 2px;text-align:left;">
							<label style="text-align:left;width:80px;">驳回修改</label>
							<input id ="reje" name ="confstatus" type = radio value = "4" 
								style="margin:0px 0px 0px 2px;text-align:left;">
							<label style="text-align:left;width:80px;">审批通过</label>
						</div>	
					</div>
					<div class="time_col time_colp11">
					 	<div style="width:100%;display: inline-block">
							<label style="width:100px;text-align:right; vertical-align:top;">说明：</label>
							<textarea id="apprnote" name="apprnote" class="easyui-textbox" style="width:804px;height:60px;" 
								data-options="validType:'length[0,50]',multiline:true" ></textarea>
						</div>	
			        </div>	  
		        </form>
		        <!-- 确认提交表单 end -->
	        </div>
			<form id="auditForm" method="post" style="margin-top:10px;">
				<input type="hidden" id="arebid" name="rebid" />
				<div id="tableDiv">
				  	<div class="time_col time_colp11">
			          	<div class="decan" style="width:30%;display: inline-block">
							<label style="width:100px;text-align:right;">&emsp;返点单号: </label>
							<input id="avcode" name="vcode" class="easyui-textbox" style="width:160px;height:26px;"
								data-options="readonly:true" />
					 	</div>
						<div class="decan" style="width:30%;display: inline-block">
							<label style="text-align:right;width:112px;">返点所属季度:</label> 
							<input id="ashowdate" name="showdate" class="easyui-textbox" style="width:94px;height:27px;"
								data-options="readonly:true" />
							<input type="hidden" id="syear" name="year" />
							<input type="hidden" id="sseason" name="season" />	
						</div>
					 	<div class="decan" style="display: inline-block;width:38%;">
							<label style="width:100px;text-align: right;">&emsp;加盟商名称:</label>
						    <input id="acorp" name="corp" class="easyui-textbox" style="width:220px;height:26px;"
								data-options="readonly:true,validType:'length[0,100]'" />
							<input id="acorpid" name="corpid" type="hidden">
						</div>
					</div>
				 	<div class="time_col time_colp11">
				 		<div class="decan" style="/* width:24%; */ display: inline-block">
							<label style="width:100px;text-align: right;">合同数量:</label>
							<input id="acontnum" name="contnum" class="easyui-numberbox" style="width:120px;height:26px;" 
								data-options="readonly:true,validType:'length[0,12]',min:0,groupSeparator:','"/>
						</div>	
					  	<div class="decan" style="width:24%;display: inline-block">
							<label style="width:80px;text-align: right;">扣款金额:</label>
							<input id="adebitmny" name="debitmny" class="easyui-numberbox" style="width:120px;height:26px;" 
								data-options="readonly:true,validType:'length[0,12]',min:0,precision:2,groupSeparator:','"/>
						</div>				
						<div class="decan" style="width:24%;display: inline-block">
							<label style="width:80px;text-align: right;">返点基数:</label>
							<input id="abasemny" name="basemny" class="easyui-numberbox" style="width:120px;height:26px;"
								data-options="readonly:true,validType:'length[0,12]',min:0,precision:2,groupSeparator:','" />
						</div>	
						<div class="decan" style="width:24%;display: inline-block">
							<label style="width:80px;text-align: right;">返点金额:</label>
							<input id="arebatemny" name="rebatemny" class="easyui-numberbox" style="width:120px;height:26px;" 
								data-options="readonly:true,validType:'length[0,12]',min:0,precision:2,groupSeparator:','"/>
						</div>
					</div>
					<div class="time_col time_colp11">
						<div class="decan" style="display: inline-block; margin-top: 5px;">
							<label style="width:100px;text-align: right;vertical-align: top;">备注:</label>
							<textarea id="amemo" name="memo" class="easyui-textbox" style="width:804px;height:60px;"
								data-options="readonly:true,multiline:true" ></textarea>
						</div>
					</div>
				 	<div  class="time_col time_colp11">
					  	<div class="decan" style="width:29%;display: inline-block">
							<label style="width:100px;text-align: right;">单据状态:</label>
							<input id="astatusname" name="statusname" class="easyui-textbox" style="width:160px;height:26px;" 
								data-options="readonly:true"/>
						</div>				
						<div class="decan" style="width:31%;display: inline-block">
							<label style="width:112px;text-align: right;">录入人:</label>
							<input id="aopername" name="opername" class="easyui-textbox" style="width:130px;height:26px;"
								data-options="readonly:true" />
						</div>	
						<div class="decan" style="width:38%;display: inline-block">
							<label style="width:100px;text-align: right;">录入时间:</label>
							<input id="aoperdate" name="operdate" class="easyui-textbox" style="width:150px;height:26px;" 
								data-options="readonly:true"/>
						</div>
					</div>
			 	</div>
		   </form>
		   <!-- 审批历史begin -->
		   <div id = "ahistory"></div>
			<!-- 审批历史end -->
		</div>
		<!-- 审批对话框end -->	
		
		
		<!-- 查看对话框 begin -->
		<div id="showDlg" class="easyui-dialog" style="width:1000px;height:530px;padding:20px 20px;background:#FFF;" 
			data-options="resizable:true,closed:true">
			<form id="showForm" method="post" style="margin-top:0px;">
				<input type="hidden" id="srebid" name="rebid" />
				<div id="tableDiv" style="overflow-y: auto;">
				  	<div class="time_col time_colp11">
			          	<div class="decan" style="width:30%;display: inline-block">
							<label style="width:100px;text-align:right;">&emsp;返点单号: </label>
							<input id="svcode" name="vcode" class="easyui-textbox" style="width:160px;height:26px;"
								data-options="readonly:true" />
					 	</div>
						<div class="decan" style="width:30%;display: inline-block">
							<label style="text-align:right;width:112px;">返点所属季度:</label> 
							<input id="sshowdate" name="showdate" class="easyui-textbox" style="width:94px;height:27px;"
								data-options="readonly:true" />
							<input type="hidden" id="syear" name="year" />
							<input type="hidden" id="sseason" name="season" />	
						</div>
					 	<div class="decan" style="display: inline-block;width:38%;">
							<label style="width:100px;text-align: right;">&emsp;加盟商名称:</label>
						    <input id="scorp" name="corp" class="easyui-textbox" style="width:220px;height:26px;"
								data-options="readonly:true,validType:'length[0,100]'" />
							<input id="scorpid" name="corpid" type="hidden">
						</div>
					</div>
				 	<div class="time_col time_colp11">
			 			<div class="decan" style="width:24%;display: inline-block">
							<label style="width:100px;text-align: right;">合同数量:</label>
							<input id="econtnum" name="contnum" class="easyui-numberbox" style="width:120px;height:26px;" 
								data-options="readonly:true,validType:'length[0,12]',min:0,groupSeparator:','"/>
						</div>	
					  	<div class="decan" style="width:24%;display: inline-block">
							<label style="width:80px;text-align: right;">扣款金额:</label>
							<input id="sdebitmny" name="debitmny" class="easyui-numberbox" style="width:120px;height:26px;" 
								data-options="readonly:true,validType:'length[0,12]',min:0,precision:2,groupSeparator:','"/>
						</div>				
						<div class="decan" style="width:24%;display: inline-block">
							<label style="width:80px;text-align: right;">返点基数:</label>
							<input id="sbasemny" name="basemny" class="easyui-numberbox" style="width:120px;height:26px;"
								data-options="readonly:true,validType:'length[0,12]',min:0,precision:2,groupSeparator:','" />
						</div>	
						<div class="decan" style="width:24%;display: inline-block">
							<label style="width:80px;text-align: right;">返点金额:</label>
							<input id="srebatemny" name="rebatemny" class="easyui-numberbox" style="width:120px;height:26px;" 
								data-options="readonly:true,validType:'length[0,12]',min:0,precision:2,groupSeparator:','"/>
						</div>
					</div>
					<div class="time_col time_colp11">
						<div class="decan" style="display: inline-block; margin-top: 5px;">
							<label style="width:100px;text-align: right;vertical-align: top;">备注:</label>
							<textarea id="smemo" name="memo" class="easyui-textbox" style="width:804px;height:60px;"
								data-options="readonly:true,multiline:true" ></textarea>
						</div>
					</div>
				 	<div  class="time_col time_colp11">
					  	<div class="decan" style="width:29%;display: inline-block">
							<label style="width:100px;text-align: right;">单据状态:</label>
							<input id="sstatusname" name="statusname" class="easyui-textbox" style="width:160px;height:26px;" 
								data-options="readonly:true"/>
						</div>				
						<div class="decan" style="width:31%;display: inline-block">
							<label style="width:112px;text-align: right;">录入人:</label>
							<input id="sopername" name="opername" class="easyui-textbox" style="width:130px;height:26px;"
								data-options="readonly:true" />
						</div>	
						<div class="decan" style="width:38%;display: inline-block">
							<label style="width:100px;text-align: right;">录入时间:</label>
							<input id="soperdate" name="operdate" class="easyui-textbox" style="width:150px;height:26px;" 
								data-options="readonly:true"/>
						</div>
					</div>
			 	</div>
		   </form>
		   <!-- 审批历史begin -->
		   <div id = "shistory"></div>
			<!-- 审批历史end -->
		</div>
		<!-- 查看对话框end -->
		
	</div>
	<!-- 列表界面end -->
	
	<!-- 查询对话框 begin -->
	<div id="qrydialog" class="qijian_box" style="display:none; width:450px; height:230px">
		<s class="s" style="left: 25px;"><i class="i"></i> </s>
		<form id="query_form">
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<label style="width: 90px;text-align:right">返点期间：</label>
				<font>
					<select id="qyear" name="qyear" class="easyui-combobox" 
						data-options="editable:false"  style="width:130px;height:28px;">
						<% DzfUtil.WriteYearOption(out);%>
			 		</select>
			 	</font>
				<font>-</font>
				<font>
					 <select id="qjd" name="qjd" class="easyui-combobox" data-options="editable:false,panelHeight:'auto'" 
					 	style="width:130px;height:28px;text-align:left">
						<option value="1">第一季度</option>
						<option value="2">第二季度</option>
						<option value="3">第三季度</option>
						<option value="4">第四季度</option>	
					</select>
				</font>
			</div>
			<div class="time_col time_colp10">
				<label style="width:90px;text-align:right">返点单状态：</label>
				<!-- 状态   0：待提交；1：待确认；2：待审批；3：审批通过；4：已驳回； -->
				<select id="qstatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
					style="width:100px;height:28px;">
					<option value="2">待审批</option>
					<option value="3">审批通过</option>
				</select>
			</div>
			<div class="time_col time_colp10">
				<label style="width:90px;text-align:right">渠道经理：</label>
				<input id="manager" class="easyui-textbox" style="width:290px;height:28px;" />
				<input id="managerid" type="hidden">
			</div>
			<div class="time_col time_colp10">
				<label style="width:90px;text-align:right">加盟商：</label>
				<input id="qcorp" class="easyui-textbox" style="width:290px;height:28px;" />
				<input id="qcorpid" type="hidden">
			</div>
		</form>
		<p>
			<a class="ui-btn save_input" onclick="clearParams()">清空</a>
			<a class="ui-btn save_input" onclick="reloadData()">确认</a>
			<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
		</p>
	</div>
	<!-- 查询对话框 end -->
	
	<!-- 渠道经理参照对话框及按钮 begin -->
	<div id="manDlg"></div>
	<div id="manBtn" style="display:none;">
		<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectMans()" style="width:90px">确认</a> 
		<a href="javascript:void(0)" class="easyui-linkbutton" 
			onclick="javascript:$('#manDlg').dialog('close');" style="width:90px">取消</a>
	</div>
	<!-- 渠道经理参照对话框及按钮 end -->
	
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