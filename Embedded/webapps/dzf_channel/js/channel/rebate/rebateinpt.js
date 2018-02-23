var contextPath = DZF.contextPath;
var grid;

$(function(){
	load();
	initRef();
});

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
	
	//加盟商参照初始化
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
		if(rowTable.length>300){
			Public.tips({content : "一次最多只能选择300个客户!" ,type:2});
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
		frozenColumns :[[{ field : 'ck', checkbox : true },
			              { field : 'operate', title : '操作列',width :'150',halign: 'center',align:'center',formatter:opermatter} ,
		               ]],
		columns : [ [ 
		              { field : 'operdate', title : '录入日期',width :'110',halign: 'center',align:'center' }, 
		              { field : 'vcode', title : '返点单号',width :'120',halign: 'center',align:'left' },
		              { field : 'aname', title : '大区',width :'130',halign: 'center',align:'left'}, 
		              { field : 'provname', title : '省(市)',width :'110',halign: 'center',align:'left'} ,
		              { field : 'mname', title : '渠道经理',width :'115',halign: 'center',align:'left'} ,
		              { field : 'corpcode', title : '加盟商编码',width :'115',halign: 'center',align:'left'} ,
		              { field : 'corpnm', title : '加盟商名称',width :'160',halign: 'center',align:'left'}, 
		              { field : 'period', title : '返点所属期间',width :'115',halign: 'center',align:'center'} ,
		              { field : 'debitmny', title : '扣款金额',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'basemny', title : '返点基数',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'rebatemny', title : '返点金额',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'status', title : '状态',width :'100',halign: 'center',align:'center'} ,
		              { field : 'memo', title : '说明',width :'180',halign: 'center',align:'left'} ,
				      { field : 'rebid', title : '主键', hidden:true}
		] ],
		onLoadSuccess : function(data) {
			$('#grid').datagrid("scrollTo",0);
		}
	});
}

/**
 * 列操作格式化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function opermatter(val, row, index) {
	return '<a href="#" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="nsignVisit(' + index + ')">修改</a>|'
	     +' <a href="#" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="caneclProtect(' + index + ')">删除</a>';
}

/**
 * 新增
 */
function onAdd(){
	$('#addDlg').dialog({
		modal:true
	});//设置dig属性
	$('#addDlg').dialog('open').dialog('center').dialog('setTitle','返点单编辑');
}

/**
 * 修改
 */
function onEdit(){
	$('#addDlg').dialog({
		modal:true
	});//设置dig属性
	$('#addDlg').dialog('open').dialog('center').dialog('setTitle','返点单查看');
	
}

/**
 * 提交
 */
function onCommit(){
	
}

/**
 * 删除
 */
function onDelete(){
	
}

/**
 * 导入
 */
function onImport(){
	
}

/**
 * 导出
 */
function onExport(){
	
}


