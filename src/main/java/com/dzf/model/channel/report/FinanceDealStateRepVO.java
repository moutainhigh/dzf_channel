package com.dzf.model.channel.report;

import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;

public class FinanceDealStateRepVO extends DataVO {

	private static final long serialVersionUID = -1352059492059162045L;
	
	@FieldAlias("custsmall")
	private Integer icustsmall;//小规模数量
	
	@FieldAlias("custtaxpay")
	private Integer icusttaxpay;//一般纳税人数量
	
	@FieldAlias("newsmall")
	private Integer inewsmall;//新增小规模数量
	
	@FieldAlias("stocksmall")
	private Integer istocksmall;//存量小规模数量
	
	@FieldAlias("newtaxpay")
	private Integer inewtaxpay;//新增一般纳税人数量
	
	@FieldAlias("stocktaxpay")
	private Integer istocktaxpay;//存量一般纳税人数量
	
	@FieldAlias("custrates")
	private DZFDouble icustratesmall;//客户占比-小规模
	
	@FieldAlias("custratet")
	private DZFDouble icustratetaxpay;//客户占比-一般纳税人
	
	@FieldAlias("vouchernums")
	private Integer ivouchernummall;//凭证数量-小规模
	
	@FieldAlias("vouchernumt")
	private Integer ivouchernumtaxpay;//凭证数量-一般纳税人
	
	@FieldAlias("dreldate")
    private DZFDate drelievedate;//解约日期

	public DZFDate getDrelievedate() {
		return drelievedate;
	}

	public void setDrelievedate(DZFDate drelievedate) {
		this.drelievedate = drelievedate;
	}

	public Integer getInewsmall() {
		return inewsmall;
	}

	public void setInewsmall(Integer inewsmall) {
		this.inewsmall = inewsmall;
	}

	public Integer getIstocksmall() {
		return istocksmall;
	}

	public void setIstocksmall(Integer istocksmall) {
		this.istocksmall = istocksmall;
	}

	public Integer getInewtaxpay() {
		return inewtaxpay;
	}

	public void setInewtaxpay(Integer inewtaxpay) {
		this.inewtaxpay = inewtaxpay;
	}

	public Integer getIstocktaxpay() {
		return istocktaxpay;
	}

	public void setIstocktaxpay(Integer istocktaxpay) {
		this.istocktaxpay = istocktaxpay;
	}

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
