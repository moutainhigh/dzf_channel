var contextPath = DZF.contextPath;
var status;
var editIndex;
var status;
$(function(){
	initQry();
	load();
	reloadData();
	initUserRef();
	initCardGrid();
});

/**
 * 查询初始化
 */
function initQry(){
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	queryBoxChange('#begdate','#enddate');
	$("#begdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#enddate").datebox("setValue",parent.SYSTEM.LoginDate);
	$("#jqj").html(parent.SYSTEM.PreDate+" 至  "+parent.SYSTEM.LoginDate);
}

/**
 * 查询人员下拉
 */
function initUserRef(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/sys/chnUseract!query.action',
		data : {"invalid":'N'},
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#uid').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

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
		showFooter : true,
		idField : 'stid',
		columns : [ [ {
			field : 'ck',
			checkbox : true
		}, {
			width : '100',
			title : '主键',
			field : 'stid',
			hidden : true
		}, {
			width : '100',
			title : '时间戳',
			field : 'updatets',
			hidden : true
		}, {
			width : '120',
			title : '单据编码',
			field : 'vcode',
			align : 'left',
			halign : 'center',
			formatter : formatCodeLink,
		}, {
			width : '100',
			title : '总金额',
			align : 'right',
			halign : 'center',
			field : 'totalmny',
			formatter : function(value, row, index) {
				if (value == 0)
					return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '入库日期',
			field : 'stdate',
			halign : 'center',
			align : 'center',
		}, {
			width : '100',
			title : '单据状态',
			field : 'status',
			halign : 'center',
			align : 'center',
			formatter : function(value) {
				if (value == '1')
					return '待确认';
				if (value == '2')
					return '已确认';
			}
		}, {
			width : '110',
			title : '录入人',
			field : 'opername',
			halign : 'center',
			align : 'center',
		}, {
			width : '150',
			title : '录入时间',
			field : 'opertime',
			halign : 'center',
			align : 'center',
		}, {
			field : 'operate',
			title : '操作列',
			width : '90',
			halign : 'center',
			align : 'center',
			formatter : opermatter
		}, ] ],
		onLoadSuccess : function(data) {
			$('#grid').datagrid("scrollTo", 0);
		},
	});
}

/**
 * 超级链接-单据编码
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function formatCodeLink(val, row, index){  
    return '<a href="#" style="color:blue" onclick="showDetail('+index+')">'+row.vcode+'</a>';  
}

/**
 * 展示详情
 * @param index
 */
function showDetail(index){
	var row = $('#grid').datagrid('getData').rows[index];
	$.ajax({
        type: "post",
        dataType: "json",
        url: contextPath + '/dealmanage/stockin!queryById.action',
        data: row,
        traditional: true,
        async: false,
        success: function(data, textStatus) {
            if (!data.success) {
            	Public.tips({content:data.msg,type:1});
            } else {
                var row = data.rows;
                showCard();
                $('#stform').form('load',row);
                if(row.children != null && row.children.length > 0){
                	$('#stgrid').datagrid('loadData',row.children);
                }
                status = "brows";
                btnCtrl();
                isItemEdit(true);
            }
        },
    });
	
}

/**
 * 按钮显示控制
 */
function btnCtrl(){
	if("add" == status || "edit" == status ){
		$('#savebtn').show();//保存按钮
	}else if("brows" == status){
		$("#savebtn").hide();//保存按钮
	}
}

/**
 * 字段是否可编辑
 * @param isedit
 */
function isItemEdit(isedit) {
	//入库日志
	$('#stdate').textbox("readonly",isedit);
}

/**
 * 查询数据
 */
function reloadData(){
	var url = DZF.contextPath + '/dealmanage/stockin!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'begdate' : $("#begdate").datebox('getValue'),
		'enddate' : $("#enddate").datebox('getValue'),
		'vcode' : $("#qvcode").val(),
		'uid' :  $('#uid').combobox('getValue'),
	});
	$('#grid').datagrid('clearSelections');
	$('#qrydialog').hide();
}

/**
 * 清除查询条件
 */
function clearParams(){
	$("#vcode").textbox('setValue',null);
	$("#uid").combobox('setValue',null);
}

/**
 * 取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 列操作格式化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function opermatter(val, row, index) {
	return '<a href="#" style="margin-bottom:0px;color:blue;" onclick="edit(' + index + ')">编辑</a> '+
	' <a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="dele(this)">删除</a>';
}

/**
 * 新增
 */
function add(){
	showCard();
	
	$('#stgrid').datagrid('appendRow', {});
	editIndex = $('#stgrid').datagrid('getRows').length - 1;
	$('#stgrid').datagrid('beginEdit',editIndex);
	
	status = "add";
	btnCtrl();
	isItemEdit(false);
}

/**
 * 卡片显示
 */
function showCard(){
	$("#listPanel").hide();
	$("#cardPanel").show();
	clearForm();
}

/**
 * 列表显示
 */
function showList(){
	$("#listPanel").show();
	$("#cardPanel").hide();
	status = "brows";
}

/**
 * 清空表头、表体所有信息
 */
function clearForm(){
	$('#stform').form('clear');
	$("#stgrid").datagrid('loadData',{ total:0, rows:[]});
}

/**
 * 入库明细初始化
 */
function initCardGrid() {
	var goods = null;
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/dealmanage/goodsmanage!queryComboBox.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				goods = result.rows;
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});
	
	$("#stgrid").datagrid({
		height : 300,
		width : "100%",
		singleSelect : false,
		columns : [ [ {
			field : 'goodsspe',
			title : '供应商',
			width : "180",
			align : 'center',
			halign : 'center',
			editor: { type: 'textbox',
        		options:{
        			height:31,
					editable:false,
					required: true,
					icons: [{
						iconCls:'icon-search',
						handler: function(e){
							initSupplierRef(e);
						}
					}]
        		}
        	}
		}, {
			field : 'supid',
			title : '供应商主键',
			hidden : true,
			editor : {
				type : 'textbox'
			}
		}, {
			field : 'gname',
			title : '商品',
			width : "180",
			align : 'center',
			halign : 'center',
			editor : {
				type : 'combobox',
				options : {
					height: 31,
                	panelHeight: 160,
                	showItemIcon: true,
                	valueField: "name",
                	editable: false,
                	required : true,
                	textField: "name",
                	data: goods,
                	onSelect: function (rec) { 
                		var gid = $('#stgrid').datagrid('getEditor', {index:editIndex,field:'gid'});
                		$(gid.target).textbox('setValue', rec.gid);
                		
                		var specid = $('#stgrid').datagrid('getEditor', {index:editIndex,field:'specid'});
                		$(specid.target).textbox('setValue', rec.id);
                		
                		var spec = $('#stgrid').datagrid('getEditor', {index:editIndex,field:'spec'});
                		$(spec.target).textbox('setValue', rec.spec);
                		
                		var type = $('#stgrid').datagrid('getEditor', {index:editIndex,field:'type'});
                		$(type.target).textbox('setValue', rec.type);
                	}
				}
			},
		}, {
			field : 'gid',
			title : '商品主键',
			hidden : true,
			editor : {
				type : 'textbox'
			}
		}, {
			field : 'specid',
			title : '商品规格主键',
			hidden : true,
			editor : {
				type : 'textbox'
			}
		}, {
			field : 'spec',
			title : '规格',
			width : "120",
			align : 'center',
			halign : 'center',
			editor : {
				type : 'textbox',
				options : {
					height : 31,
					editable : false,
					readonly : true
				}
			}
		}, {
			field : 'type',
			title : '型号',
			width : "120",
			align : 'center',
			halign : 'center',
			editor : {
				type : 'textbox',
				options : {
					height : 31,
					editable : false,
					readonly : true
				}
			}
		},

		{
			field : 'price',
			title : '成本价',
			width : "140",
			align : 'right',
			halign : 'center',
			editor : {
				type : 'numberbox',
				options : {
					height : 31,
					required : true,
					precision : 2,
					min : 0,
					onChange : function(n, o) {
						if(!isEmpty(n)){
	                		var numcell = $('#stgrid').datagrid('getEditor', {index:editIndex,field:'num'});
	                		var num = getFloatValue($(numcell.target).textbox('getValue'));
	                		
	                		var mny = num.mul(n);
	                		
	                		var mnycell = $('#stgrid').datagrid('getEditor', {index:editIndex,field:'mny'});
	                		$(mnycell.target).textbox('setValue', mny);
						}
					}
				}
			},
			formatter : formatMny
		}, {
			field : 'num',
			title : '入库数量',
			width : "140",
			align : 'right',
			halign : 'center',
			editor : {
				type : 'numberbox',
				options : {
					height : 31,
					required : true,
					precision : 0,
					min : 0,
					onChange : function(n, o) {
						if(!isEmpty(n)){
	                		var pricell = $('#stgrid').datagrid('getEditor', {index:editIndex,field:'price'});
	                		var price = getFloatValue($(pricell.target).textbox('getValue'));
	                		
	                		var mny = price.mul(n);
	                		
	                		var mnycell = $('#stgrid').datagrid('getEditor', {index:editIndex,field:'mny'});
	                		$(mnycell.target).textbox('setValue', mny);
						}
					}
				}
			},
		}, {
			field : 'mny',
			title : '金额',
			width : "120",
			align : 'right',
			halign : 'center',
			editor : {
				type : 'textbox',
				options : {
					height : 31,
					editable : false,
					readonly : true,
					precision : 2,
					min : 0,
					onChange : function(n, o) {
						if(!isEmpty(n)){
							var rows = $('#stgrid').datagrid('getRows');
							if(rows != null && rows.length > 0){
								var totalmny = parseFloat(n);
								for(var j = 0;j< rows.length; j++){
									totalmny = totalmny.add(getFloatValue(rows[j].mny));
								}
								$("#totalmny").numberbox("setValue", formatMny(totalmny));
							}
						}
					}
				}
			}
		}, {
			field : 'memo',
			title : '备注',
			width : "180",
			align : 'left',
			halign : 'center',
			editor : {
				type : 'textbox',
				options : {
					height : 31,
					validType : [ 'length[0,50]' ],
					invalidMessage : "收款说明最大长度不能超过50",
				}
			}
		}, {
        	width : '80',
			field : 'button',
			title : '操作',
        	formatter : operatorLink
		},] ],
		onDblClickRow : function(rowIndex, rowData) {
        	if(status == "brows"){
        		return;
        	}
        	endBodyEdit();
        	if($('#stgrid').datagrid('validateRow', editIndex)){
        		if (rowIndex != undefined) {
        			editIndex = rowIndex;
        			$("#stgrid").datagrid('beginEdit', editIndex);
        		}           		
        	}else{
        		Public.tips({
        			content : "请先编辑必输项",
        			type : 2
        		});
        	}
		},
	});

}

/**
 * 供应商参照初始化
 * @param e
 */
function initSupplierRef(e){
	$("#refdiv").dialog({
		width : 500,
		height : 500,
		readonly : true,
		title : '选择供应商',
		cache: false,
		modal : true,
		queryParams:{dblClickRowCallback : 'choosesupplier'},
		href : contextPath + '/ref/supplier_select.jsp',
		buttons : [ {
			text : '确认',
			handler : function() {
				chooseSupplier();
			}
		}, {
			text : '取消',
			handler : function() {
				$("#refdiv").dialog('close');
			}
		} ]
	});
	
}

/**
 * 供应商参照选择事件
 */
function chooseSupplier(){
	var row = $('#gysgrid').datagrid('getSelected');
	dClickSupplier(row);
}

/**
 * 供应商双击选择事件
 * @param row
 */
function dClickSupplier(row){
	if (row) {
		var goodsspe = $('#stgrid').datagrid('getEditor', {index : editIndex,field : 'goodsspe'});
		var supid = $('#stgrid').datagrid('getEditor', {index : editIndex,field : 'supid'});
		$(goodsspe.target).textbox('setValue', row.name);
		$(supid.target).textbox('setValue', row.suid);
	}
	$("#refdiv").dialog('close');
}

/**
 * 卡片grid按钮初始化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function operatorLink(val,row,index){  
	var add = '<div><a href="javascript:void(0)" id="addBut" onclick="addRow()">'+
		'<img title="增行" style="margin:0px 20% 0px 20%;" src="../../images/add.png" /></a>';
	var del = '<a href="javascript:void(0)" id="delBut" onclick="delRow(this)">'+
		'<img title="删行" src="../../images/del.png" /></a></div>';
    return add + del;  
}

/**
 * 增行
 */
function addRow(){
	endBodyEdit();
	if(status == 'brows') {
		return ;
	}
	if(isCanAdd()){
		$('#stgrid').datagrid('appendRow',{});
		editIndex = $('#stgrid').datagrid('getRows').length - 1;
		$('#stgrid').datagrid('beginEdit',editIndex);
	}else{
		Public.tips({
			content : "请先录入必输项",
			type : 2
		});
		return;
	}
}

/**
 * 删行
 */
function delRow(ths) {
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	endBodyEdit();
	if(status == 'brows') {
		return ;
	}
	if(tindex == editIndex){
		var rows = $('#stgrid').datagrid('getRows');
		if(rows && rows.length > 1){
			$('#stgrid').datagrid('deleteRow', Number(tindex));   //将索引转为int型，否则，删行后，剩余行的索引不重新排列
		}
	}else{
		if(isCanAdd()){
			var rows = $('#stgrid').datagrid('getRows');
			if(rows && rows.length > 1){
				$('#stgrid').datagrid('deleteRow', Number(tindex));   //将索引转为int型，否则，删行后，剩余行的索引不重新排列
			}
		}else{
			Public.tips({
				content : "请先录入必输项",
				type : 2
			});
			return;
		}
	}
}

/**
 * 行编辑结束事件
 */
function endBodyEdit(){
    var rows = $("#stgrid").datagrid('getRows');
 	for ( var i = 0; i < rows.length; i++) {
 		$("#stgrid").datagrid('endEdit', i);
 	}
};

/**
 * 能否增行
 * @returns {Boolean}
 */
function isCanAdd() {
    if (editIndex == undefined) {
        return true;
    }
    if ($('#stgrid').datagrid('validateRow', editIndex)) {
        $('#stgrid').datagrid('endEdit', editIndex);
        editIndex = undefined;
        return true;
    } else {
        return false;
    }
}

/**
 * 修改
 * @param index
 */
function edit(index){
	var row = $('#grid').datagrid('getData').rows[index];
	if (row == null) {
		Public.tips({
			content : '请您先选择一行！',
			type : 2
		});
		return;
	}

	$.ajax({
        type: "post",
        dataType: "json",
        url: contextPath + '/dealmanage/stockin!queryById.action',
        data: row,
        traditional: true,
        async: false,
        success: function(data, textStatus) {
            if (!data.success) {
            	Public.tips({content:data.msg,type:1});
            } else {
                var row = data.rows;
                if(row.status == 2){
            		Public.tips({
            			content : "该入库单状态为已确认，不允许修改！",
            			type : 2
            		});
            		$('#grid').datagrid('updateRow', {
            			index : index,
            			row : {
            				status : row.status,
            				updatets : row.updatets,
            			}
            		});
            		status = "brows";// 页面状态=浏览态
            		return;
            	}
                showCard();
                $('#stform').form('load',row);
                if(row.children != null && row.children.length > 0){
                	$('#stgrid').datagrid('loadData',row.children);
                }
                status == "edit";
                btnCtrl();
                isItemEdit(false);
            }
        },
    });
}

/**
 * 删除
 * @param ths
 */
function dele(ths){
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	var row = $('#grid').datagrid('getData').rows[tindex];
	if (row.status != 1) {
		Public.tips({
			content : '该记录不是已保存状态，不允许删除',
			type : 2
		});
		return;
	}
	$.messager.confirm("提示", "你确定删除吗？", function(flag) {
		if (flag) {
			$.ajax({
				type : "post",
				dataType : "json",
				url : contextPath + '/dealmanage/stockin!delete.action',
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
						$('#grid').datagrid('clearSelections');
						reloadData();
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

/**
 * 保存
 */
function onSave(){
	endBodyEdit();
	
	var postdata = new Object();
	
	var totalmny = parseFloat(0);
	var body = "";
	//界面数据
	var rows = $('#stgrid').datagrid('getRows');
	if(rows != null && rows.length > 0){
		for(var j = 0;j< rows.length; j++){
			body = body + JSON.stringify(rows[j]); 
			totalmny = totalmny.add(getFloatValue(rows[j].mny));
		}
		$("#totalmny").numberbox("setValue", formatMny(totalmny));
	}
	
	postdata["body"] = body;
	
	postdata["head"] = JSON.stringify(serializeObject($('#stform')));
	
	var adddata = "";
	var deldata = "";
	var upddata = "";
	//新增数据		
	var insRows = $('#stgrid').datagrid('getChanges', 'inserted');
	if(insRows != null && insRows.length > 0){
		for(var j = 0;j <insRows.length; j++){
			adddata = adddata + JSON.stringify(insRows[j]);					
		}
	}
	//删除数据	
	var delRows = $('#stgrid').datagrid('getChanges', 'deleted');
	if(delRows != null && delRows.length > 0){
		for(var j = 0;j <delRows.length; j++){
			deldata = deldata + JSON.stringify(delRows[j]);
		}
	}
	//更新数据
	var updRows = $('#stgrid').datagrid('getChanges', 'updated');
	if(updRows != null && updRows.length > 0){
		for(var j = 0;j< updRows.length; j++){
			upddata = upddata + JSON.stringify(updRows[j]);
		}			
	}
	
	postdata["adddata"] = adddata;
	postdata["deldata"] = deldata;
	postdata["upddata"] = upddata;

	onSaveSubmit(postdata);
}

/**
 * 入库单-提交后台保存
 */
function onSaveSubmit(postdata){
	if ($("#goods_add").form('validate')) {
		$.messager.progress({
			text : '数据保存中，请稍后.....'
		});
		
		$('#stform').form('submit', {
			url : DZF.contextPath + '/dealmanage/stockin!save.action',
			queryParams : postdata,
			success : function(result) {
				var result = eval('(' + result + ')');
				$.messager.progress('close');
				if (result.success) {
					var row = result.rows;
					$("#listPanel").show();
					$("#cardPanel").hide();
					reloadData();
//					if(status == "edit"){
//						$('#grid').datagrid('updateRow', {
//							index : editIndex,
//							row : row
//						});
//						editIndex = null;
//					}else if(status == "add"){
//						$('#grid').datagrid('appendRow',row);
//					}
					
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

/**
 * 确认入库
 * @param type
 */
function confirm(){
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
	$.messager.progress({
		text : '数据操作中....'
	});
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/dealmanage/stockin!confirmData.action',
		data : postdata,
		traditional : true,
		async : false,
		success : function(result) {
			$.messager.progress('close');
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
						map.put(rerows[i].stid,rerows[i]);
					}
					var index;
					var indexes = new Array();
					for(var i = 0; i < rows.length; i++){
						if(map.containsKey(rows[i].stid)){
							index = $('#grid').datagrid('getRowIndex', rows[i]);
							indexes.push(index);
						}
					}
					for(var i in indexes){
						if(type == 1){
							$('#grid').datagrid('updateRow', {
								index : indexes[i],
								row : {
									status : rerows[i].status,
									updatets : rerows[i].updatets,
								}
							});
						}else if(type == 2){
							$('#grid').datagrid('updateRow', {
								index : indexes[i],
								row : {
									status : rerows[i].status,
									updatets : rerows[i].updatets,
								}
							});
						}
					}
				}
				$("#grid").datagrid('uncheckAll');
			}
		},
	});
}
