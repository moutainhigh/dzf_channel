var contextPath = DZF.contextPath;
var grid;

$(function(){
	
});

/**
 * 新增
 */
function onAdd(){
	$('#addCorpDlg').dialog({
		modal:true
	});//设置dig属性
	$('#addCorpDlg').dialog('open').dialog('center').dialog('setTitle','返点单编辑');
	
}

/**
 * 修改
 */
function onEdit(){
	$('#addDlg').dialog({
		modal:true
	});//设置dig属性
	$('#addDlg').dialog('open').dialog('center').dialog('setTitle','返点单查看');
	
}

/**
 * 提交
 */
function onCommit(){
	
}

/**
 * 删除
 */
function onDelete(){
	
}

/**
 * 导入
 */
function onImport(){
	
}

/**
 * 导出
 */
function onExport(){
	
}


