<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>区域选择</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
<!-- <link href="./css/mian.css" rel="stylesheet"> -->
</head>
<body>
	<script type="text/javascript">
		var contextPath = "<%=request.getContextPath()%>";
		$(function(){
			$('#treeGrid').tree({
				url:contextPath+'/area/areasearch!query.action',
				lines: true,
				method: 'post',
				height:100,
				//fit:true,
				loadFilter: function(data,parent){
					//$('#treeGrid').tree('collapseAll');
					return data;
				},
				onLoadSuccess: function () {
					$('#treeGrid').tree('collapseAll');
				},
				onDblClick : function(node) {
					if(!$('#treeGrid').tree('isLeaf',node.target)){
						$(node.target).removeClass("tree-node-selected");
						return false;
					}
					selectArea();  
				}, 
				onClick:function (node) {
					var pName = $('#treeGrid').tree('getParent',node.target); //获得选中数据的父节点
					var row = $('#treeGrid').tree('getSelected'); 
				},
				onDblClickRow : function(node) {
					if(!$('#treeGrid').tree('isLeaf',node.target)){
						$(node.target).removeClass("tree-node-selected");
						return false;
					}
					selectZclb();
				}, 
				onSelect:function(node){
					if(!$('#treeGrid').tree('isLeaf',node.target)){
						$(node.target).removeClass("tree-node-selected");
						return false;
					}
				}  
			}); 

			
		}); 
	</script>
	<div >
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content" style="height:40px;height:43px;">
				<div class="left mod-crumb" style = "width:90%;margin: 4px auto 0;text-align:center;">
					<input  id="zcmc" placeholder="请输入区域名称" style=" border: 1px #ddd solid; width: 87%; height: 30px; line-height:30px;font-family: Microsoft YaHei;padding: 0px 5px; color: #333;border-radius: 5px; background-color: #fff; text-align: center;outline: none;" /> 
				</div>
			</div>
		</div>
		<div class="mod-inner">
			<div  style="height: 200px;" >
				<table id="treeGrid" ></table>
			</div>
		</div>
	</div>
</body>
</html>