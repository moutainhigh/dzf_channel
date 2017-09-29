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
		url : DZF.contextPath + "/report/custnummoneyrep!query.action",
		queryParams:{
			'begdate' : $('#qddate').datebox('getValue'),//查询结束日期
		},
		striped : true,
		title : '',
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : false,
		pagination : true, //显示分页
		pageSize : 20, //默认20行
		pageList : [ 20, 50, 100, 200 ],
		showRefresh : false,// 不显示分页的刷新按钮
		showFooter : true,
		border : true,
		remoteSort:false,
		//冻结在 左边的列 
		frozenColumns:[[
						{ field : 'ck',	checkbox : true },
						{ field : 'pid',    title : '会计公司主键', hidden : true},
//		                { field : 'larea',  title : '大区', width : 100,halign:'center',align:'left'},
		                { field : 'provin',  title : '省份', width : 100,halign:'center',align:'left'}, 
		                { field : 'pname', title : '加盟商名称', width:260,halign:'center',align:'left'},
		]],
		columns : [ 
		            [ 
		             { field : 'stockcust', title : '存量客户数量', halign:'center',align:'center',colspan:2},
		             { field : 'stockcont', title : '存量客户合同金额', halign:'center',align:'center',colspan:2},
		             { field : 'newcust', title : '新增客户数量', halign:'center',align:'center',colspan:2},
		             { field : 'newcont', title : '新增客户合同金额', halign:'center',align:'center',colspan:2},
		             { field : 'renewcust', title : '续费客户数量', halign:'center',align:'center',colspan:2},
		             { field : 'renewcont', title : '续费客户合同金额', halign:'center',align:'center',colspan:2},
		             
		             { field : 'newcustrate', title : '新增客户增长率(%)', width:120,halign:'center',align:'right',colspan:2},
		             { field : 'newcontrate', title : '新增合同增长率(%)', width:120,halign:'center',align:'right',colspan:2},
		             { field : 'renewcustrate', title : '续费客户占比(%)', width:100,halign:'center',align:'right',colspan:2},
		             { field : 'renewcontrate', title : '续费合同占比(%)', width:100,halign:'center',align:'right',colspan:2},
		             ] ,
        [
            { field : 'stockcusts', title : '小规模', width : 100, halign:'center',align:'right'}, 
            { field : 'stockcustt', title : '一般纳税人', width : 100, halign:'center',align:'right'}, 
            { field : 'stockconts', title : '小规模', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'stockcontt', title : '一般纳税人', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'newcusts', title : '小规模', width : 100, halign:'center',align:'right'}, 
            { field : 'newcustt', title : '一般纳税人', width : 100, halign:'center',align:'right'}, 
            { field : 'newconts', title : '小规模', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'newcontt', title : '一般纳税人', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'renewcusts', title : '小规模', width : 100, halign:'center',align:'right'}, 
            { field : 'renewcustt', title : '一般纳税人', width : 100, halign:'center',align:'right'}, 
            { field : 'renewconts', title : '小规模', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'renewcontt', title : '一般纳税人', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            
            { field : 'newcustrates', title : '小规模', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'newcustratet', title : '一般纳税人', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'newcontrates', title : '小规模', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'newcontratet', title : '一般纳税人', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'renewcustrates', title : '小规模', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'renewcustratet', title : '一般纳税人', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'renewcontrates', title : '小规模', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'renewcontratet', title : '一般纳税人', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            
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
