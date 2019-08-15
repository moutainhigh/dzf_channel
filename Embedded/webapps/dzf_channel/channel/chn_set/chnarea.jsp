<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>渠道区域划分</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>   
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/chn_set/areacommon.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<style type="text/css">
	.panel-body {
		overflow: auto
	}
    .mod-toolbar-top {
		margin-right: 17px !important; 
	}
</style>
</head> 
<body>
	<input type="hidden" id="hidtype" value="1" >
	<div id="listPanel" class="wrapper" style="width: 100%;overflow:hidden; height: 100%;">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<div style="float:right"><span id = "manager" style="float:right;margin-right:5px;font-weight: bold; margin-top: 5px;font-size: 14px;"></span></div>
				</div>
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="add()">新增</a> 
				</div>
			</div>
		</div>
		
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="grid"></table>
			</div>
		</div>
		
		<!--卡片界面开始-->
		<div id="cardDialog" class="easyui-dialog" data-options="closed:true" modal=true>
			<form id="chnarea" method="post" style="height:360px;width:1000px;overflow:hidden;padding-top:18px;">
				<div class="time_col time_colp11" style="display:none">
					<label style="text-align:right">主键</label> 
					<input id="pk_area" name="pk_area" class="easyui-textbox"> 
					<input id="type" name="type" class="easyui-textbox"> 
				</div>
				<div class="time_col time_colp11">
					<div style="width:32%;display: inline-block;">
						<label style="text-align:right;width:35%;">大区编码：</label> 
						<input  id='acode' name="acode" class="easyui-textbox"  data-options="validType:'length[0,4]',required:true" style="width:60%;height:28px;text-align:left"></input>
					</div>
					<div style="width:32%;display: inline-block;">
						<label style="text-align:right;width:35%;">大区名称：</label> 
						<input id='aname'  name="aname" class="easyui-textbox"  data-options="validType:'length[0,10]',required:true"  style="width:60%;height:28px;text-align:left"></input>
					</div>
					<div  style="width:32%;display: inline-block;" >
						<label style="text-align:right;width:35%;">区总：</label> 
						<input id="uname" name="uname" class="easyui-searchbox" data-options="required:true" style="width:60%;height:28px;text-align:left;"  > 
						<input id="uid" name="uid" type="hidden"> 
					</div>
				</div>
				<div class="time_col time_colp11">
					<div style="width:100%;display: inline-block;">
						<label style="text-align:right;width:11.2%;vertical-align:middle;">备注：</label>
						<textarea id="vmemo" class="easyui-textbox" name="vmemo" data-options="multiline:true,validType:'length[0,50]'" style="width:84%;height:40px;"></textarea>
					</div>
				</div>
				<div id="dataGrid" class="grid-wrap" >
					<table id="cardGrid"></table>
				</div> 
				<div style="float:right;margin-top:20px;margin-right:20px;">
				    <a href="javascript:void(0)" id="save" class="ui-btn ui-btn-xz" title="Ctrl+S" onclick="checkSave()">保存</a> 
					<a href="javascript:void(0)" id="cancel" class="ui-btn ui-btn-xz" title="CTRL+Z" onclick="cancel()">取消</a>
				</div>
			</form>
		</div>
		
	</div>
	
	
	<div id="detail_dialog" class="easyui-dialog" title="加盟商联查" data-options="modal:true,closed:true" style="width:600px;height:400px;">
		<div data-options="region:'center'" style="overflow-x:auto; overflow-y:auto;margin: 0 auto;width:90%;height:400px;padding:10px">
			 <table id="gridh"></table>	
		</div>
	</div>
	
	<div id="gs_dialog"></div>
	<div id="userdialog"></div>
	<div id="chnDlg"></div>
 	</body>
</html>