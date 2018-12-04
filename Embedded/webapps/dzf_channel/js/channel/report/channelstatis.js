var contextPath = DZF.contextPath;

$(function() {
	initQry();
	load();
	reloadData();
});

//初始化
function initQry(){
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	queryBoxChange('#bdate','#edate');
	$("#bdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#edate").datebox("setValue",parent.SYSTEM.LoginDate);
	$("#jqj").html(parent.SYSTEM.PreDate+" 至  "+parent.SYSTEM.LoginDate);
	initUser();
	initChannel();
}

function initUser(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/sys/chnUseract!queryCombobox.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#uid').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
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
		$("#corpkid_ae").val(null);
		$("#corpkna_ae").textbox("setValue",null);
		if(!isEmpty(rowTable.length)&&rowTable.length==1){
			$('#corpkna_ae').textbox('readonly',false);
		}else{
			$('#corpkna_ae').textbox('readonly',true);
		}
		
		$("#channel_select").textbox("setValue",str);
		$("#pk_account").val(corpIds);
	}
	 $("#chnDlg").dialog('close');
}

//查询框关闭事件
function closeCx() {
	$("#qrydialog").css("visibility", "hidden");
}

// 清空查询条件
function clearCondition(){
	$("#pk_account").val(null);
	$("#channel_select").textbox("setValue",null);
	$('#uid').combobox('select',null);
}

// 重新加载数据
function reloadData() {
	var queryParams =new Array();
	queryParams['bdate'] = $('#bdate').datebox('getValue');
	queryParams['edate'] = $('#edate').datebox('getValue');
	queryParams['corpid'] = $("#pk_account").val();
	queryParams['uid'] = $('#uid').combobox('getValue');
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('options').url =contextPath +'/report/channelStatis!query.action';
	$("#grid").datagrid('reload');
	$('#grid').datagrid('unselectAll');
	$("#qrydialog").css("visibility", "hidden");
}

function load() {
	// 列表显示的字段
	$('#grid').datagrid({
		fit : false,
		rownumbers : true,
		height : Public.setGrid().h,
		width:'100%',
		singleSelect : true,
		showFooter:true,
		columns : [ [ 
		    {width: '140', title: '渠道经理id', field: 'uid', hidden: true},
		 	{width : '100',title : '渠道经理',field : 'uname',align:'left'}, 
		 	{width : '100',title : '加盟商编码',field : 'corpid',align:'left'}, 
			{width : '250',title : '加盟商名称',field : 'corpnm',align:'left'}, 
		    {width : '100',title : '预付款扣款',field : 'ndemny',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
			{width : '100',title : '返点扣款',field : 'nderebmny',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}}
		]],
		onLoadSuccess : function(data) {
			insertData(data);
		}
	});
}


function insertData(data){
    var sndemny = 0;	
    var snderebmny = 0;	
    var j=0;
    var row;
　　　for (var i=0; i <data.rows.length; i++) {
		row=data.rows[i];
		if(i==0 || (row.uid!=data.rows[i-1].uid && j==0)){
			sndemny=parseFloat(row.ndemny);
			snderebmny=parseFloat(row.nderebmny);
			j=0;
		}else if(row.uid == data.rows[i-1].uid){
			sndemny+= parseFloat(row.ndemny);
			snderebmny+= parseFloat(row.nderebmny);
			j++;
		}else if(row.uid!=data.rows[i-1].uid && j>0){
			$('#grid').datagrid('insertRow',{
				index: i,
				row: {
					uid: '',
					uname: '合计',
					ndemny	:	sndemny,
					nderebmny 	:	snderebmny ,
				}
			});
			$(".datagrid-view2 .datagrid-body tr").eq(i).css("background","#d3dbe9")
			j=0;
		}
　	}
}
