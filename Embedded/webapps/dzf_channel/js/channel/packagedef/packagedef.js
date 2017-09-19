var parentRow;

//自适应边框
$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});
$(function() {
	showButtons("brows");
	initGrid();
});

function initGrid(){
	$('#grid').datagrid({
		url : DZF.contextPath + '/channel/packageDef!query.action',
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
    			width : 150,
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
                    	},]
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
                    }
                }
    		}, {
    			field : 'cylnum',
    			title : '收费周期(月)',
    			width : 100,
    			halign : 'center',
    			align : 'right',
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
                    		value: 6,
                    		text: '6'
                    	},{
                    		value: 12,
                    		text: '12'
                    	},{
                    		value: 24,
                    		text: '24'
                    	}]
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
    			width : 100,
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
    			width : 100,
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
    			width : 100,
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
    			width : 100,
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

function addType () {
	showButtons("add");
	$('#grid').datagrid('insertRow',{index: 0,	// 索引从0开始
		row: {typecode:'FW0101'}
	});
	$('#grid').datagrid("beginEdit", 0);
	editIndex = 0;
}

function save () {
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
function reload() {
	var begdate = $("#begdate").datebox('textbox').val();
	var enddate = $("#enddate").datebox('textbox').val();
	$('#grid').datagrid('unselectAll');
	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').url =DZF.contextPath + '/channel/packageDef!query.action';
	queryParams.begdate = begdate;
	queryParams.enddate = enddate;
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
	showButtons("brows");
}
var editIndex = 0;
function modify() {
	var grid = $("#grid");
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
	for (var i = 0; i < len; i++) {
		grid.datagrid("endEdit", i);
	}
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
	endEdit(grid);
	var newRows = grid.datagrid("getChanges", "inserted");
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