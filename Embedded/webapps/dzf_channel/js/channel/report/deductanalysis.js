var contextPath = DZF.contextPath;
$(function(){
	load();
});

/**
 * 表格初始化
 */
function load(){
	var columns = new Array(); 
	var columnsh = new Array();//列及合并列名称
	var columnsb = new Array();//子列表名称集合
	
	var onlycol =  new ArrayList();//金额展示集合
	var onlymap = new HashMap();//金额对应列数
	
	//退回扣款
	var columnh1 = {};
	columnh1["title"] = '退回扣款';  
	columnh1["field"] = 'ret';  
	columnh1["width"] = '180'; 
	columnh1["colspan"] = 2; 
	columnsh.push(columnh1);
	
	var columnb1 = {};
	columnb1["title"] = '户数';  
	columnb1["field"] = 'retnum';  
	columnb1["width"] = '90'; 
	columnb1["halign"] = 'center'; 
	columnb1["align"] = 'right'; 
//	columnb1["formatter"] = formatMny;
	columnsb.push(columnb1); 
	
	var columnb2 = {};
	columnb2["title"] = '总额';  
	columnb2["field"] = 'retmny';  
	columnb2["width"] = '90'; 
	columnb2["halign"] = 'center'; 
	columnb2["align"] = 'right'; 
	columnb2["formatter"] = formatMny;
	columnsb.push(columnb2); 
	
	//存量
	var columnh2 = {};
	columnh2["title"] = '存量';  
	columnh2["field"] = 'stock';  
	columnh2["width"] = '180'; 
	columnh2["colspan"] = 2; 
	columnsh.push(columnh2);
	
	var columnb3 = {};
	columnb3["title"] = '户数';  
	columnb3["field"] = 'stocknum';  
	columnb3["width"] = '90'; 
	columnb3["halign"] = 'center'; 
	columnb3["align"] = 'right'; 
//	columnb3["formatter"] = formatMny;
	columnsb.push(columnb3); 
	
	var columnb4 = {};
	columnb4["title"] = '总额';  
	columnb4["field"] = 'stockmny';  
	columnb4["width"] = '90'; 
	columnb4["halign"] = 'center'; 
	columnb4["align"] = 'right'; 
	columnb4["formatter"] = formatMny;
	columnsb.push(columnb4); 
	
	var datarray =  new Array();
	$.ajax({
		type : "post",
		dataType : "json",
		url : DZF.contextPath + "/report/deductanalysis!query.action",
		traditional : true,
//		data : {
//			corpkid : pkcpk1,
//			queryDate : queryDate,
//		},
		async : false,
		success : function(data) {
			if (data.success) {
				var rows = data.rows;
				if(rows != null && rows.length > 0){
					var corpid;
					var obj;
					var colnm = 0;
					var colfield = "";
					for(var i = 0; i < rows.length; i++){
						if(!onlycol.contains(rows[i].dedmny)){
							var column = {};
							column["title"] = rows[i].dedmny;  
							column["field"] = 'col'+(i+1);  
							column["width"] = '180'; 
							column["colspan"] = 2; 
							columnsh.push(column); 
							
							onlymap.put(rows[i].dedmny,(i+1));
							
							var column1 = {};
							column1["title"] = '户数';  
							column1["field"] = 'num'+(i+1);  
							column1["width"] = '90'; 
							column1["halign"] = 'center'; 
							column1["align"] = 'right'; 
//							column1["formatter"] = formatMny;
							columnsb.push(column1); 
							
							var column2 = {};
							column2["title"] = '总额';  
							column2["field"] = 'mny'+(i+1);  
							column2["width"] = '90'; 
							column2["halign"] = 'center'; 
							column2["align"] = 'right'; 
							column2["formatter"] = formatMny;
							columnsb.push(column2); 
							
							onlycol.add(rows[i].dedmny);
						}
					
						if(i == 0){
							corpid = rows[i].corpid;
							obj = {};
							colnm = onlymap.get(rows[i].dedmny);
							colfield = 'num'+colnm;
							obj[colfield] = rows[i].corpnum;
							colfield = 'mny'+colnm;
							obj[colfield] = rows[i].dedmny;
							
							obj['num'] = rows[i].sumnum;
							obj['mny'] = rows[i].summny;
							obj['corpcode'] = rows[i].corpcode;
							obj['corpname'] = rows[i].corpname;
						}else{
							if(corpid == rows[i].corpid){
								colnm = onlymap.get(rows[i].dedmny);
								colfield = 'num'+colnm;
								obj[colfield] = rows[i].corpnum;
								colfield = 'mny'+colnm;
								obj[colfield] = rows[i].dedmny;
								if(i == rows.length - 1){
									datarray.push(obj);
								}
							}else if(corpid != rows[i].corpid){
								datarray.push(obj);
								corpid = rows[i].corpid;
								obj = {};
								colnm = onlymap.get(rows[i].dedmny);
								colfield = 'num'+colnm;
								obj[colfield] = rows[i].corpnum;
								colfield = 'mny'+colnm;
								obj[colfield] = rows[i].dedmny;
								
								obj['num'] = rows[i].sumnum;
								obj['mny'] = rows[i].summny;
								obj['corpcode'] = rows[i].corpcode;
								obj['corpname'] = rows[i].corpname;
							}
						}
					}
					columns.push(columnsh);
					columns.push(columnsb);
				}
			}
		}
	});
	
	$('#grid').datagrid({
//		url : DZF.contextPath + "/report/custmanagerep!query.action",
//		queryParams:{
//		},
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
						{ field : 'corpid',    title : '会计公司主键', hidden : true},
		                { field : 'corpcode',  title : '加盟商编码', width : 100, halign:'center',align:'left'}, 
		                { field : 'corpname',  title : '加盟商名称', width : 160, halign:'center',align:'left'},
		                { field : 'num',  title : '总户数', width : 100, halign:'center',align:'right'}, 
		                { field : 'mny',  title : '总扣款', width : 100, halign:'center',align:'right',formatter:formatMny},
		]],
		columns : columns,
		onLoadSuccess : function(data) {
			
		},
	});
	
	if(datarray != null && datarray.length > 0){
		$('#grid').datagrid('loadData', datarray);
	}
}

/**
 * 打印
 */
function onPrint(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'当前界面数据为空',type:2});
		return;
	}
	var hblcols = $('#grid').datagrid("options").columns[0];//合并列信息
	
	var cols = $('#grid').datagrid('getColumnFields');               // 行信息
	var hbhcols = $('#grid').datagrid('getColumnFields', true);       // 合并行信息

	console.info(hblcols);
	console.info(cols);
	console.info(hbhcols);
	
	//
	Business.getFile(contextPath+ '/report/deductanalysis!print.action',{'strlist':JSON.stringify(datarows),
		'hblcols':JSON.stringify(hblcols), 'cols':JSON.stringify(cols),
		'hbhcols':JSON.stringify(hbhcols)}, true, true);
}

/**
* 导出
*/
function onExport() {
	var datarows = $('#grid').datagrid("getRows");
	if (datarows == null || datarows.length == 0) {
		Public.tips({
			content: '当前界面数据为空',
			type: 2
		});
		return;
	}
	
	var hblcols = $('#grid').datagrid("options").columns[0];//合并列信息
	var cols = $('#grid').datagrid('getColumnFields');               // 行信息
	var hbhcols = $('#grid').datagrid('getColumnFields', true);       // 合并行信息
	
	console.info(hblcols);
	console.info(cols);
	console.info(hbhcols);
	
	Business.getFile(DZF.contextPath + "/report/deductanalysis!export.action", {
		"strlist": JSON.stringify(datarows),
		'hblcols':JSON.stringify(hblcols), //合并列信息
		'cols':JSON.stringify(cols),//除冻结列之外，导出字段编码
		'hbhcols':JSON.stringify(hbhcols)//冻结列编码
	}, true, true);
}