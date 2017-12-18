var isenter = false;//是否快速查询
var loadrows = null;

// 数据表格随窗口大小改变
$(window).resize(function() {
	$('#grid').datagrid('resize', {
		height : Public.setGrid().h,
	});
});

$(function(){
	initListener();
	initDataGrid();
	initChannel();
	reloadData();
	fastQry();
});

//初始化监听
function initListener(){
	// 查询框鼠标经过
	$("#querydate").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
}

function closeCx(){
	$("#qrydialog").hide();
}

function initDataGrid(){
	$('#grid').datagrid({
		//url : contextPath + '/order/orderact!query.action',
		striped : true,
		rownumbers : true,
		fitColumns: false,
		remoteSort : false,
		singleSelect : false,
		height : Public.setGrid().h,
		idField : 'id',
		pagination : true,
		pageSize : 20,
		pageList : [ 20, 50, 100, 200 ],
		showFooter:true,
		columns : [[{width : '100',title : '主键id',field : 'id',checkbox: true},
		            {width : '150',title : '加盟商',field : 'cname',align:'left'},
		            {width : '80',title : '付款类型',field : 'paytype',align:'left',
						formatter: function(value,row,index){
			          		if (value == 0){
								return '预付款';
							} else if(value ==1){
								return '加盟费';
							} 
		            }},
		            /*{width : '80',title : '发票性质',field : 'nature',align:'left',
						formatter: function(value,row,index){
			          		if (value == 0){
								return '公司';
							} else if(value ==1){
								return '个人';
							} 
		            }},*/
		            {width : '150',title : '税号',field : 'taxnum',align:'left'},
		            {width : '80',title : '开票金额',field : 'iprice',align:'right',
		            	formatter: function(value,row,index){
							if (value == 0) {
								return "0.00";
							}
							return formatMny(value);
		            }},
		            {width : '100',title : '发票类型',field : 'itype',align:'left',
		            	formatter: function(value,row,index){
		              		if (value == 0){
								return '专用发票';
							} else if(value ==1){
								return '普通发票';
							} else if(value ==2){
								return '电子普通发票';
							}
		            }},
		            {width : '180',title : '公司地址',field : 'caddr',align:'left'},
		            {width : '100',title : '开票电话',field : 'phone',align:'left'},
		            {width : '150',title : '开户行',field : 'bname',align:'left'},
		            {width : '150',title : '开户账户',field : 'bcode',align:'left'},
		            {width : '130',title : '邮箱',field : 'email',align:'left'},
		            {width : '90',title : '申请时间',field : 'apptime',align:'center'},
		            {width : '90',title : '开票日期',field : 'invtime',align:'center'},
		            {width : '100',title : '经手人',field : 'iperson',align:'left'},
		            {width : '80',title : '发票状态',field : 'istatus',align:'center',
		            	formatter: function(value,row,index){
		              		if (value == 0){
								return '待提交';
							} else if(value ==1){
								return '待开票';
							} else if(value ==2){
								return '已开票';
							}
		            }},
		            {width : '100',title : '备注',field : 'vmome',align:'center'},
		            {width : '100',title : '',field : 'corpid',hidden:true},
		          ]],
		onBeforeLoad : function(param) {
			$.messager.progress({
				text : '数据加载中....'
			});
		},
		onLoadSuccess : function(data) {
			if(!isenter){
				loadrows = data.rows;
			}
			isenter = false;
			$.messager.progress('close');
			$("#qrydialog").hide();
			calFooter();
		}
	});
}

function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
    var iprice = 0;	
    for (var i = 0; i < rows.length; i++) {
    	iprice += parseFloat(rows[i].iprice == undefined ? 0 : rows[i].iprice);
    }
    footerData['cname'] = '合计';
    footerData['iprice'] = iprice;
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}


//初始化加盟商
function initChannel(){
    $('#channel_select').textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#kj_dialog").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择加盟商',
                    modal: true,
                    href: DZF.contextPath + '/ref/channel_select.jsp',
                    buttons: '#kj_buttons'
                });
            }
        }]
    });
}

function reloadData(){
	$('#grid').datagrid('options').url = DZF.contextPath + '/sys/sys_inv_manager!query.action';
	
	var bdate = $('#bdate').datebox('getValue'); //开始日期
	var edate = $('#edate').datebox('getValue'); //结束日期
	$('#querydate').html(bdate + ' 至 ' + edate);
	
    $('#grid').datagrid('load', {
    	corps : $("#pk_account").val(),
        bdate: bdate,
        edate: edate,
        istatus : $('#istatus').combobox('getValue')
    });
    
    $('#qrydialog').hide();
    $('#grid').datagrid('unselectAll');
}

//双击选择公司
function dClickCompany(rowTable){
	var str = "";
	var corpIds = [];
	if(rowTable){
		if(rowTable.length>300){
			Public.tips({content : "一次最多只能选择300个客户!" ,type:2});
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
		$("#channel_select").textbox("setValue",str);
		$("#pk_account").val(corpIds);
	}
	 $("#kj_dialog").dialog('close');
}

function clearParams(){
	$("#pk_account").val(null);
	$("#channel_select").textbox("setValue",null);
}

function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}

//开票
function onBilling(){
	var rows = $('#grid').datagrid("getSelections");
	if(rows == null || rows.length == 0){
		Public.tips({content : "请选择需要处理的数据!" ,type:2});
		return;
	}
	
	var pk_invoices = [];
	
	$.messager.confirm('提示','您是否确定开票？',
			function(conf){
		if(conf){
			for(var i = 0; i < rows.length; i++){
				pk_invoices.push(rows[i].id);
			}
			$.ajax({
				type : 'post',
				url : DZF.contextPath + '/sys/sys_inv_manager!onBilling.action',
				data : {
					"pk_invoices" : JSON.stringify(pk_invoices)
				},
				dataType : 'json',
				success: function(result){
					if(result.success){
						reloadData();
						Public.tips({content : result.msg ,type:0});
					}else{
						Public.tips({content : result.msg ,type:2});
					}
				}
			});
			
		}
	});
}

/**
 * 快速查询
 * @param type
 */
function qryData(type){
	var url = DZF.contextPath + '/sys/sys_inv_manager!query.action';
	$('#grid').datagrid('options').url = url;
	
    $('#grid').datagrid('load', {
    	corps : $("#pk_account").val(),
        istatus : 1,
    });
    
    $('#grid').datagrid('unselectAll');
}

/**
 * 快速过滤
 */
function fastQry() {
	$('#filter_value').textbox('textbox').keydown(function(e) {
		if (e.keyCode == 13) {
			var filtername = $("#filter_value").val();
			if (filtername != "") {
				var jsonStrArr = [];
				if (loadrows) {
					for (var i = 0; i < loadrows.length; i++) {
						var row = loadrows[i];
						if (row != null && !isEmpty(row["cname"])) {
							if (row["cname"].indexOf(filtername) >= 0) {
								jsonStrArr.push(row);
							}
						}
					}
					isenter = true;
					$('#grid').datagrid('loadData', jsonStrArr);
				}
			} else {
				$('#grid').datagrid('loadData', loadrows);
			}
		}
	});
}

function onDelete(){
	var rows = $('#grid').datagrid("getSelections");
	if(rows == null || rows.length == 0){
		Public.tips({content : "请选择需要处理的数据!" ,type:2});
		return;
	}
	
	var invoices = '';
	for(var i=0; i<rows.length;i++){
		invoices = invoices + JSON.stringify(rows[i]);
	}
	var postdata = new Object();
	postdata["invoices"] = invoices;
	
	$.messager.confirm('提示','确认删除这些加盟商的开票申请吗？',
		function(conf){
			if(conf){
				$.ajax({
					type : 'post',
					url : DZF.contextPath + '/sys/sys_inv_manager!delete.action',
					data : postdata,
					dataType : 'json',
					success: function(result){
						if(result.success){
							reloadData();
							Public.tips({content : result.msg ,type:0});
						}else{
							Public.tips({content : result.msg ,type:2});
						}
					}
				});
			}
	});
}

//保存
function save(){
	$('#fp_form').form('submit', {
		url : DZF.contextPath + '/sys/sys_inv_manager!save.action',
		onSubmit : function() {
			//再次进行表单字段验证
			return $(this).form('validate');
		},
		success : function(data, textStatus) {
			var result = JSON.parse(data);
			if (result.success) {
				Public.tips({ content : result.msg,type : 0});
				$('#fp_dialog').dialog('close');
				reloadData();
			} else {
				Public.tips({ content : result.msg, type : 1});
			}
			
		}
	});
}

function onEdit(){
	var row = $("#grid").datagrid("getSelected");
	if (!row) {
		Public.tips({content : "请选择需要处理的数据",type : 2});
		return;
	}
	if(row.istatus == 2){
		Public.tips({content : "已开票，不允许修改",type : 2});
		return;
	}
	$('#fp_dialog').dialog('open');
	$('#fp_form').form('clear');
//	$('#ipaytype').combobox('setValue',row.paytype);
	$('#fp_form').form('load', row);
	$('#tempprice').val(row.iprice);
	getTotalPrice(row.iprice,row.corpid);
}

//可开票金额
var totPrice = 0;
function getTotalPrice(invprice,corpid){
	var ipaytype = $('#paytype').combobox('getValue');
	var postData = {};
	postData.ipaytype = ipaytype;
	postData.corpid = corpid;
	if(invprice){
		postData.invprice = invprice;
	}
	$.ajax({
		type : "POST",
		url : DZF.contextPath + '/sys/sys_inv_manager!queryTotalPrice.action',
		async : false,
		data : postData,
		dataType : 'json',
		success : function(result) {
			if (result.success) {
				totPrice = result.rows.tprice;
				setTotalPrice(totPrice);
			}
		}
	});
}

function setTotalPrice(totalPrice){
	var _price = totalPrice;
	if (_price == 0) {
		_price =  "0.00";
	}
	_price = formatMny(_price);
	$("#tprice").numberbox('setValue',_price);
}

/**
 * 导出
 */
function onExport(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'请选择需导出的数据',type:2});
		return;
	}
	var columns = $('#grid').datagrid("options").columns[0];
	Business.getFile(DZF.contextPath+ '/sys/sys_inv_manager!onExport.action',{'strlist':JSON.stringify(datarows)}, true, true);
}

