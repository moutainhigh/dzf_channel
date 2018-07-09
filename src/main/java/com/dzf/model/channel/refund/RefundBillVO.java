package com.dzf.model.channel.refund;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 付款单VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class RefundBillVO extends SuperVO {

	private static final long serialVersionUID = 5994745446126620090L;
	
	@FieldAlias("refid")
	private String pk_refund;//主键
	
	@FieldAlias("corpid")
	private String pk_corp;//加盟商主键
	
	@FieldAlias("corpcode")
	private String corpcode;//加盟商编码(仅作展示)
	
	@FieldAlias("corp")
	private String corpname;//加盟商名称(仅作展示)
	
	@FieldAlias("corpfid")
	private String fathercorp;//上级机构主键
	
	@FieldAlias("vcode")
	private String vbillcode;//退款单号
	
	@FieldAlias("yfktk")
	private DZFDouble nrefyfkmny;//预付款退款
	
	@FieldAlias("bzjtk")
	private DZFDouble nrefbzjmny;//保证金退款
	
	@FieldAlias("stat")
	private Integer istatus;//状态   0：待确认；1：已确认；
	
	@FieldAlias("memo")
	private String vmemo;//备注
	
	@FieldAlias("operid")
	private String coperatorid;//录入人
	
	@FieldAlias("oper")
	private String voperator;//录入人姓名
	
	@FieldAlias("operdate")
	private DZFDate doperatedate;//录入日期
	
	@FieldAlias("refdate")
	private DZFDate drefunddate;//退款日期
	
	@FieldAlias("confid")
	private String vconfirmid;//确认人
	
	@FieldAlias("confdate")
	private DZFDate dconfirmdate;//确认日期
	
	@FieldAlias("aname")
    private String areaname;// 大区（仅作展示）
	
	@FieldAlias("provname")
	public String vprovname;// 地区（仅作展示）
	
    @FieldAlias("dr")
    private Integer dr; // 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts; // 时间戳
    
    @FieldAlias("errmsg")
    private String verrmsg;//错误信息（仅作展示）

	public String getVerrmsg() {
		return verrmsg;
	}

	public void setVerrmsg(String verrmsg) {
		this.verrmsg = verrmsg;
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

	public String getVoperator() {
		return voperator;
	}

	public void setVoperator(String voperator) {
		this.voperator = voperator;
	}

	public DZFDate getDconfirmdate() {
		return dconfirmdate;
	}

	public void setDconfirmdate(DZFDate dconfirmdate) {
		this.dconfirmdate = dconfirmdate;
	}

	public DZFDate getDrefunddate() {
		return drefunddate;
	}

	public void setDrefunddate(DZFDate drefunddate) {
		this.drefunddate = drefunddate;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getVprovname() {
		return vprovname;
	}

	public void setVprovname(String vprovname) {
		this.vprovname = vprovname;
	}

	public String getPk_refund() {
		return pk_refund;
	}

	public void setPk_refund(String pk_refund) {
		this.pk_refund = pk_refund;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getCorpcode() {
		return corpcode;
	}

	public void setCorpcode(String corpcode) {
		this.corpcode = corpcode;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getFathercorp() {
		return fathercorp;
	}

	public void setFathercorp(String fathercorp) {
		this.fathercorp = fathercorp;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public DZFDouble getNrefyfkmny() {
		return nrefyfkmny;
	}

	public void setNrefyfkmny(DZFDouble nrefyfkmny) {
		this.nrefyfkmny = nrefyfkmny;
	}

	public DZFDouble getNrefbzjmny() {
		return nrefbzjmny;
	}

	public void setNrefbzjmny(DZFDouble nrefbzjmny) {
		this.nrefbzjmny = nrefbzjmny;
	}

	public Integer getIstatus() {
		return istatus;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
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

	public String getVconfirmid() {
		return vconfirmid;
	}

	public void setVconfirmid(String vconfirmid) {
		this.vconfirmid = vconfirmid;
	}

	@Override
	public String getPKFieldName() {
		return "pk_refund";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_refund";
	}

}
