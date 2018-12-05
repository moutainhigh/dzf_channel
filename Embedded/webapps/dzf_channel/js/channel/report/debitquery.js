var contextPath = DZF.contextPath;
var isenter = false;//是否快速查询
var hstr=["one","two","three","four","five","six","seven",
        "eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen"];
$(function() {
	initQry();//初始化查询框
	var queryData = getQueryData();
	if(isEmpty(queryData)){
		Public.tips({
			content : '必输信息为空或格式不正确',
			type : 2
		});
		return;
	}
	load(queryData);//加载列表
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
				var bdate;
				var edate;
				if($('#qj').is(':checked')){
					bdate=$("#begperiod").textbox('getValue');
					edate=$("#endperiod").textbox('getValue')
				}else{
					bdate=$("#bdate").datebox('getValue');
					edate=$("#edate").datebox('getValue');
					var flag = $('#qryfrom').form('validate');
					if(!flag){
						Public.tips({
							content : '查询时间不能为空，请填全',
							type : 2
						});
						return;
					}
				}
				var rows = $('#grid').datagrid('getRows');
				var queryData = null;
				if(rows != null && rows.length > 0){
					queryData = {
						"bdate" : bdate,
						"edate" : edate,
						"corps" : $("#pk_account").val(),
						"cname" : filtername,
					};
				}else{
					queryData = {
						"bdate" : bdate,
						"edate" : edate,
						"cname" : filtername,
					};
				}
				load(queryData);
			}else{
				var queryData = getQueryData();
				if(isEmpty(queryData)){
					Public.tips({
						content : '必输信息为空或格式不正确',
						type : 2
					});
					return;
				}
				load(queryData);
			} 
        }
    });
}

function reloadData(){
	var queryData = getQueryData();
	if(isEmpty(queryData)){
		Public.tips({
			content : '必输信息为空或格式不正确',
			type : 2
		});
		return;
	}
	load(queryData);
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

function getQueryData(){
	var bdate;
	var edate;
	if($('#qj').is(':checked')){
		bdate=$("#begperiod").textbox('getValue');
		edate=$("#endperiod").textbox('getValue')
	}else{
		bdate=$("#bdate").datebox('getValue');
		edate=$("#edate").datebox('getValue');
		var flag = $('#qryfrom').form('validate');
		if(!flag){
			Public.tips({
				content : '查询时间不能为空，请填全',
				type : 2
			});
			return;
		}
	}
	var queryData = {
		"bdate" : bdate,
		"edate" : edate,
		"corps" : $("#pk_account").val(),
	};
	return queryData;
}

function load(queryData) {
	var columns=new Array();
	var columns1=new Array();
	var columns2=new Array();
	
	columns1[0]= {width : '130',title : '大区',field : 'aname',align:'left',rowspan:2}; 
	columns1[1]={width : '100',title : '区总',field : 'uname',align:'left',rowspan:2};
	columns1[2]={width : '110',title : '省（市）',field : 'provname',align:'left',rowspan:2};
	columns1[3]={width : '100',title : '渠道经理',field : 'cuname',align:'left',rowspan:2}; 
	columns1[4]={width : '110',title : '加盟商编码',field : 'ccode',align:'left', rowspan:2};
	columns1[5]={width : '200',title : '加盟商名称',field : 'cname',align:'left', rowspan:2};
	columns1[6]={width : '100',title : '加盟商类型',field : 'chtype',align:'left', rowspan:2,formatter :ftype};
	columns1[7]={width : '100',title : '加盟日期',field : 'chndate',align:'left', rowspan:2};
	columns1[8]={width : '200',title : '余额',field : 'double1',align:'right',colspan:2};
	columns1[9]={width : '200',title : '扣款合计',field : 'double2',align:'right',colspan:2};
	
//	columns1[0]={width : '110',title : '加盟商编码',field : 'ccode',align:'left', rowspan:2};
//	columns1[1]={width : '200',title : '加盟商名称',field : 'cname',align:'left', rowspan:2};
//	columns1[2]={width : '100',title : '加盟商类型',field : 'chtype',align:'left', rowspan:2,formatter :ftype};
//	columns1[3]={width : '100',title : '加盟日期',field : 'chndate',align:'left', rowspan:2};
//	columns1[4]={width : '200',title : '余额',field : 'double1',align:'right',colspan:2};
//	columns1[5]={width : '200',title : '扣款合计',field : 'double2',align:'right',colspan:2};
	
	columns2[0]={width : '100',title : '预付款',field : 'outymny',align:'right',formatter: fny},
	columns2[1]={width : '100',title : '返点',field : 'outfmny',align:'right',formatter: fny},
	columns2[2]={width : '100',title : '预付款',field : 'ndemny',align:'right',formatter: fny},
	columns2[3]={width : '100',title : '返点',field : 'nderebmny',align:'right',formatter: fny},
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
					columns1[10+i]={width : "200",title :rows[i].head,field : hstr[i],align:"right",colspan:2 }
				}
				var j=4;
				for(var i=0;i<rows[0].num;i++){
					columns2[j]={width : "100",title : "预付款",field : hstr[i]+"1",align:"right",formatter: fny}
					j++;
					columns2[j]={width : "100",title : "返点",field : hstr[i]+"2",align:"right",formatter: fny}
					j++;
				}
				columns[0]=columns1;
				columns[1]=columns2;
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
	      columns : columns,
	      onLoadSuccess : function(data) {
				$.messager.progress('close');
				$("#qrydialog").hide();
				calFooter();
			}
	  });
}

function ftype(value,row,index){
	if (value == 1) {
		return '普通加盟商';
	}
	if (value == 2)
		return '金牌加盟商';
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
    var outymny = 0;	
    var outfmny = 0;	
    var ndemny = 0;	
    var nderebmny = 0;	
    var custsum = {};
    for(var i = 0; i <30; i++){
    	custsum['custsum'+i] = 0;
    }
    for (var i = 0; i < rows.length; i++) {
    	outymny += parseFloat(rows[i].outymny == undefined ? 0 : rows[i].outymny);
    	outfmny += parseFloat(rows[i].outfmny == undefined ? 0 : rows[i].outfmny);
    	ndemny += parseFloat(rows[i].ndemny == undefined ? 0 : rows[i].ndemny);
    	nderebmny += parseFloat(rows[i].nderebmny == undefined ? 0 : rows[i].nderebmny);
    	var w=0;
    	for(var j = 0; j <15; j++){
    		var num1=hstr[j]+"1";
    		var num2=hstr[j]+"2";
    		custsum['custsum'+w]+= parseFloat(rows[i][num1] == undefined ? 0 : rows[i][num1]);
    		w++;
    		custsum['custsum'+w]+= parseFloat(rows[i][num2] == undefined ? 0 : rows[i][num2]);
    		w++;
    	}
    }
    footerData['cname'] = '合计';
    footerData['outymny'] = outymny;
    footerData['outfmny'] = outfmny;
    footerData['ndemny'] = ndemny;
    footerData['nderebmny'] = nderebmny;
    var w=0;
    for(var i = 0; i <15; i++){
    	var num1=hstr[i]+"1";
		var num2=hstr[i]+"2";
 		footerData[num1] = custsum['custsum'+w];
 		w++;
 		footerData[num2] = custsum['custsum'+w];
 		w++;
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
	var columns=$('#grid').datagrid("options").columns[0];
	var qj =$('#jqj').html();
	Business.getFile(contextPath+ '/report/debitquery!print.action',{'strlist':JSON.stringify(datarows),
		'columns':JSON.stringify(columns),'qj':qj}, true, true);
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
	var callback=function(){
		var columns = $('#grid').datagrid("options").columns[1];
		var dateColumns = $('#grid').datagrid("options").columns[0];
		Business.getFile(DZF.contextPath+ '/report/debitquery!exportExcel.action',
				{'strlist':JSON.stringify(datarows),'columns':JSON.stringify(columns),
				'dateColumns':JSON.stringify(dateColumns)}, true, true);
	}
	checkBtnPower('export','channel23',callback);
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

