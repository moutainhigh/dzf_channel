var contextPath = DZF.contextPath;
var hbstr = new Array();
var str = new Array();

$(function(){
	initQueryDlg();
	initRef();
	initRadioListen();
	load();
	reloadData();
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
	//查询-渠道经理参照初始化
	$('#manager').textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#manDlg").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择渠道经理',
                    modal: true,
                    href: DZF.contextPath + '/ref/manager_select.jsp',
                    buttons: '#manBtn'
                });
            }
        }]
    });
	//查询-渠道运营参照初始化
	$('#operater').textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#operDlg").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择渠道运营',
                    modal: true,
                    href: DZF.contextPath + '/ref/operater_select.jsp',
                    buttons: '#operBtn'
                });
            }
        }]
    });
}

/**
 * 渠道经理选择事件
 */
function selectMans(){
	var rows = $('#mgrid').datagrid('getChecked');
	dClickMans(rows);
}

/**
 * 双击选择渠道经理
 * @param rowTable
 */
function dClickMans(rowTable){
	var unames = "";
	var uids = [];
	if(rowTable){
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个经理",
				type : 2
			});
			return;
		}
		for(var i=0;i<rowTable.length;i++){
			if(i == rowTable.length - 1){
				unames += rowTable[i].uname;
			}else{
				unames += rowTable[i].uname+",";
			}
			uids.push(rowTable[i].uid);
		}
		$("#manager").textbox("setValue",unames);
		$("#managerid").val(uids);
	}
	$("#manDlg").dialog('close');
}

/**
 * 渠道运营选择事件
 */
function selectOpers(){
	var rows = $('#ogrid').datagrid('getChecked');
	dClickOpers(rows);
}

/**
 * 双击选择渠道运营
 * @param rowTable
 */
function dClickOpers(rowTable){
	var unames = "";
	var uids = [];
	if(rowTable){
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个运营",
				type : 2
			});
			return;
		}
		for(var i=0;i<rowTable.length;i++){
			if(i == rowTable.length - 1){
				unames += rowTable[i].uname;
			}else{
				unames += rowTable[i].uname+",";
			}
			uids.push(rowTable[i].uid);
		}
		$("#operater").textbox("setValue",unames);
		$("#operaterid").val(uids);
	}
	$("#operDlg").dialog('close');
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
	var callback=function(){
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
	checkBtnPower('export','channel29',callback);
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
	$('#manager').textbox("setValue",null);
	$('#managerid').val(null);
	$('#operater').textbox("setValue",null);
	$('#operaterid').val(null);
	$('#qcorp').textbox("setValue",null);
	$('#qcorpid').val(null);
}

/**
 * grid初始化冻结列
 */
function load(){
	$('#grid').datagrid({
		striped : true,
		title : '',
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : false,
		border : true,
		remoteSort:false,
		showFooter:true,
		disableAutoSize:true,
		//冻结在 左边的列 
		frozenColumns:[[
						{ field : 'corpid',    title : '会计公司主键', hidden:true},
						{ field : 'mid',  title : '渠道经理', width : 100, halign:'center',align:'left'}, 
						{ field : 'oid',  title : '渠道运营', width : 100, halign:'center',align:'left'}, 
		                { field : 'corpcode',  title : '加盟商编码', width : 100, halign:'center',align:'left'}, 
		                { field : 'corpname',  title : '加盟商', width : 160, halign:'center',align:'left'},
		                { field : 'stocknum',  title : '存量<br/>客户', width : 50, halign:'center',align:'right'},
		                { field : 'custnum',  title : '存量<br/>合同数', width : 60, halign:'center',align:'right', },
		                { field : 'zeronum',  title : '0扣款<br/>(非存量)<br/>合同数', width : 70, halign:'center',align:'right', },
		                { field : 'dednum',  title : '非存量<br/>合同数', width : 60, halign:'center',align:'right', },
		                { field : 'summny',  title : '总扣款', width : 100, halign:'center',align:'right',formatter:formatMny},
		]],
		onLoadSuccess : function(data) {
		},
	});
}


/**
 * 查询
 */
function reloadData(type){
	
	var bdate = new Date();
	
	var onlycol =  new ArrayList();//金额展示集合
	var onlymap = new HashMap();//金额对应列数
	
	var bperiod;
	var eperiod;
	var begdate;
	var enddate;
	var qtype = $("input[name='seletype']:checked").val();
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
	
	$.messager.progress({
		text : '数据查询中，请稍候.....'
	});
	var currcolumns = $("#grid").datagrid("options").columns;
    $.ajax({
    	url : DZF.contextPath + "/report/deductanalysis!query.action",
        type: "POST",  
        data : {
			bperiod : bperiod,
			eperiod : eperiod,
			begdate : begdate,
			enddate : enddate,
			qtype : qtype,
			cpid : $("#qcorpid").val(),
			mid : $("#managerid").val(),
		    oid : $("#operaterid").val(),
		},
 		dataType : 'json',
	    success: function(data){
	    	$.messager.progress('close');
			
			if (data.success) {
				var options = $("#grid").datagrid("options"); //取出当前datagrid的配置     
		        //字符串转JSON
		        if(data.columns.length != 0 && data.hbcolumns != 0){
		        	hbstr = JSON.stringify(data.hbcolumns);
		        	str = JSON.stringify(data.columns);
		        	options.columns = eval('[' + hbstr + ',' +str+ ']');
		        	$('#grid').datagrid(options);
		        }else{
		        	hbstr = null;
		        	str = null;
		        	options.columns = currcolumns;
		        	$('#grid').datagrid(options);
		        }
				
				var rowdata;
				if (data.rowdata != undefined && data.rowdata.length > 0) {
					rowdata = eval(data.rowdata);
				} else {
					rowdata = data.rows;
				}
				
				//实例化之后立刻载入数据源,加载本地数据，旧的行会被移除。
				$('#grid').datagrid("loadData", rowdata);
				
				if(data.sumdata != undefined && data.sumdata.length > 0){
					var sumdata = eval(data.sumdata);
					$('#grid').datagrid('reloadFooter', sumdata);
				}
				
				if(type == 1){
					Public.tips({
						content : "刷新成功",
						type : 0
					});
				}else{
					Public.tips({
						content : data.msg,
						type : 0
					});
				}
			} else {
				Public.tips({
					content : data.msg,
					type : 2
				});
			}
			
	     }
    });
    
    $("#qrydialog").hide();
  
}