<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>渠道经理参照</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>

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
					var rowindex= $("#mgrid").datagrid('getRowIndex',$("#mgrid").datagrid('getSelected'));
					$('#mgrid').datagrid('unselectRow',rowindex);
					$('#mgrid').datagrid('selectRow',rowindex+1);
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
			var params = new Object();
			grid = $('#mgrid').datagrid({
			    url: DZF.contextPath + '/rebate/rebateinput!queryManager.action',
			    method: 'post',
				fitColumns: true,
				idField:'uid',
				rownumbers : true,
				singleSelect : false,
				pagination : true,
				pageSize:10,
			    pageList:[10,20,30,40,50],
				showFooter : true,
				height:330,
				striped:true,
			    columns:[[{field:'uid', title:'主键',checkbox:true},
			     		  {field:'ucode',title:'编码',width:500},
			              {field:'uname',title:'渠道经理名称',width:500}
			   	 ]],
				onDblClickRow:function(rowIndex, rowData){
					var rowTable = $('#mgrid').datagrid('getSelections');
					if(rowTable && rowTable[rowTable.length-1] == rowData){
						//如果最后选择的数据和双击的数据一样，则不重复
					}else{
						rowTable.push(rowData);
					}
					dClickMans(rowTable);
					rowTable = $('#mgrid').datagrid('clearSelections');
				},
				onLoadSuccess: function (data) {
			   		if(!data.rows && data.rows.length>0){
				   		rows = data.rows;
			   		}
				}
			});
		
			 $('#mcode').bind('keypress',function(event){
			       if(event.keyCode == "13") {//Enter 键事件
			    	   var filtername = $("#mcode").val(); ; 
			      		var params = new Object();
			      		params["ucode"] = filtername;
			      		grid.datagrid('load',params); 
			       }
			   }); 
			//JPlaceHolder.init(); 
		});
	</script>
	<div  id="cardList">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-corp">
					<input id="mcode" value="请输入编码或名称" 
						onFocus="if(value==defaultValue){value='';this.style.color='#000'}" 
						onBlur="if(!value){value=defaultValue;this.style.color='#999'}" />
				</div>
			</div>
		</div>
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="mgrid"></table>
			</div>
		</div>
	</div>
</body>
</html>
