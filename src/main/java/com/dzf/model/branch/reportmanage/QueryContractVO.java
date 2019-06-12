package com.dzf.model.branch.reportmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;

/**
 * 合同查询VO
 */
@SuppressWarnings("rawtypes")
public class QueryContractVO extends SuperVO {

	@FieldAlias("contractid")
	private String pk_contract; // 合同主键

	@FieldAlias("uname")
	public String unitname;// 公司名称
	
	@FieldAlias("pk_bset")
	public String pk_branchset;// 机构id

	@FieldAlias("ucode")
	public String innercode;// 公司编码

	@FieldAlias("enum")
	public Integer expirenum;// 到期合同数

	@FieldAlias("snum")
	public Integer signednum;// 已签合同数

	@FieldAlias("usnum")
	public Integer unsignednum;// 未签合同数

	@FieldAlias("locorpnum")
	public Integer losscorpnum;// 流失客户数
	
	@FieldAlias("qjq")
	public String qjq;//查询日期
	
	@FieldAlias("nextqjq")
	public String nextqjq;//下个日期
	
	@FieldAlias("corpids")
	public String corpids;//客户主键
	
	@FieldAlias("pk_corp")
	public String  pk_corp;//公司ID
	
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

	public String getInnercode() {
		return innercode;
	}

	public void setInnercode(String innercode) {
		this.innercode = innercode;
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

	public String getQjq() {
		return qjq;
	}

	public void setQjq(String qjq) {
		this.qjq = qjq;
	}
	
	public String getCorpids() {
		return corpids;
	}

	public void setCorpids(String corpids) {
		this.corpids = corpids;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	
	public String getNextqjq() {
		return nextqjq;
	}

	public void setNextqjq(String nextqjq) {
		this.nextqjq = nextqjq;
	}
	
	public String getPk_branchset() {
		return pk_branchset;
	}

	public void setPk_branchset(String pk_branchset) {
		this.pk_branchset = pk_branchset;
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
