var contextPath = DZF.contextPath;

$(function(){
	load();
	year = $("#startYear").combobox("getValue");
	var monthDate = $("#month").val();//当前日期-月
	var yearDate = $("#startYear").combobox("getValue");
   
	$('#startMonth').combobox('setValue', monthDate);//查询框-月
	var qjq = yearDate + "-" + monthDate;
	$("#jqj").html(qjq);
	
	reloadData();
});


$(window).resize(function() {
	$('#grid').datagrid('resize', {
		height : Public.setGrid().h,
		width : 'auto',
	});
});


function load(){
	  grid = $("#grid").datagrid({
			border : true,
			striped : true,
			rownumbers : true,
			pagination : true,// 分页工具栏显示
			pageSize : DZF.pageSize,
			pageList : DZF.pageList,
			height : Public.setGrid().h,
			singleSelect : true,
			showFooter:true,
			columns : [ [ 
			              { field : 'contractid', title : '合同主键', width : '120',hidden:true},
			              { field : 'ucode', title : '公司编码', width : '120',align : 'left',halign : 'center'},
			              { field : 'uname', title : '公司名称', width : '120',align : 'center',halign : 'center'},
			              { field : 'enum', title : '到期合同数', width : '120',align : 'center',halign : 'center'},
			              { field : 'snum', title : '已签合同数', width : '120',align : 'center',halign : 'center'},
			              { field : 'usnum', title : '未签合同数', width : '120',align : 'center',halign : 'center'},
			              { field : 'locorpnum', title : '流失客户数', width : '120',align : 'center',halign : 'center'},
					   ]],
			onLoadSuccess : function(data) {
				$('#grid').datagrid("scrollTo",0);
				$('#grid').datagrid("selectRow", 0); 
			}
	  });
};	



/**
 * 重新加载数据
 * @returns
 */
function reloadData() {
  	var queryParams = $('#grid').datagrid('options').queryParams;
  	$('#grid').datagrid('options').url = DZF.contextPath + '/branch/expcontract!query.action';
	
	var yearDate = $("#startYear").combobox("getValue");
	var monthDate = $("#startMonth").combobox("getValue");
	var qjq = yearDate + "-" + monthDate;
	queryParams['qjq'] = qjq;
	queryParams['uname'] = $('#uname').textbox('getValue');
	queryParams['ucode'] = $('#ucode').textbox('getValue');
	
  	$('#grid').datagrid('options').queryParams = queryParams;
  	$('#grid').datagrid('reload');
  	$("#qrydialog").css("visibility", "hidden");
  	$("#jqj").html(qjq);
  	year = $('#startYear').combobox('getValue');
}



/**
 * 清除查询条件
 * @returns
 */
function clearQuery(){
	$("#ucode").textbox('setValue', null);
	$("#uname").textbox('setValue', null);
}

/**
 * 关闭查询框
 */
function closeCx(){
	$("#qrydialog").css("visibility","hidden");
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
	//var qj = $('#begdate').datebox('getValue')+"至"+$('#enddate').datebox('getValue');
	var qj = $('#jqj').html();
	Business.getFile(DZF.contextPath+ '/branch/expcontract!exportAuditExcel.action',
			{'strlist':JSON.stringify(datarows), 'qj':qj,}, true, true);
}
