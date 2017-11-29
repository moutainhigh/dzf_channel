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
	initArea();
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
			type :3,
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

function initArea(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/mana/manager!queryArea.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#aname').combobox('loadData',result.rows);
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
	$('#aname').combobox('select',null);
	$('#ovince').combobox('select',null);
	$('#cuid').combobox('select',null);
}
// 重新加载数据
function reloadData() {
	var queryParams =new Array();
	var aname=$('#aname').combobox('getValue')
	if(aname!=null&&aname!=""){
		queryParams['aname'] = $('#aname').combobox('getValue');
	}
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
	queryParams['type'] = 3;
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
			edate: $('#edate').datebox('getValue'),
			"type":3
		},
//		pageNumber : 1,
//		pageSize : DZF.pageSize,
//		pageList : DZF.pageList,
//		pagination : true,
		showFooter:true,
		columns : [ [ 
		    {width : '100',title : '大区',field : 'aname',align:'left'}, 
		    {width : '130',title : '区总',field : 'uname',align:'left'}, 
		    {width : '160',title : '省（市）',field : 'provname',align:'left'}, 
		 	{width : '130',title : '渠道经理',field : 'cuname',align:'left'}, 
			{width : '250',title : '加盟商',field : 'corpnm',align:'left'}, 
			{width : '100',title : '保证金',field : 'bondmny',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},  
	  	  	{width : '100',title : '预存款',field : 'predeposit',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
		  	{width : '100',title : '提单量',field : 'num',align:'right'}, 
		  	{width : '100',title : '合同总金额',field : 'ntlmny',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
		    {width : '100',title : '扣款金额',field : 'ndeductmny',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
			{width : '100',title : '预存款余额',field : 'outmny',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
			]],
		onLoadSuccess : function(data) {
			var rows = $('#grid').datagrid('getRows');
			var footerData = new Object();
            var bondmny = 0;	
            var predeposit = 0;	
            var num = 0;	
            var ntlmny = 0;	
            var ndeductmny = 0;	
            var outmny = 0;	
            for (var i = 0; i < rows.length; i++) {
            	bondmny += parseFloat(rows[i].bondmny);
            	predeposit += parseFloat(rows[i].predeposit);
            	num += parseFloat(rows[i].num);
            	ntlmny += parseFloat(rows[i].ntlmny);
            	ndeductmny += parseFloat(rows[i].ndeductmny);
            	outmny += parseFloat(rows[i].outmny);
            }
            footerData['bondmny'] = bondmny;
            footerData['predeposit'] = predeposit;
            footerData['num'] = num;
            footerData['ntlmny'] = ntlmny;
            footerData['ndeductmny'] = ndeductmny;
            footerData['outmny'] = outmny;
            var fs=new Array(1);
            fs[0] = footerData;
            $('#grid').datagrid('reloadFooter',fs);
//            parent.$.messager.progress('close');
//            $('#grid').datagrid("selectRow", 0);  
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
	Business.getFile(DZF.contextPath+ '/mana/manager!exportExcel.action',{'strlist':JSON.stringify(datarows),'type':3}, true, true);
}


