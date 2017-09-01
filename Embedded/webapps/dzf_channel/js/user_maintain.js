
$(function(){
	$.ajax({
		url : DZF.contextPath + "/sys/sys_maintainuseract!query.action",
		traditional : true,
		async : false,
		dataType : 'json',
		success : function(result, textStatus) {
			if(result.success){
				$('#base_fm').form("clear");
				var row = result.rows;
				//var children = row.children;
				$("#phonenum").textbox('setValue', row.phonenum);
				$("#mail").textbox('setValue', row.mail);
				//if(children != null && children.length > 0){
					
				//$("#unick").textbox('setValue', row.unick);
				$("#usign").val(row.usign);
				$("#id_user_e").val(row.id_user_e);
				
				if(row.imgpath != null && row.imgpath != ''){
					var src = DZF.contextPath + "/sys/sys_maintainuseract!search.action?imurl=" + row.imgpath+"&_=" +new Date().getTime();
					$("#upimg").attr("src",src);
					$('#url').val(row.imgpath);
				}
					
				//}
			}
			
		}
	});
	
});

function choose(id){
	$("#div1,#div2,#div3").hide();
	$("#div" + id).show();
}

/**
 * 图片改变事件
 */
function uploadPic(){
 	var obj_file = document.getElementById('picFile');
 	var path = obj_file.value;
	var arr = path.split('.');
	var suffix =  arr[arr.length - 1];
	if('png' != suffix && 'jpg' != suffix && 'gif' != suffix){
		$('#picFile').html('');
		Public.tips({content : '请选择png或jpg或gif格式图片', type : 1});
		return;
	}
	$("#imgCrop").attr("src","images/white.jpg");
	$('#pic_fm').form('submit', {
		url : DZF.contextPath + '/sys/sys_maintainuseract!uploadPic.action',
		success : function(data) {
			var result = JSON.parse(data);
			if (result.success) {
				$("#id_user_e").val(result.data.id_user_e);
				$("#upPic").show();
				$('#cq_dialog').dialog('open');
				$(".imgareaselect-outer").show();
				$(".window-mask + div").css("z-index",$(".window-mask + div").css('z-index') * 10);
				$(".imgareaselect-outer").css("z-index",$(".imgareaselect-outer").css('z-index') * 10);
				var src = DZF.contextPath + "/sys/sys_maintainuseract!search.action?imurl=" + result.data.imgpath;
				$("#imgCrop").attr("src",src);
				$('#imgurl').val(result.data.imgpath); 
				$("#cutshow").attr("src",src);
				$('#imgCrop').imgAreaSelect({
					 aspectRatio: '1:1', 
					 x1: 100, y1: 100, x2: 200, y2: 200,
					 onSelectEnd: function (img, selection) {
						 $('input[name="x1"]').val(selection.x1);
						 $('input[name="y1"]').val(selection.y1);
						 $('input[name="x2"]').val(selection.x2);
						 $('input[name="y2"]').val(selection.y2);            
					 }});
				
			} else {
				Public.tips({ content : result.msg, type : 1 });
			}
		}
	});
}

/**
 * 取消图片裁切
 */
function cutcancel(){
	$('#cq_dialog').dialog('close');
	$(".imgareaselect-outer").hide();
	$(".imgareaselect-selection").hide();
	$(".imgareaselect-selection").parent().hide();
}

/**
 * 图片裁切
 */
function cutsave(){
	$.ajax({
		url : DZF.contextPath + "/sys/sys_maintainuseract!cutImage.action",
		traditional : true,
		async : false,
		dataType : 'json',
		data : {
			"x1" : $('#x1').val(),
			"y1" : $('#y1').val(),
			"x2" : $('#x2').val(),
			"y2" : $('#y2').val(),
			"url" : $('#imgurl').val(),
			"id_user_e" : $("#id_user_e").val()
		},
		success : function(data, textStatus) {
			if (!data.success) {
				Public.tips({
					content : data.msg,
					type : 1
				});
			} else {
				var src = DZF.contextPath + "/sys/sys_maintainuseract!search.action?imurl=" + data.data;
				$("#upimg").attr("src",src);
				$('#url').val(data.data);
			}
		}
	});
	$('#cq_dialog').dialog('close');
	$(".imgareaselect-outer").hide();
	$(".imgareaselect-selection").hide();
	$(".imgareaselect-selection").parent().hide();
}

function updateBaseInfo(){
	if (!$("#base_fm").form('validate')) {
        return;
    }
	
	 //验证手机号码是否正确
//    if ($("#isEditInfo").prop("checked")) {
        var value = $("#phonenum").val();
        if(value != null && value != ""){
        	//手机号码正则表达式
            var telReg = !!value.match(/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/);
            if (telReg == false) {
                Public.tips({
                    content: "请输入正确的手机号码！",
                    type: 2
                });
                return;
            }
        }
        
        //邮箱正则表达式
        var mailValue = $("#mail").val();
        if(mailValue != null && mailValue != ""){
        	 var mailReg = !!mailValue.match(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/);
             if (mailReg == false) {
                 Public.tips({
                     content: "请输入正确的邮箱！",
                     type: 2
                 });
                 return;
             }
        }
        
        //var unick = $("#unick").val();
        var usign = $("#usign").val();
        if((value == null || value == "")
        	&& (mailValue == null || mailValue == "")
        	//&& (unick == null || unick == "")
        	&& (usign == null || usign == "")){
        	Public.tips({
                content: "提交前请至少录入一项数据！",
                type: 2
            });
        	return;
        }
//    }
    
    $.ajax({
    	url : DZF.contextPath + '/sys/sys_maintainuseract!updateBaseInfo.action',
    	traditional : true,
		async : false,
		dataType : 'json',
		data : {
	        'phonenum': $("#phonenum").val(),
	        'mail': $("#mail").val(),
	        //'unick': $("#unick").val(),
	        'usign': $("#usign").val(),
	        'id_user_e' : $("#id_user_e").val()
		},
		success : function(data, textStatus) {
			if (data.success) {
				var uid = data.rows.id_user_e;
				$("#id_user_e").val(uid);
	            Public.tips({
	                content: data.msg,
	                type: 0
	            });
	        } else {
	            Public.tips({
	                content: data.msg,
	                type: 1
	            });
	            return;
	        }
		}, 
		error : function(XMLHttpRequest, textStatus, errorThrown){
			
		}
    });
    
//    $.post(DZF.contextPath + '/sys/sys_maintainuseract!updateBaseInfo.action', {
//        'phonenum': $("#phonenum").val(),
//        'mail': $("#mail").val(),
//        'unick': $("#unick").val(),
//        'usign': $("#usign").val(),
//        'id_user_e' : $("#id_user_e").val()
//    },
//    function(data) {
//        if (data.success) {
//            Public.tips({
//                content: data.msg,
//                type: 0
//            });
//        } else {
//            Public.tips({
//                content: data.msg,
//                type: 1
//            });
//            return;
//        }
//    }, "json");
}

function updatePwdInfo(){
	checkUserPwd();
    if ($("#user_password").val() == $("#psw2").val()) {
        Public.tips({
            content: "提示：旧密码和新密码不能一致！",
            type: 1
        });
        return;
    }
    if ($("#psw2").val().length < 8) {
        Public.tips({
            content: "提示：密码必须大于8位！",
            type: 1
        });
        return;
    }
    if ($("#psw3").val().length < 8) {
        Public.tips({
            content: "提示：密码必须大于8位！",
            type: 1
        });
        return;
    }
    if ($("#psw3").val() != $("#psw2").val()) {
        Public.tips({
            content: "提示：两次密码不一致！",
            type: 1
        });
        return;
    }
    
    var publicKey = RSAUtils.getKeyPair(exponent, '', modulus);
    var dcpassword = RSAUtils.encryptedString(publicKey, $("#user_password").val());
    var psw2 = RSAUtils.encryptedString(publicKey, $("#psw2").val());
    var psw3 = RSAUtils.encryptedString(publicKey, $("#psw3").val());

    $.post(DZF.contextPath + '/sys/sm_user!updatePsw.action', {
            'data.user_name': $('#user_name').val(),
            'data.user_password': dcpassword,
            'psw2': psw2,
            'psw3': psw3,
            'phonenum': $("#phone").val(),
            'mail': $("#uEmail").val()
        },
        function(data) {
            if (data.success) {
                $('#upsw').dialog('close');
                Public.tips({
                    content: data.msg,
                    type: 0
                });
                setTimeout('window.location.href= DZF.contextPath + "/login.jsp"', 2000);
            } else {
                Public.tips({
                    content: data.msg,
                    type: 1
                });
                return;
            }
        }, "json");
}

function checkUserPwd() {
    $("#psw2,#psw3").blur(function() {
        var value = $(this).val();
        if (value != null && value != "") {
            //字母和数字组成
            //			var strExp=/.*([0-9]+.*[A-Za-z]+|[A-Za-z]+.*[0-9]+).*/;
            var strExp = /.*([0-9].*([a-zA-Z].*[~!@#$%^&*()<>?+=]|[~!@#$%^&*()<>?+=].*[a-zA-Z])|[a-zA-Z].*([0-9].*[~!@#$%^&*()<>?+=]|[~!@#$%^&*()<>?+=].*[0-9])|[~!@#$%^&*()<>?+=].*([0-9].*[a-zA-Z]|[a-zA-Z].*[0-9])).*/;
            if (strExp.test(value)) {
                $("#" + $(this).attr("id") + "_ck").hide();
                delete ckmap[$(this).attr("id") + "_ck"];
            } else {
                Public.tips({
                    content: "提示：密码必须包含数字、字母、特殊字符！",
                    type: 1
                });
                //$("#"+$(this).attr("id")+"_ck").html("<font size=2 color='red'>*密码必须包含数字、字母、特殊字符</font>").show();
                //ckmap[$(this).attr("id")+"_ck"]="密码必须包含数字、字母、特殊字符";
                return;
            }
        } else {
            //$("#"+$(this).attr("id")+"_ck").html("<font size=2 color='red'>*请输入密码</font>").show();
        }
    });
}
