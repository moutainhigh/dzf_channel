package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;

/**
 * 财务处理分析明细VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class FinanceDetailVO extends SuperVO {

	private static final long serialVersionUID = 2807839507108801788L;
	
	@FieldAlias("corpid")
	private String pk_corp;//加盟商主键
	
	@FieldAlias("corpnm")
	private String corpname;//加盟商名称
	
	@FieldAlias("corpkid")
	private String pk_corpk;//客户主键
	
	@FieldAlias("corpkcd")
	private String corpkcode;//客户编码
	
	@FieldAlias("corpknm")
	private String corpkname;//客户名称
	
	@FieldAlias("period")
	private String vperiod;//查询期间
	
	@FieldAlias("deptnm")
	private String vdeptname;//部门
	
	private String jzstatus;//记账状态
	
	@FieldAlias("accheck")
	private Integer iacctcheck;//账务检查： 0：未勾选；  1勾选；

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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

	public String getVperiod() {
		return vperiod;
	}

	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}

	public String getVdeptname() {
		return vdeptname;
	}

	public void setVdeptname(String vdeptname) {
		this.vdeptname = vdeptname;
	}

	public String getJzstatus() {
		return jzstatus;
	}

	public void setJzstatus(String jzstatus) {
		this.jzstatus = jzstatus;
	}

	public Integer getIacctcheck() {
		return iacctcheck;
	}

	public void setIacctcheck(Integer iacctcheck) {
		this.iacctcheck = iacctcheck;
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
