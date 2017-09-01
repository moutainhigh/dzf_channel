package com.dzf.model.channel;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 渠道-付款单VO
 * 
 * @author dzf
 *
 */
@SuppressWarnings("rawtypes")
public class ChnPayBillVO extends SuperVO {

	private static final long serialVersionUID = -4729021634998726662L;
	@FieldAlias("billid")
	private String pk_paybill;// 主键
	@FieldAlias("corpid")
	private String pk_corp;// 机构主键
	@FieldAlias("corpnm")
	private String corpname;// 机构名称
	@FieldAlias("vcode")
	private String vbillcode;// 单据号
	@FieldAlias("dpdate")
	private DZFDate dpaydate;// 付款日期
	@FieldAlias("vhid")
	private String vhandleid;// 经办人ID
	@FieldAlias("vhname")
	private String vhandlename;// 经办人名称
	@FieldAlias("vcid")
	private String vconfirmid;// 确认人ID
	@FieldAlias("vcname")
	private String vconfirmname;// 确认人名称
	@FieldAlias("dctime")
	private DZFDateTime dconfirmtime;// 确认时间
	@FieldAlias("memo")
	private String vmemo;// 备注
	@FieldAlias("cid")
	private String coperatorid;// 录入人
	@FieldAlias("ddate")
	private DZFDate doperatedate;// 录入日期

	private Integer dr;

	private DZFDateTime ts;
	@FieldAlias("npmny")
	private DZFDouble npaymny;// 付款金额
	@FieldAlias("ipmode")
	private Integer ipaymode;// 1:银行转账；2:支付宝；3：微信
	@FieldAlias("iptype")
	private Integer ipaytype;// 1:加盟费；2：预付款
	@FieldAlias("status")
	private Integer vstatus;// 1-待提交； 2-待确认；3-已确认
	
    @FieldAlias("tstp")
    private DZFDateTime tstamp;//时间戳
    
    @FieldAlias("errmsg")
    private String verrmsg;//错误信息

	public String getVerrmsg() {
		return verrmsg;
	}

	public void setVerrmsg(String verrmsg) {
		this.verrmsg = verrmsg;
	}

	public DZFDateTime getTstamp() {
		return tstamp;
	}

	public void setTstamp(DZFDateTime tstamp) {
		this.tstamp = tstamp;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getPk_paybill() {
		return pk_paybill;
	}

	public void setPk_paybill(String pk_paybill) {
		this.pk_paybill = pk_paybill;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public DZFDate getDpaydate() {
		return dpaydate;
	}

	public void setDpaydate(DZFDate dpaydate) {
		this.dpaydate = dpaydate;
	}

	public String getVhandleid() {
		return vhandleid;
	}

	public void setVhandleid(String vhandleid) {
		this.vhandleid = vhandleid;
	}

	public String getVhandlename() {
		return vhandlename;
	}

	public void setVhandlename(String vhandlename) {
		this.vhandlename = vhandlename;
	}

	public String getVconfirmid() {
		return vconfirmid;
	}

	public void setVconfirmid(String vconfirmid) {
		this.vconfirmid = vconfirmid;
	}

	public String getVconfirmname() {
		return vconfirmname;
	}

	public void setVconfirmname(String vconfirmname) {
		this.vconfirmname = vconfirmname;
	}

	public DZFDateTime getDconfirmtime() {
		return dconfirmtime;
	}

	public void setDconfirmtime(DZFDateTime dconfirmtime) {
		this.dconfirmtime = dconfirmtime;
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

	public DZFDouble getNpaymny() {
		return npaymny;
	}

	public void setNpaymny(DZFDouble npaymny) {
		this.npaymny = npaymny;
	}

	public Integer getIpaymode() {
		return ipaymode;
	}

	public void setIpaymode(Integer ipaymode) {
		this.ipaymode = ipaymode;
	}

	public Integer getIpaytype() {
		return ipaytype;
	}

	public void setIpaytype(Integer ipaytype) {
		this.ipaytype = ipaytype;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
	}

	@Override
	public String getPKFieldName() {
		return "pk_paybill";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_paybill";
	}

}
