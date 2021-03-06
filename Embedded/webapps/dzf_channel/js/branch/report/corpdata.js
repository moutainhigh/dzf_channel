var contextPath = DZF.contextPath;

$(window).resize(function() {
	$('#grid').datagrid('resize', {
		height : Public.setGrid().h,
		width : 'auto',
	});
});

$(function(){
	load();
	initQueryData();
//	reloadData();
	initCpname();
	initPcount();
});

/**
 * 监听日期初始化
 */
function initQueryData(){
	$('#jqj').html(parent.SYSTEM.PreDate + ' 至 ' + parent.SYSTEM.LoginDate);
	$("#bdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#edate").datebox("setValue", parent.SYSTEM.LoginDate);
}

/**
 * 查询公司名称-初始化
 */
function initCpname() {
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/branch/corpdataact!queryAccount.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				$('#cpname').combobox('loadData', result.rows);
				if(result.rows != null && result.rows.length > 0){
					$("#cpname").combobox('setValue', 'pk_all');
				}
			}
		}
	});
}

/**
 * 查询主办会计-初始化
 */
function initPcount() {
	$('#cpcount').searchbox({
		editable : false,
		prompt : '选择会计',
		searcher : function() {
			$('#kjdlg').dialog({
				width : 500,
				height : 500,
				readonly : true,
				close : true,
				title : '选择会计',
				modal : true,
				href : DZF.contextPath + '/branch/ref/kj_select.jsp',
				queryParams : {
					dblClickRowCallback : 'kjselect'
				},
				buttons : [ {
					text : '确认',
					handler : function() {
						kjselect();
						$('#kjdlg').dialog('close');
					}
				}, {
					text : '取消',
					handler : function() {
						$('#kjdlg').dialog('close');
					}
				} ]
			});
		}
	});
}

/**
 * 会计选择事件
 */
function kjselect() {
	var row = $('#kjgrid').datagrid('getSelected');
	if (row) {
		$('#cpcount').textbox('setValue', row.name);
		$('#cpcountid').val(row.id);
		$('#kjdlg').dialog('close');
	} else {
		Public.tips({
			content : "请选择一行数据",
			type : 2
		});
	}
};

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
		pageList : [ 20, 50, 80, 100 ],
		showRefresh : false,// 不显示分页的刷新按钮
		showFooter : true,
		border : true,
		remoteSort:false,
		//冻结在 左边的列 
		frozenColumns:[[
						{ field : 'ck',	checkbox : true },
						{ field : 'pname',  title : '公司名称', width :160,halign:'center',align:'left',},
			            { field : 'ucode',  title : '客户编码', width : 120,halign:'center',align:'left',},
			            { field : 'uname', title : '客户名称', width:180,halign:'center',align:'left',},
		              ]],
		columns : [ 
		            [ 
		            { field : 'ccounty',  title : '省市', width : 140,halign:'center',align:'left',rowspan:2, formatter:showTips}, 
		            { field : 'chname',  title : '纳税人', width : 70,halign:'center',align:'center',rowspan:2},
		            { field : 'bdate', title : '建账日期', width:80,halign:'center',align:'center',rowspan:2},
		            { field : 'jzzt',  title : '记账状态', width : 80,halign:'center',align:'center',rowspan:2},
		            { field : 'bszt',  title : '报税状态', width : 80,halign:'center',align:'center',rowspan:2},
		            { field : 'contract', title : '合同信息', halign:'center',align:'center',colspan:5},
		            
		            { field : 'pcount',  title : '主办会计', width : 80,halign:'center',align:'left',rowspan:2},
		            { field : 'fname',  title : '销售人员', width : 80,halign:'center',align:'left',rowspan:2, formatter:showTips},
		            { field : 'cdate',  title : '录入日期', width : 80,halign:'center',align:'center',rowspan:2},
	               ] ,
           [
            { field : 'cbdate', title : '开始日期', width : 80, halign:'center',align:'center'}, 
            { field : 'edate', title : '结束日期', width : 80, halign:'center',align:'center'}, 
            { field : 'ntlmny', title : '总金额', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'nmny', title : '实收金额', width : 100, formatter:formatMny,halign:'center',align:'right'}, 
            { field : 'smonth', title : '服务余额/月', width : 100, halign:'center',align:'right'}, 
        ] ],
        onLoadSuccess : function(data) {
        	var rows = $('#grid').datagrid('getRows');
        	var footerData = new Object();
        	
        	var ntlmny = 0;
        	var nmny = 0;
        	for (var i = 0; i < rows.length; i++) {
        		if(!isEmpty(rows[i].ntlmny)){
        			ntlmny += rows[i].ntlmny;
        		}
        		if(!isEmpty(rows[i].nmny)){
        			nmny += parseFloat(rows[i].nmny);
        		}
        		
        	}
        	footerData['pname'] = '合计';
        	footerData['ntlmny'] = ntlmny;
        	footerData['nmny'] = nmny;
        	
        	var fs = new Array(1);
        	fs[0] = footerData;
        	$('#grid').datagrid('reloadFooter',fs);
        },
        rowStyler:function(index,row){
			if (row.smonth >= 0 && row.smonth <= 3){
				return 'background-color:pink;';
			}
		}
	});
}	

/**
 * tips显示
 * @param value
 * @returns {String}
 */
function showTips(value){
	if(value != undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}
}

/**
 * 重新加载数据
 * 
 * @returns
 */
function reloadData() {
	var cpid = $("#cpname").combobox('getValue');
	var uname = $('#saler').textbox('getValue');
	var mid = $("#cpcountid").val();
	if((isEmpty(cpid) || cpid == "pk_all") && isEmpty(uname) && isEmpty(mid)){
		Public.tips({
			content : '请选择代账公司或人员进行查询',
			type : 2
		});
		return;
	}
	var bdate = $("#bdate").datebox("getValue");
	var edate = $("#edate").datebox("getValue");
	var url = DZF.contextPath + "/branch/corpdataact!query.action";
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		"begdate" : bdate,
		"enddate" : edate,
		"cpid" : cpid,
		"cpkcode" : $('#cpkcode').textbox('getValue'),
		"cpkname" : $('#cpkname').textbox('getValue'),
		"uname" : uname,//销售人员
		"mid" : mid,//主办会计
	});
	$("#jqj").html(bdate + " 至 " + edate);
  	
  	$("#qrydialog").css("visibility", "hidden");
}



/**
 * 清除查询条件
 * @returns
 */
function clearQuery(){
	$("#cpname").combobox('setValue', 'pk_all');
	$("#cpkcode").textbox('setValue', null);
	$("#cpkname").textbox('setValue', null);
	$("#saler").textbox('setValue', null);
	
	$("#cpcount").textbox('setValue', null);
	$("#cpcountid").val(null);
}

/**
 * 关闭查询框
 */
function closeCx(){
	$("#qrydialog").css("visibility","hidden");
}

/**
 * 导出
 */
function doExport(){
	var datarows = $('#grid').datagrid("getChecked");
	var strlist = '';
	if (datarows != null && datarows.length > 0) {
		strlist = JSON.stringify(datarows);
	}else{
		Public.tips({
			content : '请选择需要导出的数据',
			type : 2
		});
		return;
	}
	
	var url = DZF.contextPath + "/branch/corpdataact!onExport.action";
	Business.getFile(url, {
		'strlist' : strlist,
	}, true, true);
}
