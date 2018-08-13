$(function(){
	load();
});

/**
 * 列表表格加载
 */
function load(){
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
		showFooter:true,
		idField : 'gid',
		columns : [ [ {
			width : '100',
			title : '主键',
			field : 'billid',
			hidden : true
		}, {
			width : '100',
			title : '时间戳',
			field : 'updatets',
			hidden : true
		}, {
			width : '100',
			title : '订单编码',
			field : 'billcode',
			align : 'center',
            halign : 'center',
		}, {
			width : '200',
			title : '提交时间',
			field : 'gname',
			align : 'left',
            halign : 'center',
		}, {
			width : '200',
			title : '加盟商编码',
			field : 'pcode',
			align : 'left',
            halign : 'center',
		}, {
			width : '200',
			title : '加盟商名称',
			field : 'pname',
			align : 'left',
            halign : 'center',
		}, {
			width : '100',
			title : '订单金额',
			align:'right',
            halign:'center',
			field : 'ndesummny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '预付款付款',
			align:'right',
            halign:'center',
			field : 'ndemny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '返点付款',
			align:'right',
            halign:'center',
			field : 'nderebmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '订单状态',
			field : 'vstatus',
            halign : 'center',
			align : 'center',
			formatter : function(value) {
				if (value == '0')
					return '待确认';
				if (value == '1')
					return '待发货';
				if (value == '2')
					return '已发货';
				if (value == '3')
					return '已收货';
				if (value == '4')
					return '已取消';
			}
		} ] ],
		onLoadSuccess : function(data) {
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

/**
 * 确认
 */
function confirm(){
	var row = null;
	var rows = $("#grid").datagrid("getChecked");
	if (rows == null || rows.length != 1) {
		Public.tips({
			content : '请选择一行数据',
			type : 2
		});
		return;
	} else {
		row = rows[0];
	}
	if (row.vstatus != 0) {
		
	}
	
	$.messager.confirm("提示", "确认后将扣除加盟商账户余额，是否确认订单？", function(flag) {
		if (flag) {
			var index =  $('#grid').datagrid('getRowIndex', row);
			var postdata = new Object();
			var data = JSON.stringify(row);
			postdata["data"] = data;
			postdata["type"] = type;
			
		} else {
			return null;
		}
	});
}

/**
 * 取消订单
 */
function cancel(){
	
}

/**
 * 商品发货
 */
function sendOut(){
	
}

/**
 * 操作数据
 * @param row
 * @param type
 */
function operdata(postdata, index){
	$.messager.progress({
		text : '数据操作中....'
	});
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/dealmanage/channelorder!operdata.action',
		data : postdata,
		traditional : true,
		async : false,
		success : function(data, textStatus) {
			$.messager.progress('close');
			if (!data.success) {
				Public.tips({
					content : data.msg,
					type : 1
				});
			} else {
				$('#grid').datagrid('clearSelections');
				$('#grid').datagrid('updateRow', {
					index : index,
					row : data.rows
				});
				Public.tips({
					content : data.msg,
				});
			}
		},
	});
}

