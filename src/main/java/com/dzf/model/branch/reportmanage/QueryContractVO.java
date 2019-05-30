package com.dzf.model.branch.reportmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;

/**
 * 合同查询VO
 */
@SuppressWarnings("rawtypes")
public class QueryContractVO extends SuperVO {

	@FieldAlias("contractid")
	private String pk_contract; // 合同主键

	@FieldAlias("uname")
	public String unitname;// 公司名称

	@FieldAlias("ucode")
	public String unitcode;// 公司编码

	@FieldAlias("enum")
	public Integer expirenum;// 到期合同数

	@FieldAlias("snum")
	public Integer signednum;// 已签合同数

	@FieldAlias("usnum")
	public Integer unsignednum;// 未签合同数

	@FieldAlias("locorpnum")
	public Integer losscorpnum;// 流失客户数
	
	public DZFDate qjq;//查询日期

	
	public String getPk_contract() {
		return pk_contract;
	}

	public void setPk_contract(String pk_contract) {
		this.pk_contract = pk_contract;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public Integer getExpirenum() {
		return expirenum;
	}

	public void setExpirenum(Integer expirenum) {
		this.expirenum = expirenum;
	}

	public Integer getSignednum() {
		return signednum;
	}

	public void setSignednum(Integer signednum) {
		this.signednum = signednum;
	}

	public Integer getUnsignednum() {
		return unsignednum;
	}

	public void setUnsignednum(Integer unsignednum) {
		this.unsignednum = unsignednum;
	}

	public Integer getLosscorpnum() {
		return losscorpnum;
	}

	public void setLosscorpnum(Integer losscorpnum) {
		this.losscorpnum = losscorpnum;
	}

	public DZFDate getQjq() {
		return qjq;
	}

	public void setQjq(DZFDate qjq) {
		this.qjq = qjq;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
