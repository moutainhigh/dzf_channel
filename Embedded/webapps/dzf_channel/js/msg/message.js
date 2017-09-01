$(window).resize(function() {
    $(".msg-right").width($("body").width() - $(".msg-left").width() - 20);
    $(".msg-left").height($(window).height());
    $("#msgGrid").datagrid("resize", {
        width: 'auto',
        height: Public.setGrid().h
    });
});

$(function() {
    $(".msg-right").width($("body").width() - $(".msg-left").width() - 20);
    $(".msg-left").height($(window).height());
    $("#msgGrid").datagrid({
        url: '',
        striped: true,
        singleSelect: false,
        height: Public.setGrid().h,
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 20, 50],
        pagination: true,
        columns: [
            [{
                field: 'ck',
                checkbox: true
            }, {
                width: '200',
                title: '时间',
                field: 'vsdate'
            }, {
                width: '661',
                title: '内容',
                field: 'content',
                formatter: function(value,row,index){               	
                	if(row.msgtype==0||row.msgtype==8){//徐志:大账房平台公告显示为（xx标题：xx内容）；其他消息类型无标题，直接显示xx内容
                		if(row.vtitle&&row.vtitle!=''){
                			return row.vtitle + " : "+value;
                		}
                	}
                	
                	return value;
                },
    			styler: function(value,row,index){
    				
    			   return 'word-wrap:break-word;';
    			}

            },{
            	width : '200',
            	title : '操作',
            	field : 'id',
            	formatter : function(value, row) {
            		if(!isHaveLink(row.msgtype)){
            			return value;
            		}
            		return '<a rel="" href="javascript:link(\'' + row.pk_bill + '\',\'' + row.msgtype + '\',\'' + row.pk_bill_b + '\',\'' + row.content + '\',\'' + row.pk_order + '\',\'' + row.pk_order_b + '\')" style="color:blue">查看</a><br />';
            	},
			align:'center'
			}]
        ],
        rowStyler: function(index, row) {
        	 if (row.msgtype != 0 && (!row.isread || row.isread == '否'))
                return 'background-color: #ffe1e1; color: #555; cursor: pointer'
        },
        onSelect: function(index, row) {
            if (row.msgtype != 0 && (!row.isread || row.isread == '否')) {
                row.isread = '是';
                parent.updateMsgStatus(row);
                updateUnreadNum();
                $(this).datagrid("updateRow", {
                    index: index,
                    row: row
                });
                $('#dataGrid .datagrid-body  td[field="content"] div').css("word-wrap","break-word");
            }
        },
        onLoadSuccess: function () {
        	$("#dataGrid a").on("click", function (e) {
        		e.stopPropagation();
        	});
        	
        	$('#dataGrid .datagrid-body td[field="content"] div').css("word-wrap","break-word");
        }
    });
    getMsgType();
    initDatebox();
});

function initDatebox() {
    $(".easyui-datebox").datebox("setValue", Public.getLoginDate());
    $("#bdate").datebox("textbox").blur(function() {
        setTimeout(function() {
            checkDateCommon('bdate');
        }, 200);
    });
    $("#edate").datebox("textbox").blur(function() {
        setTimeout(function() {
            checkDateCommon('edate');
        }, 200);
    });
}

function getMsgType() {
    $.ajax({
        type: "POST",
        url: DZF.contextPath + '/msg/pub_message!getMsgTypes.action',
        dataType: "json",
        data: {sreceive: sys_side},
        success: function(data) {
            if (data.success) {
                var types = data.rows;
                var typeStr = '';
                var total_new = 0;
                for (var i = 0; i < types.length; i++) {
                    var type = types[i];
                    type.new_msg = type.new_msg ? type.new_msg : 0;
                    total_new += type.new_msg;
                    typeStr += '<li id="type-' + type.type_code
                        + '" class="msg-types">' +
                        '<span class="mg-timg actimg"></span>' +
                        '<a href="javascript:void(0);" title="">' +
                        type.type_name + '</a>';
                    var style = type.new_msg > 0 ? "" : "display: none";
                    typeStr += '<span class="num-tips" style="' + style + '">'
                            + type.new_msg + '</span>';
                    typeStr += '</li>'
                }
                parent.updateNavMsgNum(total_new);
                $("#types").html(typeStr);
                $("#types").on("click", "li", function() {
                    if (!$(this).hasClass("msg-types-cur")) {
                        selectType($(this).attr("id").split("-")[1]);
                    }
                });
                selectType(types[0].type_code);
            }
        }
    });
}

function selectType(code) {
    $("#msgGrid").datagrid("options").url = DZF.contextPath + '/msg/pub_message!query.action';
    $("#msgGrid").datagrid("load", {
    	sreceive: sys_side,
        msgtype: code
    });
    $("#types li").removeClass("msg-types-cur");
    $("#type-" + code).addClass("msg-types-cur");
}


function updateUnreadNum(num) {
	var numJq = $(".msg-types-cur .num-tips");
	num = num == undefined ? -1 : num;
	var total = num + Number(parent.$("#message-new-count").text());
	var subtotal = num +  Number(numJq.text());
    parent.updateNavMsgNum(total);
    numJq.text(subtotal);
    if (subtotal > 0) {
        numJq.show();
    } else {
        numJq.hide();
    }
}

function query() {
    if (checkDateCommon('bdate') && checkDateCommon('edate')) {
        var param = serializeObject($("#qry-form"));
        param.msgtype = getCurType();
        param.sreceive = sys_side;
        $("#msgGrid").datagrid("load", param);
    }
}

function getCurType() {
    return $(".msg-types-cur").attr("id").split("-")[1];
}

function onSelectType() {
    var id = $(this).attr("id");
}

function del() {
    var msgs = $("#msgGrid").datagrid("getSelections");
    if (msgs && msgs.length > 0) {
        if ($(".msg-types-cur").attr("id").split("-")[1] == "0") {
            Public.tips({
                content: '系统消息不允许删除！',
                type: 1
            });
            return;
        }
        $.messager.confirm("提示", "确认删除所选的" + msgs.length + "条消息吗", function(con) {
            if (con) {
                var unreadMsg = 0;
                for (var i = 0; i < msgs.length; i++) {
                    if (msgs[i].isread != '是') {
                        unreadMsg++;
                    }
                }
                $.ajax({
                    type: "POST",
                    url: DZF.contextPath + '/msg/pub_message!delete.action',
                    data: {
                        msgs: JSON.stringify(msgs)
                    },
                    dataType: "json",
                    success: function(data) {
                        if (data.success) {
                            if (unreadMsg > 0) {
                                updateUnreadNum(-unreadMsg);
                            }
                            $("#msgGrid").datagrid("reload");
                            Public.tips({
                                content: data.msg,
                                type: 0
                            });
                        } else {
                            Public.tips({
                                content: data.msg,
                                type: 1
                            });
                        }
                    }
                });
            }
        })
    } else {
        Public.tips({
            content: "请选择要删除的消息！",
            type: 2
        });
    }
}
function readMsg() {
    var msgs = $("#msgGrid").datagrid("getSelections");
    if (msgs && msgs.length > 0) {
        if ($(".msg-types-cur").attr("id").split("-")[1] == "0") {
            Public.tips({
                content: '不能标记系统消息',
                type: 2
            });
            return;
        }
        var unreadMsg = 0;
        for (var i = 0; i < msgs.length; i++) {
            if (msgs[i].isread != '是') {
                unreadMsg++;
            }
            msgs[i].isread = '是';
        }
        $.ajax({
            type: "POST",
            url: DZF.contextPath + '/msg/pub_message!updateMsgStatus.action',
            data: {
                msgs: JSON.stringify(msgs)
            },
            dataType: "json",
            success: function(data) {
                if (data.success) {
                    if (unreadMsg > 0) {
                        updateUnreadNum(-unreadMsg);
                    }
                    $("#msgGrid").datagrid("reload");
                    Public.tips({
                        content: "标记成功",
                        type: 0
                    });
                } else {
                    Public.tips({
                        content: data.msg,
                        type: 1
                    });
                }
            }
        });

    } else {
        Public.tips({
            content: "请至少选择一条消息",
            type: 2
        });
    }
}

function unreadMsg() {
    var msgs = $("#msgGrid").datagrid("getSelections");
    if (msgs && msgs.length > 0) {
        if ($(".msg-types-cur").attr("id").split("-")[1] == "0") {
            Public.tips({
                content: '不能标记系统消息',
                type: 2
            });
            return;
        }
        var unreadMsg = 0;
        for (var i = 0; i < msgs.length; i++) {
            if (msgs[i].isread == '是') {
                unreadMsg++;
            }
            msgs[i].isread = '否'
        }
        $.ajax({
            type: "POST",
            url: DZF.contextPath + '/msg/pub_message!updateMsgStatus.action',
            data: {
                msgs: JSON.stringify(msgs)
            },
            dataType: "json",
            success: function(data) {
                if (data.success) {
                    if (unreadMsg > 0) {
                        updateUnreadNum(unreadMsg);
                    }
                    $("#msgGrid").datagrid("reload");
                    Public.tips({
                        content: "标记成功",
                        type: 0
                    });
                } else {
                    Public.tips({
                        content: data.msg,
                        type: 1
                    });
                }
            }
        });

    } else {
        Public.tips({
            content: "请至少选择一条消息",
            type: 2
        });
    }
}

function isHaveLink(msgtype){
	
	//MsgtypeEnum
	if("3"==msgtype||"4"==msgtype||"5"==msgtype||"6"==msgtype||"23"==msgtype
			||"24"==msgtype||"25"==msgtype||"1"==msgtype||"7"==msgtype
			||"11"==msgtype||"12"==msgtype||"10"==msgtype||"27"==msgtype
			||"28"==msgtype||"9"==msgtype||"34"==msgtype){
		
		return true;
	}
	
	return false;
}


function link(pk_bill,msgtype,pk_bill_b,content,pk_order,pk_order_b){
	if(!pk_bill){
		return;
	}
	
	switch(msgtype){
	   case "3"://服务发布审核通过
		   openSrvPub(pk_bill,pk_bill_b,content);
		break;
	   case "4"://服务发布审核未通过
		   openSrvPub(pk_bill,pk_bill_b,content);
			break;
	   case "5"://平台审核接单人通过
		   openBillPsn(pk_bill,pk_bill_b,content);
			break;
	   case "6"://平台审核接单人未通过
		   openBillPsn(pk_bill,pk_bill,content);
			break;
	   case "7"://价格修改下架通知
		   openSrvPub(pk_bill,pk_bill_b,content,msgtype);
			break;
	   case "23"://下单通知
		   openBillQuery(pk_order,pk_order_b);
			break;
	   case "24"://订单付款完成通知
		   openBillQuery(pk_order,pk_order_b);
			break;
	   case "25"://客户申请退款
		   openBillProcess(pk_order,pk_order_b);
			break;
	   case "1"://订单退款已完成
		   openBillProcess(pk_order,pk_order_b);
			break;
	   case "11"://合同到期提醒
		   openContract(pk_bill,pk_bill);
			break;
	   case "12"://证书到期提醒
		   openCorpQuery(pk_bill,pk_bill);
			break;
	   case "10"://应收提醒
		   openArrearageStatis(pk_bill,pk_bill_b);
			break;
	   case "27"://手机签约意向通知
		   openCustSign(pk_bill,pk_bill_b);
			break;
	   case "28"://手机业务合作意向通知
		   openCustNewBusCoop(pk_bill,pk_bill_b);
			break;
	   case "8"://公司内部通知
		  // openInnerNotice(pk_bill,pk_bill_b);
			break;
	   case "9"://业务处理提醒
		   openBusProcess(pk_bill,pk_bill_b);
			break;
	   case "34"://订单完成
		   openBillQuery(pk_order,pk_order_b);
			break;
	}
}

//服务发布
function openSrvPub(pk_bill,pk_bill_b,content,msgtype){
		
	var name = '服务项目发布';
	var url =  '/website/srvpropub/srvpropub.jsp';
	if(!pk_bill_b||pk_bill_b==''||pk_bill_b=='undefined'||content.indexOf('套餐')>=0){
		name = '套餐发布';
		url = '/website/srvpropub/packagepub.jsp';
	}
	
	if(!checkAuth(url)){
		return ;
	}
	url = DZF.contextPath + url;
	url+='?pk_bill='+pk_bill+'&pk_bill_b='+pk_bill_b+'&msgtype='+msgtype;
	parent.addTabNew(name,url);
}

//接单人
function openBillPsn(pk_bill,pk_bill_b,content){
	
	if(!checkAuth('/website/billperson/billperson.jsp')){
		return ;
	}
	
    url = DZF.contextPath + '/website/billperson/billperson.jsp';
    url+='?pk_bill='+pk_bill;
    parent.addTabNew('接单人信息',url);
}

//订单查询
function openBillQuery(pk_order,pk_order_b){
	
	if(!checkAuth('/website/order/weborder.jsp')){
		return ;
	}
	
	url = DZF.contextPath + '/website/order/weborder.jsp';
	url+='?pk_order='+pk_order;
	parent.addTabNew('订单查询',url);
}

//订单处理
function openBillProcess(pk_order,pk_order_b){
	
	if(!checkAuth('/website/order/repayorder.jsp')){
		return ;
	}
	
    url = DZF.contextPath + '/website/order/repayorder.jsp';
    url+='?pk_order='+pk_order;
    parent.addTabNew('订单处理',url);
}

//合同到期提醒
function openContract(pk_bill,pk_bill_b){
	
	if(!checkAuth('/sys/contract/contract.jsp')){
		return ;
	}
	
    url = DZF.contextPath + '/sys/contract/contract.jsp';
    url+='?pk_bill='+pk_bill;
    parent.addTabNew('代理记账合同',url);
}

//客户查询
function openCorpQuery(pk_bill,pk_bill_b){
	
	if(!checkAuth('/sys/sys_set/sys_corp_query.jsp')){
		return ;
	}
	
    url = DZF.contextPath + '/sys/sys_set/sys_corp_query.jsp';
    url+='?pk_bill='+pk_bill;
    parent.addTabNew('客户查询',url);
}

//欠费统计
function openArrearageStatis(pk_bill,pk_bill_b){
	
	if(!checkAuth('/sys/chargemng/dljzsfmng.jsp')){
		return ;
	}
	
    url = DZF.contextPath + '/sys/chargemng/dljzsfmng.jsp';
    url+='?pk_bill='+pk_bill+'&pk_bill_b='+pk_bill_b;
    parent.addTabNew('欠费统计',url);
}

//客户签约
function openCustSign(pk_bill,pk_bill_b){
	
	if(!checkAuth('/sys/sys_set/sys_cpqy.jsp')){
		return ;
	}
	
    url = DZF.contextPath + '/sys/sys_set/sys_cpqy.jsp';
    url+='?pk_bill='+pk_bill;
    parent.addTabNew('客户签约',url);
}

//新业务合作
function openCustNewBusCoop(pk_bill,pk_bill_b){
	
	if(!checkAuth('/sys/customer/custhz.jsp')){
		return ;
	}
	
    url = DZF.contextPath + '/sys/customer/custhz.jsp';
    url+='?pk_bill='+pk_bill;
    parent.addTabNew('新业务合作',url);
}

//内部通知
function openInnerNotice(pk_bill,pk_bill_b){
	
	if(!checkAuth('/sys/sys_cust/send_notice.jsp')){
		return ;
	}
	
    url = DZF.contextPath + '/sys/sys_cust/send_notice.jsp';
    url+='?pk_bill='+pk_bill;
    parent.addTabNew('内部通知',url);
}

//业务处理
function openInnerNotice(pk_bill,pk_bill_b){
	
	if(!checkAuth('/sys/busimng/busimng.jsp')){
		return ;
	}
	
    url = DZF.contextPath + '/sys/busimng/busimng.jsp';
    url+='?pk_bill='+pk_bill;
    parent.addTabNew('业务处理',url);
}

//业务处理
function openBusProcess(pk_bill,pk_bill_b){
	
	if(!checkAuth('/sys/busimng/busimng.jsp')){
		return ;
	}
	
    url = DZF.contextPath + '/sys/busimng/busimng.jsp';
    url+='?pk_bill='+pk_bill;
    parent.addTabNew('业务处理',url);
}


//节点权限验证
function  checkAuth(url){
	
	var nodeurls = $("#nodeurls").val();
	if(url&&url!=""&&nodeurls&&nodeurls!=""&&nodeurls.indexOf(url)<0){
		
        Public.tips({
            content: "对不起,您没有相应的节点权限！",
            type: 2
        });		
		return false;
	}
	
	return true;	
}
