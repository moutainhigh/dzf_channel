package com.dzf.model.channel;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 渠道-付款-余额VO
 * @author zy
 *
 */

@SuppressWarnings("rawtypes")
public class ChnBalanceVO extends SuperVO {

	private static final long serialVersionUID = 3121686749449715796L;
	
	@FieldAlias("id")
	private String pk_balance;//主键
	
	@FieldAlias("corpid")
	private String pk_corp;//机构主键
	
	@FieldAlias("corpnm")
	private String corpname;// 机构名称
	
	@FieldAlias("usemny")
	private DZFDouble nusedmny;//已用金额
	
	@FieldAlias("npmny")
	private DZFDouble npaymny;//付款金额
	
	@FieldAlias("tickmny")
	private DZFDouble nticketmny;//开票金额
	
	@FieldAlias("iptype")
	private Integer ipaytype;//付款类型   1:加盟费；2：预付款
	
	@FieldAlias("balmny")
	private DZFDouble nbalance;//余额
	
    @FieldAlias("memo")
    private String vmemo;// 备注
    
    @FieldAlias("cid")
    private String coperatorid;// 录入人
    
    @FieldAlias("ddate")
    private DZFDate doperatedate;// 录入日期
    
    private Integer dr;
    
    private DZFDateTime ts;

	public DZFDouble getNbalance() {
		return nbalance;
	}

	public void setNbalance(DZFDouble nbalance) {
		this.nbalance = nbalance;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getPk_balance() {
		return pk_balance;
	}

	public void setPk_balance(String pk_balance) {
		this.pk_balance = pk_balance;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFDouble getNusedmny() {
		return nusedmny;
	}

	public void setNusedmny(DZFDouble nusedmny) {
		this.nusedmny = nusedmny;
	}

	public DZFDouble getNpaymny() {
		return npaymny;
	}

	public void setNpaymny(DZFDouble npaymny) {
		this.npaymny = npaymny;
	}

	public DZFDouble getNticketmny() {
		return nticketmny;
	}

	public void setNticketmny(DZFDouble nticketmny) {
		this.nticketmny = nticketmny;
	}

	public Integer getIpaytype() {
		return ipaytype;
	}

	public void setIpaytype(Integer ipaytype) {
		this.ipaytype = ipaytype;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
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
		return "pk_balance";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_balance";
	}

}
