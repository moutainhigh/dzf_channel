var grid;
var type;
var ovince;

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});

$(function() {
	initQry();
	load();
	initUserGrid();
	
	
});

//初始化
function initQry(){
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	//initQryCommbox();
	queryQtype();
	changeAreaName();
	changeProvinceName();
	initArea({"qtype" :type});
	initProvince({"qtype" :type});
	initManager({"qtype" :type});
	initChannelName();
}

function queryQtype(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/report/personStatis!queryQtype.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    type = result.rows;
			    ovince = type==1?-2:-3;
			   /* if(type==1){
			    	 ovince = -2;
			    }else if(type==2){
			    	
			    }
			   */
			} 
		}
	});
}

function changeAreaName(){
	 $("#aname").combobox({
		onChange : function(n, o) {
			var queryData={"qtype" :type};
			if(!isEmpty(n)){
				queryData={'aname' : n,"qtype" :type};
				$('#ovince').combobox('setValue',null);
				$('#uid').combobox('setValue',null);
			}
			initProvince(queryData);
			initManager(queryData);
		}
	});
}

function changeProvinceName(){
	 $("#ovince").combobox({
		onChange : function(n, o) {
			var queryData={"qtype" :type};
			if(!isEmpty(n)){
				queryData={'aname' : $("#aname").combobox('getValue'),'ovince':n,"qtype" :type};
				$('#uid').combobox('setValue',null);
			}
			initManager(queryData);
		}
	});
}



//初始化加盟商
function initChannelName(){
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
                    	ovince : ovince
    				},
                    buttons: '#kj_buttons'
                });
            }
        }]
    });
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

//清空查询条件
function clearCondition(){
	$('#aname').combobox('select',null);
	$('#ovince').combobox('select',null);
	$('#uid').combobox('select',null);
	$("#pk_account").val(null);
	$("#channel_select").textbox("setValue",null);
}


/**
 * 数据表格初始化
 */
function load(){
	var vince=$('#ovince').combobox('getValue');
	if(isEmpty(vince)){
		vince=-1;
	}
	$('#grid').datagrid({
		url : DZF.contextPath + "/report/personStatis!query.action",
		queryParams:{
			'aname' : $('#aname').combobox('getValue'),
			'ovince' :vince,
			'uid' : $('#uid').combobox('getValue'),
			'corps' : $("#pk_account").val(),
		},
		striped : true,
		title : '',
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : true,
		showRefresh : false,// 不显示分页的刷新按钮
		showFooter : true,
		border : true,
		remoteSort:false,
		frozenColumns:[[
					{ field : 'corpid',    title : '会计公司主键', hidden : true,},
					{ field : 'marketid',    title : '销售团队主键', hidden : true,},
					{ field : 'aname',  title : '大区', width : 100,halign:'center',align:'left',},
					{ field : 'uname',  title : '区总', width : 100,halign:'center',align:'left',},
					{ field : 'provname',  title : '省份', width : 160,halign:'center',align:'left',}, 
					{ field : 'incode',  title : '加盟商编码', width : 140,halign:'center',align:'left',},
					{ field : 'corpnm', title : '加盟商名称', width:200,halign:'center',align:'left',
						formatter: function(value,row,index){
							if (!isEmpty(row.dreldate)) {
								return  "<div style='position: relative;'>" + "<a href='javascript:void(0)' style='color:blue' onclick=\"qryUserDetail('"+row.corpid+"')\">" + value + "</a>" + "<img style='right: 0; position: absolute;' src='../../images/rescission.png' /></div>"
							}else{
								return "<a href='javascript:void(0)' style='color:blue' onclick=\"qryUserDetail('"+row.corpid+"')\">" + value + "</a>";
						    }
						}
					},
		       ]],
		columns : [[	
			            { field : 'chndate', title : '加盟日期', width:100,halign:'center',align:'center',rowspan:2},
			            { field : 'custnum', title : '总客户数', width:80,halign:'center',align:'right',rowspan:2},
		                { field : 'cuname',  title : '会计运营经理', width : 120,halign:'center',align:'left',rowspan:2},
		                { field : 'jms01',  title : '机构负责人', width : 80,halign:'center',align:'right',rowspan:2},
		                { field : 'meiyong1',  title : '会计团队总人数', width : 160,halign:'center',align:'right',colspan:8},
		                { field : 'ktotal',  title : '人员占比(%)', width : 90,halign:'center',align:'right',rowspan:2,formatter:formatMny},
		                { field : 'lznum',  title : '离职数', width : 60,halign:'center',align:'right',rowspan:2},
		                { field : 'ltotal',  title : '流失率(%)', width : 80,halign:'center',align:'right',rowspan:2,formatter:formatMny},
		                { field : 'meiyong2',  title : '销售团队总人数', width : 160,halign:'center',align:'right',colspan:4},
		                { field : 'xtotal',  title : '人员占比(%)', width : 90,halign:'center',align:'right',rowspan:2,formatter:formatMny},
		                { field : 'total',  title : '总用户数', width : 70,halign:'center',align:'right',rowspan:2},
	                ],[
	                   	{ field : 'jms02', title : '会计经理', width : 70,align:'right' },
	                   	{ field : 'jms06', title : '主管会计', width : 70,align:'right' },
	                   	{ field : 'jms07', title : '主办会计', width : 70,align:'right' },
	                   	{ field : 'jms08', title : '记账会计', width : 70,align:'right' },
	                   	{ field : 'jms09', title : '财税支持', width : 70,align:'right' },
	                   	{ field : 'jms05', title : '外勤主管', width : 70,align:'right' },
	                   	{ field : 'jms11', title : '助理会计', width : 70,align:'right' },
	                   	{ field : 'knum', title : '合计', width : 70,align:'right' },
	                   	
	                   	{ field : 'jms03', title : '销售经理', width : 70,align:'right' },
	                   	{ field : 'jms04', title : '销售主管', width : 70,align:'right' },
	                   	{ field : 'jms10', title : '销售', width : 70,align:'right' },
	                   	{ field : 'xnum', title : '合计', width : 70,align:'right' },
	                ]],
		onLoadSuccess : function(data) {
			var rows = $('#grid').datagrid('getRows');
			var footerData = new Object();
			var custnum = 0;	// 
			var jms01 = 0;	// 
			var jms02 = 0;	// 
			var jms06 = 0;	// 
			var jms07 = 0;	// 
			var jms08 = 0;	// 
			var jms09 = 0;	// 
			var jms05 = 0;	// 
			var jms11 = 0;	// 
			var jms03 = 0;	// 
			var jms04 = 0;	// 
			var jms10 = 0;	// 
			var lznum = 0;	// 
			var total = 0;	// 

			for (var i = 0; i < rows.length; i++) {
				if(rows[i].custnum != undefined && rows[i].custnum != null){
					custnum += parseFloat(rows[i].custnum);
				}
				if(rows[i].jms01 != undefined && rows[i].jms01 != null){
					jms01 += parseFloat(rows[i].jms01);
				}
				if(rows[i].jms02 != undefined && rows[i].jms02 != null){
					jms02 += parseFloat(rows[i].jms02);
				}
				if(rows[i].jms06 != undefined && rows[i].jms06 != null){
					jms06 += parseFloat(rows[i].jms06);
				}
				if(rows[i].jms07 != undefined && rows[i].jms07 != null){
					jms07 += parseFloat(rows[i].jms07);
				}
				if(rows[i].jms08 != undefined && rows[i].jms08 != null){
					jms08 += parseFloat(rows[i].jms08);
				}
				if(rows[i].jms09 != undefined && rows[i].jms09 != null){
					jms09 += parseFloat(rows[i].jms09);
				}
				if(rows[i].jms05 != undefined && rows[i].jms05 != null){
					jms05 += parseFloat(rows[i].jms05);
				}
				if(rows[i].jms11 != undefined && rows[i].jms11 != null){
					jms11 += parseFloat(rows[i].jms11);
				}
				if(rows[i].jms03 != undefined && rows[i].jms03 != null){
					jms03 += parseFloat(rows[i].jms03);
				}
				if(rows[i].jms04 != undefined && rows[i].jms04 != null){
					jms04 += parseFloat(rows[i].jms04);
				}
				if(rows[i].jms10 != undefined && rows[i].jms10 != null){
					jms10 += parseFloat(rows[i].jms10);
				}
				if(rows[i].lznum != undefined && rows[i].lznum != null){
					lznum += parseFloat(rows[i].lznum);
				}
				if(rows[i].total != undefined && rows[i].total != null){
					total += parseFloat(rows[i].total);
				}
			}
			var footerData = new Object();
			footerData['incode'] = '合计';
			footerData['corpnm'] = "";
			footerData['custnum'] = custnum;
			footerData['jms01'] = jms01;
			footerData['jms02'] = jms02;
			footerData['jms06'] = jms06;
			footerData['jms07'] = jms07;
			footerData['jms08'] = jms08;
			footerData['jms09'] = jms09;
			footerData['jms05'] = jms05;
			footerData['jms11'] = jms11;
			footerData['jms03'] = jms03;
			footerData['jms04'] = jms04;
			footerData['jms10'] = jms10;
			footerData['lznum'] = lznum;
			footerData['total'] = total;
			var fs=new Array(1);
			fs[0] = footerData;
			$('#grid').datagrid('reloadFooter',fs);
		},
	});
}

function qryUserDetail(corpid){
	$('#userGrid').datagrid('options').url = DZF.contextPath + '/report/personStatis!queryUserDetail.action';
	$('#userDetail').dialog('open');
    $('#userGrid').datagrid('load', {
		cpid:corpid,
    });
}

function initUserGrid(){
	gridh = $('#userGrid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height:'380',
		singleSelect : true,
//		pagination : true,
//		pageSize : DZF.pageSize_min,
//		pageList : DZF.pageList_min,
		showFooter:true,
		columns : [ [ {
			width : '100',
			title : '部门',
			align:'center',
			halign:'center',
			field : 'deptname',
		}, {
			width : '100',
			title : '用户名称',
            halign:'center',
			field : 'uname',
		},{
			width : '200',
			title : '角色',
			align:'right',
            halign:'center',
			field : 'rolename',
		},{
			width : '80',
			title : '客户数',
			align:'right',
            halign:'center',
			field : 'corpnum',
		}, {
			width : '100',
			title : '小规模纳税人',
			align:'right',
            halign:'center',
			field : 'corpnum1',
		}, {
			width : '100',
			title : '一般纳税人',
			align:'right',
            halign:'center',
			field : 'corpnum2',
		}, ] ],
		onLoadSuccess : function(data) {
			
		},
	});
}


function edit(){
	
	$('#market_edit').form('clear');
	var row = $('#grid').datagrid('getSelected');
	if(row==null){
		Public.tips({content:'请选择数据行',type:2});
		return;
	}
	$("#corpname").textbox("setValue",row.corpnm);
	$("#corpid").val(row.corpid);
	$("#code").val(row.incode);
	
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '销售团队修改');
	var data = queryById(row.marketid);
	if(isEmpty(data)){
		return;
	}
	
	$('#market_edit').form('load', data);
	
}

function queryById(marketid){
	var row;
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url : DZF.contextPath + '/report/personStatis!queryById.action',
		data : {
			"id" : marketid,
		},
		success : function(data) {
			if (!data.success) {
				Public.tips({
					content :  data.msg,
					type : 2
				});	
				return;
			} else {
				row = data.rows;
			}
		},
	});
	return row;
}


function onSave(){
	if ($("#market_edit").form('validate')) {
		$('#market_edit').form('submit', {
			url : DZF.contextPath + '/report/personStatis!save.action',
			success : function(result) {
				var result = eval('(' + result + ')');
				if (result.success) {
					$('#cbDialog').dialog('close');
					load();
					Public.tips({
						content : result.msg,
					});
				} else {
					Public.tips({
						content : result.msg,
						type : 2
					});
				}
			}
		});
	} else {
		Public.tips({
			content : "必输信息为空或信息输入不正确",
			type : 2
		});
		return; 
	}
}


function onCancel(){
	$('#cbDialog').dialog('close');
}



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
		var coltwo = $('#grid').datagrid("options").columns[1];
		var cols = $('#grid').datagrid('getColumnFields');//解冻列的字段
		var frozencols = $('#grid').datagrid('getColumnFields', true); //冻结列的字段
		Business.getFile(DZF.contextPath+ '/report/personStatis!exportAuditExcel.action',
				{
					'strlist':JSON.stringify(datarows),
					'columns':JSON.stringify(columns),
					'cols':JSON.stringify(cols),
					'coltwo':JSON.stringify(coltwo),
					'frozencols':JSON.stringify(frozencols),
				},true, true);
				

}

