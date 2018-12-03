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
//			insertData(data);
		}
	});
}


function insertData(data){
	var mark=1;
	
    var bondmny = 0;	
    var predeposit = 0;	
    var xgmNum = 0;	
    var ybrNum = 0;	
    var rnum = 0;	
    var anum = 0;	
    var rntlmny = 0;	
    var antlmny = 0;
    var ndemny = 0;	
    var nderebmny = 0;	
    var outmny = 0;	
    
    var bondmny1 = 0;	
    var predeposit1 = 0;	
    var xgmNum1 = 0;	
    var ybrNum1 = 0;	
    var rnum1 = 0;	
    var anum1 = 0;	
    var rntlmny1 = 0;	
    var antlmny1 = 0;
    var ndemny1 = 0;	
    var nderebmny1 = 0;	
    var outmny1 = 0;	
    
    var bondmny2 = 0;	
    var predeposit2 = 0;	
    var xgmNum2 = 0;	
    var ybrNum2 = 0;	
    var rnum2 = 0;	
    var anum2 = 0;	
    var rntlmny2 = 0;	
    var antlmny2 = 0;
    var ndemny2 = 0;	
    var nderebmny2 = 0;	
    var outmny2 = 0;
    
    var j=0;
　　　for (var i=0; i <data.rows.length; i++) {
		var row=data.rows[i];
		bondmny += parseFloat(row.bondmny);
		predeposit += parseFloat(row.predeposit);
		xgmNum += parseFloat(row.xgmNum);
		ybrNum += parseFloat(row.ybrNum);
		rnum += parseFloat(row.rnum);
		anum += parseFloat(row.anum);
		rntlmny += parseFloat(row.rntlmny);
		antlmny += parseFloat(row.antlmny);
		ndemny += parseFloat(row.ndemny);
		nderebmny += parseFloat(row.nderebmny);
		outmny += parseFloat(row.outmny);
		
　　　　　　if (i!=0 && row.provname == data.rows[i-1].provname) {  
	　　　　　　　　mark += 1;                                            
	　　　　　　　　$('#grid').datagrid('mergeCells',{ 
	　　　　　　　　　　index: i+1-mark,                 
	　　　　　　　　　　field: 'uprice',              
	　　　　　　　　　　rowspan:mark                 
	　　　　　　　　}); 
　　　　　　}else{
　　　　　　　　mark=1;                                
　　　　　　}
		
		j=0;
		if(i!=0 && row.provname!=data.rows[i-1].provname){
			$('#grid').datagrid('insertRow',{
				index: i,	// index start with 0
				row: {
//					aname:data.rows[i-1].aname,
					provname: data.rows[i-1].provname+'小计',
					bondmny	:	bondmny1,
					predeposit 	:	predeposit1 ,
					xgmNum 	:	xgmNum1 ,
					ybrNum :		ybrNum1 ,
					rnum	:	rnum1,
					anum	:	anum1,
					rntlmny :		rntlmny1 ,
					antlmny :		antlmny1 ,
					ndemny 	:	ndemny1 ,
					nderebmny:		nderebmny1,
					outmny	:	outmny1  ,
				}
			});
		     i++;
		     j++;
		     bondmny1 = data.rows[i].bondmny;	
		     predeposit1 = data.rows[i].predeposit;
		     xgmNum1 =data.rows[i].xgmNum;
		     ybrNum1 =data.rows[i].ybrNum;
		     rnum1 = data.rows[i].rnum;
		     rntlmny1 =data.rows[i].rntlmny;	
		     rntlmny1 = data.rows[i].rntlmny;
		     antlmny1 =data.rows[i].antlmny;
		     ndemny1 = data.rows[i].ndemny;	
		     nderebmny1 = data.rows[i].nderebmny;	
		     outmny1 =data.rows[i].outmny;
		}else{
			bondmny1 += parseFloat(row.bondmny);
			predeposit1 += parseFloat(row.predeposit);
			xgmNum1 += parseFloat(row.xgmNum);
			ybrNum1 += parseFloat(row.ybrNum);
			rnum1 += parseFloat(row.rnum);
			anum1 += parseFloat(row.anum);
			rntlmny1 += parseFloat(row.rntlmny);
			antlmny1 += parseFloat(row.antlmny);
			ndemny1 += parseFloat(row.ndemny);
			nderebmny1 += parseFloat(row.nderebmny);
			outmny1 += parseFloat(row.outmny);
		}
		
		if(i!=0 && row.aname!=data.rows[i-1-j].aname){
			$('#grid').datagrid('insertRow',{
				index: i,	// index start with 0
				row: {
					aname: data.rows[i-2].aname+'合计',
					bondmny	:	bondmny2,
					predeposit 	:	predeposit2 ,
					xgmNum 	:	xgmNum2,
					ybrNum :		ybrNum2 ,
					rnum	:	rnum2,
					anum	:	anum2,
					rntlmny :		rntlmny2 ,
					antlmny :		antlmny2 ,
					ndemny 	:	ndemny2 ,
					nderebmny:		nderebmny2,
					outmny	:	outmny2  ,
				}
			});
			 i++;
		     bondmny2 = data.rows[i].bondmny;	
		     predeposit2 = data.rows[i].predeposit;
		     xgmNum2 =data.rows[i].xgmNum;
		     ybrNum2 =data.rows[i].ybrNum;
		     rnum2 = data.rows[i].rnum;
		     rntlmny2 =data.rows[i].rntlmny;	
		     rntlmny2 = data.rows[i].rntlmny;
		     antlmny2 =data.rows[i].antlmny;
		     ndemny2 = data.rows[i].ndemny;	
		     nderebmny2 = data.rows[i].nderebmny;	
		     outmny2 =data.rows[i].outmny;
		}else{
			bondmny2 += parseFloat(row.bondmny);
			predeposit2 += parseFloat(row.predeposit);
			xgmNum2 += parseFloat(row.xgmNum);
			ybrNum2 += parseFloat(row.ybrNum);
			rnum2 += parseFloat(row.rnum);
			anum2 += parseFloat(row.anum);
			rntlmny2 += parseFloat(row.rntlmny);
			antlmny2 += parseFloat(row.antlmny);
			ndemny2 += parseFloat(row.ndemny);
			nderebmny2 += parseFloat(row.nderebmny);
			outmny2 += parseFloat(row.outmny);
		}
　	}

	$('#grid').datagrid('appendRow',{
//			aname:data.rows[data.rows.length-1].aname,
			provname: data.rows[data.rows.length-1].provname+'小计',
			bondmny	:	bondmny1,
			predeposit 	:	predeposit1 ,
			xgmNum 	:	xgmNum1 ,
			ybrNum :		ybrNum1 ,
			rnum	:	rnum1,
			anum	:	anum1,
			rntlmny :		rntlmny1 ,
			antlmny :		antlmny1 ,
			ndemny 	:	ndemny1 ,
			nderebmny:		nderebmny1,
			outmny	:	outmny1  ,
	});

	$('#grid').datagrid('appendRow',{
			aname: data.rows[data.rows.length-2].aname==undefined?'无大区合计':data.rows[data.rows.length-2].aname+'合计',
			bondmny	:	bondmny2,
			predeposit 	:	predeposit2 ,
			xgmNum 	:	xgmNum2 ,
			ybrNum :		ybrNum2 ,
			rnum	:	rnum2,
			anum	:	anum2,
			rntlmny :		rntlmny2 ,
			antlmny :		antlmny2 ,
			ndemny 	:	ndemny2 ,
			nderebmny:		nderebmny2,
			outmny	:	outmny2  ,
	});
	
	var footerData = new Object();
	footerData['corpnm'] = '合计';
	footerData['bondmny'] = bondmny;
	footerData['predeposit'] = predeposit;
	footerData['xgmNum'] = xgmNum;
	footerData['ybrNum'] = ybrNum;
	footerData['rnum'] = rnum;
	footerData['anum'] = anum;
	footerData['rntlmny'] = rntlmny;
	footerData['antlmny'] = antlmny;
	footerData['ndemny'] = ndemny;
	footerData['nderebmny'] = nderebmny;
	footerData['outmny'] = outmny;
	var fs=new Array(1);
	fs[0] = footerData;
	$('#grid').datagrid('reloadFooter',fs);
}
