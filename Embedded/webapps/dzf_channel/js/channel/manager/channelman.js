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
	Business.getFile(DZF.contextPath+ '/mana/manager!exportExcel.action',{'strlist':JSON.stringify(datarows),'type':1}, true, true);
}
