var contextPath = DZF.contextPath;
var status="brows"

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});

$(function() {
	initQry();
	load();
	reloadData();
	initCard();
});

//初始化查询
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
	
	initQryCommbox();
	initChannel();
	initCorpk();
	$('#corpkna_ae').textbox('readonly',true);
}

function initCard(){
	initPeriod("#cperiod");
	initCardCorp();//卡片界面，加盟商
	initCardCorpk();//卡片界面，客户
}

/**
 * 数据表格初始化
 */
function load(){
	$('#grid').datagrid({
//		url : DZF.contextPath + '/chn_set/account!query.action',
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height : Public.setGrid().h,
		singleSelect : false,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		idField : 'asetid',
		frozenColumns:[[ {
			field : 'ck',
			checkbox : true
		},{
			width : '100',
			title : '操作列',
			field : 'operate',
            halign : 'center',
			align : 'center',
			formatter:opermatter
		},]],
		columns : [ [ {
			width : '100',
			title : '时间戳',
			field : 'updatets',
			hidden : true
		},{
			width : '60',
			title : '大区',
			field : 'aname',
			align : 'left'
	   },{
		   width : '120',
		   title : '地区',
		   field : 'provname',
		   align : 'left'
	   },{
		   width : '100',
		   title : '会计运营经理',
		   field : 'cuname',
		   align : 'left'
	   },{
			width : '200',
			title : '加盟商',
			field : 'corpnm',
			halign : 'center',
			align : 'left',
			formatter :showTitle,
		}, {
			width : '200',
			title : '客户名称',
			field : 'corpkname',
			halign : 'center',
			align : 'left',
			formatter :showTitle,
		}, {
			width : '120',
			title : '合同编码',
			field : 'vccode',
			halign : 'center',
			align : 'left',
			formatter :showTitle,
		}, {
			width : '90',
			title : '开始日期',
			field : 'bperiod',
			halign : 'center',
			align : 'left',
		}, {
			width : '90',
			title : '结束日期',
			field : 'eperiod',
			halign : 'center',
			align : 'left',
		}, {
			width : '90',
			title : '调整后',
			field : 'cperiod',
			halign : 'center',
			align : 'left',
		},{
			width : '70',
			title : '差异月份',
			field : 'idiff',
			halign : 'center',
			align : 'left',
		}, {
			width : '60',
			title : '状态',
			field : 'istatus',
            halign : 'center',
			align : 'center',
			formatter : function(value) {
				if (value == '0')
					return '启用';
				if (value == '1')
					return '停用';
			}
		}, {
			width : '100',
			title : '录入人',
			field : 'coptname',
			halign : 'center',
			align : 'left',
		}, {
			width : '100',
			title : '录入时间',
			field : 'ddate',
            halign : 'center',
			align : 'center',
		}, ] ],
		onLoadSuccess : function(data) {
            $('#grid').datagrid("scrollTo",0);
		},
	});
}


/**
 * 查询
 */
function reloadData() {
    var queryParams = $('#grid').datagrid('options').queryParams;
    $('#grid').datagrid('options').url = DZF.contextPath + '/chn_set/account!query.action';
	var vince=$('#ovince').combobox('getValue');
	if(isEmpty(vince)){
		vince=-1;
	}
    queryParams['aname'] =  $('#aname').combobox('getValue');
    queryParams['ovince'] = vince;
    queryParams['uid'] = $('#uid').combobox('getValue');
    queryParams['corps'] = $("#pk_account").val();
    
    queryParams['corpkid'] = $("#corpkid_ae").val();
    queryParams['begdate'] = $("#begdate").datebox('getValue');
    queryParams['enddate'] = $("#enddate").datebox('getValue');
    
    $('#grid').datagrid('options').queryParams = queryParams;
    $('#grid').datagrid('reload');
    $('#grid').datagrid('unselectAll');
    $("#qrydialog").css("visibility", "hidden");
}

function clearCon(){
	$('#aname').combobox('select',null);
	$('#ovince').combobox('select',null);
	$('#uid').combobox('select',null);
	$("#pk_account").val(null);
	$("#corpkid_ae").val(null);
	$("#channel_select").textbox("setValue",null);
}

function opermatter(val, row, index) {
	if(row.istatus==0){
		return '<a href="#" style="margin-bottom:0px;color:blue;margin-left:10px;" onclick="edit(\''+index+'\')">修改</a>'+
		'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="updateStatus(\''+row.asetid+'\',\''+row.updatets+'\',\''+1+'\')">停用</a>';
	}else{
		return '<a href="#" style="margin-bottom:0px;color:blue;margin-left:10px;" onclick="edit(\''+index+'\')">修改</a>'+
		'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="updateStatus(\''+row.asetid+'\',\''+row.updatets+'\',\''+0+'\')">启用</a>';
	}
}

function showTitle(value){
	if(value!=undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}
}

function add(){
	status="add";
	$('#addDialog').dialog({modal:true});
    $('#addDialog').dialog('open').dialog('center').dialog('setTitle',"新增");
    $('#addForm').form("clear");
}

function save(){
	var flag = $('#addForm').form('validate');
	if(flag == false){
		Public.tips({content:"必输信息为空或格式不正确",type:2});
		return;
	}
	parent.$.messager.progress({
		text : '保存中....'
	});
	$('#addForm').form('submit', {
		url : contextPath + '/chn_set/account!save.action',
		success : function(result) {
			var result = eval('(' + result + ')');
			parent.$.messager.progress('close');
			if (result.success) {
				$('#addDialog').dialog('close');
				status="brows";
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

//删除
function del(){
	var rows = $('#grid').datagrid('getSelections');
	if (rows == null||rows.length<1) {
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});
		return;
	}
	var ids = '';
	for(var i=0; i<rows.length;i++){
		ids = ids + rows[i].asetid;
	}
	$.messager.confirm("提示", "你确定删除吗？", function(flag) {
		if (flag) {
			$.ajax({
				type : "post",
				dataType : "json",
				url : contextPath + '/chn_set/account!delete.action',
				data : {"ids":ids},
				traditional : true,
				async : false,
				success : function(data, textStatus) {// result
					if (!data.success) {
						Public.tips({
							content : data.msg,
							type : 1
						});
					} else {
						reloadData();
						$('#grid').datagrid('clearSelections');
						Public.tips({
							content : data.msg,
							type : 0
						});
					}
				},
			});
		} else {
			return null;
		}
	});
}

function edit(index){
    $('#addDialog').dialog({modal:true});
    $('#addDialog').dialog('open').dialog('center').dialog('setTitle',"修改");
    $('#addForm').form("clear");
    var rows = $("#grid").datagrid("getRows");
    var row = rows[index];
    $('#addForm').form('load',row);
}

/**
 * 更新状态(启用禁用)
 * @param id
 * @param status
 */
function updateStatus(id,updatets,istatus){
//	var index = $("#grid").datagrid('getRowIndex',id);
	$.ajax({
		url : DZF.contextPath + '/chn_set/account!updateStatus.action',
		data : {
			'asetid': id,
			'istatus' : istatus,
			'updatets' : updatets
		},
		dataType : 'json',
		success : function(result){
			if (result.success) {
				Public.tips({
					content : result.msg,
				});
				reloadData();
//				$('#grid').datagrid('updateRow', {
//					index : index,
//					row : istatus
//				});
			}else{
				Public.tips({
					content : result.msg,
					type : 1
				});
			}
		}
	});
}

/**
 * 取消
 */
function cancel(){
	status="brows";
	$('#addDialog').dialog('close');
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
	var qj = $('#jqj').html();
	Business.getFile(DZF.contextPath+ '/chn_set/account!exportExcel.action',
			{'strlist':JSON.stringify(datarows), 'qj':qj,}, true, true);
}

/**
 * 客户参照初始化
 */
function initCorpk(){
	$('#corpkna_ae').searchbox({
		editable:false,
		prompt:'选择客户',
	    searcher:function(){
	    	$('#gs_dialog').dialog({
	    		width : 520,
	    		height : 490,
	    		readonly : true,
	    		close:true,
	    		title : '选择客户',
	    		modal : true,
	    		href : DZF.contextPath+'/ref/qykh_select.jsp',
	    		queryParams:{
	    			dblClickRowCallback : 'selectCorpk',
	    			corpid:$("#pk_account").val(),
	    		},
	    		buttons : [ {
	    			text : '确认',
	    			handler : function() {
	    				var row = $('#khTable').datagrid('getSelected');
	    				if(row){
	    					selectCorpk(row);
	    				}else{
	    					Public.tips({
	    						content : '请选择需要处理的数据',
	    						type : 2
	    					});
	    				}
	    			}
	    		}, {
	    			text : '取消',
	    			handler : function() {
	    				$('#gs_dialog').dialog('close');
	    			}
	    		}]
	    	});
	    }
	});
}

/**
 * 客户选择事件
 * @param row
 */
function selectCorpk(row){
	$('#corpkna_ae').textbox('setValue',row.uname);
	$('#corpkid_ae').val(row.pk_gs);
	$('#gs_dialog').dialog('close');
}

function initCardCorp(){
    $("#c_corpnm").textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#kj_dialog").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择加盟商',
                    modal: true,
                    href: DZF.contextPath + '/ref/channel_select.jsp',
                    queryParams : {
                    	issingle : "true",
    					ovince :"-5"
    				},
                    buttons: '#kj_buttons'
                });
            }
        }]
    });
}


function initCardCorpk(){
	$('#c_corpkname').searchbox({
		editable:false,
		prompt:'选择客户',
	    searcher:function(){
	    	$('#gs_dialog').dialog({
	    		width : 520,
	    		height : 490,
	    		readonly : true,
	    		close:true,
	    		title : '选择客户',
	    		modal : true,
	    		href : DZF.contextPath+'/ref/account_corpk.jsp',
	    		queryParams:{
	    			dblClickRowCallback : 'selectCardCorpk',
	    			corpid:$("#c_corpid").val(),
	    		},
	    		buttons : [ {
	    			text : '确认',
	    			handler : function() {
	    				var row = $('#khTable').datagrid('getSelected');
	    				if(row){
	    					selectCardCorpk(row);
	    				}else{
	    					Public.tips({
	    						content : '请选择需要处理的数据',
	    						type : 2
	    					});
	    				}
	    			}
	    		}, {
	    			text : '取消',
	    			handler : function() {
	    				$('#gs_dialog').dialog('close');
	    			}
	    		}]
	    	});
	    }
	});
}

function selectCardCorpk(row){
	$('#c_corpkname').textbox('setValue',row.corpkname);
	$('#c_corpkid').val(row.corpkid);
	$('#bperiod').textbox('setValue',row.bperiod);
	$('#tperiod').textbox('setValue',row.bperiod);
	$('#eperiod').textbox('setValue',row.eperiod);
	$('#vccode').val(row.vccode);
	$('#contractid').val(row.contractid);
	$('#gs_dialog').dialog('close');
}

