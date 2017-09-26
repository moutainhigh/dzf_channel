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
	reloadData();
});

//初始化监听
function initListener(){
	// 查询框鼠标经过
	$("#querydate").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
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
		idField : 'id',
		pagination : true,
		pageSize : 20,
		pageList : [ 20, 50, 100, 200 ],
		columns : [[{width : '100',title : '主键id',field : 'id',checkbox: true},
		            {width : '150',title : '加盟商',field : 'cname',align:'left'},
		            {width : '80',title : '付款类型',field : 'paytype',align:'left',
						formatter: function(value,row,index){
			          		if (value == 0){
								return '预付款';
							} else if(value ==1){
								return '加盟费';
							} 
		            }},
		            {width : '80',title : '发票性质',field : 'nature',align:'left',
						formatter: function(value,row,index){
			          		if (value == 0){
								return '公司';
							} else if(value ==1){
								return '个人';
							} 
		            }},
		            {width : '180',title : '单位名称',field : 'cname',align:'left'},
		            {width : '150',title : '税号',field : 'taxnum',align:'left'},
		            {width : '80',title : '开票金额',field : 'iprice',align:'right',
		            	formatter: function(value,row,index){
							if (value == 0) {
								return "0.00";
							}
							return formatMny(value);
		            }},
		            {width : '100',title : '发票类型',field : 'itype',align:'left',
		            	formatter: function(value,row,index){
		              		if (value == 0){
								return '专用发票';
							} else if(value ==1){
								return '普通发票';
							} else if(value ==2){
								return '电子普通发票';
							}
		            }},
		            {width : '180',title : '公司地址',field : 'caddr',align:'left'},
		            {width : '100',title : '开票电话',field : 'phone',align:'left'},
		            {width : '150',title : '开户行',field : 'bname',align:'left'},
		            {width : '150',title : '开户账户',field : 'bcode',align:'left'},
		            {width : '130',title : '邮箱',field : 'email',align:'left'},
		            {width : '90',title : '申请时间',field : 'apptime',align:'center'},
		            {width : '90',title : '开票日期',field : 'invtime',align:'center'},
		            {width : '100',title : '经手人',field : 'iperson',align:'left'},
		            {width : '80',title : '发票状态',field : 'istatus',align:'center',
		            	formatter: function(value,row,index){
		              		if (value == 0){
								return '待提交';
							} else if(value ==1){
								return '待开票';
							} else if(value ==2){
								return '已开票';
							}
		            }},
		            {width : '100',title : '备注',field : 'vmome',align:'center'},
		          ]],
		onBeforeLoad : function(param) {
			$.messager.progress({
				text : '数据加载中....'
			});
		},
		onLoadSuccess : function(data) {
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
	$('#grid').datagrid('options').url = DZF.contextPath + '/sys/sys_inv_manager!query.action';
	
	var bdate = $('#bdate').datebox('getValue'); //开始日期
	var edate = $('#edate').datebox('getValue'); //结束日期
	$('#querydate').html(bdate + ' 至 ' + edate);
	
    $('#grid').datagrid('load', {
    	corps : $("#pk_account").val(),
        bdate: bdate,
        edate: edate,
        istatus : $('#istatus').combobox('getValue')
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

//开票
function onBilling(){
	var rows = $('#grid').datagrid("getSelections");
	if(rows == null || rows.length == 0){
		Public.tips({content : "请选择需要处理的数据!" ,type:2});
		return;
	}
	
	var pk_invoices = [];
	
	$.messager.confirm('提示','您是否确定开票？',
			function(conf){
		if(conf){
			for(var i = 0; i < rows.length; i++){
				pk_invoices.push(rows[i].id);
			}
			$.ajax({
				type : 'post',
				url : DZF.contextPath + '/sys/sys_inv_manager!onBilling.action',
				data : {
					"pk_invoices" : JSON.stringify(pk_invoices)
				},
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