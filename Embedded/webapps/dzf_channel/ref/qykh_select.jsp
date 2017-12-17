<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>

<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<!-- 只包含会计事务所下的企业客户 -->
<title>客户档案</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../css/index.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../css/main.css");%> rel="stylesheet">
</head>
<%
	String corpid = request.getParameter("corpid");
	String funcName = request.getParameter("dblClickRowCallback");
	String ishasjz = request.getParameter("ishasjz");
%>
<body>
<script>
var rows=null;
var grid;
$(function(){
	var params = new Object();
	var corpid = '<%=corpid%>';
	var ishasjz = '<%=ishasjz%>';
	var params = new Object();
	params["corpIds"] = corpid;
	grid = $('#khTable').datagrid({
	    url: DZF.contextPath + '/sys/sys_quchncorp!queryKhRef.action',
	    queryParams :params,
	    method: 'post',
		fitColumns: true,
		idField:'uid',
		rownumbers: true,
		singleSelect:true,
		pagination:true,
		height : 350,
		showFooter: true,
		pagination : true,
		striped:true,
	    columns:[[  { title : '主键id', field : 'pk_gs', hidden : true }, 
					{ width : '100', title : '客户编码', field : 'incode' }, 
					{ width : '100', title : '客户名称', field : 'uname' }
	   	 ]],
	   	onLoadSuccess:function(data){
	   		if(data.rows.length>0){
				$('#khTable').datagrid('selectRow', 0);
		   		rows = data.rows;
	   		}
		},
		onDblClickRow : function(rowIndex, rowData){
			var f = <%=funcName%>;
			if(f!=null && f!='null' && f!='' )
				eval('f(rowData)');
		}
	}); 
    $('#khcode').bind('keypress',function(event){
       if(event.keyCode == "13") {//Enter 键事件
    	   	var filtername = $("#khcode").val();
      		var params = new Object();
      		params["ishasjz"] = ishasjz;
      		params["corpIds"] = corpid;
      		params["corpname"] = filtername;
      		params["corpcode"] = filtername;
      		grid.datagrid('reload',params); 
       }
   });
}); 

</script>
<div class="wrapper" id="cardList">
		<div class="mod-toolbar-top">
			<div class="search-toolbar-content">
				<div class="left search-crumb">
					<input style="height:35px;color:#999;float:center;width:80%"  id="khcode" value="请输入客户编码或名称" 
					onFocus="if(value==defaultValue){value='';this.style.color='#000'}" 
					onBlur="if(!value){value=defaultValue;this.style.color='#999'}"/> 
				</div>
			</div>
		</div>
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="khTable"></table>
			</div>
		</div>
	</div>
</body>
</html>