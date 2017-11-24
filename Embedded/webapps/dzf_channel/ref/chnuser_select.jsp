<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>加盟商用户参照</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<style>
	.datagrid-body{height:263px !important;}
</style>
<body>
<%
	String funcName = request.getParameter("dblClickRowCallback");
%>
<script>
var userrows = null;
$(function(){
	$('#userTable').datagrid({   
	    url: DZF.contextPath + '/sys/chnUseract!query.action',
	    method: 'post',
	    //fit:true,
	    height:390,
		fitColumns: true,
		idField:'uid',
		rownumbers: true,
		singleSelect: true,
		pagination: true,
		showFooter: true,
		striped:true,
	    columns:[[   
	     		  {field:'ucode',title:'用户编码',width:200},  
	              {field:'uname',title:'用户名称',width:400}, 
	              {field:'uid',title:'用户主键',width:100,hidden:true},  
	   	 ]],
	   	onLoadSuccess:function(data){
	   		if(data.rows.length>0){
				$('#userTable').datagrid('selectRow', 0);
				if(userrows==null){
		   			userrows = data.rows;
	   			}
	   		}
		},
		onDblClickRow : function(rowIndex, rowData){
			var f = <%=funcName%>;
			if(f!=null && f!='null' && f!='' )
				eval('f(rowData)');
		}
	});
    $('#kjcode').bind('keypress',function(event){
        if(event.keyCode == "13") {//Enter 键事件
        	var filtername = $("#kjcode").val();
      		var params = new Object();
      		params["ucode"] = filtername;
      		params["uname"] = filtername;
      		$('#userTable').datagrid('reload',params); 
        }
    }); 
});
</script>
<div class="wrapper" >
		<div class="mod-toolbar-top" style="margin:0px 0px 0px;">
			<div class="search-toolbar-content">
				<div class="left search-crumb">
					<input id="kjcode" style="height:35px;color:#999" value="请输入用户编码或名称" 
					onFocus="if(value==defaultValue){value='';this.style.color='#000'}" 
					onBlur="if(!value){value=defaultValue;this.style.color='#999'}"/> 
				</div>
			</div>
		</div>
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="userTable" ></table>
			</div>
		</div>
	</div>
</body>
</html>