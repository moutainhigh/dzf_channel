var grid;

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});

$(function() {
	initPeriod("#qryperiod");
	initQry();
	load();
});

/**
 * 查询初始化
 */
function initQry(){
	$("#jqj").html($("#qryperiod").datebox('getValue'));
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	initQryCommbox();
	changeDate();
	initChannel();
	initDetailGrid();//明细表格初始化
}

/**
 * 查询期间改变事件
 */
function changeDate(){
	$("#qryperiod").datebox({
		onChange : function(n, o) {
			$("#jqj").html(n);
		}
	});
}

/**
 * 数据表格初始化
 */
function load(){
	var vince = $('#ovince').combobox('getValue');
	if (isEmpty(vince)) {
		vince = -1;
	}
	$('#grid').datagrid({
		url : DZF.contextPath + "/report/financedealstaterep!query.action",
		queryParams:{
			'period' : $('#qryperiod').datebox('getValue'),//查询期间
			'aname' : $('#aname').combobox('getValue'),
			'ovince' : vince,
			'uid' : $('#uid').combobox('getValue'),
			'corps' : $("#pk_account").val(),
		},
		striped : true,
		title : '',
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : true,
		pagination : true, //显示分页
		pageSize : 20, //默认20行
		pageList : [ 20, 50, 100, 200 ],
		showRefresh : false,// 不显示分页的刷新按钮
		showFooter : true,
		border : true,
		remoteSort:false,
		frozenColumns:[[
		                { field : 'corpid', title : '会计公司主键', hidden:true},
		                { field : 'aname', title : '大区', width : 60, halign:'center', align:'left', },
		                { field : 'uname', title : '区总', width : 90, halign:'center', align:'left', },
		                { field : 'provname', title : '省份', width : 140, halign:'center', align:'left', }, 
		                { field : 'incode', title : '加盟商编码', width : 120, halign:'center', align:'left', },
		                { field : 'corpnm', title : '加盟商名称', width:180, halign:'center', align:'left', formatter:nameFormat},
        ]],
		columns : [ 
		            [ 
					{ field : 'chndate', title : '加盟日期', width:100,halign:'center',align:'center',rowspan:2},
					{ field : 'cuname', title : '会计运营经理', width : 120, halign:'center', align:'left', rowspan:2},
					{ field : 'custsmall', title : '小规模数量', width:120, halign:'center', align:'right', rowspan:2, hidden:true},
					{ field : 'custtaxpay', title : '一般纳税人数量', width:120, halign:'center', align:'right', rowspan:2, hidden:true},
					
					{ field : 'smallnum', title : '小规模数量', halign:'center',align:'center',colspan:2},
		            { field : 'taxpaynum', title : '一般纳税人数量', halign:'center',align:'center',colspan:2},
		             
		            { field : 'cust', title : '客户占比(%)', halign:'center',align:'center',colspan:2},
		            { field : 'voucher', title : '凭证数量', halign:'center',align:'center',colspan:2},
		           ] ,
        [
			{ field : 'newsmall', title : '新增', width : 100, halign:'center',align:'right'}, 
			{ field : 'stocksmall', title : '存量', width : 100, halign:'center',align:'right'},
			{ field : 'newtaxpay', title : '新增', width : 100, halign:'center',align:'right'}, 
			{ field : 'stocktaxpay', title : '存量', width : 100, halign:'center',align:'right'},
         
            { field : 'custrates', title : '小规模', width : 100, formatter:formatMny, halign:'center',align:'right'}, 
            { field : 'custratet', title : '一般纳税人', width : 100, formatter:formatMny, halign:'center',align:'right'}, 
            { field : 'vouchernums', title : '小规模', width : 100, halign:'center',align:'right'}, 
            { field : 'vouchernumt', title : '一般纳税人', width : 100, halign:'center',align:'right'}, 
        ] ],
		onLoadSuccess : function(data) {
			var rows = $('#grid').datagrid('getRows');
			var footerData = new Object();
			var custsmall = 0;	// 
			var custtaxpay = 0;	// 
			var vouchernums = 0;	// 
			var vouchernumt = 0;	// 

			for (var i = 0; i < rows.length; i++) {
				if(rows[i].custsmall != undefined && rows[i].custsmall != null){
					custsmall += parseFloat(rows[i].custsmall);
				}
				if(rows[i].custtaxpay != undefined && rows[i].custtaxpay != null){
					custtaxpay += parseFloat(rows[i].custtaxpay);
				}
				if(rows[i].vouchernums != undefined && rows[i].vouchernums != null){
					vouchernums += parseFloat(rows[i].vouchernums);
				}
				if(rows[i].vouchernumt != undefined && rows[i].vouchernumt != null){
					vouchernumt += parseFloat(rows[i].vouchernumt);
				}
			}
			footerData['pname'] = '合计';
			footerData['custsmall'] = custsmall;
			footerData['custtaxpay'] = custtaxpay;
			footerData['vouchernums'] = vouchernums;
			footerData['vouchernumt'] = vouchernumt;

			var fs=new Array(1);
			fs[0] = footerData;
			$('#grid').datagrid('reloadFooter',fs);
		},
	});
}

/**
 * 加盟商名称
 */
function nameFormat(value, row, index){
	if(isEmpty(value)){
		return;
	}
	if (!isEmpty(row.dreldate)) {
		return  "<div style='position: relative;'>" + "<a href='javascript:void(0)' style='color:blue' onclick=\"qryDetail('"+index+"')\">" + value + "</a>"
		+ "<img style='right: 0; position: absolute;' src='../../images/rescission.png' /></div>"
	}else{
		return "<a href='javascript:void(0)' style='color:blue' onclick=\"qryDetail('"+index+"')\">" + value + "</a>";
	}
	
}

/**
 * 查询明细
 * @param index
 */
function qryDetail(index){
	var row = $('#grid').datagrid('getData').rows[index];
	var url = DZF.contextPath + "/report/financedealstaterep!queryDetail.action";
	$('#gridh').datagrid('options').url = url;
	$('#gridh').datagrid('load', {
		"cpid" : row.corpid,
		"period" : $('#qryperiod').datebox('getValue'),
		"mid" : "全部",
		"vcode" : "全部",
	});
	
}

/**
 * 查询明细
 */
function queryDetail(){
	var url = DZF.contextPath + "/report/financedealstaterep!queryDetail.action";
	$('#gridh').datagrid('options').url = url;
	var cpkname = $("#cpkname").textbox('getValue');
	cpkname = trimStr(cpkname,'g');
	$('#gridh').datagrid('load', {
		"cpid" : $("#corpid").val(),
		"period" : $("#qrydate").text(),
		"mid" : $("#mid").combobox('getValue'),
		"vcode" : $("#vcode").combobox('getValue'),
		"cpkname" : $("#cpkname").textbox('getValue'),
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
		fitColumns : false,
		height:'372',
		singleSelect : true,
		pagination : true, //显示分页
		pageSize : 20, //默认20行
		pageList : [ 20, 50, 100, 200 ],
		showRefresh : false,// 不显示分页的刷新按钮
		columns : [ [ {
			width : '80',
			title : '部门',
			align:'left',
			halign:'center',
			field : 'deptnm',
			formatter : function(value) {
	    		if(value!=undefined){
	    			return "<span title='" + value + "'>" + value + "</span>";
	    		}
			}
		}, {
			width : '120',
			title : '客户编码',
            halign:'center',
			field : 'corpkcd',
			
		}, {
			width : '180',
			title : '客户名称',
			align:'left',
            halign:'center',
			field : 'corpknm',
			formatter : function(value) {
	    		if(value!=undefined){
	    			return "<span title='" + value + "'>" + value + "</span>";
	    		}
			}
		}, {
            field: 'chname',
            title: '纳税人资格',
            width: '80',
            align: 'center',
            halign:'center',
        },{
            field: 'cdate',
            title: '录入日期',
            width: '80',
            align: 'center',
            halign:'center',
        },{
            field: 'bdate',
            title: '建账日期',
            width: '80',
            align: 'center',
            halign:'center',
        },{
            field: 'jzstatus',
            title: '记账状态',
            width: '110',
            align: 'center',
            halign:'center',
        }, {
            field: 'ckstatus',
            title: '关账状态',
            width: '120',
            align: 'left',
            halign: 'center',
        },] ],
		onLoadSuccess : function(data) {
			if(data == undefined){
				return;
			}
			if(data.rows && data.rows.length > 0){
				$('#detail_dialog').dialog('open');
				$('#qrydate').html(data.rows[0].period);
				$('#corpnm').html(data.rows[0].corpnm);
				
				$('#corpid').val(data.rows[0].corpid);
				$('#gridh').datagrid("scrollTo",0);
			}else{
				Public.tips({
					content : "暂无明细",
					type : 2
				});
			}
		},
	});
}

/**
 * 查询
 */
function reloadData() {
	var url =  DZF.contextPath + "/report/financedealstaterep!query.action";
	$('#grid').datagrid('options').url = url;
	var queryParams = $('#grid').datagrid('options').queryParams;
	queryParams.period = $('#qryperiod').datebox('getValue');
	queryParams.aname = $('#aname').combobox('getValue');
	var vince = $('#ovince').combobox('getValue');
	if (isEmpty(vince)) {
		vince = -1;
	}
	queryParams.ovince = vince;
	queryParams.uid = $('#uid').combobox('getValue');
	queryParams.corps = $("#pk_account").val();
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
	$("#qrydialog").css("visibility", "hidden");
}
