var sy = $.extend({}, sy);/* 全局对象 */

$.parser.auto = false;
$(function() {
	$.messager.progress({
		text : '页面加载中....',
		interval : 100
	});
	$.parser.parse(window.document);
	window.setTimeout(function() {
		$.messager.progress('close');
		if (self != parent) {
			window.setTimeout(function() {
				parent.$.messager.progress('close');
			}, 500);
		}
	}, 1);
	$.parser.auto = true;
});

$.fn.panel.defaults.onBeforeDestroy = function() {/* tab关闭时回收内存 */
	var frame = $('iframe', this);
	try {
		if (frame.length > 0) {
			frame[0].contentWindow.document.write('');
			frame[0].contentWindow.close();
			frame.remove();
			if ($.browser.msie) {
				CollectGarbage();
			}
		} else {
			$(this).find('.combo-f').each(function() {
				var panel = $(this).data().combo.panel;
				panel.panel('destroy');
			});
		}
	} catch (e) {
	}
};

$.fn.panel.defaults.loadingMessage = '数据加载中，请稍候....';
$.fn.datagrid.defaults.loadMsg = '数据加载中，请稍候....';

var easyuiErrorFunction = function(XMLHttpRequest) {
	/* $.messager.progress('close'); */
	/* alert(XMLHttpRequest.responseText.split('<script')[0]); */
	$.messager.alert('错误', XMLHttpRequest.responseText.split('<script')[0]);
};
$.fn.datagrid.defaults.onLoadError = easyuiErrorFunction;
$.fn.treegrid.defaults.onLoadError = easyuiErrorFunction;
$.fn.combogrid.defaults.onLoadError = easyuiErrorFunction;
$.fn.combobox.defaults.onLoadError = easyuiErrorFunction;
$.fn.form.defaults.onLoadError = easyuiErrorFunction;

var easyuiPanelOnMove = function(left, top) {/* 防止超出浏览器边界 */
	if (left < 0) {
		$(this).window('move', {
			left : 1
		});
	}
	if (top < 0) {
		$(this).window('move', {
			top : 1
		});
	}
};
$.fn.panel.defaults.onMove = easyuiPanelOnMove;
$.fn.window.defaults.onMove = easyuiPanelOnMove;
$.fn.dialog.defaults.onMove = easyuiPanelOnMove;

$.extend($.fn.validatebox.defaults.rules, {
	eqPassword : {/* 扩展验证两次密码 */
		validator : function(value, param) {
			return value == $(param[0]).val();
		},
		message : '密码不一致！'
	}
});

$.extend($.fn.datagrid.defaults.editors, {
	combocheckboxtree : {
		init : function(container, options) {
			var editor = $('<input/>').appendTo(container);
			options.multiple = true;
			editor.combotree(options);
			return editor;
		},
		destroy : function(target) {
			$(target).combotree('destroy');
		},
		getValue : function(target) {
			return $(target).combotree('getValues').join(',');
		},
		setValue : function(target, value) {
			$(target).combotree('setValues', getList(value));
		},
		resize : function(target, width) {
			$(target).combotree('resize', width);
		}
	}
});

/**
 * 获得项目根路径
 * 
 * 使用方法：bp();
 */
bp = function() {
	var curWwwPath = window.document.location.href;
	var pathName = window.document.location.pathname;
	var pos = curWwwPath.indexOf(pathName);
	var localhostPaht = curWwwPath.substring(0, pos);
	var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
	return (localhostPaht + projectName);
};

/**
 * 增加formatString功能
 * 
 * 使用方法：fs('字符串{0}字符串{1}字符串','第一个变量','第二个变量');
 */
fs = function(str) {
	for ( var i = 0; i < arguments.length - 1; i++) {
		str = str.replace("{" + i + "}", arguments[i + 1]);
	}
	return str;
};

/**
 * 增加命名空间功能
 * 
 * 使用方法：ns('jQuery.bbb.ccc','jQuery.eee.fff');
 */
ns = function() {
	var o = {}, d;
	for ( var i = 0; i < arguments.length; i++) {
		d = arguments[i].split(".");
		o = window[d[0]] = window[d[0]] || {};
		for ( var k = 0; k < d.slice(1).length; k++) {
			o = o[d[k + 1]] = o[d[k + 1]] || {};
		}
	}
	return o;
};

/**
 * 生成UUID
 */
random4 = function() {
	return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
};
UUID = function() {
	return (random4() + random4() + "-" + random4() + "-" + random4() + "-" + random4() + "-" + random4() + random4() + random4());
};

/**
 * 获得URL参数
 */
getUrlParam = function(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
};

getList = function(value) {
	if (value) {
		var values = [];
		var t = value.split(',');
		for ( var i = 0; i < t.length; i++) {
			values.push('' + t[i]);/* 避免他将ID当成数字 */
		}
		return values;
	} else {
		return [];
	}
};

png = function() {
	var imgArr = document.getElementsByTagName("IMG");
	for ( var i = 0; i < imgArr.length; i++) {
		if (imgArr[i].src.toLowerCase().lastIndexOf(".png") != -1) {
			imgArr[i].style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + imgArr[i].src + "', sizingMethod='auto')";
			imgArr[i].src = "images/blank.gif";
		}
		if (imgArr[i].currentStyle.backgroundImage.lastIndexOf(".png") != -1) {
			var img = imgArr[i].currentStyle.backgroundImage.substring(5, imgArr[i].currentStyle.backgroundImage.length - 2);
			imgArr[i].style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + img + "', sizingMethod='crop')";
			imgArr[i].style.backgroundImage = "url('images/blank.gif')";
		}
	}
};
bgPng = function(bgElements) {
	for ( var i = 0; i < bgElements.length; i++) {
		if (bgElements[i].currentStyle.backgroundImage.lastIndexOf(".png") != -1) {
			var img = bgElements[i].currentStyle.backgroundImage.substring(5, bgElements[i].currentStyle.backgroundImage.length - 2);
			bgElements[i].style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + img + "', sizingMethod='crop')";
			bgElements[i].style.backgroundImage = "url('images/blank.gif')";
		}
	}
};

isLessThanIe8 = function() {/* 判断浏览器是否是IE并且版本小于8 */
	return ($.browser.msie && $.browser.version < 8);
};

$.ajaxSetup({
	type : 'POST',
	error : function(XMLHttpRequest, textStatus, errorThrown) {/* 扩展AJAX出现错误的提示 */
		$.messager.progress('close');
		//$.messager.alert('错误', XMLHttpRequest.responseText.split('<script')[0]);
		Public.tips({
			type: 1,
			content: "操作失败了哦！"
		});
	}
});

$.extend($.fn.datagrid.defaults, {
	onDblClickRow: function(){
		if (dbcheck && typeof(dbcheck) == "function") {
			dbcheck();
		}
	}	
});
$.extend($.fn.treegrid.defaults, {
	onDblClickRow: function(){
		if (dbcheck && typeof(dbcheck) == "function") {
			dbcheck();
		}
	}	
});


(function() {
	var events = $.fn.combobox.defaults.events;
	var focusEvent = events.focus;
	var comboEvents = {};
	$.extend(comboEvents, events, {
		focus: function(e) {
			focusEvent.call(this, e);
			var thisInput = $(this);
			//防止form clear后load导致第一个combobox Panel没隐藏问题
			setTimeout(function(){
				if (thisInput.is(":visible") && thisInput.val() == "") {
					var combobox = $(e.target).parent().prev();
					combobox.combobox("options").readonly || combobox.combobox("showPanel");
				}
			}, 100);
		}
		
	});
	
	$.extend($.fn.combobox.defaults.inputEvents, {
		keydown: function(e) {
			//tab
			if (e.keyCode == 9) {
				var panel = $.data(e.data.target,"combo").panel;
				panel.panel("close");
			}
		}
		
	});
	$.extend($.fn.combobox.defaults, {
		events: comboEvents,
		keyHandler: {
			up: function(e) {
				nav(this, "prev");
				e.preventDefault();
			},
			down: function(e) {
				nav(this, "next");
				e.preventDefault();
			},
			left:function(e) {
				
			},
			right:function(e) {
				
			},
			enter:function(e) {
				var t = $(this);
				var opts = t.combobox("options");
				var panel = t.combobox("panel");
				if (panel.is(":visible")) {
					var item = panel.children("div.combobox-item-hover");
					if (item.length == 0) {
						item = panel.children("div.combobox-item:visible:not(.combobox-item-disabled):first");
					}
					if (item.length > 0) {
						var row = opts.finder.getRow(this, item);
						var value = row[opts.valueField];
						if (opts.multiple) {
							if (item.hasClass("combobox-item-selected")) {
								t.combobox("unselect", value);
							} else {
								t.combobox("select", value);
							}
						} else {
							t.combobox("select", value);
						}
					}
					
//					var vv = [];
//					$.map(t.combobox("getValues"), function(v) {
//					    if (_986(value, v) >= 0) {
//					        vv.push(v);
//					    }
//					});
//					t.combobox("setValues", vv);
					if (!opts.multiple) {
					    t.combobox("hidePanel");
					}
				} else {
					var inputIndex = $("body input:visible").index($(this).combobox("textbox"));
					var nextInput = $("body input:visible").eq(inputIndex + 1);
					if (nextInput.length) {
						nextInput.trigger("focus");
					}
				}
				
			},
			query: $.fn.combobox.defaults.keyHandler.query
		},
		onHidePanel: function() {
			var combo = $(this);
			var value = combo.combobox("getValue");
			var opts = combo.combobox("options");
			var row = findRowByValue(combo, opts.valueField, value);
			if (row == undefined) {
				combo.combobox("clear");
				combo.data("combo").previousText = "";
			}
		}
	});
	
	function findRowByValue(combo, valueField, value) {
		var data = combo.combobox("getData");
		for (var index = 0; index < data.length; index++) {
			if (data[index][valueField] == value) {
				return data[index];
			}
		}
		
	}
	
	function nav(combo, dir) {
		var opts = $.data(combo, "combobox").options;
		var panel = $(combo).combobox("panel");
		var item = panel.children("div.combobox-item-hover");
		if (!item.length) {
		    item = panel.children("div.combobox-item-selected");
		}
		item.removeClass("combobox-item-hover");
		var first = "div.combobox-item:visible:not(.combobox-item-disabled):first";
		var last = "div.combobox-item:visible:not(.combobox-item-disabled):last";
		if (!item.length) {
		    item = panel.children(dir == "next" ? first : last);
		} else {
		    if (dir == "next") {
		        item = item.nextAll(first);
		        if (!item.length) {
		            item = panel.children(first);
		        }
		    } else {
		        item = item.prevAll(first);
		        if (!item.length) {
		            item = panel.children(last);
		        }
		    }
		}
		if (item.length) {
		    item.addClass("combobox-item-hover");
		    scrollPanel(combo, item);
		}
	}
	
	function scrollPanel(combo, item){
		var opts = $.data(combo,"combobox").options;
		var panel = $(combo).combo("panel");
//		var item = opts.finder.getEl(combo, value);
		if(item.length){
			if (item.position().top <= 0) {
				var height = panel.scrollTop() + item.position().top;
				panel.scrollTop(height);
			} else {
				if (item.position().top + item.outerHeight() > panel.height()) {
					var height = panel.scrollTop() + item.position().top
						+ item.outerHeight() - panel.height();
					panel.scrollTop(height);
				}
			}
			
		}
	};
})();