var contextPath = DZF.contextPath;

$(function(){
	load();
});

/**
 * 列表表格加载
 */
function load(){
	$('#grid').datagrid({
		url : contextPath+"/dealmanage/goodstype!query.action",
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height : Public.setGrid().h,
		singleSelect : true,
		showFooter:true,
		idField : 'pk_goodstype',
		columns : [ [ 
			 { field : 'vcode', title : '分类编码',width :'120',halign: 'center',align:'left'} ,
			 { field : 'vname', title : '分类名称',width :'160',halign: 'center',align:'left'} ,
			 { field : 'operate', title : '操作',width :'90',halign: 'center',align:'center',formatter:opermatter} ,
		] ],
		onLoadSuccess : function(data) {
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

function opermatter(val, row, index) {
	return '<a href="#" style="margin-bottom:0px;color:blue;" onclick="edit(' + index + ')">编辑</a> '+
	' <a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="dele(this)">删除</a>';
}

function save(){
	var vname = trimStr($('#vname').textbox('getValue'),"g");
	$('#vname').textbox('setValue',vname)
	if ($("#addForm").form('validate')) {
		$.messager.progress({
			text : '数据保存中，请稍候.....'
		});
		$('#addForm').form('submit', {
			url : DZF.contextPath + '/dealmanage/goodstype!save.action',
			success : function(result) {
				var result = eval('(' + result + ')');
				$.messager.progress('close');
				if (result.success) {
					$('#addDialog').dialog('close');
					Public.tips({content : "操作成功"});
					load();
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
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return; 
	}
}

function add(){
	$('#addDialog').dialog('open').dialog('center').dialog('setTitle', '新增分类');
	$('#addForm').form('clear');
}

function edit(index){
	$('#addDialog').dialog('open').dialog('center').dialog('setTitle', '修改分类');
	var row= $('#grid').datagrid("getRows")[index];
	$('#addForm').form('clear');
	$('#addForm').form('load', row);
}

function cancel(){
	$('#addDialog').dialog('close');
}

function dele(ths){
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	var row = $('#grid').datagrid('getData').rows[tindex];
	$.messager.confirm("提示", "你确定删除吗？", function(flag) {
		if (flag) {
			$.ajax({
				type : "post",
				dataType : "json",
				url : contextPath + '/dealmanage/goodstype!delete.action',
				data : row,
				traditional : true,
				async : false,
				success : function(data, textStatus) {
					if (!data.success) {
						Public.tips({
							content : data.msg,
							type : 1
						});
					} else {
						$("#grid").datagrid("reload");
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
