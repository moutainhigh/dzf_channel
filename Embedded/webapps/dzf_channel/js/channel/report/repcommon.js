function initQryCommbox(roletype) {
	var qtype = 2;
	if(!isEmpty(roletype)){
		qtype = roletype;
	}
	changeArea(qtype);
	changeProvince(qtype);
	initArea({
		"qtype" : qtype
	});
	initProvince({
		"qtype" : qtype
	});
	initManager({
		"qtype" : 2
	});
}

/**
 * 大区改变事件
 */
function changeArea(qtype) {
	$("#aname").combobox({
		onChange : function(n, o) {
			var queryData = {
				"qtype" : qtype
			};
			if (!isEmpty(n)) {
				queryData = {
					'aname' : n,
					"qtype" : qtype
				};
				$('#ovince').combobox('setValue', null);
				$('#uid').combobox('setValue', null);
			}
			initProvince(queryData);
			initManager(queryData);
		}
	});
}

/**
 * 查询框关闭事件
 */
function closeCx() {
	$("#qrydialog").css("visibility", "hidden");
}

/**
 * 清空查询条件
 */
function clearCondition() {
	$('#aname').combobox('select', null);
	$('#ovince').combobox('select', null);
	$('#uid').combobox('select', null);
	$("#pk_account").val(null);
	$("#channel_select").textbox("setValue", null);
}

/**
 * 省(市)改变事件
 */
function changeProvince(qtype) {
	$("#ovince").combobox({
		onChange : function(n, o) {
			var queryData = {
				"qtype" : qtype
			};
			if (!isEmpty(n)) {
				queryData = {
					'aname' : $("#aname").combobox('getValue'),
					'ovince' : n,
					"qtype" : qtype
				};
				$('#uid').combobox('setValue', null);
			}
			initManager(queryData);
		}
	});
}

/**
 * 大区下拉初始化
 * @param queryData
 */
function initArea(queryData) {
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryArea.action',
		data : queryData,
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				$('#aname').combobox('loadData', result.rows);
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
 * 省(市)下拉初始化
 * @param queryData
 */
function initProvince(queryData) {
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryProvince.action',
		data : queryData,
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				$('#ovince').combobox('loadData', result.rows);
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
 * 会计运营经理初始化
 * @param queryData
 */
function initManager(queryData) {
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryTrainer.action',
		data : queryData,
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
}

/**
 * 加盟商初始化
 */
function initChannel(qtype) {
	var ovince = -3;
	if(!isEmpty(qtype)){
		ovince = qtype;
	}
	$('#channel_select').textbox({
		editable : false,
		icons : [ {
			iconCls : 'icon-search',
			handler : function(e) {
				$("#kj_dialog").dialog({
					width : 600,
					height : 480,
					readonly : true,
					title : '选择加盟商',
					modal : true,
					href : DZF.contextPath + '/ref/channel_select.jsp',
					queryParams : {
						ovince : ovince,
						qrytype : -1,
					},
					buttons : '#kj_buttons'
				});
			}
		} ]
	});
}

/**
 * 双击选择加盟商
 */
function selectCorps() {
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}

/**
 * 双击选择加盟商
 * @param rowTable
 */
function dClickCompany(rowTable) {
	var str = "";
	var corpIds = [];
	if (rowTable) {
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个客户!",
				type : 2
			});
			return;
		}
		for (var i = 0; i < rowTable.length; i++) {
			if (i == rowTable.length - 1) {
				str += rowTable[i].uname;
			} else {
				str += rowTable[i].uname + ",";
			}
			corpIds.push(rowTable[i].pk_gs);
		}
		$("#channel_select").textbox("setValue", str);
		$("#pk_account").val(corpIds);
	}
	$("#kj_dialog").dialog('close');
}
