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
				var rowindex= $("#gysgrid").datagrid('getRowIndex',$("#gysgrid").datagrid('getSelected'));
				$('#gysgrid').datagrid('unselectRow',rowindex);
				$('#gysgrid').datagrid('selectRow',rowindex+1);
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
		grid = $('#gysgrid').datagrid({
		    url: DZF.contextPath + '/dealmanage/stockin!querySupplierRef.action',
		    method: 'post',
			fitColumns: true,
			idField:'suid',
			rownumbers : true,
			singleSelect : false,
// 			pagination : true,
// 			pageSize:10,
// 		    pageList:[10,20,30,40,50],
// 			showFooter : true,
			height:330,
			striped:true,
		   
			columns : [ [ {
						field : 'suid',
						title : '主键id',
						hidden : true
					}, {
						field : 'name',
						title : '供应商名称',
						width : 800
					} ] ],
			onDblClickRow : function(rowIndex, rowData) {
				dClickSupplier(rowData);
			},
			onLoadSuccess : function(data) {
				var retrows = $("#gysgrid").datagrid('getRows');
			},
		});

		$('#stcode').bind('keypress', function(event) {
			if (event.keyCode == "13") {//Enter 键事件
				var params = new Object();
				params["cpcode"] = $("#stcode").val();
				grid.datagrid('load', params);
			}
		});
	});
</script>
	<div  id="cardList">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-corp">
					<input id="stcode" value="请输入供应商名称" 
						onFocus="if(value==defaultValue){value='';this.style.color='#000'}" 
						onBlur="if(!value){value=defaultValue;this.style.color='#999'}" />
				</div>
			</div>
		</div>
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="gysgrid"></table>
			</div>
		</div>
	</div>
</body>
</html>
