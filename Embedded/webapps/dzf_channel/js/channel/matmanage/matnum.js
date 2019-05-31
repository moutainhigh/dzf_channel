var contextPath = DZF.contextPath;
$(function(){
	initQry();
	load();
	reloadData();
});

/**
 * 查询初始化
 */
function initQry(){
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	$("#jqj").html(parent.SYSTEM.LoginDate);
}

/**
 * 列表表格加载
 */
function load(){
	grid = $('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : false,
		checkOnSelect :false,
		idField : 'matfileid',
		columns : [ [ {
			width : '100',
			title : '主键',
			field : 'matfileid',
			hidden : true
		}, {
			width : '100',
			title : '时间戳',
			field : 'updatets',
			hidden : true
		}, {
			width : '120',
			title : '物料编码',
			field : 'code',
			align : 'left',
			halign : 'center',
		}, {
			width : '100',
			title : '物料名称',
			align : 'center',
			halign : 'center',
			field : 'wlname',
		}, {
			width : '100',
			title : '单位',
			align : 'center',
			halign : 'center',
			field : 'unit',
		},  {
			width : '145',
			title : '累计入库数量',
			field : 'intnum',
			halign : 'center',
			align : 'center',
		},  {
			width : '145',
			title : '累计发出数量',
			field : 'outnum',
			halign : 'center',
			align : 'center',
		}, {
			width : '145',
			title : '待发出数量',
			field : 'waitnum',
			halign : 'center',
			align : 'center',
		}, {
			field : 'enapplynum',
			title : '可申请数量',
			width : '145',
			halign : 'center',
			align : 'center',
		}, ] ],
		onLoadSuccess : function(data) {
			$('#grid').datagrid("scrollTo", 0);
		},
	});
}

/**
 * 查询数据
 */
function reloadData(){
	var url = DZF.contextPath + '/matmanage/matnum!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'wlname' : $("#wlname").val(),
	});
	$('#grid').datagrid('clearSelections');
	$('#qrydialog').hide();
}


/**
 * 清除查询条件
 */
function clearParams(){
	$("#wlname").textbox('setValue',null);
	$("#sseal").combobox('setValue',0);
}

/**
 * 查询框取消
 */
function closeCx(){
	$("#qrydialog").hide();
}


function doExport(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'当前界面数据为空',type:2});
		return;
	}
	var qj = $('#jqj').html();
	Business.getFile(DZF.contextPath+ '/matmanage/matnum!exportAuditExcel.action',
			{'strlist':JSON.stringify(datarows), 'qj':qj,}, true, true);
}


