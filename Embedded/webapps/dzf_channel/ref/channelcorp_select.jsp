<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>选择渠道商</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<body>
<style>
	.mod-corp{ width: 90%;margin: 4px auto;text-align:center;}
	.mod-corp input{ border: 1px #ddd solid; width: 87%; height: 30px;  padding: 0px 5px; color: #666;border-radius: 5px; background-color: #fff; text-align: center;outline: none;}
</style>
<script>

$(document).ready(function(){
	$(document).on('keypress', function(e) {
		 if(e.keyCode == 13 && e.target.type!== 'submit'&&e.target.type!=='text') {
			var rowindex= $("#gsTable").datagrid('getRowIndex',$("#gsTable").datagrid('getSelected'));
			$('#gsTable').datagrid('unselectRow',rowindex);
			 $('#gsTable').datagrid('selectRow',rowindex+1);
			 //$('#leftGrid').datagrid('selectRow',2);
		 }
		});
});//enter 键代替tab键换行        end
var rows=[];
$(function(){
	$.ajax({
		type: "POST",
		url: DZF.contextPath + "/sys/sys_channel_approve!queryChannelBusiness.action",
		cache: false,
		async: true,
		dataType: "json",
		data: {
			location: $("#area_select").textbox("getValue")
		},
		success: function(data, status) {
			var corps = data.rows;
			if ($("#hideGroup").val() == "Y") {
				for (var i = 0; i < corps.length; i++) {
					if ("000001" != corps[i].pk_gs) {
						rows.push(corps[i]);
					}
				}
			} else {
				rows = corps;
			}
			var t_heigth = 300;
			$('#gsTable').datagrid({
			    url: '',
			    method: 'get',
			    //fit:true,
				fitColumns: true,
				idField:'pk_gs',
				rownumbers: true,
				singleSelect:true,
				pagination:false,
				showFooter: true,
				height:t_heigth,
				striped:true,
				data: rows,
			    columns:[[
			     		  {field:'incode',title:'公司编码',width:500},
			              {field:'uname',title:'公司名称',width:500}
			   	 ]],
				onDblClickRow(){
					selectCompany();
				},
				onLoadSuccess: function () {
					$('#gsTable').datagrid('selectRow', 0);
				}
			});
		}
	});

	$("#unitcode").change(function () {
		var filtername = $("#unitcode").val();
		if (filtername != "") {
			var jsonStrArr = [];
			if(rows){
				for(var i=0;i<rows.length;i++){
					var row = rows[i];
					if(row["ucode"].indexOf(filtername) >= 0 || row.uname.indexOf(filtername) >= 0){
						jsonStrArr.push(row);
					}
				}
				$('#gsTable').datagrid('loadData',jsonStrArr);
			}
		}else{
			$('#gsTable').datagrid('loadData',rows);
		}
   });
});
</script>
	<div  id="cardList">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-corp">
					<input id="unitcode" placeholder="请输入编码或名称" />
				</div>
			</div>
		</div>
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="gsTable"></table>
			</div>
		</div>
	</div>
		<!-- <table id="gsTable" ></table>-->
</body>
</html>
