<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
<title>服务套餐定义</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>  rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/packagedef/packagedef.js");%>  charset="UTF-8" type="text/javascript"> </script>
</head>
<body>
	<div id="types" class="wrapper">
		<div class="mod-toolbar-top">
			
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<div id="cxjs" class="h30 h30-arrow">
						<label class="mr5">查询：</label>
						<strong id="querydate"></strong>
						<span class="arrow-date"></span>
					</div>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="addBtn" onclick="addType()">新增</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="editBtn" onclick="modify()">修改</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="publishBtn" onclick="publish()">发布</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="offBtn" onclick="updateOff()">下架</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="delBtn" onclick="del()">删除</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="saveBtn" onclick="save()">保存</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="cancelBtn" onclick="cancel()">取消</a> 
					
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  id="sort" onclick="startSort()">开始排序</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  id="sort_up" onclick="moveUp()">上移</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  id="sort_down" onclick="moveDown()">下移</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  id="sort_top" onclick="moveTop()">置顶</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="sort_save" style="display:none"   onclick="sortSave()">保存</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="sort_cancle"  onclick="onCancle()">取消</a> 
				 </div> 
			</div>
	    </div>
	    <!-- 查询对话框 begin -->
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:300px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<form id="query_form">
				<h3>
					<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="width: 85px;text-align:right">录入日期：</label>
					<font><input id="begdate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
					<font>-</font>
					<font><input id="enddate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">纳税人资格：</label>
					<select id="taxtype" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:110px;height:28px;">
						<option value="">全部</option>
						<option value="小规模纳税人">小规模纳税人</option>
						<option value="一般纳税人">一般纳税人</option>
					</select>
					<label style="width:62px;text-align:right">状态：</label>
					<select id="vstatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:100px;height:28px;">
						<option value="-1">全部</option>
						<option value="4">已保存+已发布</option>
						<option value="3">已下架</option>
					</select>
				</div>
				<div class="time_col time_colp10">
					<label style="width: 85px;text-align:right">套餐类型：</label>
					<input id="normal"  type="checkbox" checked style="width:20px;height:28px;text-align:left;margin-left:2px;"/>
					<label style="width:100px;text-align:left" for="normal">常规套餐</label> 
					<input id="supple"  type="checkbox" checked style="width:20px;height:28px;text-align:left;margin-left:20px;"/>
					<label style="width:100px;text-align:left" for="supple">非常规套餐</label> 
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">收费周期：</label>
					<input id="cylnum" class="easyui-numberbox" style="width:284px;height:28px;"/>
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">合同周期：</label>
					<input id="contcycle" class="easyui-numberbox" style="width:284px;height:28px;"/>
				</div>
			</form>
			<p>
				<a class="ui-btn save_input" onclick="clearParams()">清除</a>
				<a class="ui-btn save_input" onclick="reloadData()">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		<!-- 查询对话框end -->
	    
	    <div class="mod-inner" style="height:auto;">
			<div id="dataGrid" class="grid-wrap">
				<table id="grid" ></table>
			</div>
		</div>
    </div>
</body>
</html>
