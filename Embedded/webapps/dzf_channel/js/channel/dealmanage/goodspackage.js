var editpIndex;
function setPackage() {
	showButtons("brows");
	$('#package_dialog').dialog('open');
	initPackageGrid();
}

/**
 * 套餐初始化
 */
function initPackageGrid() {
	var goods = null;
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/dealmanage/goodsmanage!queryComboBox.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				goods = result.rows;
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});

	$("#gridp").datagrid({
		url : DZF.contextPath + '/dealmanage/goodspackage!query.action',
		height : 350,
		width : 1000,
		singleSelect : false,
		columns : [ [ {
			field : 'checkbox',
			checkbox : true
		}, {
			width : '100',
			title : '主键',
			field : 'pid',
			hidden : true
		}, {
			width : '100',
			title : '时间戳',
			field : 'updatets',
			hidden : true
		}, {
			field : 'gname',
			title : '商品',
			width : "180",
			align : 'center',
			halign : 'center',
			editor : {
				type : 'combobox',
				options : {
					height : 31,
					panelHeight : 160,
					showItemIcon : true,
					valueField : "name",
					editable : true,
					required : true,
					textField : "name",
					data : goods,
					onSelect : function(rec) {
						var gid = $('#gridp').datagrid('getEditor', {
							index : editpIndex,
							field : 'gid'
						});
						$(gid.target).textbox('setValue', rec.gid);

						var specid = $('#gridp').datagrid('getEditor', {
							index : editpIndex,
							field : 'specid'
						});
						$(specid.target).textbox('setValue', rec.id);

						var spec = $('#gridp').datagrid('getEditor', {
							index : editpIndex,
							field : 'spec'
						});
						$(spec.target).textbox('setValue', rec.spec);

						var type = $('#gridp').datagrid('getEditor', {
							index : editpIndex,
							field : 'type'
						});
						$(type.target).textbox('setValue', rec.type);

						var tstp = $('#gridp').datagrid('getEditor', {
							index : editpIndex,
							field : 'tstp'
						});
						$(tstp.target).textbox('setValue', rec.tstp);

						var price = $('#gridp').datagrid('getEditor', {
							index : editpIndex,
							field : 'price'
						});
						$(price.target).textbox('setValue', rec.price);

					}
				}
			},
		}, {
			field : 'gid',
			title : '商品主键',
			hidden : true,
			editor : {
				type : 'textbox'
			}
		}, {
			field : 'tstp',
			title : '商品最新时间戳',
			hidden : true,
			editor : {
				type : 'textbox'
			}
		}, {
			field : 'specid',
			title : '商品规格主键',
			hidden : true,
			editor : {
				type : 'textbox'
			}
		}, {
			field : 'spec',
			title : '规格',
			width : "110",
			align : 'center',
			halign : 'center',
			editor : {
				type : 'textbox',
				options : {
					height : 31,
					editable : false,
					readonly : true
				}
			}
		}, {
			field : 'type',
			title : '型号',
			width : "110",
			align : 'center',
			halign : 'center',
			editor : {
				type : 'textbox',
				options : {
					height : 31,
					editable : false,
					readonly : true
				}
			}
		}, {
			field : 'mname',
			title : '计量单位',
			width : "100",
			align : 'right',
			halign : 'center',
			editor : {
				type : 'textbox',
				options : {
					height : 31,
					required : true,
					validType : [ 'length[0,10]' ],
					invalidMessage : "计量单位最大长度不能超过10",
				}
			},
		}, {
			field : 'num',
			title : '数量',
			width : "100",
			align : 'right',
			halign : 'center',
			editor : {
				type : 'numberbox',
				options : {
					height : 31,
					required : true,
					precision : 0,
					min : 1,
					max : 9999,
					onChange : function(n, o) {

					}
				}
			},
		}, {
			field : 'price',
			title : '单价',
			width : "120",
			align : 'right',
			halign : 'center',
			editor : {
				type : 'numberbox',
				options : {
					height : 31,
					readonly : true,
					precision : 2,
					min : 0,
					max : 99999,
				}
			},
			formatter : formatMny,
		}, /*{
			field : 'ispackage',
			title : '只买套餐',
			width : 100,
			align : 'center',
			formatter : packageformat,
			editor : {
				type : 'checkbox',
				options : {
					on : '是',
					off : '否',
				},
				formatter : function(value, row, index) {
					var checked = (value == '是' || value == 'Y') ? "checked" : "";
					return '<input type="checkbox" disabled ' + checked + '/>';
				}
			}
		},*/ {
			width : '100',
			title : '商品状态',
			field : 'status',
			halign : 'center',
			align : 'center',
			formatter : function(value) {
				if (value == '1')
					return '已保存';
				if (value == '2')
					return '已发布';
				if (value == '3')
					return '已下架';
			}
		}, ] ],
	});
}

///**
// * 只买套餐格式化
// * 
// * @param value
// * @returns {String}
// */
//function packageformat(value, row) {
//	if (value && (value == 'Y' || value == "是")) {
//		return "<input type=\"checkbox\" checked=\"checked\" onclick=\"return false;\" >";
//	} else {
//		return "<input type=\"checkbox\" onclick=\"return false;\" >";
//	}
//}

/**
 * 新增（套餐）
 */
function addP() {
	showButtons("add");
	$('#gridp').datagrid('insertRow', {
		index : 0, // 索引从0开始
		row : {}
	});
	$('#gridp').datagrid("beginEdit", 0);
	editpIndex = 0;
}

/**
 * 修改（套餐）
 */
function editP() {
	var rows = $("#gridp").datagrid("getChecked");
	if (rows == null || rows.length != 1) {
		Public.tips({
			content : "请选择一条数据修改",
			type : 2
		});
		return;
	}
	if (rows[0].status == 2) {
		Public.tips({
			content : "已发布的套餐不允许修改",
			type : 2
		});
		return;
	}
	showButtons("edit");
	editpIndex = $("#gridp").datagrid("getRowIndex", rows[0]);
	$("#gridp").datagrid("beginEdit", editpIndex);
}

/**
 * 发布（套餐）
 */
function publishP() {
	var rows = $("#gridp").datagrid("getChecked");
	if (rows == null || rows.length <= 0) {
		Public.tips({
			content : "请选择需要发布的数据",
			type : 2
		});
		return;
	}
	operateData(rows, 1);
}

/**
 * 下架（套餐）
 */
function offP() {
	var rows = $("#gridp").datagrid("getChecked");
	if (rows == null || rows.length <= 0) {
		Public.tips({
			content : "请选择需要下架的数据",
			type : 2
		});
		return;
	}
	operateData(rows, 2);
}

/**
 * 操作数据
 * @param rows
 * @param type 1：发布；2：下架；
 */
function operateData(rows, type){
	var msg;
	if (type == 1) {
		msg = "确认发布该套餐吗";
	} else if (type == 2) {
		msg = "确认下架该套餐吗";
	}
	
	var datas = '';
	if (rows != null && rows.length > 0) {
		for (var i = 0; i < rows.length; i++) {
			datas = datas + JSON.stringify(rows[i]);
		}
	}
	
	var postdata = new Object();
	postdata["datas"] = datas;
	postdata["opertype"] = type;
	
	$.messager.confirm("确认", msg, function (r) {
		if (r) {
			$.ajax({
				type : "POST",
				dataType : "json",
				url : DZF.contextPath + "/dealmanage/goodspackage!operateData.action",
				data : postdata,
				success : function(rs) {
					if (rs.success) {
						$("#gridp").datagrid("reload");
						Public.tips({
							content : rs.msg,
							type : 0
						});
					} else {
						Public.tips({
							content : rs.msg,
							type : 1
						});
					}
				}
			});
		}
	});
}

/**
 * 删除（套餐）
 */
function delP() {
	var rows = $("#gridp").datagrid("getChecked");
	if (rows == null || rows.length != 1) {
		Public.tips({
			content : "请选择一条数据删除",
			type : 2
		});
		return;
	}
	if (rows[0].status == 2) {
		Public.tips({
			content : "已发布的套餐不允许删除",
			type : 2
		});
		return;
	}
	
	$.messager.confirm("确认", "确认删除该套餐吗", function (r) {
		if (r) {
			$.ajax({
				type : "POST",
				dataType : "json",
				url : DZF.contextPath + "/dealmanage/goodspackage!delete.action",
		        data : rows[0],
				success : function(rs) {
					if (rs.success) {
						$("#gridp").datagrid("reload");
						Public.tips({
							content : rs.msg,
							type : 0
						});
					} else {
						Public.tips({
							content : rs.msg,
							type : 1
						});
					}
				}
			});
		}
	});
}

/**
 * 保存（套餐）
 */
function saveP() {
	var flag = endEdit();
	if (!flag) {
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return;
	}
	var submitData = getSubmitData($("#gridp"));
	$.ajax({
		type : "POST",
		dataType : "json",
		url : DZF.contextPath + "/dealmanage/goodspackage!save.action",
		data : {
			submitData : submitData
		},
		success : function(rs) {
			if (rs.success) {
				showButtons("brows");
				$("#gridp").datagrid("reload");
				Public.tips({
					content : rs.msg,
					type : 0
				});
			} else {
				$("#gridp").datagrid("beginEdit", editpIndex);
				Public.tips({
					content : rs.msg,
					type : 1
				});
			}
		}
	});
}

/**
 * 行编辑结束事件
 * 
 * @returns {Boolean}
 */
function endEdit() {
	var len = $("#gridp").datagrid("getRows").length;
	var datagrid;
	for (var i = 0; i < len; i++) {
		datagrid = $("#gridp").datagrid('validateRow', i);
		if (!datagrid) {
			return false;
		} else {
			$("#gridp").datagrid("endEdit", i);
		}
	}
	return true;
}

/**
 * 获取保存数据
 * 
 * @param grid
 * @returns {___anonymous6313_6391}
 */
function getSubmitData(grid) {
	var newRows = grid.datagrid("getChanges", "inserted");
	var datagrid;
	if (newRows != null && newRows.length > 0) {
		for (var i = 0; i < newRows.length; i++) {
			datagrid = $("#grid").datagrid("validateRow", i);
			if (!datagrid) {
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
		newRows : newRows,
		deleteRows : deleteRows,
		updateRows : updateRows
	}
	submitData = JSON.stringify(submitData);
	return submitData;
}

/**
 * 取消（套餐）
 */
function cancelP() {
	var rows = $("#gridp").datagrid("getRows");
	if (rows && rows.length > 0) {
		for (var i = 0; i < rows.length; i++) {
			$("#gridp").datagrid("cancelEdit", i);
		}
	}
	$("#gridp").datagrid("rejectChanges");
	showButtons("brows");
}

/**
 * 设置按钮是否显示
 * 
 * @param type
 */
function showButtons(type) {
	if (type == "brows") {
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
