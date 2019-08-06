var contextPath = DZF.contextPath;
var grid;

$(function() {
	load();
	initListener();
	initChannel();
	initArea();
});

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
                    href: DZF.contextPath + '/ref/channelys_select.jsp',
                    queryParams : {
    					ovince :"-1"
    				},
                    buttons: '#chnBtn'
                });
            }
        }]
    });
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
 * 双击选择公司
 * @param rowTable
 */
function dClickCompany(rowTable){
	var str = "";
	var corpIds = [];
	if(rowTable){
		if(rowTable.length>300){
			Public.tips({content : "一次最多只能选择300个客户" ,type:2});
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
	$('#aname').combobox('setValue', null);
	$("#channel_select").textbox("setValue",null);
}

/**
 * 查询
 */
function reloadData(){
	var bdate = $('#bdate').datebox('getValue'); //开始日期
	var edate = $('#edate').datebox('getValue'); //结束日期
	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').url = contextPath + '/corp/corpeditconf!query.action';
	queryParams.begdate = bdate;
	queryParams.enddate = edate;
	queryParams.qtype = $('#qtype').combobox('getValue');
	queryParams.cpid = $('#pk_account').val();
	queryParams.aname = $("#aname").combobox('getValue');
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
		},{
			width : '140',
			title : '大区',
			align : 'left',
            halign: 'center',
			field : 'aname'
		},{
			width : '140',
			title : '渠道经理',
			align : 'left',
            halign: 'center',
			field : 'uname'
		}, {
			width : '230',
			title : '加盟商',
			align : 'left',
            halign: 'center',
			field : 'fatname',
		},  {
			width : '160',
			title : '客户编码',
			align : 'left',
            halign: 'center',
			field : 'incode',
		},{
			width : '230',
			title : '原客户名称',
			align : 'left',
            halign: 'center',
			field : 'oldname',
		}, {
			width : '230',
			title : '现客户名称',
			align : 'left',
            halign: 'center',
			field : 'newname',
		}, {
			width : '200',
			title : '备注',
            halign:'center',
			field : 'memo',
			formatter : function(value) {
				if(value!=undefined){
					return "<span title='" + value + "'>" + value + "</span>";
				}
			}
		},{
			width : '60',
			title : '附件',
			field : 'url',
			align : 'center',
			formatter : function(value, row, index) {
				if(!isEmpty(value)){
					if(!isEmpty(row.pk_id)){
						return '<a href="javascript:void(0)"  style="color:blue" onclick="showImage(\''+row.pk_id+'\')" >' + "附件"+ '</a>';
					}
				}
			}
		},{
			width : '80',
			title : '审核状态',
            halign:'center',
			field : 'statu',
			align:'center',
			formatter : function(value) {
				if (value == '0')
					return '待提交';
				if (value == '1')
					return '待审核';
				if (value == '2')
					return '已审核';
				if (value == '3')
					return '已驳回';
			}
		}, {
			width : '160',
			title : '提交时间',
			align : 'left',
            halign: 'center',
            align:'center',
			field : 'subtime',
		}, /*{
			width : '140',
			title : '审批时间',
            halign:'center',
			field : 'apprtime',
		}, {
			width : '140',
			title : '驳回说明',
            halign:'center',
			field : 'apprnote',
			formatter : function(value) {
				if(value!=undefined){
					return "<span title='" + value + "'>" + value + "</span>";
				}
			}
		}, */{
			field : 'pk_id',
			title : '主键',
			hidden : true
		}, {
			field : 'updatets',
			title : '时间戳',
			hidden : true
		},] ],
		onLoadSuccess : function(data) {
            parent.$.messager.progress('close');
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

/**
 * 标签查询
 * @param type  -1：全部；1待审核；2已审核；  5:演示待审核
 */
function qryData(type){
	$('#grid').datagrid('unselectAll');
	var queryParams = $('#grid').datagrid('options').queryParams;
	$('#grid').datagrid('options').url = contextPath + '/corp/corpeditconf!query.action';
	queryParams.qtype = type;
	queryParams.begdate = '';
	queryParams.enddate = '';
	queryParams.cpid = null;
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
}

/**
 * 审核
 */
function audit(){
	var rows = $("#grid").datagrid("getChecked");
	if(rows == null || rows.length == 0){
		Public.tips({content:'请选择需要处理的数据',type:2});
        return;
	}
	$('#hlDialog').dialog({ modal:true });//设置dig属性
    $('#hlDialog').dialog('open').dialog('center').dialog('setTitle','名称审核');
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
 * 审核确认(确认按钮)
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
		postdata["type"] = 3;
		postdata["vreason"] = $('#vreason').textbox('getValue');
	}else{
		postdata["type"] = 2;
	}
	operatData(postdata,rows);
	$("#commit").form('clear');
	$('#hlDialog').dialog('close');
}

/**
 * 审核确认(取消按钮)
 */
function canConfirm(){
	$("#commit").form('clear');
	$('#hlDialog').dialog('close');
}

/**
 * 操作数据
 */
function operatData(postdata, rows){
	$.messager.progress({
		text : '数据处理中....'
	});
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/corp/corpeditconf!audit.action',
		data : postdata,
		traditional : true,
		async : false,
		success : function(result) {
			$.messager.progress('close');
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
						map.put(rerows[i].pk_id,rerows[i]);
					}
					var index;
					var indexes = new Array();
					for(var i = 0; i < rows.length; i++){
						if(map.containsKey(rows[i].pk_id)){
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
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.messager.progress('close');
		}
	});
}


/**
 * 图片展示
 * @param billid
 * @returns
 */
function showImage(id){
	var src = DZF.contextPath + "/corp/corpeditconf!getAttachImage.action?id=" + id +"&time=" +Math.random();
	$("#tpfd").empty();
//	parent.openFullViewDlg('<div style="overflow:scroll;height:80%"  >'
//			+'<a  onclick="downFile(\'' +id + '\', 2)"><img id="conturnid" alt="无法显示图片" '
//			+' onmouseover="showTips()" onmouseout="hideTips()"  src="' + src 
//			+ '" style="height: " + $(window).height()-10 + ";width: " + $(window).width()-10 +" ">'
//			+'<div id="reUpload" style="width: 100%; height:25px; position:absolute; top:30%; left:30%; display:none;" >' + 
//	  		+'</a></div>','原图');
	
	var img = '<img id="conturnid" alt="无法显示图片" ondblclick="downFile(\'' +id + '\',2)" '
	+ ' onmouseover="showTips()" onmouseout="hideTips()"'
	+ 'src="' + src + '" style="position: absolute;z-index: 1;left:50px;top:50px;">'
	+'<div id="reUpload" style="width: 100%; height:25px; position:absolute; top:30%; left:30%; display:none;" />';
	parent.openFullViewDlg(img, '原图', id, 2);
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


