var contextPath = DZF.contextPath;
var grid,gridh;

$(function() {
	load();
	initDetailGrid();
	initQryPeroid();
	initQryLitener();
	initChannel();//初始化加盟商
	quickfiltet();
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
			title : '加盟商编码',
			align:'center',
            halign:'center',
			field : 'incode',
		},{
			width : '220',
			title : '加盟商名称',
			halign:'center',
			field : 'corpnm',
			formatter : 
				function(value, row, index) {
					if(value == undefined)
						return;
	  				return "<a href='javascript:void(0)' style='color:blue' onclick=\"qryDetail('"+index+"')\">" + value + "</a>";
	  			}
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
 * 查询框-清除
 */
function clearParams(){
	$("#pk_account").val(null);
	$("#channel_select").textbox("setValue",null);
}

//初始化加盟商
function initChannel(){
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

function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
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
    footerData['incode'] = '合计';
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
function qryDetail(index){
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
	var rows = $('#grid').datagrid('getRows');
	var row= rows[index];
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
	queryBoxChange('#begdate','#enddate');
    $('input:radio[name="seledate"]').change( function(){  
		var ischeck = $('#da').is(':checked');
		if(ischeck){
			queryBoxChange('#begdate','#enddate');
			var sdv = $('#begdate').datebox('getValue');
			var edv = $('#enddate').datebox('getValue');
			$('#jqj').html(sdv + ' 至 ' + edv);
			$("#begdate").datebox("readonly", false);
			$("#enddate").datebox("readonly", false);
			$('#begperiod').textbox({"readonly" : true});
			$('#endperiod').textbox({"readonly" : true});
			$(".foxdate").hide();
		}else{
			queryBoxChange1('#begperiod','#endperiod');
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
 * 查询框时间改变事件(为期间年月)
 * @param start
 * @param end
 */
function queryBoxChange1(start,end){
	$(start).textbox({
		onChange: function(newValue, oldValue){
			var edv = $(end).textbox('getValue');
			$('#jqj').text(newValue + ' 至 ' + edv);
		}
	});
	$(end).textbox({
		onChange: function(newValue, oldValue){
			var sdv = $(start).textbox('getValue');
			$('#jqj').text(sdv + ' 至 ' + newValue);
		}
	});
}

/**
 * 查询-确定
 */
function reloadData(queryData){
	if(isEmpty(queryData)){
		$('#grid').datagrid('options').queryParams = getQueryData();
	}else{
		$('#grid').datagrid('options').queryParams =queryData;
	}
	$('#grid').datagrid('options').url = contextPath + '/chnpay/chnpaybalance!query.action';
	$('#grid').datagrid('reload');
	$('#grid').datagrid('unselectAll');
	$("#qrydialog").css("visibility", "hidden");
}

function getQueryData(cpname){
	var begdate= null;
	var enddate= null;
	var bperiod= null;
	var eperiod= null;
	var period= null;
	var cpname;
	var ischeck = $('#da').is(':checked');
	if(ischeck){
		begdate=$("#begperiod").textbox('getValue');
		enddate=$("#endperiod").textbox('getValue')
	}else{
		bperiod=$("#begperiod").val();
		eperiod=$("#endperiod").val();
		period='period';
	}
	if(!isEmpty(cpname)){
		cpname=cpname;
	}else{
		cpname= null
	}
	var qtype = $("input[name='seletype']:checked").val();
	var queryData = {
		"bperiod" : bperiod,
		"eperiod" : eperiod,
		"begdate" : begdate,
		"enddate" : enddate,
		'qtype' : qtype,
		'cpname' : cpname,
		"corps" : $("#pk_account").val(),
	};
	return queryData;
}

function quickfiltet(){
	$('#filter_value').textbox('textbox').keydown(function (e) {
		debugger
        if (e.keyCode == 13) {
        	$('#grid').datagrid('unselectAll');
 		   var filtername = $("#filter_value").val(); 
		   if (filtername) {
				reloadData(getQueryData(filtername));
			}else{
				reloadData();
			} 
        }
    });
}

/**
 * 查询-取消
 */
function closeCx(){
	$("#livediv").remove();
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
	Business.getFile(DZF.contextPath+ '/chnpay/chnpaybalance!onExport.action',{
		'strlist':JSON.stringify(datarows),'qj' : $('#jqj').html(),}, true, true);
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
	