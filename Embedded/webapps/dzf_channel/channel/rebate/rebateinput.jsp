<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.DzfUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<title>返点单录入</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>
	rel="stylesheet">
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/rebate/rebate.css");%>
	rel="stylesheet">
<script
	src=<%UpdateGradeVersion.outversion(out, request.getContextPath() + "/js/easyuiext.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/rebate/rebateinput.js");%>
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
						<a href="javascript:void(0)" style="font-size:14;color:blue;margin-left:15px;" 
							onclick="qryData(0)">待提交</a>
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px;margin-right:15px;" 
							onclick="qryData(4)">已驳回</a>
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
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onCommit()">提交</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onAdd()">新增</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onExport()">导出</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onImport()">导入</a>
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
			          	<div style="width:30%;display: inline-block">
							<label style="width:100px;text-align:right;">&emsp;返点单号: </label>
							<input id="vcode" name="vcode" class="easyui-textbox" style="width:160px;height:26px;"
								data-options="validType:'length[0,30]'" />
					 	</div>
						<div style="width:30%;display: inline-block">
							<label style="text-align:right;width:102px;">期间：</label> 
							<select id="year" name="year" class="easyui-combobox" 
								data-options="required:true,editable:false" style="width:70px;height:27px;">
						    	<% DzfUtil.WriteYearOption(out);%>
							</select>
							<select id="season" name="season" class="easyui-combobox"  
								data-options="required:true,editable:false" style="width:94px;height:27px;">
			         			<option value="1">第一季度</option>
								<option value="2">第二季度</option>
								<option value="3">第三季度</option>
								<option value="4">第四季度</option>
							</select>
						</div>
					 	<div style="display: inline-block;width:38%;">
							<label style="width:100px;text-align: right;">&emsp;加盟商名称:</label>
						    <input id="corp" name="corp" class="easyui-textbox" style="width:220px;height:26px;"
								data-options="required:true,validType:'length[0,100]'" />
							<input id="corpid" name="corpid" type="hidden">
						</div>
					</div>
				 	<div class="time_col time_colp11">
					  	<div style="width:29%;display: inline-block">
							<label style="width:100px;text-align: right;">扣款金额:</label>
							<input id="debitmny" name="debitmny" class="easyui-numberbox" style="width:160px;height:26px;" 
								data-options="readonly:true,validType:'length[0,12]',min:0,precision:2,groupSeparator:','"/>
						</div>				
						<div style="width:31%;display: inline-block">
							<label style="width:112px;text-align: right;">返点基数:</label>
							<input id="basemny" name="basemny" class="easyui-numberbox" style="width:130px;height:26px;"
								data-options="validType:'length[0,12]',min:0,precision:2,groupSeparator:','" />
						</div>	
						<div style="width:38%;display: inline-block">
							<label style="width:100px;text-align: right;">返点金额:</label>
							<input id="rebatemny" name="rebatemny" class="easyui-numberbox" style="width:150px;height:26px;" 
								data-options="validType:'length[0,12]',min:0,precision:2,groupSeparator:','"/>
						</div>
					</div>
					<div class="time_col time_colp11">
						<div style="display: inline-block; margin-top: 5px;">
							<label style="width:100px;text-align: right;vertical-align: top;">备注:</label>
							<textarea id="memo" name="memo" class="easyui-textbox" style="width:804px;height:100px;"
								data-options="validType:'length[0,50]',multiline:true" ></textarea>
						</div>
					</div>
					<div style="text-align: center;margin-top:30px;">
					    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="saveAdd()">保存并新增</a> 
				        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="save()">保存</a> 
				        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="$('#addDlg').dialog('close');">取消</a>
			        </div>
			 	</div>
		   </form>
		</div>
		<!-- 新增对话框 end -->
		
		<!-- 修改对话框 begin -->
		<div id="editDlg" class="easyui-dialog" style="width:1000px;height:530px;padding:20px 20px;" 
			data-options="resizable:true,closed:true">
			<form id="editForm" method="post" style="margin-top:0px;">
				<div id="tableDiv" style="overflow-y: auto;">
				  	<div class="time_col time_colp11">
			          	<div style="width:30%;display: inline-block">
							<label style="width:100px;text-align:right;">&emsp;返点单号: </label>
							<input id="evcode" name="vcode" class="easyui-textbox" style="width:160px;height:26px;"
								data-options="validType:'length[0,30]'" />
					 	</div>
						<div style="width:30%;display: inline-block">
							<label style="text-align:right;width:102px;">期间：</label> 
							<select id="eyear" name="year" class="easyui-combobox" 
								data-options="required:true,editable:false" style="width:70px;height:27px;">
						    	<% DzfUtil.WriteYearOption(out);%>
							</select>
							<select id="eseason" name="season" class="easyui-combobox"  
								data-options="required:true,editable:false" style="width:94px;height:27px;">
			         			<option value="1">第一季度</option>
								<option value="2">第二季度</option>
								<option value="3">第三季度</option>
								<option value="4">第四季度</option>
							</select>
						</div>
					 	<div style="display: inline-block;width:38%;">
							<label style="width:100px;text-align: right;">&emsp;加盟商名称:</label>
						    <input id="ecorp" name="corp" class="easyui-textbox" style="width:220px;height:26px;"
								data-options="required:true,validType:'length[0,100]'" />
							<input id="ecorpid" name="corpid" type="hidden">
						</div>
					</div>
				 	<div class="time_col time_colp11">
					  	<div style="width:29%;display: inline-block">
							<label style="width:100px;text-align: right;">扣款金额:</label>
							<input id="edebitmny" name="debitmny" class="easyui-numberbox" style="width:160px;height:26px;" 
								data-options="readonly:true,validType:'length[0,12]',min:0,precision:2,groupSeparator:','"/>
						</div>				
						<div style="width:31%;display: inline-block">
							<label style="width:112px;text-align: right;">返点基数:</label>
							<input id="ebasemny" name="basemny" class="easyui-numberbox" style="width:130px;height:26px;"
								data-options="validType:'length[0,12]',min:0,precision:2,groupSeparator:','" />
						</div>	
						<div style="width:38%;display: inline-block">
							<label style="width:100px;text-align: right;">返点金额:</label>
							<input id="erebatemny" name="rebatemny" class="easyui-numberbox" style="width:150px;height:26px;" 
								data-options="validType:'length[0,12]',min:0,precision:2,groupSeparator:','"/>
						</div>
					</div>
					<div class="time_col time_colp11">
						<div style="display: inline-block; margin-top: 5px;">
							<label style="width:100px;text-align: right;vertical-align: top;">备注:</label>
							<textarea id="ememo" name="memo" class="easyui-textbox" style="width:804px;height:60px;"
								data-options="validType:'length[0,50]',multiline:true" ></textarea>
						</div>
					</div>
				 	<div class="time_col time_colp11">
					  	<div style="width:29%;display: inline-block">
							<label style="width:100px;text-align: right;">单据状态:</label>
							<input id="estatusname" name="statusname" class="easyui-textbox" style="width:160px;height:26px;" 
								data-options="readonly:true"/>
						</div>				
						<div style="width:31%;display: inline-block">
							<label style="width:112px;text-align: right;">录入人:</label>
							<input id="eopername" name="opername" class="easyui-textbox" style="width:130px;height:26px;"
								data-options="readonly:true" />
						</div>	
						<div style="width:38%;display: inline-block">
							<label style="width:100px;text-align: right;">录入时间:</label>
							<input id="eoperdate" name="operdate" class="easyui-textbox" style="width:150px;height:26px;" 
								data-options="readonly:true"/>
						</div>
					</div>
					<div style="text-align: center;margin-top:10px;">
				        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="save()">保存</a> 
				        <a href="javascript:void(0)" class="easyui-linkbutton" onclick="$('#addDlg').dialog('close');">取消</a>
			        </div>
			 	</div>
		   </form>
		   <!-- 审批历史begin -->
		   <div id = "history"></div>
		   
<!-- 		   <p class="slideA">
				<a href="javascript:;" style="color:#FFF;font-size: 14px;" class="btn-slideA active">审批历史</a>
		    </p>
			<div style="height:230px;overflow:auto;">
				<div style="" id="panelA">
					<div class="tall" style=" margin-top: 16px;">
						<div  class="Aroundly">
							<img src="../../images/tbpng_03.png" style="position: absolute; left: 90px;"/>
							<img src="../../images/pngg_03.png" style="position: absolute; left: 96px; top: 14px;"/>
						</div>
						<div class="state">
							<div>
								<font>2017-08-07 09:32:47</font>&emsp;<span>康总驳回修改</span>
							</div>
							<div>说明：没问题，通过</div>
						</div>
					</div>
					<div style="display: none;" id="panela">
						<div style="width:auto;">
							<div class="tall">
								<div  class="Aroundly">
									<img style="position: absolute; left: 92px;" src="../../images/xial_03.png" /> 
									<img style="position: absolute; left: 96px; top: 8px;" src="../../images/pngg_03.png" />
								</div>
								<div class="state">
									<div>
										<font>2017-08-07 09:32:47</font>&emsp;<span>康总驳回修改</span>
									</div>
									<div>说明：没问题，通过</div>
								</div>
							</div>
							<div class="tall">
								<div class="Aroundly">
									<img style="position: absolute; left: 92px;" src="../../images/xial_03.png" /> 
									<img style="position: absolute; left: 96px; top: 8px;" src="../../images/pngg_03.png" />
								</div>
								<div class="state">
									<div>
										<font>2017-08-07 09:32:47</font>&emsp;<span>康总驳回修改</span>
									</div>
									<div>说明：没问题，通过</div>
								</div>
							</div>
						</div>
					</div>
					<p class="slide">
						<a href="javascript:;" rel="external nofollow"
							class="btn-slide active"></a>
					</p>
				</div>
			</div> -->
			<!-- 审批历史end -->
		</div>
		<!-- 修改对话框end -->
		
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
				<!-- 状态   0：待提交；1：待确认；2：待审批；3：审批通过；4：待确认驳回；5：待审批驳回； -->
				<select id="qstatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
					style="width:100px;height:28px;">
					<option value="-1">全部</option>
					<option value="0">待提交</option>
					<option value="1">待确认</option>
					<option value="2">待审批</option>
					<option value="3">审批通过</option>
					<option value="9">已驳回</option>
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