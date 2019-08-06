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
		              { field : 'aname', title : '大区',width :'100',halign: 'center',align:'left'} ,
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
//			$('#grid').datagrid("scrollTo",0);
		//	$('#grid').datagrid("clearSelections");
			var rows = $('#grid').datagrid('getRows');
        	var footerData = new Object();
        	var nmny1 = 0;// 第一季度
			var nmny2 = 0;// 第二季度
			var nmny3 = 0;// 第三季度
			var nmny4 = 0;// 第四季度
			for (var i = 0; i < rows.length; i++) {
				if (rows[i].hasOwnProperty("nmny1") && !isEmpty(rows[i].nmny1)) {
					nmny1 += parseFloat(rows[i].nmny1);
				}
				if (rows[i].hasOwnProperty("nmny2") && !isEmpty(rows[i].nmny2)) {
					nmny2 += parseFloat(rows[i].nmny2);
				}
				if (rows[i].hasOwnProperty("nmny3") && !isEmpty(rows[i].nmny3)) {
					nmny3 += parseFloat(rows[i].nmny3);
				}
				if (rows[i].hasOwnProperty("nmny4") && !isEmpty(rows[i].nmny4)) {
					nmny4 += parseFloat(rows[i].nmny4);
				}
			}
			footerData['corp'] = '合计';
			footerData['nmny1'] = nmny1;
			footerData['nmny2'] = nmny2;
			footerData['nmny3'] = nmny3;
			footerData['nmny4'] = nmny4;
			var fs = new Array(1);
			fs[0] = footerData;
			$('#grid').datagrid('reloadFooter', fs);
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
