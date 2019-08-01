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
		 onSelect  : function() {
			var queryData={"qtype" :1};
			var aname = $("#aname").combobox('getValue');
			queryData={'aname' : aname,"qtype" :1};
			$('#ovince').combobox('setValue',null);
			$('#cuid').combobox('setValue',null);
			initProvince(queryData);
			initManager(queryData);
		}
	});
}

function changeProvince(){
	 $("#ovince").combobox({
		 onSelect  : function() {
			var queryData={"qtype" :1};
			var ovince;
			if(isEmpty(ovince)){
				ovince = -1;
			}else{
				ovince = $("#ovince").combobox("getValue");
			}
			var aname = $("#aname").combobox('getValue');
			queryData={'aname' : ovince,'ovince':ovince,"qtype" :1};
			$('#cuid').combobox('setValue',null);
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

	$('#isncust').combobox('setValue',"N");
	$('#comptype').combobox('setValue','-1');
	$('#chantype').combobox('setValue','-1');
}
// 重新加载数据
function reloadData() {
	var queryParams =new Array();

	var aname=$('#aname').combobox('getValue')
	if(!isEmpty(aname)){
		queryParams['aname'] = aname;
	}
	var ovince=$('#ovince').combobox('getValue');
	if(!isEmpty(ovince)){
		queryParams['ovince'] = ovince;
	}

	queryParams['cuid'] =$('#cuid').combobox('getValue');
	queryParams['bdate'] = $('#bdate').datebox('getValue');
	queryParams['edate'] = $('#edate').datebox('getValue');
	queryParams['type'] = 3;

	queryParams['isncust'] = $('#isncust').combobox('getValue');
	queryParams['comptype'] = $('#comptype').combobox('getValue');
	queryParams['chantype'] = $('#chantype').combobox('getValue');

	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('options').url = DZF.contextPath +'/report/franchiseeman!query.action';
	$("#grid").datagrid('reload');
	$('#grid').datagrid('unselectAll');
	$("#qrydialog").css("visibility", "hidden");
	status = "brows";
}

function load() {
	// 列表显示的字段
	$('#grid').datagrid({
		url : DZF.contextPath + '/report/franchiseeman!query.action',
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
						value= "<a href='javascript:void(0)' style='color:#0000ff' onclick=\"qryDetail('"+row.corpid+"','"+row.corpnm+"')\">" + value + "</a>";
		            	if (!isEmpty(row.dreldate)) {
		            		return "<div style='position: relative;'>" + value + "<img style='right: 0; position: absolute;' src='../../images/rescission.png' /></div>"
		            	}else{
		            		return value
		            	}
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
			{width : '100',title : '返点余额',field : 'retmny',align:'right',rowspan:2,
		    	formatter : function(value,row,index){
		    		if(isEmpty(value))return "0.00";
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
			var retmny = 0;
			var row;
			for(var i = 0;i<data.rows.length;i++){
				row=data.rows[i];
				
				if(isEmpty(row.corpid) && !isEmpty(row.provname)){
					$(".datagrid-view2 .datagrid-body tr").eq(i).css("background","#d3dbe9")
				}else if(isEmpty(row.corpid)){
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
					retmny += parseFloat(isEmpty(row.retmny) ? 0 : row.retmny);
					
					$(".datagrid-view2 .datagrid-body tr").eq(i).css("background","#a9b9d5")
				}
				/*$(".datagrid-view2 .datagrid-body tr").eq(i).css("background","#bbceef")*/
			}
			
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
			footerData['retmny'] = retmny;
			var fs=new Array(1);
			fs[0] = footerData;
			$('#grid').datagrid('reloadFooter',fs);
		}
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
	        	var url = DZF.contextPath +'/report/franchiseeman!query.action';
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
