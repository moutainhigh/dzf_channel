<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>合同审核驳回原因参照</title>
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
		
		$(function(){
			var params = new Object();
			grid = $('#rgrid').datagrid({
			    url: DZF.contextPath + '/chn_set/rejectreason!query.action',
			    method: 'post',
				fitColumns: true,
				idField:'reid',
				rownumbers : true,
				singleSelect : false,
				pagination : true,
				pageSize:10,
			    pageList:[10,20,30,40,50],
				showFooter : false,
				height:380,
				striped:true,
			    columns:[[{field:'reid', title:'主键',checkbox:true},
			     		  {field:'reason',title:'驳回原因',width:360},
			              {field:'suggest',title:'修改建议',width:500}
			   	 ]],
				onDblClickRow:function(rowIndex, rowData){
					var rowTable = $('#rgrid').datagrid('getSelections');
					if(rowTable && rowTable[rowTable.length-1] == rowData){
						//如果最后选择的数据和双击的数据一样，则不重复
					}else{
						rowTable.push(rowData);
					}
					dClickReje(rowTable);
					rowTable = $('#rgrid').datagrid('clearSelections');
				},
				onLoadSuccess: function (data) {
			   		if(!data.rows && data.rows.length>0){
				   		rows = data.rows;
			   		}
				}
			});
		});
	</script>
	<div  id="cardList">
		<div class="mod-toolbar-top">
		</div>
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="rgrid"></table>
			</div>
		</div>
	</div>
</body>
</html>
