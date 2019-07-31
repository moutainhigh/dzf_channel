<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="com.dzf.pub.UpdateGradeVersion"%>
<%@page import="com.dzf.pub.IGlobalConstants"%>
<%
	String userid=(String) session.getAttribute(IGlobalConstants.login_user);
	String corp=(String) session.getAttribute(IGlobalConstants.login_corp);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>商品购买</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<link  href=<%UpdateGradeVersion.outversion(out, "../../css/index.css");%> rel="stylesheet">
<link href=<%UpdateGradeVersion.outversion(out, "../../css/dealmanage/goodslook.css");%> rel="stylesheet">
<script src=<%UpdateGradeVersion.outversion(out,"../../js/easyuiext.js");%> charset="UTF-8" type="text/javascript"></script>

</head>

<body style="background:#FFF">
	<div>
		<div class="shop_main wrapper" id="goods" v-cloak>
			<div class="shopping">
				<div class="shop_left">
                    <div class="shop_div" v-for="(goodItem,index) in allGoodsInfo" :key="goodItem.gid">
                        <div class="shop_img">
                            <div class="shop_size">
                                <img @click="openDetails(index)" class="shop_size" :src="goodItem.imgpath" />
                            </div>
                            <div class="shop_cost">¥ {{goodItem.price}}</div>
                            <div class="shop_font" @click="openDetails(index)" :title="goodItem.gname">{{goodItem.gname}} </div>
                        </div>
                    </div>
				</div>
			</div>

			<div id="deDialog" class="easyui-dialog" style="width:760px;height:440px;padding:20px; font-size:14px;position: relative;" data-options="closed:true,buttons:'#dlg-buttons'" modal=true>
				<div class="selectImg banner"style="width: 270px; height: 330px; float: left;position:relative;">
					<div class="large_box" @mouseover="handOver" @mousemove="handMove" @mouseout="handOut">
						<ul>
							<li>
								<img class="showsmalImage" :src="getShowImageUrl"   width="270" height="270">
							</li>
						</ul>
						<div class="glass"></div>
					</div>

					<div class="bigBox" >
						<img class="bigImage" :src="getShowImageUrl"  >
					</div>
					<div class="small_box">
							<span class="btn left_btn" @click="leftToImg()"></span>
						<div class="small_list">
							<ul class="controls-list">
								<li class="controls-tab" v-for="(child,index) in commodityDetails.children" :key="child.fpath" @click="getSelectImg(index)">
									<img :src="child.imgpath" width="50" height="50">
								</li>
							</ul>
						</div>
						<span class="btn right_btn" @click="rightToImg(commodityDetails.children)"></span>
					</div>
				</div>

				<%-- spec*type*num/mname   规格*型号*数量/箱 --%>
				<div class="goodsdetailInfo" style="float:left;margin-left:20px;width:420px;">
					<div class="baleName">{{commodityDetails.gname}}</div>
					<p style="line-height:20px;color:#8A8A8A;width:420px;">{{commodityDetails.note}}</p>
					<div class="baleprice"><span>单价：</span><span style="color:#F52615;font-size:16px;font-weight:600;">￥{{commodityDetails.priceType}}</span></div>
					<div class="specifications" style="line-height:36px;">
						<span>规格：</span>
						<div  style="display:inline-block;">
							<div :class="{selectedType:toViewIndex == index}"  style="display:inline-block;position:relative;margin-right:8px;" v-for="(type,index) in commodityDetails.bodys" @click="selectSpecifications(index); " :key="type.specid">
								<div v-if="type.num !== undefined" >
									<div class="typeItem">{{type.spec ? type.spec + '*' : ''}}{{type.type ? type.type + '*' : ''}}{{type.num}}/{{type.mname}}</div>
								</div>
								<div v-else>
									<div class="typeItem">{{type.spec}}&nbsp;{{type.type}}</div>
								</div>
								<div class="typeTriangle"></div>
							</div>
						</div>
					</div>
					<div style="line-height:36px;">
						<span style="color:#666;font-size:15px;">库存{{commodityDetails.stocknum}}{{mnameUnit || commodityDetails.mname}}</span>
					</div>
				</div>
			</div>
		</div>
	</div>
    <script type="text/javascript" src="../../jslib/vue/vue.min.js"></script>
    <script src="../../jslib/vue/axios.min.js"></script>
    <script src="../../jslib/vue/polyfill.min.js"></script>
    <script type="text/javascript" src="../../js/channel/dealmanage/goodslook.js"></script>
	</body>
</html>
