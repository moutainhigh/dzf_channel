var contextPath = DZF.contextPath;
var editIndex = undefined;

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
			width : '100',
			title : '收货人',
			field : 'rename',
			hidden : true
		},{
			width : '100',
			title : '手机号码',
			field : 'phone',
			hidden : true
		},{
			width : '100',
			title : '邮政编码',
			field : 'recode',
			hidden : true
		},{
			width : '100',
			title : '收货地址',
			field : 'readdress',
			hidden : true
		},{
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
			postdata["type"] = 1;
			operdata(postdata, index, 1);
		} else {
			return null;
		}
	});
}

/**
 * 取消订单
 */
function cancOrder(){
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
	$('#cancelDlg').dialog('open').dialog('center').dialog('setTitle', '选择取消原因');
	$('#cancfrom').form('clear');
	$("#reason").textbox({'readonly':true});
	$("#reason").textbox({'required':false});
	$("#reason").textbox({'prompt':null});
	initListen();
	$('#reason1').prop('checked',true);
}

/**
 * 取消原因监听
 */
function initListen(){
	$(":radio").click( function(){
		var reatype = $('input:radio[name="reatype"]:checked').val();
		if(reatype == 3){
			$("#reason").textbox({'readonly':false});
			$("#reason").textbox({'required':true});
			$("#reason").textbox({'prompt':'请输入取消订单原因'});
		}else{
			$("#reason").textbox({'readonly':true});
			$("#reason").textbox({'required':false});
			$("#reason").textbox({'prompt':null});
		}
	});
}

/**
 * 取消订单-保存
 */
function cancSave(){
	if ($("#cancfrom").form('validate')) {
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
		
		var reason = "";
		var reatype = $('input:radio[name="reatype"]:checked').val();
		if(reatype == 1){
			reason = "加盟商账户预付款余额不足";
		}else if(reatype == 2){
			reason = "商品缺货";
		}else if(reatype == 3){
			reason = $('#reason').val();
		}
		row.reason = reason;
		var index =  $('#grid').datagrid('getRowIndex', row);
		var postdata = new Object();
		var data = JSON.stringify(row);
		postdata["data"] = data;
		postdata["type"] = 2;
		operdata(postdata, index, 2);
	} else {
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return; 
	}
}

/**
 * 取消订单-取消
 */
function cancCancel(){
	$('#cancelDlg').dialog('close');
}

/**
 * 商品发货
 */
function sendOut(){
	var rows = $("#grid").datagrid("getChecked");
	if (rows == null || rows.length == 0) {
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});
		return;
	}
	
	var datarray =  new Array();
	for(var i = 0; i < rows.length; i++){
		if(rows[i].vstatus != 1){
			Public.tips({
				content : '订单'+rows[i].billcode+'状态不为待发货',
				type : 2
			});
			return;
		}else{
			datarray.push(rows[i]);
		}
	}
	
	
	$('#setOutDlg').dialog({
		modal : true
	});
	$('#setOutDlg').dialog('open').dialog('center').dialog('setTitle', '商品发货');
	$('#sgrid').datagrid({
		striped : true,
		title : '',
		rownumbers : true,
		singleSelect : false,
		idField : 'billid',
		columns : [ [ {
			field : 'billcode',
			title : '订单编号',
			width : 130,
			halign : 'center',
			align : 'left'
		}, {
			field : 'rename',
			title : '收货人',
			width : 80,
			halign : 'center',
			align : 'left'
		}, {
			field : 'phone',
			title : '手机号码',
			width : 100,
			halign : 'center',
			align : 'left'
		}, {
			field : 'recode',
			title : '邮政编码',
			width : 90,
			halign : 'center',
			align : 'left'
		}, {
			field : 'readdress',
			title : '收货地址',
			width : 180,
			halign : 'center',
			align : 'left'
		}, {
			field : 'logunit',
			title : '物流公司',
			width : 150,
			editor : {
				type : 'textbox',
				options : {
					required : true,
					height : 31
				}
			}
		}, {
			field : 'fcode',
			title : '物流单号',
			width : 150,
			editor : {
				type : 'textbox',
				options : {
					required : true,
					height : 31
				}
			}
		}, {
			field : 'billid',
			title : '主键',
			hidden : true
		}, ] ],
		onClickRow:function(rowIndex,rowData){
			endBodyEdit();
        },
        onDblClickRow:function (rowIndex, rowData) {
        	endBodyEdit();
        	if(editIndex != undefined){
        		if($('#sgrid').datagrid('validateRow', editIndex)){
        			if (rowIndex != undefined) {
        				$("#sgrid").datagrid('beginEdit', rowIndex);
        				editIndex = rowIndex;
        			}           		
        		}else{
        			Public.tips({
        				content : "请先编辑必输项",
        				type : 2
        			});
        		}
        	}else{
        		if (rowIndex != undefined) {
    				$("#sgrid").datagrid('beginEdit', rowIndex);
    				editIndex = rowIndex;
    			}
        	}
        },
	});
	
	$('#sgrid').datagrid('loadData', datarray);
}

/**
 * 编辑结束事件
 */
function endBodyEdit(){
    var rw = $("#sgrid").datagrid('getRows');
 	for ( var i = 0; i < rw.length; i++) {
 		$("#sgrid").datagrid('endEdit', i);
 	}
};

/**
 * 操作数据
 * @param row
 * @param type
 */
function operdata(postdata, index, type){
	$.messager.progress({
		text : '数据操作中....'
	});
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/dealmanage/channelorder!operData.action',
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
				if(type == 2){
					$('#cancelDlg').dialog('close');
				}
				Public.tips({
					content : data.msg,
				});
			}
		},
	});
}

/**
 * 发货-保存
 */
function sendSave(){
	endBodyEdit();
	var rows = $("#sgrid").datagrid("getRows");
	var data = '';
	for(var i = 0; i < rows.length; i++){
		if(isEmpty(rows[i].logunit)){
			Public.tips({
				content : '订单'+rows[i].billcode+'物流公司不能为空',
				type : 2
			});
			return;
		}
		if(isEmpty(rows[i].fcode)){
			Public.tips({
				content : '订单'+rows[i].billcode+'物流单号不能为空',
				type : 2
			});
			return;
		}
		data = data + JSON.stringify(rows[i]);
	}
	if(isEmpty(data)){
		Public.tips({
			content : '操作数据不能为空',
			type : 2
		});
		return;
	}
	
	var postdata = new Object();
	postdata["data"] = data;
	postdata["type"] = 3;
	
	$.messager.progress({
		text : '数据操作中....'
	});
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/dealmanage/channelorder!updateData.action',
		data : postdata,
		traditional : true,
		success : function(result) {
			$.messager.progress('close');
			if (!result.success) {
				Public.tips({
					content : result.msg,
					type : 1
				});
			} else {
				if(result.status == -1){
					Public.tips({
						content : result.msg,
						type : 2
					});
				}else{
					Public.tips({
						content : result.msg,
					});
				}
				$('#setOutDlg').dialog('close');
				var rerows = result.rows;
				if(rerows != null && rerows.length > 0){
					var map = new HashMap(); 
					for(var i = 0; i < rerows.length; i++){
						map.put(rerows[i].billid,rerows[i]);
					}
					var index;
					var indexes = new Array();
					for(var i = 0; i < rows.length; i++){
						if(map.containsKey(rows[i].billid)){
							index = $('#grid').datagrid('getRowIndex', rows[i]);
							indexes.push(index);
						}
					}
					for(var i in indexes){
						$('#grid').datagrid('updateRow', {
							index : indexes[i],
							row : rerows[i]
						});
					}
				}
				$("#grid").datagrid('uncheckAll');
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.messager.progress('close');
		}
	});
	
}

/**
 * 发货-取消
 */
function sendCancel(){
	$('#setOutDlg').dialog('close');
}
