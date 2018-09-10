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
	initUserGrid();
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
	var vince=$('#ovince').combobox('getValue');
	if(isEmpty(vince)){
		vince=-1;
	}
	$('#grid').datagrid({
		url : DZF.contextPath + "/report/personStatis!query.action",
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
		showRefresh : false,// 不显示分页的刷新按钮
		showFooter : true,
		border : true,
		remoteSort:false,
		columns : [[	{ field : 'corpid',    title : '会计公司主键', hidden : true,rowspan:2},
		                { field : 'aname',  title : '大区', width : 100,halign:'center',align:'left',rowspan:2},
		                { field : 'uname',  title : '区总', width : 100,halign:'center',align:'left',rowspan:2},
		                { field : 'provname',  title : '省份', width : 160,halign:'center',align:'left',rowspan:2}, 
		                { field : 'incode',  title : '加盟商编码', width : 140,halign:'center',align:'left',rowspan:2},
		                { field : 'corpnm', title : '加盟商名称', width:240,halign:'center',align:'left',rowspan:2,
		                	formatter: function(value,row,index){
								return "<a href='javascript:void(0)' style='color:blue' onclick=\"qryUserDetail('"+row.corpid+"')\">" + value + "</a>";
			            }},
		                { field : 'cuname',  title : '会计运营经理', width : 120,halign:'center',align:'left',rowspan:2},
		                { field : 'jms01',  title : '机构负责人', width : 80,halign:'center',align:'right',rowspan:2},
		                { field : 'meiyong1',  title : '会计团队总人数', width : 160,halign:'center',align:'right',colspan:7},
		                { field : 'ktotal',  title : '人员占比(%)', width : 90,halign:'center',align:'right',rowspan:2,formatter:formatMny},
		                { field : 'lznum',  title : '离职数', width : 60,halign:'center',align:'right',rowspan:2},
		                { field : 'ltotal',  title : '流失率(%)', width : 80,halign:'center',align:'right',rowspan:2,formatter:formatMny},
		                { field : 'meiyong2',  title : '销售团队总人数', width : 160,halign:'center',align:'right',colspan:3},
		                { field : 'xtotal',  title : '人员占比(%)', width : 90,halign:'center',align:'right',rowspan:2,formatter:formatMny},
		                { field : 'total',  title : '总用户数', width : 70,halign:'center',align:'right',rowspan:2},
	                ],[
	                   	{ field : 'jms02', title : '会计经理', width : 70,align:'right' },
	                   	{ field : 'jms06', title : '主管会计', width : 70,align:'right' },
	                   	{ field : 'jms07', title : '主办会计', width : 70,align:'right' },
	                   	{ field : 'jms08', title : '记账会计', width : 70,align:'right' },
	                   	{ field : 'jms09', title : '财税支持', width : 70,align:'right' },
	                   	{ field : 'jms05', title : '外勤主管', width : 70,align:'right' },
	                   	{ field : 'jms11', title : '外勤会计', width : 70,align:'right' },
	                   	
	                   	{ field : 'jms03', title : '销售经理', width : 70,align:'right' },
	                   	{ field : 'jms04', title : '销售主管', width : 70,align:'right' },
	                   	{ field : 'jms10', title : '销售', width : 70,align:'right' },
	                ]],
		onLoadSuccess : function(data) {},
	});
}

function qryUserDetail(corpid){
	$('#userGrid').datagrid('options').url = DZF.contextPath + '/report/personStatis!queryUserDetail.action';
	$('#userDetail').dialog('open');
    $('#userGrid').datagrid('load', {
		cpid:corpid,
    });
}

function initUserGrid(){
	gridh = $('#userGrid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height:'380',
		singleSelect : true,
//		pagination : true,
//		pageSize : DZF.pageSize_min,
//		pageList : DZF.pageList_min,
		showFooter:true,
		columns : [ [ {
			width : '100',
			title : '部门',
			align:'center',
			halign:'center',
			field : 'deptname',
		}, {
			width : '100',
			title : '用户名称',
            halign:'center',
			field : 'uname',
		},{
			width : '200',
			title : '角色',
			align:'right',
            halign:'center',
			field : 'rolename',
		},{
			width : '80',
			title : '客户数',
			align:'right',
            halign:'center',
			field : 'corpnum',
		}, ] ],
		onLoadSuccess : function(data) {
			
		},
	});
}

