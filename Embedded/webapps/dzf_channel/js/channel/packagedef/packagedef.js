var parentRow;
var editIndex = 0;
var typecode,taxtype,comptype,itype,nmsmny,cylnum,contcycle,pubnum,ispro,citynms,corpnms,memo;
var areaData;
var selmap;
var sellist;
var uidlist;

//自适应边框
$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});
$(function() {
	queryBoxChange('#begdate','#enddate');
	initGrid();
	initDgEditor();
	reloadData();
	
	areamap = new HashMap();
	arealist = new ArrayList();
	aidlist = new ArrayList();
	
	selmap = new HashMap();
	sellist = new ArrayList();
	uidlist = new ArrayList();
});

function initGrid(){
	$('#grid').datagrid({
	    idField: "pid",
//		url : DZF.contextPath + '/channel/packageDef!query.action',
		striped : true,
		width: "auto",
		singleSelect : false,
		rownumbers:true,
		height: Public.setGrid().h,
		columns : [ [
             {
            	 field : 'checkbox',
            	 checkbox: true
     		},{
    			width : '90',
    			title : '操作列',
    			field : 'operate',
                halign : 'center',
    			align : 'center',
    			formatter:coperatorLink
    		},{
    			field : 'typecode',
    			title : '业务类型',
    			width : 90,
    			halign : 'center',
    			align : 'left',
    			formatter: function (value) {
    				var text = "";
    				if (value == 'FW0101')
    					text = "代理记账";
    				return text;
    			},
    		},
    		 {
    			field : 'taxtype',
    			title : '纳税人资格',
    			width : 100,
    			halign : 'center',
    			align : 'left',
    		}, {
    			field : 'comptype',
    			title : '公司类型',
    			width : 100,
    			halign : 'center',
    			align : 'left',
    			formatter : function(value,row,index){
    				if (value == '20')
    					return '个体工商户';
    				return "非个体户";
    			},
       		}, {
    			field : 'itype',
    			title : '套餐',
    			width : 90,
    			halign : 'center',
    			align : 'left',
    			formatter : function(value,row,index){
    				if (value == '0')
    					return '常规';
    				return "非常规";
    			},
    		},{
    			field : 'nmsmny',
    			title : '月服务费',
    			width : 90,
    			align : 'right',
    			halign : 'center',
    			formatter : function(value,row,index){
    				if(value == 0)return "0.00";
    				return formatMny(value);
    			},
    		}, {
    			field : 'cylnum',
    			title : '收费周期(月)',
    			width : 90,
    			halign : 'center',
    			align : 'right',
    		}, {
    			field : 'contcycle',
    			title : '合同期限(月)',
    			width : 90,
    			halign : 'center',
    			align : 'right',
    		}, {
    			field : 'pubnum',
    			title : '发布个数',
    			width : 90,
    			halign : 'center',
    			align : 'right',
    		}, {
    			field : 'dpubdate',
    			title : '发布时间',
    			width : 90,
    			halign : 'center',
    			align : 'center',
    		}, {
    			field : 'offdate',
    			title : '下架时间',
    			width : 90,
    			halign : 'center',
    			align : 'center',
    		}, {
    			field : 'coperatorname',
    			title : '录入人',
    			width : 90,
    			halign : 'center',
    			align : 'left',
    		}, {
    			field : 'vstatus',
    			title : '状态',
    			width : 80,
    			halign : 'center',
    			align : 'center',
    			formatter: function (value) {
    				var text = "";
    				if (value == 1)
    					text = "待发布";
    				else if (value == 2)
    					text = "已发布";
    				else if (value == 3)
    					text = "已下架";
    				return text;
    			},
    		}, {
    			field : 'ispro',
    			title : '是否促销',
    			width : 90,
    			halign : 'center',
    			align : 'center',
				formatter: function (value, row, index) {
					var checked = (value == '是'||value == 'Y') ? "checked" : "";
					return '<input type="checkbox" disabled ' + checked + '/>';
				},
    		}, {
    			field : 'citynms',
    			title : '适用地区',
    			width : 200,
    			halign : 'center',
    			align : 'left',
    			formatter : showTitle
    		}, {
    			field : 'corpnms',
    			title : '适用加盟商',
    			width : 200,
    			halign : 'center',
    			align : 'left',
    			formatter : showTitle
    		},{
    			width : '100',
    			field : 'corpids',
    			title : '适用加盟商主键',
    			hidden : true,
    			editor : {
    				type : 'textbox',
    			}
    		},{
    			width : '100',
    			field : 'cityids',
    			title : '适用地区主键',
    			hidden : true,
    			editor : {
    				type : 'textbox',
    			}
    		},{
    			field : 'memo',
    			title : '备注',
    			width : 300,
    			halign : 'center',
    			align : 'left',
    			formatter : showTitle
    		}, {
    			field : 'doperatedate',
    			title : '录入日期',
       			width : 90,
    			halign : 'center',
    			align : 'center',
    		}
    	] ]
	});
}

function initArea(){
	var cityids = $('#grid').datagrid('getEditor', {index:editIndex,field:'cityids'});
	var tar_cityid;
	if(!isEmpty(cityids)){
		tar_cityid=$(cityids.target).textbox('getValue');
	}
	$("#areaDlg").dialog({
		width: 340,
	    height: 460,
		readonly: true,
		queryParams : {
			'cityids' : tar_cityid,
		},
		title: '选择省市',
		modal: true,
		href: DZF.contextPath + '/ref/mult_area.jsp',
		buttons : [ {
			text : '确认',
			handler : function() {
				areamap = new HashMap();
				seleMultArea();
				$('#areaDlg').dialog('close');
			}
		}, {
			text : '取消',
			handler : function() {
	        	areamap = new HashMap();
	    		arealist = new ArrayList();
	    		aidlist = new ArrayList();
				$('#areaDlg').dialog('close');
			}
		} ]
	});
    $("#areaDlg").dialog({  
        onClose: function () {  
        	areamap = new HashMap();
    		arealist = new ArrayList();
    		aidlist = new ArrayList();
        }  
    }); 
}

function seleMultArea(){
	var texts = "";
	var ids = "";
	if (arealist != null && arealist.size() > 0) {
		for (var i = 0; i < arealist.size(); i++) {
			if (i == 0) {
				texts = arealist.get(i).text;
				ids = arealist.get(i).id;
			} else {
				texts = texts + ';' + arealist.get(i).text;
				ids= ids + ',' + arealist.get(i).id;
			}
		}
	}
	var cityidsO = $('#grid').datagrid('getEditor', {index : editIndex,field : 'cityids'});
	$(cityidsO.target).textbox('setValue', null);
	$(cityidsO.target).textbox('setValue', ids);
	var citynmsO = $('#grid').datagrid('getEditor', {index : editIndex,field : 'citynms'});
	$(citynmsO.target).textbox('setValue',null);
	$(citynmsO.target).textbox('setValue',texts);
}


/**
 * 加盟商参照初始化
 */
function initChnCorp(){
	var corpids = $('#grid').datagrid('getEditor', {index:editIndex,field:'corpids'});
	var tar_corpid;
	if(!isEmpty(corpids)){
		tar_corpid=$(corpids.target).textbox('getValue');
	}
	$("#chnDlg").dialog({
		width: 600,
	    height: 480,
		readonly: true,
		title: '选择加盟商',
		modal: true,
		href: DZF.contextPath + '/ref/select_channels.jsp',
		queryParams:{
			'ovince' : -1,
			'corpid': tar_corpid
		},
		buttons : [ {
			text : '确认',
			handler : function() {
				selmap = new HashMap();
				selectCorps();
				$('#chnDlg').dialog('close');
			}
		}, {
			text : '取消',
			handler : function() {
				selmap = new HashMap();
				sellist = new ArrayList();
				uidlist = new ArrayList();
				$('#chnDlg').dialog('close');
			}
		} ]
	});
    $("#chnDlg").dialog({  
        onClose: function () {  
        	selmap = new HashMap();
    		sellist = new ArrayList();
    		uidlist = new ArrayList();
        }  
    }); 
}

function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}

/**
 * 双击选择加盟商
 * @param rowTable
 */
function dClickCompany(rowTable) {
	var corpIdsN = "";
	var corpNmsN = "";
	if (rowTable) {
		for (var i = 0; i < rowTable.length; i++) {
			if (i == rowTable.length - 1) {
				corpIdsN += rowTable[i].pk_gs;
				corpNmsN += rowTable[i].uname;
			} else {
				corpIdsN += rowTable[i].pk_gs + ",";
				corpNmsN += rowTable[i].uname + ",";
			}
		}
		var corpidsT = $('#grid').datagrid('getEditor', {index : editIndex,field : 'corpids'});
		var corpnmsT = $('#grid').datagrid('getEditor', {index : editIndex,field : 'corpnms'});
		$(corpidsT.target).textbox('setValue', null);
		$(corpidsT.target).textbox('setValue', corpIdsN);
		$(corpnmsT.target).textbox('setValue', null);
		$(corpnmsT.target).textbox('setValue', corpNmsN);
	}
	$("#chnDlg").dialog('close');
}

function initDgEditor(){
	//业务类型
	typecode = {
		type: 'combobox',
		options: {
        	height: 35,
        	panelHeight: 80,
        	showItemIcon: true,
        	valueField: "value",
        	editable: false,
        	textField: "text",
        	data: [{
        		value: 'FW0101',
        		text: '代理记账'
        	},]
        }
    };
    taxtype = {
			type: 'combobox',
               options: {
               		height: 35,
	               	panelHeight: 80,
	               	showItemIcon: true,
	               	valueField: "value",
	               	editable: false,
	               	textField: "text",
	               	data: [{
	               		value: '一般纳税人',
	               		text: '一般纳税人'
	               	},{
	               		value: '小规模纳税人',
	               		text: '小规模纳税人'
	               	},],
	               	required:true,
               }
           };
    comptype = {
    				type: 'combobox',
                    options: {
                    	height: 35,
                    	panelHeight: 80,
                    	showItemIcon: true,
                    	valueField: "value",
                    	editable: false,
                    	textField: "text",
                    	data: [{
                    		value: '20',
                    		text: '个体工商户'
                    	},{
                    		value: '99',
                    		text: '非个体户'
                    	},],
                    	required:true,
                    }
                };
    itype = {
    				type: 'combobox',
                    options: {
                    	height: 35,
                    	panelHeight: 80,
                    	showItemIcon: true,
                    	valueField: "value",
                    	editable: false,
                    	textField: "text",
                    	data: [{
                    		value: '0',
                    		text: '常规'
                    	},{
                    		value: '1',
                    		text: '非常规'
                    	},],
                    	required:true,
                    }
                };
    nmsmny = {
    				type: 'numberbox',
                    options: {
                    	height: 35,
                    	min:0,
                    	precision: 2,
                    	groupSeparator:',',
                    	validType:'length[1,8]',
                    	required:true,
                    }
                };  
    cylnum = {
    				type: 'numberbox',
                    options: {
                    	height: 35,
                    	precision: 0,
                    	min:0,
                    	validType:'length[1,2]',
                    	required:true,
                    }
                };     
    contcycle = {
    				type: 'numberbox',
                    options: {
                    	height: 35,
                    	precision: 0,
                    	min:0,
                    	validType:'length[1,6]',
                    	required:true,
                    }
                };
    pubnum = {
    				type: 'numberbox',
                    options: {
                    	height: 35,
                    	precision: 0,
                    	min:0,
                    	validType:'length[1,6]',
                    }
                };
    ispro = {type:'checkbox',options : {on:'是',off:'否',}};     
    citynms = {
			type : 'textbox',
			options : {
				height:31,
				editable:false,
				icons: [{
					iconCls:'icon-search',
					handler: function(){
						initArea();
					}
				}]
			}
		};
    corpnms = {
			type : 'textbox',
			options : {
				height:31,
				editable:false,
				icons: [{
					iconCls:'icon-search',
					handler: function(){
						initChnCorp();
					}
				}]
			}
		};
    memo = {
			type: 'textbox',
            options: {
            	height: 35,
            }
        };
}

function showTitle(value){
	if(value!=undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}
}


/**
 * 关闭查询对话框
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 查询框-清除
 */
function clearParams(){
	$('#corpkna_ae').combobox('readonly',true);
	$("#pk_account").val(null);
	$('#taxtype').combobox('setValue', "");
	$('#vstatus').combobox('setValue', "-1");
	$("#cylnum").numberbox("setValue",null);
	$("#contcycle").numberbox("setValue",null);
}

/**
 * 查询
 */
function reloadData(){
	var begdate = $("#begdate").datebox('getValue');
	var enddate = $("#enddate").datebox('getValue');
	var taxtype = $("#taxtype").combobox('getValue');
	var vstatus = $("#vstatus").combobox('getValue');
	var comptype = $("#comptype").combobox('getValue');
	var cylnum = $("#cylnum").numberbox('getValue');
	var contcycle = $("#contcycle").numberbox('getValue');
	
	var itype ="";
	if ($("#normal").is(':checked')) {
		itype = "1";
	} 
	if ($("#supple").is(':checked')) {
		itype += "2";
	} 
	var ptype ="";
	if ($("#cx").is(':checked')) {
		ptype = "1";
	} 
	if ($("#fcx").is(':checked')) {
		ptype += "2";
	} 
	
	$('#grid').datagrid('unselectAll');
	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').url =DZF.contextPath + '/channel/packageDef!query.action';
	queryParams.begdate = begdate;
	queryParams.enddate = enddate;
	queryParams.taxtype = taxtype;
	queryParams.vstatus = vstatus;
	queryParams.comptype = comptype;
	if(isEmpty(cylnum)){
		queryParams.cylnum = -1;
	}else{
		queryParams.cylnum = cylnum;
	}
	if(isEmpty(contcycle)){
		queryParams.contcycle = -1;
	}else{
		queryParams.contcycle = contcycle;
	}
	if(isEmpty(itype)){
		itype = "-1";
	}
	if(isEmpty(ptype)){
		ptype = "-1";
	}
	queryParams.itype = itype;
	queryParams.ptype = ptype;
	$('#grid').datagrid('options').queryParams = queryParams;
    $('#grid').datagrid('unselectAll');
	$('#grid').datagrid('reload');
	showButtons("brows");
}

function addType () {
	showButtons("add");
	$('#grid').datagrid('insertRow',{index: 0,	// 索引从0开始
		row: {typecode:'FW0101'}
	});

	var typecodeO = $('#grid').datagrid('getColumnOption', 'typecode');
	typecodeO.editor=typecode;
	var taxtypeO = $('#grid').datagrid('getColumnOption', 'taxtype');
	taxtypeO.editor=taxtype;
	var comptypeO = $('#grid').datagrid('getColumnOption', 'comptype');
	comptypeO.editor=comptype;
	var itypeO = $('#grid').datagrid('getColumnOption', 'itype');
	itypeO.editor=itype;
	var nmsmnyO = $('#grid').datagrid('getColumnOption', 'nmsmny');
	nmsmnyO.editor=nmsmny;
	var cylnumO = $('#grid').datagrid('getColumnOption', 'cylnum');
	cylnumO.editor=cylnum;
	var contcycleO = $('#grid').datagrid('getColumnOption', 'contcycle');
	contcycleO.editor=contcycle;
	var pubnumO = $('#grid').datagrid('getColumnOption', 'pubnum');
	pubnumO.editor=pubnum;
	var isproO = $('#grid').datagrid('getColumnOption', 'ispro');
	isproO.editor=ispro;
	var citynmsO = $('#grid').datagrid('getColumnOption', 'citynms');
	citynmsO.editor=citynms;
	var corpnmsO = $('#grid').datagrid('getColumnOption', 'corpnms');
	corpnmsO.editor=corpnms;
	var memoO = $('#grid').datagrid('getColumnOption', 'memo');
	memoO.editor=memo;

	$('#grid').datagrid("beginEdit", 0);
	editIndex = 0;
}

function save () {
	var flag = endEdit();
	if(!flag){
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return; 
	}
	var submitData = getSubmitData();
	$.ajax({
		type: "POST",
        dataType: "json",
        url: DZF.contextPath + "/channel/packageDef!save.action",
        data: {submitData: submitData},
        success: function(rs) {
        	if (rs.success) {
        		showButtons("brows");
        		$('#grid').datagrid('unselectAll');
        		$("#grid").datagrid("reload");
                Public.tips({content: rs.msg,type:0});
			} else {
				$("#grid").datagrid("beginEdit", editIndex);
				 Public.tips({content: rs.msg,type:1});
			}
        }
	});
}


function del() {
	var rows = $("#grid").datagrid("getChecked");
	if(rows != null && rows.length > 0){
		$.messager.confirm("确认", "确认删除该套餐吗", function (r) {
			if (r) {
				$.ajax({
					type: "POST",
			        dataType: "json",
			        url: DZF.contextPath + "/channel/packageDef!delete.action",
			        data : {
						"deldata" : JSON.stringify(rows)
					},
			        success: function(rs) {
			        	if (rs.success) {
//							var index = $("#grid").datagrid("getRowIndex", row);
//							$("#grid").datagrid("deleteRow",index);
			        		$('#grid').datagrid('unselectAll');
			        		$("#grid").datagrid("reload");
			                Public.tips({content: rs.msg,type:0});
						} else {
							 Public.tips({content: rs.msg,type:1});
						}
			        }
				});
			}
		});
	}else {
		 Public.tips({content: "请选择要删除的数据",type: 2});
		 return;
	}
}


function publish() {
	var rows = $("#grid").datagrid("getChecked");
	if(rows != null && rows.length > 0){
		$.messager.confirm("确认", "确认发布该套餐吗", function (r) {
			if (r) {
				$.ajax({
					type: "POST",
			        dataType: "json",
			        url: DZF.contextPath + "/channel/packageDef!updatePublish.action",
			        data : {
						"datas" : JSON.stringify(rows)
					},
			        success: function(rs) {
			        	if (rs.success) {
			        		$("#grid").datagrid("reload");
			                Public.tips({content: rs.msg,type:0});
						} else {
							 Public.tips({content: rs.msg,type:1});
						}
			        }
				});
			}
		});
	}else {
		 Public.tips({content: "请选择要操作的数据",type: 2});
		 return;
	}
}

function updateOff() {
	var rows = $("#grid").datagrid("getChecked");
	if(rows != null && rows.length > 0){
		$.messager.confirm("确认", "确认下架该套餐吗", function (r) {
			if (r) {
				$.ajax({
					type: "POST",
			        dataType: "json",
			        url: DZF.contextPath + "/channel/packageDef!updateOff.action",
			        data : {
						"datas" : JSON.stringify(rows)
					},
			        success: function(rs) {
			        	if (rs.success) {
			        		$("#grid").datagrid("reload");
			                Public.tips({content: rs.msg,type:0});
						} else {
							 Public.tips({content: rs.msg,type:1});
						}
			        }
				});
			}
		});
	}else {
		 Public.tips({content: "请选择要操作的数据",type: 2});
		 return;
	}
}

function modify() {
	var rows = $('#grid').datagrid("getSelections");
	if(rows && rows.length == 1){
		var row = rows[0];
		var vstatus = row.vstatus;
		if(vstatus == 3){
			var typecodeO = $('#grid').datagrid('getColumnOption', 'typecode');
			typecodeO.editor={};
			var taxtypeO = $('#grid').datagrid('getColumnOption', 'taxtype');
			taxtypeO.editor=taxtype;
			var comptypeO = $('#grid').datagrid('getColumnOption', 'comptype');
			comptypeO.editor=comptype;
			var itypeO = $('#grid').datagrid('getColumnOption', 'itype');
			itypeO.editor=itype;
			var nmsmnyO = $('#grid').datagrid('getColumnOption', 'nmsmny');
			nmsmnyO.editor=nmsmny;
			var cylnumO = $('#grid').datagrid('getColumnOption', 'cylnum');
			cylnumO.editor=cylnum;
			var contcycleO = $('#grid').datagrid('getColumnOption', 'contcycle');
			contcycleO.editor=contcycle;
			var pubnumO = $('#grid').datagrid('getColumnOption', 'pubnum');
			pubnumO.editor=pubnum;
			var isproO = $('#grid').datagrid('getColumnOption', 'ispro');
			isproO.editor={};
			var citynmsO = $('#grid').datagrid('getColumnOption', 'citynms');
			citynmsO.editor=citynms;
			var corpnmsO = $('#grid').datagrid('getColumnOption', 'corpnms');
			corpnmsO.editor=corpnms;
			var memoO = $('#grid').datagrid('getColumnOption', 'memo');
			memoO.editor={};
		}else if(vstatus == 2){
			var typecodeO = $('#grid').datagrid('getColumnOption', 'typecode');
			typecodeO.editor={};
			var taxtypeO = $('#grid').datagrid('getColumnOption', 'taxtype');
			taxtypeO.editor={};
			var comptypeO = $('#grid').datagrid('getColumnOption', 'comptype');
			comptypeO.editor={};
			var itypeO = $('#grid').datagrid('getColumnOption', 'itype');
			itypeO.editor=itype;
			var nmsmnyO = $('#grid').datagrid('getColumnOption', 'nmsmny');
			nmsmnyO.editor={};
			var cylnumO = $('#grid').datagrid('getColumnOption', 'cylnum');
			cylnumO.editor={};
			var contcycleO = $('#grid').datagrid('getColumnOption', 'contcycle');
			contcycleO.editor={};
			var pubnumO = $('#grid').datagrid('getColumnOption', 'pubnum');
			pubnumO.editor={};
			var isproO = $('#grid').datagrid('getColumnOption', 'ispro');
			isproO.editor={};
			var citynmsO = $('#grid').datagrid('getColumnOption', 'citynms');
			citynmsO.editor={};
			var corpnmsO = $('#grid').datagrid('getColumnOption', 'corpnms');
			corpnmsO.editor={};
			var memoO = $('#grid').datagrid('getColumnOption', 'memo');
			memoO.editor={};
		}else if(vstatus == 1){
			var typecodeO = $('#grid').datagrid('getColumnOption', 'typecode');
			typecodeO.editor=typecode;
			var taxtypeO = $('#grid').datagrid('getColumnOption', 'taxtype');
			taxtypeO.editor=taxtype;
			var comptypeO = $('#grid').datagrid('getColumnOption', 'comptype');
			comptypeO.editor=comptype;
			var itypeO = $('#grid').datagrid('getColumnOption', 'itype');
			itypeO.editor=itype;
			var nmsmnyO = $('#grid').datagrid('getColumnOption', 'nmsmny');
			nmsmnyO.editor=nmsmny;
			var cylnumO = $('#grid').datagrid('getColumnOption', 'cylnum');
			cylnumO.editor=cylnum;
			var contcycleO = $('#grid').datagrid('getColumnOption', 'contcycle');
			contcycleO.editor=contcycle;
			var pubnumO = $('#grid').datagrid('getColumnOption', 'pubnum');
			pubnumO.editor=pubnum;
			var isproO = $('#grid').datagrid('getColumnOption', 'ispro');
			isproO.editor=ispro;
			var citynmsO = $('#grid').datagrid('getColumnOption', 'citynms');
			citynmsO.editor=citynms;
			var corpnmsO = $('#grid').datagrid('getColumnOption', 'corpnms');
			corpnmsO.editor=corpnms;
			var memoO = $('#grid').datagrid('getColumnOption', 'memo');
			memoO.editor=memo;
		}
		showButtons("edit");
		var index = $("#grid").datagrid("getRowIndex", row);
		editIndex=index;
		$("#grid").datagrid("beginEdit", index);
	}else {
		Public.tips({content: "请选择一条数据修改",type: 2});
		return;
	}
}

function cancel () {
	var rows = $("#grid").datagrid("getRows");
	if(rows && rows.length > 0){
		for (var i = 0; i < rows.length; i++) {
			$("#grid").datagrid("cancelEdit", i);
		}
	}
	$("#grid").datagrid("rejectChanges");
	showButtons("brows");
}


function endEdit () {
	var datagrid = $('#grid').datagrid('validateRow', editIndex);
	if (!datagrid){
		return false;
	}else{
		$('#grid').datagrid("endEdit", editIndex);
	}
	return true;
}

function showButtons(type) {
	if(type=="sort"){
		$("#addBtn").hide();
		$("#editBtn").hide();
		$("#publishBtn").hide();
		$("#offBtn").hide();
		$("#delBtn").hide();
		$("#saveBtn").hide();
		$("#cancelBtn").hide();
		$("#sort").hide();
		$("#sort_save").show();
		$("#sort_cancle").show();
		
		$('#grid').datagrid('showColumn', 'operate');
	}else if (type =="brows") {
		$("#addBtn").show();
		$("#editBtn").show();
		$("#publishBtn").show();
		$("#offBtn").show();
		$("#delBtn").show();
		$("#saveBtn").hide();
		$("#cancelBtn").hide();
		$("#sort").show();
		$("#sort_save").hide();
		$("#sort_cancle").hide();
		
		$('#grid').datagrid('hideColumn', 'operate');
	} else {
		$("#addBtn").hide();
		$("#editBtn").hide();
		$("#publishBtn").hide();
		$("#offBtn").hide();
		$("#delBtn").hide();
		$("#saveBtn").show();
		$("#cancelBtn").show();
		$("#sort").hide();
		$("#sort_save").hide();
		$("#sort_cancle").hide();
		
		$('#grid').datagrid('hideColumn', 'operate');
	}
}

function getSubmitData () {
	var datagrid;
	var newRows = $('#grid').datagrid("getChanges", "inserted");
	if(newRows != null && newRows.length > 0){
		for (var i = 0; i < newRows.length; i++) {
			datagrid = $("#grid").datagrid("validateRow", i);
			if (!datagrid){
				Public.tips({
					content : "必输信息为空或格式不正确",
					type : 2
				});
				return; 
			}
		}
	}
	var updateRows = $('#grid').datagrid("getChanges", "updated");
	
	var submitData = {
		newRows: newRows,
		updateRows: updateRows
	}
	submitData = JSON.stringify(submitData);
	return submitData;
}

//////////////////////////////////////////////////////////////////////排序////////////////////////////////////////////////////////////////////////////////////////

function startSort(){
	showButtons("sort");
}

function sortSave(){
	var rows = grid.datagrid('getRows');
	$.ajax({
		type : "post",
		dataType : "json",
		url: DZF.contextPath + "/channel/packageDef!updateRows.action",
		data : {
			"datas" : JSON.stringify(rows)
		},
		async : false,
        success: function(rs) {
        	if (rs.success) {
        		reloadData();
                Public.tips({content: rs.msg,type:0});
			} else {
				 Public.tips({content: rs.msg,type:1});
			}
        }
	});
}

function onCancle(){
	showButtons("brows");
	reloadData();
}

function coperatorLink(val,row,index){  
	var up = '<div><a href="javascript:void(0)" id="upBut" onclick="upRow(this)"><img title="上移" style="margin:0px 10% 0px 10%;" src="../../images/move.png" /></a>';
	var down = '<a href="javascript:void(0)" id="downBut" onclick="downRow(this)"><img title="下移"style="margin:0px 10% 0px 10%;" src="../../images/Down.png" /></a>';
	var top = '<a href="javascript:void(0)" id="topBut" onclick="topRow(this)"><img title="置顶" style="margin:0px 10% 0px 5%;"src="../../images/top.png" /></a></div>';
    return up + down + top;  
}

/**
 * 上移
 */
function upRow(ths) {
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	index = Number(tindex);
	if (index != 0) {
		var toup = $('#grid').datagrid('getData').rows[index];
		var todown = $('#grid').datagrid('getData').rows[index - 1];
		$('#grid').datagrid('getData').rows[index] = todown;
		$('#grid').datagrid('getData').rows[index - 1] = toup;
		$('#grid').datagrid('refreshRow', index);
		$('#grid').datagrid('refreshRow', index - 1);
	}
}

/**
 * 置顶
 * @param ths
 */
function topRow(ths) {
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	index = Number(tindex);
	if (index != 0) {
		var totop = $('#grid').datagrid('getData').rows[index];
		var todown;
		for(var i= index-1;i>=0;i--){
			todown = $('#grid').datagrid('getData').rows[i];
			$('#grid').datagrid('getData').rows[i+1] = todown;
			$('#grid').datagrid('refreshRow', i+1);
		}
		$('#grid').datagrid('getData').rows[0] = totop;
		$('#grid').datagrid('refreshRow', 0);
	}
}

/**
 * 下移
 */
function downRow(ths){
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	index = Number(tindex);
	var rows = $('#grid').datagrid('getRows').length;
	if (index != rows - 1) {
		var todown = $('#grid').datagrid('getData').rows[index];
		var toup = $('#grid').datagrid('getData').rows[index + 1];
		$('#grid').datagrid('getData').rows[index + 1] = todown;
		$('#grid').datagrid('getData').rows[index] = toup;
		$('#grid').datagrid('refreshRow', index);
		$('#grid').datagrid('refreshRow', index + 1);
	}
}