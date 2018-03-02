var grid;

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});

$(function() {
	initPeriod("#qryperiod");
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
	$('#grid').datagrid({
		url : DZF.contextPath + "/report/custnummoneyrep!query.action",
		queryParams:{
			'period' : $('#qryperiod').datebox('getValue'),//查询期间
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
		                { field : 'provin',  title : '省份', width : 160,halign:'center',align:'left'}, 
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
            
            { field : 'newcustrates', title : '小规模', width : 100, formatter:formatLocalMny,halign:'center',align:'right'}, 
            { field : 'newcustratet', title : '一般纳税人', width : 100, formatter:formatLocalMny,halign:'center',align:'right'}, 
            { field : 'newcontrates', title : '小规模', width : 100, formatter:formatLocalMny,halign:'center',align:'right'}, 
            { field : 'newcontratet', title : '一般纳税人', width : 100, formatter:formatLocalMny,halign:'center',align:'right'}, 
            { field : 'renewcustrates', title : '小规模', width : 100, formatter:formatLocalMny,halign:'center',align:'right'}, 
            { field : 'renewcustratet', title : '一般纳税人', width : 100, formatter:formatLocalMny,halign:'center',align:'right'}, 
            { field : 'renewcontrates', title : '小规模', width : 100, formatter:formatLocalMny,halign:'center',align:'right'}, 
            { field : 'renewcontratet', title : '一般纳税人', width : 100, formatter:formatLocalMny,halign:'center',align:'right'}, 
            
        ] ],
        onLoadSuccess : function(data) {
        	var rows = $('#grid').datagrid('getRows');
        	var footerData = new Object();
        	var stockcusts = 0;	//
        	var stockcustt = 0;	//
        	var stockconts = 0;	//
        	var stockcontt = 0;	//
        	
        	var newcusts = 0;	//
        	var newcustt = 0;	//
        	var newconts = 0;	//
        	var newcontt = 0;	//
        	
        	var renewcusts = 0;	//
        	var renewcustt = 0;	//
        	var renewconts = 0;	//
        	var renewcontt = 0;	//
        	for (var i = 0; i < rows.length; i++) {
        		if(rows[i].stockcusts != undefined && rows[i].stockcusts != null){
        			stockcusts += parseFloat(rows[i].stockcusts);
        		}
        		if(rows[i].stockcustt != undefined && rows[i].stockcustt != null){
        			stockcustt += parseFloat(rows[i].stockcustt);
        		}
        		if(rows[i].stockconts != undefined && rows[i].stockconts != null){
        			stockconts += parseFloat(rows[i].stockconts);
        		}
        		if(rows[i].stockcontt != undefined && rows[i].stockcontt != null){
        			stockcontt += parseFloat(rows[i].stockcontt);
        		}
        		
        		if(rows[i].newcusts != undefined && rows[i].newcusts != null){
        			newcusts += parseFloat(rows[i].newcusts);
        		}
        		if(rows[i].newcustt != undefined && rows[i].newcustt != null){
        			newcustt += parseFloat(rows[i].newcustt);
        		}
        		if(rows[i].newconts != undefined && rows[i].newconts != null){
        			newconts += parseFloat(rows[i].newconts);
        		}
        		if(rows[i].newcontt != undefined && rows[i].newcontt != null){
        			newcontt += parseFloat(rows[i].newcontt);
        		}
        		
        		if(rows[i].renewcusts != undefined && rows[i].renewcusts != null){
        			renewcusts += parseFloat(rows[i].renewcusts);
        		}
        		if(rows[i].renewcustt != undefined && rows[i].renewcustt != null){
        			renewcustt += parseFloat(rows[i].renewcustt);
        		}
        		if(rows[i].renewconts != undefined && rows[i].renewconts != null){
        			renewconts += parseFloat(rows[i].renewconts);
        		}
        		if(rows[i].renewcontt != undefined && rows[i].renewcontt != null){
        			renewcontt += parseFloat(rows[i].renewcontt);
        		}

        	}
        	footerData['pname'] = '合计';
        	footerData['stockcusts'] = stockcusts;
        	footerData['stockcustt'] = stockcustt;
        	footerData['stockconts'] = stockconts;
        	footerData['stockcontt'] = stockcontt;
        	
        	footerData['newcusts'] = newcusts;
        	footerData['newcustt'] = newcustt;
        	footerData['newconts'] = newconts;
        	footerData['newcontt'] = newcontt;
        	
        	footerData['renewcusts'] = renewcusts;
        	footerData['renewcustt'] = renewcustt;
        	footerData['renewconts'] = renewconts;
        	footerData['renewcontt'] = renewcontt;
        	
        	var fs=new Array(1);
        	fs[0] = footerData;
        	$('#grid').datagrid('reloadFooter',fs);
        },
	});
}

/**
 * 金额数据格式化
 * @param value
 * @returns {String}
 */
function formatLocalMny(value){
	if(value == null){
		return;
	}
	if(getFloatValue(value) == parseFloat(0)){
		return "--";
	}else{
		if(isContains(value,"-")){
		    var mid = accMul(value, -1);
		    return "-"+formatMny(mid);
		}else{
			return formatMny(value);
		}
	}
}

/**
 * 查询
 */
function reloadData(){
	$('#grid').datagrid('options').url = DZF.contextPath + "/report/custnummoneyrep!query.action";
	var queryParams = $('#grid').datagrid('options').queryParams;
	queryParams.period = $('#qryperiod').datebox('getValue');
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
}
