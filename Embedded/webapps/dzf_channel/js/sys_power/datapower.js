var contextPath=DZF.contextPath;
var lock = false;
var lastSelectRow;
var billState;
//自适应边框
$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid(0,'dataGrid').h,
		width : Public.setGrid(0,'dataGrid').w
	});
});

$(function(){
	$('#grid').datagrid({
		url : contextPath + '/sys/dataPower!queryRoles.action',
		rownumbers : true,
		height : Public.setGrid(0,'dataGrid').h,
		singleSelect : true,
		idField : 'id',
		columns : [ [
			{ width : '300', title : '角色名称', field : 'text'},
			{ width : '100', title : '主键', field : 'id',hidden:true}
			] ],
		toolbar : '#toolbar',
		onClickRow:function(index,data){
			if(billState == 1){//分配状态下
				$("#grid").datagrid("unselectRow", index);
				$("#grid").datagrid("selectRow", lastSelectRow);
			}
			if(billState != 1){
				lastSelectRow = index;
				queryByID(data);
			}
		},
		 onDblClickRow: function (rowIndex, rowData) {
			if(billState == 1){
				$("#grid").datagrid("selectRow", lastSelectRow);
			}
		 },
		 onLoadSuccess : function(data){
			 $('#grid').datagrid('selectRow',0);
			 queryByID(data.rows[0]);
		 }
	});
	//设置按钮
	setBtnEnable(0);
});

function queryByID(data){
	$('#dataPower').form('clear');
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url : contextPath + '/sys/dataPower!queryByID.action',
		data : {"roleid" : data.id},
		success : function(data, textStatus) {
			if (!data.success) {
				Public.tips({
					content :  data.msg,
					type : 1
				});	
			} else {
				$('#dataPower').form('load', data.rows);
			}
		},
	});
}

function edit(){
	var row = $('#grid').datagrid('getSelected');
	if(row == null){
	   Public.tips({content:'请选择角色!',type:2});
	   return;
	}
	billState = 1;
	setBtnEnable(billState);
	$("#editbn").hide();
}

function save(){
	var row = $('#grid').datagrid('getSelected');
	if(row == null || row.id == null){
		Public.tips({content:'请选择角色 !',type:2});
		return;
	}
	$('#roleid').val(row.id);
	$('#rolename').val(row.text);
	$('#dataPower').form('submit', {
		url : contextPath + '/sys/dataPower!save.action',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				Public.tips({
					content : result.msg,
					type : 0
				});
				billState = 0;
				setBtnEnable(billState);
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});
}

function cancel(){
	$("#editbn").show();
	billState = 0;
	setBtnEnable(0);
}

function setBtnEnable(billState){
	switch(billState)
	{
	case 1:
		//修改
		$("#savebn").show();
		$("#cancelbn").show();
		$("#editbn").hide();
		$("input:radio").attr("disabled",false);
	  break;
	default:
		//默认初始状态
	  	$("#savebn").hide();
	  	$("#cancelbn").hide();
		$("#editbn").show();
		$("input:radio").attr("disabled","disabled");
	}
}
