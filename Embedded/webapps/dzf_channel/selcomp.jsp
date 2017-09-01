<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>

<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>会计管理</title>
<jsp:include page="./inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "./css/index.css");%> rel="stylesheet" />
<link href=<%UpdateGradeVersion.outversion(out, "./css/main.css");%> rel="stylesheet" />

</head>
<body>

<script>
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
	            self.wrap($('<div></div>').css({position:'relative', zoom:'1', border:'none', background:'none', padding:'none', margin:'none'}));
	            var pos = self.position(), h = self.outerHeight(true), paddingleft = self.css('padding-left');
	            var holder = $('<span></span>').text(txt).css({position:'absolute', left:150, top:4, height:h, color:'#333'}).appendTo(self.parent());
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
 
	var rows=null;
$(function(){
	$('#gsTable').datagrid({   
	    url:'${pageContext.request.contextPath}/sys/sm_user!gsQueryAdmin.action',
	    method: 'post',
	    //fit:true,
		fitColumns: true,
		idField:'pk_gs',
		rownumbers: true,
		singleSelect:true,
		pagination:false,
		showFooter: true,
		striped:true,
		height : 350,
	    columns:[[   
	 			 {field:'incode',title:'公司编码',width:150},
	              {field:'uname',title:'公司名称',width:450}  
	   	 ]],
	   	onLoadSuccess:function(data){
	   		if(data.rows.length>0){
	   			if(rows==null){
		   			rows = data.rows;
	   			}
				$('#gsTable').datagrid('selectRow', 0);
	   		}
		}
	});  
	 $("#unitcode").keyup(function (e) { 
		 if(e.which == 13 && !e.shiftKey){
			 var filtername = $("#unitcode").val(); 
				if (filtername != "") {
					var jsonStrArr = [];
					if(rows){
						for(var i=0;i<rows.length;i++){
							var row = rows[i];
							if(row["incode"].indexOf(filtername) >= 0 || row.uname.indexOf(filtername) >= 0){
								jsonStrArr.push(row);
							} 
						}
						$('#gsTable').datagrid('loadData',jsonStrArr);   
					}
				}else{
					$('#gsTable').datagrid('loadData',rows);
				}        
		 }
		  
   });
	 JPlaceHolder.init(); 
});
</script>
<div class="wrapper" id="cardList">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<input  id="unitcode" placeholder="请输入编码或名称" /> 
				</div>
			</div>
		</div>
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="gsTable"></table>
			</div>
		</div>
	</div>
		<!-- <table id="gsTable" data-options="fit:true"></table> -->
</body>
</html>