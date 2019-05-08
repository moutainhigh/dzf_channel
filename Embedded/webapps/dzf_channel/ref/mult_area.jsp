<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>多选省市参照</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
<script src=<%UpdateGradeVersion.outversion(out,"../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<%	
	String cityids = request.getParameter("cityids");
%>
<body>
	<script type="text/javascript">
		var contextPath = "<%=request.getContextPath()%>";
		var cityids = "<%=cityids%>";
		var crows=null;
		$(function() {
			$('#areaGrid').treegrid({
				url : contextPath + '/channel/packageDef!queryArea.action',
				idField : 'id',
				treeField : 'text',
				rownumbers : true,
				singleSelect : false,
				height : 360,
				pagination : false,
				columns : [ [ {
					field : 'ck', 
					title: '', 
					checkbox : true 
				},{
					field : 'id',
					hidden:true
				}, {
					field : 'text',
					title : '省市名称',
					width : 260
				},{
					field : 'parentId',
					hidden:true
				},
				] ],
				onLoadSuccess : function(row,data){
					if(data.rows&&data.rows.length>0){
						crows=data.rows;
					}
					var retrows=new Array();
					if(!isEmpty(crows)){
						for(var i=0;i<crows.length;i++){
							if(crows[i].children && crows[i].children.length>0){
								for(var j=0;j<crows[i].children.length;j++){
		        					retrows.push(crows[i].children[j]);
		        				}
							}
						}
					}
			   		if(retrows != null && retrows.length > 0){
						if(areamap != null && !areamap.isEmpty()){
							for(var i = 0; i < retrows.length; i++){
								if(areamap.containsKey(retrows[i].id)){
									$('#areaGrid').datagrid("checkRow",retrows[i]);
								}
							}
						}else{
							var cityid = new Array(); 
							cityid = cityids.split(","); 
							for(var i = 0; i < retrows.length; i++) { 
								if($.inArray(retrows[i].id, cityid) != -1){
									$('#areaGrid').treegrid("select",retrows[i].id);
									if(!aidlist.contains(retrows[i].id)){
										aidlist.add(retrows[i].id);
										arealist.add(retrows[i]);
										areamap.put(retrows[i].id,retrows[i]);
									}
								}
							}
						}
			   		}
				}, 
				onCheck : function(rowData){
					if(rowData){
						if(rowData.parentId != '1'){
							if(!aidlist.contains(rowData.id)){
								aidlist.add(rowData.id);
								arealist.add(rowData);
								areamap.put(rowData.id,rowData);
							}
						}else{
							$('#areaGrid').treegrid('unselect',rowData.id);
							$('#areaGrid').treegrid('uncheckRow',rowData);
						}
					}
				},
				onUncheck : function(rowData){
					if(arealist != null && arealist.size() > 0){
						if(aidlist.contains(rowData.id)){
							aidlist.removeObj(rowData.id);
							arealist.removeObj(rowData);
							areamap.remove(rowData.id);
						}
					}
				},
				onSelectAll : function(rows){
					for(var i = 0;i < rows.length; i++){
						if(rowData.parentId == '1'){
							continue;
						}
						if(!aidlist.contains(rows[i].id)){
							aidlist.add(rows[i].id);
							arealist.add(rows[i]);
							areamap.put(rows[i].id,rows[i]);
						}
					}
				},
				onUncheckAll : function(rows){
					for(var i = 0;i < rows.length; i++){
						aidlist.removeObj(rows[i].id);
						arealist.removeObj(rows[i]);
						areamap.remove(rows[i].id);
					}
				},
				onClickRow : function(rowData) {
					if(rowData.parentId == '1'){
						return ;
					}
				}
			});
	 		 $('#areaName').bind('keypress',function(event){
			        if(event.keyCode == "13") {//Enter 键事件
			        	var params = new Array();
			        	var areaName = $("#areaName").val();
			        	if(crows!=null){
				        	for(var i=0;i<crows.length;i++){
				        		if(crows[i].text.indexOf(areaName)>-1){
				        			params.push(crows[i])
				        		}else{
				        			if(crows[i].children&&crows[i].children.length>0){
				        				var ct = JSON.stringify(crows[i]);
				        				var crowi=JSON.parse(ct);
				        				var crowc=new Array();
				        				crowi.children=[];
				        				for(var j=0;j<crows[i].children.length;j++){
				        					if(crows[i].children[j].text.indexOf(areaName)>-1){
				        						crowc.push(crows[i].children[j]);
				        					}
				        				}
				        				if(crowc.length>0){
				        					crowi.children=crowc;
				        					params.push(crowi);
				        				}
				        			}
				        		}
				        	}
			        	}
			       		$('#areaGrid').treegrid('loadData',params); 
			        }
			    }); 
		});
	</script>
	<div class="mod-toolbar-top">
		<div class="search-toolbar-content">
			<div class="left search-crumb">
				<input style="height:35px;color:#999;float:center;width:80%"  id="areaName" value="请输入省市" 
				onFocus="if(value==defaultValue){value='';this.style.color='#000'}" 
				onBlur="if(!value){value=defaultValue;this.style.color='#999'}"/> 
			</div>
		</div>
	</div>
	<ul id="areaGrid"></ul>
</body>
</html>