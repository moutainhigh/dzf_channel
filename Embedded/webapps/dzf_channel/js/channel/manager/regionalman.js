var contextPath = DZF.contextPath;

$(function() {
	initQry();
	load();
	quickfiltet();
});

//初始化
function initQry(){
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	queryBoxChange('#bdate','#edate');
	$("#bdate").datebox("setValue", parent.SYSTEM.LoginDate.substring(0,7)+"-01");
	$("#edate").datebox("setValue",parent.SYSTEM.LoginDate);
	$("#jqj").html(parent.SYSTEM.LoginDate.substring(0,7)+"-01"+" 至  "+parent.SYSTEM.LoginDate);
	initProvince();
	initManager();
}

function initProvince(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/sys/sys_area!queryComboxArea.action',
		data : {
			parenter_id : 1,
		},
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#ovince').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

function initManager(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/mana/manager!queryManager.action',
		data : {
			type : 2,
		},
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#cuid').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

//查询框关闭事件
function closeCx() {
	$("#qrydialog").css("visibility", "hidden");
}

// 清空查询条件
function clearCondition(){
	$('#ovince').combobox('select',null);
	$('#cuid').combobox('select',null);
}

// 重新加载数据
function reloadData() {
	var queryParams =new Array();
	var ovince=$('#ovince').combobox('getValue')
	if(ovince!=null&&ovince!=""){
		queryParams['ovince'] = $('#ovince').combobox('getValue');
	}
	var cuid=$('#cuid').combobox('getValue')
	if(cuid!=null&&cuid!=""){
		queryParams['cuid'] = $('#cuid').combobox('getValue');
	}
	queryParams['bdate'] = $('#bdate').datebox('getValue');
	queryParams['edate'] = $('#edate').datebox('getValue');
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('options').url = DZF.contextPath +'/mana/manager!query.action';
	$("#grid").datagrid('reload');
	$('#grid').datagrid('unselectAll');
	$("#qrydialog").css("visibility", "hidden");
	status = "brows";
}

function load() {
	// 列表显示的字段
	$('#grid').datagrid({
		url : DZF.contextPath + '/mana/manager!query.action',
		fit : false,
		rownumbers : true,
		height : Public.setGrid().h,
		width:'100%',
		singleSelect : true,
		queryParams: {
			bdate: $('#bdate').datebox('getValue'),
			edate: $('#edate').datebox('getValue')
		},
//		pageNumber : 1,
//		pageSize : DZF.pageSize,
//		pageList : DZF.pageList,
//		pagination : true,
		columns : [ [ 
		    {width : '200',title : '省（市）',field : 'provname',align:'left'}, 
		    {width : '150',title : '渠道经理',field : 'cuname',align:'left'}, 
			{width : '250',title : '加盟商',field : 'corpnm',align:'left'}, 
		  	{width : '100',title : '保证金',field : 'bondmny',align:'right'},  
	  	  	{width : '100',title : '预存款',field : 'predeposit',align:'right'},
		  	{width : '100',title : '提单量',field : 'num',align:'right'}, 
		  	{width : '100',title : '合同总金额',field : 'ntlmny',align:'right'},
		    {width : '100',title : '扣款金额',field : 'ndeductmny',align:'right'},
			{width : '100',title : '预存款余额',field : 'outmny',align:'right'},
			]],
		onLoadSuccess : function(data) {
		}
	});
}

/**
 * 快速过滤
 */
function quickfiltet(){
	$('#filter_value').textbox('textbox').keydown(function (e) {
		 if (e.keyCode == 13) {
           var filtername = $("#filter_value").val(); 
           if (filtername != "") {
           	var url = DZF.contextPath +'/mana/manager!query.action';
           	$('#grid').datagrid('options').url = url;
           	$('#grid').datagrid('loadData',{ total:0, rows:[]});
           	$('#grid').datagrid('load', {
           		"corpnm" :filtername,
           	});
           }else{
        	   reloadData();
           } 
        }
  });
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
	Business.getFile(DZF.contextPath+ '/mana/manager!exportExcel.action',{'strlist':JSON.stringify(datarows),'type':2}, true, true);
}


