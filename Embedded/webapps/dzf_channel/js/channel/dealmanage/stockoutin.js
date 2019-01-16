var contextPath = DZF.contextPath;
var editIndex;
var status = "brows";

$(function(){
	initQry();
	initCombobox();
	load();
	reloadData();
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
	$("#begdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#enddate").datebox("setValue",parent.SYSTEM.LoginDate);
	$("#jqj").html(parent.SYSTEM.PreDate+" 至  "+parent.SYSTEM.LoginDate);
}

function initCombobox(){
	$("#goodsname").combobox({
		onShowPanel: function () {
			initType();
        }
    })
}

/**
 * 查询出入库类别下拉
 * 查询商品下拉
 */

function initType(){
	$.ajax({
		type : 'POST',
		async : false,
	    url : DZF.contextPath + '/dealmanage/stockoutin!queryComboBox.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result+ ')');
			if (result.success) {
				$('#goodsname').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
};

/**
 * 列表表格加载
 */
function load(){
	grid = $('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height : Public.setGrid().h,
		singleSelect : false,
		checkOnSelect : false,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		showFooter : true,
		sortName : "contime",
		sortOrder : "desc",
		remoteSort : false,
		idField : 'sid',
		columns : [ [ {
			width : '120',
			title : '单据编码',
			field : 'vcode',
			align : 'left',
			halign : 'center',
		}, {
			width : '100',
			title : '商品编码',
			align : 'left',
			halign : 'center',
			field : 'gcode',
			
		}, {
			width : '100',
			title : '商品',
			field : 'gname',
			halign : 'center',
			align : 'left',
		}, {
			width : '100',
			title : '规格',
			field : 'spec',
			halign : 'center',
			align : 'center',
		}, {
			width : '80',
			title : '型号',
			field : 'type',
			halign : 'center',
			align : 'center',
		}, {
			width : '80',
			title : '类型',
			field : 'itype',
			halign : 'center',
			align : 'center',
			formatter : function(value,row) {
				if(!isEmpty(row.gid)){
					if (value == '1')
						return '入库';
					if (value == '2')
						return '出库';
				}else{
					return value;
				}
			}
			
		},{
			field : 'nprice',
			title : '成本价',
			width : '90',
			halign : 'center',
			align : 'right',
			formatter : function(value,row,index){
					if(value == 0)return "0.00";
					return formatMny(value);
				},
		}, {
			field : 'num',
			title : '数量',
			width : '90',
			halign : 'center',
			align : 'right',
			formatter : function(value, row, index){
				if(!isEmpty(row.gid)){
					if(value=='0'){
						return value;
					}else{
						if(row.itype=='1')
							return '+'+value;
						if(row.itype=='2')
							return '-'+value;
					}
				}else{
					return value;
				}
				
			}
		},  {
			field : 'sprice',
			title : '售价',
			width : '90',
			halign : 'center',
			align : 'right',
			formatter : function(value,row,index){
					if(value == 0)return "0.00";
					return formatMny(value);
				},
		},  {
			field : 'nmny',
			title : '金额',
			width : '90',
			halign : 'center',
			align : 'right',
			formatter : function(value,row,index){
					if(value == 0)return "0.00";
					return formatMny(value);
				},
			
		},  {
			field : 'conname',
			title : '操作人',
			width : '90',
			halign : 'center',
			align : 'center',
		},  {
			field : 'contime',
			title : '入库（出库）时间',
			width : '150',
			halign : 'center',
			align : 'center',
			sortable : true,
		}, ] ],
		onLoadSuccess : function(data) {
            parent.$.messager.progress('close');
            calFooter();
            $('#grid').datagrid("scrollTo",0);
		},
		
	});
}


String.prototype.startWith=function(str){
	var reg=new RegExp("^"+str);
	return reg.test(this);
	}


/**
 * 计算合计
 */
function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
	var num = 0;// 数量
	for (var i = 0; i < rows.length; i++) {
		
		if(rows[i].itype=='1'){
			num += getFloatValue(rows[i].num);
		}else if (rows[i].itype=='2'){
			num -= getFloatValue(rows[i].num);
		}
	  
	}
	if(num>0){
		num="+"+num;
	}
	 footerData['itype'] = '合计';
	 footerData['num'] = num;
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}


/**
 * 查询数据
 */
function reloadData(){
	var url = DZF.contextPath + '/dealmanage/stockoutin!query.action';
	$('#grid').datagrid('options').url = url;
	var itype=$('#itype').combobox('getValue');
	var gids=$('#goodsname').combobox('getValues');
	var strgids="";
	for(i=0;i<gids.length;i++){
		strgids+=","+gids[i];
	}
	strgids=strgids.substring(1);
	if(isEmpty(itype)){
		itype=3;
	}
	$('#grid').datagrid('load', {
		'begdate' : $("#begdate").datebox('getValue'),
		'enddate' : $("#enddate").datebox('getValue'),
		'vcode' : $("#qvcode").val(),
		'gid' :  strgids,
		'itype' :  itype,
	});
	$('#grid').datagrid('clearSelections');
	$('#grid').datagrid('clearChecked');
	$('#qrydialog').hide();
}

/**
 * 清除查询条件
 */
function clearParams(){
	$("#qvcode").textbox('setValue',null);
	$("#goodsname").combobox('clear');
	$("#itype").combobox('setValue',null);
}

/**
 * 取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 导出
 */
function doExport(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'当前界面数据为空',type:2});
		return;
	}
	var columns = $('#grid').datagrid("options").columns[0];
	var qj = $('#begdate').datebox('getValue') + '至' + $('#enddate').datebox('getValue');
	Business.getFile(DZF.contextPath+ '/dealmanage/stockoutin!exportAuditExcel.action',
			{'strlist':JSON.stringify(datarows), 'qj':qj,}, true, true);
}


