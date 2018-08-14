$(function(){
	load();
	initRef();
	reloadData();
});

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
		idField : 'gid',
		columns : [ [ {
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
			width : '160',
			title : '订单编码',
			field : 'billcode',
			align : 'left',
            halign : 'center',
		}, {
			width : '150',
			title : '提交时间',
			field : 'submtime',
			align : 'left',
            halign : 'center',
		}, {
			width : '150',
			title : '加盟商编码',
			field : 'pcode',
			align : 'left',
            halign : 'center',
		}, {
			width : '180',
			title : '加盟商名称',
			field : 'pname',
			align : 'left',
            halign : 'center',
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
			title : '预付款付款',
			align:'right',
            halign:'center',
			field : 'ndemny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '返点付款',
			align:'right',
            halign:'center',
			field : 'nderebmny',
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
		} ] ],
		onLoadSuccess : function(data) {
            $('#grid').datagrid("scrollTo",0);
		},
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
 * 查询数据
 */
function reloadData(){
	var url = DZF.contextPath + '/dealmanage/channelorder!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'billcode' : $("#qbcode").val(),
		'corpid' : $("#qcpid").val(),
		'vstatus' :  $('#qstatus').combobox('getValue'),
	});
	$('#grid').datagrid('clearSelections');
}

/**
 * 清除查询条件
 */
function clearParams(){
	$("#qbcode").textbox('setValue',null);
	$("#qcpname").textbox('setValue',null);
	$("#qcpid").val(null);
}

/**
 * 取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 确认
 */
function confirm(){
	var row = null;
	var rows = $("#grid").datagrid("getChecked");
	if (rows == null || rows.length != 1) {
		Public.tips({
			content : '请选择一行数据',
			type : 2
		});
		return;
	} else {
		row = rows[0];
	}
	if (row.vstatus != 0) {
		Public.tips({
			content : '该数据状态不为待确认',
			type : 2
		});
		return;
	}
	
	$.messager.confirm("提示", "确认后将扣除加盟商账户余额，是否确认订单？", function(flag) {
		if (flag) {
			var index =  $('#grid').datagrid('getRowIndex', row);
			var postdata = new Object();
			var data = JSON.stringify(row);
			postdata["data"] = data;
			postdata["type"] = type;
			operdata(postdata, index);
		} else {
			return null;
		}
	});
}

/**
 * 取消订单
 */
function cancel(){
	var row = null;
	var rows = $("#grid").datagrid("getChecked");
	if (rows == null || rows.length != 1) {
		Public.tips({
			content : '请选择一行数据',
			type : 2
		});
		return;
	} else {
		row = rows[0];
	}
	if (row.vstatus != 0) {
		Public.tips({
			content : '该数据状态不为待确认',
			type : 2
		});
		return;
	}
	
}

/**
 * 商品发货
 */
function sendOut(){
	
}

/**
 * 操作数据
 * @param row
 * @param type
 */
function operdata(postdata, index){
	$.messager.progress({
		text : '数据操作中....'
	});
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/dealmanage/channelorder!operdata.action',
		data : postdata,
		traditional : true,
		async : false,
		success : function(data, textStatus) {
			$.messager.progress('close');
			if (!data.success) {
				Public.tips({
					content : data.msg,
					type : 1
				});
			} else {
				$('#grid').datagrid('clearSelections');
				$('#grid').datagrid('updateRow', {
					index : index,
					row : data.rows
				});
				Public.tips({
					content : data.msg,
				});
			}
		},
	});
}

