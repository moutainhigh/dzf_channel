var parentRow;
var grid;
//自适应边框
$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});
$(function() {
	initQueryData();
	initGrid();
	reloadData();
});

function initGrid(){
	grid = $('#grid').datagrid({
//		url : DZF.contextPath + '/channel/packageDef!query.action',
		striped : true,
		width: "auto",
		singleSelect : false,
		rownumbers:true,
		height: Public.setGrid().h,
		columns : [ [
             {
            	 field : 'checkbox',
            	 checkbox: true
             },{
    			field : 'typecode',
    			title : '业务类型',
    			width : 120,
    			halign : 'center',
    			align : 'left',
    			formatter: function (value) {
    				var text = "";
    				if (value == 'FW0101')
    					text = "代理记账";
    				return text;
    			},
				editor: {
					type: 'combobox',
					options: {
                    	height: 35,
                    	panelHeight: 80,
                    	showItemIcon: true,
                    	valueField: "value",
                    	editable: false,
                    	textField: "text",
                    	data: [{
                    		value: 'FW0101',
                    		text: '代理记账'
                    	},]
                    }
	            }
    		},
    		 {
    			field : 'taxtype',
    			title : '纳税人资格',
    			width : 120,
    			halign : 'center',
    			align : 'left',
    			editor: {
    				type: 'combobox',
                    options: {
                    	height: 35,
                    	panelHeight: 80,
                    	showItemIcon: true,
                    	valueField: "value",
                    	editable: false,
                    	textField: "text",
                    	data: [{
                    		value: '一般纳税人',
                    		text: '一般纳税人'
                    	},{
                    		value: '小规模纳税人',
                    		text: '小规模纳税人'
                    	},],
                    	required:true,
                    }
                }
    		}, {
    			field : 'comptype',
    			title : '公司类型',
    			width : 120,
    			halign : 'center',
    			align : 'left',
    			formatter : function(value,row,index){
    				if (value == '20')
    					return '个体工商户';
    				return "非个体户";
    			},
    			editor: {
    				type: 'combobox',
                    options: {
                    	height: 35,
                    	panelHeight: 80,
                    	showItemIcon: true,
                    	valueField: "value",
                    	editable: false,
                    	textField: "text",
                    	data: [{
                    		value: '20',
                    		text: '个体工商户'
                    	},{
                    		value: '99',
                    		text: '非个体户'
                    	},],
                    	required:true,
                    }
                }
       		}, {
    			field : 'itype',
    			title : '套餐',
    			width : 100,
    			halign : 'center',
    			align : 'left',
    			formatter : function(value,row,index){
    				if (value == '0')
    					return '常规';
    				return "非常规";
    			},
    			editor: {
    				type: 'combobox',
                    options: {
                    	height: 35,
                    	panelHeight: 80,
                    	showItemIcon: true,
                    	valueField: "value",
                    	editable: false,
                    	textField: "text",
                    	data: [{
                    		value: '0',
                    		text: '常规'
                    	},{
                    		value: '1',
                    		text: '非常规'
                    	},],
                    	required:true,
                    }
                }
    		},{
    			field : 'nmsmny',
    			title : '月服务费',
    			width : 100,
    			align : 'right',
    			halign : 'center',
    			formatter : function(value,row,index){
    				if(value == 0)return "0.00";
    				return formatMny(value);
    			},
    			editor: {
    				type: 'numberbox',
                    options: {
                    	height: 35,
                    	min:0,
                    	precision: 2,
                    	groupSeparator:',',
                    	validType:'length[1,8]',
                    	required:true,
                    }
                }
    		}, {
    			field : 'cylnum',
    			title : '收费周期(月)',
    			width : 100,
    			halign : 'center',
    			align : 'right',
    			editor: {
    				type: 'numberbox',
                    options: {
                    	height: 35,
                    	precision: 0,
                    	min:0,
                    	validType:'length[1,2]',
                    	required:true,
                    }
                }
    		}, {
    			field : 'contcycle',
    			title : '合同期限(月)',
    			width : 100,
    			halign : 'center',
    			align : 'right',
    			editor: {
    				type: 'numberbox',
                    options: {
                    	height: 35,
                    	precision: 0,
                    	min:0,
                    	validType:'length[1,6]',
                    	required:true,
                    }
                }
    		}, {
    			field : 'pubnum',
    			title : '发布个数',
    			width : 100,
    			halign : 'center',
    			align : 'right',
    			editor: {
    				type: 'numberbox',
                    options: {
                    	height: 35,
                    	precision: 0,
                    	min:0,
                    	validType:'length[1,6]',
                    }
                }
    		}, {
    			field : 'dpubdate',
    			title : '发布时间',
    			width : 90,
    			halign : 'center',
    			align : 'center',
//    			editor: {
//    				type: 'datebox',
//                    options: {
//                    	height: 35,
//                    }
//                }
    		}, {
    			field : 'offdate',
    			title : '下架时间',
    			width : 90,
    			halign : 'center',
    			align : 'center',
//    			editor: {
//    				type: 'datebox',
//                    options: {
//                    	height: 35,
//                    }
//                }
    		}, {
    			field : 'coperatorname',
    			title : '录入人',
    			width : 100,
    			halign : 'center',
    			align : 'left',
//    			editor: {
//    				type: 'textbox',
//                    options: {
//                    	height: 35,
//                    }
//                }
    		}, {
    			field : 'vstatus',
    			title : '状态',
    			width : 90,
    			halign : 'center',
    			align : 'center',
    			formatter: function (value) {
    				var text = "";
    				if (value == 1)
    					text = "待发布";
    				else if (value == 2)
    					text = "已发布";
    				else if (value == 3)
    					text = "已下架";
    				return text;
    			},
    		}, {
    			field : 'ispro',
    			title : '是否促销',
    			width : 90,
    			halign : 'center',
    			align : 'center',
    			editor: {type:'checkbox',options : {on:'是',off:'否',},
    				formatter: function (value, row, index) {
    					var checked = (value == '是'||value == 'Y') ? "checked" : "";
    					return '<input type="checkbox" disabled ' + checked + '/>';
    				}}
    		}, {
    			field : 'memo',
    			title : '备注',
    			width : 300,
    			halign : 'center',
    			align : 'left',
    			editor: {
    				type: 'textbox',
                    options: {
                    	height: 35,
                    }
                }
    		}
    	] ]
	});
}

/**
 * 监听查询
 */
function initQueryData(){
	$("#querydate").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	$('#querydate').html(parent.SYSTEM.PreDate + ' 至 ' + parent.SYSTEM.LoginDate);
	$("#begdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#enddate").datebox("setValue", parent.SYSTEM.LoginDate);
}

/**
 * 关闭查询对话框
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 查询框-清除
 */
function clearParams(){
	$('#corpkna_ae').combobox('readonly',true);
	$("#pk_account").val(null);
	$('#taxtype').combobox('setValue', "");
	$('#vstatus').combobox('setValue', "-1");
	$("#cylnum").numberbox("setValue",null);
	$("#contcycle").numberbox("setValue",null);
}

/**
 * 查询
 */
function reloadData(){
	var begdate = $("#begdate").datebox('getValue');
	var enddate = $("#enddate").datebox('getValue');
	var taxtype = $("#taxtype").combobox('getValue');
	var vstatus = $("#vstatus").combobox('getValue');
	var cylnum = $("#cylnum").numberbox('getValue');
	var contcycle = $("#contcycle").numberbox('getValue');
	
	var itype ="";
	if ($("#normal").is(':checked')) {
		itype = "1";
	} 
	if ($("#supple").is(':checked')) {
		itype += "2";
	} 
	
	$('#grid').datagrid('unselectAll');
	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').url =DZF.contextPath + '/channel/packageDef!query.action';
	queryParams.begdate = begdate;
	queryParams.enddate = enddate;
	queryParams.taxtype = taxtype;
	queryParams.vstatus = vstatus;
	if(isEmpty(cylnum)){
		queryParams.cylnum = -1;
	}else{
		queryParams.cylnum = cylnum;
	}
	if(isEmpty(contcycle)){
		queryParams.contcycle = -1;
	}else{
		queryParams.contcycle = contcycle;
	}
	if(isEmpty(itype)){
		queryParams.itype = -1;
	}else{
		queryParams.itype = itype;
	}
	queryParams.itype = itype;
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
	showButtons("brows");
}

function addType () {
	showButtons("add");
	$('#grid').datagrid('insertRow',{index: 0,	// 索引从0开始
		row: {typecode:'FW0101'}
	});
	$('#grid').datagrid("beginEdit", 0);
	editIndex = 0;
}

function save () {
	var flag = endEdit(grid);
	if(!flag){
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return; 
	}
	var submitData = getSubmitData($("#grid"));
	$.ajax({
		type: "POST",
        dataType: "json",
        url: DZF.contextPath + "/channel/packageDef!save.action",
        data: {submitData: submitData},
        success: function(rs) {
        	if (rs.success) {
        		showButtons("brows");
        		$("#grid").datagrid("reload");
                Public.tips({content: rs.msg,type:0});
			} else {
				$("#grid").datagrid("beginEdit", editIndex);
				 Public.tips({content: rs.msg,type:1});
			}
        }
	});
}


function del() {
	var rows = $("#grid").datagrid("getChecked");
	if(rows != null && rows.length > 0){
		$.messager.confirm("确认", "确认删除该套餐吗", function (r) {
			if (r) {
				$.ajax({
					type: "POST",
			        dataType: "json",
			        url: DZF.contextPath + "/channel/packageDef!delete.action",
			        data : {
						"deldata" : JSON.stringify(rows)
					},
			        success: function(rs) {
			        	if (rs.success) {
//							var index = $("#grid").datagrid("getRowIndex", row);
//							$("#grid").datagrid("deleteRow",index);
			        		$("#grid").datagrid("reload");
			                Public.tips({content: rs.msg,type:0});
						} else {
							 Public.tips({content: rs.msg,type:1});
						}
			        }
				});
			}
		});
	}else {
		 Public.tips({content: "请选择要删除的数据",type: 2});
		 return;
	}
}


function publish() {
	var rows = $("#grid").datagrid("getChecked");
	if(rows != null && rows.length > 0){
		$.messager.confirm("确认", "确认发布该套餐吗", function (r) {
			if (r) {
				$.ajax({
					type: "POST",
			        dataType: "json",
			        url: DZF.contextPath + "/channel/packageDef!updatePublish.action",
			        data : {
						"datas" : JSON.stringify(rows)
					},
			        success: function(rs) {
			        	if (rs.success) {
			        		$("#grid").datagrid("reload");
			                Public.tips({content: rs.msg,type:0});
						} else {
							 Public.tips({content: rs.msg,type:1});
						}
			        }
				});
			}
		});
	}else {
		 Public.tips({content: "请选择要操作的数据",type: 2});
		 return;
	}
}

function updateOff() {
	var rows = $("#grid").datagrid("getChecked");
	if(rows != null && rows.length > 0){
		$.messager.confirm("确认", "确认下架该套餐吗", function (r) {
			if (r) {
				$.ajax({
					type: "POST",
			        dataType: "json",
			        url: DZF.contextPath + "/channel/packageDef!updateOff.action",
			        data : {
						"datas" : JSON.stringify(rows)
					},
			        success: function(rs) {
			        	if (rs.success) {
			        		$("#grid").datagrid("reload");
			                Public.tips({content: rs.msg,type:0});
						} else {
							 Public.tips({content: rs.msg,type:1});
						}
			        }
				});
			}
		});
	}else {
		 Public.tips({content: "请选择要操作的数据",type: 2});
		 return;
	}
}

var editIndex = 0;
function modify() {
//	var grid = $("#grid");
	var rows = grid.datagrid("getChecked");
	if(rows){
		if(rows.length > 1){
			Public.tips({content: "请选择一条数据修改",type: 2});
			return;
		}
		var row = rows[0];
		var vstatus = row.vstatus;
		if(vstatus == 2){
			Public.tips({content: "已发布的套餐不允许修改",type: 2});
			return;
		}else if(vstatus == 3){
			Public.tips({content: "已下架的套餐不允许修改",type: 2});
			return;
		}
		showButtons("edit");
		var index = $("#grid").datagrid("getRowIndex", row);
		editIndex=index;
		grid.datagrid("beginEdit", index);
	}else {
		Public.tips({content: "请选择一条数据修改",type: 2});
		return;
	}
}

function cancel () {
	var rows = $("#grid").datagrid("getRows");
	if(rows && rows.length > 0){
		for (var i = 0; i < rows.length; i++) {
			$("#grid").datagrid("cancelEdit", i);
		}
	}
	$("#grid").datagrid("rejectChanges");
	showButtons("brows");
}


function endEdit (grid) {
	var len = grid.datagrid("getRows").length;
	var datagrid;
	for (var i = 0; i < len; i++) {
		datagrid = grid.datagrid('validateRow', i);
		if (!datagrid){
			return false;
		}else{
			grid.datagrid("endEdit", i);
		}
	}
	return true;
}

function showButtons(type) {
	if (type =="brows") {
		$("#addBtn").show();
		$("#editBtn").show();
		$("#publishBtn").show();
		$("#offBtn").show();
		$("#delBtn").show();
		$("#saveBtn").hide();
		$("#cancelBtn").hide();
	} else {
		$("#addBtn").hide();
		$("#editBtn").hide();
		$("#publishBtn").hide();
		$("#offBtn").hide();
		$("#delBtn").hide();
		$("#saveBtn").show();
		$("#cancelBtn").show();
	}
}

function getSubmitData (grid) {
//	endEdit(grid);
	var newRows = $('#grid').datagrid("getChanges", "inserted");
	var datagrid;
	if(newRows != null && newRows.length > 0){
		for (var i = 0; i < newRows.length; i++) {
			datagrid = $("#grid").datagrid("validateRow", i);
			if (!datagrid){
				Public.tips({
					content : "必输信息为空或格式不正确",
					type : 2
				});
				return; 
			}
		}
	}
	
	var deleteRows = grid.datagrid("getChanges", "deleted");
	var updateRows = grid.datagrid("getChanges", "updated");
	
	var submitData = {
		newRows: newRows,
		deleteRows: deleteRows,
		updateRows: updateRows
	}
	submitData = JSON.stringify(submitData);
	return submitData;
}