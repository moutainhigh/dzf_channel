
var contextPath = DZF.contextPath;
var editIndex;
var status = "brows";

$(function(){
	initQry();
	initCombobox();
	load();
	reloadData();
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
 * 查询出入库类别下拉
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
		    singleSelect : false,
		    checkOnSelect : false,
		    pagination : true,// 分页工具栏显示
		    pageSize : DZF.pageSize,
		    pageList : DZF.pageList,
		    showFooter : false,
		    sortName : "gcode",
		    sortOrder : "asc",
		    remoteSort : false,
		    idField : 'sid',
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
		            	      width : '100',
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
		            	           if(!isEmpty(row.gid)){
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
		            	           if(!isEmpty(row.gid)){
		            	             if (value == '1')
		            	               return '商品入库';
		            	             if (value == '2')
		            	               return '销售出库';
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
			            	   title : '单据编码',
			            	   field : 'vcode',
			            	   align : 'left',
			            	   halign : 'center',
			            	   rowspan:2,
			            	   formatter : function(value,row) {
		            	           if(!isEmpty(row.gid)){
		            	             if (row.itype == '0')
		            	             return null;
		            	             else{
		            	            	 return value;
		            	             }
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
			                   title : '出库',
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
            { field : 'numin', title : '数量', width : 100, halign:'center',align:'right',formatter : function(value,row) { if(!isEmpty(row.gid)){if(row.itype!='1'){return '';}else{ if(row.itype=='1'){return value;} }}}}, 
            { field : 'pricein', title : '单价', width : 100,halign:'center',align:'right',formatter : function(value,row) { if(!isEmpty(row.gid)){if(row.itype!='1'){return '';}else{if(value == '0')return "0.00";if(row.itype=='1'){return formatMny(value);} }}}}, 
            { field : 'moneyin', title : '金额', width : 100,halign:'center',align:'right',formatter : function(value,row) { if(!isEmpty(row.gid)){if(row.itype!='1'){return '';}else{if(value == '0')return "0.00";if(row.itype=='1'){return formatMny(value);} }}}},
            { field : 'numout', title : '数量', width : 100, halign:'center',align:'right',formatter : function(value,row){ if(!isEmpty(row.gid)){if(row.itype!='2'&& row.itype!='3'){return '';}else{ if(row.itype=='2'||row.itype=='3'){return value;} }}}}, 
            { field : 'priceout', title : '单价', width : 100,halign:'center',align:'right',formatter : function(value,row) { if(!isEmpty(row.gid)){if(row.itype!='2'&& row.itype!='3'){return '';}else{if(value == '0')return "0.00";if(row.itype=='2'||row.itype=='3'){return formatMny(value);} }}}},
            { field : 'moneyout', title : '金额', width : 100,halign:'center',align:'right',formatter : function(value,row) { if(!isEmpty(row.gid)){if(row.itype!='2'&& row.itype!='3'){return '';}else{if(value == '0')return "0.00";if(row.itype=='2'||row.itype=='3'){return formatMny(value);} }}}},
            { field : 'numb', title : '数量', width : 100, halign:'center',align:'right',formatter : function(value,row) {if(!isEmpty(row.gid)){if(value=='0'){return "0";}else{return value;}}}}, 
            { field : 'priceb', title : '单价', width : 100, halign:'center',align:'right',formatter : function(value,row){ if(!isEmpty(row.gid)){if(value=='0'||value==null){return "0.00";}return formatMny(value);}}}, 
            { field : 'moneyb', title : '金额', width : 100, halign:'center',align:'right',formatter : function(value,row){ if(!isEmpty(row.gid)){if(value=='0'){return "0.00";}return formatMny(value);}}}, 
            
        ] ],
        onLoadSuccess : function(data) {
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
	//var itype=$('#itype').combobox('getValue');
	var gids=$('#goodsname').combobox('getValues');
	var strgids="";
	for(i=0;i<gids.length;i++){
		strgids+=","+gids[i];
	}
	strgids=strgids.substring(1);
	/*if(isEmpty(itype)){
		itype=111;
	}*/
	$('#grid').datagrid('load', {
		'begdate' : $("#begdate").datebox('getValue'),
		'enddate' : $("#enddate").datebox('getValue'),
		'vcode' : $("#qvcode").val(),
		'gid' :  strgids,
		//'itype' :  itype,
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
	//$("#itype").combobox('setValue',null);
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


