<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>省业绩分析 </title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link
	href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%>
	rel="stylesheet">
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%>
	charset="UTF-8" type="text/javascript"></script>
<script
	src=<%UpdateGradeVersion.outversion(out, "../../js/channel/report/channelachievement.js");%>
	charset="UTF-8" type="text/javascript"></script>
	<script type="text/javascript" src="../../js/echarts.common.min.js" ></script>
</head>
<body>
	<div class="wrapper" style="overflow:auto;height:100%;">
		<div style="margin-bottom:20px;border:1px solid #ccc;background:#FFF">
			<div class="sel_time">
				<div class="time_col"> 
					<label style="text-align:right;width: 80px;">维度：</label> 
					<select  class="easyui-combobox" data-options="editable:false,required:true,panelHeight:80" 
						style="width: 100px; height: 28px;">
						<option value="-1">1月</option>
						<option value="0">2月</option>
						<option value="1">3月</option>
					</select> 
				
					<label style="text-align: right;">期间：</label> 
					<font> 
						<input name="begindate" type="text" id="begindate" class="easyui-datebox" data-options="width:110,height:28" /> 
					</font> 
					<font>  
						<input name="enddate" type="text" id="enddate" class="easyui-datebox" data-options="width:110,height:28" />
					</font>
				
				
					<font>-</font>
					<font> 
						<input name="begindate" type="text" id="begindate" class="easyui-datebox" data-options="width:110,height:28" /> 
					</font> 
				
					<font>  
						<input name="enddate" type="text" id="enddate" class="easyui-datebox" data-options="width:110,height:28" />
					</font>
						
				    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" style="margin-bottom: 0px; " onclick=''>查询</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" style="margin-bottom: 0px; " onclick=''>清除</a>
					<div style="float: right;">
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="">刷新</a>
						
					</div>
				</div>
			</div>
					
		    <div id="main" style="width: 100%;height:300px;"></div>
	    </div>
        
        <div style="margin-bottom:30px;border:1px solid #ccc;background:#FFF">
	        <div class="sel_time">
		       	<div class="time_col"> 
					<label style="text-align:right;width: 80px;">维度：</label> 
					<select class="easyui-combobox" data-options="editable:false,required:true,panelHeight:80" 
						style="width: 100px; height: 28px;">
						<option value="-1">1月</option>
						<option value="0">2月</option>
						<option value="1">3月</option>
					</select> 
			
					<label style="text-align: right;">期间：</label> 
					<font> 
						<input name="begindate" type="text" id="begindate" class="easyui-datebox" data-options="width:110,height:28" /> 
					</font> 
					<font>  
						<input name="enddate" type="text" id="enddate" class="easyui-datebox" data-options="width:110,height:28" />
					</font>
			
				
					<font>-</font>
					<font> 
						<input name="begindate" type="text" id="begindate" class="easyui-datebox" data-options="width:110,height:28" /> 
					</font> 
				
					<font>  
						<input name="enddate" type="text" id="enddate" class="easyui-datebox" data-options="width:110,height:28" />
					</font>
						
				    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" style="margin-bottom: 0px; " onclick=''>查询</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" style="margin-bottom: 0px; " onclick=''>清除</a>
					<div style="float: right;">
						<a href="javascript:void(0)" class="ui-btn ui-btn-xz"  onclick="">刷新</a>
					</div>
				</div>
			</div>
			<div style="background: #FFF;">
				<div style="text-align:left;width:8%;font-size:14px;float:left;padding-left:6px; padding-top:4px;font-size: 18px;font-weight: bold;">业绩同比</div>
					<div style="position:relative;padding-right:20px;padding-top:4px;">
						<select class="easyui-combobox" 
							data-options="valueField:'id', textField:'name', panelHeight:'auto',editable:false,"
							style="width: 120px; height: 28px; text-align: left">
							<option value="-1">扣款金额</option>
							<option value="0">2月</option>
							<option value="1">3月</option>
						</select>
					</div>
			</div>
	        <div id="man" style="width: 100%;height:300px;"></div>
        </div>
	</div>
</body>
</html>
