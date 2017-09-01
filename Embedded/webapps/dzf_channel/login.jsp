<%@page import="java.net.URLEncoder"%>
<%@page import="com.dzf.pub.StringUtil"%>
<%@page import="com.dzf.pub.IGlobalConstants"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.service.outresource.AdminOutResourceConst" %>
<%@page import="com.dzf.service.outresource.OutResourceVO" %>
<%@page import="com.dzf.pub.Outresource" %>

<%
	String message = (String)session.getAttribute(IGlobalConstants.logout_msg);
	String modulus =(String)session.getAttribute("MODULUS");
	String exponent =(String)session.getAttribute("EXPONENT");
	
	String service = (request.getParameter("service") == null ? "" : request.getParameter("service"));
	String appid = (request.getParameter("appid") == null ? "" : request.getParameter("appid"));
	
	String callUrl=request.getRequestURL().toString();
	OutResourceVO resourceVO=AdminOutResourceConst.getInstance().getResourceVO(callUrl);
	String path = request.getContextPath(); 
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%=resourceVO.getLoginTitle() %></title>
<jsp:include page="./inc/easyui.jsp"></jsp:include>
<%-- <script src="<%=request.getContextPath()%>/js/security.js?v=<%=version%>" charset="UTF-8" type="text/javascript"></script>
<link href="<%=request.getContextPath()%>/css/login.css?v=<%=version%>" rel="stylesheet" type="text/css" /> --%>
<script src=<%UpdateGradeVersion.outversion(out,"./js/security.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"js/jquery.qrcode.min.js");%> charset="UTF-8" type="text/javascript"></script>
<link href=<%UpdateGradeVersion.outversion(out, "./css/login.css");%> rel="stylesheet" />
<script>
DZF.contextPath = "<%=request.getContextPath() %>";
var contextPath = "<%=request.getContextPath()%>";
DZF.loginAU = false;
if(self!=top){
	<% if (StringUtil.isEmpty(service) == false) {%>
		top.location.href="<%=request.getContextPath()%>/login_admin_kj.jsp?service=<%=URLEncoder.encode(service, "UTF-8")%>&appid=<%=appid%>";
	<%} else {%>
		top.location.href="<%=request.getContextPath()%>/login.jsp";
	<%}%>

}
//enter键换行代替tab键begin
$(document).ready(function(){
	$(document).on('keyup', 'input', function(e) {
		 if(e.keyCode == 13 && e.target.type!== 'submit') {
		   var inputs = $(e.target).parents("form").eq(0).find(":input:visible"),
		   idx = inputs.index(e.target);
		       if (idx == inputs.length - 1) {
		          inputs[0].select()
		       } else {
		          inputs[idx + 1].focus();
		          inputs[idx + 1].select();
		       }
		 }
		});
});
 $(function() {
	 var isIE = !!window.ActiveXObject;
	 if(isIE){
		 var browser=navigator.appName;
		 var b_version=navigator.appVersion;
		 var version=b_version.split(";");
		 var trim_Version=version[1].replace(/[ ]/g,""); 
		 if(browser=="Microsoft Internet Explorer" && trim_Version=="MSIE6.0" ||
	    		 browser=="Microsoft Internet Explorer" && trim_Version=="MSIE7.0" ||
	    		 browser=="Microsoft Internet Explorer" && trim_Version=="MSIE8.0" ){
			 $.messager.alert('浏览器版本提示','您所使用的浏览器版本与系统不兼容!请升级到IE9及以上版本或更换火狐，谷歌或360等浏览器！',null,'info');
		 }
	 }
	 <%
	 	if(message != null && message.length() > 0){
	 	%>
	 		$.messager.alert('提示','您被其它用户强制退出！','info');
	 	<%}
	 %>
	 formatterDate = function(date) {
		 var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
		 var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0"
		 + (date.getMonth() + 1);
		 return date.getFullYear() + '-' + month + '-' + day;
	 };
	 
	$("#login").form({
		url:'${pageContext.request.contextPath}/sys/sm_user!channelLogin.action', 
		onSubmit: function(param){
			alert(param);return false;
			 return $(this).form('validate');
		}
	});
	
	
	$("#verify").keyup(function(event){//#user_code,#user_password,
        if(event.keyCode == 13){
        		loginForm();	
        }
    });

	$("#user_code").focus();
		 

	$("#date").datebox("setValue", formatterDate(new Date));
	
	//监听大小写
    var isCapital = -1; //是否大写 -1:无状态、0:小写、1:大写
   // jQuery(window).keyup(changeCapsLock);//监听全局
    //监听密码框
    $("#user_password").on('keyup', function(e){
        var lastVal = '';
        if ((e.keyCode >= 65 && e.keyCode <= 90) || e.keyCode == 13) {
            console.log(e);
            lastVal = jQuery(this).val().substr(getCursortPosition.call(this,this)-1, 1).charCodeAt(0);
            if ( lastVal == e.keyCode) {
                e.shiftKey ? '' : isCapital = 1;
                tipsCapsLock.call(this);
            }else{
                e.shiftKey ? '' : isCapital = 0;
                $("#capital").css('display','none'); 
            }
        }else{
            changeCapsLock.call(this, e);
        }
    });
    //是否切换大小写
    function changeCapsLock(e){
        e.stopPropagation();
        if (e.keyCode !== 20) {return;}
        switch(isCapital){
            case -1:
                break;
            case 0:
                isCapital = 1;
                tipsCapsLock.call(this,this);
                if (this !== window) tipsCapsLock.call(this);
                break;
            case 1:
                if (this !== window) $("#capital").css('display','none'); 
                isCapital = 0;
                break;
        }
    }
    //提示大小写
    function tipsCapsLock(){
    	 $("#capital").css('display','block');
    }
    //得到当前输入光标的位置
    function getCursortPosition (ctrl) {
        var CaretPos = 0;   // IE Support
        if (document.selection) {
            ctrl.focus ();
            var Sel = document.selection.createRange ();
            Sel.moveStart ('character', -ctrl.value.length);
            CaretPos = Sel.text.length;
        }
        // Firefox support
        else if (ctrl.selectionStart || ctrl.selectionStart == '0')
            CaretPos = ctrl.selectionStart;
        return (CaretPos);
    } 
    $("#user_password").on('blur',function(){
   	 $("#capital").css('display','none');
   });
});
function loginForm(){
	if(!$("#login").form('validate')){
		return;
	}
	if($("#verify").val() == ''){
		$.messager.show({
			title:"错误提示",
			msg:"请输入验证码！"
		});
		$("#verify").focus();
		return;
	}
	
	var modulus = '<%=modulus%>', exponent = '<%=exponent%>';
	var publicKey = RSAUtils.getKeyPair(exponent, '', modulus);
    var dcpassword=RSAUtils.encryptedString(publicKey, $("#user_password").val());
    var dcusercode=RSAUtils.encryptedString(publicKey, $("#user_code").val());
	
	//$('#login').form('submit');
	$.post('${pageContext.request.contextPath}/sys/sm_user!channelLogin.action', 
			{
				'date':$("#date").datebox("getValue"),
				//'data.user_code':$("#user_code").val(),
				//'data.user_password':$("#user_password").val(),
				'data.user_code':dcusercode,
				'data.user_password':dcpassword,
				'verify':$("#verify").val(),
				'f':$("#force").val()
			},
			   function(data){
				if(data.success){
					/* setTimeout(function(){
	            		$("#companyInfo").dialog({
							width : 500,
							height : 500,
							readonly : true,
							title : '选择公司',
							modal : true,
							href : data.head,
							align:"center",
							buttons : '#cp_buttons'
					}).dialog("center");
	            	},0);//1800 */
					<% if (StringUtil.isEmpty(service) == false) {
						%>
							if(data.head){
								var info = data.msg;
								if (info  == "1")
								{
			        				window.location.href=data.head + "&qz=" + info;
			        			}
			        			else
			        			{
			        				window.location.href=data.head;
			        			}
							}
						<%} else {
						 %>
							window.location.href="${pageContext.request.contextPath}/";
						<%} 
						 %>	
				}else{
					
					if(data.status == -100){
						$.messager.confirm('确认对话框', '该用户已经登陆，是否强制退出？', function(r){
							if (r){
								$("#force").val("1");
								loginForm();
							}
						});
						return;
					}
					changeAu();
					if(data.status == -200) {
						var tip = data.msg;
						tip = tip == null || tip.length == 0 ? '密码过于简单，请修改密码后重新登录': tip;
						$.messager.confirm('提示', tip, function(r){
							if (r){
								$("#user_password").val("");
								$("#verify").val('');
								$("#psw2").val("");
								$("#psw3").val("");
								$("#upsw").show();
								$("#upsw").dialog({
									title: '修改密码',
									width:380,
									height:250,
									modal:true,
									buttons : '#qhan_buttons'
								});
							}
						});
						return;
					}
					$.messager.show({
						title:"错误提示",
						msg:data.msg
					});
				}
			   }, "json");
}

/* function dbcheck(){
	selectCompany();
} */

<%-- function selectCompany(){
	
	var service= '<%=service%>';
	
	var row = $("#gsTable").datagrid('getSelected');
	jQuery.ajax({//提交审批，返回审批结果
		url : '${pageContext.request.contextPath}/sys/sm_user!gsSelectAdmin.action',
		data : {
			id : row.pk_gs,
			service:service
		},
		type : 'post',
		dataType : 'json',
		success : function(obj) {
			if(obj.success){
				$("#companyInfo").dialog("close");
				<% if (StringUtil.isEmpty(service) == false) {
				%>
					if(obj.head){
						var info = obj.msg;
						if (info  == "1")
						{
	        				window.location.href=obj.head + "&qz=" + info;
	        			}
	        			else
	        			{
	        				window.location.href=obj.head;
	        			}
					}
		 	 	<%} else {
				 %>
					window.location.href="${pageContext.request.contextPath}/";
				<%} 
				 %>	
			}else{
				$.messager.show({
					title:"错误提示",
					msg:obj.msg
				})
			}
		}
	});
} --%>

function changeAu(){
    var timestamp = (new Date()).valueOf();
	$("#codeImg").attr("src","./au/image.jsp?timestamp=" + timestamp);   
}

function savePsw(){
	if(!$("#pwForm").form('validate')){
		return;
	}
	
	var modulus = '<%=modulus%>', exponent = '<%=exponent%>';
	var publicKey = RSAUtils.getKeyPair(exponent, '', modulus);
	var dcpassword=RSAUtils.encryptedString(publicKey, $("#original").val());
	var dcusercode=RSAUtils.encryptedString(publicKey, $("#user_code").val());
    var psw2=RSAUtils.encryptedString(publicKey, $("#psw2").val());
    var psw3=RSAUtils.encryptedString(publicKey, $("#psw3").val());
	
	$.post('${pageContext.request.contextPath}/sys/sm_user!updatePwdLogin.action', 
			{
				//'data.user_code':$('#user_code').val(),
				//'data.user_password':$('#original').val(),
				//'psw2' : $('#psw2').val(),
				//'psw3' : $('#psw3').val()
				'data.user_code':dcusercode,
				'data.user_password':dcpassword,
				'psw2' : psw2,
				'psw3' : psw3
			},
			   function(data){
					if(data.success){
						$('#upsw').dialog('close');
						$.messager.show({
							title:"提示",
							msg:"修改成功，请重新登录"
						});
					}else{
						$.messager.show({
							title:"错误提示",
							height : 130,
							msg:data.msg
						});
						return;
					}
	}, "json");
}
</script>
</head>
<body>
<div class="header">
	<div class="top"><div class="top_logo"><img src="<%=request.getContextPath()%>/img/logo.png" /></div></div>
</div>

<div class="datu">
	<div class="zhongbu">
		<div class="zou">
			<img class="ml90" src="<%=request.getContextPath()%>/img/daizhang.png" />
			<img src="<%=request.getContextPath()%>/img/login_bg_dz.png" />
		</div>
		<form id="login" method="post">
			<div class="denglu module-static">
			<div class="login-switch" onselectstart="return false;"> <i class="iconfont quick" id="change-quick" onclick="">&#xe628;</i> <i class="iconfont static" id="change-static">&#xe626;</i> </div>
				<div class="pass1 static-form"> 
					<%
					if(basePath.contains(Outresource.b5_gs)){//b5管理端
					%>
					<!-- <h2 style="height:60px;line-height:60px">登录企业服务平台</h2> -->
					<font style="font-size: 22px;height:70px;line-height:70px">登录企业服务平台</font>
					<%
					}else{
					%>				
					<h1><img src="<%=request.getContextPath()%>/img/daizhang_login.png" /></h1>
					<%
					}
					%>
					<div class="kuang">
						<p class="riqi"><label>日期：</label><input class="easyui-datebox" name="date" id="date"  data-options="required:true,width:290,height:50,editable:false" type="text" ></p>
						<p><label>用户：</label><input name="data.user_code" id="user_code" type="text" class="easyui-validatebox" data-options="required:true"  /></p>
						<p><label>密码：</label><input name="data.user_password" id="user_password" class="easyui-validatebox" type="password"  data-options="required:true"/></p>
						<span id="capital" style="display:none;color:red;padding-left: 70px;">大写锁定已开启</span>
						<p id="verify1" ><label>验证码：</label>
						<input  type="text" class="yzm150" id="verify" name="verify" style="ime-mode:disabled"/>
						<strong><img id="codeImg" onclick="changeAu();" src="./au/image.jsp"></strong>
						<a href="javascript:changeAu();" class="yzsx"><img src="./img/sx.jpg" ></a>
						</p>
					</div>
					<div class="zidong">
						<!-- <P><input type="checkbox" name="auto" value="1"><span>自动登录</span></P> -->
						<P><a href="<%=request.getContextPath()%>/searchPsw.jsp"><span>忘记密码</span></a> </P>
					</div>
					<div class="jinru">
						<button type="button" onclick="loginForm()">登录</button>
					</div>
				</div>
				<div class="bg_f4">&nbsp;</div>
				<!--扫码登录开始-->
		        <div class="pass1 quick-form">         
		        <div class="qrcode-login">
		          <div class="login-title">手机扫码，安全登录</div>
		          <div class="qrcode-mod">
		            <div class="qrcode-main">
		              <div id="qrcode-img" class="qrcode-img"><!-- <img src="img/show.png"> --></div>
		              <div class="msg-err">
		                <h6>二维码已失效</h6>
		                <a href="javascript:;" class="refresh">请点击刷新</a> </div>
		            </div>
		            <div class="qrcode-desc"> <i class="iconfont">&#xe619;</i>
		              <p><font class="ft-gray">打开</font><a href="" target="_blank" class="light-link">企业管理平台app</a><br>
		                <span class="ft-gray">扫一扫登录</span></p>
		            </div>
		          </div>
		          <div class="qrcode-msg">
		            <div class="msg-ok">
		              <div class="msg-icon"> <i class="iconfont icon-ok">&#xe60e;</i> <i class="iconfont icon-phone">&#xe611;</i> </div>
		              <h6>扫描成功！</h6>
		              <p>请在手机上确认登录</p>
		              <div class="link"><a href="javascript:;" class="light-link back-quick">返回二维码登录</a></div>
		            </div>
		          </div>
		        </div>       
		      </div>
       	  	  <!--扫码登录结束--> 
			</div>
		<input type="hidden" name="f" id="force" value="0" />
		</form>
	</div>
</div>
<div id="cp_buttons" style="display:none;">
	<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCompany();" style="width:90px">确认</a> 
	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#companyInfo').dialog('close');" style="width:90px">取消</a>
</div>
<div id="companyInfo">
</div>
<div id="upsw" style="display:none;padding:10px 20px;">
	<form id="pwForm" method="post">
		<p style="margin-top:10px;">
		输入初始密码：
		<input id="original" name="data.user_password" type="password" class="easyui-textbox" style="width:210px;height:30px;" data-options="required:true" />
		</p>
		<p style="margin-top:10px;">
		请输入新密码：
		<input id="psw2" name="psw2" type="password" class="easyui-textbox" style="width:210px;height:30px;" data-options="required:true" />
		</p>
		<p style="margin-top:10px;">
		再次输入密码：
		<input id="psw3" name="psw3" type="password" class="easyui-textbox" style="width:210px;height:30px;" data-options="required:true" />
		</p>
	</form>
</div>
<div id="qhan_buttons" style="display:none" >
	<a href="javascript:void(0)" class="ui-btn save_input"  onclick="savePsw();" >确认</a> 
	<a href="javascript:void(0)" class="ui-btn save_input" onclick="javascript:$('#upsw').dialog('close');" >取消</a>
</div>

<div class="banquan">
	<p><%=resourceVO.getCopyRight() %></p>
</div>


<script>
$(".refresh").click(function(){
	$(this).parents(".denglu").removeClass("module-static").addClass("module-quick");
	if($(".qrcode-login").hasClass("qrcode-login-error")){
		$(".qrcode-login").removeClass("qrcode-login-error");
	};
	var msg;
	jQuery.ajax({//获取二维码
		url : '${pageContext.request.contextPath}/app/loginqr!getQRCode2.action',
		type : 'post',
		dataType : 'json',
		success : function(obj) {
			if(obj.success){
				$('#qrcode-img').empty();
				jQuery('#qrcode-img').qrcode(utf16to8(obj.msg));
				msg = obj.msg;
			}//else{
				//$.messager.show({
				//	title:"错误提示",
				//	msg:obj.msg
				//})
			//}
		}
	});
	var timesRun = 10;
	timer = setInterval(function(){
		timesRun -= 1;
		console.log(timesRun);
		if(timesRun === 0){
			clearInterval(timer);
			$(".qrcode-login").addClass("qrcode-login-error");
		}else{
			if(msg != null || msg == ''){
				longConnCheck(msg, timesRun);
			}
			
		}
	}, 3000);
});	
$("#change-quick").click(function(){
	$(this).parents(".denglu").removeClass("module-static").addClass("module-quick");
	if($(".qrcode-login").hasClass("qrcode-login-error")){
		$(".qrcode-login").removeClass("qrcode-login-error");
	};
	var msg;
	jQuery.ajax({//获取二维码
		url : '${pageContext.request.contextPath}/app/loginqr!getQRCode2.action',
		type : 'post',
		dataType : 'json',
		success : function(obj) {
			if(obj.success){
				$('#qrcode-img').empty();
				jQuery('#qrcode-img').qrcode(utf16to8(obj.msg));
				msg = obj.msg;
			}//else{
				//$.messager.show({
				//	title:"错误提示",
				//	msg:obj.msg
				//})
			//}
		}
	});
	var timesRun = 10;
	timer = setInterval(function(){
		timesRun -= 1;
		console.log(timesRun);
		if(timesRun === 0){
			clearInterval(timer);
			$(".qrcode-login").addClass("qrcode-login-error");
		}else{
			if(msg != null || msg == ''){
				longConnCheck(msg, timesRun);
			}
			
		}
	}, 3000);
});	

function longConnCheck(msg, timesRun){
	var service= '<%=service%>';
	jQuery.ajax({//验证二维码
		url : '${pageContext.request.contextPath}/app/loginqr!longConnCheck2.action',
		data: {chdata : msg,
			service:service
			},
		type : 'post',
		dataType : 'json',
		success : function(obj) {
			if(obj.success){
				if(150 === obj.status){
					$(".qrcode-login").addClass("qrcode-login-ok");
				}else if(200 === obj.status){
					clearInterval(timer);
					//checkLoginSuccess(msg);
					
					<% if (StringUtil.isEmpty(service) == false) {
					%>
						if(obj.head){
							var info = obj.msg;
							if (info  == "1")
							{
		        				window.location.href=obj.head + "&qz=" + info;
		        			}
		        			else
		        			{
		        				window.location.href=obj.head;
		        			}
		        		}
		 		 	<%} else {
					 %>
					 	window.location.href="${pageContext.request.contextPath}/";
					<%} 
					 %>
					 
					
				}
			}//else{
			//	$.messager.show({
			//		title:"错误提示",
			//		msg:obj.msg
			//	})
			//}
		}
	});
}


$("#change-static").click(function(){
	clearInterval(timer);
	$(this).parents(".denglu").removeClass("module-quick").addClass("module-static");
});
$(".back-quick").click(function(){
	$(".qrcode-login").removeClass("qrcode-login-ok");
})
function utf16to8(str) {
    var out, i, len, c;
    out = "";
    len = str.length;
    for (i = 0; i < len; i++) {
        c = str.charCodeAt(i);
        if ((c >= 0x0001) && (c <= 0x007F)) {
            out += str.charAt(i);
        } else if (c > 0x07FF) {
            out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F));
            out += String.fromCharCode(0x80 | ((c >> 6) & 0x3F));
            out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
        } else {
            out += String.fromCharCode(0xC0 | ((c >> 6) & 0x1F));
            out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
        }
    }
    return out;
}
</script>
</body>

</html>		
