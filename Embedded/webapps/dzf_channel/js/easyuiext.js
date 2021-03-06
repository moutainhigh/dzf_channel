$(function(){
	initQryDlg();
});

/**
 * 隐藏显示查询框
 */
function initQryDlg(){
	$("#cxjs").on("mouseover",function(){ 
    	$("#qrydialog").show();
    	$("#qrydialog").css("visibility","visible");
    });
	
	$(".mod-inner,.mod-toolbar-top,.grid-wrap").on("click",function(){
		$("#qrydialog").hide();
    	$("#qrydialog").css("visibility","hidden");
	});
}

/** 日历支持手工输入 begin */
$.extend($.fn.validatebox.defaults.rules, {
	checkdate : {
		validator : function(value) {
			return isDateType(value);
		},
		message : '日期无效或格式不正确.'
	},
	phone : {
		validator : function(value) {
			return value.match("^(13[0-9]|14[0-9]|15[0-9]|17[0-9]|18[0-9]|19[0-9]|16[0-9])[0-9]{8}$");
		},
		message : '不是有效的手机号.'
	}
});

$.fn.datebox.defaults = $.extend({}, $.fn.datebox.defaults, {
	validType : 'date',
	parser : function(s) {
		var t = Date.parse(s);
		if (!isNaN(t)) {
			return new Date(t);
		} else {
			return new Date();
		}
	}
});

/**
 * 校验是否如期格式
 * @param value
 * @returns {Boolean}
 */
function isDateType(value) {
	if (value != "" && !value.match(/^((?:19|20)\d\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/)) {
		return false;
	} else if(value != "" && new Date(value) == 'Invalid Date'){
		return false;
	}else {
		return true;
	}
}
/** 日历支持手工输入 end */

/*
 * 检查输入的的日期格式是否正确  例：2016-50-05 的格式
 * 非必输项不做必输校验
 */
function datecheck(dateid){
	var date = "";
	date = $("#"+dateid).textbox('getValue');
	if(date != "" && !date.match(/^((?:19|20)\d\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/)) {
		$.messager.alert('提示','日期格式不正确，请输入数字并按"XXXX-XX-XX"的格式输入。');
		return false;
	}
	else if(date != "" && new Date(date) == 'Invalid Date'){
		$.messager.alert('提示','无效的日期。');
		return false;
	}else {
		return true;
	}
}

/**
 * 初始化会计公司
 */
function initQryAccount(name_id,corp_id){
	var login_corpid = parent.SYSTEM.login_corp_id;//登录公司
	var login_corpname=parent.SYSTEM.login_corp_name; //会计公司名称
	$('#'+name_id).textbox('setValue',login_corpname);
	$('#'+corp_id).val(login_corpid);
}

/**
 * 获取输入字符的长度
 * @param val
 * @returns {Number}
 */
function getByteLeng(str) {
	return str.replace(/[^\x00-\xff]/g,"**").length;
}

/**
 * 禁用表单
 * @param from
 * @param exclude
 * @param isReadOnly
 */
function showFormAs(from,exclude,isReadOnly){
	var eles = $(from).find('[class^="easyui-"]').not(exclude);
	for(var e=0;e<eles.length;e++){
		var ele = $(eles[e]);
		var type = ele.attr("class")+'';
		if(type==undefined || type=='' || type=='undefined')
			continue;
		var ea = type.substring(type.indexOf('-')+1,type.indexOf(' '));
		 if(ea == 'linkbutton'){
			eval('ele.'+ ea + '({"disabled":'+isReadOnly+'})');
		}else{
			eval('ele.'+ ea + '({"readonly":'+isReadOnly+'})');
		}
	}
}

/**
 * 初始化会计人员
 */
function initCoperRef(){
	$('#coper,#qrycoper').textbox({
		editable:false,
		onClickIcon:function(){
			voperid = $(this).attr("id");
		}
	}).textbox({
		editable:false,
		icons : [ {
			iconCls : 'icon-search',
			handler : function(e) {
				$("#kj_dialog").dialog({
					width : 500,
					height : 500,
					readonly : true,
					title : '选择人员',
					modal : true,
					href :DZF.contextPath + '/ref/kj_select.jsp',
					buttons : [ {
						text : '确认',
						handler : function() {
							selectkj($(e.data.target).attr("id"));
						}
					}, {
						text : '取消',
						handler : function() {
							$('#kj_dialog').dialog('close');
						}
					} ]
				});
			}
		} ]
	});
}

var voperid;
/**
 * 会计人员选择事件
 * @param id
 */
function selectkj(id){
	id = id || voperid;
	var row = $('#kjTable').datagrid('getSelected');
	if (row) {
		$("#" + id).textbox('setValue', row.uname);
		if(id == "d7"){
			$("#d6").val(row.uid);
		}else{
			$("#" + id + "id").val(row.uid);
			if("taxer" == id){
				$("#taxercode").textbox('setValue', row.ucode);
			}
		}
	} else {
		Public.tips({
			content : "请选择一行数据",
			type : 2
		});
	}
	$("#kj_dialog").dialog('close');
}

//说明：javascript的加法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的加法结果。  
//调用：accAdd(arg1,arg2)  
//返回值：arg1加上arg2的精确结果  
function accAdd(arg1,arg2){  
	var r1,r2,m;  
	try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}  
	try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}  
	m=Math.pow(10,Math.max(r1,r2))  
	return (arg1.mul(m)+arg2.mul(m))/m;  
}  
//给Number类型增加一个add方法，调用起来更加方便。  
Number.prototype.add = function (arg){  
	return accAdd(arg,this);  
}  

//说明：javascript的减法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的减法结果。  
//调用：accSub(arg1,arg2)  
//返回值：arg1减上arg2的精确结果  
function accSub(arg1,arg2){  
	return accAdd(arg1,-arg2);  
}  
//给Number类型增加一个sub方法，调用起来更加方便。  
Number.prototype.sub = function (arg){  
	return accSub(this,arg);  
} 

//说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。  
//调用：accMul(arg1,arg2)  
//返回值：arg1乘以arg2的精确结果  
function accMul(arg1,arg2){  
	var m=0,s1=arg1.toString(),s2=arg2.toString();  
	try{m+=s1.split(".")[1].length}catch(e){}  
	try{m+=s2.split(".")[1].length}catch(e){}  
	return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);  
}  
//给Number类型增加一个mul方法，调用起来更加方便。  
Number.prototype.mul = function (arg){  
	return accMul(arg, this);  
}  

//说明：javascript的除法结果会有误差，在两个浮点数相除的时候会比较明显。这个函数返回较为精确的除法结果。  
//调用：accDiv(arg1,arg2)  
//返回值：arg1除以arg2的精确结果  
function accDiv(arg1, arg2) {
	var t1 = 0, t2 = 0, r1, r2;
	try {
		t1 = arg1.toString().split(".")[1].length;
	} catch (e) {
	}
	try {
		t2 = arg2.toString().split(".")[1].length;
	} catch (e) {
	}
	r1 = Number(arg1.toString().replace(".", ""));
	r2 = Number(arg2.toString().replace(".", ""));
	return (r1 / r2) * Math.pow(10, t2 - t1);
}  
//给Number类型增加一个div方法，调用起来更加方便。  
Number.prototype.div = function (arg){  
	return accDiv(this, arg);  
}  

/**
 * 获取字段的float值
 * @param code
 * @returns
 */
function getFloatValue(code){
	return (code == "" || code == null || code == undefined ) ? parseFloat(0) : parseFloat(code);
}

/**
 * 判断值是否为空
 * @param code
 * @returns {Boolean}
 */
function isEmpty(code) {
	if (code != "" && code != null && code != undefined && code != "undefined"
			&& typeof (code) != "undefined" && code != "null") {
		return false;
	} else {
		return true;
	}
}

/**
 * 查询框时间改变事件
 * @param start
 * @param end
 */
function queryBoxChange(start,end){
	$(start).datebox({
		onChange: function(newValue, oldValue){
			var edv = $(end).datebox('getValue');
			$('#jqj').text(newValue + ' 至 ' + edv);
			if(isEmpty(newValue) && isEmpty(edv)){
				$('#jqj').text('');
			}
		}
	});
	$(end).datebox({
		onChange: function(newValue, oldValue){
			var sdv = $(start).datebox('getValue');
			$('#jqj').text(sdv + ' 至 ' + newValue);
			if(isEmpty(newValue) && isEmpty(sdv)){
				$('#jqj').text('');
			}
		}
	});
}

function HashMap(){  
    //定义长度  
    var length = 0;  
    //创建一个对象  
    var obj = new Object();  
  
    /** 
    * 判断Map是否为空 
    */  
    this.isEmpty = function(){  
        return length == 0;  
    };  
  
    /** 
    * 判断对象中是否包含给定Key 
    */  
    this.containsKey=function(key){  
        return (key in obj);  
    };  
  
    /** 
    * 判断对象中是否包含给定的Value 
    */  
    this.containsValue=function(value){  
        for(var key in obj){  
            if(obj[key] == value){  
                return true;  
            }  
        }  
        return false;  
    };  
  
    /** 
    *向map中添加数据 
    */  
    this.put=function(key,value){  
        if(!this.containsKey(key)){  
            length++;  
        }  
        obj[key] = value;  
    };  
  
    /** 
    * 根据给定的Key获得Value 
    */  
    this.get=function(key){  
        return this.containsKey(key)?obj[key]:null;  
    };  
  
    /** 
    * 根据给定的Key删除一个值 
    */  
    this.remove=function(key){  
        if(this.containsKey(key)&&(delete obj[key])){  
            length--;  
        }  
    };  
  
    /** 
    * 获得Map中的所有Value 
    */  
    this.values=function(){  
        var _values= new Array();  
        for(var key in obj){  
            _values.push(obj[key]);  
        }  
        return _values;  
    };  
  
    /** 
    * 获得Map中的所有Key 
    */  
    this.keySet=function(){  
        var _keys = new Array();  
        for(var key in obj){  
            _keys.push(key);  
        }  
        return _keys;  
    };  
  
    /** 
    * 获得Map的长度 
    */  
    this.size = function(){  
        return length;  
    };  
  
    /** 
    * 清空Map 
    */  
    this.clear = function(){  
        length = 0;  
        obj = new Object();  
    };  
}

/**
 * js实现list
 * 
 */
function ArrayList(){  
    this.arr=[],  
    this.size=function(){  
        return this.arr.length;  
    },  
    this.add=function(){  
        if(arguments.length==1){  
            this.arr.push(arguments[0]);  
        }else if(arguments.length>=2){  
            var deleteItem=this.arr[arguments[0]];  
            this.arr.splice(arguments[0],1,arguments[1],deleteItem)  
        }  
        return this;  
    },  
    this.get=function(index){  
        return this.arr[index];  
    },  
    this.removeIndex=function(index){  
        this.arr.splice(index,1);  
    },  
    this.removeObj=function(obj){  
        this.removeIndex(this.indexOf(obj));  
    },  
    this.indexOf=function(obj){  
        for(var i=0;i<this.arr.length;i++){  
            if (this.arr[i]===obj) {  
                return i;  
            };  
        }  
        return -1;  
    },  
    this.isEmpty=function(){  
        return this.arr.length==0;  
    },  
    this.clear=function(){  
        this.arr=[];  
    },  
    this.contains=function(obj){  
        return this.indexOf(obj)!=-1;  
    }  
}; 

function qryData(){
	$('#ywlxDlg').dialog({
		width : 500,
		height : 510,
		readonly : true,
		close : true,
		title : '选择业务类型',
		modal : true,
		queryParams : {
			dblClickRowCallback : 'qryBusiType',
			type	:	'',
		},
		href : DZF.contextPath + '/ref/busi_select.jsp',
		buttons : [ {
			text : '确认',
			handler : function() {
				var row = $('#busiGrid').treegrid('getSelected');
				if(row){
					$("#qryywxlid").val(row.id);
					qryBusiType(row);
				}
			}
		}, {
			text : '取消',
			handler : function() {
				$('#ywlxDlg').dialog('close');
			}
		} ]
	});
}

function qryBusiType(row){
	if(row.isleaf == '否'){
		Public.tips({
			content : "请选择末级科目",
			type : 2
		});
		return ;
	}
	$("#qryywxlid").val(row.id);
   	$("#qryywdlid").val(row.pid);
   	$("#qrySeries").textbox('setValue', row.text);
	$('#ywlxDlg').dialog('close');
}

/**
 * 字符串是否包含某字符
 * @param str
 * @param substr
 * @returns
 */
function isContains(str, substr) {
    return new RegExp(substr).test(str);
}

/**
 * 字符类型排序
 * @param a
 * @param b
 * @returns
 */
function charorderfun(a,b){
	return (a>b?1:-1);
}

/**
 * 查询期间初始化
 * @param code
 */
function initPeriod(code){
    $(code).datebox({
        onShowPanel: function() { //显示日趋选择对象后再触发弹出月份层的事件，初始化时没有生成月份层
            span.trigger('click'); //触发click事件弹出月份层
            if (!tds) setTimeout(function() { //延时触发获取月份对象，因为上面的事件触发和对象生成有时间间隔
                tds = p.find('div.calendar-menu-month-inner td');
                tds.click(function(e) {
                    e.stopPropagation(); //禁止冒泡执行easyui给月份绑定的事件
                    var year = /\d{4}/.exec(span.html())[0], //得到年份
                    month = parseInt($(this).attr('abbr'), 10); //月份，这里不需要+1
                    $(code).datebox('hidePanel') //隐藏日期对象
                    .datebox('setValue', year + '-' + month); //设置日期的值
                });
            },
            0);
            yearIpt.unbind(); //解绑年份输入框中任何事件
        },
        parser: function(s) {
            if (!s) return new Date();
            var arr = s.split('-');
            return new Date(parseInt(arr[0], 10), parseInt(arr[1], 10) - 1, 1);
        },
        formatter: function(d) {
            var a = parseInt(d.getMonth()) < parseInt('9') ? '0' + parseInt(d.getMonth() + 1) : d.getMonth() + 1;
            return d.getFullYear() + '-' + a;
        }
    });
    var p = $(code).datebox('panel'),
    //日期选择对象
    tds = false,
    //日期选择对象中月份
    yearIpt = p.find('input.calendar-menu-year'),
    //年份输入框
    span = p.find('span.calendar-text'); //显示月份层的触发控件
}

/**
 * 数值类型排序
 * @param a
 * @param b
 * @returns
 */
function orderfun(a,b){
	a = getFloatValue(a);  
	b = getFloatValue(b);  
	return (a>b?1:-1);
}


/**
 * 去除字符空格（如果需要去掉字符间空格，第二个参数传'g'）
 * @param str
 * @param is_global
 * @returns
 */
function trimStr(str, is_global) {
    var result;
    result = str.replace(/(^\s+)|(\s+$)/g, "");
    if (is_global== "g") {
        result = result.replace(/\s/g, "");
    }
    return result;
}

/**
 * 校验按钮权限
 */
function checkBtnPower(btncode,funnode,callback){
	$.ajax({
        type: "post",
        dataType: "json",
        url: DZF.contextPath + '/sys/btnPower!checkBtnPower.action',
        data : {
        	btncode : btncode,
        	funnode : funnode,
		},
        traditional: true,
        async: false,
        success: function(data, textStatus) {
            if (data.success) {
            	callback();
            }else{
            	Public.tips({content:data.msg,type:1});
            }
        },
    });
}

/**
 * 替换所有字符
 */
String.prototype.replaceAll = function (FindText, RepText) {
    regExp = new RegExp(FindText, "g");
    return this.replace(regExp, RepText);
}

/**
 * 计算日期或期间相差几个月
 * @param date1(格式2017-12 或 2018-02-12)
 * @param date2(格式2017-12 或 2018-02-12)
 * @returns
 */
function MonthDiff(date1, date2) {
	date1 = date1.split('-');
	date1 = parseInt(date1[0]) * 12 + parseInt(date1[1]);
	date2 = date2.split('-');
	date2 = parseInt(date2[0]) * 12 + parseInt(date2[1]);
	return Math.abs(date1 - date2);
}

/**
 * 获取两个月份之间的月份
 * @param start
 * @param end
 * @returns {Array}
 */
function getBetweenPeriod(start, end) {
	var result = [];
	var s = start.split("-");
	var e = end.split("-");
	if(s[1].indexOf('0') == 0){
		s[1] = s[1].replace('0', '');
	}
	if(e[1].indexOf('0') == 0){
		e[1].replace('0', '')
	}
	var nb = parseInt(s[0]) * 12 + parseInt(s[1]);
	var ne = parseInt(e[0]) * 12 + parseInt(e[1]);
	var year = 0;
	var month = 0;
	for (var i = nb; i <= ne; i++) {
		month = i % 12;
		year = parseInt(i / 12);
		if (month == 0) {
			month = 12;
			year -= 1;
		}
		result.push(year + "-" + (month < 10 ? "0" + month : month));
	}
	return result;
}
