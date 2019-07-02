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
	
	@FieldAlias("applyid")
    private String pk_changeapply; // 合同变更申请主键
	
    @FieldAlias("conid")
    private String pk_contract; // 原合同主键
    
	@FieldAlias("hisid")
	private String pk_confrim; // 合同历史主键
	
	@FieldAlias("corpid")
	private String pk_corp;//加盟商主键
	
	@FieldAlias("corpkid")
	private String pk_corpk;//客户主键
	
	@FieldAlias("opertype")
	private Integer iopertype;// 操作类型1：审核；2：变更；
	
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
    
    @FieldAlias("memo")
    private String vmemo;//备注
    
    @FieldAlias("uname")
    private String user_name;//操作人名称
    
	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public Integer getIopertype() {
		return iopertype;
	}

	public void setIopertype(Integer iopertype) {
		this.iopertype = iopertype;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public String getPk_applyaudit() {
		return pk_applyaudit;
	}

	public void setPk_applyaudit(String pk_applyaudit) {
		this.pk_applyaudit = pk_applyaudit;
	}

	public String getPk_changeapply() {
		return pk_changeapply;
	}

	public void setPk_changeapply(String pk_changeapply) {
		this.pk_changeapply = pk_changeapply;
	}

	public String getPk_contract() {
		return pk_contract;
	}

	public void setPk_contract(String pk_contract) {
		this.pk_contract = pk_contract;
	}

	public String getPk_confrim() {
		return pk_confrim;
	}

	public void setPk_confrim(String pk_confrim) {
		this.pk_confrim = pk_confrim;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public Integer getIapplystatus() {
		return iapplystatus;
	}

	public void setIapplystatus(Integer iapplystatus) {
		this.iapplystatus = iapplystatus;
	}

	public String getVreason() {
		return vreason;
	}

	public void setVreason(String vreason) {
		this.vreason = vreason;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getPKFieldName() {
		return "pk_applyaudit";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_changeapply";
	}

	@Override
	public String getTableName() {
		return "cn_applyaudit";
	}

}
