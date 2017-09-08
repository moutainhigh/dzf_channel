/**
 * 1）扩展jquery easyui tree的节点检索方法。使用方法如下：
 * $("#treeId").tree("search", searchText);
 * 其中，treeId为easyui tree的根UL元素的ID，searchText为检索的文本。
 * 如果searchText为空或""，将恢复展示所有节点为正常状态
 */
(function() {
    $.extend($.fn.tree.methods, {
        /**
         * 扩展easyui tree的搜索方法
         * @param tree easyui tree的根DOM节点(UL节点)的jQuery对象
         * @param searchText 检索的文本
         * @param this-context easyui tree的tree对象
         */
        search: function(jqTree, searchText) {
            //easyui tree的tree对象。可以通过tree.methodName(jqTree)方式调用easyui tree的方法
            var tree = this;

            //获取所有的树节点
            var nodeList = getAllNodes(jqTree, tree);

            //如果没有搜索条件，则展示所有树节点
            searchText = $.trim(searchText);
            if (searchText == "") {
                for (var i = 0; i < nodeList.length; i++) {
                    $(".tree-node-targeted", nodeList[i].target).removeClass("tree-node-targeted");
                    $(nodeList[i].target).show();
                }
                //展开已选择的节点（如果之前选择了）
                var selectedNode = tree.getSelected(jqTree);
                if (selectedNode) {
                    tree.expandTo(jqTree, selectedNode.target);
                }
                return;
            }

            //搜索匹配的节点并高亮显示
            var matchedNodeList = [];
            if (nodeList && nodeList.length > 0) {
                var node = null;
                for (var i = 0; i < nodeList.length; i++) {
                    node = nodeList[i];
                    if (isMatch(searchText, node.text)) {
                        matchedNodeList.push(node);
                    }
                }

                //隐藏所有节点
                for (var i = 0; i < nodeList.length; i++) {
                    $(".tree-node-targeted", nodeList[i].target).removeClass("tree-node-targeted");
                    $(nodeList[i].target).hide();
                }

                //折叠所有节点
                tree.collapseAll(jqTree);

                //展示所有匹配的节点以及父节点
                for (var i = 0; i < matchedNodeList.length; i++) {
                    showMatchedNode(jqTree, tree, matchedNodeList[i]);
                }
            }
        },

        /**
         * 展示节点的子节点（子节点有可能在搜索的过程中被隐藏了）
         * @param node easyui tree节点
         */
        showChildren: function(jqTree, node) {
            //easyui tree的tree对象。可以通过tree.methodName(jqTree)方式调用easyui tree的方法
            var tree = this;

            //展示子节点
            if (!tree.isLeaf(jqTree, node.target)) {
                var children = tree.getChildren(jqTree, node.target);
                if (children && children.length > 0) {
                    for (var i = 0; i < children.length; i++) {
                        if ($(children[i].target).is(":hidden")) {
                            $(children[i].target).show();
                        }
                    }
                }
            }
        },

        /**
         * 将滚动条滚动到指定的节点位置，使该节点可见（如果有滚动条才滚动，没有滚动条就不滚动）
         * @param param {
         *    treeContainer: easyui tree的容器（即存在滚动条的树容器）。如果为null，则取easyui tree的根UL节点的父节点。
         *    targetNode:  将要滚动到的easyui tree节点。如果targetNode为空，则默认滚动到当前已选中的节点，如果没有选中的节点，则不滚动
         * }
         */
        scrollTo: function(jqTree, param) {
            //easyui tree的tree对象。可以通过tree.methodName(jqTree)方式调用easyui tree的方法
            var tree = this;

            //如果node为空，则获取当前选中的node
            var targetNode = param && param.targetNode ? param.targetNode : tree.getSelected(jqTree);

            if (targetNode != null) {
                //判断节点是否在可视区域
                var root = tree.getRoot(jqTree);
                var $targetNode = $(targetNode.target);
                var container = param && param.treeContainer ? param.treeContainer : jqTree.parent();
                var containerH = container.height();
                var nodeOffsetHeight = $targetNode.offset().top - container.offset().top;
                if (nodeOffsetHeight > (containerH - 30)) {
                    var scrollHeight = container.scrollTop() + nodeOffsetHeight - containerH + 30;
                    container.scrollTop(scrollHeight);
                }
            }
        }
    });




    /**
     * 展示搜索匹配的节点
     */
    function showMatchedNode(jqTree, tree, node) {
        //展示所有父节点
        $(node.target).show();
        $(".tree-title", node.target).addClass("tree-node-targeted");
        var pNode = node;
        while ((pNode = tree.getParent(jqTree, pNode.target))) {
            $(pNode.target).show();
        }
        //展开到该节点
        tree.expandTo(jqTree, node.target);
        //如果是非叶子节点，需折叠该节点的所有子节点
        if (!tree.isLeaf(jqTree, node.target)) {
            var children = tree.getChildren(jqTree, node.target);
            if (children && children.length > 0) {
                for (var i = 0; i < children.length; i++) {
                    if ($(children[i].target).is(":hidden")) {
                        $(children[i].target).show();
                    }
                }
            }
        }
    }

    /**
     * 判断searchText是否与targetText匹配
     * @param searchText 检索的文本
     * @param targetText 目标文本
     * @return true-检索的文本与目标文本匹配；否则为false.
     */
    function isMatch(searchText, targetText) {
        return $.trim(targetText) != "" && targetText.indexOf(searchText) != -1;
    }

    /**
     * 获取easyui tree的所有node节点
     */
    function getAllNodes(jqTree, tree) {
        var allNodeList = jqTree.data("allNodeList");
        if (!allNodeList) {
            var roots = tree.getRoots(jqTree);
            allNodeList = getChildNodeList(jqTree, tree, roots);
            jqTree.data("allNodeList", allNodeList);
        }
        return allNodeList;
    }

    /**
     * 定义获取easyui tree的子节点的递归算法
     */
    function getChildNodeList(jqTree, tree, nodes) {
        var childNodeList = [];
        if (nodes && nodes.length > 0) {
            var node = null;
            for (var i = 0; i < nodes.length; i++) {
                node = nodes[i];
                childNodeList.push(node);
                if (!tree.isLeaf(jqTree, node.target)) {
                    var children = tree.getChildren(jqTree, node.target);
                    childNodeList = childNodeList.concat(getChildNodeList(jqTree, tree, children));
                }
            }
        }
        return childNodeList;
    }
})();

$(document).on('keydown', '#zcmc', function(e) {
    if (e.keyCode == 13) {
        $('#treeGrid').tree("search", $(this).val());
    }
});

var grid;
var area_dialog;
$(function() {
    //打开查询框
    $("#query_period").on("mouseover", function() {
        $("#qrydialog").show();
    });
    initQueryData();
    grid = $("#grid").datagrid({
        //		fit: true,
        rownumbers: true,
        border: true,
        striped: true,
        url: '',
        height: Public.setGrid().h,
        width: 'auto',
        pagination: true,
        singleSelect: false,
        pageSize: 300,
        pageList: [300, 500, 1000, 1500, 2000],
        columns: [
            [{
                field: 'ck',
                checkbox: true
            	},
            	{
                    field: 'fname',
                    width: 200,
                    title: '渠道商'
                },
            	{
                    field: 'incode',
                    width: 100,
                    title: '客户编码'
                },
                {
                    field: 'uname',
                    width: 250,
                    title: '客户名称'
                },
                {
                    field: 'bodycode',
                    width: 100,
                    title: '法定代表人',
                    hidden:true,
                },
                {
                    field: 'corprhone',
                    width: 100,
                    title: '法人电话',
                    hidden:true,
                },
                {
                    field: 'chname',
                    width: 150,
                    title: '纳税人资格',
                    hidden:true,
                },
                {
                    field: 'indusname',
                    width: 100,
                    title: '国家标准行业',
                    hidden:true,
                },
                {
                    field: 'cdate',
                    width: 100,
                    title: '录入日期'
                },
                {
                    field: 'fj',
                    width: 150,
                    title: '附件',
                    formatter: function(val, row) {
                    	var str = "";
                    	if (row.corpDocVos) {
							for (var i = 0; i < row.corpDocVos.length; i++) {
								var doc = row.corpDocVos[i];
								str += '<a style="color:blue" href="javascript:void(0)" onclick="downFile(\''
									+ doc.doc_id + '\')">' + doc.doc_name 
									+ '</a>&nbsp;&nbsp;'
							}
							return str
						}
                    },
                    hidden:true,
                },
                {
                    field: 'fname',
                    width: 100,
                    title: '销售人员'
                },
                {
                    field: 'approve_status',
                    width: 100,
                    title: '审批状态',
                    formatter: function(val) {
                    	var display = null;
                    	if (val == 1) {
                    		display = "已审批";
						} else if (val == 2) {
							display = "未通过";
						} else {
							display = "未审批";
						}
                    	return display;
                    }
                },
                {
                    field: 'approve_comment',
                    width: 150,
                    title: '审批意见'
                },
                {
                    field: 'approve_user_name',
                    width: 100,
                    title: '操作人'
                },
                {
                    field: 'approve_time',
                    width: 150,
                    title: '操作时间'
                }
            ]
        ],
        onLoadSuccess: function(result) {
            if (result.success) {
                $("#qrydialog").hide();
            } else {
                Public.tips({
                    content: result.msg,
                    type: 2
                });
            }
        }

    });
    // 渠道商
    $('#channel_select').textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                $("#kj_dialog").dialog({
                    width: 600,
                    height: 480,
                    readonly: true,
                    title: '选择渠道商',
                    modal: true,
                    href: DZF.contextPath + '/ref/channelcorp_select.jsp',
                    buttons: '#kj_buttons',
                    toolbar: '#tb'
                });
            }
        }]
    });
    // 地区
    $("#area_select").textbox({
        editable: false,
        icons: [{
            iconCls: 'icon-search',
            handler: function(e) {
                if (!area_dialog) {
                    area_dialog = $("#area_dialog").dialog({
                        width: 500,
                        height: 500,
                        readonly: true,
                        title: '选择区域',
                        modal: true,
                        href: DZF.contextPath + '/ref/select_area.jsp',
                        buttons: '#area_buttons',
                    });
                } else {
                    area_dialog.dialog("open").dialog("center")
                    $('#zcmc').val('');
                    $('#treeGrid').tree("search", '');
                }
            }
        }]
    });
    
    $("#quick_query").textbox({
    	prompt: "输入客户编码或名称进行定位",
    	onChange: function(val) {
    		$(".pos-background").removeClass("pos-background");
    		var rows = $("#grid").datagrid("getRows");
    		if (val == "" || rows == null || rows.length == 0) {
				return
			}
    		var firstIndex = null;
    		var trs = $("#grid").datagrid("getPanel").find(".datagrid-view2 .datagrid-btable tr");
    		for (var i = 0; i < rows.length; i++) {
				var row = rows[i];
				if (row.incode.indexOf(val) > -1) {
					trs.eq(i).find("td[field=incode]").addClass("pos-background");
				}
				if (row.uname.indexOf(val) > -1) {
					trs.eq(i).find("td[field=uname]").addClass("pos-background");
				}
			}
    		if (firstIndex != null) {
    			$("#grid").datagrid("scrollTo", firstIndex);
			}
    	}
    })
});

function clearParams() {
    $("#query_form").form("clear");
    initQueryData();
    $("#approve_status").combobox("setValue", "");
}

function selectgs() {
    var row = $('#gsTable').datagrid('getSelected');
    if (row) {
        $("#channel_select").textbox('setValue', row.uname);
        $("#pk_account").val(row.pk_gs);
        $("#kj_dialog").dialog('close');
    }
}
// 关闭查询框
function closeCx() {
    $("#qrydialog").hide();
}
// 查询事件
function reloadData() {
    var pk_account = $("#pk_account").val();
//    if (!pk_account) {
//        Public.tips({
//            content: "请选择渠道商",
//            type: 2
//        });
//        return
//    }
    $('#grid').datagrid('options').url = DZF.contextPath +
        '/sys/sys_channel_approve!queryChannelCustomer.action';
    $('#grid').datagrid('load', {
        pk_account: $("#pk_account").val(),
        begindate: $("#startDate").datebox("getValue"),
        enddate: $("#endDate").datebox("getValue"),
        status: $("#approve_status").combobox("getValue")
    });
    $("#qrydialog").hide();
}

function initQueryData() {
    var loginDateStr = Public.getLoginDate();
    var ldate = new Date(loginDateStr);
    ldate.setMonth(ldate.getMonth() - 1);
    ldate = ldate.Format('yyyy-MM-dd');
    $("#startDate").datebox("setValue", ldate);
    $("#endDate").datebox("setValue", loginDateStr);
    $("#query_period").html(ldate + " 至  " + loginDateStr);
}

// 刷新
function refresh() {
    reloadData();
}

function selectArea() {
    var cname;
    var row = $('#treeGrid').tree('getSelected');
    var pName = $('#treeGrid').tree('getParent', row.target);
    if (pName != null) {
        cname = pName.text + "-" + row.text;
    }
    var rName = $('#treeGrid').tree('getParent', pName.target);
    if (rName != null) {
        if (rName.text != "北京市" && rName.text != "天津市" && rName.text != "上海市") {
            cname = rName.text + "-" + pName.text + "-" + row.text;
        } else {
            cname = rName.text + "-" + row.text;
        }
    }
    $('#area_select').textbox("setValue", cname);
    $('#area_dialog').dialog('close');
}

function approve() {
    var row = $('#grid').datagrid("getSelected");
    if (row) {
    	$("#approve_form").form("clear");
    	$("#approve_form input[name=pass]").eq(0).prop("checked", true);
        $("#approve_dialog").dialog({
            title: "审批",
            width: 300,
            height: 230,
            modal: true,
            buttons: "#approve_buttons"
        });
    } else {
        Public.tips({
            content: "请选择客户",
            type: 2
        });
    }
}

function doApprove() {
    var rows = $('#grid').datagrid("getSelections");
    $.messager.progress();
    var data = serializeObject($("#approve_form"));
    var postRow = new Array();
    var status = data.pass == "Y" ? 1 : 2;
    var comment = data.comment;
    for (var i = 0; i < rows.length; i++) {
    	var row = rows[i];
    	postRow.push({
    		pk_gs: row.pk_gs,
    		approve_status: status,
    		approve_comment: comment,
    		hasaccount: row.hasaccount
    	});
	}
    $.ajax({
        type: "post",
        dataType: "json",
        url: DZF.contextPath + '/sys/sys_channel_approve!doApprove.action',
        data: {corps: JSON.stringify(postRow)},
        success: function(data) {
            $.messager.progress("close");
            if (!data.success) {
                Public.tips({
                    content: data.msg,
                    type: 2
                });
            } else {
                $('#approve_dialog').dialog('close');
                Public.tips({
                    content: data.msg
                });
                refresh();
            }
        }
    });
}

function abandonApprove() {
	var rows = $('#grid').datagrid("getSelections");
	var postRow = new Array();
	for (var i = 0; i < rows.length; i++) {
		var row = rows[i];
		if (!row.approve_status) {
			return Public.tips({
				content: "客户编码" + row.incode + "审批状态为未审批",
				type: 2
			});
		}
		postRow.push({
    		pk_gs: row.pk_gs,
    		hasaccount: row.hasaccount,
    		approve_status: row.approve_status
    	});
	}
    $.messager.progress();
    $.ajax({
        type: "post",
        dataType: "json",
        url: DZF.contextPath + '/sys/sys_channel_approve!abandonApprove.action',
        data: {corps: JSON.stringify(postRow)},
        success: function(data) {
            $.messager.progress("close");
            if (!data.success) {
                Public.tips({
                    content: data.msg,
                    type: 2
                });
            } else {
                Public.tips({
                    content: data.msg
                });
                refresh();
            }
        }
    });
}
// 附件下载
function downFile(pk_doc){
	Business.getFile(DZF.contextPath + '/sys/sys_channel_approve!downFile.action', {pk_doc:pk_doc}, true, true);
}
