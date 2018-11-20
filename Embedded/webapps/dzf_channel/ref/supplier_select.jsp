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
.search-crumb input{width:84%;}
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
	
	$(function(){
		loadGsy();
		$('#stcode').bind('keypress', function(event) {
			if (event.keyCode == "13") {//Enter 键事件
				var params = new Object();
				params["cpcode"] = $("#stcode").val();
				grid.datagrid('load', params);
			}
		});
		
		$('#vmemo').textbox({// 去除空格
			onChange : function(n, o) {
				if(isEmpty(n)){
					return;
				}
				var _trim = trimStr(n,'g');
				$("#vmemo").textbox("setValue", _trim);
			}
		});
		
	});
	
	//加载表格
	function loadGsy(){
		grid = $('#gysgrid').datagrid({
		    url: DZF.contextPath + '/dealmanage/stockin!querySupplierRef.action',
		    method: 'post',
			fitColumns: true,
			idField:'suid',
			rownumbers : true,
			singleSelect : true,
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
	}
	
	/**
	 * 添加供应商
	 */
	function addSupplier(){
		$('#supDlg').dialog('open').dialog('center').dialog('setTitle', '新增供应商');
		$('#supForm').form('clear');
	}

	/**
	 * 新增供应商-保存
	 */
	function supSave(){
		if ($("#supForm").form('validate')) {
			$.messager.progress({
				text : '数据保存中，请稍后.....'
			});
			
			$('#supForm').form('submit', {
				url : DZF.contextPath + '/dealmanage/stockin!saveSupplier.action',
				success : function(result) {
					var result = eval('(' + result + ')');
					$.messager.progress('close');
					if (result.success) {
						loadGsy();
						$('#supDlg').dialog('close');
					} else {
						Public.tips({
							content : result.msg,
							type : 2
						});
					}
				}
			});
		} else {
			Public.tips({
				content : "必输信息为空或格式不正确",
				type : 2
			});
			return; 
		}
	}

	/**
	 * 新增供应商-取消
	 */
	function supCancel(){
		$('#supDlg').dialog('close');
	}
	
</script>
	<div  id="cardList">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left search-crumb">
					<input id="stcode" style="color: rgb(153, 153, 153);" value="请输入供应商名称点击Enter键查询" 
						onFocus="if(value==defaultValue){value='';this.style.color='#000'}" 
						onBlur="if(!value){value=defaultValue;this.style.color='#999'}" />
					<a href="javascript:void(0)" id="ok" class="easyui-linkbutton" onClick="addSupplier()">添加</a> 
				</div>
			</div>
		</div>
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="gysgrid"></table>
			</div>
		</div>
	</div>
	
	<!-- 供应商对话框  begin-->
	<div id="supDlg" class="easyui-dialog" style="width:400px;height:220px;padding-top:30px;" 
		data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
		<form id="supForm" method="post">
			<div class="time_col time_colp11">
				<div style="display: inline-block;">
					<label style="text-align:right;width:140px;">供应商名称：</label>
					<input id="vmemo" name="memo" class="easyui-textbox" 
						data-options="validType:'length[0,50]'" style="width:150px;height:25px;"/>
				</div>
			</div>
		</form>
		<div style="text-align:center;margin-top:40px;">
		    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="supSave()">保存</a> 
			<a href="javascript:void(0)"  class="ui-btn ui-btn-xz" onclick="supCancel()">取消</a>
		</div>
	</div>
	<!-- 供应商对话框  end-->
	
</body>
</html>
