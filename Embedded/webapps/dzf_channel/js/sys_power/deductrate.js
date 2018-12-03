var grid;

$(function() {
	load();
	reloadData();
	fastQry();
	initImplDlg();
});

/**
 * 初始化导入对话框
 */
function initImplDlg(){
	$("#impUpdDlg").dialog({
    	title: '导入-选择导入文件',
		width: 430,
		height: 280,
		modal: true,
		closed: true,
		buttons:'#impUpd-buttons',
    });
}

/**
 * 列表表格加载
 */
function load() {
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
		showFooter : true,
		checkOnSelect : false,
		idField : 'corpid',
		columns : [ [ {
			field : 'ck',
			checkbox : true
		}, {
			width : '100',
			title : '主键',
			field : 'rateid',
			hidden : true
		}, {
			width : '100',
			title : '加盟商主键',
			field : 'corpid',
			hidden : true
		}, {
			width : '100',
			title : '登录公司主键',
			field : 'fcorpid',
			hidden : true
		}, {
			width : '120',
			title : '加盟商编码',
			field : 'cpcode',
			halign : 'center',
			align : 'left',
		}, {
			width : '160',
			title : '加盟商名称',
			field : 'cpname',
			halign : 'center',
			align : 'left',
		}, {
			width : '80',
			title : '新增(%)',
			field : 'nrate',
			halign : 'center',
			align : 'center',
		}, {
			width : '80',
			title : '续费(%)',
			field : 'rnrate',
			halign : 'center',
			align : 'center',
		}, {
			width : '100',
			title : '最后修改人',
			field : 'lmpsn',
			halign : 'center',
			align : 'left',
		}, {
			width : '130',
			title : '修改时间',
			field : 'lsdate',
			halign : 'center',
			align : 'left',
		}, {
			field : 'operate',
			title : '操作',
			width : '120',
			halign : 'center',
			align : 'center',
			formatter : opermatter
		} ] ],
		onLoadSuccess : function(data) {
			$('#grid').datagrid("scrollTo", 0);
		},
	});
}

/**
 * 操作格式化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function opermatter(val, row, index) {
	if(isEmpty(row.rateid)){
		return '<a href="#" style="margin-bottom:0px;color:blue;" onclick="onEdit(' + index + ')">修改</a> '+
		' <a href="#" style="margin-bottom:0px;margin-left:10px;" >变更记录</a>';

	}else{
		return '<a href="#" style="margin-bottom:0px;color:blue;" onclick="onEdit(' + index + ')">修改</a> '+
		' <a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="onShowLog(' + index + ')">变更记录</a>';
	}
}

/**
 * 查询数据
 */
function reloadData(filtername){
	var url = DZF.contextPath + '/sys/deductrate!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'cpcode' : filtername,
	});
	$('#grid').datagrid('clearSelections');
	$('#grid').datagrid('clearChecked');
}

/**
* 快速过滤
*/
function fastQry() {
	$('#filter_value').textbox('textbox').keydown(function(e) {
		if (e.keyCode == 13) {
			var value = $("#filter_value").val();
			if(!isEmpty(value)){
				var filter = trimStr(value,'g');
				if (!isEmpty(filter)) {
					reloadData(filter);
				}
			}else{
				reloadData();
			}
		}
	});
}

/**
 * 修改
 * 
 * @param index
 */
function onEdit(index) {
	var row = $('#grid').datagrid('getData').rows[index];
	if (row == null) {
		Public.tips({
			content : '请您先选择一行！',
			type : 2
		});
		return;
	}
	$('#editDlg').dialog({
		modal : true
	});
	$('#editDlg').dialog('open').dialog('center').dialog('setTitle', "合同扣款率设置");
	$('#editform').form("clear");
	$('#editform').form('load', row);
}

/**
 * 修改-保存
 */
function onSave(){
	var postdata = new Object();
	postdata["data"] = JSON.stringify(serializeObject($('#editform')));
	
	$.messager.progress({
		text : '数据保存中，请稍后.....'
	});
	$('#editform').form('submit', {
		url : DZF.contextPath + '/sys/deductrate!save.action',
		queryParams : postdata,
		success : function(result) {
			var result = eval('(' + result + ')');
			$.messager.progress('close');
			if (result.success) {
				
				Public.tips({
					content : result.msg,
					type : 0
				});
				$('#editDlg').dialog('close');
				reloadData();
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.messager.progress('close');
		}
	});
}

/**
 * 变更记录
 * @param index
 */
function onShowLog(index){
	var row = $('#grid').datagrid('getData').rows[index];
	if (row == null) {
		Public.tips({
			content : '请您先选择一行！',
			type : 2
		});
		return;
	}
	
	$('#logDlg').dialog({
		modal : true
	});// 设置dig属性
	$('#logDlg').dialog('open').dialog('center').dialog('setTitle', '变更记录');
	$('#loggrid').datagrid({
		url : DZF.contextPath + '/sys/deductrate!queryLog.action',
		queryParams : {
			"id" : row.rateid,
		},
//		striped : true,
//		title : '',
//		width : '100%',
//		fitColumns : true,
		
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,		
		singleSelect : false,
		idField : 'corpid',
		
		rownumbers : true,
		singleSelect : true,
		columns : [ [ {
			width :  '60',
			title : '新增',
			field : 'nrate',
			halign : 'center',
			align : 'center',
		}, {
			width : '60',
			title : '续费',
			field : 'rnrate',
			halign : 'center',
			align : 'center',
		}, {
			width : '120',
			title : '操作人',
			field : 'copter',
			halign : 'center',
			align : 'left',
		}, {
			width : '150',
			title : '操作时间',
			field : 'ddate',
			halign : 'center',
			align : 'left',
		},] ],
	});
}

/**
 * 批量设置
 */
function onBatchSet() {
	var rows = $('#grid').datagrid('getChecked');
	if (rows == null || rows.length == 0) {
		Public.tips({
			content : "请选择需要处理的数据",
			type : 2
		});
		return;
	}
	$('#setDlg').dialog({
		modal : true
	});
	$('#setDlg').dialog('open').dialog('center').dialog('setTitle', "合同扣款率设置");
	$('#setform').form("clear");
}

/**
 * 批量设置-保存
 */
function onBatchSave() {
	
	var rows = $('#grid').datagrid('getChecked');
	if (rows == null || rows.length == 0) {
		Public.tips({
			content : "请选择需要处理的数据",
			type : 2
		});
		return;
	}

	var postdata = new Object();
	postdata["data"] = JSON.stringify(serializeObject($('#setform')));
	
	var body = "";
	for(var i = 0; i < rows.length; i++){
		body = body + JSON.stringify(rows[i]); 
	}
	postdata["body"] = body;
	
	$.messager.progress({
		text : '数据保存中，请稍后.....'
	});
	$('#setform').form('submit', {
		url : DZF.contextPath + '/sys/deductrate!saveBatchSet.action',
		queryParams : postdata,
		success : function(result) {
			var result = eval('(' + result + ')');
			$.messager.progress('close');
			if (result.success) {
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
				$('#setDlg').dialog('close');
				reloadData();
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.messager.progress('close');
		}
	});
}

/**
 * 导入
 */
function onImport(){
	$(".selecticon").show();
	$(".fileicon").hide();
	$("#impufileName").text("未选择任何文件");
	$('#impUpdDlg').dialog('open');
	$('#impUpdDlg').window('center');
	$('#impUpdForm').form('clear');
	$('#impUpd_msg').html('');
}

/**
 * 选择上传文件
 */
function onFileUpSelected() {
	var fileName = $("#impufile").val();
	var index = fileName.lastIndexOf("\\");
	if (index < 0) {
		index = fileName.lastIndexOf("/");
	}
	if (index > -1) {
		fileName = fileName.substring(index + 1);
	}
	$(".selecticon").hide();
	$(".fileicon").show();
	$("#impufileName").text(fileName);
}

/**
 * 导入-上传
 */
function onUpload(){
	$('#impUpdForm').form('submit', {
	    url: DZF.contextPath + '/sys/dtrateimport!saveImport.action',
	    onSubmit: function(){
	    	$.messager.progress();
	    },
	    success:function(data){
	    	data = $.parseJSON(data);
	    	$.messager.progress('close');
	    	if (data.success) {
	    		$('#impUpd_msg').html(data.msg);
	    		if(data.status == -1){
					Public.tips({
						content : data.msg,
						type : 2
					});
				}else{
					Public.tips({
						content : data.msg,
					});
				}
	    		if (data.rows) {
					afterUploaded(data.rows);
				}
	    		$('#impUpdDlg').dialog('close')
	    	} else {
	    		Public.tips({
		        	type: 1,
		        	content: data.msg
		        });
	    	}
	    }
	});
}

/**
 * 更新导入后，刷新界面数据
 * @param rows
 */
function afterUploaded(rows) {
	$("#grid").datagrid("loadData", rows);
}

/**
 * 导出
 */
function onExport() {
	var checkrows = $('#grid').datagrid("getChecked");
	if (checkrows == null || checkrows.length == 0) {
		Public.tips({
			content : '请选择需要导出的数据!',
			type : 2
		});
		return;
	}
	// 导出Excel
	Business.getFile(DZF.contextPath + '/sys/deductrate!exportExcel.action', {
		'strlist' : JSON.stringify(checkrows)
	}, true, true);
}
