<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>找回密码</title>
<jsp:include page="./inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, request.getContextPath()+"/css/login.css");%> rel="stylesheet"/>
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
            var holder = $('<span></span>').text(txt).css({position:'absolute', left:pos.left, top:pos.top, height:h, lienHeight:h, paddingLeft:paddingleft, color:'#aaa'}).appendTo(self.parent());
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
var phOrEm;//记录是手机号还是邮箱
function CheckPhone(){
	var flag = true;
	$("#phone").bind("blur",function(){
		var value = $(this).val();
		
		var telReg = !!value.match(/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/);
		var mailReg = !!value.match( /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/);
		//
		if(value.length == 0){
			$("#phone").parents("span").next().html("手机号或邮箱不能为空！");
			flag = false;
		} 
		
		if(telReg ==  false && mailReg == false){
			$("#phone").parents("span").next().html("请输入正确的手机号或邮箱！");
			flag = false;
		}else{
			$("#phone").parents("span").next().html("");
			flag = true;
		} 
		if(telReg){
			phOrEm = "ph";//手机号
		}else if(mailReg){
			phOrEm = "em";
		}
	});
	return flag;
}
<%-- function checkUsercode(usercode){
	$.ajax({
		url : "<%=request.getContextPath()%>/st/searchPsw!checkUserName.action",
		data : {
			'data.user_code' : $("#user_code").val()
		},
		type : 'post',
		dataType : 'json',
		async:false,
		success: function(result){
            if (!result.success){
            	$("#user_code").parents("span").next().html(result.msg);
            }
		}
	});
} --%>
$(function(){
	JPlaceHolder.init(); 
	
	CheckPhone();
	//用户编码不能为空
	$("#user_code").bind("blur",function(){
		var value = $(this).val();
		//
		if(value.length == 0){
			$("#user_code").parents("span").next().html("用户编码不能为空！");
		}else{
			$("#user_code").parents("span").next().html("");
		}
		var usercode=$("#user_code").val();
		//checkUsercode(usercode);
	});
	//密码验证
	$("#user_password,#pwdparam").bind("blur",function(){
		var value = $(this).val();
		if(value.length == 0){
			$(this).parents("span").next().html("密码不能为空！");
			return;
		} 
		var strExp = /.*([0-9].*([a-zA-Z].*[~!@#$%^&*()<>?+=]|[~!@#$%^&*()<>?+=].*[a-zA-Z])|[a-zA-Z].*([0-9].*[~!@#$%^&*()<>?+=]|[~!@#$%^&*()<>?+=].*[0-9])|[~!@#$%^&*()<>?+=].*([0-9].*[a-zA-Z]|[a-zA-Z].*[0-9])).*/;
		if(!strExp.test(value)){
			$(this).parents("span").next().html("密码必须包含数字、字母、特殊字符！");
			return;
		}else{
			$(this).parents("span").next().html("");
		}
		if(value.length < 8){
	        $(this).parents("span").next().html("密码必须大于8位！");
	        return;
		}else{
			$(this).parents("span").next().html("");
		}
	});
	//获取验证码：
	$("#getYZcode").click(function(){
		//校验图片验证码
		if($("#verify").val() == ''){
			$.messager.show({
				title:"错误提示",
				msg:"请输入验证码！"
			}); 
			//$("#freshId").append("请输入验证码！");
			$("#verify").focus();
			return;
		}
		
		var ucode = $("#user_code").val();
		//var uphone = $("#phone").val();
		if(ucode == "" && ucode.length == 0 ){
			$("#user_code").parents("span").next().html("用户编码不能为空！");
			return;
		}
		//验证手机
		if(!CheckPhone()){
			return;
		}
		$.ajax({
			url : "<%=request.getContextPath()%>/st/searchPsw!sandYZcode.action",
			data : {
				'data.user_code' : $("#user_code").val(),
				'data.phone' : $("#phone").val(),
				'data.user_mail' : $("#phone").val(),
				'phOrEm' : phOrEm,
				'verify':$("#verify").val()
			},
			type : 'post',
			dataType : 'json',
			async:false,
			success: function(result){
	            if (result.success){
	            	changeAu();
	            	$("#phone").parents("span").next().html("验证码已发送！");
	            }else{
	            	changeAu();
	            	if('-100'==result.status || '-300' == result.status){
	            		$('#user_code').parents("span").next().html(result.msg);
	            	}else{
	            		$("#phone").parents("span").next().html(result.msg);
	            	}
	            	
	            }
			}
		});
	});
	$("#updatePsw").click(function(){
		var psw1 = $("#user_password").val();
		var psw2 = $("#pwdparam").val();
		if(!psw1){
			$("#user_password").parents("span").next().html("请输入密码！");
			return ;
		}
		if(!psw2){
			$("#pwdparam").parents("span").next().html("请输入密码！");
			return ;
		}
	    if (psw1.length < 8) {
	        $("#user_password").parents("span").next().html("密码必须大于8位！");
	        return;
	    }
	    if (psw2.length < 8) {
	        $("#pwdparam").parents("span").next().html("密码必须大于8位！");
	        return;
	    }
		if(psw1 != psw2){
			$("#pwdparam").parents("span").next().html("两次密码输入不一致！");
			return ;
		}
		$.ajax({
			url : "<%=request.getContextPath()%>/st/searchPsw!getPswBack.action",
			data : {
				'data.user_code' : $("#user_code").val(),
				'data.phone' : $("#phone").val(),
				'data.checkcode' : $("#checkcode").val(),
				'data.user_password' : psw1,
				'data.pwdparam' : psw2,
			},
			type : 'post',
			dataType : 'json',
			async:false,
			success: function(result){
	            if (result.success){
	            	//Public.tips({content: result.msg,type:0});
	            	$.messager.show({
							title:"提示",
							msg:"新密码保存成功！"
					});
	            	setTimeout('window.location.href="${pageContext.request.contextPath}/"',2000);
	            	
	            	
	            }else{
	            	//Public.tips({content: result.msg,type:2});
	            	$.messager.show({
						title:"提示",
						msg:"密码保存失败！"
					});
	            }
			}
		});
	});
});

function changeAu(){
    var timestamp = (new Date()).valueOf();
	$("#codeImg").attr("src","./au/image.jsp?timestamp=" + timestamp);   
}
</script>
</head>
<body>
<div class="header">
	<div class="top"><div class="top_logo"><img src="<%=request.getContextPath()%>/img/logo.png" /></div><p><a href="<%=request.getContextPath()%>/login.jsp">直接登录</a></p></div>
</div>

<div class="registered">
	<h1>忘记密码</h1>
	<form name="mailBox" id="mailBox" method="post" action="">	
	<ul>
		<li><label>*</label><span><input type="text" class="text-inp w378" placeholder="用户" id="user_code"/></span><em></em></li>
		<li><label>*</label><span><input type="text" class="text-inp w378" placeholder="手机号/邮箱" id="phone"/></span><em></em></li>
		<li><label>*</label><span><input type="text" class="text-inp w217" placeholder="验证码" id="verify" name="verify" /></span><strong><img id="codeImg" src="./au/image.jsp"></strong><a href="javascript:changeAu();" class="yzsx"><img id="freshId" src="./img/sx.jpg" ></a><em></em></li>
		<li><label>*</label><span><input type="text" class="text-inp w217" placeholder="手机或邮箱验证码" id="checkcode"/></span><strong class="hq" id="getYZcode">获取验证码</strong><em></em></li>
		<li><label>*</label><span><input type="password" class="text-inp w378" placeholder="设置密码" id="user_password"/></span><em></em></li>
		<li><label>*</label><span><input type="password" class="text-inp w378" placeholder="确认密码" id="pwdparam"/></span><em></em></li>
		<li><label>&nbsp;</label><input type="button" class="but_tj" value="提交" id="updatePsw"style="margin-left:0;width:400px;"/></li>
	</ul>

	</form>
</div>


</body>

</html>		
