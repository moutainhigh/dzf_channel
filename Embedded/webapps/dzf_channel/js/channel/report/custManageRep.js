var grid;

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});

$(function() {
	initQry();
	load();
});

//初始化
function initQry(){
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	initQryCommbox();
	initChannel();
}

/**
 * 数据表格初始化
 */
function load(){
	var columns = getArrayColumns();
	var vince = $('#ovince').combobox('getValue');
	if(isEmpty(vince)){
		vince = -1;
	}
	$('#grid').datagrid({
		url : DZF.contextPath + "/report/custmanagerep!query.action",
		queryParams:{
			'aname' : $('#aname').combobox('getValue'),
			'ovince' :vince,
			'uid' : $('#uid').combobox('getValue'),
			'corps' : $("#pk_account").val(),
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
		columns : columns,
		frozenColumns:[[
						{ field : 'pid',    title : '会计公司主键', hidden : true},
						{ field : 'aname',  title : '大区', width :60,halign:'center',align:'left'},
						{ field : 'uname',  title : '区总', width : 90,halign:'center',align:'left'},
						{ field : 'provname',  title : '省份', width : 100,halign:'center',align:'left'}, 
						{ field : 'incode',  title : '加盟商编码', width : 120,halign:'center',align:'left'},
						{ field : 'corpnm', title : '加盟商名称', width:180,halign:'center',align:'left',
							formatter: function (value,row,index) {
				            	if (!isEmpty(row.dreldate)) {
				            		return "<div style='position: relative;'>" + value + "<img style='right: 0; position: absolute;' src='../../images/rescission.png' /></div>"
				            	}else{
				            		return value
				            	}
				            }
						},
		        	]],
		onLoadSuccess : function(data) {
			var rows = $('#grid').datagrid('getRows');
			var footerData = new Object();
			var custsmall = 0;	// 
			var custtaxpay = 0;	// 

			for (var i = 0; i < rows.length; i++) {
				if(rows[i].custsmall != undefined && rows[i].custsmall != null){
					custsmall += parseFloat(rows[i].custsmall);
				}
				if(rows[i].custtaxpay != undefined && rows[i].custtaxpay != null){
					custtaxpay += parseFloat(rows[i].custtaxpay);
				}
			}
			footerData['pname'] = '合计';
			footerData['custsmall'] = custsmall;
			footerData['custtaxpay'] = custtaxpay;

			var fs=new Array(1);
			fs[0] = footerData;
			$('#grid').datagrid('reloadFooter',fs);
		},
	});
	$("#qrydialog").css("visibility", "hidden");
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
		traditional : true,
		async : false,
		success : function(data) {
			if (data.success) {
				var rows = data.rows;
				if(rows != null && rows.length > 0){
					var columnsh = new Array();
					var columnsh = [
					// { field : 'pid',    title : '会计公司主键', hidden : true,rowspan:2},
					// { field : 'aname',  title : '大区', width :60,halign:'center',align:'left',rowspan:2},
					// { field : 'uname',  title : '区总', width : 90,halign:'center',align:'left',rowspan:2},
					// { field : 'provname',  title : '省份', width : 100,halign:'center',align:'left',rowspan:2},
					// { field : 'incode',  title : '加盟商编码', width : 120,halign:'center',align:'left',rowspan:2},
					// { field : 'corpnm', title : '加盟商名称', width:180,halign:'center',align:'left',rowspan:2,
					// 	formatter: function (value,row,index) {
			        //     	if (!isEmpty(row.dreldate)) {
			        //     		return "<div style='position: relative;'>" + value + "<img style='right: 0; position: absolute;' src='../../images/rescission.png' /></div>"
			        //     	}else{
			        //     		return value
			        //     	}
			        //     }
					//
					// },
					                
	                { field : 'chndate', title : '加盟日期', width:100,halign:'center',align:'center',rowspan:2},
	                { field : 'cuname',  title : '会计运营经理', width : 120,halign:'center',align:'left',rowspan:2}
	                ]; 
					var column = {};
					column["title"] = '客户纳税人类型分层';  
					column["field"] = 'col';  
					column["width"] = '160'; 
					column["colspan"] = 2; 
					columnsh.push(column); 
					for(var i = 0; i < rows.length; i++){
						var column = {};
						column["title"] = rows[i].industryname+"占比(%)";  
						column["field"] = 'col';  
						column["width"] = '160'; 
						column["colspan"] = 4; 
						columnsh.push(column); 
					}
					var columnsb = new Array(); 
					var column1 = {};
					column1["title"] = '小规模';  
					column1["field"] = 'custsmall';  
					column1["width"] = '80'; 
					column1["halign"] = 'center'; 
					column1["align"] = 'right'; 
					columnsb.push(column1); 
					var column2 = {};
					column2["title"] = '一般人';  
					column2["field"] = 'custtaxpay';  
					column2["width"] = '80'; 
					column2["halign"] = 'center'; 
					column2["align"] = 'right'; 
					columnsb.push(column2); 
					for(var i = 0; i < rows.length; i++){
						var column0 = {};
						column0["title"] = '小规模数量';  
						column0["field"] = 'custs'+(i+1);  
						column0["width"] = '80'; 
						column0["halign"] = 'center'; 
						column0["align"] = 'right'; 
						columnsb.push(column0); 
						
						var column1 = {};
						column1["title"] = '小规模占比';  
						column1["field"] = 'rates'+(i+1);  
						column1["width"] = '80'; 
						column1["halign"] = 'center'; 
						column1["align"] = 'right'; 
						column1["formatter"] = formatMny;
						columnsb.push(column1); 
						
						var column2 = {};
						column2["title"] = '一般人数量';  
						column2["field"] = 'custt'+(i+1);  
						column2["width"] = '80'; 
						column2["halign"] = 'center'; 
						column2["align"] = 'right'; 
						columnsb.push(column2);
						
						var column3 = {};
						column3["title"] = '一般人占比';  
						column3["field"] = 'ratet'+(i+1);  
						column3["width"] = '80'; 
						column3["halign"] = 'center'; 
						column3["align"] = 'right'; 
						column3["formatter"] = formatMny;
						columnsb.push(column3); 
					}
					columns.push(columnsh);
					columns.push(columnsb);
				}
			} 
		},
	});
	return columns;
}
