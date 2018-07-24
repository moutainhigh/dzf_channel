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
		url : DZF.contextPath + "/report/personStatis!query.action",
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
		showRefresh : false,// 不显示分页的刷新按钮
		showFooter : true,
		border : true,
		remoteSort:false,
		columns : [[	{ field : 'pid',    title : '会计公司主键', hidden : true,rowspan:2},
		                { field : 'aname',  title : '大区', width : 100,halign:'center',align:'left',rowspan:2},
		                { field : 'uname',  title : '区总', width : 100,halign:'center',align:'left',rowspan:2},
		                { field : 'provname',  title : '省份', width : 160,halign:'center',align:'left',rowspan:2}, 
		                { field : 'incode',  title : '加盟商编码', width : 140,halign:'center',align:'left',rowspan:2},
		                { field : 'corpnm', title : '加盟商名称', width:240,halign:'center',align:'left',rowspan:2},
		                { field : 'cuname',  title : '培训师', width : 100,halign:'center',align:'left',rowspan:2},
		                { field : 'jms01',  title : '机构负责人', width : 70,halign:'center',align:'right',rowspan:2},
		                { field : 'meiyong1',  title : '会计团队总人数', width : 160,halign:'center',align:'right',colspan:7},
		                { field : 'ktotal',  title : '人员占比(%)', width : 80,halign:'center',align:'right',rowspan:2,formatter:formatMny},
		                { field : 'meiyong2',  title : '销售团队总人数', width : 160,halign:'center',align:'right',colspan:3},
		                { field : 'xtotal',  title : '人员占比(%)', width : 80,halign:'center',align:'right',rowspan:2,formatter:formatMny},
		                { field : 'total',  title : '总用户数', width : 60,halign:'center',align:'right',rowspan:2},
	                ],[
	                   	{ field : 'jms02', title : '会计经理', width : 60,align:'right' },
	                   	{ field : 'jms06', title : '会计主管', width : 60,align:'right' },
	                   	{ field : 'jms07', title : '主办会计', width : 60,align:'right' },
	                   	{ field : 'jms08', title : '记账会计', width : 60,align:'right' },
	                   	{ field : 'jms09', title : '财税支持', width : 60,align:'right' },
	                   	{ field : 'jms05', title : '外勤主管', width : 60,align:'right' },
	                   	{ field : 'jms11', title : '外勤会计', width : 60,align:'right' },
	                   	
	                   	{ field : 'jms03', title : '销售经理', width : 60,align:'right' },
	                   	{ field : 'jms04', title : '销售主管', width : 60,align:'right' },
	                   	{ field : 'jms10', title : '销售', width : 60,align:'right' },
	                ]],
		onLoadSuccess : function(data) {},
	});
}

