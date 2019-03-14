<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@ page import="com.dzf.pub.IGlobalConstants"%>
<%@page import="com.dzf.model.sys.sys_power.UserVO"%>
<%@ page import="com.dzf.pub.cache.UserCache"%>
<%@page import="com.dzf.pub.constant.AdminDateUtil"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>加盟商订单</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../../css/channelorder.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../../css/dealmanage/channelorderinvoice.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out, "../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/dealmanage/channelorder.js");%> charset="UTF-8" type="text/javascript"></script>
<script src=<%UpdateGradeVersion.outversion(out, "../../js/channel/dealmanage/channelorderinvoice.js");%> charset="UTF-8" type="text/javascript"></script>
</head>
<body>
	<div id="List_panel" class="wrapper" data-options="closed:false">
		<div class="mod-toolbar-top">
			<div class="mod-toolbar-content">
				<div class="left mod-crumb">
					<div id="cxjs" class="h30 h30-arrow">
						<label class="mr5">查询：</label>
						<strong id="querydate"></strong>
						<span class="arrow-date"></span>
					</div>
				</div>
				<div class="left mod-crumb">
					<span class="cur"></span>
				</div>
				
				<div class="left mod-crumb">
					<div style="margin:4px 0px 0px 10px;float:left;font-size:14px;">
						<a href="javascript:void(0)"  style="font-size:14;color:blue;margin-left:15px; margin-right:15px;" 
							onclick="qryData()">待确认</a>
					</div>
				</div>
				
				<div class="right">
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="confirm()">确认</a>
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="cancOrder()">取消订单</a>
					<!-- <a href="javascript:void(0)" class="ui-btn ui-btn-xz" data-options="plain:true" onclick="onBilling()">发票申请</a> -->
				</div>
			</div>
		</div>
		
		<!-- 查询对话框 begin -->
		<div class="qijian_box" id="qrydialog" style="display:none; width:450px; height:280px">
			<s class="s" style="left: 25px;"><i class="i"></i> </s>
			<h3>
				<span>查询</span><a class="panel-tool-close" href="javascript:closeCx()"></a>
			</h3>
			<div class="time_col time_colp10">
				<input id="tddate" type="radio" name="seledate" checked />
				<label style="width: 70px;text-align:right">提交日期：</label>
				<font><input name="bdate" type="text" id="bdate" class="easyui-datebox" 
					data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
				<font>-</font>
				<font><input name="edate" type="text" id="edate" class="easyui-datebox" 
					data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
			</div>
			<div class="time_col time_colp10">
				<input id="kkdate" type="radio" name="seledate" />
				<label style="width: 70px;text-align:right">确认日期：</label>
				<font><input name="bperiod" type="text" id="bperiod" class="easyui-datebox" 
					data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
				<font>-</font>
				<font><input name="eperiod" type="text" id="eperiod" class="easyui-datebox" 
					data-options="width:130,height:27,editable:true,validType:'checkdate'"/></font>
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">订单编码：</label>
				<input id="qbcode" class="easyui-textbox" style="width:290px;height:28px;"/>
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">加盟商：</label>
				<input id="qcpname" class="easyui-textbox" style="width:290px;height:28px;"/>
				<input id="qcpid" type="hidden">
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">订单状态：</label>
				<select id="qstatus" class="easyui-combobox" data-options="panelHeight:'auto',multiple:true" 
					editable="false" style="width:290px;height:28px;">
					<!-- 状态  0：待确认；1：待发货；2：已发货；3：已收货；4：已取消； -->
					<option value="-1">全部</option>
					<option value="0">待确认</option>
					<option value="1">待发货</option>
					<option value="2">已发货</option>
					<option value="3">已收货</option>
					<option value="4">已取消</option>
				</select>
			</div>
			<div class="time_col time_colp10">
				<label style="width:85px;text-align:right">开票状态：</label>
				<select id="tistatus" class="easyui-combobox" data-options="panelHeight:'auto'" 
					editable="false" style="width:290px;height:28px;">
					<!-- 状态  1：未开票；2：已开票； -->
					<option value="-1">全部</option>
					<option value="1">未开票</option>
					<option value="2">已开票</option>
				</select>
			</div>
			<p>
				<a class="ui-btn save_input" onclick="clearParams()">清除</a>
				<a class="ui-btn save_input" onclick="reloadData()">确定</a>
				<a class="ui-btn cancel_input" onclick="closeCx()">取消</a>
			</p>
		</div>
		<!-- 查询对话框end -->
		
		<div id="dataGrid" class="grid-wrap">
			<table id="grid"></table>
		</div>
		
		<!-- 加盟商参照对话框begin -->
		<div id="chnDlg"></div>
		<!-- 加盟商参照对话框end -->
		
		<!-- 取消订单begin -->
		<div id="cancelDlg" class="easyui-dialog" style="width: 500px; height: 300px; padding-top: 20px; font-size: 14px;"
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
        	<div style="width: 80%; margin: 0 auto;">
				<div class="time_col time_colp11">
					<input id="reason1" name="reatype" type="radio" value="1" checked />
					<label style="width: 250px;" for="reason1">加盟商账户预付款余额不足</label>
				</div>
				<div class="time_col time_colp11">
					<input id="reason2" name="reatype" type="radio" value="2" />
					<label style="width: 250px;" for="reason2">商品缺货</label>
				</div>
				<div class="time_col time_colp11">
					<input id="reason3" name="reatype" type="radio" value="3" />
					<label style="width: 250px;" for="reason3">其它原因</label>
				</div>	
			   	<div class="time_col time_colp11">
			   		<form id="cancfrom" method="post">
						<div style="display: inline-block; margin-top: 5px;">
							<input type="text" class="easyui-textbox" id="reason" name="reason"
								data-options="multiline:true,validType:'length[0,50]',"
								style="width:380px; height:70px; display:none;">
						</div>
					</form>
				</div>
				<div style="text-align:center;margin-top:20px;">
				    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="cancSave()">保存</a> 
					<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="cancCancel()">取消</a>
				</div>
			</div>
		</div>
		<!-- 取消订单end -->
		
		<!-- 发货列表begin -->
		<div id="setOutDlg" class="easyui-dialog" style="width:1000px;height:450px;" data-options="closed:true">
			<table id="sgrid" style="height:85%;"></table>
			<div style="text-align:center;margin-top:20px;">
			    <a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="sendSave()">保存</a> 
				<a href="javascript:void(0)" class="ui-btn ui-btn-xz" onclick="sendCancel()">取消</a>
			</div>
		</div>
		<!-- 发货列表end -->

		<!-- 订单详情begin -->
		<div id="infoDlg" class="easyui-dialog" style="width: 830px; height: 450px; padding-top: 10px; font-size: 14px;"
			data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
			<div class="shift">
				<div style="margin-bottom: 20px;">
					<div style="width: 10%; display: inline-block">
						<div class="shop_pick">订单信息</div>
					</div>
					<div class="order">
						<label>订单编号：</label> <span id="billcode"></span>
					</div>
					<div class="order">
						<label>加盟商：</label> <span id="pname"></span>
					</div>
				</div>
				<!-- 订单详情 begin -->
				<div id="detail"></div>
				<!-- 订单详情 end -->
				
				<!-- 快递信息begin -->
				<div id="fast"></div>
				<!-- 快递信息end -->
				
				<div class="icon">
					<div class="shop_pick">收货信息</div>
				</div>
				<div style="margin-left: 40px;">
					<span id="receinfo"></span>
				</div>
				<div class="icon">
					<div class="shop_pick">已选商品</div>
				</div>
				<!-- 商品详情 begin -->
				<div id="goods"></div>
				<!-- 商品详情 end -->
			</div>
			<div class="goodsbuy_foot">
				<div class="shop_cut_left">
				    实付款：¥<span id="ndesummny"></span>
				</div>
				<div class="shop_cut_right">
					(包含返点¥<span id="nderebmny"></span>)
				</div>
			</div>
		</div>
		<!-- 订单详情end -->
		
		<!-- 新增出库单 start-->
		<div id="cardDialog" >
			<form id="stockout" method="post" style="height:440px;width:1100px;overflow:hidden;padding-top:18px;">
				<div class="time_col time_colp11" style="display:none">
					<input id="corpid" name="corpid" class="easyui-textbox"> 
					<input id="nmny" name="nmny" class="easyui-numberbox"> 
				</div>
				<div class="time_col time_colp11">
					<div style="width:32%;display: inline-block;">
						<label style="text-align:right;width:35%;">加盟商：</label> 
						<input id="corpname"  name="corpname" class="easyui-textbox" style="width:60%;height:28px;text-align:left"
							data-options="readonly:true"/>  
					</div>
				</div>
				<div class="time_col time_colp11">
					<div style="width:100%;display: inline-block;">
						<label style="text-align:right;width:11.2%;vertical-align:middle;">备注：</label>
						<textarea id="memo" class="easyui-textbox" name="memo" data-options="multiline:true,validType:'length[0,50]'" style="width:84%;height:40px;"></textarea>
					</div>
				</div>
				<div class="time_col time_colp11">
					<div style="width:32%;display: inline-block;">
						<label style="text-align:right;width:35%;">收货人：</label> 
						<input id="rename"  name="rename" class="easyui-textbox" style="width:60%;height:28px;text-align:left"
							data-options="required:true"/>  
					</div>
					<div style="width:32%;display: inline-block;">
						<label style="text-align:right;width:35%;">联系方式：</label> 
						<input id='phone'  name="phone" class="easyui-textbox"   style="width:60%;height:28px;text-align:left"
							data-options="validType:'phone[\'#p1\']',required:true" ></input>
					</div>
					<div style="width:32%;display: inline-block;" >
						<label style="text-align:right;width:35%;">邮政编码：</label> 
						<input id="recode" name="recode" class="easyui-textbox" style="width:60%;height:28px;text-align:left;"
							data-options="validType:'length[0,10]'"> 
					</div>
				</div>
				<div class="time_col time_colp11">
					<div style="width:100%;display: inline-block;">
						<label style="text-align:right;width:11.2%;vertical-align:middle;">收货地址：</label>
						<textarea id="readdress" class="easyui-textbox" name="readdress" data-options="multiline:true,validType:'length[0,100]',required:true" style="width:84%;height:40px;"></textarea>
					</div>
				</div>
				<div class="grid-wrap" >
					<table id="cardGrid"></table>
				</div> 
				<div style="float:right;margin-top:20px;margin-right:20px;">
				    <a href="javascript:void(0)" id="addSave" class="ui-btn ui-btn-xz" onclick="addSave()">确定</a> 
					<a href="javascript:void(0)" id="addCancel" class="ui-btn ui-btn-xz"  onclick="addCancel()">取消</a>
				</div>
			</form>
		</div>
		<!-- 新增出库单 end-->
		
		<!-- 开票申请界面 begin -->
		<div id="tabledia" class="easyui-dialog" style="overflow: scroll;" data-options="closed:true">
		    <form id="tablediaForm">
		        <div id="table_header">
		
		            <div class="basic_info" style="height:30px;">
		                <div class="basic_title">
		                    <label style="width:100px;text-align:right">订单编号：</label>
		                    <font>
		                    	<input id="dl_invcode" name="invcode" class="fp_title easyui-textbox" 
		                    		data-options="readonly:true" style="width:137px; height:24px;"/> 
		                    </font>
		                </div>
		            </div>
		            <div class="purchaser">
		                <table>
		                    <tbody>
		                    <tr>
		                        <td style="width:32px;text-align:center;padding-top:18px;">购<br/>买<br/>方</td>
		                        <td class="basic_pur" style="width:691px;">
		                            <div style="margin-top:2px;">
		                            	<lable style="text-align:right">名称：</lable>
		                            	<input id="dl_cname" name="cname" class="easyui-textbox" 
		                            		data-options="required:true,readonly:true," style="width:230px;"/>
		                            	<lable style="text-align:right">纳税人识别号：</lable>
		                            	<input id="dl_taxnum" name="taxnum" class="easyui-textbox" style="width:230px;"
		                            		data-options="required:true,validType:['length[0,20]'],invalidMessage:'录入长度不能超过20'"/>
		                            </div>
		                            <div>
		                            	<lable style="text-align:right">地址：</lable>
		                            	<input id="dl_caddr" name="caddr" class="easyui-textbox" style="width:566px;"
		                            		data-options="validType:['length[0,50]'],invalidMessage:'录入长度不能超过50'"/>
		                            </div>
		                            <div>
		                            	<lable style="text-align:right">开户行：</lable>
		                            	<input id="dl_bname" name="bname" class="easyui-textbox" style="width:230px;"
		                            		data-options="validType:['length[0,30]'],invalidMessage:'录入长度不能超过30'"/>
		                            	<lable style="text-align:right">电话：</lable>
		                            	<input id="dl_phone" name="phone" class="easyui-textbox" 
		                            		data-options="required:true,validType:'phoneNum'," style="width:230px;"/>
		                            </div>
		                            <div>
		                            	<lable style="text-align:right">开户账号：</lable>
		                            	<input id="dl_bcode" name="bcode" class="easyui-textbox" style="width:230px;"
		                            		data-options="validType:['length[0,30]'],invalidMessage:'录入长度不能超过30'"/>
		                            	<lable style="text-align:right">邮箱：</lable>
		                            	<input id="dl_email" name="email" class="easyui-textbox" 
		                            		data-options="required:true,validType:'email'," style="width:230px;"/>
		                            </div>
		                        </td>
		                    	<td class="basic_pur" style="width:214px;">
			                    	<div style="margin-top:2px;">
		                            	<lable style="width:76px;text-align:right;">订单金额：</lable>
		                            	<input id="dl_ndesummny" name="ndesummny" class="easyui-numberbox" 
		                            		data-options="readonly:true" style="width:100px;"/>
		                            </div>
		                            <div>
		                            	<lable style="width:76px;text-align:right;">返点金额：</lable>
		                            	<input id="dl_nderebmny" name="nderebmny" class="easyui-numberbox" 
		                            		data-options="readonly:true" style="width:100px;"/>
		                            </div>
		                            <div>
		                            	<lable style="width:76px;text-align:right;color:red;">预付款金额：</lable>
		                            	<input id="dl_ndemny" name="ndemny" class="easyui-numberbox" 
		                            		data-options="readonly:true" style="width:100px;"/>
		                            </div>
		                    	</td>
		                    </tr>
		                    </tbody>
		                </table>
		            </div>
		        </div>
		        <table style="display: none">
		            <tr id = 'trTM' style="display: none">
		                <td style="width:333px;">
		                    <div class="removeAdd">
		                        <div class="iconRemove" onclick="removeTr(this)"></div>
		                    </div>
		                    <input style="text-align:left;" class="bspmc" name="bspmc" readonly unselectable="on">
	                    </td>
		                <td style="width:90px;">
		                	<input style="text-align:left;" class="gg" name="gg" readonly unselectable="on">
		                </td>
		                <td style="width:60px;">
		                	<input style="text-align:left;" class="bspmc" name="jldw" readonly unselectable="on">
		                </td>
		                <td style="width:90px;">
		                	<input class="bspmc" name="bnum" 
		                		onchange="trOnchange(this,'bnum')" onkeyup="trKeyup(this,'bnum')" >
		                </td>
		                <td style="width:100px;">
		                	<input class="bspmc" name="bdj" readonly unselectable="on">
		                </td>
		                <td style="display:none;">
							<input class="bspmc" name="cmny" readonly unselectable="on">
						</td>
		                <td style="width:100px;">
		                	<input class="bspmc" name="bje" readonly unselectable="on">
		                </td>
		                <td style="width:60px;">
		                	<input style="text-align:center;" class="bspmc" name="bsl" 
		                		readonly unselectable="on">
		                </td>
		                <td style="width:100px;">
		                	<input class="bspmc" name="bse" readonly unselectable="on">
		                </td>
		                <td style="display:none;">
							<input class="bspmc" name="sourceid" readonly unselectable="on">
						</td>
		            </tr>
		        </table>
		        <div class="goodslist" >
		
		            <table id="goodslist">
		                <tbody>
		                <tr style="background:#f6f6f6;border:none;">
		                    <td style="width:333px;">货物或应税劳务名称</td>
		                    <td style="width:90px;">规格型号</td>
		                    <td style="width:60px;">单位</td>
		                    <td style="width:90px;">数量</td>
		                    <td style="width:100px;">单价</td>
		                    <td style="display:none;">含税金额</td>
		                    <td style="width:100px;">金额</td>
		                    <td style="width:60px;">税率(%)</td>
		                    <td style="width:100px;">税额</td>
		                    <td style="display:none;">来源主键</td>
		                </tr>
		                <tr class="goodsbody">
		                    <td colspan="8" style="border:none;">
		                        <table class="goodsgrid" id="table_body">
		
									<tr id="id0">
		                                <td style="width:333px;">
		                                    <div class="removeAdd">
		                                        <div class="iconRemove"></div>
		                                    </div>
		                                    <input style="text-align:left;" class="bspmc" name="bspmc" readonly unselectable="on">
		                                </td>
										<td style="width:90px;">
											<input style="text-align:left;" class="gg" name="gg" readonly unselectable="on">
										</td>
										<td style="width:60px;">
											<input style="text-align:left;" class="bspmc" name="jldw" readonly unselectable="on">
										</td>
										<td style="width:90px;">
											<input onchange="trOnchange(this,'bnum')" onkeyup="trKeyup(this,'bnum')" class="bspmc" name="bnum">
										</td>
										<td style="width:100px;">
											<input class="bspmc" name="bdj" readonly unselectable="on">
										</td>
										<td style="display:none;">
											<input class="bspmc" name="cmny" readonly unselectable="on">
										</td>
										<td style="width:100px">
											<input class="bspmc" name="bje" readonly unselectable="on">
										</td>
										<td style="width:60px;">
											<input style="text-align:center;" 
												class="bspmc" name="bsl" readonly unselectable="on">
										</td>
										<td style="width:100px;">
											<input class="bspmc" name="bse" readonly unselectable="on">
										</td>
										<td style="display:none;">
											<input class="bspmc" name="sourceid" readonly unselectable="on">
										</td>
									</tr>
		                        </table>
		
		                    </td>
		                </tr>
		
		                <tr class="goodsfoot">
		                    <td>合计</td>
		                    <td></td>
		                    <td></td>
		                    <td></td>
		                    <td></td>
		                    <td>￥<span id="concatje"></span></td>
		                    <td></td>
		                    <td>￥<span id="concatse"></span></td>
		                </tr>
		                <tr>
		                    <td>价税合计 (大写)</td>
		                    <td colspan="8">
		                    	<span style="float:left;margin-left:30px;">
		                    		<div class="delicon"></div> 
		                    		<span id="content"></span>
		                    	</span>
		                    	<span style="float:right;margin-right:150px;">(小写)  ￥<span id="content2"></span>
		                    	</span>
		                    </td>
		                </tr>
		                </tbody>
		            </table>
		        </div >
		    </form>
		</div>
		<!-- 开票申请界面 end -->
	</div>
</body>
</html>