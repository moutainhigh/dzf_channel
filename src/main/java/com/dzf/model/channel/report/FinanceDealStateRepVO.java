package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

@SuppressWarnings("rawtypes")
public class FinanceDealStateRepVO extends SuperVO {

	private static final long serialVersionUID = -1352059492059162045L;
	
	@FieldAlias("larea")
	private String vlargearea;//大区
	 
	@FieldAlias("provin")
	private String vprovince;//省份
	
	@FieldAlias("pid")
	private String pk_corp;//会计公司主键
	
	@FieldAlias("pname")
	private String vcorpname;//加盟商名称
	
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

	public String getVlargearea() {
		return vlargearea;
	}

	public void setVlargearea(String vlargearea) {
		this.vlargearea = vlargearea;
	}

	public String getVprovince() {
		return vprovince;
	}

	public void setVprovince(String vprovince) {
		this.vprovince = vprovince;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVcorpname() {
		return vcorpname;
	}

	public void setVcorpname(String vcorpname) {
		this.vcorpname = vcorpname;
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
