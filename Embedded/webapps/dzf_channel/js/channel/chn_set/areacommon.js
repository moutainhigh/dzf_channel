var contextPath = DZF.contextPath;
var chargeData=[{value:"否",text:"否"},{value:"是",text:"是"}];
var status="brows";
var editIndex = undefined;
var type;
var selmap;
var sellist;
var uidlist;

$(function() {
	type=$("#hidtype").val();
	loadManager();
	load();
	initManger();
	initCardCorpnm();
	selmap = new HashMap();
	sellist = new ArrayList();
	uidlist = new ArrayList();
});

function loadManager(){
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/chn_set/chnarea!queryManager.action',
		traditional : true,
		async : false,
		success : function(data, textStatus) {
			if (data.success&& !isEmpty(data.rows)) {
				$("#manager").text("加盟商总经理: "+data.rows);
			}else{
				$("#manager").text("加盟商总经理");
			}
		},
	});
}

function load() {
	// 列表显示的字段
	$('#grid').datagrid({
		url : DZF.contextPath + '/chn_set/chnarea!query.action',
		idField : 'pk_area',
		rownumbers : true,
		singleSelect : true,
		queryParams : {'type' :type},
		height : Public.setGrid().h,
		columns : [ [ {
			width : '80',
			title : '编码',
			field : 'acode',
		},{
			width : '130',
			title : '区域',
			field : 'aname'
		}, {
			width : '140',
			title : '区域负责人',
			field : 'uname'
		},{
			width : '260',
			title : '所属省市',
			field : 'provnames',
			formatter : showTitle
		},{
			width : '280',
			title : '备注',
			field : 'vmemo',
	  		formatter :showTitle
		},{
			width :'250',
			title : '操作',
			field : 'cz',
			align : 'center',
			formatter: 
				function(value,row,index){
					 return '<a class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="edit(\''+row.pk_area+'\',\''+1+'\');" plain="true" href="javascript:void(0);">查看</a>'+
					 	'<a class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="edit(\''+row.pk_area+'\',\''+0+'\');" plain="true" href="javascript:void(0);">修改</a>'+
						'<a class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="del(\''+row.pk_area+'\');" plain="true" href="javascript:void(0);">删除</a>'
						;
      	  		}
		},{
			title : '主键',
			field : 'pk_area',
			hidden: true
		},{
			title : '用户主键',
			field : 'uid',
			hidden: true
		}
		] ],
		onLoadSuccess : function(data) {
			parent.$.messager.progress('close');
			$('#grid').datagrid("selectRow", 0);  	
		}
	});
}

function add(){
	initCard();
    $('#cardDialog').dialog({modal:true});
	var title;
	if(type=="1"){
		title='渠道区域划分';
	}else if (type=="2"){
		title="培训区域划分";
	}else{
		title="运营区域划分";
	}
    $('#cardDialog').dialog('open').dialog('center').dialog('setTitle',title);
    status="add";
    setItemEdit(false);
    $('#chnarea').form("clear");
    $('#cardGrid').datagrid('loadData', { total : 0, rows : [] });// 清楚缓存数据
    for(var i=0;i<5;i++){
    	$('#cardGrid').datagrid('appendRow',{isCharge:chargeData[0].value})
    }
	editIndex =0;
	$('#cardGrid').datagrid('beginEdit',editIndex);
};


/**
 * 编辑
 */
function edit(id,style) {
	initCard(id);
	if(style==0){//修改
		status='edit';
	}else{//查看
		status='brows';
	}
    jQuery.ajax({
    	url : DZF.contextPath + '/chn_set/chnarea!queryByPrimaryKey.action',
    	data : {'pk_area' : id,},
    	type : 'post',
    	dataType : 'json',
    	success: function(result){
    		if (result.success){
    			$('#cardDialog').dialog({
    				modal:true,
    			});
    			var title;
    			if(type=="1"){
    				title="渠道区域";
    			}else if (type=="2"){
    				title="培训区域";
    			}else{
    				title="运营区域";
    			}
    			if(status=="brows"){
    				$('#cardDialog').dialog('open').dialog('center').dialog('setTitle',title);
    				setItemEdit(true);
    			}else{
    				$('#cardDialog').dialog('open').dialog('center').dialog('setTitle',title+"修改");
    				setItemEdit(false);
    			}
    			$('#chnarea').form("clear");
    			$('#chnarea').form('load', result.rows[0]);
    			var row =result.rows[0];
    			$("#cardGrid").datagrid("loadData",row.children);
    			editIndex = $('#cardGrid').datagrid('getRows').length - 1;
    		} else {
    			Public.tips({content:result.msg,type:1});
    		} 
    	}
    });
}



//删除
function del(id){
	var rows = $('#grid').datagrid('getRows');
	var index=$('#grid').datagrid('getRowIndex',id);
	var row=rows[index];
	$.messager.confirm("提示", "你是否确认要删除"+row.aname+"？", function(flag) {
		if (flag) {
			$.ajax({
				url : DZF.contextPath + '/chn_set/chnarea!delete.action',
				data : {
					'pk_area': id,
				},
				type : 'post',
				dataType : 'json',
				success : function(result) {
					if (result.success) {
						$("#grid").datagrid("reload");
						Public.tips({
							content : result.msg
						});
					} else {
						Public.tips({
							content : result.msg,
							type : 1
						});
					}
				}
			});
		} else {
			return null;
		}
	});
}

/**
*  校验保存
*/
function checkSave(){
	//校验
	var flag = $('#chnarea').form('validate');
	if(flag == false){
		Public.tips({content:"必输信息为空或格式不正确",type:2});
		return;
	}
	var reg = new RegExp("[\\u4E00-\\u9FFF]+","g");
	if(reg.test($('#acode').textbox('getValue'))){ 
		Public.tips({content:"大区编码不能包含汉字",type:2});
		$('#acode').textbox('setValue',"");
		return;
	}
	endBodyEdit();
	// 校验子表
	var bodys= $("#cardGrid").datagrid("getRows");
	var length=JSON.parse(JSON.stringify(bodys.length));
	var u=0;
	for (var i = 0; i <length; i++) {
		if(!bodys[u].ovince&&!bodys[u].uid&&!bodys[u].vmemo){
			$('#cardGrid').datagrid('deleteRow',u);
			continue;
		}
		$('#cardGrid').datagrid('beginEdit',u);
		flg = $("#cardGrid").datagrid("validateRow", u);
		if (!flg){
			Public.tips({
				content : "必输信息为空或格式不正确",
				type : 2
			});
			return ;
		}
		u++;
	}
	save();
}

/**
*  保存（新增和修改）
*/
function save() {
	var childBody = "";
	var rows = $("#cardGrid").datagrid('getRows');
	for (var i = 0; i < rows.length; i++) {
		childBody = childBody + JSON.stringify(rows[i]);
		$("#cardGrid").datagrid('endEdit', i);
	}
	$("#type").textbox('setValue',type);
	var postdata = new Object();
	postdata["head"] = JSON.stringify(serializeObject($('#chnarea')));
	postdata["body"] = childBody;
	$.ajax({
		type : 'POST',
		url :DZF.contextPath + '/chn_set/chnarea!save.action',
		data : postdata,
		dataType : 'json',
		success : function(result) {
			$.messager.progress('close');
			if (result.success) {
				$('#cardDialog').dialog('close');
				Public.tips({
					content : result.msg,
					type : 0
				});
				load();
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});
}

function cancel(){
	if(status == "add" || status == "edit"){
		$.messager.confirm("提示", "确定取消吗？", function(flag) {
			if (flag) {
				$('#cardDialog').dialog('close');
			} else {
				return null;
			}
		});
	}else{
		$('#cardDialog').dialog('close');
	}
}


/**
 * 卡片grid初始化
 */
function initCard(id){
	var areas;
	//地区初始化
	$.ajax({
		type : 'POST',
		async : false,
		url : DZF.contextPath + '/chn_set/chnarea!queryComboxArea.action',
		data : {"pk_area" : id,'type' : type,},
		dataTye : 'json',
		success : function(result) {
			var result = eval('(' + result + ')');
			if (result.success) {
				areas=result.rows;
			} else {
				Public.tips({content : result.msg,type : 2});
			}
		}
	});
	var uname;
	if(type=="1"){
		uname="渠道经理";
	}else if (type=="2"){
		uname="培训师";
	}else{
		uname="渠道运营";
	}
	$('#cardGrid').datagrid({
		striped : true,
		rownumbers : true,
		idField : 'pk_areab',
		height : 220,
		singleSelect : true,
		columns : [ [ 
        {
        	width : '60',
			field : 'button',
			title : '操作',
        	formatter : coperatorLink
		}, {
			width : '150',
			field : 'provname', 
			title : '负责地区',
			editor : {
				type: 'combobox',
                options: {
                	height: 35,
                	panelHeight: 160,
                	showItemIcon: true,
                	valueField: "name",
                	editable: false,
                	required : true,
                	textField: "name",
                	data: areas,
                	onSelect: function (rec) { 
                		var ovince = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'ovince'});
                		$(ovince.target).textbox('setValue', rec.id);
                		var corpnm = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'corpnm'});
                		$(corpnm.target).textbox('setValue', null);
                		var corpid = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'corpid'});
                		$(corpid.target).textbox('setValue', null);
                	}
                }
            }
		}, {
			width : '150',
			field : 'uname',
			title : uname,
			editor : {
				type : 'textbox',
				options : {
					height:31,
					required : true,
					editable:false,
					icons: [{
						iconCls:'icon-search',
						handler: function(){
							initChnUser();
						}
					}]
				}
			}
		},{
			width : '220',
			field : 'corpnm',
			title : '加盟商',
			formatter :function(value, row, index) {
				if(value!=undefined &&　status=="brows"){
					return "<a href='javascript:void(0)' style='color:blue' onclick=\"qryDetail('"+index+"')\">" + value + "</a>";
				}else{
					return value;
				}
  			},
			editor : {
				type : 'textbox',
				options : {
					height:31,
					editable:false,
					icons: [{
						iconCls:'icon-search',
						handler: function(){
							initChnCorp();
						}
					}]
				}
			}
		},{
			width : '95',
			field : 'isCharge',
			title : '省/市负责人',
			formatter: function (value) {
				var text = "";
				if (value == '是')
					text = "是";
				if (value == '否')
					text = "否";
				return text;
			},
			editor : { 
				   type: 'combobox',
	               options: {
	            	   height:31,
	                   data: chargeData,
	                   panelHeight: 80,
                   	   showItemIcon: true,
	                   valueField: "value",
	                   textField: "text",
	                   editable: false
	               }},
		},{
			width : '280',
			field : 'vmemo',
			title : '备注',
			formatter : showTitle,
			editor : {
				type : 'textbox',
				options : {
					height:31,
					validType:['length[0,20]'],
        			invalidMessage:"备注最大长度不能超过20",
				}
			}
		},{
			width : '100',
			field : 'pk_area',
			title : '主表主键',
			hidden : true,
		},{
			width : '100',
			field : 'uid',
			title : '渠道经理主键',
			hidden : true,
			editor : {
				type : 'textbox',
			}
		},{
			width : '100',
			field : 'ovince',
			title : '地区',
			hidden : true,
			editor : {
				type : 'textbox',
			}
		},{
			width : '100',
			field : 'corpid',
			title : '加盟商主键',
			hidden : true,
			editor : {
				type : 'textbox',
			}
		},{
			width : '100',
			field : 'incode',
			title : '加盟商编码',
			hidden : true,
			editor : {
				type : 'textbox',
			}
		}
		] ],
		onClickRow :  function(index, row){
			if(status == "brows"){
				return;
			}
			endBodyEdit('#cardGrid');
			if($('#cardGrid').datagrid('validateRow', editIndex)){
				if (index != undefined) {
					$('#cardGrid').datagrid('beginEdit', index);
					editIndex = index;
				}           		
			}else{
				Public.tips({
					content : "请先编辑必输项",
					type : 2
				});
			}
		} ,
	});
}

function showTitle(value){
	if(value!=undefined){
		return "<span title='" + value + "'>" + value + "</span>";
	}
}

/**
 * 字段编辑
 */
function setItemEdit(isEdit){
	$('#acode').textbox('readonly',isEdit);
	$('#aname').textbox('readonly',isEdit);
	$('#uname').textbox('readonly',isEdit);
	$('#vmemo').textbox("readonly",isEdit);
	updateBtnState();
}

/**
 * 卡片界面的按钮显示及隐藏
 */
function updateBtnState(){
	if("add"==status||"edit"==status){
		$('#save').show();
		$('#cancel').show();
	}else if("brows"==status){
		$('#save').hide();
		$('#cancel').hide();
	}	
}
/**
 * 加盟商参照初始化
 */
function initChnCorp(){
	var ovince = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'ovince'});
	var tar_ovince=$(ovince.target).textbox('getValue');
	if(isEmpty(tar_ovince)){
		Public.tips({content : "请先选择负责地区",type :2});
		return;
	}
	var corpid = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'corpid'});
	var tar_corpid;
	if(!isEmpty(corpid)){
		tar_corpid=$(corpid.target).textbox('getValue');
	}
	var rows= $('#cardGrid').datagrid('getRows');
	var corpids="";
	for(var i=0;i<rows.length;i++){
		if(i==editIndex ){
			continue;
		}
		if(!isEmpty(rows[i].corpid) && tar_ovince==rows[i].ovince){
			corpids=corpids+","+rows[i].corpid;
		}
	}
	if(!isEmpty(corpids)){
		corpids=corpids.substring(1);
	}
	$("#chnDlg").dialog({
		width: 600,
	    height: 480,
		readonly: true,
		title: '选择加盟商',
		modal: true,
		href: DZF.contextPath + '/ref/select_channels.jsp',
		queryParams:{
			'ovince' : tar_ovince,
			'corpids' : corpids,
			'corpid': tar_corpid
		},
		buttons : [ {
			text : '确认',
			handler : function() {
				selmap = new HashMap();
				selectCorps();
				$('#chnDlg').dialog('close');
			}
		}, {
			text : '取消',
			handler : function() {
				selmap = new HashMap();
				sellist = new ArrayList();
				uidlist = new ArrayList();
				$('#chnDlg').dialog('close');
			}
		} ]
	});
    $("#chnDlg").dialog({  
        onClose: function () {  
        	selmap = new HashMap();
    		sellist = new ArrayList();
    		uidlist = new ArrayList();
        }  
    }); 
}


function initChnUser(){
	$("#userdialog").dialog({
		width : 520,
		height : 530,
		readonly : true,
		title : '选择用户',
		cache : false,
		modal : true,
		href : contextPath + '/ref/chnuser_select.jsp',
		queryParams:{
			dblClickRowCallback : 'selectChnUser',
		},
		buttons : [ {
			text : '确认',
			handler : function() {
				var row = $('#userTable').datagrid('getSelected');
				if(row){
					selectChnUser(row);
				}else{
					Public.tips({
						content : "请选择需要处理的数据",
						type : 2
					});
				}
			}
		}, {
			text : '取消',
			handler : function() {
				$("#userdialog").dialog('close');
			}
		} ]
	});
}

/**
 * 渠道经理选择事件
 */
function selectChnUser(row){
	var uid = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'uid'});
	var uname = $('#cardGrid').datagrid('getEditor', {index:editIndex,field:'uname'});
	$(uid.target).textbox('setValue', row.uid);
	$(uname.target).textbox('setValue', row.uname);
	$('#userdialog').dialog('close');
}

function initCardCorpnm(){
	$('#gridh').datagrid({
		striped : true,
		rownumbers : true,
		height : 320,
		singleSelect : true,
		columns : [ [ 
	    {
	    	width : '240',
			field : 'incode',
			title : '加盟商编码',
		}, {
			width : '240',
			field : 'corpnm',
			title : '加盟商名称',
		}
		] ]
	});
}

/**
 * 大区总经理参照初始化
 */
function initManger(){
	$('#uname').searchbox({
		editable:false,
		prompt:'选择用户',
	    searcher:function(){
	    	$('#userdialog').dialog({
	    		width : 520,
	    		height : 530,
	    		readonly : true,
	    		close:true,
	    		title : '选择用户',
	    		modal : true,
	    		href : DZF.contextPath+'/ref/chnuser_select.jsp',
	    		queryParams:{
	    			dblClickRowCallback : 'selectManager',
	    		},
	    		buttons : [ {
	    			text : '确认',
	    			handler : function() {
	    				var row = $('#userTable').datagrid('getSelected');
	    				if(row){
	    					selectManager(row);
	    				}else{
	    					Public.tips({
	    						content : "请选择一行数据",
	    						type : 2
	    					});
	    				}
	    			}
	    		}, {
	    			text : '取消',
	    			handler : function() {
	    				$('#userdialog').dialog('close');
	    			}
	    		}]
	    	});
	    }
	});
}

function selectManager(row){
	$('#uname').textbox('setValue',row.uname);
	$('#uid').val(row.uid);
	$('#userdialog').dialog('close');
}


/**
 * 双击选择加盟商
 * @param rowTable
 */
function dClickCompany(rowTable) {
	var str = "";
	var corpIds = "";
	var incodes="";
	if (rowTable) {
		for (var i = 0; i < rowTable.length; i++) {
			if (i == rowTable.length - 1) {
				str += rowTable[i].uname;
				corpIds += rowTable[i].pk_gs;
				incodes += rowTable[i].incode;
			} else {
				str += rowTable[i].uname + ",";
				corpIds += rowTable[i].pk_gs + ",";
				incodes += rowTable[i].incode+ ",";
			}
		}
		var corpid = $('#cardGrid').datagrid('getEditor', {index : editIndex,field : 'corpid'});
		var corpnm = $('#cardGrid').datagrid('getEditor', {index : editIndex,field : 'corpnm'});
		var incode = $('#cardGrid').datagrid('getEditor', {index : editIndex,field : 'incode'});
		$(corpid.target).textbox('setValue', corpIds);
		$(corpnm.target).textbox('setValue', str);
		$(incode.target).textbox('setValue', incodes);
	}
	$("#chnDlg").dialog('close');
}

function selectCorps(){
	var rows = $('#gsTable').datagrid('getSelections');
	dClickCompany(rows);
}

function qryDetail(index){
	var row=$("#cardGrid").datagrid('getRows')[index];
	var corpnm=row.corpnm;
	var incode=row.incode;
	var corpnms= new Array(); //定义一数组 
	var incodes= new Array(); //定义一数组 
	var param =  new Array();
	var obj =  new Object();
	if(corpnm.indexOf(",")!=-1){
		corpnms=corpnm.split(",");
		incodes=incode.split(",");
		for(var i=0;i<corpnms.length;i++){
			 obj =  new Object();
			 obj["corpnm"]=corpnms[i];
			 obj["incode"]=incodes[i];
			 param[i]=obj;
		}
	}else{
		 obj =  new Object();
		 obj["corpnm"]=corpnm;
		 obj["incode"]=incode;
		 param[0]=obj;
	}
	$('#detail_dialog').dialog('open');
	$("#gridh").datagrid('loadData',param);
}

function coperatorLink(val,row,index){  
	var add = '<div><a href="javascript:void(0)" id="addBut" onclick="addRow(arguments[0])"><img title="增行" style="margin:0px 20% 0px 20%;" src="../../images/add.png" /></a>';
	var del = '<a href="javascript:void(0)" id="delBut" onclick="delRow(this)"><img title="删行" src="../../images/del.png" /></a></div>';
    return add + del;  
}

/**
 * 增行
 */
function addRow(e){
	e.stopPropagation();
	endBodyEdit();
	if(status == 'brows') {
		return ;
	}
	if(isCanAdd()){
		$('#cardGrid').datagrid('appendRow',{isCharge:chargeData[0].value});
		editIndex = $('#cardGrid').datagrid('getRows').length - 1;
		$('#cardGrid').datagrid('beginEdit',editIndex);
	}else{
		Public.tips({
			content : "请先录入必输项",
			type : 2
		});
		return;
	}
}

/**
 * 删行
 */
function delRow(ths) {
	endBodyEdit();
	if(status == 'brows') {
		return ;
	}
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
	if(tindex == editIndex){
		var rows = $('#cardGrid').datagrid('getRows');
		if(rows && rows.length > 1){
			$('#cardGrid').datagrid('deleteRow', Number(tindex));   //将索引转为int型，否则，删行后，剩余行的索引不重新排列
		}
	}else{
		if(isCanAdd()){
			var rows = $('#cardGrid').datagrid('getRows');
			if(rows && rows.length > 1){
				$('#cardGrid').datagrid('deleteRow', Number(tindex));   //将索引转为int型，否则，删行后，剩余行的索引不重新排列
			}
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
 * 行编辑结束事件
 */
function endBodyEdit(){
    var rows = $("#cardGrid").datagrid('getRows');
 	for ( var i = 0; i < rows.length; i++) {
 		$("#cardGrid").datagrid('endEdit', i);
 	}
};

/**
 * 能否增行
 * @returns {Boolean}
 */
function isCanAdd() {
    if (editIndex == undefined) {
        return true;
    }
    if ($('#cardGrid').datagrid('validateRow', editIndex)) {
        $('#cardGrid').datagrid('endEdit', editIndex);
        editIndex = undefined;
        return true;
    } else {
        return false;
    }
}

