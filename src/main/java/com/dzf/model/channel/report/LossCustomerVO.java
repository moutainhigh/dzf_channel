package com.dzf.model.channel.report;

import com.dzf.pub.Field.FieldAlias;

/***
 * 流失客户明细
 */
public class LossCustomerVO extends DataVO{

	@FieldAlias("corpkid")
	private String pk_corpk;//客户主键
	
	@FieldAlias("cname")
	private String corpkname;//客户名称
	
	@FieldAlias("chname")
	private String chargedeptname;//纳税人资格
	
	@FieldAlias("jdate")
	private String jzdate;//建账日期
	
	@FieldAlias("istatus")
	private String isClose;//是否关账
	
	@FieldAlias("stime")
	private String stopdatetime;//停用时间
	
	@FieldAlias("sreason")
	private String stopreason;//停用原因
	
	@FieldAlias("sname")
	private String stopname;//停用人

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public String getCorpkname() {
		return corpkname;
	}

	public void setCorpkname(String corpkname) {
		this.corpkname = corpkname;
	}

	public String getChargedeptname() {
		return chargedeptname;
	}

	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
	}

	public String getJzdate() {
		return jzdate;
	}

	public void setJzdate(String jzdate) {
		this.jzdate = jzdate;
	}

	public String getIsClose() {
		return isClose;
	}

	public void setIsClose(String isClose) {
		this.isClose = isClose;
	}

	public String getStopdatetime() {
		return stopdatetime;
	}

	public void setStopdatetime(String stopdatetime) {
		this.stopdatetime = stopdatetime;
	}

	public String getStopname() {
		return stopname;
	}

	public void setStopname(String stopname) {
		this.stopname = stopname;
	}

	public String getStopreason() {
		return stopreason;
	}

	public void setStopreason(String stopreason) {
		this.stopreason = stopreason;
	}
	
	
	
}
