<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>选择加盟商</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
<script src=<%UpdateGradeVersion.outversion(out,"../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<%
	String ovince = request.getParameter("ovince");
	String corpids = request.getParameter("corpids");
	String corpid = request.getParameter("corpid");
%>
<body>
<style>
.mod-corp {
	width: 90%;
	margin: 4px auto;
	text-align: center;
}

.mod-corp input {
	border: 1px #ddd solid;
	width: 87%;
	height: 30px;
	padding: 0px 5px;
	color: #666;
	border-radius: 5px;
	background-color: #fff;
	text-align: center;
	outline: none;
}
</style>

<script>
	$(document).ready(function(){
		$(document).on('keypress', function(e) {
			 if(e.keyCode == 13 && e.target.type!== 'submit'&&e.target.type!=='text') {
				var rowindex= $("#gsTable").datagrid('getRowIndex',$("#gsTable").datagrid('getSelected'));
				$('#gsTable').datagrid('unselectRow',rowindex);
				$('#gsTable').datagrid('selectRow',rowindex+1);
			 }
			});
	});//enter 键代替tab键换行        end
 	var JPlaceHolder = {
	    //检测
	    _check : function(){
	        return 'placeholder' in document.createElement('input');
	    },
	    //初始化
	    init : function(){
	        if(!this._check()){
	            this.fix();
	        }
	    },
	    //修复
	    fix : function(){
	        jQuery(':input[placeholder]').each(function(index, element) {
	            var self = $(this), txt = self.attr('placeholder');
	            self.wrap($('<div></div>').css({position:'relative', zoom:'1', border:'none', 
	            	background:'none', padding:'none', margin:'none'}));
	            var pos = self.position(), h = self.outerHeight(true), paddingleft = self.css('padding-left');
	            var holder = $('<span></span>').text(txt).css({position:'absolute', left:150, top:4, 
	            	height:h, color:'#333'}).appendTo(self.parent());
	            self.focusin(function(e) {
	                holder.hide();
	            }).focusout(function(e) {
	                if(!self.val()){
	                    holder.show();
	                }
	            });
	            holder.click(function(e) {
	                holder.hide();
	                self.focus();
	            });
	        });
	    }
	}; 
	
	var rows = null;
	$(function(){
		var ovince = <%=ovince%>;
		var corpids = '<%=corpids%>';
		var corpid = '<%=corpid%>';
		if(isEmpty(ovince)){
			ovince=-1;
		}
		var params = new Object();
		grid = $('#gsTable').datagrid({
		    url: DZF.contextPath + '/sys/sys_inv_manager!queryChannel.action',
		    method: 'post',
			fitColumns: true,
			idField:'pk_gs',
			rownumbers : true,
			singleSelect : false,
// 			pagination : true,
// 			pageSize:10,
// 		    pageList:[10,20,30,40,50],
// 			showFooter : true,
			height:330,
			striped:true,
			queryParams: {'dr':ovince,'vmome':corpids,'rows':100},
		    columns: [[   {field:'ck', checkbox:true },
			               {field:'pk_gs', title:'主键id', hidden:true},
			     		   {field:'incode',title:'公司编码',width:500},
			               {field:'uname',title:'公司名称',width:500}
			   	 	]],
			onDblClickRow:function(rowIndex, rowData){
				var rowTable = $('#gsTable').datagrid('getSelections');
				if(rowTable && rowTable[rowTable.length-1] == rowData){
					//如果最后选择的数据和双击的数据一样，则不重复
				}else{
					rowTable.push(rowData);
				}
				dClickCompany(rowTable);
				rowTable = $('#gsTable').datagrid('clearSelections');
			},
			onLoadSuccess: function (data) {
				var retrows = $("#gsTable").datagrid('getRows');
		   		if(retrows != null && retrows.length > 0){
					if(selmap != null && !selmap.isEmpty()){
						for(var i = 0; i < retrows.length; i++){
							if(selmap.containsKey(retrows[i].pk_gs)){
								$('#gsTable').datagrid("checkRow",i);
							}
						}
					}else{
						var id = corpid.split(","); 
						if(!isEmpty(id)){
							for(var i = 0; i < retrows.length; i++) { 
								if($.inArray(retrows[i].pk_gs, id) != -1){
									$('#gsTable').datagrid("checkRow",i);
									if(!uidlist.contains(retrows[i].pk_gs)){
										uidlist.add(retrows[i].pk_gs);
										sellist.add(retrows[i]);
										selmap.put(retrows[i].pk_gs,retrows[i]);
									}
								}
							}
						}
					}
		   		}
			},
			onCheck : function(rowIndex,rowData){
				if(!uidlist.contains(rowData.pk_gs)){
					uidlist.add(rowData.pk_gs);
					sellist.add(rowData);
					selmap.put(rowData.pk_gs,rowData);
				}
			},
			onUncheck : function(rowIndex,rowData){
				if(sellist != null && sellist.size() > 0){
					if(uidlist.contains(rowData.pk_gs)){
						uidlist.removeObj(rowData.pk_gs);
						sellist.removeObj(rowData);
						selmap.remove(rowData.pk_gs);
					}
				}
			},
			onSelectAll : function(rows){
				for(var i = 0;i < rows.length; i++){
					if(!uidlist.contains(rows[i].pk_gs)){
						uidlist.add(rows[i].pk_gs);
						sellist.add(rows[i]);
						selmap.put(rows[i].pk_gs,rows[i]);
					}
				}
			},
			onUncheckAll : function(rows){
				for(var i = 0;i < rows.length; i++){
					uidlist.removeObj(rows[i].pk_gs);
					sellist.removeObj(rows[i]);
					selmap.remove(rows[i].pk_gs);
				}
			}
		});
	
		 $('#unitcode').bind('keypress',function(event){
		       if(event.keyCode == "13") {//Enter 键事件
		    	   var filtername = $("#unitcode").val(); ; 
		      		var params = new Object();
		      		//params["corpname"] = filtername;
		      		params["corpcode"] = filtername;
		      		params["dr"] = ovince;
		      		params["rows"] = 100;
		      		grid.datagrid('load',params); 
		       }
		   }); 
	});
</script>
	<div  id="cardList">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-corp">
					<input id="unitcode" value="请输入编码或名称" 
						onFocus="if(value==defaultValue){value='';this.style.color='#000'}" 
						onBlur="if(!value){value=defaultValue;this.style.color='#999'}" />
				</div>
			</div>
		</div>
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="gsTable"></table>
			</div>
		</div>
	</div>
</body>
</html>
