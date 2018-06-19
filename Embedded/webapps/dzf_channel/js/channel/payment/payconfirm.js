var contextPath = DZF.contextPath;
var grid;

$(function() {
	load();
//	fastQry();
	initListener();
	loadJumpData();
});

/**
 * 由别的界面（付款单余额明细）跳转待合同确认界面
 */
function loadJumpData(){
	var obj = Public.getRequest();
	var operate = obj.operate;
	if(operate == "topayc"){
		var id = obj.pk_billid;
		
		$('#grid').datagrid('unselectAll');
		var queryParams = $('#grid').datagrid('options').queryParams;
		$('#grid').datagrid('options').url =contextPath + '/chnpay/chnpayconf!query.action';
		queryParams.qtype = $('#status').combobox('getValue');
		queryParams.begdate = null;
		queryParams.enddate = null;
		queryParams.iptype = -1;
		queryParams.ipmode = -1;
		queryParams.cpid = null;
		queryParams.id = id;
		
		$('#grid').datagrid('options').queryParams = queryParams;
		$('#grid').datagrid('reload');
	}
}

/**
 * 监听事件
 */
function initListener(){
	$("#querydate").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	$('#querydate').html(parent.SYSTEM.PreDate + ' 至 ' + parent.SYSTEM.LoginDate);
	$("#bdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#edate").datebox("setValue", parent.SYSTEM.LoginDate);
	initChannel();
}

/**
 * 关闭查询框
 */
function closeCx(){
	$("#qrydialog").hide();
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
                    buttons: '#chnBtn'
                });
            }
        }]
    });
}

/**
 * 双击选择公司
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
	 $("#chnDlg").dialog('close');
}

/**
 * 选择公司
 */
function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}

/**
 * 清除查询框
 */
function clearParams(){
	$("#pk_account").val(null);
	$("#channel_select").textbox("setValue",null);
	$('#status').combobox('setValue', '-1');
	$('#iptype').combobox('setValue', '-1');
	$('#ipmode').combobox('setValue', '-1');
}

/**
 * 查询
 */
function reloadData(){
	var bdate = $('#bdate').datebox('getValue'); //开始日期
	var edate = $('#edate').datebox('getValue'); //结束日期
	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').url =contextPath + '/chnpay/chnpayconf!query.action';
	queryParams.qtype = $('#status').combobox('getValue');
	queryParams.begdate = bdate;
	queryParams.enddate = edate;
	queryParams.iptype = $('#iptype').combobox('getValue');
	queryParams.ipmode = $('#ipmode').combobox('getValue');
	queryParams.cpid = $("#pk_account").val();
	queryParams.id = null;
	queryParams.cpname = null;
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
	$('#querydate').html(bdate + ' 至 ' + edate);
    $('#qrydialog').hide();
    $('#grid').datagrid('unselectAll');
}

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
		singleSelect : false,
		pagination : true,// 分页工具栏显示
		showFooter:true,
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		showFooter:true,
		columns : [ [ {
			field : 'ck',
			checkbox : true
		}, {
			width : '240',
			title : '加盟商',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '80',
			title : '付款时间',
			field : 'dpdate',
		}, {
			width : '160',
			title : '单据号',
			field : 'vcode',
		}, {
			width : '70',
			title : '付款类型',
            halign:'center',
			field : 'iptype',
			formatter : function(value) {
				if (value == '1')
					return '保证金';
				if (value == '2')
					return '预付款';
			}
		}, {
			width : '100',
			title : '付款金额',
			align:'right',
            halign:'center',
			field : 'npmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '80',
			title : '支付方式',
            halign:'center',
			field : 'ipmode',
			formatter : function(value) {
				if (value == '1')
					return '银行转账';
				if (value == '2')
					return '支付宝';
				if (value == '3')
					return '微信';
				if (value == '4')
					return '其他';
			}
		}, {
			width : '160',
			title : '付款银行',
			field : 'vbname',
		}, {
			width : '160',
			title : '付款账号',
			field : 'vbcode',
		}, {
			width : '40',
			title : '附件',
			field : 'fj',
			align : 'center',
			formatter : function(value, row, index) {
				if(!isEmpty(row.billid)){
					return '<a href="javascript:void(0)"  style="color:blue" onclick="showImage(\''+row.billid+'\')" >' + "附件"+ '</a>';
				}
			}
		}, {
			width : '70',
			title : '单据状态',
            halign:'center',
			field : 'status',
			formatter : function(value) {
				if (value == '1')
					return '待提交';
				if (value == '2')
					return '待审批';
				if (value == '3')
					return '已确认';
				if (value == '4')
					return '已驳回';
				if (value == '5')
					return '待确认';
			}
		}, {
			width : '140',
			title : '备注',
            halign:'center',
			field : 'memo',
			formatter : function(value) {
				if(value!=undefined){
					return "<span title='" + value + "'>" + value + "</span>";
				}
			}
		}, {
			width : '140',
			title : '收款确认时间',
            halign:'center',
			field : 'dctime',
		}, {
			width : '140',
			title : '驳回说明',
            halign:'center',
			field : 'vreason',
			formatter : function(value) {
				if(value!=undefined){
					return "<span title='" + value + "'>" + value + "</span>";
				}
			}
		}, {
			field : 'billid',
			title : '主键',
			hidden : true
		}, ] ],
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
    var npmny = 0;	
    for (var i = 0; i < rows.length; i++) {
    	npmny += parseFloat(rows[i].npmny);
    }
    footerData['npmny'] = npmny;
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}

/**
 * 标签查询
 * @param type  -1：全部；5：待确认；3：已确认；
 */
function qryData(type){
	$('#grid').datagrid('unselectAll');
	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').url =contextPath + '/chnpay/chnpayconf!query.action';
	queryParams.qtype = type;
	queryParams.begdate = '';
	queryParams.enddate = '';
	queryParams.iptype = -1;
	queryParams.ipmode = -1;
	queryParams.cpid = "";
	queryParams.id = null;
	queryParams.cpname = null;
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
}

///**
// * 快速过滤
// */
//function fastQry(){
//	$('#filter_value').textbox('textbox').keydown(function (e) {
//		 if (e.keyCode == 13) {
//            var filtername = $("#filter_value").val(); 
//            if(filtername != ""){
//            	var queryParams = $('#grid').datagrid('options').queryParams;
//            	$('#grid').datagrid('options').url =contextPath + '/chnpay/chnpayconf!query.action';
//            	var rows = $('#grid').datagrid('getRows');
//            	if(rows != null && rows.length > 0){
//            		queryParams.qtype = $('#status').combobox('getValue');
//            		queryParams.iptype = $('#iptype').combobox('getValue');
//            		queryParams.ipmode = $('#ipmode').combobox('getValue');
//            		queryParams.cpid = $("#pk_account").val();
//            		queryParams.id = null;
//            	}
//            	queryParams.begdate = $('#bdate').datebox('getValue');
//            	queryParams.enddate = $('#edate').datebox('getValue');
//            	queryParams.cpname = filtername;
//            	$('#grid').datagrid('options').queryParams = queryParams;
//            	$('#grid').datagrid('reload');
//            }
//         }
//   });
//}

/**
 * 取消确认 
 * @param type 5：取消确认；
 */
function operate(type){
	var rows = $("#grid").datagrid("getChecked");
	if(rows == null || rows.length == 0){
		Public.tips({content:'请选择需要处理的数据',type:2});
        return;
	}
	var data = '';
	if (rows != null && rows.length > 0) {
		for (var i = 0; i < rows.length; i++) {
			data = data + JSON.stringify(rows[i]);
		}
	}
	$.messager.confirm("提示", "你确定要取消确认吗?", function(r) {
		if (r) {
			var postdata = new Object();
			postdata["data"] = data;
			postdata["type"] = type;
			operatData(postdata,rows);
		}
	});
}

/**
 * 收款确认(跳转页面按钮)
 */
function certain(){
	var rows = $("#grid").datagrid("getChecked");
	if(rows == null || rows.length == 0){
		Public.tips({content:'请选择需要处理的数据',type:2});
        return;
	}
	$('#hlDialog').dialog({ modal:true });//设置dig属性
    $('#hlDialog').dialog('open').dialog('center').dialog('setTitle','收款确认');
    $("#confirm").prop("checked",true);
    $("#vreason").textbox('readonly',true);
    $('input:radio[name="seletype"]').change( function(){  
		var ischeck = $('#confirm').is(':checked');
		if(ischeck){
			$("#vreason").textbox('setValue',"");
			$("#vreason").textbox('readonly',true);
		}else{
			$("#vreason").textbox('readonly',false);
		}
	});
}

/**
 * 收款确认(确认按钮)
 */
function confirm(){
	var ischeck = $('#reject').is(':checked');
	if(ischeck && isEmpty($('#vreason').textbox('getValue'))){
		Public.tips({content:'请填写驳回说明',type:2});
		return;
	}
	var formValid = $("#commit").form('validate');
	if(!formValid){
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return;
	}
	var rows = $("#grid").datagrid("getChecked");
	var data = '';
	if (rows != null && rows.length > 0) {
		for (var i = 0; i < rows.length; i++) {
			data = data + JSON.stringify(rows[i]);
		}
	}
	var postdata = new Object();
	postdata["data"] = data;
	if($('#reject').is(':checked')){
		postdata["type"] = 4;
		postdata["vreason"] = $('#vreason').textbox('getValue');
	}else{
		postdata["type"] = 3;
	}
	operatData(postdata,rows);
	$("#commit").form('clear');
	$('#hlDialog').dialog('close');
}

/**
 * 收款确认(取消按钮)
 */
function canConfirm(){
	$("#commit").form('clear');
	$('#hlDialog').dialog('close');
}

/**
 * 操作数据
 */
function operatData(postdata, rows){
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/chnpay/chnpayconf!operate.action',
		data : postdata,
		traditional : true,
		async : false,
		success : function(result) {
			if (!result.success) {
				if("数据不能为空" == result.msg){
					Public.tips({
						content : result.msg,
						type : 2
					});
				}
			} else {
				if(result.status == -1){
					Public.tips({
						content : result.msg,
						type : 2
					});
				}else{
					Public.tips({
						content : result.msg,
					});
				}
				var rerows = result.rows;
				if(rerows != null && rerows.length > 0){
					var map = new HashMap(); 
					for(var i = 0; i < rerows.length; i++){
						map.put(rerows[i].billid,rerows[i]);
					}
					var index;
					var indexes = new Array();
					for(var i = 0; i < rows.length; i++){
						if(map.containsKey(rows[i].billid)){
							index = $('#grid').datagrid('getRowIndex', rows[i]);
							indexes.push(index);
						}
					}
					for(var i in indexes){
						$('#grid').datagrid('updateRow', {
							index : indexes[i],
							row : result.rows[i]
						});
					}
				}
				$("#grid").datagrid('uncheckAll');
			}
		},
	});
}

/**
 * 显示大图
 * @param billid
 */
function showImage(billid){
	var src = DZF.contextPath + "/chnpay/chnpayconf!getAttachImage.action?billid=" + billid +"&time=" +Math.random();
	$("#tpfd").empty();
//	parent.openFullViewDlg('<div style="overflow:scroll;height:80%"  >'
//			+'<a  onclick="downFile(\'' +billid + '\',1)"><img id="conturnid" alt="无法显示图片"  onmouseover="showTips()" onmouseout="hideTips()"  src="' + src 
//			+ '" style="height: " + $(window).height()-10 + ";width: " + $(window).width()-10 +" ">'
//			+'<div id="reUpload" style="width: 100%; height:25px; position:absolute; top:30%; left:30%; display:none;" >' + 
//	  		+'</a></div>','原图');
	var img = '<img id="conturnid" alt="无法显示图片" onclick="downFile(\'' +billid + '\',1)" '
	+ ' onmouseover="showTips()" onmouseout="hideTips()"'
	+ 'src="' + src + '" style="height: " + $(window).height()-10 + ";width: " + $(window).width()-10 +" ">'
	+'<div id="reUpload" style="width: 100%; height:25px; position:absolute; top:30%; left:30%; display:none;" />';
	parent.openFullViewDlg(img, '原图', billid, 1);
}

/**
 * 设置快捷键
 */
$(document).keydown(function(e) {
	//ESC 关闭附件预览框
	if (e.keyCode == 27) {
		parent.closeFullViewDlg();
	}
});


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
	var qj = $('#bdate').datebox('getValue') + '至' + $('#edate').datebox('getValue');
	Business.getFile(DZF.contextPath+ '/chnpay/chnpayconf!exportExcel.action',
			{'strlist':JSON.stringify(datarows), 'qj':qj,}, true, true);
}

