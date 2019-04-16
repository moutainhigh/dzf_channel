var contextPath = DZF.contextPath;
$(function(){
	initQry();
	//initCombobox();
	load();
	reloadData();
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
		showFooter: true,
		idField : 'matinid',
		columns : [ [ {
			field : 'ck',
			checkbox : true
		}, {
			width : '100',
			title : '主键',
			field : 'matinid',
			hidden : true
		}, {
			width : '100',
			title : '时间戳',
			field : 'updatets',
			hidden : true
		}, {
			width : '120',
			title : '单据编码',
			field : 'code',
			align : 'center',
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
		}, {
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
		}, {
			width : '100',
			title : '数量',
			field : 'num',
			halign : 'center',
			align : 'center',
		}, {
			width : '110',
			title : '金额',
			field : 'tmny',
			halign : 'right',
			align : 'center',
			formatter : function(value, row, index) {
				if (value == 0)
					return "0.00";
				return formatMny(value);
			},
		}, {
			width : '200',
			title : '备注',
			field : 'memo',
			halign : 'center',
			align : 'center',
		},{
			width : '150',
			title : '入库日期',
			field : 'indate',
			halign : 'center',
			align : 'center',
		},{
			width : '150',
			title : '录入人',
			field : 'opername',
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
			calFooter();
		},
	});
}

/**
 * 计算总金额
 */
function calMoney(){
	 $("#cost").numberbox({
	      precision:2,
	 });
	 $("#tmny").numberbox({
	      precision:2,
	 });
	 $('#num').numberbox().next('span').find('input').focus(function(){
		    $("#num").next("span").children().first().blur(function(){
				var num=$('#num').numberbox('getValue');
				var cost=$('#cost').numberbox('getValue');
				$('#tmny').numberbox('setValue',num*cost);
			});
	 });
	 
	 $('#cost').numberbox().next('span').find('input').focus(function(){
		    $("#cost").next("span").children().first().blur(function(){
				var num=$('#num').numberbox('getValue');
				var cost=$('#cost').numberbox('getValue');
				$('#tmny').numberbox('setValue',num*cost);
			});
	 });
	

}

/**
 * 计算合计
 */
function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
	var num = 0;	
	var tmny = 0;
	
	for (var i = 0; i < rows.length; i++) {
		num += getFloatValue(rows[i].num);
		tmny += getFloatValue(rows[i].tmny);
	  
	}

	 footerData['wlname'] = '合计';
	 footerData['num'] = num;
	 footerData['tmny'] = tmny;
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}


/**
 * 查询数据
 */
function reloadData(){
	var url = DZF.contextPath + '/matmanage/matstockin!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'matfileid' : $('#matfileid2').combobox('getValue'),
		'begdate' : $('#begdate').datebox('getValue'),
	    'enddate' : $('#enddate').datebox('getValue'), 
	});
	$('#grid').datagrid('clearSelections');
	$('#grid').datagrid('clearChecked');
	$('#qrydialog').hide();
}

/**
 * 清除查询条件
 */
function clearParams(){
	$("#matfileid2").combobox('setValue',null);
}

/**
 * 取消
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
	if(row.matinid==null){
		return null;
	}
	return '<a href="#" style="margin-bottom:0px;color:blue;" onclick="edit(' + index + ')">编辑</a> '+
	'|'+' <a href="#" style="margin-bottom:0px;color:blue;" onclick="del(this)">删除</a>';
}


function initCombobox(){
	$("#matfileid").combobox({
		onShowPanel: function () {
			initLogistics();
        }
    })
}

/**
 * 查询物料下拉
 */

function initLogistics(){
	$.ajax({
		type : 'POST',
		async : false,
	    url : DZF.contextPath + '/matmanage/matstockin!queryComboBox.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result+ ')');
			if (result.success) {
				$("#matfileid,#matfileid2").combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
};


/**
 * 新增
 */
function add() {
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '入库单新增');
	$('#mat_add').form('clear');
	$('#matfileid').combobox("readonly",false);
	//$('#indate').combobox("readonly",true);
	$("#num").numberbox({
	      min :1,
	});
	$("#cost").numberbox({
	      min :0,
	});
	$('#indate').datebox('setValue',parent.SYSTEM.LoginDate);
	
	initCombobox();
	initEvent();
	calMoney();
}

/**
 * 监听事件
 */
function initEvent(){
	$('#cost').numberbox({// 去除空格
		onChange : function(n, o) {
			if(isEmpty(n)){
				return;
			}
			var _trim = trimStr(n,'g');
			$("#cost").numberbox("setValue", _trim);
		}
	});
	$('#unit').numberbox({// 去除空格
		onChange : function(n, o) {
			if(isEmpty(n)){
				return;
			}
			var _trim = trimStr(n,'g');
			$("#unit").numberbox("setValue", _trim);
		}
	});
	$('#num').numberbox({// 去除空格
		onChange : function(n, o) {
			if(isEmpty(n)){
				return;
			}
			var _trim = trimStr(n,'g');
			$("#num").numberbox("setValue", _trim);
		}
	});
}

/**
 * 编辑
 */
function edit(index){
	var erow = $('#grid').datagrid('getData').rows[index];
	var row = queryByID(erow.matinid);
	if(isEmpty(row)){
		return;
	}
	
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '编辑入库单');
	$('#matfileid').combobox("readonly",true);
	$('#mat_add').form('clear');
	$('#mat_add').form('load', row);
	
}

/**
 * 通过主键查询入库单信息
 */
function queryByID(matinid){
	var row;
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url : contextPath + '/matmanage/matstockin!queryById.action',
		data : {
			"id" : matinid,
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
 * @param ths
 */
function del(ths){
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	var row = $('#grid').datagrid('getData').rows[tindex];
	$.messager.confirm("提示", "你确定删除吗？", function(flag) {
		if (flag) {
			$.ajax({
				type : "post",
				dataType : "json",
				url : contextPath + '/matmanage/matstockin!delete.action',
				data : row,
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

/**
 * 保存
 */
function onSave(){
	
	if ($("#mat_add").form('validate')) {
		$('#mat_add').form('submit', {
			url : DZF.contextPath + '/matmanage/matstockin!save.action',
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
	Business.getFile(DZF.contextPath+ '/matmanage/matstockin!exportAuditExcel.action',
			{'strlist':JSON.stringify(datarows), 'qj':qj,}, true, true);
}




