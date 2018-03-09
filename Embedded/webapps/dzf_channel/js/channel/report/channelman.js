var contextPath = DZF.contextPath;

$(function() {
	$("#bdate").datebox("setValue", parent.SYSTEM.LoginDate.substring(0,7)+"-01");
	$("#edate").datebox("setValue",parent.SYSTEM.LoginDate);
	load();
	quickfiltet();
	initDetailGrid();
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
		showFooter:true,
		columns : [ [ 
			{width : '250',title : '加盟商',field : 'corpnm',align:'left',
				formatter : function(value, row, index) {
					if(value == undefined){
						return;
					}else if(value=="合计"){
						return "合计";
					}else{
						return "<a href='javascript:void(0)' style='color:blue' onclick=\"qryDetail('"+index+"')\">" + value + "</a>";
					}
			}}, 
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
		    {width : '100',title : '预付款扣款',field : 'ndemny',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
			{width : '100',title : '返点扣款',field : 'nderebmny',align:'right',
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
            var ndemny = 0;	
            var nderebmny = 0;	
            var outmny = 0;	
            for (var i = 0; i < rows.length; i++) {
            	bondmny += parseFloat(rows[i].bondmny);
            	predeposit += parseFloat(rows[i].predeposit);
            	num += parseFloat(rows[i].num);
            	ntlmny += parseFloat(rows[i].ntlmny);
            	ndemny += parseFloat(rows[i].ndemny);
            	nderebmny += parseFloat(rows[i].nderebmny);
            	outmny += parseFloat(rows[i].outmny);
            }
            footerData['corpnm'] = '合计';
            footerData['bondmny'] = bondmny;
            footerData['predeposit'] = predeposit;
            footerData['num'] = num;
            footerData['ntlmny'] = ntlmny;
            footerData['ndemny'] = ndemny;
            footerData['nderebmny'] = nderebmny;
            footerData['outmny'] = outmny;
            var fs=new Array(1);
            fs[0] = footerData;
            $('#grid').datagrid('reloadFooter',fs);
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
	var qj = $('#bdate').datebox('getValue') + '至' + $('#edate').datebox('getValue');
	Business.getFile(DZF.contextPath+ '/report/manager!exportExcel.action',{'strlist':JSON.stringify(datarows),'type':1,'qj':qj}, true, true);
}
