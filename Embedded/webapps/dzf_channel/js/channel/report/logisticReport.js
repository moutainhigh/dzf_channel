var contextPath = DZF.contextPath;

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : '100%'
	});
});

$(function() {
	loadMater();
	initRef();
});

function reloadData(){
	var item = $(":radio:checked").val(); 
	if(item =="goods"){
		loadGoods();
	}else{
		loadMater();
	}
}


/**
 * 加载商品
 */
function loadGoods(){
	parent.$.messager.progress({text : '数据加载中....'});
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + "/report/logistic!qryGoodsHead.action",
		traditional : true,
		async : false,
		success : function(data) {
			parent.$.messager.progress('close');
			if (data.success) {
				var headData = data.rows;
				setGoodsGrid(headData);
				getGoodsData();
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			parent.$.messager.progress('close');
		}
	});
}

/**
 * 加载物料
 */
function loadMater(){
	parent.$.messager.progress({text : '数据加载中....'});
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + "/report/logistic!qryMaterHead.action",
		traditional : true,
		async : false,
		success : function(data) {
			parent.$.messager.progress('close');
			if (data.success) {
				var headData = data.rows;
				setMaterGrid(headData);
				getMaterData();
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			parent.$.messager.progress('close');
		}
	});
}

/**
 * 更新商品grid
 */
function setGoodsGrid(headData){
	var columns = getGoodsColumn(headData);
	$('#grid').datagrid({
		striped : true,
		title : '',
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : true,
		showFooter:true,
		border : true,
		remoteSort:false,
		columns : columns,
		onLoadSuccess : function(data) {
			
		},
	});		
}

/**
 * 更新物料grid
 */
function setMaterGrid(headData){
	var columns = getMaterColumn(headData);
	$('#grid').datagrid({
		striped : true,
		title : '',
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : true,
		showFooter:true,
		border : true,
		remoteSort:false,
		columns : columns,
		onLoadSuccess : function(data) {
			
		},
	});		
}

function getGoodsData(){
	var rows;
	var params = getParams();
	console.log(params);
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + "/report/logistic!queryGoods.action",
		traditional : true,
		data : params,
		async : false,
		success : function(data) {
			if (data.success) {
				rows = data.rows;
				var children;
				var goods;
				for(var i=0;i<rows.length;i++){
					children = rows[i].children;
					for(var j=0;j<children.length;j++){
						goods = children[j];
						rows[i][goods.id] = goods.name;
					}
				}
			}
		},
	});
	$('#grid').datagrid('loadData',{ total:rows.length, rows:rows});
}


function getMaterData(){
	var rows;
	var params = getParams();
	console.log(params);
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + "/report/logistic!queryMateriel.action",
		traditional : true,
		data : params,
		async : false,
		success : function(data) {
			if (data.success) {
				rows = data.rows;
				var children;
				var mater;
				for(var i=0;i<rows.length;i++){
					children = rows[i].children;
					for(var j=0;j<children.length;j++){
						mater = children[j];
						rows[i][mater.id] = mater.name;
					}
				}
			}
		},
	});
	$('#grid').datagrid('loadData',{ total:rows.length, rows:rows});
}

function getGoodsColumn(headData){
	var columns = new Array(); 
	var columnl = new Array();
	var columnh = new Array();
	
	var columnl1 = {};
	columnl1["title"] = '大区';  
	columnl1["field"] = 'aname';  
	columnl1["rowspan"] = 2;  
	columnl1["width"] = '100'; 
	columnl1["halign"] = 'center'; 
	columnl1["align"] = 'left'; 
	columnl.push(columnl1);
	
	var columnl2 = {};
	columnl2["title"] = '渠道经理';  
	columnl2["field"] = 'mid';  
	columnl2["rowspan"] = 2;  
	columnl2["width"] = '100'; 
	columnl2["halign"] = 'center'; 
	columnl.push(columnl2);
	
	var columnl3 = {};
	columnl3["title"] = '加盟商';  
	columnl3["field"] = 'corpname';  
	columnl3["rowspan"] = 2;  
	columnl3["width"] = '200'; 
	columnl3["halign"] = 'center'; 
	columnl.push(columnl3);
	
	if(headData != null && headData.length>0){
		var columnl3 = {};
		columnl3["title"] = '商品信息';  
		columnl3["field"] = 'goods';  
		columnl3["colspan"] = headData.length;  
		columnl3["width"] = '200'; 
		columnl3["halign"] = 'center'; 
		columnl.push(columnl3);
		for(var i=0;i<headData.length;i++){
			var column = {};
			column["title"] = headData[i].name;  
			column["field"] = headData[i].id;  
			column["width"] = '100'; 
			column["halign"] = 'center';
			column["align"] = 'center'; 
			columnh.push(column);
		}
	}
	
	var columnl3 = {};
	columnl3["title"] = '收货信息';  
	columnl3["field"] = 'receive';  
	columnl3["colspan"] = 3;  
	columnl3["width"] = '120'; 
	columnl3["halign"] = 'center'; 
	columnl.push(columnl3);
	
	var column = {};
	column["title"] = "收货人";  
	column["field"] = "rename";  
	column["width"] = '100'; 
	column["halign"] = 'center';
	column["align"] = 'left'; 
	columnh.push(column);
	var column = {};
	column["title"] = "联系电话";  
	column["field"] = "phone";  
	column["width"] = '90'; 
	column["halign"] = 'center';
	column["align"] = 'left'; 
	columnh.push(column);
	var column = {};
	column["title"] = "地址";  
	column["field"] = "readdress";  
	column["width"] = '300'; 
	column["halign"] = 'center';
	column["align"] = 'left'; 
	columnh.push(column);
	
	
	var columnl3 = {};
	columnl3["title"] = '快递信息';  
	columnl3["field"] = 'kd';  
	columnl3["colspan"] = 4;  
	columnl3["width"] = '200'; 
	columnl3["halign"] = 'center'; 
	columnl.push(columnl3);
	
	var column = {};
	column["title"] = "快递公司";  
	column["field"] = "logunit";  
	column["width"] = '100'; 
	column["halign"] = 'center';
	column["align"] = 'left'; 
	columnh.push(column);
	var column = {};
	column["title"] = "金额";  
	column["field"] = "fcost";  
	column["width"] = '100'; 
	column["halign"] = 'center';
	column["align"] = 'right'; 
	columnh.push(column);
	var column = {};
	column["title"] = "单号";  
	column["field"] = "fcode";  
	column["width"] = '120'; 
	column["halign"] = 'center';
	column["align"] = 'left'; 
	columnh.push(column);
	var column = {};
	column["title"] = "发货日期";  
	column["field"] = "dedate";  
	column["width"] = '100'; 
	column["halign"] = 'center';
	column["align"] = 'center'; 
	columnh.push(column);
	
	var columnl3 = {};
	columnl3["title"] = '备注';  
	columnl3["field"] = 'memo';  
	columnl3["rowspan"] = 2;  
	columnl3["width"] = '200'; 
	columnl3["halign"] = 'center'; 
	columnl.push(columnl3);
	
	columns.push(columnl);
	columns.push(columnh);
	return columns;
}

function getMaterColumn(headData){
	var columns = new Array(); 
	var columnl = new Array();
	var columnh = new Array();
	
	var columnl1 = {};
	columnl1["title"] = '大区';  
	columnl1["field"] = 'aname';  
	columnl1["rowspan"] = 2;  
	columnl1["width"] = '100'; 
	columnl1["halign"] = 'center'; 
	columnl1["align"] = 'left'; 
	columnl.push(columnl1);
	
	var columnl2 = {};
	columnl2["title"] = '渠道经理';  
	columnl2["field"] = 'mid';  
	columnl2["rowspan"] = 2;  
	columnl2["width"] = '100'; 
	columnl2["halign"] = 'center'; 
	columnl.push(columnl2);
	
	var columnl3 = {};
	columnl3["title"] = '加盟商';  
	columnl3["field"] = 'corpname';  
	columnl3["rowspan"] = 2;  
	columnl3["width"] = '200'; 
	columnl3["halign"] = 'center'; 
	columnl.push(columnl3);
	
	if(headData != null && headData.length>0){
		var columnl3 = {};
		columnl3["title"] = '物料信息';  
		columnl3["field"] = 'goods';  
		columnl3["colspan"] = headData.length;  
		columnl3["width"] = '200'; 
		columnl3["halign"] = 'center'; 
		columnl.push(columnl3);
		for(var i=0;i<headData.length;i++){
			var column = {};
			column["title"] = headData[i].name;  
			column["field"] = headData[i].id;  
			column["width"] = '100'; 
			column["halign"] = 'center';
			column["align"] = 'center'; 
			columnh.push(column);
		}
	}
	
	var columnl3 = {};
	columnl3["title"] = '收货信息';  
	columnl3["field"] = 'receive';  
	columnl3["colspan"] = 3;  
	columnl3["width"] = '120'; 
	columnl3["halign"] = 'center'; 
	columnl.push(columnl3);
	
	var column = {};
	column["title"] = "收货人";  
	column["field"] = "rename";  
	column["width"] = '100'; 
	column["halign"] = 'center';
	column["align"] = 'left'; 
	columnh.push(column);
	var column = {};
	column["title"] = "联系电话";  
	column["field"] = "phone";  
	column["width"] = '90'; 
	column["halign"] = 'center';
	column["align"] = 'left'; 
	columnh.push(column);
	var column = {};
	column["title"] = "地址";  
	column["field"] = "readdress";  
	column["width"] = '300'; 
	column["halign"] = 'center';
	column["align"] = 'left'; 
	columnh.push(column);
	
	
	var columnl3 = {};
	columnl3["title"] = '快递信息';  
	columnl3["field"] = 'kd';  
	columnl3["colspan"] = 4;  
	columnl3["width"] = '200'; 
	columnl3["halign"] = 'center'; 
	columnl.push(columnl3);
	
	var column = {};
	column["title"] = "快递公司";  
	column["field"] = "logunit";  
	column["width"] = '100'; 
	column["halign"] = 'center';
	column["align"] = 'left'; 
	columnh.push(column);
	var column = {};
	column["title"] = "金额";  
	column["field"] = "fcost";  
	column["width"] = '100'; 
	column["halign"] = 'center';
	column["align"] = 'right'; 
	columnh.push(column);
	var column = {};
	column["title"] = "单号";  
	column["field"] = "fcode";  
	column["width"] = '120'; 
	column["halign"] = 'center';
	column["align"] = 'left'; 
	columnh.push(column);
	var column = {};
	column["title"] = "发货日期";  
	column["field"] = "dedate";  
	column["width"] = '100'; 
	column["halign"] = 'center';
	column["align"] = 'center'; 
	columnh.push(column);
	
	var columnl3 = {};
	columnl3["title"] = '备注';  
	columnl3["field"] = 'memo';  
	columnl3["rowspan"] = 2;  
	columnl3["width"] = '200'; 
	columnl3["halign"] = 'center'; 
	columnl.push(columnl3);
	
	columns.push(columnl);
	columns.push(columnh);
	return columns;
}

/**
 * 获取查询条件
 */
function getParams(){
	var queryParams = {};
    queryParams['begdate'] = $("#begdate").datebox("getValue");
    queryParams['enddate'] = $("#enddate").datebox("getValue");
    queryParams['corps'] = $("#corpid").val();
    queryParams['ucode'] = $("#ucode").textbox("getValue");
    queryParams['aname'] = $("#aname").combobox("getValue");
    return queryParams;
}


function initRef(){
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	queryBoxChange('#begdate','#enddate');
//	$("#begdate").datebox("setValue", parent.SYSTEM.PreDate);
//	$("#enddate").datebox("setValue",parent.SYSTEM.LoginDate);
//	$("#jqj").html(parent.SYSTEM.PreDate+" 至  "+parent.SYSTEM.LoginDate);
	initCorp();
	initArea();
}

function initArea(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryArea.action',
		data : {"qtype" :3},
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

/**
 * 新增-加盟商参照初始化
 */
function initCorp(){
	$('#corpnm').textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#chnDlg").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择加盟商',
                    modal: true,
                    href: DZF.contextPath + '/ref/channel_select.jsp',
                    queryParams : {
    					ovince :"-4"
    				},
                    buttons: '#chnBtn'
                });
            }
        }]
    });
}

/**
 * 加盟商选择事件
 */
function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}

/**
 * 双击选择加盟商
 * @param rowTable
 */
function dClickCompany(rowTable){
	var str = "";
	var corpIds = [];
	if(rowTable){
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个客户",
				type : 2
			});
			return;
		}
		for(var i=0;i<rowTable.length;i++){
			if(i == rowTable.length - 1){
				str += rowTable[i].uname;
			}else{
				str += rowTable[i].uname+",";
			}
			corpIds.push(rowTable[i].pk_gs);
		}
		$("#corpnm").textbox("setValue",str);
		$("#corpid").val(rowTable[0].pk_gs);
	}
	$("#chnDlg").dialog('close');
}

/**
 * 关闭查询框
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 清除查询框
 */
function clearParams(){
	$("#corpid").val(null);
	$("#corpnm").textbox("setValue",null);
	$('#aname').combobox('setValue', null);
	$('#ucode').textbox("setValue",null);
}

/**
 * 导出  type:1:申请表 2：审核表 3:处理表
 */
function doExport(){
	var datarows = $('#grid').datagrid("getRows");
	if (datarows == null || datarows.length == 0) {
		Public.tips({
			content: '当前界面数据为空',
			type: 2
		});
		return;
	}
	var head0 = $('#grid').datagrid("options").columns[0];//  title+field名称
	var head1 = $('#grid').datagrid("options").columns[1];//  title+field名称
	
	var cols = $('#grid').datagrid('getColumnFields');  // 字段编码
	Business.getFile(DZF.contextPath + "/report/logistic!exportLogistic.action", {
		"strlist": JSON.stringify(datarows),
		'head0':JSON.stringify(head0), 
		'head1':JSON.stringify(head1), 
		'cols':JSON.stringify(cols),
	}, true, true);
		
}
