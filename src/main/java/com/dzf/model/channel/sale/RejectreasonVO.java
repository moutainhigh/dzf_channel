package com.dzf.model.channel.sale;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 合同审批设置VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class RejectreasonVO extends SuperVO {

	private static final long serialVersionUID = -3464220587248993625L;
	
	@FieldAlias("reid")
	private String pk_rejectreason;//档案主键
	
	@FieldAlias("corpid")
	private String pk_corp;//公司主键
	
	@FieldAlias("reason")
	private String vreason;//驳回原因
	
	@FieldAlias("suggest")
	private String vsuggest;//修改建议
	
	@FieldAlias("note")
	private String vnote;//备注
	
    @FieldAlias("operatorid")
    private String coperatorid; // 录入人

    @FieldAlias("zddate")
    private DZFDate doperatedate; // 录入日期
    
    @FieldAlias("lmpsnid")
    private String lastmodifypsnid; // 最后修改人
    
    @FieldAlias("dr")
    private Integer dr; // 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts; // 时间戳

	public String getPk_rejectreason() {
		return pk_rejectreason;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getVreason() {
		return vreason;
	}

	public String getVsuggest() {
		return vsuggest;
	}

	public String getVnote() {
		return vnote;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public String getLastmodifypsnid() {
		return lastmodifypsnid;
	}

	public Integer getDr() {
		return dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setPk_rejectreason(String pk_rejectreason) {
		this.pk_rejectreason = pk_rejectreason;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setVreason(String vreason) {
		this.vreason = vreason;
	}

	public void setVsuggest(String vsuggest) {
		this.vsuggest = vsuggest;
	}

	public void setVnote(String vnote) {
		this.vnote = vnote;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public void setLastmodifypsnid(String lastmodifypsnid) {
		this.lastmodifypsnid = lastmodifypsnid;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getPKFieldName() {
		return "pk_rejectreason";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_rejectreason";
	}

}
