layui.use(['layim', 'laypage','laydate'], function (layim) {
    var laypage = layui.laypage, $ = layui.jquery,laydate = layui.laydate;
    function getPage(curr) {
        $.post(DZF.contextPath+"/chat/chat!gethistory.action",{touser:touserid,page:curr}, function (res) {
        	//var res = eval('('+res+')');
        	 var res = JSON.parse(res);
        	if(res.success){
        		var data = res.rows;
        		var html = '';
        		for(var key in data){
//                    var item = JSON.parse(data[key]);
        			var item = data[key];
                    var sendtime = laydate.now(item.timestamp, "YYYY-MM-DD hh:mm:ss");
                    html += '<li><div class="layim-chat-user"><img src="'+item.avatar+'"><cite>'+item.username+'<i>'+sendtime+'</i></cite></div><div class="layim-chat-text">'+layim.content(item.content)+'</div></li>';                			
        		}
//        		$(".layim-chat-main ul").append(html);
        		$(".layim-chat-main ul").empty().html(html);
                laypage({
                    cont: 'page', //容器。值支持id名、原生dom对象，
                    pages: res.head, //总页数
                    first: false,
                    last: false,
                    curr: curr || 1, //当前页
                    jump: function (e, first) { //触发分页后的回调
                        if (!first) { //点击跳页触发函数自身
                            getPage(e.curr);
                        };
                    }
                });
        	}
        });
    }
    getPage(1);

});