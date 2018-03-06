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
	changeArea();
	changeProvince();
	initArea();
	initProvince();
	initManager();
}

function changeArea(){
	 $("#aname").combobox({
		onChange : function(n, o) {
			var queryData=[];
			if(!isEmpty(n)){
				queryData={'aname' : n};
				$('#ovince').combobox('setValue',null);
				$('#uid').combobox('setValue',null);
			}
			initProvince(queryData);
			initManager(queryData);
		}
	});
}

function changeProvince(){
	 $("#ovince").combobox({
		onChange : function(n, o) {
			var queryData=[];
			if(!isEmpty(n)){
				queryData={'aname' : $("#aname").combobox('getValue'),'ovince':n};
				$('#uid').combobox('setValue',null);
			}
			initManager(queryData);
		}
	});
}

function initArea(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryArea.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#aname').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

function initProvince(queryData){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryProvince.action',
		data : queryData,
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#ovince').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

function initManager(queryData){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryTrainer.action',
		data : queryData,
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#uid').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

//查询框关闭事件
function closeCx() {
	$("#qrydialog").css("visibility", "hidden");
}

// 清空查询条件
function clearCondition(){
	$('#aname').combobox('select',null);
	$('#ovince').combobox('select',null);
	$('#uid').combobox('select',null);
}

/**
 * 数据表格初始化
 */
function load(){
	var columns = getArrayColumns();
	var vince=$('#ovince').combobox('getValue');
	if(isEmpty(vince)){
		vince=-1;
	}
	$('#grid').datagrid({
		url : DZF.contextPath + "/report/custmanagerep!query.action",
		queryParams:{
			'aname' : $('#aname').combobox('getValue'),
			'ovince' :vince,
			'uid' : $('#uid').combobox('getValue')
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
		                { field : 'aname',  title : '大区', width : 100,halign:'center',align:'left'},
		                { field : 'uname',  title : '区总', width : 100,halign:'center',align:'left'},
		                { field : 'provin',  title : '省份', width : 160,halign:'center',align:'left'}, 
		                { field : 'incode',  title : '加盟商编码', width : 160,halign:'center',align:'left'},
		                { field : 'pname', title : '加盟商名称', width:230,halign:'center',align:'left'},
		]],
		columns : columns,
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
					column1["align"] = 'right'; 
					columnsb.push(column1); 
					var column2 = {};
					column2["title"] = '一般纳税人';  
					column2["field"] = 'custtaxpay';  
					column2["width"] = '115'; 
					column2["halign"] = 'center'; 
					column2["align"] = 'right'; 
					columnsb.push(column2); 
					for(var i = 0; i < rows.length; i++){
						var column1 = {};
						column1["title"] = '小规模';  
						column1["field"] = 'rates'+(i+1);  
						column1["width"] = '120'; 
						column1["halign"] = 'center'; 
						column1["align"] = 'right'; 
						column1["formatter"] = formatMny;
						columnsb.push(column1); 
						var column2 = {};
						column2["title"] = '一般纳税人';  
						column2["field"] = 'ratet'+(i+1);  
						column2["width"] = '120'; 
						column2["halign"] = 'center'; 
						column2["align"] = 'right'; 
						column2["formatter"] = formatMny;
						columnsb.push(column2); 
					}
					columns.push(columnsh);
					columns.push(columnsb);
				}
			} 
		},
	});
	return columns;
}
