var corpid;
/**
 * 明细查询
 */
function qryDetail(cid,corpnm){
	var bdate = $("#bdate").datebox("getValue");
	var edate = $("#edate").datebox("getValue");
	var	qrydate = bdate + "至" + edate;
	corpid=cid;
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath +'/report/manager!queryDetail.action',
		data : {
			"corpid" : corpid,
			"bdate" : bdate,
			"edate" : edate,
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
				if (isEmpty(res)) {
					$('#detail_dialog').dialog('close');
					Public.tips({
						content : "无明细记录",
						type : 2
					});
					return;
				}
				$('#corpnm').html(corpnm);
				$('#qrydate').html(qrydate);
				$('#gridh').datagrid('loadData',res);
				$('#detail_dialog').dialog('open');
				
				$('#gridh').datagrid('resize',{ 
					height : '350',
					width : '100%'
				});
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
		fitColumns : true,
		/*height : Public.setGrid().h,*/
		height:'350',
		singleSelect : true,
		showFooter:true,
		columns : [ [ {
			width : '90',
			title : '日期',
			align:'center',
			halign:'center',
			field : 'edate',
		}, {
			width : '60',
			title : '提单量',
			align:'center',
			halign:'center',
			field : 'anum',
		}, {
			width : '160',
			title : '合同编码',
            halign:'left',
			field : 'vccode',
			formatter :useFormat,
		},{
			
			width : '100',
			title : '合同代账费',
			align:'right',
            halign:'center',
			field : 'antlmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '100',
			title : '预付款扣款',
			align:'right',
            halign:'center',
			field : 'ndemny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '100',
			title : '返点扣款',
			align:'right',
            halign:'center',
			field : 'nderebmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},] ],
		onLoadSuccess : function(data) {
			var rows = $('#gridh').datagrid('getRows');
			var footerData = new Object();
            var anum = parseFloat(0);	
            var antlmny = parseFloat(0);	
            var ndemny = parseFloat(0);	
            var nderebmny = parseFloat(0);	
            for (var i = 0; i < rows.length; i++) {
            	anum += getFloatValue(rows[i].anum);
            	antlmny += getFloatValue(rows[i].antlmny);
            	ndemny += getFloatValue(rows[i].ndemny);
            	nderebmny += getFloatValue(rows[i].nderebmny);
            }
            footerData['edate'] = '合计';
            footerData['anum'] = anum;
            footerData['antlmny'] = antlmny;
            footerData['ndemny'] = ndemny;
            footerData['nderebmny'] = nderebmny;
            var fs=new Array(1);
            fs[0] = footerData;
            $('#gridh').datagrid('reloadFooter',fs);
            $('#gridh').datagrid("scrollTo",0);
		},
	});
}

/**
 * 扣款金额格式化
 * @param value
 * @param row
 * @param index
 * @returns
 */
function useFormat(value,row,index){
	if(row.edate != "合计"){
		var url = 'channel/contract/contractconfrim.jsp?operate=tocont&pk_billid='+row.corpid;
		var ss = "<a href='javascript:void(0)' style='color:blue' onclick=\"parent.addTabNew('合同审核','"+url+"');\">"+value+"</a>";
		return ss ;
	}
}


/**
 * 明细打印
 */
function onDetPrint(){
	var datarows = $(id).datagrid("getRows");
	if( datarows == null||datarows.length == 0){
		Public.tips({content:'明细数据为空',type:2});
		return;
	}
	var columns = $(id).datagrid("options").columns[0];
	var qrydate = $("#qrydate").text();
	var corpnm = $("#corpnm").text();
	Business.getFile(contextPath+ '/report/manager!onDetPrint.action',{'strlist':JSON.stringify(datarows),
		'columns':JSON.stringify(columns),'qrydate':qrydate,'corpnm':corpnm}, true, true);
}

/**
 * 明细导出
 */
function onDetExport(type){
	var datarows = $(id).datagrid("getRows");
	if( datarows == null||datarows.length == 0){
		Public.tips({content:'明细数据为空',type:2});
		return;
	}
	var callback=function(){
		var columns = $(id).datagrid("options").columns[0];
		var qrydate = $("#qrydate").text();
		var corpnm = $("#corpnm").text();
		Business.getFile(contextPath+ '/report/manager!onDetExport.action',{'strlist':JSON.stringify(datarows),
			'columns':JSON.stringify(columns),'qrydate':qrydate,'corpnm':corpnm}, true, true);
	}
	var funcode='channel18';
	if(type=='1'){
		funcode='channel16';
	}else if(type=='2'){
		funcode='channel17';
	}
	checkBtnPower('export',funcode,callback);
}


function initProvince(queryData){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryProvince.action',
		data : queryData,
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

function initManager(queryData){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryTrainer.action',
		data : queryData,
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#cuid').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

/**
 * 合计行
 */
function setFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
    var bondmny = 0;	
    var predeposit = 0;	
    var xgmNum = 0;	
    var ybrNum = 0;	
    var rnum = 0;	
    var anum = 0;	
    var rntlmny = 0;	
    var antlmny = 0;
//    var uprice= 0;
    var ndemny = 0;	
    var nderebmny = 0;	
    var outmny = 0;	
    var retmny = 0;
    for (var i = 0; i < rows.length; i++) {
    	bondmny += parseFloat(rows[i].bondmny);
    	predeposit += parseFloat(rows[i].predeposit);
    	xgmNum += parseFloat(rows[i].xgmNum);
    	ybrNum += parseFloat(rows[i].ybrNum);
    	rnum += parseFloat(rows[i].rnum);
    	anum += parseFloat(rows[i].anum);
    	rntlmny += parseFloat(rows[i].rntlmny);
    	antlmny += parseFloat(rows[i].antlmny);
//    	uprice += parseFloat(rows[i].uprice);
    	ndemny += parseFloat(rows[i].ndemny);
    	nderebmny += parseFloat(rows[i].nderebmny);
    	outmny += parseFloat(rows[i].outmny);
    	
    	retmny += parseFloat(isEmpty(rows[i].retmny) ? 0 : rows[i].retmny);
    }
    footerData['corpnm'] = '合计';
    footerData['bondmny'] = bondmny;
    footerData['predeposit'] = predeposit;
    footerData['xgmNum'] = xgmNum;
    footerData['ybrNum'] = ybrNum;
    footerData['rnum'] = rnum;
    footerData['anum'] = anum;
    footerData['rntlmny'] = rntlmny;
    footerData['antlmny'] = antlmny;
//    footerData['uprice'] = uprice;
    footerData['ndemny'] = ndemny;
    footerData['nderebmny'] = nderebmny;
    footerData['outmny'] = outmny;
    footerData['retmny'] = retmny;
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}

/**
 * 合并单元格
 * @returns
 */
function mergeCell(data,is){
	var mark=1;                                              
　　　for (var i=1; i <data.rows.length; i++) {    
　　　　　　if (data.rows[i]['provname'] == data.rows[i-1]['provname']) {  
　　　　　　　　mark += 1;                                            
　　　　　　　　$(is).datagrid('mergeCells',{ 
　　　　　　　　　　index: i+1-mark,                 
　　　　　　　　　　field: 'uprice',              
　　　　　　　　　　rowspan:mark                 
　　　　　　　　}); 
　　　　　　}else{
　　　　　　　　mark=1;                                
　　　　　　}
　	}
}

/**
 * 导出
 */
function doExport(type){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'当前界面数据为空',type:2});
		return;
	}
	var callback=function(){
		var columns = $('#grid').datagrid("options").columns[0];
//		var qj = $('#bdate').datebox('getValue') + '至' + $('#edate').datebox('getValue');,'qj':qj
		Business.getFile(DZF.contextPath+ '/report/manager!exportExcel.action',
				{'strlist':JSON.stringify(datarows),'columns':JSON.stringify(columns),'type':type}, true, true);
	}
	var funcode='channel18';
	if(type=='1'){
		funcode='channel16';
	}else if(type=='2'){
		funcode='channel17';
	}
	checkBtnPower('export',funcode,callback);
}

/**
 * 初始化未审核的合同明细
 */
function initWshGrid(){
	 $('#gridw').datagrid({
			border : true,
			striped : true,
			rownumbers : true,
			fitColumns : true,
			/*height : Public.setGrid().h,*/
			height:'350',
			singleSelect : true,
			showFooter:true,
			columns : [ [ {
				width : '100',
				title : '提单日期',
				align:'center',
				halign:'center',
				field : 'edate',
			}, {
				width : '80',
				title : '提单量',
				align:'center',
				halign:'center',
				field : 'anum',
			}, {
				width : '180',
				title : '合同编码',
	            halign:'left',
				field : 'vccode',
				formatter :weiFormat,
			},{
				width : '120',
				title : '合同代账费',
				align:'right',
	            halign:'center',
				field : 'antlmny',
				formatter : function(value,row,index){
					if(value == 0)return "0.00";
					return formatMny(value);
				}
			}] ],
			onLoadSuccess : function(data) {
				var rows = $('#gridw').datagrid('getRows');
				var footerData = new Object();
	            var anum = parseFloat(0);	
	            var antlmny = parseFloat(0);	
	            for (var i = 0; i < rows.length; i++) {
	            	anum += getFloatValue(rows[i].anum);
	            	antlmny += getFloatValue(rows[i].antlmny);
	            }
	            footerData['edate'] = '合计';
	            footerData['anum'] = anum;
	            footerData['antlmny'] = antlmny;
	            var fs=new Array(1);
	            fs[0] = footerData;
	            $('#gridw').datagrid('reloadFooter',fs);
	            $('#gridw').datagrid("scrollTo",0);
			},
		});
}

function weiFormat(value,row,index){
	if(row.edate != "合计"){
		var url = 'channel/contract/contractconfrim.jsp?operate=toYnt&contractid='+row.corpid;
		var ss = "<a href='javascript:void(0)' style='color:blue' onclick=\"parent.addTabNew('合同审核','"+url+"');\">"+value+"</a>";
		return ss ;
	}
}

function initYbhGrid(){
	 $('#gridb').datagrid({
			border : true,
			striped : true,
			rownumbers : true,
			fitColumns : true,
			/* height : Public.setGrid().h, */
			height:'350',
			singleSelect : true,
			showFooter:true,
			columns : [ [ {
				width : '80',
				title : '提单日期',
				align:'center',
				halign:'center',
				field : 'edate',
			}, {
				width : '80',
				title : '提单量',
				align:'center',
				halign:'center',
				field : 'anum',
			}, {
				width : '160',
				title : '合同编码',
	            halign:'left',
				field : 'vccode',
				formatter :ybhFormat,
			},{
				width : '100',
				title : '合同代账费',
				align:'right',
	            halign:'center',
				field : 'antlmny',
				formatter : function(value,row,index){
					if(value == 0)return "0.00";
					return formatMny(value);
				}
			}, {
				width : '80',
				title : '驳回日期 ', 
				align:'center',
				halign:'center',
				field : 'bdate',
			},{
				width : '140',
				title : '驳回原因',
	            halign:'left',
				field : 'provname',
				formatter :forString,
			},] ],
			onLoadSuccess : function(data) {
				var rows = $('#gridw').datagrid('getRows');
				var footerData = new Object();
	            var anum = parseFloat(0);	
	            var antlmny = parseFloat(0);	
	            for (var i = 0; i < rows.length; i++) {
	            	anum += getFloatValue(rows[i].anum);
	            	antlmny += getFloatValue(rows[i].antlmny);
	            }
	            footerData['edate'] = '合计';
	            footerData['anum'] = anum;
	            footerData['antlmny'] = antlmny;
	            var fs=new Array(1);
	            fs[0] = footerData;
	            $('#gridw').datagrid('reloadFooter',fs);
	            $('#gridw').datagrid("scrollTo",0);
			},
		});
}

function forString(value){
	if(value!=undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}
}

function ybhFormat(value,row,index){
	if(row.edate != "合计"){
		var url = 'channel/contract/contractconfrim.jsp?operate=toYnt&contractid='+row.corpid;
		var ss = "<a href='javascript:void(0)' style='color:blue' onclick=\"parent.addTabNew('合同审核','"+url+"');\">"+value+"</a>";
		return ss ;
	}
}

function initTabs(){
	$('#detail').tabs({
	    border:false,
	    onSelect:function(title){
			if("已审核" == title){
				id='#gridh';
				$('#gridh').datagrid('options').url = contextPath + '/report/manager!queryDetail.action';
				$('#gridh').datagrid('load', {"corpid":corpid,"bdate":$('#bdate').datebox('getValue'),"edate":$('#edate').datebox('getValue')});
			}else if("未审核" == title){
				id='#gridw';
				$('#gridw').datagrid('options').url = contextPath + '/report/manager!queryWDetail.action?ovince=5';
				$('#gridw').datagrid('load', {"corpid":corpid,"bdate":$('#bdate').datebox('getValue'),"edate":$('#edate').datebox('getValue')});
			}else if("已驳回" == title){
				id='#gridb';
				$('#gridb').datagrid('options').url = contextPath + '/report/manager!queryWDetail.action?ovince=7';
				$('#gridb').datagrid('load', {"corpid":corpid,"bdate":$('#bdate').datebox('getValue'),"edate":$('#edate').datebox('getValue')});
			}
	    }
	});
}

