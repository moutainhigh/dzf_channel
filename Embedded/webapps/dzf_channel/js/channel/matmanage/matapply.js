var contextPath = DZF.contextPath;
var hjcols = null;//汇总列字段
var editIndex;
var status="brows";

$(function(){
	initQry();
	initRef();
	initRadioListen();
	load(0);
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
	$("#begdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#enddate").datebox("setValue",parent.SYSTEM.LoginDate);
	$("#jqj").html(parent.SYSTEM.PreDate+" 至  "+parent.SYSTEM.LoginDate);
	$("#bperiod").datebox("setValue", parent.SYSTEM.PreDate);
	$("#eperiod").datebox("setValue",parent.SYSTEM.LoginDate);
}


/**
 * 表格初始化
 * @param type  0：初始化界面加载；1；查询；
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
	var corpname = $("#qcorpname").val();
	if($('#qj').is(':checked')){
		bperiod = $("#bperiod").datebox('getValue');
		eperiod = $("#eperiod").datebox('getValue');
		if(isEmpty(bperiod)){
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
		}
		
		$('#jqj').html(bperiod + ' 至 ' + eperiod);
	}else{
		begdate = $("#begdate").datebox('getValue');
		enddate = $("#enddate").datebox('getValue');
		if(isEmpty(begdate)){
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
		}
		
		$('#jqj').html(begdate + ' 至 ' + enddate);
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
					var fcorp;
					var obj;
					var colfield = "";
					for(var i = 0; i < rows.length; i++){
						if(i == 0){
							fcorp = rows[i].fcorp;
							obj = {};
							var n=i+1;
							//colnm = onlymap.get(rows[i].wlname);
							colfield = 'applynum'+n;
							obj[colfield] = rows[i].applynum;
							colfield = 'outnum'+n;
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
							
							obj['aname'] = rows[i].aname;//大区
							obj['mname'] = rows[i].mname;//渠道经理
							obj['proname'] = rows[i].proname;//省（市）
							obj['corpname'] = rows[i].corpname;//加盟商
							obj['code'] = rows[i].code;//合同编码
							if(i == rows.length - 1){//一行数据
								//colnm = onlymap.get(rows[i].wlname);
								colfield = 'applynum'+i;
								obj[colfield] = rows[i].applynum;
								colfield = 'outnum'+i;
								obj[colfield] = rows[i].outnum;
								
								
								datarray.push(obj);
							}
						}else{
							var m=i+1;
							if(fcorp == rows[i].fcorp){
								//colnm = onlymap.get(rows[i].wlname);
								colfield = 'applynum'+m;
								obj[colfield] = rows[i].applynum;
								colfield = 'outnum'+m;
								obj[colfield] = rows[i].outnum;
								
								if(i == rows.length - 1){//最后一行数据
									datarray.push(obj);
								}
							}else if(fcorp != rows[i].fcorp){
								datarray.push(obj);
								fcorp = rows[i].fcorp;
								obj = {};
								//colnm = onlymap.get(rows[i].dedmny);
								colfield = 'applynum'+m;
								obj[colfield] = rows[i].applynum;
								colfield = 'outnum'+m;
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
								
								obj['aname'] = rows[i].corpcode;//大区
								obj['mname'] = rows[i].corpname;//渠道经理
								obj['proname'] = rows[i].stocknum;//省（市）
								obj['corpname'] = rows[i].stocknum;//加盟商
								obj['code'] = rows[i].stocknum;//合同编码
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
		singleSelect : false,
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
	hjcols = new Array();
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
				columnsh[0] = { field : 'matbillid',    title : '主键', hidden:true,rowspan:2};
				columnsh[1] = { field : 'updatets',    title : '时间戳', hidden:true,rowspan:2};
				columnsh[2] = { field : 'aname',  title : '大区', width : 100, halign:'center',align:'center',rowspan:2}; 
				columnsh[3] = { field : 'mname',  title : '渠道经理', width : 100, halign:'center',align:'center',rowspan:2}; 
				columnsh[4] = { field : 'proname',  title : '省/市', width : 100, halign:'center',align:'center',rowspan:2}; 
				columnsh[5] = { field : 'corpname',  title : '加盟商', width : 130, halign:'center',align:'center',rowspan:2}; 
				columnsh[6] = { field : 'code',  title : '合同编号', width : 100, halign:'center',align:'center',rowspan:2}; 
				
				var rows = data.rows;
				var len = 0;
				if(rows != null && rows.length > 0){
					len = rows.length;
					var fcorp;
					var obj;
					var colnm = 0;
					var colfield = "";
					for(var i = 0; i < rows.length; i++){
						if(!onlycol.contains(rows[i].wlname)
								&&!onlycol.contains(rows[i].unit)){
							
							var wlname = rows[i].wlname;
							var unit = rows[i].unit;
							var column = {};
							column["title"] = wlname+"/"+unit; 
							column["field"] = 'col'+(i+1);  
							column["width"] = '100'; 
							column["colspan"] = 2; 
							
							columnsh.push(column); 
							onlymap.put(rows[i].wlname+"/"+rows[i].unit,(i+1));
							
							var column1 = {};
							column1["title"] = '申请';  
							column1["field"] = 'applynum'+(i+1);  
							column1["width"] = '50';
							column1["halign"] = 'center'; 
							column1["align"] = 'right'; 
							columnsb.push(column1); 
							
							hjcols.push(new Array('applynum'+(i+1)+'', 0));
							
							var column2 = {};
							column2["title"] = '实发';  
							column2["field"] = 'outnum'+(i+1);  
							column2["width"] = '50'; 
							column2["halign"] = 'center'; 
							column2["align"] = 'right'; 
							columnsb.push(column2); 
							
							hjcols.push(new Array('outnum'+(i+1)+'', 0));
							
							onlycol.add(rows[i].wlname);
							onlycol.add(rows[i].unit);
						}
					
					}
				}

				columnsh[7+len] = { field : 'reinfo',  title : '收货信息', width : 280, halign:'center',align:'center',colspan:3}; 
				columnsh[8+len] = { field : 'expressinfo',  title : '快递信息', width : 360, halign:'center',align:'center',colspan:4}; 
				columnsh[9+len] = { field : 'memo',  title : '备注', width : 100, halign:'center',align:'center',rowspan:2}; 
				
				columnsh[10+len] = { field : 'status',  title : '状态', width : 100, halign:'center',align:'center',rowspan:2, 
                	formatter : function(row,index,value) {
        				if (value == '1')
        					return '待审核';
        				if (value == '2')
        					return '待发货';
        				if (value == '3')
        					return '已发货';
        				if (value == '4')
        					return '已驳回';
        		}};
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
	            			return formFourMny(value);
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
	//$("#").combobox('setValue',null);
}

/**
 * 取消
 */
function closeCx(){
	$("#qrydialog").hide();
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
	initEvent();
	initCard();
	$('#cardGrid').datagrid('loadData', {// 清除缓存数据
		total : 0,
		rows : []
	});
	$('#cardGrid').datagrid('appendRow', {});
	editIndex = $('#cardGrid').datagrid('getRows').length - 1;
	$('#cardGrid').datagrid('beginEdit', editIndex);
   
	status = "add";
	updateBtnState();
}

/**
 * 卡片界面的按钮显示及隐藏
 */
function updateBtnState() {
	/*if ("add" == status) {
		$('#addSave').show();
		$('#addCancel').show();
		$('#getdate').datebox('readonly', false);
		$('#memo').textbox('readonly', false);
		$('#cardGrid').datagrid('showColumn', 'usenum');
	} else if ("edit" == status) {
		$('#addSave').show();
		$('#addCancel').show();
		$('#getdate').datebox('readonly', false);
		$('#memo').textbox('readonly', false);
		$('#cardGrid').datagrid('showColumn', 'usenum');
	} else if ("brows" == status) {
		$('#addSave').hide();
		$('#addCancel').hide();
		$('#getdate').datebox('readonly', true);
		$('#memo').textbox('readonly', true);
		$('#cardGrid').datagrid('hideColumn', 'usenum');
	}*/
}

/**
 * 监听事件
 */
function initEvent(){
	$('#cost').numberbox({// 去除空格
		onChange : function(n, o) {
			if(isEmpty(n)){
				return;
			}
			var _trim = trimStr(n,'g');
			$("#cost").numberbox("setValue", _trim);
		}
	});
	$('#unit').numberbox({// 去除空格
		onChange : function(n, o) {
			if(isEmpty(n)){
				return;
			}
			var _trim = trimStr(n,'g');
			$("#unit").numberbox("setValue", _trim);
		}
	});
	$('#num').numberbox({// 去除空格
		onChange : function(n, o) {
			if(isEmpty(n)){
				return;
			}
			var _trim = trimStr(n,'g');
			$("#num").numberbox("setValue", _trim);
		}
	});
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
				}
			}
		}, {
			width : '100',
			title : '申请数量',
			field : 'applynum',
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
			endBodyEdit();
				if (index != undefined && isEmpty(row.matbillid_b)) {
					$('#cardGrid').datagrid('beginEdit', index);
					editIndex = index;
				}           		
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
	e.stopPropagation();
	endBodyEdit();
	if (status == 'brows') {
		return;
	}
	if (isCanAdd()) {
		$('#cardGrid').datagrid('appendRow', {});
		editIndex = $('#cardGrid').datagrid('getRows').length - 1;
		$('#cardGrid').datagrid('beginEdit', editIndex);
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
			    url : DZF.contextPath + '/matmanage/matapply!queryAllProvince.action',
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


$(function(){                                                            
	 //触发省选项  
	 $("#pname").combobox({  
		 onSelect:function(record){  
		      $("#cityname").combobox("setValue",''); //清空市  
		      $("#countryname").combobox("setValue",''); //清空县  
		      var provinceid=$('#pname').combobox('getValue'); 
		      $.ajax({
		    	    type : 'POST',
					async : false,
				    url : DZF.contextPath + '/matmanage/matapply!queryCityByProId.action',
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
	              
	});
    //触发市选项  
     $("#cityname").combobox({  
	    onSelect:function(record){  
	        $("#countryname").combobox("setValue",''); //清空县  
	        var cityid=$('#cityname').combobox('getValue'); 
	        $.ajax({
	    	    type : 'POST',
				async : false,
			    url : DZF.contextPath + '/matmanage/matapply!queryAreaByCid.action',
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
                $("#chnDlg").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择加盟商',
                    modal: true,
                    href: DZF.contextPath + '/ref/channel_select.jsp',
                    queryParams : {
    					ovince :"-1"
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
		/*if(rowTable.length>300){
			Public.tips({content : "一次最多只能选择300个客户!" ,type:2});
			return;
		}*/
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
	 $("#chnDlg").dialog('close');
}

/**
 * 物料档案参照初始化
 */
function initMatFile(){
	 $("#matDlg").dialog({
         width: 600,
         height: 480,
         readonly: true,
         title: '选择物料',
         modal: true,
         href: DZF.contextPath + '/ref/matfile_select.jsp',
         /*queryParams : {
				ovince :"-1"
			},*/
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
	var rows = $('#matTable').datagrid('getSelections');
	dClickMat(rows);
}


/**
 * 双击选择物料
 * @param rowTable
 */
function dClickMat(rowTable){
	var wlnameValue = "";
	var unitValue="";
	var matid="";
	if(rowTable){
		wlnameValue = rowTable[0].wlname;
		unitValue = rowTable[0].unit;
		matid = rowTable[0].matfileid;
		
		var wlname = $('#cardGrid').datagrid('getEditor', {index:editIndex,field : 'wlname'});
		var unit = $('#cardGrid').datagrid('getEditor', {index:editIndex,field : 'unit'});
		var matfileid = $('#cardGrid').datagrid('getEditor', {index:editIndex,field : 'matfileid'});
		
		$(wlname.target).textbox('setValue', wlnameValue);
		$(unit.target).textbox('setValue', unitValue);
		$(matfileid.target).textbox('setValue', matid);
		
	}
	 $("#matDlg").dialog('close');
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
					$('#pname').combobox({  
						 //readonly : true,
					     editable:false,
					     valueField:'vprovince',    
					     textField:'pname',
					     value:row.pname,
					});
					$('#cityname').combobox({  
						 //readonly : true,
					     editable:false,
					     valueField:'vcity',    
					     textField:'cityname',
					     value:row.cityname,
					});
					$('#countryname').combobox({  
						 //readonly : true,
					     editable:false,
					     valueField:'varea',    
					     textField:'countryname',
					     value:row.countryname,
					});
					$('#vprovince').val(row.vprovince);
					$('#vcity').val(row.vcity);
					$('#varea').val(row.varea);
					if(row.varea==undefined){
						$('#varea').val();
					}
					$('#receiver').textbox("setValue",row.receiver);
					$('#phone').textbox("setValue",row.phone);
					$('#unit').textbox("setValue",row.unit);
				} else {
					Public.tips({content : result.msg,type : 2});
				}
			}
  }); 
	
}


/**
 * 编辑
 */
function edit(index){
	var erow = $('#grid').datagrid('getData').rows[index];
	var row = queryByID(erow.matinid);
	if(isEmpty(row)){
		return;
	}
	
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '编辑入库单');
	$('#matfileid').combobox("readonly",true);
	$('#mat_add').form('clear');
	$('#mat_add').form('load', row);
	
}

/**
 * 通过主键查询入库单信息
 */
function queryByID(matinid){
	var row;
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url : contextPath + '/matmanage/matstockin!queryById.action',
		data : {
			"id" : matinid,
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
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	var row = $('#grid').datagrid('getData').rows[tindex];
	$.messager.confirm("提示", "你确定删除吗？", function(flag) {
		if (flag) {
			$.ajax({
				type : "post",
				dataType : "json",
				url : contextPath + '/matmanage/matstockin!delete.action',
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
						//reloadData();
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
function onSave(){
	endBodyEdit();
	
	var postdata = new Object();
	var body = "";
	//物料数据
	var rows = $('#cardGrid').datagrid('getRows');
	for(var j = 0;j< rows.length; j++){
		body = body + JSON.stringify(rows[j]); 
	}
	postdata["body"] = body;
	
	var adddata = "";
	var deldata = "";
	var upddata = "";
	//新增数据		
	var insRows = $('#cardGrid').datagrid('getChanges', 'inserted');
	if(insRows != null && insRows.length > 0){
		for(var j = 0;j <insRows.length; j++){
			adddata = adddata + JSON.stringify(insRows[j]);					
		}
	}
	//删除数据	
	var delRows = $('#cardGrid').datagrid('getChanges', 'deleted');
	if(delRows != null && delRows.length > 0){
		for(var j = 0;j <delRows.length; j++){
			deldata = deldata + JSON.stringify(delRows[j]);
		}
	}
	//更新数据
	var updRows = $('#cardGrid').datagrid('getChanges', 'updated');
	if(updRows != null && updRows.length > 0){
		for(var j = 0;j< updRows.length; j++){
			upddata = upddata + JSON.stringify(updRows[j]);
		}			
	}
	
	postdata["adddata"] = adddata;
	postdata["upddata"] = upddata;
	postdata["deldata"] = deldata;

	onSaveSubmit(postdata);
}


/**
 * 物料申请单-提交后台保存
 */
function onSaveSubmit(postdata){
	if ($("#mat_add").form('validate')) {
		$('#mat_add').form('submit', {
			url : DZF.contextPath + '/matmanage/matapply!save.action',
			queryParams : postdata,
			success : function(result) {
				var result = eval('(' + result + ')');
				if (result.success) {
					$('#cbDialog').dialog('close');
					//reloadData();
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


