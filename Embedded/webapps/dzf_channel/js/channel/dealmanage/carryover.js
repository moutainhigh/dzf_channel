var contextPath = DZF.contextPath;

$(function(){
	initPeriod("#bperiod");
	initPeriod("#eperiod");
	load();
	reloadData();
});

/**
 * 列表表格加载
 */
function load(){
	$('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height : Public.setGrid().h,
		singleSelect : true,
		showFooter:true,
		idField : 'coid',
		columns : [ [ 
			 { field : 'period', title : '期间',width :'100',halign: 'center',align:'center'} ,
			 { field : 'isco', title : '成本结转',width :'100',halign: 'center',align:'center',formatter:isnformat} ,
			 { field : 'operate', title : '操作',width :'200',halign: 'center',align:'center',formatter:opermatter} ,
		] ],
		onLoadSuccess : function(data) {
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

function reloadData(){
	var url = contextPath+"/dealmanage/carryover!query.action";
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'bperiod' : $("#bperiod").datebox('getValue'),
		'eperiod' : $("#eperiod").datebox('getValue'),
	});
}

function isnformat(value,row){
	if(value&&(value=='Y'||value=="是")){
		return "<input type=\"checkbox\" checked=\"checked\" onclick=\"return false;\" >";
	}else{
		return "<input type=\"checkbox\" onclick=\"return false;\" >";
	}
}

function opermatter(val, row, index) {
	if(isEmpty(row.isco) || row.isco=="N" || row.isco=="否"){
		return '<a href="#" style="margin-bottom:0px;color:blue;margin-left:10px;" onclick="forward(\''+index+'\')">成本结转</a>'+
		'<span style="margin-bottom:0px;margin-left:10px;">反成本结转</span>';
	}else {
		return '<span style="margin-bottom:0px;margin-left:10px;">成本结转</span>'+
		'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="back(\''+index+'\')">反成本结转</a>';
	}
}
	

function forward(index){
	var row = $('#grid').datagrid('getRows')[index];
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/dealmanage/carryover!save.action',
		data : {"coid":row.coid,"isco":"Y","period":row.period,"updatets":row.updatets},
		traditional : true,
		async : false,
		success : function(data, textStatus) {
			if (!data.success) {
				Public.tips({
					content : data.msg,
					type : 2
				});
			} else {
				$("#grid").datagrid("reload");
				Public.tips({
					content : data.msg,
				});
			}
		},
	});
}

function back(index){
	var row = $('#grid').datagrid('getRows')[index];
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/dealmanage/carryover!save.action',
		data : {"coid":row.coid,"isco":"N","period":row.period,"updatets":row.updatets},
		traditional : true,
		async : false,
		success : function(data, textStatus) {
			if (!data.success) {
				Public.tips({
					content : data.msg,
					type : 2
				});
			} else {
				$("#grid").datagrid("reload");
				Public.tips({
					content : data.msg,
				});
			}
		},
	});
}


