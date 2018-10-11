var contextPath = DZF.contextPath;
var grid;
var editIndex;

$(function(){
	load();
	initRef();
	setQryData();
	reloadData();
});

/**
 * 列表表格初始化
 */
function load(){
	grid = $('#grid').datagrid({
		striped : true,
		title : '',
		fitColumns:false,
		rownumbers : true,
		height : Public.setGrid().h,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		singleSelect : false,
		idField : 'refid',
		showFooter : true,
		columns : [ [ {
			field : 'aname',
			title : '大区',
			width : '130',
			halign : 'center',
			align : 'left'
		}, {
			field : 'area',
			title : '地区',
			width : '140',
			halign : 'center',
			align : 'left'
		}, {
			field : 'mname',
			title : '渠道经理',
			width : '160',
			halign : 'center',
			align : 'left'
		}, {
			field : 'corpcd',
			title : '加盟商编码',
			width : '115',
			halign : 'center',
			align : 'left'
		}, {
			field : 'corpnm',
			title : '加盟商名称',
			width : '160',
			halign : 'center',
			align : 'left'
		}, {
			field : 'remny',
			title : '退款金额',
			width : '115',
			halign : 'center',
			align : 'right',
			formatter : reFormat
		}, {
			field : 'corpid',
			title : '加盟商主键',
			hidden : true
		}, ] ],
		onLoadSuccess : function(data) {
			var rows = $('#grid').datagrid('getRows');
			var footerData = new Object();
			var remny = 0;// 退款金额
			for (var i = 0; i < rows.length; i++) {
				if (!isEmpty(rows[i].remny)) {
					remny += parseFloat(rows[i].remny);
				}
			}
			footerData['corpnm'] = '合计';
			footerData['remny'] = remny;
			var fs = new Array(1);
			fs[0] = footerData;
			$('#grid').datagrid('reloadFooter', fs);
		}
	});
}

/**
 * 退款金额格式化
 * @param value
 * @param row
 * @param index
 */
function reFormat(value,row,index){
	if(row.corpnm != "合计"){
		return "<a href='javascript:void(0)' style='color:blue' onclick=\"qryDetail('"+index+"')\">" + value + "</a>";
	}else{
		return formatMny(value);
	}
}

/**
 * 查询框初始化
 */
function setQryData(){
	var bdate = $('#bdate').datebox('getValue'); 
	var edate = $('#edate').datebox('getValue'); 
	$("#jqj").html(bdate + ' 至 ' + edate);
}

/**
 * 参照初始化
 */
function initRef(){
	//查询-加盟商参照初始化
	$('#qcorp').textbox({
		onClickIcon : function() {
			refid = $(this).attr("id");
		},
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
    					issingle : "false",
    					ovince :"-1"
    				},
                    buttons: '#chnBtn'
                });
            }
        }]
    });
	
	initArea();//查询大区初始化
	changeArea();//查询大区改变事件
	initManager({"qtype" :1});
}

/**
 * 查询大区（渠道区域）-下拉初始化
 */
function initArea(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryArea.action',
		data : {"qtype" : 1},
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
 * 查询大区改变事件
 */
function changeArea() {
	$("#aname").combobox({
		onChange : function(n, o) {
			var queryData = {
				"qtype" : 1
			};
			if (!isEmpty(n)) {
				queryData = {
					'aname' : n,
					"qtype" : 1
				};
				$('#ovince').combobox('setValue', null);
				$('#managerid').combobox('setValue', null);
			}
			initManager(queryData);
		}
	});
}

/**
 * 区域经理-下拉初始化
 * @param queryData
 */
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
			    $('#managerid').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
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
	var corpIds = [];
	var vprovince="";
	if(rowTable){
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个客户",
				type : 2
			});
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
		vprovince=rowTable[0].ovince;
		$("#" + refid).textbox("setValue",str);
		$("#" + refid + "id").val(corpIds);
		if(refid == "corp"){
			getRefundMny(corpIds[0]);
			$("#ovince").val(vprovince);
		}
	}
	$("#chnDlg").dialog('close');
}

/**
 * 查询框-清空
 */
function clearParams(){
	$('#qcorp').textbox("setValue",null);
	$('#qcorpid').val(null);
	$('#aname').combobox('setValue', null);
	$('#managerid').combobox('setValue', null);
}

/**
 * 查询框-取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 查询
 */
function reloadData(){
	var url = contextPath + '/refund/refunddetail!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'begdate' : $('#bdate').datebox('getValue'),
		'enddate' : $('#edate').datebox('getValue'),
		'cpid' : $("#qcorpid").val(),
		'aname' : $("#aname").combobox("getValue"),
		'mid' : $("#managerid").combobox("getValue"),
	});
	setQryData();
	$('#grid').datagrid('unselectAll');
	$('#grid').datagrid('clearSelections');
	$("#qrydialog").hide();
	$("#grid").datagrid('uncheckAll');
}

/**
 * 明细查询
 */
function qryDetail(index){
	var begdate = $("#bdate").datebox("getValue");
	var enddate = $("#edate").datebox("getValue");
	var qrydate = begdate + "至" + enddate;
	var rows = $('#grid').datagrid('getRows');
	var row= rows[index];
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + "/refund/refunddetail!queryDetail.action",
		data : {
			"cpid" : row.corpid,
			'begdate' : $('#bdate').datebox('getValue'),
			'enddate' : $('#edate').datebox('getValue'),
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
				initDetailGrid();
				$('#qrydate').html(qrydate);
				$('#corpnm').html(res[0].corpnm);
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
	$('#gridh').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height:'350',
		singleSelect : true,
		showFooter:true,
		columns : [ [ {
			width : '110',
			title : '摘要',
            halign:'center',
			field : 'memo',
		}, {
			width : '100',
			title : '客户编码',
			align:'left',
            halign:'center',
			field : 'corpkcd',
		}, {
			width : '160',
			title : '客户名称',
			align:'left',
            halign:'center',
			field : 'corpkna',
			formatter : function(value) {
	    		if(value!=undefined){
	    			return "<span title='" + value + "'>" + value + "</span>";
	    		}
			}
		}, {
			width : '90',
			title : '退款金额',
			align:'right',
            halign:'center',
			field : 'remny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '90',
			title : '退款日期',
			align:'center',
			halign:'center',
			field : 'refdate',
		}, {
			width : '160',
			title : '单据编号',
			align:'left',
            halign:'center',
			field : 'vcode',
		}, ] ],
		onLoadSuccess : function(data) {
			var rows = $('#gridh').datagrid('getRows');
			var footerData = new Object();
			var remny = parseFloat(0);
            for (var i = 0; i < rows.length; i++) {
            	remny += getFloatValue(rows[i].remny);
            }
            footerData['corpkna'] = '合计';
            footerData['remny'] = remny;
            var fs=new Array(1);
            fs[0] = footerData;
            $('#gridh').datagrid('reloadFooter',fs);
            $('#gridh').datagrid("scrollTo",0);
		},
	});
}

/**
 * 导出
 */
function onExport() {
	var datarows = $('#grid').datagrid("getRows");
	if (datarows == null || datarows.length == 0) {
		Public.tips({
			content : '请选择需导出的数据',
			type : 2
		});
		return;
	}
	var columns = $('#grid').datagrid("options").columns[0];
	Business.getFile(DZF.contextPath + '/refund/refunddetail!onExport.action',
			{
				'strlist' : JSON.stringify(datarows),
				'qj' : $('#jqj').html(),
			}, true, true);
}