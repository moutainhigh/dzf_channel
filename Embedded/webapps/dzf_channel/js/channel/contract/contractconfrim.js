var contextPath = DZF.contextPath;
var grid,gridh;
var loadrows = null;
var isenter = false;//是否快速查询

$(function() {
	initQueryData();
	initChannel();
	initCorpk();
	$('#corpkna_ae').combobox('readonly',true);
	load();
	fastQry();
	$('#confreason').textbox('textbox').attr('maxlength', 200);
	loadJumpData();
});

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
}

function closeCx(){
	$("#qrydialog").hide();
}

//初始化加盟商
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
                    buttons: '#chnBtn'
                });
            }
        }]
    });
}
//双击选择公司
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
			$('#corpkna_ae').combobox('readonly',false);
		}else{
			$('#corpkna_ae').combobox('readonly',true);
		}
		
		$("#channel_select").textbox("setValue",str);
		$("#pk_account").val(corpIds);
	}
	 $("#chnDlg").dialog('close');
}

function clearParams(){
	$('#corpkna_ae').combobox('readonly',true);
	$("#pk_account").val(null);
	$("#channel_select").textbox("setValue",null);
	$("#corpkid_ae").val(null);
	$("#corpkna_ae").textbox("setValue",null);
}

function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}

//卡片客户参照
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

function selectCorpk(row){
	$('#corpkna_ae').textbox('setValue',row.uname);
	$('#corpkid_ae').val(row.pk_gs);
	$('#gs_dialog').dialog('close');
}

function reloadData(){
	var bdate = $('#bdate').datebox('getValue'); //开始日期
	var edate = $('#edate').datebox('getValue'); //结束日期
	if(isEmpty(bdate)){
		Public.tips({
			content : '查询开始日期不能为空',
			type : 2
		});
		return;
	}
	if(isEmpty(edate)){
		Public.tips({
			content : '查询结束日期不能为空',
			type : 2
		});
		return;
	}
	if(!isEmpty(bdate) && !isEmpty(edate)){
		if(!checkdate1("bdate","edate")){
			return;
		}		
	}
	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').url =contextPath + '/contract/contractconf!query.action';
	queryParams.begdate = bdate;
	queryParams.enddate = edate;
	queryParams.qtype = -1;
	queryParams.destatus = $('#destatus').combobox('getValue');
	var isncust=$('#isncust').combobox('getValue');
	if(!isEmpty(isncust)){
		queryParams.isncust =isncust;
	}else{
		 delete queryParams.isncust;
	}
	queryParams.cpid = $("#pk_account").val();
	queryParams.cpkid = $("#corpkid_ae").val();
	queryParams.id = '';
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
	$('#querydate').html(bdate + ' 至 ' + edate);
    $('#qrydialog').hide();
    $('#grid').datagrid('unselectAll');
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
			width : '150',
			title : '提交时间',
			halign:'center',
			field : 'submitime',
		}, {
			width : '150',
			title : '地区',
			halign:'center',
			field : 'area',
		}, {
			width : '140',
			title : '加盟商',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '260',
			title : '客户名称',
			halign:'center',
			field : 'corpkna',
		}, {
			width :'100',
			title : '存量客户',
			field : 'isncust',
			align : 'center',
			halign : 'center',
			formatter:isnformat,
		},{
			width : '120',
			title : '纳税人资格',
			halign:'center',
			field : 'chname',
		}, {
			width : '100',
			title : '业务类型',
            halign:'center',
			field : 'typeminm',
		}, {
			width : '140',
			title : '合同编码',
			halign:'center',
			field : 'vccode',
		}, {
			width : '110',
			title : '合同总金额',
			align:'right',
            halign:'center',
			field : 'ntlmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '100',
			title : '附件',
			halign:'center',
			field : 'contdoc',
			formatter : formatDocLink
		}, {
			width : '120',
			title : '收款周期(月)',
			align : 'center',
			halign : 'center',
			field : 'chgcycle',
		}, {
			width : '120',
			title : '开始日期',
			halign:'center',
			align:'center',
			field : 'bperiod',
		}, {
			width : '120',
			title : '结束日期',
			halign:'center',
			align:'center',
			field : 'eperiod',
		}, {
			width : '120',
			title : '合同周期(月)',
			align : 'center',
			halign : 'center',
			field : 'contcycle',
		}, {
			width : '100',
			title : '月服务费',
			align:'right',
            halign:'center',
			field : 'nmsmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '100',
			title : '扣费日期',
			halign:'center',
			align:'center',
			field : 'dedate',
		}, {
			width : '100',
			title : '扣款比例(%)',
			halign:'center',
			align:'right',
			field : 'propor',
//			formatter : function(value,row,index){
//				if(!isEmpty(value))
//					return value+"%";
//			}
		},{
			width : '100',
			title : '扣费金额',
			align:'right',
            halign:'center',
			field : 'ndemny',
			formatter : function(value,row,index){
//				if(value == 0)return "";
				return formatMny(value);
			}
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
					return '拒绝审核';
				if (value == '8')
					return '服务到期';
			}
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
			title : '销售顾问',
			field : 'adviser',
		}, {
			width : '100',
			title : '经办人',
			field : 'vopernm',
		}, {
			width : '140',
			title : '驳回原因',
			field : 'confreason',
		}, {
			width : '100',
			title : '套餐主键',
			field : 'pid',
			hidden : true
		},] ],
		onLoadSuccess : function(data) {
            parent.$.messager.progress('close');
            if(!isenter){
				loadrows = data.rows;
			}
			isenter = false;
			calFooter();
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

/**
 * 计算合计
 */
function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
    var ntlmny = 0;	
    var nmsmny = 0;	
    var ndemny = 0;	
    for (var i = 0; i < rows.length; i++) {
    	ntlmny += parseFloat(rows[i].ntlmny);
    	nmsmny += parseFloat(rows[i].nmsmny);
    	ndemny += parseFloat(rows[i].ndemny==undefined?0:rows[i].ndemny);
    }
    footerData['corpnm'] = '合计';
    footerData['ntlmny'] = ntlmny;
    footerData['nmsmny'] = nmsmny;
    footerData['ndemny'] = ndemny;
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
	$('#grid').datagrid('options').url =contextPath + '/contract/contractconf!query.action';
	queryParams.qtype = type;
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
}

/**
 * 快速过滤
 */
function fastQry(){
	$('#filter_value').textbox('textbox').keydown(function (e) {
		 if (e.keyCode == 13) {
            var filtername = $("#filter_value").val(); 
            if (filtername != "") {
           	 var jsonStrArr = [];
           	 if(loadrows){
           		 for(var i=0;i<loadrows.length;i++){
           			 var row = loadrows[i];
           			 if(row != null && !isEmpty(row["corpnm"])){
           				 if(row["corpnm"].indexOf(filtername) >= 0){
           					 jsonStrArr.push(row);
           				 } 
           			 }
           		 }
           		 isenter = true;
           		 $('#grid').datagrid('loadData',jsonStrArr);  
           	 }
            }else{
           	 $('#grid').datagrid('loadData',loadrows);
            } 
         }
   });
}

/**
 * 初始化监听
 */
function initListener(){
	$("#propor").numberbox({
		onChange : function(n, o) {
			if (n == ""){
				n = 0; 
			}
			var ntlmny = $('#ntlmny').numberbox('getValue');//合同金额
			var nbmny = $('#nbmny').numberbox('getValue');//账本费
//			var ndemny = parseFloat(ntlmny).mul(parseFloat(n)).div(100);
			 //扣费标准修改为扣掉账本费的合同金额
            var countmny = getFloatValue(ntlmny).sub(getFloatValue(nbmny));
            var ndemny = getFloatValue(countmny).mul(parseFloat(n)).div(100);
			$('#ndemny').numberbox('setValue', ndemny);
		}
	});
	
	$(":radio").click( function(){
		var opertype = $('input:radio[name="opertype"]:checked').val();
		if(opertype == 1){
			$("#confreason").textbox('readonly',true);
			$("#confreason").textbox('setValue',null);
		}else if(opertype == 2){
			$("#confreason").textbox('readonly',false);
		}
	});
}

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
	$('#deduct_Dialog').dialog({ modal:true });//设置dig属性
	$('#deduct_Dialog').dialog('open').dialog('center').dialog('setTitle','合同审核');
	$("#fileshow").hide();
	initdeductData(rows[0]);//初始化扣款数据
	initFileDoc(rows[0]);//初始化附件
	initListener();//初始化扣款比例监听
	
}

/**
 * 初始化扣款数据
 */
function initdeductData(row){
	$('#deductfrom').form('clear');
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
                $('#deductfrom').form('load',row);
                $('#confreason').textbox('setValue', null);//驳回原因
                $('#propor').numberbox('setValue', 10);
                //扣费标准修改为扣掉账本费的合同金额
                var countmny = getFloatValue(row.ntlmny).sub(getFloatValue(row.nbmny));
                var ndemny = getFloatValue(countmny).mul(parseFloat(10)).div(100);
                $('#ndemny').numberbox('setValue', ndemny);
                $("#dedate").datebox("setValue",Public.getLoginDate());
                $('#vopernm').textbox('setValue',$("#unm").val());
                $('#voper').val($("#uid").val());
                $('#balmny').numberbox('setValue', row.balmny);//预付款余额
                $('#corpnm').textbox('setValue', row.corpnm);//渠道商
                $('#hntlmny').numberbox('setValue', row.ntlmny);//合同金额
                $('#contractid').val(row.contractid);//合同主键
                $('#salespromot').textbox('setValue', row.salespromot);//促销活动
                document.getElementById("debit").checked="true";
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
							'onmouseout="hideTips(' + i + ')"  ondblclick="doubleDocImage(\'' + i + '\');" ><span><img src="' +attachImgUrl +  '" />'+
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
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/contract/contractconf!bathconfrim.action',
		data : postdata,
		traditional : true,
		async : false,
		success : function(result) {
			if (!result.success) {
				Public.tips({
					content : result.msg,
					type : 2
				});
			} else {
				Public.tips({
					content : result.msg,
				});
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
			}
		},
	});
}

/**
 * 批量审核-取消
 */
function bathcanc(){
	$('#bdeduct_Dialog').dialog('close');
}

