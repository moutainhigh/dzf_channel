$(function(){
	load();
	reloadData();
	initType();
});

/**
 * 列表表格加载
 */
function load(){
	grid = $('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height : Public.setGrid().h,
		singleSelect : true,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		showFooter:true,
		columns : [ [ {
			width : '100',
			title : '分类',
			field : 'vname',
			halign : 'center',
			align : 'left',
		}, {
			width : '160',
			title : '商品编码',
			field : 'gcode',
			halign : 'center',
			align : 'left',
		}, {
			width : '240',
			title : '商品名称',
			field : 'gname',
			halign : 'center',
			align : 'left',
		}, {
			width : '120',
			title : '规格',
			field : 'spec',
			halign:'center',
			align:'left',
		}, {
			width : '120',
			title : '型号',
			field : 'type',
            halign : 'center',
			align : 'left',
		},{
			width : '100',
			title : '库存数量',
			field : 'stock',
            halign : 'center',
			align : 'right',
		}, {
			width : '100',
			title : '购买数量',
			field : 'lock',
            halign : 'center',
			align : 'right',
		}, {
			width : '100',
			title : '可用数量',
			field : 'use',
            halign : 'center',
			align : 'right',
		} ] ],
		onLoadSuccess : function(data) {
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

function reloadData(){
	var url = DZF.contextPath + '/dealmanage/goodsnum!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'pk_goodstype' : $("#vname").combobox('getValue'),
		'gcode' : $("#gcode").textbox('getValue'),
		'gname' :  $('#gname').textbox('getValue'),
	});
	$('#grid').datagrid('clearSelections');
	$('#qrydialog').hide();
}

function initType(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/dealmanage/goodstype!queryComboBox.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#vname').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});

}

/**
 * 清除查询条件
 */
function clearParams(){
	$("#gcode").textbox('setValue',null);
	$("#gname").textbox('setValue',null);
	$("#vname").combobox('setValue',null);
}

/**
 * 取消
 */
function closeCx(){
	$("#qrydialog").hide();
}
