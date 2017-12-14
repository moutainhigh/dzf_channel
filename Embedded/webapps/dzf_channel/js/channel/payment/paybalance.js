var contextPath = DZF.contextPath;
var grid,gridh;

$(function() {
	load();
	initDetailGrid();
	initQryPeroid();
	initQryLitener();
});

/**
 * 列表grid初始化
 */
function load(){
	grid = $('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height : Public.setGrid().h,
		singleSelect : true,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		showFooter:true,
		columns : [ [ {
			width : '140',
			title : '加盟商',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '140',
			title : '付款类型',
			align:'center',
            halign:'center',
			field : 'ptypenm',
		},{
			width : '140',
			title : '期初余额',
			align:'right',
            halign:'center',
			field : 'initbal',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '140',
			title : '本期付款金额',
			align:'right',
            halign:'center',
			field : 'npmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '140',
			title : '本期已用金额',
			align:'right',
            halign:'center',
			field : 'usemny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '140',
			title : '期末余额',
			align:'right',
            halign:'center',
			field : 'balmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},] ],
		onLoadSuccess : function(data) {
            parent.$.messager.progress('close');
			calFooter();
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

/**
 * 计算合计
 */
function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
	var initbal = 0;
    var npmny = 0;	
    var usemny = 0;	
    var balmny = 0;	
    for (var i = 0; i < rows.length; i++) {
    	initbal += getFloatValue(rows[i].initbal);
    	npmny += getFloatValue(rows[i].npmny);
    	usemny += getFloatValue(rows[i].usemny);
    	balmny += getFloatValue(rows[i].balmny); 
    }
    footerData['initbal'] = initbal;
    footerData['npmny'] = npmny;
    footerData['usemny'] = usemny;
    footerData['balmny'] = balmny;
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}

/**
 * 明细查询
 */
function qryDetail(){
	
	var begdate = null;
	var enddate = null;
	var bperiod = null;
	var eperiod = null;
	var period = null;
	var ischeck = $('#da').is(':checked');
	var qrydate = null;
	if(ischeck){
		begdate = $("#begdate").datebox("getValue");
		enddate = $("#enddate").datebox("getValue");
		bperiod = null;
		eperiod = null;
		period = null;
		qrydate = begdate + "至" + enddate;
	}else{
		bperiod = $("#begperiod").val();
		eperiod = $("#endperiod").val();
		begdate = null;
		enddate = null;
		period = 'period';
		qrydate = bperiod + "至" + eperiod;
	}
	var row = $('#grid').datagrid('getSelected');
	if (row == null) {
		Public.tips({
			content : "请选择需要处理的数据",
			type : 2
		});
		return;
	}
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + "/chnpay/chnpaybalance!queryDetail.action",
		data : {
			"cpid" : row.corpid,
			"qtype" : row.iptype,
			"bperiod" : bperiod,
			"eperiod" : eperiod,
			"begdate" : begdate,
			"enddate" : enddate,
			"period" : period,
		},
		traditional : true,
		async : false,
		success : function(data, textStatus) {
			if (!data.success) {
				Public.tips({
					content : data.msg,
					type : 1
				});
			} else {
				var res = data.rows;
				if (res == null || res == "") {
					$('#detail_dialog').dialog('close');
					Public.tips({
						content : "无明细记录",
						type : 2
					});
					return;
				}
				$('#corpnm').html(res[0].corpnm);
				$('#ptypenm').html(res[0].ptypenm);
				$('#qrydate').html(qrydate);
				$('#detail_dialog').dialog('open');
				$('#gridh').datagrid('loadData',res);
			}
		}
	});
}

/**
 * 明细列表初始化
 */
function initDetailGrid(){
	gridh = $('#gridh').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		/*height : Public.setGrid().h,*/
		height:'350',
		singleSelect : true,
//		pagination : true,// 分页工具栏显示
//		pageSize : DZF.pageSize_min,
//		pageList : DZF.pageList_min,
		showFooter:true,
		columns : [ [ {
			width : '110',
			title : '日期',
			align:'center',
			halign:'center',
			field : 'ddate',
		}, {
			width : '200',
			title : '摘要',
            halign:'center',
			field : 'memo',
		}, {
			width : '140',
			title : '付款金额',
			align:'right',
            halign:'center',
			field : 'npmny',
			formatter : npFormat
		},{
			width : '140',
			title : '扣款金额',
			align:'right',
            halign:'center',
			field : 'usemny',
			formatter : useFormat
		},{
			width : '140',
			title : '期末余额',
			align:'right',
            halign:'center',
			field : 'balmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},] ],
		onLoadSuccess : function(data) {
			
			var rows = $('#gridh').datagrid('getRows');
			var footerData = new Object();
            var npmnysum = parseFloat(0);	// 付款金额
            var usemnysum = parseFloat(0);	// 已用金额
            var balmnysum = parseFloat(0);	// 余额
            for (var i = 0; i < rows.length; i++) {
            	npmnysum += getFloatValue(rows[i].npmny);
            	usemnysum += getFloatValue(rows[i].usemny);
            	if(i == rows.length - 1){
            		balmnysum = getFloatValue(rows[i].balmny);
            	}
            }
            footerData['ddate'] = '合计';
            footerData['npmny'] = npmnysum;
            footerData['usemny'] = usemnysum;
            footerData['balmny'] = balmnysum;
            var fs=new Array(1);
            fs[0] = footerData;
            $('#gridh').datagrid('reloadFooter',fs);
            $('#gridh').datagrid("scrollTo",0);
		},
	});
}

/**
 * 付款金额格式化
 * @param value
 * @param row
 * @param index
 * @returns
 */
function npFormat(value,row,index){
	if(value == 0){
		return "0.00";
	}else{
		if(row.ddate != "合计"){
			var url = 'channel/payment/payconfirm.jsp?operate=topayc&pk_billid='+row.billid;
			var ss = "<a href='javascript:void(0)' style='color:blue' onclick=\"parent.addTabNew('付款单确认','"+url+"');\">"+formatMny(value)+"</a>";
			return ss ;
		}else{
			return formatMny(value);
		}
	}
}

/**
 * 扣款金额格式化
 * @param value
 * @param row
 * @param index
 * @returns
 */
function useFormat(value,row,index){
	if(value == 0){
		return "0.00";
	}else{
		if(row.ddate != "合计"){
			var url = 'channel/contract/contractconfrim.jsp?operate=tocont&pk_billid='+row.billid;
			var ss = "<a href='javascript:void(0)' style='color:blue' onclick=\"parent.addTabNew('合同审核','"+url+"');\">"+formatMny(value)+"</a>";
			return ss ;
		}else{
			return formatMny(value);
		}
	}
}

/**
 * 查询期间初始化
 */
function initQryPeroid(){
	var begperiod = $('#begperiod').textbox('getValue');
	var year = "";
	var month = "";
	if(!isEmpty(begperiod)){
		year = begperiod.substring(0,4);
		month = begperiod.substring(5);
		month = parseInt(month) - 1;
	}
	$('#begperiod').textbox({
		icons: [{
			iconCls:'foxdate',
			handler: function(e){
				click_icon(205, 135, begperiod, year, month, function(val){
					if(!isEmpty(val)){
						$('#begperiod').textbox('setValue', val);
						begperiod = val;
						if(!isEmpty(begperiod)){
							year = begperiod.substring(0,4);
							month = begperiod.substring(5);
							month = parseInt(month) - 1;
						}
					}
				})
			}
		}]
	});
	
	var endperiod = $('#endperiod').textbox('getValue');
	var eyear = "";
	var emonth = "";
	if(!isEmpty(endperiod)){
		eyear = endperiod.substring(0,4);
		emonth = endperiod.substring(5);
		emonth = parseInt(emonth) - 1;
	}
	$('#endperiod').textbox({
		icons: [{
			iconCls:'foxdate',
			handler: function(e){
				click_icon(205, 296, endperiod, eyear, emonth, function(val){
					if(!isEmpty(val)){
						$('#endperiod').textbox('setValue', val);
						endperiod = val;
						if(!isEmpty(endperiod)){
							eyear = endperiod.substring(0,4);
							emonth = endperiod.substring(5);
							emonth = parseInt(emonth) - 1;
						}
					}
				})
			}
		}]
	});
}

/**
 * 查询框监听事件
 */
function initQryLitener(){
	$("#begdate").datebox("readonly", false);
	$("#enddate").datebox("readonly", false);
	$('#begperiod').textbox({"readonly" : true});
	$('#endperiod').textbox({"readonly" : true});
	$(".foxdate").hide();
    $('input:radio[name="seledate"]').change( function(){  
		var ischeck = $('#da').is(':checked');
		if(ischeck){
			var sdv = $('#begdate').datebox('getValue');
			var edv = $('#enddate').datebox('getValue');
			$('#jqj').html(sdv + ' 至 ' + edv);
			$("#begdate").datebox("readonly", false);
			$("#enddate").datebox("readonly", false);
			$('#begperiod').textbox({"readonly" : true});
			$('#endperiod').textbox({"readonly" : true});
			$(".foxdate").hide();
		}else{
			var sdv = $("#begperiod").val();
			var edv = $("#endperiod").val();
			$('#jqj').html(sdv + ' 至 ' + edv);
			$("#begdate").datebox("readonly", true);
			$("#enddate").datebox("readonly", true);
			$('#begperiod').textbox({"readonly" : false});
			$('#endperiod').textbox({"readonly" : false});
			$(".foxdate").show();
		}
	});
}

/**
 * 查询-确定
 */
function reloadData(){
	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').url = contextPath + '/chnpay/chnpaybalance!query.action';
	var ischeck = $('#da').is(':checked');
	if(ischeck){
		queryParams['begdate'] = $("#begdate").datebox("getValue");
		queryParams['enddate'] = $("#enddate").datebox("getValue");
		queryParams['bperiod'] = null;
		queryParams['eperiod'] = null;
		queryParams['period'] = null;
	}else{
		queryParams['bperiod'] = $("#begperiod").val();
		queryParams['eperiod'] = $("#endperiod").val();
		queryParams['begdate'] = null;
		queryParams['enddate'] = null;
		queryParams['period'] = 'period';
	}
	var qtype = $("input[name='seletype']:checked").val();
	queryParams['qtype'] = qtype;
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
	$('#grid').datagrid('unselectAll');
	$("#qrydialog").css("visibility", "hidden");
}

/**
 * 查询-取消
 */
function closeCx(){
	$("#qrydialog").css("visibility", "hidden");
}

/**
 * 导出
 */
function onExport(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'请选择需导出的数据',type:2});
		return;
	}
	var columns = $('#grid').datagrid("options").columns[0];
	Business.getFile(DZF.contextPath+ '/chnpay/chnpaybalance!onExport.action',{'strlist':JSON.stringify(datarows)}, true, true);
}

/**
 * 打印
 */
function onPrint(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'当前界面数据为空',type:2});
		return;
	}
	var columns = $('#grid').datagrid("options").columns[0];
	Business.getFile(contextPath+ '/chnpay/chnpaybalance!print.action',{'strlist':JSON.stringify(datarows),
		'columns':JSON.stringify(columns)}, true, true);
}

/**
 * 明细打印
 */
function onDetPrint(){
		var datarows = $('#gridh').datagrid("getRows");
 		if( datarows == null||datarows.length == 0){
 			Public.tips({content:'明细数据为空',type:2});
 			return;
 		}
 		var columns = $('#gridh').datagrid("options").columns[0];
 		var qrydate = $("#qrydate").text();
 		var corpnm = $("#corpnm").text();
 		var ptypenm = $("#ptypenm").text();
 		
 		Business.getFile(contextPath+ '/chnpay/chnpaybalance!onDetPrint.action',{'strlist':JSON.stringify(datarows),
 			'columns':JSON.stringify(columns),'qrydate':qrydate,'corpnm':corpnm,'ptypenm':ptypenm}, true, true);
}

/**
 * 明细导出
 */
function onDetExport(){
	var datarows = $('#gridh').datagrid("getRows");
	if( datarows == null||datarows.length == 0){
			Public.tips({content:'明细数据为空',type:2});
			return;
		}
		var columns = $('#gridh').datagrid("options").columns[0];
 		var qrydate = $("#qrydate").text();
 		var corpnm = $("#corpnm").text();
 		var ptypenm = $("#ptypenm").text();
	Business.getFile(contextPath+ '/chnpay/chnpaybalance!onDetExport.action',{'strlist':JSON.stringify(datarows),
		'columns':JSON.stringify(columns),'qrydate':qrydate,'corpnm':corpnm,'ptypenm':ptypenm}, true, true);
}
	