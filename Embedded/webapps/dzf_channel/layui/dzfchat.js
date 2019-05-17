var currentId = $("#loginid").val();
var currentName = $("#xingming").val();
var basePath = DZF.contextPath;
var socketPath = $("#socketPath").val();

var socket = socket || {};
if(window.WebSocket){
	connect_dzf();
	setInterval('send_heartbeat()', 60000);
}

function connect_dzf(){
//    socket = new WebSocket('ws://'+document.domain+':8282');
	socket = new WebSocket(encodeURI(Base64.decode(socketPath) + currentId + "/chatservlet.ws"));
    socket.onopen = function(){
        //socket.send(JSON.stringify({type: 'init'}));
    };
    socket.onmessage = function(e){
        var msg = JSON.parse(e.data);
        switch(msg.message_type) {
            case 'init':
//            	console.log(layim.cache()) ;
                initim(msg.history_message);
                return;
            case 'addList'://添加好友
//                if($('#layim-friend'+msg.data.id).length == 0 && userinfo['id'] != msg.data.id){
//                    return layui.layim.addList(msg.data);
//                }
//                $('#layim-friend'+msg.data.id+' img'). removeClass('gray_icon');
                return;
            case 'chatMessage':
                if(msg.to_id == layui.layim.cache().mine.id){
                    layui.layim.getMessage(msg);
                    change_online_status(msg.id, 'online');
                    //聊天图片
                    changelinephoto(true);
                }
                return;
            case 'logout':
            case 'hide':
            case 'online':
                var status = msg.message_type;
                change_online_status(msg.id, status);
                return;
        }
    }
}

function changelinephoto(flag){
	 var id = "#layui-layim-close";
	 var item = $(id) ;
	 if(flag){
		 item.find("img").attr('src',"./layui/lay/headphoto/hasmsg.png");
	 }else{
		 item.find("img").attr('src',"./layui/lay/headphoto/nomsg.png");
	 }
}


//发送心跳，防止链接长时间空闲被防火墙关闭
function send_heartbeat(){
    if(socket && socket.readyState == 1) {
        socket.send(JSON.stringify({message_type :'ping'}));
    }
}

//设置单个好友的上下线
var change_online_status = function (userId, status) {
    var id = "#layim-friend" + userId;
    var item = $(id) ;
    if (item == null) return;
    if (status === "online") {
    	item.find("img").removeClass('gray_icon');
    } else if (status === "hide") {
        item.find("img").addClass('gray_icon');
    }
    //更新在线人数
    var layimcount = item.parent().parent().find(".layim-count"),
    counts = layimcount.text().split("/"),
    offline = item.parent().find(".gray_icon").length,//离线人数;
    count = counts.length === 1 ? parseInt(counts[0]) : parseInt(counts[1]);//总人数
    layimcount.text(count - offline + "/" + count);
    
    //设置历史会话好友在线状态。
    var historyid = "#layim-history" + userId;
    if(status === "online"){
    	$(historyid).find("img").removeClass('gray_icon');
    }else{
    	$(historyid).find("img").addClass('gray_icon');
    }
    //设置已打开窗口的好友在线状态
	var list = ".layim-chatlist-friend"+userId;
	if(status === "online"){//在线
		//这个无法做到，这里先注销
		///$(??????).find(".layim-chat-other").find("img").removeClass('gray_icon');
		$("#layui-layim-chat").find(list).find("img").removeClass('gray_icon');
	}else{//不在线
		//这个无法做到，这里先注销
		///$(??????).find(".layim-chat-other").find("img").addClass('gray_icon');
		$("#layui-layim-chat").find(list).find("img").addClass('gray_icon');
	}
  
}

//function change_online_status(id, status){
//    if (status === 'hide' || status === 'logout') {
//        return $('#layim-friend'+id+' img').addClass('gray_icon');
//    }
//    $('#layim-friend'+id+' img').removeClass('gray_icon');
//}


function add_history_tip(){
    $('.layim-chat-main ul').append('<li><div class="history-tip">以上是历史消息</div></li>');
}


//初始化聊天窗口
function initim(history_message){
	layui.use('layim', function(layim){
	  //基础配置
	  layim.config({
	    //初始化接口
	    init: {
	    	url: ''
		    ,type: 'post' //默认get，一般可不填
	    }
	    //查看群员接口  ，注销则不显示
//  	,members: {
//  		url: ''
//  		,data: {}
//		}
	    ,uploadImage: {
	       url: basePath + '/chat/chat!uploadImage.action'
	      ,type: 'post' //默认post
	      //,data:{}
	    }
	    //,uploadFile: {
	    //  url: ''
	    //  ,type: 'post' //默认post
	    //}
	    //,skin: ['http://cdn.firstlinkapp.com/upload/2016_4/1461747766565_14690.jpg'] //增加皮肤
	    ,title: '业务沟通'
	    ,brief: false //是否简约模式（默认false，如果只用到在线客服，且不想显示主面板，可以设置 true）
		,maxLength: 2000 //最长发送的字符长度，默认2000，数据库是2000不要动了。
		,min: true
		,isfriend: true //是否开启好友（默认true，即开启）
		,isgroup: false //是否开启群组（默认true，即开启）
		,right: '0px' //默认0px，用于设定主面板右偏移量。该参数可避免遮盖你页面右下角已经的bar。
		,chatLog: './log.jsp' //聊天记录地址（如果未填则不显示）
		,copyright: true //是否授权，如果通过官网捐赠获得LayIM，此处可填true
	  });
	  //监听在线状态的切换事件
	 layim.on('online', function(status){
		 var message1 = {
			 id :layim.cache().mine.id
			 ,message_type: status
		 };
		 var message = JSON.stringify(message1); 
		 socket.send(message);
	 });
	 
	 //监听发送消息
	 layim.on('sendMessage', function(data){
	    if(data.mine.id != data.to.id ){
	    	socket.send(sendmessage(data));
	    }
	  });

	  //layim建立就绪
	  layim.on('ready', function(res){
		  // 加载离线消息
          for(var key in history_message){
        	  layim.getMessage(history_message[key]);
//              layim.getMessage(JSON.parse(history_message[key]));
              changelinephoto(true);
          }
          //设置所有的历史会话图像为灰色
          $("#layui-layim").find(".layim-list-history").find("img").addClass('gray_icon');

          // 将不在线的置为下线
          var friend_list = res.friend[0].list;
          for(var key in friend_list) {
              var user_id = friend_list[key].id;
              change_online_status(user_id, friend_list[key]['status']);
          }
          //上线提示
      	  var message1 = {
      		id :layim.cache().mine.id
      		,message_type: 'online'
      	  };
      	  var message = JSON.stringify(message1); 
      	  socket.send(message);
	  });
	  
	  //监听查看群员
	  layim.on('members', function(data){
	   // console.log(data);
	  });
	  
	  //监听聊天窗口的切换
	  layim.on('chatChange', function(data){
		  //聊天窗口是否在线操作
		  onChatChange(data);
		  changelinephoto(false);
	  });

	});
}

var sendmessage = function (data){
	var mine = data.mine;
	var to = data.to;
	var message1 = {}; 
	 $.extend(message1, {
		username: mine.username 
		,avatar:mine.avatar
		,id: mine.id
		,type:  to.type
		,source:to.source
		,content:mine.content
        ,message_type: 'chatMessage'
        ,to_id:to.id
        ,to_username:to.username
        ,to_avatar:to.avatar
    });  
	 console.info("发送方名称："+mine.username+",发送方头像："+mine.avatar+",发送方ID："+mine.id+","+to.type+","+to.source+","+mine.content
			 +",聊天人id："+to.id+","+to.username+","+to.avatar);
    var message = JSON.stringify(message1); 
    return message;
}

var onChatChange = function (data){
	//判断当前操作用户是否在线
	var userId = data.data.id;
	var id = "#layim-friend" + userId;
	var len = $(id).find(".gray_icon").length;
	var list = ".layim-chatlist-friend"+userId;
	var historyid = "#layim-history" + userId;
	if(len == 0){//在线
		$(data.elem).find(".layim-chat-other").find("img").removeClass('gray_icon');
		$("#layui-layim-chat").find(list).find("img").removeClass('gray_icon');
	}else{//不在线
		$(data.elem).find(".layim-chat-other").find("img").addClass('gray_icon');
		$("#layui-layim-chat").find(list).find("img").addClass('gray_icon');
	}
	//等待一秒种更新历史聊天窗口的是否在线状态
	setTimeout(function(){
		if(len == 0){//在线
	    	$(historyid).find("img").removeClass('gray_icon');
		}else{
			//延时一秒执行如下语句，因为打开聊天窗口，系统正在创建historyid.
	    	$(historyid).find("img").addClass('gray_icon');	
		}
	},1000);
}