<%@page import="java.util.Map"%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.IGlobalConstants"%>
<%
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Date nowDate = new Date();//当天日期
	Calendar c=Calendar.getInstance();
	c.setTime(nowDate);
	c.add(Calendar.MONTH, -1);//向前推一个月
	Date beforeDate=c.getTime();

%>
<!DOCTYPE html>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="renderer" content="webkit|ie-stand|ie-comp">
<title>发票管理</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out,"../../js/channel/invoice/sys_fpmng.js");%> charset="UTF-8" type="text/javascript"></script>
<style type="text/css">
.pos-background {
	background-color: yellow;
}
</style>
</head>
<body>
	<div class="wrapper" style="overflow:hidden">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<div id="cxjs" class="h30 h30-arrow">
						<label class="mr5">查询：</label>
						<strong id="querydate"><%=sdf.format(beforeDate)%> 至 <%=sdf.format(nowDate)%></strong>
						<span class="arrow-date"></span>
					</div>
				</div>
				
				<div class="left mod-crumb">
					<div style="margin:4px 0px 0px 10px;float:right;font-size:14px;">
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; margin-right:15px;" onclick="qryData(1)">待开票</a>
					</div>
				</div>
				<div class="left mod-crumb">
					<div class="h30 h30-arrow" id="filter">
						<input style="height:28px;width:220px" class="easyui-textbox" id="filter_value" prompt="请输入加盟商名称,按Enter键 "/> 
					</div>
				</div>
				
				<div class=right>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="">电票余量</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onAutoBill()">电子票</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onBilling()">纸质票</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onEdit()">修改</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onDelete()">删除</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="onExport()">导出</a>
				</div>
			</div>
		</div>
		<div class="qijian_box" id="qrydialog" style="display: none; width: 420px; height: 230px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<form id="query_form">
				<h3>
					<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
				</h3>
				<div class="time_col time_colp10">
					<label style="width: 80px;text-align:right">日期：</label>
					<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'" value="<%=sdf.format(beforeDate)%>" /></font>
					<font>-</font>
					<font><input name="edate" type="text" id="edate" class="easyui-datebox" 
						data-options="width:130,height:27,editable:true,validType:'checkdate'"  value="<%=sdf.format(nowDate)%>" /></font>
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">加盟商：</label>
					<input id="channel_select" class="easyui-textbox" style="width:219px;height:28px;" />
					<input id="pk_account" type="hidden">
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">发票状态：</label>
					<select id="istatus" class="easyui-combobox" data-options="panelHeight:'auto'" style="width:219px;height:28px;">
						<option value="-1">全部</option>
						<option value="1">待开票</option>
						<option value="2">已开票</option>
						<option value="3">开票失败</option>
					</select>
				</div>
				<div class="time_col time_colp10">
					<label style="width:80px;text-align:right">发票类型：</label>
					<select id="itype" class="easyui-combobox" data-options="panelHeight:'auto'" style="width:219px;height:28px;">
						<option value="-1">全部</option>
						<option value="0">专用发票</option>
						<option value="1">普通发票</option>
						<option value="2">电子发票</option>
					</select>
				</div>
			</form>
			<p>
				<a class="ui-btn save_input" onclick="clearParams()">清除</a>
				<a class="ui-btn save_input" onclick="reloadData()">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		<div class="mod-inner">
			<div id="dataGrid" class="grid-wrap">
				<table id="grid"></table>
			</div>
		</div>
		<div id="fp_dialog" class="easyui-dialog" title="发票申请" 	data-options="modal:true,closed:true" style="width:900px;height:450px;">
			<div class="card_table" style="height:100%;min-width:890px;">
				<div data-options="region:'north'" style="padding-top:20px;">
					<form id="fp_form" method="post">
						<input type='hidden' id="id" name="id"/>
						<input type='hidden' id="corpid" name="corpid"/>
						<input id="tempprice" type='hidden' name="tempprice"/>
						<div class="time_col time_colp11">
							<div style="width: 32%;display: inline-block">
								<label style="text-align:right;width:34%;">付款类型：</label>
								<select id="paytype" name="paytype" class="easyui-combobox" data-options="editable:false,readonly:true,panelHeight:50" style="width:62%;height:28px;text-align:left">
									<option value="0">预付款</option>
									<option value="1">加盟费</option>
								</select>
							</div>
							<div style="width: 32%;display: inline-block">
								<label style="text-align:right;width:34%;">可开票金额：</label>
								<input id="tprice" name="tprice" class="easyui-numberbox" data-options="required:true,min:0,precision:2,readonly:true,groupSeparator:','" style="width:62%;height:28px;text-align:left">
							</div>
								<div style="width:32%;display: inline-block">
								<label style="text-align:right;width:34%;">开票金额：</label>
								<input id="iprice" name="iprice" class="easyui-numberbox" data-options="required:true,min:0,max:9999999999,precision:2,groupSeparator:','" style="width:62%;height:28px;text-align:left">
							</div>
						</div>
						<div class="time_col time_colp11">
								<div style="width: 32%;display: inline-block">
									<label style="text-align:right;width:34%;">发票类型：</label>
									<select id="itype" name="itype" class="easyui-combobox" data-options="editable:false,panelHeight:75" style="width:62%;height:28px;text-align:left">
										<option value="0">专用发票</option>
										<option value="1">普通发票</option>
										<option value="2">电子发票</option>
									</select>
								</div>
						</div>	
						<div class="time_col time_colp11">
							<label style="text-align:right;width:10.8%;">单位名称：</label>
							<input id="cname" name="cname" class="easyui-textbox" data-options="required:true,readonly:true" style="width:36%;height:28px;text-align:left">
				
							<label style="text-align:right;width:10.8%;">税号：</label>
							<input id="taxnum" name="taxnum" class="easyui-textbox" data-options="required:true" style="width:37%;height:28px;text-align:left">
						</div>
					
						<div class="time_col time_colp11">
							<label style="text-align:right;width:10.8%;">公司地址：</label>
							<input id="caddr" name="caddr" class="easyui-textbox" data-options="required:true" style="width:85%;height:28px;text-align:left">
						</div>
						<div class="time_col time_colp11">
							<label style="text-align:right;width:10.8%;">开户行：</label>
							<input id="bname" name="bname" class="easyui-textbox" data-options="required:true" style="width:36%;height:28px;text-align:left">
							<label style="text-align:right;width:10.8%;">开户账号：</label>
							<input id="bcode" name="bcode" class="easyui-textbox" data-options="required:true" style="width:37%;height:28px;text-align:left">
						</div>
						<div class="time_col time_colp11">
							<div style="width:32%;display: inline-block">
								<label style="text-align:right;width:33.8%;">邮箱：</label>
								<input id="email" name="email" class="easyui-validatebox easyui-textbox" data-options="required:true,validType:'email'" style="width:62%;height:28px;text-align:left">
							</div>
							<div style="width: 32%;display: inline-block">
								<label style="text-align:right;width:34%;">开票电话：</label>
								<input id="phone" name="phone" class="easyui-validatebox easyui-textbox" data-options="required:true,validType:'phoneNum'" style="width:62%;height:28px;text-align:left">
							</div>
							<div style="width: 32%;display: inline-block">
								<label style="text-align:right;width:34%;">收票人：</label>
								<input id="runame" name="runame" class="easyui-textbox" data-options="required:true" style="width:62%;height:28px;text-align:left">
							</div>
						</div>
						<div class="time_col time_colp11">
							<label style="text-align:right;width:10.8%;">备注：</label>
							<input id="vmome" name="vmome" class="easyui-textbox" data-options="multiline:true" style="width:85%;height:56px;text-align:left">
						</div>
				 </form>
			</div>
			<div data-options="region:'center',title:'center title'" style="padding:20px 20px 20px 0px; float:right;">
				<a id="collectsave" href="javascript:void(0)" class="ui-btn" onclick="save()">保存</a>
				<a id="cancelBtn" 	href="javascript:void(0)" class="ui-btn" onclick="$('#fp_dialog').dialog('close');">取消</a>
			</div>
		</div>
	</div>
		
		<div id="kj_dialog"></div>
		<div id="kj_buttons" style="display:none;">
			<a href="javascript:void(0)" class="easyui-linkbutton c6"  onclick="selectCorps()" style="width:90px">确认</a> 
			<a href="javascript:void(0)" class="easyui-linkbutton" onclick="javascript:$('#kj_dialog').dialog('close');" style="width:90px">取消</a>
		</div>
	</div>
</body>
</html>