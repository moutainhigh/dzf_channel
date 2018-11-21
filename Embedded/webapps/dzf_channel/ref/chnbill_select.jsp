<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>加盟商订单参照</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<style>
	.datagrid-body{height:263px !important;}
</style>
<body>
<%
	String bills = request.getParameter("bills");
	String corpid = request.getParameter("corpid");
%>
<script>
$(function(){
	$('#billTable').datagrid({   
	    url: DZF.contextPath + '/dealmanage/stockout!queryOrders.action',
	    method: 'post',
	    //fit:true,
	    height:306,
		fitColumns: true,
// 		idField:'soutbid',
		rownumbers: true,
		singleSelect: false,
		showFooter: true,
		striped:true,
		queryParams: {"corpid": "<%=corpid%>", "bills":  "<%=bills%>"},
		columns : [ [ 
		{
			width : '160',
			title : '订单编码',
			field : 'vcode',
			halign : 'center',
			align : 'left',
		}, {
			width : '160',
			title : '商品',
			field : 'gname',
			halign : 'center',
			align : 'left',
		}, {
			width : '100',
			title : '规格',
			field : 'invspec',
            halign : 'center',
			align : 'left',
		},{
			width : '100',
			title : '型号',
			field : 'invtype',
            halign : 'center',
			align : 'left',
		}, {
			width : '100',
			title : '购买数量',
			field : 'nnum',
            halign : 'center',
			align : 'right',
		},{
			width : '100',
			title : '销售价',
			field : 'nprice',
			hidden : true
		},{
			width : '100',
			title : '总金额',
			field : 'nmny',
			hidden : true
		},{
			width : '100',
			title : '商品主键',
			field : 'gid',
			hidden : true
		},{
			width : '100',
			title : '规格主键',
			field : 'specid',
			hidden : true
		},{
			width : '100',
			title : '订单主键',
			field : 'billid_b',
			hidden : true
		}, 
		] ],
	   	onLoadSuccess:function(data){
	   		if(data.rows.length>0){
				$('#billTable').datagrid('selectRow', 0);
	   		}
		}
	});
});
</script>
	<div class="wrapper" >
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="billTable" ></table>
			</div>
		</div>
	</div>
</body>
</html>