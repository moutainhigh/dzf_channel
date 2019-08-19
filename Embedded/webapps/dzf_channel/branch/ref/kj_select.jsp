<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>

<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>主办会计</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../../css/main.css");%> rel="stylesheet">
</head>
<%
	String funcName = request.getParameter("dblClickRowCallback");
    String cpcode = request.getParameter("cpcode");
%>
<body>
<script>
var rows=null;
var grid;
$(function(){
	var params = new Object();
	params["cpcode"] = '<%=cpcode%>';
	grid = $('#kjgrid').datagrid({
	    url: DZF.contextPath + '/branch/corpdataact!queryPcount.action',
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
	    columns:[[  { title : '主键id', field : 'id', hidden : true }, 
					{ width : '100', title : '会计编码', field : 'code' }, 
					{ width : '100', title : '会计名称', field : 'name' }
	   	 ]],
	   	onLoadSuccess:function(data){
	   		if(data.rows.length>0){
				$('#kjgrid').datagrid('selectRow', 0);
		   		rows = data.rows;
	   		}
		},
		onDblClickRow : function(rowIndex, rowData){
			var f = <%=funcName%>;
			if(f!=null && f!='null' && f!='' )
				eval('f(rowData)');
		}
	}); 
    $('#ucode').bind('keypress',function(event){
       if(event.keyCode == "13") {//Enter 键事件
    	   	var filtername = $("#ucode").val();
      		var params = new Object();
      		params["ucode"] = filtername;
      		params["cpcode"] = '<%=cpcode%>';
      		grid.datagrid('reload',params); 
       }
   });
}); 

</script>
<div class="wrapper" id="cardList">
	<div class="mod-toolbar-top">
		<div class="search-toolbar-content">
			<div class="left search-crumb">
				<input id="ucode" value="请输入会计编码或名称" 
					style="height:35px;color:#999;float:center;width:80%"
					onFocus="if(value==defaultValue){value='';this.style.color='#000'}" 
					onBlur="if(!value){value=defaultValue;this.style.color='#999'}"/> 
			</div>
		</div>
	</div>
	<div class="mod-inner">
		<div id="dataGrid" class="grid-wrap">
			<table id="kjgrid"></table>
		</div>
	</div>
</div>
</body>
</html>