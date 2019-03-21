$(function(){
	initQryDate();
	load();
	initChannel();
	reloadData();
});

/**
 * 查询日期初始化
 */
function initQryDate(){
	initPeriod("#qryperiod");
	$("#qryperiod").datebox("setValue", parent.SYSTEM.LoginDate);
	$("#jqj").html(parent.SYSTEM.LoginDate);
}

/**
 * 查询数据
 */
function reloadData(){
	var url = DZF.contextPath + '/report/goodssalesanalysis!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		"period" : $("#qryperiod").datebox("getValue"),
		"cpid" : $("#pk_account").val(),
	});
	$('#qrydialog').hide();
	$("#jqj").html($("#qryperiod").datebox("getValue"));
}

/**
 * 管理查询对话框
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 清除查询条件
 */
function clearParams(){
	$("#pk_account").val(null);
	$("#channel_select").textbox("setValue",null);
}

/**
 * 加盟商参照初始化
 */
function initChannel(){
    $('#channel_select').textbox({
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
    					ovince :"-1"
    				},
                    buttons: '#chnBtn'
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
		$("#corpkid_ae").val(null);
		$("#corpkna_ae").textbox("setValue",null);
		if(!isEmpty(rowTable.length)&&rowTable.length==1){
			$('#corpkna_ae').textbox('readonly',false);
		}else{
			$('#corpkna_ae').textbox('readonly',true);
		}
		
		$("#channel_select").textbox("setValue",str);
		$("#pk_account").val(corpIds);
	}
	$("#chnDlg").dialog('close');
}

/**
 * 加盟商选择
 */
function selectCorps(){
	var rows = $('#gsTable').datagrid('getChecked');
	dClickCompany(rows);
}

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
		singleSelect : true,
		showFooter:true,
		columns : [ [ {
			width : '160',
			title : '加盟商',
			field : 'pname',
			align : 'left',
			halign : 'center',
		}, {
			width : '120',
			title : '商品',
			field : 'gname',
			align : 'left',
			halign : 'center',
		}, {
			width : '90',
			title : '规格',
			field : 'spec',
			align : 'left',
			halign : 'center',
		}, {
			width : '90',
			title : '型号',
			field : 'type',
			align : 'left',
			halign : 'center',
		}, {
			width : '60',
			title : '数量',
			field : 'amount',
			align : 'left',
			halign : 'center',
		}, {
			width : '100',
			title : '成本',
			align : 'right',
			halign : 'center',
			field : 'cost',
		}, {
			width : '100',
			title : '成本合计',
			align : 'right',
			halign : 'center',
			field : 'totalcost',
			styler : cellStyler,
		}, {
			width : '100',
			title : '售价',
			align : 'right',
			halign : 'center',
			field : 'price',
			formatter : function(value, row, index) {
				if (value == 0)
					return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '预付款',
			align : 'right',
			halign : 'center',
			field : 'ndemny',
			formatter : function(value, row, index) {
				if (value == 0)
					return "0.00";
				return formatMny(value);
			},
			styler : cellStyler,
		}, {
			width : '100',
			title : '返点',
			align : 'right',
			halign : 'center',
			field : 'nderebmny',
			formatter : function(value, row, index) {
				if (value == 0)
					return "0.00";
				return formatMny(value);
			},
			styler : cellStyler,
		}, {
			width : '100',
			title : '合计',
			align : 'right',
			halign : 'center',
			field : 'ndesummny',
			formatter : function(value, row, index) {
				if (value == 0)
					return "0.00";
				return formatMny(value);
			},
			styler : cellStyler,
		} ] ],
		onLoadSuccess : function(data) {
			$('#grid').datagrid("scrollTo", 0);
			calFooter();
			$("#grid").datagrid("autoMergeCells", ['pname']);
		},
	});
}

/**
 * 计算合计
 */
function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
    var totalcost = parseFloat(0);	
    for (var i = 0; i < rows.length; i++) {
    	if(rows[i].gname != "小计"){
    		totalcost = totalcost.add(getFloatValue(rows[i].totalcost));
    	}
    }
    footerData['gname'] = '合计';
    footerData['totalcost'] = totalcost;
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
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
	Business.getFile(DZF.contextPath+ '/report/goodssalesanalysis!onExport.action',
			{'strlist':JSON.stringify(datarows),'qj' : $("#qryperiod").datebox("getValue")}, true, true);
}

/**
 * 设置字体颜色
 * @param value
 * @param row
 * @param index
 * @returns {String}
 */
function cellStyler(value, row, index) {
	if (row.gname == "小计") {
		return 'color:green;';
	}
}

/**
 * 扩展合并行
 */
$.extend($.fn.datagrid.methods, {
	autoMergeCells : function(jq, fields) {
		return jq.each(function() {
			var target = $(this);
			if (!fields) {
				fields = target.datagrid("getColumnFields");
			}
			var rows = target.datagrid("getRows");
			var i = 0,
			j = 0,
			temp = {};
			for (i; i < rows.length; i++) {
				var row = rows[i];
				j = 0;
				for (j; j < fields.length; j++) {
					var field = fields[j];
					var tf = temp[field];
					if (!tf) {
						tf = temp[field] = {};
						tf[row[field]] = [ i ];
					} else {
						var tfv = tf[row[field]];
						if (tfv) {
							tfv.push(i);
						} else {
							tfv = tf[row[field]] = [ i ];
						}
					}
				}
			}
			$.each(temp, function(field, colunm) {
				$.each(colunm, function() {
					var group = this;
					if (group.length > 1) {
						var before,
						after,
						megerIndex = group[0];
						for (var i = 0; i < group.length; i++) {
							before = group[i];
							after = group[i + 1];
							if (after && (after - before) == 1) {
								continue;
							}
							var rowspan = before - megerIndex + 1;
							if (rowspan > 1) {
								target.datagrid('mergeCells', {
									index : megerIndex,
									field : field,
									rowspan : rowspan
								});
							}
							if (after && (after - before) != 1) {
								megerIndex = after;
							}
						}
					}
				});
			});
		});
	}
});