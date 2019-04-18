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
    					ovince :"-2"
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
	
	if(fpath == ""){
		$('#grid').datagrid('loadData',{ total:0, rows:[]});
		$("#qrydialog").hide();
		return;
	}
	
	var url = DZF.contextPath + "/contract/contractaudit!query.action";
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		"bperiod" : bdate,
		"eperiod" : edate,
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
	$('#qapstatus').combobox('setValue', -1);
	$('#qchname').combobox('setValue', -1);
	$("#channel_select").textbox("setValue",null);
	$("#pk_account").val(null);
	$("#corpkna_ae").textbox("setValue",null);
	$("#corpkid_ae").val(null);
	$("#manager").textbox("setValue",null);
	$("#managerid").val(null);
}

/**
 * 关闭查询对话框
 */
function closeCx(){
	$("#qrydialog").hide();
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
			formatter:codeLink,
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
	
	$.ajax({
		url : DZF.contextPath + "/contract/contractaudit!queryById.action",
		dataType : 'json',
		data : {
			"applyid" : rows[0].applyid,
			"opertype"  : -1,
		},
		success : function(rs) {
			if (rs.success) {
				var row = rs.rows;
				
				if(row.changetype == 1 || row.changetype == 2){
					showChangeDlg(row);
				}else if(row.changetype == 3){
					showAuditDlg(row);
				}
				
			}else{
				Public.tips({
					content : rs.msg,
					type : 2
				});
			}
		}
	});
}

/**
 * 非常规套餐审核对话框
 * @param row
 */
function showAuditDlg(row){
	$('#audit_Dialog').dialog({ modal:true });//设置dig属性
	$('#audit_Dialog').dialog('open').dialog('center').dialog('setTitle', "非常规套餐审核");
	
	$('#aaudit').css('display','none');
	$('#aoper').css('display','none');
	if(row.apstatus == 2){
		$('#aoper').css('display','inline-block');
	}else if(row.apstatus == 1){
		$('#aaudit').css('display','inline-block');
		//下一审核人初始化
		$('#aauditer').combobox('clear');
		setComboxValue(row);
	}
	$('#aauditer').combobox("readonly",false);
	$("#aconfreason").textbox('readonly',true);
	
	$('#auditfrom').form('clear');
	$('#auditfrom').form('load',row);
	
	$('#aauditer').combobox("setValue",null);
	$("#aconfreason").textbox('setValue',null);
	
	showAuditImage(row);
	
	document.getElementById("adebit").checked = "true";
	$('#auopertype').val(1);
	initAuditRedioListener();
	
	$("#rejereson").empty();
	$('#rejereson').css('display','none');
	if(!isEmpty(row.confreason)){
    	$('#rejereson').css('display','block');
    	showRejectReason(row);
    }
}

/**
 * 变更审核对话框
 */
function showChangeDlg(row){
	$('#change_Dialog').dialog({ modal:true });//设置dig属性
	$('#change_Dialog').dialog('open').dialog('center').dialog('setTitle', "变更合同审核");
	
	$('#audit').css('display','none');
	$('#oper').css('display','none');
	if(row.apstatus == 3){
		$('#oper').css('display','inline-block');
	}else if(row.apstatus == 1 || row.apstatus == 2){
		$('#audit').css('display','inline-block');
		//下一审核人初始化
		$('#auditer').combobox('clear');
		setComboxValue(row);
	}
	$('#auditer').combobox("readonly",false);
	$("#confreason").textbox('readonly',true);
	
	$('#changefrom').form('clear');
	$('#changefrom').form('load',row);
	
	$('#auditer').combobox("setValue",null);
	$("#confreason").textbox('setValue',null);
	
	showChangeImage(row);
	
	document.getElementById("debit").checked = "true";
	initRedioListener();
	
}

/**
 * 展示驳回原因
 */
function showRejectReason(row){
	var rejesons = row.bodys;
	$("#rejereson").empty();
	if(rejesons != null && rejesons.length > 0){
		var showinfo = "";
		showinfo = showinfo + "<div class='time_col time_colp11 heading'>";
		showinfo = showinfo + "		<label style='width: 68px;text-align:center;color:#FFF;font-weight: bold;'>驳回历史</label>";
		showinfo = showinfo + "</div>";
		
		showinfo = showinfo + "<div style='height: 50px; margin-top: 16px; width: 100%;'>";
		showinfo = showinfo + "  <div"; 
		showinfo = showinfo + "    style='height: 50px; width: 100px; float: left; position: relative;'>"; 
		showinfo = showinfo + "    <img src='../../images/tbpng_03.png' style='position: absolute; left: 69px;' />"; 
		showinfo = showinfo + "    <img src='../../images/pngg_03.png' style='position: absolute; left: 75px; top: 14px;height:50px;' />"; 
		showinfo = showinfo + "  </div>"; 
		showinfo = showinfo + "  <div style='height: 50px; width: 90%; float: left;'>"; 
		showinfo = showinfo + "    <div  class='dot'>"; 
		showinfo = showinfo + "      <font>"+rejesons[0].updatets+"</font> &emsp;<span>"+rejesons[0].reason+"</span>&emsp;<font color='#FF0000'>"+rejesons[0].operator+"</font>"; 
		showinfo = showinfo + "    </div>"; 
		showinfo = showinfo + "  </div>"; 
		showinfo = showinfo + "</div>";
		if(rejesons.length > 1){
			showinfo = showinfo + "<div style='display: none;' id='panela'>";
			showinfo = showinfo + "		<div style='width: 100%;'>";
			showinfo = showinfo + "";
			showinfo = showinfo + "";
			for(var i = 1; i < rejesons.length; i++){
				showinfo = showinfo + "<div style='height: 50px;'>";
				showinfo = showinfo + "  <div"; 
				showinfo = showinfo + "    style='height: 50px; width: 100px; float: left; position: relative;'>"; 
				showinfo = showinfo + "    <img style='position: absolute; left: 71px;' src='../../images/xial_03.png' />"; 
				showinfo = showinfo + "    <img style='position: absolute; left: 75px; top: 8px;height:50px;' src='../../images/pngg_03.png' />"; 
				showinfo = showinfo + "  </div>"; 
				showinfo = showinfo + "  <div style='height: 50px; width: 90%; float: left;'>"; 
				showinfo = showinfo + "    <div  class='dot'>"; 
				showinfo = showinfo + "      <font>"+rejesons[i].updatets+"</font>&emsp;<span>"+rejesons[i].reason+"</span>&emsp;<font color='#FF0000'>"+rejesons[i].operator+"</font>"; 
				showinfo = showinfo + "    </div>"; 
				showinfo = showinfo + "  </div>"; 
				showinfo = showinfo + "</div>";
			}
			showinfo = showinfo + "		</div>";
			showinfo = showinfo + "</div>";
		}
		showinfo = showinfo + "<p class='slide'>";
		showinfo = showinfo + "		<a href='javascript:;' rel='external nofollow' class='btn-slide active'></a>";
		showinfo = showinfo + "</p>";
		$("#rejereson").append(showinfo);
		actionListen();
	}
}

/**
 * 监听
 */
function actionListen(){
	$(".btn-slide").click(function() {
		$("#panela").slideToggle("slow");
		$(this).toggleClass("active");
		return false;
	})
}

/**
 * 初始化待审核人下拉
 * @param id
 */
function setComboxValue(row) {
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/contract/contractaudit!queryAuditer.action',
		data : {
			applyid : row.applyid,
		},
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				$('#auditer,#aauditer').combobox("loadData", result.rows);
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
 * 单选按钮初始化-变更申请
 */
function initRedioListener(){
	$(":radio").click( function(){
		var opertype = $('input:radio[name="opertype"]:checked').val();
		if(opertype == 1){
			$("#confreason").textbox('readonly',true);
			$("#confreason").textbox('setValue',null);
			
			$('#auditer').combobox("readonly",false);
			
		}else if(opertype == 2){
			$("#confreason").textbox('readonly',false);
			
			$('#auditer').combobox("readonly",true);
			$('#auditer').combobox('setValue',null);
		}
	});
}

/**
 * 单选按钮初始化-非常规套餐
 */
function initAuditRedioListener(){
	$(":radio").click( function(){
		var opertype = $('input:radio[name="aopertype"]:checked').val();
		if(opertype == 1){
			$("#aconfreason").textbox('readonly',true);
			$("#aconfreason").textbox('setValue',null);
			
			$('#aauditer').combobox("readonly",false);
			$('#auopertype').val(1);
		}else if(opertype == 2){
			$("#aconfreason").textbox('readonly',false);
			
			$('#aauditer').combobox("readonly",true);
			$('#aauditer').combobox('setValue',null);
			$('#auopertype').val(2);
		}
	});
}

/**
 * 变更审核-确认
 */
function changeConfri(){
	var rows = $('#grid').datagrid('getChecked');
	if (rows == null || rows.length != 1) {
		Public.tips({
			content : '请选择一行数据',
			type : 2
		});			
		return;
	}
	var opertype = $('input:radio[name="opertype"]:checked').val();
	if(opertype == 2){
		var confreason = $("#confreason").textbox("getValue");
		if(isEmpty(confreason)){
			Public.tips({
				content : '驳回原因不能为空',
				type : 2
			});			
			return;
		}
	}
	
	parent.$.messager.progress({
		text : '审核中....'
	});
	$('#changefrom').form('submit', {
		url : DZF.contextPath + '/contract/contractaudit!updateChange.action',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				Public.tips({
					content : result.msg,
					type : 0
				});
				$('#change_Dialog').dialog('close');
				reloadData();
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
			parent.$.messager.progress('close');
		}
	});
}

/**
 * 变更审核-取消
 */
function changeCancel(){
	$('#change_Dialog').dialog('close');
}

/**
 * 非常规套餐-确认
 */
function auditConfri(){
	var rows = $('#grid').datagrid('getChecked');
	if (rows == null || rows.length != 1) {
		Public.tips({
			content : '请选择一行数据',
			type : 2
		});			
		return;
	}
	if($('#auopertype').val() == 2){
		var aconfreason = $("#aconfreason").textbox("getValue");
		if(isEmpty(aconfreason)){
			Public.tips({
				content : '驳回原因不能为空',
				type : 2
			});			
			return;
		}
	}
	parent.$.messager.progress({
		text : '变更中....'
	});
	$('#auditfrom').form('submit', {
		url : DZF.contextPath + '/contract/contractaudit!updateChange.action',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				Public.tips({
					content : result.msg,
					type : 0
				});
				$('#audit_Dialog').dialog('close');
				reloadData();
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
			parent.$.messager.progress('close');
		}
	});

}
/**
 * 非常规套餐-取消
 */
function auditCancel(){
	$('#audit_Dialog').dialog('close');
}

/**
 * 合同编码格式化
 * @param value
 * @param row
 * @param index
 */
function codeLink(value,row,index){
	return '<a href="javascript:void(0)" style="color:blue"  onclick="showInfo(' + index + ')">'+value+'</a>';
}

/**
 * 合同明细查看
 * @param index
 */
function showInfo(index){
	var row = $('#grid').datagrid('getData').rows[index];
	$.ajax({
		url : DZF.contextPath + "/contract/contractaudit!queryById.action",
		dataType : 'json',
		data : {
			"applyid" : row.applyid,
			"opertype"  : 1,
		},
		success : function(rs) {
			if (rs.success) {
				var row = rs.rows;
				
				if(row.changetype == 1 || row.changetype == 2){
					showChangeInfoDlg(row);
				}else if(row.changetype == 3){
					showAuditInfoDlg(row);
				}
				
			}else{
				Public.tips({
					content : rs.msg,
					type : 2
				});
			}
		}
	});
}

/**
 * 合同变更详情
 * @param row
 */
function showChangeInfoDlg(row){
	$('#ichange_Dialog').dialog({ modal:true });//设置dig属性
	$('#ichange_Dialog').dialog('open').dialog('center').dialog('setTitle', "变更合同详情");
	
	$('#ichangefrom').form('clear');
	$('#ichangefrom').form('load',row);
	showChangeDetImage(row);
	
	$("#ihistory").empty();
	$("#ihistory").append("已申请");
	var children = row.children;
	if(children != null && children.length > 0){
		for(var i = 0; i < children.length; i++){
			$("#ihistory").append("--->").append(children[i].memo);
		}
		if(row.apstatus == 1){
			$("#ihistory").append("--->渠道待审");
		}else if(row.apstatus == 2){
			$("#ihistory").append("--->区总待审");
		}else if(row.apstatus == 3){
			$("#ihistory").append("--->总经理待审");
		}else if(row.apstatus == 4){
			$("#ihistory").append("--->运营待审");
		}
	}else{
		if(row.apstatus == 1){
			$("#ihistory").append("--->渠道待审");
		}else if(row.apstatus == 2){
			$("#ihistory").append("--->区总待审");
		}else if(row.apstatus == 3){
			$("#ihistory").append("--->总经理待审");
		}else if(row.apstatus == 4){
			$("#ihistory").append("--->运营待审");
		}
	}
}

/**
 * 非常规套餐详情
 * @param row
 */
function showAuditInfoDlg(row){
	$('#iaudit_Dialog').dialog({ modal:true });//设置dig属性
	$('#iaudit_Dialog').dialog('open').dialog('center').dialog('setTitle', "非常规套餐申请详情");
	
	$('#iauditfrom').form('clear');
	$('#iauditfrom').form('load',row);
	showAuditDetImage(row);
	
	$("#ahistory").empty();
	$("#ahistory").append("已申请");
	var children = row.children;
	if(children != null && children.length > 0){
		for(var i = 0; i < children.length; i++){
			$("#ahistory").append("--->").append(children[i].memo);
		}
		if(row.apstatus == 1){
			$("#ahistory").append("--->渠道待审");
		}else if(row.apstatus == 2){
			$("#ahistory").append("--->区总待审");
		}else if(row.apstatus == 4){
			$("#ahistory").append("--->运营待审");
		}
	}else{
		if(row.apstatus == 1){
			$("#ahistory").append("--->渠道待审");
		}else if(row.apstatus == 2){
			$("#ahistory").append("--->区总待审");
		}else if(row.apstatus == 4){
			$("#ahistory").append("--->运营待审");
		}
	}
}

