var contextPath = DZF.contextPath;
var grid;
var editIndex;

$(function(){
	load();
	initRef();
	setQryData();
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
//	//渠道经理参照初始化
//	$('#manager').textbox({
//        editable: false,
//        icons: [{
//            iconCls: 'icon-search',
//            handler: function(e) {
//                $("#manDlg").dialog({
//                    width: 600,
//                    height: 480,
//                    readonly: true,
//                    title: '选择渠道经理',
//                    modal: true,
//                    href: DZF.contextPath + '/ref/manager_select.jsp',
//                    buttons: '#manBtn'
//                });
//            }
//        }]
//    });
	
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

//function initArea(){
//	$.ajax({
//		type : 'POST',
//		async : false,
//		url : DZF.contextPath + '/chn_set/chnarea!queryArea.action',
//		data : {"qtype" :3},
//		dataTye : 'json',
//		success : function(result) {
//			var result = eval('(' + result + ')');
//			if (result.success) {
//			    $('#aname').combobox('loadData',result.rows);
//			} else {
//				Public.tips({content : result.msg,type : 2});
//			}
//		}
//	});
//}

///**
// * 渠道经理选择事件
// */
//function selectMans(){
//	var rows = $('#mgrid').datagrid('getSelections');
//	dClickMans(rows);
//}

///**
// * 双击选择渠道经理
// * @param rowTable
// */
//function dClickMans(rowTable){
//	var unames = "";
//	var uids = [];
//	if(rowTable){
//		if (rowTable.length > 300) {
//			Public.tips({
//				content : "一次最多只能选择300个经理",
//				type : 2
//			});
//			return;
//		}
//		for(var i=0;i<rowTable.length;i++){
//			if(i == rowTable.length - 1){
//				unames += rowTable[i].uname;
//			}else{
//				unames += rowTable[i].uname+",";
//			}
//			uids.push(rowTable[i].uid);
//		}
//		$("#manager").textbox("setValue",unames);
//		$("#managerid").val(uids);
//	}
//	 $("#manDlg").dialog('close');
//}

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
		checkOnSelect : false,
		idField : 'rebid',
		columns : [ [ 
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
				      { field : 'reid', title : '主键', hidden:true},
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
		$('#addForm').form('submit', {
			url : contextPath + '/refund/refundbill!checkBeforeSave.action',
			queryParams : postdata,
			success : function(result) {
				var result = eval('(' + result + ')');
				if (result.success) {
					Public.tips({
						content : result.msg,
						type : 0
					})
					var row = result.rows;
					if(!isEmpty(row.errmsg)){
						$.messager.confirm("提示", row.errmsg, function(r) {
							if (r) {
								saveSubmit(postdata);
							}
						});
					}else{
						saveSubmit(postdata);
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
 */
function saveSubmit(postdata) {
	$.messager.progress({
		text : '数据保存中，请稍后.....'
	});
	$('#addForm').form('submit', {
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
				$('#addDlg').dialog('close');
				$('#grid').datagrid('appendRow',row);
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
function onEdit(index){
	var row = $('#grid').datagrid('getData').rows[index];
	$.ajax({
		url : DZF.contextPath + "/rebate/rebateinput!queryById.action",
		dataType : 'json',
		data : row,
		success : function(rs) {
			if (rs.success) {
				editIndex = index;
				$('#editDlg').dialog({
					modal:true
				});//设置dig属性
				$('#editDlg').dialog('open').dialog('center').dialog('setTitle','返点单修改');
				var row = rs.rows;
				$('#editForm').form('clear');
				setFormValue(row);
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
 * 修改设置表单的值
 * @param row
 */
function setFormValue(row){
	$('#erebid').val(row.rebid);
	$('#evcode').textbox('setValue',row.vcode);
	$('#eshowdate').textbox('setValue', row.showdate);
	$('#eyear').val(row.year);
	$('#eseason').val(row.season);
	$('#ecorp').textbox('setValue',row.corp);
	$('#ecorpid').val(row.corpid);
	$('#econtnum').numberbox('setValue', row.contnum);
	$('#edebitmny').numberbox('setValue', row.debitmny);
	$('#ebasemny').numberbox('setValue', row.basemny);
	$('#erebatemny').numberbox('setValue', row.rebatemny);
	$('#ememo').textbox('setValue',row.memo);
	$('#estatusname').textbox('setValue',row.statusname);
	$('#eopername').textbox('setValue',row.opername);
	$('#eoperdate').textbox('setValue',row.operdate);
}

/**
 * 修改监听事件
 */
function initEditListener(){
//	$("#eyear").combobox({
//		onChange : function(n, o) {
//			getEditDebateMny($("#ecorpid").val());
//		}
//	});
//	$("#eseason").combobox({
//		onChange : function(n, o) {
//			getEditDebateMny($("#ecorpid").val());
//		}
//	});
	$("#ebasemny").numberbox({
		onChange : function(n, o) {
			var debitmny = getFloatValue($("#edebitmny").numberbox("getValue"));
			if(getFloatValue(n) > debitmny){
				Public.tips({
					content : "返点基数不能大于扣款金额",
					type : 2
				});
				$("#ebasemny").numberbox("setValue",debitmny);
				return; 
			}
		}
	});
	$("#erebatemny").numberbox({
		onChange : function(n, o) {
			var basemny = getFloatValue($("#ebasemny").numberbox("getValue"));
			if(getFloatValue(n) > basemny){
				Public.tips({
					content : "返点金额不能大于返点基数",
					type : 2
				});
				$("#erebatemny").numberbox("setValue",basemny);
				return; 
			}
		}
	});
}

///**
// * 通过加盟商主键、所属年、所属季度计算扣款金额和返点基数
// */
//function getEditDebateMny(cpid){
//	var year = $("#eyear").combobox("getValue");
//	var season = $("#eseason").combobox("getValue");
//	if(isEmpty(year) || isEmpty(year) || isEmpty(cpid)){
//		return;
//	}
//	$.ajax({
//		url : contextPath + '/rebate/rebateinput!queryDebateMny.action',
//		dataType : 'json',
//		data : {
//			'year' : year,
//			'season' : season,
//			'corpid' : cpid,
//		},
//		success : function(rs) {
//			if (rs.success) {
//				var row = rs.rows;
//				$("#edebitmny").numberbox("setValue",row['debitmny']);
//				$("#ebasemny").numberbox("setValue",row['basemny']);
//			} 
//		},
//	});
//}

/**
 * 审批历史按钮点击监听事件
 */
function historyListen(){
	$(".btn-slide").click(function() {
		$("#panela").slideToggle("slow");
		$(this).toggleClass("active");
		return false;
	})
}

/**
 * 修改保存
 */
function onEditSave(){
	var postdata = new Object();
	if($("#editForm").form('validate')){
		postdata["data"] = JSON.stringify(serializeObject($('#editForm')));
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
	$('#editForm').form('submit', {
		url : contextPath + '/rebate/rebateinput!save.action',
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
				$('#editDlg').dialog('close');
				$('#grid').datagrid('updateRow', {
					index : editIndex,
					row : row
				});
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
 * 删除
 */
function onDelete(ths){
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
//	var row = $('#grid').datagrid('getData').rows[index];
	var row = $('#grid').datagrid('getData').rows[tindex];
	if (row.istatus != 0 && row.istatus != 4) {
		Public.tips({
			content : '该记录状态不为待提交或已驳回，不允许删除',
			type : 2
		});
		return;
	}
	$.messager.confirm("提示", "你确定要删除吗?", function(r) {
		if (r) {
			$.ajax({
				url : DZF.contextPath + "/rebate/rebateinput!delete.action",
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
 * 提交
 */
function onCommit(){
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
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/rebate/rebateinput!saveCommit.action',
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
				$('#allotDialog').dialog('close');
				var rerows = result.rows;
				if(rerows != null && rerows.length > 0){
					var map = new HashMap(); 
					for(var i = 0; i < rerows.length; i++){
						map.put(rerows[i].pkcustno,rerows[i]);
					}
					var index;
					var indexes = new Array();
					for(var i = 0; i < rows.length; i++){
						if(map.containsKey(rows[i].pkcustno)){
							index = $('#grid').datagrid('getRowIndex', rows[i]);
							indexes.push(index);
						}
					}
					for(var i in indexes){
						$('#grid').datagrid('updateRow', {
							index : indexes[i],
							row : rerows[i]
						});
					}
				}
				$("#grid").datagrid('uncheckAll');
			}
		},
	});
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
	Business.getFile(DZF.contextPath+ '/rebate/rebateinput!onExport.action',{
		'strlist':JSON.stringify(datarows),/*'qj' : $('#jqj').html(),*/}, true, true);
}


