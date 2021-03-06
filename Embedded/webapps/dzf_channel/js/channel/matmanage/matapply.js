var contextPath = DZF.contextPath;
//var hjcols = null;//汇总列字段
var editIndex;
var status="brows";
var flag = true;
var addflag= false;
var rowflag = false;
$(window).resize(function () {
    $('#grid').datagrid('resize', {
        height: Public.setGrid().h,
        width: "auto",
    });
});

$(function(){
	initQry();
	initRef();
	//initMatFile();
	initRadioListen();
	//load(0);
	showColumn();
	$("#bperiod").datebox('readonly',true);
	$("#eperiod").datebox('readonly',true);
	$("#begdate").datebox('readonly',false);
	$("#enddate").datebox('readonly',false);
});

/**
 * 查询初始化
 */
function initQry(){
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	
	queryBoxChange('#begdate','#enddate');
	queryBoxChange('#bperiod','#eperiod'); 
	
	$("#begdate").datebox("setValue", '');
	$("#enddate").datebox("setValue",'');
	$("#bperiod").datebox("setValue", '');
	$("#eperiod").datebox("setValue", '');
	
}



/**
 * 表格初始化
 * @param type  0：初始化界面加载；1；查询；2：待审核；3：已驳回；
 */
function load(type){
	var bdate = new Date();
	
	var onlycol =  new ArrayList();//物料展示集合
	var onlymap = new HashMap();//物料对应列数
	
	var bperiod;
	var eperiod;
	var begdate;
	var enddate;
	var status = $('#status').combobox('getValue');
	if(type==2){
		status = 1;
	}
	if(type==3){
		status = 4;
	}
	var corpname = $("#qcorpname").val();
	if($('#qj').is(':checked')){
		bperiod = $("#bperiod").datebox('getValue');
		eperiod = $("#eperiod").datebox('getValue');
		
		
		$('#jqj').html(bperiod + ' 至 ' + eperiod);
	}else{
		begdate = $("#begdate").datebox('getValue');
		enddate = $("#enddate").datebox('getValue');
		
		
		if(!isEmpty(begdate) && !isEmpty(enddate)){
			$('#jqj').html(begdate + ' 至 ' + enddate);
		}
	}
	
	$.messager.progress({
		text : '数据加载中....'
	});
	
	//获取标题列
	var columns = getcolumn(onlymap, onlycol, bperiod, eperiod, begdate, enddate, status,corpname);
	
	var datarray =  new Array();
	var errmsg = "";
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/matmanage/matapply!query.action',
		traditional : true,
		data : {
			bperiod : bperiod,
			eperiod : eperiod,
			begdate : begdate,
			enddate : enddate,
			corpname : corpname,
			status : status,
		},
		async : false,
		success : function(data) {
			if (data.success) {
				var rows = data.rows;
				if(rows != null && rows.length > 0){
					var matbillid;
					var obj;
					var colfield = "";
					
					for(var i = 0; i < rows.length; i++){
						if(i == 0){
							matbillid = rows[i].matbillid;
							obj = {};
							colnm = onlymap.get(rows[i].wlname+"/"+rows[i].unit);
							colfield = 'applynum'+colnm;
							obj[colfield] = rows[i].applynum;
							colfield = 'outnum'+colnm;
							obj[colfield] = rows[i].outnum;
							
							obj['receiver'] = rows[i].receiver;//收货人
							obj['phone'] = rows[i].phone;//联系电话
							obj['address'] = rows[i].address;//地址
							obj['logname'] = rows[i].logname;//快递公司
							obj['fcode'] = rows[i].fcode;//单号
							obj['fcost'] = rows[i].fcost;//金额
							obj['dedate'] = rows[i].dedate;//发货时间
							obj['status'] = rows[i].status;//状态
							obj['memo'] = rows[i].memo;//备注
							obj['reason'] = rows[i].reason;//驳回原因
							obj['operdate'] = rows[i].operdate;//录入时间
							obj['applyname'] = rows[i].applyname;//申请人
							obj['adate'] = rows[i].adate;//申请时间
							
							obj['matbillid'] = rows[i].matbillid;//主键
							obj['aname'] = rows[i].aname;//大区
							obj['uname'] = rows[i].uname;//渠道经理
							obj['proname'] = rows[i].proname;//省（市）
							obj['corpname'] = rows[i].corpname;
							obj['code'] = rows[i].code;//合同编码
							if(i == rows.length - 1){//一行数据
								colnm = onlymap.get(rows[i].wlname+"/"+rows[i].unit);
								colfield = 'applynum'+colnm;
								obj[colfield] = rows[i].applynum;
								colfield = 'outnum'+colnm;
								obj[colfield] = rows[i].outnum;
								
								datarray.push(obj);
							}
						}else{
							if(matbillid == rows[i].matbillid){
								colnm = onlymap.get(rows[i].wlname+"/"+rows[i].unit);
								colfield = 'applynum'+colnm;
								obj[colfield] = rows[i].applynum;
								colfield = 'outnum'+colnm;
								obj[colfield] = rows[i].outnum;
								
								if(i == rows.length - 1){//最后一行数据
									datarray.push(obj);
								}
							}else if(matbillid != rows[i].matbillid){
								datarray.push(obj);
								matbillid = rows[i].matbillid;
								obj = {};
								colnm = onlymap.get(rows[i].wlname+"/"+rows[i].unit);
								colfield = 'applynum'+colnm;
								obj[colfield] = rows[i].applynum;
								colfield = 'outnum'+colnm;
								obj[colfield] = rows[i].outnum;
								
								obj['receiver'] = rows[i].receiver;//收货人
								obj['phone'] = rows[i].phone;//联系电话
								obj['address'] = rows[i].address;//地址
								obj['logname'] = rows[i].logname;//快递公司
								obj['fcode'] = rows[i].fcode;//单号
								obj['fcost'] = rows[i].fcost;//金额
								obj['dedate'] = rows[i].dedate;//发货时间
								obj['status'] = rows[i].status;//状态
								obj['memo'] = rows[i].memo;//备注
								obj['reason'] = rows[i].reason;//驳回原因
								obj['operdate'] = rows[i].operdate;//录入时间
								obj['applyname'] = rows[i].applyname;//申请人
								obj['adate'] = rows[i].adate;//申请时间
								
								obj['matbillid'] = rows[i].matbillid;//主键
								obj['aname'] = rows[i].aname;//大区
								obj['uname'] = rows[i].uname;//渠道经理
								obj['proname'] = rows[i].proname;//省（市）
								obj['corpname'] = rows[i].corpname;
								obj['code'] = rows[i].code;//合同编码
								if(i == rows.length - 1){//最后一行数据
									datarray.push(obj);
								}
							}
						}
					}
				}
			}else{
				errmsg = data.msg;
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.messager.progress('close');
		}
	});
	
	if(!isEmpty(errmsg)){
		$.messager.progress('close');
		Public.tips({
			content : errmsg,
			type : 2
		});
		return;
	}
	
	$('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : true,
		checkOnSelect :false,
		showFooter: true,
		remoteSort : false,
		idField : 'matbillid',
		columns : columns,
		onLoadSuccess : function(data) {
			
			var edate = new Date();
			var time = edate.getTime() - bdate.getTime();
			
			setTimeout(function(){
				$.messager.progress('close');
			}, time/2);
		},
	});
	
	if(datarray != null && datarray.length > 0){
		$('#grid').datagrid('loadData', datarray);
	}else{
		$('#grid').datagrid('loadData',{ total:0, rows:[]});
	}
	
	$("#qrydialog").hide();
	
	var msg = "";
	if(type == 1){
		msg = "查询成功";
	}
	if(!isEmpty(msg)){
		Public.tips({
			content : msg,
			type : 0
		});
	}

	
}


/**
 * 首先加载固定列
 */
function showColumn(){
	grid = $('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : true,
		checkOnSelect :false,
		showFooter: true,
		remoteSort : false,
		idField : 'matbillid',
		columns : [ [  
        { field : 'matbillid',    title : '主键',hidden:true,rowspan:2}, 
        { field : 'updatets',    title : '时间戳', hidden:true,rowspan:2},
        { field : 'aname',  title : '大区', width : 100, halign:'center',align:'center',rowspan:2},
        { field : 'uname',  title : '渠道经理', width : 100, halign:'center',align:'center',rowspan:2},
        { field : 'proname',  title : '省/市', width : 100, halign:'center',align:'center',rowspan:2}, 
		{ field : 'corpname',  title : '加盟商', width : 130, halign:'center',align:'center',rowspan:2,
        	formatter : function(value, row, index) {
				return '<a href="javascript:void(0)"  style="color:blue" onclick="showDetail(\''
						+ index
						+ '\')">'
						+ value
						+ '</a>';
			}
		},
		{ field : 'code',  title : '合同编号', width : 100, halign:'center',align:'center',rowspan:2},
		{ field : 'reinfo',  title : '收货信息', width : 280, halign:'center',align:'center',colspan:3},
		{ field : 'expressinfo',  title : '快递信息', width : 360, halign:'center',align:'center',colspan:4}, 
		
		{ field : 'memo',  title : '备注', width : 100, halign:'center',align:'center',rowspan:2},
		{ field : 'status',  title : '状态', width : 100, halign:'center',align:'center',rowspan:2, 
				formatter : staForma,
		},
		{ field : 'reason',  title : '驳回原因', width : 100, halign:'center',align:'center',rowspan:2},
		{ field : 'operdate',  title : '录入时间', width : 100, halign:'center',align:'center',rowspan:2}, 
		{ field : 'applyname',  title : '申请人', width : 100, halign:'center',align:'center',rowspan:2}, 
		{ field : 'adate',  title : '申请时间', width : 100, halign:'center',align:'center',rowspan:2},
        
		 ],
		 [
		  
		  {width : '80',title : '收货人',field : 'receiver',align:'center',},
		  {width : '100',title : '联系电话',field : 'phone',align:'center',},
		  {width : '100',title : '地址',field : 'address',align:'center',},
	
		  {width : '100',title : '快递公司',field : 'logname',align:'center',},
		  {width : '80',title : '金额',field : 'fcost',align:'center',
		 		formatter : function(value,row) {
          		if(!isEmpty(row.matbillid)){
          			return formatMny(value);
          		}
          	}, 	
		  },
		  {width : '80',title : '单号',field : 'fcode',align:'right',},
		  {width : '100',title : '发货时间',field : 'dedate',align:'right',},
		  
		] ],
		onLoadSuccess : function(data) {
			
		},
	});
}


/**
 * 获取标题列
 * @param onlymap
 */
function getcolumn(onlymap, onlycol, bperiod, eperiod, begdate, enddate, status,corpname){
//	hjcols = new Array();
	var columns = new Array(); 
	var columnsh = new Array();//列及合并列名称
	var columnsb = new Array();//子列表名称集合
	
	$.ajax({
		type : "post",
		dataType : "json",
		url : DZF.contextPath + "/matmanage/matcomm!queryNumber.action",
		traditional : true,
		data : {
			bperiod : bperiod,
			eperiod : eperiod,
			begdate : begdate,
			enddate : enddate,
			status : status,
			corpname : corpname,
		},
		async : false,
		success : function(data) {
			if (data.success) {
				columnsh[0] = { field : 'matbillid',    title : '主键',hidden:true,rowspan:2};
				columnsh[1] = { field : 'updatets',    title : '时间戳', hidden:true,rowspan:2};
				columnsh[2] = { field : 'aname',  title : '大区', width : 100, halign:'center',align:'center',rowspan:2}; 
				columnsh[3] = { field : 'uname',  title : '渠道经理', width : 100, halign:'center',align:'center',rowspan:2}; 
				columnsh[4] = { field : 'proname',  title : '省/市', width : 100, halign:'center',align:'center',rowspan:2}; 
				columnsh[5] = { field : 'corpname',  title : '加盟商', width : 130, halign:'center',align:'center',rowspan:2,
						formatter : function(value, row, index) {
							return '<a href="javascript:void(0)"  style="color:blue" onclick="showDetail(\''
									+ index
									+ '\')">'
									+ value
									+ '</a>';
						}
				}; 
				columnsh[6] = { field : 'code',  title : '合同编号', width : 100, halign:'center',align:'center',rowspan:2}; 
				
				var rows = data.rows;
				var len = 0;
				if(rows != null && rows.length > 0){
					len = rows.length;
					var matbillid;
					var obj;
					var colnm = 0;
					var colfield = "";
					for(var i = 0; i < rows.length; i++){
						if(!onlycol.contains(rows[i].wlname+"/"+rows[i].unit) ){
							
							var wlname = rows[i].wlname;
							var unit = rows[i].unit;
							var column = {};
							column["title"] = wlname+"/"+unit; 
							column["field"] = 'col'+(i+1);  
							column["width"] = '160'; 
							column["colspan"] = 2; 
							
							columnsh.push(column); 
							onlymap.put(rows[i].wlname+"/"+rows[i].unit,(i+1));
							
							var column1 = {};
							column1["title"] = '申请';  
							column1["field"] = 'applynum'+(i+1);  
							column1["width"] = '80';
							column1["halign"] = 'center'; 
							column1["align"] = 'right'; 
							columnsb.push(column1); 
							
//							hjcols.push(new Array('applynum'+(i+1)+'', 0));
							
							var column2 = {};
							column2["title"] = '实发';  
							column2["field"] = 'outnum'+(i+1);  
							column2["width"] = '80'; 
							column2["halign"] = 'center'; 
							column2["align"] = 'right'; 
							columnsb.push(column2); 
							
//							hjcols.push(new Array('outnum'+(i+1)+'', 0));
							
							onlycol.add(rows[i].wlname+"/"+rows[i].unit);
						}
					
					}
				}

				columnsh[7+len] = { field : 'reinfo',  title : '收货信息', width : 280, halign:'center',align:'center',colspan:3}; 
				columnsh[8+len] = { field : 'expressinfo',  title : '快递信息', width : 360, halign:'center',align:'center',colspan:4}; 
				
				columnsh[9+len] = { field : 'memo',  title : '备注', width : 100, halign:'center',align:'center',rowspan:2}; 
				columnsh[10+len] = { field : 'status',  title : '状态', width : 100, halign:'center',align:'center',rowspan:2, 
						formatter : staForma,
				};
        		columnsh[11+len] = { field : 'reason',  title : '驳回原因', width : 100, halign:'center',align:'center',rowspan:2}; 
        		columnsh[12+len] = { field : 'operdate',  title : '录入时间', width : 100, halign:'center',align:'center',rowspan:2}; 
        		columnsh[13+len] = { field : 'applyname',  title : '申请人', width : 100, halign:'center',align:'center',rowspan:2}; 
        		columnsh[14+len] = { field : 'adate',  title : '申请时间', width : 100, halign:'center',align:'center',rowspan:2};
        		
        		columnsb[0+len*2] = {width : '80',title : '收货人',field : 'receiver',align:'center',};
				columnsb[1+len*2] = {width : '100',title : '联系电话',field : 'phone',align:'center',};
				columnsb[2+len*2] = {width : '100',title : '地址',field : 'address',align:'center',};
			 	
				columnsb[3+len*2] = {width : '100',title : '快递公司',field : 'logname',align:'center',};
				columnsb[4+len*2] = {width : '80',title : '金额',field : 'fcost',align:'center',
			 		formatter : function(value,row) {
	            		if(!isEmpty(row.matbillid)){
	            			return formatMny(value);
	            		}
	            	}, 	
			 	};
			 	columnsb[5+len*2] = {width : '80',title : '单号',field : 'fcode',align:'right',};
			 	columnsb[6+len*2] = {width : '100',title : '发货时间',field : 'dedate',align:'right',};
				columns.push(columnsh);
				columns.push(columnsb);
				
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			parent.$.messager.progress('close');
		}
	});
	return columns;
}	

/**
 * 状态格式化
 * @param row
 * @param index
 * @param value
 * @returns {String}
 */
function staForma(value, row, index) {
	if (value == '1')
		return '待审核';
	if (value == '2')
		return '待发货';
	if (value == '3')
		return '已发货';
	if (value == '4')
		return '已驳回';
}

/**
 * 查询框监听事件
 */
function initRadioListen(){
	$('input:radio[name="seledate"]').change( function(){  
		var ischeck = $('#qj').is(':checked');
		if(ischeck){
			$("#bperiod").datebox('readonly',false);
			$("#eperiod").datebox('readonly',false);
			$("#begdate").datebox('readonly',true);
			$("#enddate").datebox('readonly',true);
		}else{
			$("#bperiod").datebox('readonly',true);
			$("#eperiod").datebox('readonly',true);
			$("#begdate").datebox('readonly',false);
			$("#enddate").datebox('readonly',false);
		}
	});
}


/**
 * 清除查询条件
 */
function clearParams(){
	$("#qcorpname").textbox('setValue',null);
	$("#status").combobox('setValue',0);
}

/**
 * 取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 新增修改取消
 */
function onCancel(){
	$('#cbDialog').dialog('close');
}


/**
 * 新增
 */
function add() {
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '新增物料申请单');
	$('#mat_add').form('clear');
	$('#cityname').combobox('loadData', {});//清空市option选项  
	$('#countryname').combobox('loadData', {});//清空县option选项  
	queryAllProvince();
	initCard();
	userInfo();
	editable();
	getLastQuarter();//获取上个季度日期
	operCard();
	$("#applyname").textbox('readonly',true);
	$("#adate").textbox('readonly',true);
    $('.hid').css("display", "none"); 
    $('.xid').css("display", "none");
	$('.bid').css("display", "");
	$('.aid').css("display", "none");
	$('#code').textbox({width:431});
	status = "add";
	addflag = true;
	rowflag = false;
}

/**
 * 查询申请人
 */
function userInfo(){
	$.ajax({
		type : 'POST',
		async : false,
		url : contextPath + '/matmanage/matapply!queryUserData.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				$("#applyname").textbox("setValue", result.rows.applyname);
				$("#adate").datebox("setValue", parent.SYSTEM.LoginDate);
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
 * 卡片表格
 */
function initCard(){
	
	/*var mat = null;
	$.ajax({
		type : 'POST',
		async : false,
		url : contextPath + '/matmanage/matcomm!queryMatFile.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				mat = result.rows;
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});*/

	$('#cardGrid').datagrid({
		striped : true,
		rownumbers : true,
		//fitColumns : true,
		scrollbarSize:0,
		idField : 'matbillid',
		height : 100,
		singleSelect : true,
		columns : [ [ 
		  {
			width : '100',
			title : '物料主键',
			field : 'matfileid',
            halign : 'center',
			align : 'left',
			hidden : true,
			editor : {
				type : 'textbox',
				options : {
					height : 28,
				}
			}
		}, {
			width : '100',
			title : '物料名称',
			field : 'wlname',
			halign : 'center',
			align : 'left',
			editor : {
				type : 'textbox',
				options : {
					height : 28,
					required:true,
					icons: [{
						iconCls:'icon-search',
						handler: function(){
							initMatFile();
						}
					}]
				}
			},
		}, {
			width : '100',
			title : '单位',
			field : 'unit',
            halign : 'center',
			align : 'left',
			editor : {
				type : 'textbox',
				options : {
					height : 28,
					required:true,
				}
			}
		}, {
			width : '91',
			title : '可申请数量',
			field : 'enapplynum',
            halign : 'center',
			align : 'right',
			editor : {
				type : 'numberbox',
				options : {
					height : 28,
					required:true,
					readonly:true,
				}
			}
		}, {
			width : '91',
			title : '申请数量',
			field : 'applynum',
            halign : 'center',
			align : 'right',
			editor : {
				type : 'numberbox',
				options : {
					height : 28,
					required:true,
					min : 1,
				}
			}
		}, {
			width : '91',
			title : '操作',
			field : 'operate',
            halign : 'center',
			align : 'center',
			formatter:coperatorLink
		},
		] ],
		onDblClickRow  :  function(index, row){
			if(status == "brows"){
				return;
			}
			endBodyEdit();
//				if (index != undefined && isEmpty(row.matbillid_b)) {
					$('#cardGrid').datagrid('beginEdit', index);
					editIndex = index;
//				}           		
		} ,
	});

}

function coperatorLink(val,row,index){  
	var add = '<div><a href="javascript:void(0)" id="addBut" onclick="addRow(arguments[0])"><img title="增行" style="margin:0px 20% 0px 20%;" src="../../images/add.png" /></a>';
	var del = '<a href="javascript:void(0)" id="delBut" onclick="delRow(this)"><img title="删行" src="../../images/del.png" /></a></div>';
    return add + del;  
}

/**
 * 增行
 */
function addRow(e) {
	//e.stopPropagation();
	endBodyEdit();
	if (status == 'brows') {
		return;
	}
	if (isCanAdd()) {
		$('#cardGrid').datagrid('appendRow', {});
		editIndex = $('#cardGrid').datagrid('getRows').length -1;
		$('#cardGrid').datagrid('beginEdit', editIndex);
		rowflag = true;
	} 
	
	
}



/**
 * 删行
 */
function delRow(ths) {
	endBodyEdit();
	if (status == 'brows') {
		return;
	}
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	if (tindex == editIndex) {
		var rows = $('#cardGrid').datagrid('getRows');
		if (rows && rows.length > 1) {
			$('#cardGrid').datagrid('deleteRow', Number(tindex)); // 将索引转为int型，否则，删行后，剩余行的索引不重新排列
		}
	} else {
		if (isCanAdd()) {
			var rows = $('#cardGrid').datagrid('getRows');
			if (rows && rows.length > 1) {
				$('#cardGrid').datagrid('deleteRow', Number(tindex)); // 将索引转为int型，否则，删行后，剩余行的索引不重新排列
			}
		} 
	}
}

/**
 * 能否增行
 * 
 * @returns {Boolean}
 */
function isCanAdd() {
	if (editIndex == undefined) {
		return true;
	}
	if ($('#cardGrid').datagrid('validateRow', editIndex)) {
		$('#cardGrid').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}


/**
 * 行编辑结束事件
 */
function endBodyEdit(){
    var rows = $("#cardGrid").datagrid('getRows');
 	for ( var i = 0; i < rows.length; i++) {
 		$("#cardGrid").datagrid('endEdit', i);
 	}
};

/**
 * 查询所有的省
 */
function queryAllProvince(){
			$.ajax({
				type : 'POST',
				async : false,
			    url : DZF.contextPath + '/matmanage/matcomm!queryAllProvince.action',
				dataTye : 'json',
				success : function(result) {
					var result = eval('(' + result+ ')');
					if (result.success) {
						$("#pname").combobox('loadData',result.rows);
						
					} else {
						Public.tips({content : result.msg,type : 2});
					}
				}
			});
}


function querycity(provinceid){
		      $.ajax({
		    	    type : 'POST',
					async : false,
				    url : DZF.contextPath + '/matmanage/matcomm!queryCityByProId.action',
					dataTye : 'json',
					data : "provinceid="+provinceid,
					success : function(result) {
						var result = eval('(' + result+ ')');
						if (result.success) {
							$("#cityname").combobox('loadData',result.rows);
						} else {
							Public.tips({content : result.msg,type : 2});
						}
					}
		   }); 
	
}


function queryCountry(cityid){
	        $.ajax({
	    	    type : 'POST',
				async : false,
			    url : DZF.contextPath + '/matmanage/matcomm!queryAreaByCid.action',
				dataTye : 'json',
				data : "cityid="+cityid,
				success : function(result) {
					var result = eval('(' + result+ ')');
					if (result.success) {
						
						$("#countryname").combobox('loadData',result.rows);
					} else {
						Public.tips({content : result.msg,type : 2});
					}
				}
	   }); 
}


$(function(){ 
	 //触发省选项  
	 $("#pname").combobox({ 
		 onSelect:function(record){  
			$("#cityname").combobox("setValue",''); //清空市  
			$("#countryname").combobox("setValue",''); //清空县  
		    querycity($('#pname').combobox('getValue'));
			$('#vprovince').val($('#pname').combobox('getValue'));
		}         
	 });
	 $("#cityname").combobox({  
	 	onShowPanel:function(record){
			if(!isEmpty($('#vprovince').val())){
				querycity($('#vprovince').val());
			}
		}         
	 });

    //触发市选项  
     $("#cityname").combobox({  
	    onSelect:function(record){
		 	$("#countryname").combobox("setValue",''); //清空县  
			queryCountry($('#cityname').combobox('getValue'));
		 	$('#vcity').val($('#cityname').combobox('getValue'));
	 	}         
    });

	$("#countryname").combobox({  
	 	onShowPanel:function(record){
			if(!isEmpty($('#vcity').val())){
				queryCountry($('#vcity').val());
			}
		}         
	 });


});		
	
/**
 * 加盟商参照初始化
 */
function initRef(){
    $('#corpnm').textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
            	$('<div></div>').dialog({
            	    id : 'chnDlg',
                    width: 600,
                    height: 480,
                    readonly: true,
                    singleSelect : true,
                    title: '选择加盟商',
                    modal: true,
                    href: DZF.contextPath + '/ref/corpchannel_select.jsp',
                    queryParams : {
                    	issingle : true
    				},
    				buttons : [ {
    					text : '确认',
    					handler : function() {
    						selectCorps();
    					}
    				}, {
    					text : '取消',
    					handler : function() {
    						$("#chnDlg").dialog('close');
    					}
    				} ]
                });
            }
        }]
    });
}


/**
 * 加盟商选择事件
 */
function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}


/**
 * 双击选择加盟商
 * @param rowTable
 */
function dClickCompany(rowTable){
	var str = "";
	var corpid="";
	//var corpIds = [];
	
	if(rowTable){
		if(rowTable.length>1){
			Public.tips({content : "只能选择1个加盟商!" ,type:2});
			return;
		}
		/*for(var i=0;i<rowTable.length;i++){
			if(i == rowTable.length - 1){
				str += rowTable[i].uname;
			}else{
				str += rowTable[i].uname+",";
			}
			corpIds.push(rowTable[i].pk_gs);
		}*/
		str = rowTable[0].uname;
		corpid = rowTable[0].pk_gs;
		
		$("#corpnm").textbox("setValue",str);
		$("#fcorp").val(corpid);
		var fcorp=	$("#fcorp").val();
		showApplyData(fcorp);
	}
	// $("#chnDlg").dialog('close');
	 $('#chnDlg').panel('destroy');
}

/**
 * 物料档案参照初始化
 */
function initMatFile(){
	 $('<div></div>').dialog({
		 id : 'matDlg',
         width: 600,
         height: 480,
         readonly: true,
         title: '选择物料',
         modal: true,
         href: DZF.contextPath + '/ref/matfile_select.jsp',
         queryParams : {
        	 issingle : false
			},
			buttons : [ {
				text : '确认',
				handler : function() {
					selectMat();
				}
			}, {
				text : '取消',
				handler : function() {
					$("#matDlg").dialog('close');
				}
			} ]
     });
}



/**
 * 物料选择事件
 */
function selectMat(){
	var rows = $('#matTable').datagrid('getChecked');
	dClickMat(rows);
}


/**
 * 双击选择物料
 * @param rowTable
 */
function dClickMat(rowTable){
	
	if(rowTable.length>0){
		//endBodyEdit();
		var rows = $('#cardGrid').datagrid('getRows');
		var len = rows.length;
		var rowIndex = $('#cardGrid').datagrid('getRowIndex',$('#cardGrid').datagrid('getSelected'));
		var r = 0;
		var w = 0;
		if(rowflag){
			len = len -1;
			rowIndex = rowIndex+1;
			r = rowIndex;
			w = rowIndex;
			rowflag = false;
		}else{
			r = rowIndex+1;
			w = rowIndex+1;
		}
		
		var names = [];
			for(var i=0;i<len;i++){
				if(rows[i].matfileid!=null){
					names.push(rows[i].wlname);
				}
			}
			if(!flag){
				names.pop();
			}
			for(var j=0;j<rowTable.length;j++){
				if(names.indexOf(rowTable[j].wlname)!=-1){
					Public.tips({content : "不能选择重复物料",type : 2});
					return;
				}
			}
			
			var i = 0;
			if(addflag){
				for(var j=len-1;j<rowTable.length;j++){
					
					var wlname = $('#cardGrid').datagrid('getEditor', {index:j,field : 'wlname'});
					var unit = $('#cardGrid').datagrid('getEditor', {index:j,field : 'unit'});
					var matfileid = $('#cardGrid').datagrid('getEditor', {index:j,field : 'matfileid'});
					var enapplynum = $('#cardGrid').datagrid('getEditor', {index:j,field : 'enapplynum'});
					var appnum = $('#cardGrid').datagrid('getEditor', {index:j,field : 'applynum'});
					
					$(wlname.target).textbox('setValue', rowTable[i].wlname);
					$(unit.target).textbox('setValue', rowTable[i].unit);
					$(matfileid.target).textbox('setValue', rowTable[i].matfileid);
					$(enapplynum.target).textbox('setValue', rowTable[i].enapplynum);
					$(appnum.target).textbox('setValue', 1);
					
					if(i!=rowTable.length-1){
						addRow();
						i++;
					}
					
				}
				addflag = false;
				rowflag = false;
			}else{
				if(rowIndex+1<=len){
					$('#cardGrid').datagrid('deleteRow',rowIndex );
					for(var i=0;i<rowTable.length;i++){
						$('#cardGrid').datagrid('insertRow',{
						    index: rowIndex, 
						    row: {
						    	matfileid : rowTable[i].matfileid,
						    	wlname : rowTable[i].wlname,
						    	unit : rowTable[i].unit,
						    	enapplynum : rowTable[i].enapplynum,
						    	applynum : 1,
						    }
						});
					}
				}else{
					for(var j=r;j<rowTable.length+w;j++){
						var wlname = $('#cardGrid').datagrid('getEditor', {index:j,field : 'wlname'});
						var unit = $('#cardGrid').datagrid('getEditor', {index:j,field : 'unit'});
						var matfileid = $('#cardGrid').datagrid('getEditor', {index:j,field : 'matfileid'});
						var enapplynum = $('#cardGrid').datagrid('getEditor', {index:j,field : 'enapplynum'});
						var appnum = $('#cardGrid').datagrid('getEditor', {index:j,field : 'applynum'});
						
						$(wlname.target).textbox('setValue', rowTable[i].wlname);
						$(unit.target).textbox('setValue', rowTable[i].unit);
						$(matfileid.target).textbox('setValue', rowTable[i].matfileid);
						$(enapplynum.target).textbox('setValue', rowTable[i].enapplynum);
						$(appnum.target).textbox('setValue', 1);
						
						if(i!=rowTable.length-1){
							addRow();
							i++;
						}
					}
					addflag = false;
					rowflag = false;
				}
			}
	}
	
	// $("#matDlg").dialog('close');
	 $('#matDlg').panel('destroy');
	 endBodyEdit();
}


/**
 * 联动显示申请信息
 */
function showApplyData(fcorp){
	
	 $.ajax({
 	    type : 'POST',
			async : false,
		    url : DZF.contextPath + '/matmanage/matapply!showDataByCorp.action',
			dataTye : 'json',
			data : "fcorp="+fcorp,
			success : function(result) {
				var result = eval('(' + result+ ')');
				if (result.success) {
					var row=result.rows;
					if(row!=undefined){
						$('#pname').combobox({  
							 readonly : false,
						     editable:false,
						     valueField:'vprovince',    
						     textField:'pname',
						});
						
						$('#cityname').combobox({  
							 //readonly : true,
						     editable:false,
						     valueField:'vcity',    
						     textField:'cityname',
						});
						
						$('#countryname').combobox({  
							 //readonly : true,
						     editable:false,
						     valueField:'varea',    
						     textField:'countryname',
						});
						showArea(row);
						
						$('#vprovince').val(row.vprovince);
						$('#vcity').val(row.vcity);
						$('#varea').val(row.varea);
						if(row.varea==undefined){
							$('#varea').val();
						}
						$('#receiver').textbox("setValue",row.receiver);
						$('#phone').textbox("setValue",row.phone);
						$('#unit').textbox("setValue",row.unit);
					}
					
				} else {
					Public.tips({content : result.msg,type : 2});
				}
			}
  }); 
	
}

function showArea(row){
	$('#pname').combobox('setValue',row.vprovince);
	$('#pname').combobox('setText',row.pname);
	
	$('#cityname').combobox('setValue',row.vcity);
	$('#cityname').combobox('setText',row.cityname);
	
	$('#countryname').combobox('setValue',row.varea);
	$('#countryname').combobox('setText',row.countryname);
}


/**
 * 编辑
 */
function edit(index){
	status = "edit";
	var erow = $('#grid').datagrid('getSelected');
	if(erow==null){
		Public.tips({content:'请选择数据行',type:2});
		return;
	}
	var row = queryByID(erow.matbillid);
	if(row.status == 2 || row.status ==3){
		Public.tips({content:'只有待审核或已驳回状态的申请单支持修改',type:2});
		return;
	}
	if(isEmpty(row)){
		return;
	}
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '编辑物料申请单');
	$('#mat_add').form('clear');
	$('#mat_add').form('load', row);
	showArea(row);
	$("#code").textbox('readonly',false);
	$("#stat").textbox('readonly',false);
	$("#corpnm").textbox('readonly',false);
	$("#pname").combobox('readonly',false);
	$("#cityname").combobox('readonly',false);
	$("#countryname").combobox('readonly',false);
	$("#address").textbox('readonly',false);
	$("#receiver").textbox('readonly',false);
	$("#phone").textbox('readonly',false);
	$("#memo").textbox('readonly',false);
	$('#applyname').textbox('readonly',true);
	$("#adate").textbox('readonly',true);
	
	queryAllProvince();
	initCard();
	operCard();
	$('.hid').css("display", "none"); 
	$('.xid').css("display", "none");
    $('.bid').css("display", "none"); 
    $('.aid').css("display", ""); 
    $('#code').textbox({width:431});
    
	if(row.children != null && row.children.length > 0){
		$('#cardGrid').datagrid('loadData',row.children);
	}
	
}

function operCard(){
	$('#cardGrid').datagrid('loadData', {// 清除缓存数据
		total : 0,
		rows : []
	});
	$('#cardGrid').datagrid('appendRow', {});
	editIndex = $('#cardGrid').datagrid('getRows').length - 1;
	$('#cardGrid').datagrid('beginEdit', editIndex);
}

/**
 * 通过主键查询申请单信息
 */
function queryByID(matbillid){
	var row;
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url : contextPath + '/matmanage/matapply!queryById.action',
		data : {
			"id" : matbillid,
		},
		success : function(data) {
			if (!data.success) {
				Public.tips({
					content :  data.msg,
					type : 1
				});	
				return;
			} else {
				row = data.rows;
			}
		},
	});
	return row;
}

/**
 * 删除
 * @param ths
 */
function del(ths){
	var row = $('#grid').datagrid('getSelected');
	if(row==null){
		Public.tips({content:'请选择数据行',type:2});
		return;
	}
	var rrow= queryByID(row.matbillid);
	if(rrow.status == 2 || rrow.status ==3){
		Public.tips({content:'只有待审核或已驳回状态的申请单支持删除',type:2});
		return;
	}
	$.messager.confirm("提示", "你确定删除吗？", function(flag) {
		if (flag) {
			$.ajax({
				type : "post",
				dataType : "json",
				url : contextPath + '/matmanage/matapply!delete.action',
				data : row,
				traditional : true,
				async : false,
				success : function(data) {
					if (!data.success) {
						Public.tips({
							content : data.msg,
							type : 1
						});
					} else {
						load(0);
						$('#grid').datagrid('clearSelections');
						Public.tips({
							content : data.msg,
						});
					}
				},
			});
		} else {
			return null;
		}
	});
}

/**
 * 保存
 */
function onSave(t){
	
	endBodyEdit();
	
	var postdata = new Object();
	var body = "";
	var name = [];
	//物料数据
	var rows = $('#cardGrid').datagrid('getRows');
	for(var j = 0;j< rows.length; j++){
		body = body + JSON.stringify(rows[j]); 
	}
	postdata["body"] = body;
	
	if(t==1){//新增保存
		for(var i = 0; i < rows.length; i++){
			if((parseInt(rows[i].enapplynum) < parseInt(rows[i].applynum)
					&& parseInt(rows[i].enapplynum) >= 0) || 
					(parseInt(rows[i].enapplynum)<0)){
				name.push(rows[i].wlname);
			}
		}
		if(!isEmpty(name)){
			for(var i=0;i<name.length;i++){
				Public.tips({
					content : name+"可申请数量不足，请调整",
					type : 2
				});
			}
			flag = false;
			return;
		}
		
		onSaveSubmit(postdata,t);
	}else{
		onSaveSubmit(postdata,null);
	}
	
	
}


/**
 * 物料申请单-提交后台保存
 */
function onSaveSubmit(postdata,t){
	setValue();
	if ($("#mat_add").form('validate')) {
		$('#mat_add').form('submit', {
			url : DZF.contextPath + '/matmanage/matapply!save.action',
			queryParams : postdata,
			success : function(result) {
				var result = eval('(' + result + ')');
				var rows = result.rows;
				if(rows!=undefined){
						if(result.msg == "提示"){
							$.messager.confirm("注意",rows, function(flag) {
								if (flag) {
									$('#stype').val(1);
									if(t==1){
										$('#kind').val(1);
									}
									setValue();
									//申请
									if ($("#mat_add").form('validate')) {
										$('#mat_add').form('submit', {
											url : DZF.contextPath + '/matmanage/matapply!save.action',
											queryParams : postdata,
											success : function(result) {
												var result = eval('(' + result + ')');
												if (result.success) {
													$('#cbDialog').dialog('close');
													load(0);
													Public.tips({
														content : result.msg,
													});
												} else {
													Public.tips({
														content : result.msg,
														type : 2
													});
												}
											}
										});
									}
								} else {
									return null;
								}
							});
						}
				}
				
				
				if (result.success) {
					$('#cbDialog').dialog('close');
					load(0);
					Public.tips({
						content : result.msg,
					});
				} else {
					/*Public.tips({
						content : result.msg,
						type : 2
					});*/
				}
			}
		});
	} else {
		Public.tips({
			content : "必输信息为空或信息输入不正确",
			type : 2
		});
		return; 
	}
}

/**
 * 详情页面修改
 */
function updateData(){
	var status = $('#stat').val();
	if(status == "待发货" || status == "已发货"){
		Public.tips({
			content: '只有待审核或已驳回状态的申请单支持修改',
			type: 2
		});
		return;
	}
	
	editable();
	$('.xid').css("display", "none");
	$('.bid').css("display", "none"); 
	$('.aid').css("display", ""); 
	$('#stat').textbox('readonly',true);
	$('#adate').datebox('readonly',true);
	$('#applyname').textbox('readonly',true);
	$('#cardGrid').datagrid('showColumn','enapplynum');
	$('#cardGrid').datagrid('showColumn','operate');
	var col = $('#cardGrid').datagrid('getColumnOption', 'applynum');//获取Column
	col.width = 112;
	
}


function showDetail(index){
	status = "show";
	var erow = $('#grid').datagrid('getData').rows[index];
	var row = queryByID(erow.matbillid);
	
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '物料申请详情');
	queryAllProvince();
	initCard();
	$('#cardGrid').datagrid('hideColumn','enapplynum');
	$('.hid').css("display", "");
	$('.xid').css("display", "");
	$('.bid').css("display", "none");
	$('.aid').css("display", "none");
	
	$('#code').textbox({width:168});
	
	if(row.status!=4){
		$('#reject').css("display", "none"); 
	}
	
	$('#mat_add').form('clear');
	$('#mat_add').form('load', row);
	showArea(row);
	
	if(row.status==1){
		$('#stat').textbox('setValue','待审核');
	}else if(row.status==2){
		$('#stat').textbox('setValue','待发货');
	}else if(row.status==3){
		$('#stat').textbox('setValue','已发货');
	}else if(row.status==4){
		$('#stat').textbox('setValue','已驳回');
	}
	
	
	$('#applyname').textbox('setValue',row.applyname);
	
	//if(row.status==2 || row.status==3){
		readonly();
	//}
	
	if(row.children != null && row.children.length > 0){
		$('#cardGrid').datagrid('loadData',row.children);
		$('#cardGrid').datagrid('hideColumn','operate');
	}
	
}

/**
 * 获取上个季度日期
 */
function getLastQuarter(){ 
	
	var y = new Date().getFullYear();  //当前年份
	var m = new Date().getMonth();  //当前月份
	//0（一月） 到 11（十二月）
	var q = parseInt(m / 3);  //当前季度
	var qs = new Date(y, (q - 1) * 3, 1);  //上一季度的开始日期
	var qe = new Date(y, q * 3, 0);  //上一季度的结束日期
	
	document.getElementById("debegdate").value = qs;
    document.getElementById("deenddate").value = qe;
    
}  

function readonly(){
	$("#code").textbox('readonly',true);
	$("#stat").textbox('readonly',true);
	$("#corpnm").textbox('readonly',true);
	$("#pname").combobox('readonly',true);
	$("#cityname").combobox('readonly',true);
	$("#countryname").combobox('readonly',true);
	//$("#address").attr('readonly','readonly');
	$("#address").textbox('readonly',true);
	$("#receiver").textbox('readonly',true);
	$("#phone").textbox('readonly',true);
	//$("#memo").attr('readonly','readonly');
	$("#memo").textbox('readonly',true);
	$("#applyname").textbox('readonly',true);
	$("#adate").textbox('readonly',true);
	$("#adate").datebox('readonly',true);
	
	
}

function editable(){
	$("#code").textbox('readonly',false);
	$("#stat").textbox('readonly',false);
	$("#corpnm").textbox('readonly',false);
	$("#pname").combobox('readonly',false);
	$("#cityname").combobox('readonly',false);
	$("#countryname").combobox('readonly',false);
	$("#receiver").textbox('readonly',false);
	$("#phone").textbox('readonly',false);
	$("#applyname").textbox('readonly',false);
	$("#adate").textbox('readonly',false);
	$("#address").textbox('readonly',false);
	$("#memo").textbox('readonly',false);
	
}

/**
 * 导出  type:1:申请表 2：审核表 3:处理表
 */
function doExport(){
	var datarows = $('#grid').datagrid("getRows");
	if (datarows == null || datarows.length == 0) {
		Public.tips({
			content: '当前界面数据为空',
			type: 2
		});
		return;
	}
		var hblcols = $('#grid').datagrid("options").columns[0];//  title+field名称
		
		var cols = $('#grid').datagrid('getColumnFields');  // 字段编码
		Business.getFile(DZF.contextPath + "/matmanage/matcomm!exportAuditExcel.action", {
			"strlist": JSON.stringify(datarows),
			'hblcols':JSON.stringify(hblcols), 
			'cols':JSON.stringify(cols),
			'type': 1,
		}, true, true);
		
}

/**
 * 设置省市区值
 */
function setValue(){
	var reg = /^[0-9]+.?[0-9]*$/;
	var pname = $('#pname').combobox('getValue');
	var cityname = $('#cityname').combobox('getValue');
	var countryname = $('#countryname').combobox('getValue');
	if(reg.test(pname)){
		var text = $('#pname').combobox('getText');
		$('#pname').combobox('setValue',text);
		$('#vprovince').val(pname);
	}
	if(reg.test(cityname)){
		var text = $('#cityname').combobox('getText');
		$('#cityname').combobox('setValue',text);
		$('#vcity').val(cityname);
	}
	if(reg.test(countryname)){
		var text = $('#countryname').combobox('getText');
		$('#countryname').combobox('setValue',text);
		$('#varea').val(countryname);
	}
}




