/**
 * 明细查询
 */
function qryDetail(index){
	var bdate = $("#bdate").datebox("getValue");
	var edate = $("#edate").datebox("getValue");
	var	qrydate = bdate + "至" + edate;
	var rows = $('#grid').datagrid('getRows');
	var row= rows[index];
	var corpnm=row.corpnm;
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath +'/report/manager!queryDetail.action',
		data : {
			"corpid" : row.corpid,
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
		showFooter:true,
		columns : [ [ {
			width : '110',
			title : '日期',
			align:'center',
			halign:'center',
			field : 'edate',
		}, {
			width : '100',
			title : '提单量',
            halign:'center',
			field : 'num',
		},{
			width : '120',
			title : '合同代账费',
			align:'right',
            halign:'center',
			field : 'ntlmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '120',
			title : '预付款扣款',
			align:'right',
            halign:'center',
			field : 'ndemny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '140',
			title : '返点扣款',
			align:'right',
            halign:'center',
			field : 'nderebmny',
			formatter :useFormat
		},] ],
		onLoadSuccess : function(data) {
			var rows = $('#gridh').datagrid('getRows');
			var footerData = new Object();
            var num = parseFloat(0);	
            var ntlmny = parseFloat(0);	
            var ndemny = parseFloat(0);	
            var nderebmny = parseFloat(0);	
            for (var i = 0; i < rows.length; i++) {
            	num += getFloatValue(rows[i].num);
            	ntlmny += getFloatValue(rows[i].ntlmny);
            	ndemny += getFloatValue(rows[i].ndemny);
            	nderebmny += getFloatValue(rows[i].nderebmny);
            }
            footerData['edate'] = '合计';
            footerData['num'] = num;
            footerData['ntlmny'] = ntlmny;
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
		var ss = "<a href='javascript:void(0)' style='color:blue' onclick=\"parent.addTabNew('合同审核','"+url+"');\">"+formatMny(value)+"</a>";
		return ss ;
	}else{
		return formatMny(value);
	}
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
	Business.getFile(contextPath+ '/report/manager!onDetPrint.action',{'strlist':JSON.stringify(datarows),
		'columns':JSON.stringify(columns),'qrydate':qrydate,'corpnm':corpnm}, true, true);
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
	Business.getFile(contextPath+ '/report/manager!onDetExport.action',{'strlist':JSON.stringify(datarows),
		'columns':JSON.stringify(columns),'qrydate':qrydate,'corpnm':corpnm}, true, true);
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
