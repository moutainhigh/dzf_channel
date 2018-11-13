var contextPath = DZF.contextPath;
var grid;

$(function(){
	initGrid();
	fastQry();
	$('#qyear').combobox({
		onChange: function(newValue, oldValue){
			reloadData();
		},
	});
});

/**
 * 列表表格初始化
 */
function initGrid(){
	grid = $('#grid').datagrid({
		url : contextPath + '/report/rebateCount!query.action',
		queryParams: {
			year: $("#qyear").combobox("getValue"),
		},
		striped : true,
		title : '',
		fitColumns:false,
		rownumbers : true,
		height : Public.setGrid().h,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		singleSelect : false,
		idField : 'rebid',
		showFooter:true,
		columns : [ [ 
		              { field : 'provname', title : '省(市)',width :'110',halign: 'center',align:'left'} ,
		              { field : 'mname', title : '渠道经理',width :'115',halign: 'center',align:'left'} ,
		              { field : 'corpcode', title : '加盟商编码',width :'115',halign: 'center',align:'left'} ,
		              { field : 'corp', title : '加盟商名称',width :'160',halign: 'center',align:'left'}, 
		              { field : 'nmny1', title : '第一季度',width :'115',halign: 'center',align:'right',formatter : formatMny} ,
		              { field : 'nmny2', title : '第二季度',width :'115',halign: 'center',align:'right',formatter : formatMny} ,
		              { field : 'nmny3', title : '第三季度',width :'115',halign: 'center',align:'right',formatter : formatMny} ,
		              { field : 'nmny4', title : '第四季度',width :'115',halign: 'center',align:'right',formatter : formatMny} ,
				      { field : 'rebid', title : '主键', hidden:true},
		] ],
		onLoadSuccess : function(data) {
			$('#grid').datagrid("scrollTo",0);
		}
	});
}

/**
 * 查询
 */
function reloadData(){
	url = contextPath + '/report/rebateCount!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'year' : $("#qyear").combobox("getValue"),
	});
}

/**
 * 快速过滤
 */
function fastQry(){
	$('#filter_value').textbox('textbox').keydown(function (e) {
		 if (e.keyCode == 13) {
            var filtername = $("#filter_value").val(); 
        	var queryParams = $('#grid').datagrid('options').queryParams;
        	queryParams.year = $("#qyear").combobox("getValue");
        	queryParams.cpkname = filtername;
      		grid.datagrid('options').url = contextPath + '/report/rebateCount!query.action';
      		$('#grid').datagrid('options').queryParams = queryParams;
      		$('#grid').datagrid('reload');
         }
   });
}
