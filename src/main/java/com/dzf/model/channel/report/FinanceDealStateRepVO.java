package com.dzf.model.channel.report;

import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

public class FinanceDealStateRepVO extends DataVO {

	private static final long serialVersionUID = -1352059492059162045L;
	
	@FieldAlias("custsmall")
	private Integer icustsmall;//小规模数量
	
	@FieldAlias("custtaxpay")
	private Integer icusttaxpay;//一般纳税人数量
	
	@FieldAlias("custrates")
	private DZFDouble icustratesmall;//客户占比-小规模
	
	@FieldAlias("custratet")
	private DZFDouble icustratetaxpay;//客户占比-一般纳税人
	
	@FieldAlias("vouchernums")
	private Integer ivouchernummall;//凭证数量-小规模
	
	@FieldAlias("vouchernumt")
	private Integer ivouchernumtaxpay;//凭证数量-一般纳税人

	public Integer getIcustsmall() {
		return icustsmall;
	}

	public void setIcustsmall(Integer icustsmall) {
		this.icustsmall = icustsmall;
	}

	public Integer getIcusttaxpay() {
		return icusttaxpay;
	}

	public void setIcusttaxpay(Integer icusttaxpay) {
		this.icusttaxpay = icusttaxpay;
	}

	public DZFDouble getIcustratesmall() {
		return icustratesmall;
	}

	public void setIcustratesmall(DZFDouble icustratesmall) {
		this.icustratesmall = icustratesmall;
	}

	public DZFDouble getIcustratetaxpay() {
		return icustratetaxpay;
	}

	public void setIcustratetaxpay(DZFDouble icustratetaxpay) {
		this.icustratetaxpay = icustratetaxpay;
	}

	public Integer getIvouchernummall() {
		return ivouchernummall;
	}

	public Integer getIvouchernumtaxpay() {
		return ivouchernumtaxpay;
	}

	public void setIvouchernummall(Integer ivouchernummall) {
		this.ivouchernummall = ivouchernummall;
	}

	public void setIvouchernumtaxpay(Integer ivouchernumtaxpay) {
		this.ivouchernumtaxpay = ivouchernumtaxpay;
	}

}
