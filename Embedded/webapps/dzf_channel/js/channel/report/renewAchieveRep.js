var grid;

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});

$(function() {
	initPeriod("#qryperiod");
	initQryData();
	//大区、省（市）、会计运营经理下拉初始化
	initQryCommbox();
	//加盟商参照初始化
	initChannel();
	load();
	reloadData();
});

/**
 * 查询日期初始化
 */
function initQryData(){
	$('#jqj').html(parent.SYSTEM.PreDate + ' 至 ' + parent.SYSTEM.LoginDate);
	$("#bdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#edate").datebox("setValue", parent.SYSTEM.LoginDate);
	
	$("#qryperiod").datebox("setValue", parent.SYSTEM.period);
}

/**
 * 查询框监听事件
 */
function initQryLitener(){
	$("#bdate").datebox("readonly", false);
	$("#edate").datebox("readonly", false);
	
	$('#qryperiod').datebox("readonly", true);
	
	$('input:radio[name="seledate"]').change( function(){  
		var ischeck = $('#tddate').is(':checked');
		if(ischeck){
			var sdv = $('#bdate').datebox('getValue');
			var edv = $('#edate').datebox('getValue');
			$('#jqj').html(sdv + ' 至 ' + edv);
			$("#bdate").datebox("readonly", false);
			$("#edate").datebox("readonly", false);
			$('#qryperiod').datebox("readonly", true);
		}else{
			$("#qryperiod").datebox("setValue", parent.SYSTEM.period);
			$('#jqj').html(parent.SYSTEM.period);
			$("#bdate").datebox("readonly", true);
			$("#edate").datebox("readonly", true);
			$('#qryperiod').datebox("readonly", false);
		}
	});
}

/**
 * 数据表格初始化
 */
function load(){
	$('#grid').datagrid({
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
		frozenColumns:[[
		                { field : 'aname',  title : '大区', width : 60,halign:'center',align:'left',},
		                { field : 'uname',  title : '区总', width : 90,halign:'center',align:'left',},
		                { field : 'provname',  title : '省份', width : 140,halign:'center',align:'left',}, 
		                { field : 'incode',  title : '加盟商编码', width : 120,halign:'center',align:'left',},
		                { field : 'corpnm', title : '加盟商名称', width:180,halign:'center',align:'left',
		                	formatter: function (value,row,index) {
		                		if (!isEmpty(row.dreldate)) {
		                			return "<div style='position: relative;'>" + value 
		                			+ "<img style='right: 0; position: absolute;' src='../../images/rescission.png' /></div>";
		                		}else{
		                			return value;
		                		}
		                	}
		                },
        ]],
		columns : [ [ 
		            { field : 'chndate', title : '加盟日期', width:100,halign:'center',align:'center',rowspan:2},
		            { field : 'cuname',  title : '会计运营经理', width : 120,halign:'center',align:'left',rowspan:2},
		            
		            { field : 'stockcust', title : '客户数量', halign:'center',align:'center',colspan:2},
		            { field : 'stockcont', title : '客户合同金额', halign:'center',align:'center',colspan:2},
		            { field : 'renewcust', title : '续费客户数量', halign:'center',align:'center',colspan:2},
		            { field : 'renewcont', title : '续费客户合同金额', halign:'center',align:'center',colspan:2},
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
function reloadData() {
	var url = DZF.contextPath + "/report/renewachieverep!queryRenew.action";
	$('#grid').datagrid('options').url = url;
	
	var bdate = null;//查询开始日期
	var edate = null;//查询结束日期
	var period = null;//查询月份
	
	var jqj = null;//查询期间
	
	var ischeck = $('#tddate').is(':checked');
	if(ischeck){
		bdate = $('#bdate').datebox('getValue'); 
		edate = $('#edate').datebox('getValue'); 
		if(isEmpty(bdate)){
			Public.tips({
				content : '查询开始日期不能为空',
				type : 2
			});
			return;
		}
		if(isEmpty(edate)){
			Public.tips({
				content : '查询结束日期不能为空',
				type : 2
			});
			return;
		}
		if(!isEmpty(bdate) && !isEmpty(edate)){
			if(!checkdate1("bdate","edate")){
				return;
			}		
		}
		jqj = bdate + ' 至 ' + edate;
	}else{
		period = $('#qryperiod').datebox('getValue');
		if(isEmpty(period)){
			Public.tips({
				content : '查询月份不能为空',
				type : 2
			});
			return;
		}
		jqj = period;
	}
	
	//省（市）
	var ovince = $('#ovince').combobox('getValue');
	if (isEmpty(ovince)) {
		ovince = -1;
	}
	//包含已解约加盟商
	var stype = $('#stype').is(':checked') ? 0 : 1;

	$('#grid').datagrid('load', {
		"aname" : $('#aname').combobox('getValue'),//大区
		"ovince" : ovince,//省（市）
		"uid" : $('#uid').combobox('getValue'),//会计运营经理
		"corps" : $("#pk_account").val(),//加盟商
		"stype" : stype,//包含已解约加盟商
		"begdate" : bdate,
		"enddate" : edate,
		"period" : period,
	});
	
	$('#jqj').html(jqj);
	$("#qrydialog").hide();
}

/**
 * 导出
 */
function doExport() {
	var datarows = $('#grid').datagrid("getRows");
	if (datarows == null || datarows.length == 0) {
		Public.tips({
			content : '当前界面数据为空',
			type : 2
		});
		return;
	}
	var callback = function() {
		var columns = $('#grid').datagrid("options").columns[0];
		var djcols = $('#grid').datagrid('getColumnFields', true);
		Business.getFile(DZF.contextPath
				+ '/report/renewachieverep!exportExcel.action', {
			'strlist' : JSON.stringify(datarows),
			'columns' : JSON.stringify(columns),
			'djcols':JSON.stringify(djcols)
		}, true, true);
	}
	checkBtnPower('export', "channel47", callback);
}
