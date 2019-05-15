var contextPath = DZF.contextPath;
var status="brows"

$(window).resize(function(){ 
	$('#grid').datagrid('resize',{ 
		height : Public.setGrid().h,
		width : 'auto'
	});
});

$(function() {
	initQry();
	load();
	initCard();
});

//初始化查询
function initQry(){
	// 下拉按钮的事件
	$("#jqj").on("mouseover", function() {
		$("#qrydialog").show();
		$("#qrydialog").css("visibility", "visible");
	});
	initQryCommbox();
	initChannel();
	initCorpk();
}

function initCard(){
	initPeriod("#cperiod");
	initCardCorp();//卡片界面，加盟商
	initCardCorpk();//卡片界面，客户
}

/**
 * 数据表格初始化
 */
function load(){
	var vince=$('#ovince').combobox('getValue');
	if(isEmpty(vince)){
		vince=-1;
	}
	$('#grid').datagrid({
		url : DZF.contextPath + "/report/personStatis!query.action",
		queryParams:{
			'aname' : $('#aname').combobox('getValue'),
			'ovince' :vince,
			'uid' : $('#uid').combobox('getValue'),
			'corps' : $("#pk_account").val(),
		},
		striped : true,
		title : '',
		rownumbers : true,
		height : Public.setGrid().h,
		singleSelect : false,
		showRefresh : false,// 不显示分页的刷新按钮
		showFooter : true,
		border : true,
		remoteSort:false,
		columns : [[	{ field : 'corpid',    title : '会计公司主键', hidden : true,},
		                { field : 'aname',  title : '大区', width : 100,halign:'center',align:'left',},
		                { field : 'uname',  title : '区总', width : 100,halign:'center',align:'left',},
		                { field : 'provname',  title : '省份', width : 160,halign:'center',align:'left',}, 
		                { field : 'incode',  title : '加盟商编码', width : 140,halign:'center',align:'left',},
		                { field : 'corpnm', title : '加盟商名称', width:240,halign:'center',align:'left',},
			            { field : 'chndate', title : '加盟日期', width:100,halign:'center',align:'center',},
			            { field : 'custnum', title : '总客户数', width:80,halign:'center',align:'right',},
		                { field : 'cuname',  title : '会计运营经理', width : 120,halign:'center',align:'left',},
		                { field : 'jms01',  title : '机构负责人', width : 80,halign:'center',align:'right',},
		                { field : 'meiyong1',  title : '会计团队总人数', width : 160,halign:'center',align:'right',},
		                { field : 'ktotal',  title : '人员占比(%)', width : 90,halign:'center',align:'right',formatter:formatMny},
		                { field : 'lznum',  title : '离职数', width : 60,halign:'center',align:'right',},
		                { field : 'ltotal',  title : '流失率(%)', width : 80,halign:'center',align:'right',formatter:formatMny},
		                { field : 'meiyong2',  title : '销售团队总人数', width : 160,halign:'center',align:'right',},
		                { field : 'xtotal',  title : '人员占比(%)', width : 90,halign:'center',align:'right',formatter:formatMny},
		                { field : 'total',  title : '总用户数', width : 70,halign:'center',align:'right',},
	                ]],
		onLoadSuccess : function(data) {},
	});
}

function add(){
	status="add";
	$('#addDialog').dialog({modal:true});
    $('#addDialog').dialog('open').dialog('center').dialog('setTitle',"新增");
    $('#addForm').form("clear");
}

function save(){
	var flag = $('#addForm').form('validate');
	if(flag == false){
		Public.tips({content:"必输信息为空或格式不正确",type:2});
		return;
	}
	parent.$.messager.progress({
		text : '保存中....'
	});
	$('#addForm').form('submit', {
		url : contextPath + '/chn_set/account!save.action',
		success : function(result) {
			var result = eval('(' + result + ')');
			parent.$.messager.progress('close');
			if (result.success) {
				$('#addDialog').dialog('close');
				status="brows";
				Public.tips({
					content : result.msg,
					type : 0
				});
				reloadData();
			} else {
				Public.tips({
					content : result.msg,
					type : 2
				});
			}
		}
	});
}

/**
 * 取消
 */
function cancel(){
	status="brows";
	$('#addDialog').dialog('close');
}

/**
 * 客户参照初始化
 */
function initCorpk(){
	$('#corpkna_ae').searchbox({
		editable:false,
		prompt:'选择客户',
	    searcher:function(){
	    	$('#gs_dialog').dialog({
	    		width : 520,
	    		height : 490,
	    		readonly : true,
	    		close:true,
	    		title : '选择客户',
	    		modal : true,
	    		href : DZF.contextPath+'/ref/qykh_select.jsp',
	    		queryParams:{
	    			dblClickRowCallback : 'selectCorpk',
	    			corpid:$("#pk_account").val(),
	    		},
	    		buttons : [ {
	    			text : '确认',
	    			handler : function() {
	    				var row = $('#khTable').datagrid('getSelected');
	    				if(row){
	    					selectCorpk(row);
	    				}else{
	    					Public.tips({
	    						content : '请选择需要处理的数据',
	    						type : 2
	    					});
	    				}
	    			}
	    		}, {
	    			text : '取消',
	    			handler : function() {
	    				$('#gs_dialog').dialog('close');
	    			}
	    		}]
	    	});
	    }
	});
}

/**
 * 客户选择事件
 * @param row
 */
function selectCorpk(row){
	$('#corpkna_ae').textbox('setValue',row.uname);
	$('#corpkid_ae').val(row.pk_gs);
	$('#gs_dialog').dialog('close');
}

function initCardCorp(){
    $("#c_corpnm").textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#kj_dialog").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择加盟商',
                    modal: true,
                    href: DZF.contextPath + '/ref/channel_select.jsp',
                    queryParams : {
                    	issingle : "true",
    					ovince :"-5"
    				},
                    buttons: '#kj_buttons'
                });
            }
        }]
    });
}


function initCardCorpk(){
	$('#c_corpkname').searchbox({
		editable:false,
		prompt:'选择客户',
	    searcher:function(){
	    	$('#gs_dialog').dialog({
	    		width : 520,
	    		height : 490,
	    		readonly : true,
	    		close:true,
	    		title : '选择客户',
	    		modal : true,
	    		href : DZF.contextPath+'/ref/account_corpk.jsp',
	    		queryParams:{
	    			dblClickRowCallback : 'selectCardCorpk',
	    			corpid:$("#c_corpid").val(),
	    		},
	    		buttons : [ {
	    			text : '确认',
	    			handler : function() {
	    				var row = $('#khTable').datagrid('getSelected');
	    				if(row){
	    					selectCardCorpk(row);
	    				}else{
	    					Public.tips({
	    						content : '请选择需要处理的数据',
	    						type : 2
	    					});
	    				}
	    			}
	    		}, {
	    			text : '取消',
	    			handler : function() {
	    				$('#gs_dialog').dialog('close');
	    			}
	    		}]
	    	});
	    }
	});
}

function selectCardCorpk(row){
	$('#c_corpkname').textbox('setValue',row.corpkname);
	$('#c_corpkid').val(row.corpkid);
	$('#bperiod').textbox('setValue',row.bperiod);
	$('#tperiod').textbox('setValue',row.bperiod);
	$('#eperiod').textbox('setValue',row.eperiod);
	$('#vccode').val(row.vccode);
	$('#contractid').val(row.contractid);
	$('#gs_dialog').dialog('close');
}

