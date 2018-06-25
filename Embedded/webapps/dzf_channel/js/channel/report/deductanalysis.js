var contextPath = DZF.contextPath;
var hjcols = null;//汇总列字段

$(function(){
	initQueryDlg();
	initRef();
	initRadioListen();
	load(0);
});

/**
 * 查询对话框初始化
 */
function initQueryDlg(){
	initPeriod("#begperiod");
	initPeriod("#endperiod");
	var sdv = $('#bdate').datebox('getValue');
	var edv = $('#edate').datebox('getValue');
	$('#jqj').html(sdv + ' 至 ' + edv);
	$("#begperiod").datebox('readonly',true);
	$("#endperiod").datebox('readonly',true);
	$("#bdate").datebox('readonly',false);
	$("#edate").datebox('readonly',false);
}

/**
 * 参照初始化
 */
function initRef(){
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
				content : "一次最多只能选择300个客户",
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
 * 查询框按钮单选事件
 */
function initRadioListen(){
	$('input:radio[name="seledate"]').change( function(){  
		var ischeck = $('#qj').is(':checked');
		if(ischeck){
			$("#begperiod").datebox('readonly',false);
			$("#endperiod").datebox('readonly',false);
			$("#bdate").datebox('readonly',true);
			$("#edate").datebox('readonly',true);
		}else{
			$("#begperiod").datebox('readonly',true);
			$("#endperiod").datebox('readonly',true);
			$("#bdate").datebox('readonly',false);
			$("#edate").datebox('readonly',false);
		}
	});
}

/**
 * 表格初始化
 * @param type  0：初始化界面加载；1；查询；2；刷新；
 */
function load(type){
	var bdate = new Date();
	
	var onlycol =  new ArrayList();//金额展示集合
	var onlymap = new HashMap();//金额对应列数
	
	var bperiod;
	var eperiod;
	var begdate;
	var enddate;
	var qtype = $("input[name='seletype']:checked").val();
	if($('#qj').is(':checked')){
		bperiod = $("#begperiod").datebox('getValue');
		eperiod = $("#endperiod").datebox('getValue');
		if(isEmpty(bperiod)){
			Public.tips({
				content : "开始期间不能为空",
				type : 2
			});
			return;
		}
		if(isEmpty(eperiod)){
			Public.tips({
				content : "结束期间不能为空",
				type : 2
			});
			return;
		}
		
		$('#jqj').html(bperiod + ' 至 ' + eperiod);
	}else{
		begdate = $("#bdate").datebox('getValue');
		enddate = $("#edate").datebox('getValue');
		$('#jqj').html(begdate + ' 至 ' + enddate);
	}
	
	$.messager.progress({
		text : '数据加载中....'
	});
	
	//获取标题列
	var columns = getcolumn(onlymap, onlycol, bperiod, eperiod, begdate, enddate, qtype);
	
	var datarray =  new Array();
	var errmsg = "";
	$.ajax({
		type : "post",
		dataType : "json",
		url : DZF.contextPath + "/report/deductanalysis!query.action",
		traditional : true,
		data : {
			bperiod : bperiod,
			eperiod : eperiod,
			begdate : begdate,
			enddate : enddate,
			cpid : $("#qcorpid").val(),
			qtype : qtype,
		},
		async : false,
		success : function(data) {
			if (data.success) {
				var rows = data.rows;
				if(rows != null && rows.length > 0){
					var corpid;
					var obj;
					var colfield = "";
					for(var i = 0; i < rows.length; i++){
						if(i == 0){
							corpid = rows[i].corpid;
							obj = {};
							colnm = onlymap.get(rows[i].dedmny);
							colfield = 'num'+colnm;
							obj[colfield] = rows[i].corpnum;
							colfield = 'mny'+colnm;
							obj[colfield] = getFloatValue(rows[i].corpnum).mul(getFloatValue(rows[i].dedmny));
							
							obj['num'] = rows[i].sumnum;//总合同数
							obj['mny'] = rows[i].summny;//总扣款
							obj['retnum'] = rows[i].retnum;//退回合同数
							obj['custnum'] = rows[i].custnum;//存量合同数
							obj['zeronum'] = rows[i].zeronum;//0扣款(非存量)合同数
							obj['dednum'] = rows[i].dednum;//非存量合同数
//							if(getFloatValue(rows[i].retnum) > getFloatValue(0)){
//								obj['retmny'] = '-'+rows[i].retmny;//退回金额
//							}else{
//								obj['retmny'] = rows[i].retmny;//退回金额
//							}
							if(getFloatValue(rows[i].retmny) > getFloatValue(0)){
								obj['retmny'] = '-'+rows[i].retmny;//退回金额
							}
							obj['corpcode'] = rows[i].corpcode;//加盟商编码
							obj['corpname'] = rows[i].corpname;//加盟商名称
							obj['stocknum'] = rows[i].stocknum;//存量客户数
							if(i == rows.length - 1){//一行数据
								colnm = onlymap.get(rows[i].dedmny);
								colfield = 'num'+colnm;
								obj[colfield] = rows[i].corpnum;
								colfield = 'mny'+colnm;
								obj[colfield] = getFloatValue(rows[i].corpnum).mul(getFloatValue(rows[i].dedmny));
								datarray.push(obj);
							}
						}else{
							if(corpid == rows[i].corpid){
								colnm = onlymap.get(rows[i].dedmny);
								colfield = 'num'+colnm;
								obj[colfield] = rows[i].corpnum;
								colfield = 'mny'+colnm;
								obj[colfield] = getFloatValue(rows[i].corpnum).mul(getFloatValue(rows[i].dedmny));
								if(i == rows.length - 1){//最后一行数据
									datarray.push(obj);
								}
							}else if(corpid != rows[i].corpid){
								datarray.push(obj);
								corpid = rows[i].corpid;
								obj = {};
								colnm = onlymap.get(rows[i].dedmny);
								colfield = 'num'+colnm;
								obj[colfield] = rows[i].corpnum;
								colfield = 'mny'+colnm;
								obj[colfield] = getFloatValue(rows[i].corpnum).mul(getFloatValue(rows[i].dedmny));
								
								obj['num'] = rows[i].sumnum;//总合同数
								obj['mny'] = rows[i].summny;//总扣款
								obj['retnum'] = rows[i].retnum;//退回合同数
								obj['custnum'] = rows[i].custnum;//存量合同数
								obj['zeronum'] = rows[i].zeronum;//0扣款(非存量)合同数
								obj['dednum'] = rows[i].dednum;//非存量合同数
//								if(getFloatValue(rows[i].retnum) > getFloatValue(0)){
//									obj['retmny'] = '-'+rows[i].retmny;//退回金额
//								}else{
//									obj['retmny'] = rows[i].retmny;//退回金额
//								}
								if(getFloatValue(rows[i].retmny) > getFloatValue(0)){
									obj['retmny'] = '-'+rows[i].retmny;//退回金额
								}
								obj['corpcode'] = rows[i].corpcode;
								obj['corpname'] = rows[i].corpname;
								obj['stocknum'] = rows[i].stocknum;//存量客户数
								if(i == rows.length - 1){//最后一行数据
									datarray.push(obj);
								}
							}
						}
					}
				}
			}else{
				errmsg = data.msg;
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.messager.progress('close');
		}
	});
	
	if(!isEmpty(errmsg)){
		$.messager.progress('close');
		Public.tips({
			content : errmsg,
			type : 2
		});
		return;
	}
	
	$('#grid').datagrid({
		striped : true,
		title : '',
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : false,
		border : true,
		remoteSort:false,
		showFooter:true,
		//冻结在 左边的列 
		frozenColumns:[[
						{ field : 'corpid',    title : '会计公司主键', hidden:true},
		                { field : 'corpcode',  title : '加盟商编码', width : 100, halign:'center',align:'left'}, 
		                { field : 'corpname',  title : '加盟商', width : 160, halign:'center',align:'left'},
		                { field : 'stocknum',  title : '存量<br/>客户', width : 50, halign:'center',align:'right'},
//		                { field : 'num',  title : '总合同数', width : 100, halign:'center',align:'right', hidden:true}, 
		                { field : 'custnum',  title : '存量<br/>合同数', width : 60, halign:'center',align:'right', },
		                { field : 'zeronum',  title : '0扣款<br/>(非存量)<br/>合同数', width : 70, halign:'center',align:'right', },
		                { field : 'dednum',  title : '非存量<br/>合同数', width : 60, halign:'center',align:'right', },
		                { field : 'mny',  title : '总扣款', width : 100, halign:'center',align:'right',formatter:formatMny},
		]],
		columns : columns,
		onLoadSuccess : function(data) {
			calFooter();
		},
	});
	
	if(datarray != null && datarray.length > 0){
		$('#grid').datagrid('loadData', datarray);
	}else{
		$('#grid').datagrid('loadData',{ total:0, rows:[]});
	}
	
	$("#qrydialog").hide();
	
	var msg = "";
	if(type == 1){
		msg = "查询成功";
	}else if(type == 2){
		msg = "刷新成功";
	}
	if(!isEmpty(msg)){
		Public.tips({
			content : msg,
			type : 0
		});
	}
	var edate = new Date();
	var time = edate.getTime() - bdate.getTime();
	
	setTimeout(function(){
		$.messager.progress('close');
	}, time/2);
}

/**
 * 计算合计
 */
function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
//    var num = 0;	
    var mny = 0;	
    var stocknum = 0;//存量客户
    var custnum = 0;//存量合同数
    var zeronum = 0;//0扣款(非存量)合同数
    var dednum = 0;//非存量合同数
    
    for (var i = 0; i < rows.length; i++) {
//    	num += getFloatValue(rows[i].num);
    	mny += getFloatValue(rows[i].mny);
    	stocknum += getFloatValue(rows[i].stocknum);
    	custnum += getFloatValue(rows[i].custnum);
    	zeronum += getFloatValue(rows[i].zeronum);
    	dednum += getFloatValue(rows[i].dednum);
    	
		if(hjcols != null && hjcols.length > 0){
			for(var j = 0; j < hjcols.length; j++){
				var col = hjcols[j][0];
	    		hjcols[j][1] += getFloatValue(rows[i][col]);
	    	}
	    }
    }
    footerData['corpname'] = '合计';
//    footerData['num'] = num;
    footerData['mny'] = mny;
    footerData['stocknum'] = stocknum;
    footerData['custnum'] = stocknum;
    footerData['zeronum'] = zeronum;
    footerData['dednum'] = dednum;
    
    if(hjcols != null && hjcols.length > 0){
		for(var j = 0; j < hjcols.length; j++){
			var col = hjcols[j][0];
    		footerData[col] = hjcols[j][1];
    	}
    }
    
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}

/**
 * 获取标题列
 * @param onlymap
 */
function getcolumn(onlymap, onlycol, bperiod, eperiod, begdate, enddate, qtype){
	hjcols = new Array();
	var columns = new Array(); 
	var columnsh = new Array();//列及合并列名称
	var columnsb = new Array();//子列表名称集合
	
	//退回扣款
	var columnh1 = {};
	columnh1["title"] = '退回扣款';  
	columnh1["field"] = 'ret';  
	columnh1["width"] = '180'; 
	columnh1["colspan"] = 2; 
	columnsh.push(columnh1);
	
	var columnb1 = {};
	columnb1["title"] = '合同数';  
	columnb1["field"] = 'retnum';  
	columnb1["width"] = '90'; 
	columnb1["halign"] = 'center'; 
	columnb1["align"] = 'right'; 
//	columnb1["formatter"] = useFormat;
	columnsb.push(columnb1); 
	
	hjcols.push(new Array('retnum', 0));
	
	var columnb2 = {};
	columnb2["title"] = '金额';  
	columnb2["field"] = 'retmny';  
	columnb2["width"] = '90'; 
	columnb2["halign"] = 'center'; 
	columnb2["align"] = 'right'; 
	columnb2["formatter"] = formatMny;
	columnsb.push(columnb2); 
	
	hjcols.push(new Array('retmny', 0));
	
	$.ajax({
		type : "post",
		dataType : "json",
		url : DZF.contextPath + "/report/deductanalysis!queryMnyOrder.action",
		traditional : true,
		data : {
			bperiod : bperiod,
			eperiod : eperiod,
			begdate : begdate,
			enddate : enddate,
			qtype : qtype,
			cpid : $("#qcorpid").val(),
		},
		async : false,
		success : function(data) {
			if (data.success) {
				var rows = data.rows;
				if(rows != null && rows.length > 0){
					var corpid;
					var obj;
					var colnm = 0;
					var colfield = "";
					for(var i = 0; i < rows.length; i++){
						if(!onlycol.contains(rows[i].dedmny)){
							var column = {};
							var dedmny = rows[i].dedmny+"";
							if(dedmny.indexOf(".") != -1 ){
								column["title"] = dedmny;  
							}else{
								column["title"] = dedmny + ".00";  
							}
							column["field"] = 'col'+(i+1);  
							column["width"] = '180'; 
							column["colspan"] = 2; 
							columnsh.push(column); 
							
							onlymap.put(rows[i].dedmny,(i+1));
							
							var column1 = {};
							column1["title"] = '合同数';  
							column1["field"] = 'num'+(i+1);  
							column1["width"] = '90'; 
							column1["halign"] = 'center'; 
							column1["align"] = 'right'; 
//							column1["formatter"] = useFormat;
							columnsb.push(column1); 
							
							hjcols.push(new Array('num'+(i+1)+'', 0));
							
							var column2 = {};
							column2["title"] = '扣款';  
							column2["field"] = 'mny'+(i+1);  
							column2["width"] = '90'; 
							column2["halign"] = 'center'; 
							column2["align"] = 'right'; 
							column2["formatter"] = formatMny;
							columnsb.push(column2); 
							
							hjcols.push(new Array('mny'+(i+1)+'', 0));
							
							onlycol.add(rows[i].dedmny);
						}
					
					}
				}
				columns.push(columnsh);
				columns.push(columnsb);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			parent.$.messager.progress('close');
		}
	});
	return columns;
}

/**
* 导出
*/
function onExport() {
	var datarows = $('#grid').datagrid("getRows");
	if (datarows == null || datarows.length == 0) {
		Public.tips({
			content: '当前界面数据为空',
			type: 2
		});
		return;
	}
	
	var hblcols = $('#grid').datagrid("options").columns[0];//合并列信息
	var cols = $('#grid').datagrid('getColumnFields');               // 行信息
	var hbhcols = $('#grid').datagrid('getColumnFields', true);       // 合并行信息
	
	Business.getFile(DZF.contextPath + "/report/deductanalysis!export.action", {
		"strlist": JSON.stringify(datarows),
		'hblcols':JSON.stringify(hblcols), //合并列信息
		'cols':JSON.stringify(cols),//除冻结列之外，导出字段编码
		'hbhcols':JSON.stringify(hbhcols)//冻结列编码
	}, true, true);
}

/**
 * 查询框-取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 查询框-清空
 */
function clearParams(){
	$('#qcorp').textbox("setValue",null);
	$('#qcorpid').val(null);
}

/**
 * 扣款金额格式化
 * @param value
 * @param row
 * @param index
 * @returns
 */
function useFormat(value,row,index){
	var url = 'channel/contract/contractconfrim.jsp?operate=tocont&pk_billid='+row.billid;
	var ss = "<a href='javascript:void(0)' style='color:blue' onclick=\"parent.addTabNew('合同审核','"+url+"');\">"+formatMny(value)+"</a>";
	return ss ;
}
