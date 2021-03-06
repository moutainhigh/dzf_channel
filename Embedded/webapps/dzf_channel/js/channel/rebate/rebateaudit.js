var contextPath = DZF.contextPath;
var grid;
var refid;

$(function(){
	load();
	initRef();
	initQryData();
	fastQry();
});

/**
 * 查询框初始化
 */
function initQryData(){
	var qyear = $("#qyear").combobox("getValue");
	var qjd = $("#qjd").combobox("getText");
	$("#jqj").html(qyear+"年");
	$("#qjd").combobox("setValue", null);
}

/**
 * 参照格式化
 */
function initRef(){
	//渠道经理参照初始化
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
 * 渠道经理选择事件
 */
function selectMans(){
	var rows = $('#mgrid').datagrid('getSelections');
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
//		$("#corpkid_ae").val(null);
//		$("#corpkna_ae").textbox("setValue",null);
//		if(!isEmpty(rowTable.length)&&rowTable.length==1){
//			$('#corpkna_ae').textbox('readonly',false);
//		}else{
//			$('#corpkna_ae').textbox('readonly',true);
//		}
		
		$("#manager").textbox("setValue",unames);
		$("#managerid").val(uids);
	}
	 $("#manDlg").dialog('close');
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
 * 列表表格初始化
 */
function load(){
	grid = $('#grid').datagrid({
		striped : true,
		title : '',
		fitColumns:false,
		rownumbers : true,
		height : Public.setGrid().h,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		singleSelect : false,
		idField : 'rebid',
		showFooter:true,
		frozenColumns :[[ { field : 'ck', checkbox : true },
			              { field : 'operate', title : '操作列',width :'150',halign: 'center',align:'center',formatter:opermatter} ,
		               ]],
		columns : [ [ 
		              { field : 'operdate', title : '录入日期',width :'110',halign: 'center',align:'center' }, 
		              { field : 'vcode', title : '返点单号',width :'120',halign: 'center',align:'left', formatter:codeLink },
		              { field : 'aname', title : '大区',width :'130',halign: 'center',align:'left'}, 
		              { field : 'provname', title : '省(市)',width :'110',halign: 'center',align:'left'} ,
		              { field : 'mname', title : '渠道经理',width :'115',halign: 'center',align:'left'} ,
		              { field : 'corpcode', title : '加盟商编码',width :'115',halign: 'center',align:'left'} ,
		              { field : 'corp', title : '加盟商名称',width :'160',halign: 'center',align:'left'}, 
		              { field : 'period', title : '返点所属期间',width :'115',halign: 'center',align:'center'} ,
		              { field : 'contnum', title : '合同数量',width :'100',halign: 'center',align:'right'} ,
		              { field : 'debitmny', title : '扣款金额',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'basemny', title : '返点基数',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'rebatemny', title : '返点金额',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'istatus', title : '状态',width :'100',halign: 'center',align:'center', formatter : formatSta} ,
		              { field : 'memo', title : '说明',width :'180',halign: 'center',align:'left'} ,
				      { field : 'rebid', title : '主键', hidden:true},
				      { field : 'tstp', title : '时间戳', hidden:true},
		] ],
		onLoadSuccess : function(data) {
			$('#grid').datagrid("scrollTo",0);
			calFooter();
		}
	});
}

/**
 * 计算合计
 */
function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
    var debitmny = parseFloat(0);	
    var basemny = parseFloat(0);	
    var rebatemny = parseFloat(0);
    for (var i = 0; i < rows.length; i++) {
    	debitmny += getFloatValue(rows[i].debitmny);
    	basemny += getFloatValue(rows[i].basemny);
    	rebatemny += getFloatValue(rows[i].rebatemny);
    }
    footerData['corp'] = '合计';
    footerData['debitmny'] = debitmny;
    footerData['basemny'] = basemny;
    footerData['rebatemny'] = rebatemny;
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}

/**
 * 单号格式化
 * @param value
 * @param row
 * @param index
 */
function codeLink(value,row,index){
	if(!isEmpty(value)){
		return '<a href="javascript:void(0)" style="color:blue"  onclick="showInfo(' + index + ')">'+value+'</a>';
	}
}

/**
 * 返点单查看
 * @param index
 */
function showInfo(index){
	var row = $('#grid').datagrid('getData').rows[index];
	$.ajax({
		url : DZF.contextPath + "/rebate/rebateinput!queryInfoById.action",
		dataType : 'json',
		data : row,
		success : function(rs) {
			if (rs.success) {
				$('#showDlg').dialog({
					modal:true
				});//设置dig属性
				$('#showDlg').dialog('open').dialog('center').dialog('setTitle','返点单查看');
				var row = rs.rows;
				$('#showForm').form('clear');
				$('#showForm').form('load', row);
				$("#shistory").empty();
				if(row.children != null && row.children.length > 0){
					var history = null;
					var info = "<p class='slideA'>"+
					"<a href='javascript:;' style='color:#FFF;font-size: 14px;' class='btn-slideA active'>审批历史</a>"+
					"</p>"+
					"<div style='height:230px;overflow:auto;'>"+
					"<div style='' id='panelA'>";
					if(row.children.length >= 1){
						history = row.children[0];
						info = info + "<div class='tall' style=' margin-top: 16px;'>"+
						"<div  class='Aroundly'>"+
						"<img src='../../images/tbpng_03.png' style='position: absolute; left: 90px;'/>"+
						"<img src='../../images/pngg_03.png' style='position: absolute; left: 96px; top: 14px;'/>"+
						"</div>"+
						"<div class='state'>"+
						"<div>"+
						"<font>"+history.sendtime+"</font>&emsp;<span>"+history.dealname+"</span>&emsp;<span>"+history.vsnote+"</span>"+
						"</div>";
						if(!isEmpty(history.pronote)){
							info = info +"<div>"+history.pronote+"</div>";
						}
						info = info + "</div>"+
						"</div>";
					}
					if(row.children.length > 1){
						info = info + "<div style='display: none;' id='panela2'>"+
						"<div style='width:auto;'>";
						for(var i = 1; i < row.children.length; i++){
							history = row.children[i];
							info = info +"<div class='tall'>"+
							"<div  class='Aroundly'>"+
							"<img style='position: absolute; left: 92px;' src='../../images/xial_03.png' /> "+
							"<img style='position: absolute; left: 96px; top: 8px;' src='../../images/pngg_03.png' />"+
							"</div>"+
							"<div class='state'>"+
							"<div>"+
							"<font>"+history.sendtime+"</font>&emsp;<span>"+history.dealname+"</span>&emsp;<span>"+history.vsnote+"</span>"+
							"</div>";
							if(!isEmpty(history.pronote)){
								info = info +"<div>"+history.pronote+"</div>";
							}
							info = info + "</div>"+
							"</div>";
						}
						info = info +"</div>"+"</div>";
					}
					info = info +"<p class='slide'>"+
					"<a href='javascript:;' rel='external nofollow' class='btn-slide active'></a>"+
					"</p>"+
					"</div>"+
					"</div>";
					$("#shistory").append(info);
					historyListenB();
                }
				
			} else {
				Public.tips({
					content : rs.msg,
					type : 1
				});
			}
		},
	});
}

/**
 * 查看-审批历史按钮点击监听事件
 */
function historyListenB(){
	$(".btn-slide").click(function() {
		$("#panela2").slideToggle("slow");
		$(this).toggleClass("active");
		return false;
	})
}

/**
 * 审批历史按钮点击监听事件
 */
function historyListen(){
	$(".btn-slide").click(function() {
		$("#panela").slideToggle("slow");
		$(this).toggleClass("active");
		return false;
	})
}

/**
 * 状态格式化
 * @param val
 * @param row
 * @param index
 */
function formatSta(val, row, index){
	//状态   0：待提交；1：待确认；2：待审批；3：审批通过；4：已驳回；
	if(val == 0)
		return "待提交";
	if(val == 1)
		return "待确认";
	if(val == 2)
		return "待审批";
	if(val == 3)
		return "审批通过";
	if(val == 4)
		return "已驳回";
}

/**
 * 列操作格式化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function opermatter(val, row, index) {
	if(row.istatus == 2){
		return '<a href="#" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="audit(' + index + ')">审批</a>';
	}
}

/**
 * 查询框-清空
 */
function clearParams(){
	$('#manager').textbox("setValue",null);
	$('#managerid').val(null);
	$('#qcorp').textbox("setValue",null);
	$('#qcorpid').val(null);
}

/**
 * 查询框-确认
 */
function reloadData(){
	url = contextPath + '/rebate/rebateinput!query.action';
	$('#grid').datagrid('options').url = url;
	var season =  isEmpty($("#qjd").combobox("getValue")) ? -1 : $("#qjd").combobox("getValue");
	$('#grid').datagrid('load', {
		'year' : $("#qyear").combobox("getValue"),
		'season' : season,
		'destatus' : $("#qstatus").combobox("getValue"),
		'mid' : $("#managerid").val(),
		'cpid' : $("#qcorpid").val(),
	});
	var qyear = $("#qyear").combobox("getValue");
	var qjd = $("#qjd").combobox("getText");
	if(!isEmpty(qjd)){
		$("#jqj").html(qyear+"年-"+qjd);
	}else{
		$("#jqj").html(qyear+"年");
	}
	$("#qrydialog").hide();
}

/**
 * 快捷查询
 * @param type
 */
function qryData(type){
	url = contextPath + '/rebate/rebateinput!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'qtype' : type,
	});
}

/**
 * 快速过滤
 */
function fastQry(){
	$('#filter_value').textbox('textbox').keydown(function (e) {
		 if (e.keyCode == 13) {
            var filtername = $("#filter_value").val(); 
            if(!isEmpty(filtername)){
            	var queryParams = $('#grid').datagrid('options').queryParams;
            	var rows = $('#grid').datagrid('getRows');
            	clearQryParam(queryParams);
            	if(rows != null && rows.length > 0){
            		//做过查询
            		queryParams.destatus = $("#qstatus").combobox("getValue");
            		queryParams.mid = $("#managerid").val();
            		queryParams.cpid = $("#qcorpid").val();
            	}
            	queryParams.year = $("#qyear").combobox("getValue");
        		queryParams.season = isEmpty($("#qjd").combobox("getValue")) ? -1 : $("#qjd").combobox("getValue");
            	queryParams.cpname = filtername;
          		grid.datagrid('options').url = contextPath + '/rebate/rebateinput!query.action';
          		$('#grid').datagrid('options').queryParams = queryParams;
          		$('#grid').datagrid('reload');
            }
         }
   });
}

/**
 * 清除查询传递查询条件
 * @param queryParams
 */
function clearQryParam(queryParams){
	queryParams.year = null;
	queryParams.season = -1;
	queryParams.qtype = -1;
	queryParams.destatus = -3;
	queryParams.cpid = null;
	queryParams.uid = null;
}

/**
 * 查询框-取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 审核
 * @param index
 */
function audit(index){
	var row = $('#grid').datagrid('getData').rows[index];
	if (row.istatus != 2) {
		Public.tips({
			content : '该记录不是待审批状态，不允许审批',
			type : 2
		});
		return;
	}
	confIndex = index;
	showAuditDlg(row);
}

/**
 * 展示审核对话框
 * @param row
 */
function showAuditDlg(row){
	$.ajax({
		url : DZF.contextPath + "/rebate/rebateinput!queryInfoById.action",
		dataType : 'json',
		data : row,
		success : function(rs) {
			if (rs.success) {
				$('#auditDlg').dialog({
					modal:true
				});//设置dig属性
				$('#auditDlg').dialog('open').dialog('center').dialog('setTitle','返点单审批');
				var row = rs.rows;
				$('#auditForm').form('clear');
				$('#auditForm').form('load', row);
				$('#apprnote').textbox('setValue',null);
				$('#commitForm').form('clear');
				$('#crebid').val(row.rebid);
				$('#ctstp').val(row.tstp);
				$('#cistatus').val(row.istatus);
				$('#cvcode').val(row.vcode);
				$('#ccorpid').val(row.corpid);
				$('#cdebitmny').val(row.debitmny);
				$('#cbasemny').val(row.basemny);
				$('#crebatemny').val(row.rebatemny);
				$('#cyear').val(row.year);
				$('#cseason').val(row.season);
				$(":radio[name='confstatus'][value='" + 1 + "']").prop("checked", "checked");
				$("#ahistory").empty();
				if(row.children != null && row.children.length > 0){
					var history = null;
					var info = "<p class='slideA'>"+
					"<a href='javascript:;' style='color:#FFF;font-size: 14px;' class='btn-slideA active'>审批历史</a>"+
					"</p>"+
					"<div style='height:230px;overflow:auto;'>"+
					"<div style='' id='panelA'>";
					if(row.children.length >= 1){
						history = row.children[0];
						info = info + "<div class='tall' style=' margin-top: 16px;'>"+
						"<div  class='Aroundly'>"+
						"<img src='../../images/tbpng_03.png' style='position: absolute; left: 90px;'/>"+
						"<img src='../../images/pngg_03.png' style='position: absolute; left: 96px; top: 14px;'/>"+
						"</div>"+
						"<div class='state'>"+
						"<div>"+
						"<font>"+history.sendtime+"</font>&emsp;<span>"+history.dealname+"</span>&emsp;<span>"+history.vsnote+"</span>"+
						"</div>";
						if(!isEmpty(history.pronote)){
							info = info +"<div>"+history.pronote+"</div>";
						}
						info = info + "</div>"+
						"</div>";
					}
					if(row.children.length > 1){
						info = info + "<div style='display: none;' id='panela'>"+
						"<div style='width:auto;'>";
						for(var i = 1; i < row.children.length; i++){
							history = row.children[i];
							info = info +"<div class='tall'>"+
							"<div  class='Aroundly'>"+
							"<img style='position: absolute; left: 92px;' src='../../images/xial_03.png' /> "+
							"<img style='position: absolute; left: 96px; top: 8px;' src='../../images/pngg_03.png' />"+
							"</div>"+
							"<div class='state'>"+
							"<div>"+
							"<font>"+history.sendtime+"</font>&emsp;<span>"+history.dealname+"</span>&emsp;<span>"+history.vsnote+"</span>"+
							"</div>";
							if(!isEmpty(history.pronote)){
								info = info +"<div>"+history.pronote+"</div>";
							}
							info = info + "</div>"+
							"</div>";
						}
						info = info +"</div>"+"</div>";
					}
					info = info +"<p class='slide'>"+
					"<a href='javascript:;' rel='external nofollow' class='btn-slide active'></a>"+
					"</p>"+
					"</div>"+
					"</div>";
					$("#ahistory").append(info);
					historyListen();
                }
				
			} else {
				Public.tips({
					content : rs.msg,
					type : 1
				});
			}
		},
	});
}

/**
 * 审核-提交
 */
function onAudit(){
	var confstatus = $('input:radio[name="confstatus"]:checked').val();
	if(isEmpty(confstatus)){
		Public.tips({
			content : '请先选择操作方式',
			type : 2
		});			
		return;
	}
	if(confstatus == "1"){
		if(isEmpty($("#apprnote").val())){
			Public.tips({
				content : '驳回修改说明不能为空',
				type : 1
			});			
			return;
		}
		onAuditCommit(confstatus);
	}else{
		$.messager.confirm("提示", "&emsp;&emsp;确认后，返点金额即冲入加</br>" +
				"盟商的可用余额中，且不能反审</br>" +
				"&emsp;&emsp;&nbsp;&nbsp;批，请谨慎操作！", function(r) {
			if (r) {
				onAuditCommit(confstatus);
			}
		});
	}
	
	
}

/**
 * 审核-提交数据
 */
function onAuditCommit(confstatus){
	var postdata = new Object();
	if($("#commitForm").form('validate')){
		postdata["data"] = JSON.stringify(serializeObject($('#commitForm')));
		postdata["opertype"] = confstatus;
	} else {
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return; 
	}
	$.messager.progress({
		text : '数据保存中，请稍候.....'
	});
	$('#commitForm').form('submit', {
		url : contextPath + '/rebate/rebateaudit!updateAudit.action',
		queryParams : postdata,
		success : function(result) {
			var result = eval('(' + result + ')');
			$.messager.progress('close');
			if (result.success) {
				Public.tips({
					content : result.msg,
					type : 0
				})
				var row = result.rows;
				$('#auditDlg').dialog('close');
				$('#grid').datagrid('updateRow', {
					index : confIndex,
					row : row
				});
			} else {
				Public.tips({
					content : result.msg,
					type : 1
				})
			}
		}
	});
}

/**
 * 导出
 */
function onExport(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'请选择需导出的数据',type:2});
		return;
	}
	var columns = $('#grid').datagrid("options").columns[0];
	Business.getFile(DZF.contextPath+ '/rebate/rebateinput!onExport.action',{
		'strlist':JSON.stringify(datarows),'printype' : 1,}, true, true);
}

