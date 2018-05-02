var contextPath = DZF.contextPath;

$(function(){
	load();
});

function load(){
	$('#grid').datagrid({
//		url : DZF.contextPath + '/chn_set/chnarea!query.action',
		idField : 'reid',
		pageNumber : 1,
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		pagination : true,
		rownumbers : true,
		singleSelect : false,
//		queryParams : {'type' :2},
		height : Public.setGrid().h,
		columns : [ [ {
			width : '500',
			title : '驳回原因',
			field : 'reason'
		}, {
			width : '500',
			title : '修改建议',
			field : 'suggest'
		},{
			title : '主键',
			field : 'reid',
			hidden: true
		}
		] ],
		onLoadSuccess : function(data) {
			parent.$.messager.progress('close');
			$('#grid').datagrid("selectRow", 0);  	
		}
		
	});
}

/**
 * 新增
 */
function add(){
	$("#dlg").dialog({
		title: '新增驳回原因',
		width:500,
		height:340,
		modal: true,
		buttons : '#opinion_buttons'
	});
}


