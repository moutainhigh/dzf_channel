/**
 * 新增
 */
function addReje(){
	$('#dataform').form('clear');

	$('#datadlg').dialog({
		modal : true
	});// 设置dig属性
	$('#datadlg').dialog('open').dialog('center').dialog('setTitle', '新增驳回原因');
}

/**
 * 驳回原因列操作格式化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function opermatter(val, row, index) {
	return '<a href="javascript:void(0)" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="onEdit(' + index + ')">修改</a>'
	+' <a href="javascript:void(0)" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="onDelete(this)">删除</a>';
}

/**
 * 新增或修改保存
 */
function save(){
	var postdata = new Object();
	if($("#dataform").form('validate')){
		postdata["data"] = JSON.stringify(serializeObject($('#dataform')));
	} else {
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return; 
	}
	
	$.messager.progress({
		text : '数据保存中，请稍候.....'
	});
	$('#dataform').form('submit', {
		url : contextPath + '/chn_set/rejectreason!save.action',
		queryParams : postdata,
		success : function(result) {
			var result = eval('(' + result + ')');
			$.messager.progress('close');
			if (result.success) {
				Public.tips({
					content : result.msg,
					type : 0
				})
				var row = result.rows;
				$('#dataform').form('clear');
				$('#datadlg').dialog('close');
				reloadRejeData();
			} else {
				Public.tips({
					content : result.msg,
					type : 1
				})
			}
		}
	});
	
}

/**
 * 取消保存
 */
function cancel(){
	$('#datadlg').dialog('close');
}

/**
 * 修改
 */
function onEdit(index){
	var row = $('#rgrid').datagrid('getData').rows[index];
	$.ajax({
		url : DZF.contextPath + "/chn_set/rejectreason!queryById.action",
		dataType : 'json',
		data : row,
		success : function(rs) {
			if (rs.success) {
				editIndex = index;
				$('#datadlg').dialog({
					modal : true
				});// 设置dig属性
				$('#datadlg').dialog('open').dialog('center').dialog('setTitle', '修改驳回原因');
				var row = rs.rows;
				$('#dataform').form('clear');
				$('#dataform').form('load', row);
			} else {
				Public.tips({
					content : rs.msg,
					type : 1
				});
			}
		},
	});
}

/**
 * 删除
 * @param ths
 */

function onDelete(ths){
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	var row = $('#rgrid').datagrid('getData').rows[tindex];
	$.messager.confirm("提示", "你确定要删除吗?", function(r) {
		if (r) {
			$.ajax({
				url : DZF.contextPath + "/chn_set/rejectreason!delete.action",
				dataType : 'json',
				data : row,
				success : function(rs) {
					if (rs.success) {
						$('#rgrid').datagrid('deleteRow', Number(tindex)); 
						$("#rgrid").datagrid('unselectAll');
						reloadRejeData();
						Public.tips({
							content : rs.msg,
							type : 0
						});
					} else {
						Public.tips({
							content : rs.msg,
							type : 1
						});
					}
				},
			});
		}
	});
}

/**
 * 刷新
 */
function reloadRejeData(){
	url = DZF.contextPath + '/chn_set/rejectreason!query.action',
	$('#rgrid').datagrid('options').url = url;
	$('#rgrid').datagrid('load', {});
}
