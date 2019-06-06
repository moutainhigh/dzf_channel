$(function(){
    initQry();
    initBranch();
	load();
	reloadData();
});

/**
 * 查询初始化
 */
function initQry(){
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	queryBoxChange('#begdate','#enddate');
	$("#begdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#enddate").datebox("setValue",parent.SYSTEM.LoginDate);
	$("#jqj").html(parent.SYSTEM.PreDate+" 至  "+parent.SYSTEM.LoginDate);
}


/**
 * 列表表格加载
 */
function load(){
	$('#grid').datagrid({
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
		columns : [[	{ field : 'branchname',  title : '机构名称', width : 100,halign:'center',align:'left',rowspan:2},
		                { field : 'innercode',  title : '公司编码', width : 100,halign:'center',align:'left',rowspan:2},
		                { field : 'corpname',  title : '公司名称', width : 160,halign:'center',align:'left',rowspan:2}, 
		                { field : 'allcorp',  title : '现有总客户', width : 90,halign:'center',align:'center',rowspan:2},
		                { field : 'jz', title : '建账客户数', width:240,halign:'center',align:'left',colspan:2,},
			            { field : 'bq', title : '本期', width:100,halign:'center',align:'left',colspan:6},
	                ],[
	                   	{ field : 'ybrcorp', title : '一般人', width : 70,align:'center' },
	                   	{ field : 'xgmcorp', title : '小规模', width : 70,align:'center' },
	                   	{ field : 'addcorp', title : '客户新增数', width : 100,align:'center' },
	                   	{ field : 'losecorp', title : '客户流失数', width : 100,align:'center' },
	                   	{ field : 'contcorp', title : '客户合同数', width : 100,align:'center' },
	                   	{ field : 'totalmny', title : '合同总金额', width : 100,align:'right',formatter :formatMny},
	                   	{ field : 'ysmny', title : '合同已收金额', width : 100,align:'right' ,formatter :formatMny},
	                   	{ field : 'wsmny', title : '合同未收金额', width : 100,align:'right' ,formatter :formatMny},
	                ]],
		onLoadSuccess : function(data) {
            $('#grid').datagrid("scrollTo",0);
            calFooter();
		},
	});
}

function reloadData(){
	var url = DZF.contextPath + '/branch/companyData!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'begdate' : $("#begdate").datebox('getValue'),
		'enddate' : $("#enddate").datebox('getValue'),
		'id' : $("#id").combobox('getValue'),
		'cpkcode' : $("#cpkcode").textbox('getValue'),
		'cpkname' :  $('#cpkname').textbox('getValue'),
	});
	$('#grid').datagrid('clearSelections');
	$('#qrydialog').hide();
}

/**
 * 计算合计
 */
function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
	var allcorp = 0;	
	var ybrcorp = 0;
	var xgmcorp = 0;
	var addcorp = 0;
	var losecorp = 0;
	var contcorp = 0;
	var totalmny = 0;
	var ysmny = 0;
	var wsmny = 0;
	
	for (var i = 0; i < rows.length; i++) {
		allcorp += getFloatValue(rows[i].allcorp);
		ybrcorp += getFloatValue(rows[i].ybrcorp);
		xgmcorp += getFloatValue(rows[i].xgmcorp);
		addcorp += getFloatValue(rows[i].addcorp);
		losecorp += getFloatValue(rows[i].losecorp);
		contcorp += getFloatValue(rows[i].contcorp);
		totalmny += getFloatValue(rows[i].totalmny);
		ysmny += getFloatValue(rows[i].ysmny);
		wsmny += getFloatValue(rows[i].wsmny);
	}
	 footerData['corpname'] = "合计";
	 footerData['allcorp'] = allcorp;
	 footerData['ybrcorp'] = ybrcorp;
	 footerData['xgmcorp'] = xgmcorp;
	 footerData['addcorp'] = addcorp;
	 footerData['losecorp'] = losecorp;
	 footerData['contcorp'] = contcorp;
	 footerData['totalmny'] = totalmny;
	 footerData['ysmny'] = ysmny;
	 footerData['wsmny'] = wsmny;
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}

function initBranch(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/branch/instSetupAct!qryBranchs.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#id').combobox('loadData',result.rows);
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
	$("#id").combobox('setValue',null);
	$("#cpkcode").textbox('setValue',null);
	$("#cpkname").textbox('setValue',null);
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
	Business.getFile(DZF.contextPath+ '/branch/companyData!exportExcel.action',
			{'strlist':JSON.stringify(datarows),'columns':JSON.stringify(columns)}, true, true);
}
