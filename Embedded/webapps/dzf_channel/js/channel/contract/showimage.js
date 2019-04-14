
var arrachrows = null;
var flowImgUrls = null;

/**
 * 展示变更附件图片
 * @param row
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
 * 展示变更详情附件图片
 * @param row
 */
function showChangeDetImage(row){
	$("#ifileshow").show();
	$("#ifiledocs").html('');
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
			'<font>' + 	row.doc_name + '</font></a></li>').appendTo($("#ifiledocs"));
}

/**
 * 展示非常规套餐图片
 * @param row
 */
function showAuditImage(row){
	var para = {};
	para.corp_id = row.corpid;
	para.c_id = row.conid;
	
	$.ajax({
		type : "POST",
		url : DZF.contextPath + "/contract/contractconf!getAttaches.action",
  		dataType : 'json',
  		data : para,
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var rows = result.rows;
			if(rows != null && rows.length > 0){
				$("#afileshow").show();
				arrachrows = result.rows;
				$("#afiledocs").html('');
				flowImgUrls = new Array();
				for(var i = 0;i<rows.length;i++){
					var srcpath = rows[i].fpath.replace(/\\/g, "/");
					var attachImgUrl = getAuditImgUrl(rows[i]);
					$('<li><a href="javascript:void(0)"  onmouseover="showTips(' + i + ')"  '+
							'onmouseout="hideTips(' + i + ')"  ondblclick="doubleAuditImage(\'' + i + 
							'\');" ><span><img src="' +attachImgUrl +  '" />'+
							'<div id="reUpload' + i +
							'" style="width: 60%; height: 25px; position: absolute; top: 105px; left: 0px; display:none;">'+
							'<h4><span id="tips'+ i +'"></span></h4></div></span>'+
							'<font>' + 	rows[i].doc_name + '</font></a></li>').appendTo($("#afiledocs"));
					
					var src = DZF.contextPath + "/contract/contractconf!getAttachImage.action?doc_id=" +
					rows[i].doc_id + "&corp_id=" + rows[i].corp_id;
					var img = '<img id="conturnid" alt="无法显示图片" src="' + src 
					+ '" style="position: absolute;z-index: 1;left:50px;">';
					flowImgUrls[i] = img;
				}
			}
		}
	});
}

/**
 * 展示非常规套餐详情图片
 * @param row
 */
function showAuditDetImage(row){
	var para = {};
	para.corp_id = row.corpid;
	para.c_id = row.conid;
	
	$.ajax({
		type : "POST",
		url : DZF.contextPath + "/contract/contractconf!getAttaches.action",
  		dataType : 'json',
  		data : para,
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var rows = result.rows;
			if(rows != null && rows.length > 0){
				$("#iafileshow").show();
				arrachrows = result.rows;
				$("#iafiledocs").html('');
				flowImgUrls = new Array();
				for(var i = 0;i<rows.length;i++){
					var srcpath = rows[i].fpath.replace(/\\/g, "/");
					var attachImgUrl = getAuditImgUrl(rows[i]);
					$('<li><a href="javascript:void(0)"  onmouseover="showTips(' + i + ')"  '+
							'onmouseout="hideTips(' + i + ')"  ondblclick="doubleAuditImage(\'' + i + 
							'\');" ><span><img src="' +attachImgUrl +  '" />'+
							'<div id="reUpload' + i +
							'" style="width: 60%; height: 25px; position: absolute; top: 105px; left: 0px; display:none;">'+
							'<h4><span id="tips'+ i +'"></span></h4></div></span>'+
							'<font>' + 	rows[i].doc_name + '</font></a></li>').appendTo($("#iafiledocs"));
					
					var src = DZF.contextPath + "/contract/contractconf!getAttachImage.action?doc_id=" +
					rows[i].doc_id + "&corp_id=" + rows[i].corp_id;
					var img = '<img id="conturnid" alt="无法显示图片" src="' + src 
					+ '" style="position: absolute;z-index: 1;left:50px;">';
					flowImgUrls[i] = img;
				}
			}
		}
	});
}

/**
 * 获取附件显示url
 * @param attach
 * @returns {String}
 */
function getAuditImgUrl(attach) {
	var ext = getFileExt(attach['doc_name']);
	if ("pdf" == ext.toLowerCase()) {
		return "../../images/typeicon/pdf.jpg";
	} else if ("txt" == ext.toLowerCase()) {
		return "../../images/typeicon/txt.jpg";
	}
	return DZF.contextPath
			+ '/contract/contractconf!getAttachImage.action?doc_id='
			+ attach.doc_id + '&corp_id=' + attach.corp_id;
}

/**
 * 双击显示大图
 * @param i
 */
function doubleAuditImage(i) {
	var ext = getFileExt(arrachrows[i]['doc_name']);
	var src = DZF.contextPath
			+ "/contract/contractconf!getAttachImage.action?doc_id="
			+ arrachrows[i].doc_id + "&corp_id=" + arrachrows[i].corp_id;
	if ("png" == ext.toLowerCase() || "jpg" == ext.toLowerCase()
			|| "jpeg" == ext.toLowerCase() || "bmp" == ext.toLowerCase()) {
		$("#tpfd").empty();
		var offset = $("#tpght").offset();
		var img = '<img id="conturnid" alt="无法显示图片" src="' + src
				+ '" style="position: absolute;z-index: 1;left:50px;">';
		parent.openFullViewDlg(img, '原图', null, null, i, flowImgUrls);
	}
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
