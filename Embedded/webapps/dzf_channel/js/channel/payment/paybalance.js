var contextPath = DZF.contextPath;
var grid,gridh;
var loadrows = null;
var isenter = false;//是否快速查询

$(function() {
	load();
	fastQry();
	initDetailGrid();
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
		singleSelect : true,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		showFooter:true,
		columns : [ [ {
			width : '140',
			title : '渠道商',
			halign:'center',
			field : 'corpnm',
		}, {
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
		},{
			width : '140',
			title : '已用金额',
			align:'right',
            halign:'center',
			field : 'usemny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '140',
			title : '余额',
			align:'right',
            halign:'center',
			field : 'balmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},] ],
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
	$('#grid').datagrid('options').url =contextPath + '/chnpay/chnpaybalance!query.action';
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
 * 明细查询
 */
function qryDetail(){
	var row = $('#grid').datagrid('getSelected');
	if (row == null) {
		Public.tips({
			content : "请选择需要处理的数据",
			type : 2
		});
		return;
	}
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + "/chnpay/chnpaybalance!queryDetail.action",
		data : {
			"cpid" : row.corpid,
			"qtype" : row.iptype,
		},
		traditional : true,
		async : false,
		success : function(data, textStatus) {
			if (!data.success) {
				Public.tips({
					content : data.msg,
					type : 1
				});
			} else {
				var res = data.rows;
				if (res == null || res == "") {
					$('#detail_dialog').dialog('close');
					Public.tips({
						content : "无明细记录",
						type : 2
					});
					return;
				}
				$('#corpnm').textbox('setValue',res[0].corpnm);
				$('#ptypenm').textbox('setValue',res[0].ptypenm);
				$('#detail_dialog').dialog('open');
				$('#gridh').datagrid('loadData',res);
			}
		}
	});
}

/**
 * 明细列表初始化
 */
function initDetailGrid(){
	gridh = $('#gridh').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		/*height : Public.setGrid().h,*/
		height:'350',
		singleSelect : true,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		showFooter:true,
		columns : [ [ {
			width : '140',
			title : '日期',
			halign:'center',
			field : 'ddate',
		}, {
			width : '140',
			title : '摘要',
            halign:'center',
			field : 'memo',
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
		},{
			width : '140',
			title : '已用金额',
			align:'right',
            halign:'center',
			field : 'usemny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '140',
			title : '余额',
			align:'right',
            halign:'center',
			field : 'balmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},] ],
		onLoadSuccess : function(data) {
			
			var rows = $('#gridh').datagrid('getRows');
			var footerData = new Object();
            var npmnysum = parseFloat(0);	// 付款金额
            var usemnysum = parseFloat(0);	// 已用金额
            var balmnysum = parseFloat(0);	// 余额
            for (var i = 0; i < rows.length; i++) {
            	npmnysum += parseFloat(rows[i].npmny);
            	usemnysum += parseFloat(rows[i].usemny);
            	if(i == rows.length - 1){
            		balmnysum = rows[i].balmny;
            	}
            }
            footerData['ddate'] = '合计';
            footerData['npmny'] = npmnysum;
            footerData['usemny'] = usemnysum;
            footerData['balmny'] = balmnysum;
            var fs=new Array(1);
            fs[0] = footerData;
            $('#gridh').datagrid('reloadFooter',fs);
			
            $('#gridh').datagrid("scrollTo",0);
		},
	});
}
