
/**
 * 按钮点击事件
 * @param fun
 */
function click_icon(top, left, fun){
	if($('body').find("#livediv").length == 0){
		showPanel(top, left);
	}else{
		$("#livediv").remove();
		showPanel(top, left);
	}
	$(".add_price #cardPanel").scroll(function(){
		$("#livediv").remove();
	})
	$(".datagrid-body").scroll(function(){
		$("#livediv").remove();
	})
	//点击左右箭头
	$(".first_left").click(function(){
		var i = $(".first_year").text();
		i--;
		$(".first_year").text(i)
		$(".begin_date").val($(".first_year").text()+'-'+$(".first_body_month .blue").text().substring(0,2));
	});
	$(".two_left").click(function(){
		var i = $(".two_year").text();
		i--;
		$(".two_year").text(i)
		$(".begin_date").val($(".first_year").text()+'-'+$(".first_body_month .blue").text().substring(0,2));
	});
	$(".first_right").click(function(){
		var i = $(".first_year").text();
		i++;
		$(".first_year").text(i)
		$(".begin_date").val($(".first_year").text()+'-'+$(".first_body_month .blue").text().substring(0,2));
	});
	
	//获取点击的月份 提到上面的框里
	var span_s = $(".first_body_month span");
	span_s.click(function(){
		$(this).addClass("blue").siblings("span").removeClass("blue");
		$(".begin_date").val($(".first_year").text()+'-'+$(".first_body_month .blue").text().substring(0,2));
		$(".over_date").val($(".two_year").text()+'-'+$(".two_body_month .blue").text().substring(0,2));
		
	});
	
  
	var span_s2 = $(".two_body_month span");
	span_s2.click(function(){
		$(this).addClass("blue").siblings("span").removeClass("blue");
		$(".begin_date").val($(".first_year").text()+'-'+$(".first_body_month .blue").text().substring(0,2));
		  $(".over_date").val($(".two_year").text()+'-'+$(".two_body_month .blue").text().substring(0,2));
	});
	//点击确定界面消失  值进行保存
	$(".certain").click(function(){
		var begin_first = $(".begin_date").val();
		paper_size(begin_first, fun);
	});

	//点击取消 
	$(".cancels").click(function(){
		$("#livediv").remove();
	});

//    $(document).bind('click',function(){
//    	$("#livediv").remove();
//    });
//    $('#livediv').bind('click',function(e){
//        stopPropagation(e);
//    });
}

function stopPropagation(e) {
    if (e.stopPropagation) 
        e.stopPropagation();
    else 
        e.cancelBubble = true;
}

/**
 * 展示期间面板
 * @param top
 * @param left
 */
function showPanel(top,left){
	$('body').append(
			"<div id='livediv' data-options='modal:true' style='background:white;width:202px;height:190px;z-index:999999;position:absolute;top:"+top+"px;left:"+left+"px;border:1px solid #ccc;overflow:hidden;'>" +
				"<div class='date_title' style='width:394px;height:18px;font-size:14px;padding:6px 5px;background: #e9edf3;'>" +
					"<div style='float:left;'>" +
						"<span style='margin:0 6px;'></span>"+"<input class='begin_date' style='width:70px;height:18px;border-radius:5px;' type='text' value='2017-01' readonly>"+
					"</div>"+
					"<div style='float:left;'>" +
						"<p class='certain'>确定</p>"+"<p class='cancels'>取消</p>"+
					"</div>"+
				"</div>" +
				"<div class='date_impor' style='width:392px;height:156px;padding:0 5px 10px 5px;text-align:center;'>" +
					"<div class='first_body' style='float:left;margin-right:8px;width:190px;border:1px solid #b7b7b7;'>" +
						"<span class='first_left' style='width:32px;height:32px;background:#eaeaea;display:inline-block; line-height:32px;font-size:14px;color:#000'>&lt</span>" +
						"<span class='first_year' style='width:126px;height:32px;display:inline-block;line-height:32px'>2017</span>"+
						"<span style='width:32px;height:32px;background:#eaeaea;display:inline-block;line-height:32px;font-size:14px;color:#000' class='first_right'>&gt</span>"+
						"<div class='first_body_month'>" +
							"<span class='blue'>01月</span>" +"<span>02月</span>" +"<span>03月</span>" +"<span>04月</span>" +
							"<span>05月</span>" +"<span>06月</span>" +"<span>07月</span>" +"<span>08月</span>" +
							"<span>09月</span>" +"<span>10月</span>" +"<span>11月</span>" +"<span>12月</span>" +
						"</div>"+
					"</div>" +
				"</div>"+
			"</div>"+
		"</div>"); 
}

/**
 * 月份开始结束限制
 * @param begin
 * @param over
 * @param jsp
 * @param fun
 */
function paper_size(begin, fun){
	var years_size = begin.substr(0, begin.indexOf('-'));
	var month_size = begin.substr(begin.indexOf('-')+1);
	
	fun && fun(begin)
	$("#livediv").remove();
};