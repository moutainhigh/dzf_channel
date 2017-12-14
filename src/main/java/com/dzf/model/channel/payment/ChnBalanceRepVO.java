package com.dzf.model.channel.payment;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 付款余额查询VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class ChnBalanceRepVO extends SuperVO{

	private static final long serialVersionUID = -2238601467643308081L;
	
	@FieldAlias("corpid")
	private String pk_corp;//机构主键
	
	@FieldAlias("corpnm")
	private String corpname;// 机构名称
	
	@FieldAlias("usemny")
	private DZFDouble nusedmny;//已用金额
	
	@FieldAlias("npmny")
	private DZFDouble npaymny;//付款金额
	
	@FieldAlias("iptype")
	private Integer ipaytype;//付款类型   1:加盟费；2：预付款
	
	@FieldAlias("ptypenm")
	private String vpaytypename;//付款类型名称 
	
	@FieldAlias("balmny")
	private DZFDouble nbalance;//余额
	
    @FieldAlias("initbal")
    private DZFDouble initbalance;//期初余额
    
    @FieldAlias("bail")
    private DZFDouble bail;//保证金
    
    @FieldAlias("charge")
    private DZFDouble charge;//预付款
    
	public String getVpaytypename() {
		return vpaytypename;
	}

	public void setVpaytypename(String vpaytypename) {
		this.vpaytypename = vpaytypename;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getCorpname() {
		return corpname;
	}

	public DZFDouble getNusedmny() {
		return nusedmny;
	}

	public DZFDouble getNpaymny() {
		return npaymny;
	}

	public Integer getIpaytype() {
		return ipaytype;
	}

	public DZFDouble getNbalance() {
		return nbalance;
	}

	public DZFDouble getInitbalance() {
		return initbalance;
	}

	public DZFDouble getBail() {
		return bail;
	}

	public DZFDouble getCharge() {
		return charge;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public void setNusedmny(DZFDouble nusedmny) {
		this.nusedmny = nusedmny;
	}

	public void setNpaymny(DZFDouble npaymny) {
		this.npaymny = npaymny;
	}

	public void setIpaytype(Integer ipaytype) {
		this.ipaytype = ipaytype;
	}

	public void setNbalance(DZFDouble nbalance) {
		this.nbalance = nbalance;
	}

	public void setInitbalance(DZFDouble initbalance) {
		this.initbalance = initbalance;
	}

	public void setBail(DZFDouble bail) {
		this.bail = bail;
	}

	public void setCharge(DZFDouble charge) {
		this.charge = charge;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
