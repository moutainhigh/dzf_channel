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
				<div id="dlg" style="display:none;padding:10px 20px;height:300px;">
					<p style="margin-top:10px;">
						<label style="float:left">驳回原因：</label>
						<textarea id="content" placeholder="请输入您要反馈问题或者改进建议" id="usign" name="usign" 
						 	autocomplete="off" class="layui-textarea" style="width:350px;height:260px;resize: none;">
						</textarea>
					</p>
					<p style="margin-top:10px;">
						修改意见：
						<input id="contact" name="contact" style="width:343px;height:30px;" data-options="required:true" />
					</p>
				</div>
			</form>
			<div id="opinion_buttons" style="display:none" >
				<a href="javascript:void(0)" class="ui-btn save_input"  onclick="saveOpinion();" >确认</a> 
				<a href="javascript:void(0)" class="ui-btn save_input" onclick="javascript:$('#dlg').dialog('close');" >取消</a>
			</div>
			<!-- 新增或修改对话框 end -->
		</div>
    </div>
</body>
</html>
