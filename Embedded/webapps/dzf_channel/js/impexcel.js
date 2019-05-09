$(function() { 
	$("#impDlg").dialog({
    	title: '导入',
		width: 430,
		height: 280,
		modal: true,
		closed: true,
		buttons:'#imp-buttons',
    });
});

function onFileSelected() {
	var fileName = $("#impfile").val();
	var index = fileName.lastIndexOf("\\");
	if (index < 0) {
		index = fileName.lastIndexOf("/");
	}
	if (index > -1) {
		fileName = fileName.substring(index + 1);
	}
	$(".selecticon").hide();
	$(".fileicon").show();
	$("#impfileName").text(fileName);
}

/**
 * 导入（未签约）
 */
function onImport(){
	$(".selecticon").show();
	$(".fileicon").hide();
	$("#impfileName").text("未选择任何文件");
	$('#impDlg').dialog('open');
	$('#impDlg').window('center');
	$('#impForm').form('clear');
	$('#imp_msg').html('');
}