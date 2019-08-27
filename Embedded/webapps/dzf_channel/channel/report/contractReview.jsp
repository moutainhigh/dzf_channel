<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<%@ page import="java.util.Calendar"%>
<%@ page import="java.text.SimpleDateFormat"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>合同查询</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../../css/contractconfrim.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/report/contractReview.js");%> charset="UTF-8" type="text/javascript"></script>
<%-- <script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/report/repcommon.js");%> charset="UTF-8" type="text/javascript"></script>   --%>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/contract/showfile.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/contract/rejectreason_select.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../jslib/build/layer.js");%> charset="UTF-8" type="text/javascript"></script>
<style type="text/css">
    .right_menu {
        display:inline-block;position:relative;
    }
    .right_menu .more_div a {
        padding: 1px 5px;
        text-align: center;
    }
    .mod-toolbar-top {
		margin-right: 0px !important; 
	}
</style>
</head>
<%
	String login_user = (String) session.getAttribute(IGlobalConstants.login_user);
	String period = AdminDateUtil.getPeriod();
	//获取当前月期间
	Calendar e = Calendar.getInstance();
	String ym = new SimpleDateFormat("yyyy-MM").format(e.getTime());
	e.add(Calendar.MONTH, +2);
	String bym = new SimpleDateFormat("yyyy-MM").format(e.getTime());//当前日期（往后推2个月）年-月
%>					
<body>
	<input id="uid" name="uid" type="hidden" value=<%= login_user %>> 
	<input id="period" name="period" type="hidden" value=<%= period %>> 
	<div id="List_panel" class="wrapper" data-options="closed:false">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<div id="cxjs" class="h30 h30-arrow">
						<label class="mr5">查询：</label>
						<strong id="jqj"></strong>
						<span class="arrow-date"></span>
					</div>
				</div>
				<div class="left mod-crumb">
					<span class="cur"></span>
				</div>
				<div class="left mod-crumb">
					<div style="margin:4px 0px 0px 10px;float:left;font-size:14px;">
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; margin-right:15px;" 
							onclick="queryData(1)">待审核</a>
					</div>
					<div style="margin:4px 0px 0px 10px;float:left;font-size:14px;">
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; margin-right:15px;" 
							onclick="queryData(2)">已驳回</a>
					</div>
				</div>
				
				<div class="right">
					<div class="right_menu">
			              <a href="javascript:void(0);" class="ui-btn menu-btn" onclick="onExport()">导出<em></em></a>
			              <s class="middle_info"></s>
			              <ul class="more_div position_more" style="display:none; width:95px;left:-15px;">
			                  <li><a href="javascript:void(0);" onclick="onExportAll()">全部导出</a></li>
			              </ul>
			        </div>
					
				</div>
			</div>
		</div>
		
		<!-- 查询对话框 begin -->
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:420px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<form id="query_form">
				<h3>
					<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<input id="dqdate" type="radio" name="seledate" checked />
					<label style="text-align:right;width:70px;">到期月份：</label>
					<font><input type="text" id="stdate" class="easyui-textbox" data-options="editable:false"
						style="width:130px;height:27px;" value=<%=ym%> /><font>
					<font>-</font>
					<font><input type="text" id="ovdate" class="easyui-textbox" data-options="editable:false"
						style="width:130px;height:27px;" value=<%=bym%> /><font>
				</div>
				<div class="time_col time_colp10">
					<input id="tddate" type="radio" name="seledate" />
					<label style="width: 70px;text-align:right">提单日期：</label>
					<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
					<font>-</font>
					<font><input name="edate" type="text" id="edate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
				</div>
				<div class="time_col time_colp10">
					<input id="kkdate" type="radio" name="seledate" />
					<label style="width: 70px;text-align:right">扣款日期：</label>
					<font><input name="bperiod" type="text" id="bperiod" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
					<font>-</font>
					<font><input name="eperiod" type="text" id="eperiod" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">纳税人资格：</label>
					<select id="corptype" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:100px;height:28px;" editable="false">
						<option value="-1">全部</option>
						<option value="1">小规模</option>
						<option value="2">一般人</option>
					</select>
					<label style="width:74px;text-align:right">套餐类型：</label>
					<select id="comptype" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:100px;height:28px;" editable="false">
						<option value="-1">全部</option>
						<option value="20">个体户</option>
						<option value="99">非个体户</option>
					</select>
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">合同状态：</label>
					<select id="destatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:100px;height:28px;">
						<option value="-1">全部</option>
						<option value="5">待审核</option>
						<option value="1">已审核</option>
						<option value="9">已终止</option>
						<option value="10">已作废</option>
						<option value="8">服务到期</option>
						<option value="7">已驳回</option>
					</select>
					<label style="width:74px;text-align:right">合同类型：</label>
					<select id="qtype" name="qtype" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:100px;height:28px;">
						<option value="-1">全部</option>
						<option value="1">正常提单</option>
						<option value="2">纳税人变更</option>
					</select>
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">客户类型：</label>
					<select id="isncust" class="easyui-combobox" data-options="panelHeight:'auto'" 
						style="width:100px;height:28px;">
						<option value="">全部</option>
						<option value="N">非存量客户</option>
						<option value="Y">存量客户</option>
					</select>
				</div>
				<div class="time_col time_colp10">
				<label style="text-align:right;width:85px;">大区：</label> 
					<input id="aname"  name="aname" class="easyui-combobox" style="width:100px; height: 28px;" 
						data-options="required:false,valueField:'name',textField:'name',panelHeight:100" editable="false" />  
					<label style="width:74px;text-align:right">渠道经理：</label>
					<input id="mid"  name="mid" class="easyui-combobox" style="width:100px; height: 28px;" 
						data-options="required:false,valueField:'id',textField:'name',panelHeight:100" editable="false" />  
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">加盟商：</label>
					<input id="channel_select" class="easyui-textbox" style="width:284px;height:28px;"/>
					<input id="pk_account" type="hidden">
				</div>
				<div class="time_col time_colp10">
					<label style="width:85px;text-align:right">客户：</label>
					<input id="corpkna_ae" class="easyui-textbox" style="width:284px;height:28px;"/>
					<input id="corpkid_ae" name="corpkid" type="hidden"> 
				</div>
			</form>
			<p>
				<a class="ui-btn save_input" onclick="clearParams()">清除</a>
				<a class="ui-btn save_input" onclick="reloadData()">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		<!-- 查询对话框end -->
		
		<!-- 查看加盟商 -->
		<div id="chnDlg"></div>
		<!-- 公司列表 -->
		<div id="gs_dialog"></div>
		<div id="chnBtn" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#chnDlg').dialog('close');" style="width:90px">取消</a>
		</div>
		
		<!-- 合同详情 begin  -->
		<div id="info_Dialog" class="easyui-dialog" style="width:1160px;height:90%;background:#FFF" data-options="closed:true">

			<div style="height: 100%; overflow: auto;">
				<div class="time_col time_colp11 Marketing" style="margin:0 auto;height:50px;line-height:50px;background:#eff1f2;">
					<div style="width:14%;display: inline-block;float: right;margin-top:10px;">
						<a class="ui-btn ui-btn-xz" onclick="$('#info_Dialog').dialog('close');">返回</a>
					</div>
				</div>
				<form id = "infofrom" method="post" enctype="multipart/form-data">
				
					<!-- 变更信息 begin -->
					<div id = "changeinfo" style="height:0;">
						<div class="time_col time_colp11 ">
							<label style="width: 100px;text-align:center;color:#1b8cf2;font-weight: bold;">变更信息</label>
						</div>
						<div class="time_col time_colp11 " style="margin-top:10px;">
							<div class="decan" style="margin-top: 10px;">
								<div style="width: 30%; display: inline-block">
									<label style="width:32%; text-align:right;">变更原因：</label>
									<input id="ichangereason" name="changereason" class="easyui-textbox" data-options="readonly:true"
										style="width:60%;height:28px;text-align:left">
								</div>
								<div style="width: 42%; display: inline-block">
									<label style="text-align: right; width: 20%;">备注：</label>
									<input id="ichangememo" name="changememo" class="easyui-textbox" data-options="readonly:true"
										style="width:70%;height:28px;text-align:left">
								</div>
								<div style="width:25.5%; display:inline-block">
									<label style="text-align: right; width: 40%;white-space: nowrap;">变更日期：</label> 
									<input id="changedate" name="changedate" class="easyui-textbox"  data-options="readonly:true"
									style="width:40%;height:28px;text-align:left"> 
								</div>
							</div>
							<div class="time_col time_colp11">
								<div id = "addclass" class="decan">
									<div style="width:24%; display:inline-block">
										<label style="text-align: right; width: 40%;">终止期间：</label> 
										<input id="istperiod" name="stperiod" class="easyui-textbox" data-options="readonly:true"
											style="width:56%;height:28px;text-align:left">
									</div>
									<div style="width:24%; display:inline-block">
										<label style="text-align: right; width: 40%;">退回扣款：</label> 
										<input id="iremny" name="remny" class="easyui-numberbox" 
											data-options="readonly:true,precision:2,groupSeparator:','"
											style="width:40%;height:28px;text-align:left">
									</div>
									<div style="width:24%; display:inline-block">
										<label style="text-align: right; width: 40%;white-space: nowrap;">变更后合同金额：</label> 
										<input id="inchtlmny" name="nchtlmny" class="easyui-numberbox" 
											data-options="readonly:true,precision:2,groupSeparator:','"
											style="width: 40%; height: 28px; text-align: left">
									</div>
									<div style="width:24%; display:inline-block">
										<label style="text-align: right; width: 40%;white-space: nowrap;">变更后扣款金额：</label> 
										<input id="inchsumny" name="nchsumny" class="easyui-numberbox" 
											data-options="readonly:true,precision:2,groupSeparator:','"
											style="width:40%;height:28px;text-align:left"> 
									</div>
								</div>
							</div>
						</div>
					</div>
					<!-- 变更信息 end -->
					
					<!-- 扣款信息begin -->
					<div id = "dedinfo" style="height:0;">
						<div class="time_col time_colp11 ">
							<label style="width: 100px;text-align:center;color:#1b8cf2;font-weight: bold;">扣款信息</label>
						</div>
						<div class="time_col time_colp11 " style="margin-top:10px;">
							<input id="iid" name="id" type="hidden">
							<input id="icontractid" name="contractid" type="hidden">
							<input id="itstp" name="tstp" type="hidden">
							<input id="iarea" name="area" type="hidden">
							<input id="icorpid" name="corpid" type="hidden">
							<input id="ipid" name="pid" type="hidden">
							<input id="icorpkid" name="corpkid" type="hidden">
							<input id="icorpkna" name="corpkna" type="hidden">
							<input id="ibdate" name="bdate" type="hidden">
							<input id="iedate" name="edate" type="hidden">
							<input id="itypemin" name="typemin" type="hidden">
							<input id="icorpnm" name="corpnm" type="hidden">
							<input id="iadviser" name="adviser" type="hidden">
							<input id="isubmitime" name="submitime" type="hidden">
							<input id="iisncust" name="isncust" type="hidden">
							
							<input id="indemny" name="ndemny" type="hidden"><!-- 预付款扣款金额 -->
							<input id="inderebmny" name="nderebmny" type="hidden"><!-- 返点款扣款金额 -->
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">扣款比例：</label>
								<input id="ipropor" name="propor" class="easyui-numberbox" data-options="readonly:true"
									style="width:20%;height:28px;text-align:right; ">%
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">扣款金额：</label>
								<input id="indesummny" name="ndesummny" class="easyui-numberbox" 
									style="width:56%;height:28px;text-align:left;"
									data-options="readonly:true,precision:2,groupSeparator:','" >
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">扣费日期：</label>
								<input id="idedate" name="dedate" class="easyui-textbox"  data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left"></input>
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">经办人：</label>
								<input id="ivopernm" name="vopernm" class="easyui-textbox" data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left; ">
								<input id="ivoper" name="voper" type="hidden">
							</div>
						</div>
					</div>
					<!-- 扣款信息end -->
				
					<!-- 合同信息 begin -->
					<div>
						<div class="time_col time_colp11 ">
							<label style="width: 100px;text-align:center;color:#1b8cf2;font-weight: bold;">合同信息</label>
						</div>
						<div class="time_col time_colp11 ">
							<div class="decan" style="width:48%;display: inline-block;">
								<label style="width:20%;text-align: right;">客户名称：</label>
								<input id="icorpkna" name="corpkna" class="easyui-textbox" data-options="readonly:true" 
									style="width:76%;height:28px;text-align:left;">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">签订日期：</label>
								<input id="isigndate" name="signdate" class="easyui-textbox"  data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">业务类型：</label>
								<input id="itypeminm" name="typeminm" class="easyui-textbox" data-options="readonly:true"
									style="width:56%;height:28px;text-align:left;">
							</div>
						</div>
						<div class="time_col time_colp11 ">
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">纳税人资格：</label>
								<input id="ichname" name="chname" class="easyui-textbox" data-options="readonly:true"
									style="width:56%;height:28px;text-align:left;">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-indent: 15px;">代账费(元/月):</label>
								<input id="inmsmny" name="nmsmny" class="easyui-numberbox"  
									data-options="readonly:true,precision:2,groupSeparator:','"
									style="width:56%;height:28px;text-align:left;">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">账本费：</label>
								<input id="inbmny" name="nbmny" class="easyui-numberbox"  
									data-options="readonly:true,precision:2,groupSeparator:','"
									style="width:56%;height:28px;text-align:left"></input>
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">合同金额：</label>
								<input id="intlmny" name="ntlmny" class="easyui-numberbox"  
									data-options="readonly:true,precision:2,groupSeparator:','"
									style="width:56%;height:28px;text-align:left;">
							</div>
						</div>
						<div class="time_col time_colp11 ">
							<div class="decan" style="width:24%;display:inline-block;">
								<label style="width:40%;text-align: right;">合同周期(月)：</label>
								<input id="icontcycle" name="contcycle" class="easyui-textbox" data-options="readonly:true" 
									style="width:56%; height: 28px; text-align:left;">
							</div>
							<div class="decan" style="width:24%;display:inline-block;">
								<label style="width:40%;text-align: right;">收款周期(月)：</label>
								<input id="irecycle" name="recycle" class="easyui-textbox" data-options="readonly:true" 
									style="width: 56%; height: 28px; text-align:left;">
							</div>
							<div class="decan" style="width: 24%;display:inline-block;">
								<div class="time_col">
									<label style="width: 109px;text-align: right;">服务期限：</label> 
									<input type="text" id="ibperiod" name="bperiod" class="easyui-textbox" data-options="readonly:true"
										style="width:70px; height: 28px; " >-
									<input type="text" id="ieperiod" name="eperiod" class="easyui-textbox" data-options="readonly:true"
									 	style="width:70px; height: 28px;">
								</div>
							</div>
							<div class="decan" style="width:24%;display:inline-block;">
								<label style="width:40%;text-align: right;">合同状态：</label>
								<input id="istatusname" name="statusname" class="easyui-textbox" data-options="readonly:true" 
									style="width:56%; height: 28px; text-align:left;">
							</div>
						</div>
						<div class="time_col time_colp11 ">
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">合同编号：</label>
								<input id="ivccode" name="vccode"  class="easyui-textbox" data-options="readonly:true" 
									style="width:56%;height:28px;text-align:left;">
							</div>
							<div class="decan" style="width:24%;display: inline-block;">
								<label style="width:40%;text-align: right;">存量客户：</label>
								<input type="checkbox" id="iisncust" name="isncust" value="是" onclick="return false;">
							</div>
						</div>
						<!-- 附件信息begin -->
						<div id ="ifileshow" class="menu_entrance menu_entrances" style="margin-top:0;margin-right:5px;display:none;">
							<div class="entrance_block_tu" id="stpght" style="height:auto;width:99%;">
								<ul class="tu_block" id="ifiledocs"></ul>
							</div>
						</div>
						<!-- 附件信息end -->
					</div>
				</form>
				<!-- 驳回历史 -->
                <div id="rejeson">
      			</div>
				<!-- 驳回历史 -->
				<div id="ifiledoc"></div>
			<!-- 合同信息 end -->
			</div>
		</div>
		<!-- 合同变更  end  -->
		
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
	</div>
	
</body>
</html>