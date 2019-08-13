var grid;
var roletype;// 1：渠道；2：培训；3：运营；

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});

$(function() {
	initQryData();
	//大区、省（市）、会计运营经理下拉初始化
	initQryCommbox();
	queryRoleType();
	//加盟商参照初始化
	initChannel(roletype);
	load();
});

/**
 * 查询登录用户角色类型
 */
function queryRoleType() {
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/corp/channel!queryQtype.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				roletype = result.rows == 1 ? -2 : -3;
				if(roletype == -2){
					setReadOnly(true);
				}else{
					setReadOnly(false);
				}
			}
		}
	});
}

/**
 * 设置是否可编辑
 */
function setReadOnly(flag){
	$('#aname').combobox('readonly', flag);
	$('#ovince').combobox('readonly', flag);
	$('#uid').combobox('readonly', flag);
}

/**
 * 查询日期初始化
 */
function initQryData(){
	initPeriod("#begperiod");
	initPeriod("#endperiod");
	var period = parent.SYSTEM.period;
	
	$("#begperiod").datebox("setValue", period);
	$("#endperiod").datebox("setValue", nperiod);
	
	$('#jqj').html(period + ' 至 ' + nperiod);
}

/**
 * 初始化表格
 */
function load() {
	var begperiod = $("#begperiod").datebox("getValue");
	var endperiod = $("#endperiod").datebox("getValue");
	if (begperiod > endperiod) {
		errMsg = "开始期间不能大于结束期间"
	}
	var diff = MonthDiff(begperiod, endperiod) + 1;
	if (diff > 6) {
		Public.tips({
			content : '查询期间不能超过6个月',
			type : 2
		});
		return;
	}
	
	//1、获取展示列：
	var columns = getColumns(begperiod, endperiod, diff);
	
	//2、加载表格：
	initGrid(columns);
	
	setTimeout(function(){
		$('#grid').datagrid('loadData',{ total:0, rows:[]});
		//3、查询数据：
		reloadData();
		
	},100);
}

/**
 * 获取展示列
 * @returns {Array}
 */
function getColumns(begperiod, endperiod, diff){
	var columns = new Array(); 
	var columnsh = new Array();//列及合并列名称
	var columnsb = new Array();//子列表名称集合
	
	var column0 = {};
	column0["title"] = '加盟日期';  
	column0["field"] = 'chndate';  
	column0["width"] = '80'; 
	column0["halign"] = 'center'; 
	column0["align"] = 'center'; 
	column0["rowspan"] = 2; 
	columnsh.push(column0); 
	
	var column1 = {};
	column1["title"] = '会计运营<br>经理';  
	column1["field"] = 'cuname';  
	column1["width"] = '90'; 
	column1["halign"] = 'center'; 
	column1["align"] = 'left'; 
	column1["rowspan"] = 2; 
	columnsh.push(column1);
	
	var column2 = {};
	column2["title"] = '总客户数';  
	column2["field"] = 'corpnum';  
	column2["width"] = '80'; 
	column2["halign"] = 'center'; 
	column2["align"] = 'left'; 
	column2["rowspan"] = 2; 
	columnsh.push(column2);
	
	var column3 = {};
	column3["title"] = '建账数';  
	column3["field"] = 'accnum';  
	column3["width"] = '80'; 
	column3["halign"] = 'center'; 
	column3["align"] = 'left'; 
	column3["rowspan"] = 2; 
	columnsh.push(column3);
	
	var column4 = {};
	column4["title"] = '未建账数';  
	column4["field"] = 'naccnum';  
	column4["width"] = '80'; 
	column4["halign"] = 'center'; 
	column4["align"] = 'left'; 
	column4["rowspan"] = 2; 
	columnsh.push(column4);
	
	var periods = getBetweenPeriod(begperiod, endperiod);
	for(var i = 0; i < diff; i++){
		var column = {};
		column["title"] = periods[i];  
		column["field"] = 'col'+i;  
		column["width"] = '260'; 
		column["colspan"] = 4; 
		columnsh.push(column); 
		
		var column0 = {};
		column0["title"] = '到期数';  
		column0["field"] = 'expire'+i;  
		column0["width"] = '60'; 
		column0["halign"] = 'center'; 
		column0["align"] = 'right'; 
		columnsb.push(column0); 
		
		var column1 = {};
		column1["title"] = '续费数';  
		column1["field"] = 'num'+i;  
		column1["width"] = '60'; 
		column1["halign"] = 'center'; 
		column1["align"] = 'right'; 
		columnsb.push(column1);
		
		var column2 = {};
		column2["title"] = '续费率<br>(%)';  
		column2["field"] = 'rate'+i;  
		column2["width"] = '60'; 
		column2["halign"] = 'center'; 
		column2["align"] = 'right'; 
		column2["formatter"] = formatMny;
		columnsb.push(column2);
		
		var column3 = {};
		column3["title"] = '续费额';  
		column3["field"] = 'mny'+i;  
		column3["width"] = '80'; 
		column3["halign"] = 'center'; 
		column3["align"] = 'right'; 
		column3["formatter"] = formatMny;
		columnsb.push(column3);
		
	}
	columns.push(columnsh);
	columns.push(columnsb);
	return columns;
}

/**
 * 数据表格初始化
 */
function initGrid(columns){
	$('#grid').datagrid({
		striped : true,
		title : '',
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : true,
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
        columns : columns,
        onLoadSuccess : function(data) {
			
		},
	});
}

/**
 * 查询
 */
function reloadData() {
	var url = DZF.contextPath + "/report/renewachieverep!queryRenew.action";
	$('#grid').datagrid('options').url = url;
	
	var period = $("#begperiod").datebox("getValue");
	var nperiod = $("#endperiod").datebox("getValue");
	var jqj = null;//查询期间
	
	//省（市）
	var ovince = $('#ovince').combobox('getValue');
	if (isEmpty(ovince)) {
		ovince = -1;
	}
	//包含已解约加盟商
	var stype = $('#stype').is(':checked') ? 0 : 1;
	var isncust = $('#isncust').combobox('getValue');

	if(isEmpty(isncust)){
		$('#grid').datagrid('load', {
			"aname" : $('#aname').combobox('getValue'),//大区
			"ovince" : ovince,//省（市）
			"uid" : $('#uid').combobox('getValue'),//会计运营经理
			"corps" : $("#pk_account").val(),//加盟商
			"stype" : stype,//包含已解约加盟商
			"bperiod" : period,
			"eperiod" : nperiod,
		});
	}else{
		$('#grid').datagrid('load', {
			"aname" : $('#aname').combobox('getValue'),//大区
			"ovince" : ovince,//省（市）
			"uid" : $('#uid').combobox('getValue'),//会计运营经理
			"corps" : $("#pk_account").val(),//加盟商
			"stype" : stype,//包含已解约加盟商
			"bperiod" : period,
			"eperiod" : nperiod,
			"isncust" : isncust,
		});
	}
	
	$('#jqj').html(period + ' 至 ' + nperiod);
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
