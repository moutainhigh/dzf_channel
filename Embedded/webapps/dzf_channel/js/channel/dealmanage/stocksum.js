
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
		    pagination : true,// 分页工具栏显示
		    pageSize : DZF.pageSize,
		    pageList : DZF.pageList,
		    showFooter : false,
//		    sortName : "gcode",
//		    sortOrder : "asc",
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
//		            	      sortable : true,
		            	      rowspan:2,
		            	      
		            	    }, {
		            	      width : '100',
		            	      title : '商品',
		            	      field : 'gname',
		            	      halign : 'center',
		            	      align : 'left',
		            	      rowspan:2,
		            	      formatter : function(value, row, index) {
		              			return '<a href="javascript:void(0)"  style="color:blue" onclick="toRenew()">' + value + '</a>';
		                      }
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
			                   width : '100',
			                   title : '期初余额',
			                   field : 'balancestart',
			                   halign : 'center',
			                   align : 'center',
			                   colspan:3,
			              },{
			                   width : '100',
			                   title : '本期入库',
			                   field : 'instock',
			                   halign : 'center',
			                   align : 'center',
			                   colspan:2,
			              },{
			                   width : '100',
			                   title : '本期出库',
			                   field : 'outstock',
			                   halign : 'center',
			                   align : 'center',
			                   colspan:2,
			              },{
			                   width : '100',
			                   title : '期末余额',
			                   field : 'balanceend',
			                   halign : 'center',
			                   align : 'center',
			                   colspan:3,
			              },
		            	    
		             ] ,
        [
            { field : 'numstart', title : '数量', width : 100, halign:'center',align:'right'},
            { field : 'pricestart', title : '单价', width : 100, halign:'center',align:'right', formatter : function(value,row){if(value == null)return "0.00";return formatMny(value);}},
            { field : 'moneystart', title : '金额', width : 100, halign:'center',align:'right', formatter : function(value,row){if(value == null)return "0.00";return formatMny(value);}},
            { field : 'numin', title : '数量', width : 100, halign:'center',align:'right', formatter : function(value,row) { if(!isEmpty(row.gid)){if(value!=null){return value;}}} },
            { field : 'moneyin', title : '金额', width : 100,halign:'center',align:'right',formatter : function(value,row){if(value!=null)return formatMny(value);}},
            { field : 'numout', title : '数量', width : 100, halign:'center',align:'right', formatter : function(value,row) { if(!isEmpty(row.gid)){if(value!=null){return value;}}}},
            { field : 'moneyout', title : '金额', width : 100,halign:'center',align:'right',formatter : function(value,row){if(value!=null)return formatMny(value);}},
            { field : 'numend', title : '数量', width : 100, halign:'center',align:'right'},
            { field : 'priceend', title : '单价', width : 100, halign:'center',align:'right',formatter : function(value,row){if(value == null)return "0.00";return formatMny(value);}},
            { field : 'moneyend', title : '金额', width : 100, halign:'center',align:'right',formatter : function(value,row){if(value == null)return "0.00";return formatMny(value);}},
            
        ] ],
        
        onLoadSuccess : function(data) {
        	var rows = $('#grid').datagrid('getRows');
        	var numstart = 0;	
        	var pricestart = 0;	
        	var moneystart = 0;	
        	
        	var numin = 0;	
        	var moneyin = 0;
        	
        	var numout = 0;	
        	var moneyout = 0;
        	
        	var numend = 0;	
        	var priceend = 0;	
        	var moneyend = 0;	
        	
        	for (var i = 0; i < rows.length; i++) {
        		if(rows[i].numstart != undefined && rows[i].numstart != null){
        			numstart += parseFloat(rows[i].numstart);
        		}
        		if(rows[i].pricestart != undefined && rows[i].pricestart != null){
        			pricestart += parseFloat(rows[i].pricestart);
        		}
        		if(rows[i].moneystart != undefined && rows[i].moneystart != null){
        			moneystart += parseFloat(rows[i].moneystart);
        		}
        		
        		if(rows[i].numin != undefined && rows[i].numin != null){
        			numin += parseFloat(rows[i].numin);
        		}
        		if(rows[i].moneyin != undefined && rows[i].moneyin != null){
        			moneyin += parseFloat(rows[i].moneyin);
        		}
        		
        		if(rows[i].numout != undefined && rows[i].numout != null){
        			numout += parseFloat(rows[i].numout);
        		}
        		if(rows[i].moneyout != undefined && rows[i].moneyout != null){
        			moneyout += parseFloat(rows[i].moneyout);
        		}
        		
        		if(rows[i].numend != undefined && rows[i].numend != null){
        			numend += parseFloat(rows[i].numend);
        		}
        		if(rows[i].priceend != undefined && rows[i].priceend != null){
        			priceend += parseFloat(rows[i].priceend);
        		}
        		if(rows[i].moneyend != undefined && rows[i].moneyend != null){
        			moneyend += parseFloat(rows[i].moneyend);
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
	var url = DZF.contextPath + '/dealmanage/stocksum!query.action';
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
 * 跳转到出入库明细表页面
 */
function toRenew(){
	var url = 'channel/dealmanage/stockoutin.jsp?operate=toRenew';
	parent.addTabNew('出入库明细表', url);
}

