var contextPath = DZF.contextPath;
var status;
var editIndex;
var etIndex = undefined;
var flowImgUrls = null;
var gid;
var setIndex;
$(function(){
	load();
	reloadData();
	initMeasSelect();
	expendRow()
});

/**
 * 查询/新增商品-初始化商品分类
 */
function initGoodstyps(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/dealmanage/goodstype!queryComboBox.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				$("#qgtype,#gtype").combobox("loadData",result.rows);
			}
		}
	});
}

/**
 * 列表表格加载
 */
function load(){
	grid = $('#grid').datagrid({
		border : true,
		striped : true,
		rownumbers : true,
		fitColumns : false,
		height : Public.setGrid().h,
		singleSelect : false,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		showFooter:true,
		checkOnSelect:false,
		idField : 'gid',
		frozenColumns : [ [ 
		    {field : 'ck',checkbox : true}, 
		    {field : 'operate',title : '操作列',width : '120',halign : 'center',align : 'center',formatter : opermatter},
		    ] ],
		columns : [ [ 
		    {width : '100',title : '主键',field : 'gid',hidden : true}, 
		    {width : '100',title : '时间戳',field : 'updatets',hidden : true}, 
		    {width : '100',title : '是否已入库',field : 'isin',hidden : true},
		    {width : '100',title : '商品分类',field : 'gtypenm',align : 'left',halign : 'center',}, 
		    {width : '100',title : '商品编码',field : 'gcode',align : 'left',halign : 'center',}, 
		    {width : '200',title : '商品名称',field : 'gname',align : 'left',halign : 'center',formatter:namematter,}, 
		    {
			width : '60',
			title : '单位',
			field : 'mname',
            halign : 'center',
			align : 'left',
		},{
			width : '160',
			title : '商品说明',
			field : 'note',
            halign : 'center',
			align : 'left',
			formatter : noteFormat
		}, {
			width : '100',
			title : '商品状态',
			field : 'status',
            halign : 'center',
			align : 'center',
			formatter : function(value) {
				if (value == '1')
					return '已保存';
				if (value == '2')
					return '已发布';
				if (value == '3')
					return '已下架';
			}
		}, {
			width : '100',
			title : '发布日期',
			field : 'pubdate',
            halign : 'center',
			align : 'center',
		}, {
			width : '100',
			title : '下架日期',
			field : 'dofdate',
            halign : 'center',
			align : 'center',
		}, {
			width : '100',
			title : '录入人',
			field : 'opername',
            halign : 'center',
			align : 'center',
		}, {
			width : '100',
			title : '录入日期',
			field : 'operdate',
            halign : 'center',
			align : 'center',
		} ] ],
		onLoadSuccess : function(data) {
            $('#grid').datagrid("scrollTo",0);
		},
	});
}

function expendRow(){
	 $('#grid').datagrid({
        view: detailview,
        detailFormatter:function(index,row){
            return '<div style="padding:2px;position:relative;"><table class="ddv"></table></div>';
        },
        onExpandRow: function(index,row){
            var ddv = $(this).datagrid('getRowDetail',index).find('table.ddv');
            ddv.datagrid({
	           	url: contextPath + '/dealmanage/goodsmanage!queryGoodsSet.action',
	           	queryParams: {
	           		gid: row.gid,
	           	},
//	            fitColumns:true,
	            singleSelect:true,
	            rownumbers:true,
	            loadMsg:'',
	            height:'auto',
                columns:[[
                    {field:'spec',title:'规格',width:150,},
                    {field:'type',title:'型号',width:150,},
                    {field:'price',title:'成本价',width:100,align:'right',
                   	 formatter : function(value, row, index) {
            				if (value == 0)
            					return "0.00";
            				return formatMny(value);
            			},},
                ]],
                onResize:function(){
                    $('#grid').datagrid('fixDetailRowHeight',index);
                },
                onLoadSuccess:function(data){
                    setTimeout(function(){
                        $('#grid').datagrid('fixDetailRowHeight',index);
                    },0);
                }
            });
            $('#grid').datagrid('fixDetailRowHeight',index);
        }
    });
}


/**
 * 查询数据
 */
function reloadData(){
	var url = DZF.contextPath + '/dealmanage/goodsmanage!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'gcode' : $("#qgcode").val(),
		'gname' : $("#qgname").val(),
		'status' : $('#qstatus').combobox('getValue'),
		'gtype' : $('#qgtype').combobox('getValue'),
	});
	$('#grid').datagrid('clearSelections');
	$('#grid').datagrid('clearChecked');
	$('#qrydialog').hide();
}

/**
 * 清除查询条件
 */
function clearParams(){
	$("#qgcode").textbox('setValue',null);
	$("#qgname").textbox('setValue',null);
	$('#qgtype').combobox('setValue',null)
	$('#qstatus').combobox('setValue',-1)
}

/**
 * 取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 商品说明添加tips显示
 * @param value
 */
function noteFormat(value){
	if(value != undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}
}

/**
 * 列操作格式化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function opermatter(val, row, index) {
	if (row.status == 2) {//已发布
		return '<a href="#" style="margin-bottom:0px;color:blue;" onclick="setup(' + index + ')">设置</a>'+
		'<span style="margin-bottom:0px;margin-left:10px;">编辑</span>'+
		'<span style="margin-bottom:0px;margin-left:10px;">删除</span>';
	}else if(row.status == 3){//已下架
		return '<a href="#" style="margin-bottom:0px;color:blue;" onclick="setup(' + index + ')">设置</a>'+
		'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="edit(' + index + ')">编辑</a>'+
		'<span style="margin-bottom:0px;margin-left:10px;">删除</span>';
	}else if(row.status == 1){//已保存
		if(row.isin == 'Y' || row.isin == '是'){
			return '<a href="#" style="margin-bottom:0px;color:blue;" onclick="setup(' + index + ')">设置</a>'+
			'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="edit(' + index + ')">编辑</a>'+
			'<span style="margin-bottom:0px;margin-left:10px;">删除</span>';
		}else{
			return '<a href="#" style="margin-bottom:0px;color:blue;" onclick="setup(' + index + ')">设置</a>'+
			'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="edit(' + index + ')">编辑</a>'+
			'<a href="#" style="margin-bottom:0px;margin-left:10px;color:blue;" onclick="dele(this)">删除</a>';
		}
	}
}

/**
 * 设置
 */
function setup(index){
	var row = $('#grid').datagrid('getData').rows[index];
	gid = row.gid;
	
	$("#setDialog").dialog({
		title : '商品规格设置',
		modal : true,
		align:'center',
		onClose: function () {
			etIndex = undefined;
		}
	}).dialog('open').dialog("center");
	
	initSetGrid();
	
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/dealmanage/goodsmanage!queryGoodsSet.action',
		dataTye : 'json',
		data : {
			"gid" :gid,
		},
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				var rows = result.rows;
				if(rows != null && rows.length > 0){
					$('#setgrid').datagrid('loadData', rows);
				}else{
					$('#setgrid').datagrid('loadData',{ total:0, rows:[]});
					$('#setgrid').datagrid('appendRow', {
						gid : gid,
					});
					etIndex = 0;
					$('#setgrid').datagrid('beginEdit', 0);
				}
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});
	setIndex = index;
}

/**
 * 设置表格初始化
 */
function initSetGrid(){
	$("#setgrid").datagrid({
		height : 300,
		width : "100%",
		singleSelect : false,
		columns : [ [ 
		   {width : '100',title : '主键',field : 'specid',hidden : true}, 
		   {width : '100',title : '时间戳',field : 'updatets',hidden : true}, 
		   {field : 'gid',title : '主表主键',hidden : true,
			   editor : {
				   type : 'textbox',
			   }
		   }, 
		   {field : 'spec',title : '规格',width : "120",align : 'eft',halign : 'center',
				editor : {
					type : 'textbox',
					options : {
						height : 31,
						validType : [ 'length[0,25]' ],
						invalidMessage : "规格最大长度不能超过25",
					}
				}
		   }, 
		   {field : 'type',title : '型号',width : "120",align : 'left',halign : 'center',
			editor : {
				type : 'textbox',
				options : {
					height : 31,
					validType : [ 'length[0,25]' ],
					invalidMessage : "型号最大长度不能超过25",
				}
			}
		}, {width : '100',title : '单价',align:'right',halign:'center',field : 'price',
			editor : {
				type : 'numberbox',
				options : {
					height : 31,
					required: true,
 					precision:2,
 					min:0,
 					max:99999,
				}
			},
			formatter : function(value,row,index){
				if(value == 0)return "0.00";
				return formatMny(value);
			},
		}, {width : '100',field : 'button',title : '操作',
        	formatter : operatorLink
		},] ],
		onDblClickRow : function(rowIndex, rowData) {
			endBodyEdit();
			if(isEmpty(etIndex)){
				$("#setgrid").datagrid('beginEdit', rowIndex);
				etIndex = rowIndex;
			}else{
				if(etIndex == rowIndex){
					$("#setgrid").datagrid('beginEdit', etIndex);
				}else{
					if(isCanAdd()){
						$("#setgrid").datagrid('beginEdit', rowIndex);
						etIndex = rowIndex;
					}else{
						Public.tips({
							content : "规格和型号不能同时为空",
							type : 2
						});
					}
				}
			}
			
			if(rowData.beused == "是"){
				var spec = $("#setgrid").datagrid('getEditor', {index:rowIndex,field:'spec'}); 
				if(spec != undefined){
					$(spec.target).textbox('readonly', true);
				}
				
				var type = $("#setgrid").datagrid('getEditor', {index:rowIndex,field:'type'}); 
				if(type != undefined){
					$(type.target).textbox('readonly', true);
				}
			}
		},
	});
}

/**
 * 卡片grid按钮初始化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function operatorLink(val, row, index){  
	var add = '<div><a href="javascript:void(0)" id="addBut" onclick="addRow()">'+
		'<img title="增行" style="margin:0px 20% 0px 20%;" src="../../images/add.png" /></a>';
	var del = '<a href="javascript:void(0)" id="delBut" onclick="delRow(this)">'+
		'<img title="删行" src="../../images/del.png" /></a></div>';
	if(row.beused == "是"){
		return add;
	}else{
		return add + del;  
	}
}

/**
 * 设置-增行
 */
function addRow(){
	endBodyEdit();
	if(isCanAdd()){
		$('#setgrid').datagrid('appendRow', {
			gid : gid,
		});
		etIndex = $('#setgrid').datagrid('getRows').length - 1;
		$('#setgrid').datagrid('beginEdit',etIndex);
	}else{
		Public.tips({
			content : "规格和型号不能同时为空",
			type : 2
		});
		$("#setgrid").datagrid('beginEdit', etIndex);
		return;
	}
}

/**
 * 行编辑结束事件
 */
function endBodyEdit(){
    var rows = $("#setgrid").datagrid('getRows');
 	for ( var i = 0; i < rows.length; i++) {
 		$("#setgrid").datagrid('endEdit', i);
 	}
};

/**
 * 能否增行
 * @returns {Boolean}
 */
function isCanAdd() {
    if (etIndex == undefined) {
        return true;
    }
    var etRow = $('#setgrid').datagrid('getData').rows[etIndex];
    if (isEmpty(etRow.spec) && isEmpty(etRow.type)) {
    	return false;
    } else {
        $('#setgrid').datagrid('endEdit', etIndex);
        etIndex = undefined;
        return true;
    }
}

/**
 * 设置-删行
 */
function delRow(ths){
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	endBodyEdit();
	if(tindex == etIndex){
		var rows = $('#setgrid').datagrid('getRows');
		if(rows && rows.length > 1){
			$('#setgrid').datagrid('deleteRow', Number(tindex));   //将索引转为int型，否则，删行后，剩余行的索引不重新排列
		}
		etIndex = undefined;
	}else{
		if(isCanAdd()){
			var rows = $('#setgrid').datagrid('getRows');
			if(rows && rows.length > 1){
				$('#setgrid').datagrid('deleteRow', Number(tindex));   //将索引转为int型，否则，删行后，剩余行的索引不重新排列
			}
			etIndex = undefined;
		}else{
			Public.tips({
				content : "请先录入必输项",
				type : 2
			});
			return;
		}
	}
}

/**
 * 设置-保存
 */
function setSave(){
	endBodyEdit();
	
	var postdata = new Object();
	var deldata = "";
	
	//删除数据	
	var delRows = $('#setgrid').datagrid('getChanges', 'deleted');
	if(delRows != null && delRows.length > 0){
		for(var j = 0;j <delRows.length; j++){
			deldata = deldata + JSON.stringify(delRows[j]);
		}
	}
	
	var body = "";
	//界面数据
	var rows = $('#setgrid').datagrid('getRows');
	if(rows != null && rows.length > 0){
		for(var j = 0;j< rows.length; j++){
			body = body + JSON.stringify(rows[j]); 
			
			var datagrid = $("#setgrid").datagrid("validateRow", j);
			if (!datagrid){
				Public.tips({
					content : "必输信息为空或格式不正确",
					type : 2
				});
				return; 
			}
			if(isEmpty(rows[j].spec) && isEmpty(rows[j].type)){
				Public.tips({
					content : "规格和型号不能同时为空",
					type : 2
				});
				$("#setgrid").datagrid('beginEdit', j);
				etIndex = j;
				return;
			}
		}
	}
	
	postdata["deldata"] = deldata;
	postdata["body"] = body;
	onSaveSet(postdata);
}

/**
 * 设置-提交后台保存
 * @param postdata
 */
function onSaveSet(postdata){
	$.messager.progress({
		text : '数据操作中....'
	});
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/dealmanage/goodsmanage!saveSet.action',
		data : postdata,
		traditional : true,
		async : false,
		success : function(result) {
			$.messager.progress('close');
			if (!result.success) {
				Public.tips({
					content : result.msg,
					type : 1
				});
			} else {
				Public.tips({
					content : result.msg,
				});
				reloadData();
				$('#setDialog').dialog('close');
				etIndex = undefined;
			}
		},
	});
}

/**
 * 设置-取消
 */
function setCancel(){
	$('#setDialog').dialog('close');
	etIndex = undefined;
}

/**
 * 修改
 * @param index
 */
function edit(index){
	var erow = $('#grid').datagrid('getData').rows[index];
	var row = queryByID(erow.gid, 0);
	if(isEmpty(row)){
		return;
	}
	
	initGoodstyps();
	initFileEvent();
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '修改商品');
	$('#gcode').textbox("readonly",true);
	if(row.isin == 'Y' || row.isin == '是'){
		$('#gtype').combobox("readonly",true);
		$('#gname').textbox("readonly",true);
		$('#measid').combobox("readonly",true);
	}
	$('#goods_add').form('clear');
	initMeas();
//	initMeasSelect();
	$('#goods_add').form('load', row);
	$("#measid").combobox("setValue",row.measid);
	viewImageFiles(row);
	$('.filepath1').prop('disabled',false);
	var htmlImg= '<div class="imgbox">'+
			'<div class="imgnum">'+
				'<input type="file" class="filepath1" name="imageFile" multiple="multiple"'+
					'accept="image/gif,image/jpeg,image/jpg,image/png"/>'+
				'<span class="close1"><img src="../../images/Dustbin.png"/></span>'+
				'<img src="../../images/wer_03.png" class="img1" /> ' +
				'<img src="" class="img2" />'+
			'</div>'+
		'</div>';
	$("#image1").append(htmlImg);
	initClick();
	initIdDelClick();
	status = "edit";
	editIndex = index;
}

/**
 * 展示商品图片
 * @param row
 */
function viewImageFiles(row){
	$.ajax({
		type : "POST",
		url : contextPath + "/dealmanage/goodsmanage!getAttaches.action",
  		dataType : 'json',
  		data : row,
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var rows = result.rows;
			arrachrows = result.rows;
			$("#image1").html('')
			$("#img12").attr("src",'');
			$("#span1").attr("data-id",'');
			$("#img11").show();
			if(rows && rows.length > 0){
				var ret = 0;
				for(var i = 0;i<rows.length;i++){
					if(rows[i].fpath){
						var url = getAttachImgUrl(rows[i]);
						var htmlImg= '<div class="imgbox">'+
										'<div class="imgnum">'+
										'<input type="file" class="filepath1" name="imageFile" multiple="multiple" '+
											'accept="image/gif,image/jpeg,image/jpg,image/png"/>'+
										'<span class="close1" data-id="'+rows[i].doc_id+'"><img src="../../images/Dustbin.png"/></span>'+
										'<img src="'+url+'" class="img2" />'+
										'</div>'+
									 '</div>';
						$("#image1").append(htmlImg);
					}
				} 
				$('.filepath1').prop('disabled',true);
			}
		}
	});
}

/**
 * 获取图片信息
 * @param attach
 * @returns {String}
 */
function getAttachImgUrl(row) {
	var ext = getFileExt(row['fpath']);
	if ("pdf" == ext.toLowerCase()) {
		return "../../images/typeicon/pdf.jpg";
	} else if ("txt" == ext.toLowerCase()) {
		return "../../images/typeicon/txt.jpg";
	}
	return DZF.contextPath
			+ '/dealmanage/goodsmanage!getAttachImage.action?&doc_id='
			+ row.doc_id;
}

/**
 * 获取附件扩展
 * @param filename
 * @returns
 */
function getFileExt(filename){
	var index1 = filename.lastIndexOf(".")+1;
	var index2 = filename.length;
	var ext = filename.substring(index1,index2);
	return ext;
}

/**
 * 通过主键查询商品信息
 * @param contractid
 * @returns
 */
function queryByID(gid, type){
	var row;
	$.ajax({
		type : "post",
		dataType : "json",
		traditional : true,
		async : false,
		url : contextPath + '/dealmanage/goodsmanage!queryByID.action',
		data : {
			"gid" : gid,
			"type" : type,
		},
		success : function(data, textStatus) {
			if (!data.success) {
				Public.tips({
					content :  data.msg,
					type : 1
				});	
				return;
			} else {
				row = data.rows;
			}
		},
	});
	return row;
}

/**
 * 删除
 * @param ths
 */
function dele(ths){
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	var row = $('#grid').datagrid('getData').rows[tindex];
	if (row.status != 1) {
		Public.tips({
			content : '该记录不是已保存状态，不允许删除',
			type : 2
		});
		return;
	}
	$.messager.confirm("提示", "你确定删除吗？", function(flag) {
		if (flag) {
			$.ajax({
				type : "post",
				dataType : "json",
				url : contextPath + '/dealmanage/goodsmanage!delete.action',
				data : row,
				traditional : true,
				async : false,
				success : function(data, textStatus) {
					if (!data.success) {
						Public.tips({
							content : data.msg,
							type : 1
						});
					} else {
						$('#grid').datagrid('clearSelections');
						$("#grid").datagrid("reload");
						Public.tips({
							content : data.msg,
						});
					}
				},
			});
		} else {
			return null;
		}
	});
}

/**
 * 新增
 */
function add(){
	initGoodstyps();
	initFileEvent();
	$('#cbDialog').dialog('open').dialog('center').dialog('setTitle', '新增商品');
	$('#gcode').textbox("readonly",false);
	$('#gtype').combobox("readonly",false);
	$('#gname').textbox("readonly",false);
	$('#measid').combobox("readonly",false);
	$('#goods_add').form('clear');
	$("#image1").html('');
	var htmlImg = '<div class="imgbox">'+
					'<div class="imgnum">'+
						'<input type="file" class="filepath1" name="imageFile" multiple="multiple" '+
							'accept="image/gif,image/jpeg,image/jpg,image/png"/>'+
						'<span class="close1"><img src="../../images/Dustbin.png"/></span>'+
						'<img src="../../images/wer_03.png" class="img1" /> ' +
						'<img src="" class="img2" />'+
					'</div>'+
				  '</div>';
	$("#image1").html(htmlImg);
	initMeas();
//	initMeasSelect();
	
	initClick();
	initIdDelClick();
	initEvent();
	status = "add";
}

/**
 * 监听事件
 */
function initEvent(){
	$('#gcode').textbox({// 去除空格
		onChange : function(n, o) {
			if(isEmpty(n)){
				return;
			}
			var _trim = trimStr(n,'g');
			$("#gcode").textbox("setValue", _trim);
		}
	});
	$('#gname').textbox({// 去除空格
		onChange : function(n, o) {
			if(isEmpty(n)){
				return;
			}
			var _trim = trimStr(n,'g');
			$("#gname").textbox("setValue", _trim);
		}
	});
}

/**
 * 计量单位选择事件
 */
function initMeasSelect(){
	$("#measid").combobox({
		onSelect : function(record) {
			$('#mname').val(record.name);
		}
	});
}

/**
 * 新增商品-初始化计量单位
 */
function initMeas(){
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/dealmanage/goodsmanage!initMeasCombox.action',
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				$("#measid").combobox("loadData",result.rows);
			}
		}
	});
}

/**
 * 上传图片添加事件
 */
function initFileEvent(){
	$(".uploadImg").on("change",".filepath1",function(){//添加附件
		if(this.files.length <= 0){
			return;
		}
		var fname = this.files[0].name;
		var imageExt=fname.substr(fname.lastIndexOf(".")).toLowerCase();//获得文件后缀名
	    if(imageExt !='.jpg' && imageExt !='.png' && imageExt !='.jpeg'){
	        Public.tips({ content : "请上传后缀名为jpg、png、jpeg的图片", type : 2 });
	        return;
	    }
	    checkfile(this);
	    
		var srcs = getObjectURL(this.files[0]);   //获取路径
		var imgsrc = $(this).nextAll(".img2").attr("src"); 
	    //this指的是input
	    $(this).nextAll(".img1").hide();   //this指的是input
	    $(this).nextAll(".img2").show();  //fireBUg查看第二次换图片不起做用
	    $(this).nextAll('.close1').show();   //this指的是input
	    $(this).nextAll(".img2").attr("src",srcs);    //this指的是input
	    if(imgsrc == null || imgsrc == ""){
	    	 var htmlImg='<div class="imgbox">'+
				     		'<div class="imgnum">'+
				     			'<input type="file" class="filepath1" name="imageFile" multiple="multiple"'+
				     				' accept="image/gif,image/jpeg,image/jpg,image/png"/>'+
				     			'<span class="close1"><img height="26" src="../../images/Dustbin.png"/></span>'+
						            '<img src="../../images/wer_03.png" class="img1" />'+
						            '<img src="" class="img2" />'+
						        '</div>'+
				         '</div>';
	    	 $(this).parent().parent().after(htmlImg);
	    }
	    initClick();
	    var data_id = $(this).nextAll('.close1').attr("data-id");
	   	if(data_id){
	   		deleteImageFile(data_id);
	   	}
	});
}

/**
 * 检查图片大小，不能超过1M
 * @param index
 */
function checkfile(ths){
	try{
	 	if(ths.files[0].value == ""){
	 		Public.tips({content : '请先选择上传文件', type : 1});
	 		return;
	 	}
	 	var filesize = 0;
	 	if(browserCfg.firefox || browserCfg.chrome ){
	 		filesize = ths.files[0].size;
	 	}else if(browserCfg.ie){
	 		var obj_img = document.getElementById('tempimg');
	 		obj_img.dynsrc = ths.files[0].value;
	 		filesize = obj_img.fileSize;
	 	}else{
//	 		Public.tips({content : tipMsg, type : 2});
//	 		return;
	 		filesize = ths.files[0].size;
	 	}
	 	console.info(filesize);
	 	if(filesize == -1){
	 		Public.tips({content : tipMsg, type : 2});
	 		return;
	 	}else if(filesize > maxsize){
	 		ths.value = ''; 
//	 		$('#filename'+index).html('');
	 		Public.tips({content : errMsg, type : 1});
	 		return;
		}
	}catch(e){
		Public.tips({content : e, type : 2});
	}
}

/**
 * 获取图片路径
 * @param file
 * @returns
 */
function getObjectURL(file) {
    var url = null;
	if (window.createObjectURL != undefined) {
		url = window.createObjectURL(file)
	} else if (window.URL != undefined) {
		url = window.URL.createObjectURL(file)
	} else if (window.webkitURL != undefined) {
		url = window.webkitURL.createObjectURL(file)
	}
    return url;
};

/**
 * 删除界面图片
 */
function initClick(){
	$(".close1").on("click",function() {
		var data_id = $(this).attr("data-id");
    	if(data_id){
    		deleteImageFile(data_id);
    	}
    	if($(this).nextAll(".img2").attr("src")){
    		$(this).hide();     //this指的是span
    		$(this).nextAll(".img2").hide();
    		$(this).nextAll(".img1").show();
    		if($('.imgbox').length>1){
    			$(this).parent().parent().remove();
    		}
    	}
    })
}

/**
 * 删除图片
 */
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

/**
 * 调用后台接口删除图片
 * @param data_id
 */
function deleteImageFile(data_id){
	$.ajax({
		url : contextPath + "/dealmanage/goodsmanage!deleteFile.action",
		traditional : true,
		async : false,
		dataType : 'json',
		data : {
			"doc_id" : data_id,
		},
	});
}

/**
 * 商品-新增保存
 */
function onSave(){
	if ($('.img2')[0].src.indexOf('goodsmanage.jsp') >= 0) {
		Public.tips({
			content : "商品图片不能为空",
			type : 2
		});
		return;
	}
	
	if ($("#goods_add").form('validate')) {
		var gcode = $('#gcode').val();
		if(!isEmpty(gcode)){
			var flag = isLetterAndNum(gcode);
			if(!flag){
				Public.tips({
					content : "商品编码只能含有数字和字母",
					type : 2
				});
				return;
			}
		}
		
		$.messager.progress({
			text : '数据保存中，请稍后.....'
		});
		$('#goods_add').form('submit', {
			url : DZF.contextPath + '/dealmanage/goodsmanage!save.action',
			success : function(result) {
				var result = eval('(' + result + ')');
				$.messager.progress('close');
				if (result.success) {
					var row = result.rows;
					initMeas();
					$('#cbDialog').dialog('close');
					reloadData();
					editIndex = null;
				} else {
					Public.tips({
						content : result.msg,
						type : 2
					});
				}
			}
		});
	} else {
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return; 
	}
}

/**
 * 商品-取消
 */
function onCancel(){
	$('#cbDialog').dialog('close');
	status = "brows";
	editIndex = null;
}

/**
 * 发布
 */
function publish(){
	operdata(1);
}

/**
 * 下架
 */
function off(){
	operdata(2);
}

/**
 * 操作数据
 * @param type
 */
function operdata(type){
	var rows = $('#grid').datagrid('getChecked');
	if (rows == null || rows.length == 0) {
		Public.tips({
			content : '请选择需要处理的数据',
			type : 2
		});
		return;
	}
	var postdata = new Object();
	var data = "";
	for(var i = 0; i < rows.length; i++){
		data = data + JSON.stringify(rows[i]);
	}
	postdata["data"] = data;
	postdata["type"] = type;
	$.messager.progress({
		text : '数据操作中....'
	});
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/dealmanage/goodsmanage!updateData.action',
		data : postdata,
		traditional : true,
		async : false,
		success : function(result) {
			$.messager.progress('close');
			if (!result.success) {
				Public.tips({
					content : result.msg,
					type : 1
				});
			} else {
				if(result.status == -1){
					Public.tips({
						content : result.msg,
						type : 2
					});
				}else{
					Public.tips({
						content : result.msg,
					});
				}
				reloadData();
				$("#grid").datagrid('uncheckAll');
			}
		},
	});
}

/**
 * 添加单位
 */
function addMeas(){
	$('#jlDialog').dialog('open').dialog('center').dialog('setTitle', '商品计量单位');
	$('#meas_add').form('clear');
}

/**
 * 计量单位-保存
 */
function measSave(){
	if ($("#meas_add").form('validate')) {
		$.messager.progress({
			text : '数据保存中，请稍后.....'
		});
		
		$('#meas_add').form('submit', {
			url : DZF.contextPath + '/dealmanage/goodsmanage!saveMeas.action',
			success : function(result) {
				var result = eval('(' + result + ')');
				$.messager.progress('close');
				if (result.success) {
					initMeas();
					$('#jlDialog').dialog('close');
				} else {
					Public.tips({
						content : result.msg,
						type : 2
					});
				}
			}
		});
	} else {
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return; 
	}
}

/**
 * 计量单位-取消
 */
function measCancel(){
	$('#jlDialog').dialog('close');
}

/**
 * 商品名称格式化
 * @param value
 * @param row
 * @param index
 * @returns {String}
 */
function namematter(value,row,index){
	return '<a href="javascript:void(0)" style="color:blue"  onclick="showInfo(' + index + ')">'+value+'</a>';
}

/**
 * 展示详情
 * @param index
 */
function showInfo(index){
	var erow = $('#grid').datagrid('getData').rows[index];
	var row = queryByID(erow.gid, 1);
	if(isEmpty(row)){
		return;
	}
	initGoodstyps();
	$('#infoDlg').dialog('open').dialog('center').dialog('setTitle', '商品详情');
	$('#goods_info').form('clear');
	$('#goods_info').form('load', row);
	viewImageInfo(row);
	status = "brow";
}

/**
 * 展示商品图片
 * @param row
 */
var arrachrows = null;
function viewImageInfo(row){
	$.ajax({
		type : "POST",
		url : contextPath + "/dealmanage/goodsmanage!getAttaches.action",
  		dataType : 'json',
  		data : row,
  		processData : true,
  		async : false,//异步传输
  		success : function(result) {
			var rows = result.rows;
			arrachrows = result.rows;
			$("#image2").html('')
			$("#img12").attr("src",'');
			$("#span1").attr("data-id",'');
			$("#img11").show();
			if(rows && rows.length > 0){
				arrachrows = rows;
				var ret = 0;
				flowImgUrls = new Array();
				for(var i = 0;i<rows.length;i++){
					if(rows[i].fpath){
						var url = getAttachImgUrl(rows[i]);
						var htmlImg= '<div class="imgbox">'+
										'<div class="imgnum">'+
											'<a href="javascript:void(0)" ondblclick="doubleImage(\'' + i + '\');">'+
												'<img src="'+url+'" class="img2" />'+
											'</a>'
										'</div>'+
									 '</div>';
						$("#image2").append(htmlImg);
						
						var src = DZF.contextPath + "/dealmanage/goodsmanage!getAttachImage.action?&doc_id=" +
							rows[i].doc_id ;
						var img = '<img id="conturnid" alt="无法显示图片" src="' + src 
						+ '" style="position: absolute;z-index: 1;left:50px;">';
						flowImgUrls[i] = img;
					}
				} 
				$('.filepath1').prop('disabled',true);
			}
		}
	});
}

/**
 * 双击显示大图
 * @param i
 */
function doubleImage(i){
	var src = DZF.contextPath+ '/dealmanage/goodsmanage!getAttachImage.action?&doc_id='	+ arrachrows[i].doc_id;
	if(!isEmpty(src)){
		var img = '<img id="conturnid" alt="无法显示图片" src="' + src 
			+ '" style="position: absolute;z-index: 1;left:50px;top:50px;">';
		parent.openFullViewDlg(img,'原图', null, null, i, flowImgUrls)
	}

}

