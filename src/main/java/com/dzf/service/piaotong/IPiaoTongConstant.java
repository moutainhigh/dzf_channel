package com.dzf.service.piaotong;

public class IPiaoTongConstant {
	//系统参数
	public static final String SUCCESS = "0000";
	
//	public static final HashMap<String, String> errorMap=new HashMap<String, String>(){{    
//        put("9999", "验签失败");    
//        put("9998", "平台编码无效");  
//        put("9997", "纳税人识别号无效");    
//        put("8996", "业务异常， 请联系运维");
//        put("8995", "数据校验不通过(对应参考详细信息)");    
//        put("8004", "找不到对应的开票企业信息， 请检查税号");  
//        put("8005", "找不到对应的税收分类编码， 请参照税收分类编码表， 检查编码号");    
//        put("8006", "找不到对应的税率， 请输入正确的税率");
//        put("8007", "折扣金额不可大于对应商品行金额/折扣金额和折扣率不匹配");
//        put("8008", "优惠政策不为空时， 增值税特殊管理不能为空");    
//        put("8009", "企业注册/修改中不能开票");  
//        put("8010", "企业账户余额不足， 请充值");    
//        put("8011", "税额与税率不匹配");
//        put("8012", "差额开票抵扣金额过大， 不能超过价税合计金额");
//        put("8013", "差额开票只允许单个商品行");
//        put("8014", "税率为 0 时， 零税率标示必须选择");
//        put("8015", "不存在对应的零税率标示");
//        put("8016", "单价数量金额不匹配");
//        put("8017", "折扣税额和税率不匹配");
//    }};  
    
    
    /** 发票种类代码  */
    public static final String INVOICEKINDCODE_01 = "01";
    
    public static final String INVOICEKINDCODE_02 = "02";
    
    public static final String INVOICEKINDCODE_03 = "03";
    
    public static final String INVOICEKINDCODE_04 = "04";
    
    public static final String INVOICEKINDCODE_10 = "10";
    
    public static final String INVOICEKINDCODE_41 = "41";
    
    /** 发票种类名称  */
    public static final String INVOICEKINDNAME_01 = "增值税专用发票";
    
    public static final String INVOICEKINDNAME_02 = "货运运输增值税专用发票";
    
    public static final String INVOICEKINDNAME_03 = "机动车销售统一发票";
    
    public static final String INVOICEKINDNAME_04 = "增值税普通发票";
    
    public static final String INVOICEKINDNAME_10 = "增值税普通发票(电子)";
    
    public static final String INVOICEKINDNAME_41 = "卷票";
}
