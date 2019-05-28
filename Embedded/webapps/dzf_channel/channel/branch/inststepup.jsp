<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
<title>机构设置</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<%-- <SCRIPT SRC=<%UPDATEGRADEVERSION.OUTVERSION(OUT, "../../JS/CHANNEL/BRANCH/INSTSTEPUP.JS");%> CHARSET="UTF-8" TYPE="TEXT/JAVASCRIPT"></SCRIPT> --%>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<style>
 
</style>
</head>
<body class="wrapper char" style="height:100%"> 
    <div class="easyui-layout" style="height:100%;">
		<div class="char" data-options="region:'west',border:false,split:true" style="width:50%;border-width:1px 0px 0px 0px;" id="leftGrid">
			<div class="h30 h30-arrow" style="margin-left:60px;" id="filter">
	    	     <span style="font-size:20px;font-weight:bold">机构设置</span>
			</div>
			<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options=" plain:true" onclick="addInst()">新增</a>
			<table id="instgrid" ></table>
		</div>
	 	<div class="char" data-options="region:'center',border:true,split:true" style="width:50%;border-width:1px 0px 0px 0px;" id="rightGrid" >
 			 <div class="h30 h30-arrow" style="margin-left:60px;">
	    	     <span style="font-size:20px;font-weight:bold">公司设置-直营</span>
			 </div>
			 <a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options=" plain:true" onclick="addCorp()">复制</a>
			 <a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options=" plain:true" onclick="updateInst()">更换机构</a>
 			 <form id="corpgridForm" method="post" style="height:100%;margin-top:0px ">
 			 	<input type="hidden" id="user_id" name="user_id" value="">
				<table id="corpgrid" ></table>
		     </form>
	    </div>		
	</div>
</body>
</html>
