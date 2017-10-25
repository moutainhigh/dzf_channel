var contextPath = DZF.contextPath;
var grid;
var loadrows = null;
var isenter = false;//是否快速查询

$(function() {
	load();
	fastQry();
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
		singleSelect : false,
		pagination : true,// 分页工具栏显示
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
					return '加盟费';
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
		},
		{
			width : '100',
			title : '附件',
			field : 'fj',
			align : 'center',
			formatter : function(value, row, index) {
				return '<a href="#" style="color:blue"  onclick="viewAttachCard(\''+row.billid+'\')">' + "附件"+ '</a>';
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
            if(!isenter){
				loadrows = data.rows;
			}
			isenter = false;
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

/**
 * 标签查询
 * @param type  1：全部；2：加盟费；3：预付款；
 */
function qryData(type){
	$('#grid').datagrid('unselectAll');
	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').url =contextPath + '/chnpay/chnpayconf!query.action';
	queryParams.qtype = type;
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
            if (filtername != "") {
           	 var jsonStrArr = [];
           	 if(loadrows){
           		 for(var i=0;i<loadrows.length;i++){
           			 var row = loadrows[i];
           			 if(row != null && !isEmpty(row["corpnm"])){
           				 if(row["corpnm"].indexOf(filtername) >= 0){
           					 jsonStrArr.push(row);
           				 } 
           			 }
           		 }
           		 isenter = true;
           		 $('#grid').datagrid('loadData',jsonStrArr);  
           	 }
            }else{
           	 $('#grid').datagrid('loadData',loadrows);
            } 
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
	var postdata = new Object();
	postdata["data"] = data;
	postdata["type"] = type;
	operatData(postdata,rows);
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
 * 详细界面查看附件
 */
function viewAttachCard(billid){
	var para = {};
	para.billid = billid;
	$.ajax({
		type : "POST",
		url : DZF.contextPath + "/chnpay/chnpayconf!queryByID.action",
  		dataType : 'json',
  		data : para,
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var row= result.rows;
			arrachrow = result.rows;
			$("#attachs").html('');
			if(row.fpath){
				var srcpath = row.fpath.replace(/\\/g, "/");
				var attachImgUrl = getAttachImgUrl(row);
				$('<li><a href="javascript:void(0)" onmouseover="showTips(' + 0 + ')"  onmouseout="hideTips(' + 0 + ')" '+
				' ondblclick="doubleImage(\'' + 0 + '\');" > '+
				' <span><img src="' +  attachImgUrl +  '" />' + 
				'<div id="reUpload' + 0 +'" style="width: 100%; height: 25px; position: absolute; top: 105px; left: 0px; display:none;">' + 
				'<h4><span id="tips'+0+'"></span></h4></div></span>' +
				'<input type="checkbox" id="delimg' + 0 + '" name="checkbox" style="position:absolute;top:1%;left:1%;width:20px;height:20px"/>' +
				'<font>' + row.doc_name + '</font></a></li>').appendTo($("#attachs"));
			}
		}
	});
	$("#attachViewDlg").dialog({
		width:$(window).width()-100,
		height:$(window).height()-100,
		closable:true,
		title:'附件浏览',
		modal:true
	});	
	$("#attachViewDlg").css("display","block");
	$("#attachViewDlg").dialog("center");
}

//获取附件显示url
function getAttachImgUrl(attach){
	var ext = getFileExt(attach['doc_name']);
	return DZF.contextPath + '/chnpay/chnpayconf!getAttachImage.action?billid=' + attach.billid+"&time="+Math.random();
}

//显示附件上的提示
function showTips(i){
	var div = "#reUpload"+i;
	$(div).css("display","block");
	var tips = getTipContents(i);
	var tipkey = "#tips"+ i;
	$(tipkey).html(tips);	
}

//隐藏提示
function hideTips(i){
	var div = "#reUpload"+i;
	$(div).css("display","none");	
}

//获取提示内容
function getTipContents(i){
	var tips = "";
	if(arrachrow){
		var ext = getFileExt(arrachrow['doc_name']);
		if("png"==ext.toLowerCase()||"jpg"==ext.toLowerCase()
				||"jpeg"==ext.toLowerCase()||"bmp"==ext.toLowerCase()){
			tips = "双击查看原图";
		}
	}
	return tips;
}

//双击附件
function doubleImage(i){
	var ext = getFileExt(arrachrow['doc_name']);
	var src = DZF.contextPath + "/chnpay/chnpayconf!getAttachImage.action?billid=" + arrachrow.billid+"&time=" +Math.random();
	if("png"==ext.toLowerCase()||"jpg"==ext.toLowerCase()
			||"jpeg"==ext.toLowerCase()||"bmp"==ext.toLowerCase()){
		$("#tpfd").empty();
		var offset = $("#tpght").offset();
		$("#tpfd").dialog({
			title: '原图' ,
			width:$("#tpght").width(),
			height:$("#tpght").height(),
			left: offset.left,
			top: offset.top,
			cache: false,
			resizable: true,
			center : true,
			align:"center",
			content : '<div style="overflow:scroll;height:100%"><img alt="无法显示图片" src="' + src + '" style="height: " + $(window).height()-10 + ";width: " + $(window).width()-10 +" "></div>',
			onLoad:function(){}
		});
	}
}

//获取附件扩展
function getFileExt(filename){
	var index1=filename.lastIndexOf(".")+1;
	var index2=filename.length;
	var ext=filename.substring(index1,index2);
	return ext;
}
