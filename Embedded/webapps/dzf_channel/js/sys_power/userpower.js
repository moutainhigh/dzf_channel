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
	$('#treeGrid').tree('resize',{ 
		height : Public.setGrid(0,'dataTreeGrid').h,
		width : 'auto'
	});
});
$(function(){
	$('#grid').datagrid({
		url : contextPath + '/sys/chnUseract!query.action',
		rownumbers : true,
		/*height : Public.setGrid(0,'dataGrid').h,*/
		height:'100%',
		singleSelect : true,
		idField : 'uid',
		columns : [ [
		    { width : '120', title : '用户编码', field : 'ucode' },
			{ width : '252', title : '用户名称', field : 'uname'},
			{ width : '100', title : '主键', field : 'uid',hidden:true}
			] ],
		toolbar : '#toolbar',

		onClickRow:function(index,data){
			if(billState == 1){//分配状态下
				$("#grid").datagrid("unselectRow", index);
				$("#grid").datagrid("selectRow", lastSelectRow);
			}
			if(billState != 1){
				lastSelectRow = index;
				getChecked(index,data);
			}
		},
		 onDblClickRow: function (rowIndex, rowData) {
			if(billState == 1){
				$("#grid").datagrid("selectRow", lastSelectRow);
			}
		 },
		 onLoadSuccess : function(data){
			 $('#grid').datagrid('selectRow',0);
			 //默认选中第一行数据
			 getChecked(0,data.rows[0]);
		 }
	});
	
	$('#treeGrid').tree({
		url : contextPath + '/sys/userPower!queryUserRole.action',
		method: 'post',
		height : Public.setGrid(0,'dataTreeGrid').h,
		checkbox:true,
		lines:true,
		onLoadSuccess:function(node, data){
			checkAble(false);
			//展开第一组数据
			if(data!=null&&data.length>0){
				$('#treeGrid').tree('collapseAll');
				for(var i=0; i<data.length; i++){
					$('#treeGrid').tree('expand',data[i].target);
				}
			}			
		}
	});
	//设置按钮
	setBtnEnable(0);
});
function checkAble(checked){
	$("#treeGrid").find('span.tree-checkbox').unbind().click(function(){
		return checked;
	});
}
function checkAble(checked){
	$("#treeGrid").find('span.tree-checkbox').unbind().click(function(){
		return checked;
	});
}
function getChecked(index,data){
	 var queryParams = $('#treeGrid').tree('options').queryParams; 
	 queryParams['uid'] = data.uid;
	 $('#treeGrid').tree('options').queryParams = queryParams;  
	 $("#treeGrid").tree('reload');  
}
function edit(){
	var row = $('#grid').datagrid('getSelected');
	if(row == null){
	   Public.tips({content:'请选择用户 !',type:2});
	   return;
	}
	checkAble(true);
	billState = 1;
	setBtnEnable(billState);
	$("#editbn").hide();
}
function save(){
	var row = $('#grid').datagrid('getSelected');
	if(row == null || row.uid == null){
		Public.tips({content:'请选择用户 !',type:2});
		return;
	}
	var nodes = $('#treeGrid').tree('getChecked');
	var checkIdArr = [];
	for(var i=0; i<nodes.length; i++){
		checkIdArr.push(nodes[i].id);
	}
	var succse = false;
	jQuery.ajax({
		url : contextPath + '/sys/userPower!saveUserRoleVO.action',
		data : {
			'uid' : row.uid,
			'ckaccarr[]':checkIdArr
		},
		type : 'post',
		dataType : 'json',
		async:false,
		success: function(result){
            if (result.success){
            	succse = true;
            	Public.tips({content : '保存成功！'});
				$("#editbn").show();
            }else{
            	succse = false;
            	Public.tips({type : 1,content : '保存失败！'});
            }
		}
	});
	
	if(succse){
		billState = 0;
		setBtnEnable(billState);
		checkAble(false);
	}else{
		checkAble(true);
	}
}
function cancel(){
	checkAble(false);
	$("#editbn").show();
	billState = 0;
	setBtnEnable(0);
	$("#treeGrid").tree('reload');
}
function setBtnEnable(billState){
	switch(billState)
	{
	case 1:
		//修改
		$("#savebn").show();
		$("#cancelbn").show();
		$("#editbn").hide();
	  break;
	default:
		//默认初始状态
	  	$("#savebn").hide();
	  	$("#cancelbn").hide();
		$("#editbn").show();
	}
}
