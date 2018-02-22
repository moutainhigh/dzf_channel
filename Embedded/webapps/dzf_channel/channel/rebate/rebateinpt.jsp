<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
<title>返点单录入</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>
	rel="stylesheet">
<script
	src=<%UpdateGradeVersion.outversion(out, request.getContextPath() + "/js/easyuiext.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/rebate/rebateinpt.js");%>
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
						<strong id="querydate"></strong>
						<span class="arrow-date"></span>
					</div>
				</div>
				<div class="left mod-crumb">
					<span class="cur"></span>
				</div>
				<div class="left mod-crumb">
					<div style="margin:6px 0px 0px 10px;float:right;font-size:14px;">
						<!-- <a href="javascript:void(0)"  style="font-size:14;color:blue;" 
							onclick="qryData(-1)">全部</a> -->
						<a href="javascript:void(0)" style="font-size:14;color:blue;margin-left:15px;" 
							onclick="qryData(2)">待提交</a>
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px;margin-right:15px;" 
							onclick="qryData(3)">已驳回</a>
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
						data-options="plain:true" onclick="onAdd()">新增</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onEdit()">修改</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onCommit()">提交</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onDelete()">删除</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onImport()">导入</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" 
						data-options="plain:true" onclick="onExport()">导出</a>
				</div>
			</div>
		</div>
		<div class="qijian_box" id="qrydialog" style="display: none; width: 450px; height: 230px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<form id="query_form">
				<h3>
					<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="width: 80px;text-align:right">付款日期：</label>
					<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
					<font>-</font>
					<font><input name="edate" type="text" id="edate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">单据状态：</label>
					<select id="status" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:100px;height:28px;">
						<option value="-1">全部</option>
						<option value="2">待确认</option>
						<option value="3">已确认</option>
						<option value="4">已驳回</option>
					</select>
					<label style="width:80px;text-align:right">付款类型：</label>
					<select id="iptype" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:100px;height:28px;">
						<option value="-1">全部</option>
						<option value="1">保证金</option>
						<option value="2">预付款</option>
					</select>
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">加盟商：</label>
					<input id="channel_select" class="easyui-textbox" style="width:290px;height:28px;" />
					<input id="pk_account" type="hidden">
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">支付方式：</label>
					<select id="ipmode" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:150px;height:28px;">
						<option value="-1">全部</option>
						<option value="1">银行转账</option>
						<option value="2">支付宝</option>
						<option value="3">微信</option>
						<option value="4">其他</option>
					</select>
				</div>
			</form>
			<p>
				<a class="ui-btn save_input" onclick="clearParams()">清除</a>
				<a class="ui-btn save_input" onclick="reloadData()">确认</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
	</div>
	<!-- 列表界面end -->
	
	<!-- 卡片界面begin -->
	<div id="Card_panel"  style="height:98%; width: 100%; overflow:auto;">
		<div id = "btn1" class="mod-toolbar-top">
	        <div class="mod-toolbar-content">
		        <div class="right">
					<a href="javascript:void(0)" id="retubtn" class="ui-btn ui-btn-xz" title="CTRL+Z" onclick="cancel()">返回</a>
		         	<a href="javascript:void(0)" id="addbtn" class="ui-btn ui-btn-xz" title="Alt+N" onclick="addpanel()">新增</a>
		        </div> 
	        </div>
		</div>
	  	<div class="mod-inner">
	   		<div id="mainId"  style="height: 81%; width:100%;">
				<form id="charge_head" method="post" class="top-margin" style="padding-top:18px;" enctype="multipart/form-data">
				<div class="time_col time_colp11 sty_refer">
				    <input type="hidden" name="chargeid1" id="chargeid1">
				    <input type="hidden" name="pkcorp1" id="pkcorp1">
				    <input type="hidden" name="type" id="type">
					<label style="text-align:right">退款日期：</label>
					<input type="text" id="sfdate1" class="easyui-datebox"  name="sfdate1" style="width:15%;height:28px;"/>
					<label style="text-align:right">单据编码：</label>
					<input id="vbc11" name="vbc1" class="easyui-textbox" data-options = "required:true" 
						style="width:15%;height:28px;text-align:left">
				</div>
				<div class="time_col time_colp11 sty_refer">
					<label style="text-align:right"><font color="red">*</font>银行转账：</label>
					<input id="nbm11" name="nbm1" class="easyui-numberbox" data-options ="max:0,precision:2,groupSeparator:','"  
						style="width:15%;height:28px;text-align:left" value="" />
					<label style="text-align:right"><font color="red">*</font>现金：</label>
					<input id="ncm11" name="ncm1" class="easyui-numberbox" data-options ="max:0,precision:2,groupSeparator:','"   
						style="width:15%;height:28px;text-align:left">
					<label style="text-align:right">预收退款 ：</label>
					<input id="nyumny" name="nyumny" class="easyui-numberbox" data-options ="readonly:true,max:0,precision:2,groupSeparator:','"  
						style="width:15%;height:28px;text-align:left"/>
				</div>
				<div class="time_col time_colp11 sty_refer">
					<label style="text-align:right;">直接退款 ：</label>
					<input id="ncmny" name="ncmny" class="easyui-numberbox" data-options ="readonly:true,max:0,precision:2,groupSeparator:','"  
						style="width:15%;height:28px;text-align:left"/>
					<label style="text-align:right">合同退款 ：</label>
					<input id="nhtmny" name="nhtmny" class="easyui-numberbox" data-options ="readonly:true,max:0,precision:2,groupSeparator:',' "  
						style="width:15%;height:28px;text-align:left"/>
					<label style="text-align:right">经办人：</label>
					<input id="inputname" name="inputname" class="easyui-textbox" style="width:15%;height:28px;" /> 
					<input id="inputnameid" name="inputer" type="hidden">
				</div>
				</form>
	   		</div>
	  	</div>
	</div>
	<!-- 卡片界面end -->
<!-- 新增返点编辑对话框 -->
<div id="addCorpDlg" class="easyui-dialog" style="width:1000px;height:360px;padding:20px 20px;" data-options="resizable:true,closed:true">
	<form id="addCorpForm" method="post" style="margin-top:0px;">
		<div id="tableDiv" style="height:280px;overflow-y: auto;">
			  <div class="time_col time_colp11">
	          	<div style="width:30%;display: inline-block">
					<label style="width:100px;text-align: right;">&emsp;返点单号: </label>
					<input  class="easyui-textbox" style="width:160px;height:26px;"
						data-options="validType:'length[0,30]'" />
			 	</div>
			<div style="width:30%;display: inline-block">
				<label style="text-align:right;width:102px;">期间：</label> 
				<select  class="easyui-combobox" data-options="editable:false" style="width:70px;height:27px;">
			     <option>2018</option>
			     <option>2017</option>
				</select>
				<select  class="easyui-combobox"  data-options="editable:false" style="width:94px;height:27px;">
		         <option>第一季度</option>
			     <option>第二季度</option>
				</select>
			</span>
			</div>
			 	<div style="display: inline-block;width:38%;">
					<label style="width:100px;text-align: right;">&emsp;加盟商名称:</label>
				    <input class="easyui-searchbox" style="width:220px;height:26px;"
						data-options="required:true,validType:'length[0,100]'" />
				</div>
			</div>
		 	<div class="time_col time_colp11">
			  	<div style="width:29%;display: inline-block">
					<label style="width:100px;text-align: right;">扣款金额:</label>
					<input  class="easyui-textbox" style="width:160px;height:26px;" 
						data-options="validType:'length[0,12]'"/>
				</div>				
				<div style="width:31%;display: inline-block">
					<label style="width:112px;text-align: right;">返点基数:</label>
					<input  class="easyui-textbox" style="width:130px;height:26px;" />
				</div>	
				<div style="width:38%;display: inline-block">
					<label style="width:100px;text-align: right;">返点金额:</label>
					<input class="easyui-textbox" style="width:150px;height:26px;" />
				</div>
			</div>
			<div class="time_col time_colp11">
				<div style="display: inline-block; margin-top: 5px;">
					<label style="width:100px;text-align: right;vertical-align: top;">备注:</label>
					<textarea class="easyui-textbox" data-options="multiline:true" style="width:804px;height:100px;"></textarea>
				</div>
			</div>
		<div style="text-align: center;margin-top:30px;">
		    <a href="javascript:void(0)" class="easyui-linkbutton" onclick="">保存并新增</a> 
	         <a href="javascript:void(0)" class="easyui-linkbutton" onclick="">保存</a> 
	         <a href="javascript:void(0)" class="easyui-linkbutton" onclick="$('#addCorpDlg').dialog('close');">取消</a>
        </div>
	 </div>
   </form>
</div>
<!-- 新增返点编辑对话框 -->
<!--卡片返点查看对话框 -->
	<div id="addDlg" class="easyui-dialog" style="width:1100px;height:430px;padding:10px 20px;" data-options="resizable:true,closed:true">
		<form id="addForm" method="post" style="margin-top:0px;">
			<div id="tableDiv" style="height:370px;overflow-y: auto;">
	       <div style="border-bottom: 1px solid;padding: 10px 0px 10px;">
				<div class="time_col time_colp11">
				 	<div style="width:34%;display: inline-block">
						<label style="width:100px;text-align: right;">审批状态：</label>
						<input class="easyui-numberbox" style="width:240px;height:26px;text-align:left"></input>
					</div>	
					<div style="width:28%;display: inline-block">
						<label style="width:100px;text-align: right;">审批人：</label>
						<input class="easyui-numberbox"  style="width:140px;height:26px;text-align:left"></input>
					</div>
					<div style="width:28%;display: inline-block">
						<label style="width:100px;text-align: right;">确认时间：</label>
						<input class="easyui-numberbox"  style="width:140px;height:26px;text-align:left"></input>
					</div>
				</div>
				<div class="time_col time_colp11">
				 	<div style="width:100%;display: inline-block">
						<label style="width:100px;text-align: right;">说明：</label>
						<input class="easyui-numberbox" style="width:806px;height:26px;text-align:left"></input> 
					</div>	
		         </div>	
					
		</div>
		 <div style="border-bottom: 1px solid; padding: 10px 0px 10px;">
					<div class="time_col time_colp11">
				 	<div style="width:34%;display: inline-block">
						<label style="width:100px;text-align: right;">确认状态：</label>
						<input class="easyui-numberbox" style="width:240px;height:26px;text-align:left"></input>
					</div>	
					<div style="width:28%;display: inline-block">
						<label style="width:100px;text-align: right;">确认人：</label>
						<input class="easyui-numberbox"  style="width:140px;height:26px;text-align:left"></input>
					</div>
					<div style="width:28%;display: inline-block">
						<label style="width:100px;text-align: right;">确认时间：</label>
						<input class="easyui-numberbox"  style="width:140px;height:26px;text-align:left"></input>
					</div>
				</div>

				<div class="time_col time_colp11">
				 	<div style="width:100%;display: inline-block">
						<label style="width:100px;text-align: right;">说明：</label>
						<input class="easyui-numberbox" style="width:806px;height:26px;text-align:left"></input> 
					</div>	
				</div>
			</div>		
				
					<div class="time_col time_colp11" style="padding-top:10px">
				 	<div style="width:40%;display: inline-block">
						<label style="width:100px;text-align: right;">返点单号：</label>
						<input class="easyui-numberbox" style="width:240px;height:26px;text-align:left"></input>
					</div>	
					<div style="width:28%;display: inline-block">
						<label style="width:100px;text-align: right;">返点所属季度：</label>
						<input class="easyui-numberbox"  style="width:180px;height:26px;text-align:left"></input>
					</div>
					<div style="width:28%;display: inline-block">
						<label style="width:100px;text-align: right;">加盟商名称：</label>
						<input class="easyui-numberbox"  style="width:180px;height:26px;text-align:left"></input>
					</div>
				</div>
				<div class="time_col time_colp11">
				 	<div style="width:40%;display: inline-block">
						<label style="width:100px;text-align: right;">扣款金额：</label>
						<input class="easyui-numberbox" style="width:240px;height:26px;text-align:left"></input>
					</div>	
					<div style="width:28%;display: inline-block">
						<label style="width:100px;text-align: right;">返点基数：</label>
						<input class="easyui-numberbox"  style="width:180px;height:26px;text-align:left"></input>
					</div>
					<div style="width:28%;display: inline-block">
						<label style="width:100px;text-align: right;">返点金额：</label>
						<input class="easyui-numberbox"  style="width:180px;height:26px;text-align:left"></input>
					</div>
				</div>

				<div class="time_col time_colp11">
				 	<div style="width:100%;display: inline-block">
						<label style="width:100px;text-align: right;">备注：</label>
						<input class="easyui-numberbox" style="width:910px;height:26px;text-align:left"></input> 
					</div>		
				</div>
				<div class="time_col time_colp11">
				 	<div style="width:40%;display: inline-block">
						<label style="width:100px;text-align: right;">单据状态：</label>
						<input class="easyui-numberbox" style="width:240px;height:26px;text-align:left"></input>
					</div>	
					<div style="width:28%;display: inline-block">
						<label style="width:100px;text-align: right;">录入人：</label>
						<input class="easyui-numberbox"  style="width:180px;height:26px;text-align:left"></input>
					</div>
					<div style="width:28%;display: inline-block">
						<label style="width:100px;text-align: right;">录入时间：</label>
						<input class="easyui-numberbox"  style="width:180px;height:26px;text-align:left"></input>
					</div>
				</div>
			</div>
		</form>
	</div>
<!--卡片返点查看对话框 -->
</body>

</html>