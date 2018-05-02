<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
<title>合同审批设置</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>   
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/chn_set/rejectreason.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<body>
	<div id="types" class="wrapper">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
				
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="addBtn" onclick="add()">新增</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="editBtn" onclick="edit()">修改</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" id="delBtn" onclick="del()">删除</a>
				 </div> 
			</div>
	    </div>
	    <div class="mod-inner" style="height:auto;">
			<div id="dataGrid" class="grid-wrap">
				<table id="grid" ></table>
			</div>
			<!-- 新增或修改对话框 begin -->
			<form id="opinionForm" method="post">
				<div id="dlg" style="padding:10px 20px;">
					<div style="margin-top:10px;vertical-align: top;">
						<label style="display: inline-block;vertical-align: top;">驳回原因：</label>
						<textarea name="usign" 
						 	autocomplete="off" class="layui-textarea" style="width:350px;height:80px;">
						</textarea>
					</div>
					<div style="margin-top:10px;vertical-align: top;">
						<label style="display: inline-block;vertical-align: top;">修改建议：</label>
						<textarea name="usign" 
						 	autocomplete="off" class="layui-textarea" style="width:350px;height:80px;">
						</textarea>
					</div>
					<div style="text-align: center;margin-top:20px;">
							<a class="ui-btn ui-btn-xz" href="javascript:void(0)" onclick="save()">保存</a>
							<a class="ui-btn ui-btn-xz" style="margin-right: 0px;" href="javascript:void(0)" onclick="cancel()" >取消</a>
						</div>
				</div>
			</form>
			
			<!-- 新增或修改对话框 end -->
		</div>
    </div>
</body>
</html>
