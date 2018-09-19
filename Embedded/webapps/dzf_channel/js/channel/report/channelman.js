var contextPath = DZF.contextPath;
var id='#gridh';

$(function() {
	$("#bdate").datebox("setValue", parent.SYSTEM.LoginDate.substring(0,7)+"-01");
	$("#edate").datebox("setValue",parent.SYSTEM.LoginDate);
	load();
	quickfiltet();
	initDetailGrid();
	initWshGrid();
	initTabs();
});

function load() {
	// 列表显示的字段
	$('#grid').datagrid({
		url : DZF.contextPath + '/report/manager!query.action',
		fit : false,
		rownumbers : true,
		height : Public.setGrid().h,
		width:'100%',
		singleSelect : true,
		queryParams: {
			bdate: $('#bdate').datebox('getValue'),
			edate: $('#edate').datebox('getValue'),
			type:1
		},
		showFooter:true,
		columns : [ [
		    {width : '110',title : '省（市）',field : 'provname',align:'left',rowspan:2}, 
		    {width : '160',title : '渠道经理',field : 'cuname',align:'left',rowspan:2}, 
			{width : '260',title : '加盟商',field : 'corpnm',align:'left',rowspan:2,
				formatter : function(value, row, index) {
					if(value == undefined){
						return;
					}else if(value=="合计"){
						return "合计";
					}else{
						return "<a href='javascript:void(0)' style='color:blue' onclick=\"qryChnDetail('"+row.corpid+"','"+row.corpnm+"')\">" + value + "</a>";
					}
			}}, 
			{width : '60',title : '小规模',field : 'xgmNum',align:'right',rowspan:2}, 
			{width : '60',title : '一般人',field : 'ybrNum',align:'right',rowspan:2}, 
			{width : '100',title : '保证金',field : 'bondmny',align:'right',rowspan:2,
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},  
			{width : '100',title : '预存款余额',field : 'outmny',align:'right',rowspan:2,
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
	  	  	{width : '100',title : '本期预存款',field : 'predeposit',align:'right',rowspan:2,
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
		  	{width : '80',title : '提单量',field : 'num',align:'right',colspan:2}, 
		  	{width : '100',title : '合同代账费',field : 'ntlmny',align:'right',colspan:2,
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
		 	{width : '100',title : '客单价',field : 'uprice',align:'right',rowspan:2,
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
		    {width : '100',title : '预付款扣款',field : 'ndemny',align:'right',rowspan:2,
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
			}},
			{width : '100',title : '返点扣款',field : 'nderebmny',align:'right',rowspan:2,
		    	formatter : function(value,row,index){
		    		if(value == 0)return "0.00";
		    		return formatMny(value);
				}}],[
					 	{width : '60',title : '续费',field : 'rnum',align:'right'}, 
					  	{width : '60',title : '新增',field : 'anum',align:'right'}, 
					 	{width : '100',title : '续费',field : 'rntlmny',align:'right',
					    	formatter : function(value,row,index){
					    		if(value == 0)return "0.00";
					    		return formatMny(value);
						}},
					 	{width : '100',title : '新增',field : 'antlmny',align:'right',
					    	formatter : function(value,row,index){
					    		if(value == 0)return "0.00";
					    		return formatMny(value);
						}},
					    ]
					],
		onLoadSuccess : function(data) {
			mergeCell(data,this);
			setFooter();
		}
	});
}

function reloadData(){
	$('#grid').datagrid('load', {
		bdate: $('#bdate').datebox('getValue'),
		edate: $('#edate').datebox('getValue'),
		"type":1
	});
}

/**
 * 快速过滤
 */
function quickfiltet(){
	$('#filter_value').textbox('textbox').keydown(function (e) {
		 if (e.keyCode == 13) {
            var filtername = $("#filter_value").val(); 
            if (filtername != "") {
            	var url = DZF.contextPath +'/report/manager!query.action';
            	$('#grid').datagrid('options').url = url;
            	$('#grid').datagrid('loadData',{ total:0, rows:[]});
            	$('#grid').datagrid('load', {
            		bdate: $('#bdate').datebox('getValue'),
        			edate: $('#edate').datebox('getValue'),
            		"corpnm" :filtername,
            		"type":1
            	});
            }else{
            	load();
            } 
         }
   });
}

/**
 * 初始化未审核的合同明细
 */
function initWshGrid(){
	 $('#gridw').datagrid({
			border : true,
			striped : true,
			rownumbers : true,
			fitColumns : true,
			/*height : Public.setGrid().h,*/
			height:'350',
			singleSelect : true,
			showFooter:true,
			columns : [ [ {
				width : '100',
				title : '提单日期',
				align:'center',
				halign:'center',
				field : 'edate',
			}, {
				width : '80',
				title : '提单量',
				align:'center',
				halign:'center',
				field : 'anum',
			}, {
				width : '180',
				title : '合同编码',
	            halign:'left',
				field : 'vccode',
				formatter :useFormat,
			},{
				width : '120',
				title : '合同代账费',
				align:'right',
	            halign:'center',
				field : 'antlmny',
				formatter : function(value,row,index){
					if(value == 0)return "0.00";
					return formatMny(value);
				}
			}] ],
			onLoadSuccess : function(data) {
				var rows = $('#gridh').datagrid('getRows');
				var footerData = new Object();
	            var anum = parseFloat(0);	
	            var antlmny = parseFloat(0);	
	            for (var i = 0; i < rows.length; i++) {
	            	anum += getFloatValue(rows[i].anum);
	            	antlmny += getFloatValue(rows[i].antlmny);
	            }
	            footerData['edate'] = '合计';
	            footerData['anum'] = anum;
	            footerData['antlmny'] = antlmny;
	            var fs=new Array(1);
	            fs[0] = footerData;
	            $('#gridw').datagrid('reloadFooter',fs);
	            $('#gridw').datagrid("scrollTo",0);
			},
		});
}

function initTabs(){
	var bdate = $('#bdate').datebox('getValue');
	var edate = $('#edate').datebox('getValue');
	$('#detail').tabs({
	    border:false,
	    onSelect:function(title){
			if("已审核" == title){
				id='#gridh';
				$('#gridh').datagrid('options').url = contextPath + '/report/manager!queryDetail.action';
				$('#gridh').datagrid('load', {"corpid":corpid,"bdate":bdate,"edate":edate});
			}else if("未审核" == title){
				id='#gridw';
				$('#gridw').datagrid('options').url = contextPath + '/report/manager!queryWDetail.action';
				$('#gridw').datagrid('load', {"corpid":corpid,"bdate":bdate,"edate":edate});
			}
	    }
	});
}

function qryChnDetail(cid,corpnm){
	var bdate = $("#bdate").datebox("getValue");
	var edate = $("#edate").datebox("getValue");
	var	qrydate = bdate + "至" + edate;
	corpid=cid;
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath +'/report/manager!queryDetail.action',
		data : {
			"corpid" : corpid,
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
				$('#corpnm').html(corpnm);
				$('#qrydate').html(qrydate);
				$('#gridh').datagrid('loadData',res);
				$('#detail_dialog').dialog('open');
				
				$('#gridh').datagrid('resize',{ 
					height : '350',
					width : '90%'
				});
			}
		}
	});
}
