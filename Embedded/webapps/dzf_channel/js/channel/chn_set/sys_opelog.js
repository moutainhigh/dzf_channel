var dgmsinfo;
var contextPath = DZF.contextPath;
var obj  = null;
var objuser = null;
$(window).resize(function() {
	$('#dgmsinfo').datagrid('resize', {
		height : Public.setGrid().h,
		width : 'auto'
	});
});
// 扩展datagrid:动态添加删除editor
$(function() {
	// 下拉按钮的事件
	$("#cxjs").on("mouseover", function() {
		$("#contid1").show();
		$("#contid1").css("visibility", "visible");
	});
	$(".mod-inner,.mod-toolbar-top").on("click",function(){
		$("#contid1").hide();
    	$("#contid1").css("visibility","hidden");
	});

	$("#file_field").change(function() {
		$(".cell-btn").eq(1).attr("onclick", "showPicLocal()");
	});
	inittypebox();
	dgmsinfo = $('#dgmsinfo').datagrid({
		title : '',
		striped : true,
		rownumbers : true,
		singleSelect : true,
		pagination:true,
		pageSize: DZF.pageSize,
		pageList:DZF.pageList,
		height : Public.setGrid().h,
		width : 'auto',
		idField : 'id',
		columns : [ [ {
			field : 'id',
			title : '主键',
			width : 100,
			hidden : true
		}, {
			field : 'odate',
			title : '操作时间',
			width :180,
		}, {
			field : 'user',
			title : '操作用户',
			width : 180,
		}, {
			field : 'ip',
			title : 'ip地址',
			width : 180,
		}, {
			field : 'otype',
			title : '操作类型',
			width : 180,
			formatter:function(num, row, index){
				for(var i =0;i<obj.length;i++){
					if(num ==  obj[i].id){
						return obj[i].name;
					}
				}
	       	}
		}, {
			title : '操作说明',
			field : 'omsg',
			width : 400
		}, ] ],
		 onLoadSuccess : function () {
			 $('#dgmsinfo').datagrid("selectRow", 0);  
	     }
	});
	
	var date =parent.SYSTEM.LoginDate;
	var lastDay = new Date(date.substring(0,4), date.substring(5,7), 0);
	var lastmon = lastDay.toDateString().substring(8, 10);
	$("#begindate1").datebox("setValue", date.substring(0,7)+"-"+"01");
	$("#enddate").datebox("setValue", date.substring(0,7)+"-"+lastmon);
	$("#jqj").text(date.substring(0,7)+"-"+"01"+" 至  "+date.substring(0,7)+"-"+lastmon);
});

function inittypebox(){
	$.ajax({
		type : "post",
		dataType : "json",
		url : DZF.contextPath + '/sys/sys_opelog!queryType.action',
		traditional : true,
		async : false,
		success : function(data, textStatus) {
			$("#opetype").combobox("loadData",data);
			obj = data;
		},
		error: function(err){
			Public.tips({ type : 1, content : '操作失败' });
		}
	});
	
	
	$.ajax({
		type : "post",
		dataType : "json",
		url : DZF.contextPath + '/sys/sys_opelog!queryOpeUser.action',
		traditional : true,
		async : false,
		success : function(data, textStatus) {
			$("#opeuser").combobox("loadData",data);
			objuser = data;
		},
		error: function(err){
			Public.tips({ type : 1, content : '操作失败' });
		}
	});
	
}

function reloadData() {
	$('#dgmsinfo').datagrid('options').url = contextPath + '/sys/sys_opelog!query.action';
	
	$('#dgmsinfo').datagrid('load', {
		begindate : $("#begindate1").datebox("getValue"),
		enddate : $("#enddate").datebox("getValue"),
		otpye : $("#opetype").combobox("getValue"),
		omsg : $("#opemsg").textbox("getValue"),
		opeuser : $("#opeuser").combobox("getValue"),
		ident : 3
	});
	
	$("#jqj").html(
			$("#begindate1").datebox("getValue") + " 至  " + $("#enddate").datebox("getValue"));
	$("#contid1").css("visibility", "hidden");
}

// 查询框关闭事件
function closeCx() {
	$("#contid1").css("visibility", "hidden");
}

/**
 * 直接打印
 */
function directPrint(){
	var datarows = $('#dgmsinfo').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'当前界面数据为空',type:2});
		return;
	}
	var columns = $('#dgmsinfo').datagrid("options").columns[0];
	Business.getFile(contextPath+ '/sys/sys_opelog!print.action',{'strlist':JSON.stringify(datarows),
		'columns':JSON.stringify(columns),
		'begindate' : $("#begindate1").datebox("getValue"),
		'enddate' : $("#enddate").datebox("getValue"),
		'otpye' : $("#opetype").combobox("getValue"),
		'omsg' : $("#opemsg").textbox("getValue"),
		'opeuser' : $("#opeuser").combobox("getValue"),
		'ident' : 3
	}, true, true);
}

function expexcel() {
	//导出操作
	var datarows = $('#dgmsinfo').datagrid("getRows");
	if(datarows.length == 0){
		Public.tips({content:'当前界面数据为空',type:2});
		return;
	}
	Business.getFile(DZF.contextPath +"/sys/sys_opelog!excelReport.action", {
		begindate : $("#begindate1").datebox("getValue"),
		enddate : $("#enddate").datebox("getValue"),
		otpye : $("#opetype").combobox("getValue"),
		omsg : $("#opemsg").textbox("getValue"),
		opeuser : $("#opeuser").combobox("getValue"),
		ident : 3
	}, true, true);
}
