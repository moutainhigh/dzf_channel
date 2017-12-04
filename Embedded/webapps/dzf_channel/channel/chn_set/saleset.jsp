<%@ page language="java"  pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
<title>加盟商销售管理</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>   
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/chn_set/saleset.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<style type="text/css">
	.checkbox{
		margin : 20px;
		line-height: 1.5em;
        font-family: "Times New Roman", Times, serif;
		font-size: 14px;
        color: #000000;
	}
	.box{
		margin-top : 10px;
		margin-right : 5px;
	}
	.saleset{margin:6px 0px 10px 10px;font-size:14px;color:#000}
</style>
</head>
<body class="easyui-layout">
	<div class="wrapper">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="right">
					<a id='edit' class="ui-btn ui-btn-xz" onclick="edit();" href="javascript:void(0);">修改</a> 
					<a id='history'class="ui-btn ui-btn-xz" onclick="history();" href="javascript:void(0);">变更明细</a> 
					<a id='save' class="ui-btn ui-btn-xz" onclick="save();" href="javascript:void(0);">保存</a> 
				</div>
			</div>
		</div>
		<div class="mod-inner" style="width:99%;height:82%;padding-top: 10px;">
			<form id="sale_set" method="post">
				<div class="time_col time_colp11" style="display:none">
					<label style="text-align:right">主键</label> 
					<input id="id" name="id" class="easyui-textbox"> 
				</div>
				<span class="checkbox">
					<label style="width: 100px;display: inline-block;text-align:right;margin-left:-7px;">回收规则：</label>
					<input name="isfirecovery" type="checkbox" id='isfirecovery' style="margin-left:7px;" value="是"/>
					<label for="isfirecovery">领取后</label>
					<input class="easyui-numberbox" id ="finum" name="finum" style="width:50px;    vertical-align: top;"> 
					<label>自然日未拜访，自动回收客户 </label>
				</span><br/>
				<div class="saleset" style="margin-left:124px;">
					<input name="isserecovery" type="checkbox" id="isserecovery"  value="是"/>
					<label for="isserecovery">销售持有客户30个自然日未拜访</label>
					<input class="easyui-numberbox" id ="senum" name="senum" style="width:50px"> 
					<label>次以上，自动回收客户</label>
					<br/>
				</div>
				<div class="saleset" style="margin-left:124px;">
					<input name="isthrecovery" type="checkbox" id="isthrecovery"  value="是"/>
					<input class="easyui-numberbox" id ="thnum" name="thnum" style="width:50px"> 
					<label>个自然日未签约，自动回收客户</label>
					<br/>
				</div>
			   <div class="saleset">
					<label for="pwdstrategy" style="width: 100px;display: inline-block;text-align:right;margin-right:10px;">领取上限：</label>
					<input class="box" type="checkbox" id="isreceive" name="isreceive" value="是"/>
					<label for="isreceive">每个成员最多持有</label>
					<input class="easyui-numberbox" id ="recnum" name="recnum" style="width:50px"> 
					<label>未签约客户 </label>
				</div>
				<div class="saleset">
					<label for="pwdstrategy" style="width: 100px;display: inline-block;text-align:right;">保护规则：</label>
					<span>
						<label>点击保护，释放日期增加</label>
						<input class="easyui-numberbox" id ="relnum" name="relnum" style="width:50px"> 
						<label>个自然日</label><br/>
					</span><br/>
					<span style="margin-left:104px;">
						<label>最多可同时保护</label>
						<input class="easyui-numberbox" id ="pronum" name="pronum" style="width:50px"> 
						<label>个客户
					</span>
                </div>
                <div class="saleset">
				<label style="width: 100px;display: inline-block; text-align:right;">客户分类：</label>
				<input  id="ficrla"  name="ficrla" type="text" class="easyui-textbox" data-options="width:100,height:27,editable:true" />
				<input  id="seccla"  name="seccla" type="text" class="easyui-textbox" data-options="width:100,height:27,editable:true" />
				<input  id="thicla"  name="thicla" type="text" class="easyui-textbox" data-options="width:100,height:27,editable:true" />
				<input  id="foucla"  name="foucla" type="text" class="easyui-textbox" data-options="width:100,height:27,editable:true" />
				<input  id="fifcla"  name="fifcla" type="text" class="easyui-textbox" data-options="width:100,height:27,editable:true" />
				</div>
				<div class="saleset">
				<div style="width:25%;display: inline-block;">
					<label style="width: 100px;display: inline-block;text-align:right;">最后修改人：</label> 
					<input id="lmpsn" name="lmpsn" class="easyui-textbox" data-options="readonly:true," style="width:60%;height:28px;text-align:left"></input>	
				</div>
				<div style="width:38%;display: inline-block;">
					<label style="text-align:right;width:22%;">最后修改时间：</label> 
					<input id="lsdate" name="lsdate" class="easyui-textbox" data-options="readonly:true," style="width:48%;height:28px;text-align:left"></input>	
				</div>
				</div>
			</form>
		</div>
		<div id="dialog" class="easyui-dialog" style="width:500px;height:500px;" data-options="closed:true">
			<table id="grid" style="height:98%;"></table>
		</div>
	</div>
</body>
</html>
