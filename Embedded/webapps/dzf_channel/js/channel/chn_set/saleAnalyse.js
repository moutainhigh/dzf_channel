var contextPath = DZF.contextPath;
var loadrows;

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});

$(function() {
	changeArea();
	initArea({"qtype" :0});
	initProvince({"qtype" :0});
	initDate();
	load();
	quickfiltet();
});

function initArea(queryData){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath +  '/chn_set/chnarea!queryArea.action',
		dataTye : 'json',
		data : queryData,
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				var rows=result.rows;
				var all={'id':'全部','name':'全部'};
				rows.unshift(all);
			    $('#aname').combobox('loadData',rows);
			    $('#aname').combobox('select','全部');
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

function initProvince(queryData){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryProvince.action',
		dataTye : 'json',
		data : queryData,
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#ovince').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

function changeArea(){
	 $("#aname").combobox({
		onChange : function(n, o) {
			var queryData={"qtype" :3};
			if(n!='全部'){
				queryData={'aname' : n,"qtype" :3};
				$('#ovince').combobox('setValue',null);
			}
			initProvince(queryData);
		}
	});
}

function initDate(){
	$("#bdate").datebox('setValue',parent.SYSTEM.PreDate);
	$("#edate").datebox('setValue',parent.SYSTEM.LoginDate)
}

function load() {
	var bdate=$("#bdate").datebox('getValue');
	var edate=$("#edate").datebox('getValue')
	var queryData={"bdate" : bdate,"edate" : edate,};
	var aname=$("#aname").combobox('getValue');
	var aid=$("#aname").combobox('getText');
	if(!isEmpty(aname)&&!isEmpty(aid)&&aid!='全部'){
		queryData['aname']=aname;
	}
	var ovince=$("#ovince").combobox('getValue');
	if(!isEmpty(ovince)){
		queryData['ovince']=ovince;
	}
	// 列表显示的字段
	$('#grid').datagrid({
		url : DZF.contextPath + '/chn_set/saleAnalyse!query.action',
		rownumbers : true,
		striped : true,
		singleSelect : true,
		fitColumns:false,
		height : Public.setGrid().h,
		queryParams : queryData,
		remoteSort : false,
		showFooter:true,
//		sortName:"visnum",
//		sortOrder:"desc",
		columns : [ [	
		             	{width : '130',title : '大区',field : 'aname',halign: 'center',align:'left'}, 
		            	{width : '120',title : '省（市）',field : 'provname',halign: 'center',align:'left'}, 
		            	{width : '250',title : '加盟商',field : 'corpnm',halign: 'center',align:'left'},
		            	{ field : 'visnum', title : '拜访数',width :'120',halign: 'center',align:'center',sortable:true,sorter:orderfun },
			            { field : 'viscustnum', title : '拜访客户数',width :'120',halign: 'center',align:'center',sortable:true,sorter:orderfun },
			            { field : 'signum', title : '签约客户数',width :'120',halign: 'center',align:'center',sortable:true,sorter:orderfun}, 
			            { field : 'agentnum', title : '代账合同数',width :'120',halign: 'center',align:'center',sortable:true,sorter:orderfun} ,
			            { field : 'increnum', title : '增值合同数',width :'120',halign: 'center',align:'center',sortable:true,sorter:orderfun} ,
			            { field : 'contmny', title : '合同金额',width :'120',halign: 'center',align:'right',
			            	  sortable:true,formatter : formatMny,sorter:orderfun} ,
			            { field : 'pricemny', title : '客单价',width :'120',halign: 'center',align:'right',
			            	  sortable:true,formatter : formatMny,sorter:orderfun}, 
		          ] ],
		onLoadSuccess : function(data) {
			if(data.rows && loadrows == null){
				loadrows = data.rows;
			}
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
	var visnum = 0;
	var viscustnum = 0;
	var signum = 0;
	var agentnum = 0;
	var increnum = 0;
	var contmny = 0;
	var pricemny = 0;
	for (var i = 0; i < rows.length; i++) {
		if(rows[i].visnum != undefined && rows[i].visnum != null){
			visnum += parseFloat(rows[i].visnum);
		}
		if(rows[i].viscustnum != undefined && rows[i].viscustnum != null){
			viscustnum += parseFloat(rows[i].viscustnum);
		}
		if(rows[i].signum != undefined && rows[i].signum != null){
			signum += parseFloat(rows[i].signum);
		}
		if(rows[i].agentnum != undefined && rows[i].agentnum != null){
			agentnum += parseFloat(rows[i].agentnum);
		}
		
		if(rows[i].increnum != undefined && rows[i].increnum != null){
			increnum += parseFloat(rows[i].increnum);
		}
		if(rows[i].contmny != undefined && rows[i].contmny != null){
			contmny += parseFloat(rows[i].contmny);
		}
		if(rows[i].pricemny != undefined && rows[i].pricemny != null){
			pricemny += parseFloat(rows[i].pricemny);
		}
	}
	footerData['corpnm'] = '合计';
	footerData['visnum'] = visnum;
	footerData['viscustnum'] = viscustnum;
	footerData['signum'] = signum;
	footerData['agentnum'] = agentnum;
	footerData['increnum'] = increnum;
	footerData['contmny'] = contmny;
	footerData['pricemny'] = pricemny;
	var fs=new Array(1);
	fs[0] = footerData;
	$('#grid').datagrid('reloadFooter',fs);
}

function quickfiltet(){
	$('#quname').textbox('textbox').keydown(function (e) {
        if (e.keyCode == 13) {
        	$('#grid').datagrid('unselectAll');
 		   var filtername = $("#quname").val(); 
		   if (filtername) {
			   var rows = $('#grid').datagrid('getRows');
			   var bdate = $("#bdate").datebox('getValue');
			   var edate = $("#edate").datebox('getValue');
			   if(rows != null && rows.length > 0){
				   var aname = null;
				   var aid = $("#aname").combobox('getText');
				   if(!isEmpty(aname)&&!isEmpty(aid)&&aid!='全部'){
					   aname = $("#aname").combobox('getValue');
				   }
				   var ovince = $("#ovince").combobox('getValue');
				   if(isEmpty(ovince)){
					   ovince = -1;
				   }
					$('#grid').datagrid('load', {
				    	"bdate" : bdate,
						"edate" : edate,
						"corpnm" : filtername,
						"aname" : aname,
						"ovince" : ovince,
				    });
			   }else{
					$('#grid').datagrid('load', {
				    	"bdate" : bdate,
						"edate" : edate,
						"corpnm" : filtername,
				    });
			   }
			}else{
				load();
			} 
        }
    });
}

/**
 * 导出
 */
function doExport(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'当前界面数据为空',type:2});
		return;
	}
	var columns = $('#grid').datagrid("options").columns[0];
	var qj = $('#bdate').datebox('getValue') + "至" +  $('#edate').datebox('getValue') ;
	Business.getFile(contextPath+ '/chn_set/saleAnalyse!exportExcel.action',{'strlist':JSON.stringify(datarows),'qj':qj}, true, true);
}
