var contextPath = DZF.contextPath;
var grid;
//var loadrows = null;
//var isenter = false;//是否快速查询

$(function() {
	load();
	fastQry();
	initListener();
	loadJumpData();
});

/**
 * 由别的界面（付款单余额明细）跳转待合同审核界面
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
			width : '140',
			title : '加盟商',
			halign:'center',
			field : 'corpnm',
		}, {
			width : '150',
			title : '付款时间',
			field : 'dpdate',
		}, {
			width : '160',
			title : '单据号',
			field : 'vcode',
		}, {
			width : '140',
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
			width : '140',
			title : '付款金额',
			align:'right',
            halign:'center',
			field : 'npmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		}, {
			width : '140',
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
			width : '100',
			title : '附件',
			field : 'fj',
			align : 'center',
			formatter : function(value, row, index) {
				if(!isEmpty(row.billid)){
					return '<a href="#" style="color:blue" onclick="showImage(\''+row.billid+'\')" >' + "附件"+ '</a>';
				}
			}
		},{
			width : '140',
			title : '单据状态',
            halign:'center',
			field : 'status',
			formatter : function(value) {
				if (value == '1')
					return '待提交';
				if (value == '2')
					return '待确认';
				if (value == '3')
					return '已确认';
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
			field : 'billid',
			title : '主键',
			hidden : true
		}, ] ],
		onLoadSuccess : function(data) {
            parent.$.messager.progress('close');
//            if(!isenter){
//				loadrows = data.rows;
//			}
//			isenter = false;
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
 * @param type  -1：全部；2：待确认；3：已确认；
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
/**
 * 快速过滤
 */
function fastQry(){
	$('#filter_value').textbox('textbox').keydown(function (e) {
		 if (e.keyCode == 13) {
            var filtername = $("#filter_value").val(); 
            if(filtername != ""){
            	var queryParams = $('#grid').datagrid('options').queryParams;
            	$('#grid').datagrid('options').url =contextPath + '/chnpay/chnpayconf!query.action';
            	var rows = $('#grid').datagrid('getRows');
            	if(rows != null && rows.length > 0){
            		queryParams.qtype = $('#status').combobox('getValue');
            		queryParams.iptype = $('#iptype').combobox('getValue');
            		queryParams.ipmode = $('#ipmode').combobox('getValue');
            		queryParams.cpid = $("#pk_account").val();
            		queryParams.id = null;
            	}
            	queryParams.begdate = $('#bdate').datebox('getValue');
            	queryParams.enddate = $('#edate').datebox('getValue');
            	queryParams.cpname = filtername;
            	$('#grid').datagrid('options').queryParams = queryParams;
            	$('#grid').datagrid('reload');
            }
//            if (filtername != "") {
//           	 var jsonStrArr = [];
//           	 if(loadrows){
//           		 for(var i=0;i<loadrows.length;i++){
//           			 var row = loadrows[i];
//           			 if(row != null && !isEmpty(row["corpnm"])){
//           				 if(row["corpnm"].indexOf(filtername) >= 0){
//           					 jsonStrArr.push(row);
//           				 } 
//           			 }
//           		 }
//           		 isenter = true;
//           		 $('#grid').datagrid('loadData',jsonStrArr);  
//           	 }
//            }else{
//           	 $('#grid').datagrid('loadData',loadrows);
//            } 
         }
   });
}

/**
 * 操作
 * @param type 2：取消收款；3：收款确认；
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
	var msg = "";
	if(type == 3){
		msg = "收款确认";
	}else if(type == 2){
		msg = "取消确认";
	}
	$.messager.confirm("提示", "你确定要"+msg+"吗?", function(r) {
		if (r) {
			var postdata = new Object();
			postdata["data"] = data;
			postdata["type"] = type;
			operatData(postdata,rows);
		}
	});
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
 * 显示提示
 * @param i
 */
function showTips(i){
	var div = "#reUpload"+i;
	$(div).css("display","block");
	var tips = "单击下载";
	var tipkey = "#tips"+ i;
	$(tipkey).html(tips);	
}

/**
 * 隐藏提示
 * @param i
 */
function hideTips(i){
	var div = "#reUpload"+i;
	$(div).css("display","none");	
}

/**
 * 查看原图
 * @param i
 */
function showImage(billid){
//	var ext = getFileExt(arrachrow['doc_name']);
	var src = DZF.contextPath + "/chnpay/chnpayconf!getAttachImage.action?billid=" + billid +"&time=" +Math.random();
//	if("png"==ext.toLowerCase()||"jpg"==ext.toLowerCase()
//			||"jpeg"==ext.toLowerCase()||"bmp"==ext.toLowerCase()){
//	}
	$("#tpfd").empty();
//	var offset = $("#tpght").offset();
	$("#tpfd").dialog({
		title: '附件浏览' ,
		width:$(window).width()-100,
		height:$(window).height()-100,
		left: 10,
		top: 10,
		cache: false,
		resizable: true,
		center : true,
		align:"center",
		content : '<div style="overflow:scroll;height:100%;" >'+
		'<a href="javascript:void(0)"   onmouseover="showTips(' + 0 + ')" onmouseout="hideTips(' + 0 + ')"  > ' +
			'<span><img alt="无法显示图片" src="' + src + '" style="height: " + $(window).height()-10 + ";width: " + $(window).width()-10 +" ">' + 
				  '<div id="reUpload' + 0 +'" style="width: 100%; height:25px; position:absolute; top:30%; left:30%; display:none;" >' + 
				  		'<h4><span id="tips'+0+'"></span></h4> '+
				  '</div>  </span>' +
       '</a> </div>',
		onLoad:function(){}
	});
}

/**
 * 附件下载
 * @param billid
 */
function downFile(billid){
	Business.getFile(DZF.contextPath + '/chnpay/chnpayconf!downFile.action', {billid : billid}, true, true);
}

