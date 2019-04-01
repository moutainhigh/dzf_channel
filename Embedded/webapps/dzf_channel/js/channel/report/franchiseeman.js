var contextPath = DZF.contextPath;
var id='#gridh';

$(function() {
	initQry();
	load();
	quickfiltet();
	initDetailGrid();
	initWshGrid();
	initYbhGrid();
	initTabs();
});

//初始化
function initQry(){
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	queryBoxChange('#bdate','#edate');
	$("#bdate").datebox("setValue", parent.SYSTEM.LoginDate.substring(0,7)+"-01");
	$("#edate").datebox("setValue",parent.SYSTEM.LoginDate);
	$("#jqj").html(parent.SYSTEM.LoginDate.substring(0,7)+"-01"+" 至  "+parent.SYSTEM.LoginDate);
	changeArea();
	changeProvince();
	initArea({"qtype" :1});
	initProvince({"qtype" :1});
	initManager({"qtype" :1});
}

function initArea(queryData){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryArea.action',
		data : queryData,
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#aname').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

function changeArea(){
	 $("#aname").combobox({
		onChange : function(n, o) {
			var queryData={"qtype" :1};
			if(!isEmpty(n)){
				queryData={'aname' : n,"qtype" :1};
				$('#ovince').combobox('setValue',null);
				$('#cuid').combobox('setValue',null);
			}
			initProvince(queryData);
			initManager(queryData);
		}
	});
}

function changeProvince(){
	 $("#ovince").combobox({
		onChange : function(n, o) {
			var queryData={"qtype" :1};
			if(!isEmpty(n)){
				queryData={'aname' : $("#aname").combobox('getValue'),'ovince':n,"qtype" :1};
				$('#cuid').combobox('setValue',null);
			}
			initManager(queryData);
		}
	});
}

//查询框关闭事件
function closeCx() {
	$("#qrydialog").css("visibility", "hidden");
}

// 清空查询条件
function clearCondition(){
	$('#aname').combobox('select',null);
	$('#ovince').combobox('select',null);
	$('#cuid').combobox('select',null);
}
// 重新加载数据
function reloadData() {
	var queryParams =new Array();
	var aname=$('#aname').combobox('getValue')
	if(aname!=null&&aname!=""){
		queryParams['aname'] = $('#aname').combobox('getValue');
	}
	var ovince=$('#ovince').combobox('getValue')
	if(ovince!=null&&ovince!=""){
		queryParams['ovince'] = $('#ovince').combobox('getValue');
	}
	var cuid=$('#cuid').combobox('getValue')
	if(cuid!=null&&cuid!=""){
		queryParams['cuid'] = $('#cuid').combobox('getValue');
	}
	queryParams['bdate'] = $('#bdate').datebox('getValue');
	queryParams['edate'] = $('#edate').datebox('getValue');
	queryParams['type'] = 3;
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('options').url = DZF.contextPath +'/report/manager!query.action';
	$("#grid").datagrid('reload');
	$('#grid').datagrid('unselectAll');
	$("#qrydialog").css("visibility", "hidden");
	status = "brows";
}

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
			"type":3
		},
		showFooter:true,
		columns : [ [ 
		    {width : '130',title : '大区',field : 'aname',align:'left',rowspan:2}, 
		    {width : '100',title : '区总',field : 'uname',align:'left',rowspan:2}, 
		    {width : '110',title : '省（市）',field : 'provname',align:'left',rowspan:2}, 
		 	{width : '100',title : '渠道经理',field : 'cuname',align:'left',rowspan:2}, 
			{width : '250',title : '加盟商',field : 'corpnm',align:'left',rowspan:2,
				formatter : function(value, row, index) {
					if(value == undefined){
						return;
					}else if(value=="合计"){
						return "合计";
					}else{
						return "<a href='javascript:void(0)' style='color:blue' onclick=\"qryDetail('"+row.corpid+"','"+row.corpnm+"')\">" + value + "</a>";
					}
			}}, 
			{width : '70',title : '小规模',field : 'xgmNum',align:'right',rowspan:2}, 
			{width : '70',title : '一般人',field : 'ybrNum',align:'right',rowspan:2}, 
			{width : '90',title : '保证金',field : 'bondmny',align:'right',rowspan:2,
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
		    {width : '100',title : '合同代账费',field : 'ntlmny',align:'right',colspan:2},
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
			 	{width : '80',title : '续费',field : 'rnum',align:'right'}, 
			  	{width : '80',title : '新增',field : 'anum',align:'right'}, 
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
			insertData(data);
			
			for(var i = 0;i<data.rows.length;i++){
				if(data.rows[i].provname){
					if(data.rows[i].provname.substr(data.rows[i].provname.length -1,1) ==  "省" || data.rows[i].provname.substr(data.rows[i].provname.length -1,1) ==  "市" ){
						
						/*$(".datagrid-view2 .datagrid-body tr").eq(i).css("background","#bbceef")*/
					}else if(data.rows[i].provname.substr(data.rows[i].provname.length -1,1) ==  "计" ){
						$(".datagrid-view2 .datagrid-body tr").eq(i).css("background","#d3dbe9")
					}
				}else if(data.rows[i].aname.substr(data.rows[i].aname.length -2,2) == "合计"){
					
					$(".datagrid-view2 .datagrid-body tr").eq(i).css("background","#a9b9d5")
				}
			}
			
			
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
		     anum1 =data.rows[i].anum;	
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
		     anum2 =data.rows[i].anum;	
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
	        	var rows = $('#grid').datagrid('getRows');
	        	if(rows != null && rows.length > 0){
	        		var aname = isEmpty($('#aname').combobox('getValue')) ? null : $('#aname').combobox('getValue');
	        		var ovince = isEmpty($('#ovince').combobox('getValue')) ? -1 : $('#ovince').combobox('getValue');
	        		var cuid = isEmpty($('#cuid').combobox('getValue')) ? null : $('#cuid').combobox('getValue');
	        		$('#grid').datagrid('load', {
	        			"corpnm": filtername,
	        			"bdate": $('#bdate').datebox('getValue'),
	        			"edate": $('#edate').datebox('getValue'),
	        			"type":3,
	        			"aname": aname,
	        			"ovince": ovince,
	        			"cuid": cuid,
	        		});
	        	}else{
	        		$('#grid').datagrid('load', {
	        			"corpnm": filtername,
	        			"bdate": $('#bdate').datebox('getValue'),
	        			"edate": $('#bdate').datebox('getValue'),
	        			"type":3,
	        		});
	        	}
	        }else{
	        	reloadData();
	        } 
        }
  });
}
