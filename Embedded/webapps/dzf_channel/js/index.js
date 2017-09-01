function updateMsgStatus (data, callback) {
    $.ajax({
		type : 'POST',
		url : DZF.contextPath + '/msg/pub_message!updateMsgStatus.action',
        data: data,
		dataType : "json",
		success : function(result) {
			if (result.success) {
                if (typeof callback == "function")
                    callback();
			}
		}
	});
}

function updateNavMsgNum (num) {
    var numJq = $("#message-new-count");
    var total = 0;
    if (num == undefined) {
        total = Number(numJq.text()) - 1;
    } else if (num >= 0) {
        total = num;
    } else {
        total = Number(numJq.text()) + num;
    }
    numJq.text(total);
    if (total > 0) {
        numJq.show();
    } else {
        numJq.hide();
    }
}

function checkNewMessage () {
    $.ajax({
		type : 'POST',
		url : DZF.contextPath + '/msg/pub_message!checkNewMessage.action',
		dataType : "json",
		success : function(result) {
			if (result.success) {
                updateNavMsgNum(result.rows);
			}
		}
	});
}

function getLatestMsg(type) {
    $(".message-content-body").html('');
    var isSys = type == 'admin' ? false: true;
    var url = DZF.contextPath + '/msg/pub_message!' + (type == 'admin' ? 'getAdminMsg' : 'getSysMsg') + '.action';
    var param = new Object();
    if (isSys)
        param.sreceive = sys_side;
    $.ajax({
        type : 'POST',
        url : url,
        data: param,
        dataType : "json",
        success : function(result) {
            if (result.success) {
                var msgs = result.rows;
                if (msgs.length > 0) {
                    $("#empty-type").hide();
                    for (var i = 0; i < msgs.length; i++) {
                        var msg = msgs[i];
                        var newClass = isSys || msg.isread == "是" ? "" : " message-item-new"
                        var str = '<div class="message-item' + newClass + '" data-info="' + msg.msgtype + ',' + msg.pk_id + '"><div class="message-item-title">'
                        + msg.msgtypename + '<span class="right">' + msg.vsdate + '</span></div>'
                        + '<div class="message-item-content" style="word-wrap:break-word;">' + msg.content + '</div></div>';
                        $(".message-content-body").append(str);
                    }
                } else {
                    $("#empty-type").show();
                }
            }
        }
    });
}

function bindEvent () {
    $(".message-popup-footer").on('click', "a", function(){
        addTab('消息', DZF.contextPath + '/msg/message.jsp');
        $("#messages").hide();
    });
    $(".dh-message").click(function (e) {
        e.stopPropagation();
        checkNewMessage();
        getLatestMsg('admin');
        $(".message-popup-nav .select").removeClass("select");
        $(".message-popup-nav .notice").addClass("select");
        if ($("#messages").is(":hidden"))
            $("#messages").show();
    });

    $(".message-popup-nav-btn").click(function () {
        if ($(this).hasClass("select"))
            return;
        if ($(this).hasClass("notice")) {
            getLatestMsg('admin');
        } else {
            getLatestMsg('sys');
        }
        $(".message-popup-nav .select").removeClass("select");
        $(this).addClass("select");
    });

    $("#messages").click(function (e) {
        e.stopPropagation();
    });
    $(".message-content-body").on('click', '.message-item-new', function () {
        var info = $(this).data("info").split(",");
        updateNavMsgNum();
        updateMsgStatus({
            msgtype: info[0],
            pk_id: info[1],
            isread: '是'
        }, function () {
            invokeFunByTabTitle("消息", "getMsgType");
        });
        $(this).removeClass("message-item-new");

    });
    $(document).on('click', function () {
        if ($("#messages").is(":visible"))
            $("#messages").hide();
    });
    $(".tabs-header").click(function (e) {
        if ($("#messages").is(":visible"))
            $("#messages").hide();
    });
}

