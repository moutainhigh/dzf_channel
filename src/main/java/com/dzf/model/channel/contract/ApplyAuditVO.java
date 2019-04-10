package com.dzf.model.channel.contract;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 合同申请审批历史
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class ApplyAuditVO extends SuperVO {
	
	private static final long serialVersionUID = -6310886520043273119L;

	@FieldAlias("apauid")
	private String pk_applyaudit; // 申请审批历史主键
	
    @FieldAlias("conid")
    private String pk_contract; // 原合同主键
    
	@FieldAlias("hisid")
	private String pk_confrim; // 合同历史主键
	
	@FieldAlias("corpid")
	private String pk_corp;//加盟商主键
	
	@FieldAlias("corpkid")
	private String pk_corpk;//客户主键
	
	//申请状态  1：渠道待审（保存态）；2： 区总待审（处理中）；3：总经理待审（处理中）；4：运营待审（处理中）；5：已处理；6：已拒绝；
	@FieldAlias("apstatus")
	private Integer iapplystatus;
	
	@FieldAlias("reason")
	private String vreason;//驳回原因
	
    @FieldAlias("oper")
    private String coperatorid; // 操作人

    @FieldAlias("opdate")
    private DZFDate doperatedate; // 操作日期
    
    @FieldAlias("dr")
    private Integer dr; // 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts; // 时间戳

	@Override
	public String getPKFieldName() {
		return "pk_applyaudit";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_applyaudit";
	}

}
