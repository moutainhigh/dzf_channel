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

var bid;//图片下载专用id
var dtype;//图片下载专用类型

$(function() {
	initListener();
	initBusiData();
});

/**
 * 监听初始化
 */
function initListener() {
    $('#main').tabs({
        width: $("#main").parent().width(),
        height: (document.body.clientHeight - 58),
        tabHeight: 30,
        onSelect: function(title, index) {}
    });

    $('.nav_li').hover(function() {
        var backSpan = $(this).find("span");
        var preClass = backSpan.attr("class");
        backSpan.removeClass();
        $(this).find("span").addClass("h" + preClass);
        $(this).find("a").addClass("menu_1");
        $("#" + $(this).attr("id") + "_sub").show();
        document.getElementById($(this).attr("id") + "_sub").style.top = 
        	($(this).offset().top) + "px";
        if ($("#" + $(this).attr("id") + "_sub").offset().top + 
        		$("#" + $(this).attr("id") + "_sub").height() > $(window).height()) {
            document.getElementById($(this).attr("id") + "_sub").style.top = $(window).height() - 
            	$("#" + $(this).attr("id") + "_sub").height() + "px";
            $("#" + $(this).attr("id") + "_sub").find("em").css("top", $(this).offset().top - 
            		$("#" + $(this).attr("id") + "_sub").offset().top + 25 + "px");
        }
    },
    function() {
        var backSpan = $(this).find("span");
        var preClass = backSpan.attr("class");
        backSpan.removeClass();
        $(this).find("span").addClass(preClass.substr(1));
        $(this).find("a").removeClass("menu_1");
        var subSta = true;
        $("#" + $(this).attr("id") + "_sub").hover(function() {
            $(this).show();
            subSta = false;
        },
        function() {
            $(this).hide();
        });
        if (subSta) $("#" + $(this).attr("id") + "_sub").hide();
    });
    $(".sub_menu").hover(function() {
        var oh = $(this).height();
        $(this).find("div").height(oh);
        var index = $(this).index();
        $(".nav_li").eq(index).find("a").addClass("menu_1");
    },
    function() {
        var index = $(this).index();
        $(".nav_li").eq(index).find("a").removeClass("menu_1");
        $(this).hide();
    });

    $(".sub_menu_div li").hover(function() {
        $("#" + $(this).attr("id") + "_sub").show();
    },
    function() {
        var subSta = true;
        $("#" + $(this).attr("id") + "_sub").hover(function() {
            $(this).show();
            subSta = false;
        },
        function() {
            $(this).hide();
        });
        if (subSta) $("#" + $(this).attr("id") + "_sub").hide();

    });

    $("#main").tabs({
        onContextMenu: function(e, title) {
            e.preventDefault();
            if (title != '首页') {
                $('#tabsMenu').menu('show', {
                    left: e.pageX,
                    top: e.pageY
                }).data("tabTitle", title);
            }
        },
    });

    $("#tabsMenu").menu({
        onClick: function(item) {
            CloseTab(this, item.name);
        }
    });

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
		var content = '<iframe scrolling="auto" name="win-iframe" frameborder="0"  src="'+url+
			'" style="width:100%;height:99%;"></iframe>';
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
	var content = '<iframe scrolling="auto" name="win-iframe" frameborder="0" name="' + title + 
		'" ' + (iframeId ? " id=\"" + iframeId + "\" " : "") + ' src="'+url+
		'" style="width:100%;height:99%;"></iframe>';
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

function updatePsw(){
	$("#user_password").val("");
	$("#psw2").val("");
	$("#psw3").val("");
	$("#upsw").show();
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
			var strExp = /.*([0-9].*([a-zA-Z].*[~!@#$%^&*()<>?+=]|[~!@#$%^&*()<>?+=].*[a-zA-Z])|[a-zA-Z].*([0-9].*[~!@#$%^&*()<>?+=]|[~!@#$%^&*()<>?+=].*[0-9])|[~!@#$%^&*()<>?+=].*([0-9].*[a-zA-Z]|[a-zA-Z].*[0-9])).*/;
			if(strExp.test(value)){
				$("#"+$(this).attr("id")+"_ck").hide(); 
				delete ckmap[$(this).attr("id")+"_ck"];
			}else{
				Public.tips({content:"提示：密码必须包含数字、字母、特殊字符！",type:1});
				return;
			}
		}else{
			
		}
	});
}

function savePsw() {
    checkUserPwd();
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
    if (!$("#form").form('validate')) {
        return;
    }

    var publicKey = RSAUtils.getKeyPair(exponent, '', modulus);
    var dcpassword = RSAUtils.encryptedString(publicKey, $("#user_password").val());
    var psw2 = RSAUtils.encryptedString(publicKey, $("#psw2").val());
    var psw3 = RSAUtils.encryptedString(publicKey, $("#psw3").val());
    var url = DZF.contextPath + '/sys/sm_user!updatePsw.action';

    $.post(url, {
        'data.user_name': $('#user_name').val(),
        'data.user_password': dcpassword,
        'psw2': psw2,
        'psw3': psw3,
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
    },
    "json");
}

/**
 * 展示最大化图片
 * @param content 图片
 * @param title   表体
 * @param billid  下载主键
 * @param downtype   下载类型
 * @param index   图片下标
 */

function openFullViewDlg (content,title, billid, downtype, index, flowImgUrls) {
	var simg = '<div style="text-align: center;padding-top:10px;"> '+
		'<a href="javascript:;" class="ui-btn ui-btn-xz btn" data-control="last">上一页</a>'+
		'<a class="ui-btn ui-btn-xz" onclick="tranImg(-90)">左转</a> '+
		'<a class="ui-btn ui-btn-xz" onclick="tranImg(90)">右转</a>'+
	　	'<a href="javascript:;" class="ui-btn ui-btn-xz btn" data-control="next">下一页</a>'+
	 	'</div>'+
	 	'<div id="fullViewContent" '+
	 		'style="text-align: center;padding-top:20px; margin: 0 auto;position:relative;width:90%;overflow: auto;height:84%;">'+
	 		content +
	 	'</div>'
	showImage(simg, index, flowImgUrls, 0);
	initconturnid();
}

/**
 * layui展示图片
 * @param content  图片
 * @param index  图片下标
 * @param flowImgUrls 图片数组
 * @param opertype 操作展示类型
 */
function showImage(content, index, flowImgUrls, opertype){
	if(opertype == 0){//初始化展示
		layer.open({  
			type: 1,  
			shade: false,  
			title: '图片', //不显示标题  
			area:['auto','auto'],  
			area: ['90%','90%'],  
			content: content  , 
			maxmin: true,
			shadeClose: true,
			moveOut: false,
			cancel: function () {  
			}  
		});  
		$(".layui-layer-content").attr("id","fullViewDlg");
		
		findex = index;
		if(flowImgUrls != null && flowImgUrls.length > 0){
			flen = flowImgUrls.length;
		}
	　	$('.btn').on('click',function() {　　　　
	　	    if ($(this).data('control') === "last") {
	　	        findex = --findex;
	　	        if (findex < 0) {
	　	            findex = flowImgUrls.length;
	　	        }　　　　
	　	    } else {
	　	        findex = ++findex;
	　	        if (findex > flowImgUrls.length) {
	　	            findex = 0;
	　	        }　　　　
	　	    }
	　	    showImage(flowImgUrls[findex], findex, flowImgUrls, 1);
	　	});
		
	}else if(opertype == 1){//上一页、下一页展示
		$('#fullViewContent').html(content);
		initconturnid();
	}
}

/**
 * 关闭图片展示框
 */
function closeFullViewDlg(){
	 $('#fullViewDlg').dialog('close');
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
 * 判断dialog等是否打开
 * @param id
 * @returns
 */
function checkPanelVisible (id) {
	return $('#' + id).parent().is(".panel:visible")
}

/**
 * 更新日志 图片最大化
 * @param url
 */
function loaduplogpic(url){
	var left,top;
	if($(window).width() -1100 <= 0){
		left = 0;
	}else{
		left =  Math.floor(($(window).width() -1100)/2);
	}
	if($(window).height() -500 <=0){
		top = 0;
	}else{
		top =  Math.floor(($(window).height() -500)/2);
	}
	layui.use('layer', function(){
	 var layer = layui.layer;
	 layer.open({
		  type: 1,
		  title: false,
		  closeBtn: 1,
		  area: ['1100px','500px'],
		  offset: [top,left],
		  skin: 'layui-layer-nobg', //没有背景色
		  shadeClose: true,
		  scrollbar :false,
		  content: '<div><img src="'+url+'"></div>'
		});
	});
}

/**
 * 添加事件
 * @param obj
 * @param sType
 * @param fn
 */
function addEvent(obj, sType, fn) {
	if (obj.addEventListener) {
		obj.addEventListener(sType, fn, false);
	} else {
		obj.attachEvent('on' + sType, fn);
	}
};

/**
 * 移除事件
 * @param obj
 * @param sType
 * @param fn
 */
function removeEvent(obj, sType, fn) {
	if (obj.removeEventListener) {
		obj.removeEventListener(sType, fn, false);
	} else {
		obj.detachEvent('on' + sType, fn);
	}
};

/**
 * 上一事件
 * @param ev
 * @returns
 */
function prEvent(ev) {
	var oEvent = ev || window.event;
	if (oEvent.preventDefault) {
		oEvent.preventDefault();
	}
	return oEvent;
}

/**
 * 添加滑轮事件
 * @param obj
 * @param callback
 */
function addWheelEvent(obj, callback) {
	if (window.navigator.userAgent.toLowerCase().indexOf('firefox') != -1) {
		addEvent(obj, 'DOMMouseScroll', wheel);
	} else {
		addEvent(obj, 'mousewheel', wheel);
	}
	function wheel(ev) {
		var oEvent = prEvent(ev),
		delta = oEvent.detail ? oEvent.detail > 0 : oEvent.wheelDelta < 0;
		callback && callback.call(oEvent, delta);
		return false;
	}
};

/**
 * 最大化图片（放大、缩小、左转、右转）事件
 */
function initconturnid() {
	document.getElementById('conturnid').onload = function(){
		var widths = document.getElementById("fullViewDlg").offsetWidth - 142 ;
		 heights = document.getElementById("fullViewDlg").offsetHeight  -66 ;
		 widthpx = widths+"px";
		 heightpx = heights+"px";
		$("#fullViewContent").css({"width":widthpx,"height":heightpx})
		document.getElementById('conturnid').style.left = (widths - document.getElementById('conturnid').offsetWidth)/2 + "px"
		document.getElementById('conturnid').style.top = (heights - document.getElementById('conturnid').offsetHeight)/2 + "px"

	}
	

	var oImg = document.getElementById('conturnid');
	/*拖拽功能*/
	(function() {
		addEvent(oImg, 'mousedown', function(ev) {
			
			var oEvent = prEvent(ev),
			oParent = oImg.parentNode,
			disX = oEvent.clientX,
			disY = oEvent.clientY,
			marginX =oImg.offsetLeft,
			marginY =oImg.offsetTop,
			startMove = function(ev) {
				if (oParent.setCapture) {
					oParent.setCapture();
				}
				var oEvent = ev || window.event,
				l = oEvent.clientX - disX,
				t = oEvent.clientY - disY;
				oImg.style.left =marginX + l +'px';
				oImg.style.top = marginY+ t +'px';
				oParent.onselectstart = function() {
					return false;
				}
			}, endMove = function(ev) {
				if (oParent.releaseCapture) {
					oParent.releaseCapture();
				}
				oParent.onselectstart = null;
				removeEvent(oParent, 'mousemove', startMove);
				removeEvent(oParent, 'mouseup', endMove);
			};
			addEvent(oParent, 'mousemove', startMove);
			addEvent(oParent, 'mouseup', endMove);
			return false;
		});
	})();
	/*以鼠标位置为中心的滑轮放大功能*/
	
	(function() {
		addWheelEvent(oImg, function(delta) {
			
			var ratioL = this.offsetX / oImg.offsetWidth,
			ratioT = this.offsetY / oImg.offsetHeight,
			qusX = oImg.offsetLeft + this.offsetX,
			qusY = oImg.offsetTop + this.offsetY,
			ratioDelta = !delta ? 1 + 0.1 : 1 - 0.1,
			
			w = parseInt(oImg.offsetWidth * ratioDelta),
			h = parseInt(oImg.offsetHeight * ratioDelta),
	
			l = Math.round(qusX - (w * ratioL)),
			t = Math.round(qusY - (h * ratioT));
			if(w < 150 || h<80){
				return false
			};
			with(oImg.style) {
				width = w +'px';
				height = h +'px';
				left = l +'px';
				top = t +'px';
			}
		});
	})();
};

/**
 * 旋转图片
 */
var current = 0;
function tranImg(trun){
    var imgObj= document.getElementById('conturnid');
    current = (current+trun)%360;
    imgObj.style.transform = 'rotate('+current+'deg)';
}
