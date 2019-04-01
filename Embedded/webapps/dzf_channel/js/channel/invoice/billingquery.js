
//var isenter = false;//是否快速查询
//var loadrows = null;

// 数据表格随窗口大小改变
$(window).resize(function() {
	$('#grid').datagrid('resize', {
		height : Public.setGrid().h,
	});
});

$(function(){
	initListener();
	load();
	initArea();
	initChannel();
//	reloadData();
	initGridP();
	initRef();
});

//初始化监听
function initListener(){
	$('#bdate').datebox("setValue",parent.SYSTEM.LoginDate);
	$("#querydate").html(parent.SYSTEM.LoginDate);
	// 查询框鼠标经过
	$("#querydate").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	fastQry();
}

/**
 * 参照初始化
 */
function initRef(){
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
	
	$('#operater').textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#operDlg").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择渠道运营',
                    modal: true,
                    href: DZF.contextPath + '/ref/operater_select.jsp',
                    buttons: '#operBtn'
                });
            }
        }]
    });
}

/**
 * 渠道经理选择事件
 */
function selectMans(){
	var rows = $('#mgrid').datagrid('getChecked');
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

/**
 * 渠道运营选择事件
 */
function selectOpers(){
	var rows = $('#ogrid').datagrid('getChecked');
	dClickOpers(rows);
}

/**
 * 双击选择渠道运营
 * @param rowTable
 */
function dClickOpers(rowTable){
	var unames = "";
	var uids = [];
	if(rowTable){
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个运营",
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
		$("#operater").textbox("setValue",unames);
		$("#operaterid").val(uids);
	}
	$("#operDlg").dialog('close');
}

/**
 * 快速查询
 */
function fastQry(){
	$('#quname').textbox('textbox').keydown(function (e) {
        if (e.keyCode == 13) {
        	$('#grid').datagrid('unselectAll');
        	var url = DZF.contextPath + '/invoice/billingquery!query.action';
        	$('#grid').datagrid('options').url = url;
        	var bdate = $('#bdate').datebox('getValue'); //开始日期
        	var rows = $('#grid').datagrid('getRows');
 		    var filtername = $("#quname").val(); 
 		    if (filtername) {
 		    	if(rows != null && rows.length > 0){
 		    	    $('#grid').datagrid('load', {
 		    	        bdate: bdate,
 		    	        cname: filtername,
 		    	    });
 		    	}else{
 		    	    $('#grid').datagrid('load', {
 		    	    	corps : $("#pk_account").val(),
 		    	        bdate: bdate,
 		    	        cname: filtername,
 		    	    });
 		    	}
 		    }
        }
    });
}

function closeCx(){
	$("#qrydialog").hide();
}

function load(){
	$('#grid').datagrid({
		//url : contextPath + '/order/orderact!query.action',
		striped : true,
		rownumbers : true,
		fitColumns: false,
		remoteSort : false,
		singleSelect : false,
		height : Public.setGrid().h,
		idField : 'corpid',
		showFooter:true,
//		pagination : true,
//		pageSize : 20,
//		pageList : [ 20, 50, 100, 200 ],
		columns : [[{width : '10',title : '',field : 'id',checkbox: true},
		            {width : '60',title : '大区',field : 'aname',align : 'left'},
		            {width : '120',title : '地区',field : 'provname',align : 'left'},
		            {width : '80',title : '渠道经理',field : 'mid',align : 'left'},
		            {width : '80',title : '渠道运营',field : 'oid',align : 'left'},
		            {width : '120',title : '加盟商编码',field : 'ccode',align:'left'},
		            {width : '260',title : '加盟商名称',field : 'cname',align:'left'},
//		            {width : '120',title : '累计扣款金额',field : 'dtotalmny',align:'right',
//		            	formatter: function(value,row,index){
//							if (value == 0) {
//								return "0.00";
//							}
////							return formatMny(value);
//							return "<a href='javascript:void(0)' style='color:blue' onclick=\"payDetail('"+index+"')\">" + formatMny(value) + "</a>";
//		            }},
		            {width : '140',title : '累计合同扣款金额',field : 'dconmny',align:'right',
		            	formatter: function(value,row,index){
							if (value == 0) {
								return "0.00";
							}
							if(!isEmpty(row.cname)){
								return "<a href='javascript:void(0)' style='color:blue' onclick=\"payDetail('"+index+"',1)\">" 
								+ formatMny(value) + "</a>";
							}else{
								return formatMny(value);
							}
		            }},
		            {width : '140',title : '累计商品扣款金额',field : 'dbuymny',align:'right',
		            	formatter: function(value,row,index){
							if (value == 0) {
								return "0.00";
							}
							if(!isEmpty(row.cname)){
								return "<a href='javascript:void(0)' style='color:blue' onclick=\"payDetail('"+index+"',2)\">" 
								+ formatMny(value) + "</a>";
							}else{
								return formatMny(value);
							}
		            }},
//		            {width : '120',title : '累计开票金额',field : 'btotalmny',align:'right',
//		            	formatter: function(value,row,index){
//							if (value == 0) {
//								return "0.00";
//							}
////							return formatMny(value);
//							return "<a href='javascript:void(0)' style='color:blue' onclick=\"invDetail('"+index+"')\">" + formatMny(value) + "</a>";
//		            }},
		            {width : '130',title : '累计合同开票金额',field : 'bconmny',align:'right',
		            	formatter: function(value,row,index){
							if (value == 0) {
								return "0.00";
							}
							if(!isEmpty(row.cname)){
								return "<a href='javascript:void(0)' style='color:blue' onclick=\"invDetail('"+index+"', 1)\">" 
								+ formatMny(value) + "</a>";
							}else{
								return formatMny(value);
							}
		            }},
		            {width : '130',title : '累计商品开票金额',field : 'bbuymny',align:'right',
		            	formatter: function(value,row,index){
							if (value == 0) {
								return "0.00";
							}
							if(!isEmpty(row.cname)){
								return "<a href='javascript:void(0)' style='color:blue' onclick=\"invDetail('"+index+"', 2)\">" 
								+ formatMny(value) + "</a>";
							}else{
								return formatMny(value);
							}
		            }},
		            {width : '140',title : '合同扣款未开票金额',field : 'noticketmny',align:'right',
		            	formatter: function(value,row,index){
							if (value == 0) {
								return "0.00";
							}
							return formatMny(value);
		            }},
		            {width : '140',title : '商品购买未开票金额',field : 'notbmny',align:'right',
		            	formatter: function(value,row,index){
							if (value == 0) {
								return "0.00";
							}
							return formatMny(value);
		            }},
		            {width : '100',title : '',field : 'corpid',hidden:true},
		          ]],
		onBeforeLoad : function(param) {
			$.messager.progress({
				text : '数据加载中....'
			});
		},
		onLoadSuccess : function(data) {
//			if(data.rows && loadrows == null){
//				loadrows = data.rows;
//			}
			$.messager.progress('close');
			$("#qrydialog").hide();
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
//    var dtotalmny = parseFloat(0);	
//    var btotalmny = parseFloat(0);	
    var noticketmny = parseFloat(0);	
    
    var dconmny = parseFloat(0);//累计合同扣款金额
    var dbuymny = parseFloat(0);//累计商品扣款金额
    var bconmny = parseFloat(0);//累计合同开票金额
    var bbuymny = parseFloat(0);//累计商品开票金额
    
    var notbmny = parseFloat(0);//商品购买未开票金额
    for (var i = 0; i < rows.length; i++) {
//    	dtotalmny = dtotalmny.add(getFloatValue(rows[i].dtotalmny));
//    	btotalmny = btotalmny.add(getFloatValue(rows[i].btotalmny));
    	noticketmny = noticketmny.add(getFloatValue(rows[i].noticketmny));
    	
    	dconmny = dconmny.add(getFloatValue(rows[i].dconmny));
    	dbuymny = dbuymny.add(getFloatValue(rows[i].dbuymny));
    	bconmny = bconmny.add(getFloatValue(rows[i].bconmny));
    	bbuymny = bbuymny.add(getFloatValue(rows[i].bbuymny));
    	notbmny = notbmny.add(getFloatValue(rows[i].notbmny));
    	
    }
    footerData['ccode'] = '合计';
//    footerData['dtotalmny'] = dtotalmny;
//    footerData['btotalmny'] = btotalmny;
    footerData['noticketmny'] = noticketmny;
    
    footerData['dconmny'] = dconmny;
    footerData['dbuymny'] = dbuymny;
    footerData['bconmny'] = bconmny;
    footerData['bbuymny'] = bbuymny;
    footerData['notbmny'] = notbmny;
    
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}

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
 * 初始化加盟商
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
 * 查询
 */
function reloadData(){
	loadrows = null;
	$('#grid').datagrid('options').url = DZF.contextPath + '/invoice/billingquery!query.action';
	var bdate = $('#bdate').datebox('getValue'); //开始日期
    $('#grid').datagrid('load', {
    	corps : $("#pk_account").val(),
        bdate: bdate,
        aname: $("#aname").combobox('getValue'),
        mid : $("#managerid").val(),
		oid : $("#operaterid").val(),
    });
    $('#qrydialog').hide();
    $('#grid').datagrid('unselectAll');
    $("#querydate").html(bdate);
}

function clearParams(){
	$("#pk_account").val(null);
	$('#aname').combobox('setValue', null);
	$("#channel_select").textbox("setValue",null);
}

function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}

/**
 * 导出
 */
function onExport(){
	var datarows = $('#grid').datagrid("getChecked");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'请选择需导出的数据',type:2});
		return;
	}
	var callback=function(){
		var columns = $('#grid').datagrid("options").columns[0];
		var bdate = $('#bdate').datebox('getValue'); //开始日期
		Business.getFile(DZF.contextPath+ '/invoice/billingquery!onExport.action',
				{'strlist':JSON.stringify(datarows),'bdate':bdate}, true, true);
	}
	checkBtnPower('export','channel22',callback);
}

/**
 * 开票
 */
function onBilling(){
	var rows = $('#grid').datagrid("getSelections");
	if(rows == null || rows.length == 0){
		Public.tips({content : "请选择需要处理的数据!" ,type:2});
		return;
	}
	
	var invoices = '';
	for(var i=0; i<rows.length;i++){
		invoices = invoices + JSON.stringify(rows[i]);
	}
	var postdata = new Object();
	postdata["invoices"] = invoices;
	
	$.messager.confirm('提示','确认生成这些加盟商的开票申请吗？',
		function(conf){
			if(conf){
				$.ajax({
					type : 'post',
					url : DZF.contextPath + '/invoice/billingquery!insertBilling.action',
					data : postdata,
					dataType : 'json',
					success: function(result){
						if(result.success){
							reloadData();
							Public.tips({content : result.msg ,type:0});
						}else{
							Public.tips({content : result.msg ,type:2});
						}
					}
				});
			}
	});
}

/**
 * 扣款明细联查
 * @param index
 */
function payDetail(index, qtype){
	var qrydate = $("#bdate").datebox("getValue");
	var rows = $('#grid').datagrid('getRows');
	var row= rows[index];
	$('#gridp').datagrid('options').url = DZF.contextPath + '/invoice/billingquery!queryRecDetail.action';
	$('#corpnm').html(row.cname);
	$('#qrydate').html(qrydate);
	$('#payDetail').dialog('open');
    $('#gridp').datagrid('load', {
    	begdate : qrydate,
		cpid: row.corpid,
		qtype : qtype,
    });
}

/**
 * 扣款明细列表
 */
function initGridP(){
	gridh = $('#gridp').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height:'350',
		singleSelect : true,
		pagination : true,
		pageSize : DZF.pageSize_min,
		pageList : DZF.pageList_min,
		showFooter:true,
		columns : [ [ {
			width : '80',
			title : '日期',
			align:'center',
			halign:'center',
			field : 'ddate',
		}, {
			width : '260',
			title : '摘要',
            halign:'center',
			field : 'memo',
			formatter : function(value) {
	    		if(value!=undefined){
	    			return "<span title='" + value + "'>" + value + "</span>";
	    		}
			}
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
			width : '80',
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
		},{
			width : '90',
			title : '扣款金额',
			align:'right',
            halign:'center',
			field : 'usemny',
			formatter : useFormat
		}] ],
		onLoadSuccess : function(data) {
			var rows = $('#gridp').datagrid('getRows');
			var footerData = new Object();
			var nbmny = parseFloat(0);
			var namny = parseFloat(0);
            var npmnysum = parseFloat(0);	// 付款金额
            for (var i = 0; i < rows.length; i++) {
            	nbmny += getFloatValue(rows[i].nbmny);
            	namny += getFloatValue(rows[i].namny);
            	npmnysum += getFloatValue(rows[i].usemny);
            }
            footerData['ddate'] = '合计';
            footerData['nbmny'] = nbmny;
            footerData['namny'] = namny;
            footerData['usemny'] = npmnysum;
            var fs=new Array(1);
            fs[0] = footerData;
            $('#gridp').datagrid('reloadFooter',fs);
            $('#gridp').datagrid("scrollTo",0);
		},
	});
}

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
 * 扣款明细---打印
 */
function onRecPrint(){
	var datarows = $('#gridp').datagrid("getRows");
	if( datarows == null||datarows.length == 0){
		Public.tips({content:'明细数据为空',type:2});
		return;
	}
	var columns = $('#gridp').datagrid("options").columns[0];
	var qrydate = $("#qrydate").text();
	var corpnm = $("#corpnm").text();
	
	Business.getFile(DZF.contextPath+ '/invoice/billingquery!onRecPrint.action',{'strlist':JSON.stringify(datarows),
		'columns':JSON.stringify(columns),'qrydate':qrydate,'corpnm':corpnm}, true, true);
}
/**
 * 扣款明细---导出
 */
function onRecExport(){
	var datarows = $('#gridp').datagrid("getRows");
	if( datarows == null||datarows.length == 0){
			Public.tips({content:'明细数据为空',type:2});
			return;
		}
	var callback=function(){
		var columns = $('#gridp').datagrid("options").columns[0];
 		var qrydate = $("#qrydate").text();
 		var corpnm = $("#corpnm").text();
 		Business.getFile(DZF.contextPath+ '/invoice/billingquery!onRecExport.action',{'strlist':JSON.stringify(datarows),
 			'columns':JSON.stringify(columns),'qrydate':qrydate,'corpnm':corpnm}, true, true);
	}
	checkBtnPower('export','channel22',callback);
}

/**
 * 累计开票明细
 * @param index
 */
function invDetail(index, isourtype){
	var qrydate = $("#bdate").datebox("getValue");
	var rows = $('#grid').datagrid('getRows');
	var row= rows[index];
	var url = 'channel/invoice/sys_fpmng.jsp?operate=linkqry&corpid='+row.corpid+'&edate='+qrydate+'&isourtype='+isourtype;
	parent.addTabNew('发票管理', url);
}
