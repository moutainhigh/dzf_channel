var contextPath = DZF.contextPath;
var grid;
var editIndex;

$(function(){
	load();
	initRef();
	setQryData();
	reloadData();
});

/**
 * 查询框初始化
 */
function setQryData(){
	var bdate = $('#bdate').datebox('getValue'); 
	var edate = $('#edate').datebox('getValue'); 
	$("#jqj").html(bdate + ' 至 ' + edate);
}

/**
 * 参照格式化
 */
function initRef(){
	//查询-加盟商参照初始化
	$('#qcorp').textbox({
		onClickIcon : function() {
			refid = $(this).attr("id");
		},
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#chnDlg").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择加盟商',
                    modal: true,
                    href: DZF.contextPath + '/ref/channel_select.jsp',
                    queryParams : {
    					issingle : "false",
    					ovince :"-1"
    				},
                    buttons: '#chnBtn'
                });
            }
        }]
    });
	
	//新增-加盟商参照初始化
	$('#corp').textbox({
		onClickIcon : function() {
			refid = $(this).attr("id");
		},
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#chnDlg").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择加盟商',
                    modal: true,
                    href: DZF.contextPath + '/ref/channel_select.jsp',
                    queryParams : {
    					issingle : "true",
    					ovince :"-1"
    				},
                    buttons: '#chnBtn'
                });
            }
        }]
    });
}

/**
 * 加盟商选择事件
 */
function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}

/**
 * 双击选择加盟商
 * @param rowTable
 */
function dClickCompany(rowTable){
	var str = "";
	var corpIds = [];
	var vprovince="";
	if(rowTable){
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个客户",
				type : 2
			});
			return;
		}
		for(var i=0;i<rowTable.length;i++){
			if(i == rowTable.length - 1){
				str += rowTable[i].uname;
			}else{
				str += rowTable[i].uname+",";
			}
			corpIds.push(rowTable[i].pk_gs);
		}
		vprovince=rowTable[0].ovince;
		$("#" + refid).textbox("setValue",str);
		$("#" + refid + "id").val(corpIds);
		if(refid == "corp"){
			getRefundMny(corpIds[0]);
			$("#ovince").val(vprovince);
		}
	}
	$("#chnDlg").dialog('close');
}

/**
 * 列表表格初始化
 */
function load(){
	grid = $('#grid').datagrid({
		striped : true,
		title : '',
		fitColumns:false,
		rownumbers : true,
		height : Public.setGrid().h,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		singleSelect : false,
		idField : 'refid',
		columns : [ [ 
					  { field : 'ck', checkbox : true },
		              { field : 'aname', title : '大区',width :'130',halign: 'center',align:'left'}, 
		              { field : 'provname', title : '地区',width :'140',halign: 'center',align:'left'} ,
		              { field : 'corpcode', title : '加盟商编码',width :'115',halign: 'center',align:'left'} ,
		              { field : 'corp', title : '加盟商名称',width :'160',halign: 'center',align:'left'}, 
		              { field : 'vcode', title : '退款单号',width :'120',halign: 'center',align:'left',},
		              { field : 'yfktk', title : '预付款退款',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'bzjtk', title : '保证金退款',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'refdate', title : '退款日期',width :'100',halign: 'center',align:'left'} ,
		              { field : 'stat', title : '单据状态',width :'100',halign: 'center',align:'center', formatter : formatSta} ,
		              { field : 'memo', title : '备注',width :'160',halign: 'center',align:'left'} ,
		              { field : 'confdate', title : '确认日期',width :'100',halign: 'center',align:'left'} ,
		              { field : 'oper', title : '录入人',width :'120',halign: 'center',align:'left'} ,
		              { field : 'operdate', title : '录入日期',width :'100',halign: 'center',align:'left'} ,
				      { field : 'refid', title : '主键', hidden:true},
				      { field : 'updatets', title : '时间戳', hidden:true},
		] ],
		onLoadSuccess : function(data) {
			$('#grid').datagrid("scrollTo",0);
		}
	});
}

/**
 * 状态格式化
 * @param val
 * @param row
 * @param index
 */
function formatSta(val, row, index){
	//状态   0：待确认；1：已确认；
	if(val == 0)
		return "待确认";
	if(val == 1)
		return "已确认";
}

/**
 * 查询框-清空
 */
function clearParams(){
	$('#qcorp').textbox("setValue",null);
	$('#qcorpid').val(null);
	$('#vcode').textbox("setValue",null);
	$('#qstatus').combobox('setValue', -1);
}

/**
 * 查询框-确认
 */
function reloadData(){
	var url = contextPath + '/refund/refundbill!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'begdate' : $('#bdate').datebox('getValue'),
		'enddate' : $('#edate').datebox('getValue'),
		'cpid' : $("#qcorpid").val(),
		'vcode' : $('#vcode').textbox('getValue'),
		'destatus' : $("#qstatus").combobox("getValue"),
	});
	setQryData();
	$("#qrydialog").hide();  
	$("#grid").datagrid('uncheckAll');
}

/**
 * 查询框-取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 新增
 */
function onAdd(){
	$('#addDlg').dialog({
		modal:true
	});//设置dig属性
	$('#addDlg').dialog('open').dialog('center').dialog('setTitle','退款单新增');
	$('#addForm').form('clear');
}

/**
 * 获取退款金额（预付款退款、返点退款）
 * @param corpid
 */
function getRefundMny(corpid){
	$("#yfktk").numberbox("setValue",null);
	$("#bzjtk").numberbox("setValue", null);
	if(isEmpty(corpid)){
		return;
	}
	$.ajax({
		url : contextPath + '/refund/refundbill!queryRefundMny.action',
		dataType : 'json',
		data : {
			'corpid' : corpid,
		},
		success : function(rs) {
			if (rs.success) {
				var row = rs.rows;
				$("#yfktk").numberbox("setValue",row['yfktk']);
				$("#bzjtk").numberbox("setValue",row['bzjtk']);
			} 
		},
	});
}

/**
 * 保存
 */
function onSave(){
	if($("#addForm").form('validate')){
		var postdata = new Object();
		postdata["data"] = JSON.stringify(serializeObject($('#addForm')));
		postdata["checktype"] = 1;
		$('#addForm').form('submit', {
			url : contextPath + '/refund/refundbill!checkBeforeSave.action',
			queryParams : postdata,
			success : function(result) {
				var result = eval('(' + result + ')');
				if (result.success) {
					var row = result.rows;
					if(!isEmpty(row.errmsg)){
						$.messager.confirm("提示", row.errmsg, function(r) {
							if (r) {
								saveSubmit(postdata, 1);
							}
						});
					}else{
						saveSubmit(postdata, 1);
					}
				} else {
					Public.tips({
						content : result.msg,
						type : 1
					})
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

/**
 * 保存-公用方法
 * @param postdata
 * @param type  1：新增；2：修改；
 */
function saveSubmit(postdata, type) {
	$.messager.progress({
		text : '数据保存中，请稍后.....'
	});
	var ele;
	if(type == 1){
		ele = '#addForm';
	}else if(type == 2){
		ele = '#editForm';
	}
	$(ele).form('submit', {
		url : contextPath + '/refund/refundbill!save.action',
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
				if(type == 1){
					$('#addDlg').dialog('close');
					$('#grid').datagrid('appendRow',row);
				}else if(type == 2){
					$('#editDlg').dialog('close');
					reloadData();
				}
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
 * 修改
 */
function onEdit(){
	var rows = $("#grid").datagrid("getChecked");
	if(rows == null || rows.length != 1){
		Public.tips({content:'请选择一行的数据',type:2});
		return;
	}
	
	var row = rows[0];
	if(row.stat == 1)	{
		Public.tips({content:'该记录状态为已确认，不允许修改',type:2});
		return;
	}
	
	$('#editDlg').dialog({
		modal:true
	});//设置dig属性
	$('#editDlg').dialog('open').dialog('center').dialog('setTitle','返点单修改');
	$('#editForm').form('clear');
	$('#editForm').form('load', row);
}

/**
 * 修改保存
 */
function onEditSave(){
	if($("#editForm").form('validate')){
		var postdata = new Object();
		postdata["data"] = JSON.stringify(serializeObject($('#editForm')));
		postdata["checktype"] = 1;
		$('#editForm').form('submit', {
			url : contextPath + '/refund/refundbill!checkBeforeSave.action',
			queryParams : postdata,
			success : function(result) {
				var result = eval('(' + result + ')');
				if (result.success) {
					var row = result.rows;
					if(!isEmpty(row.errmsg)){
						$.messager.confirm("提示", row.errmsg, function(r) {
							if (r) {
								saveSubmit(postdata, 2);
							}
						});
					}else{
						saveSubmit(postdata, 2);
					}
				} else {
					Public.tips({
						content : result.msg,
						type : 1
					})
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

/**
 * 删除
 */
function onDelete(){
	var rows = $('#grid').datagrid('getChecked');
	if (rows == null || rows.length == 0) {
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});
		return;
	}
	var postdata = new Object();
	var data = "";
	for(var i = 0; i < rows.length; i++){
		data = data + JSON.stringify(rows[i]);
	}
	postdata["data"] = data;
	
	$.messager.confirm("提示", "你确定要删除吗?", function(r) {
		if (r) {
			$.ajax({
				url : DZF.contextPath + "/refund/refundbill!delete.action",
				dataType : 'json',
				data : postdata,
				success : function(rs) {
					if (rs.success) {
						reloadData();
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
 * 确认、取消确认-操作数据
 * @param type  1：确认；2：取消确认；
 */

function onOperat(type){
	var rows = $('#grid').datagrid('getChecked');
	if (rows == null || rows.length != 1) {
		Public.tips({
			content : '请选择一行的数据',
			type : 2
		});
		return;
	}
	
	var msg;
	if(type == 1){
		if(rows[0].stat == 1){
			Public.tips({
				content : '该数据状态为已确认',
				type : 2
			});
			return;
		}
		msg = "你确定要确认吗";
	}else if(type == 2){
		if(rows[0].stat == 0){
			Public.tips({
				content : '该数据状态为待确认',
				type : 2
			});
			return;
		}
		msg = "你确定要取消确认吗";
	}
	
	$.messager.confirm("提示", msg, function(r) {
		if (r) {
			if(type == 1){
				var postdata = new Object();
				postdata["data"] = JSON.stringify(rows[0]);
				postdata["checktype"] = 2;
				$.ajax({
					type : "post",
					dataType : "json",
					url : contextPath + '/refund/refundbill!checkBeforeSave.action',
					data : postdata,
					traditional : true,
					async : false,
					success : function(result) {
						if (result.success) {
							var row = result.rows;
							if(!isEmpty(row.errmsg)){
								$.messager.confirm("提示", row.errmsg, function(r) {
									if (r) {
										updateData(type, rows);
									}
								});
							}else{
								updateData(type, rows);
							}
						} 
					},
				});
			}else if(type == 2){
				updateData(type, rows);
			}
		}
	});
}

/**
 * 确认、取消确认-更新数据
 */
function updateData(type, rows){
	var postdata = new Object();
	var data = "";
	for(var i = 0; i < rows.length; i++){
		data = data + JSON.stringify(rows[i]);
	}
	postdata["data"] = data;
	postdata["opertype"] = type;
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/refund/refundbill!operat.action',
		data : postdata,
		traditional : true,
		async : false,
		success : function(result) {
			if (!result.success) {
				Public.tips({
					content : result.msg,
					type : 1
				});
			} else {
				if(result.status == -1){
					Public.tips({
						content : result.msg,
						type : 2
					});
				}else{
					Public.tips({
						content : result.msg,
					});
				}
				var rerows = result.rows;
				if(rerows != null && rerows.length > 0){
					var map = new HashMap(); 
					for(var i = 0; i < rerows.length; i++){
						map.put(rerows[i].refid, rerows[i]);
					}
					var index;
					var indexes = new Array();
					for(var i = 0; i < rows.length; i++){
						if(map.containsKey(rows[i].refid)){
							index = $('#grid').datagrid('getRowIndex', rows[i]);
							indexes.push(index);
						}
					}
					for(var i in indexes){
						if(type == 1){
							$('#grid').datagrid('updateRow', {
								index : indexes[i],
								row : {
									stat : rerows[i].stat,
									updatets : rerows[i].updatets,
									confid : rerows[i].confid,
									confdate : rerows[i].confdate,
								}
							});
						}else if(type == 2){
							$('#grid').datagrid('updateRow', {
								index : indexes[i],
								row : {
									stat : rerows[i].stat,
									updatets : rerows[i].updatets,
									confid : null,
									confdate : null,
								}
							});
						}
					}
				}
				$("#grid").datagrid('uncheckAll');
			}
		},
	});
	$("#grid").datagrid('uncheckAll');
}

/**
 * 导出
 */
function onExport(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'请选择需导出的数据',type:2});
		return;
	}
	var columns = $('#grid').datagrid("options").columns[0];
	Business.getFile(DZF.contextPath+ '/refund/refundbill!onExport.action',{
		'strlist':JSON.stringify(datarows),'qj' : $('#jqj').html(),}, true, true);
}


