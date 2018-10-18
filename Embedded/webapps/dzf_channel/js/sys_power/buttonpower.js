var contextPath=DZF.contextPath;
var status="brows";
var roleMap=new HashMap();//角色编码对应角主键
var roleCode=["jms01","jms02","jms03","jms04","jms05","jms06","jms07","jms08","jms09","jms10","jms11"];
//自适应边框
$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid(0,'dataGrid').h,
		width : Public.setGrid(0,'dataGrid').w
	});
});

$(function(){
	loadHead();
	updateBtnState();
});

/**
 * 表格加载
 * @returns
 */
function loadHead(){
	$.ajax({
		async : false,
		type : "post",
		dataType : "json",
		url : contextPath + "/sys/buttonPower!queryHead.action",
		traditional : true,
		success : function(data) {
			if (data.success) {
				var head=data.rows;
				var columns = getColumn(head); 
				$('#grid').datagrid({
					striped : true,
					singleSelect : true,
					width: "auto",
					height : Public.setGrid().h,
					remoteSort:false,
					columns : columns,
					onLoadSuccess : function(data) {
						mergeCell(data,this);
					},
				});
			}else{
				Public.tips({content : msg,type : 1});
			}
		},
	});
}

function reloadData(){
	$.ajax({
		async : false,
		type : "post",
		dataType : "json",
		url : contextPath + "/sys/buttonPower!query.action",
		traditional : true,
		success : function(data) {
			if (data.success) {
				var body=data.rows;
				if(body != null && body.length > 0){
					$('#grid').datagrid('loadData', body);
				}else{
					$('#grid').datagrid('loadData',{ total:0, rows:[]});
				}
			}else{
				Public.tips({content : msg,type : 1});
			}
		},
	});
}

function edit(){
	status="edit";
	updateBtnState();
	beginBodyEdit();
}

function save(){
	endBodyEdit();
	var rows =$('#grid').datagrid('getRows');
	var body = "";
	var parm={};
	var data;
	for (var i = 0; i< rows.length; i++) {
		parm={};
		for(var j = 0; j< roleCode.length; j++){
			data=eval("rows[i]."+roleCode[j]);
			if(!isEmpty(data) && data=="Y"){
				parm.fid=rows[i].fid;
				parm.bid=rows[i].bid;
				parm.rid=roleMap.get(roleCode[j]);
				body = body + JSON.stringify(parm);
			} 
		}
	}
	var postdata = new Object();
	postdata["body"] = body;
	$.ajax({
		async : false,
		type : "post",
		dataType : "json",
		data : postdata,
		url : contextPath + "/sys/buttonPower!save.action",
		traditional : true,
		success : function(data) {
			if (data.success) {
				status="brows";
				updateBtnState();
			}else{
				Public.tips({content : data.msg,type : 1});
			}
		},
	});
}

function cancel(){
	status="brows";
	updateBtnState();
}

/**
 * 获取column
 * @param head
 * @returns {Array}
 */
function getColumn(head){
	var columns = new Array(); 
	var columnl = new Array();
	
	var columnl1 = {};
	columnl1["title"] = '';  
	columnl1["field"] = 'fname';  
	columnl1["width"] = '100'; 
	columnl1["halign"] = 'center'; 
	columnl.push(columnl1);
	
	var columnl2 = {};
	columnl2["title"] = '操作';  
	columnl2["field"] = 'name';  
	columnl2["width"] = '70'; 
	columnl2["halign"] = 'center'; 
	columnl2["align"] = 'center'; 
	columnl.push(columnl2);
	
	roleMap=new HashMap();
	for(var i=0;i<head.length;i++){
		roleMap.put(head[i].rcode,head[i].rid);
		var column = {};
		column["title"] = head[i].rname;  
		column["field"] = head[i].rcode;  
		column["rid"] = head[i].rid;  
		column["width"] = '90'; 
		column["halign"] = 'center';
		column["align"] = 'center'; 
		column["editor"] = { type: "checkbox",options: {on: "Y", off: "N"}}; 
		column["formatter"] = showCheck; 
		columnl.push(column);
	}
	columns.push(columnl);
	return columns;
}

function showCheck(value, row, index){
	var checked = (!isEmpty(value)) ? "checked" : "";
	return '<input type="checkbox" disabled  ' + checked + '/>';
}

function endBodyEdit(){
	var rows = $("#grid").datagrid('getRows');
 	for ( var i = 0; i < rows.length; i++) {
 		$("#grid").datagrid('endEdit', i);
 	}
};

function beginBodyEdit(){
	 var rows = $("#grid").datagrid('getRows');
	 for ( var i = 0; i < rows.length; i++) {
	 	$("#grid").datagrid('beginEdit', i);
	 }
};

/**
 * 合并单元格
 * @returns
 */
function mergeCell(data,is){
	var mark=1;                                              
　　　for (var i=1; i <data.rows.length; i++) {    
　　　　　　if (data.rows[i]['fname'] == data.rows[i-1]['fname']) {  
　　　　　　　　mark += 1;                                            
　　　　　　　　$(is).datagrid('mergeCells',{ 
　　　　　　　　　　index: i+1-mark,                 
　　　　　　　　　　field: 'fname',              
　　　　　　　　　　rowspan:mark                 
　　　　　　　　}); 
　　　　　　}else{
　　　　　　　　mark=1;                                
　　　　　　}
　	}
}

/**
 * 按钮显示
 */
function updateBtnState(){
	if("edit"==status){
		$('#saveBtn').show();
		$('#cancelBtn').show();
		$('#editBtn').hide();
		beginBodyEdit();
		reloadData();
	}else if("brows"==status){
		$('#saveBtn').hide();
		$('#cancelBtn').hide();
		$('#editBtn').show();
		endBodyEdit();
		reloadData();
	}
}