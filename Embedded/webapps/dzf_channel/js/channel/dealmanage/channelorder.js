var contextPath = DZF.contextPath;
var editIndex = undefined;

$(function(){
	load();
	initRef();
	initQueryData();
	initQryLitener();
	initStockOut();
	loadData();//加载数据
});

/**
 * 查询初始化
 */
function initQueryData(){
	$("#querydate").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	$('#querydate').html(parent.SYSTEM.PreDate + ' 至 ' + parent.SYSTEM.LoginDate);
	$("#bdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#edate").datebox("setValue", parent.SYSTEM.LoginDate);
	
	$("#bperiod").datebox("setValue", parent.SYSTEM.PreDate);
	$("#eperiod").datebox("setValue", parent.SYSTEM.LoginDate);
}

/**
 * 查询框监听事件
 */
function initQryLitener(){
	$("#bdate").datebox("readonly", false);
	$("#edate").datebox("readonly", false);
	$('#bperiod').datebox("readonly", true);
	$('#eperiod').datebox("readonly", true);
    $('input:radio[name="seledate"]').change( function(){  
		var ischeck = $('#tddate').is(':checked');
		if(ischeck){
			var sdv = $('#bdate').datebox('getValue');
			var edv = $('#edate').datebox('getValue');
			$('#jqj').html(sdv + ' 至 ' + edv);
			$("#bdate").datebox("readonly", false);
			$("#edate").datebox("readonly", false);
			$('#bperiod').datebox("readonly", true);
			$('#eperiod').datebox("readonly", true);
		}else{
			var sdv = $("#bperiod").val();
			var edv = $("#eperiod").val();
			$('#jqj').html(sdv + ' 至 ' + edv);
			$("#bdate").datebox("readonly", true);
			$("#edate").datebox("readonly", true);
			$('#bperiod').datebox("readonly", false);
			$('#eperiod').datebox("readonly", false);
		}
	});
}

/**
 * 加载数据
 */
function loadData(){
	var obj = Public.getRequest();
	var operate = obj.operate;
	var url = DZF.contextPath + '/dealmanage/channelorder!query.action';
	$('#grid').datagrid('options').url = url;
	if(operate == "toorder"){
		$('#grid').datagrid('unselectAll');
		$('#grid').datagrid('clearSelections');
		var billid = obj.billid;
		$('#grid').datagrid('load', {
			'billid' : billid,
		});
	}else{
		reloadData();
	}
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
		singleSelect : false,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		showFooter:true,
		idField : 'billid',
		columns : [ [ {
			field : 'ck',
			checkbox : true
		}, {
			width : '100',
			title : '主键',
			field : 'billid',
			hidden : true
		}, {
			width : '100',
			title : '时间戳',
			field : 'updatets',
			hidden : true
		}, {
			width : '100',
			title : '收货人',
			field : 'rename',
			hidden : true
		}, {
			width : '100',
			title : '手机号码',
			field : 'phone',
			hidden : true
		}, {
			width : '100',
			title : '邮政编码',
			field : 'recode',
			hidden : true
		}, {
			width : '100',
			title : '收货地址',
			field : 'readdress',
			hidden : true
		}, {
			width : '100',
			title : '物流公司',
			field : 'logunit',
			hidden : true
		}, {
			width : '100',
			title : '物流单号',
			field : 'fcode',
			hidden : true
		}, {
			width : '160',
			title : '订单编码',
			field : 'billcode',
			formatter:codeLink,
			align : 'left',
            halign : 'center',
		}, {
			width : '140',
			title : '提交时间',
			field : 'submtime',
			align : 'center',
            halign : 'center',
		}, {
			width : '150',
			title : '加盟商编码',
			field : 'pcode',
			align : 'left',
            halign : 'center',
		}, {
			width : '180',
			title : '加盟商名称',
			field : 'pname',
			align : 'left',
            halign : 'center',
		}, {
			width : '100',
			title : '订单金额',
			align:'right',
            halign:'center',
			field : 'ndesummny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '预付款付款',
			align:'right',
            halign:'center',
			field : 'ndemny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '返点付款',
			align:'right',
            halign:'center',
			field : 'nderebmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {
			width : '100',
			title : '订单状态',
			field : 'vstatus',
            halign : 'center',
			align : 'center',
			formatter : function(value) {
				if (value == '0')
					return '待确认';
				if (value == '1')
					return '待发货';
				if (value == '2')
					return '已发货';
				if (value == '3')
					return '已收货';
				if (value == '4')
					return '已取消';
			}
		}, {
			width : '80',
			title : '开票状态',
			field : 'tistatus',
            halign : 'center',
			align : 'center',
			formatter : function(value) {
				if (value == '1')
					return '未开票';
				if (value == '2')
					return '已开票';
			}
		}, {
			width : '100',
			title : '确认日期',
			field : 'confdate',
			align : 'center',
            halign : 'center',
		}, {
			field : 'operate',
			title : '操作列',
			width : '160',
			halign : 'center',
			align : 'center',
			formatter : opermatter,
		}] ],
		onLoadSuccess : function(data) {
			parent.$.messager.progress('close');
			var rows = $('#grid').datagrid('getRows');
			var footerData = new Object();
			var ndesummny = 0;// 订单金额
			var ndemny = 0;// 预付款付款
			var nderebmny = 0;// 返点付款
			for (var i = 0; i < rows.length; i++) {
				if (!isEmpty(rows[i].ndesummny)) {
					ndesummny += parseFloat(rows[i].ndesummny);
				}
				if (!isEmpty(rows[i].ndemny)) {
					ndemny += parseFloat(rows[i].ndemny);
				}
				if (!isEmpty(rows[i].nderebmny)) {
					nderebmny += parseFloat(rows[i].nderebmny);
				}
			}
			footerData['pname'] = '合计';
			footerData['ndesummny'] = ndesummny;
			footerData['ndemny'] = ndemny;
			footerData['nderebmny'] = nderebmny;
			var fs = new Array(1);
			fs[0] = footerData;
			$('#grid').datagrid('reloadFooter', fs);
		},
	});
}

/**
 * 列操作格式化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function opermatter(val, row, index) {
	if(row.vstatus != null){
		var url = '<span style="margin-bottom:0px;margin-left:0px;">取消确认</span> ';;
		if(row.vstatus == 1 && row.tistatus != 2){//待发货且未开票，可以取消确认；其他状态均不可以取消确认；
			url = '<a href="#" style="margin-bottom:0px;margin-left:0px;color:blue;" onclick="cancelConf(this)">取消确认</a>';
		}
		if(row.vstatus == 1){//1：待发货；
			url += 	'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="stockOut(\''+index+'\')">出库</a>';
		}else{
			url += '<span style="margin-bottom:0px;margin-left:10px;">出库</span>';
		}
		return url;
	}
}

/**
 * 出库
 */
function stockOut(index){
	var row=$('#grid').datagrid('getRows')[index];
	$.ajax({
		type : 'POST',
		async : false,
		data:  {'corpid' : row.corpid,'billid':row.billid},  
		url : contextPath + '/dealmanage/stockout!queryOrders.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				if(!isEmpty(result.rows)){
				    $('#cardDialog').dialog({modal:true});
				    $('#cardDialog').dialog('open').dialog('center').dialog('setTitle',"出库单新增");
				    $('#stockout').form("clear");
				    $('#cardGrid').datagrid('loadData', { total : 0, rows : [] });// 清楚缓存数据
					
					$('#cardGrid').datagrid("loadData",result.rows);
					
					$('#corpname').textbox("setValue",row.pname);
					$('#corpid').textbox("setValue",row.corpid);
					$('#rename').textbox("setValue",row.rename);
					$('#phone').textbox("setValue",row.phone);
					$('#readdress').textbox("setValue",row.readdress);
					if(!isEmpty(row.recode)){
						$('#recode').textbox("setValue",row.recode);
					}
				}else{
					Public.tips({content:"该订单都已出库",type:2});
				}
			}
		}
	});
}

function addSave(){
	var rows=$('#cardGrid').datagrid('getRows');
	var flag = $('#stockout').form('validate');
	if(flag == false){
		Public.tips({content:"必输信息为空或格式不正确",type:2});
		return;
	}
	var nmny=0;
	var childBody = "";
	var rows = $("#cardGrid").datagrid('getRows');
	for (var i = 0; i < rows.length; i++) {
		childBody = childBody + JSON.stringify(rows[i]);
		nmny =getFloatValue(nmny)+getFloatValue(rows[i].nmny)
	}
	$("#nmny").numberbox('setValue',nmny);
	var postdata = new Object();
	postdata["head"] = JSON.stringify(serializeObject($('#stockout')));
	postdata["body"] = childBody;
	parent.$.messager.progress({
		text : '保存中....'
	});
	$.ajax({
		type : 'POST',
		url :	contextPath + '/dealmanage/stockout!save.action',
		data : postdata,
		dataType : 'json',
		success : function(result) {
			parent.$.messager.progress('close');
			if (result.success) {
				$('#cardDialog').dialog('close');
				Public.tips({
					content : result.msg,
					type : 0
				});
				reloadData();
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});
}

function addCancel(){
	$.messager.confirm("提示", "确定取消吗？", function(flag) {
		if (flag) {
			$('#cardDialog').dialog('close');
		} else {
			return null;
		}
	});
}

function initStockOut(){
	$('#cardGrid').datagrid({
		striped : true,
		rownumbers : true,
		idField : 'soutbid',
		height : 220,
		singleSelect : true,
		columns : [ [ 
		{
			width : '200',
			title : '订单编码',
			field : 'vcode',
			halign : 'center',
			align : 'left',
		}, {
			width : '200',
			title : '商品',
			field : 'gname',
			halign : 'center',
			align : 'left',
		}, {
			width : '150',
			title : '规格',
			field : 'invspec',
            halign : 'center',
			align : 'left',
		},{
			width : '150',
			title : '型号',
			field : 'invtype',
            halign : 'center',
			align : 'left',
		}, {
			width : '100',
			title : '购买数量',
			field : 'nnum',
            halign : 'center',
			align : 'right',
		}, {
			width : '100',
			title : '出库数量',
			field : 'nnum',
            halign : 'center',
			align : 'right',
		},{
			width : '100',
			title : '销售价',
			field : 'nprice',
			hidden : true
		},{
			width : '100',
			title : '总金额',
			field : 'nmny',
			hidden : true
		},{
			width : '100',
			title : '商品主键',
			field : 'gid',
			hidden : true
		},{
			width : '100',
			title : '规格主键',
			field : 'specid',
			hidden : true
		},{
			width : '100',
			title : '订单主键',
			field : 'billid_b',
			hidden : true
		}, 
		] ],
	});
}

/**
 * 取消确认
 */
function cancelConf(ths){
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	var row = $('#grid').datagrid('getData').rows[tindex];
	if (row.vstatus != 1) {
		Public.tips({
			content : '该记录不是待发货状态，不允许取消确认',
			type : 2
		});
		return;
	}
	
	$.messager.confirm("提示", "你确定取消确认吗？", function(flag) {
		if (flag) {
			var postdata = new Object();
			var data = JSON.stringify(row);
			postdata["data"] = data;
			postdata["type"] = 3;
			operdata(postdata, 3);
		} else {
			return null;
		}
	});
}

/**
 * 参照初始化
 */
function initRef(){
    $('#qcpname').textbox({
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
    				buttons : [ {
    					text : '确认',
    					handler : function() {
    						selectCorps();
    					}
    				}, {
    					text : '取消',
    					handler : function() {
    						$("#chnDlg").dialog('close');
    					}
    				} ]
                });
            }
        }]
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
		
		$("#qcpname").textbox("setValue",str);
		$("#qcpid").val(corpIds);
	}
	 $("#chnDlg").dialog('close');
}

/**
 * 查询数据
 */
function reloadData(){
	var bdate = null;//提单开始日期
	var edate = null;//提单结束日期
	var bperiod = null;//扣款开始日期
	var eperiod = null;//扣款结束日期
	var querydate;
	var ischeck = $('#tddate').is(':checked');
	if(ischeck){
		bdate = $('#bdate').datebox('getValue'); 
		edate = $('#edate').datebox('getValue'); 
		if(isEmpty(bdate)){
			Public.tips({
				content : '提交日期开始日期不能为空',
				type : 2
			});
			return;
		}
		if(isEmpty(edate)){
			Public.tips({
				content : '提交日期结束日期不能为空',
				type : 2
			});
			return;
		}
		if(!isEmpty(bdate) && !isEmpty(edate)){
			if(!checkdate1("bdate","edate")){
				return;
			}		
		}
		querydate = bdate + ' 至 ' + edate;
	}else{
		bperiod = $('#bperiod').datebox('getValue');
		eperiod = $('#eperiod').datebox('getValue');
		if(isEmpty(bperiod)){
			Public.tips({
				content : '确认日期开始日期不能为空',
				type : 2
			});
			return;
		}
		if(isEmpty(eperiod)){
			Public.tips({
				content : '确认日期结束日期不能为空',
				type : 2
			});
			return;
		}
		if(!isEmpty(bperiod) && !isEmpty(eperiod)){
			if(!checkdate1("bperiod","eperiod")){
				return;
			}		
		}
		querydate = bperiod + ' 至 ' + eperiod;
	}
	
	var qstatus = $('#qstatus').combobox('getValues');
	var vstatus = "";
	for( i= 0; i < qstatus.length; i++){
		vstatus+=","+qstatus[i];
	}
	vstatus = vstatus.substring(1);
	
	var url = DZF.contextPath + '/dealmanage/channelorder!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'billcode' : $("#qbcode").val(),
		'corpid' : $("#qcpid").val(),
		'tistatus' :  $('#tistatus').combobox('getValue'),
		'logunit' : vstatus,
		'bdate' : bdate,
		'edate' : edate,
		'bbdate' : bperiod,
		'eedate' : eperiod,
	});
	$('#grid').datagrid('clearSelections');
	$('#qrydialog').hide();
	
	$('#querydate').html(querydate);
}

/**
 * 标签查询-待确认
 */
function qryData(){
	var url = DZF.contextPath + '/dealmanage/channelorder!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'vstatus' : 0,
	});
	$('#grid').datagrid('clearSelections');
}

/**
 * 清除查询条件
 */
function clearParams(){
	$("#qbcode").textbox('setValue',null);
	$("#qcpname").textbox('setValue',null);
	$("#qcpid").val(null);
	$('#qstatus').combobox('clear');
}

/**
 * 取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 确认
 */
function confirm(){
	var row = null;
	var rows = $("#grid").datagrid("getChecked");
	if (rows == null || rows.length != 1) {
		Public.tips({
			content : '请选择一行数据',
			type : 2
		});
		return;
	} else {
		row = rows[0];
	}
	if (row.vstatus != 0) {
		Public.tips({
			content : '该数据状态不为待确认',
			type : 2
		});
		return;
	}
	
	$.messager.confirm("提示", "确认后将扣除加盟商账户余额，是否确认订单？", function(flag) {
		if (flag) {
//			var index =  $('#grid').datagrid('getRowIndex', row);
			var postdata = new Object();
			var data = JSON.stringify(row);
			postdata["data"] = data;
			postdata["type"] = 1;
			operdata(postdata, 1);
		} else {
			return null;
		}
	});
}

/**
 * 发票申请
 */
function onBilling(){
	$.messager.confirm("提示", "你确定开票吗？", function(flag) {
		if (flag) {
			var rows = $("#grid").datagrid("getChecked");
			if (rows == null || rows.length == 0) {
				Public.tips({
					content : '请选择需要处理的数据',
					type : 2
				});
				return;
			} 
			
			var data = "";
			for(var i = 0; i<rows.length; i++ ){
				if (rows[i].vstatus == 0) {
					Public.tips({
						content : '订单编码'+rows[i].billcode+'状态为待确认',
						type : 2
					});
					return;
				}else{
					if(rows[i].tistatus == 2){
						Public.tips({
							content : '订单编码'+rows[i].billcode+'状态为待确认',
							type : 2
						});
						return;
					}else{
						rows[i].reason = reason;
						data = data + JSON.stringify(rows[i]);
					}
				}
			}
		
			var postdata = new Object();
			postdata["data"] = data;
			postdata["type"] = 4;
			operdata(postdata, 4);
			
		} else {
			return null;
		}
	});
}

/**
 * 取消订单
 */
function cancOrder(){
	var row = null;
	var rows = $("#grid").datagrid("getChecked");
	if (rows == null || rows.length == 0) {
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});
		return;
	} 
	var errmsg = "";
	for(var i = 0; i<rows.length; i++ ){
		if (rows[i].vstatus != 0) {
			errmsg = errmsg + '订单编码'+rows[i].billcode+'状态不为待确认；<br>';
		}
	}
	if(!isEmpty(errmsg)){
		Public.tips({
			content : errmsg,
			type : 2
		});
		return;
	}
	$('#cancelDlg').dialog('open').dialog('center').dialog('setTitle', '选择取消原因');
	$('#cancfrom').form('clear');
	$("#reason").textbox({'readonly':true});
	$("#reason").textbox({'required':false});
	$("#reason").textbox({'prompt':null});
	initListen();
	$('#reason1').prop('checked',true);
}

/**
 * 取消原因监听
 */
function initListen(){
	$(":radio").click( function(){
		var reatype = $('input:radio[name="reatype"]:checked').val();
		if(reatype == 3){
			$("#reason").textbox({'readonly':false});
			$("#reason").textbox({'required':true});
			$("#reason").textbox({'prompt':'请输入取消订单原因'});
		}else{
			$("#reason").textbox({'readonly':true});
			$("#reason").textbox({'required':false});
			$("#reason").textbox({'prompt':null});
		}
	});
}

/**
 * 取消订单-保存
 */
function cancSave(){
	if ($("#cancfrom").form('validate')) {
		var row = null;
		var rows = $("#grid").datagrid("getChecked");
		if (rows == null || rows.length == 0) {
			Public.tips({
				content : '请选择需要处理的数据',
				type : 2
			});
			return;
		} 
		
		var reason = "";
		var reatype = $('input:radio[name="reatype"]:checked').val();
		if(reatype == 1){
			reason = "加盟商账户预付款余额不足";
		}else if(reatype == 2){
			reason = "商品缺货";
		}else if(reatype == 3){
			reason = $('#reason').val();
		}
		
		var data = "";
		for(var i = 0; i<rows.length; i++ ){
			if (rows[i].vstatus != 0) {
				Public.tips({
					content : '订单编码'+rows[i].billcode+'状态不为待确认',
					type : 2
				});
				return;
			}else{
				rows[i].reason = reason;
				data = data + JSON.stringify(rows[i]);
			}
		}

		var postdata = new Object();
		postdata["data"] = data;
		postdata["type"] = 2;
		operdata(postdata, 2);
	} else {
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return; 
	}
}

/**
 * 取消订单-取消
 */
function cancCancel(){
	$('#cancelDlg').dialog('close');
}

/**
 * 操作数据
 * @param row
 * @param type
 */
function operdata(postdata, type){
	$.messager.progress({
		text : '数据操作中....'
	});
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/dealmanage/channelorder!operData.action',
		data : postdata,
		traditional : true,
		async : false,
		success : function(data, textStatus) {
			$.messager.progress('close');
			if (!data.success) {
				Public.tips({
					content : data.msg,
					type : 1
				});
			} else {
//				$('#grid').datagrid('clearSelections');
//				$('#grid').datagrid('updateRow', {
//					index : index,
//					row : data.rows
//				});
				reloadData();
				if(type == 2){
					$('#cancelDlg').dialog('close');
				}
				if(data.status == -1){
					Public.tips({
						content : data.msg,
						type : 2
					});
				}else{
					Public.tips({
						content : data.msg,
					});
				}
			}
		},
	});
}


/**
 * 订单编码格式化
 * @param value
 * @param row
 * @param index
 */
function codeLink(value,row,index){
	if(!isEmpty(row.billid)){
		return '<a href="javascript:void(0)" style="color:blue"  onclick="showInfo(' + index + ')">'+value+'</a>';
	}
}

/**
 * 查看订单明细
 * @param index
 */
function showInfo(index){
	var qrow = $('#grid').datagrid('getData').rows[index];
	$('#infoDlg').dialog('open').dialog('center').dialog('setTitle', '订单详情');
	$("#billcode").html(null);
	$("#pname").html(null);
	$("#receinfo").html(null);
	$("#ndesummny").html(null);
	$("#nderebmny").html(null);
	
	$.ajax({
        type: "post",
        dataType: "json",
        url: contextPath + '/dealmanage/channelorder!qryOrderDet.action',
        data: {
        	"billcode" : qrow.billcode,
        	"corpid" : qrow.corpid,
        	"billid" : qrow.billid,
        },
        traditional: true,
        async: false,
        success: function(data, textStatus) {
            if (!data.success) {
            	Public.tips({content:data.msg,type:1});
            } else {
                var row = data.rows;
                $("#billcode").html(row.billcode);
            	$("#pname").html(row.pname);
            	if(!isEmpty(row.recode)){
            		$("#receinfo").html(row.rename+"，"+row.phone+"，"+row.readdress+"，"+row.recode);
            	}else{
            		$("#receinfo").html(row.rename+"，"+row.phone+"，"+row.readdress);
            	}
            	$("#ndesummny").html(row.ndesummny);
            	$("#nderebmny").html(row.nderebmny);
                
            	//1、订单流程详情：
            	$("#detail").empty();
                if(row.detail != null && row.detail.length > 0){
                	var detail = row.detail;
                	var showinfo = "";
            		showinfo = showinfo + "<div class='flow'>";
            		if(row.vstatus != 4){//非取消订单
            			for(var i = 0; i < detail.length; i++){
            				if(i == 0){
            					showinfo = showinfo + "<div class='flow_one'>";
            					showinfo = showinfo + "	<div>";
            					showinfo = showinfo + "		<img src='../../images/shop_cj.png'>";
            					showinfo = showinfo + "	</div>";
            					showinfo = showinfo + "	<div>"+detail[i].describe+"</div>";
            					showinfo = showinfo + "	<div>"+detail[i].optime+"</div>";
            					showinfo = showinfo + "</div>";
            				}else{
            					showinfo = showinfo + "<div class='flow_main'>";
            					showinfo = showinfo + "	<div>";
            					if(i == 1){
            						showinfo = showinfo + "<img src='../../images/shop_qr.png'>";
            					}else if(i == 2){
            						showinfo = showinfo + "<img src='../../images/shop_fh.png'>";
            					}else if(i == 3){
            						showinfo = showinfo + "<img src='../../images/shop_sh.png'>";
            					}
            					showinfo = showinfo + "	</div>";
            					showinfo = showinfo + "	<div>"+detail[i].describe+"</div>";
            					showinfo = showinfo + "	<div>"+detail[i].optime+"</div>";
            					showinfo = showinfo + " <div>";
            					showinfo = showinfo + "		<img class='flow_mian_img' src='../../images/shop_xq.png'>";
            					showinfo = showinfo + "	</div>";
            					showinfo = showinfo + "</div>";
            				}
            			}
            		}else if(row.vstatus == 4){//取消订单
            			for(var i = 0; i < detail.length; i++){
            				if(i == 0){
            					showinfo = showinfo + "<div class='flow_one'>";
            					showinfo = showinfo + "	<div>";
            					showinfo = showinfo + "		<img src='../../images/shop_cj.png'>";
            					showinfo = showinfo + "	</div>";
            					showinfo = showinfo + "	<div>"+detail[i].describe+"</div>";
            					showinfo = showinfo + "	<div>"+detail[i].optime+"</div>";
            					showinfo = showinfo + "</div>";
            				}else{
            					showinfo = showinfo + "<div class='flow_main'>";
            					showinfo = showinfo + "	<div>";
            					showinfo = showinfo + "		<img src='../../images/shop_qx.png'>";
            					showinfo = showinfo + "	</div>";
            					showinfo = showinfo + "	<div>"+detail[i].describe+"</div>";
            					showinfo = showinfo + "	<div>"+detail[i].optime+"</div>";
            					showinfo = showinfo + "	<div>"+detail[i].note+"</div>";
            					showinfo = showinfo + " <div>";
            					showinfo = showinfo + "		<img class='flow_mian_img' src='../../images/shop_xq.png'>";
            					showinfo = showinfo + "	</div>";
            					showinfo = showinfo + "</div>";
            				}
            			}
            		}
            		showinfo = showinfo + "<div class='flow'>";
            		$("#detail").append(showinfo);
                }
                
                //2、快递信息
                $("#fast").empty();
                if(row.vstatus == 2 || row.vstatus == 3){
                	var showinfo = "";
                	showinfo = showinfo + "<div class='icon'>";
                	showinfo = showinfo + "	<div class='shop_pick'>快递信息</div>";
                	showinfo = showinfo + "</div>";
                	showinfo = showinfo + "<div style='margin-left: 40px;'>";
                	showinfo = showinfo + "	<div class='order' title='"+row.logunit+"'>";
                	showinfo = showinfo + "		<label>物流公司：</label> <span>"+row.logunit+"</span>";
                	showinfo = showinfo + " </div>";
                	showinfo = showinfo + " <div class='order'>";
                	showinfo = showinfo + "	<label>快递单号：</label> <span>"+row.fcode+"</span>";
                	showinfo = showinfo + "	</div>";
                	showinfo = showinfo + "</div>";
                	$("#fast").append(showinfo);
                }
                
                //3、商品信息
                $("#goods").empty();
                if(row.goods != null && row.goods.length > 0){
                	var goods = row.goods;
                	var showinfo = "";
                	for(var i = 0; i < goods.length; i++){
                		var url = getImgUrl(goods[i]);
                		showinfo = showinfo + "<div class='pick_main'>";
                		showinfo = showinfo + "	<div class='pick_left'>";
                		showinfo = showinfo + "		<img src='"+ url +"'/>";
                		showinfo = showinfo + "	</div>";
                		showinfo = showinfo + "	<div class='pick_right'>";
                	/*	showinfo = showinfo + "		<div>"+goods[i].gname+"</div>";*/
                		showinfo = showinfo + "		<div class='pick_cost'>";
                		showinfo = showinfo + "			<div class='pick_cost_A'>";
                		showinfo = showinfo + "				<div>"+goods[i].gname+"</div>";
                		showinfo = showinfo + "			</div>";
                		showinfo = showinfo + "			<div class='pick_cost_B'>"+(goods[i].spec ? goods[i].spec : '') + (goods[i].type ? goods[i].type : '') +"</div>";
                		showinfo = showinfo + "			<div class='pick_cost_C'>"+"x&nbsp;"+goods[i].amount+"</div>";
                		showinfo = showinfo + "			<div class='pick_cost_D'>¥"+goods[i].price+"</div>";
                		showinfo = showinfo + "		</div>";
                		showinfo = showinfo + "	</div>";
                		showinfo = showinfo + "</div>";
                	}
                	$("#goods").append(showinfo);
                }
            }
        },
    });
	
}

/**
 * 获取图片信息
 * @param attach
 * @returns {String}
 */
function getImgUrl(row) {
	var ext = getFileExt(row['fpath']);
	if ("pdf" == ext.toLowerCase()) {
		return "../../images/typeicon/pdf.jpg";
	} else if ("txt" == ext.toLowerCase()) {
		return "../../images/typeicon/txt.jpg";
	}
	return DZF.contextPath
			+ '/dealmanage/goodsmanage!getAttachImage.action?&doc_id='
			+ row.doc_id;
}

/**
 * 获取附件扩展
 * @param filename
 * @returns
 */
function getFileExt(filename){
	var index1 = filename.lastIndexOf(".")+1;
	var index2 = filename.length;
	var ext = filename.substring(index1,index2);
	return ext;
}
