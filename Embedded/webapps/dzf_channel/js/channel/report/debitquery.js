var contextPath = DZF.contextPath;
var isenter = false;//是否快速查询
var loadrows = null;
var hstr=["one","two","three","four","five","six","seven",
        "eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen"];
var printHead;
$(function() {
	initQry();//初始化查询框
	load();//加载列表
	initChannel();//初始化加盟商
	quickfiltet();
});


//初始化
function initQry(){
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	$(".mod-inner,.mod-toolbar-top").on("click",function(){
		$("#qrydialog").hide();
		$("#livediv").remove();
    	$("#qrydialog").css("visibility","hidden");
	});
	var beginShow=parent.SYSTEM.LoginDate.substring(0,4)+"-01";
	var endShow=parent.SYSTEM.LoginDate.substring(0,7);
	$("#begperiod").textbox('setValue',beginShow);
	$("#endperiod").textbox('setValue',endShow);
	$("#jqj").html(beginShow+" 至  "+endShow);
	queryBoxChange1('#begperiod','#endperiod');
	$("#begperiod").textbox({"readonly" : false});
	$("#endperiod").textbox({"readonly" : false});
	$("#bdate").datebox('readonly',true);
	$("#edate").datebox('readonly',true);
	initQryPeroid();
	changeRadio();
}

function changeRadio(){
	$('input:radio[name="seledate"]').change( function(){  
		var ischeck = $('#qj').is(':checked');
		if(ischeck){
			queryBoxChange1('#begperiod','#endperiod');
			var sdv = $('#begperiod').textbox('getValue');
			var edv = $('#endperiod').textbox('getValue');
			$('#jqj').html(sdv + ' 至 ' + edv);
			$("#begperiod").textbox({"readonly" : false});
			$("#endperiod").textbox({"readonly" : false});
			$("#bdate").datebox('readonly',true);
			$("#edate").datebox('readonly',true);
		}else{
			queryBoxChange2('#bdate','#edate');
			var sdv = $('#bdate').datebox('getValue');
			var edv = $('#edate').datebox('getValue');
			$('#jqj').html(sdv + ' 至 ' + edv);
			$("#begperiod").textbox({"readonly" : true});
			$("#endperiod").textbox({"readonly" : true});
			$(".foxdate").hide();
			$("#bdate").datebox('readonly',false);
			$("#edate").datebox('readonly',false);
		}
	});
}

/**
 * 查询框时间改变事件(为期间年月)
 * @param start
 * @param end
 */
function queryBoxChange1(start,end){
	$(start).textbox({
		onChange: function(newValue, oldValue){
			var edv = $(end).textbox('getValue');
			$('#jqj').text(newValue + ' 至 ' + edv);
		}
	});
	$(end).textbox({
		onChange: function(newValue, oldValue){
			var sdv = $(start).textbox('getValue');
			$('#jqj').text(sdv + ' 至 ' + newValue);
		}
	});
}

/**
 * 查询框时间改变事件(为日期年月日)
 * @param start
 * @param end
 */
function queryBoxChange2(){
	$('#bdate').datebox({
		onChange: function(newValue, oldValue){
			var edv = $('#edate').datebox('getValue');
			var start=new Date(newValue).getTime();
			var end=new Date(edv).getTime();
			var time=end-start;
			if(time<0){
				Public.tips({
					content : "起始时间不能大于结束时间",
					type : 2
				});
				$('#bdate').datebox('setValue',oldValue);
				$('#jqj').text(oldValue + ' 至 ' + edv);
			}else if(Math.floor(time / 86400000) > 14){
				Public.tips({
					content : "起始时间与结束时间间隔最大为14天",
					type : 2
				});
				$('#bdate').datebox('setValue',oldValue);
				$('#jqj').text(oldValue + ' 至 ' + edv);
			}else{
				$('#bdate').datebox('setValue',newValue);
				$('#jqj').text(newValue + ' 至 ' + edv);
			}
		}
	});
	$('#edate').datebox({
		onChange: function(newValue, oldValue){
			var sdv = $('#bdate').datebox('getValue');
			var start=new Date(sdv).getTime();
			var end=new Date(newValue).getTime();
			var time=end-start;
			if(time<0){
				Public.tips({
					content : "起始时间不能大于结束时间",
					type : 2
				});
				$('#edate').datebox('setValue',oldValue);
				$('#jqj').text(sdv + ' 至 ' + oldValue);
			}else if(Math.floor(time / 86400000) > 14){
				Public.tips({
					content : "起始时间与结束时间间隔最大为14天",
					type : 2
				});
				$('#edate').datebox('setValue',oldValue);
				$('#jqj').text(sdv + ' 至 ' + oldValue);
			}else{
				$('#edate').datebox('setValue',newValue);
				$('#jqj').text(sdv + ' 至 ' + newValue);
			}
		}
	});
}

//查询框关闭事件
function closeCx() {
	$("#qrydialog").css("visibility", "hidden");
}

// 清空查询条件
function clearCondition(){
	$("#pk_account").val(null);
	$("#channel_select").textbox("setValue",null);
}

//初始化加盟商
function initChannel(){
  $('#channel_select').textbox({
      editable: false,
      icons: [{
          iconCls: 'icon-search',
          handler: function(e) {
              $("#kj_dialog").dialog({
                  width: 600,
                  height: 480,
                  readonly: true,
                  title: '选择加盟商',
                  modal: true,
                  href: DZF.contextPath + '/ref/channel_select.jsp',
                  buttons: '#kj_buttons'
              });
          }
      }]
  });
}

function quickfiltet(){
	$('#quname').textbox('textbox').keydown(function (e) {
        if (e.keyCode == 13) {
        	$('#grid').datagrid('unselectAll');
 		   var filtername = $("#quname").val(); 
		   if (filtername) {
				var jsonStrArr = [];
				if(loadrows){
					for(var i=0;i<loadrows.length;i++){
						var row = loadrows[i];
						if(row.ccode.indexOf(filtername) >= 0 || row.cname.indexOf(filtername) >= 0){
							jsonStrArr.push(row);
						} 
					}
					$('#grid').datagrid('loadData',jsonStrArr);   
				}
			}else{
				load();
			} 
        }
    });
}

function reloadData(){
	loadrows = null;
	load();
	$('#grid').datagrid('unselectAll');
}

//双击选择公司
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
		$("#channel_select").textbox("setValue",str);
		$("#pk_account").val(corpIds);
	}
	 $("#kj_dialog").dialog('close');
}


function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}

function load() {
	var bdate;
	var edate;
	if($('#qj').is(':checked')){
		bdate=$("#begperiod").textbox('getValue');
		edate=$("#endperiod").textbox('getValue')
	}else{
		bdate=$("#bdate").datebox('getValue');
		edate=$("#edate").datebox('getValue');
	}
	if(isEmpty(bdate)||isEmpty(edate)){
		Public.tips({
			content :  "查询时间不能为空，请填全",
			type : 2
		});	
		return;
	}
	var queryData={
			"bdate" : bdate,
			"edate" : edate,
			"corps" : $("#pk_account").val(),
		};
	var columns=new Array();
	var columns1=new Array();
	printHead="";
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url : contextPath + '/report/debitquery!queryHeader.action',
		data : queryData,
		success : function(data, textStatus) {
			if (!data.success) {
				Public.tips({
					content :  data.msg,
					type : 1
				});	
			} else {
				var rows=data.rows;
				for(var i=0;i<rows[0].num;i++){
					columns[i]={width : "100",title :rows[i].head,field : hstr[i],align:"right",formatter: fny}
					printHead+=rows[i].head+",";
				}
				columns1[0]=columns;
			}
		}
	});
	
	 $('#grid').datagrid({
	      url : DZF.contextPath + '/report/debitquery!query.action',
	      queryParams:queryData,
	      fit : false,
	      rownumbers : true,
	      height : Public.setGrid().h,
	      width:'100%',
	      singleSelect : false,
	      showFooter:true,
	      frozenColumns:[[
							{width : '110',title : '加盟商编码',field : 'ccode',align:'left'},
							{width : '200',title : '加盟商名称',field : 'cname',align:'left'},
							{width : '100',title : '加盟日期',field : 'chndate',align:'left'},
							{width : '80',title : '预付款余额',field : 'outmny',align:'right',formatter: fny},
							{width : '80',title : '扣款合计',field : 'ndeductmny',align:'right',formatter: fny},
			       		]],
	      columns : columns1,
	      onLoadSuccess : function(data) {
				if(data.rows && loadrows == null){
					loadrows = data.rows;
				}
				$.messager.progress('close');
				$("#qrydialog").hide();
				calFooter();
			}
	  });
}

function fny(value,row,index){
	if (value == 0) {
		return "0.00";
	}
	return formatMny(value);
}


/**
 * 计算合计
 */
function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
    var outmny = 0;	
    var ndeductmny = 0;	
    var custsum = {};
    for(var i = 0; i <15; i++){
		custsum['custsum'+i] = 0;
	}
    for (var i = 0; i < rows.length; i++) {
    	outmny += parseFloat(rows[i].outmny == undefined ? 0 : rows[i].outmny);
    	ndeductmny += parseFloat(rows[i].ndeductmny == undefined ? 0 : rows[i].ndeductmny);
    	for(var j = 0; j <15; j++){
    		var num=hstr[j];
    		if(!isEmpty(rows[i][num])){
    			custsum['custsum'+j]+= parseFloat(rows[i][num] == undefined ? 0 : rows[i][num]);
    		}
    	}
    }
    footerData['cname'] = '合计';
    footerData['outmny'] = outmny;
    footerData['ndeductmny'] = ndeductmny;
    for(var i = 0; i <15; i++){
    	var num=hstr[i];
 		footerData[num] = custsum['custsum'+i];
     }
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}


/**
 * 打印
 */
function doPrint(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'当前界面数据为空',type:2});
		return;
	}
	var columns = $('#grid').datagrid("options").frozenColumns[0].slice(0,4);
	var array=$('#grid').datagrid("options").columns[0];
	for(var i=0;i<array.length;i++){
		columns.push(array[i]);
	}
	Business.getFile(contextPath+ '/report/debitquery!print.action',{'strlist':JSON.stringify(datarows),
		'columns':JSON.stringify(columns)}, true, true);
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
	Business.getFile(DZF.contextPath+ '/report/debitquery!exportExcel.action',
			{'strlist':JSON.stringify(datarows),'columns':printHead}, true, true);
}

/**
 * 查询期间初始化
 */
function initQryPeroid(){
	var begperiod = $('#begperiod').textbox('getValue');
	var year = "";
	var month = "";
	if(!isEmpty(begperiod)){
		year = begperiod.substring(0,4);
		month = begperiod.substring(5);
		month = parseInt(month) - 1;
	}
	$('#begperiod').textbox({
		icons: [{
			iconCls:'foxdate',
			handler: function(e){
				click_icon(150, 100, begperiod, year, month, function(val){
					if(!isEmpty(val)){
						var second = $('#endperiod').textbox('getValue');
						if(!isEmpty(second)){
							var syears = second.substr(0, second.indexOf('-'));
							var smonth = second.substr(second.indexOf('-')+1);
							var fyears = val.substr(0, val.indexOf('-'));
							var fmonth = val.substr(val.indexOf('-')+1);
							if(fyears > syears){
								Public.tips({
									content : "起始年份不能大于结束年份",
									type : 2
								});
								return;
							}else if(fyears == syears && fmonth > smonth){
								Public.tips({
									content : "起始月份不能大于结束月份",
									type : 2
								});
								return;
							}
							var ifyears = parseInt(fyears);
							var ifmonth = parseInt(fmonth);
							var isyears = parseInt(syears);
							var ismonth = parseInt(smonth);
							if(isyears - ifyears > 1){
								Public.tips({
									content : "查询结束期间不能超过12个月",
									type : 2
								});
								return;
							}
							var begin = ifyears * 12 + ifmonth;
							var end = isyears * 12 + ismonth;
							if((end - begin + 1) > 12){
								Public.tips({
									content : "查询结束期间不能超过12个月",
									type : 2
								});
								return;
							}
							$('#begperiod').textbox('setValue', val);
						}
					}else{
						$('#begperiod').textbox('setValue', val);
					}
					begperiod = val;
					if(!isEmpty(begperiod)){
						year = begperiod.substring(0,4);
						month = begperiod.substring(5);
						month = parseInt(month) - 1;
					}
				})
			}
		}]
	});
	var endperiod = $('#endperiod').textbox('getValue');
	var eyear = "";
	var emonth = "";
	if(!isEmpty(endperiod)){
		eyear = endperiod.substring(0,4);
		emonth = endperiod.substring(5);
		emonth = parseInt(emonth) - 1;
	}
	$('#endperiod').textbox({
		icons: [{
			iconCls:'foxdate',
			handler: function(e){
				click_icon(150, 250, endperiod, eyear, emonth, function(val){
					if(!isEmpty(val)){
						var first = $('#begperiod').textbox('getValue');
						if(!isEmpty(first)){
							var fyears = first.substr(0, first.indexOf('-'));
							var fmonth = first.substr(first.indexOf('-')+1);
							var syears = val.substr(0, val.indexOf('-'));
							var smonth = val.substr(val.indexOf('-')+1);
							if(fyears > syears){
								Public.tips({
									content : "起始年份不能大于结束年份",
									type : 2
								});
								return;
							}else if(fyears == syears && fmonth > smonth){
								Public.tips({
									content : "起始月份不能大于结束月份",
									type : 2
								});
								return;
							}
							var ifyears = parseInt(fyears);
							var ifmonth = parseInt(fmonth);
							var isyears = parseInt(syears);
							var ismonth = parseInt(smonth);
							if(isyears - ifyears > 1){
								Public.tips({
									content : "查询期间不能超过12个月",
									type : 2
								});
								return;
							}
							var begin = ifyears * 12 + ifmonth;
							var end = isyears * 12 + ismonth;
							if((end - begin + 1) > 12){
								Public.tips({
									content : "查询期间不能超过12个月",
									type : 2
								});
								return;
							}
							$('#endperiod').textbox('setValue', val);
						}
					}else{
						$('#endperiod').textbox('setValue', val);
					}
					endperiod = val;
					if(!isEmpty(endperiod)){
						eyear = endperiod.substring(0,4);
						emonth = endperiod.substring(5);
						emonth = parseInt(emonth) - 1;
					}
				})
			}
		}]
	});
}

