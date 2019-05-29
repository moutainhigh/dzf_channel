package com.dzf.model.branch.setup;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 机构设置主表VO
 */
@SuppressWarnings("rawtypes")
public class BranchInstSetupVO extends SuperVO {

	@FieldAlias("pk_bset")
	private String pk_branchset;

	@FieldAlias("corpid")
	private String pk_corp;// 公司id

	@FieldAlias("name")
	private String vname;// 机构名称

	@FieldAlias("oper")
	private String coperatorid; // 录入人

	@FieldAlias("opdate")
	private DZFDate doperatedate; // 录入日期

	@FieldAlias("dr")
	private Integer dr; // 删除标记

	@FieldAlias("ts")
	private DZFDateTime ts; // 时间戳
	
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
		return "pk_branchset";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "br_branchset";
	}

}
