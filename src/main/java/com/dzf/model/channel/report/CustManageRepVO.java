package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;

@SuppressWarnings("rawtypes")
public class CustManageRepVO extends SuperVO{

	private static final long serialVersionUID = -4959336844095570423L;
	
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
	
	@FieldAlias("custs1")
	private Integer icustsmall1;//第一类行业数量-小规模
	
	@FieldAlias("custt1")
	private Integer icusttaxpay1;//第一类行业数量-一般纳税人
	
	@FieldAlias("custs2")
	private Integer icustsmall2;//第二类行业数量-小规模
	
	@FieldAlias("custt2")
	private Integer icusttaxpay2;//第二类行业数量-一般纳税人
	
	@FieldAlias("custs3")
	private Integer icustsmall3;//第三类行业数量-小规模
	
	@FieldAlias("custt3")
	private Integer icusttaxpay3;//第三类行业数量-一般纳税人
	
	@FieldAlias("custs4")
	private Integer icustsmall4;//第四类行业数量-小规模
	
	@FieldAlias("custt4")
	private Integer icusttaxpay4;//第四类行业数量-一般纳税人
	
	@FieldAlias("custs5")
	private Integer icustsmall5;//第五类行业数量-小规模
	
	@FieldAlias("custt5")
	private Integer icusttaxpay5;//第五类行业数量-一般纳税人
	
	@FieldAlias("custs6")
	private Integer icustsmall6;//其他类行业数量-小规模
	
	@FieldAlias("custt6")
	private Integer icusttaxpay6;//其他类行业数量-一般纳税人
	
	@FieldAlias("rates1")
	private Integer icustratesmall1;//第一类行业占比-小规模
	
	@FieldAlias("ratet1")
	private Integer icustratetaxpay1;//第一类行业占比-一般纳税人
	
	@FieldAlias("rates2")
	private Integer icustratesmall2;//第二类行业占比-小规模
	
	@FieldAlias("ratet2")
	private Integer icustratetaxpay2;//第二类行业占比-一般纳税人
	
	@FieldAlias("rates3")
	private Integer icustratesmall3;//第三类行业占比-小规模
	
	@FieldAlias("ratet3")
	private Integer icustratetaxpay3;//第三类行业占比-一般纳税人
	
	@FieldAlias("rates4")
	private Integer icustratesmall4;//第四类行业占比-小规模
	
	@FieldAlias("ratet4")
	private Integer icustratetaxpay4;//第四类行业占比-一般纳税人
	
	@FieldAlias("rates5")
	private Integer icustratesmall5;//第五类行业占比-小规模
	
	@FieldAlias("ratet5")
	private Integer icustratetaxpay5;//第五类行业占比-一般纳税人
	
	@FieldAlias("rates6")
	private Integer icustratesmall6;//其他类行业占比-小规模
	
	@FieldAlias("ratet6")
	private Integer icustratetaxpay6;//其他类行业占比-一般纳税人

	public String getVlargearea() {
		return vlargearea;
	}

	public String getVprovince() {
		return vprovince;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getVcorpname() {
		return vcorpname;
	}

	public Integer getIcustsmall() {
		return icustsmall;
	}

	public Integer getIcusttaxpay() {
		return icusttaxpay;
	}

	public Integer getIcustsmall1() {
		return icustsmall1;
	}

	public Integer getIcusttaxpay1() {
		return icusttaxpay1;
	}

	public Integer getIcustsmall2() {
		return icustsmall2;
	}

	public Integer getIcusttaxpay2() {
		return icusttaxpay2;
	}

	public Integer getIcustsmall3() {
		return icustsmall3;
	}

	public Integer getIcusttaxpay3() {
		return icusttaxpay3;
	}

	public Integer getIcustsmall4() {
		return icustsmall4;
	}

	public Integer getIcusttaxpay4() {
		return icusttaxpay4;
	}

	public Integer getIcustsmall5() {
		return icustsmall5;
	}

	public Integer getIcusttaxpay5() {
		return icusttaxpay5;
	}

	public Integer getIcustsmall6() {
		return icustsmall6;
	}

	public Integer getIcusttaxpay6() {
		return icusttaxpay6;
	}

	public Integer getIcustratesmall1() {
		return icustratesmall1;
	}

	public Integer getIcustratetaxpay1() {
		return icustratetaxpay1;
	}

	public Integer getIcustratesmall2() {
		return icustratesmall2;
	}

	public Integer getIcustratetaxpay2() {
		return icustratetaxpay2;
	}

	public Integer getIcustratesmall3() {
		return icustratesmall3;
	}

	public Integer getIcustratetaxpay3() {
		return icustratetaxpay3;
	}

	public Integer getIcustratesmall4() {
		return icustratesmall4;
	}

	public Integer getIcustratetaxpay4() {
		return icustratetaxpay4;
	}

	public Integer getIcustratesmall5() {
		return icustratesmall5;
	}

	public Integer getIcustratetaxpay5() {
		return icustratetaxpay5;
	}

	public Integer getIcustratesmall6() {
		return icustratesmall6;
	}

	public Integer getIcustratetaxpay6() {
		return icustratetaxpay6;
	}

	public void setVlargearea(String vlargearea) {
		this.vlargearea = vlargearea;
	}

	public void setVprovince(String vprovince) {
		this.vprovince = vprovince;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setVcorpname(String vcorpname) {
		this.vcorpname = vcorpname;
	}

	public void setIcustsmall(Integer icustsmall) {
		this.icustsmall = icustsmall;
	}

	public void setIcusttaxpay(Integer icusttaxpay) {
		this.icusttaxpay = icusttaxpay;
	}

	public void setIcustsmall1(Integer icustsmall1) {
		this.icustsmall1 = icustsmall1;
	}

	public void setIcusttaxpay1(Integer icusttaxpay1) {
		this.icusttaxpay1 = icusttaxpay1;
	}

	public void setIcustsmall2(Integer icustsmall2) {
		this.icustsmall2 = icustsmall2;
	}

	public void setIcusttaxpay2(Integer icusttaxpay2) {
		this.icusttaxpay2 = icusttaxpay2;
	}

	public void setIcustsmall3(Integer icustsmall3) {
		this.icustsmall3 = icustsmall3;
	}

	public void setIcusttaxpay3(Integer icusttaxpay3) {
		this.icusttaxpay3 = icusttaxpay3;
	}

	public void setIcustsmall4(Integer icustsmall4) {
		this.icustsmall4 = icustsmall4;
	}

	public void setIcusttaxpay4(Integer icusttaxpay4) {
		this.icusttaxpay4 = icusttaxpay4;
	}

	public void setIcustsmall5(Integer icustsmall5) {
		this.icustsmall5 = icustsmall5;
	}

	public void setIcusttaxpay5(Integer icusttaxpay5) {
		this.icusttaxpay5 = icusttaxpay5;
	}

	public void setIcustsmall6(Integer icustsmall6) {
		this.icustsmall6 = icustsmall6;
	}

	public void setIcusttaxpay6(Integer icusttaxpay6) {
		this.icusttaxpay6 = icusttaxpay6;
	}

	public void setIcustratesmall1(Integer icustratesmall1) {
		this.icustratesmall1 = icustratesmall1;
	}

	public void setIcustratetaxpay1(Integer icustratetaxpay1) {
		this.icustratetaxpay1 = icustratetaxpay1;
	}

	public void setIcustratesmall2(Integer icustratesmall2) {
		this.icustratesmall2 = icustratesmall2;
	}

	public void setIcustratetaxpay2(Integer icustratetaxpay2) {
		this.icustratetaxpay2 = icustratetaxpay2;
	}

	public void setIcustratesmall3(Integer icustratesmall3) {
		this.icustratesmall3 = icustratesmall3;
	}

	public void setIcustratetaxpay3(Integer icustratetaxpay3) {
		this.icustratetaxpay3 = icustratetaxpay3;
	}

	public void setIcustratesmall4(Integer icustratesmall4) {
		this.icustratesmall4 = icustratesmall4;
	}

	public void setIcustratetaxpay4(Integer icustratetaxpay4) {
		this.icustratetaxpay4 = icustratetaxpay4;
	}

	public void setIcustratesmall5(Integer icustratesmall5) {
		this.icustratesmall5 = icustratesmall5;
	}

	public void setIcustratetaxpay5(Integer icustratetaxpay5) {
		this.icustratetaxpay5 = icustratetaxpay5;
	}

	public void setIcustratesmall6(Integer icustratesmall6) {
		this.icustratesmall6 = icustratesmall6;
	}

	public void setIcustratetaxpay6(Integer icustratetaxpay6) {
		this.icustratetaxpay6 = icustratetaxpay6;
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
