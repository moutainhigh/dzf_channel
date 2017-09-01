package com.dzf.action.app.pub;

public interface IConstant {

	public static String DEFAULT = "0";
	public static String FIRDES = "1";
	public static boolean SUCCESS = true; // 成功标识
	public static boolean FAIL = false; // 失败标识

	public static final String MNY_ERR_MSG = "请认真核对金额";

	public static int RESCODE_0 = 0; //成功
	public static int RESCODE_1 = 1; //失败
	
	public static final String CHATADMIN_KJ = "/Chat/dzf_head.jpg";
	
	/**业务处理消息	业务状态 */
	public static int busistate_0 = 0;//退回
	public static int busistate_1 = 1;//历史数据
	public static int busistate_2 = 2;//微信端提醒业务信息
	public static int busistate_5 = 5;//代办
	public static int busistate_6 = 6;//已办
	
	
	/** 用户信息-登录接口 */
	public static int INFO_1 = 1;

	/** 用户信息-修改密码 */
	public static int INFO_2 = 2;

	/** 忘记密码-获取验证码 */
	public static int INFO_3 = 3;

	/** 忘记密码-提交密码 */
	public static int INFO_4 = 4;

	/** 业务大类 */
	public static int INFO_5 = 5;

	/** 业务小类 */
	public static int INFO_6 = 6;
	
	/** 获取当前系统时间 */
	public static int INFO_7 = 7;
	
	/** 获取图片地址 */
	public static int INFO_8 = 8;
	
	/** 获取图片流*/
	public static int INFO_9 = 9;
	
	/** 获取费用类型*/
	public static int INFO_10 = 10;
	
	/** 二维码验证*/
	public static int INFO_11 = 11;
	
	/** 二维码授权*/
	public static int INFO_12 = 12;
	
	/** 上传头像*/
	public static int INFO_13 = 13;

	/** 我的客户-会计公司下的客户列表信息 */
	public static int INFO_101 = 101;

	/** 我的客户-证件到期提醒信息 */
	public static int INFO_102 = 102;

	/** 会计公司-所属分部信息 */
	public static int INFO_103 = 103;

	/** 潜在客户-潜在客户信息列表 */
	public static int INFO_201 = 201;

	/** 潜在客户-潜在客户跟进历史信息查询 */
	public static int INFO_202 = 202;

	/** 潜在客户-潜在客户跟进 */
	public static int INFO_203 = 203;

	/** 潜在客户-保存 */
	public static int INFO_204 = 204;

	/** 潜在客户-通过ID查询潜在客户 */
	public static int INFO_205 = 205;

	/** 潜在客户-通过ID删除潜在客户 */
	public static int INFO_206 = 206;

	/** 收款管理-收款（欠费）列表信息 */
	public static int INFO_301 = 301;

	/** 收款管理-合同列表信息 */
	public static int INFO_302 = 302;

	/** 收款管理-预付款查询 */
	public static int INFO_303 = 303;

	/** 收款管理-收款登记（保存） */
	public static int INFO_304 = 304;

	/** 收款管理-收款列表 */
	public static int INFO_305 = 305;

	/** 收款管理-收款明细（表头） */
	public static int INFO_306 = 306;

	/** 收款管理-删除收款登记 */
	public static int INFO_307 = 307;

	/** 收款管理-收款明细（合同列表明细） */
	public static int INFO_308 = 308;

	/** 收款管理-收款明细（核销列表明细） */
	public static int INFO_309 = 309;
	
	/** 收款管理-获取收款单号 */
	public static int INFO_310 = 310;

	/** 收款管理-校验收款单号唯一性 */
	public static int INFO_311 = 311;

	/** 业务处理-业务列表信息（全部） */
	public static int INFO_401 = 401;

	/** 业务处理-业务列表信息（待处理） */
	public static int INFO_402 = 402;

	/** 业务处理-详细信息（主表） */
	public static int INFO_403 = 403;

	/** 业务处理-客户提供资料 */
	public static int INFO_404 = 404;

	/** 业务处理-办理情况 */
	public static int INFO_405 = 405;

	/** 业务处理-根据业务大小类查询业务流程 */
	public static int INFO_406 = 406;

	/** 业务处理-根据流程主键查询流程详细信息 */
	public static int INFO_407 = 407;

	/** 业务处理-登记保存 */
	public static int INFO_408 = 408;

	/** 业务处理-提交下一流程 */
	public static int INFO_409 = 409;

	/** 业务处理-办理产生资料信息 */
	public static int INFO_410 = 410;

	/** 业务处理-初始化记账月份 */
	public static int INFO_411 = 411;

	/** 业务处理-退回 */
	public static int INFO_412 = 412;

	/** 业务处理-结束流程 */
	public static int INFO_413 = 413;

	/** 业务处理-保存办理情况（只在表头做记录） */
	public static int INFO_414 = 414;

	/** 业务处理-保存表体信息（更新客户提供资料、保存办理产生资料） */
	public static int INFO_415 = 415;

	/** 流程提交，选择用户接口 **/
	public static int INFO_416 = 416;

	/** 提醒信息-待办事项 */
	public static int INFO_501 = 501;

	/** 提醒信息-欠费提醒 */
	public static int INFO_502 = 502;

	/** 提醒信息-合同到期提醒 */
	public static int INFO_503 = 503;
	
	/**获取系统消息总数*/
	public static int INFO_504 = 504;
	
	/**报表-合同汇总表*/
	public static int INFO_601 = 601;
	
	/**报表-合同明细表*/
	public static int INFO_602 = 602;
	
	/**报表-客户应收汇总表*/
	public static int INFO_603 = 603;
	
	/**报表-会计人员工作量统计表*/
	public static int INFO_604 = 604;
	
	/**报表-代收代缴余额*/
	public static int INFO_605 = 605;
	
	/**报表-客户应收收款明细表*/
	public static int INFO_606 = 606;
	
	/**报表-客户业务量统计*/
	public static int INFO_607 = 607;
	
	/**报表-代收代缴余额明细*/
	public static int INFO_608 = 608;
	
	/**管理驾驶舱-客户*/
	public static int INFO_701 = 701;
	
	/**管理驾驶舱-合同*/
	public static int INFO_702 = 702;
	
	/**管理驾驶舱-收款*/
	public static int INFO_703 = 703;
	
	/**管理驾驶舱-员工*/
	public static int INFO_704 = 704;
	
	/**消息-意见反馈*/
	public static int INFO_801 = 801;
	
	/**消息-消息分类*/
	public static int INFO_802 = 802;
	
	/**消息-联系人信息*/
	public static int INFO_803 = 803;
	
	/**消息-消息列表*/
	public static int INFO_804 = 804;
	
	/**代办提醒信息-条数合计*/
	public static int INFO_805 = 805;
	
	/**消息-未读消息总条数*/
	public static int INFO_806 = 806;
	
	/**消息-更新已读消息*/
	public static int INFO_807 = 807;
	
	/**聊天-聊天图片存储*/
	public static int INFO_808 = 808;

	/**聊天-删除提醒信息*/
	public static int INFO_809 = 809;
	
	/**进度管理-做账进度列表*/
	public static int INFO_1101 = 1101;
	
	/**进度管理-做账详情*/
	public static int INFO_1102 = 1102;
	
	/**进度管理-发送提醒(推送或者短信)、确认处理*/
	public static int INFO_1103 = 1103;
	
	/**进度管理-提醒历史*/
	public static int INFO_1105 = 1105;
	
	/**进度管理-发送激活码*/
	public static int INFO_1106 = 1106;
	
	/**账务处理-票据查询列表*/
	public static int INFO_1201 = 1201;
	
	/**账务处理-票据查询详情*/
	public static int INFO_1203 = 1203;
	
	/**账务处理-票据重传*/
	public static int INFO_1204 = 1204;
	
	/**账务处理-票据退回*/
	public static int INFO_1205 = 1205;
	
	/**账务处理-票据通过*/
	public static int INFO_1206 = 1206;
	
	/**发票扫码-发票扫码*/
	public static int INFO_1301 = 1301;
	
	/**发票扫码-上传票据*/
	public static int INFO_1302 = 1302;
	
	/**发票扫码-发票扫码上传*/
	public static int INFO_1303 = 1303;
	
	/**资料交接-交接列表查询*/
	public static int INFO_1401 = 1401;
	
	/**资料交接-交接列表确认*/
	public static int INFO_1402 = 1402;
	
	/**资料交接-接手人信息*/
	public static int INFO_1403 = 1403;
	
	/**资料交接-转交-交出确认*/
	public static int INFO_1404 = 1404;
	
	/**资料交接-当面交*/
	public static int INFO_1405 = 1405;
	
	/**资料交接-当面收-查询*/
	public static int INFO_1406 = 1406;
	
	/**资料交接-当面收-确认*/
	public static int INFO_1407 = 1407;
	
	/**资料交接-资料档案*/
	public static int INFO_1408 = 1408;

	/**资料交接-添加资料*/
	public static int INFO_1409 = 1409;
	
	/**资料交接-查询资料交接消息详情*/
	public static int INFO_1410 = 1410;
	
	/**资料交接-交接单整单确认*/
	public static int INFO_1411 = 1411;
}
