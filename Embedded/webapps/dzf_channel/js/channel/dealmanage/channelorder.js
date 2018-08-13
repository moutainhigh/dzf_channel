
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

