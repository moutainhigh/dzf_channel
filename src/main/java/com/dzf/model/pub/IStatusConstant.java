package com.dzf.model.pub;

public interface IStatusConstant {

	/** 付款单操作类型 (2：取消审批；3：收款确认；4：收款驳回；5：取消确认；9：审批驳回；10：审批通过；) */
	public static int ICHNOPRATETYPE_2 = 2;
	public static int ICHNOPRATETYPE_3 = 3; 
	public static int ICHNOPRATETYPE_4 = 4; 
	public static int ICHNOPRATETYPE_5 = 5; 
	public static int ICHNOPRATETYPE_9 = 9;
	public static int ICHNOPRATETYPE_10 = 10;
	
	/** 付款单状态 (1：待提交；2：待审批；3：已确认；4：已驳回；5：待确认；) */
	public static int IPAYSTATUS_1 = 1;
	public static int IPAYSTATUS_2 = 2;
	public static int IPAYSTATUS_3 = 3;
	public static int IPAYSTATUS_4 = 4;
	public static int IPAYSTATUS_5 = 5;
	
	/** 合同扣款状态(5：待审核；1：已审核；7：已驳回；8：服务到期；9：已终止；10：已作废；) */
	public static int IDEDUCTSTATUS_5 = 5;
	public static int IDEDUCTSTATUS_1 = 1;
	public static int IDEDUCTSTATUS_7 = 7;
	public static int IDEDUCTSTATUS_8 = 8;
	public static int IDEDUCTSTATUS_9 = 9;
	public static int IDEDUCTSTATUS_10 = 10;

	/** 合同扣款确认/驳回 (1：扣款；2：驳回；) */
	public static int IDEDUCTYPE_1 = 1;
	public static int IDEDUCTYPE_2 = 2;
	
	/** 付款类型 (1:保证金；2：预付款；3：返点；) */
	public static int IPAYTYPE_1 = 1;
	public static int IPAYTYPE_2 = 2;
	public static int IPAYTYPE_3 = 3;
	
	/** 扣款明细单操作类型 (1:预付款付款；2：合同扣款；3：返点录入；4：退款单退款；5：商品购买；) */
	public static int IDETAILTYPE_1 = 1;
	public static int IDETAILTYPE_2 = 2;
	public static int IDETAILTYPE_3 = 3;
	public static int IDETAILTYPE_4 = 4;
	public static int IDETAILTYPE_5 = 5;
	
	/** 首页查询类型(1:周查询；2：月查询；3：年查询；)*/
	public static int IINDEXQRYTYPE_1 = 1;
	public static int IINDEXQRYTYPE_2 = 2;
	public static int IINDEXQRYTYPE_3 = 3;
	
	/** 合同变更类型(1：终止；2：作废)*/
	public static int ICONCHANGETYPE_1 = 1;//终止
	public static int ICONCHANGETYPE_2 = 2;//作废
	
	/** 返点单状态(0：待提交；1：待确认；2：待审批；3：审批通过；4：已驳回；)*/
	public static int IREBATESTATUS_0 = 0;//待提交
	public static int IREBATESTATUS_1 = 1;//待确认
	public static int IREBATESTATUS_2 = 2;//待审批
	public static int IREBATESTATUS_3 = 3;//审批通过
	public static int IREBATESTATUS_4 = 4;//已驳回
	
	/** 单据类型  */
	public static String IBILLTYPE_FD01 = "FD01";//返点单确认
	public static String IBILLTYPE_FD02 = "FD02";//返点单审核
	
	/** 返点单操作类型(1：驳回修改；2：确认通过；3：取消确认；4：审核通过)*/
	public static int IREBATEOPERTYPE_1 = 1;//驳回修改
	public static int IREBATEOPERTYPE_2 = 2;//确认通过
	public static int IREBATEOPERTYPE_3 = 3;//取消确认
	public static int IREBATEOPERTYPE_4 = 4;//审核通过
	
	/** 返点单、客户名称审核操作类型(0：待提交；1：待审核；2：已审核；3：拒绝审核；)*/
	public static int ICORPEDITSTATUS_0 = 0;//待提交
	public static int ICORPEDITSTATUS_1 = 1;//待审核
	public static int ICORPEDITSTATUS_2 = 2;//已审核
	public static int ICORPEDITSTATUS_3 = 3;//拒绝审核
	
	/** 退款单状态(0：待确认；1：已确认；)*/
	public static int IREFUNDSTATUS_0 = 0;
	public static int IREFUNDSTATUS_1 = 1;
	
	/** 退款单操作(1：确认退款；2：取消确认；)*/
	public static int IREFOPERATYPE_1 = 1;
	public static int IREFOPERATYPE_2 = 2;
	
	/** 商品状态(1：已保存；2：已发布；3：已下架)*/
	public static int IGOODSSTATUS_1 = 1;
	public static int IGOODSSTATUS_2 = 2;
	public static int IGOODSSTATUS_3 = 3;
	
	/** 商品订单状态(0：待确认；1：待发货；2：已发货；3：已收货；4：已取消；)*/
	public static int IORDERSTATUS_0 = 0;
	public static int IORDERSTATUS_1 = 1;
	public static int IORDERSTATUS_2 = 2;
	public static int IORDERSTATUS_3 = 3;
	public static int IORDERSTATUS_4 = 4;
	
	/** 商品订单操作动作 0：订单创建；1：订单确认；2：商品发货；3：已收货；4：取消订单；5：取消确认；*/
	public static String IORDERACTION_0 = "create";
	public static String IORDERACTION_1 = "confirm";
	public static String IORDERACTION_2 = "send";
	public static String IORDERACTION_3 = "receive";
	public static String IORDERACTION_4 = "cancel";
	public static String IORDERACTION_5 = "cancelConf";
	
	/** 商品订单操作说明 0：订单创建；1：订单确认；2：商品发货；3：已收货；4：取消订单；5：取消确认；*/
	public static String IORDERDESCRIBE_0 = "订单创建";
	public static String IORDERDESCRIBE_1 = "订单确认";
	public static String IORDERDESCRIBE_2 = "商品发货";
	public static String IORDERDESCRIBE_3 = "已收货";
	public static String IORDERDESCRIBE_4 = "取消订单";
	public static String IORDERDESCRIBE_5 = "取消确认";
	
	/** 加盟商合同类型 null正常合同；1：被2补提交的原合同；2：小规模转一般人的合同；3：变更合同;4：被5补提交的原合同;5:一般人转小规模的合同*/
	public static int ICONTRACTTYPE_1 = 1;
	public static int ICONTRACTTYPE_2 = 2;
	public static int ICONTRACTTYPE_3 = 3;
	public static int ICONTRACTTYPE_4 = 4;
	public static int ICONTRACTTYPE_5 = 5;
	
	/** 区域划分类型 1:渠道；2：培训；3：运营*/
	public static int IQUDAO = 1;
	public static int IPEIXUN = 2;
	public static int IYUNYING = 3;
	
	/** 入库单状态  1：待确认；2：已确认 */
	public static int ISTOCKINSTATUS_1 = 1;
	public static int ISTOCKINSTATUS_2 = 2;
	
	/** 仓库主键 */
	public static String CK_ID = "00000100000000000000CK01";
}
