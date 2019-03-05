
var contextPath = DZF.contextPath;
var editIndex;
var status = "brows";

$(function(){
	initQry();
	initCombobox();
	load();
	loadJumpData();
});

/**
 * 查询初始化
 */
function initQry(){
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	queryBoxChange('#begdate','#enddate');
	$("#begdate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#enddate").datebox("setValue",parent.SYSTEM.LoginDate);
	$("#jqj").html(parent.SYSTEM.PreDate+" 至  "+parent.SYSTEM.LoginDate);
}

function initCombobox(){
	$("#goodsname").combobox({
		onShowPanel: function () {
			initType();
        }
    })
}

/**
 * 查询商品下拉
 */

function initType(){
	$.ajax({
		type : 'POST',
		async : false,
	    url : DZF.contextPath + '/dealmanage/stockoutin!queryComboBox.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result+ ')');
			if (result.success) {
				$('#goodsname').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
};

/**
 * 列表表格加载
 */
function load(){
	grid = $('#grid').datagrid({
		    border : true,
		    striped : true,
		    rownumbers : true,
		    fitColumns : false,
		    height : Public.setGrid().h,
		    singleSelect : true,
		    checkOnSelect : false,
//		    pagination : true,// 分页工具栏显示
//		    pageSize : DZF.pageSize,
//		    pageList : DZF.pageList,
		    showFooter : false,
		    sortName : "gcode",
		    sortOrder : "asc",
		    remoteSort : false,
		    showFooter: true,
		columns : [ 
		            [ 
		            	 {
		            	      width : '100',
		            	      title : '商品编码',
		            	      align : 'left',
		            	      halign : 'center',
		            	      field : 'gcode',
		            	      sortable : true,
		            	      rowspan:2,
		            	      
		            	    }, {
		            	      width : '150',
		            	      title : '商品',
		            	      field : 'gname',
		            	      halign : 'center',
		            	      align : 'left',
		            	      rowspan:2,
		            	    }, {
		            	      width : '100',
		            	      title : '规格',
		            	      field : 'spec',
		            	      halign : 'center',
		            	      align : 'center',
		            	      rowspan:2,
		            	    }, {
			            	    width : '100',
			            	    title : '型号',
			            	    field : 'type',
			            	    halign : 'center',
			            	    align : 'center',
			            	    rowspan:2,
			            	 }, {
		            	       field : 'contime',
		            	       title : '时间',
		            	       width : '150',
		            	       halign : 'center',
		            	       align : 'center',
		            	       rowspan:2,
		            	       formatter : function(value,row) {
		            	           if(!isEmpty(row.gcode)){
		            	             if (row.itype == '0'){
		            	               return $("#begdate").datebox('getValue');
		            	           }else{
		            	        	   //value=value.substring(0,10);
		            	               return value;
		            	           }
		            	         }
		            	      }
		            	    },{
		            	       width : '80',
		            	       title : '业务',
		            	       field : 'itype',
		            	       halign : 'center',
		            	       align : 'center',
		            	       rowspan:2,
		            	       formatter : function(value,row) {
		            	           if(!isEmpty(row.gcode)){
		            	             if (value == '1')
		            	               return '商品入库';
		            	             if (value == '2')
		            	               return '销售卖出';
		            	             if (value =='0')
		            	               return '期初余额';
		            	             if (value == '3')
			            	           return '其他出库';
		            	           }else{
		            	             return value;
		            	           }
		            	         }
		            	   },{
			            	   width : '120',
			            	   title : '关联单据',
			            	   field : 'vcode',
			            	   align : 'left',
			            	   halign : 'center',
			            	   rowspan:2,
			            	   formatter : function(value,row) {
		            	           if(!isEmpty(row.gcode)){
		            	             if (row.itype == '0')
		            	             return null;
		            	             else{
		            	            	 return value;
		            	             }
		            	           }else{
		            	        	   return "合计"; 
		            	           }
		            	         }
			              },{
			                   width : '100',
			                   title : '入库',
			                   field : 'instock',
			                   halign : 'center',
			                   align : 'center',
			                   colspan:3,
			              },{
			                   width : '100',
			                   title : '卖出',
			                   field : 'outstock',
			                   halign : 'center',
			                   align : 'center',
			                   colspan:3,
			              },{
			                   width : '100',
			                   title : '结存',
			                   field : 'balance',
			                   halign : 'center',
			                   align : 'center',
			                   colspan:3,
			              },
		            	    
		             ] ,
        [
            { field : 'numin', title : '数量', width : 100, halign:'center',align:'right',formatter : function(value,row) { if(!isEmpty(row.gcode)){if(row.itype!='1'){return '';}else{ if(row.itype=='1'){return value;} }} else{return value}}}, 
            { field : 'pricein', title : '单价', width : 100,halign:'center',align:'right',
            	formatter : function(value,row) {
            		if(!isEmpty(row.gcode)){
            			if(isEmpty(row.itype) || row.itype!='1'){
            				return '';
            			}else{
            				return formFourMny(value);
        				}
            		}
            	}}, 
            { field : 'moneyin', title : '金额', width : 100,halign:'center',align:'right',
                	formatter : function(value,row) {
                		if(!isEmpty(row.gcode)){
                			if(isEmpty(row.itype) || row.itype!='1'){
                				return '';
                			}else{
                				return formFourMny(value);
            				}
                		}
                	}}, 
            { field : 'numout', title : '数量', width : 100, halign:'center',align:'right',formatter : function(value,row){ if(!isEmpty(row.gcode)){if(row.itype!='2'&& row.itype!='3'){return '';}else{ if(row.itype=='2'||row.itype=='3'){return value;} }} else{return value}}}, 
            { field : 'priceout', title : '单价', width : 100,halign:'center',align:'right',
            	formatter : function(value,row) {
            		if(!isEmpty(row.gcode)){
            			if(row.itype=='1' || isEmpty(row.itype)){
            				return '';
            			}else {
            				return formFourMny(value);
        				}
            		}
            	}},             
            { field : 'moneyout', title : '金额', width : 100,halign:'center',align:'right',
                   	formatter : function(value,row) {
                		if(!isEmpty(row.gcode)){
                			if(isEmpty(row.itype) || row.itype=='1'){
                				return '';
                			}else{
                				return formFourMny(value);
            				}
                		}
                	}}, 
            { field : 'numb', title : '数量', width : 100, halign:'center',align:'right',
            	formatter : function(value,row) {
            		if(!isEmpty(row.gcode)){
            			if(value=='0'){
            				return "0";
            			}else{
            				return value;
            			}
            		}
            }}, 
            { field : 'priceb', title : '单价', width : 100, halign:'center',align:'right',
            	formatter :formFourMny},  
            { field : 'moneyb', title : '金额', width : 100, halign:'center',align:'right',
                formatter : formFourMny},  
        ] ],
        onLoadSuccess : function(data) {
        	calFooter();
        	var rows = $('#grid').datagrid('getRows');
        	var numin = 0;	
        	var pricein = 0;	
        	var moneyin = 0;
        	
        	var numout = 0;	
        	var priceout = 0;	
        	var moneyout = 0;
        	
        	var numb = 0;	
        	var priceb = 0;	
        	var moneyb = 0;	
        	
        	for (var i = 0; i < rows.length; i++) {
        		if(rows[i].numin != undefined && rows[i].numin != null){
        			numin += parseFloat(rows[i].numin);
        		}
        		if(rows[i].pricein != undefined && rows[i].pricein != null){
        			pricein += parseFloat(rows[i].pricein);
        		}
        		if(rows[i].moneyin != undefined && rows[i].moneyin != null){
        			moneyin += parseFloat(rows[i].moneyin);
        		}
        		
        		if(rows[i].numout != undefined && rows[i].numout != null){
        			numout += parseFloat(rows[i].numout);
        		}
        		if(rows[i].priceout != undefined && rows[i].priceout != null){
        			priceout += parseFloat(rows[i].priceout);
        		}
        		if(rows[i].moneyout != undefined && rows[i].moneyout != null){
        			moneyout += parseFloat(rows[i].moneyout);
        		}
        		
        		if(rows[i].numb != undefined && rows[i].numb != null){
        			numb += parseFloat(rows[i].numb);
        		}
        		if(rows[i].priceb != undefined && rows[i].priceb != null){
        			priceb += parseFloat(rows[i].priceb);
        		}
        		if(rows[i].moneyb != undefined && rows[i].moneyb != null){
        			moneyb += parseFloat(rows[i].moneyb);
        		}
        	}
        },
	});
}

//金额，保留小数点后4位
function formFourMny(value) {
	if(isEmpty(value)){
		return "0.0000";
	}else{
		return value.toFixed(4);
	}
}

/**
 * 计算合计
 */
function calFooter(){
	var rows = $('#grid').datagrid('getRows');
	var footerData = new Object();
	var numin = 0;	
	var moneyin = 0;
	var numout = 0;	
	var moneyout = 0;
	for (var i = 0; i < rows.length; i++) {
		numin += getFloatValue(rows[i].numin);
		moneyin += getFloatValue(rows[i].moneyin);
		numout += getFloatValue(rows[i].numout);
		moneyout += getFloatValue(rows[i].moneyout);
	  
	}

	 footerData['numin'] = numin;
	 footerData['moneyin'] = moneyin;
	 footerData['numout'] = numout;
	 footerData['moneyout'] = moneyout;
    var fs=new Array(1);
    fs[0] = footerData;
    $('#grid').datagrid('reloadFooter',fs);
}


String.prototype.startWith=function(str){
	var reg=new RegExp("^"+str);
	return reg.test(this);
	}


/**
 * 查询数据
 */
function reloadData(){
	var url = DZF.contextPath + '/dealmanage/stockoutin!query.action';
	$('#grid').datagrid('options').url = url;
	var gids=$('#goodsname').combobox('getValues');
	var strgids="";
	for(i=0;i<gids.length;i++){
		strgids+=","+gids[i];
	}
	strgids=strgids.substring(1);
	$('#grid').datagrid('load', {
		'begdate' : $("#begdate").datebox('getValue'),
		'enddate' : $("#enddate").datebox('getValue'),
		'vcode' : $("#qvcode").val(),
		'gid' :  strgids,
	});
	$('#grid').datagrid('clearSelections');
	$('#grid').datagrid('clearChecked');
	$('#qrydialog').hide();
}

/**
 * 清除查询条件
 */
function clearParams(){
	$("#qvcode").textbox('setValue',null);
	$("#goodsname").combobox('clear');
}

/**
 * 取消
 */
function closeCx(){
	$("#qrydialog").hide();
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
		var qj = $('#begdate').datebox('getValue') + '至' + $('#enddate').datebox('getValue');
		Business.getFile(DZF.contextPath+ '/dealmanage/stockoutin!exportAuditExcel.action',
				{'strlist':JSON.stringify(datarows),'columns':JSON.stringify(columns),'qj':qj,}, true, true);

}

/**
 * 跳转到此页面
 */
function loadJumpData() {
	 var obj = Public.getRequest();
	 var gid=obj.gid;
	 var pk_goodsspec=obj.pk_goodsspec;
	 var begdate=obj.begdate;
	 var enddate=obj.enddate;
	 var operate = obj.operate;
	 if (operate == "toDetail"){
		 $('#grid').datagrid('options').url = DZF.contextPath + "/dealmanage/stockoutin!query.action";
			$('#grid').datagrid('load', 
										{"gid" : gid,
										"begdate" : begdate,
										"enddate" : enddate,
										"pk_goodsspec" :pk_goodsspec,
								        });
			$("#begdate").datebox("setValue", begdate);
			$("#enddate").datebox("setValue",enddate);
			$("#jqj").html(begdate+" 至  "+enddate);
	 }else{
		 reloadData();
	 }
}

