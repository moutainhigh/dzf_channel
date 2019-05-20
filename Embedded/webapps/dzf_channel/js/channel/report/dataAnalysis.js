var grid;

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});

$(function(){
	initQryData();
	load();
	reloadData();
	initArea();
	initChannel();
	
});

/**
 * 列表表格初始化
 */
function load(){
	$('#grid').datagrid({
		striped : true,
		title : '',
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : false,
		pagination : true, //显示分页
		pageSize : 50,
		pageList : [10, 30, 50, 80, 100],
		showRefresh : false,// 不显示分页的刷新按钮
		showFooter : true,
		border : true,
		remoteSort:false,
		//冻结在 左边的列 
		frozenColumns:[[
						{ field : 'ck',	checkbox : true },
						{ field : 'corpid', title : '加盟商主键', hidden : true},
						{ field : 'aname',  title : '大区', width : 60, formatter:anameFormat}, 
						{ field : 'provname',  title : '省市', width : 100}, 
		                { field : 'corpname',  title : '加盟商', width : 150}, 
		]],
		columns : [ 
		            [ 
		             { field : 'jdate', title : '加盟日期', width:90,halign:'center',align:'center',rowspan:2},
		             { field : 'dreldate', title : '解约日期', width:90,halign:'center',align:'center',rowspan:2},
		             { field : 'custnum', title : '客户数',  halign:'center',align:'center',colspan:2},
		             { field : 'stocknum', title : '存量客户', halign:'center',align:'center',colspan:2},
		             { field : 'nstocknum', title : '非存量客户', halign:'center',align:'center',colspan:2},
		             
		             { field : 'sconnum', title : '存量<br>合同', width:70,halign:'center',align:'right',rowspan:2},
		             { field : 'zconnum', title : '0扣款<br>(非存量)<br>合同', width:70,halign:'center',align:'right',rowspan:2},
		             { field : 'nsconnum', title : '非存量<br>合同', width:70,halign:'center',align:'right',rowspan:2},
		             
		             { field : 'naccmny', title : '合同代账费', width:80,formatter:formatMny,halign:'center',align:'right',rowspan:2}, 
		             { field : 'nbmny', title : '账本费', width:80,formatter:formatMny,halign:'center',align:'right',rowspan:2},
		             { field : 'ndpmny', title : '保证金', width:80,formatter:formatMny,halign:'center',align:'right',rowspan:2},
		             { field : 'npmny', title : '预存款余额', width:80,formatter:formatMny,halign:'center',align:'right',rowspan:2},
		             { field : 'nrmny', title : '返点余额', width:80,formatter:formatMny,halign:'center',align:'right',rowspan:2},
		             { field : 'ndtmny', title : '合同扣款', width:80,formatter:formatMny,halign:'center',align:'right',rowspan:2},
		             { field : 'ngbmny', title : '商品购买', width:80,formatter:formatMny,halign:'center',align:'right',rowspan:2},
		             
		             { field : 'ssumnum', title : '总用户数', width:80,halign:'right',align:'left',rowspan:2},
		            ] ,
		        [
		            { field : 'smcnum', title : '小规模', width : 60, halign:'center',align:'right'}, 
		            { field : 'gecnum', title : '一般人', width : 60, halign:'center',align:'right'}, 
		            { field : 'smsnum', title : '小规模', width : 60, halign:'center',align:'right'}, 
		            { field : 'gesnum', title : '一般人', width : 60, halign:'center',align:'right'}, 
		            { field : 'smnsnum', title : '小规模', width : 60, halign:'center',align:'right'}, 
		            { field : 'gensnum', title : '一般人', width : 60, halign:'center',align:'right'}, 
		        ] ],
	});
}

/**
 * 大区添加tips显示
 * @returns
 */
function anameFormat(value){
	if(value != undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}
}

/**
 * 关闭查询对话框
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
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个客户!",
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
		
		$("#channel_select").textbox("setValue",str);
		$("#pk_account").val(corpIds);
	}
	 $("#chnDlg").dialog('close');
}

/**
 * 选择加盟商
 */
function selectCorps(){
	var rows = $('#gsTable').datagrid('getChecked');
	dClickCompany(rows);
}

/**
 * 查询大区初始化
 */
function initArea() {
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryArea.action',
		data : {
			"qtype" : 3
		},
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				$('#aname').combobox('loadData', result.rows);
			}
		}
	});
}

/**
 * 查询日期初始化
 */
function initQryData(){
	$('#jqj').html(parent.SYSTEM.PreDate + ' 至 ' + parent.SYSTEM.LoginDate);
	$("#bdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#edate").datebox("setValue", parent.SYSTEM.LoginDate);
}

/**
 * 查询框-清除
 */
function clearParams(){
	$("#channel_select").textbox("setValue",null);
	$("#pk_account").val(null);
	$('#aname').combobox('setValue', null);
}

/**
 * 查询
 */
function reloadData(){
	// 字段校验
	var bdate = $("#bdate").datebox("getValue");
	var edate = $("#edate").datebox("getValue");
	if(isEmpty(bdate)){
		Public.tips({
			content : "查询开始日期不能为空",
			type : 2
		});
		return;
	}
	if(isEmpty(edate)){
		Public.tips({
			content : "查询结束日期不能为空",
			type : 2
		});
		return;
	}
	var ovince = $("#aname").combobox('getValue');
	if(isEmpty()){
		ovince = -1;
	}
	var url = DZF.contextPath + "/report/dataanalysis!query.action";
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		"begdate" : bdate,
		"enddate" : edate,
		"bperiod" : $("#jmbdate").datebox("getValue"),
		"eperiod" : $("#jmedate").datebox("getValue"),
		"cpid" : $("#pk_account").val(),
		"ovince" : ovince,
	});
	$("#jqj").html(bdate + " 至 " + edate);
	closeCx();
}

/**
 * 导出
 */
function onExport(){
	
}
