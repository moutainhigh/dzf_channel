// 当浏览器加载时 自动识别当前的屏幕大小 设置main的不同大小
window.onload = function(){
	$('#main').tabs('resize', {
	   width: $("#main").parent().width(),
	   height: (document.documentElement.clientHeight - 58)
 	});
	
}; 

$(window).resize(function() {
	// 当浏览器大小变化时
	$('#main').tabs('resize', {
	 width: $("#main").parent().width(),
	   height: (document.documentElement.clientHeight - 58)
	});
});  

$(function() {
	initListener();
	initBusiData();
	
//	downFile(billid, type);
//	showTips();
//	hideTips();
});

/**
 * 监听初始化
 */
function initListener(){
	 $('#main').tabs({
		 width: $("#main").parent().width(),
		 height:(document.body.clientHeight-58),
		 tabHeight:30,
		 onSelect:function(title,index){
		 }
	});
	
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
	
//	$("dd .tz_row1").hover(function(){
//		 var tzcon = $(this).find("a").attr("data-title");
//		 var str = '<div class="tz-content">' + tzcon + "</div>"
//		 $(this).append(str);
//	}, function(){
//		 $(this).find("div").remove();
//	})
  
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
	
	
	
	//判断是否要修改手机及邮箱信息
//	$("#isEditInfo").change(function(){
//		if($("#isEditInfo").prop("checked")){
//			$("#upsw").dialog({
//				height:350
//			});	
//			$("#editInof").show();
//		}else{
//			$("#upsw").dialog({
//				height:270
//			});		
//			$("#editInof").hide();
//		}
//	});
}

function showOpt(id, show){
	if(!$('#' + id))
		return;
	if (show)
		$('#' + id).show();
	else
		$('#' + id).hide();
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

/**
 * 周、月业务数据初始化
 */
function initBusiData(){
	
	$.ajax({
		type: "post",
		dataType: "json",
		url: DZF.contextPath + '/report/indexrep!queryBusiByWeek.action',
		data : {
			"cpid" : $("#login_corp_id").val(),
		},
		success: function(data, textStatus) {
			if (!data.success) {
				Public.tips({content:data.msg,type:1});
			} else {
				var row = data.rows;
				$('#tsweek').form('clear');
				$('#tsweek').form('load', row);
			}
		},
	});
	
	$.ajax({
		type: "post",
		dataType: "json",
		url: DZF.contextPath + '/report/indexrep!queryBusiByMonth.action',
		data : {
			"cpid" : $("#login_corp_id").val(),
		},
		success: function(data, textStatus) {
			if (!data.success) {
				Public.tips({content:data.msg,type:1});
			} else {
				var row = data.rows;
				$('#tsmonth').form('clear');
				$('#tsmonth').form('load', row);
			}
		},
	});
}

/**
 * 展示最大化图片
 * @param content
 * @param title
 */
function openFullViewDlg (content,title) {
	$("#fullViewContent").html(content);
	$("#fullViewDlg").dialog({
		width:$(window).width()-100,
		height:$(window).height()-50,
		closable:true,
		title: title,
		modal:true,
	});	
	$("#fullViewDlg").css("display","block");
	$("#fullViewDlg").dialog("center");
}

/**
 * 关闭图片展示框
 */
function closeFullViewDlg(){
	 $('#fullViewDlg').dialog('close');
}

function updatePsw(){
//	$("#isEditInfo").removeAttr("checked");
	$("#user_password").val("");
	$("#psw2").val("");
	$("#psw3").val("");
	$("#upsw").show();
//	$("#editInof").hide();
	
	$("#upsw").dialog({
		modal:true,
		title: '用户信息维护',
		width:380,
		height:270,
		buttons : '#pwd_buttons'
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
				//$("#"+$(this).attr("id")+"_ck").html("<font size=2 color='red'>*密码必须包含数字、字母、特殊字符</font>").show();
				//ckmap[$(this).attr("id")+"_ck"]="密码必须包含数字、字母、特殊字符";
				return;
			}
		}else{
			//$("#"+$(this).attr("id")+"_ck").html("<font size=2 color='red'>*请输入密码</font>").show();
		}
	});
}
function savePsw(){
	checkUserPwd();
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
//	if($("#isEditInfo").prop("checked")){
//		var value = $("#phone").val();
//		//手机号码正则表达式
//		var telReg = !!value.match(/^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/);
//		if(telReg ==  false){
//			Public.tips({content: "请输入正确的手机号码！",type:2});
//			return ;
//		}
//		//邮箱正则表达式
//		var mailValue = $("#uEmail").val();
//		var mailReg = !!mailValue.match(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/);
//		if(mailReg == false){
//			Public.tips({content: "请输入正确的邮箱！",type:2});
//			return ;
//		}
//	}
	
	var publicKey = RSAUtils.getKeyPair(exponent, '', modulus);
	var dcpassword=RSAUtils.encryptedString(publicKey, $("#user_password").val());
    var psw2=RSAUtils.encryptedString(publicKey, $("#psw2").val());
    var psw3=RSAUtils.encryptedString(publicKey, $("#psw3").val());
    var url = DZF.contextPath + '/sys/sm_user!updatePsw.action';
	
	$.post(url, 
			{
				'data.user_name':$('#user_name').val(),
				'data.user_password':dcpassword,
				'psw2' : psw2,
				'psw3' : psw3,
//				'phonenum' : $("#phone").val(),
//				'mail' : $("#uEmail").val()
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

/**
 * 显示提示
 * @param i
 */
function showTips(){
	$("#reUpload").css("display","block");
	$("#reUpload").html("双击下载");	
}

/**
 * 隐藏提示
 * @param i
 */
function hideTips(){
	$("#reUpload").css("display","none");	
}

/**
 * 附件下载
 * @param billid
 * @param type 1:付款单确认；2：客户名称修改确认；
 */
function downFile(billid, type){
	if(billid && billid != ""){
		if(type == 1){
			Business.getFile(DZF.contextPath + '/chnpay/chnpayconf!downFile.action', {billid : billid}, true, true);
		}else if(type == 2){
			Business.getFile(DZF.contextPath + '/corp/corpeditconf!downFile.action', {id : billid}, true, true);
		}
	}
}

/**
 * 左转
 */
function tranLeft(){
	dealRotate($("#fullViewDlg > div > img ").get(0), -1);
}

/**
 * 右转
 */
function tranRight(){
	dealRotate($("#fullViewDlg > div > img ").get(0), 1);
}

function dealRotate(target, direction) {
	var angle = $(target).data("angle") || 0;
	angle = Number(angle);
	angle = (360 + angle + direction * 90) % 360;
	transformImage(target, undefined, angle);
}

/**
 * 图片旋转及缩放
 * @param img
 * @param zoom
 * @param angle
 * @returns {Boolean}
 */
function transformImage(img, zoom, angle) {
    if (!img)
    	return false;
    var canvas = document.getElementById('img_canvas');
    if (canvas == null) {
        canvas = document.createElement('canvas');
        canvas.setAttribute("id", 'img_canvas');
        $(img).after(canvas);
    }
    $(img).hide();
    $(canvas).show();
    if (zoom == undefined) {
    	zoom = $(img).data("zoom");
    	if (zoom == undefined) {
    		zoom = 1
		}
	}
    if (angle == undefined) {
    	angle = $(img).data("angle");
    	if (angle == undefined) {
    		angle = 0;
		}
	}
    var canvasContext = canvas.getContext('2d');
    var positionX = 0;
    var positionY = 0;
    var zoomWidth = zoom * img.naturalWidth;
    var zoomHeight = zoom * img.naturalHeight;
    var cwidth = 0;
    var cheight = 0;
    if (zoom < 1 && (zoomWidth < 25 || zoomHeight < 25)) {
    	// 太小，鼠标放不上去，就不能还原了
		return;
	}
    switch (angle) {
        case 0:
        	cwidth = zoomWidth;
        	cheight = zoomHeight;
            break;
        case 90:
        	cwidth = zoomHeight;
        	cheight = zoomWidth;
        	positionY = -zoomHeight;
            break;
        case 180:
        	cwidth = zoomWidth;
        	cheight = zoomHeight;
        	positionX = -zoomWidth;
        	positionY = -zoomHeight;
            break;
        case 270:
        	cwidth = zoomHeight;
        	cheight = zoomWidth;
        	positionX = -zoomWidth;
            break;
        default:
    }
    canvas.setAttribute('width', cwidth);
    canvas.setAttribute('height', cheight);
    canvasContext.rotate(angle * Math.PI / 180);
    canvasContext.drawImage(img, positionX, positionY, zoomWidth, zoomHeight);
    $(img).data({
    	zoom: zoom,
    	angle : angle
    });
    var dlgWidth = $("#tpfd").width() - 25;
    if (cwidth > dlgWidth) {
       $("#img_container span").hide();
	} else {
		$("#img_container span").show();
	}
}

/**
 * 判断dialog等是否打开
 * @param id
 * @returns
 */
function checkPanelVisible (id) {
	return $('#' + id).parent().is(".panel:visible")
}

$(document).on("wheel",function(e) {
	if (checkPanelVisible("fullViewDlg")) {
		if (e.target == $("#fullViewDlg > div > img").get(0)
				|| e.target == $("#img_canvas").get(0)) {
			var orgEvent = e.originalEvent;
			var zoom = $("#fullViewDlg > div > img").data("zoom");
			zoom = zoom ? zoom : 1;
			zoom = zoom - (orgEvent.deltaY > 0 ? 0.05 : -0.05);
			transformImage($("#fullViewDlg > div > img")[0], zoom);
		}
	}
});
