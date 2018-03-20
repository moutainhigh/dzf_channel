var contextPath = DZF.contextPath;

$(function(){
	initQueryDlg();
	initRef();
	initRadioListen();
	load(0);
});

/**
 * 查询对话框初始化
 */
function initQueryDlg(){
	initPeriod("#begperiod");
	initPeriod("#endperiod");
	var sdv = $('#bdate').datebox('getValue');
	var edv = $('#edate').datebox('getValue');
	$('#jqj').html(sdv + ' 至 ' + edv);
	$("#begperiod").datebox('readonly',true);
	$("#endperiod").datebox('readonly',true);
	$("#bdate").datebox('readonly',false);
	$("#edate").datebox('readonly',false);
}

/**
 * 参照初始化
 */
function initRef(){
	//查询-加盟商参照初始化
	$('#qcorp').textbox({
		onClickIcon : function() {
			refid = $(this).attr("id");
		},
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
    					issingle : "false"
    				},
                    buttons: '#chnBtn'
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
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个客户",
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
		$("#" + refid).textbox("setValue",str);
		$("#" + refid + "id").val(corpIds);
	}
	$("#chnDlg").dialog('close');
}

/**
 * 查询框按钮单选事件
 */
function initRadioListen(){
	$('input:radio[name="seledate"]').change( function(){  
		var ischeck = $('#qj').is(':checked');
		if(ischeck){
			$("#begperiod").datebox('readonly',false);
			$("#endperiod").datebox('readonly',false);
			$("#bdate").datebox('readonly',true);
			$("#edate").datebox('readonly',true);
		}else{
			$("#begperiod").datebox('readonly',true);
			$("#endperiod").datebox('readonly',true);
			$("#bdate").datebox('readonly',false);
			$("#edate").datebox('readonly',false);
		}
	});
}

/**
 * 表格初始化
 * @param type  0：初始化界面加载；1；查询；2；刷新；
 */
function load(type){
	var columns = new Array(); 
	var columnsh = new Array();//列及合并列名称
	var columnsb = new Array();//子列表名称集合
	
	var onlycol =  new ArrayList();//金额展示集合
	var onlymap = new HashMap();//金额对应列数
	
//	//退回扣款
//	var columnh1 = {};
//	columnh1["title"] = '退回扣款';  
//	columnh1["field"] = 'ret';  
//	columnh1["width"] = '180'; 
//	columnh1["colspan"] = 2; 
//	columnsh.push(columnh1);
//	
//	var columnb1 = {};
//	columnb1["title"] = '户数';  
//	columnb1["field"] = 'retnum';  
//	columnb1["width"] = '90'; 
//	columnb1["halign"] = 'center'; 
//	columnb1["align"] = 'right'; 
////	columnb1["formatter"] = formatMny;
//	columnsb.push(columnb1); 
//	
//	var columnb2 = {};
//	columnb2["title"] = '总额';  
//	columnb2["field"] = 'retmny';  
//	columnb2["width"] = '90'; 
//	columnb2["halign"] = 'center'; 
//	columnb2["align"] = 'right'; 
//	columnb2["formatter"] = formatMny;
//	columnsb.push(columnb2); 
//	
//	//存量
//	var columnh2 = {};
//	columnh2["title"] = '存量';  
//	columnh2["field"] = 'stock';  
//	columnh2["width"] = '180'; 
//	columnh2["colspan"] = 2; 
//	columnsh.push(columnh2);
//	
//	var columnb3 = {};
//	columnb3["title"] = '户数';  
//	columnb3["field"] = 'stocknum';  
//	columnb3["width"] = '90'; 
//	columnb3["halign"] = 'center'; 
//	columnb3["align"] = 'right'; 
////	columnb3["formatter"] = formatMny;
//	columnsb.push(columnb3); 
//	
//	var columnb4 = {};
//	columnb4["title"] = '总额';  
//	columnb4["field"] = 'stockmny';  
//	columnb4["width"] = '90'; 
//	columnb4["halign"] = 'center'; 
//	columnb4["align"] = 'right'; 
//	columnb4["formatter"] = formatMny;
//	columnsb.push(columnb4); 
	
	var bperiod;
	var eperiod;
	var begdate;
	var enddate;
	if($('#qj').is(':checked')){
		bperiod = $("#begperiod").datebox('getValue');
		eperiod = $("#endperiod").datebox('getValue');
		if(isEmpty(bperiod)){
			Public.tips({
				content : "开始期间不能为空",
				type : 2
			});
			return;
		}
		if(isEmpty(eperiod)){
			Public.tips({
				content : "结束期间不能为空",
				type : 2
			});
			return;
		}
		
		$('#jqj').html(bperiod + ' 至 ' + eperiod);
	}else{
		begdate = $("#bdate").datebox('getValue');
		enddate = $("#edate").datebox('getValue');
		$('#jqj').html(begdate + ' 至 ' + enddate);
	}
	
	parent.$.messager.progress({
		text : '数据加载中....'
	});
	var datarray =  new Array();
	$.ajax({
		type : "post",
		dataType : "json",
		url : DZF.contextPath + "/report/deductanalysis!query.action",
		traditional : true,
		data : {
			bperiod : bperiod,
			eperiod : eperiod,
			begdate : begdate,
			enddate : enddate,
			cpid : $("#qcorpid").val(),
		},
		async : false,
		success : function(data) {
			if (data.success) {
				var rows = data.rows;
				if(rows != null && rows.length > 0){
					var corpid;
					var obj;
					var colnm = 0;
					var colfield = "";
					for(var i = 0; i < rows.length; i++){
						if(!onlycol.contains(rows[i].dedmny)){
							var column = {};
							column["title"] = rows[i].dedmny;  
							column["field"] = 'col'+(i+1);  
							column["width"] = '180'; 
							column["colspan"] = 2; 
							columnsh.push(column); 
							
							onlymap.put(rows[i].dedmny,(i+1));
							
							var column1 = {};
							column1["title"] = '户数';  
							column1["field"] = 'num'+(i+1);  
							column1["width"] = '90'; 
							column1["halign"] = 'center'; 
							column1["align"] = 'right'; 
//							column1["formatter"] = formatMny;
							columnsb.push(column1); 
							
							var column2 = {};
							column2["title"] = '总额';  
							column2["field"] = 'mny'+(i+1);  
							column2["width"] = '90'; 
							column2["halign"] = 'center'; 
							column2["align"] = 'right'; 
							column2["formatter"] = formatMny;
							columnsb.push(column2); 
							
							onlycol.add(rows[i].dedmny);
						}
					
						if(i == 0){
							corpid = rows[i].corpid;
							obj = {};
							colnm = onlymap.get(rows[i].dedmny);
							colfield = 'num'+colnm;
							obj[colfield] = rows[i].corpnum;
							colfield = 'mny'+colnm;
							obj[colfield] = rows[i].dedmny;
							
							obj['num'] = rows[i].sumnum;//总户数
							obj['mny'] = rows[i].summny;//总扣款
							obj['corpcode'] = rows[i].corpcode;//加盟商编码
							obj['corpname'] = rows[i].corpname;//加盟商名称
							if(i == rows.length - 1){//一行数据
								colnm = onlymap.get(rows[i].dedmny);
								colfield = 'num'+colnm;
								obj[colfield] = rows[i].corpnum;
								colfield = 'mny'+colnm;
								obj[colfield] = rows[i].dedmny;
								datarray.push(obj);
							}
						}else{
							if(corpid == rows[i].corpid){
								colnm = onlymap.get(rows[i].dedmny);
								colfield = 'num'+colnm;
								obj[colfield] = rows[i].corpnum;
								colfield = 'mny'+colnm;
								obj[colfield] = rows[i].dedmny;
								if(i == rows.length - 1){//最后一行数据
									datarray.push(obj);
								}
							}else if(corpid != rows[i].corpid){
								datarray.push(obj);
								corpid = rows[i].corpid;
								obj = {};
								colnm = onlymap.get(rows[i].dedmny);
								colfield = 'num'+colnm;
								obj[colfield] = rows[i].corpnum;
								colfield = 'mny'+colnm;
								obj[colfield] = rows[i].dedmny;
								
								obj['num'] = rows[i].sumnum;
								obj['mny'] = rows[i].summny;
								obj['corpcode'] = rows[i].corpcode;
								obj['corpname'] = rows[i].corpname;
								if(i == rows.length - 1){//最后一行数据
									datarray.push(obj);
								}
							}
						}
					}
					columns.push(columnsh);
					columns.push(columnsb);
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			parent.$.messager.progress('close');
		}
	});
	
	$('#grid').datagrid({
//		url : DZF.contextPath + "",
//		queryParams:{
//		},
		striped : true,
		title : '',
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : false,
//		pagination : true, //显示分页
//		pageSize : 20, //默认20行
//		pageList : [ 20, 50, 100, 200 ],
//		showRefresh : false,// 不显示分页的刷新按钮
//		showFooter : true,
		border : true,
		remoteSort:false,
		//冻结在 左边的列 
		frozenColumns:[[
						{ field : 'corpid',    title : '会计公司主键', hidden : true},
		                { field : 'corpcode',  title : '加盟商编码', width : 100, halign:'center',align:'left'}, 
		                { field : 'corpname',  title : '加盟商名称', width : 160, halign:'center',align:'left'},
		                { field : 'num',  title : '总户数', width : 100, halign:'center',align:'right'}, 
		                { field : 'mny',  title : '总扣款', width : 100, halign:'center',align:'right',formatter:formatMny},
		]],
		columns : columns,
		onLoadSuccess : function(data) {
			
		},
	});
	
	if(datarray != null && datarray.length > 0){
		$('#grid').datagrid('loadData', datarray);
	}else{
		$('#grid').datagrid('loadData',{ total:0, rows:[]});
	}
	
	$("#qrydialog").hide();
	
	var msg = "";
	if(type == 1){
		msg = "查询成功";
	}else if(type == 2){
		msg = "刷新成功";
	}
	if(!isEmpty(msg)){
		Public.tips({
			content : msg,
			type : 0
		});
	}
	parent.$.messager.progress('close');
}

/**
* 导出
*/
function onExport() {
	var datarows = $('#grid').datagrid("getRows");
	if (datarows == null || datarows.length == 0) {
		Public.tips({
			content: '当前界面数据为空',
			type: 2
		});
		return;
	}
	
	var hblcols = $('#grid').datagrid("options").columns[0];//合并列信息
	var cols = $('#grid').datagrid('getColumnFields');               // 行信息
	var hbhcols = $('#grid').datagrid('getColumnFields', true);       // 合并行信息
	
	Business.getFile(DZF.contextPath + "/report/deductanalysis!export.action", {
		"strlist": JSON.stringify(datarows),
		'hblcols':JSON.stringify(hblcols), //合并列信息
		'cols':JSON.stringify(cols),//除冻结列之外，导出字段编码
		'hbhcols':JSON.stringify(hbhcols)//冻结列编码
	}, true, true);
}

/**
 * 查询框-取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 查询框-清空
 */
function clearParams(){
	$('#qcorp').textbox("setValue",null);
	$('#qcorpid').val(null);
}


