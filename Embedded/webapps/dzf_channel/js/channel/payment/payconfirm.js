var contextPath = DZF.contextPath;
var grid;
var loadrows = null;
var isenter = false;//是否快速查询

$(function() {
	load();
	fastQry();
});

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
			width : '140',
			title : '渠道商',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '150',
			title : '付款时间',
			field : 'dpdate',
		},{
			width : '260',
			title : '单据号',
			field : 'vcode',
		}, 	{
			width : '140',
			title : '付款类型',
            halign:'center',
			field : 'iptype',
			formatter : function(value) {
				if (value == '1')
					return '加盟费';
				if (value == '2')
					return '预付款';
			}
		}, {
			width : '140',
			title : '付款金额',
			align:'right',
            halign:'center',
			field : 'npmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '140',
			title : '支付方式',
            halign:'center',
			field : 'ipmode',
			formatter : function(value) {
				if (value == '1')
					return '银行转账';
				if (value == '2')
					return '支付宝';
				if (value == '3')
					return '微信';
			}
		},{
			width : '140',
			title : '单据状态',
            halign:'center',
			field : 'status',
			formatter : function(value) {
				if (value == '1')
					return '待提交';
				if (value == '2')
					return '待确认';
				if (value == '3')
					return '已确认';
			}
		},{
			width : '140',
			title : '备注',
            halign:'center',
			field : 'memo',
		},{
			width : '140',
			title : '收款确认时间',
            halign:'center',
			field : 'dctime',
		}] ],
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
	$('#grid').datagrid('options').url =contextPath + '/chnpay/chnpayconf!query.action';
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
 * 操作
 * @param type 2：取消收款；3：收款确认；
 */
function operate(type){
	var rows = $("#grid").datagrid("getChecked");
	if(rows == null || rows.length == 0){
		Public.tips({content:'请选择需要处理的数据',type:2});
        return;
	}
	var data = '';
	if (rows != null && rows.length > 0) {
		for (var i = 0; i < rows.length; i++) {
			data = data + JSON.stringify(rows[i]);
		}
	}
	var postdata = new Object();
	postdata["data"] = data;
	postdata["type"] = type;
	operatData(postdata,rows);
}

/**
 * 操作数据
 */
function operatData(postdata, rows){
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/chnpay/chnpayconf!operate.action',
		data : postdata,
		traditional : true,
		async : false,
		success : function(result) {
			if (!result.success) {
				if("数据不能为空" == result.msg){
					Public.tips({
						content : result.msg,
						type : 2
					});
				}
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
				var rerows = result.rows;
				if(rerows != null && rerows.length > 0){
					var map = new HashMap(); 
					for(var i = 0; i < rerows.length; i++){
						map.put(rerows[i].chargeid1,rerows[i]);
					}
					var index;
					var indexes = new Array();
					for(var i = 0; i < rows.length; i++){
						if(map.containsKey(rows[i].chargeid1)){
							index = $('#grid').datagrid('getRowIndex', rows[i]);
							indexes.push(index);
						}
					}
					for(var i in indexes){
						$('#grid').datagrid('updateRow', {
							index : indexes[i],
							row : result.rows[i]
						});
					}
				}
				$("#grid").datagrid('uncheckAll');
			}
		},
	});
}
