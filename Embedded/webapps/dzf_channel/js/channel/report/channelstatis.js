var contextPath = DZF.contextPath;

$(function() {
	initQry();
	load();
	initDetailGrid();
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
		    {width : '140', title: '渠道经理id', field: 'uid', hidden: true},
		 	{width : '180',title : '加盟商id',field : 'corpid',hidden: true}, 
		 	{width : '180',title : '渠道经理',field : 'uname',align:'left'}, 
		 	{width : '180',title : '加盟商编码',field : 'vccode',align:'left'}, 
			{width : '280',title : '加盟商名称',field : 'corpnm',align:'left',
				formatter : function(value, row, index) {
					if(value == undefined){
						return;
					}else if(value=="合计"){
						return "合计";
					}else{
						return "<a href='javascript:void(0)' style='color:blue' onclick=\"qryDetail('"+row.corpid+"','"+row.uid+"','"+row.corpnm+"')\">" + value + "</a>";
					}
			}}, 
		    {width : '180',title : '预付款扣款',field : 'ndemny',align:'right',
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
			{width : '180',title : '返点扣款',field : 'nderebmny',align:'right',
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

/**
 * 明细列表初始化
 */
function initDetailGrid(){
	gridh = $('#gridh').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : true,
		/*height : Public.setGrid().h,*/
		height:'350',
		singleSelect : true,
		showFooter:true,
		columns : [ [ {
			width : '90',
			title : '日期',
			align:'center',
			halign:'center',
			field : 'edate',
		}, {
			width : '60',
			title : '提单量',
			align:'center',
			halign:'center',
			field : 'anum',
		}, {
			width : '160',
			title : '合同编码',
            halign:'left',
			field : 'vccode',
			formatter :useFormat,
		},{
			
			width : '100',
			title : '合同代账费',
			align:'right',
            halign:'center',
			field : 'antlmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '100',
			title : '预付款扣款',
			align:'right',
            halign:'center',
			field : 'ndemny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},{
			width : '100',
			title : '返点扣款',
			align:'right',
            halign:'center',
			field : 'nderebmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			}
		},] ],
		onLoadSuccess : function(data) {
			var rows = $('#gridh').datagrid('getRows');
			var footerData = new Object();
            var anum = parseFloat(0);	
            var antlmny = parseFloat(0);	
            var ndemny = parseFloat(0);	
            var nderebmny = parseFloat(0);	
            for (var i = 0; i < rows.length; i++) {
            	anum += getFloatValue(rows[i].anum);
            	antlmny += getFloatValue(rows[i].antlmny);
            	ndemny += getFloatValue(rows[i].ndemny);
            	nderebmny += getFloatValue(rows[i].nderebmny);
            }
            footerData['edate'] = '合计';
            footerData['anum'] = anum;
            footerData['antlmny'] = antlmny;
            footerData['ndemny'] = ndemny;
            footerData['nderebmny'] = nderebmny;
            var fs=new Array(1);
            fs[0] = footerData;
            $('#gridh').datagrid('reloadFooter',fs);
            $('#gridh').datagrid("scrollTo",0);
		},
	});
}

/**
 * 扣款金额格式化
 * @param value
 * @param row
 * @param index
 * @returns
 */
function useFormat(value,row,index){
	if(row.edate != "合计"){
		var url = 'channel/contract/contractconfrim.jsp?operate=tocont&pk_billid='+row.corpid;
		var ss = "<a href='javascript:void(0)' style='color:blue' onclick=\"parent.addTabNew('合同审核','"+url+"');\">"+value+"</a>";
		return ss ;
	}
}

function qryDetail(cid,uid,corpnm){
	var bdate = $("#bdate").datebox("getValue");
	var edate = $("#edate").datebox("getValue");
	if(isEmpty(uid)){
		uid = null;
	}
	var	qrydate = bdate + "至" + edate;
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath +'/report/channelStatis!queryDetail.action',
		data : {
			"corpid" : cid,
			"cuid" : uid,
			"bdate" : bdate,
			"edate" : edate,
		},
		traditional : true,
		async : false,
		success : function(data, textStatus) {
			if (!data.success) {
				Public.tips({
					content : data.msg,
					type : 1
				});
			} else {
				var res = data.rows;
				if (isEmpty(res)) {
					$('#detail_dialog').dialog('close');
					Public.tips({
						content : "无明细记录",
						type : 2
					});
					return;
				}
				$('#corpnm').html(corpnm);
				$('#qrydate').html(qrydate);
				$('#gridh').datagrid('loadData',res);
				$('#detail_dialog').dialog('open');
			}
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
			j++;
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
	if(data.rows!=null && data.rows.length>0){
		$('#grid').datagrid('insertRow',{
			index: data.rows.length,
			row: {
				uid: '',
				uname: '合计',
				ndemny	:	sndemny,
				nderebmny 	:	snderebmny ,
			}
		});
		$(".datagrid-view2 .datagrid-body tr").eq(i).css("background","#d3dbe9")
	}
}

