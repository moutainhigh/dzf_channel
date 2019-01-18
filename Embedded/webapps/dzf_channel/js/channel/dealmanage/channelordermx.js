var contextPath = DZF.contextPath;
var editIndex = undefined;

$(function(){
	load();
	initRef();
	reloadData();
	initQry();
	initType();
});

/**
 * 查询数据
 */
function reloadData(){
	var url = DZF.contextPath + '/dealmanage/channelordermx!querymx.action';
	$('#grid').datagrid('options').url = url;
	var ischeck = $('#tjdate').is(':checked');
	var gids=$('#goodsname').combobox('getValues');
	var strgids="";
	for(i=0;i<gids.length;i++){
		strgids+=","+gids[i];
	}
	strgids=strgids.substring(1);
	
	var orderstatus=$('#qstatus').combobox('getValues');
	var strstatus="";
	for(i=0;i<orderstatus.length;i++){
		strstatus+=","+orderstatus[i];
	}
	strstatus=strstatus.substring(1);
	
	if(ischeck){
		bdate = $('#submitbegdate').datebox('getValue'); 
		edate = $('#submitenddate').datebox('getValue'); 
		$('#grid').datagrid('load', {
			'billcode' : $("#qbcode").val(),
			'corpid' : $("#qcpid").val(),
			'vstatus' : strstatus,
			'submitbegin' : bdate,
			'submitend' : edate,
			'gid' :  strgids,
		});
	}else{
		bperiod = $('#kbegdate').datebox('getValue');
		eperiod = $('#kenddate').datebox('getValue');
		$('#grid').datagrid('load', {
			'billcode' : $("#qbcode").val(),
			'corpid' : $("#qcpid").val(),
			'vstatus' :  strstatus,
			'kbegin' : bperiod,
			'kend' : eperiod,
			'gid' :  strgids,
		});
	}
	
	if(ischeck){
		$('#querydate').html(bdate + ' 至 ' + edate);
	}else{
		$('#querydate').html(bperiod + ' 至 ' + eperiod);
	}
	
	$('#grid').datagrid('clearSelections');
	$('#qrydialog').hide();
}

/**
 * 列表表格加载
 */
function load(){
	grid = $('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height : Public.setGrid().h,
		singleSelect : false,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		showFooter:true,
		remoteSort : false,
		idField : 'billid',
		columns : [ [ {
			field : 'ck',
			checkbox : true
		}, {
			width : '100',
			title : '主键',
			field : 'billid',
			hidden : true
		}, {
			width : '100',
			title : '时间戳',
			field : 'updatets',
			hidden : true
		}, {
			width : '150',
			title : '加盟商编码',
			field : 'pcode',
			align : 'left',
            halign : 'center',
		}, {
			width : '180',
			title : '加盟商',
			field : 'pname',
			align : 'left',
            halign : 'center',
		}, {
			width : '160',
			title : '订单编码',
			field : 'billcode',
			align : 'left',
            halign : 'center',
            sortable : true,
		}, {
			width : '100',
			title : '订单金额',
			align:'right',
            halign:'center',
			field : 'ndesummny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '预付款扣款',
			align:'right',
            halign:'center',
			field : 'ndemny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '返点扣款',
			align:'right',
            halign:'center',
			field : 'nderebmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		},{
			width : '100',
			title : '商品',
			field : 'gname',
			align:   'left',
            halign:'center',
		}, {
			width : '100',
			title : '规格',
			field : 'spec',
			align:'center',
            halign:'center',
		}, {
			width : '100',
			title : '型号',
			field : 'type',
			align:'center',
            halign:'center',
		}, {
			width : '100',
			title : '数量',
			field : 'amount',
			align:'right',
            halign:'center',
		}, {
			width : '100',
			title : '单价',
			field : 'price',
			align:'right',
            halign:'center',
            formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '金额',
			field : 'totalmny',
			align:'right',
            halign:'center',
            formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '订单状态',
			field : 'vstatus',
            halign : 'center',
			align : 'center',
			formatter : function(value) {
				if (value == '0')
					return '待确认';
				if (value == '1')
					return '待发货';
				if (value == '2')
					return '已发货';
				if (value == '3')
					return '已收货';
				if (value == '4')
					return '已取消';
			}
		},{
			width : '110',
			title : '扣款日期',
			field : 'confirmtime',
			align : 'center',
            halign : 'center',
            sortable : true,
		},{
			width : '110',
			title : '提交日期',
			field : 'submtime',
			align : 'center',
            halign : 'center',
            sortable : true,
		},{
			width : '110',
			title : '发货日期',
			field : 'sendtime',
			align : 'center',
            halign : 'center',
		},  ] ],
		onLoadSuccess : function(data) {
			parent.$.messager.progress('close');
		},
	});
}

/**
 * 查询商品下拉
 */
function initType(){
	$.ajax({
		type : 'POST',
		async : false,
	    url : DZF.contextPath + '/dealmanage/channelordermx!queryComboBox.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result+ ')');
			if (result.success) {
				$('#goodsname').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});

}
/**
 * 参照初始化
 */
function initRef(){
    $('#qcpname').textbox({
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
    					ovince :"-1"
    				},
    				buttons : [ {
    					text : '确认',
    					handler : function() {
    						selectCorps();
    					}
    				}, {
    					text : '取消',
    					handler : function() {
    						$("#chnDlg").dialog('close');
    					}
    				} ]
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
		
		$("#qcpname").textbox("setValue",str);
		$("#qcpid").val(corpIds);
	}
	 $("#chnDlg").dialog('close');
}



/**
 * 查询初始化
 */
function initQry(){
	// 下拉按钮的事件
	$("#querydate").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	queryBoxChange('#submitbegdate','#submitenddate');
	$("#submitbegdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#submitenddate").datebox("setValue",parent.SYSTEM.LoginDate);
	
	queryBoxChange('#kbegdate','#kenddate');
	$("#kbegdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#kenddate").datebox("setValue",parent.SYSTEM.LoginDate);
	
	$("#querydate").html(parent.SYSTEM.PreDate+" 至  "+parent.SYSTEM.LoginDate);
}



/**
 * 清除查询条件
 */
function clearParams(){
	$("#qbcode").textbox('setValue',null);
	$("#qcpname").textbox('setValue',null);
	$("#qcpid").val(null);
	$("#goodsname").combobox('clear');
}

/**
 * 取消
 */
function closeCx(){
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
	var columns = $('#grid').datagrid("options").columns[0];
	var qjt = $('#submitbegdate').datebox('getValue') + '至' + $('#submitenddate').datebox('getValue');
	var qjk = $('#kbegdate').datebox('getValue') + '至' + $('#kenddate').datebox('getValue');
	var ischeck = $('#tjdate').is(':checked');
	var qj=null;
	if(ischeck){
		qj=qjt;
	}else{
		qj=qjk;
	}
	Business.getFile(DZF.contextPath+ '/dealmanage/channelordermx!exportAuditExcel.action',
			{'strlist':JSON.stringify(datarows), 'qj':qj,}, true, true);
}


