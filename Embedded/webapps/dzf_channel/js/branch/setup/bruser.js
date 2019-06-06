var contextPath=DZF.contextPath;
var url;
var o;
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
			 
		 }
		});
});
//enter 键代替tab键换行        end

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
	initListen();
});

function load(){
	$('#grid').datagrid({
		url : contextPath + '/branch/brachuser!query.action',
		queryParams: {
			 "qtype": 0,
		},
		rownumbers : true,
		singleSelect : true,
		idField : 'uid',
		striped:true,
		height : Public.setGrid(0,'dataGrid').h,
		frozenColumns:[[{width : '140',title : '操作列',field : 'operate',halign : 'center',align : 'center',formatter:opermatter}
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
	url=contextPath+ '/branch/brachuser!save.action';
	$('#addDialog').dialog({modal:true});
    $('#addDialog').dialog('open').dialog('center').dialog('setTitle',"新增用户");
    $('#addForm').form("clear");
	$.ajax({
		url : DZF.contextPath + "/branch/brachuser!queryByID.action",
		dataType : 'json',
		processData : true,
  		async : false,//异步传输
		success : function(result) {
			var branchs = result.rows.branchs;
			var roles = result.rows.roles;
			setAddCheck(branchs,roles);
			$("#en_time").textbox('setValue',parent.SYSTEM.LoginDate);
			$('#b_mng').combobox('setValue', 'Y');
			
			$('#ucode').textbox({'readonly': false});
			
			$('#saveEdit').hide();
			$('#saveNew').show();
			status="add";
		},
	});
}

function setAddCheck(branchs,roles){
	$("#branch").empty();
	if(branchs != null && branchs.length > 0){
		var br = "<label style='text-align:right;width:140px;'>所属机构:</label>";
		var num = 1;
		for(var i = 0; i <branchs.length; i++){
			if(num % 4 == 0){
				br += "<input type='radio' name='departid' value="+branchs[i].id
					+" style='width:18px;height:28px;' />"+branchs[i].name+"<br>";
			}else{
				br = br + "<input type='radio' name='departid' value="+branchs[i].id
				+" style='width:18px;height:28px;'/>"+branchs[i].name+"&nbsp;&nbsp;";
			}
			num ++;
		}
		$("#branch").append(br);
	}
	$("#roles").empty();
	if(roles != null && roles.length > 0){
		var br = "<label style='text-align:right;width:140px;'>角色:</label>";
		var num = 1;
		for(var i = 0; i <roles.length; i++){
			if(num % 4 == 0){
				br += "<input type='checkbox' name='roleids0' value="+roles[i].id
					+" style='width:18px;height:28px;' />"+roles[i].name+"<br>";
			}else{
				br = br + "<input type='checkbox' name='roleids0' value="+roles[i].id
				+" style='width:18px;height:28px;'/>"+roles[i].name+"&nbsp;&nbsp;";
			}
		}
		$("#roles").append(br);
	}
}


function edit(index){
	url=contextPath+ '/branch/user!saveEdit.action';
    $('#addDialog').dialog({modal:true});
    $('#addDialog').dialog('open').dialog('center').dialog('setTitle',"修改用户");
    $('#addForm').form("clear");
    
    var rows = $("#grid").datagrid("getRows");
    var row = rows[index];
	$.ajax({
		url : DZF.contextPath + "/branch/brachuser!queryByID.action",
		dataType : 'json',
		processData : true,
  		async : false,//异步传输
		data : {
			qryId : row.uid,
		},
		success : function(result) {
			row = result.rows.uservo;
			var branchs = result.rows.branchs;
			var roles = result.rows.roles;
			setEditCheck(row,branchs,roles);
			
		    row.uc_pwd=row.u_pwd;
		    $('#addForm').form('load',row);
		    
		    $('#saveEdit').show();
		    $('#saveNew').hide();
			status="edit";
		},
	});
}

function setEditCheck(row,branchs,roles){
	$("#branch").empty();
	if(branchs != null && branchs.length > 0){
		var br = "<label style='text-align:right;width:140px;'>所属机构:</label>";
		var num = 1;
		for(var i = 0; i <branchs.length; i++){
			if(num % 4 == 0){
				if(row.departid.indexOf(branchs[i].id) > -1){
					br += "<input type='radio' name='departid' value="+branchs[i].id
						+" style='width:18px;height:28px;'checked />"+branchs[i].name+"<br>";
				}else{
					br += "<input type='radio' name='departid' value="+branchs[i].id
						+" style='width:18px;height:28px;' />"+branchs[i].name+"<br>";
				}
			}else{
				if(row.departid.indexOf(branchs[i].id) > -1){
					br += "<input type='radio' name='departid' value="+branchs[i].id
						+" style='width:18px;height:28px;'checked />"+branchs[i].name+"&nbsp;&nbsp;";
				}else{
					br += "<input type='radio' name='departid' value="+branchs[i].id
						+" style='width:18px;height:28px;' />"+branchs[i].name+"&nbsp;&nbsp;";
				}
			}
			num ++;
		}
		$("#branch").append(br);
	}
	$("#roles").empty();
	if(roles != null && roles.length > 0){
		var br = "<label style='text-align:right;width:140px;'>角色:</label>";
		var num = 1;
		for(var i = 0; i <roles.length; i++){
			if(num % 4 == 0){
				if(row.roleids.indexOf(roles[i].id) > -1){
					br += "<input type='checkbox' name='roleids1' value="+roles[i].id
						+" style='width:18px;height:28px;'checked  />"+roles[i].name+"<br>";
				}else{
					br += "<input type='checkbox' name='roleids1' value="+roles[i].id
						+" style='width:18px;height:28px;'/>"+roles[i].name+"<br>";
				}
			}else{
				if(row.roleids.indexOf(roles[i].id) > -1){
					br += "<input type='checkbox' name='roleids1' value="+roles[i].id
						+" style='width:18px;height:28px;' checked />"+roles[i].name+"&nbsp;&nbsp;";
				}else{
					br += "<input type='checkbox' name='roleids1' value="+roles[i].id
						+" style='width:18px;height:28px;' />"+roles[i].name+"&nbsp;&nbsp;";
				}
			}
		}
		$("#roles").append(br);
	}
}

function onSave(isAdd) {
	//校验是否可校验通过
	for(key in ckmap){  
		 $.messager.alert("提示",ckmap[key]);
		return;
	}  
	var roleIds ="";
	if(isAdd){
        $("input[name='roleids0']:checked").each(function(i){
        	roleIds += $(this).val()+",";
        });
        if(!isEmpty(roleIds)){
        	$("#roleids").val(roleIds.substr(0,roleIds.length-1));
        }
	}else{
        $("input[name='roleids1']:checked").each(function(i){
        	roleIds += $(this).val()+",";
        });
        if(!isEmpty(roleIds)){
        	$("#roleids").val(roleIds.substr(0,roleIds.length-1));
        }
	}
    $('#addForm').form('submit',{
        url: url,
        onSubmit: function(){
            return  $('#addForm').form('validate');
        },
        success: function(result){
            var result = eval('('+result+')');
            if (result.success){
                $('#grid').datagrid('reload',{unjl:'Y'}); 
                $('#addDialog').dialog('close');
                Public.tips({content: result.msg});
                $('#grid').datagrid('clearSelections');
            } else {
            	Public.tips({content: result.msg,type:1});
            } 
        }
    });
}


function cancel(){
	$('#addDialog').dialog('close');
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
		' <span>锁定</span>'+
		'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="unlock(\''+row.uid+'\',\''+row.updatets+'\')">解锁</a>';
	}else{
		return '<a href="#" style="margin-bottom:0px;color:blue;margin-left:10px;" onclick="edit(\''+index+'\')">编辑</a>'+
		'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="lock(\''+row.uid+'\',\''+row.updatets+'\')">锁定</a>'+
		' <span>解锁</span>';
	}
}

function lock(uid,updatets){
	$.messager.confirm('提示','确认锁定?',function(conf){
		if (conf){
			jQuery.ajax({
				url : contextPath + '/branch/brachuser!updateLock.action',
				data : {
					'uid' : uid,
					'updatets' : updatets,
					'lock_flag' : 'Y'
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

function unlock(uid,updatets){
	$.messager.confirm('提示','确认解锁?',function(conf){
		if (conf){
			jQuery.ajax({
				url : contextPath + '/branch/brachuser!updateLock.action',
				data : {
					'uid' : uid,
					'updatets' : updatets,
					'lock_flag' : 'N'
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

function initListen() {
	$('#qrylock').combobox({    
		onSelect: function(rec){
			var qryType;
			if(rec.value=="Y" || rec.value=="是"){
				qryType = 1;
			}else{
				qryType = 0;
			}
			$('#grid').datagrid('load', {"qtype" : qryType});
		    $('#grid').datagrid('clearSelections');
        }
	}); 
	
	$('#quname').textbox('textbox').keydown(function (e) {
        if (e.keyCode == 13) {
 		   var ucode = $("#quname").textbox('getValue'); 
 		   var lock = $('#qrylock').combobox('getValue');
 		   var qryType;
 		   if(lock=='N'){
 			  qryType = 0;
 		   }else{
 			  qryType = 1;
 		   }
		 $('#grid').datagrid('load', {"qtype" : qryType,"ucode" : ucode});
		   $('#grid').datagrid('clearSelections');
        }
    });
}

