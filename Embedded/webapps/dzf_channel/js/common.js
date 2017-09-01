var Public = Public || {};
var DZF = DZF || {};
var Business = Business || {};	//业务对象
Public.isIE6 = !window.XMLHttpRequest;
var cur_corp_Id = '109X';
DZF.contextPath = parent ? parent.indexContextPath : "/dzf_admin";
DZF.loginCorpId="000001";
DZF.loginCorpName="大账房";
//管理端标志
var sys_side = "admin_kj";

DZF.pageSize_min = 20;
DZF.pageList_min = [10, 20, 30, 40,50];

DZF.pageSize = 100;
DZF.pageList = [100, 200, 300, 400,500];
//皮肤设置
$(document).ready(function (){
	setSkin();
	if (parent != self) {
		$(document).on('click', function () {
			parent.$(parent.document).trigger("click");
	    });
	}
});
function setSkin(){
	var title = document.title;
	if(title == "大账房会计管理登陆页"){
		return;
	}
	if( parent.SYS_SKIN == undefined){
		return;
	}
	var skin = parent.SYS_SKIN.skincode;
	var hrefStr = DZF.contextPath + '/css/' + skin + '_skin.css';
	if($("#parent-skin").length > 0){
		$("#parent-skin").attr("href",hrefStr);
        $("body").addClass("dzf-skin");
	}else{
		if($("#child-skin").length > 0){
			$("#child-skin").attr("href",hrefStr);
		}else{
			$("head").append('<link id="child-skin" rel="stylesheet" href="' + hrefStr + '">');
			$("body").addClass('dzf-skin');
			 if(skin == "keji"){
		        $("body").addClass("child-iframe");
		     }
		}
	}
}
// 设置表格宽高
Public.setGrid = function(adjust,objId) {
	var adjust = adjust || 0;
	var objId = objId || 'dataGrid';
	var gridW = $(window).width() - 20, gridH = $(window).height()
			- $("#" + objId).offset().top - adjust;
	return {
		w : gridW,
		h : gridH
	}
};
//获取文件
Business.getFile = function(url, args, isNewWinOpen, isExport){
	if (typeof url != 'string') {
		return ;
	}
	var url = url.indexOf('?') == -1 ? url += '?' : url;
	url += '&random=' + new Date().getTime();
	var downloadForm = $('form#downloadForm');
	if (downloadForm.length == 0) {
		downloadForm = $('<form method="post" />').attr('id', 'downloadForm').hide().appendTo('body');
	} else {
		downloadForm.empty();
	}
	downloadForm.attr('action', url);
	for( k in args){
		$('<input type="hidden" />').attr({name: k, value: args[k]}).appendTo(downloadForm);
	}
	if (isNewWinOpen) {
		downloadForm.attr('target', '_blank');
	} else{
		var downloadIframe = $('iframe#downloadIframe');
		if (downloadIframe.length == 0) {
			downloadIframe = $('<iframe />').attr('id', 'downloadIframe').hide().appendTo('body');
		}
		downloadForm.attr('target', 'downloadIframe');
	}
//	promptDigfunc 判断是否有Pdf，提供下载链接
//	isExport ? downloadForm.trigger('submit') : promptDigfunc(downloadForm, true);
	downloadForm.trigger('submit');
};

// Ajax请求，
// url:请求地址， params：传递的参数{...}， callback：请求成功回调
// Ajax请求，
// url:请求地址， params：传递的参数{...}， callback：请求成功回调
Public.postAjax = function(url, params, callback, $dom) {
	if ($dom) {
		if ($dom.hasClass('ui-btn-dis')) {
			Public.tips({
				type : 2,
				content : '正在处理，请稍后...'
			});
			return;
		} else {
			$dom.addClass('ui-btn-dis');
			$.ajax({
				type : "POST",
				url : url,
				cache : false,
				async : true,
				dataType : "json",
				data : params,

				// 当异步请求成功时调用
				success : function(data, status) {
					callback(data);
					$dom.removeClass('ui-btn-dis');
				},

				// 当请求出现错误时调用
				error : function(err) {
					Public.tips({
						type : 1,
						content : '操作失败了哦！' + err
					});
					$dom.removeClass('ui-btn-dis');
				}
			});
		}
	} else {
		$.ajax({
			type : "POST",
			url : url,
			cache : false,
			async : true,
			dataType : "json",
			data : params,

			// 当异步请求成功时调用
			success : function(data, status) {
				callback(data);
			},

			// 当请求出现错误时调用
			error : function(err) {
				Public.tips({
					type : 1,
					content : '操作失败了哦！' + err
				});
			}
		});
	}

};
// Ajax请求，
// url:请求地址， params：传递的参数{...}， callback：请求成功回调
Public.getAjax = function(url, params, callback) {
	$.ajax({
		type : "GET",
		url : url,
		cache : false,
		async : true,
		dataType : "json",
		data : params,

		// 当异步请求成功时调用
		success : function(data, status) {
			callback(data);
		},

		// 当请求出现错误时调用
		error : function(err) {
			Public.tips({
				type : 1,
				content : '操作失败了哦！' + err
			});
		}
	});
};

// Public.corpAccount = {};
// Public.getCorpAccount = function() {
// $.jStorage.deleteKey("corpAccount");
// if(!Public.corpAccount || Public.corpAccount.length == 0){
// //Public.corpAccount = $.jStorage.get("corpAccount");
// if (!Public.corpAccount) {
// Public.corpAccount = Public.loadCorpAccountFromServer();
// //$.jStorage.set("corpAccount", Public.corpAccount,{TTL: 86400000});
// }
// }
// return Public.corpAccount;
// };

DZF.KMList = function() {
	var temp = getCorpAccount();
	return temp ? temp.rows : {};
}

var my_ca = [];
function getCorpAccount(id) {
	//if (!my_ca)
		//my_ca = $.jStorage.get("corpAccount");
	if (!my_ca || !my_ca[0]) {
		my_ca[0] = Public.loadCorpAccountFromServer();
		
		/*for(var i=0;i<6;i++){
			my_ca[i + 1] = Public.loadCorpAccountFromServer(i);
		}*/
		//SUBJECT_DATA? SUBJECT_DATA[0] = my_ca[0] : "";
			//JSON.stringify(Public.loadCorpAccountFromServer());
		//var temp = JSON.stringify(my_ca[0]);
		/*if (my_ca[0]) {
			for (var kmType = 0; kmType < 7; kmType++) {
				var data =JSON.parse(temp);
				var size = data.rows.length;
				for (var i = size - 1; i >= 0; i--) {
					var row = data.rows[i];
					if (row.kmlx != kmType) {
						data.rows.splice(i, 1);
					}
				}
				my_ca[kmType+1] = data;*/
				//SUBJECT_DATA? SUBJECT_DATA[kmType+1] = data : "";
				/*$.jStorage.set("corpAccount" + kmType, JSON.stringify(data), {
					TTL : 86400000
				});*/
			//}
		//}
		/*$.jStorage.set("corpAccount", my_ca, {
			TTL : 86400000
		});*/
	}
	if (!id) {
		return /*JSON.parse(my_ca);*/ my_ca[0];
	} else {
		return /*JSON.parse($.jStorage.get("corpAccount" + id));*/ my_ca[id];
	}
}

var my_curr;
function getBdCurrency(){
	//从缓存取一次
//	if(!my_curr){
//		my_curr = $.jStorage.get("currency");
//	}
	//如果没有再次从action取
	if(!my_curr){
		my_curr = Public.loadBdCurrency();
	}
	
//	$.jStorage.set("currency",my_curr,{TTl:0});//币种信息更改
	
	return my_curr;
}

DZF.xjllKm = {km1001:true,km1002:true,km1009:true,km1012:true};
Public.loadCorpAccountFromServer = function(accindex) {
	var tempVal = {},url=DZF.contextPath + '/gl/gl_cpacckmact!queryByPz.action';
	if(!isNaN(accindex)){
		url = url + "?accindex=" + accindex;
	}
	$.ajax({
		type : 'POST',
		url : url,
		dataType : "json",
		data : {
			corp_Id : cur_corp_Id,
			sort : 'kmbm'
		},
		async : false,
		success : function(result) {
			if (result.success) {
				tempVal = result;
			}
		}
	});
	return tempVal;
};


//jQuery Cookie plugin
$.cookie = function (key, value, options) {
    // set cookie...
    if (arguments.length > 1 && String(value) !== "[object Object]") {
        options = jQuery.extend({}, options);

        if (value === null || value === undefined) {
            options.expires = -1;
        }

        if (typeof options.expires === 'number') {
            var days = options.expires, t = options.expires = new Date();
            t.setDate(t.getDate() + days);
        }

        value = String(value);

        return (document.cookie = [
            encodeURIComponent(key), '=',
            options.raw ? value : encodeURIComponent(value),
            options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
            options.path ? '; path=' + options.path : '',
            options.domain ? '; domain=' + options.domain : '',
            options.secure ? '; secure' : ''
        ].join(''));
    }
    // get cookie...
    options = value || {};
    var result, decode = options.raw ? function (s) { return s; } : decodeURIComponent;
    return (result = new RegExp('(?:^|; )' + encodeURIComponent(key) + '=([^;]*)').exec(document.cookie)) ? decode(result[1]) : null;
};

Public.KMList=function(){
	var temp =  getCorpAccount();//JSON.parse(parent.input_pz_km);
	return temp.rows;
}

/*获取URL参数值*/
Public.getRequest = function() {
   var param, url = location.search, theRequest = {};
   if (url.indexOf("?") != -1) {
      var str = url.substr(1);
      strs = str.split("&");
      for(var i = 0, len = strs.length; i < len; i ++) {
		 param = strs[i].split("=");
         theRequest[param[0]]=decodeURIComponent(param[1]);
      }
   }
   return theRequest;
};
/*
 * 扩展时间对象
 * 
*/
Date.prototype.Format = function(fmt) 
{
    //author: meizz 
    var o =
    { 
        "M+" : this.getMonth() + 1, //月份 
        "d+" : this.getDate(), //日 
        "h+" : this.getHours(), //小时 
        "m+" : this.getMinutes(), //分 
        "s+" : this.getSeconds(), //秒 
        "q+" : Math.floor((this.getMonth() + 3) / 3), //季度 
        "S" : this.getMilliseconds() //毫秒 
    }; 
    if (/(y+)/.test(fmt)) 
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length)); 
    for (var k in o) 
        if (new RegExp("(" + k + ")").test(fmt)) 
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length))); 
    return fmt; 
};
Date.prototype.addMonths= function(m)
{
    var d = this.getDate();
    this.setMonth(this.getMonth() + m);
    if (this.getDate() < d)
        this.setDate(0);
};
Date.prototype.addDays = function(d)
{
    this.setDate(this.getDate() + d);
};

/** 
* 金额保留两位小数处理(总账)
* @param cellvalue 需要格式化的字符串
*/ 
function formatForDecimal(cellvalue) {
	if(typeof cellvalue == 'number') {
		return numZeroFmatter(cellvalue);
	}
	$value = $(cellvalue);
	var valueStr = "";
	$value.each(function() {
		var result = fmoney(parseFloat($(this).text()), 2);	//保留两位小数
		$(this).text((result == 0) ? " " : result); 
		valueStr += this.outerHTML;
	});
	valueStr = valueStr.replace(/[\r\n]/g, ""); 	//IE下去除换行符
	return valueStr == "" ? "&nbsp;" : valueStr;
};




Public.loadBdCurrency = function(){
	var tempVal={};
	$.ajax({
		type:'POST',
		url:DZF.contextPath +'/sys/sys_currentact!queryCurrency.action',
		dataTye:'json',
		data:'',
		async:false,
		success:function(result){
			if(result){
				tempVal = result;
			}
		}
	})
	return tempVal;
}

function formatMny(value) {
	//value = value.replace(/,/g, "");
	value = String(value).replace(/,/g, "");
	if (isNaN(value) || value == "")
		return "";
	value = Math.round(value * 100) / 100;
	if (value < 0)
		return '-' + outputdollars(Math.floor(Math.abs(value) - 0) + '')
				+ outputcents(Math.abs(value) - 0);
	else
		return outputdollars(Math.floor(value - 0) + '')
				+ outputcents(value - 0);
}
// 格式化金额
function outputdollars(number) {
	if (number.length <= 3)
		return (number == '' ? '0' : number);
	else {
		var mod = number.length % 3;
		var output = (mod == 0 ? '' : (number.substring(0, mod)));
		for (i = 0; i < Math.floor(number.length / 3); i++) {
			if ((mod == 0) && (i == 0))
				output += number.substring(mod + 3 * i, mod + 3 * i + 3);
			else
				output += ',' + number.substring(mod + 3 * i, mod + 3 * i + 3);
		}
		return (output);
	}
}
function outputcents(amount) {
	amount = Math.round(((amount) - Math.floor(amount)) * 100);
	return (amount < 10 ? '.0' + amount : '.' + amount);
}

serializeObject = function(form) {
	var o = {};
	$.each(form.serializeArray(), function(index) {
		if (this['value'] != undefined && this['value'].length > 0) {// 如果表单项的值非空，才进行序列化操作
			if (o[this['name']]) {
				o[this['name']] = o[this['name']] + "," + this['value'];
			} else {
				o[this['name']] = this['value'];
			}
		}
	});
	return o;
};

/*
 * / 将form中的值转换为键值对。
 * 形如：{name:'aaa',password:'tttt'}
 * ps:注意将同名的放在一个数组里
 * frm:form表单的ID名称
 */
function getFormJson(frm) {
	var o = {};
	var a = $("#" +frm).serializeArray();
	$.each(a, function () {
		if (o[this.name] !== undefined) {
			if (!o[this.name].push) {
				o[this.name] = [o[this.name]];
			}
			o[this.name].push(this.value || '');
		} else {
			o[this.name] = this.value || '';
		}
	});
	return o;
}

function UrlEncode(str) {
	var ret = "";
	var strSpecial = "!\"#$%&'()*+,/:;<=>?[]^`{|}~%";
	for (var i = 0; i < str.length; i++) {
		var chr = str.charAt(i);
		var c = str2asc(chr);
		if (parseInt("0x" + c) > 0x7f) {
			ret += "%" + c.slice(0, 2) + "%" + c.slice(-2);
		} else {
			if (chr == " ")
				ret += "+";
			else if (strSpecial.indexOf(chr) != -1)
				ret += "%" + c.toString(16);
			else
				ret += chr;
		}
	}
	return ret;
}
/*
 * 检查输入的的日期格式是否正确  例：2016-50-05 的格式
 */
function checkdate(dateid){
	var date = "";
	date = $("#"+dateid).datebox('textbox').val();
	var res = true;
	if(date==""){
		res = false;
	}
	else if(date != "" && !date.match(/^((?:19|20)\d\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/)) {
		res = false;
	}
	else if(date != "" && new Date(date) == 'Invalid Date'){
		res = false;
	}
	if(!res){
		Public.tips({
			content : '日期无效或格式不正确.',
			type : 2
		});
	}
	return res;
}
function checkdate1(begindate,enddate){
	var begin = new Date ($("#"+begindate).datebox('textbox').val());
	var end = new Date( $("#"+enddate).datebox('textbox').val());
	if(Date.parse(begin)>Date.parse(end)){
		Public.tips({
			content : '开始日期不能大于截至日期!',
			type : 2
		});
		return false;
	}
	return true;
}
function checkyear(yearid){
	var date = $("#"+yearid).combobox('getValue');
	if(date==""){
		Public.tips({
			content : '年份不能为空。',
			type : 2
		});
		return false
	}
	else if(date != "" && !date.match(/^((?:19|20)\d\d)$/)) {
		Public.tips({
			content : '年份不正确，请输入数字并按"XXXX"的格式输入。',
			type : 2
		});
		return false;
	}
	else {
		return true;
	}
}
function checkmonth(monthid){
	var date = $("#"+monthid).combobox('getValue');
	if(date==""){
		Public.tips({
			content : '月份不能为空。',
			type : 2
		});
		return false
	}
	else if(date != "" && !date.match(/^(0[1-9]|1[012])$/)) {
		Public.tips({
			content : '月份不正确，请输入数字并按"XX"的格式输入。',
			type : 2
		});
		return false;
	}
	else {
		return true;
	}
}
//UrlDecode函数：
function UrlDecode(str) {
	var ret = "";
	for (var i = 0; i < str.length; i++) {
		var chr = str.charAt(i);
		if (chr == "+") {
			ret += " ";
		} else if (chr == "%") {
			var asc = str.substring(i + 1, i + 3);
			if (parseInt("0x" + asc) > 0x7f) {
				ret += asc2str(parseInt("0x" + asc + str.substring(i + 4, i + 6)));
				i += 5;
			} else {
				ret += asc2str(parseInt("0x" + asc));
				i += 2;
			}
		} else {
			ret += chr;
		}
	}
	return ret;
}

function str2asc(strstr) {
	return ("0" + strstr.charCodeAt(0).toString(16)).slice(-2);
}

function asc2str(ascasc) {
	return String.fromCharCode(ascasc);
}

Public.tips = function(options){ 
	return new Public.tsinit(options); 
}
Public.tsinit = function(options){
	var defaults = {
		type : 0,
		autoClose : true,
		time : undefined,
		top : 25
	}
	this.options = $.extend({},defaults,options);
	this._init();
	!Public.tsinit.sctip ?  Public.tsinit.sctip = [this] : Public.tsinit.sctip.push(this);
	
}

Public.tsinit.removeAll = function(){
	try {
		for(var i=Public.tsinit.sctip.length-1; i>=0; i--){
			Public.tsinit.sctip[i].remove();
		}
	}catch(e){}
}

Public.tsinit.prototype = {
	_init : function(){
		var self = this,opts = this.options,time;
		Public.tsinit.removeAll();
		this.createStyle();

		this.closeBtn.bind('click',function(){
			self.remove();
		});

		if(opts.autoClose){
			time = opts.time || opts.type == 1 ? 5000 : 3000;
			window.setTimeout(function(){
				self.remove();
			},time);
		}
	},
	createStyle : function(){
		var opts = this.options;
		this.obj = $('<div class="tips"><i></i><span class="close"></span></div>').append(opts.content);
		this.closeBtn = this.obj.find('.close');
		
		switch(opts.type){
			case 0 : 		//绿色，成功
				this.obj.addClass('tips-success');
				break ;
			case 1 : 		//红色，错误提示
				this.obj.addClass('tips-error');
				break ;
			case 2 : 		//黄色，警告
				this.obj.addClass('tips-warning');
				break ;
			default :
				this.obj.addClass('tips-success');
				break ;
		}
		
		this.obj.appendTo('body').hide();
		this.setDw();
	},

	setDw : function(){
		var self = this, opts = this.options;
		if(opts.width){
			this.obj.css('width',opts.width);
		}
		var scrollTop = $(window).scrollTop();
		var top = parseInt(opts.top) + scrollTop;
		this.obj.css({
			position : Public.isIE6 ? 'absolute' : 'fixed',
			left : '50%',
			top : top,
			zIndex : '999999',
			marginLeft : -self.obj.outerWidth()/2	
		});

		window.setTimeout(function(){
			self.obj.show().css({
				marginLeft : -self.obj.outerWidth()/2
			});
		},150);

		if(Public.isIE6){
			$(window).bind('resize scroll',function(){
				var top = $(window).scrollTop() + parseInt(opts.top);
				self.obj.css('top',top);
			});
		}
	},

	remove : function(){
		var opts = this.options;
		this.obj.fadeOut(200,function(){
			$(this).remove();
		});
	}
};
//子节点获取登录时间
Public.getLoginDate = function(){
	return $("body",parent.document).find("#showDate").text();
};
Public.getYestoday = function(date){
//	var start = new Date(date),
//    end = new Date(start.getTime() + 3600 * 24 * 30 * 1000),
//    m =(end.getMonth() - 1),
//    d=end.getDate();
//	var datastr = end.getFullYear() + '-' + (m < 10 ? '0' + m : m) + '-' + (d < 10 ? '0' + d : d);
//	return datastr;
	var Nowdate = new Date(date);
	var vYear = Nowdate.getFullYear();
	var vMon = Nowdate.getMonth() + 1;
	var vDay = Nowdate.getDate();
	//每个月的最后一天日期（为了使用月份便于查找，数组第一位设为0）
	var daysInMonth = new Array(0,31,28,31,30,31,30,31,31,30,31,30,31);
	if(vMon==1){
        vYear = Nowdate.getFullYear()-1;
        vMon = 12;
	}else{
        vMon = vMon -1;
	}
	 　　//若是闰年，二月最后一天是29号
    if(vYear%4 == 0 && vYear%100 != 0){
        daysInMonth[2]= 29;
    }
	if(daysInMonth[vMon] < vDay){
	    vDay = daysInMonth[vMon];
	}
	if(vDay<10){
	    vDay="0"+vDay;
	}
	if(vMon<10){
	    vMon="0"+vMon;
	}
	var date =vYear+"-"+ vMon +"-"+vDay;
	return date;
  }

/*
 * subCode:当前编码，curLev：获取代码级别
 * 返回值[上级编码,本级编码] 
 */
function getNumberArray(subCode, curLev) {
    var d, e = "",f = "";
    return 1 == curLev ? f = subCode: curLev >= 2 && (d = ruleLenArray[1 * curLev - 2], f = subCode.slice(d), e = subCode.slice(0, d)), [e, f];
}

/*
 * a:编码规则，如"4/2/2/2"
 * 返回值[4，6，8，10]
 */
function getRuleLenArr(a) {
    for (var b = a.split("/"), c = [], d = 0, e = 0; e < b.length; e++) d += parseInt(b[e], 10),
    c.push(d);
    return c;
}
/*
 * subCode:需要验证的编码
 * 返回值：如果编码符合规则，返回编码级别（级别从1开始），如果不符合返回false
 */
function checkRule(subCode){
	for (codeLen = subCode.length, i = 0; i < ruleLenArray.length; i++){
		if (level = i + 1, codeLen == ruleLenArray[i]) {
        	return (i + 1);
   		}
	}
	return !1;
}

/**
 * 根据标签名调用其他标签页面的方法
 * @param title 标签名
 * @param funName 方法名
 * @param args 任意多个参数
 */
function invokeFunByTabTitle (title, funName) {
	var tab = parent.$('#main');
	if (tab.tabs('exists', title)) {
		var content = tab.tabs('getTab',title).find('iframe').get(0).contentWindow;//iframe对象
		var fun = content[funName];
		if (typeof fun === 'function') {
			var args = new Array();
			for (var i = 2; i < arguments.length; i++)
		        args.push(arguments[i]);
			return fun.apply(this, args);
		}
	}
}

/**
 * 验证年月日时间格式 XXXX-XX-XX
 * @param dateid
 * @returns {Boolean}
 */
function checkDateCommon(dateid){
	var date = $("#"+dateid).datebox('textbox').val();
	if(!date || date  == ""){
		Public.tips({type: 2,content: '日期不能为空。'});
		return false;
	}
	if(date != "" && !date.match(/^((?:19|20)\d\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/)) {
		Public.tips({type: 2,content: '日期格式不正确，请输入数字并按"XXXX-XX-XX"的格式输入。'});
		return false;
	}
	else if(date != "" && new Date(date) == 'Invalid Date'){
		Public.tips({type: 2,content: '无效的日期。'});
		return false;
	}else {
		return true;
	}
}
/**
 * 验证月份时间格式 01-12
 * @param dateid
 * @returns {Boolean}
 */
function checkMonthCommon(dateid){
	var date = $("#"+dateid).datebox('textbox').val();
	if(!date || date  == ""){
		Public.tips({type: 2,content: '月份不能为空。'});
		return false;
	}else if(date != "" && !date.match(/^(0[1-9]|1[012])$/)) {
		Public.tips({type: 2,content: '月份不正确。'});
		return false;
	}else{
		return true;
	}
}
/**
 * 验证年份时间格式 1900-2099
 * @param dateid
 * @returns {Boolean}
 */
function checkYearCommon(dateid){
	var date = $("#"+dateid).datebox('textbox').val();
	if(!date || date  == ""){
		Public.tips({type: 2,content: '年份不能为空。'});
		return false;
	}else if(date != "" && !date.match(/^((?:19|20)\d\d)$/)) {
		Public.tips({type: 2,content: '年份不正确。'});
		return false;
	}else {
		return true;
	}
}



/**
 * 自动调整表格列的宽度
 * @param t
 */
function reDatagridWidth (t) {
	var datagridview =  $(t).parent();
	var resizeWidth = function () {
		datagridview.find('.datagrid-view2 table').width(datagridview.find('.datagrid-view2').width());
	}
	
//	if(datagridview.find('#reW').length == 0){
//		var tableStyle = '<style id="reW" type="text/css">.datagrid-view2 table{width:' + datagridview.find('.datagrid-view2').width() + 'px;}</style>';
//		datagridview.append(tableStyle);
//	}
//	
//	$.each(datagridview.find('.datagrid-view2 .datagrid-body table tbody tr'), function (idx, itme) {
//		
//	});
	
	setTimeout(function () {
		resizeWidth();
	},50);
	$(window).resize(function () {
	resizeWidth();
	});
}

function parseMny(obj){
	if(obj == null || obj == "")
		return 0.00;
	return parseFloat(obj);
}

(function($){  
    $.fn.serializeJson=function(){  
        var serializeObj={};  
        $(this.serializeArray()).each(function(){  
            serializeObj[this.name]=this.value;  
        });  
        return serializeObj;  
    };  
})(jQuery);  

function linkKhbm (value, row) {
	var url = DZF.contextPath +　'/sys/sys_set/sys_corp.jsp?opt=edit&khcode=' + value;
	return '<a href="javascript:void(0)" onclick="parent.addTab(\'我的客户\',\'' + url + '\')" style="color:blue;">' + value + '</a>';
}

/**
 * 绑定查询区的事件
 * @param searchAreaId
 * @param showSrc
 * @param hideSrc
 */
function addQryEvent(searchAreaId,showSrc,hideSrc){
	$(showSrc).mouseover(function(){ 
		$(searchAreaId).show();
		$(searchAreaId).css("visibility","visible");
	});
	$(hideSrc).on('click',function(){
		$(searchAreaId).hide();
		$(searchAreaId).css("visibility","hidden");
	});
}
