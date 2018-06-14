var contextPath = DZF.contextPath;
var status="brows";
var qaid;

$(window).resize(function() {
	$('#chn_grid').datagrid('resize', {
		height : Public.setGrid().h,
		width : 'auto',
	});
});

$(function() {
	load();
	initFileEvent();
	initRef();
	reloadData();
	fastQry();
});

function load() {
	$('#chn_grid').datagrid({
		fit : false,
		rownumbers : true,
		height : Public.setGrid().h,
		width:'100%',
		singleSelect : false,
		pageNumber : 1,
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		pagination : true,
		columns : [ [ 
		    {field : 'ck',checkbox : true}, 
			{width : '140',title : '大区',field : 'aname'}, 
			{width : '140',title : '地区',field : 'provname'}, 
			{width : '240',title : '加盟商',field : 'corpnm'}, 
			{width : '100',title : '付款日期',field : 'dpdate',align:'center'}, 
		  	{width : '200',title : '单据号',field : 'vcode',},  
	  	  	{width : '80',title : '付款类型',field : 'iptype',
		  		formatter : function(value) {
				if (value == '1')
					return '保证金'; 
				if (value == '2')
					return '预付款';
			}},
		  	{width : '120',title : '付款金额',field : 'npmny',align:'right',
				formatter : function(value, row, index) {
				if (value == 0)
					return "0.00";
				return formatMny(value);
			}}, 
		  	{width : '80',title : '支付方式',field : 'ipmode',
		  		formatter : function(value) {
				if (value == '1')
					return '银行转账'; 
				if (value == '2')
					return '支付宝';
				if (value == '3')
					return '微信';
				if (value == '4')
					return '其他';
			}},
		    {width : '50',title : '附件',field : 'fj',align:'center',
				formatter: function(value,row,index){
					if(!isEmpty(row.billid)){
						return '<a href="javascript:void(0)"  style="color:blue" onclick="showImage(\''+row.billid+'\')" >' + "附件"+ '</a>';
					}
		    }},
			{width : '80',title : '单据状态',field : 'status',align:'center',
		  		formatter : function(value) {
				if (value == '1')
					return '待提交'; 
				if (value == '2')
					return '待审批';
				if (value == '3')
					return '已确认';
				if (value == '4')
					return '已驳回';
				if (value == '5')
					return '待确认';
			}},
			{width : '140',title : '驳回说明', halign:'center',field : 'vreason',formatter : showTitle},
		  	{width : '120',title : '付款人',field : 'vhname'}, 
		  	{width : '300',title : '备注',field : 'memo',formatter : showTitle},
		  	{width : '140',title : '提交人',field : 'subname'},
		  	{width : '140',title : '提交时间',field : 'subtime'},
		  	{title : '主键id',field : 'billid',hidden : true}, 
		  	{title : '来源类型',field : 'stype',hidden : true}, 
		  	{title : 'tstp',field : 'tstp',hidden : true}, 
			]],
		onLoadSuccess : function(data) {
		}
	});
}

/**
 * 清除查询框
 */
function clearParams(){
	$("#qcorpid").val(null);
	$("#qcorpnm").textbox("setValue",null);
	$('#aname').combobox('setValue', null);
	$('#qstatus').combobox('setValue', '-1');
	$('#qiptype').combobox('setValue', '-1');
	$('#qipmode').combobox('setValue', '-1');
}

/**
 * 查询
 */
function reloadData(){
	var bdate = $('#qddate').datebox('getValue'); //开始日期
	var edate = $('#qdpdate').datebox('getValue'); //结束日期
	var queryParams = $('#chn_grid').datagrid('options').queryParams;
	queryParams.status = $('#qstatus').combobox('getValue');
	queryParams.ddate = bdate;
	queryParams.dpdate = edate;
	queryParams.iptype = $('#qiptype').combobox('getValue');
	queryParams.ipmode = $('#qipmode').combobox('getValue');
	queryParams.corpid = $("#qcorpid").val();
	queryParams.aname = $("#aname").combobox('getValue');
	queryParams.corpnm = null;
	$('#chn_grid').datagrid('unselectAll');
	$('#chn_grid').datagrid('options').url =DZF.contextPath + '/chnpay/chnpaybill!query.action';
	$('#chn_grid').datagrid('options').queryParams = queryParams;
	$('#chn_grid').datagrid('reload');
	$('#querydate').html(bdate + ' 至 ' + edate);
    $('#qrydialog').hide();
}


function loadData(iptype){
	$('#chn_grid').datagrid('unselectAll');
	var queryParams =new Object();
	queryParams['iptype'] = iptype;
	$('#chn_grid').datagrid('options').url =contextPath + '/chnpay/chnpaybill!query.action';
	$('#chn_grid').datagrid('options').queryParams = queryParams;
	$('#chn_grid').datagrid('reload');
}

function initRef(){
	$("#qddate").datebox("setValue", parent.SYSTEM.PreDate);
	$("#qdpdate").datebox("setValue",parent.SYSTEM.LoginDate);
	initAcorp();
	initQcorp();
	initArea();
}

/**
 * 快速过滤
 */
function fastQry(){
	$('#filter_value').textbox('textbox').keydown(function (e) {
		 if (e.keyCode == 13) {
            var filtername = $("#filter_value").val(); 
            if(!isEmpty(filtername)){
            	var queryParams = $('#chn_grid').datagrid('options').queryParams;
            	queryParams.corpnm = filtername;
            	$('#chn_grid').datagrid('options').url = contextPath + '/chnpay/chnpaybill!query.action';
          		$('#chn_grid').datagrid('options').queryParams = queryParams;
          		$('#chn_grid').datagrid('reload');
            }else{
            	reloadData();
            }
         }
   });
}

/**
 * 关闭查询框
 */
function closeCx(){
	$("#qrydialog").hide();
}

function initArea(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryArea.action',
		data : {"qtype" :3},
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
			    $('#aname').combobox('loadData',result.rows);
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
}

/**
 * 新增-加盟商参照初始化
 */
function initAcorp(){
	$('#corpnm').textbox({
		onClickIcon : function() {
			qaid = $(this).attr("id");
		},
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#chnDlg").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择加盟商',
                    modal: true,
                    href: DZF.contextPath + '/ref/channel_select.jsp',
                    queryParams : {
    					issingle : "true",
    					ovince :"-1"
    				},
                    buttons: '#chnBtn'
                });
            }
        }]
    });
}

/**
 * 查询-加盟商参照初始化
 */
function initQcorp(){
	$('#qcorpnm').textbox({
		onClickIcon : function() {
			qaid = $(this).attr("id");
		},
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#chnDlg").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择加盟商',
                    modal: true,
                    href: DZF.contextPath + '/ref/channel_select.jsp',
                    queryParams : {
    					issingle : "false",
    					ovince :"-1"
    				},
                    buttons: '#chnBtn'
                });
            }
        }]
    });
}


/**
 * 加盟商选择事件
 */
function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}

/**
 * 双击选择加盟商
 * @param rowTable
 */
function dClickCompany(rowTable){
	var str = "";
	var corpIds = [];
	if(rowTable){
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个客户",
				type : 2
			});
			return;
		}
		for(var i=0;i<rowTable.length;i++){
			if(i == rowTable.length - 1){
				str += rowTable[i].uname;
			}else{
				str += rowTable[i].uname+",";
			}
			corpIds.push(rowTable[i].pk_gs);
		}
		if(qaid=="qcorpnm"){
			$("#qcorpnm").textbox("setValue",str);
			$("#qcorpid").val(corpIds);
		}else{
			$("#ovince").val(rowTable[0].ovince);
			$("#corpnm").textbox("setValue",str);
			$("#corpid").val(rowTable[0].pk_gs);
		}
	}
	$("#chnDlg").dialog('close');

}


function showTitle(value){
	if(value!=undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}else{
		return;
	}
}

function add(){
	//设置dig属性
    $('#htDialog').dialog({modal:true});
    $('#htDialog').dialog('open').dialog('center').dialog('setTitle','付款单');
    setItemEdit(false);
    status="add";
    $('#chn_add').form("clear");
	$('#dpdate').datebox("setValue",parent.SYSTEM.LoginDate);
	$('.filepath2').prop('disabled',false);//img21 img22 
	$("#img12").attr("src","");
	$("#span1").attr("data-id","");
	$("#img11").show();
    $("#img12").hide();
};

//修改
function edit(){
	status="edit";
	setItemEdit(false);
	var rows = $('#chn_grid').datagrid('getSelections');
	if (rows == null||rows.length==0) {
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});			
		return;
	}
	if(rows.length > 1){
		Public.tips({content : "请选择一行数据",type : 2});
		return;
	}
	var row = $('#chn_grid').datagrid('getSelected');
	if (row.status!=1 &&row.status!=4) {
		Public.tips({content : '只有待提交和已驳回的能修改',type : 2});
		return;
	}
	if (row.stype!=2) {
		Public.tips({content : '客户的单子不能修改',type : 2});
		return;
	}
	$('#chn_add').form("clear");
	$('#htDialog').dialog({modal:true});
	$('#htDialog').dialog('open').dialog('center').dialog('setTitle','付款单');
	var pk = $('#chn_grid').datagrid('getSelected').billid;
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url : contextPath + '/chnpay/chnpaybill!queryByID.action',
		data : {
			"billid" : pk,
		},
		success : function(data, textStatus) {
			if (!data.success) {
				Public.tips({
					content :  data.msg,
					type : 1
				});	
			} else {
				$('#chn_add').form('load', data.rows);
			}
		},
	});
	
	viewImageFile(row);
	$('#card_fm').form('load', row);
	$('.filepath2').prop('disabled',false);
	initIdDelClick();
}

//删除
function del(){
	var rows = $('#chn_grid').datagrid('getSelections');
	if (rows == null||rows.length<1) {
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});
		return;
	}
	var chns = '';
	var item;
	var indexs = new Array();
	var index;
	for(var i=0; i<rows.length;i++){
		item = rows[i];
		index = $('#chn_grid').datagrid('getRowIndex',item);
		indexs.push(index);
		chns = chns + JSON.stringify(item);
	}
	var postdata = new Object();
	postdata["chns"] = chns;
	postdata["stat"] = -1;
	$.messager.confirm("提示", "你确定删除吗？", function(flag) {
		if (flag) {
			$.ajax({
				type : "post",
				dataType : "json",
				url : contextPath + '/chnpay/chnpaybill!updateStatus.action',
				data : postdata,
				traditional : true,
				async : false,
				success : function(data, textStatus) {// result
					if (!data.success) {
						Public.tips({
							content : data.msg,
							type : 1
						});
					} else {
						reloadData();
						$('#chn_grid').datagrid('clearSelections');
						if(data.status == -1){
							Public.tips({
								content : data.msg,
							});
						}else{
							Public.tips({
								content : data.msg,
								type : 2
							});
						}
					}
				},
			});
		} else {
			return null;
		}
	});
}

/**
 *  保存（新增和修改）
 */
function save(){
	//校验
	var flag = $('#chn_add').form('validate');
	if(flag == false){
		Public.tips({content:"必输信息为空或格式不正确",type:2});
		return;
	}
	var reg = new RegExp("[\\u4E00-\\u9FFF]+","g");
	if(reg.test($('#vcode').textbox('getValue'))){ 
		Public.tips({content:"单据号不能包含汉字",type:2});
		$('#vcode').textbox('setValue',"");
		return;
	}
	parent.$.messager.progress({
		text : '保存中....'
	});
	$('#chn_add').form('submit', {
		url : contextPath + '/chnpay/chnpaybill!save.action',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				$('#htDialog').dialog('close');
				Public.tips({
					content : result.msg,
					type : 0
				});
				var rowg=$("#chn_grid").datagrid('getSelected');
				var rowsa=$('#chn_grid').datagrid('getRows');
				var ia=$('#chn_grid').datagrid('getRowIndex',$('#chn_grid').datagrid('getSelected'));
				var rowr=result.rows[0];
				if(rowg&&rowg.billid==rowr.billid){//修改
					$("#chn_grid").datagrid("loadData",{ "total":rowsa.length,rows:rowsa });
					$('#chn_grid').datagrid('updateRow', {
            			index : ia,
            			row :rowr
            		});
				}else{//新增
					$('#chn_grid').datagrid('insertRow',{index:0,row:rowr});
					$("#chn_grid").datagrid("loadData",{ "total":rowsa.length,rows:rowsa });
				}
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
			parent.$.messager.progress('close');
		}
	});
}

function commit() {
	var rows = $('#chn_grid').datagrid('getSelections');
	if(rows.length <= 0){
		Public.tips({
			content : '请至少选择一行数据',
			type : 2
		});
		return;
	}
	var chns = '';
	var item;
	var indexs = new Array();
	var index;
	for(var i=0; i<rows.length;i++){
		item = rows[i];
		index = $('#chn_grid').datagrid('getRowIndex',item);
		indexs.push(index);
		chns = chns + JSON.stringify(item);
		
	}
	var postdata = new Object();
	postdata["chns"] = chns;
	postdata["stat"] = 2;
	updateStatusMutl(postdata,indexs);
}

function uncommit() {
	var rows = $('#chn_grid').datagrid('getSelections');
	if(rows.length <= 0){
		Public.tips({
			content : '请至少选择一行数据',
			type : 2
		});
		return;
	}
	var chns = '';
	var item;
	var indexs = new Array();
	var index;
	for(var i=0; i<rows.length;i++){
		item = rows[i];
		index = $('#chn_grid').datagrid('getRowIndex',item);
		indexs.push(index);
		chns = chns + JSON.stringify(item);
	}
	var postdata = new Object();
	postdata["chns"] = chns;
	postdata["stat"] = 1;
	updateStatusMutl(postdata,indexs);
}

function updateStatusMutl(data,indexs) {
	$.ajax({
		type : 'POST',
		async : false, 
		url : contextPath + '/chnpay/chnpaybill!updateStatus.action',
		data : data,
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				for(var i in indexs){
					$('#chn_grid').datagrid('updateRow',{index : indexs[i] , row : {status : data.status}});
				}
				if(result.status == -1){
					Public.tips({
						content : result.msg,
					});
				}else{
					Public.tips({
						content : result.msg,
						type : 2
					});
				}
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
				return;
			}
			$('#chn_grid').datagrid('reload'); 
			$("#chn_grid").datagrid('uncheckAll');
		}
	});
}


function cancel(){
	if(status == "add" || status == "edit"){
		$.messager.confirm("提示", "确定取消吗？", function(flag) {
			if (flag) {
				$('#htDialog').dialog('close');
			} else {
				return null;
			}
		});
	}else{
		$('#htDialog').dialog('close');
	}
}

/**
 * 字段编辑
 */
function setItemEdit(isEdit){
	$('#vcode').textbox('readonly',isEdit);
	$('#dpdate').datebox('readonly',isEdit);
	$('#vhname').textbox('readonly',isEdit);
	$('#ipmode').combobox('readonly',isEdit);
	$('#npmny').numberbox('readonly',isEdit);
	$('#iptype').combobox('readonly',isEdit);
	$('#memo').textbox("readonly",isEdit);
	var info = document.getElementsByClassName('filepath2');
	if(isEdit){
		info[0].setAttribute('type','hidden');
	}else{
		info[0].setAttribute('type','file');
	}
}


function initFileEvent(){
	$(".uploadImg").on("change",".filepath2",function(){//上传执照
		if(this.files[0]){
			var fname = this.files[0].name;
			var imageExt=fname.substr(fname.lastIndexOf(".")).toLowerCase();//获得文件后缀名
		    if(imageExt !='.jpg' && imageExt !='.png' && imageExt !='.jpeg'){
		        Public.tips({ content : "请上传后缀名为jpg、png、jpeg的图片", type : 2 });
		        return false;
		    }
			var srcs = getObjectURL(this.files[0]);   //获取路径
			$(this).nextAll(".img11").hide();   //this指的是input
			$(this).nextAll(".img22").show();  //fireBUg查看第二次换图片不起做用
			$(this).nextAll('.Dlelete').show();   //this指的是input
			$(this).nextAll(".img22").attr("src",srcs);    //this指的是input
			initIdDelClick();
			var data_id = $(this).nextAll('.Dlelete').attr("data-id");
			if(data_id){
				deleteImageFile(data_id)
			}
		}
	});
}

function getObjectURL(file) {
    var url = null;
	if (window.createObjectURL != undefined) {
		url = window.createObjectURL(file)
	} else if (window.URL != undefined) {
		url = window.URL.createObjectURL(file)
	} else if (window.webkitURL != undefined) {
		url = window.webkitURL.createObjectURL(file)
	}
    return url
};


function initIdDelClick(){
    $(".Dlelete").on("click",function() {
    	if(status == "brows"){
    		return;
    	}
    	var data_id = $(this).attr("data-id");
    	if(data_id){
    		deleteImageFile(data_id);
    	}
        $(this).hide();     //this指的是span
        $(this).nextAll(".img22").hide();
        $(this).nextAll(".img11").show();
        $(this).nextAll(".img22").attr("src","")
        $(this).prev().val('');
    });
}

function deleteImageFile(data_id){
	$.ajax({
		url : contextPath + "/chnpay/chnpaybill!deleteImageFile.action",
		traditional : true,
		async : false,
		dataType : 'json',
		data : {
			"billid" : data_id,
		},
	});
}

function viewImageFile(row){
	var para = {};
	para.billid = row.billid;
	$.ajax({
		type : "POST",
		url : contextPath + "/chnpay/chnpaybill!queryByID.action",
  		dataType : 'json',
  		data : para,
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var row= result.rows;
			arrachrow= result.rows;
			$("#img12").attr("src",'');
			$("#span1").attr("data-id",'');
			$("#img11").show();
			if(row.fpath){
				var srcpath = row.fpath.replace(/\\/g, "/");
				var attachImgUrl = getAttachImgUrl(row);
				$("#img12").attr("src",attachImgUrl);
				$("#span1").attr("data-id",row.billid);
				$("#img11").hide();
			    $("#img12").show();
			}
		}
	});
}

//获取附件显示url
function getAttachImgUrl(attach){
	return DZF.contextPath + '/chnpay/chnpaybill!getAttachImage.action?billid=' + attach.billid+"&time="+Math.random();
}

/**
 * 显示大图
 * @param billid
 */
function showImage(billid){
	var src = DZF.contextPath + "/chnpay/chnpaybill!getAttachImage.action?billid=" + billid +"&time=" +Math.random();
	$("#tpfd").empty();
	var img = '<img id="conturnid" alt="无法显示图片" '+
		'src="' + src + '" style="height: " + $(window).height()-10 + ";width: " + $(window).width()-10 +" ">'
	parent.openFullViewDlg(img, '原图');
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

