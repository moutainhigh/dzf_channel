var contextPath = DZF.contextPath;
var status="brows";
var editIndex;

//数据表格随窗口大小改变
$(window).resize(function() {
	$('#grid').datagrid('resize', {
		height : Public.setGrid().h,
		width : 'auto',
	});
});

$(function(){
	initQry();
	initQryLitener();
	initUser();
	load();
	reloadData();
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
	queryBoxChange(); // 绑定时间对
	$("#begdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#enddate").datebox("setValue",parent.SYSTEM.LoginDate);
	$("#jqj").html(parent.SYSTEM.PreDate+" 至  "+parent.SYSTEM.LoginDate);
	$("#bperiod").datebox("setValue", parent.SYSTEM.PreDate);
	$("#eperiod").datebox("setValue",parent.SYSTEM.LoginDate);
}

/**
 * 查询框监听事件
 */
function initQryLitener(){
	$("#begdate").datebox("readonly", false);
	$("#enddate").datebox("readonly", false);
	$('#bperiod').datebox("readonly", true);
	$('#eperiod').datebox("readonly", true);
    $('input:radio[name="seledate"]').change( function(){  
		var ischeck = $('#tddate').is(':checked');
		if(ischeck){
			var sdv = $('#begdate').datebox('getValue');
			var edv = $('#enddate').datebox('getValue');
			$('#jqj').html(sdv + ' 至 ' + edv);
			$("#begdate").datebox("readonly", false);
			$("#enddate").datebox("readonly", false);
			$('#bperiod').datebox("readonly", true);
			$('#eperiod').datebox("readonly", true);
		}else{
			var sdv = $("#bperiod").datebox('getValue');
			var edv = $("#eperiod").datebox('getValue');
			$('#jqj').html(sdv + ' 至 ' + edv);
			$("#begdate").datebox("readonly", true);
			$("#enddate").datebox("readonly", true);
			$('#bperiod').datebox("readonly", false);
			$('#eperiod').datebox("readonly", false);
		}
	});
}

function queryBoxChange(){
	$("#begdate").datebox({
		onChange: function(newValue, oldValue){
			var edv = $("#enddate").datebox('getValue');
			$('#jqj').text(newValue + ' 至 ' + edv);
		}
	});
	$("#enddate").datebox({
		onChange: function(newValue, oldValue){
			var sdv = $("#begdate").datebox('getValue');
			$('#jqj').text(sdv + ' 至 ' + newValue);
		}
	});
	$('#bperiod').datebox({
		onChange: function(newValue, oldValue){
			var edv = $('#eperiod').datebox('getValue');
			$('#jqj').text(newValue + ' 至 ' + edv);
		}
	});
	$('#eperiod').datebox({
		onChange: function(newValue, oldValue){
			var sdv = $('#bperiod').datebox('getValue');
			$('#jqj').text(sdv + ' 至 ' + newValue);
		}
	});
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
			width : '120',
			title : '领取日期',
			field : 'getdate',
            halign : 'center',
			align : 'center',
		},{
			width : '200',
			title : '事项',
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
					return '已确认';
			}
		}, {
			width : '100',
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
			title : '确认时间',
			field : 'contime',
            halign : 'center',
			align : 'center',
		}, {
			width : '180',
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
		'<span style="margin-bottom:0px;margin-left:10px;">取消确认 </span>';
	}else if(row.vstatus==1){
		return '<span style="margin-bottom:0px;margin-left:10px;">编辑</span>'+
		'<span style="margin-bottom:0px;margin-left:10px;">删除 </span>'+
		'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="tanCancel(\''+index+'\')">取消确认</a>';
	}else{
		return '<span style="margin-bottom:0px;margin-left:10px;">编辑</span>'+
		'<span style="margin-bottom:0px;margin-left:10px;">删除 </span>'+
		'<span style="margin-bottom:0px;margin-left:10px;">取消确认 </span>';
	}
}

function add(){
    $('#cardDialog').dialog({modal:true});
    $('#cardDialog').dialog('open').dialog('center').dialog('setTitle',"其他出库单新增");
    $('#stockout').form("clear");
    
    initCard();
    $('#cardGrid').datagrid('loadData', { total : 0, rows : [] });// 清楚缓存数据
    
	$("#getdate").datebox("setValue",parent.SYSTEM.LoginDate);
	$('#cardGrid').datagrid('appendRow', {});
	editIndex = $('#cardGrid').datagrid('getRows').length - 1;
	$('#cardGrid').datagrid('beginEdit',editIndex);
	
    status="add";
    updateBtnState();
};

function edit(id){
	var row = queryByID(id,1);
	if(isEmpty(row)){
		return;
	}
    $('#cardDialog').dialog({modal:true});
    $('#cardDialog').dialog('open').dialog('center').dialog('setTitle',"其他出库单修改");
    $('#stockout').form("clear");
    $('#stockout').form('load',row);
    initCard();
	$("#cardGrid").datagrid("loadData",row.children);
	status="edit";
    updateBtnState();
}

function view(id){
	var row = queryByID(id,2);
	if(isEmpty(row)){
		return;
	}
    $('#cardDialog').dialog({modal:true});
    $('#cardDialog').dialog('open').dialog('center').dialog('setTitle',"其他出库单查看");
    $('#stockout').form("clear");
    $('#stockout').form('load',row);
    initCard();
	$("#cardGrid").datagrid("loadData",row.children);
	status="brows";
    updateBtnState();
}

function queryByID(id,type){
	var urlText=contextPath;
	if(type==1){
		urlText+='/dealmanage/otherout!queryByID.action';
	}else{
		urlText+='/dealmanage/otherout!queryForLook.action';
	}
	var row;
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url :  urlText ,
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
	var rows=$('#cardGrid').datagrid('getRows');
	endBodyEdit();
//	if(rows&&rows.length>0){
//		if(isEmpty(rows[rows.length-1].gid)){
//			$('#cardGrid').datagrid('deleteRow',rows.length-1);
//		}
//	}
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
		url :	contextPath + '/dealmanage/otherout!save.action',
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
		$('#getdate').datebox('readonly',false);
		$('#memo').textbox('readonly',false);
		$('#cardGrid').datagrid('showColumn', 'usenum');
	}else if("edit"==status){
		$('#addSave').show();
		$('#addCancel').show();
		$('#getdate').datebox('readonly',false);
		$('#memo').textbox('readonly',false);
		$('#cardGrid').datagrid('showColumn', 'usenum');
	}else if("brows"==status){
		$('#addSave').hide();
		$('#addCancel').hide();
		$('#getdate').datebox('readonly',true);
		$('#memo').textbox('readonly',true);
		$('#cardGrid').datagrid('hideColumn', 'usenum');
	}	
}

/**
 * 查询数据
 */
function reloadData(){
	var begdate = null;
	var enddate = null;
	var bperiod = null;
	var eperiod = null;
	var ischeck = $('#tddate').is(':checked');
	if(ischeck){
		begdate = $('#begdate').datebox('getValue'); 
		enddate = $('#enddate').datebox('getValue'); 
	}else{
		bperiod = $('#bperiod').datebox('getValue'); 
		eperiod = $('#eperiod').datebox('getValue'); 
	}	
	
	var url = contextPath + '/dealmanage/otherout!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'begdate' : begdate,
		'enddate' : enddate,
		'bperiod' : bperiod,
		'eperiod' : eperiod,
		'ucode' : $("#ucode").textbox('getValue'),
		'uid' :   $('#uid').combobox('getValue'),
		'qtype' : $('#qtype').combobox('getValue'),
	});
	$('#grid').datagrid('clearSelections');
	$('#grid').datagrid('clearChecked');
	$('#qrydialog').hide();
}

//查询框关闭事件
function closeCx() {
	$("#qrydialog").css("visibility", "hidden");
}

// 清空查询条件
function clearCondition(){
	$('#qtype').combobox('select',"-1");
	$('#ucode').textbox('setValue',null);
	$('#uid').combobox('select',null);
}

/**
 * 确认出库
 */
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
 * 取消确认出库
 */
function tanCancel(index){
	$.messager.confirm("提示", "你确认取消确认吗？", function(flag) {
		if (flag) {
			var row= $('#grid').datagrid('getRows')[index];
			$.ajax({
				type : 'POST',
				async : false,
				url : contextPath + '/dealmanage/otherout!updateCancel.action',
				data : row,
				dataTye : 'json',
				success : function(result) {
					var result = eval('(' + result + ')');
					if (result.success) {
						Public.tips({content :  "操作成功",type : 0});	
						reloadData();
					} else {
						Public.tips({content : result.msg,type : 2});
						return;
					}
				}
			});
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
			type : 2
		});	
	}
	reloadData();
}

function updateCommit(row){
	$.ajax({
		type : 'POST',
		async : false,
		url : contextPath + '/dealmanage/otherout!updateCommit.action',
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
		url : contextPath + '/dealmanage/otherout!delete.action',
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
	var rows = $('#grid').datagrid('getChecked');
	if(rows==undefined || rows.length != 1){
		Public.tips({
			content : '请选择一行数据',
			type : 2
		});
		return;
	}
	var id=rows[0].soutid;
	Business.getFile(contextPath+ '/dealmanage/otherout!print.action',{
		'id':id}, true, true);
}

/**
 * 导出
 */
function doExport(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'当前界面数据为空',type:2});
		return;
	}
	var columns = $('#grid').datagrid("options").columns[0];
//	var qj = $('#bdate').datebox('getValue') + '至' + $('#edate').datebox('getValue');
	var qj = $('#jqj').html();
	Business.getFile(DZF.contextPath+ '/dealmanage/otherout!exportExcel.action',
			{'strlist':JSON.stringify(datarows), 'qj':qj,}, true, true);
}


function initUser(){
	$.ajax({
		type : 'POST',
		async : false,
		url : contextPath + '/sys/chnUseract!queryCombobox.action',
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
 * 卡片界面表格
 */
function initCard(){
	var goods = null;
	$.ajax({
		type : 'POST',
		async : false,
		url : contextPath + '/dealmanage/otherout!queryGoodsAble.action',
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
	
	$('#cardGrid').datagrid({
		striped : true,
		rownumbers : true,
		idField : 'soutbid',
		height : 220,
		singleSelect : true,
		columns : [ [ 
		  {
			width : '300',
			title : '商品',
			field : 'gname',
			halign : 'center',
			align : 'left',
			editor : {
				type : 'combobox',
				options : {
					height: 31,
                	panelHeight: 160,
                	showItemIcon: true,
                	valueField: "gname",
                	editable: true,
                	required : true,
                	textField: "gname",
                	data: goods,
                	onSelect: function (rec) { 
                		var gid = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'gid'});
                		$(gid.target).textbox('setValue', rec.gid);
                		
                		var specid = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'specid'});
                		$(specid.target).textbox('setValue', rec.specid);
                		
                		var invspec = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'invspec'});
                		$(invspec.target).textbox('setValue', rec.invspec);
                		
                		var invtype = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'invtype'});
                		$(invtype.target).textbox('setValue', rec.invtype);
                		
                		var nprice = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'nprice'});
                		$(nprice.target).textbox('setValue', rec.nprice);
                		
                		var usenum = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'usenum'});
                		$(usenum.target).textbox('setValue', rec.usenum);
                	}
				}
			},
		}, {
			width : '120',
			title : '规格',
			field : 'invspec',
            halign : 'center',
			align : 'left',
			editor : {
				type : 'textbox',
				options : {
					height : 31,
					editable : false,
					readonly : true
				}
			}
		},{
			width : '120',
			title : '型号',
			field : 'invtype',
            halign : 'center',
			align : 'left',
			editor : {
				type : 'textbox',
				options : {
					height : 31,
					editable : false,
					readonly : true
				}
			}
		}, {
			width : '90',
			title : '可用数量',
			field : 'usenum',
            halign : 'center',
			align : 'right',
			hidden : true,
			editor : {
				type : 'numberbox',
				options : {
					height : 31,
					editable : false,
					readonly : true
				}
			}
		}, {
			width : '80',
			title : '出库数量',
			field : 'nnum',
            halign : 'center',
			align : 'right',
			editor : {
				type : 'numberbox',
				options : {
					height : 31,
					required : true,
					precision :0,
					min : 1,
					max : 99999,
					onChange : function(n, o) {
						if(!isEmpty(n) && !isEmpty(o)){
	                		var usenumcell = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'usenum'});
	                		var usenum = getFloatValue($(usenumcell.target).textbox('getValue'));
	                		if(n>usenum){
//	                			Public.tips({content : "出库数量得小于等于可用数量",type : 2});
	                    		var nnumcell = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'nnum'});
		                		$(nnumcell.target).numberbox('setValue', usenum);
		                		n=usenum
	                		}
	                		var npricecell = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'nprice'});
	                		var nprice = getFloatValue($(npricecell.target).textbox('getValue'));
	                		var nmny = nprice.mul(n);
	                		
	                		var nmnycell = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'nmny'});
	                		$(nmnycell.target).numberbox('setValue', formatMny(nmny));
						}
					}
				}
			}
		},{
			width : '100',
			title : '销售价',
			field : 'nprice',
			hidden : true,
			editor : {
				type : 'numberbox',
			}
		},{
			width : '100',
			title : '总金额',
			field : 'nmny',
			hidden : true,
			editor : {
				type : 'numberbox',
			}
		},{
			width : '100',
			title : '操作列',
			field : 'operate',
            halign : 'center',
			align : 'center',
			formatter:coperatorLink
		},{
			width : '100',
			title : '商品主键',
			field : 'gid',
			hidden : true,
			editor : {
				type : 'textbox',
			}
		},{
			width : '100',
			title : '规格主键',
			field : 'specid',
			hidden : true,
			editor : {
				type : 'textbox',
			}
		},
		] ],
		onClickRow :  function(index, row){
			if(status == "brows"){
				return;
			}
			endBodyEdit();
			if($('#cardGrid').datagrid('validateRow', editIndex)){
				if (index != undefined && isEmpty(row.billid_b)) {
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