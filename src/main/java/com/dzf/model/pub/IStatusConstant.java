package com.dzf.model.pub;

public interface IStatusConstant {

	/** 渠道付款单操作类型 (2-取消确认；3-收款确认；4-收款驳回) */
	public static int ICHNOPRATETYPE_2 = 2;
	public static int ICHNOPRATETYPE_3 = 3; 
	public static int ICHNOPRATETYPE_4 = 4; 
	
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
	
	/** 付款类型 (1:加盟费；2：预付款；3：返点) */
	public static int IPAYTYPE_1 = 1;
	public static int IPAYTYPE_2 = 2;
	public static int IPAYTYPE_3 = 3;
	
	/** 合同明细单操作类型 (1:预付款付款；2：合同扣款；3：返点录入；) */
	public static int IDETAILTYPE_1 = 1;
	public static int IDETAILTYPE_2 = 2;
	public static int IDETAILTYPE_3 = 3;
	
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
	
}
