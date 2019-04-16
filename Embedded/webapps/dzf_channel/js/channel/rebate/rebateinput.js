var contextPath = DZF.contextPath;
var grid;
var editIndex;

$(function(){
	load();
	initRef();
	initArea();
	initQryData();
	fastQry();
	loadJumpData();
});

/**
 * 由别的界面（付款单余额明细）跳转至
 */
function loadJumpData(){
	var obj = Public.getRequest();
	var operate = obj.operate;
	if(operate == "topayc"){
		var id = obj.pk_billid;
		url = contextPath + '/rebate/rebateinput!query.action';
		$('#grid').datagrid('options').url = url;
		$('#grid').datagrid('load', {
			'id' : id,
		});
	}
}

/**
 * 查询框初始化
 */
function initQryData(){
	var qyear = $("#qyear").combobox("getValue");
//	var qjd = $("#qjd").combobox("getText");
	$("#jqj").html(qyear+"年");
	$("#qjd").combobox("setValue", null);
}

/**
 * 参照格式化
 */
function initRef(){
	//渠道经理参照初始化
	$('#manager').textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#manDlg").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择渠道经理',
                    modal: true,
                    href: DZF.contextPath + '/ref/manager_select.jsp',
                    buttons: '#manBtn'
                });
            }
        }]
    });
	
	//查询-加盟商参照初始化
	$('#qcorp').textbox({
		onClickIcon : function() {
			refid = $(this).attr("id");
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
	
	//新增-加盟商参照初始化
	$('#corp').textbox({
		onClickIcon : function() {
			refid = $(this).attr("id");
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
	
	$('#operater').textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#operDlg").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择渠道运营',
                    modal: true,
                    href: DZF.contextPath + '/ref/operater_select.jsp',
                    buttons: '#operBtn'
                });
            }
        }]
    });
}

/**
 * 渠道运营选择事件
 */
function selectOpers(){
	var rows = $('#ogrid').datagrid('getChecked');
	dClickOpers(rows);
}

/**
 * 双击选择渠道运营
 * @param rowTable
 */
function dClickOpers(rowTable){
	var unames = "";
	var uids = [];
	if(rowTable){
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个运营",
				type : 2
			});
			return;
		}
		for(var i=0;i<rowTable.length;i++){
			if(i == rowTable.length - 1){
				unames += rowTable[i].uname;
			}else{
				unames += rowTable[i].uname+",";
			}
			uids.push(rowTable[i].uid);
		}
		$("#operater").textbox("setValue",unames);
		$("#operaterid").val(uids);
	}
	$("#operDlg").dialog('close');
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
 * 渠道经理选择事件
 */
function selectMans(){
	var rows = $('#mgrid').datagrid('getSelections');
	dClickMans(rows);
}

/**
 * 双击选择渠道经理
 * @param rowTable
 */
function dClickMans(rowTable){
	var unames = "";
	var uids = [];
	if(rowTable){
		if (rowTable.length > 300) {
			Public.tips({
				content : "一次最多只能选择300个经理",
				type : 2
			});
			return;
		}
		for(var i=0;i<rowTable.length;i++){
			if(i == rowTable.length - 1){
				unames += rowTable[i].uname;
			}else{
				unames += rowTable[i].uname+",";
			}
			uids.push(rowTable[i].uid);
		}
//		$("#corpkid_ae").val(null);
//		$("#corpkna_ae").textbox("setValue",null);
//		if(!isEmpty(rowTable.length)&&rowTable.length==1){
//			$('#corpkna_ae').textbox('readonly',false);
//		}else{
//			$('#corpkna_ae').textbox('readonly',true);
//		}
		
		$("#manager").textbox("setValue",unames);
		$("#managerid").val(uids);
	}
	 $("#manDlg").dialog('close');
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
	var vprovince="";
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
		vprovince=rowTable[0].ovince;
		$("#" + refid).textbox("setValue",str);
		$("#" + refid + "id").val(corpIds);
		if(refid == "corp"){
			getDebateMny(corpIds[0]);
			$("#ovince").val(vprovince);
		}
	}
	$("#chnDlg").dialog('close');
}

/**
 * 通过加盟商主键、所属年、所属季度计算扣款金额和返点基数
 */
function getDebateMny(cpid){
	$("#debitmny").numberbox("setValue",null);
	$("#basemny").numberbox("setValue",null);
	$("#contnum").numberbox("setValue",null);
	$("#rebatemny").numberbox("setValue", null);
	var year = $("#year").combobox("getValue");
	var season = $("#season").combobox("getValue");
	if(isEmpty(year) || isEmpty(year) || isEmpty(cpid)){
		return;
	}
	$.ajax({
		url : contextPath + '/rebate/rebateinput!queryDebateMny.action',
		dataType : 'json',
		data : {
			'year' : year,
			'season' : season,
			'corpid' : cpid,
		},
		success : function(rs) {
			if (rs.success) {
				var row = rs.rows;
				$("#debitmny").numberbox("setValue",row['debitmny']);
				$("#basemny").numberbox("setValue",row['basemny']);
				$("#contnum").numberbox("setValue",row['contnum']);
				$("#rebatemny").numberbox("setValue", null);
			} 
		},
	});
}

/**
 * 新增监听事件
 */
function initListener(){
	$("#year").combobox({
		onChange : function(n, o) {
			getDebateMny($("#corpid").val());
		}
	});
	$("#season").combobox({
		onChange : function(n, o) {
			getDebateMny($("#corpid").val());
		}
	});
	$("#basemny").numberbox({
		onChange : function(n, o) {
			var debitmny = getFloatValue($("#debitmny").numberbox("getValue"));
			if(getFloatValue(n) > debitmny){
				Public.tips({
					content : "返点基数不能大于扣款金额",
					type : 2
				});
				$("#basemny").numberbox("setValue",debitmny);
				return; 
			}
		}
	});
	$("#rebatemny").numberbox({
		onChange : function(n, o) {
			var basemny = getFloatValue($("#basemny").numberbox("getValue"));
			if(getFloatValue(n) > basemny){
				Public.tips({
					content : "返点金额不能大于返点基数",
					type : 2
				});
				$("#rebatemny").numberbox("setValue",basemny);
				return; 
			}
		}
	});
}

/**
 * 列表表格初始化
 */
function load(){
	grid = $('#grid').datagrid({
		striped : true,
		title : '',
		fitColumns:false,
		rownumbers : true,
		height : Public.setGrid().h,
		pagination : true,// 分页工具栏显示
		pageSize : DZF.pageSize,
		pageList : DZF.pageList,
		singleSelect : false,
		checkOnSelect : false,
		idField : 'rebid',
		frozenColumns :[[ { field : 'ck', checkbox : true },
			              { field : 'operate', title : '操作列',width :'130',halign: 'center',align:'center',formatter:opermatter} ,
		               ]],
		columns : [ [ 
		              { field : 'operdate', title : '录入日期',width :'80',halign: 'center',align:'center' }, 
		              { field : 'vcode', title : '返点单号',width :'120',halign: 'center',align:'left', formatter:codeLink },
		              { field : 'aname', title : '大区',width :'60',halign: 'center',align:'left'}, 
		              { field : 'provname', title : '地区',width :'140',halign: 'center',align:'left'} ,
		              { field : 'mname', title : '渠道经理',width :'100',halign: 'center',align:'left'} ,
		              { field : 'oid', title : '渠道运营',width :'100',halign: 'center',align:'left'} ,
		              { field : 'corpcode', title : '加盟商编码',width :'115',halign: 'center',align:'left'} ,
		              { field : 'corp', title : '加盟商名称',width :'160',halign: 'center',align:'left'}, 
		              { field : 'period', title : '返点所属期间',width :'115',halign: 'center',align:'center'} ,
		              { field : 'contnum', title : '合同数量',width :'100',halign: 'center',align:'right'} ,
		              { field : 'debitmny', title : '扣款金额',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'basemny', title : '返点基数',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'rebatemny', title : '返点金额',width :'115',halign: 'center',align:'right',formatter : formatMny,} ,
		              { field : 'istatus', title : '状态',width :'100',halign: 'center',align:'center', formatter : formatSta} ,
		              { field : 'memo', title : '备注',width :'180',halign: 'center',align:'left'} ,
				      { field : 'rebid', title : '主键', hidden:true},
				      { field : 'tstp', title : '时间戳', hidden:true},
		] ],
		onLoadSuccess : function(data) {
			$('#grid').datagrid("scrollTo",0);
		}
	});
}

/**
 * 单号格式化
 * @param value
 * @param row
 * @param index
 */
function codeLink(value,row,index){
	return '<a href="javascript:void(0)" style="color:blue"  onclick="showInfo(' + index + ')">'+value+'</a>';
}

/**
 * 返点单查看
 * @param index
 */
function showInfo(index){
	var row = $('#grid').datagrid('getData').rows[index];
	$.ajax({
		url : DZF.contextPath + "/rebate/rebateinput!queryInfoById.action",
		dataType : 'json',
		data : row,
		success : function(rs) {
			if (rs.success) {
				$('#showDlg').dialog({
					modal:true
				});//设置dig属性
				$('#showDlg').dialog('open').dialog('center').dialog('setTitle','返点单查看');
				var row = rs.rows;
				$('#showForm').form('clear');
//				setFormValue(row);
				$('#showForm').form('load', row);
				$("#shistory").empty();
				if(row.children != null && row.children.length > 0){
					var history = null;
					var info = "<p class='slideA'>"+
					"<a href='javascript:;' style='color:#FFF;font-size: 14px;' class='btn-slideA active'>审批历史</a>"+
					"</p>"+
					"<div style='height:230px;overflow:auto;'>"+
					"<div style='' id='panelA'>";
					if(row.children.length >= 1){
						history = row.children[0];
						info = info + "<div class='tall' style=' margin-top: 16px;'>"+
						"<div  class='Aroundly'>"+
						"<img src='../../images/tbpng_03.png' style='position: absolute; left: 90px;'/>"+
						"<img src='../../images/pngg_03.png' style='position: absolute; left: 96px; top: 14px;'/>"+
						"</div>"+
						"<div class='state'>"+
						"<div>"+
						"<font>"+history.sendtime+"</font>&emsp;<span>"+history.dealname+"</span>&emsp;<span>"+history.vsnote+"</span>"+
						"</div>";
						if(!isEmpty(history.pronote)){
							info = info +"<div>"+history.pronote+"</div>";
						}
						info = info + "</div>"+
						"</div>";
					}
					if(row.children.length > 1){
						info = info + "<div style='display: none;' id='panela'>"+
						"<div style='width:auto;'>";
						for(var i = 1; i < row.children.length; i++){
							history = row.children[i];
							info = info +"<div class='tall'>"+
							"<div  class='Aroundly'>"+
							"<img style='position: absolute; left: 92px;' src='../../images/xial_03.png' /> "+
							"<img style='position: absolute; left: 96px; top: 8px;' src='../../images/pngg_03.png' />"+
							"</div>"+
							"<div class='state'>"+
							"<div>"+
							"<font>"+history.sendtime+"</font>&emsp;<span>"+history.dealname+"</span>&emsp;<span>"+history.vsnote+"</span>"+
							"</div>";
							if(!isEmpty(history.pronote)){
								info = info +"<div>"+history.pronote+"</div>";
							}
							info = info + "</div>"+
							"</div>";
						}
						info = info +"</div>"+"</div>";
					}
					info = info +"<p class='slide'>"+
					"<a href='javascript:;' rel='external nofollow' class='btn-slide active'></a>"+
					"</p>"+
					"</div>"+
					"</div>";
					$("#shistory").append(info);
					historyListen();
                }
				
			} else {
				Public.tips({
					content : rs.msg,
					type : 1
				});
			}
		},
	});
}

/**
 * 状态格式化
 * @param val
 * @param row
 * @param index
 */
function formatSta(val, row, index){
	//状态   0：待提交；1：待确认；2：待审批；3：审批通过；4：已驳回；
	if(val == 0)
		return "待提交";
	if(val == 1)
		return "待确认";
	if(val == 2)
		return "待审批";
	if(val == 3)
		return "审批通过";
	if(val == 4)
		return "已驳回";
}

/**
 * 列操作格式化
 * @param val
 * @param row
 * @param index
 * @returns {String}
 */
function opermatter(val, row, index) {
	if(row.istatus == 0 || row.istatus == 4){//待提交或已驳回
		return '<a href="#" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="onEdit(' + index + ')">修改</a>'
		+' <a href="#" class="ui-btn ui-btn-xz" style="margin-bottom:0px;" onclick="onDelete(this)">删除</a>';
	}
}

/**
 * 查询框-清空
 */
function clearParams(){
	$('#aname').combobox('setValue', null);
	$('#manager').textbox("setValue",null);
	$('#managerid').val(null);
	$('#qcorp').textbox("setValue",null);
	$('#qcorpid').val(null);
	
	$('#operater').textbox("setValue",null);
	$('#operaterid').val(null);
}

/**
 * 查询框-确认
 */
function reloadData(){
	url = contextPath + '/rebate/rebateinput!query.action';
	$('#grid').datagrid('options').url = url;
	var season =  isEmpty($("#qjd").combobox("getValue")) ? -1 : $("#qjd").combobox("getValue");
	$('#grid').datagrid('load', {
		'year' : $("#qyear").combobox("getValue"),
		'season' : season,
		'destatus' : $("#qstatus").combobox("getValue"),
		'mid' : $("#managerid").val(),
		'cpid' : $("#qcorpid").val(),
		'aname' : $("#aname").combobox('getValue'),
		'oid' : $('#operaterid').val(),
	});
	var qyear = $("#qyear").combobox("getValue");
	var qjd = $("#qjd").combobox("getText");
	if(!isEmpty(qjd)){
		$("#jqj").html(qyear+"年-"+qjd);
	}else{
		$("#jqj").html(qyear+"年");
	}
	$("#qrydialog").hide();
}

/**
 * 快捷查询
 * @param type
 */
function qryData(type){
	url = contextPath + '/rebate/rebateinput!query.action';
	$('#grid').datagrid('options').url = url;
	$('#grid').datagrid('load', {
		'qtype' : type,
	});
}

/**
 * 快速过滤
 */
function fastQry(){
	$('#filter_value').textbox('textbox').keydown(function (e) {
		 if (e.keyCode == 13) {
            var filtername = $("#filter_value").val(); 
            if(!isEmpty(filtername)){
            	var queryParams = $('#grid').datagrid('options').queryParams;
            	var rows = $('#grid').datagrid('getRows');
            	clearQryParam(queryParams);
            	if(rows != null && rows.length > 0){
            		//做过查询
            		queryParams.destatus = $("#qstatus").combobox("getValue");
            		queryParams.mid = $("#managerid").val();
            		queryParams.cpid = $("#qcorpid").val();
            		queryParams.year = $("#qyear").combobox("getValue");
            		queryParams.season = isEmpty($("#qjd").combobox("getValue")) ? -1 : $("#qjd").combobox("getValue");
            	}
            	queryParams.cpname = filtername;
          		grid.datagrid('options').url = contextPath + '/rebate/rebateinput!query.action';
          		$('#grid').datagrid('options').queryParams = queryParams;
          		$('#grid').datagrid('reload');
            }
         }
   });
}

/**
 * 清除查询传递查询条件
 * @param queryParams
 */
function clearQryParam(queryParams){
	queryParams.year = null;
	queryParams.season = -1;
	queryParams.qtype = -1;
	queryParams.destatus = -1;
	queryParams.cpid = null;
	queryParams.uid = null;
}

/**
 * 查询框-取消
 */
function closeCx(){
	$("#qrydialog").hide();
}

/**
 * 新增
 */
function onAdd(){
	$('#addDlg').dialog({
		modal:true
	});//设置dig属性
	$('#addDlg').dialog('open').dialog('center').dialog('setTitle','返点单新增');
	$('#addForm').form('clear');
	initListener();
}

/**
 * 保存并新增
 */
function onSaveAdd(){
	var postdata = new Object();
	if($("#addForm").form('validate')){
		postdata["data"] = JSON.stringify(serializeObject($('#addForm')));
	} else {
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return; 
	}
	saveSubmit(true, postdata);
}

/**
 * 保存
 */
function onSave(){
	var postdata = new Object();
	if($("#addForm").form('validate')){
		postdata["data"] = JSON.stringify(serializeObject($('#addForm')));
	} else {
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return; 
	}
	saveSubmit(false, postdata);
}

/**
 * 保存-公用方法
 */
function saveSubmit(isadd, postdata) {
	$.messager.progress({
		text : '数据保存中，请稍候.....'
	});
	$('#addForm').form('submit', {
		url : contextPath + '/rebate/rebateinput!save.action',
		queryParams : postdata,
		success : function(result) {
			var result = eval('(' + result + ')');
			$.messager.progress('close');
			if (result.success) {
				Public.tips({
					content : result.msg,
					type : 0
				})
				var row = result.rows;
				if(isadd){
					$('#addForm').form('clear');
				}else{
					$('#addDlg').dialog('close');
				}
				$('#grid').datagrid('appendRow',row);
			} else {
				Public.tips({
					content : result.msg,
					type : 1
				})
			}
		}
	});
}

/**
 * 修改
 */
function onEdit(index){
	var row = $('#grid').datagrid('getData').rows[index];
	$.ajax({
		url : DZF.contextPath + "/rebate/rebateinput!queryById.action",
		dataType : 'json',
		data : row,
		success : function(rs) {
			if (rs.success) {
				editIndex = index;
				$('#editDlg').dialog({
					modal:true
				});//设置dig属性
				$('#editDlg').dialog('open').dialog('center').dialog('setTitle','返点单修改');
				var row = rs.rows;
				$('#editForm').form('clear');
				setFormValue(row);
//				$('#editForm').form('load', row);
				$("#history").empty();
				if(row.children != null && row.children.length > 0){
					var history = null;
					var info = "<p class='slideA'>"+
					"<a href='javascript:;' style='color:#FFF;font-size: 14px;' class='btn-slideA active'>审批历史</a>"+
					"</p>"+
					"<div style='height:230px;overflow:auto;'>"+
					"<div style='' id='panelA'>";
					if(row.children.length >= 1){
						history = row.children[0];
						info = info + "<div class='tall' style=' margin-top: 16px;'>"+
						"<div  class='Aroundly'>"+
						"<img src='../../images/tbpng_03.png' style='position: absolute; left: 90px;'/>"+
						"<img src='../../images/pngg_03.png' style='position: absolute; left: 96px; top: 14px;'/>"+
						"</div>"+
						"<div class='state'>"+
						"<div>"+
						"<font>"+history.sendtime+"</font>&emsp;<span>"+history.dealname+"</span>&emsp;<span>"+history.vsnote+"</span>"+
						"</div>"+
						"<div>"+history.pronote+"</div>"+
						"</div>"+
						"</div>";
					}
					if(row.children.length > 1){
						info = info + "<div style='display: none;' id='panela'>"+
						"<div style='width:auto;'>";
						for(var i = 1; i < row.children.length; i++){
							history = row.children[i];
							info = info +"<div class='tall'>"+
							"<div  class='Aroundly'>"+
							"<img style='position: absolute; left: 92px;' src='../../images/xial_03.png' /> "+
							"<img style='position: absolute; left: 96px; top: 8px;' src='../../images/pngg_03.png' />"+
							"</div>"+
							"<div class='state'>"+
							"<div>"+
							"<font>"+history.sendtime+"</font>&emsp;<span>"+history.dealname+"</span>&emsp;<span>"+history.vsnote+"</span>"+
							"</div>"+
							"<div>"+history.pronote+"</div>"+
							"</div>"+
							"</div>";
						}
						info = info +"</div>"+"</div>";
					}
					info = info +"<p class='slide'>"+
					"<a href='javascript:;' rel='external nofollow' class='btn-slide active'></a>"+
					"</p>"+
					"</div>"+
					"</div>";
					$("#history").append(info);
					historyListen();
					initEditListener();
                }
				
			} else {
				Public.tips({
					content : rs.msg,
					type : 1
				});
			}
		},
	});
}

/**
 * 修改设置表单的值
 * @param row
 */
function setFormValue(row){
	$('#erebid').val(row.rebid);
	$('#evcode').textbox('setValue',row.vcode);
	$('#eshowdate').textbox('setValue', row.showdate);
	$('#eyear').val(row.year);
	$('#eseason').val(row.season);
	$('#ecorp').textbox('setValue',row.corp);
	$('#ecorpid').val(row.corpid);
	$('#econtnum').numberbox('setValue', row.contnum);
	$('#edebitmny').numberbox('setValue', row.debitmny);
	$('#ebasemny').numberbox('setValue', row.basemny);
	$('#erebatemny').numberbox('setValue', row.rebatemny);
	$('#ememo').textbox('setValue',row.memo);
	$('#estatusname').textbox('setValue',row.statusname);
	$('#eopername').textbox('setValue',row.opername);
	$('#eoperdate').textbox('setValue',row.operdate);
}

/**
 * 修改监听事件
 */
function initEditListener(){
//	$("#eyear").combobox({
//		onChange : function(n, o) {
//			getEditDebateMny($("#ecorpid").val());
//		}
//	});
//	$("#eseason").combobox({
//		onChange : function(n, o) {
//			getEditDebateMny($("#ecorpid").val());
//		}
//	});
	$("#ebasemny").numberbox({
		onChange : function(n, o) {
			var debitmny = getFloatValue($("#edebitmny").numberbox("getValue"));
			if(getFloatValue(n) > debitmny){
				Public.tips({
					content : "返点基数不能大于扣款金额",
					type : 2
				});
				$("#ebasemny").numberbox("setValue",debitmny);
				return; 
			}
		}
	});
	$("#erebatemny").numberbox({
		onChange : function(n, o) {
			var basemny = getFloatValue($("#ebasemny").numberbox("getValue"));
			if(getFloatValue(n) > basemny){
				Public.tips({
					content : "返点金额不能大于返点基数",
					type : 2
				});
				$("#erebatemny").numberbox("setValue",basemny);
				return; 
			}
		}
	});
}

///**
// * 通过加盟商主键、所属年、所属季度计算扣款金额和返点基数
// */
//function getEditDebateMny(cpid){
//	var year = $("#eyear").combobox("getValue");
//	var season = $("#eseason").combobox("getValue");
//	if(isEmpty(year) || isEmpty(year) || isEmpty(cpid)){
//		return;
//	}
//	$.ajax({
//		url : contextPath + '/rebate/rebateinput!queryDebateMny.action',
//		dataType : 'json',
//		data : {
//			'year' : year,
//			'season' : season,
//			'corpid' : cpid,
//		},
//		success : function(rs) {
//			if (rs.success) {
//				var row = rs.rows;
//				$("#edebitmny").numberbox("setValue",row['debitmny']);
//				$("#ebasemny").numberbox("setValue",row['basemny']);
//			} 
//		},
//	});
//}

/**
 * 审批历史按钮点击监听事件
 */
function historyListen(){
	$(".btn-slide").click(function() {
		$("#panela").slideToggle("slow");
		$(this).toggleClass("active");
		return false;
	})
}

/**
 * 修改保存
 */
function onEditSave(){
	var postdata = new Object();
	if($("#editForm").form('validate')){
		postdata["data"] = JSON.stringify(serializeObject($('#editForm')));
	} else {
		Public.tips({
			content : "必输信息为空或格式不正确",
			type : 2
		});
		return; 
	}
	$.messager.progress({
		text : '数据保存中，请稍候.....'
	});
	$('#editForm').form('submit', {
		url : contextPath + '/rebate/rebateinput!save.action',
		queryParams : postdata,
		success : function(result) {
			var result = eval('(' + result + ')');
			$.messager.progress('close');
			if (result.success) {
				Public.tips({
					content : result.msg,
					type : 0
				})
				var row = result.rows;
				$('#editDlg').dialog('close');
				$('#grid').datagrid('updateRow', {
					index : editIndex,
					row : row
				});
			} else {
				Public.tips({
					content : result.msg,
					type : 1
				})
			}
		}
	});
}

/**
 * 删除
 */
function onDelete(ths){
	var tindex = $(ths).parents("tr").attr("datagrid-row-index");
//	var row = $('#grid').datagrid('getData').rows[index];
	var row = $('#grid').datagrid('getData').rows[tindex];
	if (row.istatus != 0 && row.istatus != 4) {
		Public.tips({
			content : '该记录状态不为待提交或已驳回，不允许删除',
			type : 2
		});
		return;
	}
	$.messager.confirm("提示", "你确定要删除吗?", function(r) {
		if (r) {
			$.ajax({
				url : DZF.contextPath + "/rebate/rebateinput!delete.action",
				dataType : 'json',
				data : row,
				success : function(rs) {
					if (rs.success) {
						$('#grid').datagrid('deleteRow', Number(tindex)); 
						$("#grid").datagrid('unselectAll');
						Public.tips({
							content : rs.msg,
							type : 0
						});
					} else {
						Public.tips({
							content : rs.msg,
							type : 1
						});
					}
				},
			});
		}
	});
}

/**
 * 提交
 */
function onCommit(){
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
	$.messager.progress({
		text : '数据处理中....'
	});
	$.ajax({
		type : "post",
		dataType : "json",
		url : contextPath + '/rebate/rebateinput!saveCommit.action',
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
				$('#allotDialog').dialog('close');
				var rerows = result.rows;
				if(rerows != null && rerows.length > 0){
					var map = new HashMap(); 
					for(var i = 0; i < rerows.length; i++){
						map.put(rerows[i].rebid,rerows[i]);
					}
					var index;
					var indexes = new Array();
					for(var i = 0; i < rows.length; i++){
						if(map.containsKey(rows[i].rebid)){
							index = $('#grid').datagrid('getRowIndex', rows[i]);
							indexes.push(index);
						}
					}
					for(var i in indexes){
						$('#grid').datagrid('updateRow', {
							index : indexes[i],
							row : rerows[i]
						});
					}
				}
				$("#grid").datagrid('uncheckAll');
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			$.messager.progress('close');
		}
	});
}

/**
 * 导出
 */
function onExport(){
	var datarows = $('#grid').datagrid("getRows");
	if(datarows == null || datarows.length == 0){
		Public.tips({content:'请选择需导出的数据',type:2});
		return;
	}
	var columns = $('#grid').datagrid("options").columns[0];
	Business.getFile(DZF.contextPath+ '/rebate/rebateinput!onExport.action',{
		'strlist':JSON.stringify(datarows),'printype' : 0,}, true, true);
}


