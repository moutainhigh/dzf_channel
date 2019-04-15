var contextPath = DZF.contextPath;
//var hjcols = null;//汇总列字段
var editIndex = undefined;
var status="brows";

$(function(){
	initQry();
	initCombobox();
	initLogistics();
	initRadioListen();
	load(0);
});


function review(){
	if($('#false').is(':checked')){
    	$('#reason').textbox({required:true});
    	$('#reason').textbox({prompt:'最多输入100字'});
    }
	if($('#true').is(':checked')){
    	$('#reason').textbox({required:false});
    	$('#reason').textbox({prompt:''});
    }
}

function initCombobox(){
	$("#uid").combobox({
		onShowPanel: function () {
			initUname();
        }
    })
}

/**
 * 查询渠道经理下拉
 */

function initUname(){
	$.ajax({
		type : 'POST',
		async : false,
	    url : DZF.contextPath + '/matmanage/matcheck!queryComboBox.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result+ ')');
			if (result.success) {
				$('#uid').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
};

/**
 * 初始化快递公司
 */
function initLogistics(){
	$("#logname").combobox({
		onShowPanel: function () {
			initLogName();
        }
    })
}

/**
 * 查询快递公司下拉
 */

function initLogName(){
	$.ajax({
		type : 'POST',
		async : false,
	    url : DZF.contextPath + '/matmanage/mathandle!queryComboBox.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result+ ')');
			if (result.success) {
				$('#logname').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
};

/**
 * 查询初始化
 */
function initQry(){
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	/*$("#begdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#enddate").datebox("setValue",parent.SYSTEM.LoginDate);
	$("#jqj").html(parent.SYSTEM.PreDate+" 至  "+parent.SYSTEM.LoginDate);
	$("#bperiod").datebox("setValue", parent.SYSTEM.PreDate);
	$("#eperiod").datebox("setValue",parent.SYSTEM.LoginDate);*/
	
	$("#begdate").datebox("setValue", null);
	$("#enddate").datebox("setValue",null);
	//$("#jqj").html(parent.SYSTEM.PreDate+" 至  "+parent.SYSTEM.LoginDate);
	$("#bperiod").datebox("setValue", null);
	$("#eperiod").datebox("setValue", null);
	
}



/**
 * 表格初始化
 * @param type  0：初始化界面加载；1；查询；2：待审核；3：待发货
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
		status = 2;
	}
	
	var corpname = $("#qcorpname").val();
	var uid = $("#uid").combobox('getValue');
	if($('#qj').is(':checked')){
		bperiod = $("#bperiod").datebox('getValue');
		eperiod = $("#eperiod").datebox('getValue');
		/*if(isEmpty(bperiod)){
			Public.tips({
				content : "申请开始日期不能为空",
				type : 2
			});
			return;
		}
		if(isEmpty(eperiod)){
			Public.tips({
				content : "申请结束日期不能为空",
				type : 2
			});
			return;
		}*/
		if(!isEmpty(bperiod) && !isEmpty(eperiod)){
			$('#jqj').html(bperiod + ' 至 ' + eperiod);
		}
	}else{
		begdate = $("#begdate").datebox('getValue');
		enddate = $("#enddate").datebox('getValue');
		/*if(isEmpty(begdate)){
			Public.tips({
				content : "录入开始日期不能为空",
				type : 2
			});
			return;
		}
		if(isEmpty(enddate)){
			Public.tips({
				content : "录入结束日期不能为空",
				type : 2
			});
			return;
		}*/
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
			uid : uid,
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
//							    '<a href="#" style="margin-bottom:0px;color:blue;" onclick="showDetail()">'
//							    +rows[i].corpname
//							    +'</a>';//加盟商
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
//									'<a href="#" style="margin-bottom:0px;color:blue;" onclick="showDetail()">'
//								    +rows[i].corpname
//								    +'</a>';//加盟商
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
		url : DZF.contextPath + "/matmanage/matapply!queryNumber.action",
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
							column["width"] = '140'; 
							column["colspan"] = 2; 
							
							columnsh.push(column); 
							onlymap.put(rows[i].wlname+"/"+rows[i].unit,(i+1));
							
							var column1 = {};
							column1["title"] = '申请';  
							column1["field"] = 'applynum'+(i+1);  
							column1["width"] = '70';
							column1["halign"] = 'center'; 
							column1["align"] = 'right'; 
							columnsb.push(column1); 
							
//							hjcols.push(new Array('applynum'+(i+1)+'', 0));
							
							var column2 = {};
							column2["title"] = '实发';  
							column2["field"] = 'outnum'+(i+1);  
							column2["width"] = '70'; 
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
	$("#uid").combobox('setValue',null);
}

/**
 * 取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 审核取消
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
	//queryAllProvince();
	initCard();
	operCard();
    $('.hid').css("display", "none"); 
	
	status = "add";
}

/**
 * 卡片表格
 */
function initCard(){
	var mat = null;
	$.ajax({
		type : 'POST',
		async : false,
		url : contextPath + '/matmanage/matfile!queryMatFile.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				mat = result.rows;
				$("#applyname").textbox("setValue", mat[0].applyname);
				$("#adate").datebox("setValue", parent.SYSTEM.LoginDate);
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});

	$('#cardGrid').datagrid({
		striped : true,
		rownumbers : true,
		fitColumns : true,
		/*onClickCell: onClickCell,
	    onAfterEdit: onAfterEdit,*/
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
			
		}, {
			width : '100',
			title : '物料名称',
			field : 'wlname',
			halign : 'center',
			align : 'left',
			
		}, {
			width : '100',
			title : '单位',
			field : 'unit',
            halign : 'center',
			align : 'left',
			
		}, {
			width : '100',
			title : '申请数量',
			field : 'applynum',
            halign : 'center',
			align : 'right',
			
		},{
			width : '100',
			title : '数量',
			field : 'outnum',
            halign : 'center',
			align : 'right',
			editor : {
				type : 'numberbox',
				options : {
					height : 28,
				}
			}
		}, {
			width : '70',
			title : '操作',
			field : 'operate',
            halign : 'center',
			align : 'center',
			formatter:coperatorLink
		},
		] ],
		onClickRow :  function(index, row){
			if(status == "brows"){
				return;
			}
			//endBodyEdit();
//				if (index != undefined && isEmpty(row.matbillid_b)) {
					$('#cardGrid').datagrid('beginEdit', index);
					editIndex = index;
//				}           		
		} ,
		onClickCell: function(index,field,value){
			$(this).datagrid('beginEdit', index);
			//var ed = $(this).datagrid('getEditor', {index:index,field:field});
	  
		/*var editors = $(this).datagrid('getEditors', index);
        var sfgzEditor = editors[0];
        sfgzEditor.target.bind('blur', function () {
                        $('this').datagrid('selectRow', index)
							.datagrid('endEdit', index);
		
		  });*/
	   },
   });
	
}

function coperatorLink(val,row,index){  
	var add = '<div><a href="javascript:void(0)" id="addBut" onclick="addRow(arguments[0])"><img title="增行" style="margin:0px 20% 0px 20%;" src="../../images/add.png" /></a>';
	var del = '<a href="javascript:void(0)" id="delBut" onclick="delRow(this)"><img title="删行" src="../../images/del.png" /></a></div>';
    return add + del;  
}


/**
 * 发货
 */
function send(){
	var row = $('#grid').datagrid('getSelected');
	if(row == null){
		Public.tips({content : "请选择数据行" ,type:2});
		return;
	}
	var id = row.matbillid;
	var type = 1;
	$.ajax({
	    type : 'POST',
		async : false,
	    url : DZF.contextPath + '/matmanage/matapply!queryById.action',
		dataTye : 'json',
		data : {
			id : id,
			type : type,
			stype : 1,
		},
		success : function(result) {
			var result = eval('(' + result+ ')');
			var row = result.rows;
			
			if(row.msg =="提示"){
				$.messager.confirm("注意", , function(flag) {
					if (flag) {
						showCard(row);
					} else {
						return null;
					}
				});
			}else{
				//不需要提示，直接弹出发货框
				showCard(row);
			}
			
			if (result.success) {
				//if(type==0){
					showCard(row);
				//}
				
		   }
        }
	});
}

function showCard(row){
	if(row.status==2){
		$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '物料发货');
		$('#mat_add').form('clear');
		$('#mat_add').form('load', row);
		 $("#fcost").numberbox({
		      precision:2,
		 });
		 $('#dedate').datebox('setValue',parent.SYSTEM.LoginDate);
		initCard();
	    $('.hid').css("display", "none"); 
	    $('.sid').css("display", ""); 
	    readonly();
	    if(row.children != null && row.children.length > 0){
			$('#cardGrid').datagrid('loadData', row.children);
			$('#cardGrid').datagrid('hideColumn','operate');
			$('#cardGrid').datagrid('showColumn','outnum');
		}
	    
	}else {
		Public.tips({content : "单据状态不为待发货！" ,type:2});
		return;
	}
}

/**
 * 发货保存
 */
function onSave(){
	   // var type = 1;
	   $('#type').val(1);
	    var postdata = new Object();
		var body = "";
		//物料数据
		var rows = $('#cardGrid').datagrid('getRows');
		for(var j = 0;j< rows.length; j++){
			$('#cardGrid').datagrid('endEdit', j);
			body = body + JSON.stringify(rows[j]); 
		}
		postdata["body"] = body;
		
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
	} else {
		Public.tips({
			content : "必输信息为空或信息输入不正确",
			type : 2
		});
		return; 
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
 * 通过主键查询信息
 */
function queryByID(matbillid){
	var row;
	var type = 1;
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url : contextPath + '/matmanage/matapply!queryById.action',
		data : {
			"id" : matbillid,
			type : type,
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


function showDetail(index){
	var erow = $('#grid').datagrid('getData').rows[index];
	var row = queryByID(erow.matbillid);
	
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '物料详情');
	initCard();
	$('.hid').css("display", "");
	$('.sid').css("display", "none");
	
	$('#mat_add').form('clear');
	$('#mat_add').form('load', row);
	$('#cardGrid').datagrid('hideColumn','operate');
	showStatus(row);
	
	$('#applyname').textbox('setValue',row.applyname);
	
	$('#dename').textbox('setValue',row.dename);
	
	readonly();
	
	if(row.children != null && row.children.length > 0){
		$('#cardGrid').datagrid('loadData',row.children);
	}
	
}

/**
 * 显示合同状态
 * @param row
 */
function showStatus(row){
	if(row.status==1){
		$('#stat').textbox('setValue','待审核');
	}else if(row.status==2){
		$('#stat').textbox('setValue','待发货');
	}else if(row.status==3){
		$('#stat').textbox('setValue','已发货');
	}else if(row.status==4){
		$('#stat').textbox('setValue','已驳回');
	}
}


function readonly(){
	$("#code").textbox('readonly',true);
	$("#stat").textbox('readonly',true);
	$("#corpnm").textbox('readonly',true);
	$("#pname").combobox('readonly',true);
	$("#cityname").combobox('readonly',true);
	$("#countryname").combobox('readonly',true);
	$("#address").attr('readonly','readonly');
	$("#receiver").textbox('readonly',true);
	$("#phone").textbox('readonly',true);
	$("#memo").attr('readonly','readonly');
	$("#applyname").textbox('readonly',true);
	$("#adate").textbox('readonly',true);
}

/*function endEditing() {//该方法用于关闭上一个焦点的editing状态
	if (editIndex == undefined) {
		return true
	}
	if ($('#cardGrid').datagrid('validateRow', editIndex)) {
		$('#cardGrid').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}


//点击单元格事件：
function onClickCell(index,field,value) {
	if (endEditing()) {
		if(field=="outnum"){
			$(this).datagrid('beginEdit', index);
			var ed = $(this).datagrid('getEditor', {index:index,field:field});
			$(ed.target).focus();
		}		
		editIndex = index;
	}
	//$('#cardGrid').datagrid('onClickRow');
}


//单元格失去焦点执行的方法
function onAfterEdit(index, row, changes) {
	var updated = $('#cardGrid').datagrid('getChanges', 'updated');
	if (updated.length < 1) {
		editRow = undefined;
		$('#cardGrid').datagrid('unselectAll');
		return;
	} else {
		// 传值
		//submitForm(index, row, changes);
	}
 
	
}
*/
