var grid;
var ovince;
var type;

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});

$(function() {
	initPeriod("#sbegdate");
	initPeriod("#senddate");
	queryQtype();
	changeAreaName();
	changeProvinceName();
	initArea({"qtype" :type});
	initProvince({"qtype" :type});
	if(type==2){
		initManager({"qtype" :2});
	}/*else{
		$('#uid').combobox('readonly',true);
	}*/
	initQry();
	load();
	begloadData();
	//initQryCommbox();
	initChannelName();
	
});


function changeAreaName(){
	 $("#aname").combobox({
		onChange : function(n, o) {
			var queryData={"qtype" :type};
			if(!isEmpty(n)){
				queryData={'aname' : n,"qtype" :type};
				$('#ovince').combobox('setValue',null);
				$('#uid').combobox('setValue',null);
			}
			initProvince(queryData);
			if(type==2){
				initManager(queryData);
			}
		}
	});
}

function changeProvinceName(){
	 $("#ovince").combobox({
		onChange : function(n, o) {
			var queryData={"qtype" :type};
			if(!isEmpty(n)){
				queryData={'aname' : $("#aname").combobox('getValue'),'ovince':n,"qtype" :type};
				$('#uid').combobox('setValue',null);
			}
			if(type==2){
				initManager(queryData);
			}
		}
	});
}

function queryQtype(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/corp/channel!queryQtype.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    type = result.rows;
			    ovince = type==1?-2:-3;
			} 
		}
	});
}

//初始化
function initQry(){
	// 下拉按钮的事件
	$('#jqj').html($("#sbegdate").datebox('getValue') + ' 至  ' + $("#senddate").datebox('getValue'));
	// 下拉按钮的事件
	queryBoxChange('#sbegdate','#senddate');
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	
}

function begloadData(){
	var url = DZF.contextPath + '/report/losscust!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'bperiod' : $("#sbegdate").datebox('getValue'),
		'eperiod' : $("#senddate").datebox('getValue'),
	});
	$('#grid').datagrid('clearSelections');
	$('#grid').datagrid('clearChecked');
	$('#qrydialog').hide();
}


/**
 * 查询数据
 */
function reloadData(){
	var url = DZF.contextPath + '/report/losscust!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('options').sortName = "";
	$('#grid').datagrid('options').sortOder = "";
	var province = $('#ovince').combobox('getValue') == "" ? -1 : $('#ovince').combobox('getValue');
	$('#grid').datagrid('load', {
		'corps' : $("#pk_account").val(),
		'vcode' : $("#sreason").val(),
		'aname' : $('#aname').combobox('getValue'),
		'uid' : $('#uid').combobox('getValue'),
		'ovince' : province,
		'bperiod' : $("#sbegdate").datebox('getValue'),
		'eperiod' : $("#senddate").datebox('getValue'),
	});
	$('#grid').datagrid('clearSelections');
	$('#grid').datagrid('clearChecked');
	$('#qrydialog').hide();
}

//初始化加盟商
function initChannelName(){
    $('#channel_select').textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#kj_dialog").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择加盟商',
                    modal: true,
                    href: DZF.contextPath + '/ref/channel_select.jsp',
                    queryParams : {
                    	ovince : ovince
    				},
                    buttons: '#kj_buttons'
                });
            }
        }]
    });
}

//双击选择公司
function dClickCompany(rowTable){
	var str = "";
	var corpIds = [];
	if(rowTable){
		if(rowTable.length>300){
			Public.tips({content : "一次最多只能选择300个客户!" ,type:2});
			return;
		}
		for(var i=0;i<rowTable.length;i++){
			if(i == rowTable.length - 1){
				str += rowTable[i].uname;
			}else{
				str += rowTable[i].uname+",";
			}
			corpIds.push(rowTable[i].pk_gs);
		}
		$("#channel_select").textbox("setValue",str);
		$("#pk_account").val(corpIds);
	}
	$("#kj_dialog").dialog('close');
}

//清空查询条件
function clearCondition(){
	$('#aname').combobox('select',null);
	$('#ovince').combobox('select',null);
	$('#uid').combobox('select',null);
	$("#sreason").val(null);
	$("#channel_select").textbox("setValue",null);
}


/**
 * 数据表格初始化
 */
function load(){

	grid = $('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : false,
		checkOnSelect :false,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		remoteSort : false,//定义从服务器排序
		sortName:"corpid",//排序字段
		sortOrder:"desc",//排序方式
		showFooter:true,
		idField : 'corpid',
		columns : [ [ {
			field : 'ck',
			checkbox : true
		}, {
			width : '100',
			title : '主键',
			field : 'corpid',
			hidden : true
		}, {
			width : '100',
			title : '时间戳',
			field : 'updatets',
			hidden : true
		}, {
			width : '100',
			title : '加盟商主键',
			field : 'corpid',
			hidden : true,
			sortable:true,
		},{
			width : '80',
			title : '大区',
			field : 'aname',
			align : 'left',
			halign : 'center',
		}, {
			width : '80',
			title : '会计运营',
			align : 'center',
			halign : 'center',
			field : 'cuname',
		}, {
			width : '100',
			title : '省市',
			align : 'center',
			halign : 'center',
			field : 'provname',
		}, {
			width : '150',
			title : '加盟商名称',
			field : 'corpnm',
			halign : 'right',
			align : 'center',
		}, {
			width : '150',
			title : '客户名称',
			field : 'cname',
			halign : 'center',
			align : 'center',
		}, {
			width : '110',
			title : '纳税人资格',
			field : 'chname',
			halign : 'center',
			align : 'center',
		}, {
			width : '100',
			title : '建账日期',
			field : 'jdate',
			halign : 'center',
			align : 'center',
		}, {
			width : '100',
			title : '关账状态',
			field : 'istatus',
			halign : 'center',
			align : 'center',
		}, {
			field : 'stime',
			title : '停用时间',
			width : '100',
			halign : 'center',
			align : 'center',
			sortable:true,
			sorter:charorderfun,
			formatter: function(value, row, index) {
				if(!isEmpty(value) && value=='1970-01-01')
					return null;
				if(!isEmpty(value) && value!='1970-01-01')
					return value;
			}
			
		},  {
			field : 'sname',
			title : '停用人',
			width : '80',
			halign : 'center',
			align : 'center',
		}, {
			field : 'sreason',
			title : '停用原因',
			width : '100',
			halign : 'center',
			align : 'center',
		},] ],
		onLoadSuccess : function(data) {
			$('#grid').datagrid("scrollTo", 0);
		},
	});

}


function closeCx(){
	$('#qrydialog').dialog('close');
}


/**
 * 标签查询
 * @param type   1：正常流失；2：非正常流失
 */
function qryByType(type){
	$('#grid').datagrid('unselectAll');
	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').sortName = "";
	$('#grid').datagrid('options').sortOder = "";
	$('#grid').datagrid('options').url = DZF.contextPath + '/report/losscust!query.action';
	queryParams.qtype = type;
	queryParams.bperiod = null;
	queryParams.eperiod = null;
	queryParams.corps = null;
	queryParams.vcode = null;//停用原因
	queryParams.aname = null;
	queryParams.ovince = -1;
	queryParams.uid = null;
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
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
	var qj = $('#jqj').html();
	Business.getFile(DZF.contextPath+ '/report/losscust!exportAuditExcel.action',
			{'strlist':JSON.stringify(datarows), 'qj':qj,}, true, true);
}
