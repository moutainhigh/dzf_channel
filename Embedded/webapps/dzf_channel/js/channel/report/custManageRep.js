var grid;

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});

$(function() {
	load();
});

/**
 * 数据表格初始化
 */
function load(){
	$('#grid').datagrid({
//		url : DZF.contextPath + "/report/custnummoneyrep!query.action",
//		queryParams:{'startDate' : $("#startDate").datebox("getValue"),//查询开始日期
//			'endDate' : $("#endDate").datebox("getValue"),//查询结束日期
//			'pid' : $("#qpid").val(),//所属分部主键
//			'kid' : $("#qkid").val(),//客户主键
//			'kname' : $("#qcname").val()//客户名称
//			},
		striped : true,
		title : '',
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : false,
//		pagination : true, //显示分页
//		pageSize : 20, //默认20行
//		pageList : [ 20, 50, 100, 200 ],
//		showRefresh : false,// 不显示分页的刷新按钮
//		showFooter : true,
		border : true,
		remoteSort:false,
		//冻结在 左边的列 
		frozenColumns:[[
						{ field : 'ck',	checkbox : true },
						{ field : 'pid',    title : '会计公司主键', hidden : true},
		                { field : 'larea',  title : '大区', width : 100,halign:'center',align:'left'},
		                { field : 'provin',  title : '省份', width : 100,halign:'center',align:'left'}, 
		                { field : 'pname', title : '加盟商名称', width:260,halign:'center',align:'left'},
		]],
		columns : [ 
		            [ 
					 { field : 'newcustrate', title : '小规模数量', width:120,halign:'center',align:'right',rowspan:2},
					 { field : 'newcontrate', title : '一般纳税人数量', width:120,halign:'center',align:'right',rowspan:2},
		             
		             { field : 'stockcust', title : '客户占比', halign:'center',align:'center',colspan:2},
		             { field : 'stockcont', title : '凭证数量', halign:'center',align:'center',colspan:2},
		             ] ,
        [
            { field : 'stockcusts', title : '小规模', width : 100, halign:'center',align:'right'}, 
            { field : 'stockcustt', title : '一般纳税人', width : 100, halign:'center',align:'right'}, 
            { field : 'stockconts', title : '小规模', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'stockcontt', title : '一般纳税人', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
        ] ],
	});
}

function reloadData(){
	
	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').url = DZF.contextPath + "/report/custnummoneyrep!query.action";
	queryParams.begdate = $('#qddate').datebox('getValue');
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
	
}