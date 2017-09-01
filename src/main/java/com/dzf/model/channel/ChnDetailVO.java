package com.dzf.model.channel;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 渠道-付款-明细VO
 * @author 宗岩
 *
 */
@SuppressWarnings("rawtypes")
public class ChnDetailVO extends SuperVO {

	private static final long serialVersionUID = 6842760228104510287L;
	
	@FieldAlias("id")
	private String pk_detail;
	
	@FieldAlias("corpid")
	private String pk_corp;
	
	@FieldAlias("corpnm")
	private String corpname;// 机构名称
	
	@FieldAlias("usemny")
	private DZFDouble nusedmny;//已用金额
	
	@FieldAlias("balmny")
	private DZFDouble nbalance;//余额
	
	@FieldAlias("npmny")
	private DZFDouble npaymny;//付款金额
	
	@FieldAlias("iptype")
	private Integer ipaytype;//付款类型   1:加盟费；2：预付款
	
	@FieldAlias("ptypenm")
	private String vpaytypename;//付款类型名称 
	
	@FieldAlias("billid")
    private String pk_bill;//业务ID
	
    @FieldAlias("memo")
    private String vmemo;// 备注
    
    @FieldAlias("cid")
    private String coperatorid;// 录入人
    
    @FieldAlias("ddate")
    private DZFDate doperatedate;// 录入日期
    
    private Integer dr;
    
    private DZFDateTime ts;
    
	public String getVpaytypename() {
		return vpaytypename;
	}

	public void setVpaytypename(String vpaytypename) {
		this.vpaytypename = vpaytypename;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getPk_bill() {
		return pk_bill;
	}

	public void setPk_bill(String pk_bill) {
		this.pk_bill = pk_bill;
	}

	public String getPk_detail() {
		return pk_detail;
	}

	public void setPk_detail(String pk_detail) {
		this.pk_detail = pk_detail;
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

	public DZFDouble getNbalance() {
		return nbalance;
	}

	public void setNbalance(DZFDouble nbalance) {
		this.nbalance = nbalance;
	}

	public DZFDouble getNpaymny() {
		return npaymny;
	}

	public void setNpaymny(DZFDouble npaymny) {
		this.npaymny = npaymny;
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
		return "pk_detail";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_detail";
	}

}
