var contextPath = DZF.contextPath;
// var hjcols = null;//汇总列字段
var editIndex = undefined;
var status = "brows";

$(window).resize(function() {
	$('#grid').datagrid('resize', {
		height : Public.setGrid().h,
		width : "auto",
	});
});

$(function() {
	initQry();
	initCombobox();
	// initLogistics();
	initRadioListen();
	load(0);
});

function initCombobox() {
	$("#uid").combobox({
		onShowPanel : function() {
			initUname();
		}
	})
}

/**
 * 查询渠道经理下拉
 */

function initUname() {
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryTrainer.action',
		data : {
			qtype : 1,
		},
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				$('#uid').combobox('loadData', result.rows);
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});
};

/**
 * 初始化快递公司
 */
function initLogistics() {
	$("#logid,#ilogid").combobox({
		onShowPanel : function() {
			initLogName();
		}
	})
}

/**
 * 查询快递公司下拉
 */

function initLogName() {
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/matmanage/mathandle!queryComboBox.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				$('#logid').combobox('loadData', result.rows);
				$('#ilogid').combobox('loadData', result.rows);
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});
};

/**
 * 查询初始化
 */
function initQry() {
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	/*
	 * $("#begdate").datebox("setValue", parent.SYSTEM.PreDate);
	 * $("#enddate").datebox("setValue",parent.SYSTEM.LoginDate);
	 * $("#jqj").html(parent.SYSTEM.PreDate+" 至 "+parent.SYSTEM.LoginDate);
	 * $("#bperiod").datebox("setValue", parent.SYSTEM.PreDate);
	 * $("#eperiod").datebox("setValue",parent.SYSTEM.LoginDate);
	 */

	$("#begdate").datebox("setValue", null);
	$("#enddate").datebox("setValue", null);
	// $("#jqj").html(parent.SYSTEM.PreDate+" 至 "+parent.SYSTEM.LoginDate);
	$("#bperiod").datebox("setValue", null);
	$("#eperiod").datebox("setValue", null);

}

/**
 * 表格初始化
 * 
 * @param type
 *            0：初始化界面加载；1；查询；2：待审核；3：待发货
 */
function load(type) {
	var bdate = new Date();

	var onlycol = new ArrayList();// 物料展示集合
	var onlymap = new HashMap();// 物料对应列数

	var bperiod;
	var eperiod;
	var begdate;
	var enddate;
	var status = $('#status').combobox('getValue');
	if (type == 2) {
		status = 1;
	}
	if (type == 3) {
		status = 2;
	}

	var corpname = $("#qcorpname").val();
	var uid = $("#uid").combobox('getValue');
	if ($('#qj').is(':checked')) {
		bperiod = $("#bperiod").datebox('getValue');
		eperiod = $("#eperiod").datebox('getValue');
		/*
		 * if(isEmpty(bperiod)){ Public.tips({ content : "申请开始日期不能为空", type : 2
		 * }); return; } if(isEmpty(eperiod)){ Public.tips({ content :
		 * "申请结束日期不能为空", type : 2 }); return; }
		 */
		if (!isEmpty(bperiod) && !isEmpty(eperiod)) {
			$('#jqj').html(bperiod + ' 至 ' + eperiod);
		}
	} else {
		begdate = $("#begdate").datebox('getValue');
		enddate = $("#enddate").datebox('getValue');
		/*
		 * if(isEmpty(begdate)){ Public.tips({ content : "录入开始日期不能为空", type : 2
		 * }); return; } if(isEmpty(enddate)){ Public.tips({ content :
		 * "录入结束日期不能为空", type : 2 }); return; }
		 */
		if (!isEmpty(begdate) && !isEmpty(enddate)) {
			$('#jqj').html(begdate + ' 至 ' + enddate);
		}

	}

	$.messager.progress({
		text : '数据加载中....'
	});

	// 获取标题列
	var columns = getcolumn(onlymap, onlycol, bperiod, eperiod, begdate,
			enddate, status, corpname);

	var datarray = new Array();
	var errmsg = "";
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/matmanage/matapply!query.action',
		traditional : true,
		data : {
			bperiod : bperiod,
			eperiod : eperiod,
			begdate : begdate,
			enddate : enddate,
			corpname : corpname,
			status : status,
			uid : uid,
			stype : 1,// 物料处理节点只显示待发货和已发货的数据
		},
		async : false,
		success : function(data) {
			if (data.success) {
				var rows = data.rows;
				if (rows != null && rows.length > 0) {
					var matbillid;
					var obj;
					var colfield = "";
					for (var i = 0; i < rows.length; i++) {
						if (i == 0) {
							matbillid = rows[i].matbillid;
							obj = {};
							colnm = onlymap.get(rows[i].wlname + "/"
									+ rows[i].unit);
							colfield = 'applynum' + colnm;
							obj[colfield] = rows[i].applynum;
							colfield = 'outnum' + colnm;
							obj[colfield] = rows[i].outnum;

							obj['receiver'] = rows[i].receiver;// 收货人
							obj['phone'] = rows[i].phone;// 联系电话
							obj['address'] = rows[i].address;// 地址
							obj['logname'] = rows[i].logname;// 快递公司
							obj['fcode'] = rows[i].fcode;// 单号
							obj['fcost'] = rows[i].fcost;// 金额
							obj['dedate'] = rows[i].dedate;// 发货时间
							obj['status'] = rows[i].status;// 状态
							obj['memo'] = rows[i].memo;// 备注
							obj['reason'] = rows[i].reason;// 驳回原因
							obj['operdate'] = rows[i].operdate;// 录入时间
							obj['applyname'] = rows[i].applyname;// 申请人
							obj['adate'] = rows[i].adate;// 申请时间

							obj['matbillid'] = rows[i].matbillid;// 主键
							obj['aname'] = rows[i].aname;// 大区
							obj['uname'] = rows[i].uname;// 渠道经理
							obj['proname'] = rows[i].proname;// 省（市）
							obj['corpname'] = rows[i].corpname;
							// '<a href="#"
							// style="margin-bottom:0px;color:blue;"
							// onclick="showDetail()">'
							// +rows[i].corpname
							// +'</a>';//加盟商
							obj['code'] = rows[i].code;// 合同编码
							if (i == rows.length - 1) {// 一行数据
								colnm = onlymap.get(rows[i].wlname + "/"
										+ rows[i].unit);
								colfield = 'applynum' + colnm;
								obj[colfield] = rows[i].applynum;
								colfield = 'outnum' + colnm;
								obj[colfield] = rows[i].outnum;

								datarray.push(obj);
							}
						} else {
							if (matbillid == rows[i].matbillid) {
								colnm = onlymap.get(rows[i].wlname + "/"
										+ rows[i].unit);
								colfield = 'applynum' + colnm;
								obj[colfield] = rows[i].applynum;
								colfield = 'outnum' + colnm;
								obj[colfield] = rows[i].outnum;

								if (i == rows.length - 1) {// 最后一行数据
									datarray.push(obj);
								}
							} else if (matbillid != rows[i].matbillid) {
								datarray.push(obj);
								matbillid = rows[i].matbillid;
								obj = {};
								colnm = onlymap.get(rows[i].wlname + "/"
										+ rows[i].unit);
								colfield = 'applynum' + colnm;
								obj[colfield] = rows[i].applynum;
								colfield = 'outnum' + colnm;
								obj[colfield] = rows[i].outnum;

								obj['receiver'] = rows[i].receiver;// 收货人
								obj['phone'] = rows[i].phone;// 联系电话
								obj['address'] = rows[i].address;// 地址
								obj['logname'] = rows[i].logname;// 快递公司
								obj['fcode'] = rows[i].fcode;// 单号
								obj['fcost'] = rows[i].fcost;// 金额
								obj['dedate'] = rows[i].dedate;// 发货时间
								obj['status'] = rows[i].status;// 状态
								obj['memo'] = rows[i].memo;// 备注
								obj['reason'] = rows[i].reason;// 驳回原因
								obj['operdate'] = rows[i].operdate;// 录入时间
								obj['applyname'] = rows[i].applyname;// 申请人
								obj['adate'] = rows[i].adate;// 申请时间

								obj['matbillid'] = rows[i].matbillid;// 主键
								obj['aname'] = rows[i].aname;// 大区
								obj['uname'] = rows[i].uname;// 渠道经理
								obj['proname'] = rows[i].proname;// 省（市）
								obj['corpname'] = rows[i].corpname;
								// '<a href="#"
								// style="margin-bottom:0px;color:blue;"
								// onclick="showDetail()">'
								// +rows[i].corpname
								// +'</a>';//加盟商
								obj['code'] = rows[i].code;// 合同编码
								if (i == rows.length - 1) {// 最后一行数据
									datarray.push(obj);
								}
							}
						}
					}
				}
			} else {
				errmsg = data.msg;
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.messager.progress('close');
		}
	});

	if (!isEmpty(errmsg)) {
		$.messager.progress('close');
		Public.tips({
			content : errmsg,
			type : 2
		});
		return;
	}

	$('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : true,
		checkOnSelect : false,
		showFooter : true,
		remoteSort : false,
		idField : 'matbillid',
		columns : columns,
		onLoadSuccess : function(data) {

			var edate = new Date();
			var time = edate.getTime() - bdate.getTime();

			setTimeout(function() {
				$.messager.progress('close');
			}, time / 2);
		},
	});

	if (datarray != null && datarray.length > 0) {
		$('#grid').datagrid('loadData', datarray);
	} else {
		$('#grid').datagrid('loadData', {
			total : 0,
			rows : []
		});
	}

	$("#qrydialog").hide();

	var msg = "";
	if (type == 1) {
		msg = "查询成功";
	}
	if (!isEmpty(msg)) {
		Public.tips({
			content : msg,
			type : 0
		});
	}

}

/**
 * 获取标题列
 * 
 * @param onlymap
 */
function getcolumn(onlymap, onlycol, bperiod, eperiod, begdate, enddate,
		status, corpname) {
	// hjcols = new Array();
	var columns = new Array();
	var columnsh = new Array();// 列及合并列名称
	var columnsb = new Array();// 子列表名称集合

	$
			.ajax({
				type : "post",
				dataType : "json",
				url : DZF.contextPath
						+ "/matmanage/matapply!queryNumber.action",
				traditional : true,
				data : {
					bperiod : bperiod,
					eperiod : eperiod,
					begdate : begdate,
					enddate : enddate,
					status : status,
					corpname : corpname,
				},
				async : false,
				success : function(data) {
					if (data.success) {
						columnsh[0] = {
							field : 'matbillid',
							title : '主键',
							hidden : true,
							rowspan : 2
						};
						columnsh[1] = {
							field : 'updatets',
							title : '时间戳',
							hidden : true,
							rowspan : 2
						};
						columnsh[2] = {
							field : 'aname',
							title : '大区',
							width : 100,
							halign : 'center',
							align : 'center',
							rowspan : 2
						};
						columnsh[3] = {
							field : 'uname',
							title : '渠道经理',
							width : 100,
							halign : 'center',
							align : 'center',
							rowspan : 2
						};
						columnsh[4] = {
							field : 'proname',
							title : '省/市',
							width : 100,
							halign : 'center',
							align : 'center',
							rowspan : 2
						};
						columnsh[5] = {
							field : 'corpname',
							title : '加盟商',
							width : 130,
							halign : 'center',
							align : 'center',
							rowspan : 2,
							formatter : function(value, row, index) {
								return '<a href="javascript:void(0)"  style="color:blue" onclick="showDetail(\''
										+ index + '\')">' + value + '</a>';
							}
						};
						columnsh[6] = {
							field : 'code',
							title : '合同编号',
							width : 100,
							halign : 'center',
							align : 'center',
							rowspan : 2
						};

						var rows = data.rows;
						var len = 0;
						if (rows != null && rows.length > 0) {
							len = rows.length;
							var matbillid;
							var obj;
							var colnm = 0;
							var colfield = "";
							for (var i = 0; i < rows.length; i++) {
								if (!onlycol.contains(rows[i].wlname + "/"
										+ rows[i].unit)) {

									var wlname = rows[i].wlname;
									var unit = rows[i].unit;
									var column = {};
									column["title"] = wlname + "/" + unit;
									column["field"] = 'col' + (i + 1);
									column["width"] = '160';
									column["colspan"] = 2;

									columnsh.push(column);
									onlymap.put(rows[i].wlname + "/"
											+ rows[i].unit, (i + 1));

									var column1 = {};
									column1["title"] = '申请';
									column1["field"] = 'applynum' + (i + 1);
									column1["width"] = '80';
									column1["halign"] = 'center';
									column1["align"] = 'right';
									columnsb.push(column1);

									// hjcols.push(new
									// Array('applynum'+(i+1)+'', 0));

									var column2 = {};
									column2["title"] = '实发';
									column2["field"] = 'outnum' + (i + 1);
									column2["width"] = '80';
									column2["halign"] = 'center';
									column2["align"] = 'right';
									columnsb.push(column2);

									// hjcols.push(new Array('outnum'+(i+1)+'',
									// 0));

									onlycol.add(rows[i].wlname + "/"
											+ rows[i].unit);
								}

							}
						}

						columnsh[7 + len] = {
							field : 'reinfo',
							title : '收货信息',
							width : 280,
							halign : 'center',
							align : 'center',
							colspan : 3
						};
						columnsh[8 + len] = {
							field : 'expressinfo',
							title : '快递信息',
							width : 360,
							halign : 'center',
							align : 'center',
							colspan : 4
						};

						columnsh[9 + len] = {
							field : 'memo',
							title : '备注',
							width : 100,
							halign : 'center',
							align : 'center',
							rowspan : 2
						};
						columnsh[10 + len] = {
							field : 'status',
							title : '状态',
							width : 100,
							halign : 'center',
							align : 'center',
							rowspan : 2,
							formatter : staForma,
						};
						columnsh[11 + len] = {
							field : 'reason',
							title : '驳回原因',
							width : 100,
							halign : 'center',
							align : 'center',
							rowspan : 2
						};
						columnsh[12 + len] = {
							field : 'operdate',
							title : '录入时间',
							width : 100,
							halign : 'center',
							align : 'center',
							rowspan : 2
						};
						columnsh[13 + len] = {
							field : 'applyname',
							title : '申请人',
							width : 100,
							halign : 'center',
							align : 'center',
							rowspan : 2
						};
						columnsh[14 + len] = {
							field : 'adate',
							title : '申请时间',
							width : 100,
							halign : 'center',
							align : 'center',
							rowspan : 2
						};

						columnsb[0 + len * 2] = {
							width : '80',
							title : '收货人',
							field : 'receiver',
							align : 'center',
						};
						columnsb[1 + len * 2] = {
							width : '100',
							title : '联系电话',
							field : 'phone',
							align : 'center',
						};
						columnsb[2 + len * 2] = {
							width : '100',
							title : '地址',
							field : 'address',
							align : 'center',
						};

						columnsb[3 + len * 2] = {
							width : '100',
							title : '快递公司',
							field : 'logname',
							align : 'center',
						};
						columnsb[4 + len * 2] = {
							width : '80',
							title : '金额',
							field : 'fcost',
							align : 'center',
							formatter : function(value, row) {
								if (!isEmpty(row.matbillid)) {
									return formatMny(value);
								}
							},
						};
						columnsb[5 + len * 2] = {
							width : '80',
							title : '单号',
							field : 'fcode',
							align : 'right',
						};
						columnsb[6 + len * 2] = {
							width : '100',
							title : '发货时间',
							field : 'dedate',
							align : 'right',
						};
						columns.push(columnsh);
						columns.push(columnsb);

					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					parent.$.messager.progress('close');
				}
			});
	return columns;
}

/**
 * 状态格式化
 * 
 * @param row
 * @param index
 * @param value
 * @returns {String}
 */
function staForma(value, row, index) {
	if (value == '1')
		return '待审核';
	if (value == '2')
		return '待发货';
	if (value == '3')
		return '已发货';
	if (value == '4')
		return '已驳回';
}

/**
 * 查询框监听事件
 */
function initRadioListen() {
	$('input:radio[name="seledate"]').change(function() {
		var ischeck = $('#qj').is(':checked');
		if (ischeck) {
			$("#bperiod").datebox('readonly', false);
			$("#eperiod").datebox('readonly', false);
			$("#begdate").datebox('readonly', true);
			$("#enddate").datebox('readonly', true);
		} else {
			$("#bperiod").datebox('readonly', true);
			$("#eperiod").datebox('readonly', true);
			$("#begdate").datebox('readonly', false);
			$("#enddate").datebox('readonly', false);
		}
	});
}

/**
 * 清除查询条件
 */
function clearParams() {
	$("#qcorpname").textbox('setValue', null);
	$("#status").combobox('setValue', 0);
	$("#uid").combobox('setValue', null);
}

/**
 * 取消
 */
function closeCx() {
	$("#qrydialog").hide();
}

/**
 * 审核取消
 */
function onCancel() {
	$('#cbDialog').dialog('close');
}

/**
 * 卡片表格
 */
function initCard() {
	var mat = null;
	$.ajax({
		type : 'POST',
		async : false,
		url : contextPath + '/matmanage/matfile!queryMatFile.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				mat = result.rows;
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});

	$('#cardGrid').datagrid({
		striped : true,
		rownumbers : true,
		fitColumns : true,
		/*
		 * onClickCell: onClickCell, onAfterEdit: onAfterEdit,
		 */
		scrollbarSize : 0,
		idField : 'matbillid',
		height : 100,
		singleSelect : true,
		columns : [ [ {
			width : '100',
			title : '物料主键',
			field : 'matfileid',
			halign : 'center',
			align : 'left',
			hidden : true,

		}, {
			width : '100',
			title : '物料名称',
			field : 'wlname',
			halign : 'center',
			align : 'left',

		}, {
			width : '100',
			title : '单位',
			field : 'unit',
			halign : 'center',
			align : 'left',

		}, {
			width : '100',
			title : '申请数量',
			field : 'applynum',
			halign : 'center',
			align : 'right',

		}, {
			width : '100',
			title : '数量',
			field : 'outnum',
			halign : 'center',
			align : 'right',
			editor : {
				type : 'numberbox',
				options : {
					height : 28,
				}
			}
		}, {
			width : '70',
			title : '操作',
			field : 'operate',
			halign : 'center',
			align : 'center',
			formatter : coperatorLink
		}, ] ],
		onClickRow : function(index, row) {
			if (status == "brows") {
				return;
			}
			$('#cardGrid').datagrid('beginEdit', index);
			editIndex = index;
		},
		onClickCell : function(index, field, value) {
			$(this).datagrid('beginEdit', index);
		},
	});

}

function coperatorLink(val, row, index) {
	var add = '<div><a href="javascript:void(0)" id="addBut" onclick="addRow(arguments[0])"><img title="增行" style="margin:0px 20% 0px 20%;" src="../../images/add.png" /></a>';
	var del = '<a href="javascript:void(0)" id="delBut" onclick="delRow(this)"><img title="删行" src="../../images/del.png" /></a></div>';
	return add + del;
}

/**
 * 修改
 */
function edit() {
	status = "brows"
	var row = $('#grid').datagrid('getSelected');
	if (row == null) {
		Public.tips({
			content : "请选择数据行",
			type : 2
		});
		return;
	}
	var id = row.matbillid;
	var type = 1;

	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/matmanage/matapply!queryById.action',
		dataTye : 'json',
		data : {
			id : id,
			type : type,
		},
		success : function(result) {
			var result = eval('(' + result + ')');
			var row = result.rows;

			if (row.status != 2 && row.status != 3) {
				Public.tips({
					content : "只有待发货和已发货支持修改！",
					type : 2
				});
				return;
			}
			if (result.success) {
				showEdit(row);
				queryAllProvince();
			}

		}
	});

}

/**
 * 查询所有的省
 */
function queryAllProvince() {
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/matmanage/matapply!queryAllProvince.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				$("#ipname").combobox('loadData', result.rows);
				$("#pname").combobox('loadData', result.rows);
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});
}

/**
 * 详情和修改界面
 */
$(function() {
	// 触发省选项  
	$("#ipname").combobox(
					{
						onSelect : function() {
							$("#icityname").combobox("setValue", ''); // 清空市  
							$("#icountryname").combobox("setValue", ''); // 清空县  
							var provinceid = $('#ipname').combobox('getValue');
							$("#ivprovince").val(provinceid);
									$.ajax({
										type : 'POST',
										async : false,
										url : DZF.contextPath
												+ '/matmanage/matapply!queryCityByProId.action',
										dataTye : 'json',
										data : "provinceid=" + provinceid,
										success : function(result) {
											var result = eval('(' + result+ ')');
											if (result.success) {
												$("#icityname").combobox('loadData',result.rows);
												
											} else {
												Public.tips({
													content : result.msg,
													type : 2
												});
											}
										}
									});
						}

				});
	// 触发市选项
	$("#icityname").combobox(
			{
				onSelect : function() {
					$("#icountryname").combobox("setValue", ''); // 清空县  
					var cityid = $('#icityname').combobox('getValue');
					$("#ivcity").val(cityid);
					$.ajax({
						type : 'POST',
						async : false,
						url : DZF.contextPath
								+ '/matmanage/matapply!queryAreaByCid.action',
						dataTye : 'json',
						data : "cityid=" + cityid,
						success : function(result) {
							var result = eval('(' + result + ')');
							if (result.success) {
								$("#icountryname").combobox('loadData',result.rows);
							} else {
								Public.tips({
									content : result.msg,
									type : 2
								});
							}
						}
					});
				}

			});
	
	//触发县选项
	$("#icountryname").combobox({
		onSelect : function() {
			var countryid = $('#icountryname').combobox('getValue');
			$("#ivarea").val(countryid);
		}
	});
			

});


/**
 * 发货界面
 */
$(function() {
	// 触发省选项  
	$("#pname").combobox(
					{
						onSelect : function() {
							$("#cityname").combobox("setValue", ''); // 清空市  
							$("#countryname").combobox("setValue", ''); // 清空县  
							var provinceid = $('#pname').combobox('getValue');
							$("#vprovince").val(provinceid);
									$.ajax({
										type : 'POST',
										async : false,
										url : DZF.contextPath
												+ '/matmanage/matapply!queryCityByProId.action',
										dataTye : 'json',
										data : "provinceid=" + provinceid,
										success : function(result) {
											var result = eval('(' + result+ ')');
											if (result.success) {
												$("#cityname").combobox('loadData',result.rows);
												
											} else {
												Public.tips({
													content : result.msg,
													type : 2
												});
											}
										}
									});
						}

				});
	// 触发市选项
	$("#cityname").combobox(
			{
				onSelect : function() {
					$("#countryname").combobox("setValue", ''); // 清空县  
					var cityid = $('#cityname').combobox('getValue');
					$("#vcity").val(cityid);
					$.ajax({
						type : 'POST',
						async : false,
						url : DZF.contextPath
								+ '/matmanage/matapply!queryAreaByCid.action',
						dataTye : 'json',
						data : "cityid=" + cityid,
						success : function(result) {
							var result = eval('(' + result + ')');
							if (result.success) {
								$("#countryname").combobox('loadData',result.rows);
							} else {
								Public.tips({
									content : result.msg,
									type : 2
								});
							}
						}
					});
				}

			});
	
	//触发县选项
	$("#countryname").combobox({
		onSelect : function() {
			var countryid = $('#countryname').combobox('getValue');
			$("#varea").val(countryid);
		}
	});
			

});


/**
 * 发货
 */
function send() {
	status = "brows"
	var row = $('#grid').datagrid('getSelected');
	if (row == null) {
		Public.tips({
			content : "请选择数据行",
			type : 2
		});
		return;
	}
	var quarterdate = getLastQuarter();// 获取上个季度日期
	var qdate = quarterdate.split(",");
	var id = row.matbillid;
	var type = 1;
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/matmanage/matapply!queryById.action',
		dataTye : 'json',
		data : {
			id : id,
			type : type,
			stype : 1,// 表示是物料处理
			debegdate : qdate[0],
			deenddate : qdate[1],
		},
		success : function(result) {
			var result = eval('(' + result + ')');
			var row = result.rows;

			if (row.status == 2) {
				if (result.msg == "提示") {
					$.messager.confirm("注意", row.message, function(flag) {
						if (flag) {
							showCard(row);
							queryAllProvince();
						} else {
							return null;
						}
					});
				} else {
					// 不需要提示，直接弹出发货框
					showCard(row);
					queryAllProvince();
				}

			} else {
				Public.tips({
					content : "单据状态不为待发货！",
					type : 2
				});
				return;
			}

		}
	});
}

function showCard(row) {
	// if(row.status==2){
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '物料发货');
	$('#mat_add').form('clear');
	$('#mat_add').form('load', row);
	$("#fcost").numberbox({
		precision : 2,
		min : 0,
	});
	$('#dedate').datebox('setValue', parent.SYSTEM.LoginDate);
	initCard();
	$('.hid').css("display", "none");
	$('.sid').css("display", "");
	$('#code').textbox({
		width : 431
	});
	readonly(1);
	$("#logid").combobox('readonly', false);
	$("#fcost").numberbox('readonly', false);
	$("#fcode").textbox('readonly', false);
	$("#dedate").datebox('readonly', false);
	$("#dename").textbox('readonly', false);
	
	$("#pname").combobox('readonly', false);
	$("#cityname").combobox('readonly', false);
	$("#countryname").combobox('readonly', false);
	$("#address").textbox('readonly', false);
	$("#receiver").textbox('readonly', false);
	$("#phone").textbox('readonly', false);
	
	if (row.children != null && row.children.length > 0) {
		$('#cardGrid').datagrid('loadData', row.children);
		$('#cardGrid').datagrid('hideColumn', 'operate');
		$('#cardGrid').datagrid('showColumn', 'outnum');
	}
	queryAllProvince();
	initLogistics();

}

/**
 * 发货保存
 */
function onSave() {
	// var type = 1;
	$('#type').val(1);
	var postdata = new Object();
	var body = "";
	// 物料数据
	var rows = $('#cardGrid').datagrid('getRows');
	for (var j = 0; j < rows.length; j++) {
		$('#cardGrid').datagrid('endEdit', j);
		body = body + JSON.stringify(rows[j]);
	}
	postdata["body"] = body;
	
	var pname = $('#pname').combobox('getText')
	$('#pname').combobox('setValue',pname);
	
	var cname = $('#cityname').combobox('getText')
	$('#cityname').combobox('setValue',cname);
	
	var coname = $('#countryname').combobox('getText')
	$('#countryname').combobox('setValue',coname);

	if ($("#mat_add").form('validate')) {
		$('#mat_add').form('submit', {
			url : DZF.contextPath + '/matmanage/matapply!save.action',
			queryParams : postdata,
			success : function(result) {
				var result = eval('(' + result + ')');
				if (result.success) {
					$('#cbDialog').dialog('close');
					load(0);
					Public.tips({
						content : result.msg,
					});
				} else {
					Public.tips({
						content : result.msg,
						type : 2
					});
				}
			}
		});
	} else {
		Public.tips({
			content : "必输信息为空或信息输入不正确",
			type : 2
		});
		return;
	}
}

/**
 * 修改保存
 */
function editSave() {
	var pname = $('#ipname').combobox('getText')
	$('#ipname').combobox('setValue',pname);
	
	var cname = $('#icityname').combobox('getText')
	$('#icityname').combobox('setValue',cname);
	
	var coname = $('#icountryname').combobox('getText')
	$('#icountryname').combobox('setValue',coname);
	
	if ($("#infoform").form('validate')) {
		$('#infoform').form('submit', {
			url : DZF.contextPath + '/matmanage/matapply!save.action',
			success : function(result) {
				var result = eval('(' + result + ')');
				if (result.success) {
					$('#infoDialog').dialog('close');
					load(0);
					Public.tips({
						content : result.msg,
					});
				} else {
					Public.tips({
						content : result.msg,
						type : 2
					});
				}
			}
		});
	} else {
		Public.tips({
			content : "必输信息为空或信息输入不正确",
			type : 2
		});
		return;
	}
}


/**
 * 通过主键查询信息
 */
function queryByID(matbillid) {
	var row;
	var type = 1;
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url : contextPath + '/matmanage/matapply!queryById.action',
		data : {
			"id" : matbillid,
			type : type,
		},
		success : function(data) {
			if (!data.success) {
				Public.tips({
					content : data.msg,
					type : 1
				});
				return;
			} else {
				row = data.rows;
			}
		},
	});
	return row;
}

function showDetail(index) {
	var erow = $('#grid').datagrid('getData').rows[index];
	var row = queryByID(erow.matbillid);
	$('#infoDialog').dialog('open').dialog('center').dialog('setTitle', '物料详情');
	initCardDetail();
	$('#infoform').form('clear');
	$('#infoform').form('load', row);
	$('#ilogid').textbox('setValue', row.logname);
	if (row.children != null && row.children.length > 0) {
		$('#icardGrid').datagrid('loadData', row.children);
	}
	$('.eid').css("display", "none");
	
	$("#icityname").combobox('readonly', true);
	$("#ipname").combobox('readonly', true);
	$("#icountryname").combobox('readonly', true);
	$("#iaddress").textbox('readonly', true);
	$("#ireceiver").textbox('readonly', true);
	$("#iphone").textbox('readonly', true);
	$("#ifcost").textbox('readonly', true);
	$("#iaudname").combobox('readonly', true);
	$("#iaudate").combobox('readonly', true);
	$('#ilogid').combobox('readonly', true);
	$('#ifcode').textbox('readonly', true);
	showStatusDetail(row);
}

function showEdit(row) {
	$('#infoDialog').dialog('open').dialog('center').dialog('setTitle', '物料修改');
	initCardDetail();
	
	$('#infoform').form('clear');
	$('#infoform').form('load', row);
	initLogistics();
	$('#ilogid').textbox('setValue', row.logname);
	if (row.children != null && row.children.length > 0) {
		$('#icardGrid').datagrid('loadData', row.children);
	}
	showStatusDetail(row);
	$('.eid').css("display", "");
	$("#icityname").combobox('readonly', false);
	$("#ipname").combobox('readonly', false);
	$("#icountryname").combobox('readonly', false);
	$("#iaddress").textbox('readonly', false);
	$("#ireceiver").textbox('readonly', false);
	$("#iphone").textbox('readonly', false);
	$("#iaudname").combobox('readonly', true);
	$("#iaudate").combobox('readonly', true);
	if(row.status==3){
		$("#ifcost").textbox('readonly', false);
		$('#ilogid').combobox('readonly', false);
		$('#ifcode').textbox('readonly', false);
	}
	
	$("input",$("#ipname").next("span")).blur(function(){
		if($("#ipname").combobox('getValue')==""){
		$("#ipname").combobox('setValue',row.pname);
		$("#ivprovince").val(row.vprovince);
	    }
	});
	$("input",$("#icityname").next("span")).blur(function(){
		if($("#icityname").combobox('getValue')==""){
		$("#icityname").combobox('setValue',row.cityname);
		$("#ivcity").val(row.vcity);
	    }
	});
	$("input",$("#icountryname").next("span")).blur(function(){
		if($("#icountryname").combobox('getValue')==""){
		$("#icountryname").combobox('setValue',row.countryname);
		$("#ivarea").val(row.varea);
	    }
	});
	
	$("input",$("#ilogid").next("span")).blur(function(){
		if($("#ilogid").combobox('getValue')==""){
		$("#ilogid").combobox('setValue',row.logid);
	    }
	});
}


function showStatusDetail(row) {
	if (row.status == 1) {
		$('#istat').textbox('setValue', '待审核');
	} else if (row.status == 2) {
		$('#istat').textbox('setValue', '待发货');
	} else if (row.status == 3) {
		$('#istat').textbox('setValue', '已发货');
	} else if (row.status == 4) {
		$('#istat').textbox('setValue', '已驳回');
	}
}

/**
 * 卡片表格
 */
function initCardDetail() {
	var mat = null;
	$.ajax({
		type : 'POST',
		async : false,
		url : contextPath + '/matmanage/matfile!queryMatFile.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				mat = result.rows;
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});

	$('#icardGrid').datagrid({
		striped : true,
		rownumbers : true,
		// fitColumns : true,
		/*
		 * onClickCell: onClickCell, onAfterEdit: onAfterEdit,
		 */
		scrollbarSize : 0,
		idField : 'matbillid',
		height : 100,
		singleSelect : true,
		columns : [ [ {
			width : '100',
			title : '物料主键',
			field : 'matfileid',
			halign : 'center',
			align : 'left',
			hidden : true,

		}, {
			width : '100',
			title : '物料名称',
			field : 'wlname',
			halign : 'center',
			align : 'left',

		}, {
			width : '100',
			title : '单位',
			field : 'unit',
			halign : 'center',
			align : 'left',

		}, {
			width : '100',
			title : '申请数量',
			field : 'applynum',
			halign : 'center',
			align : 'right',

		}, {
			width : '82',
			title : '数量',
			field : 'outnum',
			halign : 'center',
			align : 'right',
			editor : {
				type : 'numberbox',
				options : {
					height : 28,
				}
			}
		}, ] ],
	});

}

/**
 * 显示合同状态
 * 
 * @param row
 */
function showStatus(row) {
	if (row.status == 1) {
		$('#stat').textbox('setValue', '待审核');
	} else if (row.status == 2) {
		$('#stat').textbox('setValue', '待发货');
	} else if (row.status == 3) {
		$('#stat').textbox('setValue', '已发货');
	} else if (row.status == 4) {
		$('#stat').textbox('setValue', '已驳回');
	}
}

function readonly(type) {
	$("#code").textbox('readonly', true);
	$("#stat").textbox('readonly', true);
	$("#corpnm").textbox('readonly', true);
	$("#pname").combobox('readonly', true);
	$("#cityname").combobox('readonly', true);
	$("#countryname").combobox('readonly', true);
	$("#address").textbox('readonly', true);
	// $("#address").attr('readonly','readonly');
	$("#receiver").textbox('readonly', true);
	$("#phone").textbox('readonly', true);
	// $("#memo").attr('readonly','readonly');
	$("#memo").textbox('readonly', true);
	$("#applyname").textbox('readonly', true);
	$("#adate").textbox('readonly', true);
	$("#logid").combobox('readonly', true);
	$("#fcost").numberbox('readonly', true);
	$("#fcode").textbox('readonly', true);
	$("#dedate").datebox('readonly', true);
	$("#dename").textbox('readonly', true);

}

/**
 * 导出 type:1:申请表 2：审核表 3:处理表
 */
function doExport() {
	var datarows = $('#grid').datagrid("getRows");
	if (datarows == null || datarows.length == 0) {
		Public.tips({
			content : '当前界面数据为空',
			type : 2
		});
		return;
	}
	var hblcols = $('#grid').datagrid("options").columns[0];// title+field名称

	var cols = $('#grid').datagrid('getColumnFields'); // 字段编码
	Business.getFile(DZF.contextPath
			+ "/matmanage/matapply!exportAuditExcel.action", {
		"strlist" : JSON.stringify(datarows),
		'hblcols' : JSON.stringify(hblcols),
		'cols' : JSON.stringify(cols),
		'type' : 3,
	}, true, true);

}

/**
 * 上传
 */
function onFileConfirm() {
	$('#impForm').form('submit', {
	    url: contextPath + '/matmanage/mathandle!matImport.action',
	    onSubmit: function(){
	    	$.messager.progress();
	    },
	    success:function(data){
	    	data = $.parseJSON(data);
	    	$.messager.progress('close');
	    	if (data.success) {
	    		$('#imp_msg').html(data.msg);
	    		if(data.msg != null && data.msg != ''){
	    			Public.tips({
			        	type: 0,
			        	content: data.msg
			        });
	    		}
	    		if (data.rows) {
	    			load(0);
				}
	    		$('#impDlg').dialog('close')
	    	} else {
	    		Public.tips({
		        	type: 1,
		        	content: data.msg
		        });
	    	}
	    	$("#impDlg").dialog("close");
	    }
	});

}

/**
 * 获取上个季度日期
 */
function getLastQuarter() {

	var y = new Date().getFullYear(); // 当前年份
	var m = new Date().getMonth(); // 当前月份
	// 0（一月） 到 11（十二月）
	var q = parseInt(m / 3); // 当前季度
	var qs = new Date(y, (q - 1) * 3, 1); // 上一季度的开始日期
	var qe = new Date(y, q * 3, 0); // 上一季度的结束日期

	var debegdate = qs;
	var deenddate = qe;

	return debegdate + "," + deenddate;

}
