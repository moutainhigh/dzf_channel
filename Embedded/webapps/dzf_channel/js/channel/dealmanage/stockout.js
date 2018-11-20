var contextPath = DZF.contextPath;
var status="brows";
var krows=null;//客户名称
var selIndex;//确认收货，选中的index
var editIndex = undefined;

//数据表格随窗口大小改变
$(window).resize(function() {
	$('#grid').datagrid('resize', {
		height : Public.setGrid().h,
		width : 'auto',
	});
});

$(function(){
	initQry();
	load();
	reloadData();
});

//初始化
function initQry(){
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	queryBoxChange('#begdate','#enddate');
	$("#begdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#enddate").datebox("setValue",parent.SYSTEM.LoginDate);
	$("#jqj").html(parent.SYSTEM.PreDate+" 至  "+parent.SYSTEM.LoginDate);
	initCard();
	initCorp();
	initUser();
}

function loadCardGrid(corpid){
	var url = contextPath + '/dealmanage/stockout!queryOrders.action';
	$('#cardGrid').datagrid('options').url = url;
	$('#cardGrid').datagrid('load', {'corpid' : corpid});
}

/**
 * 列表表格加载
 */
function load(){
	$('#grid').datagrid({
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
		checkOnSelect:false,
//		selectOnCheck:true,
		idField : 'soutid',
		columns : [ [ 
  		 {
   			field : 'ck',
   			checkbox : true 
		}, {
			width : '100',
			title : '时间戳',
			field : 'updatets',
			hidden : true
		}, {
			width : '120',
			title : '单据编码',
			field : 'vcode',
			halign : 'center',
			align : 'left',
            formatter : function(value, row, index) {
    			return '<a href="javascript:void(0)"  style="color:blue" onclick="view(\''+row.soutid+'\')">' + value + '</a>';
            }
		}, {
			width : '200',
			title : '加盟商',
			field : 'corpname',
			halign : 'center',
			align : 'left',
		}, {
			width : '100',
			title : '物流公司',
			field : 'logunit',
			halign : 'center',
			align : 'left',
			formatter :showTitle,
		}, {
			width : '100',
			title : '物流单号',
			field : 'fcode',
			halign : 'center',
			align : 'left',
			formatter :showTitle,
		},{
			width : '160',
			title : '备注',
			field : 'memo',
			halign : 'center',
			align : 'left',
			formatter :showTitle,
		}, {
			width : '80',
			title : '单据状态',
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
			}
		}, {
			width : '80',
			title : '录入人',
			field : 'ctname',
			halign : 'center',
			align : 'left',
		}, {
			width : '140',
			title : '录入时间',
			field : 'ctdate',
            halign : 'center',
			align : 'center',
		}, {
			width : '140',
			title : '出库时间',
			field : 'contime',
            halign : 'center',
			align : 'center',
		}, {
			width : '150',
			title : '操作列',
			field : 'operate',
            halign : 'center',
			align : 'center',
			formatter:opermatter
		} , 
		] ],
		onLoadSuccess : function(data) {
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

function showTitle(value){
	if(value!=undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}
}

function opermatter(val, row, index) {
	if(row.vstatus==0){
		return '<a href="#" style="margin-bottom:0px;color:blue;margin-left:10px;" onclick="edit(\''+row.soutid+'\')">编辑</a>'+
		'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="delOrder(\''+index+'\')">删除</a>'+
		'<span style="margin-bottom:0px;margin-left:10px;">确认发货</span>';
	}else if(row.vstatus==1){
		return '<span style="margin-bottom:0px;margin-left:10px;">编辑</span>'+
		'<span style="margin-bottom:0px;margin-left:10px;">删除 </span>'+
		'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="tanLog(\''+index+'\')">确认发货</a>';
	}else{
		return '<span style="margin-bottom:0px;margin-left:10px;">编辑</span>'+
		'<span style="margin-bottom:0px;margin-left:10px;">删除 </span>'+
		'<span style="margin-bottom:0px;margin-left:10px;">确认发货 </span>';
	}
}

function add(){
    $('#cardDialog').dialog({modal:true});
    $('#cardDialog').dialog('open').dialog('center').dialog('setTitle',"出库单新增");
    $('#stockout').form("clear");
    $('#cardGrid').datagrid('loadData', { total : 0, rows : [] });// 清楚缓存数据
    status="add";
    updateBtnState();
};

function edit(id){
	var row = queryByID(id);
	if(isEmpty(row)){
		return;
	}
    $('#cardDialog').dialog({modal:true});
    $('#cardDialog').dialog('open').dialog('center').dialog('setTitle',"出库单修改");
    $('#stockout').form("clear");
    $('#stockout').form('load',row);
	$("#cardGrid").datagrid("loadData",row.children);
	status="edit";
    updateBtnState();
}

function view(id){
	var row = queryByID(id);
	if(isEmpty(row)){
		return;
	}
    $('#cardDialog').dialog({modal:true});
    $('#cardDialog').dialog('open').dialog('center').dialog('setTitle',"出库单查看");
    $('#stockout').form("clear");
    $('#stockout').form('load',row);
	$("#cardGrid").datagrid("loadData",row.children);
	status="brows";
    updateBtnState();
}

function queryByID(id){
	var row;
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url : contextPath + '/dealmanage/stockout!queryByID.action',
		data : {"soutid" : id},
		success : function(data, textStatus) {
			if (!data.success) {
				Public.tips({
					content :  data.msg,
					type : 1
				});	
				return;
			} else {
				row=data.rows;
			}
		},
	});
	return row;
}


function addSave(){
	var flag = $('#stockout').form('validate');
	if(flag == false){
		Public.tips({content:"必输信息为空或格式不正确",type:2});
		return;
	}
	var nmny=0;
	var childBody = "";
	var rows = $("#cardGrid").datagrid('getRows');
	for (var i = 0; i < rows.length; i++) {
		childBody = childBody + JSON.stringify(rows[i]);
		nmny =getFloatValue(nmny)+getFloatValue(rows[i].nmny)
	}
	$("#nmny").numberbox('setValue',nmny);
	var postdata = new Object();
	postdata["head"] = JSON.stringify(serializeObject($('#stockout')));
	postdata["body"] = childBody;
	parent.$.messager.progress({
		text : '保存中....'
	});
	$.ajax({
		type : 'POST',
		url :	contextPath + '/dealmanage/stockout!save.action',
		data : postdata,
		dataType : 'json',
		success : function(result) {
			parent.$.messager.progress('close');
			if (result.success) {
				$('#cardDialog').dialog('close');
				Public.tips({
					content : result.msg,
					type : 0
				});
				reloadData();
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});
}

function addCancel(){
	$.messager.confirm("提示", "确定取消吗？", function(flag) {
		if (flag) {
			$('#cardDialog').dialog('close');
		} else {
			return null;
		}
	});
}


/**
 * 卡片界面的按钮显示及隐藏
 */
function updateBtnState(){
	if("add"==status){
		$('#addSave').show();
		$('#addCancel').show();
		$('#corpid').combobox('readonly',false);
		$('#memo').textbox('readonly',false);
	}else if("edit"==status){
		$('#addSave').show();
		$('#addCancel').show();
		$('#corpid').combobox('readonly',true);
		$('#memo').textbox('readonly',false);
	}else if("brows"==status){
		$('#addSave').hide();
		$('#addCancel').hide();
		$('#corpid').combobox('readonly',true);
		$('#memo').textbox('readonly',true);
	}	
}

/**
 * 查询数据
 */
function reloadData(){
	var url = contextPath + '/dealmanage/stockout!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'begdate' : $("#begdate").datebox('getValue'),
		'enddate' : $("#enddate").datebox('getValue'),
		'ucode' : $("#ucode").textbox('getValue'),
		'cpid' :  $('#cpid').combobox('getValue'),
	});
	$('#grid').datagrid('clearSelections');
	$('#qrydialog').hide();
}

//查询框关闭事件
function closeCx() {
	$("#qrydialog").css("visibility", "hidden");
}

// 清空查询条件
function clearCondition(){
	$('#cpid').combobox('select',null);
	$('#ucode').textbox('setValue',null);
	$('#uid').combobox('select',null);
}

function loadData(qtype){
	$('#grid').datagrid('unselectAll');
	var queryParams =new Object();
	queryParams['qtype'] = qtype;
	$('#grid').datagrid('options').url =contextPath + '/dealmanage/stockout!query.action';
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
}


function commit(){
	var rows = $('#grid').datagrid('getChecked');
	if(rows.length <= 0){
		Public.tips({
			content : '请至少选择一行数据',
			type : 2
		});
		return;
	}
	$.messager.confirm("提示", "确认出库吗？", function(flag) {
		if (flag) {
			commitConfirm(rows);
		} else {
			return null;
		}
	});
}

/**
 * 确认出库
 */
function commitConfirm(rows){
	parent.$.messager.progress({text : '确认中....'});
	failen=0;
	failmsg="";
	for(var i=0;i<rows.length;i++){
		updateCommit(rows[i]);
	}
	parent.$.messager.progress('close');
	if(failen==0){
		Public.tips({
			content :  "成功"+(rows.length)+"条",
			type : 0
		});	
	}else{
		Public.tips({
			content :  "成功"+(rows.length-failen)+"条，失败"+failen+"条，其中："+failmsg,
			type : 1
		});	
	}
	reloadData();
}

function updateCommit(row){
	$.ajax({
		type : 'POST',
		async : false,
		url : contextPath + '/dealmanage/stockout!updateCommit.action',
		data : row,
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (!result.success) {
				failmsg+=result.msg;
				failen+=1;
			}
		}
	});
}

function delOrder(index){
	var row=$('#grid').datagrid('getRows')[index];
	$.ajax({
		type : 'POST',
		async : false,
		url : contextPath + '/dealmanage/stockout!delete.action',
		data : row,
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				Public.tips({
					content : result.msg,
					type : 0
				});
				reloadData();
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});
}

/**
 * 弹出物流信息对话框
 */
function tanLog(index){
	selIndex=index;
	$('#logDialog').dialog({modal:true});
    $('#logDialog').dialog('open').dialog('center').dialog('setTitle',"物流信息");
    $('#logUpdate').form("clear");
}

function logCancel(){
	$.messager.confirm("提示", "确定取消吗？", function(flag) {
		if (flag) {
			$('#logDialog').dialog('close');
		} else {
			return null;
		}
	});
}

function logCommit(){
	var flag = $('#logUpdate').form('validate');
	if(flag == false){
		Public.tips({content:"必输信息为空或格式不正确",type:2});
		return;
	}
	var row= $('#grid').datagrid('getRows')[selIndex];
	updateDeliver(row);
}

function updateDeliver(row){
	var postdata = new Object();
	postdata["head"] = JSON.stringify(row);
	postdata["logunit"] = $("#slogunit").textbox('getValue');
	postdata["fcode"] = $("#sfcode").textbox('getValue');
	$.ajax({
		type : 'POST',
		async : false,
		url : contextPath + '/dealmanage/stockout!updateDeliver.action',
		data : postdata,
		dataTye : 'json',
		success : function(result) {
			$('#logDialog').dialog('close');
			var result = eval('(' + result + ')');
			if (result.success) {
				Public.tips({
					content : result.msg,
					type : 0
				});
				reloadData();
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});
}

/**
 * 打印
 */
function doPrint(){
	var rows = $('#grid').datagrid('getChecked');
	if(rows==undefined || rows.length != 1){
		Public.tips({
			content : '请选择一行数据',
			type : 2
		});
		return;
	}
	var id=rows[0].soutid;
	Business.getFile(contextPath+ '/dealmanage/stockout!print.action',{
		'id':id}, true, true);
}

function initCorp(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/sys/sys_inv_manager!queryChannel.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				krows=result.rows;
			}
		}
	});
	$('#cpid').combobox('loadData',krows);
	
	$("#corpid").combobox({
	     valueField:'pk_gs',
	     textField:'uname',
	     multiple: false,
	     onSelect: function(rec){
	    	 loadCardGrid(rec.pk_gs)
	     }
	});
	$("#corpid").combobox("loadData",krows);
}

function initUser(){
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

function initCard(){
	$('#cardGrid').datagrid({
		striped : true,
		rownumbers : true,
		idField : 'soutbid',
		height : 220,
		singleSelect : true,
		columns : [ [ 
		{
			width : '200',
			title : '订单编码',
			field : 'vcode',
			halign : 'center',
			align : 'left',
			editor : {
				type : 'textbox',
				options : {
					height:31,
					required : true,
					editable:false,
					icons: [{
						iconCls:'icon-search',
						handler: function(){
							initChnBill();
						}
					}]
				}
			}
		}, {
			width : '200',
			title : '商品',
			field : 'gname',
			halign : 'center',
			align : 'left',
		}, {
			width : '150',
			title : '规格',
			field : 'invspec',
            halign : 'center',
			align : 'left',
		},{
			width : '150',
			title : '型号',
			field : 'invtype',
            halign : 'center',
			align : 'left',
		}, {
			width : '100',
			title : '购买数量',
			field : 'nnum',
            halign : 'center',
			align : 'right',
		}, {
			width : '100',
			title : '出库数量',
			field : 'nnum',
            halign : 'center',
			align : 'right',
		},{
			width : '60',
			title : '操作列',
			field : 'operate',
            halign : 'center',
			align : 'center',
			formatter:coperatorLink
		},{
			width : '100',
			title : '销售价',
			field : 'nprice',
			hidden : true
		},{
			width : '100',
			title : '总金额',
			field : 'nmny',
			hidden : true
		},{
			width : '100',
			title : '商品主键',
			field : 'gid',
			hidden : true
		},{
			width : '100',
			title : '规格主键',
			field : 'specid',
			hidden : true
		},{
			width : '100',
			title : '订单主键',
			field : 'billid_b',
			hidden : true
		}, 
		] ],
		onClickRow :  function(index, row){
			if(status == "brows"){
				return;
			}
			endBodyEdit();
			if($('#cardGrid').datagrid('validateRow', editIndex)){
				if (index != undefined) {
					$('#cardGrid').datagrid('beginEdit', index);
					editIndex = index;
				}           		
			}else{
				Public.tips({
					content : "请先编辑必输项",
					type : 2
				});
			}
		} ,
	});
}

function initChnBill(){
	var corpid=$("#corpid").combobox('getValue');
	if(isEmpty(corpid)){
		Public.tips({
			content : "请先选择加盟商",
			type : 2
		});
	}
	var rows=$('#cardGrid').datagrid('getRows');
	var bills="";
	for(var i=0;i<rows.length;i++){
		if(!isEmpty(rows[i].billid_b)){
			bills+="'"+rows[i].billid_b+"',";
		}
	}
	$("#billDialog").dialog({
		width : 700,
		height : 400,
		readonly : true,
		title : '加盟商订单',
		cache : false,
		modal : true,
		href : contextPath + '/ref/chnbill_select.jsp',
		queryParams:{
			"corpid" : corpid,
			"bills" : bills,
		},
		buttons : [ {
			text : '确认',
			handler : function() {
				var rows = $('#billTable').datagrid('getSelections');
				if(rows && rows.length>0){
					selectChnBills(rows);
				}else{
					Public.tips({
						content : "请至少选择一行数据",
						type : 2
					});
				}
			}
		}, {
			text : '取消',
			handler : function() {
				$("#billDialog").dialog('close');
			}
		} ]
	});
}

function selectChnBills(rows){
	for(ro in rows){
		editIndex = $('#cardGrid').datagrid('getRows').length - 1;
		$('#cardGrid').datagrid('insertRow',{index:editIndex,row:rows[ro]});
	}
	$('#billDialog').dialog('close');
}

function coperatorLink(val,row,index){  
	var add = '<div><a href="javascript:void(0)" id="addBut" onclick="addRow(arguments[0])"><img title="增行" style="margin:0px 20% 0px 20%;" src="../../images/add.png" /></a>';
	var del = '<a href="javascript:void(0)" id="delBut" onclick="delRow(this)"><img title="删行" src="../../images/del.png" /></a></div>';
    return add + del;  
}

/**
 * 增行
 */
function addRow(e){
	e.stopPropagation();
	endBodyEdit();
	if(status == 'brows') {
		return ;
	}
	if(isCanAdd()){
		$('#cardGrid').datagrid('appendRow',{});
		editIndex = $('#cardGrid').datagrid('getRows').length - 1;
		$('#cardGrid').datagrid('beginEdit',editIndex);
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
	endBodyEdit();
	if(status == 'brows') {
		return ;
	}
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	if(tindex == editIndex){
		var rows = $('#cardGrid').datagrid('getRows');
		if(rows && rows.length > 1){
			$('#cardGrid').datagrid('deleteRow', Number(tindex));   //将索引转为int型，否则，删行后，剩余行的索引不重新排列
		}
	}else{
		if(isCanAdd()){
			var rows = $('#cardGrid').datagrid('getRows');
			if(rows && rows.length > 1){
				$('#cardGrid').datagrid('deleteRow', Number(tindex));   //将索引转为int型，否则，删行后，剩余行的索引不重新排列
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
    var rows = $("#cardGrid").datagrid('getRows');
 	for ( var i = 0; i < rows.length; i++) {
 		$("#cardGrid").datagrid('endEdit', i);
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
    if ($('#cardGrid').datagrid('validateRow', editIndex)) {
        $('#cardGrid').datagrid('endEdit', editIndex);
        editIndex = undefined;
        return true;
    } else {
        return false;
    }
}