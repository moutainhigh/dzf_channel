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
	$("#jqj").html(qyear+"-"+qjd);
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
				content : "一次最多只能选择300个客户!",
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
		$("#" + refid).textbox("setValue",str);
		$("#" + refid + "id").val(corpIds);
	}
	$("#chnDlg").dialog('close');
}

/**
 * 列表表格初始化
 */
function load(){
	$('#grid').datagrid({
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
		frozenColumns :[[ { field : 'ck', checkbox : true },
			              { field : 'operate', title : '操作列',width :'150',halign: 'center',align:'center',formatter:opermatter} ,
		               ]],
		columns : [ [ 
		              { field : 'operdate', title : '录入日期',width :'110',halign: 'center',align:'center' }, 
		              { field : 'vcode', title : '返点单号',width :'120',halign: 'center',align:'left' },
		              { field : 'aname', title : '大区',width :'130',halign: 'center',align:'left'}, 
		              { field : 'provname', title : '省(市)',width :'110',halign: 'center',align:'left'} ,
		              { field : 'mname', title : '渠道经理',width :'115',halign: 'center',align:'left'} ,
		              { field : 'corpcode', title : '加盟商编码',width :'115',halign: 'center',align:'left'} ,
		              { field : 'corp', title : '加盟商名称',width :'160',halign: 'center',align:'left'}, 
		              { field : 'period', title : '返点所属期间',width :'115',halign: 'center',align:'center'} ,
		              { field : 'debitmny', title : '扣款金额',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'basemny', title : '返点基数',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'rebatemny', title : '返点金额',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'status', title : '状态',width :'100',halign: 'center',align:'center', formatter : formatSta} ,
		              { field : 'memo', title : '说明',width :'180',halign: 'center',align:'left'} ,
				      { field : 'rebid', title : '主键', hidden:true},
				      { field : 'tstp', title : '时间戳', hidden:true},
		] ],
		onLoadSuccess : function(data) {
			$('#grid').datagrid("scrollTo",0);
		}
	});
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
	return '<a href="#" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="onConf(' + index + ')">确认</a>|'
	     +' <a href="#" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="onCanc(' + index + ')">取消确认</a>';
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
	$('#grid').datagrid('load', {
		'year' : $("#qyear").combobox("getValue"),
		'season' : $("#qjd").combobox("getValue"),
		'destatus' : $("#qstatus").combobox("getValue"),
		'uid' : $("#managerid").val(),
		'cpid' : $("#qcorpid").val(),
	});
	var qyear = $("#qyear").combobox("getValue");
	var qjd = $("#qjd").combobox("getText");
	$("#jqj").html(qyear+"-"+qjd);
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
            		queryParams.cpid = $("#managerid").val();
            		queryParams.uid = $("#qcorpid").val();
            	}
            	queryParams.year = $("#qyear").combobox("getValue");
        		queryParams.season = $("#qjd").combobox("getValue");
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
	queryParams.season = null;
	queryParams.qtype = -1;
	queryParams.destatus = -1;
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
 * 确认
 * @param index
 */
function onConf(index){
	var row = $('#grid').datagrid('getData').rows[index];
	if (row.status != 1) {
		Public.tips({
			content : '该记录不是待确认状态，不允许删除',
			type : 2
		});
		return;
	}
	$.messager.confirm("提示", "你确定要删除吗?", function(r) {
		if (r) {
			$.ajax({
				url : DZF.contextPath + "/rebate/rebateinput!delete.action",
				dataType : 'json',
				data : row,
				success : function(rs) {
					if (rs.success) {
						$('#grid').datagrid('deleteRow', index); 
						$("#grid").datagrid('unselectAll');
						Public.tips({
							content : rs.msg,
							type : 0
						});
					} else {
						Public.tips({
							content : rs.msg,
							type : 1
						});
					}
				},
			});
		}
	});
}

/**
 * 确认-提交
 */
function onConfirm(){
	
}

/**
 * 取消确认
 * @param index
 */
function onCanc(index){
	
}