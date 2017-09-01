var contextPath = DZF.contextPath;
var grid,gridh;
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
		},{
			width : '140',
			title : '客户名称',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '140',
			title : '纳税人性质',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '140',
			title : '合同类型',
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
			title : '合同号',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '140',
			title : '合同总金额',
			align:'right',
            halign:'center',
			field : 'npmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '140',
			title : '开始日期',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '140',
			title : '结束日期',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '140',
			title : '合同周期',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '140',
			title : '月服务费',
			align:'right',
            halign:'center',
			field : 'usemny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '140',
			title : '扣费日期',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '140',
			title : '扣费金额',
			align:'right',
            halign:'center',
			field : 'usemny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '140',
			title : '合同状态',
            halign:'center',
			field : 'iptype',
			formatter : function(value) {
				if (value == '1')
					return '加盟费';
				if (value == '2')
					return '预付款';
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
	
}

/**
 * 合同确认
 */
function doConfrim(){
	
}

/**
 * 取消确认
 */
function doCancel(){
	
}

/**
 * 扣款处理
 */
function deduct(){
	
}

/**
 * 取消扣款
 */
function dedancel(){
	
}





