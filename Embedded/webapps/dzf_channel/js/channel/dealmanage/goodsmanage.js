
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
		frozenColumns :[[ { field : 'ck', checkbox : true },
			              { field : 'operate', title : '操作列',width :'150',halign: 'center',align:'center',formatter:opermatter} ,
		               ]],
		columns : [ [ {
			width : '100',
			title : '主键',
			field : 'gid',
			hidden : true
		}, {
			width : '100',
			title : '商品编码',
			field : 'gcode',
			align : 'center',
            halign : 'center',
		}, {
			width : '200',
			title : '商品名称',
			field : 'gname',
			align : 'center',
            halign : 'center',
		}, {
			width : '100',
			title : '单价',
			align:'right',
            halign:'center',
			field : 'price',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		},{
			width : '160',
			title : '商品说明',
			field : 'note',
            halign : 'center',
			align : 'left',
			formatter : noteFormat
		}, {
			width : '100',
			title : '合同状态',
			field : 'status',
            halign : 'center',
			align : 'center',
			formatter : function(value) {
				if (value == '1')
					return '已保存';
				if (value == '2')
					return '已发布';
				if (value == '3')
					return '已下架';
			}
		}, {
			width : '100',
			title : '发布日期',
			field : 'pubdate',
            halign : 'center',
			align : 'center',
		}, {
			width : '100',
			title : '下架日期',
			field : 'dofdate',
            halign : 'center',
			align : 'center',
		}, {
			width : '100',
			title : '录入人',
			field : 'opername',
            halign : 'center',
			align : 'center',
		}, {
			width : '100',
			title : '录入日期',
			field : 'operdate',
            halign : 'center',
			align : 'center',
		} ] ],
		onLoadSuccess : function(data) {
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

/**
 * 商品说明添加tips显示
 * @param value
 */
function noteFormat(value){
	if(value != undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}
}

/**
 * 列操作格式化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function opermatter(val, row, index) {
	return '<a href="#" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="edit(' + index + ')">审批</a>'+
	'<a href="#" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="dele(' + index + ')">审批</a>';
}

/**
 * 修改
 * @param index
 */
function edit(index){
	
}

/**
 * 删除
 * @param index
 */
function dele(index){
	var row = $('#grid').datagrid('getData').rows[index];
	if (row.istatus != 1) {
		Public.tips({
			content : '该记录不是已保存状态，不允许删除',
			type : 2
		});
		return;
	}
}

/**
 * 新增
 */
function add(){
	
		$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '新增商品');
}

/**
 * 发布
 */
function publish(){
	
}

/**
 * 下架
 */
function off(){
	
}
