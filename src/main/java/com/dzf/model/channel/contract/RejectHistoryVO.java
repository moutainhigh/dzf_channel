package com.dzf.model.channel.contract;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 加盟商合同驳回历史
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class RejectHistoryVO extends SuperVO {

	private static final long serialVersionUID = 1024925115857871611L;
	
	@FieldAlias("histid")
	private String pk_rejecthistory;
	
    @FieldAlias("contractid")
    private String pk_contract; // 合同主键
	
	@FieldAlias("sourceid")
	private String pk_source;
	
	@FieldAlias("corpid")
	private String pk_corp;//公司主键
	
	@FieldAlias("reason")
	private String vreason;//驳回原因
	
	@FieldAlias("suggest")
	private String vsuggest;//修改建议
	
    @FieldAlias("operatorid")
    private String coperatorid; // 录入人

    @FieldAlias("zddate")
    private DZFDate doperatedate; // 录入日期
    
    @FieldAlias("dr")
    private Integer dr; // 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts; // 时间戳

	public String getPk_contract() {
		return pk_contract;
	}

	public void setPk_contract(String pk_contract) {
		this.pk_contract = pk_contract;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_rejecthistory() {
		return pk_rejecthistory;
	}

	public String getPk_source() {
		return pk_source;
	}

	public String getVreason() {
		return vreason;
	}

	public String getVsuggest() {
		return vsuggest;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public Integer getDr() {
		return dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setPk_rejecthistory(String pk_rejecthistory) {
		this.pk_rejecthistory = pk_rejecthistory;
	}

	public void setPk_source(String pk_source) {
		this.pk_source = pk_source;
	}

	public void setVreason(String vreason) {
		this.vreason = vreason;
	}

	public void setVsuggest(String vsuggest) {
		this.vsuggest = vsuggest;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getPKFieldName() {
		return "pk_rejecthistory";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_rejecthistory";
	}

}
