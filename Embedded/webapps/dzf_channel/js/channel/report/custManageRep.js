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
	var columns = getArrayColumns();
	$('#grid').datagrid({
		url : DZF.contextPath + "/report/custmanagerep!query.action",
		queryParams:{
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
//						{ field : 'ck',	checkbox : true },
						{ field : 'pid',    title : '会计公司主键', hidden : true},
//		                { field : 'larea',  title : '大区', width : 100,halign:'center',align:'left'},
		                { field : 'provin',  title : '省份', width : 100,halign:'center',align:'left'}, 
		                { field : 'pname', title : '加盟商名称', width:230,halign:'center',align:'left'},
		]],
		columns : columns,
	});
}

/**
 * 获取展示列
 * @returns {Array}
 */
function getArrayColumns(){
	var columns = new Array(); 
	$.ajax({
		type : "post",
		dataType : "json",
		url : DZF.contextPath + "/report/custmanagerep!queryIndustry.action",
		data : {
//			corpkid : pkcpk1,
		},
		traditional : true,
		async : false,
		success : function(data) {
			if (data.success) {
				var rows = data.rows;
				var columnsh = new Array(); 
				var column = {};
				column["title"] = '客户纳税人类型分层';  
				column["field"] = 'col';  
				column["width"] = '230'; 
				column["colspan"] = 2; 
				columnsh.push(column); 
				for(var i = 0; i < rows.length; i++){
					var column = {};
					column["title"] = rows[i].industryname+"占比(%)";  
					column["field"] = 'col';  
					column["width"] = '230'; 
					column["colspan"] = 2; 
					columnsh.push(column); 
				}
				var columnsb = new Array(); 
				var column1 = {};
				column1["title"] = '小规模';  
				column1["field"] = 'custsmall';  
				column1["width"] = '115'; 
				column1["halign"] = 'center'; 
				column1["align"] = 'center'; 
				columnsb.push(column1); 
				var column2 = {};
				column2["title"] = '一般纳税人';  
				column2["field"] = 'custtaxpay';  
				column2["width"] = '115'; 
				column2["halign"] = 'center'; 
				column2["align"] = 'center'; 
				columnsb.push(column2); 
				for(var i = 0; i < 6; i++){
					var column1 = {};
					column1["title"] = '小规模';  
					column1["field"] = 'rates'+(i+1);  
					column1["width"] = '120'; 
					column1["halign"] = 'center'; 
					column1["align"] = 'center'; 
					column1["formatter"] = formatMny;
					columnsb.push(column1); 
					var column2 = {};
					column2["title"] = '一般纳税人';  
					column2["field"] = 'ratet'+(i+1);  
					column2["width"] = '120'; 
					column2["halign"] = 'center'; 
					column2["align"] = 'center'; 
					column2["formatter"] = formatMny;
					columnsb.push(column2); 
				}
				columns.push(columnsh);
				columns.push(columnsb);
			} 
		},
	});
	return columns;
}

function reloadData(){
	
//	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').url = DZF.contextPath + "/report/custmanagerep!query.action";
//	queryParams.begdate = $('#qddate').datebox('getValue');
//	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
	
}