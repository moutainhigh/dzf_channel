var contextPath = DZF.contextPath;

$(function() {
	initArea();
	initProvince();
	initDate();
	load();
	quickfiltet();
});

function initArea(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/report/manager!queryArea.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#aname').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

function initProvince(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/sys/sys_area!queryComboxArea.action',
		data : {
			parenter_id : 1,
		},
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#ovince').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

function initDate(){
	$('#numweek').combobox('select',$('#num').val());
	var period = parent.SYSTEM.LoginDate;
	period=period.substring(0,7);
	$('#period').textbox('setValue',period);
	var year =period.substring(0,4);
	var month =period.substring(5);
	month = parseInt(month) - 1;
	$("#period").textbox({
		icons: [{
			iconCls:'foxdate',
			handler: function(e){
				click_icon(40, 520, period, year, month, function(val){
					$("#period").textbox('setValue', val);
					period = val;
					if(!isEmpty(period)){
						year = period.substring(0,4);
						month = period.substring(5);
						month = parseInt(month) - 1;
					}
				})
			}
		}]
	});
}

function load() {
	var bdate;
	var edate;
	if($('#qj').is(':checked')){
		bdate=$("#begperiod").textbox('getValue');
		edate=$("#endperiod").textbox('getValue')
	}else{
		bdate=$("#bdate").datebox('getValue');
		edate=$("#edate").datebox('getValue');
	}
	var queryData={
			"bdate" : bdate,
			"edate" : edate,
			"corps" : $("#pk_account").val(),
		};
	// 列表显示的字段
	$('#grid').datagrid({
		url : DZF.contextPath + '/chn_set/saleAnalyse!query.action',
		idField : 'pk_chnarea',
//		pageNumber : 1,
//		pageSize : DZF.pageSize,
//		pageList : DZF.pageList,
//		pagination : true,
		rownumbers : true,
		singleSelect : true,
		height : Public.setGrid().h,
		data : queryData,
		columns : [ [	
		             	{width : '130',title : '大区',field : 'aname',align:'left'}, 
		            	{width : '110',title : '省（市）',field : 'provname',align:'left'}, 
		            	{width : '250',title : '加盟商',field : 'corpnm',align:'left'},
		          ] ],
		onLoadSuccess : function(data) {
			if(data.rows && loadrows == null){
				loadrows = data.rows;
			}
			$.messager.progress('close');
			calFooter();
		}
	});
}

/**
 * 计算合计
 */
function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
    var outmny = 0;	
    var ndeductmny = 0;	
    for (var i = 0; i < rows.length; i++) {
    	outmny += parseFloat(rows[i].outmny == undefined ? 0 : rows[i].outmny);
    	ndeductmny += parseFloat(rows[i].ndeductmny == undefined ? 0 : rows[i].ndeductmny);
    }
    footerData['cname'] = '合计';
    footerData['outmny'] = outmny;
    footerData['ndeductmny'] = ndeductmny;
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}


function quickfiltet(){
	$('#quname').textbox('textbox').keydown(function (e) {
        if (e.keyCode == 13) {
        	$('#grid').datagrid('unselectAll');
 		   var filtername = $("#quname").val(); 
		   if (filtername) {
				var jsonStrArr = [];
				if(loadrows){
					for(var i=0;i<loadrows.length;i++){
						var row = loadrows[i];
						if(row.cname.indexOf(filtername) >= 0){
							jsonStrArr.push(row);
						} 
					}
					$('#grid').datagrid('loadData',jsonStrArr);   
				}
			}else{
				load();
			} 
        }
    });
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
	Business.getFile(contextPath+ '/chn_set/saleAnalyse!exportExcel.action',{'strlist':JSON.stringify(datarows)}, true, true);
}
