var contextPath = DZF.contextPath;
var grid,gridh;

$(function() {
	load();
	initDetailGrid();
	initQryPeroid();
	initQryLitener();
	initChannel();//初始化加盟商
	initArea();
	quickfiltet();
	initManagerRef();//渠道经理参照初始化
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
		pageList : [100, 200, 300, 400, 500, 1000],
		showFooter:true,
		columns : [ [{
			width : '140',
			title : '大区',
			align : 'left',
	        halign: 'center',
			field : 'aname'
		}, {
			width : '120',
			title : '渠道经理',
			halign:'center',
			field : 'mname',
		}, {
			width : '120',
			title : '加盟商编码',
            halign:'center',
            align:'left',
			field : 'incode',
		},{
			width : '210',
			title : '加盟商名称',
			halign:'center',
			field : 'corpnm',
			formatter : 
				function(value, row, index) {
	  				if(!isEmpty(value)){
	  					if (!isEmpty(row.dreldate)) {
	  						return "<div style='position:relative;color:blue;' onclick=\"qryDetail('"+index+"')\" >" + value 
	  							+ "<img style='right: 0; position: absolute;' src='../../images/rescission.png' /></div>";
	  					}else{
	  						return "<a href='javascript:void(0)' style='color:blue' onclick=\"qryDetail('"+index+"')\" >" + value + "</a>";
	  					}
	  				}
	  			}
		},{
			width : '100',
			title : '存量合同数',
			align:'center',
            halign:'center',
			field : 'custnum',
		},{
			width : '150',
			title : '0扣款(非存量)合同数',
			align:'center',
            halign:'center',
			field : 'zeronum',
		},{
			width : '100',
			title : '非存量合同数',
			align:'center',
            halign:'center',
			field : 'dednum',
		},{
			width : '100',
			title : '合同代账费',
			align:'right',
            halign:'center',
			field : 'namny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '70',
			title : '账本费',
			align:'right',
            halign:'center',
			field : 'nbmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '90',
			title : '扣款比率(%)',
			align:'center',
            halign:'center',
			field : 'propor',
		},{
			width : '70',
			title : '付款类型',
			align:'center',
            halign:'center',
			field : 'ptypenm',
		},{
			width : '100',
			title : '期初余额',
			align:'right',
            halign:'center',
			field : 'initbal',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '100',
			title : '本期付款金额',
			align:'right',
            halign:'center',
			field : 'npmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '100',
			title : '合同扣款',
			align:'right',
            halign:'center',
			field : 'condedmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '100',
			title : '商品购买',
			align:'right',
            halign:'center',
			field : 'buymny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '100',
			title : '本期已用金额',
			align:'right',
            halign:'center',
			field : 'usemny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '100',
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
	$('#aname').combobox('setValue', null);
	$("#channel_select").textbox("setValue", null);
	$("#manager").textbox("setValue", null);
	$("#managerid").val(null);
}

/**
 * 查询-大区下拉初始化
 */
function initArea(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryArea.action',
		data : {"qtype" :3},
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

/**
 * 查询-加盟商参照初始化
 */
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
                  queryParams : {
  					ovince :"-1"
  				  },
                  buttons: '#kj_buttons'
              });
          }
      }]
  });
}

/**
 * 双击选择加盟商
 * @param rowTable
 */
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

/**
 * 选择加盟商
 */
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
	var custnum = 0;
	var zeronum = 0;
	var dednum = 0;
	var nbmny = 0;
	var namny = 0;
	var initbal = 0;
    var npmny = 0;	
    var usemny = 0;	
    var balmny = 0;	
    var condedmny = 0;
    var buymny = 0;
    for (var i = 0; i < rows.length; i++) {
    	custnum += getFloatValue(rows[i].custnum);
    	zeronum += getFloatValue(rows[i].zeronum);
    	dednum += getFloatValue(rows[i].dednum);
    	nbmny += getFloatValue(rows[i].nbmny);
    	namny += getFloatValue(rows[i].namny);
    	initbal += getFloatValue(rows[i].initbal);
    	npmny += getFloatValue(rows[i].npmny);
    	usemny += getFloatValue(rows[i].usemny);
    	balmny += getFloatValue(rows[i].balmny); 
    	condedmny += getFloatValue(rows[i].condedmny);
    	buymny += getFloatValue(rows[i].buymny);
    }
    footerData['incode'] = '合计';
    footerData['custnum'] = custnum;
    footerData['zeronum'] = zeronum;
    footerData['dednum'] = dednum;
    footerData['nbmny'] = nbmny;
    footerData['namny'] = namny;
    footerData['initbal'] = initbal;
    footerData['npmny'] = npmny;
    footerData['condedmny'] = condedmny;
    footerData['buymny'] = buymny;
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
	var qtype = null;
	if(row.iptype == 2){
		qtype = $("#qtype").combobox('getValue');
	}else{
		qtype = row.iptype;
	}
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + "/chnpay/chnpaybalance!queryDetail.action",
		data : {
			"cpid" : row.corpid,
			"qtype" : qtype,
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
				$('#mname').html(res[0].mname);
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
		height:'350',
		singleSelect : true,
		showFooter:true,
		columns : [ [ {
			width : '80',
			title : '日期',
			align:'center',
			halign:'center',
			field : 'ddate',
		}, {
			width : '80',
			title : '摘要',
            halign:'center',
			field : 'memo',
			formatter : function(value) {
	    		if(value!=undefined){
	    			return "<span title='" + value + "'>" + value + "</span>";
	    		}
			}
		}, {
			width : '150',
			title : '原客户名称',
			align:'left',
            halign:'center',
			field : 'oldname',
			formatter : function(value) {
	    		if(value!=undefined){
	    			return "<span title='" + value + "'>" + value + "</span>";
	    		}
			}
		}, {
			width : '150',
			title : '客户名称',
			align:'left',
            halign:'center',
			field : 'corpknm',
			formatter : function(value) {
	    		if(value!=undefined){
	    			return "<span title='" + value + "'>" + value + "</span>";
	    		}
			}
		}, {
			width : '130',
			title : '合同编码',
			align:'left',
            halign:'center',
			field : 'vccode',
			formatter : function(value) {
	    		if(value!=undefined){
	    			return "<span title='" + value + "'>" + value + "</span>";
	    		}
			}
		}, {
			width : '80',
			title : '合同代账费',
			align:'right',
            halign:'center',
			field : 'namny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '60',
			title : '账本费',
			align:'right',
            halign:'center',
			field : 'nbmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '90',
			title : '扣款比率(%)',
			align:'center',
            halign:'center',
			field : 'propor',
			formatter : function(value,row,index){
				if(value == 0){
					return null;
				}else{
					return value;
				}
			}
		}, {
			width : '80',
			title : '付款金额',
			align:'right',
            halign:'center',
			field : 'npmny',
			formatter : npFormat
		}, {
			width : '70',
			title : '扣款金额',
			align:'right',
            halign:'center',
			field : 'usemny',
			formatter : useFormat
		}, {
			width : '90',
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
			var nbmny = parseFloat(0);
			var namny = parseFloat(0);
            var npmnysum = parseFloat(0);	// 付款金额
            var usemnysum = parseFloat(0);	// 已用金额
            var balmnysum = parseFloat(0);	// 余额
            for (var i = 0; i < rows.length; i++) {
            	nbmny += getFloatValue(rows[i].nbmny);
            	namny += getFloatValue(rows[i].namny);
            	npmnysum += getFloatValue(rows[i].npmny);
            	usemnysum += getFloatValue(rows[i].usemny);
            	if(i == rows.length - 1){
            		balmnysum = getFloatValue(rows[i].balmny);
            	}
            }
            footerData['ddate'] = '合计';
            footerData['nbmny'] = nbmny;
            footerData['namny'] = namny;
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
			if(row.opertype ==  3){
				var url = 'channel/rebate/rebateinput.jsp?operate=topayc&pk_billid='+row.billid;
				var ss = "<a href='javascript:void(0)' style='color:blue' onclick=\"parent.addTabNew('返点单录入','"+url+"');\">"
				+formatMny(value)+"</a>";
				return ss ;
			}else if(row.opertype == 1){
				var url = 'channel/payment/payconfirm.jsp?operate=topayc&pk_billid='+row.billid;
				var ss = "<a href='javascript:void(0)' style='color:blue' onclick=\"parent.addTabNew('付款单确认','"+url+"');\">"
				+formatMny(value)+"</a>";
				return ss ;
			}else if(row.opertype == 4){
				var url = 'channel/refund/refundbill.jsp?operate=toref&id='+row.billid;
				var ss = "<a href='javascript:void(0)' style='color:blue' onclick=\"parent.addTabNew('退款单','"+url+"');\">"
				+formatMny(value)+"</a>";
				return ss ;
			}
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
	if(row.ddate != "合计"){
		if(row.opertype == 2){
			var url = 'channel/contract/contractconfrim.jsp?operate=tocont&pk_billid='+row.billid;
			var ss = "<a href='javascript:void(0)' style='color:blue' onclick=\"parent.addTabNew('合同审核','"+url+"');\">"
				+formatMny(value)+"</a>";
			return ss ;
		}else if(row.opertype == 5){
			var url = 'channel/dealmanage/channelorder.jsp?operate=toorder&billid='+row.billid;
			var ss = "<a href='javascript:void(0)' style='color:blue' onclick=\"parent.addTabNew('加盟商订单','"+url+"');\">"
				+formatMny(value)+"</a>";
			return ss ;
		}else{
			return formatMny(value);
		}
	}else{
		return formatMny(value);
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
		$('#grid').datagrid('options').queryParams = queryData;
	}
	$('#grid').datagrid('options').url = contextPath + '/chnpay/chnpaybalance!query.action';
	$('#grid').datagrid('reload');
	$('#grid').datagrid('unselectAll');
	$("#qrydialog").css("visibility", "hidden");
}

/**
 * 获取查询条件
 * @param cpname
 * @returns {___anonymous17096_17317}
 */
function getQueryData(cpname){
	var begdate = null;
	var enddate = null;
	var bperiod = null;
	var eperiod = null;
	var period = null;
	var cpname;
	var ischeck = $('#da').is(':checked');
	if(ischeck){
		begdate = $("#begdate").textbox('getValue');
		enddate = $("#enddate").textbox('getValue')
	}else{
		bperiod = $("#begperiod").val();
		eperiod = $("#endperiod").val();
		period = 'period';
	}
	if(!isEmpty(cpname)){
		cpname = cpname;
	}else{
		cpname = null
	}
	var aname = $("#aname").combobox('getValue');
	var qtype = $("#qtype").combobox('getValue');
	var mid = $("#managerid").val();
	var queryData = {
		"bperiod" : bperiod,
		"eperiod" : eperiod,
		"begdate" : begdate,
		"enddate" : enddate,
		'qtype' : qtype,
		'cpname' : cpname,
		'period' : period,
		'aname'	: aname,
		"corps" : $("#pk_account").val(),
		"mid" : mid,
	};
	return queryData;
}

/**
 * 快速过滤查询
 */
function quickfiltet(){
	$('#filter_value').textbox('textbox').keydown(function (e) {
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
	var callback=function(){
		var columns = $('#grid').datagrid("options").columns[0];
		Business.getFile(DZF.contextPath+ '/chnpay/chnpaybalance!onExport.action',{
			'strlist':JSON.stringify(datarows),'qj' : $('#jqj').html(),}, true, true);
	}
	checkBtnPower('export','channel3',callback);
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
	var mname = $("#mname").text();
	
	Business.getFile(contextPath+ '/chnpay/chnpaybalance!onDetPrint.action',{'strlist':JSON.stringify(datarows),
		'columns':JSON.stringify(columns),'qrydate':qrydate,'corpnm':corpnm,'ptypenm':ptypenm,'mname':mname}, true, true);
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
	var callback=function(){
		var columns = $('#gridh').datagrid("options").columns[0];
 		var qrydate = $("#qrydate").text();
 		var corpnm = $("#corpnm").text();
 		var ptypenm = $("#ptypenm").text();
 		var mname = $("#mname").text();
 		
 		Business.getFile(contextPath+ '/chnpay/chnpaybalance!onDetExport.action',{'strlist':JSON.stringify(datarows),
 			'columns':JSON.stringify(columns),'qrydate':qrydate,'ptypenm':ptypenm,'corpnm':corpnm,'mname':mname}, true, true);
	}
	checkBtnPower('export','channel3',callback);
}

/**
 * 渠道经理参照初始化
 */
function initManagerRef(){
	$('#manager').textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#manDlg").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择渠道经理',
                    modal: true,
                    href: DZF.contextPath + '/ref/manager_select.jsp',
                    buttons: '#manBtn'
                });
            }
        }]
    });
}

/**
 * 渠道经理选择事件
 */
function selectMans(){
	var rows = $('#mgrid').datagrid('getSelections');
	dClickMans(rows);
}

/**
 * 双击选择渠道经理
 * @param rowTable
 */
function dClickMans(rowTable){
	var unames = "";
	var uids = [];
	if(rowTable){
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个经理",
				type : 2
			});
			return;
		}
		for(var i=0;i<rowTable.length;i++){
			if(i == rowTable.length - 1){
				unames += rowTable[i].uname;
			}else{
				unames += rowTable[i].uname+",";
			}
			uids.push(rowTable[i].uid);
		}
		$("#manager").textbox("setValue",unames);
		$("#managerid").val(uids);
	}
	 $("#manDlg").dialog('close');
}
	