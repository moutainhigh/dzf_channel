var contextPath = DZF.contextPath;
var grid,gridh;
var loadrows = null;
var isenter = false;//是否快速查询

$(function() {
	initQueryData();
	load();
	fastQry();
	$('#confreason').textbox('textbox').attr('maxlength', 200);
});

/**
 * 查询日期初始化
 */
function initQueryData() {
	var ldate = new Date(Public.getLoginDate());
    ldate.setMonth(ldate.getMonth()-1);
    ldate = ldate.Format('yyyy-MM-dd');
	$("#begdate").datebox("setValue", ldate);
	$("#enddate").datebox("setValue",Public.getLoginDate());
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
		singleSelect : true,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		showFooter:true,
		columns : [ [ {
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
			title : '渠道商主键',
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
			width : '140',
			title : '渠道商',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '140',
			title : '客户名称',
			halign:'center',
			field : 'corpkna',
		}, {
			width : '140',
			title : '纳税人性质',
			halign:'center',
			field : 'chname',
		}, {
			width : '200',
			title : '合同类型',
            halign:'center',
			field : 'typeminm',
		}, {
			width : '140',
			title : '合同号',
			halign:'center',
			field : 'vccode',
		}, {
			width : '140',
			title : '合同总金额',
			align:'right',
            halign:'center',
			field : 'ntlmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '140',
			title : '开始日期',
			halign:'center',
			field : 'bdate',
		}, {
			width : '140',
			title : '结束日期',
			halign:'center',
			field : 'edate',
		}, {
			width : '140',
			title : '合同周期',
			halign:'center',
			field : 'cylnum',
		}, {
			width : '140',
			title : '月服务费',
			align:'right',
            halign:'center',
			field : 'nmsmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '140',
			title : '扣费日期',
			halign:'center',
			field : 'dedate',
		}, {
			width : '140',
			title : '扣款比例',
			halign:'center',
			field : 'propor',
			formatter : function(value,row,index){
				if(!isEmpty(value))
					return value+"%";
			}
		},{
			width : '140',
			title : '扣费金额',
			align:'right',
            halign:'center',
			field : 'ndemny',
			formatter : function(value,row,index){
				if(value == 0)return "";
				return formatMny(value);
			}
		}, {
			width : '140',
			title : '合同状态',
            halign:'center',
			field : 'destatus',
			formatter : function(value) {
				if (value == '1')
					return '待确认';
				if (value == '2')
					return '待扣款';
				if (value == '3')
					return '已扣款';
			}
		}, {
			width : '100',
			title : '时间戳',
			field : 'tstp',
			hidden : true
		}, ] ],
		onLoadSuccess : function(data) {
            parent.$.messager.progress('close');
            if(!isenter){
				loadrows = data.rows;
			}
			isenter = false;
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

/**
 * 标签查询
 * @param type  1：全部；2：加盟费；3：预付款；
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
 * 查询
 */
function query(){
	var begdate = $("#begdate").datebox('textbox').val();
	var enddate = $("#enddate").datebox('textbox').val();
	if(isEmpty(begdate)){
		Public.tips({
			content : '查询开始日期不能为空',
			type : 2
		});
		return;
	}
	if(isEmpty(enddate)){
		Public.tips({
			content : '查询结束日期不能为空',
			type : 2
		});
		return;
	}
	if(!isEmpty(begdate) && !isEmpty(enddate)){
		if(!checkdate1("begdate","enddate")){
			return;
		}		
	}
	
	$('#grid').datagrid('unselectAll');
	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').url =contextPath + '/contract/contractconf!query.action';
	queryParams.begdate = begdate;
	queryParams.enddate = enddate;
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
}

/**
 * 合同确认
 */
function doConfrim(){
	var row = $('#grid').datagrid('getSelected');
	if(row == null){
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});			
		return;
	}
	$('#confrim_Dialog').dialog({ modal:true });//设置dig属性
	$('#confrim_Dialog').dialog('open').dialog('center').dialog('setTitle','合同确认');
	$('#conform').form('clear');
}

/**
 * 确认成功/确认失败/取消确认
 */
function confrim(type){
	var row = $('#grid').datagrid('getSelected');
	if(row == null){
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});			
		return;
	}
	//1-确认成功；2-确认失败；3-取消确认；
	if(1 == type){
		if(row.destatus == 2){
			Public.tips({
				content : '合同状态为待扣款',
				type : 2
			});			
			return;
		}else if(row.destatus == 3){
			Public.tips({
				content : '合同状态为已扣款',
				type : 2
			});			
			return;
		}
	}else if(2 == type || 3 == type){
		if(row.destatus == 1){
			Public.tips({
				content : '合同状态为待确认',
				type : 2
			});			
			return;
		}else if(row.destatus == 3){
			Public.tips({
				content : '合同状态为已扣款',
				type : 2
			});			
			return;
		}
	}
	
	row.confreason = $('#confreason').val();
	var postdata = new Object();
	postdata["data"] = JSON.stringify(row);
	postdata["confstatus"] = type;
	
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/contract/contractconf!updateConfStatus.action',
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
				$('#confrim_Dialog').dialog('close');
				var rerow = result.rows;
				var index = $("#grid").datagrid("getRowIndex",row);
				$('#grid').datagrid('updateRow',{
            		index: index,
            		row: rerow,
            	});
				$("#grid").datagrid('uncheckAll');
			}
		},
	});
}

/**
 * 扣款处理
 */
function deduct(){
	var row = $('#grid').datagrid('getSelected');
	if(row == null){
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});			
		return;
	}
	if(row.destatus != 2){
		Public.tips({
			content : '合同状态不为待扣款',
			type : 2
		});			
		return;
	}
	$('#deduct_Dialog').dialog({ modal:true });//设置dig属性
	$('#deduct_Dialog').dialog('open').dialog('center').dialog('setTitle','扣款');
	$('#deductfrom').form('clear');
	initListener();
	$.ajax({
        type: "post",
        dataType: "json",
        url: contextPath + '/contract/contractconf!queryDeductData.action',
//        data: row,
        data : {
        	id : row.id,
        	corpid : row.corpid,
		},
        traditional: true,
        async: false,
        success: function(data, textStatus) {
            if (!data.success) {
            	Public.tips({content:data.msg,type:1});
            } else {
                var row = data.rows;
                $('#deductfrom').form('load',row);
                $('#propor').numberbox('setValue', 10);
                var ntlmny = $('#ntlmny').numberbox('getValue');//合同金额
                var ndemny = parseFloat(ntlmny).mul(parseFloat(10)).div(100);
                $('#ndemny').numberbox('setValue', ndemny);
                $("#dedate").datebox("setValue",Public.getLoginDate());
                $('#vopernm').textbox('setValue',$("#unm").val());
                $('#voper').val($("#uid").val());
                $('#corpkna').val(row.corpkna);
                $('#vccode').val(row.vccode);
            }
        },
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
			var ndemny = parseFloat(ntlmny).mul(parseFloat(n)).div(100);
			$('#ndemny').numberbox('setValue', ndemny);
		}
	});
}

/**
 * 扣款确认
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
	if(row.destatus != 2){
		Public.tips({
			content : '合同状态不为待扣款',
			type : 2
		});			
		return;
	}
	var postdata = new Object();
	postdata["head"] = JSON.stringify(serializeObject($('#deductfrom')));
	postdata["opertype"] = 1;
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
					row : {
						propor : rerow.propor,
						ndemny : rerow.ndemny,
						dedate : rerow.dedate,
						voper : rerow.voper,
						destatus : rerow.destatus,
        			}
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
 * 扣款取消
 */
function deductCancel(){
	$('#deduct_Dialog').dialog('close');
}

/**
 * 取消扣款
 */
function cancelDeduct(){
	var row = $('#grid').datagrid('getSelected');
	if(row == null){
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});			
		return;
	}
	if(row.destatus != 3){
		Public.tips({
			content : '合同状态不为已扣款',
			type : 2
		});			
		return;
	}
	var postdata = new Object();
	postdata["head"] = JSON.stringify(row);
	postdata["opertype"] = 2;
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/contract/contractconf!updateDeductData.action',
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
				$('#deduct_Dialog').dialog('close');
				var rerow = result.rows;
				var index = $("#grid").datagrid("getRowIndex",row);
				$('#grid').datagrid('updateRow',{
					index: index,
					row : {
						propor : null,
						ndemny : "",
						dedate : "",
						voper : "",
						destatus : 2,
        			}
				});
				$("#grid").datagrid('uncheckAll');
			}
		},
	});
}
