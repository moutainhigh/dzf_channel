<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
<title>机构设置</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/branch/inststepup.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<style>
 
</style>
</head>
<body class="wrapper char" style="height:100%"> 
    <div class="easyui-layout" style="height:100%;">
	    <div class="char" data-options="region:'west',border:false,split:true" style="width:22%;border-width:1px 1px 1px 1px;" id="leftGrid">
			<div data-options="region:'north'" style="border-width:0px 0px 0px 0px;">
    		<div class="index_top mod-toolbar-top"  >
		 		<div class="right mod-btn" id="view_buttons">
	    	     	<div class="h30 h30-arrow" style="margin-left:60px;" id="filter">
	    	     		<span style="font-size:18px;font-weight:bold">机构设置</span>&nbsp;&nbsp;&nbsp;
					 </div>
					 <a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options=" plain:true" onclick="addInst()">新增</a> 
		        </div>
        	</div>
    	   </div>
			<table id="instgrid" ></table>
		</div>
		
	 	<div class="char" data-options="region:'center',border:true,split:true" style="width:50%;border-width:1px 1px 1px 1px;" id="rightGrid" >
	    	<div data-options="region:'north'" style="border-width:0px 0px 0px 0px;">
    		<div class="index_top mod-toolbar-top"  >
		 		<div class="right mod-btn" id="view_buttons">
	    	     	<div class="h30 h30-arrow" style="margin-left:60px;" id="filter">
	    	     		<span style="font-size:18px;font-weight:bold">公司设置-直营</span>&nbsp;&nbsp;&nbsp;
					 </div>
					  <a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options=" plain:true" onclick="addCorp()">新增</a>
			          <a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options=" plain:true" onclick="updateInst()">更换机构</a>
		        </div>
        	</div>
    	   </div>
			<table id="corpgrid" ></table>
	    </div>	
	    
	    <!-- 新增机构对话框  begin-->
		<div id="addInstDialog" class="easyui-dialog" style="height:200px;width:360px;overflow:hidden;padding-top:18px;" 
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<form id="inst_add" method="post">
				<input name="pk_bset" type="hidden">
				<input name="updatets" type="hidden">
				<div class="time_col time_colp10">
						<label style="width:85px;text-align:right"><i class="bisu">*</i>机构名称：</label> 
					    <input class="easyui-textbox" id="iname" name="name" style="width:200px;height:28px;"
						      data-options="required:true"/>
				</div>
				
				<div style="float:right;margin-top:20px;margin-right:116px;">
				    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onSave()">保存</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onCancel()">取消</a>
				</div>
			</form>
		</div>
		<!-- 新增机构对话框  end-->
		
		
		<!-- 新增公司对话框  begin-->
		<div id="addCorpDialog" class="easyui-dialog" style="height:300px;width:500px;overflow:hidden;padding-top:18px;" 
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<form id="corp_add" method="post">
				<input name="pk_bcorp" type="hidden">
				<input name="updatets" type="hidden">
				<input name="pk_bset" id ="bset" type="hidden">
				<div class="time_col time_colp10">
						<label style="width:100px;text-align:right;margin-left:52px;"><i class="bisu">*</i>企业识别号：</label> 
					    <input class="easyui-textbox" id="entname" name="name" style="width:200px;height:28px;"
						      data-options="required:true"/>
				</div>
				<div class="time_col time_colp10">
						<label style="width:100px;text-align:right;margin-left:52px;">公司名称：</label> 
					    <input class="easyui-textbox" id="cname" name="uname" style="width:200px;height:28px;"
						      data-options="required:true"  validType= "remote['<%=request.getContextPath() %>/branch/setup!queryCorpname.action','name' ]" 
						      invalidMessage= "此公司名称已存在"/>
				</div>
				<div class="time_col time_colp10">
						<label style="width:100px;text-align:right;margin-left:52px;">联系人：</label> 
					    <input class="easyui-textbox" id="linkman" name="lman" style="width:200px;height:28px;"
						      data-options="required:true"/>
				</div>
				<div class="time_col time_colp10">
						<label style="width:100px;text-align:right;margin-left:52px;">联系方式：</label> 
					    <input class="easyui-textbox" id="phone" name="phone" style="width:200px;height:28px;"
						      data-options="required:true"/>
				</div>
				<div class="time_col time_colp10">
						<label style="width:100px;text-align:right;margin-left:52px;">备注：</label> 
					    <input class="easyui-textbox" id="vmemo" name="memo" style="width:200px;height:28px;"/>
				</div>
				
				<div style="float:right;margin-top:20px;margin-right:116px;">
				    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onSaveCorp()">保存</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onCancel()">取消</a>
				</div>
			</form>
		</div>
		<!-- 新增公司对话框  end-->
		
		 <!-- 更换机构对话框  begin-->
		<div id="updateInstDialog" class="easyui-dialog" style="height:200px;width:360px;overflow:hidden;padding-top:18px;" 
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<form id="inst_update" method="post">
				<input name="pk_bcorp" type="hidden">
				<input name="updatets" type="hidden">
				<div class="time_col time_colp10">
						<label style="width:85px;text-align:right"><i class="bisu">*</i>所属机构：</label> 
					    <input class="easyui-combobox"  style="width:200px;height:28px;"
						      data-options="required:true"/>
				</div>
				
				<div style="float:right;margin-top:20px;margin-right:116px;">
				    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onSave()">保存</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="onCancel()">取消</a>
				</div>
			</form>
		</div>
		<!-- 更换机构对话框  end-->
		
		
	</div>
	
</body>
</html>
