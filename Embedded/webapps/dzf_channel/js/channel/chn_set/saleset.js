var contextPath = DZF.contextPath;
var status="brows";
$(function() {
	load();
	changeItype();
});

function load() {
	$.ajax({
		url : DZF.contextPath + '/chn_set/saleset!query.action',
		data : {
			corpid : parent.SYSTEM.login_corp_id
		},
		async : false,
		dataType : 'json',
		success : function(result) {
			setItemReadonly(true);
			updateBtnState();
			$('#sale_set').form('load', result.data);
		}
	});
}

function edit(){
	status="edit";
	setItemReadonly(false);
	updateBtnState();
	editStart();
}

function save() {
	$('#sale_set').form('submit', {
		url : DZF.contextPath + '/chn_set/saleset!save.action',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				status="brows";
				setItemReadonly(true);
				updateBtnState();
				$('#sale_set').form('load', result.data);
				Public.tips({
					content : "操作成功"
				});
			} else {
				Public.tips({
					content : result.msg,
					type : 1
				});
			}
		}
	});
}

function history(){
		$('#dialog').dialog({
			modal : true
		});// 设置dig属性
		$('#dialog').dialog('open').dialog('center').dialog('setTitle', '变更明细');
		$('#grid').datagrid({
			url : DZF.contextPath + '/chn_set/saleset!history.action',
			striped : true,
			title : '',
			width :'100%',
			fitColumns : true,
			rownumbers : true,
			singleSelect : true,
			columns : [ [ {field : 'lsdate',title : '变更操作日期',width : 40,align : 'center'}, 
			              {field : 'lmpsn',title : '变更人',width : 40,align : 'center'}, 
			             ] ],
		});
}


function editStart(){
	
	if ($('#isfirecovery').prop("checked")) {
		$('#finum').textbox('readonly', false);
	} else {
		$('#finum').textbox('readonly', true);
		$('#finum').textbox('setValue', "");
	}
	
	if ($('#isserecovery').prop("checked")) {
		$('#senum').textbox('readonly', false);
	} else {
		$('#senum').textbox('readonly', true);
		$('#senum').textbox('setValue', "");
	}	
	
	if ($('#isthrecovery').prop("checked")) {
		$('#thnum').textbox('readonly', false);
	} else {
		$('#thnum').textbox('readonly', true);
		$('#thnum').textbox('setValue', "");
	}	
	
	if ($('#isreceive').prop("checked")) {
		$('#recnum').textbox('readonly', false);
	} else {
		$('#recnum').textbox('readonly', true);
		$('#recnum').textbox('setValue', "");
	}	
}

function changeItype() {
	$('#isfirecovery').change(function() {
		if ($('#isfirecovery').prop("checked")) {
			$('#finum').textbox('readonly', false);
		} else {
			$('#finum').textbox('readonly', true);
			$('#finum').textbox('setValue', "");
		}	
	});
	
	$('#isserecovery').change(function() {
		if ($('#isserecovery').prop("checked")) {
			$('#senum').textbox('readonly', false);
		} else {
			$('#senum').textbox('readonly', true);
			$('#senum').textbox('setValue', "");
		}	
	});
	
	$('#isthrecovery').change(function() {
		if ($('#isthrecovery').prop("checked")) {
			$('#thnum').textbox('readonly', false);
		} else {
			$('#thnum').textbox('readonly', true);
			$('#thnum').textbox('setValue', "");
		}	
	});
	
	$('#isreceive').change(function() {
		if ($('#isreceive').prop("checked")) {
			$('#recnum').textbox('readonly', false);
		} else {
			$('#recnum').textbox('readonly', true);
			$('#recnum').textbox('setValue', "");
		}	
	});
}

function setItemReadonly(isedit) {
	$('#isfirecovery').prop("disabled", isedit);
	$('#isserecovery').prop("disabled", isedit);
	$('#isthrecovery').prop("disabled", isedit);
	$('#isreceive').prop("disabled", isedit);
	
	$('#finum').textbox('readonly', isedit);
	$('#senum').textbox('readonly', isedit);
	$('#thnum').textbox('readonly', isedit);
	$('#recnum').textbox('readonly', isedit);
	$('#relnum').textbox('readonly', isedit);
	$('#pronum').textbox('readonly', isedit);
	$('#ficrla').textbox('readonly', isedit);
	$('#finum').textbox('readonly', isedit);
	$('#seccla').textbox('readonly', isedit);
	$('#thicla').textbox('readonly', isedit);
	$('#foucla').textbox('readonly', isedit);
	$('#fifcla').textbox('readonly', isedit);
}

/**
 * 按钮显示
 */
function updateBtnState(){
	if("edit"==status){
		$('#history').hide();
		$('#edit').hide();
		$('#save').show();
	}else if("brows"==status){
		$('#history').show();
		$('#edit').show();  
		$('#save').hide();
	}
}