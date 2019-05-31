package com.dzf.model.branch.setup;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

@SuppressWarnings("rawtypes")
public class UserBranchVO extends SuperVO {

	private static final long serialVersionUID = 1L;
	
	@FieldAlias("ubid")
	private String pk_user_branch;//主键
	
	@FieldAlias("corpid")
	private String pk_corp;//公司ID

	@FieldAlias("branchid")
	private String pk_branchset;//机构ID

	@FieldAlias("uid")
	private String cuserid;//用户ID
	
	@FieldAlias("oid")
	private String coperatorid;//录入人ID
	
	@FieldAlias("odate")
	private DZFDate doperatedate;
	
	@FieldAlias("dr")
	private Integer dr; // 删除标记

	@FieldAlias("ts")
	private DZFDateTime ts; // 时间戳

	public String getPk_user_branch() {
		return pk_user_branch;
	}

	public void setPk_user_branch(String pk_user_branch) {
		this.pk_user_branch = pk_user_branch;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_branchset() {
		return pk_branchset;
	}

	public void setPk_branchset(String pk_branchset) {
		this.pk_branchset = pk_branchset;
	}

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
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
		return "pk_user_branch";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "br_user_branch";
	}

}
