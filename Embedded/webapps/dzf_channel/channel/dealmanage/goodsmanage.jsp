<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>商品管理</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../../css/dealmanage/goodsmanage.css");%> rel="stylesheet" />
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/validate.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/dealmanage/goodsmanage.js");%> charset="UTF-8" type="text/javascript"></script>
<script
	src="<%=request.getContextPath()%>/jslib/jquery-easyui-1.4.3/datagrid-detailview.js"
	charset="UTF-8" type="text/javascript"></script>
<script type="text/javascript">
	var maxsize = 1*1024*1024;//10M
	var errMsg = "上传的图片不能超过1M！！！";
	/* var tipMsg = "您的浏览器暂不支持计算上传文件的大小，确保上传文件不要超过1M，建议使用IE、FireFox、Chrome浏览器。"; */
	var  browserCfg = {};
	var ua = window.navigator.userAgent;
	if (ua.indexOf("MSIE")>=1){
		browserCfg.ie = true;
	}else if(ua.indexOf("Firefox")>=1){
		browserCfg.firefox = true;
	}else if(ua.indexOf("Chrome")>=1){
		browserCfg.chrome = true;
	}
</script>

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
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:220px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">商品分类：</label>
				<input id="qgtype" class="easyui-combobox" style="width:290px;height:28px;"
					data-options="valueField:'id', textField:'name', panelHeight:'200px'" />
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">商品编码：</label>
				<input id="qgcode" class="easyui-textbox" style="width:290px;height:28px;"/>
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">商品名称：</label>
				<input id="qgname" class="easyui-textbox" style="width:290px;height:28px;"/>
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">商品状态：</label>
				<select id="qstatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
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
		
		<!-- 新增/修改对话框  begin-->
		<div id="cbDialog" class="easyui-dialog" style="height:500px;width:860px;overflow:hidden;padding-top:18px;" 
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<form id="goods_add" method="post" enctype="multipart/form-data">
				<input id="status" name="status" type="hidden">
				<input id="pubdate" name="pubdate" type="hidden">
				<input id="dofdate" name="dofdate" type="hidden">
				<input id="operid" name="operid" type="hidden">
				<input id="operdate" name="operdate" type="hidden">
				<input id="updatets" name="updatets" type="hidden">
				<input id="gid" name="gid" type="hidden">
				<input id="price" name="price" type="hidden">
				<div class="time_col time_colp10">
					<div style="width: 46%; display: inline-block;">
						<label style="text-align: right; width: 120px;"><i class="bisu">*</i>商品分类：</label> 
						<input class="easyui-combobox" id="gtype" name="gtype" 
							data-options="valueField:'id', textField:'name', panelHeight:'200px',required:true,"  
							style="width:66%; height: 28px; text-align: left">
					</div>
					<div style="width: 46%; display: inline-block;">
						<label style="text-align: right; width: 120px;">商品编码：</label> 
						<input class="easyui-textbox"  id="gcode" name="gcode" data-options="validType:'length[0,30]'" 
							style="width:66%; height: 28px; text-align: left"> 
					</div>
					
				</div>
               	<div class="time_col time_colp10">
               		<div style="width:46%;display: inline-block;">
						<label style="width:119px;text-align: right;"><i class="bisu">*</i>商品名称：</label>
					    <input class="easyui-textbox" id="gname" name="gname" data-options="required:true,validType:'length[0,50]'"
					    	style="width:66%;height:28px;" />
					</div>
               		<div style="width: 46%; display: inline-block;">
						<label style="text-align: right; width: 120px;"><i class="bisu">*</i>单位：</label> 
						<input id="measid" name="measid" class="easyui-combobox" 
							data-options="valueField:'id', textField:'name', panelHeight:'200px',required:true," 
							style="width:50%; height: 28px; text-align: left"></input>
						<input id="mname" name="mname" type="hidden">
						<a href="javascript:void(0)" style="margin-bottom:0px;" class="ui-btn ui-btn-xz"  onclick="addMeas()">添加</a>
					</div>
               	</div>
				<div class="time_col time_colp11" style="margin-bottom:0px; padding-bottom:5px;">
					<div style="width:100%;display: inline-block;">
						<label style="text-align:right;width:135px;vertical-align:top;">商品说明：</label>
						<textarea id ="note" name="note" class="easyui-textbox" data-options="multiline:true,validType:'length[0,150]'" 
							style="width:74%;height:100px;">
						</textarea>
					</div>
				</div>
				<div class="time_col time_colp11" style="margin:10px 0px 10px 0px;">
			    	<div style="display: inline-block;white-space: nowrap;">
						<label style="text-align:right;width:135px; display: inline-block;vertical-align: top;">商品图片：</label>
						<div style="display: inline-block;white-space: nowrap;width:700px;height:120px;overflow:auto">
							 <div class="uploadImg"  style="display: inline-block;white-space: nowrap;width:100%;">
								<div style="overflow: auto;" id="image1"></div> 
							</div>
						</div>
					</div>
				</div>
				
				<div style="float:right;margin-top:40px;margin-right:76px;">
				    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" title="Ctrl+S" onclick="onSave()">保存</a> 
					<a href="javascript:void(0)"  class="ui-btn ui-btn-xz" title="CTRL+Z" onclick="onCancel()">取消</a>
				</div>
			</form>
		</div>
		<!-- 新增/修改对话框  end-->
		
		<!-- 计量单位对话框  begin-->
		<div id="jlDialog" class="easyui-dialog" style="width:400px;height:220px;padding-top:30px;" 
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<form id="meas_add" method="post">
				<div class="time_col time_colp11">
					<div style="display: inline-block;">
						<label style="text-align:right;width:140px;">单位：</label>
						<input id="amname" name="mname" class="easyui-textbox" 
							data-options="validType:'length[0,2]'" style="width:150px;height:25px;"/>
					</div>
				</div>
			</form>
			<div style="text-align:center;margin-top:40px;">
			    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" title="Ctrl+S" onclick="measSave()">保存</a> 
				<a href="javascript:void(0)"  class="ui-btn ui-btn-xz" title="CTRL+Z" onclick="measCancel()">取消</a>
			</div>
		</div>
		<!-- 计量单位对话框  end-->
		
		<!-- 详情对话框  begin-->
		<div id="infoDlg" class="easyui-dialog" style="height:500px;width:860px;overflow:hidden;padding-top:18px;" 
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<form id="goods_info" method="post" enctype="multipart/form-data">
				<input id="istatus" name="status" type="hidden">
				<input id="ipubdate" name="pubdate" type="hidden">
				<input id="idofdate" name="dofdate" type="hidden">
				<input id="ioperid" name="operid" type="hidden">
				<input id="ioperdate" name="operdate" type="hidden">
				<input id="iupdatets" name="updatets" type="hidden">
				<input id="igid" name="gid" type="hidden">
				
				<div class="time_col time_colp10">
					<div style="width: 46%; display: inline-block;">
						<label style="text-align: right; width: 120px;">商品分类：</label> 
						<input class="easyui-textbox" id="igtypenm" name="gtypenm" data-options="readonly:true,"  
							style="width:66%; height: 28px; text-align: left">
					</div>
					<div style="width: 46%; display: inline-block;">
						<label style="text-align: right; width: 120px;">商品编码：</label> 
						<input class="easyui-textbox"  id="igcode" name="gcode" data-options="readonly:true" 
							style="width:66%; height: 28px; text-align: left"> 
					</div>
				</div>
              	<div class="time_col time_colp10">
               		<div style="width:46%;display: inline-block;">
						<label style="width:119px;text-align: right;">商品名称：</label>
					    <input class="easyui-textbox" id="igname" name="gname" data-options="readonly:true"
					    	style="width:66%;height:28px;" />
					</div>
               		<div style="width: 46%; display: inline-block;">
						<label style="text-align: right; width: 120px;">单位：</label> 
						<input class="easyui-textbox" id="imname" name="mname" data-options="readonly:true" 
							style="width:66%; height: 28px; text-align: left"></input>
					</div>
               	</div>
				
				<div class="time_col time_colp10">
					<div style="width:46%;display: inline-block;">
						<label style="width:119px;text-align: right;">状态：</label>
					    <input class="easyui-textbox" id="istaname" name="staname" data-options="readonly:true"
					    	style="width:66%;height:28px;" />
					</div>
				</div>
				
				<div class="time_col time_colp11" style="margin-bottom:0px; padding-bottom:5px;">
					<div style="width:100%;display: inline-block;">
						<label style="text-align:right;width:135px;vertical-align:top;">商品说明：</label>
						<textarea id ="inote" name="note" class="easyui-textbox" data-options="multiline:true,readonly:true" 
							style="width:74%;height:100px;">
						</textarea>
					</div>
				</div>
				<div class="time_col time_colp11" style="margin:10px 0px 10px 0px;">
			    	<div style="display: inline-block;white-space: nowrap;">
						<label style="text-align:right;width:135px; display: inline-block;vertical-align: top;">商品图片：</label>
						<div style="display: inline-block;white-space: nowrap;width:83%;height:120px;overflow:auto">
							 <div class="uploadImg"  style="display: inline-block;white-space: nowrap;width:100%;">
								<div style="overflow: auto;" id="image2"></div> 
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<!-- 详情对话框  end-->
		
		<!-- 设置对话框  begin-->
		<div id="setDialog" class="easyui-dialog" style="width:480px;height:460px;padding-top:30px;" 
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<div id="dataGrid" style="width:100%;height:300px;display:inline-block;">
				<table id="setgrid"></table>
		    </div>
			<div style="text-align:center;margin-top:40px;">
			    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" title="Ctrl+S" onclick="setSave()">保存</a> 
				<a href="javascript:void(0)" class="ui-btn ui-btn-xz" title="CTRL+Z" onclick="setCancel()">取消</a>
			</div>
		</div>
		<!-- 设置对话框  end-->
		
	</div>
</body>
</html>