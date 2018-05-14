var contextPath = DZF.contextPath;
var isadd;
var editIndex;

$(function(){
	load();
});

function load(){
	$('#grid').datagrid({
		url : DZF.contextPath + '/chn_set/rejectreason!query.action',
//		queryParams : {'type' :2},
		striped : true,
		title : '',
		fitColumns:false,
		rownumbers : true,
		height : Public.setGrid().h,
		pagination : true,// 分页工具栏显示
		pageNumber : 1,//在设置分页属性的时候初始化页码
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		singleSelect : false,
		checkOnSelect : false,
		idField : 'reid',
		frozenColumns :[[ 
			              { field : 'operate', title : '操作列',width :'150',halign: 'center',align:'center',formatter:opermatter} ,
		               ]],
		columns : [ [ {
			width : '500',
			title : '驳回原因',
			field : 'reason'
		}, {
			width : '500',
			title : '修改建议',
			field : 'suggest'
		},{
			title : '主键',
			field : 'reid',
			hidden: true
		}
		] ],
		onLoadSuccess : function(data) {
			parent.$.messager.progress('close');
			$('#grid').datagrid("selectRow", 0);  	
		}
		
	});
}

/**
 * 列操作格式化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function opermatter(val, row, index) {
//	return '<a href="javascript:void(0)" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="onEdit(' + index + ')">修改</a>'+
//	'<a href="javascript:void(0)" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="onDelete(this)">删除</a>';
	return '<a href="javascript:void(0)" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="onEdit(' + index + ')">修改</a>'
	+' <a href="javascript:void(0)" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="onDelete(' + index + ')">删除</a>';
}

/**
 * 新增
 */
function add(){
	$('#dataform').form('clear');
	$("#datadlg").dialog({
		title: '新增驳回原因',
		width:500,
		height:340,
		modal: true,
	});
	isadd = true;
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
		text : '数据保存中，请稍后.....'
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
				if(isadd){
					$('#grid').datagrid('appendRow',row);
				}else{
					$('#grid').datagrid('updateRow',{index:editIndex,row});
				}
				isadd = false;
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
	isadd = false;
}

/**
 * 修改
 */
function onEdit(index){
	var row = $('#grid').datagrid('getData').rows[index];
	$.ajax({
		url : DZF.contextPath + "/chn_set/rejectreason!queryById.action",
		dataType : 'json',
		data : row,
		success : function(rs) {
			if (rs.success) {
				editIndex = index;
				$("#datadlg").dialog({
					title: '修改驳回原因',
					width:500,
					height:340,
					modal: true,
				});
				var row = rs.rows;
				$('#dataform').form('clear');
				$('#dataform').form('load', row);
				isadd = false;
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
	var row = $('#grid').datagrid('getData').rows[tindex];
	$.messager.confirm("提示", "你确定要删除吗?", function(r) {
		if (r) {
			$.ajax({
				url : DZF.contextPath + "/chn_set/rejectreason!delete.action",
				dataType : 'json',
				data : row,
				success : function(rs) {
					if (rs.success) {
						$('#grid').datagrid('deleteRow', Number(tindex)); 
						$("#grid").datagrid('unselectAll');
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
function reloadData(){
	url = DZF.contextPath + '/chn_set/rejectreason!query.action',
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {});
}


