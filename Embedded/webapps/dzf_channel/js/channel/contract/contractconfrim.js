 var contextPath = DZF.contextPath;
var grid,gridh;
//var loadrows = null;
//var isenter = false;//是否快速查询

$(function() {
	initQryDlg();
	initQueryData();
	initQryLitener();
	initChannel();
	initCorpk();
	$('#corpkna_ae').textbox('readonly',true);
	initManagerRef();
	load();
//	fastQry();
	initArea();
	$('#confreason').textbox('textbox').attr('maxlength', 200);
	loadJumpData();
});

/**
 * 查询框监听事件
 */
function initQryLitener(){
	$("#begdate").datebox("readonly", false);
	$("#enddate").datebox("readonly", false);
	$('#bperiod').textbox({"readonly" : true});
	$('#eperiod').textbox({"readonly" : true});
    $('input:radio[name="seledate"]').change( function(){  
		var ischeck = $('#da').is(':checked');
		if(ischeck){
			var sdv = $('#begdate').datebox('getValue');
			var edv = $('#enddate').datebox('getValue');
			$('#jqj').html(sdv + ' 至 ' + edv);
			$("#begdate").datebox("readonly", false);
			$("#enddate").datebox("readonly", false);
			$('#bperiod').textbox({"readonly" : true});
			$('#eperiod').textbox({"readonly" : true});
		}else{
			var sdv = $("#bperiod").val();
			var edv = $("#eperiod").val();
			$('#jqj').html(sdv + ' 至 ' + edv);
			$("#begdate").datebox("readonly", true);
			$("#enddate").datebox("readonly", true);
			$('#bperiod').textbox({"readonly" : false});
			$('#eperiod').textbox({"readonly" : false});
		}
	});
}

/**
 * 渠道经理参照初始化
 */
function initManagerRef(){
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
                    buttons: '#manBtn'
                });
            }
        }]
    });
}

/**
 * 渠道经理选择事件
 */
function selectMans(){
	var rows = $('#mgrid').datagrid('getSelections');
	dClickMans(rows);
}

/**
 * 双击选择渠道经理
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
 * 由别的界面（付款单余额明细）跳转待合同审核界面
 */
function loadJumpData(){
	
	var obj = Public.getRequest();
	var operate = obj.operate;
	if(operate == "tocont"){
		var id = obj.pk_billid;
		$('#grid').datagrid('unselectAll');
		var queryParams = $('#grid').datagrid('options').queryParams;
		$('#grid').datagrid('options').url =contextPath + '/contract/contractconf!query.action';
		queryParams.begdate = null;
		queryParams.enddate = null;
		queryParams.qtype = -1;
		queryParams.id = id;
		$('#grid').datagrid('options').queryParams = queryParams;
		$('#grid').datagrid('reload');
	}else if(operate == "toYnt"){//ynt_contract 主键
		var contractid = obj.contractid;
		$('#grid').datagrid('unselectAll');
		var queryParams = $('#grid').datagrid('options').queryParams;
		$('#grid').datagrid('options').url =contextPath + '/contract/contractconf!query.action';
		queryParams.begdate = null;
		queryParams.enddate = null;
		queryParams.qtype = -1;
		queryParams.contractid = contractid;
		$('#grid').datagrid('options').queryParams = queryParams;
		$('#grid').datagrid('reload');
	}
}

/**
 * 监听查询
 */
function initQueryData(){
	$("#querydate").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	$('#querydate').html(parent.SYSTEM.PreDate + ' 至 ' + parent.SYSTEM.LoginDate);
	$("#bdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#edate").datebox("setValue", parent.SYSTEM.LoginDate);
	
	$("#bperiod").datebox("setValue", parent.SYSTEM.PreDate);
	$("#eperiod").datebox("setValue", parent.SYSTEM.LoginDate);
}

/**
 * 管理查询对话框
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 加盟商参照初始化
 */
function initChannel(){
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
}

/**
 * 双击选择加盟商
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

function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}

/**
 * 查询框-清除
 */
function clearParams(){
	$('#corpkna_ae').combobox('readonly',true);
	$("#pk_account").val(null);
	$('#aname').combobox('setValue', null);
	$("#channel_select").textbox("setValue",null);
	$("#corpkid_ae").val(null);
	$("#corpkna_ae").textbox("setValue",null);
	$("#manager").textbox("setValue",null);
	$("#managerid").val(null);
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

/**
 * 查询
 */
function reloadData(){
	var bdate = null;//提单开始日期
	var edate = null;//提单结束日期
	var bperiod = null;//扣款开始日期
	var eperiod = null;//扣款结束日期
	var ischeck = $('#tddate').is(':checked');
	if(ischeck){
		bdate = $('#bdate').datebox('getValue'); 
		edate = $('#edate').datebox('getValue'); 
		if(isEmpty(bdate)){
			Public.tips({
				content : '提单开始日期不能为空',
				type : 2
			});
			return;
		}
		if(isEmpty(edate)){
			Public.tips({
				content : '提单结束日期不能为空',
				type : 2
			});
			return;
		}
		if(!isEmpty(bdate) && !isEmpty(edate)){
			if(!checkdate1("bdate","edate")){
				return;
			}		
		}
	}else{
		bperiod = $('#bperiod').datebox('getValue');
		eperiod = $('#eperiod').datebox('getValue');
		if(isEmpty(bperiod)){
			Public.tips({
				content : '扣款开始日期不能为空',
				type : 2
			});
			return;
		}
		if(isEmpty(eperiod)){
			Public.tips({
				content : '扣款结束日期不能为空',
				type : 2
			});
			return;
		}
		if(!isEmpty(bperiod) && !isEmpty(eperiod)){
			if(!checkdate1("bperiod","eperiod")){
				return;
			}		
		}
	}
	
	var queryParams = $('#grid').datagrid('options').queryParams;
	clearQryParam(queryParams);
	$('#grid').datagrid('options').url =contextPath + '/contract/contractconf!query.action';
	if ($("#normal").is(':checked') && !$("#supple").is(':checked')) {
		queryParams.qtype = 1;
	} else if(!$("#normal").is(':checked') && $("#supple").is(':checked')) {
		queryParams.qtype = 2;
	} else if(!$("#normal").is(':checked') && !$("#supple").is(':checked')) {
		$('#grid').datagrid('loadData',{ total:0, rows:[]});
	    $('#qrydialog').hide();
	    $('#grid').datagrid('unselectAll');
		return;
	}
	queryParams.begdate = bdate;
	queryParams.enddate = edate;
	queryParams.bperiod = bperiod;
	queryParams.eperiod = eperiod;
	queryParams.destatus = $('#destatus').combobox('getValue');
	var isncust = $('#isncust').combobox('getValue');
	if(!isEmpty(isncust)){
		queryParams.isncust = isncust;
	}else{
		 delete queryParams.isncust;
	}
	queryParams.cpid = $("#pk_account").val();
	queryParams.cpkid = $("#corpkid_ae").val();
	queryParams.mid = $("#managerid").val();
	queryParams.corptype = $('#corptype').combobox('getValue');
	queryParams.aname = $("#aname").combobox('getValue');
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
	if(ischeck){
		$('#querydate').html(bdate + ' 至 ' + edate);
	}else{
		$('#querydate').html(bperiod + ' 至 ' + eperiod);
	}
    $('#qrydialog').hide();
    $('#grid').datagrid('unselectAll');
}

/**
 * 清除查询传递查询条件
 * @param queryParams
 */
function clearQryParam(queryParams){
	queryParams.begdate = null;//提单开始日期
	queryParams.enddate = null;//提单结束日期
	queryParams.bperiod = null;//扣款开始日期
	queryParams.eperiod = null;//扣款结束日期
	queryParams.qtype = -1;
	queryParams.destatus = -1;
	delete queryParams.isncust;
	queryParams.cpid = null;
	queryParams.cpkid = null;
	queryParams.id = null;
	queryParams.cpname = null;
	queryParams.mid = null;
}

/**
 * 列表grid初始化
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
		showFooter:true,
		remoteSort : false,//定义从服务器排序
		sortName:"submitime",//排序字段
		sortOrder:"desc",//排序方式
		columns : [ [ {
			field : 'ck',
			checkbox : true
		},{
			width : '100',
			title : '主键',
			field : 'id',
			hidden : true
		}, {
			width : '100',
			title : '合同主键',
			field : 'contractid',
			hidden : true
		}, {
			width : '100',
			title : '加盟商主键',
			field : 'corpid',
			hidden : true
		}, {
			width : '100',
			title : '客户主键',
			field : 'corpkid',
			hidden : true
		}, {
			width : '100',
			title : '业务小类主键',
			field : 'typemin',
			hidden : true
		}, {
			width : '100',
			title : '时间戳',
			field : 'tstp',
			hidden : true
		}, {
			width : '100',
			title : '制单人',
			field : 'operatorid',
			hidden : true
		}, {
			width : '100',
			title : '套餐主键',
			field : 'pid',
			hidden : true
		}, {
			width : '100',
			title : '加盟商合同类型',
			field : 'pstatus',
			hidden : true
		}, {
			width : '100',
			title : '合同总金额',
			align:'right',
            halign:'center',
			field : 'ntlmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
//			sortable:true,
//			sorter:orderfun,
			hidden : true
		}, {
			width :'100',
			title : '存量客户',
			field : 'isncust',
			align : 'center',
			halign : 'center',
			formatter:isnformat,
		}, {
			width : '150',
			title : '提交时间',
			halign:'center',
			field : 'submitime',
			sortable:true,
		}, {
			width : '140',
			title : '大区',
			align : 'left',
            halign: 'center',
			field : 'aname'
		}, {
			width : '150',
			title : '地区',
			halign:'center',
			field : 'area',
		}, {
			width : '120',
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
			width : '100',
			title : '扣费日期',
			halign:'center',
			align:'center',
			field : 'dedate',
			sortable:true,
		}, {
			width : '120',
			title : '扣款比例(%)',
			halign:'center',
			align:'right',
			field : 'propor',
			sortable:true,
			sorter:orderfun,
		}, {
			width : '100',
			title : '扣费金额',
			align:'right',
            halign:'center',
			field : 'ndesummny',
			formatter : function(value,row,index){
				return formatMny(value);
			},
//			sortable:true,
//			sorter:orderfun,
			hidden : true
		}, {
			width : '100',
			title : '预付款扣款',
			align:'right',
            halign:'center',
			field : 'ndemny',
			formatter : function(value,row,index){
				return formatMny(value);
			},
			sortable:true,
			sorter:orderfun,
		}, {
			width : '100',
			title : '返点扣款',
			align:'right',
            halign:'center',
			field : 'nderebmny',
			formatter : function(value,row,index){
				return formatMny(value);
			},
			sortable:true,
			sorter:orderfun,
		}, {
			width : '100',
			title : '合同状态',
            halign:'center',
			field : 'destatus',
			align:'center',
			formatter : function(value) {
				if (value == '5')
					return '待审核';
				if (value == '1')
					return '已审核';
				if (value == '7')
					return '已驳回';
				if (value == '8')
					return '服务到期';
				if (value == '9')
					return '已终止';
				if (value == '10')
					return '已作废';
			}
		}, {
			width : '140',
			title : '驳回原因',
			field : 'confreason',
			formatter:reaFormat,
		}, {
			width : '140',
			title : '合同编码',
			halign:'center',
			field : 'vccode',
			formatter:codeLink,
		}, {
			width : '110',
			title : '合同代账费',//合同代账费 = 合同总金额 - 合同账本费
			align:'right',
            halign:'center',
			field : 'naccmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
			sortable:true,
			sorter:orderfun,
		}, {
			width : '110',
			title : '账本费',
			align:'right',
            halign:'center',
			field : 'nbmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
			sortable:true,
			sorter:orderfun,
		}, {
			width : '100',
			title : '附件',
			halign:'center',
			align:'center',
			field : 'contdoc',
			formatter : formatDocLink
		}, {
			width : '100',
			title : '未确定期限',
			halign:'center',
			align:'center',
			field : 'isnconfirm',
			formatter:isnformat,
		}, {
			width : '120',
			title : '开始日期',
			halign:'center',
			align:'center',
			field : 'bperiod',
			sortable:true,
		}, {
			width : '120',
			title : '结束日期',
			halign:'center',
			align:'center',
			field : 'eperiod',
			sortable:true,
		}, {
			width : '120',
			title : '收款周期(月)',
			align : 'center',
			halign : 'center',
			field : 'recycle',
			sortable:true,
			sorter:orderfun,
		}, {
			width : '120',
			title : '合同周期(月)',
			align : 'center',
			halign : 'center',
			field : 'contcycle',
			sortable:true,
			sorter:orderfun,
		}, {
			width : '120',
			title : '纳税人资格',
			halign:'center',
			field : 'chname',
		}, {
			width : '100',
			title : '月服务费',
			align:'right',
            halign:'center',
			field : 'nmsmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
			sortable:true,
			sorter:orderfun,
		}, {
			width : '100',
			title : '业务类型',
            halign:'center',
			field : 'typeminm',
		}, {
			width : '120',
			title : '作废/终止日期',
			halign:'center',
			align:'center',
			field : 'changedate',
			sortable:true,
		}, {
			width : '100',
			title : '销售顾问',
			field : 'adviser',
		}, {
			width : '100',
			title : '经办人',
			field : 'vopernm',
		},  ] ],
		onLoadSuccess : function(data) {
            parent.$.messager.progress('close');
			calFooter();
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

/**
 * 查询大区初始化
 */
function initArea(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryArea.action',
		data : {"qtype" :3},
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#aname').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

/**
 * 驳回原因添加tips显示
 * @param value
 */
function reaFormat(value){
	if(value != undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}
}

/**
 * 计算合计
 */
function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
    var ntlmny = 0;	
    var nmsmny = 0;	
//    var ndesummny = 0;	
    var ndemny = 0;
    var nderebmny = 0;
    var naccmny = 0;
    var nbmny = 0;
    for (var i = 0; i < rows.length; i++) {
    	ntlmny += getFloatValue(rows[i].ntlmny);
    	nmsmny += getFloatValue(rows[i].nmsmny);
//    	ndesummny += getFloatValue(rows[i].ndesummny);
    	ndemny += getFloatValue(rows[i].ndemny);
    	nderebmny += getFloatValue(rows[i].nderebmny);
    	naccmny += getFloatValue(rows[i].naccmny);
    	nbmny += getFloatValue(rows[i].nbmny);
    }
    footerData['corpnm'] = '合计';
    footerData['ntlmny'] = ntlmny;
    footerData['nmsmny'] = nmsmny;
//    footerData['ndesummny'] = ndesummny;
    footerData['ndemny'] = ndemny;
    footerData['nderebmny'] = nderebmny;
    footerData['naccmny'] = naccmny;
    footerData['nbmny'] = nbmny;
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}


/**
 * 存量客户格式化
 * @param value
 * @returns {String}
 */
function isnformat(value,row){
	if(!isEmpty(row.contractid)){
		if(value&&(value=='Y'||value=="是")){
			return "<input type=\"checkbox\" checked=\"checked\" onclick=\"return false;\" >";
		}else{
			return "<input type=\"checkbox\" onclick=\"return false;\" >";
		}
	}
}

/**
 * 超级链接-穿透查询
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function formatDocLink(val,row,index){  
	if(!isEmpty(row.contractid)){
		return '<a href="#" style="color:blue" onclick="viewattach('+index+')">合同附件</a>';  
	}
}

/**
 * 标签查询
 * @param type  1：待审核；2：存量待审；
 */
function qryData(type){
	$('#grid').datagrid('unselectAll');
	var queryParams = $('#grid').datagrid('options').queryParams;
	clearQryParam(queryParams);
	if(type == 2){
		queryParams.isncust = "Y";
	}
	queryParams.destatus = 5;
	grid.datagrid('options').url =contextPath + '/contract/contractconf!query.action';
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
}

///**
// * 快速过滤
// */
//function fastQry(){
//	$('#filter_value').textbox('textbox').keydown(function (e) {
//		 if (e.keyCode == 13) {
//            var filtername = $("#filter_value").val(); 
//            if(!isEmpty(filtername)){
//            	var queryParams = $('#grid').datagrid('options').queryParams;
//            	var rows = $('#grid').datagrid('getRows');
//            	clearQryParam(queryParams);
//            	if(rows != null && rows.length > 0){
//            		//做过查询
//            		queryParams.destatus = $('#destatus').combobox('getValue');
//            		var isncust=$('#isncust').combobox('getValue');
//            		if(!isEmpty(isncust)){
//            			queryParams.isncust = isncust;
//            		}
//            		queryParams.cpid = $("#pk_account").val();
//            		queryParams.cpkid = $("#corpkid_ae").val();
//            		if ($("#normal").is(':checked') && !$("#supple").is(':checked')) {
//            			queryParams.qtype = 1;
//            		} else if(!$("#normal").is(':checked') && $("#supple").is(':checked')) {
//            			queryParams.qtype = 2;
//            		} else if(!$("#normal").is(':checked') && !$("#supple").is(':checked')) {
//            			$('#grid').datagrid('loadData',{ total:0, rows:[]});
//            		    $('#qrydialog').hide();
//            		    $('#grid').datagrid('unselectAll');
//            			return;
//            		}
//            	}
//            	var ischeck = $('#tddate').is(':checked');
//            	if(ischeck){
//            		queryParams.begdate = $('#bdate').datebox('getValue'); //开始日期
//            		queryParams.enddate = $('#edate').datebox('getValue'); //结束日期
//            	}else{
//            		queryParams.bperiod = $('#bperiod').datebox('getValue');
//            		queryParams.eperiod = $('#eperiod').datebox('getValue');
//            	}
//            	queryParams.cpname = filtername;
//          		grid.datagrid('options').url = contextPath + '/contract/contractconf!query.action';
//          		$('#grid').datagrid('options').queryParams = queryParams;
//          		$('#grid').datagrid('reload');
//            }
//         }
//   });
//}

/**
 * 单条审核
 */
function audit(){
	var rows = $('#grid').datagrid('getSelections');
	if (rows == null || rows.length != 1) {
		Public.tips({
			content : '请选择一行数据',
			type : 2
		});			
		return;
	}
	if(rows[0].destatus != 5){
		Public.tips({
			content : '合同状态不为待审核',
			type : 2
		});			
		return;
	}
	var title = "";
	if(rows[0].pstatus == 2){
		title = "合同审核-小规模转一般人";
    }else if(rows[0].pstatus == 5){
    	title = "合同审核-一般人转小规模";
    }else{
    	title = "合同审核";
    }
	$('#deduct_Dialog').dialog({ modal:true });//设置dig属性
	$('#deduct_Dialog').dialog('open').dialog('center').dialog('setTitle',title);
	
	$('#nbook').css('display','none');
	if(rows[0].pstatus != 5){
		$('#nbook').css('display','inline-block');
	}
	
	$("#fileshow").hide();
	initListener();//初始化扣款比例监听
	$('#deductfrom').form('clear');
	if(rows[0].pstatus == 2 || rows[0].pstatus == 5){
    	$("#issupple").show();
    }else{
    	$("#issupple").hide();
    }
	initdeductData(rows[0]);//初始化扣款数据
	initFileDoc(rows[0]);//初始化附件
	initRejectReason();
}

/**
 * 展示驳回原因
 */
function showRejectReason(row){
	var rejesons = row.children;
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
		showinfo = showinfo + "      <font>"+rejesons[0].updatets+"</font> &emsp;<span>"+rejesons[0].reason+"</span>"; 
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
				showinfo = showinfo + "      <font>"+rejesons[i].updatets+"</font>&emsp;<span>"+rejesons[i].reason+"</span>"; 
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
 * 初始化监听
 */
function initListener(){
	$("#propor").numberbox({
		onChange : function(n, o) {
			if (isEmpty(n)){
				n = 0; 
			}
			var ntlmny = $('#ntlmny').numberbox('getValue');//合同金额
			var nbmny = $('#nbmny').numberbox('getValue');//账本费
			 //扣费标准修改为扣掉账本费的合同金额
            var countmny = getFloatValue(ntlmny).sub(getFloatValue(nbmny));
            var ndesummny = getFloatValue(countmny).mul(parseFloat(n)).div(100);
			$('#ndesummny').numberbox('setValue', ndesummny);
		}
	});
	
	$("#confreason").textbox('readonly',true);
	$(":radio").click( function(){
		var opertype = $('input:radio[name="opertype"]:checked').val();
		if(opertype == 1){
			$("#confreason").textbox('readonly',true);
			$("#confreason").textbox('setValue',null);
			$("#confreasonid").val(null);
		}else if(opertype == 2){
			$("#confreason").textbox('readonly',false);
		}
	});
}

/**
 * 初始化扣款数据
 */
function initdeductData(row){
	$.ajax({
        type: "post",
        dataType: "json",
        url: contextPath + '/contract/contractconf!queryDeductData.action',
        data : {
        	contractid : row.contractid,
        	corpid : row.corpid,
        	operatorid : row.operatorid,
        	corpkid : row.corpkid,
        	pid : row.pid,
		},
        traditional: true,
        async: false,
        success: function(data, textStatus) {
            if (!data.success) {
            	Public.tips({content:data.msg,type:1});
            } else {
                var row = data.rows;
                $('#deductfrom').form('clear');
                $('#deductfrom').form('load',row);
                $('#confreason').textbox('setValue', null);//驳回原因
                if(row.canedit == "Y" || row.canedit == "是"){
                	$('#propor').numberbox({"readonly" : true});
                }else{
                	$('#propor').numberbox({"readonly" : false});
                }
                $('#propor').numberbox('setValue', row.propor);
                //扣费标准修改为扣掉账本费的合同金额
                var countmny = getFloatValue(row.ntlmny).sub(getFloatValue(row.nbmny));
                var ndesummny = getFloatValue(countmny).mul(parseFloat(row.propor)).div(100);
                $('#ndesummny').numberbox('setValue', ndesummny);
                $("#dedate").datebox("setValue",Public.getLoginDate());
                $('#vopernm').textbox('setValue',$("#unm").val());
                $('#voper').val($("#uid").val());
                $('#balmny').numberbox('setValue', row.balmny);//预付款余额
                $('#rebbalmny').numberbox('setValue', row.rebbalmny);//返点余额
                $('#corpnm').textbox('setValue', row.corpnm);//渠道商
                $('#hntlmny').numberbox('setValue', row.ntlmny);//合同金额
                $('#contractid').val(row.contractid);//合同主键
                $('#salespromot').textbox('setValue', row.salespromot);//促销活动
                $('#scperiod').textbox('setValue', row.cperiod);//变更日期
                $('#corptp').textbox('setValue', row.corptp);//加盟商类型
                document.getElementById("debit").checked="true";
                $('#rejereson').css('display','none');
                if(!isEmpty(row.confreason)){
                	$('#rejereson').css('display','block');
                	showRejectReason(row);
                }
                if(row.isnconfirm == "Y" || row.isnconfirm == "是"){
                	$('#isnconfirm').prop('checked',true);
                }
            }
        },
    });
}

/**
 * 初始化附件
 * @param row
 */
function initFileDoc(row){
	var para = {};
	para.corp_id = row.corpid;
	para.c_id = row.contractid;
	
	$.ajax({
		type : "POST",
		url : DZF.contextPath + "/contract/contractconf!getAttaches.action",
  		dataType : 'json',
  		data : para,
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var rows = result.rows;
			if(rows != null && rows.length > 0){
				$("#fileshow").show();
				arrachrows = result.rows;
				$("#filedocs").html('');
				for(var i = 0;i<rows.length;i++){
					var srcpath = rows[i].fpath.replace(/\\/g, "/");
					var attachImgUrl = getAttachImgUrl(rows[i]);
					$('<li><a href="javascript:void(0)"  onmouseover="showTips(' + i + ')"  '+
							'onmouseout="hideTips(' + i + ')"  ondblclick="doubleImage(\'' + i + '\');" ><span><img src="' +attachImgUrl +  '" />'+
							'<div id="reUpload' + i +'" style="width: 60%; height: 25px; position: absolute; top: 105px; left: 0px; display:none;">'+
							'<h4><span id="tips'+ i +'"></span></h4></div></span>'+
							'<font>' + 	rows[i].doc_name + '</font></a></li>').appendTo($("#filedocs"));
				}
			}
		}
	});
	
}

/**
 * 单个审核-确认
 */
function deductConfri(){
	var row = $('#grid').datagrid('getSelected');
	if(row == null){
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});			
		return;
	}
	if(row.destatus != 5){
		Public.tips({
			content : '合同状态不为待审核',
			type : 2
		});			
		return;
	}
	var opertype = $('input:radio[name="opertype"]:checked').val();
	if(isEmpty(opertype)){
		Public.tips({
			content : '请先选择操作方式',
			type : 2
		});			
		return;
	}
	var propor = $('#propor').numberbox('getValue');
	if(isEmpty(propor)){
		Public.tips({
			content : '扣款比例不能为空',
			type : 2
		});			
		return;
	}/*else if(propor == 0){
		Public.tips({
			content : '扣款比例不能为0',
			type : 2
		});			
		return;
	}*/
	
	var postdata = new Object();
	postdata["head"] = JSON.stringify(serializeObject($('#deductfrom')));
	postdata["opertype"] = opertype;
	
	$.messager.progress({
		text : '数据保存中，请稍后.....'
	});
	$('#deductfrom').form('submit', {
		url : contextPath + '/contract/contractconf!updateDeductData.action',
		queryParams : postdata,
		success : function(result) {
			var result = eval('(' + result + ')');
			$.messager.progress('close');
			if (result.success) {
				
				Public.tips({
					content : result.msg,
					type : 0
				});
				$('#deduct_Dialog').dialog('close');
				var rerow = result.rows;
				var index = $("#grid").datagrid("getRowIndex",row);
				$('#grid').datagrid('updateRow',{
					index: index,
					row : rerow
				});
				$("#grid").datagrid('uncheckAll');
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.messager.progress('close');
		}
	});
}

/**
 * 审核-取消
 */
function deductCancel(){
	$('#deduct_Dialog').dialog('close');
}

/**
 * 批量审核
 */
function bathAudit(){
	var rows = $('#grid').datagrid('getSelections');
	if (rows == null) {
		Public.tips({
			content : "请选择需要处理的数据",
			type : 2
		});
		return;
	}
	
	$('#bdeduct_Dialog').dialog({ modal:true });//设置dig属性
	$('#bdeduct_Dialog').dialog('open').dialog('center').dialog('setTitle','扣款');
	$('#bdeductfrom').form('clear');
	$('#bpropor').numberbox('setValue', 10);
	$("#bdedate").datebox("setValue",Public.getLoginDate());
	$('#bvopernm').textbox('setValue',$("#unm").val());
	$('#bvoper').val($("#uid").val());
	document.getElementById("bdebit").checked="true";
	initRedioListener();
	initRejectReason();
}

/**
 * 单选按钮初始化
 */
function initRedioListener(){
	$(":radio").click( function(){
		var opertype = $('input:radio[name="bopertype"]:checked').val();
		if(opertype == 1){
			$("#bconfreason").textbox('readonly',true);
			$("#bconfreason").textbox('setValue',null);
			$("#bconfreasonid").val(null);
		}else if(opertype == 2){
			$("#bconfreason").textbox('readonly',false);
		}
	});
}

/**
 * 批量审核-确认
 */
function bathconf(){
	var rows = $('#grid').datagrid('getSelections');
	if (rows == null) {
		Public.tips({
			content : "请选择需要处理的数据",
			type : 2
		});
		return;
	}
	
	var opertype = $('input:radio[name="bopertype"]:checked').val();
	if(isEmpty(opertype)){
		Public.tips({
			content : '请先选择操作方式',
			type : 2
		});			
		return;
	}
	
	var propor = $('#bpropor').numberbox('getValue');
	if(isEmpty(propor)){
		Public.tips({
			content : '扣款比例不能为空',
			type : 2
		});			
		return;
	}/*else if(propor == 0){
		Public.tips({
			content : '扣款比例不能为0',
			type : 2
		});			
		return;
	}*/
	
	var contract = '';
	if (rows != null && rows.length > 0) {
		for (var i = 0; i < rows.length; i++) {
			contract = contract + JSON.stringify(rows[i]);
		}
	}
	var postdata = new Object();
	postdata["head"] = JSON.stringify(serializeObject($('#bdeductfrom')));
	postdata["contract"] = contract;
	postdata["opertype"] = opertype;
	bathconfrim(postdata, rows);
}

/**
 * 批量审核-确认操作
 * @param postdata
 */
function bathconfrim(postdata, rows){
	$.messager.progress({
		text : '数据审核中....'
	});
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/contract/contractconf!bathconfrim.action',
		data : postdata,
		traditional : true,
		success : function(result) {
			$.messager.progress('close');
			if (!result.success) {
				Public.tips({
					content : result.msg,
					type : 1
				});
				isclose = true;
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
				$('#bdeduct_Dialog').dialog('close');
				var rerows = result.rows;
				if(rerows != null && rerows.length > 0){
					var map = new HashMap(); 
					for(var i = 0; i < rerows.length; i++){
						map.put(rerows[i].contractid,rerows[i]);
					}
					var index;
					var indexes = new Array();
					for(var i = 0; i < rows.length; i++){
						if(map.containsKey(rows[i].contractid)){
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
				isclose = true;
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.messager.progress('close');
		}
	});

}

/**
 * 批量审核-取消
 */
function bathcanc(){
	$('#bdeduct_Dialog').dialog('close');
}

/**
 * 合同变更
 */
function change(){
	var rows = $('#grid').datagrid('getSelections');
	if (rows == null || rows.length != 1) {
		Public.tips({
			content : '请选择一行数据',
			type : 2
		});			
		return;
	}
	if(rows[0].destatus != 1){
		Public.tips({
			content : '合同状态不为审核通过',
			type : 2
		});			
		return;
	}
	if(rows[0].pstatus == 1 || rows[0].pstatus == 4){
		Public.tips({
			content : '该合同纳税人资格已变更，不允许变更',
			type : 2
		});			
		return;
	}
	if(rows[0].pstatus == 2 || rows[0].pstatus == 5){
		Public.tips({
			content : '该合同为纳税人资格变更单，不允许变更',
			type : 2
		});			
		return;
	}
	if(rows[0].pstatus == 3){
		Public.tips({
			content : '该合同已变更，不允许再次变更',
			type : 2
		});			
		return;
	}
	initPeriod("#stperiod");//变更期间初始化
	$('#change_Dialog').dialog({ modal:true });//设置dig属性
	$('#change_Dialog').dialog('open').dialog('center').dialog('setTitle','合同变更');
	$("#sfileshow").hide();
	$('#changefrom').form('load',rows[0]);
	$('#changememo').textbox('setValue',null);
	initChangeFileDoc(rows[0]);//初始化变更合同附件
	initFileEvent();
	initChangeListener();
	$("#end").prop({"disabled":false});
	if(rows[0].isnconfirm == "Y" || rows[0].isnconfirm == "是"){
		document.getElementById("nullify").checked="true";
		$("#end").prop({"disabled":true});
		$('#cisnconfirm').prop('checked',true);
		setChangeMny(2);
		
		$("#addclass").attr("class", "decan");
		$("#stperiod").datebox("readonly", true);
		$("#remny").numberbox("readonly", true);
		$("#nchtlmny").numberbox("readonly", true);
		$("#nchsumny").numberbox("readonly", true);
		$("#changetype").val(2);
	}else{
		document.getElementById("end").checked="true";
		$('#cisnconfirm').prop('checked',false);
		setChangeMny(1);
		
		$("#addclass").removeClass("decan");
		$("#stperiod").datebox("readonly", false);
		$("#remny").numberbox("readonly", false);
		$("#nchtlmny").numberbox("readonly", false);
		$("#nchsumny").numberbox("readonly", false);
		$("#changetype").val(1);
	}
}



/**
 * 变更原因改变事件
 */
function initChangeListener(){
	$("#addclass").removeClass("decan");
	//变更原因监听事件
	$(":radio").click( function(){
		var opertype = $('input:radio[name="chtype"]:checked').val();
		setChangeMny(opertype);
		if(opertype == 1){
			$("#addclass").removeClass("decan");
			$("#stperiod").datebox("readonly", false);
			$("#remny").numberbox("readonly", false);
			$("#nchtlmny").numberbox("readonly", false);
			$("#nchsumny").numberbox("readonly", false);
			$("#changetype").val(1);
		}else if(opertype == 2){
			$("#addclass").attr("class", "decan");
			$("#stperiod").datebox("readonly", true);
			$("#remny").numberbox("readonly", true);
			$("#nchtlmny").numberbox("readonly", true);
			$("#nchsumny").numberbox("readonly", true);
			$("#changetype").val(2);
		}
	});
	
	//终止期间监听事件
	$("#stperiod").datebox({
		onChange : function(n, o) {
			if(o == "" || o == null){
				return;
			}
			var sbperiod = $("#sbperiod").val();//开始期间
			var seperiod = $("#seperiod").val();//结束期间
			if(n < sbperiod || n > seperiod){
				$('#stperiod').textbox('setValue', o);
				Public.tips({
					content : '终止期间不能在服务期间之外，请重新选择终止期间',
					type : 2
				});			
				return;
			}
			var sntlmny = getFloatValue($('#sntlmny').numberbox('getValue'));//原合同金额
			var sndesummny = getFloatValue($('#sndesummny').numberbox('getValue'));//原扣款金额
			var cnum = getMonthNum(n, sbperiod)+1;//变更期数
			var srecycle = getFloatValue($("#srecycle").val());//原收款周期
			//退回总扣款 = 退回总扣款 - （退回总扣款/原收款周期*新收款周期）
			var remny = sndesummny.sub(sndesummny.div(srecycle).mul(cnum));
			if(getFloatValue(remny) < getFloatValue(0)){
				remny = getFloatValue(0);
			}
			$('#remny').numberbox('setValue', remny);//退回扣款
			var snmsmny = getFloatValue($('#snmsmny').numberbox('getValue'));//原月代账费
			var snbmny = getFloatValue($('#snbmny').numberbox('getValue'));//账本费
			var nchtlmny = snmsmny.mul(cnum).add(snbmny);
			if(getFloatValue(remny) == getFloatValue(0)){
				$('#nchtlmny').numberbox('setValue', sntlmny);//变更后合同金额 = 原合同金额
			}else{
				$('#nchtlmny').numberbox('setValue', nchtlmny);//变更后合同金额 = 原月代账费 * （原开始期间到终止期间的期数）+ 账本费
			}
			var nchsumny = sndesummny.sub(remny);
			$('#nchsumny').numberbox('setValue', nchsumny);//变更后扣款金额 = 原扣款金额 - 退回扣款金额
		}
	});
}

/**
 * 设置变更金额
 * @param opertype
 */
function setChangeMny(opertype){
	$('#stperiod').textbox('setValue', $("#period").val());
	var sndesummny = getFloatValue($('#sndesummny').numberbox('getValue'));//原扣款金额
	if(opertype == 1){//终止
		var sntlmny = getFloatValue($('#sntlmny').numberbox('getValue'));//原合同金额
		//退回扣款 = （原扣款金额/原收款期间）*（原开始期间到终止期间的期数）
		//变更后合同金额 = 原月代账费 *（原开始期间到终止期间的期数）+ 账本费
		//变更后扣款金额 = 原扣款金额-退回扣款
		var sbperiod = $("#sbperiod").val();//开始期间
		var stperiod = $("#stperiod").datebox('getValue');//变更期间
		var cnum = getMonthNum(stperiod, sbperiod)+1;//变更期数
		var srecycle = getFloatValue($("#srecycle").val());//原收款周期
		//退回扣款算法：原扣款金额-{（原扣款金额/原收款期间）*（原开始期间到终止期间的期数）}
		var remny = sndesummny.sub(sndesummny.div(srecycle).mul(cnum));
		if(getFloatValue(remny) < getFloatValue(0)){
			remny = getFloatValue(0);
		}
		$('#remny').numberbox('setValue', remny);//退回扣款
		var snmsmny = getFloatValue($('#snmsmny').numberbox('getValue'));//原月代账费
		var snbmny = getFloatValue($('#snbmny').numberbox('getValue'));//账本费
		var nchtlmny = snmsmny.mul(cnum).add(snbmny);
		if(getFloatValue(remny) == getFloatValue(0)){
			$('#nchtlmny').numberbox('setValue', sntlmny);//变更后合同金额 = 原合同金额
		}else{
			$('#nchtlmny').numberbox('setValue', nchtlmny);//变更后合同金额 = 原月代账费 * （原开始期间到终止期间的期数）+ 账本费
		}
		var nchsumny = sndesummny.sub(remny);
		$('#nchsumny').numberbox('setValue', nchsumny);//变更后扣款金额 = 原扣款金额 - 退回扣款金额
	}else if(opertype == 2){//作废
		//退回扣款 = 原扣款金额
		//变更后合同金额 = 0
		//变更后扣款金额 = 0
		$('#remny').numberbox('setValue', sndesummny);
		$('#nchtlmny').numberbox('setValue', 0);
		$('#nchsumny').numberbox('setValue', 0);
	}
}

/**
 * 计算期间月份差的函数，通用  
 * @param begperiod  ****-**
 * @param endperiod  ****-**
 * @returns
 */
function getMonthNum(endperiod, begperiod){    
	begperiod = begperiod.split('-');
	// 得到月数
	begperiod = parseInt(begperiod[0]) * 12 + parseInt(begperiod[1]);
	// 拆分年月
	endperiod = endperiod.split('-');
	// 得到月数
	endperiod = parseInt(endperiod[0]) * 12 + parseInt(endperiod[1]);
	var m = Math.abs(endperiod - begperiod);
    return  m;  
}

/**
 * 初始化变更合同附件
 * @param row
 */
function initChangeFileDoc(row){
	var para = {};
	para.corp_id = row.corpid;
	para.c_id = row.contractid;
	
	$.ajax({
		type : "POST",
		url : DZF.contextPath + "/contract/contractconf!getAttaches.action",
  		dataType : 'json',
  		data : para,
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var rows = result.rows;
			if(rows != null && rows.length > 0){
				$("#sfileshow").show();
				arrachrows = result.rows;
				$("#sfiledocs").html('');
				for(var i = 0;i<rows.length;i++){
					var srcpath = rows[i].fpath.replace(/\\/g, "/");
					var attachImgUrl = getAttachImgUrl(rows[i]);
					$('<li><a href="javascript:void(0)"  onmouseover="showTips(' + i + ')"  '+
							'onmouseout="hideTips(' + i + ')"  ondblclick="doubleImage(\'' + i + '\');" ><span><img src="' +attachImgUrl +  '" />'+
							'<div id="reUpload' + i +'" style="width: 60%; height: 25px; position: absolute; top: 105px; left: 0px; display:none;">'+
							'<h4><span id="tips'+ i +'"></span></h4></div></span>'+
							'<font>' + 	rows[i].doc_name + '</font></a></li>').appendTo($("#sfiledocs"));
				}
			}
		}
	});
}

/**
 * 上传变更附件初始化
 */
function initFileEvent(){
	
	//上传附件
	$("#image1").html('');
	var htmlImg = '<div class="imgbox">'+
					'<div class="imgnum">'+
						'<input type="file" class="filepath1" name="imageFile" multiple="multiple" '+
						' accept="image/gif,image/jpeg,image/jpg,image/png"/>'+
						'<span class="close1"><img src="../../images/Dustbin.png"/></span>'+
						'<img src="../../images/wer_03.png" class="img1" /> ' +
						'<img src="" class="img2" />'+
					'</div>'+
				  '</div>';
	$("#image1").html(htmlImg);
	
	//改变上传附件
	$(".uploadImg").on("change",".filepath1",function(){
		if(this.files.length <= 0){
			return;
		}
		var fname = this.files[0].name;
		var imageExt=fname.substr(fname.lastIndexOf(".")).toLowerCase();//获得文件后缀名
	    if(imageExt !='.jpg' && imageExt !='.png' && imageExt !='.jpeg'){
	        Public.tips({ content : "请上传后缀名为jpg、png、jpeg的图片", type : 2 });
	        return;
	    }
		var srcs = getObjectURL(this.files[0]);   //获取路径
		var imgsrc = $(this).nextAll(".img2").attr("src"); 
	    //this指的是input
	    $(this).nextAll(".img1").hide();   //this指的是input
	    $(this).nextAll(".img2").show();  //fireBUg查看第二次换图片不起做用
	    $(this).nextAll('.close1').show();   //this指的是input
	    $(this).nextAll(".img2").attr("src",srcs);    //this指的是input
	    if(imgsrc == null || imgsrc == ""){
	    	 var htmlImg='<div class="imgbox">'+
				     		'<div class="imgnum">'+
				     			'<input type="file" class="filepath1" name="imageFile" multiple="multiple" '+
				     			' accept="image/gif,image/jpeg,image/jpg,image/png"/>'+
				     			'<span class="close1"><img height="26" src="../../images/Dustbin.png"/></span>'+
						            '<img src="../../images/wer_03.png" class="img1" />'+
						            '<img src="" class="img2" />'+
						        '</div>'+
				         '</div>';
	    	 $(this).parent().parent().after(htmlImg);
	    }
	    initClick();
	});
	
}

/**
 * 获取上传附件路径
 * @param file
 * @returns
 */
function getObjectURL(file) {
    var url = null;
	if (window.createObjectURL != undefined) {
		url = window.createObjectURL(file)
	} else if (window.URL != undefined) {
		url = window.URL.createObjectURL(file)
	} else if (window.webkitURL != undefined) {
		url = window.webkitURL.createObjectURL(file)
	}
    return url
};

/**
 * 删除点击事件
 */
function initClick(){
	$(".close1").on("click",function() {
    	if($(this).nextAll(".img2").attr("src")){
    		$(this).hide();     //this指的是span
    		$(this).nextAll(".img2").hide();
    		$(this).nextAll(".img1").show();
    		if($('.imgbox').length>1){
    			$(this).parent().parent().remove();
    		}
    	}
    })
}

/**
 * 变更保存
 */
function changeConfri(){
	var rows = $('#grid').datagrid('getSelections');
	if (rows == null || rows.length != 1) {
		Public.tips({
			content : '请选择一行数据',
			type : 2
		});			
		return;
	}
	var opertype = $('input:radio[name="chtype"]:checked').val();	
	if(opertype == 1){
		var stperiod = $("#stperiod").val();//终止期间
		var sbperiod = $("#sbperiod").val();//开始期间
		var seperiod = $("#seperiod").val();//结束期间
		if(stperiod < sbperiod || stperiod > seperiod){
			Public.tips({
				content : '终止期间不能在服务期间之外，请重新选择终止期间',
				type : 2
			});			
			return;
		}
	}
	parent.$.messager.progress({
		text : '变更中....'
	});
	$('#changefrom').form('submit', {
		url : contextPath + '/contract/contractconf!saveChange.action',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				Public.tips({
					content : result.msg,
					type : 0
				});
//				reloadData();
				$('#change_Dialog').dialog('close');
				var rerow = result.rows;
				var index = $("#grid").datagrid("getRowIndex",rows[0]);
				$('#grid').datagrid('updateRow',{
					index: index,
					row : rerow
				});
				$("#grid").datagrid('uncheckAll');
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
 * 变更取消
 */
function changeCancel(){
	$('#change_Dialog').dialog('close');
}

/**
 * 合同编码格式化
 * @param value
 * @param row
 * @param index
 */
function codeLink(value,row,index){
	if(!isEmpty(row.contractid)){
		return '<a href="javascript:void(0)" style="color:blue"  onclick="showInfo(' + index + ')">'+value+'</a>';
	}
}

/**
 * 合同明细查看
 * @param index
 */
function showInfo(index){
	var row = $('#grid').datagrid('getData').rows[index];
	$.ajax({
		url : DZF.contextPath + "/contract/contractconf!queryInfoById.action",
		dataType : 'json',
		data : row,
		success : function(rs) {
			if (rs.success) {
				var row = rs.rows;
				var changeshow = false;
				var dedshow = false;
				if(row.destatus == 9 || row.destatus == 10){
					changeshow = true;
					dedshow = true;
				}
				if(row.destatus == 1){
					dedshow = true;
				}
				if(changeshow){
					$('#changeinfo').css('height','auto');
					$('#changeinfo').css('display','block');
				}else{
					$('#changeinfo').css('display','none');
				}
				if(dedshow){
					$('#dedinfo').css('height','auto');
					$('#dedinfo').css('display','block');
				}else{
					$('#dedinfo').css('display','none');
				}
				
				$('#info_Dialog').dialog({ modal:true });//设置dig属性
				$('#info_Dialog').dialog('open').dialog('center').dialog('setTitle','合同详情');
				$('#infofrom').form('clear');
				$('#infofrom').form('load',row);
				initInfoFileDoc(row);
				$('#rejeson').css('display','none');
				if(!isEmpty(row.confreason)){
					$('#rejeson').css('display','block');
					showRejReason(row);
				}
			}
		}
		
	});
}

/**
 * 展示驳回原因-详情界面
 */
function showRejReason(row){
	var rejesons = row.children;
	$("#rejeson").empty();
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
		showinfo = showinfo + "      <font>"+rejesons[0].updatets+"</font> &emsp;<span>"+rejesons[0].reason+"</span>"; 
		showinfo = showinfo + "    </div>"; 
		showinfo = showinfo + "  </div>"; 
		showinfo = showinfo + "</div>";
		if(rejesons.length > 1){
			showinfo = showinfo + "<div style='display: none;' id='panel'>";
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
				showinfo = showinfo + "      <font>"+rejesons[i].updatets+"</font>&emsp;<span>"+rejesons[i].reason+"</span>"; 
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
		$("#rejeson").append(showinfo);
		actionListener();
	}
}

/**
 * 监听-详情
 */
function actionListener(){
	$(".btn-slide").click(function() {
		$("#panel").slideToggle("slow");
		$(this).toggleClass("active");
		return false;
	})
}

/**
 * 初始化变更合同附件
 * @param row
 */
function initInfoFileDoc(row){
	var para = {};
	para.corp_id = row.corpid;
	para.c_id = row.contractid;
	
	$.ajax({
		type : "POST",
		url : DZF.contextPath + "/contract/contractconf!getAttaches.action",
  		dataType : 'json',
  		data : para,
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var rows = result.rows;
			if(rows != null && rows.length > 0){
				$("#ifileshow").show();
				arrachrows = result.rows;
				$("#ifiledocs").html('');
				for(var i = 0;i<rows.length;i++){
					var srcpath = rows[i].fpath.replace(/\\/g, "/");
					var attachImgUrl = getAttachImgUrl(rows[i]);
					$('<li><a href="javascript:void(0)"  onmouseover="showTips(' + i + ')"  '+
							'onmouseout="hideTips(' + i + ')"  ondblclick="doubleImage(\'' + i + '\');" ><span><img src="' +attachImgUrl +  '" />'+
							'<div id="reUpload' + i +'" style="width: 60%; height: 25px; position: absolute; top: 105px; left: 0px; display:none;">'+
							'<h4><span id="tips'+ i +'"></span></h4></div></span>'+
							'<font>' + 	rows[i].doc_name + '</font></a></li>').appendTo($("#ifiledocs"));
				}
			}
		}
	});
}

/**
 * 驳回原因参照初始化
 */
var rejeid;
function initRejectReason(){
	$('#confreason,#bconfreason').textbox({
		editable : false,
		onClickIcon : function() {
			rejeid = $(this).attr("id");
		}
	}).textbox({
		icons : [ {
			iconCls : 'icon-search',
			handler : function(e) {
				$("#manDlg").dialog('clear');
				$("#rejeDlg").dialog({
					width : 1000,
					height : 480,
					readonly : true,
					title : '选择原因',
					modal : true,
					href : contextPath+ '/ref/rejectreason_select.jsp',
					buttons : [{text : '确认',
								handler : function() {
									var rows = $('#rgrid').datagrid('getSelections');
									dClickReje(rows);
								}
								},
							{text : '取消',
								handler : function() {
									$('#rejeDlg').dialog('close');
								}
								} ]
				});
			}
		} ]
});
}

/**
 * 双击选择驳回原因
 * @param rowTable
 */
function dClickReje(rowTable){
	var reasons = "";
	var rids = [];
	if(rowTable){
//		if (rowTable.length > 300) {
//			Public.tips({
//				content : "一次最多只能选择300个经理",
//				type : 2
//			});
//			return;
//		}
		for(var i = 0; i<rowTable.length; i++){
			if(i == rowTable.length - 1){
				reasons += rowTable[i].reason;
			}else{
				reasons += rowTable[i].reason+"；";
			}
			rids.push(rowTable[i].reid);
		}
		$("#" + rejeid).textbox("setValue",reasons);
		$("#" + rejeid + "id").val(rids);
	}
	$("#rejeDlg").dialog('close');
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
	var callback=function(){
		var columns = $('#grid').datagrid("options").columns[0];
    	Business.getFile(DZF.contextPath+ '/contract/contractconf!onExport.action',
    			{'strlist':JSON.stringify(datarows),'qj' : $('#querydate').html()}, true, true);
	}
	checkBtnPower('export','channel4',callback);
}
