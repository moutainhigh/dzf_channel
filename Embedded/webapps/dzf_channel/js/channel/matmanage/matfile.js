var contextPath = DZF.contextPath;
$(function(){
	initQry();
	load();
	begloadData();
});

/**
 * 查询初始化
 */
function initQry(){
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	$("#begdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#enddate").datebox("setValue",parent.SYSTEM.LoginDate);
	$("#jqj").html(parent.SYSTEM.PreDate+" 至  "+parent.SYSTEM.LoginDate);
}

/**
 * 列表表格加载
 */
function load(){
	grid = $('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : false,
		checkOnSelect :false,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		showFooter:true,
		idField : 'matfileid',
		columns : [ [ {
			field : 'ck',
			checkbox : true
		}, {
			width : '100',
			title : '主键',
			field : 'matfileid',
			hidden : true
		}, {
			width : '100',
			title : '时间戳',
			field : 'updatets',
			hidden : true
		}, {
			width : '120',
			title : '物料编码',
			field : 'code',
			align : 'left',
			halign : 'center',
		}, {
			width : '100',
			title : '物料名称',
			align : 'center',
			halign : 'center',
			field : 'wlname',
		}, {
			width : '100',
			title : '单位',
			align : 'center',
			halign : 'center',
			field : 'unit',
		}, /*{
			width : '100',
			title : '成本价',
			field : 'cost',
			halign : 'right',
			align : 'center',
			formatter : function(value, row, index) {
				if (value == 0)
					return "0.00";
				return formatMny(value);
			},
		},*/ {
			width : '300',
			title : '申请条件',
			field : 'apply',
			halign : 'center',
			align : 'center',
			formatter : function(value) {
				if (value == '0')
					return null;
				if (value == '1')
					return '上季度合同审核通过数≥上季度申请数量的 70%';
			}
		}, {
			width : '110',
			title : '封存状态',
			field : 'sseal',
			halign : 'center',
			align : 'center',
			formatter : function(value) {
				if (value == '1')
					return '否';
				if (value == '2')
					return '是';
			}
		}, {
			width : '150',
			title : '录入人',
			field : 'applyname',
			halign : 'center',
			align : 'center',
		}, {
			width : '150',
			title : '录入时间',
			field : 'opertime',
			halign : 'center',
			align : 'center',
		}, {
			field : 'operate',
			title : '操作',
			width : '80',
			halign : 'center',
			align : 'center',
			formatter : opermatter
		}, ] ],
		onLoadSuccess : function(data) {
			$('#grid').datagrid("scrollTo", 0);
		},
	});
}

/**
 * 查询数据
 */
function reloadData(){
	var url = DZF.contextPath + '/matmanage/matfile!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'wlname' : $("#wlname").val(),
		'sseal' : $('#sseal').combobox('getValue'),
		/*'begdate' : $('#begdate').datebox('getValue'),
	    'enddate' : $('#enddate').datebox('getValue'), */
	});
	$('#grid').datagrid('clearSelections');
	$('#grid').datagrid('clearChecked');
	$('#qrydialog').hide();
}

/**
 * 默认查询数据
 */
function begloadData(){
	var url = DZF.contextPath + '/matmanage/matfile!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'wlname' : $("#wlname").val(),
		'sseal' : 1,
	});
	$('#grid').datagrid('clearSelections');
	$('#grid').datagrid('clearChecked');
	$('#qrydialog').hide();
}



/**
 * 封存
 */
function closed(){
	operdata(2);
}

/**
 * 启用
 */
function opened(){
	operdata(1);
}


/**
 * 封存/启用
 */
function operdata(type){
	var rows = $('#grid').datagrid('getChecked');
	if (rows == null || rows.length == 0) {
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});
		return;
	}
	var ids="";
	for(var i = 0; i < rows.length; i++){
		ids+=","+rows[i].matfileid;
	}
	ids=ids.substring(1);
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/matmanage/matfile!ToSseal.action',
		data : "ids="+ids+"&type="+type,
		traditional : true,
		async : false,
		success : function(result) {
			if (!result.success) {
				Public.tips({
					content : result.msg,
					type : 2
				});
			} else {
				Public.tips({
					content : result.msg,
				});
			}
			reloadData();
		},
	});
}




/**
 * 清除查询条件
 */
function clearParams(){
	$("#wlname").textbox('setValue',null);
	$("#sseal").combobox('setValue',0);
}

/**
 * 查询框取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 新增修改取消
 */
function onCancel(){
	$('#cbDialog').dialog('close');
}

/**
 * 列操作格式化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function opermatter(val, row, index) {
	return '<a href="#" style="margin-bottom:0px;color:blue;" onclick="edit(' + index + ')">编辑</a> ';
}

/**
 * 新增
 */
function add() {
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '新增物料');
	initEvent();
	$('#cbDialog').form("clear");
    $('.hid').css("display", ""); 
    $('.show').css("display", "none");
    $("#name").textbox({required:false});
    $('#nname').textbox({required:true});
}

/**
 * 监听事件
 */
function initEvent(){
	$('#wlname').textbox({// 去除空格
		onChange : function(n, o) {
			if(isEmpty(n)){
				return;
			}
			var _trim = trimStr(n,'g');
			$("#wlname").textbox("setValue", _trim);
		}
	});
	$('#unit').textbox({// 去除空格
		onChange : function(n, o) {
			if(isEmpty(n)){
				return;
			}
			var _trim = trimStr(n,'g');
			$("#unit").textbox("setValue", _trim);
		}
	});
}

/**
 * 编辑
 */
function edit(index){
	var erow = $('#grid').datagrid('getData').rows[index];
	var row = queryByID(erow.matfileid);
	if(isEmpty(row)){
		return;
	}
	
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '编辑物料');
	$('#mat_add').form('clear');
	$('.hid').css("display", "none"); 
	$('.show').css("display", "");
	
	$('#mat_add').form('load', row);
	$('#nname').textbox({required:false});
	$("#name").textbox({required:true});
	$('#nname').textbox("setValue",null); 
	
	if(row.apply==1){
		$("#apply").prop("checked",true);
	}
	
	$("#matfileid").combobox("setValue",row.matfileid);
}


/**
 * 通过主键查询物料信息
 */
function queryByID(matfileid){
	var row;
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url : contextPath + '/matmanage/matfile!queryById.action',
		data : {
			"id" : matfileid,
		},
		success : function(data) {
			if (!data.success) {
				Public.tips({
					content :  data.msg,
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

/**
 * 删除
 */
function dele(){
	var rows = $('#grid').datagrid('getChecked');
	if (rows == null || rows.length == 0) {
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});
		return;
	}
	var ids="";
	for(var i = 0; i < rows.length; i++){
		ids+=","+rows[i].matfileid;
	}
	ids=ids.substring(1);
	
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url : contextPath + '/matmanage/matfile!queryIsRk.action',
		data : {
			"ids" : ids,
		},
		success : function(data) {
			if (!data.success) {
				Public.tips({
					content :  data.msg,
					type : 1
				});	
				return;
			} else {
				 var wl = '';
				    for (var i = 0; i < rows.length; i++) {
				    	wl = wl + JSON.stringify(rows[i]);
				    }
				    var postdata = new Object();
				    postdata["wl"] = wl;
				$.messager.confirm("提示", "你确定删除吗？", function(flag) {
					if (flag) {
						$.ajax({
							type : "post",
							dataType : "json",
							url : contextPath + '/matmanage/matfile!delete.action',
							data : postdata,
							traditional : true,
							async : false,
							success : function(data) {
								if (!data.success) {
									Public.tips({
										content : data.msg,
										type : 1
									});
								} else {
									reloadData();
									Public.tips({
										content : data.msg,
									});
								}
							},
						});
					} else {
						return null;
					}
				});
			}
		},
	});
	
	
	
	
}

/**
 * 保存
 */
function onSave(){
	var wlname = $('#nname').textbox('getValue');
	
	if (wlname.indexOf(" ") != -1) {
		Public.tips({
			content :"物料名称不可以输入空格",
			type : 2
		});
		return ;
	} 
	
	if ($("#mat_add").form('validate')) {
		$('#mat_add').form('submit', {
			url : DZF.contextPath + '/matmanage/matfile!save.action',
			success : function(result) {
				var result = eval('(' + result + ')');
				if (result.success) {
					$('#cbDialog').dialog('close');
					reloadData();
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

function doExport(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'当前界面数据为空',type:2});
		return;
	}
	var columns = $('#grid').datagrid("options").columns[0];
	//var qj = $('#begdate').datebox('getValue')+"至"+$('#enddate').datebox('getValue');
	var qj = $('#jqj').html();
	Business.getFile(DZF.contextPath+ '/matmanage/matfile!exportAuditExcel.action',
			{'strlist':JSON.stringify(datarows), 'qj':qj,}, true, true);
}


