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

function load(){
	$('#grid').datagrid({
		url : contextPath + '/branch/user!query.action',
		rownumbers : true,
		singleSelect : true,
		idField : 'uid',
		striped:true,
		height : Public.setGrid(0,'dataGrid').h,
		frozenColumns:[[{width : '100',title : '操作列',field : 'operate',halign : 'center',align : 'center',formatter:opermatter}
						]],
		columns : [ [{ width : '120', title : '登陆账号', field : 'ucode' },
		             { width : '120', title : '用户名称', field : 'uname' },
		             { width : '160', title : '角色', field : 'rolenames',formatter:showTitle},
		             { width : '120', title : '所属机构', field : 'pk_depts',},
		             { width : '100', title : '生效时间', field : 'en_time' },
		             { width : '100', title : '失效时间', field : 'dis_time'},
		             { width : '80',title : '锁定标志', field : 'lock_flag',align:'center' },
		             { width : '200',title : '用户描述', field : 'u_note' ,formatter:showTitle},
		             { title : '主键', field : 'uid', hidden:true},
		             { title : '所属公司', field : 'corp_id', hidden:true},
		             { title : '创建公司', field : 'crtcorp_id', hidden:true},
		             { title : '密码', field : 'u_pwd', hidden:true}
		             ] ],
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


function add() {
	url=contextPath+ '/branch/user!save.action';
	$('#addDialog').dialog({modal:true});
    $('#addDialog').dialog('open').dialog('center').dialog('setTitle',"新增用户");
    
    $('#addForm').form("clear");
	$("#en_time").textbox('setValue',parent.SYSTEM.LoginDate);
	$('#b_mng').combobox('setValue', 'Y');
	
	$('#ucode').textbox({'readonly': false});
	
	$('#saveEdit').hide();
	$('#saveNew').show();
	status="add";
}


function edit(index){
	url=contextPath+ '/branch/user!saveEdit.action';
    $('#addDialog').dialog({modal:true});
    $('#addDialog').dialog('open').dialog('center').dialog('setTitle',"修改用户");
    
    $('#addForm').form("clear");
    var rows = $("#grid").datagrid("getRows");
    var row = rows[index];
    row.uc_pwd=row.u_pwd;
    
    $('#addForm').form('load',row);
    
    $('#saveEdit').show();
    $('#saveNew').hide();
	status="edit";
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

function showTitle(value){
	if(value!=undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}
}

function opermatter(val, row, index) {
	if(row.lock_flag=="是"){
		return '<a href="#" style="margin-bottom:0px;color:blue;margin-left:10px;" onclick="edit(\''+index+'\')">编辑</a>'+
		'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="unlock(\''+row.asetid+'\',\''+row.updatets+'\')">解锁</a>';
	}else{
		return '<a href="#" style="margin-bottom:0px;color:blue;margin-left:10px;" onclick="edit(\''+index+'\')">编辑</a>'+
		'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="lock(\''+row.asetid+'\',\''+row.updatets+'\')">锁定</a>';
	}
}

function lock(id,updatets){
	$.messager.confirm('提示','确认锁定?',function(conf){
		if (conf){
			jQuery.ajax({
				url : contextPath + '/branch/user!updateLock.action',
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

function unlock(id,updatets){
	$.messager.confirm('提示','确认解锁?',function(conf){
		if (conf){
			jQuery.ajax({
				url : contextPath + '/branch/user!updateLock.action',
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

function qryLockUser(ilock) {
	var queryParams = $('#grid').datagrid('options').queryParams; 
    queryParams['ilock'] =ilock;
    $('#grid').datagrid('options').queryParams = queryParams;  
    $("#grid").datagrid('load'); 
    $('#grid').datagrid('clearSelections');
}

