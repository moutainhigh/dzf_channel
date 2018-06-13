<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>付款单录入</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../../css/chnpaybill.css");%> rel="stylesheet" /></head>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/payment/chnpaybill.js");%> charset="UTF-8" type="text/javascript"></script>
<style>
.visitplan_col label{ margin-top:0px;}
.panel-body{overflow:auto}
</style>
</head>
<body>
	<div class="wrapper">
		<div id="listPanel" class="wrapper" style="width: 100%;overflow:hidden; height: 100%;">
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
							<a href="javascript:void(0)"  style="font-size:14;color:blue;" onclick="loadData(-1)">全部</a>
							<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; " onclick="loadData(-2)">待提交</a>
							<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px;margin-right:15px; " onclick="loadData(-3)">已驳回</a>
						</div>
					</div>
					
					<div class="left mod-crumb">
						<div class="h30 h30-arrow" id="filter">
							<label class="mr5">加盟商：</label>
							<input style="height:28px;width:250px" class="easyui-textbox" id="filter_value" prompt="请输入加盟商名称,按Enter键 "/> 
						</div>
					</div>
					<div class="right">
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="add()" >新增</a> 
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="edit()">修改</a> 
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="commit()">提交</a> 
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="uncommit()">取消提交</a>
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="del()">删除</a>
					</div>
				</div>
			</div>
			
			<div class="qijian_box" id="qrydialog" style="display: none; width: 450px; height: 230px">
				<s class="s" style="left: 25px;"><i class="i"></i> </s>
				<h3>
					<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="width: 80px;text-align:right">付款日期：</label>
					<font>
						<input id="qddate" name="ddate"  class="easyui-datebox" style="width:130px;height:27px;" />
					</font>
					<font>-</font>
					<font>
						<input id="qdpdate"  name="dpdate"  class="easyui-datebox" style="width:130px;height:27px;"/>
					</font>
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">单据状态：</label>
					<select id="qstatus" class="easyui-combobox" data-options="panelHeight:'auto'" style="width:100px;height:28px;">
						<option value="-1">全部</option>
						<option value="1">待提交</option>
						<option value="5">待审批</option>
						<option value="2">待确认</option>
						<option value="3">已确认</option>
						<option value="4">已驳回</option>
					</select>
					<label style="width:80px;text-align:right">付款类型：</label>
					<select id="qiptype" class="easyui-combobox" data-options="panelHeight:'auto'" style="width:100px;height:28px;">
						<option value="-1">全部</option>
						<option value="1">保证金</option>
						<option value="2">预付款</option>
					</select>
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">加盟商：</label>
					<input id="qcorpnm" class="easyui-textbox" style="width:290px;height:28px;" />
					<input id="qcorpid" type="hidden">
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">支付方式：</label>
					<select id="qipmode" class="easyui-combobox" data-options="panelHeight:'auto'" style="width:100px;height:28px;">
						<option value="-1">全部</option>
						<option value="1">银行转账</option>
						<option value="2">支付宝</option>
						<option value="3">微信</option>
						<option value="4">其他</option>
					</select>
					<label style="text-align:right;width: 43px;">大区：</label> 
					<input id="aname"  name="aname" class="easyui-combobox" style="width: 133px; height: 28px;" 
						data-options="required:false,valueField:'name',textField:'name',panelHeight:100" editable="false" />  
				</div>
				<p>
					<a class="ui-btn save_input" onclick="clearParams()">清除</a>
					<a class="ui-btn save_input" onclick="reloadData()">确定</a>
					<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
				</p>
			</div>
			
			<div class="mod-inner">
				<div id="dataGrid" class="grid-wrap">
					<table id="chn_grid"></table>
				</div>
			</div> 
	 </div>	
	<!--渠道商管理付款单卡片界面开始-->
		<div id="htDialog">
			<form id="chn_add" method="post" enctype="multipart/form-data" style="height:400px;width:1000px;overflow:hidden;padding-top:18px;">
				<div class="time_col time_colp11" style="display:none">
					<label style="text-align:right">主键</label> 
					<input id="billid" name="billid" class="easyui-textbox"> 
					<input id="status" name="status" class="easyui-textbox"> 
					<input id="tstp" name="tstp" class="easyui-textbox"> 
				</div>
				<div class="time_col time_colp10">
					<div style="width: 32%; display: inline-block;">
						<label style="text-align: right; width: 100px;">单据号：</label> 
						<input id="vcode" name="vcode" class="easyui-textbox"  data-options="validType:'length[0,30]'" style="width: 50%; height: 28px; text-align: left"> 
					</div>
					<div style="width: 32%; display: inline-block;">
						<label style="text-align: right; width: 100px;">付款日期：</label> 
						<input id='dpdate' name="dpdate" class="easyui-datebox"  data-options="required:true" style="width: 50%; height: 28px; text-align: left">
					</div>
					<div style="width:32%;display: inline-block;">
						<label style="width:100px;text-align: right;">加盟商名称:</label>
					    <input id="corpnm" name="corpnm" class="easyui-textbox" style="width:50%;height:28px;" data-options="required:true" />
						<input id="corpid" name="corpid" type="hidden">
						<input id="ovince" name="ovince" type="hidden">
					</div>
				</div>

				<div class="time_col time_colp10">
					<div style="width: 32%; display: inline-block;">
						<label style="text-align: right; width: 100px;">付款人：</label> 
						<input id="vhname" name="vhname" data-options="validType:'length[0,30]'" class="easyui-textbox" style="width: 50%; height: 28px; text-align: left">
					</div>
					<div style="width: 32%; display: inline-block;">
						<label  style="text-align: right; width: 100px;"><i class="bisu">*</i>支付方式：</label> 
							<select id='ipmode' name="ipmode" class="easyui-combobox" editable="false" data-options="required:true,panelHeight:70" style="width:50%; height: 30px;">
								<option value="1">银行转账</option>
								<option value="2">支付宝</option>
								<option value="3">微信</option>	
								<option value="4">其他</option>	
							</select>
					</div>
					<div style="width: 32%; display: inline-block;">
						<label style="text-align: right; width: 100px;"><i class="bisu">*</i>付款金额：</label> 
						<input id="npmny" name="npmny" class="easyui-numberbox" data-options="required:true, min:0, precision:2" style="width: 50%; height: 28px; text-align: left"></input>
					</div>
				</div>
				<div class="time_col time_colp10">
					<div style="width: 32%; display: inline-block;">
						<label  style="text-align: right; width: 100px;"><i class="bisu">*</i>付款类型：</label> 
							<select id='iptype' name="iptype" class="easyui-combobox" editable="false" data-options="required:true,panelHeight:50" style="width:50%; height: 30px;">
								<option value="2">预付款</option>
								<option value="1">保证金</option>
							</select>
					</div>
					<div style="width: 32%; display: inline-block;">
						<label style="text-align: right; width: 100px;">付款银行：</label> 
						<input id="vbname" name="vbname" class="easyui-textbox" data-options="required:false,validType:['length[0,25]']" style="width:50%;height:28px;text-align:left">
					</div>
					<div style="width: 32%; display: inline-block;">
						<label style="text-align: right; width: 100px;">付款账号：</label> 
						<input id="vbcode" name="vbcode" class="easyui-textbox" data-options="required:false,validType:['length[0,25]']" style="width:50%;height:28px;text-align:left">
					</div>
				</div>
				<div class="time_col time_colp11" style="margin-bottom:0px; padding-bottom:5px;">
					<div style="width:100%;display: inline-block;">
						<label style="text-align:right;width:114px;    vertical-align: top;">备注：</label>
						<textarea id="memo" name="memo" class="easyui-textbox" data-options="multiline:true,validType:'length[0,150]'" style="width:79%;height:60px;"></textarea>
					</div>
				</div>
				<div class="time_col time_colp11" style="margin:10px 0px 10px 0px;">
			       <div style="width:24%;display: inline-block;white-space: nowrap;">
					<label style="text-align:right;width:114px; vertical-align: top;">添加附件：</label>
					<div class="uploadImg" style="display: inline-block;">
					   	<div class="imgbox1">
						    <div class="imgnum">
						        <input type="file" class="filepath2" name="pFile"  accept="image/gif,image/jpeg,image/jpg,image/png"/>
						        <span class="Dlelete" id ="span1" ><img src="../../images/Dustbin.png"/></span>
						        <img src="../../images/wer_03.png" class="img11" id="img11"/>
						        <img src="" class="img22" id="img12" />
						    </div>
						</div>
					</div>	
					</div>
				</div>
				<div style="float:right;margin-top:40px;margin-right:76px;">
				      <a href="javascript:void(0)" id="save" class="ui-btn ui-btn-xz" title="Ctrl+S" onclick="save()">保存</a> 
					<a href="javascript:void(0)" id="cancel" class="ui-btn ui-btn-xz" title="CTRL+Z" onclick="cancel()">取消</a>
				</div>
			</form>
		</div>
		<!--渠道商管理付款单卡片界面结束-->
	</div>
	<!--用户列表-->
	<div id="kj_dialog"></div>
	<!-- 查看附件begin -->
	<div id="attachViewDlg" style="display: none;">	
	  	<div class="menu_entrance menu_entrances" style="margin-top:0;margin-right:5px;">
			<div class="entrance_block_tu" id="tpght" style="overflow-y:auto;height:85%">
				<ul class="tu_block" id="attachs"></ul>
			</div>
		</div>
	</div>
	<!-- 查看附件end -->
	<div id="tpfd"></div>
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
