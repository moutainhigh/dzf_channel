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
	load();
	initArea();
	initChannel();
	var obj = Public.getRequest();
	var operate = obj.operate;
	if(operate == 'linkqry'){
		queryLink(obj)
	}else{
		reloadData();
	}
	fastQry();
});

/**
 * 
 * 穿透查询
 */
function queryLink(obj){
	$('#grid').datagrid('options').url = DZF.contextPath + '/sys/sys_inv_manager!query.action';
    $('#grid').datagrid('load', {
    	corps : obj.corpid,
        edate: obj.edate,
        qrytype : 1,
        isourtype : obj.isourtype,
    });
    $('#grid').datagrid('unselectAll');
    $('#querydate').html(obj.edate);
}

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

/**
 * 列表表格初始化
 */
function load(){
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
		pageList : [ 20, 50, 100, 200, 1000 ],
		showFooter:true,
		columns : [[{width : '100',title : '主键id',field : 'id',checkbox: true},
		            {width : '140',title : '大区',field : 'aname',align : 'left'},
		            {width : '150',title : '加盟商',field : 'cname',align:'left'},
		            {width : '80',title : '付款类型',field : 'paytype',align:'left',
						formatter: function(value,row,index){
			          		if (value == 0){
								return '预付款';
							} else if(value == 1){
								return '加盟费';
							} else if(value == 2){
								return '预付款+返点';
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
								return '电子发票';
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
							} else if(value ==3){
								return '开票失败';
							}
		            }},
		            {width : '100',title : '备注',field : 'vmome',align:'center'},
		            {width : '100',title : '换票说明',field : 'vcmemo',align:'center',formatter : function(value) {
			    		if(value!=undefined){
			    			return "<span title='" + value + "'>" + value + "</span>";
			    		}
					}},
				
		            {width : '160',title : '发票流水号',field : 'reqserialno',align:'left'},
		            {width : '200',title : '二维码URL',field : 'qrcodepath',align:'center',formatter:codeLink,},
		            
		            {width : '100',title : '',field : 'corpid',hidden:true},
		            {width : '100',title : '',field : 'updatets',hidden:true},
		          ]],
		onBeforeLoad : function(param) {
			$.messager.progress({
				text : '数据加载中....'
			});
		},
		onLoadSuccess : function(data) {
			console.log(data)
			for(var i = 0;i<data.rows.length;i++){
				if(data.rows[i].istatus == 2 && data.rows[i].vcmemo !== " " ){
					console.log($(".datagrid-view2 .datagrid-body .datagrid-row").eq(i).find(".datagrid-cell-c1-istatus").parent())
					$(".datagrid-view2 .datagrid-body .datagrid-row").eq(i).find(".datagrid-cell-c1-istatus").parent().css("background","url(../../img/getexchange.png) no-repeat")
				}
			}
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

function codeLink(value,row,index){
	if(!isEmpty(row.qrcodepath)){
		return '<a href="'+value+'" style="color:blue" target="view_window" ">'+value+'</a>';
	}
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
                    queryParams : {
    					ovince :"-1"
    				},
                    buttons: '#kj_buttons'
                    	
                });
            }
        }]
    });
}

function reloadData(){
	$('#grid').datagrid('options').url = DZF.contextPath + '/sys/sys_inv_manager!query.action';
	
	var ischeck = $('#sq_tddate').is(':checked');
	var bdate = ''; //开始日期
	var edate = ''; //结束日期
	if(ischeck){
		qrytype = 1;
		bdate = $('#bdate').datebox('getValue'); //开始日期
		edate = $('#edate').datebox('getValue'); //结束日期
	}else{
		qrytype = 2;
		bdate = $('#kp_bdate').datebox('getValue'); //开始日期
		edate = $('#kp_edate').datebox('getValue'); //结束日期
	}

	$('#querydate').html(bdate + ' 至 ' + edate);
	
    $('#grid').datagrid('load', {
    	corps : $("#pk_account").val(),
        bdate: bdate,
        edate: edate,
        istatus : $('#istatus').combobox('getValue'),
        itype : $('#qitype').combobox('getValue'),
        qrytype : qrytype,
        aname　: $("#aname").combobox('getValue'),
//        paytype : $('#qpaytype').combobox('getValue'),
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
	$('#aname').combobox('setValue', null);
	$("#channel_select").textbox("setValue",null);
}

function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}

/**
 * 纸质开票
 */
function onBilling(){
	var rows = $('#grid').datagrid("getSelections");
	if(rows == null || rows.length == 0){
		Public.tips({content : "请选择需要处理的数据!" ,type:2});
		return;
	}
	$('#billing').dialog('open');
	$('#invtime').datebox('setValue',parent.SYSTEM.LoginDate);
}

/**
 * 纸质票确认
 */
function onBill(){
	var rows = $('#grid').datagrid("getSelections");
	if(rows == null || rows.length == 0){
		Public.tips({content : "请选择需要处理的数据!" ,type:2});
		return;
	}
	
	var pk_invoices = [];

	for(var i = 0; i < rows.length; i++){
		pk_invoices.push(rows[i].id);
	}
	$.ajax({
		type : 'post',
		url : DZF.contextPath + '/sys/sys_inv_manager!onBilling.action',
		data : {
			"pk_invoices" : JSON.stringify(pk_invoices),
			"invtime" : $('#invtime').datebox('getValue'),
		},
		dataType : 'json',
		success: function(result){
			if(result.success){
				reloadData();
				Public.tips({content : result.msg ,type:0});
				$('#billing').dialog('close')
			}else{
				Public.tips({content : result.msg ,type:2});
			}
		}
	});
}


function onAutoBill(){

	var rows = $('#grid').datagrid("getSelections");
	if(rows == null || rows.length == 0){
		Public.tips({content : "请选择需要处理的数据!" ,type:2});
		return;
	}
	
	var ids = [];
	var msg = '已选中'+rows.length+'张开票申请，请确认电票余量充足，传自动开票接口后不可取消，请慎重操作！'
	$.messager.confirm('提示',msg,
	  function(conf){
		if(conf){
			$.messager.progress({text : '开票中，请稍候.....'});
			for(var i = 0; i < rows.length; i++){
				ids.push(rows[i].id);
			}
			$.ajax({
				type : 'post',
				url : DZF.contextPath + '/sys/sys_inv_manager!onAutoBill.action',
				data : {
					"ids" : JSON.stringify(ids)
				},
				dataType : 'json',
				success: function(result){
					if(result.success){
						reloadData();
						Public.tips({content : result.msg ,type:0});
						$.messager.progress('close');
					}else{
//						Public.tips({content : result.msg ,type:2});
						$.messager.alert('提示',result.msg); 
						$.messager.progress('close');
					}
				}
			});
			
		}
	});

}

function queryInvInfo(){
	initInvGrid();
	$.ajax({
		type : 'post',
		url : DZF.contextPath + '/sys/sys_inv_manager!queryInvInfo.action',
		dataType : 'json',
		success: function(result){
			if(result.success){
				var res = result.rows;
				$('#invInfo').dialog('open');
				$('#gridInvInfo').datagrid('loadData',res);
				$('#qrydate').html(parent.SYSTEM.ServerTime);
			}else{
				$.messager.alert('提示',result.msg); 
			}
		}
	});
}

function initInvGrid(){
	invgridh = $('#gridInvInfo').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height:'380',
		singleSelect : true,
		showFooter:true,
		columns : [ [ {
			width : '80',
			title : '分机号',
			align:'center',
			halign:'center',
			field : 'extensionNum',
		}, {
			width : '150',
			title : '发票种类',
            halign:'center',
			field : 'invoiceKindCode',
		},{
			width : '100',
			title : '发票代码',
			align:'right',
            halign:'center',
			field : 'invoiceCode',
		},{
			width : '100',
			title : '发票起始号码',
			align:'right',
            halign:'center',
			field : 'invoiceStartNo',
		}, {
			width : '100',
			title : '发票终止号码',
			align:'center',
            halign:'center',
			field : 'invoiceEndNo',
		},{
			width : '90',
			title : '剩余份数',
			align:'right',
            halign:'center',
			field : 'invoiceSurplusNum',
		},{
			width : '150',
			title : '购买日期',
			align:'right',
            halign:'center',
			field : 'invoiceBuyTime',
		},] ],
		onLoadSuccess : function(data) {
			var rows = $('#gridInvInfo').datagrid('getRows');
			var footerData = new Object();
			var invoiceSurplusNum = parseFloat(0);
            for (var i = 0; i < rows.length; i++) {
            	invoiceSurplusNum += getFloatValue(rows[i].invoiceSurplusNum);
            }
            footerData['extensionNum'] = '合计';
            footerData['invoiceSurplusNum'] = invoiceSurplusNum;
            var fs=new Array(1);
            fs[0] = footerData;
            $('#gridInvInfo').datagrid('reloadFooter',fs);
            $('#gridInvInfo').datagrid("scrollTo",0);
		},
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
			if(filtername != ""){
				var url = DZF.contextPath + '/sys/sys_inv_manager!query.action';
				$('#grid').datagrid('options').url = url;
				
				var ischeck = $('#sq_tddate').is(':checked');
				var bdate = ''; //开始日期
				var edate = ''; //结束日期
				if(ischeck){
					qrytype = 1;
					bdate = $('#bdate').datebox('getValue'); //开始日期
					edate = $('#edate').datebox('getValue'); //结束日期
				}else{
					qrytype = 2;
					bdate = $('#kp_bdate').datebox('getValue'); //开始日期
					edate = $('#kp_edate').datebox('getValue'); //结束日期
				}
				
				$('#grid').datagrid('load', {
					bdate: bdate,
					edate: edate,
					corps : $("#pk_account").val(),
					istatus : $('#istatus').combobox('getValue'),
					itype : $('#itype').combobox('getValue'),
					cname: filtername,
					aname　: $("#aname").combobox('getValue'),
					qrytype : qrytype,
				});
				
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
	if(row.isourtype == 2){
		Public.tips({content : "加盟商订单开票，不允许修改",type : 2});
		return;
	}
	$('#fp_dialog').dialog('open');
	$('#fp_form').form('clear');
//	$('#ipaytype').combobox('setValue',row.paytype);
	$('#fp_form').form('load', row);
	$('#tempprice').val(row.iprice);
	//发票来源类型  1：合同扣款开票； 2：商品扣款开票；
	if(row.isourtype == 1){
		$("#iprice").numberbox("readonly",false);//开票金额
		getTotalPrice(row.iprice,row.corpid);//可开票金额
		$('#itype').combobox("readonly",false);//发票类型
	}/*else if(row.isourtype == 2){
		$("#iprice").numberbox("readonly",true);//开票金额
		$("#tprice").numberbox('setValue', row.iprice);//可开票金额
		$('#itype').combobox("readonly",true);//发票类型
	}*/
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
	var callback=function(){
		var columns = $('#grid').datagrid("options").columns[0];
		Business.getFile(DZF.contextPath+ '/sys/sys_inv_manager!onExport.action',
				{'strlist':JSON.stringify(datarows),'qj':$('#querydate').html()}, true, true);
	}
	checkBtnPower('export','channel39',callback);
}

/**
 * 导出电票余量
 */
function onExportInvInfo(){
	var datarows = $('#gridInvInfo').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'请选择需导出的数据',type:2});
		return;
	}
	var columns = $('#gridInvInfo').datagrid("options").columns[0];
	Business.getFile(DZF.contextPath+ '/sys/sys_inv_manager!onExportInvInfo.action',{'strlist':JSON.stringify(datarows),'qj':$('#qrydate').html()}, true, true);
}

/**
 * 纸质开票
 */
function onChange(){
	var rows = $('#grid').datagrid("getSelections");
	if(rows == null || rows.length == 0){
		Public.tips({content : "请选择需要处理的数据!" ,type:2});
		return;
	}else if(rows.length > 1){
		Public.tips({content : "只能选择一条数据处理。" ,type:2});
		return;
	}
	if(rows[0].istatus != 2){
		Public.tips({content : "只有已开票的单据可以换票。" ,type:2});
		return;
	}
	$('#change').dialog('open');
	$('#dcdate').datebox('setValue',parent.SYSTEM.LoginDate);
}


/**
 * 换票
 */
function change(){
	var rows = $('#grid').datagrid("getSelections");
	if(rows == null || rows.length == 0){
		Public.tips({content : "请选择需要处理的数据!" ,type:2});
		return;
	}else if(rows.length > 1){
		Public.tips({content : "只能选择一条数据处理。" ,type:2});
		return;
	}
	$.ajax({
		type : 'post',
		url : DZF.contextPath + '/sys/sys_inv_manager!onChange.action',
		data : {
			"id" : rows[0].id,
			"dcdate" : $('#dcdate').datebox('getValue'),
			"vcmemo" : $('#vcmemo').textbox('getValue'),
		},
		dataType : 'json',
		success: function(result){
			if(result.success){
				reloadData();
				Public.tips({content : result.msg ,type:0});
				$('#change').dialog('close')
			}else{
				Public.tips({content : result.msg ,type:2});
			}
		}
	});
}

