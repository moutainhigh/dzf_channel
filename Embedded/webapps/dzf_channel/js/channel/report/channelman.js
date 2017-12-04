var contextPath = DZF.contextPath;

$(function() {
	$("#bdate").datebox("setValue", parent.SYSTEM.LoginDate.substring(0,7)+"-01");
	$("#edate").datebox("setValue",parent.SYSTEM.LoginDate);
	load();
	quickfiltet();
});

function load() {
	// 列表显示的字段
	$('#grid').datagrid({
		url : DZF.contextPath + '/report/manager!query.action',
		fit : false,
		rownumbers : true,
		height : Public.setGrid().h,
		width:'100%',
		singleSelect : true,
		queryParams: {
			bdate: $('#bdate').datebox('getValue'),
			edate: $('#edate').datebox('getValue'),
			type:1
		},
//		pageNumber : 1,
//		pageSize : DZF.pageSize,
//		pageList : DZF.pageList,
//		pagination : true,
		showFooter:true,
		columns : [ [ 
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

function reloadData(){
	$('#grid').datagrid('load', {
		bdate: $('#bdate').datebox('getValue'),
		edate: $('#edate').datebox('getValue'),
		"type":1
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
            	var url = DZF.contextPath +'/report/manager!query.action';
            	$('#grid').datagrid('options').url = url;
            	$('#grid').datagrid('loadData',{ total:0, rows:[]});
            	$('#grid').datagrid('load', {
            		bdate: $('#bdate').datebox('getValue'),
        			edate: $('#edate').datebox('getValue'),
            		"corpnm" :filtername,
            		"type":1
            	});
            }else{
            	load();
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
	Business.getFile(DZF.contextPath+ '/report/manager!exportExcel.action',{'strlist':JSON.stringify(datarows),'type':1}, true, true);
}
