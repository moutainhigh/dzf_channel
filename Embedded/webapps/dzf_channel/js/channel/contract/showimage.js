
var arrachrows = null;
var flowImgUrls = null;

/**
 * 显示变更附件图片
 */
function showChangeImage(row){
	
		$("#fileshow").show();
		$("#filedocs").html('');
		arrachrows = new Array();
		flowImgUrls = new Array();
		var srcpath = row.fpath.replace(/\\/g, "/");
		var src = getAttachImgUrl(row);
		arrachrows[0] = row;
		var src = DZF.contextPath + '/contract/contractaudit!getAttachImage.action?applyid='
				+ row.applyid ;
		
		var img = '<img id="conturnid" alt="无法显示图片" src="' + src 
			+ '" style="position: absolute;z-index: 1;left:50px;">';
		flowImgUrls[0] = img;
		
		$('<li><a href="javascript:void(0)"  onmouseover="showTips(0)"  '+
				'onmouseout="hideTips(0)"  ondblclick="doubleImage(0);" >' + 
				'<span><img src="' +src +  '" />'+
				'<div id="reUpload0' +
				'" style="width: 60%; height: 25px; position: absolute; top: 105px; left: 0px; display:none;">'+
				'<h4><span id="tips0"></span></h4></div></span>'+
				'<font>' + 	row.doc_name + '</font></a></li>').appendTo($("#filedocs"));
}

/**
 * 获取附件显示url
 * @param attach
 * @returns {String}
 */
function getAttachImgUrl(row) {
	var ext = getFileExt(row['doc_name']);
	if ("pdf" == ext.toLowerCase()) {
		return "../../images/typeicon/pdf.jpg";
	} else if ("txt" == ext.toLowerCase()) {
		return "../../images/typeicon/txt.jpg";
	}
	return DZF.contextPath + '/contract/contractaudit!getAttachImage.action?applyid='
			+ row.applyid ;
}

/**
 * 获取附件扩展
 * @param filename
 * @returns
 */
function getFileExt(filename) {
	var index1 = filename.lastIndexOf(".") + 1;
	var index2 = filename.length;
	var ext = filename.substring(index1, index2);
	return ext;
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
		}
	}
	return tips;
}

/**
 * 双击显示大图
 * @param i
 */
function doubleImage(i) {
	var ext = getFileExt(arrachrows[i]['doc_name']);
	var src = DZF.contextPath
			+ '/contract/contractaudit!getAttachImage.action?applyid='
			+ arrachrows[i].applyid ;
	if ("png" == ext.toLowerCase() || "jpg" == ext.toLowerCase()
			|| "jpeg" == ext.toLowerCase() || "bmp" == ext.toLowerCase()) {
		$("#tpfd").empty();
		var offset = $("#tpght").offset();
		var img = '<img id="conturnid" alt="无法显示图片" src="' + src
				+ '" style="position: absolute;z-index: 1;left:50px;">';
		parent.openFullViewDlg(img, '原图', null, null, i, flowImgUrls);
	}
}
