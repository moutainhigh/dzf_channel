package com.dzf.model.branch.setup;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 机构设置子表VO
 */
@SuppressWarnings("rawtypes")
public class BranchInstSetupBVO extends SuperVO {
	
	 
	@FieldAlias("pk_bcorp")
	private String pk_branchcorp;
	
	@FieldAlias("pk_bset")
	private String pk_branchset;//主表主键

	@FieldAlias("corpid")
	private String pk_corp;// 公司id
	
	@FieldAlias("uname")
	private String unitname;//公司名称

	@FieldAlias("name")
	private String vname;// 企业识别码

	@FieldAlias("lman")
	private String linkman; //联系人
	
	private String phone;//联系方式
	
	private String isseal; //是否封存  Y:封存  N:启用
	
	@FieldAlias("memo")
	private String vmemo; //备注
	
	@FieldAlias("opid")
	private String coperatorid; // 录入人

	@FieldAlias("opdate")
	private DZFDate doperatedate; // 录入日期

	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间戳
	
	public String getPk_branchcorp() {
		return pk_branchcorp;
	}

	public void setPk_branchcorp(String pk_branchcorp) {
		this.pk_branchcorp = pk_branchcorp;
	}

	public String getPk_branchset() {
		return pk_branchset;
	}

	public void setPk_branchset(String pk_branchset) {
		this.pk_branchset = pk_branchset;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIsseal() {
		return isseal;
	}

	public void setIsseal(String isseal) {
		this.isseal = isseal;
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
	
	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	@Override
	public String getPKFieldName() {
		return "pk_branchcorp";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_branchset";
	}

	@Override
	public String getTableName() {
		return "br_branchcorp";
	}

}
