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
	$("#jqj").html($("#qryperiod").datebox('getValue'));
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	initQryCommbox();
	changeDate();
	initChannel();
}

function changeDate(){
	$("#qryperiod").datebox({
		onChange : function(n, o) {
			$("#jqj").html(n);
		}
	});
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
		columns : [ 
		            [ 
					{ field : 'pid',    title : '会计公司主键', hidden : true,rowspan:2},
					{ field : 'aname',  title : '大区', width : 100,halign:'center',align:'left',rowspan:2},
					{ field : 'uname',  title : '区总', width : 100,halign:'center',align:'left',rowspan:2},
					{ field : 'provname',  title : '省份', width : 160,halign:'center',align:'left',rowspan:2}, 
					{ field : 'incode',  title : '加盟商编码', width : 160,halign:'center',align:'left',rowspan:2},
					{ field : 'corpnm', title : '加盟商名称', width:260,halign:'center',align:'left',rowspan:2},
					{ field : 'cuname',  title : '培训师', width : 160,halign:'center',align:'left',rowspan:2},
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
	queryParams.corps = $("#pk_account").val();
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
}
