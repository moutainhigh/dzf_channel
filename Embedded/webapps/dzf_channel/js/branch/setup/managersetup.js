var contextPath=DZF.contextPath;
var url;
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
	initQryListener();
});

/**
 * 表格初始化
 */
function load() {
	$('#grid').datagrid({
		url : contextPath + '/branch/managersetup!query.action',
		rownumbers : true,
		singleSelect : true,
		idField : 'uid',
		striped : true,
		height : Public.setGrid(0, 'dataGrid').h,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		frozenColumns : [ [ {
			field : 'operate',
			title : '操作',
			width : '100',
			halign : 'center',
			align : 'center',
			formatter : operFormat
		}, ] ],
		columns : [ [ {
			title : '主键',
			field : 'uid',
			hidden : true
		}, {
			width : '120',
			title : '登录账号',
			field : 'ucode',
			align : 'left',
			halign : 'center',
		}, {
			width : '150',
			title : '用户名称',
			field : 'uname',
			align : 'left',
			halign : 'center',
		}, {
			width : '200',
			title : '机构',
			field : 'braname',
			formatter : braFormat,
			align : 'left',
			halign : 'center',
		}, {
			width : '80',
			title : '生效时间',
			field : 'en_time',
			align : 'center',
			halign : 'center',
		}, {
			width : '80',
			title : '失效时间',
			field : 'dis_time',
			align : 'center',
			halign : 'center',
		}, {
			width : '80',
			title : '锁定标志',
			field : 'lock_flag',
			align : 'center',
			halign : 'center',
		}, {
			width : '260',
			title : '用户描述',
			field : 'u_note',
			align : 'left',
			halign : 'center',
		},
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

/**
 * 操作格式化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function operFormat(val, row, index) {
	if(row.lock_flag == "Y" || row.lock_flag == "是"){
		return '<a href="#" style="color:blue;" onclick="edit(' + index + ')">编辑</a>'
		+' <span>锁定</span>'
		+' <a href="#" style="color:blue;" onclick="unlock(' + index + ')">解锁</a>';
	}else{
		return '<a href="#" style="color:blue;" onclick="edit(' + index + ')">编辑</a>'
		+' <a href="#" style="color:blue;" onclick="lock(' + index + ')">锁定</a>'
		+' <span>解锁</span>';
	}
}

/**
 * 添加tips显示
 * @param value
 */
function braFormat(value){
	if(value != undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}
}

/**
 * 查询监听事件
 */
function initQryListener(){
	$('#qrylock').combobox({    
		onSelect: function(rec){
			var lock_flag = rec.value;
			var ucode = $('#quname').textbox('getValue');
			reloadData(lock_flag, ucode);
        }
	}); 
	$('#quname').textbox('textbox').keydown(function (e) {
        if (e.keyCode == 13) {
 		   var ucode = $("#quname").val(); 
 		   var lock_flag = $('#qrylock').combobox('getValue');
 		   reloadData(lock_flag, ucode);
        }
    });
}

/**
 * 查询
 * @param ilock
 * @param ucode
 */
function reloadData(lock_flag, ucode) {
	var queryParams = $('#grid').datagrid('options').queryParams; 
    queryParams['lock_flag'] = lock_flag;
    queryParams['ucode'] = ucode;
    $('#grid').datagrid('options').queryParams = queryParams;  
    $("#grid").datagrid('load'); 
    $('#grid').datagrid('clearSelections');
}

/**
 * 新增
 */
function add() {
	if ($(window).height() < 450) {
		$('#cbDialog').dialog('resize', {
			width : 600,
			height : ($(window).height() - 40)
		});
		$('#tableDiv').css("height", ($(window).height() - 40 - 100));
	}
	$('#bill').form('clear');
	$('#cbDialog').dialog({
		modal : true
	});// 设置dig属性
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '新增');
	//设置生效时间
	$("#en_time").textbox('setValue', parent.SYSTEM.LoginDate);
	$('#b_mng').val('Y');//是否管理员
//	$('#ucode').textbox({
//		'readonly' : false
//	});
	
	//设置机构
	$("#branch").empty();
	$.ajax({
		type : "POST",
		url : DZF.contextPath + "/branch/managersetup!queryBranch.action",
  		dataType : 'json',
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var rows = result.rows;
			if(rows != null && rows.length > 0){
				var br = "";
				var num = 1;
				for(var i = 0; i < rows.length; i++){
					if(num % 3 == 0){
						br = br + "<input type='checkbox' name='braname' value="+rows[i].pk_bset
						+" style='width:18px;height:18px;' />"+rows[i].name+"<br>";
					}else{
						br = br + "<input type='checkbox' name='braname' value="+rows[i].pk_bset
						+" style='width:18px;height:18px;'/>"+rows[i].name+"&nbsp;&nbsp;";
					}
					num ++;
				}
			}
			$("#branch").append(br);
		}
	});
	
}

/**
 * 编辑
 */
function edit(index) {
	var row = $('#grid').datagrid('getData').rows[index];
	if ($(window).height() < 450) {
		$('#cbDialog').dialog('resize', {
			width : 600,
			height : ($(window).height() - 40)
		});
		$('#tableDiv').css("height", ($(window).height() - 40 - 100));
	}
	$('#bill').form('clear');
	$('#cbDialog').dialog({
		modal : true
	});// 设置dig属性
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '修改');
	
	$.ajax({
		url : DZF.contextPath + "/branch/managersetup!queryById.action",
		dataType : 'json',
		processData : true,
  		async : false,//异步传输
		data : {
			uid : row.uid,
//			lock_flag : $('#qrylock').combobox('getValue'),
		},
		success : function(rs) {
			row = rs.rows;
			row.uc_pwd = row.u_pwd;
			$('#bill').form('load', row);
		},
	});
	
//	$('#ucode').textbox({'readonly': true});
	
	//设置机构
	$("#branch").empty();
	$.ajax({
		type : "POST",
		url : DZF.contextPath + "/branch/managersetup!queryBranch.action",
  		dataType : 'json',
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var rows = result.rows;
			if(rows != null && rows.length > 0){
				var br = "";
				var num = 1;
				for(var i = 0; i < rows.length; i++){
					if(num % 3 == 0){
						if(!isEmpty(row.braname) && row.braname.indexOf(rows[i].pk_bset) != -1){
							br = br + "<input type='checkbox' name='braname' value="+rows[i].pk_bset
							+" style='width:18px;height:18px;' checked />"+rows[i].name+"<br>";
						}else{
							br = br + "<input type='checkbox' name='braname' value="+rows[i].pk_bset
							+" style='width:18px;height:18px;' />"+rows[i].name+"<br>";
						}
					}else{
						if(!isEmpty(row.braname) && row.braname.indexOf(rows[i].pk_bset) != -1){
							br = br + "<input type='checkbox' name='braname' value="+rows[i].pk_bset
							+" style='width:18px;height:18px;' checked />"+rows[i].name+"&nbsp;&nbsp;";
						}else{
							br = br + "<input type='checkbox' name='braname' value="+rows[i].pk_bset
							+" style='width:18px;height:18px;'/>"+rows[i].name+"&nbsp;&nbsp;";
						}
					}
					num ++;
				}
			}
			$("#branch").append(br);
		}
	});
}

/**
 * 保存
 */
function onSave() {
	// 校验是否可校验通过
	for (key in ckmap) {
		$.messager.alert("提示", ckmap[key]);
		return;
	}
	$('#bill').form('submit', {
		url : contextPath + '/branch/managersetup!save.action',
		onSubmit : function() {
			return $('#bill').form('validate');
		},
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				var ucode = $("#quname").val(); 
		 		var lock_flag = $('#qrylock').combobox('getValue');
		 		reloadData(lock_flag, ucode);
		 		   
				$('#cbDialog').dialog('close');
				$('#bill').form().find('input').val("");
				Public.tips({
					content : result.msg
				});
				$('#grid').datagrid('clearSelections');
			} else {
				Public.tips({
					content : result.msg,
					type : 1
				});
			}
		}
	});
}

/**
 * 取消新增或编辑
 */
function cancel(){
	$('#cbDialog').dialog('close');
	// 清除提示信息标记
	$("font").hide();
}

/**
 * 校验用户编码是否存在
 */
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

/**
 * 校验用户编码是否存在
 */
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

/**
 * 用户密码校验
 */
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

/**
 * 锁定
 * @param index
 */
function lock(index) {
	var row = $('#grid').datagrid('getData').rows[index];
	if (row.lock_flag == "Y" || row.lock_flag == "是") {
		Public.tips({
			content : "用户已经锁定。",
			type : 2
		});
		return;
	}
	$.messager.confirm('提示', '确认锁定?', function(conf) {
		if (conf) {
			jQuery.ajax({
				url : contextPath + '/sys/sys_useract!updateLock.action',
				data : {
					'uid' : row.uid,
					'corp_id' : row.corp_id
				},
				type : 'post',
				dataType : 'json',
				success : function(result) {
					if (result.success) {
						$("#grid").datagrid("reload", {
//							lock_flag : 'Y'
						});
						Public.tips({
							content : result.msg
						});
					} else {
						Public.tips({
							content : result.msg,
							type : 1
						});
					}
				}
			});
		}
	});
}

/**
 * 解锁
 * @param index
 */
function unlock(index) {
	var row = $('#grid').datagrid('getData').rows[index];
	if (row.lock_flag == "否") {
		Public.tips({
			content : "用户未锁定。",
			type : 2
		});
		return;
	}
	$.messager.confirm('提示', '确认解锁?', function(conf) {
		if (conf) {
			jQuery.ajax({
				url : contextPath + '/sys/sys_useract!updateUnLock.action',
				data : {
					'uid' : row.uid,
					'corp_id' : row.corp_id
				},
				type : 'post',
				dataType : 'json',
				success : function(result) {
					if (result.success) {
						$("#grid").datagrid("reload", {
//							lock_flag : 'Y'
						});
						Public.tips({
							content : result.msg
						});
					} else {
						Public.tips({
							content : result.msg,
							type : 1
						});
					}
				}
			});
		}
	});
}
