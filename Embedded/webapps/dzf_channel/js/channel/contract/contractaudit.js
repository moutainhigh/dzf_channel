var grid;
$(function(){
	load();
	reloadData();
	initRef();
	$('#corpkna_ae').textbox('readonly',true);
});

/**
 * 参照初始化
 */
function initRef(){
	//查询-加盟商参照初始化
	$('#channel_select').textbox({
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
    					ovince :"-1"
    				},
                    buttons: '#chnBtn'
                });
            }
        }]
    });
	
	//查询-客户参照初始化
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
	
	//查询-渠道经理参照初始化
	$('#manager').textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#manDlg").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择渠道经理',
                    modal: true,
                    href: DZF.contextPath + '/ref/manager_select.jsp',
                    queryParams:{
                    	uid : $("#uid").val(),
    	    		},
                    buttons: '#manBtn'
                });
            }
        }]
    });
}

/**
 * 查询-加盟商选择事件
 */
function selectCorps(){
	var rows = $('#gsTable').datagrid('getChecked');
	dClickCompany(rows);
}

/**
 * 查询-加盟商双击选择
 * @param rowTable
 */
function dClickCompany(rowTable){
	var str = "";
	var corpIds = [];
	if(rowTable){
		if(rowTable.length>300){
			Public.tips({content : "一次最多只能选择300个客户!" ,type:2});
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
		$("#corpkid_ae").val(null);
		$("#corpkna_ae").textbox("setValue",null);
		if(!isEmpty(rowTable.length)&&rowTable.length==1){
			$('#corpkna_ae').textbox('readonly',false);
		}else{
			$('#corpkna_ae').textbox('readonly',true);
		}
		
		$("#channel_select").textbox("setValue",str);
		$("#pk_account").val(corpIds);
	}
	 $("#chnDlg").dialog('close');
}

/**
 * 查询-客户选择事件
 * @param row
 */
function selectCorpk(row){
	$('#corpkna_ae').textbox('setValue',row.uname);
	$('#corpkid_ae').val(row.pk_gs);
	$('#gs_dialog').dialog('close');
}

/**
 * 查询-渠道经理选择事件
 */
function selectMans(){
	var rows = $('#mgrid').datagrid('getChecked');
	dClickMans(rows);
}

/**
 * 查询-渠道经理双击选择
 * @param rowTable
 */
function dClickMans(rowTable){
	var unames = "";
	var uids = [];
	if(rowTable){
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个经理",
				type : 2
			});
			return;
		}
		for(var i=0;i<rowTable.length;i++){
			if(i == rowTable.length - 1){
				unames += rowTable[i].uname;
			}else{
				unames += rowTable[i].uname+",";
			}
			uids.push(rowTable[i].uid);
		}
		$("#manager").textbox("setValue",unames);
		$("#managerid").val(uids);
	}
	 $("#manDlg").dialog('close');
}

/**
 * 快速查询
 */
function qryData(){
	var url = DZF.contextPath + "/contract/contractaudit!query.action";
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		"recycle" : 1,
	});
}

/**
 * 查询
 */
function reloadData(){
	$("#grid").datagrid('unselectAll');
	$("#grid").datagrid('uncheckAll');
	// 字段校验
	var bdate = $("#bdate").datebox("getValue");
	var edate = $("#edate").datebox("getValue");
	if(!isEmpty(bdate)){
		if (!checkdate("bdate")) {
			return;
		}
	}
	if(!isEmpty(edate)){
		if (!checkdate("edate")) {
			return;
		}
	}
	
	var fpath = "";
	if($("#qnullify").is(':checked')){
		fpath += "2,"
	}
	if($("#qstop").is(':checked')){
		fpath += "1,"
	}
	if($("#unroutine").is(':checked')){
		fpath += "3,"
	}
	
	var url = DZF.contextPath + "/contract/contractaudit!query.action";
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		"bperiod" : bdate,
		"eperiod" : bdate,
		"fpath" : fpath,
		"apstatus" : $("#qapstatus").combobox('getValue'),
		"chname" : $("#qchname").combobox('getValue'),
		"corpid" : $("#pk_account").val(),
		"corpkid" : $("#corpkid_ae").val(),
		"managerid" : $("#corpkid_ae").val(),
	});
	if(!isEmpty(bdate) && !isEmpty(edate)){
		$("#jqj").html(bdate + " 至 " + edate);
	}
	$("#qrydialog").hide();
}

/**
 * 查询框-清除
 */
function clearParams(){
	$('#corpkna_ae').textbox('readonly',true);
	$('#qapstatus').combobox('setValue', null);
	$('#qchname').combobox('setValue', null);
	$("#channel_select").textbox("setValue",null);
	$("#pk_account").val(null);
	$("#corpkna_ae").textbox("setValue",null);
	$("#corpkid_ae").val(null);
	$("#manager").textbox("setValue",null);
	$("#managerid").val(null);
}

/**
 * 列表表格初始化
 */
function load(){
	grid = $('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height : Public.setGrid().h,
		singleSelect : false,
		checkOnSelect : true,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		showFooter:true,
		remoteSort : false,//定义从服务器排序
		sortName:"aptime",//排序字段
		sortOrder:"desc",//排序方式
		columns : [ [ {
			field : 'ck',
			checkbox : true
		}, {
			width : '100',
			title : '主键',
			field : 'applyid',
			hidden : true
		}, {
			width : '130',
			title : '申请时间',
			halign:'center',
			field : 'aptime',
			sortable:true,
		}, {
			width : '80',
			title : '申请类型',
            halign:'center',
			field : 'changetype',
			align:'center',
			formatter : function(value) {
				//1：合同终止；2：合同作废；3：非常规套餐；
				if (value == '1')
					return '合同终止';
				if (value == '2')
					return '合同作废';
				if (value == '3')
					return '非常规套餐';
			}
		}, {
			width : '130',
			title : '地区',
			halign:'center',
			field : 'area',
		}, {
			width : '90',
			title : '渠道经理',
			halign:'center',
			field : 'mname',
		}, {
			width : '180',
			title : '加盟商',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '220',
			title : '客户名称',
			halign:'center',
			field : 'corpkna',
		}, {
			width : '80',
			title : '处理状态',
            halign:'center',
			field : 'apstatus',
			align:'center',
			formatter : function(value) {
				//1：渠道待审（保存态）；2： 区总待审（处理中）；3：总经理待审（处理中）；4：运营待审（处理中）；5：已处理；6：已拒绝；
				if (value == '1')
					return '渠道待审';
				if (value == '2')
					return '区总待审';
				if (value == '3')
					return '总经理待审';
				if (value == '4')
					return '运营待审';
				if (value == '5')
					return '已处理';
				if (value == '6')
					return '已拒绝';
			}
		}, {
			width : '140',
			title : '合同编码',
			halign:'center',
			field : 'vccode',
//			formatter:codeLink,
		}, {
			width : '80',
			title : '开始日期',
			halign:'center',
			field : 'bperiod',
		}, {
			width : '80',
			title : '结束日期',
			halign:'center',
			field : 'eperiod',
		}, {
			width : '80',
			title : '纳税人资格',
			halign:'center',
			field : 'chname',
		}, {
			width : '70',
			title : '月服务费',
			align:'right',
            halign:'center',
			field : 'nmsmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '70',
			title : '收款周期',
			halign:'center',
			field : 'recycle',
		}, {
			width : '70',
			title : '合同周期',
			halign:'center',
			field : 'contcycle',
		}, {
			width : '70',
			title : '代账费',
			align:'right',
            halign:'center',
			field : 'naccmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '70',
			title : '账本费',
			align:'right',
            halign:'center',
			field : 'nbmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		},    ] ],
		onLoadSuccess : function(data) {
            parent.$.messager.progress('close');
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

/**
 * 审核
 */
function audit(){
	var rows = $('#grid').datagrid('getChecked');
	if (rows == null || rows.length != 1) {
		Public.tips({
			content : '请选择一行数据',
			type : 2
		});			
		return;
	}
	
	$('#deduct_Dialog').dialog({ modal:true });//设置dig属性
	$('#deduct_Dialog').dialog('open').dialog('center').dialog('setTitle',title);
	
}

