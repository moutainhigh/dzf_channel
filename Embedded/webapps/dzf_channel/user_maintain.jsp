<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%
	String modulus =(String)session.getAttribute("MODULUS");
	String exponent =(String)session.getAttribute("EXPONENT");
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<title>用户信息</title>
<jsp:include page="inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out,"css/index.css");%>  rel="stylesheet">
<!-- <link href="layui/css/layui.css" rel="stylesheet"> -->
<!-- 图片裁切  -->
<link rel="stylesheet" type="text/css" href="css/imgareaselect-default.css" />
<script src=<%UpdateGradeVersion.outversion(out,"js/security.js");%> charset="UTF-8" type="text/javascript"></script>
<script type="text/javascript" src="js/jquery.imgareaselect.pack.js"></script>
<!-- 图片裁切  -->
<script type="text/javascript" src=<%UpdateGradeVersion.outversion(out,"js/user_maintain.js");%> ></script>
<link rel="stylesheet" type="text/css" href="css/userinfo.css"/>
<script type="text/javascript">
var modulus = '<%=modulus%>',
exponent = '<%=exponent%>';
</script>
</head>
<body>
	<div id="page-wrap">
		<div id="slot-machine-tabs">
			<ul class="tabs machine">
				<li><a href="#" onclick = "choose(1)">我的资料</a></li>
				<li><a href="#" onclick = "choose(2)">头像</a></li>
				<li><a href="#" onclick = "choose(3)">密码</a></li>
			</ul>
			<div class="box-wrapper">
				<div id="div1" class="content-box">
					<!-- <div class="col-one col"> -->
					<form id="base_fm" method="post">
						<p>
							<label>手机号：</label>
							<input id="phonenum" name="phonenum" type="text" class="easyui-textbox" style="height:30px" />
						</p>
						<p>
							<label>邮箱：</label>
							<input id="mail" name="mail" type="text" class="easyui-textbox" style="height:30px" />
						</p>
						<!-- <p>
							<label>昵称：</label>
							<input id="unick" name="unick" type="text" class="easyui-textbox" style="height:30px" />
						</p> -->
						<!-- <p><label>性别</label></p>
						<span><input type="radio" name="sex" value="0" />男</span>
						<span><input type="radio" name="sex" value="1"/>女</span> -->
						<!-- <p><label>城市</label></p>
						<input id="ucity" name="ucity" type="text" class="easyui-textbox" style="width:210px;height:30px;" data-options="required:true" /> -->
						<p>
							<label>签名：</label>
							<textarea placeholder="随便写些什么刷下存在感" id="usign" name="usign" autocomplete="off" class="layui-textarea" style="height: 80px;"></textarea>
						</p>
						
					</form>
					<p class="btn_change">
							<button onclick="updateBaseInfo()">提交修改</button>
					</p>
					<!-- </div> -->
					</div>
					<div id="div2" class="content-box" style="display: none;text-align:center;padding-top:50px;">
						<!-- <div class="col-one col">
							 <div class="layui-form-item">
								<div class="avatar-add">
									<p>建议尺寸168*168，支持jpg、png、gif，最大不能超过30KB</p>
									<div class="upload-img">
										<input type="file" name="file" id="LAY-file" lay-title="上传头像">
									</div>
									<img src=""><span class="loading"></span>
								</div>
							</div> 
						</div> -->
						<img id="upimg" name="upimg" onclick="$('#picFile').click()" src="images/website/logo/account.png" style="width:200px;height:200px; border-radius: 200px;">
				        <div class="site-demo-upbar">
				        	<div class="layui-box layui-upload-button">
						        <form id="pic_fm" method="post" enctype="multipart/form-data">
							       <!--  <div id="upbutton" class="change-avatar-text" onclick="$('#picFile').click()">修改LOGO</div> -->
							        <input id="picFile" name="picFile" type="file" hidden accept="image/jpg,image/jpeg,image/png" onchange="uploadPic()">
							        <input type="hidden" name="id_user_e" id="id_user_e" />
						        </form>
						        <span class="layui-upload-icon" onclick="$('#picFile').click()">
						        	<i class="layui-icon"></i>上传图片
						        </span>
				        	</div>
				        </div>
					</div>
					<div id="div3" class="content-box" style="display: none;">
						<!-- <div class="col-one col"> -->
						<form id="pwd_fm" method="post">
		 					<p style="margin-top: 10px;"><label>输入原始密码：</label>
							<input id="user_password" name="user_password"	type="password" class="easyui-textbox" 	style="width: 210px; height: 30px;" data-options="required:true" />
							</p>
							<p style="margin-top: 10px;"><label>请输入新密码：</label>
							<input id="psw2" name="psw2" type="password" class="easyui-textbox" style="width: 210px; height: 30px;"	data-options="required:true" />
							</p>
							<p style="margin-top: 10px;"><label>再次输入密码：</label>
							<input id="psw3" name="psw3" type="password" class="easyui-textbox" style="width: 210px; height: 30px;"	data-options="required:true" />
							</p>
						</form>
						<!-- </div> -->
						<p class="btn_change">
							<button onclick="updatePwdInfo()">提交修改</button>
						</p>
					</div>
				</div>
			</div>
		</div>
		
			<!-- 照片裁切对话框 begin -->
	<div id="cq_dialog" class="easyui-dialog" title="照片裁切" data-options="modal:true,closed:true" style="width:700px;height:560px;">
		<div class="card_table" style="min-width:500px;margin:0 auto;margin-top:20px;width:75%;">
			<div data-options="region:'north'" class="bcon" style="padding:10px;width:500px;height:400px;margin:0 auto;vertical-align:middle;display:table-cell; text-align:center;  " id ="test">
				<form id="cq_form" method="post" enctype="multipart/form-data">
					<img src="images/white.jpg" id="imgCrop" name="imgCrop" style="margin-top:0px;"/>     
			    </form>
			 </div>
			 <input type="hidden" name="x1" id="x1" value="100" />
			 <input type="hidden" name="y1" id="y1" value="100" />
			 <input type="hidden" name="x2" id="x2" value="200" />
			 <input type="hidden" name="y2" id="y2" value="200" />
			 <input type="hidden" name="imgurl" id="imgurl" />
		</div>
		<div data-options="region:'center',title:'center title'" style="margin-top:20px;text-align: center;">
			<a id="cutsaveBtn" href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="cutsave()">确定</a>
			<a id="cutcancBtn" 	href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="cutcancel()">取消</a>
		</div>
	</div>
	<!-- 照片裁切对话框 end -->
</body>
</html>