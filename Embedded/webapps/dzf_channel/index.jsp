<%@page import="com.dzf.pub.chat.ChatCommon"%>
<%@page import="com.dzf.pub.chat.CryptUtil" %>
<%@page import="com.dzf.model.sys.tips.VersionTipsVO"%>
<%@page import="com.dzf.service.sys.versiontips.ISysVersionTips"%>
<%@page import="com.dzf.model.sys.sys_power.SysFunNodeVO"%>
<%@page import="com.dzf.service.sys.sys_power.ISysFunnodeService"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="com.dzf.pub.cache.CorpCache"%>
<%@page import="com.dzf.model.sys.sys_power.CorpVO"%>
<%@page import="com.dzf.pub.cache.UserCache"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.pub.DzfUtil"%>
<%@page import="com.dzf.pub.lang.DZFBoolean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.StringUtil"%>

<%
	String datevalue = (String)session.getAttribute(IGlobalConstants.login_date);
	String corp=(String) session.getAttribute(IGlobalConstants.login_corp);
	String userid=(String) session.getAttribute(IGlobalConstants.login_user);
	if(corp == null || userid == null){
		session.setAttribute("errorMsg", "无权操作,请联系管理员!");
		request.getRequestDispatcher("/error_kj.jsp").forward(request,response);
		return;
	}
	UserVO userVo = UserCache.getInstance().get(userid, corp);//()(UserVO)session.getAttribute(IGlobalConstants.login_user);
	String date = (String)session.getAttribute(IGlobalConstants.login_date);
	CorpVO corpVo =CorpCache.getInstance().get(userVo.getPrimaryKey(), corp);
	ServletContext servletContext = session.getServletContext();		
	ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
	ISysFunnodeService sysFunnodeService = (ISysFunnodeService)ctx.getBean("sys_funnodeserv");
	List<SysFunNodeVO> list = sysFunnodeService.querySysnodeByUserAndCorp(userVo,corpVo, "dzf_channel");
//  首页的节点
	Map<String,SysFunNodeVO> firstPageMap = new TreeMap<String,SysFunNodeVO>();
	Map<String,List<SysFunNodeVO>> nodeMap = new TreeMap<String,List<SysFunNodeVO>>();
	if(list!=null && list.size()>0){
		for(SysFunNodeVO vo:list){
			
			DZFBoolean isshow = vo.getIshidenode()==null ?DZFBoolean.FALSE:vo.getIshidenode();
			if(isshow.booleanValue()){
				firstPageMap.put(vo.getFun_code(),vo);
			}else{
				String parentId = vo.getPk_parent();
				if(parentId == null || parentId.trim().length()<1){
					parentId = "0";
				}
				if(nodeMap.get(parentId) == null){
					nodeMap.put(parentId,new ArrayList<SysFunNodeVO>());
				}
				nodeMap.get(parentId).add(vo);
			}
		}
	}
	session.removeAttribute(IGlobalConstants.logout_msg);
	//版本信息提示
	ISysVersionTips sys_versionimpl =(ISysVersionTips)ctx.getBean("sys_versionimpl");
	VersionTipsVO[] tipsvos = (VersionTipsVO[])sys_versionimpl.getVersionTips("1");

	
	String modulus =(String)session.getAttribute("MODULUS");
	String exponent =(String)session.getAttribute("EXPONENT");
	
	String cuserid = CryptUtil.getInstance().encryptAES(userid+"###kjgs");
	String username = userVo.getUser_name();
	String chatserver = ChatCommon.readchatServer();
	
	String callUrl=request.getRequestURL().toString();
	
	//"3004753485", "3004754158", "3004770986"
	String qqline = (String)session.getAttribute("QQLINE");
	String path = request.getContextPath(); 
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html><head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>大账房</title>
<jsp:include page="./inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "css/main.css");%> rel="stylesheet" />
<link href=<%UpdateGradeVersion.outversion(out, "css/index_index.css");%> rel="stylesheet" />
<link id="parent-skin" href="css/lan_skin.css" rel="stylesheet" />
<link href="layui/css/layui.css" rel="stylesheet">
<link href="layui/dzfchat.css" rel="stylesheet">
<style>
</style> 
</head>
<script language=javaScript> 
var indexContextPath = "<%=request.getContextPath() %>";
DZF.contextPath = indexContextPath;
var islogin =  "<%=userVo.getIslogin() != null ? userVo.getIslogin() : "N"%>";
	
function showOpt(id, show){
	if(!$('#' + id))
		return;
	if (show)
		$('#' + id).show();
	else
		$('#' + id).hide();
}	
</script>
<body class="dzf-skin index_body index_body_index" >
<div class="logo">
	<span><img src="./images/logoo.png" /></span>
</div>
<div id="sidebar" class="sidebar easyui-accordion" style="height:100%;" >
	<ul id="nav">
		<%List<SysFunNodeVO> lfirst = nodeMap.get("0");
		if(lfirst!=null && lfirst.size()>0){
			for(SysFunNodeVO first:lfirst){
		%>
		<li class="nav_li" id="<%=first.getFun_code()%>_<%=first.getPk_funnode()%>">
			<a href="javascript:void(0)"><span><i class="<%=first.getFun_code()%>"></i></span><font><%=first.getFun_name() %></font></a>
		</li>
		<%}
		}%>
	</ul>
</div>
<div class="main">
	<%	
	if(lfirst != null && lfirst.size() > 0){
		for(SysFunNodeVO first:lfirst){
			List<SysFunNodeVO> lsecond = nodeMap.get(first.getPk_funnode());
			%>
			<div class="sub_menu" style="display:none;" id="<%=first.getFun_code()%>_<%=first.getPk_funnode()%>_sub">
			<%
			if (lsecond != null && lsecond.size() > 0) {
				int i = 0;
				List<SysFunNodeVO> hasThirdNode = new ArrayList<SysFunNodeVO>();
				for (SysFunNodeVO second:lsecond) {
					List<SysFunNodeVO> lthree = nodeMap.get(second.getPk_funnode());
					String arrow = "";
					if(i == 0){%>
					<div class="sub_menu_div">
						<ul class="sub_menu_ul">
					<%}
					if (lthree != null && lthree.size() > 0) {
						hasThirdNode.add(second);
						arrow = "<em></em>";
					} 
					%>
					<li id="<%= second.getPk_funnode()%>"><a href="<%=second.getFile_dir()!=null&&second.getFile_dir().trim().length()>0?"javascript:addTab('"+second.getFun_name()+"','"+request.getContextPath()+second.getFile_dir()+"')":"javascript:void(0)"%>" id="<%=second.getPk_funnode()%>"><%=second.getFun_name() %></a><%=arrow %></li>
					<%
					if(i == lsecond.size()-1){%>
					</ul>
				</div>
					<%}
					i++;
				}
				if (hasThirdNode.size() > 0) {
					for (SysFunNodeVO hasThird: hasThirdNode) {
						%>
						<div id="<%=hasThird.getPk_funnode() + "_sub" %>" style="display:none" class="sub_menu_div1">
							<ul class="sub_menu_ul">
						<%
						List<SysFunNodeVO> thirds = nodeMap.get(hasThird.getPk_funnode());
						for(SysFunNodeVO third:thirds){
						%>
							<li><a href="<%=third.getFile_dir()!=null&&third.getFile_dir().trim().length()>0?"javascript:addTab('"+third.getFun_name()+"','"+request.getContextPath()+third.getFile_dir()+"')":"javascript:void(0)" %>" id="<%=third.getFun_name()%>"><%=third.getFun_name()%></a></li>
						<%
						}
						%>
							</ul>
						</div>
						<%
					}
				}
			}
			%>
			</div>
			<%
		}
	}
	%>
	<div id="main_container">
		<div class="main_top" style="min-width:1100px !important;">
			<div class="top_text">
				<span class="home-icon"></span>
				<div class="corp_info">
					<p id="user_info" style="overflow:hidden;width:200px;white-space:nowrap;text-overflow:ellipsis;cursor: pointer;" 
						title='<%= ( (corpVo.getUnitname() == null) || (corpVo.getUnitname() == "") ? "":corpVo.getUnitname() + "  ")%>'>
						<%= ( (corpVo.getUnitname() == null) || (corpVo.getUnitname() == "") ? "":corpVo.getUnitname() + "  ")%>
					</p>
					<p id="showDate"><%=date!=null?date:"" %></p>
				</div>
			</div>
			
			<div class="user_menu" style="position: relative;">
				<ul class="ui_operation">
					<li class="drop_down_block" onmouseover="showOpt('set_sub1', true);" onmouseout="showOpt('set_sub1', false);">
						<span>
							<b></b>
							<button style="cursor: pointer;width:80px;text-overflow：ellipsis;white-space:nowrap;overflow:hidden;" title="<%=userVo.getUser_name()%>"><%=userVo.getUser_name()%></button>
							<i class="user-arrow"></i>
						</span>
						<div class="drop_down" id="set_sub1" style="display: none;">
							<!-- <p onclick="javascript:addTab('用户信息','user_maintain.jsp')"><a href="#"><span class="circle-icon"></span>用户信息</a></p> -->
							<p onclick="javascript:loginOut()"><a href="#"><span class="circle-icon"></span>安全退出</a></p>
						</div>
					</li>
				</ul>
			</div>
		</div>
	
		<div class="main_cont" id="main">
			<div class="mian_content" title="首页">
			<div class="mian_content1">
				<div id="tabsMenu" class="easyui-menu" style="width:120px; display:none"> 
		   			 <div name="close">关闭</div> 
				     <div name="Other">关闭其他</div> 
				     <div name="All">关闭所有</div>
		  		</div> 
		</div>
	</div>
</div>
</div>
</div>
<form id="form" method="post">
	<div id="upsw" style="display:none;padding:10px 20px;">
		<p style="margin-top:5px;">
		是否补充密码信息
		<input id="isEditInfo" name="isEditInfo" type="checkbox"/>
		</p>
		<p style="margin-top:10px;">
		输入初始密码：
		<input id="user_password" name="data.user_password" type="password" class="easyui-textbox" style="width:210px;height:30px;" data-options="required:true" />
		</p>
		<p style="margin-top:10px;">
		请输入新密码：
		<input id="psw2" name="psw2" type="password" class="easyui-textbox" style="width:210px;height:30px;" data-options="required:true" />
		</p>
		<p style="margin-top:10px;">
		再次输入密码：
		<input id="psw3" name="psw3" type="password" class="easyui-textbox" style="width:210px;height:30px;" data-options="required:true" />
		<input name="data.user_name" id="user_name" type="hidden" value="<%=userVo.getUser_name()%>"/>
		</p>
		<div id="editInof" style="display:none">
			<p style="margin-top:10px;">
			请输入手机号：
			<input id="phone" name="phone" type="text" class="easyui-textbox" style="width:210px;height:30px;" data-options="required:true" />
			</p>
			<p style="margin-top:10px;">
			请输入邮箱&nbsp;&nbsp;&nbsp;：
			<input id="uEmail" name="uEmail" type="text" class="easyui-textbox" style="width:210px;height:30px;" data-options="required:true" />
			</p>
		</div>
	</div>
</form>
<div id="qhan_buttons" style="display:none" >
		<a href="javascript:void(0)" class="ui-btn save_input"  onclick="savePsw();" >确认</a> 
		<a href="javascript:void(0)" class="ui-btn save_input" onclick="javascript:$('#upsw').dialog('close');" >取消</a>
</div>
<script>
var SYSTEM = {
		login_corp_id:"<%=corpVo.getPk_corp() %>",
		login_corp_code:"<%=corpVo.getUnitcode()%>",
		login_corp_name:"<%=corpVo.getUnitname()%>",
		UserName:"<%=userVo != null ? userVo.getUser_name() : "" %>",//当前登录用户名
		LoginDate:"<%=date!=null?date:"" %>"
	};
	//当浏览器加载时 自动识别当前的屏幕大小 设置main的不同大小
	window.onload = function(){
		$('#main').tabs('resize', {
		   width: $("#main").parent().width(),
		   height: (document.documentElement.clientHeight - 58)
		 
	 	});
		
	}; 
 $(window).resize(function() {
    //当浏览器大小变化时
    $('#main').tabs('resize', {
     width: $("#main").parent().width(),
       height: (document.documentElement.clientHeight - 58)
    });
    
});  
function updatePsw(){
	$("#isEditInfo").removeAttr("checked");
	$("#user_password").val("");
	$("#psw2").val("");
	$("#psw3").val("");
	$("#upsw").show();
	$("#editInof").hide();
	
	$("#upsw").dialog({
		title: '用户信息维护',
		width:380,
		height:250,
		modal: true,
		buttons : '#qhan_buttons'
	});
}
function checkUserPwd(){
	$("#psw2,#psw3").blur(function(){
		var value = $(this).val();
		if(value != null && value != ""){
			//字母和数字组成
//			var strExp=/.*([0-9]+.*[A-Za-z]+|[A-Za-z]+.*[0-9]+).*/;
			var strExp = /.*([0-9].*([a-zA-Z].*[~!@#$%^&*()<>?+=]|[~!@#$%^&*()<>?+=].*[a-zA-Z])|[a-zA-Z].*([0-9].*[~!@#$%^&*()<>?+=]|[~!@#$%^&*()<>?+=].*[0-9])|[~!@#$%^&*()<>?+=].*([0-9].*[a-zA-Z]|[a-zA-Z].*[0-9])).*/;
			if(strExp.test(value)){
				$("#"+$(this).attr("id")+"_ck").hide(); 
				delete ckmap[$(this).attr("id")+"_ck"];
			}else{
				Public.tips({content:"提示：密码必须包含数字、字母、特殊字符！",type:1});
				return;
			}
		}
	});
}
function savePsw(){
	checkUserPwd();
	if( $("#user_password").val() == $("#psw2").val() ){
		Public.tips({content:"提示：旧密码和新密码不能一致！",type:1});
		return;
	}
	if($("#psw2").val().length<8){
		Public.tips({content:"提示：密码必须大于8位！",type:1});
		return;
	}
	if($("#psw3").val().length<8){
		Public.tips({content:"提示：密码必须大于8位！",type:1});
		return;
	}
	if($("#psw3").val() != $("#psw2").val()){
		Public.tips({content:"提示：两次密码不一致！",type:1});
		return;
	}
	if(!$("#form").form('validate')){
		return;
	}
	
		//验证手机号码是否正确
	if($("#isEditInfo").prop("checked")){
		var value = $("#phone").val();
		//手机号码正则表达式
		var telReg = !!value.match(/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/);
		if(telReg ==  false){
			Public.tips({content: "请输入正确的手机号码！",type:2});
			return ;
		}
		//邮箱正则表达式
		var mailValue = $("#uEmail").val();
		var mailReg = !!mailValue.match(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/);
		if(mailReg == false){
			Public.tips({content: "请输入正确的邮箱！",type:2});
			return ;
		}
	}
	
	var modulus = '<%=modulus%>', exponent = '<%=exponent%>';
	
	var publicKey = RSAUtils.getKeyPair(exponent, '', modulus);
	var dcpassword=RSAUtils.encryptedString(publicKey, $("#user_password").val());
    var psw2=RSAUtils.encryptedString(publicKey, $("#psw2").val());
    var psw3=RSAUtils.encryptedString(publicKey, $("#psw3").val());
	
	$.post('${pageContext.request.contextPath}/sys/sm_user!updatePsw.action', 
			{
				'data.user_name':$('#user_name').val(),
				'data.user_password':dcpassword,
				'psw2' : psw2,
				'psw3' : psw3,
				'phonenum' : $("#phone").val(),
				'mail' : $("#uEmail").val()
			},
			   function(data){
					if(data.success){
						$('#upsw').dialog('close');
						Public.tips({content:data.msg,type:0});
						setTimeout('window.location.href= DZF.contextPath + "/login.jsp"', 2000 );
					}else{
						Public.tips({content:data.msg,type:1});
						return;
					}
	}, "json");
}

function refreshPage(){
	var allTabs = $("#main").tabs("tabs");
	var closeTabsTitle = [];
	$.each(allTabs, function () {
		var opt = $(this).panel("options");
		if (opt.closable) {
			closeTabsTitle.push(opt.title);
		}
	});
	for (var i = 0; i < closeTabsTitle.length; i++) {
		 $("#main").tabs("close", closeTabsTitle[i]);
	}
}
$(function() {
	 $('#main').tabs({
		 width: $("#main").parent().width(),
		 height:(document.body.clientHeight-58),
		 tabHeight:30,
		 onSelect:function(title,index){
		 }
	});
	checkUserPwd();
	
	$('.nav_li').hover(function(){
		var backSpan = $(this).find("span");
		var preClass = backSpan.attr("class");
		backSpan.removeClass();
		$(this).find("span").addClass("h" + preClass);
        $(this).find("a").addClass("menu_1");
		$("#" + $(this).attr("id") + "_sub").show();
		document.getElementById($(this).attr("id")+"_sub").style.top = ($(this).offset().top)+"px";
		if($("#"+$(this).attr("id")+"_sub").offset().top+$("#"+$(this).attr("id")+"_sub").height()>$(window).height()){
			document.getElementById($(this).attr("id")+"_sub").style.top = $(window).height()-$("#"+$(this).attr("id")+"_sub").height()+"px";
			$("#"+$(this).attr("id")+"_sub").find("em").css("top",$(this).offset().top - $("#"+$(this).attr("id")+"_sub").offset().top+25+"px");
		}
     }, function(){
    	 var backSpan = $(this).find("span");
 		 var preClass = backSpan.attr("class");
 		 backSpan.removeClass();
 		 $(this).find("span").addClass(preClass.substr(1));
    	 $(this).find("a").removeClass("menu_1");
		 var subSta = true;
		 $("#" + $(this).attr("id") + "_sub").hover(function(){
			 $(this).show();
			 subSta= false;
		 },function(){
			 $(this).hide();
		 });
		if(subSta)
			$("#" + $(this).attr("id") + "_sub").hide();
	 });
	$(".sub_menu").hover(function(){
		var oh = $(this).height();
		$(this).find("div").height(oh);
		var index = $(this).index();
		$(".nav_li").eq(index).find("a").addClass("menu_1");
	},function(){
		var index = $(this).index();
		$(".nav_li").eq(index).find("a").removeClass("menu_1");
		$(this).hide();
	});
	
	$(".sub_menu_div li").hover(function () {
		$("#" + $(this).attr("id") + "_sub").show();
	}, function () {
		var subSta = true;		
		 $("#" + $(this).attr("id") + "_sub").hover(function(){
			 $(this).show();
			 subSta= false;
		 },function(){
			 $(this).hide();
		 });
		if(subSta)
			$("#" + $(this).attr("id") + "_sub").hide();
		
	});
   
	 $("#main").tabs({
		onContextMenu : function (e, title) {
			e.preventDefault();
			if(title != '首页'){
				$('#tabsMenu').menu('show', {
					left : e.pageX,
					top : e.pageY
				}).data("tabTitle", title);
			}
		},
	});
	
	$("#tabsMenu").menu({
		onClick : function (item) {
			CloseTab(this, item.name);
		}
	});
	
	function CloseTab(menu, type) {
		var curTabTitle = $(menu).data("tabTitle");
		var tabs = $("#main");
		
		if (type === "close") {
			tabs.tabs("close", curTabTitle);
			return;
		}
		
		var allTabs = tabs.tabs("tabs");
		var closeTabsTitle = [];
		
		$.each(allTabs, function () {
			var opt = $(this).panel("options");
			if (opt.closable && opt.title != curTabTitle && type === "Other") {
				closeTabsTitle.push(opt.title);
			} else if (opt.closable && type === "All") {
				closeTabsTitle.push(opt.title);
			}
		});
		
		for (var i = 0; i < closeTabsTitle.length; i++) {
			tabs.tabs("close", closeTabsTitle[i]);
		}
	}

	//判断是否要修改手机及邮箱信息
	$("#isEditInfo").change(function(){
		if($("#isEditInfo").prop("checked")){
			$("#upsw").dialog({
				//title: '修改密码',
				//width:380,
				height:350
				//buttons : '#qhan_buttons'
			});
			$("#editInof").show();
		}else{
			$("#upsw").dialog({
				height:270
			});
			$("#editInof").hide();
		}
	});
});	

function addTab(title, url){
	if ($('#main').tabs('exists', title)){
		$('#main').tabs('select', title);
	} else {
		var content = '<iframe scrolling="auto" name="win-iframe" frameborder="0"  src="'+url+'" style="width:100%;height:99%;"></iframe>';
		$('#main').tabs('add',{
			title:title,
			content:content,
			closable:true,
			onLoad: function(){
				alert(1);
			}
		});
	}
}
function addTabNew(title, url,iframeId){ 
	var content = '<iframe scrolling="auto" name="win-iframe" frameborder="0" name="' + title + '" ' + (iframeId ? " id=\"" + iframeId + "\" " : "") + ' src="'+url+'" style="width:100%;height:99%;"></iframe>';
	if ($('#main').tabs('exists', title)){
		var osrc = $("iframe[name=\"" + title + "\"]").attr("src");
		if(osrc != url){
			var tab = $('#main').tabs("getTab",title);
			$('#main').tabs("update",{
				tab: tab,
				options: {
					title:title,
					content:content,
					closable:true
				}
			});
		}
		
		$('#main').tabs('select', title);
	} else {
		$('#main').tabs("add",{
			title:title,
			content:content,
			closable:true
		});
	}
	void("function" == typeof callback && window.setTimeout(function() {
		callback();	
		}, 3000));
}
function closeTab(title){
	$('#main').tabs('close', title);
}

function loginOut(){
	$.messager.confirm("提示", "你确定要退出吗?", function(r) {
		if (r) {
			window.location.href=DZF.contextPath+'/sys/sm_user!logout.action'
		}
	});
}
</script>
<script>

$("dd .tz_row1").hover(function(){
	 var tzcon = $(this).find("a").attr("data-title");
	 var str = '<div class="tz-content">' + tzcon + "</div>"
	 $(this).append(str);
}, function(){
	 $(this).find("div").remove();
})
</script>
<script type="text/javascript" src="./js/jsapi.js"></script>
<script type="text/javascript" src="./js/corechart.js"></script>		
<script type="text/javascript" src="./js/jquery.gvChart-1.0.1.min.js"></script>
<script type="text/javascript" src="./js/jquery.ba-resize.min.js"></script>
<script type="text/javascript" src="./js/echarts.common.min.js"></script>
<input type="hidden" id="socketPath" value="<%=chatserver%>" />
<input type="hidden" id="loginid" value="<%=cuserid%>" />
<input type="hidden" id="xingming" value="<%=username%>" />
<script src="layui/layui.js" charset="UTF-8" type="text/javascript"></script>
<script type="text/javascript" src="layui/base64.min.js"></script>
<script type="text/javascript" src="layui/dzfchat.js"></script>
<script src=<%UpdateGradeVersion.outversion(out,"js/security.js");%> charset="UTF-8" type="text/javascript"></script>
<script type="text/javascript" src=<%UpdateGradeVersion.outversion(out,"js/index.js");%>></script>
</body>
</html>
