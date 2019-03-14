var dgmsinfo;
var contextPath = DZF.contextPath;
var tablediaWin;
var state;
var currentRow = 0;

//管理端精度暂定4位
var pricePrecision = Number(4);//单价

$(window).resize(function() {
    $('#dgmsinfo').datagrid('resize', {
        height : Public.setGrid().h,
        width : 'auto'
    });
});

$(document).ready(function(){
	
    window.getBLen = function(str) {
        if (str == null) return 0;
        if (typeof str != "string"){
            str += "";
        }
        return str.replace(/[^\x00-\xff]/g,"01").length;
    }
    
    $(document).on('keyup', 'input', function(e) {
        if(e.keyCode == 13 && e.target.type!== 'submit') {
            var inputs = $(e.target).parents("form").eq(0).find(":input:visible");
            var idx = inputs.index(e.target);
            if (idx == inputs.length - 3) {
                inputs[0].select();
            } else {
            	inputs[idx + 1] && inputs[idx + 1].focus();
            	inputs[idx + 1] && inputs[idx + 1].select();
            }
        }
    });
	
	$("input",$(".basic_info .basic_title:first-child  label").next("font")).blur(function(e){  
		var snumber = e.target.value;
		var value = snumber.replace(/[^0-9]/ig,""); 
		$("#dl_invcode").textbox('setValue', value);
    });  
	
	$("input",$(".basic_info .basic_title:nth-child(2)  label").next("font")).blur(function(e){  
		var numbers = e.target.value;
		var values = numbers.replace(/[^0-9]/ig,""); 
    }); 
	
	$(".Custom").click(function(){
	   $(".input_add").toggle();
	});
    
});//enter 键代替tab键换行        end

$(document).ready(function(){
    $(document.getElementById("toolbarHide")).show();
});

$(window).load(function () {
	$.extend($.fn.validatebox.defaults.rules, {
		date : {
			validator : function(value) {
				return isDateType(value);
			},
			message : '日期无效或格式不正确.'
		},
	});

	function isDateType(value) {
		if (value != "" && !value.match(/^((?:19|20)\d\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$/)) {
			return false;
		} else if(value != "" && new Date(value) == 'Invalid Date'){
			return false;
		}else {
			return true;
		}
	}

});

$(function(){

    window.addTr = function () {

        var trId = "id" +  new Date().valueOf();
        var $tr = $("<tr id='" + trId + "' class='add-tr'>" + $("#trTM").html() + "</tr>");

        $tr.hover(function () {
            $(this).find(".removeAdd").show()
        }, function () {
            $(this).find(".removeAdd").hide()
        });
        $tr.click(function () {
            currentRow = $(this).attr('id');
        });

        $("#table_body").append($tr);
    };

    function comdify(n){
        var re=/\d{1,3}(?=(\d{3})+$)/g;
        var n1=n.toString().replace(/^(\d+)((\.\d+)?)$/,function(s,s1,s2){return s1.replace(re,"$&,")+s2;});
        return n1;
    }
    
    function valFormat(str) {
        return Number(str.replace(/,/g,''))
    }
    
    window.trOnchange = function (t, field) {
        if (isNaN(valFormat(t.value))) {
            t.value = '';
            return;
        }
        var $tr = $(t).parent().parent();

        var bnum = getFloatValue($tr.find('[name="bnum"]').val());
        var bje = getFloatValue($tr.find('[name="bje"]').val());
        if (field == 'bnum') {//数量或单价
        	$tr.find('[name="bdj"]').val(bje.div(bnum).toFixed(pricePrecision));
        }
        
        var concatje = 0;
        var concatse = 0;
        $.each($('#table_body input'), function (idx, item) {
            if (item.value == 'Infinity') {
                item.value = '';
            }
            if (item.name == 'bje' && !isEmpty(item.value)) {
          	concatje = concatje.add(getFloatValue(item.value));
          }
          if (item.name == 'bse' && !isEmpty(item.value)) {
          	concatse = concatse.add(getFloatValue(item.value));
          }
        });

        $('#concatje').text(comdify(concatje.toFixed(2)));
        $('#concatse').text(comdify(concatse.toFixed(2)));

        $('#content2').text(comdify((concatse + concatje).toFixed(2)));
        $('#content').text(DX(((concatse + concatje).toFixed(2) + '')));

        $.each($tr.find('[name="bdj"], [name="bje"], [name="bse"]'), function (idx, item) {
            this.value = comdify(valFormat(this.value));
        })
        $tr.find('[name="bdj"]').val(valFormat($tr.find('[name="bdj"]').val()).toFixed(pricePrecision));
        $tr.find('[name="bnum"]').val(valFormat($tr.find('[name="bnum"]').val()));

        $tr.find('[name="bje"]').val(valFormat($tr.find('[name="bje"]').val()).toFixed(2));
        $tr.find('[name="bse"]').val(valFormat($tr.find('[name="bse"]').val()).toFixed(2));
    }
    window.trKeyup = function () {

    };

    window.removeTr = function (tr) {
    	
    	//获取当前操作行行数
    	var index = $(tr).parent().parent().parent().prevAll().length;//行号
    	
    	var dbje = $("#table_body tr:eq(" + index + ") ").find('[name="bje"]').val();
    	if(!isEmpty(dbje)){
    		//含税金额
    		var cmny = $("#table_body tr:eq(" + index + ") ").find('[name="cmny"]').val();
    		cmny = getFloatValue(cmny);
    		
    		//含税金额（上一行）
    		var lcmny = $("#table_body tr:eq(" + (index - 1) + ") ").find('[name="cmny"]').val();
    		lcmny = getFloatValue(lcmny);
    		
    		cmny = cmny.add(lcmny);
    		$("#table_body tr:eq(" + (index - 1) + ") ").find('[name="cmny"]').val(cmny.toFixed(2));
    		
    		//不含税金额 = 含税金额/(1+0.16);
    		var bje = cmny.div(parseFloat(1.16));
    		$("#table_body tr:eq(" + (index - 1) + ") ").find('[name="bje"]').val(bje.toFixed(2));
    		
    		//数量
    		var bnum = $("#table_body tr:eq(" + (index - 1) + ") ").find('[name="bnum"]').val();
    		bnum = getFloatValue(bnum);
    			
    		//不含税单价 = 不含税金额/数量；
    		var bdj = bje.div(bnum);
    		$("#table_body tr:eq(" + (index - 1) + ") ").find('[name="bdj"]').val(bdj.toFixed(pricePrecision));
    		
    		//税额 = 不含税金额 * 0.16；
    		var bse = bje.mul(parseFloat(0.16));
    		$("#table_body tr:eq(" + (index - 1) + ") ").find('[name="bse"]').val(bse.toFixed(2));
    		
    	}
    	
    	
        $(tr).parent().parent().parent().remove();
        var concatje = parseFloat(0);
        var concatse = parseFloat(0);
        $.each($('#table_body input'), function (idx, item) {
            if(item.value == 'Infinity') {
                item.value = ''
            }
            if (item.name == 'bje' && !isEmpty(item.value)) {
            	concatje = concatje.add(getFloatValue(item.value));
            }
            if (item.name == 'bse' && !isEmpty(item.value)) {
            	concatse = concatse.add(getFloatValue(item.value));
            }
        });

        $('#concatje').text(comdify(concatje));
        $('#concatse').text(comdify(concatse));

        $('#content2').text(comdify(concatse + concatje));
        $('#content').text(DX(((concatse + concatje).toFixed(2) + '')));
    };

    tablediaWin = $("#tabledia").dialog({
        width : 1000,
        height : 480,
        readonly : true,
        title : '商品订单开票',
        cache: false,
        modal : true,
        buttons : [ {
            text : '提交',
            handler : function() {
            	state = true;
                onSave();
            }
        },{
            text : '取消',
            handler : function() {
            	state = false;
                $("#tabledia").dialog('close');
            }
        } ],
        onClose: function(){
            $('#tabledia :input').val('');
            $("#table_header").find('#pk_id').remove();
            $("#tabledia").parent().find(".dialog-button a:eq(0)").show();
            $("#tabledia").parent().find(".dialog-button a:eq(1)").find('.l-btn-text').text('保存');
            $('#tablediaForm').form('reset');
            $("#table_body .add-tr").remove();
            $('#concatje').html('');
            $('#concatse').html('');
            $('#content').html('');
            $('#content2').html('');
        }
    });
    
	var width=Public.setGrid(40).w > 1623 ? 1623 : Public.setGrid(40).w;
    
})

$(window).resize(function() {
    //当浏览器大小变化时
	$('#tabledia').window('center');
});

/**
 * 大写转换
 * @param n
 * @returns
 */
function DX(n) {

    var unit = "千百拾亿千百拾万千百拾元角分", str = "";
    n += "00";

    var p = n.indexOf('.');
 
    if (p >= 0){
        n = n.substring(0, p) + n.substr(p+1, 2);
        unit = unit.substr(unit.length - n.length);
        for (var i=0; i < n.length; i++)
            str += '零壹贰叁肆伍陆柒捌玖'.charAt(n.charAt(i)) + unit.charAt(i);
        var concatM =  str.replace(/零(千|百|拾|角)/g, "零").replace(/(零)+/g, "零").replace(/零(万|亿|元)/g, "$1")
        .replace(/(亿)万|壹(拾)/g, "$1$2").replace(/^元零?|零分/g, "").replace(/元$/g, "元整");
    }
    if(n > 0){	
    	 return concatM
    }else if(n<0){
    	return "(负数)"+concatM;
    }else{
    	return "零"+concatM;
    }
}

/**
 * 金额格式化
 * @param opt
 * @param num1
 * @param num2
 * @param power
 * @returns {Number}
 */
function calculateMny(opt, num1, num2, power) {
    var number = powerCalculator(10, power);
    if (isNaN(num1)) {
        num1 = 0;
    }

    if (isNaN(num2)) {
        num2 = 0;
    }
    if (opt === 'add') {
        return ((num1 * number) + (num2 * number)) / number;
    } else if (opt === 'sub') {
        return ((num1 * number) - (num2 * number)) / number;
    } else if (opt === 'mul') {
        return ((num1 * number) * (num2 * number)) / (number * number);
    } else if (opt === 'div') {
        return ((num1 * number) / (num2 * number));
    }
}

/**
 * 金额格式化
 * @param base
 * @param power
 * @returns {Number}
 */
function powerCalculator(base, power) {
    var number = base;
    if (power == 1) return number;
    if (power == 0) return 1;
    for (var i = 2; i <= power; i++) {
        number = number * base;
    }
    return number;
}

/**
 * 按指定精度格式化数字
 * @param Number pnumber 待格式化数字
 * @param Number decimals 小数位数
 * @returns {String}
 */
function formatPrice(pnumber) {
    if (isNaN(pnumber) || pnumber == "")
        return '';
    var value = String(pnumber).replace(/,/g, "");
    if (isNaN(value) || value == "")
        return "";
    value = Math.round(value * 10000) / 10000;
    if (value < 0)
        return '-' + outputdollars(Math.floor(Math.abs(value) - 0) + '')
            + outputcents4((Math.abs(value) - 0));
    else
        return outputdollars(Math.floor(value - 0) + '')
            + outputcents4((value - 0));
}

/**
 * 精度控制
 * @param amount
 * @returns {String}
 */
function outputcents4(amount) {
    amount = Math.round(((amount) - Math.floor(amount)) * 10000);
    if(amount<10){
        return '.000' + amount;
    }else if(amount<100){
        return '.00' + amount;
    }else if(amount<1000){
        return '.0' + amount;
    }else if(amount<10000){
        return '.' + amount;
    }else{
        return '.0000';
    }
}

/**
 * 清空界面值
 */
function cleanValue(){
	
//	//购买方信息：
	$('#dl_invcode').textbox('setValue', null);
	$('#dl_cname').textbox('setValue', null);
	$('#dl_taxnum').textbox('setValue', null);
	$('#dl_caddr').textbox('setValue', null);
	$('#dl_bname').textbox('setValue', null);
	$('#dl_phone').textbox('setValue', null);
	$('#dl_bcode').textbox('setValue', null);
	$('#dl_email').textbox('setValue', null);
	$('#dl_ndesummny').numberbox('setValue', null);
	$('#dl_nderebmny').numberbox('setValue', null);
	$('#dl_ndemny').numberbox('setValue', null);
	
	$.each($("#table_body tr"),
	function(idx, item) {
		$(item).find('input').val(null);
	})
}

/**
 * 新增商品订单开票
 * @param index
 * @returns {Boolean}
 */
function addInvoce(index) {

	var row = $('#grid').datagrid('getRows')[index];

    tablediaWin.dialog('open');
    tablediaWin.window('center');// 使Dialog居中显示
    
    cleanValue();
    
    var url = DZF.contextPath + "/dealmanage/channelorder!queryInvoiceInfo.action";
    $.post(url,  {"billid": row.billid, "corpid": row.corpid, "updatets": row.updatets},
    function(res) {
        if(res.success) {
        	var hiddenstr = "<input type='hidden' id='sourceid' name='sourceid' value='" + res.data.sourceid + "' />"+
        	"<input type='hidden' id='updatets' name='updatets' value='" + res.data.updatets + "' />"+
        	"<input type='hidden' id='runame' name='runame' value='" + res.data.runame + "' />"+
        	"<input type='hidden' id='corpid' name='corpid' value='" + res.data.corpid + "' />";
            $("#table_header").append(hiddenstr);
            
            $.each(res.data,function (row, item) {
            	var type = typeof item;
            	
				if($("#dl_" + row).length > 0) {
					try {
						$("#dl_" + row).textbox('setValue',item);
					} catch (e) {
						$("#dl_" + row).val(item);
					}
				}

            })
            $("#table_body .add-tr").remove();
            for(var idx in res.data.children) {
                for (var chi in res.data.children[idx]) {
                	$("#table_body tr:eq(" + idx + ") [name='" + chi + "']").val(res.data.children[idx][chi]);
                    if (chi == 'bnum'){
                        $("#table_body tr:eq(" + idx + ") [name='" + chi + "']").val(res.data.children[idx][chi]);
                    }
                    if (chi == 'bdj'){
                        $("#table_body tr:eq(" + idx + ") [name='" + chi + "']").val(res.data.children[idx][chi].toFixed(pricePrecision));
                    }
                }
                addTr();
            }
            $("#table_body .add-tr:last").remove();
			var kiss1 = 0;
			var kiss2 = 0;
            for(var ix in res.data.children ){
                var bseTemp = res.data.children[ix].bse == undefined ? '0' : res.data.children[ix].bse;
                var bjeTemp = res.data.children[ix].bje == undefined ? '0' : res.data.children[ix].bje;
                kiss1 += Number(bseTemp);
                kiss2 += Number(bjeTemp);
            }

            $('#concatse').text(parseFloat(kiss1).toFixed(2));
            $('#concatje').text(parseFloat(kiss2).toFixed(2));

        	var DGcat2 = Number(parseFloat(kiss1).toFixed(2)) + Number(parseFloat(kiss2).toFixed(2));
        	var DGcat = parseFloat(DGcat2).toFixed(2);
            $('#content').text(DX(DGcat));
            $('#content2').text(DGcat);
     
            $("#goodslist .goodsbody .goodsgrid  tr").hover(function(){
        		$(this).find(".removeAdd").show();
            },function(){
            	$(this).find(".removeAdd").hide();
            });

        }else {
            Public.tips({
                content : res.msg,
                type : 2
            });
        }
    },'json');
    $.messager.progress('close');
    return false;
}

/**
 * 新增保存
 * @param type
 * @returns {Boolean}
 */
function onSave(type) {
	if ($("#tablediaForm").form('validate')) {
		var parseJSON = function (ele) {
	        var arrJson = ele.serializeArray();
	        var obj = {};
	        for (var item in arrJson) {
	            obj[arrJson[item].name] = arrJson[item].value;
	        }
	        return obj;
	    }

	    var parseHeaderJSON = function (ele) {
	        var obj = {};
	        $.each(ele, function (idx, item) {
	            if (item.name == 'sourceid' || item.name == 'updatets' || item.name == 'runame'
	            	|| item.name == 'corpid') {
	                obj[item.name] = item.value;
	            }else {
	                if($(item).parent().find('.textbox-text').val() != '' && item.name != ''){
	                    obj[item.name] = $(item).parent().find('.textbox-text').val();
	                }
	            }
	        })
	        return obj;
	    }
	    
	    var paraHeader, paraBody = [];
	    var ph = parseHeaderJSON($("#table_header :input , #table_footer :input , textarea" ));
		
	    paraHeader = JSON.stringify(ph);
		$.each($("#table_body tr"), function(idx, item) {
			if(!isEmpty($(item).find('[name="bje"]').val())){
				paraBody.push(parseJSON($(item).find('input')));
			}
		})
		
		paraBody = JSON.stringify(paraBody);
		
		var postdata = new Object();
		postdata["head"] = paraHeader;
		postdata["body"] = paraBody;
	    
		$.messager.progress({
			text : '数据操作中....'
		});
		$.ajax({
			type : "post",
			dataType : "json",
			url : contextPath + '/dealmanage/channelorder!saveInvoice.action',
			data : postdata,
			traditional : true,
			async : false,
			success : function(data, textStatus) {
				$.messager.progress('close');
				if (!data.success) {
					Public.tips({
						content : data.msg,
						type : 2
					});
				} else {
					$('#tablediaForm').form('reset');
		            $.messager.progress('close');

		            tablediaWin.dialog('close');
		            reloadData();
				}
			},
		});
		
	} else {
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return; 
	}
	
    
//	var url = contextPath + "/financetax/orderInvoiceAct!onUpdate.action";
//    $.messager.progress({
//        text : '数据保存中，请稍候.....'
//    });
//    
//    $.post(url, {
//        "adddoc": {
//            "header": paraHeader,
//            "body": paraBody
//        }
//    },
//    function(result) {
//        if (result.success) {
//            Public.tips({
//                content: '操作成功',
//                type: 0
//            });
//            $('#tablediaForm').form('reset');
//            $.messager.progress('close');
//
//            tablediaWin.dialog('close');
//            reloadData();
//        } else {
//            Public.tips({
//                content: result.msg,
//                type: 1
//            });
//            $.messager.progress('close');
//        }
//    },
//    'json');
}
