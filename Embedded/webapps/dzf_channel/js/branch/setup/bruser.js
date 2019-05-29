var contextPath=DZF.contextPath;
var url;
var status="add";
var ckmap = {};
//自适应边框
$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid(0,'dataGrid').h,
		width : 'auto'
	});
});
//enter 键代替tab键换行        begin
$(document).ready(function(){
	$(document).on('keyup', 'input', function(e) {
		 if(e.keyCode == 13 && e.target.type!== 'submit') {
//		   var inputs = $("#qrydialog").find(":input:visible"),
//		   idx = inputs.index(e.target);
//		       if (idx == inputs.length - 1) {
//		          inputs[0].select()
//		       } else {
//		          inputs[idx + 1].focus();
//		          inputs[idx + 1].select();
//		       }
		 }
		});
});//enter 键代替tab键换行        end
$.extend($.fn.validatebox.defaults.rules, {    
    equals: {    
        validator: function(value,param){ 
        	return value == $(param[0]).val();    
        },    
        message: "<font size=2 color='red'>*两次密码不一致</font>" 
    } 
});  
$.extend($.fn.validatebox.defaults.rules, {    
    minLength: {    
        validator: function(value, param){
            return value.length >= param[0]; 
        },    
        message: "<font size=2 color='red'>*密码长度不能小于8</font>"   
    }
});
function add() {
	if($(window).height() < 450){
		$('#cbDialog').dialog('resize',{width: 600,height:($(window).height() - 40)});
		$('#tableDiv').css("height",($(window).height() - 40 -100));
	}
	url=contextPath+ '/sys/chnUseract!save.action';
	$('#bill').form('clear');
	$('#cbDialog').dialog({
		modal:true
	});//设置dig属性
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle','新增');
	$("#en_time").textbox('setValue',getNowFormatDate());
	$('#b_mng').combobox('setValue', 'Y');//
	$('#editOne').hide();
	$('#addNew').show();
	$('#ucode').textbox({'readonly': false});
}
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + seperator1 + month + seperator1 + strDate;
    return currentdate;
}
function edit() {
	  var row = $('#grid').datagrid('getSelected');
	   if(row == null){
		   Public.tips({content: "请选择一行数据修改",type:2});
		   return;
	   }
		if($(window).height() < 450){
			$('#cbDialog').dialog('resize',{width: 600,height:($(window).height() - 40)});
			$('#tableDiv').css("height",($(window).height() - 40 -100));
		}
		url=contextPath+ '/sys/chnUseract!update.action';
	   $('#bill').form('clear');
	   $('#cbDialog').dialog({
   			modal:true
   		});//设置dig属性
       row.uc_pwd=row.u_pwd;
       $('#cbDialog').dialog('open').dialog('center').dialog('setTitle','修改');
       $('#bill').form('load',row);
   	   $('#editOne').show();
	   $('#addNew').hide();
	   status="edit";
//	   $('#ucode').textbox({'readonly': true});
}

function onSave(cls) {
	//校验是否可校验通过
	for(key in ckmap){  
		 $.messager.alert("提示",ckmap[key]);
		return;
	}  
    $('#bill').form('submit',{
        url: url,
        onSubmit: function(){
            return  $('#bill').form('validate');
        },
        success: function(result){
            var result = eval('('+result+')');
            if (result.success){
                $('#grid').datagrid('reload',{unjl:'Y'}); 
                $('#cbDialog').dialog('close');
                $('#bill').form().find('input').val("");
                Public.tips({content: result.msg});
                status="add";
                $('#grid').datagrid('clearSelections');
            } else {
            	Public.tips({content: result.msg,type:1});
            } 
        }
    });
}

$(function() {
	load();
    
	var t_heigth = 500;
	if($(window).height() < 600){
		t_heigth = $(window).height() - 40;
	}
	checkUserCode();
	checkUserPwd();
	$('#qrylock').combobox({    
		onSelect: function(rec){
			var ilock = rec.value;
			qryLockUser(ilock);
        }
	}); 
	$('#quname').textbox('textbox').keydown(function (e) {
        if (e.keyCode == 13) {
 		   var filtername = $("#quname").val(); 
    		var rows = $('#grid').datagrid('getRows');
    		if(rows != null && rows.length >0){
    			var panel = $('#grid').datagrid('getPanel'); 
		        var tr = panel.find('div.datagrid-body tr');
		        var first_id = null;
		        tr.each(function(){   
		            var td_name = $(this).children('td[field="uname"]');   
		            var value_name = td_name.children("div").text();
	            	td_name.children("div").removeClass("search-rs");
		            if(filtername && (value_name.indexOf(filtername) >= 0)){
		            	if (first_id == null ){
		            		var td_id = $(this).children('td[field="uid"]');   
				            var value_id = td_id.children("div").text();
				            first_id = value_id;
		            	}
		            	td_name.children("div").addClass("search-rs");
		            }; 
		        });  
		        if (first_id != null ) {
		        	var index = $('#grid').datagrid('getRowIndex',first_id);
					$('#grid').datagrid('scrollTo',index);
					$('#grid').datagrid('selectRow',index);
		        }
    		}
        }
    });
});
function cancel(){
	$('#cbDialog').dialog('close');
	//清除提示信息标记
	$("font").hide();
}
function checkUserCode(){
	$("#ucode").blur(function(){
		var value = $(this).val();
		if(value != null && value != ""){
			var codeReg = /^[a-zA-Z0-9_]{6,20}$/;
			var isLegal = codeReg.exec(value) != null;
			//校验长度
			if(!isLegal){
				ckmap["ckcode_len"] = "编码由6-20位字母、数字或\"_\"组成";  
				$("#ckcode").html("<font size=2 color='red'>*编码由6-20位字母、数字或\"_\"组成</font>").show();
				return;
			}else{
				$("#ckcode").hide(); 
				delete ckmap["ckcode_len"]; 
			}
			//校验存在
			if(checkExist($(this).attr("name"),value)){
				ckmap["ckcode_ext"]="用户编码已经存在";
				$("#ckcode").html("<font size=2 color='red'>*用户编码已经存在</font>").show();
				return;
			}else{
				$("#ckcode").hide(); 
				delete ckmap["ckcode_ext"];
			}
		}
	});
}

function checkUserPwd(){
	$("#uc_pwd,#u_pwd").blur(function(){
		var value = $(this).val();
		if(value != null && value != ""){
			//字母和数字组成
//			var strExp=/.*([0-9]+.*[A-Za-z]+|[A-Za-z]+.*[0-9]+).*/;
			var strExp = /.*([0-9].*([a-zA-Z].*[~!@#$%^&*()<>?+=]|[~!@#$%^&*()<>?+=].*[a-zA-Z])|[a-zA-Z].*([0-9].*[~!@#$%^&*()<>?+=]|[~!@#$%^&*()<>?+=].*[0-9])|[~!@#$%^&*()<>?+=].*([0-9].*[a-zA-Z]|[a-zA-Z].*[0-9])).*/;
			if(strExp.test(value)){
				$("#"+$(this).attr("id")+"_ck").hide(); 
				delete ckmap[$(this).attr("id")+"_ck"];
			}else{
				$("#"+$(this).attr("id")+"_ck").html("<font size=2 color='red'>*密码必须包含数字、字母、特殊字符</font>").show();
				ckmap[$(this).attr("id")+"_ck"]="密码必须包含数字、字母、特殊字符";
				return;
			}
		}else{
			$("#"+$(this).attr("id")+"_ck").html("<font size=2 color='red'>*请输入密码</font>").show();
		}
	});
}
function checkExist(name,val){
	var flag = false;
	jQuery.ajax({
		url : contextPath + '/sys/sys_useract!check.action',
		data : {
			'ck_code' : name,
			'uid' : val
		},
		type : 'post',
		dataType : 'json',
		async:false,
		success: function(result){
            if (!result.success){
            	flag=true;
            }
		}
	});
	return flag;
}
function load(){
	$('#grid').datagrid({
		url : contextPath + '/sys/chnUseract!query.action',
		rownumbers : true,
		singleSelect : true,
		idField : 'uid',
		striped:true,
		height : Public.setGrid(0,'dataGrid').h,
		columns : [ [{ width : '150', title : '用户编码', field : 'ucode' },
		             { width : '150', title : '用户名称', field : 'uname' },
		             { width : '100', title : '生效时间', field : 'en_time' },
		             { width : '100', title : '失效时间', field : 'dis_time'},
		             { width : '100',title : '锁定标志', field : 'lock_flag',align:'center' },//, formatter:formatCheckBox
		             { width : '360',title : '用户描述', field : 'u_note' },
		             { title : '主键', field : 'uid', hidden:true},
		             { title : '所属公司', field : 'corp_id', hidden:true},
		             { title : '创建公司', field : 'crtcorp_id', hidden:true},
		             { title : '密码', field : 'u_pwd', hidden:true}
		             ] ],
		toolbar : '#toolbar',
		onBeforeLoad : function(param) {
			parent.$.messager.progress({
				text : '数据加载中....'
			});
		},
		onLoadSuccess : function(data) {
			parent.$.messager.progress('close');
		}
	});
}
function qryLockUser(ilock) {
	var queryParams = $('#grid').datagrid('options').queryParams; 
    queryParams['ilock'] =ilock;
    $('#grid').datagrid('options').queryParams = queryParams;  
    $("#grid").datagrid('load'); 
    $('#grid').datagrid('clearSelections');
}
function del(){
	var row = $("#grid").datagrid('getSelected');
	if(row == null){
		   Public.tips({content:"请选择一行数据进行删除",type:2});
		   return;
	   }
	$.messager.confirm('提示','确认删除?',function(conf){
		if (conf){
			jQuery.ajax({
				url : contextPath + '/sys/sys_useract!delete.action',
				data : {
					'uid' : row.uid,
					'corp_id' : row.corp_id
				},
				type : 'post',
				dataType : 'json',
				success: function(result){
		            if (result.success){
		               $("#grid").datagrid("reload",{unjl:'Y'});
					   Public.tips({content: result.msg});
					   $('#grid').datagrid('clearSelections');
		            } else {
		               Public.tips({content: result.msg,type:1});
		            } 
				}
			});
		}
	});
}

function lock(){
	var row = $("#grid").datagrid('getSelected');
	if(row == null){
		   Public.tips({content:"请选择一行数据进行操作。",type:2});
		   return;
	}
	if(row.lock_flag=="是"){
		 Public.tips({content:"用户已经锁定。",type:2});
		 return;
	}
	$.messager.confirm('提示','确认锁定?',function(conf){
		if (conf){
			jQuery.ajax({
				url : contextPath + '/sys/sys_useract!updateLock.action',
				data : {
					'uid' : row.uid,
					'corp_id' : row.corp_id
				},
				type : 'post',
				dataType : 'json',
				success: function(result){
		            if (result.success){
		               $("#grid").datagrid("reload",{unjl:'Y'});
					   Public.tips({content: result.msg});
		            } else {
		               Public.tips({content: result.msg,type:1});
		            } 
				}
			});
		}
	});
}

function unlock(){
	var row = $("#grid").datagrid('getSelected');
	if(row == null){
		   Public.tips({content:"请选择一行数据进行操作。",type:2});
		   return;
	}
	if(row.lock_flag=="否"){
		 Public.tips({content:"用户未锁定。",type:2});
		 return;
	}
	$.messager.confirm('提示','确认解锁?',function(conf){
		if (conf){
			jQuery.ajax({
				url : contextPath + '/sys/sys_useract!updateUnLock.action',
				data : {
					'uid' : row.uid,
					'corp_id' : row.corp_id
				},
				type : 'post',
				dataType : 'json',
				success: function(result){
		            if (result.success){
		               $("#grid").datagrid("reload",{unjl:'Y'});
					   Public.tips({content: result.msg});
		            } else {
		               Public.tips({content: result.msg,type:1});
		            } 
				}
			});
		}
	});
}