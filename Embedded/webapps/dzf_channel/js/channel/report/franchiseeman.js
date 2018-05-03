var contextPath = DZF.contextPath;

$(function() {
	initQry();
	load();
	quickfiltet();
	initDetailGrid();
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
	changeArea();
	changeProvince();
	initArea({"qtype" :1});
	initProvince({"qtype" :1});
	initManager({"qtype" :1});
}

function initArea(queryData){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryArea.action',
		data : queryData,
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

function changeArea(){
	 $("#aname").combobox({
		onChange : function(n, o) {
			var queryData={"qtype" :1};
			if(!isEmpty(n)){
				queryData={'aname' : n,"qtype" :1};
				$('#ovince').combobox('setValue',null);
				$('#cuid').combobox('setValue',null);
			}
			initProvince(queryData);
			initManager(queryData);
		}
	});
}

function changeProvince(){
	 $("#ovince").combobox({
		onChange : function(n, o) {
			var queryData={"qtype" :1};
			if(!isEmpty(n)){
				queryData={'aname' : $("#aname").combobox('getValue'),'ovince':n,"qtype" :1};
				$('#cuid').combobox('setValue',null);
			}
			initManager(queryData);
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
	$('#grid').datagrid('options').url = DZF.contextPath +'/report/manager!query.action';
	$("#grid").datagrid('reload');
	$('#grid').datagrid('unselectAll');
	$("#qrydialog").css("visibility", "hidden");
	status = "brows";
}

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
			"type":3
		},
		showFooter:true,
		columns : [ [ 
		    {width : '130',title : '大区',field : 'aname',align:'left'}, 
		    {width : '90',title : '区总',field : 'uname',align:'left'}, 
		    {width : '110',title : '省（市）',field : 'provname',align:'left'}, 
		 	{width : '90',title : '渠道经理',field : 'cuname',align:'left'}, 
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
			{width : '60',title : '小规模',field : 'xgmNum',align:'right'}, 
			{width : '60',title : '一般人',field : 'ybrNum',align:'right'}, 
			{width : '80',title : '保证金',field : 'bondmny',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},  
			{width : '80',title : '预存款余额',field : 'outmny',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
	  	  	{width : '80',title : '本期预存款',field : 'predeposit',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
		  	{width : '60',title : '提单量',field : 'num',align:'right'}, 
		  	{width : '80',title : '合同代账费',field : 'ntlmny',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
		    {width : '80',title : '预付款扣款',field : 'ndemny',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
			{width : '80',title : '返点扣款',field : 'nderebmny',align:'right',
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
	        	var rows = $('#grid').datagrid('getRows');
	        	if(rows != null && rows.length > 0){
	        		var aname = isEmpty($('#aname').combobox('getValue')) ? null : $('#aname').combobox('getValue');
	        		var ovince = isEmpty($('#ovince').combobox('getValue')) ? -1 : $('#ovince').combobox('getValue');
	        		var cuid = isEmpty($('#cuid').combobox('getValue')) ? null : $('#cuid').combobox('getValue');
	        		$('#grid').datagrid('load', {
	        			"corpnm": filtername,
	        			"bdate": $('#bdate').datebox('getValue'),
	        			"edate": $('#edate').datebox('getValue'),
	        			"type":3,
	        			"aname": aname,
	        			"ovince": ovince,
	        			"cuid": cuid,
	        		});
	        	}else{
	        		$('#grid').datagrid('load', {
	        			"corpnm": filtername,
	        			"bdate": $('#bdate').datebox('getValue'),
	        			"edate": $('#bdate').datebox('getValue'),
	        			"type":3,
	        		});
	        	}
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
	Business.getFile(DZF.contextPath+ '/report/manager!exportExcel.action',{'strlist':JSON.stringify(datarows),'type':3,'qj':$('#jqj').html()}, true, true);
}


