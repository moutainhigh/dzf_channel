 var contextPath = DZF.contextPath;
var grid,gridh;
//var loadrows = null;
//var isenter = false;//是否快速查询
var flowImgUrls = null;

$(function() {
	initPeriod("#stdate");
	initPeriod("#ovdate");
	initQryDlg();
	initQueryData();
	initQryLitener();
	initChannel();
	initCorpk();
	$('#corpkna_ae').textbox('readonly',true);
	initManager();
	load();
	initArea();
	
	$(document).on("mouseover", ".right_menu", function (e) {
		$(this).removeClass("rt_menu").addClass("rt_menu2");
		$(this).find(".more_div").show();
    });
    $(document).on("mouseout", ".right_menu", function (e) {
        $(this).removeClass("rt_menu2").addClass("rt_menu");
        $(this).find(".more_div").hide();
    });
});

/**
 * 查询框监听事件
 */
function initQryLitener(){
	$("#bdate").datebox("readonly", true);
	$("#edate").datebox("readonly", true);
	$('#bperiod').datebox("readonly", true);
	$('#eperiod').datebox("readonly", true);
    $('input:radio[name="seledate"]').change( function(){  
		var dqischeck = $('#dqdate').is(':checked');
		var tdischeck = $('#tddate').is(':checked');
		var kkischeck = $('#kkdate').is(':checked');
		if(dqischeck){
			var sdv = $("#stdate").datebox('getValue');
			var edv = $("#ovdate").datebox('getValue');
			$('#jqj').html(sdv + ' 至 ' + edv);
			$('#stdate').datebox("readonly", false);
			$('#ovdate').datebox("readonly", false);
			$("#bdate").datebox("readonly", true);
			$("#edate").datebox("readonly", true);
			$('#bperiod').datebox("readonly", true);
			$('#eperiod').datebox("readonly", true);
		}else if(tdischeck){
			var sdv = $('#bdate').datebox('getValue');
			var edv = $('#edate').datebox('getValue');
			$('#jqj').html(sdv + ' 至 ' + edv);
			$("#bdate").datebox("readonly", false);
			$("#edate").datebox("readonly", false);
			$('#bperiod').datebox("readonly", true);
			$('#eperiod').datebox("readonly", true);
			$('#stdate').datebox("readonly", true);
			$('#ovdate').datebox("readonly", true);
		}else if(kkischeck){
			var sdv = $("#bperiod").datebox('getValue');
			var edv = $("#eperiod").datebox('getValue');
			$('#jqj').html(sdv + ' 至 ' + edv);
			$('#bperiod').datebox("readonly", false);
			$('#eperiod').datebox("readonly", false);
			$("#bdate").datebox("readonly", true);
			$("#edate").datebox("readonly", true);
			$('#stdate').datebox("readonly", true);
			$('#ovdate').datebox("readonly", true);
		}
	});
}

/**
 * 查询渠道经理初始化
 */
function initManager(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryTrainer.action',
		data : {"qtype" :1},
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#mid').combobox('loadData',result.rows);
			}
		}
	});
}




/**
 * 监听查询
 */
function initQueryData(){
	
	queryBoxChange('#stdate','#ovdate');
	queryBoxChange('#bdate','#edate');
	queryBoxChange('#bperiod','#eperiod');
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	$("#bdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#edate").datebox("setValue", parent.SYSTEM.LoginDate);
	
	$("#bperiod").datebox("setValue", parent.SYSTEM.PreDate);
	$("#eperiod").datebox("setValue", parent.SYSTEM.LoginDate);
	
	$('#jqj').html($('#stdate').datebox('getValue') + ' 至 ' + $('#ovdate').datebox('getValue'));
}

/**
 * 关闭查询对话框
 */
function closeCx(){
	$("#qrydialog").hide();
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
    					ovince :"-2"
    				},
                    buttons: '#chnBtn'
                });
            }
        }]
    });
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

/**
 * 选择加盟商
 */
function selectCorps(){
	var rows = $('#gsTable').datagrid('getChecked');
	dClickCompany(rows);
}

/**
 * 查询框-清除
 */
function clearParams(){
	$('#corpkna_ae').combobox('readonly',true);
	$("#corpkna_ae").textbox("setValue",null);
	$("#corpkid_ae").val(null);
	$("#channel_select").textbox("setValue",null);
	$("#pk_account").val(null);
	$('#aname').combobox('setValue', null);
	$("#ichname").textbox("setValue",-1);
	$('#mid').combobox('setValue', null);
	$('#isncust').combobox('setValue', "");
	$('#qtype').combobox('setValue', -1);
	$('#destatus').combobox('setValue', -1);
	$('#comptype').combobox('setValue', -1);
	$('#corptype').combobox('setValue', -1);
	
}

/**
 * 客户参照初始化
 */
function initCorpk(){
	$('#corpkna_ae').searchbox({
		editable:false,
		prompt:'选择客户',
	    searcher:function(){
	    	$('#gs_dialog').dialog({
	    		width : 520,
	    		height : 490,
	    		readonly : true,
	    		close:true,
	    		title : '选择客户',
	    		modal : true,
	    		href : DZF.contextPath+'/ref/qykh_select.jsp',
	    		queryParams:{
	    			dblClickRowCallback : 'selectCorpk',
	    			corpid:$("#pk_account").val(),
	    		},
	    		buttons : [ {
	    			text : '确认',
	    			handler : function() {
	    				var row = $('#khTable').datagrid('getSelected');
	    				if(row){
	    					selectCorpk(row);
	    				}else{
	    					Public.tips({
	    						content : '请选择需要处理的数据',
	    						type : 2
	    					});
	    				}
	    			}
	    		}, {
	    			text : '取消',
	    			handler : function() {
	    				$('#gs_dialog').dialog('close');
	    			}
	    		}]
	    	});
	    }
	});
}

/**
 * 客户选择事件
 * @param row
 */
function selectCorpk(row){
	$('#corpkna_ae').textbox('setValue',row.uname);
	$('#corpkid_ae').val(row.pk_gs);
	$('#gs_dialog').dialog('close');
}

/**
 * 查询
 */
function reloadData(){
	var bdate = null;//提单开始日期
	var edate = null;//提单结束日期
	var bperiod = null;//扣款开始日期
	var eperiod = null;//扣款结束日期
	var stdate = null;//到期开始月份
	var ovdate = null;//到期结束月份
	var dqischeck = $('#dqdate').is(':checked');
	var tdischeck = $('#tddate').is(':checked');
	var kkischeck = $('#kkdate').is(':checked');
	if(tdischeck){
		bdate = $('#bdate').datebox('getValue'); 
		edate = $('#edate').datebox('getValue'); 
		if(isEmpty(bdate)){
			Public.tips({
				content : '提单开始日期不能为空',
				type : 2
			});
			return;
		}
		if(isEmpty(edate)){
			Public.tips({
				content : '提单结束日期不能为空',
				type : 2
			});
			return;
		}
		if(!isEmpty(bdate) && !isEmpty(edate)){
			if(!checkdate1("bdate","edate")){
				return;
			}		
		}
	}else if(kkischeck){
		bperiod = $('#bperiod').datebox('getValue');
		eperiod = $('#eperiod').datebox('getValue');
		if(isEmpty(bperiod)){
			Public.tips({
				content : '扣款开始日期不能为空',
				type : 2
			});
			return;
		}
		if(isEmpty(eperiod)){
			Public.tips({
				content : '扣款结束日期不能为空',
				type : 2
			});
			return;
		}
		if(!isEmpty(bperiod) && !isEmpty(eperiod)){
			if(!checkdate1("bperiod","eperiod")){
				return;
			}		
		}
	}else if(dqischeck){
		stdate = $('#stdate').datebox('getValue');
		ovdate = $('#ovdate').datebox('getValue');
		if(isEmpty(stdate)){
			Public.tips({
				content : '到期开始月份不能为空',
				type : 2
			});
			return;
		}
		if(isEmpty(ovdate)){
			Public.tips({
				content : '到期月份结束不能为空',
				type : 2
			});
			return;
		}
		if(!isEmpty(stdate) && !isEmpty(ovdate)){
			if(!checkdate1("stdate","ovdate")){
				return;
			}		
		}
	}
	
	
	var queryParams = $('#grid').datagrid('options').queryParams;
	clearQryParam(queryParams);
	$('#grid').datagrid('options').url =contextPath + '/contract/contractconfquery!query.action';
	
	if(isEmpty($("#qtype").combobox('getValue'))){
		$('#grid').datagrid('loadData',{ total:0, rows:[]});
	    $('#qrydialog').hide();
	    $('#grid').datagrid('unselectAll');
		return;
	}
	queryParams.begdate = bdate;
	queryParams.enddate = edate;
	queryParams.bperiod = bperiod;
	queryParams.eperiod = eperiod;
	queryParams.stdate = stdate;
	queryParams.ovdate = ovdate;
	queryParams.destatus = $('#destatus').combobox('getValue');//合同状态
	var isncust = $('#isncust').combobox('getValue');//客户类型
	if(!isEmpty(isncust)){//
		queryParams.isncust = isncust;
	}else{
		 delete queryParams.isncust;
	}
	queryParams.cpid = $("#pk_account").val();//加盟商
	queryParams.cpkid = $("#corpkid_ae").val();//客户
	queryParams.mid = $("#mid").combobox('getValue');//渠道经理
	queryParams.corptype = $('#corptype').combobox('getValue');//纳税人资格
	queryParams.comptype = $('#comptype').combobox('getValue');//服务套餐
	queryParams.aname = $("#aname").combobox('getValue');//大区
	queryParams.qtype = $("#qtype").combobox('getValue');//合同类型
	
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
    $('#qrydialog').hide();
	$('#grid').datagrid('clearSelections');
	$('#grid').datagrid('clearChecked');
}

/**
 * 清除查询传递查询条件
 * @param queryParams
 */
function clearQryParam(queryParams){
	queryParams.begdate = null;//提单开始日期
	queryParams.enddate = null;//提单结束日期
	queryParams.bperiod = null;//扣款开始日期
	queryParams.eperiod = null;//扣款结束日期
	queryParams.stdate = null;//到期开始月份
	queryParams.ovdate = null;//到期结束月份
	queryParams.qtype = -1;
	queryParams.destatus = -1;
	delete queryParams.isncust;
	queryParams.cpid = null;
	queryParams.cpkid = null;
	queryParams.id = null;
	queryParams.cpname = null;
	queryParams.mid = null;
}

/**
 * 列表grid初始化
 */
function load(){
	grid = $('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height : Public.setGrid().h,
		singleSelect : false,
		checkOnSelect : true,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		showFooter:true,
		remoteSort : false,//定义从服务器排序
		sortName:"submitime",//排序字段
		sortOrder:"desc",//排序方式
		columns : [ [ {
			field : 'ck',
			checkbox : true
		},{
			width : '100',
			title : '主键',
			field : 'id',
			hidden : true
		}, {
			width : '100',
			title : '合同主键',
			field : 'contractid',
			hidden : true
		}, {
			width : '100',
			title : '加盟商主键',
			field : 'corpid',
			hidden : true
		}, {
			width : '100',
			title : '客户主键',
			field : 'corpkid',
			hidden : true
		}, {
			width : '100',
			title : '业务小类主键',
			field : 'typemin',
			hidden : true
		}, {
			width : '100',
			title : '时间戳',
			field : 'tstp',
			hidden : true
		}, {
			width : '100',
			title : '制单人',
			field : 'operatorid',
			hidden : true
		}, {
			width : '100',
			title : '套餐主键',
			field : 'pid',
			hidden : true
		}, {
			width : '100',
			title : '加盟商合同类型',
			field : 'pstatus',
			hidden : true
		}, {
			width : '100',
			title : '合同总金额',
			align:'right',
            halign:'center',
			field : 'ntlmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
//			sortable:true,
//			sorter:orderfun,
			hidden : true
		},{
			width : '60',
			title : '大区',
			align : 'left',
            halign: 'center',
			field : 'aname'
		}, {
			width : '100',
			title : '省（市）',
			halign:'center',
			field : 'area',
		}, {
			width : '80',
			title : '渠道经理',
			halign:'center',
			field : 'mname',
		}, {
			width : '180',
			title : '加盟商名称',
			halign:'center',
			field : 'corpnm',
			formatter: function (value,row,index) {
            	if (!isEmpty(row.dreldate)) {
            		return "<div style='position: relative;'>" + value 
            			+ "<img style='right: 0; position: absolute;' src='../../images/rescission.png' /></div>";
            	}else{
            		return value;
            	}
            }
		}, {
			width : '200',
			title : '客户名称',
			halign:'center',
			field : 'corpkna',
		}, {
			width : '100',
			title : '纳税人资格',
			halign:'center',
			field : 'chname',
		}, {
			width : '150',
			title : '合同编码',
			halign:'center',
			field : 'vccode',
			styler: function (value, row, index) {//扣款方式为续费
                if (row.ictype == 2) {
                    return 'background:url(../../images/adde.png) no-repeat 132px 2px;';
                }
            },
			formatter:codeLink,
		}, {
			width : '80',
			title : '代账费',//合同代账费 = 合同总金额 - 合同账本费
			align:'right',
            halign:'center',
			field : 'naccmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
			sortable:true,
			sorter:orderfun,
		}, {
			width : '80',
			title : '账本费',
			align:'right',
            halign:'center',
			field : 'nbmny',
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
			sortable:true,
			sorter:orderfun,
		},  {
			width : '100',
			title : '开始月份',
			halign:'center',
			align:'center',
			field : 'bperiod',
			sortable:true,
			sorter:charorderfun,
		}, {
			width : '100',
			title : '到期月份',
			halign:'center',
			align:'center',
			field : 'eperiod',
			sortable:true,
			sorter:charorderfun,
		},{
			width : '60',
			title : '状态',
            halign:'center',
			field : 'destatus',
			align:'center',
			formatter : function(value) {
				if (value == '5')
					return '待审核';
				if (value == '1')
					return '已审核';
				if (value == '7')
					return '已驳回';
				if (value == '8')
					return '服务到期';
				if (value == '9')
					return '已终止';
				if (value == '10')
					return '已作废';
			}
		}, {
			width : '150',
			title : '提交时间',
			halign:'center',
			field : 'submitime',
			//sortable:true,
			//sorter:charorderfun,
			styler: function (value, row, index) {//扣款方式为续费
                if (row.ictype == 2) {
                    return 'background:url(../../images/adde.png) no-repeat 132px 2px;';
                }
            }
		}, {
			width : '80',
			title : '扣费日期',
			halign:'center',
			align:'center',
			field : 'dedate',
		},    ] ],
		onLoadSuccess : function(data) {
            parent.$.messager.progress('close');
			calFooter();
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

/**
 * 查询大区初始化
 */
function initArea(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryArea.action',
		data : {"qtype" :1},
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#aname').combobox('loadData',result.rows);
			}
		}
	});
}

/**
 * 驳回原因添加tips显示
 * @param value
 */
function reaFormat(value){
	if(value != undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}
}

/**
 * 计算合计
 */
function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
    var ntlmny = 0;	
    var nmsmny = 0;	
//    var ndesummny = 0;	
    var ndemny = 0;
    var nderebmny = 0;
    var naccmny = 0;
    var nbmny = 0;
    for (var i = 0; i < rows.length; i++) {
    	ntlmny += getFloatValue(rows[i].ntlmny);
    	nmsmny += getFloatValue(rows[i].nmsmny);
//    	ndesummny += getFloatValue(rows[i].ndesummny);
    	ndemny += getFloatValue(rows[i].ndemny);
    	nderebmny += getFloatValue(rows[i].nderebmny);
    	naccmny += getFloatValue(rows[i].naccmny);
    	nbmny += getFloatValue(rows[i].nbmny);
    }
    footerData['corpnm'] = '合计';
    footerData['ntlmny'] = ntlmny;
    footerData['nmsmny'] = nmsmny;
//    footerData['ndesummny'] = ndesummny;
    footerData['ndemny'] = ndemny;
    footerData['nderebmny'] = nderebmny;
    footerData['naccmny'] = naccmny;
    footerData['nbmny'] = nbmny;
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}


/**
 * 存量客户格式化
 * @param value
 * @returns {String}
 */
function isnformat(value,row){
	if(!isEmpty(row.contractid)){
		if(value&&(value=='Y'||value=="是")){
			return "<input type=\"checkbox\" checked=\"checked\" onclick=\"return false;\" >";
		}else{
			return "<input type=\"checkbox\" onclick=\"return false;\" >";
		}
	}
}

/**
 * 超级链接-穿透查询
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function formatDocLink(val,row,index){  
	if(!isEmpty(row.contractid)){
		return '<a href="#" style="color:blue" onclick="viewattach('+index+')">合同附件</a>';  
	}
}

/**
 * 标签查询
 * @param type  1：待审核；2：已驳回
 */
function queryData(type){
	$('#grid').datagrid('unselectAll');
	var queryParams = $('#grid').datagrid('options').queryParams;
	clearQryParam(queryParams);
	if(type == 1){
		queryParams.destatus = 5;
	}else if(type == 2){
		queryParams.destatus = 7;
	}
	grid.datagrid('options').url =contextPath + '/contract/contractconfquery!query.action';
	$('#grid').datagrid('options').queryParams = queryParams;
	$('#grid').datagrid('reload');
}




/**
 * 监听
 */
function actionListen(){
	$(".btn-slide").click(function() {
		$("#panela").slideToggle("slow");
		$(this).toggleClass("active");
		return false;
	})
}

/**
 * 初始化监听
 */
function initListener(){
	$("#propor").numberbox({
		onChange : function(n, o) {
			if (isEmpty(n)){
				n = 0; 
			}
			var ntlmny = $('#ntlmny').numberbox('getValue');//合同金额
			var nbmny = $('#nbmny').numberbox('getValue');//账本费
			 //扣费标准修改为扣掉账本费的合同金额
            var countmny = getFloatValue(ntlmny).sub(getFloatValue(nbmny));
            var ndesummny = getFloatValue(countmny).mul(parseFloat(n)).div(100);
			$('#ndesummny').numberbox('setValue', ndesummny);
		}
	});
	
	$("#confreason").textbox('readonly',true);
	$(":radio").click( function(){
		var opertype = $('input:radio[name="opertype"]:checked').val();
		if(opertype == 1){
			$("#confreason").textbox('readonly',true);
			$("#confreason").textbox('setValue',null);
			$("#confreasonid").val(null);
		}else if(opertype == 2){
			$("#confreason").textbox('readonly',false);
		}
	});
}


/**
 * 初始化附件
 * @param row
 */
function initFileDoc(row){
	var para = {};
	para.corp_id = row.corpid;
	para.c_id = row.contractid;
	
	$.ajax({
		type : "POST",
		url : DZF.contextPath + "/contract/contractconfquery!getAttaches.action",
  		dataType : 'json',
  		data : para,
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var rows = result.rows;
			if(rows != null && rows.length > 0){
				$("#fileshow").show();
				arrachrows = result.rows;
				$("#filedocs").html('');
				flowImgUrls = new Array();
				for(var i = 0;i<rows.length;i++){
					var srcpath = rows[i].fpath.replace(/\\/g, "/");
					var attachImgUrl = getAttachImgUrl(rows[i]);
					$('<li><a href="javascript:void(0)"  onmouseover="showTips(' + i + ')"  '+
							'onmouseout="hideTips(' + i + ')"  ondblclick="doubleImage(\'' + i + 
							'\');" ><span><img src="' +attachImgUrl +  '" />'+
							'<div id="reUpload' + i +
							'" style="width: 60%; height: 25px; position: absolute; top: 105px; left: 0px; display:none;">'+
							'<h4><span id="tips'+ i +'"></span></h4></div></span>'+
							'<font>' + 	rows[i].doc_name + '</font></a></li>').appendTo($("#filedocs"));
					
					var src = DZF.contextPath + "/contract/contractconfquery!getAttachImage.action?doc_id=" +
					rows[i].doc_id + "&corp_id=" + rows[i].corp_id;
					var img = '<img id="conturnid" alt="无法显示图片" src="' + src 
					+ '" style="position: absolute;z-index: 1;left:50px;">';
					flowImgUrls[i] = img;
				}
			}
		}
	});
	
}


/**
 * 单选按钮初始化
 */
function initRedioListener(){
	$(":radio").click( function(){
		var opertype = $('input:radio[name="bopertype"]:checked').val();
		if(opertype == 1){
			$("#bconfreason").textbox('readonly',true);
			$("#bconfreason").textbox('setValue',null);
			$("#bconfreasonid").val(null);
		}else if(opertype == 2){
			$("#bconfreason").textbox('readonly',false);
		}
	});
}


/**
 * 合同编码格式化
 * @param value
 * @param row
 * @param index
 */
function codeLink(value,row,index){
	if(!isEmpty(row.contractid)){
		return '<a href="javascript:void(0)" style="color:blue"  onclick="showInfo(' + index + ')">'+value+'</a>';
	}
}

/**
 * 合同明细查看
 * @param index
 */
function showInfo(index){
	var row = $('#grid').datagrid('getData').rows[index];
	$.ajax({
		url : DZF.contextPath + "/contract/contractconfquery!queryInfoById.action",
		dataType : 'json',
		data : row,
		success : function(rs) {
			if (rs.success) {
				var row = rs.rows;
				var changeshow = false;
				var dedshow = false;
				if(row.destatus == 9 || row.destatus == 10){
					changeshow = true;
					dedshow = true;
				}
				if(row.destatus == 1){
					dedshow = true;
				}
				if(changeshow){
					$('#changeinfo').css('height','auto');
					$('#changeinfo').css('display','block');
				}else{
					$('#changeinfo').css('display','none');
				}
				if(dedshow){
					$('#dedinfo').css('height','auto');
					$('#dedinfo').css('display','block');
				}else{
					$('#dedinfo').css('display','none');
				}
				
				$('#info_Dialog').dialog({ modal:true });//设置dig属性
				$('#info_Dialog').dialog('open').dialog('center').dialog('setTitle','合同详情');
				$('#infofrom').form('clear');
				$('#infofrom').form('load',row);
				initInfoFileDoc(row);
				$('#rejeson').css('display','none');
				if(!isEmpty(row.confreason)){
					$('#rejeson').css('display','block');
					showRejReason(row);
				}
			}
		}
		
	});
}

/**
 * 展示驳回原因-详情界面
 */
function showRejReason(row){
	var rejesons = row.children;
	$("#rejeson").empty();
	if(rejesons != null && rejesons.length > 0){
		var showinfo = "";
		showinfo = showinfo + "<div class='time_col time_colp11 heading'>";
		showinfo = showinfo + "		<label style='width: 68px;text-align:center;color:#FFF;font-weight: bold;'>驳回历史</label>";
		showinfo = showinfo + "</div>";
		
		showinfo = showinfo + "<div style='height: 50px; margin-top: 16px; width: 100%;'>";
		showinfo = showinfo + "  <div"; 
		showinfo = showinfo + "    style='height: 50px; width: 100px; float: left; position: relative;'>"; 
		showinfo = showinfo + "    <img src='../../images/tbpng_03.png' style='position: absolute; left: 69px;' />"; 
		showinfo = showinfo + "    <img src='../../images/pngg_03.png' style='position: absolute; left: 75px; top: 14px;height:50px;' />"; 
		showinfo = showinfo + "  </div>"; 
		showinfo = showinfo + "  <div style='height: 50px; width: 90%; float: left;'>"; 
		showinfo = showinfo + "    <div  class='dot'>"; 
		showinfo = showinfo + "      <font>"+rejesons[0].updatets+"</font> &emsp;<span>"+rejesons[0].reason+"</span>&emsp;<font color='#FF0000'>"+rejesons[0].operator+"</font>"; 
		showinfo = showinfo + "    </div>"; 
		showinfo = showinfo + "  </div>"; 
		showinfo = showinfo + "</div>";
		if(rejesons.length > 1){
			showinfo = showinfo + "<div style='display: none;' id='panel'>";
			showinfo = showinfo + "		<div style='width: 100%;'>";
			showinfo = showinfo + "";
			showinfo = showinfo + "";
			for(var i = 1; i < rejesons.length; i++){
				showinfo = showinfo + "<div style='height: 50px;'>";
				showinfo = showinfo + "  <div"; 
				showinfo = showinfo + "    style='height: 50px; width: 100px; float: left; position: relative;'>"; 
				showinfo = showinfo + "    <img style='position: absolute; left: 71px;' src='../../images/xial_03.png' />"; 
				showinfo = showinfo + "    <img style='position: absolute; left: 75px; top: 8px;height:50px;' src='../../images/pngg_03.png' />"; 
				showinfo = showinfo + "  </div>"; 
				showinfo = showinfo + "  <div style='height: 50px; width: 90%; float: left;'>"; 
				showinfo = showinfo + "    <div  class='dot'>"; 
				showinfo = showinfo + "      <div>"+rejesons[i].updatets+"</div>&emsp;<span>"+rejesons[i].reason+"</span>&emsp;<font color='#FF0000'>"+rejesons[i].operator+"</font>";
				showinfo = showinfo + "    </div>"; 
				showinfo = showinfo + "  </div>"; 
				showinfo = showinfo + "</div>";
			}
			showinfo = showinfo + "		</div>";
			showinfo = showinfo + "</div>";
		}
		showinfo = showinfo + "<p class='slide'>";
		showinfo = showinfo + "		<a href='javascript:;' rel='external nofollow' class='btn-slide active'></a>";
		showinfo = showinfo + "</p>";
		$("#rejeson").append(showinfo);
		actionListener();
	}
}

/**
 * 监听-详情
 */
function actionListener(){
	$(".btn-slide").click(function() {
		$("#panel").slideToggle("slow");
		$(this).toggleClass("active");
		return false;
	})
}

/**
 * 初始化变更合同附件
 * @param row
 */
function initInfoFileDoc(row){
	var para = {};
	para.corp_id = row.corpid;
	para.c_id = row.contractid;
	
	$.ajax({
		type : "POST",
		url : DZF.contextPath + "/contract/contractconfquery!getAttaches.action",
  		dataType : 'json',
  		data : para,
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var rows = result.rows;
			if(rows != null && rows.length > 0){
				$("#ifileshow").show();
				arrachrows = result.rows;
				$("#ifiledocs").html('');
				flowImgUrls = new Array();
				for(var i = 0;i<rows.length;i++){
					var srcpath = rows[i].fpath.replace(/\\/g, "/");
					var attachImgUrl = getAttachImgUrl(rows[i]);
					$('<li><a href="javascript:void(0)"  onmouseover="showTips(' + i + ')"  '+
							'onmouseout="hideTips(' + i + ')"  ondblclick="doubleImage(\'' + i + '\');" ><span style="height: 120px"><img src="' +attachImgUrl +  '" />'+
							'<div id="reUpload' + i +'" style="width: 100%; height: 25px; top: 105px; left: 0px; display:none;">'+
							'<h4><span id="tips'+ i +'" ></span></h4></div></span>'+
							'<div style="width: 221px; word-break: break-all; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">' + 	rows[i].doc_name + '</div></a></li>').appendTo($("#ifiledocs"));
					
					var src = DZF.contextPath + "/contract/contractconfquery!getAttachImage.action?doc_id=" +
						rows[i].doc_id + "&corp_id=" + rows[i].corp_id;
					var img = '<img id="conturnid" alt="无法显示图片" src="' + src 
						+ '" style="position: absolute;z-index: 1;left:50px;">';
					flowImgUrls[i] = img;
				}
			}
		}
	});
}



/**
 * 导出
 */
function onExport(){
	var seledata = $('#grid').datagrid("getChecked");
	var datarows = $('#grid').datagrid("getRows");
	var exportdata = datarows;
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'请选择需导出的数据',type:2});
		return;
	}
	if(seledata != null && seledata.length > 0){
		exportdata = seledata;
	}
		var columns = $('#grid').datagrid("options").columns[0];
    	Business.getFile(DZF.contextPath+ '/contract/contractconfquery!onExport.action',
    			{'strlist':JSON.stringify(exportdata),'qj' : $('#jqj').html()}, true, true);
}

/**
 * 全部导出
 */
function onExportAll(){
	var bdate = null;//提单开始日期
	var edate = null;//提单结束日期
	var bperiod = null;//扣款开始日期
	var eperiod = null;//扣款结束日期
	var stdate = null;//到期开始月份
	var ovdate = null;//到期结束月份
	var dqischeck = $('#dqdate').is(':checked');
	var tdischeck = $('#tddate').is(':checked');
	var kkischeck = $('#kkdate').is(':checked');
	if(tdischeck){
		bdate = $('#bdate').datebox('getValue'); 
		edate = $('#edate').datebox('getValue'); 
		if(isEmpty(bdate)){
			Public.tips({
				content : '提单开始日期不能为空',
				type : 2
			});
			return;
		}
		if(isEmpty(edate)){
			Public.tips({
				content : '提单结束日期不能为空',
				type : 2
			});
			return;
		}
		if(!isEmpty(bdate) && !isEmpty(edate)){
			if(!checkdate1("bdate","edate")){
				return;
			}		
		}
	}else if(kkischeck){
		bperiod = $('#bperiod').datebox('getValue');
		eperiod = $('#eperiod').datebox('getValue');
		if(isEmpty(bperiod)){
			Public.tips({
				content : '扣款开始日期不能为空',
				type : 2
			});
			return;
		}
		if(isEmpty(eperiod)){
			Public.tips({
				content : '扣款结束日期不能为空',
				type : 2
			});
			return;
		}
		if(!isEmpty(bperiod) && !isEmpty(eperiod)){
			if(!checkdate1("bperiod","eperiod")){
				return;
			}		
		}
	}else if(dqischeck){
		stdate = $('#stdate').datebox('getValue');
		ovdate = $('#ovdate').datebox('getValue');
		if(isEmpty(stdate)){
			Public.tips({
				content : '到期开始月份不能为空',
				type : 2
			});
			return;
		}
		if(isEmpty(ovdate)){
			Public.tips({
				content : '到期月份结束不能为空',
				type : 2
			});
			return;
		}
		if(!isEmpty(stdate) && !isEmpty(ovdate)){
			if(!checkdate1("stdate","ovdate")){
				return;
			}		
		}
	}
	
	var isncust = $('#isncust').combobox('getValue');
	
	var queryParams = new Object();
	
	queryParams.qj = $('#jqj').html();
	queryParams.begdate = bdate;
	queryParams.enddate = edate;
	queryParams.bperiod = bperiod;
	queryParams.eperiod = eperiod;
	queryParams.stdate = stdate;
	queryParams.ovdate = ovdate;
	queryParams.destatus = $('#destatus').combobox('getValue');
	queryParams.cpid = $("#pk_account").val();
	queryParams.cpkid = $("#corpkid_ae").val();
	queryParams.mid = $("#mid").combobox('getValue');
	queryParams.corptype = $('#corptype').combobox('getValue');
	queryParams.aname = $("#aname").combobox('getValue');
	queryParams.qtype = $("#qtype").combobox('getValue');
	if(!isEmpty(isncust)){
		queryParams.isncust = isncust;
	}
	
	Business.getFile(DZF.contextPath+ '/contract/contractconfquery!onExportAll.action',
					queryParams, true, true);
}

