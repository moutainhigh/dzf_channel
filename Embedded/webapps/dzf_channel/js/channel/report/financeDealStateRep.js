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
	initQryCommbox();
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
	var vince=$('#ovince').combobox('getValue');
	if(isEmpty(vince)){
		vince=-1;
	}
	$('#grid').datagrid({
		url : DZF.contextPath + "/report/financedealstaterep!query.action",
		queryParams:{
			'period' : $('#qryperiod').datebox('getValue'),//查询期间
			'aname' : $('#aname').combobox('getValue'),
			'ovince' : vince,
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
						{ field : 'pid',    title : '会计公司主键', hidden : true},
						{ field : 'aname',  title : '大区', width : 100,halign:'center',align:'left'},
			            { field : 'uname',  title : '区总', width : 100,halign:'center',align:'left'},
			            { field : 'provin',  title : '省份', width : 160,halign:'center',align:'left'}, 
			            { field : 'incode',  title : '加盟商编码', width : 160,halign:'center',align:'left'},
		                { field : 'pname', title : '加盟商名称', width:260,halign:'center',align:'left'},
		]],
		columns : [ 
		            [ 
					 { field : 'custsmall', title : '小规模数量', width:120,halign:'center',align:'right',rowspan:2},
					 { field : 'custtaxpay', title : '一般纳税人数量', width:120,halign:'center',align:'right',rowspan:2},
		             
		             { field : 'cust', title : '客户占比(%)', halign:'center',align:'center',colspan:2},
		             { field : 'voucher', title : '凭证数量', halign:'center',align:'center',colspan:2},
		             ] ,
        [
            { field : 'custrates', title : '小规模', width : 150, formatter:formatMny, halign:'center',align:'right'}, 
            { field : 'custratet', title : '一般纳税人', width : 150, formatter:formatMny, halign:'center',align:'right'}, 
            { field : 'vouchernums', title : '小规模', width : 150, halign:'center',align:'right'}, 
            { field : 'vouchernumt', title : '一般纳税人', width : 150, halign:'center',align:'right'}, 
        ] ],
		onLoadSuccess : function(data) {
			var rows = $('#grid').datagrid('getRows');
			var footerData = new Object();
			var custsmall = 0;	// 
			var custtaxpay = 0;	// 
			var vouchernums = 0;	// 
			var vouchernumt = 0;	// 

			for (var i = 0; i < rows.length; i++) {
				if(rows[i].custsmall != undefined && rows[i].custsmall != null){
					custsmall += parseFloat(rows[i].custsmall);
				}
				if(rows[i].custtaxpay != undefined && rows[i].custtaxpay != null){
					custtaxpay += parseFloat(rows[i].custtaxpay);
				}
				if(rows[i].vouchernums != undefined && rows[i].vouchernums != null){
					vouchernums += parseFloat(rows[i].vouchernums);
				}
				if(rows[i].vouchernumt != undefined && rows[i].vouchernumt != null){
					vouchernumt += parseFloat(rows[i].vouchernumt);
				}
			}
			footerData['pname'] = '合计';
			footerData['custsmall'] = custsmall;
			footerData['custtaxpay'] = custtaxpay;
			footerData['vouchernums'] = vouchernums;
			footerData['vouchernumt'] = vouchernumt;

			var fs=new Array(1);
			fs[0] = footerData;
			$('#grid').datagrid('reloadFooter',fs);
		},
	});
}

/**
 * 查询
 */
function reloadData(){
	$('#grid').datagrid('options').url = DZF.contextPath + "/report/financedealstaterep!query.action";
	var queryParams = $('#grid').datagrid('options').queryParams;
	queryParams.period = $('#qryperiod').datebox('getValue');
	queryParams.aname = $('#aname').combobox('getValue');
	var vince=$('#ovince').combobox('getValue');
	if(isEmpty(vince)){
		vince=-1;
	}
	queryParams.ovince = vince;
	queryParams.uid = $('#uid').combobox('getValue');
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
}
