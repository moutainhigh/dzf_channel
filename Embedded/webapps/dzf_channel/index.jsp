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
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>

<%
	String datevalue = (String) session.getAttribute(IGlobalConstants.login_date);
	String corp = (String) session.getAttribute(IGlobalConstants.login_corp);
	String userid = (String) session.getAttribute(IGlobalConstants.login_user);
	if (corp == null || userid == null) {
		session.setAttribute("errorMsg", "无权操作,请联系管理员!");
		request.getRequestDispatcher("/error_kj.jsp").forward(request, response);
		return;
	}
	UserVO userVo = UserCache.getInstance().get(userid, corp);//()(UserVO)session.getAttribute(IGlobalConstants.login_user);
	//String date = (String) session.getAttribute(IGlobalConstants.login_date);
	String date = AdminDateUtil.getServerDate();
	String preDate = AdminDateUtil.getPreviousDate();
	String preYear = AdminDateUtil.getPreviousYear();
	CorpVO corpVo = CorpCache.getInstance().get(userVo.getPrimaryKey(), corp);
	ServletContext servletContext = session.getServletContext();
	ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);
	ISysFunnodeService sysFunnodeService = (ISysFunnodeService) ctx.getBean("sys_funnodeserv");
	List<SysFunNodeVO> list = sysFunnodeService.querySysnodeByUserAndCorp(userVo, corpVo, "dzf_channel");
	//  首页的节点
	Map<String, SysFunNodeVO> firstPageMap = new TreeMap<String, SysFunNodeVO>();
	Map<String, List<SysFunNodeVO>> nodeMap = new TreeMap<String, List<SysFunNodeVO>>();
	if (list != null && list.size() > 0) {
		for (SysFunNodeVO vo : list) {
			DZFBoolean isshow = vo.getIshidenode() == null ? DZFBoolean.FALSE : vo.getIshidenode();
			if (isshow.booleanValue()) {
				firstPageMap.put(vo.getFun_code(), vo);
			} else {
				String parentId = vo.getPk_parent();
				if (parentId == null || parentId.trim().length() < 1) {
					parentId = "0";
				}
				if (nodeMap.get(parentId) == null) {
					nodeMap.put(parentId, new ArrayList<SysFunNodeVO>());
				}
				nodeMap.get(parentId).add(vo);
			}
		}
	}
	session.removeAttribute(IGlobalConstants.logout_msg);
	//版本信息提示
	ISysVersionTips sys_versionimpl = (ISysVersionTips) ctx.getBean("sys_versionimpl");
	VersionTipsVO[] tipsvos = (VersionTipsVO[]) sys_versionimpl.getVersionTips("1");

	String modulus = (String) session.getAttribute("MODULUS");
	String exponent = (String) session.getAttribute("EXPONENT");

	String cuserid = CryptUtil.getInstance().encryptAES(userid + "###kjgs");
	String username = userVo.getUser_name();
	String chatserver = ChatCommon.readchatServer();

	String callUrl = request.getRequestURL().toString();

	//"3004753485", "3004754158", "3004770986"
	/* 	String qqline = (String)session.getAttribute("QQLINE");
		String path = request.getContextPath(); 
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"; */
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

<script type="text/javascript" src="./js/jsapi.js"></script>
<script type="text/javascript" src="./js/corechart.js"></script>		
<script type="text/javascript" src="./js/jquery.gvChart-1.0.1.min.js"></script>
<script type="text/javascript" src="./js/jquery.ba-resize.min.js"></script>
<script type="text/javascript" src="./js/echarts.common.min.js"></script>

<script src="layui/layui.js" charset="UTF-8" type="text/javascript"></script>
<script type="text/javascript" src="layui/base64.min.js"></script>
<script src=<%UpdateGradeVersion.outversion(out,"js/security.js");%> charset="UTF-8" type="text/javascript"></script>
<script type="text/javascript" src=<%UpdateGradeVersion.outversion(out,"js/index.js");%>></script>

<script language=javaScript> 
var indexContextPath = "<%=request.getContextPath() %>";
DZF.contextPath = indexContextPath;
var islogin =  "<%=userVo.getIslogin() != null ? userVo.getIslogin() : "N"%>";

var SYSTEM = {
	login_corp_id:"<%=corpVo.getPk_corp() %>",
	login_corp_code:"<%=corpVo.getUnitcode()%>",
	login_corp_name:"<%=corpVo.getUnitname()%>",
	UserName:"<%=userVo != null ? userVo.getUser_name() : "" %>",
	LoginDate:"<%=date!=null?date:"" %>",
	PreDate:"<%=preDate!= null ? preDate:"" %>",
	PreYear:"<%=preYear!= null ? preYear:"" %>",
};
var modulus = "<%=modulus%>";
var exponent = "<%=exponent%>";

var current = 0;
function tranImg(trun){
    var imgObj= document.getElementById('conturnid');
    current = (current+trun)%360;
    imgObj.style.transform = 'rotate('+current+'deg)';
}
</script>

</head>

<body class="dzf-skin index_body index_body_index" >
<input type="hidden" id="socketPath" value="<%=chatserver%>" />
<input type="hidden" id="loginid" value="<%=cuserid%>" />
<input type="hidden" id="xingming" value="<%=username%>" />
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
							<p onclick="javascript:updatePsw()"><a href="#"><span class="circle-icon"></span>用户信息</a></p>
							<p onclick="javascript:loginOut()"><a href="#"><span class="circle-icon"></span>安全退出</a></p>
						</div>
					</li>
				</ul>
			</div>
		</div>
	
		<div class="main_cont" id="main">
			<div class="mian_content" title="首页">
				<div class="mian_content1">
					<div clearfix row_customer style="height:100%;">
						<div class="week_AA">
							<form id = "tsweek">
								<div class="week">
									<h1>本周业务发展情况</h1>
									<p><samp></samp><span>本周新增加盟商：</span>
									<font>
										<input id="tswkfranc" name="tswkfranc" class="easyui-numberbox" 
											data-options="readonly:true," 
											style="width:80px;height:28px;text-align:right">户
									</font></p>
									<p><samp></samp><span>本周收到加盟费：</span>
									<font>
										<input id="tswkinitfee" name="tswkinitfee" class="easyui-numberbox" 
											data-options="readonly:true,precision:2,groupSeparator:','" 
											style="width:80px;height:28px;text-align:right">元
									</font></p>
									<p><samp></samp><span>本周新增客户：</span>
									<font>
										<input id="tswkcust" name="tswkcust" class="easyui-numberbox" 
											data-options="readonly:true," 
											style="width:80px;height:28px;text-align:right">户
									</font></p>
									<p><samp></samp><span>本周新增合同金额：</span><font>
										<input id="tswkcont" name="tswkcont" class="easyui-numberbox" 
											data-options="readonly:true,precision:2,groupSeparator:','" 
											style="width:80px;height:28px;text-align:right">元
									</font></p>
									<p><samp></samp><span>本周收到预付款：</span><font>
										<input id="tswkcharge" name="tswkcharge" class="easyui-numberbox" 
											data-options="readonly:true,precision:2,groupSeparator:','" 
											style="width:80px;height:28px;text-align:right">元
									</font></p>
									<p><samp></samp><span>本周扣款金额：</span><font>
										<input id="tswkamount" name="tswkamount" class="easyui-numberbox" 
											data-options="readonly:true,precision:2,groupSeparator:','" 
											style="width:80px;height:28px;text-align:right">元
									</font></p>
								</div>
							</form>
						</div>
						<div class="week_BB">
							<form id = "tsmonth">
								<div class="week" style="width: 40%;float: left;">
								<div class="week_right">
								<h1>本月业务发展情况</h1>
								<p><samp></samp><span>现有加盟商：</span>
								<font>
									<input id="franch" name="franch" class="easyui-numberbox" 
										data-options="readonly:true," 
										style="width:80px;height:28px;text-align:right">户
								</font></p>
								<p><samp></samp><span>本月新增加盟商：</span>
								<font>
									<input id="tsmhfranch" name="tsmhfranch" class="easyui-numberbox" 
										data-options="readonly:true," 
										style="width:80px;height:28px;text-align:right">户
								</font></p>
								<p><samp></samp><span>本年累计已收加盟费：</span>
								<font>
									<input id="tsyrinitfee" name="tsyrinitfee" class="easyui-numberbox" 
										data-options="readonly:true,precision:2,groupSeparator:','" 
										style="width:80px;height:28px;text-align:right">元
								</font></p>
								<p><samp></samp><span>本月收到加盟费：</span>
								<font>
									<input id="tsmhinitfee" name="tsmhinitfee" class="easyui-numberbox" 
										data-options="readonly:true,precision:2,groupSeparator:','" 
										style="width:80px;height:28px;text-align:right">元
								</font></p>
								<p><samp></samp><span>现有客户数：</span>
								<font>
									<input id="cust" name="cust" class="easyui-numberbox" 
										data-options="readonly:true," 
										style="width:80px;height:28px;text-align:right">户
								</font></p>
								<p><samp></samp><span>本月新增客户：</span>
								<font>
									<input id="tsmhcust" name="tsmhcust" class="easyui-numberbox" 
										data-options="readonly:true," 
										style="width:80px;height:28px;text-align:right">户
								</font></p>
							    </div>
								</div>
								<div class="week" style="width: 40%;float:right;">
									<div class="week_right">
										<h1>&nbsp;</h1>
										<p><samp></samp><span>本月新增合同金额：</span>
										<font>
											<input id="tsmhcont" name="tsmhcont" class="easyui-numberbox" 
												data-options="readonly:true,precision:2,groupSeparator:','" 
												style="width:80px;height:28px;text-align:right">元
										</font></p>
										<p><samp></samp><span>本年累计收到预付款：</span>
										<font>
											<input id="tsyrcharge" name="tsyrcharge" class="easyui-numberbox" 
												data-options="readonly:true,precision:2,groupSeparator:','" 
												style="width:80px;height:28px;text-align:right">元
										</font></p>
										<p><samp></samp><span>本年累计扣款金额：</span>
										<font>
											<input id="tsyramount" name="tsyramount" class="easyui-numberbox" 
												data-options="readonly:true,precision:2,groupSeparator:','" 
												style="width:80px;height:28px;text-align:right">元
										</font></p>
										<p><samp></samp><span>本月收到预付款：</span>
										<font>
											<input id="tsmhcharge" name="tsmhcharge" class="easyui-numberbox" 
												data-options="readonly:true,precision:2,groupSeparator:','" 
												style="width:80px;height:28px;text-align:right">元
										</font></p>
										<p><samp></samp><span>本月扣款金额：</span>
										<font>
										<input id="tsmhamount" name="tsmhamount" class="easyui-numberbox" 
											data-options="readonly:true,precision:2,groupSeparator:','" 
											style="width:80px;height:28px;text-align:right">元
									</font></p>
									</div>
								</div>
							</form>
						</div>
					</div>
				
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
	<!-- <p style="margin-top:5px;">
	是否补充密码信息
	<input id="isEditInfo" name="isEditInfo" type="checkbox"/>
	</p> -->
	<p style="margin-top:10px;">
	输入初始密码：
	<input id="user_password" name="data.user_password" type="password" class="easyui-textbox" style="width:210px;height:30px;border-color:#ffa8a8" data-options="required:true" />
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
	<!-- <div id="editInof" style="display:none">
		<p style="margin-top:10px;">
		请输入手机号：
		<input id="phone" name="phone" type="text" class="easyui-textbox" style="width:210px;height:30px;" data-options="required:true" />
		</p>
		<p style="margin-top:10px;">
		请输入邮箱&nbsp;&nbsp;&nbsp;：
		<input id="uEmail" name="uEmail" type="text" class="easyui-textbox" style="width:210px;height:30px;" data-options="required:true" />
		</p>
	</div> -->
</div>
</form>
<div id="pwd_buttons" style="display:none" >
<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="savePsw();" >确认</a> 
<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#upsw').dialog('close');" >取消</a>
</div>
<!-- 打开图片展示窗口 begin -->
<div id="fullViewDlg" style="display: none;">	
	<div  style="text-align: center;">
	<a class="ui-btn ui-btn-xz" style="margin:6px 0 6px 0;" onclick="tranImg(-90)">左转</a>
	<a class="ui-btn ui-btn-xz" style="margin:6px 0 6px 0;" onclick="tranImg(90)">右转</a>
	</div>
  	<div class="menu_entrance menu_entrances" style="margin-top:0;margin-right:5px;">
		<div class="entrance_block_tu" id="tpght" style="overflow;height:95%">
			<ul class="tu_block" id="fullViewContent"></ul>
		</div>
	</div>
</div>
<!-- 打开图片展示窗口 end -->

</body>
</html>
