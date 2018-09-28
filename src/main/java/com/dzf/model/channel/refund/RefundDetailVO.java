package com.dzf.model.channel.refund;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;

/**
 * 退款明细查询VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class RefundDetailVO extends SuperVO {

	private static final long serialVersionUID = -66170919533640249L;
	
	@FieldAlias("corpid")
	private String pk_corp; // 加盟商主键
	
	@FieldAlias("corpcd")
	private String corpcode; // 加盟商编码

	@FieldAlias("corpnm")
	private String corpname; // 加盟商名称
	
	@FieldAlias("corpkid")
	private String pk_corpk; // 客户主键
	
	@FieldAlias("corpkcd")
	private String corpkcode; // 客户编码

	@FieldAlias("corpkna")
	private String corpkname; // 客户名称
	
	@FieldAlias("aname")
    private String areaname;//大区名称 (运营区域)
	
	@FieldAlias("area")
	private String varea;// 地区：取客户的省+市
	
	@FieldAlias("ovince")
	public Integer vprovince;// 省/市
	
	@FieldAlias("mname")
	private String vmanagername; // 渠道经理
	
	@FieldAlias("remny")
	private DZFDouble nreturnmny;// 退款金额
	
    @FieldAlias("memo")
    private String vmemo;//摘要
    
    @FieldAlias("refdate")
    private DZFDate drefunddate;//退款日期
    
	@FieldAlias("vcode")
	private String vbillcode;//单据编号
	
	@FieldAlias("ndesummny")
	private DZFDouble ndedsummny;// 原扣款金额
	
	@FieldAlias("dedate")
	private DZFDate deductdata;// 原扣款日期

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
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

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public String getCorpkcode() {
		return corpkcode;
	}

	public void setCorpkcode(String corpkcode) {
		this.corpkcode = corpkcode;
	}

	public String getCorpkname() {
		return corpkname;
	}

	public void setCorpkname(String corpkname) {
		this.corpkname = corpkname;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getVarea() {
		return varea;
	}

	public void setVarea(String varea) {
		this.varea = varea;
	}

	public String getVmanagername() {
		return vmanagername;
	}

	public void setVmanagername(String vmanagername) {
		this.vmanagername = vmanagername;
	}

	public DZFDouble getNreturnmny() {
		return nreturnmny;
	}

	public void setNreturnmny(DZFDouble nreturnmny) {
		this.nreturnmny = nreturnmny;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public DZFDate getDrefunddate() {
		return drefunddate;
	}

	public void setDrefunddate(DZFDate drefunddate) {
		this.drefunddate = drefunddate;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public DZFDouble getNdedsummny() {
		return ndedsummny;
	}

	public void setNdedsummny(DZFDouble ndedsummny) {
		this.ndedsummny = ndedsummny;
	}

	public DZFDate getDeductdata() {
		return deductdata;
	}

	public void setDeductdata(DZFDate deductdata) {
		this.deductdata = deductdata;
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
