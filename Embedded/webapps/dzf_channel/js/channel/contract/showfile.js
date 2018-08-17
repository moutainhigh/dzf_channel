//附件items
var arrachrows = null;
/**
 * 列表界面查看附件
 */
function viewattach(infoindex){
//	var rows = $('#grid').datagrid('getSelections');
//	if (rows == null) {
//		$.messager.alert("提示", "请选择一行数据进行查看"); 
//		return;
//	}else if(rows && rows.length > 1){
//		$.messager.alert("提示", "请选择一行数据进行查看"); 
//		return;
//	}
	var row = $('#grid').datagrid('getData').rows[infoindex];
	if (row == null) {
		Public.tips({content:'请您先选择一行',type:2});
		return;
	}
	
	var para = {};
	para.corp_id = row.corpid;
	para.c_id = row.contractid;
	
	$.ajax({
		type : "POST",
		url : DZF.contextPath + "/contract/contractconf!getAttaches.action",
  		dataType : 'json',
  		data : para,
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var rows = result.rows;
			arrachrows = result.rows;
			$("#attachs").html('');
			for(var i = 0;i<rows.length;i++){
				var srcpath = rows[i].fpath.replace(/\\/g, "/");
				var attachImgUrl = getAttachImgUrl(rows[i]);
				$('<li><a href="javascript:void(0)"  onmouseover="showTips(' + i + ')"  '+
						'onmouseout="hideTips(' + i + ')"  ondblclick="doubleImage(\'' + i + '\');" ><span><img src="' +attachImgUrl +  '" />'+
						'<div id="reUpload' + i +'" style="width: 60%; height: 25px; position: absolute; top: 105px; left: 0px; display:none;">'+
						'<h4><span id="tips'+ i +'"></span></h4></div></span>'+
						'<font>' + 	rows[i].doc_name + '</font></a></li>').appendTo($("#attachs"));
			}
		}
	});
	
	$("#attachViewDlg").dialog({
		width:$(window).width()-200,
		height:$(window).height()-100,
		closable:true,
		title:'附件浏览',
		modal:true
	});	
	
	$("#attachViewDlg").css("display","block");
	$("#attachViewDlg").dialog("center");
}
/**
 * 详细界面查看附件
 */
function viewAttachCard(){
	var corpid = $('#pk_corp').val();
	var contractid = $('#contractid').val();
	if (corpid == null) {
		$.messager.alert("提示", "请选择数据进行查看"); 
		return;
	}
	var para = {};
	para.corp_id = corpid;
	para.c_id = contractid;
	
	$.ajax({
		type : "POST",
		url : DZF.contextPath + "/contract/contractconf!getAttaches.action",
  		dataType : 'json',
  		data : para,
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var rows = result.rows;
			arrachrows = result.rows;
			$("#attachs").html('');
			for(var i = 0;i<rows.length;i++){
				var srcpath = rows[i].fpath.replace(/\\/g, "/");
				var attachImgUrl = getAttachImgUrl(rows[i]);
				$('<li><a href="javascript:void(0)" onmouseover="showTips(' + i + ')" onmouseout="hideTips(' + i + ')" '+
						'ondblclick="doubleImage(\'' + i + '\');" ><span><img src="' + attachImgUrl +  '" />'+ '<div id="reUpload' + i +
						'" style="width: 100%; height: 25px; position: absolute; top: 105px; left: 0px; display:none;">'
						+'<h4><span id="tips'+i	+'"></span></h4></div></span>'+
						'<font>' + rows[i].doc_name + '</font></a></li>').appendTo($("#attachs"));
			}
		}
	});
	
	$("#attachViewDlg").dialog({
		width:$(window).width()-200,
		height:$(window).height()-100,
		closable:true,
		title:'附件浏览',
		modal:true
	});	
	
	$("#attachViewDlg").css("display","block");
	$("#attachViewDlg").dialog("center");
}

/**
 * 获取附件显示url
 * @param attach
 * @returns {String}
 */
function getAttachImgUrl(attach){
	
	var ext = getFileExt(attach['doc_name']);
	if("pdf"==ext.toLowerCase()){		
		return "../../images/typeicon/pdf.jpg";
	}else if("txt"==ext.toLowerCase()){		
		return "../../images/typeicon/txt.jpg";
	}
	
	return DZF.contextPath + '/contract/contractconf!getAttachImage.action?doc_id=' + attach.doc_id 
		+ '&corp_id=' + attach.corp_id ;	
}


/**
 * 显示附件上的提示
 * @param i
 */
function showTips(i){

	var div = "#reUpload"+i;
	$(div).css("display","block");
	
	var tips = getTipContents(i);
	var tipkey = "#tips"+ i;
	$(tipkey).html(tips);	
}

/**
 * 隐藏提示
 * @param i
 */
function hideTips(i){
	var div = "#reUpload"+i;
	$(div).css("display","none");	
}

/**
 * 获取提示内容
 * @param i
 * @returns {String}
 */
function getTipContents(i) {

	var tips = "";
	if (arrachrows && arrachrows[i]) {
		var ext = getFileExt(arrachrows[i]['doc_name']);
		if ("png" == ext.toLowerCase() || "jpg" == ext.toLowerCase()
				|| "jpeg" == ext.toLowerCase() || "bmp" == ext.toLowerCase()
				|| "gif" == ext.toLowerCase()) {
			tips = "双击查看原图";
		} else if ("pdf" == ext.toLowerCase()) {
			tips = "双击预览";
		} /*else {
			tips = "双击下载";
		}*/
	}

	return tips;
}

/**
 * 双击显示大图
 * @param i
 */
function doubleImage(i){
	var ext = getFileExt(arrachrows[i]['doc_name']);
	var src = DZF.contextPath + "/contract/contractconf!getAttachImage.action?doc_id=" +
		arrachrows[i].doc_id + "&corp_id=" + arrachrows[i].corp_id;
	if("png"==ext.toLowerCase()||"jpg"==ext.toLowerCase()
			||"jpeg"==ext.toLowerCase()||"bmp"==ext.toLowerCase()){
		$("#tpfd").empty();
		var offset = $("#tpght").offset();
		var img = '<img id="conturnid" alt="无法显示图片" src="' + src 
		+ '" style="position: absolute;z-index: 1;left:50px;top:50px;">';
//		parent.openFullViewDlg('<div style="text-align: center;padding-top:10px;"> '+
//								'<a class="ui-btn ui-btn-xz" onclick="tranImg(-90)">左转</a> '+
//								'<a class="ui-btn ui-btn-xz" onclick="tranImg(90)">右转</a></div>'+
//								'<div id="fullViewContent" '+
//								'style="overflow: auto;width: 1200px; height:460px;text-align: center;padding-top:60px; margin: 0 auto;position:relative;">'+
//								'<img id="conturnid" alt="无法显示图片" src="' + src 
//								+ '" style="position: absolute;z-index: 1;left:50px;top:50px;">'+
//								' </div>','原图')
		parent.openFullViewDlg(img, '原图');
	}

}

//function doubleImage(i){
//	var ext = getFileExt(arrachrows[i]['doc_name']);
//	var src = DZF.contextPath + "/contract/contractconf!getAttachImage.action?doc_id=" +
//		arrachrows[i].doc_id + "&corp_id=" + arrachrows[i].corp_id;
//	if("png"==ext.toLowerCase()||"jpg"==ext.toLowerCase()
//			||"jpeg"==ext.toLowerCase()||"bmp"==ext.toLowerCase()){
//		$("#tpfd").empty();
//		var offset = $("#tpght").offset();
////		parent.openFullViewDlg('<img id="conturnid" alt="无法显示图片" src="' + src 
////		+ '" style="position: absolute;z-index: 1;left:50px;top:50px;">','原图')
//		
//		var img = "<img id='fullViewDlg' src=" + src + " style='position: absolute;z-index: 1;left:50px;top:50px;' />";  
//		layer.open({  
//		    type: 1,  
//		    shade: false,  
//		    title: '图片', //不显示标题  
//		    area:['auto','auto'],  
//		    area: ['99%','90%'],  
//		    content: img + "<div id='fullViewContent' style='text-align: center;padding-top:10px;'><a class='ui-btn ui-btn-xz' onclick='tranImg(-90)'>左转</a><a class='ui-btn ui-btn-xz' onclick='tranImg(90)'>右转</a></div>", 
//		    maxmin: true,
//		    shadeClose: true,
//		    cancel: function () {  
//		        //layer.msg('图片查看结束！', { time: 5000, icon: 6 });  
//		    }  
//		});  
//	}
//
//}



/**
 * 获取附件扩展
 * @param filename
 * @returns
 */
function getFileExt(filename){
	
	var index1=filename.lastIndexOf(".")+1;
	var index2=filename.length;
	var ext=filename.substring(index1,index2);
	
	return ext;
}

/**
 * 设置快捷键
 */
$(document).keydown(function(e) {
	//ESC 关闭附件预览框
	if (e.keyCode == 27) {
		parent.closeFullViewDlg();
	}
});
