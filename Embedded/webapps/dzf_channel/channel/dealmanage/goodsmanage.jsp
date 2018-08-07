<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>合同审核</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/chnpaybill.css");%> rel="stylesheet" />
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/dealmanage/goodsmanage.js");%> charset="UTF-8" type="text/javascript"></script>

</head>
<body>
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
				
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="add()">新增</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="publish()">发布</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="off()">下架</a>
				</div>
			</div>
		</div>
		
		<!-- 查询对话框 begin -->
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:190px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">商品编码：</label>
				<input id="gcode" class="easyui-textbox" style="width:290px;height:28px;"/>
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">商品名称：</label>
				<input id="gname" class="easyui-textbox" style="width:290px;height:28px;"/>
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">合同状态：</label>
				<select id="destatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
					editable="false" style="width:290px;height:28px;">
					<option value="-1">全部</option>
					<option value="1">已保存</option>
					<option value="2">已发布</option>
					<option value="3">已下架</option>
				</select>
			</div>
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
		
	<!-- 新增对话框 -->
		<div id="cbDialog" class="easyui-dialog" style="height:500px;width:860px;overflow:hidden;padding-top:18px;" data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
				<form id="chn_add" method="post">
				<div class="time_col time_colp10">
					<div style="width: 46%; display: inline-block;">
						<label style="text-align: right; width: 120px;">商品编码：</label> 
						<input class="easyui-textbox"  data-options="validType:'length[0,30]'" style="width:66%; height: 28px; text-align: left"> 
					</div>
					<div style="width:46%;display: inline-block;">
						<label style="width:120px;text-align: right;">商品名称：</label>
					    <input class="easyui-textbox" style="width:66%;height:28px;" />
					</div>
				</div>

				<div class="time_col time_colp10">
					<div style="width: 46%; display: inline-block;">
						<label style="text-align: right; width: 120px;">单价：</label> 
						<input class="easyui-textbox" style="width:66%; height: 28px; text-align: left">
					</div>
					<div style="width: 46%; display: inline-block;">
						<label style="text-align: right; width: 120px;">单位：</label> 
						<input class="easyui-textbox" style="width:66%; height: 28px; text-align: left"></input>
					</div>
				</div>
				<div class="time_col time_colp10">
					<div style="width: 46%; display: inline-block;">
						<label style="text-align: right; width: 120px;">付款银行：</label> 
						<input class="easyui-textbox" style="width:66%;height:28px;text-align:left">
					</div>
				</div>
				<div class="time_col time_colp11" style="margin-bottom:0px; padding-bottom:5px;">
					<div style="width:100%;display: inline-block;">
						<label style="text-align:right;width:135px;vertical-align: top;">备注：</label>
						<textarea class="easyui-textbox" data-options="multiline:true,validType:'length[0,150]'" style="width:74%;height:100px;"></textarea>
					</div>
				</div>
				<div class="time_col time_colp11" style="margin:10px 0px 10px 0px;">
			       <div style="width:24%;display: inline-block;white-space: nowrap;">
					<label style="text-align:right;width:135px; vertical-align: top;">商品图片：</label>
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
				      <a href="javascript:void(0)" class="ui-btn ui-btn-xz" title="Ctrl+S" onclick="">保存</a> 
					<a href="javascript:void(0)"  class="ui-btn ui-btn-xz" title="CTRL+Z" onclick="">取消</a>
				</div>
			</form>
	</div>
		
	</div>
</body>
</html>