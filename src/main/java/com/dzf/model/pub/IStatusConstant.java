package com.dzf.model.pub;

public interface IStatusConstant {

	/** 渠道付款单操作类型 (2-取消确认；3-收款确认；4-收款驳回) */
	public static int ICHNOPRATETYPE_2 = 2;
	public static int ICHNOPRATETYPE_3 = 3; 
	public static int ICHNOPRATETYPE_4 = 4; 
	
//	/** 合同扣款状态(1：待审核；2：已审核；3：已驳回；4：服务到期；) */
	/** 合同扣款状态(5：待审核；1：已审核；7：已驳回；8：服务到期；9：已终止；10：已作废；) */
	public static int IDEDUCTSTATUS_5 = 5;
	public static int IDEDUCTSTATUS_1 = 1;
	public static int IDEDUCTSTATUS_7 = 7;
	public static int IDEDUCTSTATUS_8 = 8;
	public static int IDEDUCTSTATUS_9 = 9;
	public static int IDEDUCTSTATUS_10 = 10;

	/** 合同扣款确认/取消扣款操作 (1：扣款；2：驳回；) */
	public static int IDEDUCTYPE_1 = 1;
	public static int IDEDUCTYPE_2 = 2;
	
	/** 付款类型 (1:加盟费；2：预付款；) */
	public static int IPAYTYPE_1 = 1;
	public static int IPAYTYPE_2 = 2;
	
	/** 合同明细单操作类型 (1:付款；2：扣款；) */
	public static int IDETAILTYPE_1 = 1;
	public static int IDETAILTYPE_2 = 2;
	
	/** 首页查询类型(1:周查询；2：月查询；3：年查询；)*/
	public static int IINDEXQRYTYPE_1 = 1;
	public static int IINDEXQRYTYPE_2 = 2;
	public static int IINDEXQRYTYPE_3 = 3;
	
	/** 合同变更类型(1：终止；2：作废)*/
	public static int ICONCHANGETYPE_1 = 1;//终止
	public static int ICONCHANGETYPE_2 = 2;//作废
}
