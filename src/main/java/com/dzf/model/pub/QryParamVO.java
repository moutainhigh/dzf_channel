package com.dzf.model.pub;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;

/**
 * 公用查询条件VO
 * @author 宗岩
 *
 */
@SuppressWarnings("rawtypes")
public class QryParamVO extends SuperVO {

	private static final long serialVersionUID = -8140085829123699985L;

	@FieldAlias("id")
	private String pk_bill;// 主键

	@FieldAlias("cpid")
	private String pk_corp;// 会计机构
	
	@FieldAlias("cpcode")
	private String corpcode;// 会计编码
	
	@FieldAlias("cpname")
	private String corpname;// 会计名称

	@FieldAlias("cpkid")
	private String pk_corpk;// 客户
	
	@FieldAlias("cpkcode")
	private String corpkcode;
	
	@FieldAlias("cpkname")
	private String corpkname;

	@FieldAlias("uid")
	private String cuserid;// 用户ID
	
	@FieldAlias("ucode")
	private String user_code;// 用户编码

	@FieldAlias("uname")
	private String user_name;// 用户名称
	
	@FieldAlias("qtype")
	private Integer qrytype;//查询状态
	
	public String getPk_bill() {
		return pk_bill;
	}

	public void setPk_bill(String pk_bill) {
		this.pk_bill = pk_bill;
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

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public String getUser_code() {
		return user_code;
	}

	public void setUser_code(String user_code) {
		this.user_code = user_code;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public Integer getQrytype() {
		return qrytype;
	}

	public void setQrytype(Integer qrytype) {
		this.qrytype = qrytype;
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
