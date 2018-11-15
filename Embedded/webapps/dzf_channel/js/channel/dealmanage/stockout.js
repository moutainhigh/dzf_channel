var contextPath = DZF.contextPath;
var status="brows";
var krows=null;//客户名称

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
	// 下拉按钮的事件
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
		idField : 'pk_areab',
		height : 220,
		singleSelect : true,
		columns : [ [ 
		{
			width : '100',
			title : '订单编码',
			field : 'vcode',
			align : 'center',
            halign : 'center',
		}, {
			width : '200',
			title : '商品',
			field : 'gname',
			align : 'left',
            halign : 'center',
		}, {
			width : '100',
			title : '规格',
			field : 'invspec',
            halign : 'center',
			align : 'left',
		},{
			width : '160',
			title : '型号',
			field : 'invtype',
            halign : 'center',
			align : 'left',
//		}, {
//			width : '100',
//			title : '购买数量',
//			field : 'nnum',
//            halign : 'center',
//			align : 'center',
		}, {
			width : '100',
			title : '出库量',
			field : 'nnum',
            halign : 'center',
			align : 'center',
		}, {
			width : '100',
			title : '金额',
			field : 'nmny',
			hidden : true
		}, {
			width : '100',
			title : '操作列',
			field : 'operate',
            halign : 'center',
			align : 'center',
			formatter:coperatorLink
		} , 
		] ],
	});
}

function coperatorLink(val,row,index){  
    return '<div><a href="javascript:void(0)" id="delBut" onclick="delRow(this)"><img title="删行" src="../../images/del.png" /></a></div>';  
}

/**
 * 删行
 */
function delRow(ths) {
	if(status == 'brows') {
		return ;
	}
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	$('#cardGrid').datagrid('deleteRow', Number(tindex));   //将索引转为int型，否则，删行后，剩余行的索引不重新排列
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
		idField : 'gid',
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
			width : '100',
			title : '单据编码',
			field : 'vcode',
			align : 'center',
            halign : 'center',
		}, {
			width : '200',
			title : '加盟商',
			field : 'corpname',
			align : 'left',
            halign : 'center',
		}, {
			width : '100',
			title : '物流公司',
			field : 'logcom',
			align:'right',
            halign:'center',
		}, {
			width : '100',
			title : '物流单号',
			field : 'lognum',
            halign : 'center',
			align : 'left',
		},{
			width : '160',
			title : '备注',
			field : 'memo',
            halign : 'center',
			align : 'left',
		}, {
			width : '100',
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
			width : '100',
			title : '录入人',
			field : 'ctname',
            halign : 'center',
			align : 'center',
		}, {
			width : '100',
			title : '录入时间',
			field : 'ctdate',
            halign : 'center',
			align : 'center',
		}, {
			width : '100',
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

function opermatter(val, row, index) {
	return '<a href="#" style="margin-bottom:0px;color:blue;" onclick="edit(\''+row.soutid+'\')">编辑</a> '+
	' <a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="delOrder(\''+index+'\')">删除</a>'+
	' <a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="updateDeliver(\''+index+'\')">确认收货</a>';
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
	var nmny;
	var childBody = "";
	var rows = $("#cardGrid").datagrid('getRows');
	for (var i = 0; i < rows.length; i++) {
		childBody = childBody + JSON.stringify(rows[i]);
		nmny =getFloatValue(rows[i].nmny*rows[i].nnum)
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
	if("add"==status||"edit"==status){
		$('#addSave').show();
		$('#addCancel').show();
		$('#corpid').combobox('readonly',false);
		$('#vmemo').textbox('readonly',false);
	}else if("brows"==status){
		$('#addSave').hide();
		$('#addCancel').hide();
		$('#corpid').combobox('readonly',true);
		$('#vmemo').textbox('readonly',true);
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

/**
 * 弹出物流信息对话框
 */
function tanLog(){
	var rows = $('#grid').datagrid('getSelections');
	if(rows.length <= 0){
		Public.tips({
			content : '请至少选择一行数据',
			type : 2
		});
		return;
	}
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
	var rows = $('#grid').datagrid('getSelections');
	commit(rows);
}

function commit(rows){
	parent.$.messager.progress({text : '确认中....'});
	failen=0;
	failmsg="";
	var postdata = new Object();
	postdata["logunit"] = $("#logunit").textbox('getValue');
	postdata["fcode"] = $("#fcode").textbox('getValue');
	for(var i=0;i<rows.length;i++){
		delete rows[i].tableCodes;
		postdata["head"] = JSON.stringify(rows[i]);
		updateCommit(postdata);
	}
	parent.$.messager.progress('close');
	$('#logDialog').dialog('close');
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

function updateCommit(postdata){
	$.ajax({
		type : 'POST',
		async : false,
		url : contextPath + '/dealmanage/stockout!updateCommit.action',
		data : postdata,
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

function updateDeliver(index){
	var row=$('#grid').datagrid('getRows')[index];
	$.ajax({
		type : 'POST',
		async : false,
		url : contextPath + '/dealmanage/stockout!updateDeliver.action',
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
 * 打印
 */
function doPrint(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'当前界面数据为空',type:2});
		return;
	}
	var columns = $('#grid').datagrid("options").columns[0];
	Business.getFile(contextPath+ '/report/conReceive!print.action',{'strlist':JSON.stringify(datarows),
		'columns':JSON.stringify(columns)}, true, true);
}
