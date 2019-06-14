var grid;

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});

$(function() {
	initPeriod("#qryperiod");
	initQry();
	load();
});

//初始化
function initQry(){
	$("#jqj").html($("#qryperiod").datebox('getValue'));
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	initQryCommbox();
	changeDate();
	initChannel();
}

function changeDate(){
	$("#qryperiod").datebox({
		onChange : function(n, o) {
			$("#jqj").html(n);
		}
	});
}

/**
 * 数据表格初始化
 */
function load(){
	var vince = $('#ovince').combobox('getValue');
	if(isEmpty(vince)){
		vince = -1;
	}
	$('#grid').datagrid({
		url : DZF.contextPath + "/report/renewachieverep!queryRenew.action",
		queryParams:{
			'period' : $('#qryperiod').datebox('getValue'),//查询期间
			'aname' : $('#aname').combobox('getValue'),
			'ovince' : vince ,
			'uid' : $('#uid').combobox('getValue'),
			'stype' : $('#stype').is(':checked') ? 0 : 1,
		},
		striped : true,
		title : '',
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : false,
		pagination : true, //显示分页
		pageSize : 20, //默认20行
		pageList : [ 20, 50, 100, 200 ],
		showRefresh : false,// 不显示分页的刷新按钮
		showFooter : true,
		border : true,
		remoteSort:false,
		columns : [ 
		            [ 
		            { field : 'aname',  title : '大区', width : 60,halign:'center',align:'left',rowspan:2},
		            { field : 'uname',  title : '区总', width : 90,halign:'center',align:'left',rowspan:2},
		            { field : 'provname',  title : '省份', width : 140,halign:'center',align:'left',rowspan:2}, 
		            { field : 'incode',  title : '加盟商编码', width : 120,halign:'center',align:'left',rowspan:2},
		            { field : 'corpnm', title : '加盟商名称', width:180,halign:'center',align:'left',rowspan:2,
		            	formatter: function (value,row,index) {
		            		if (!isEmpty(row.dreldate)) {
		            			return "<div style='position: relative;'>" + value 
		            				+ "<img style='right: 0; position: absolute;' src='../../images/rescission.png' /></div>";
		            		}else{
		            			return value;
		            		}
		            	}
		            },
		            { field : 'chndate', title : '加盟日期', width:100,halign:'center',align:'center',rowspan:2},
		            { field : 'cuname',  title : '会计运营经理', width : 120,halign:'center',align:'left',rowspan:2},
		            
		            { field : 'stockcust', title : '客户数量', halign:'center',align:'center',colspan:2},
		            { field : 'stockcont', title : '客户合同金额', halign:'center',align:'center',colspan:2},
		            { field : 'renewcust', title : '续费客户数量', halign:'center',align:'center',colspan:2},
		            { field : 'renewcont', title : '续费客户合同金额', halign:'center',align:'center',colspan:2},
		             
		            { field : 'renewcustrate', title : '续费客户占比(%)', width:100,halign:'center',align:'right',colspan:2},
		            { field : 'renewcontrate', title : '续费合同占比(%)', width:100,halign:'center',align:'right',colspan:2},
		            
		            { field : 'xqnum', title : '续签客户数', width:100,halign:'center',align:'right',colspan:2},
		           ] ,
        [
            { field : 'stockcusts', title : '小规模', width : 100, halign:'center',align:'right'}, 
            { field : 'stockcustt', title : '一般纳税人', width : 100, halign:'center',align:'right'}, 
            { field : 'stockconts', title : '小规模', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'stockcontt', title : '一般纳税人', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'renewcusts', title : '小规模', width : 100, halign:'center',align:'right'}, 
            { field : 'renewcustt', title : '一般纳税人', width : 100, halign:'center',align:'right'}, 
            { field : 'renewconts', title : '小规模', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'renewcontt', title : '一般纳税人', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            
            { field : 'renewcustrates', title : '小规模', width : 100, formatter:formatLocalMny,halign:'center',align:'right'}, 
            { field : 'renewcustratet', title : '一般纳税人', width : 100, formatter:formatLocalMny,halign:'center',align:'right'}, 
            { field : 'renewcontrates', title : '小规模', width : 100, formatter:formatLocalMny,halign:'center',align:'right'}, 
            { field : 'renewcontratet', title : '一般纳税人', width : 100, formatter:formatLocalMny,halign:'center',align:'right'}, 
            
            { field : 'yrenewnum', title : '应续签', width : 100, halign:'center',align:'center'},
            { field : 'renewnum', title : '已续签', width : 100, halign:'center',align:'center',},
            
        ] ],
        onLoadSuccess : function(data) {
        	var rows = $('#grid').datagrid('getRows');
        	var footerData = new Object();
        	var stockcusts = 0;	//
        	var stockcustt = 0;	//
        	var stockconts = 0;	//
        	var stockcontt = 0;	//
        	
        	var renewcusts = 0;	//
        	var renewcustt = 0;	//
        	var renewconts = 0;	//
        	var renewcontt = 0;	//
        	
        	var yrenewnum = 0;
        	var renewnum = 0;
        	for (var i = 0; i < rows.length; i++) {
        		if(!isEmpty(rows[i].stockcusts)){
        			stockcusts += parseFloat(rows[i].stockcusts);
        		}
        		if(!isEmpty(rows[i].stockcustt)){
        			stockcustt += parseFloat(rows[i].stockcustt);
        		}
        		if(!isEmpty(rows[i].stockconts)){
        			stockconts += parseFloat(rows[i].stockconts);
        		}
        		if(!isEmpty(rows[i].stockcontt)){
        			stockcontt += parseFloat(rows[i].stockcontt);
        		}
        		
        		if(!isEmpty(rows[i].renewcusts)){
        			renewcusts += parseFloat(rows[i].renewcusts);
        		}
        		if(!isEmpty(rows[i].renewcustt)){
        			renewcustt += parseFloat(rows[i].renewcustt);
        		}
        		if(!isEmpty(rows[i].renewconts)){
        			renewconts += parseFloat(rows[i].renewconts);
        		}
        		if(!isEmpty(rows[i].renewcontt)){
        			renewcontt += parseFloat(rows[i].renewcontt);
        		}
        		
        		if(!isEmpty(rows[i].yrenewnum)){
        			yrenewnum += parseFloat(rows[i].yrenewnum);
        		}
        		if(!isEmpty(rows[i].renewnum)){
        			renewnum += parseFloat(rows[i].renewnum);
        		}

        	}
        	footerData['pname'] = '合计';
        	footerData['stockcusts'] = stockcusts;
        	footerData['stockcustt'] = stockcustt;
        	footerData['stockconts'] = stockconts;
        	footerData['stockcontt'] = stockcontt;
        	
        	footerData['renewcusts'] = renewcusts;
        	footerData['renewcustt'] = renewcustt;
        	footerData['renewconts'] = renewconts;
        	footerData['renewcontt'] = renewcontt;
        	
        	footerData['yrenewnum'] = yrenewnum;
        	footerData['renewnum'] = renewnum;
        	
        	var fs=new Array(1);
        	fs[0] = footerData;
        	$('#grid').datagrid('reloadFooter',fs);
        },
	});
}

/**
 * 金额数据格式化
 * @param value
 * @returns {String}
 */
function formatLocalMny(value){
	if(value == null){
		return;
	}
	if(getFloatValue(value) == parseFloat(0)){
		return "--";
	}else{
		if(isContains(value,"-")){
		    var mid = accMul(value, -1);
		    return "-"+formatMny(mid);
		}else{
			return formatMny(value);
		}
	}
}

/**
 * 查询
 */
function reloadData(){
	$('#grid').datagrid('options').url = DZF.contextPath + "/report/renewachieverep!queryRenew.action";
	var queryParams = $('#grid').datagrid('options').queryParams;
	queryParams.period = $('#qryperiod').datebox('getValue');
	queryParams.aname = $('#aname').combobox('getValue');
	var vince=$('#ovince').combobox('getValue');
	if(isEmpty(vince)){
		vince = -1;
	}
	queryParams.ovince = vince;
	queryParams.uid = $('#uid').combobox('getValue');
	queryParams.corps = $("#pk_account").val();
	queryParams.stype = $('#stype').is(':checked')?0:1,
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
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
	var callback=function(){
		var columns = $('#grid').datagrid("options").columns[0];
		Business.getFile(DZF.contextPath+ '/report/renewachieverep!exportExcel.action',
				{'strlist':JSON.stringify(datarows),'columns':JSON.stringify(columns)}, true, true);
	}
	checkBtnPower('export',"channel47",callback);
}
