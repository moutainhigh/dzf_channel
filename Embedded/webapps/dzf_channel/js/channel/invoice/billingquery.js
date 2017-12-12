var isenter = false;//是否快速查询
var loadrows = null;

// 数据表格随窗口大小改变
$(window).resize(function() {
	$('#grid').datagrid('resize', {
		height : Public.setGrid().h,
	});
});

$(function(){
	initListener();
	initDataGrid();
	initChannel();
//	reloadData();
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
	
	$('#quname').textbox('textbox').keydown(function (e) {
        if (e.keyCode == 13) {
        	$('#grid').datagrid('unselectAll');
 		   var filtername = $("#quname").val(); 
		   if (filtername) {
				var jsonStrArr = [];
				if(loadrows){
					for(var i=0;i<loadrows.length;i++){
						var row = loadrows[i];
						if(row.ccode.indexOf(filtername) >= 0 || row.cname.indexOf(filtername) >= 0){
							jsonStrArr.push(row);
						} 
					}
					$('#grid').datagrid('loadData',jsonStrArr);   
				}
			}else{
				$('#grid').datagrid('loadData',loadrows);
			} 
        }
    });
}

function closeCx(){
	$("#qrydialog").hide();
}
function initDataGrid(){
	$('#grid').datagrid({
		//url : contextPath + '/order/orderact!query.action',
		striped : true,
		rownumbers : true,
		fitColumns: false,
		remoteSort : false,
		singleSelect : false,
		height : Public.setGrid().h,
		idField : 'corpid',
//		pagination : true,
//		pageSize : 20,
//		pageList : [ 20, 50, 100, 200 ],
		columns : [[{width : '100',title : '',field : 'id',checkbox: true},
		            {width : '150',title : '加盟商编码',field : 'ccode',align:'left'},
		            {width : '260',title : '加盟商名称',field : 'cname',align:'left'},
		            {width : '120',title : '累计扣款金额',field : 'dtotalmny',align:'right',
		            	formatter: function(value,row,index){
							if (value == 0) {
								return "0.00";
							}
							return formatMny(value);
		            }},
		            {width : '120',title : '累计开票金额',field : 'btotalmny',align:'right',
		            	formatter: function(value,row,index){
							if (value == 0) {
								return "0.00";
							}
							return formatMny(value);
		            }},
		            {width : '120',title : '未开票金额',field : 'noticketmny',align:'right',
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
			if(data.rows && loadrows == null){
				loadrows = data.rows;
			}
			$.messager.progress('close');
			$("#qrydialog").hide();
		}
	});
}

//初始化加盟商
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
                    buttons: '#kj_buttons'
                });
            }
        }]
    });
}

function reloadData(){
	loadrows = null;
	$('#grid').datagrid('options').url = DZF.contextPath + '/invoice/billingquery!query.action';
	var bdate = $('#bdate').datebox('getValue'); //开始日期
    $('#grid').datagrid('load', {
    	corps : $("#pk_account").val(),
        bdate: bdate,
    });
    $('#qrydialog').hide();
    $('#grid').datagrid('unselectAll');
}

//双击选择公司
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

function clearParams(){
	$("#pk_account").val(null);
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
	var columns = $('#grid').datagrid("options").columns[0];
	Business.getFile(DZF.contextPath+ '/invoice/billingquery!onExport.action',{'strlist':JSON.stringify(datarows)}, true, true);
}

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

