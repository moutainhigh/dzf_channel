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