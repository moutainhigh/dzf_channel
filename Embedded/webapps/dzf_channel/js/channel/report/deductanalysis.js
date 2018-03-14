var contextPath = DZF.contextPath;
$(function(){
	load();
});

function load(){
	var columns = new Array(); 
	var columnsh = new Array();//列及合并列名称
	var columnsb = new Array();//子列表名称集合
	
	var onlycol =  new ArrayList();//金额展示集合
	var onlymap = new HashMap();//金额对应列数
	
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
		                { field : 'num',  title : '户数', width : 100, halign:'center',align:'right'}, 
		                { field : 'mny',  title : '总额', width : 100, halign:'center',align:'right',formatter:formatMny},
		]],
		columns : columns,
		onLoadSuccess : function(data) {
			
		},
	});
	
	if(datarray != null && datarray.length > 0){
		$('#grid').datagrid('loadData', datarray);
	}
}