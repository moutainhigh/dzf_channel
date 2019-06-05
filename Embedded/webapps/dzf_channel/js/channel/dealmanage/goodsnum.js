$(function(){
    initQry();
	load();
	reloadData();
	initType();
});

/**
 * 查询初始化
 */
function initQry(){
	$('#nowdate').datebox({
		onChange: function(newValue, oldValue){
			$('#querydate').text(newValue);
		}
	});
	$("#nowdate").datebox("setValue", parent.SYSTEM.LoginDate);
	$("#querydate").html(parent.SYSTEM.LoginDate);
}



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
			align:'center',
		}, {
			width : '120',
			title : '型号',
			field : 'type',
            halign : 'center',
			align : 'center',
		}, {
			width : '80',
			title : '单位',
			field : 'unit',
            halign : 'center',
			align : 'center',
		}, {
			width : '80',
			title : '售价',
			field : 'price',
            halign : 'center',
			align : 'right',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '累计入库数量',
			field : 'stockin',
            halign : 'center',
			align : 'right',
		},{
			width : '100',
			title : '订单购买数量',
			field : 'lock',
            halign : 'center',
			align : 'right',
		},{
			width : '100',
			title : '累计出库数量',
			field : 'outnum',
            halign : 'center',
			align : 'right',
		},{
			width : '100',
			title : '待出库数量',
			field : 'nooutnum',
            halign : 'center',
			align : 'right',
		},{
			width : '100',
			title : '待发货数量',
			field : 'nosendnum',
            halign : 'center',
			align : 'right',
		},{
			width : '100',
			title : '实际库存数量',
			field : 'stock',
            halign : 'center',
			align : 'right',
		}, {
			width : '100',
			title : '可购买数量',
			field : 'buy',
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
	var nowdate = $("#nowdate").datebox('getValue');
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'nowdate' : nowdate,
		'pk_goodstype' : $("#vname").combobox('getValue'),
		'gcode' : $("#gcode").textbox('getValue'),
		'gname' :  $('#gname').textbox('getValue'),
		
	});
	$('#grid').datagrid('clearSelections');
	$('#qrydialog').hide();
	$('#querydate').html(nowdate);
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

/**
 * 导出
 */
function doExport(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'当前界面数据为空',type:2});
		return;
	}
	var columns = $('#grid').datagrid("options").columns[0];
	var qj = $('#nowdate').datebox('getValue');
	Business.getFile(DZF.contextPath+ '/dealmanage/goodsnum!exportAuditExcel.action',
			{'strlist':JSON.stringify(datarows), 'qj':qj,}, true, true);
}

