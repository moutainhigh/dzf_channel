package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 加盟商客户数量、金额统计表
 * @author 宗岩
 *
 */
@SuppressWarnings("rawtypes")
public class CustNumMoneyRepVO extends SuperVO {

	private static final long serialVersionUID = 7914020685684503615L;
	
	@FieldAlias("larea")
	private String vlargearea;//大区
	 
	@FieldAlias("provin")
	private String vprovince;//省份
	
	@FieldAlias("pid")
	private String pk_corp;//会计公司主键
	
	@FieldAlias("pname")
	private String vcorpname;//加盟商名称
	
	@FieldAlias("stockcusts")
	private Integer istockcustsmall;//存量客户-小规模
	
	@FieldAlias("stockcustt")
	private Integer istockcusttaxpay;//存量客户-一般纳税人

	@FieldAlias("stockconts")
	private DZFDouble istockcontsmall;//存量客户合同-小规模
	
	@FieldAlias("stockcontt")
	private DZFDouble istockconttaxpay;//存量客户合同-一般纳税人
	
	@FieldAlias("newcusts")
	private Integer inewcustsmall;//新增客户-小规模
	
	@FieldAlias("newcustt")
	private Integer inewcusttaxpay;//新增客户-一般纳税人

	@FieldAlias("newconts")
	private DZFDouble inewcontsmall;//新增客户合同-小规模
	
	@FieldAlias("newcontt")
	private DZFDouble inewconttaxpay;//新增客户合同-一般纳税人
	
	@FieldAlias("renewcusts")
	private Integer irenewcustsmall;//续费客户-小规模
	
	@FieldAlias("renewcustt")
	private Integer irenewcusttaxpay;//续费客户-一般纳税人

	@FieldAlias("renewconts")
	private DZFDouble irenewcontsmall;//续费客户合同-小规模
	
	@FieldAlias("renewcontt")
	private DZFDouble irenewconttaxpay;//续费客户合同-一般纳税人
	
	@FieldAlias("newcustrate")
	private Integer inewcustrate;//新增客户增长率
	
	@FieldAlias("newcontrate")
	private Integer inewcontrate;//新增合同增长率
	
	@FieldAlias("renewcustrate")
	private Integer irenewcustrate;//续费客户占比
	
	@FieldAlias("renewcontrate")
	private Integer irenewcontrate;//续费合同占比
	
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

	public Integer getIstockcustsmall() {
		return istockcustsmall;
	}

	public Integer getIstockcusttaxpay() {
		return istockcusttaxpay;
	}

	public DZFDouble getIstockcontsmall() {
		return istockcontsmall;
	}

	public DZFDouble getIstockconttaxpay() {
		return istockconttaxpay;
	}

	public Integer getInewcustsmall() {
		return inewcustsmall;
	}

	public Integer getInewcusttaxpay() {
		return inewcusttaxpay;
	}

	public DZFDouble getInewcontsmall() {
		return inewcontsmall;
	}

	public DZFDouble getInewconttaxpay() {
		return inewconttaxpay;
	}

	public Integer getIrenewcustsmall() {
		return irenewcustsmall;
	}

	public Integer getIrenewcusttaxpay() {
		return irenewcusttaxpay;
	}

	public DZFDouble getIrenewcontsmall() {
		return irenewcontsmall;
	}

	public DZFDouble getIrenewconttaxpay() {
		return irenewconttaxpay;
	}

	public Integer getInewcustrate() {
		return inewcustrate;
	}

	public Integer getInewcontrate() {
		return inewcontrate;
	}

	public Integer getIrenewcustrate() {
		return irenewcustrate;
	}

	public Integer getIrenewcontrate() {
		return irenewcontrate;
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

	public void setIstockcustsmall(Integer istockcustsmall) {
		this.istockcustsmall = istockcustsmall;
	}

	public void setIstockcusttaxpay(Integer istockcusttaxpay) {
		this.istockcusttaxpay = istockcusttaxpay;
	}

	public void setIstockcontsmall(DZFDouble istockcontsmall) {
		this.istockcontsmall = istockcontsmall;
	}

	public void setIstockconttaxpay(DZFDouble istockconttaxpay) {
		this.istockconttaxpay = istockconttaxpay;
	}

	public void setInewcustsmall(Integer inewcustsmall) {
		this.inewcustsmall = inewcustsmall;
	}

	public void setInewcusttaxpay(Integer inewcusttaxpay) {
		this.inewcusttaxpay = inewcusttaxpay;
	}

	public void setInewcontsmall(DZFDouble inewcontsmall) {
		this.inewcontsmall = inewcontsmall;
	}

	public void setInewconttaxpay(DZFDouble inewconttaxpay) {
		this.inewconttaxpay = inewconttaxpay;
	}

	public void setIrenewcustsmall(Integer irenewcustsmall) {
		this.irenewcustsmall = irenewcustsmall;
	}

	public void setIrenewcusttaxpay(Integer irenewcusttaxpay) {
		this.irenewcusttaxpay = irenewcusttaxpay;
	}

	public void setIrenewcontsmall(DZFDouble irenewcontsmall) {
		this.irenewcontsmall = irenewcontsmall;
	}

	public void setIrenewconttaxpay(DZFDouble irenewconttaxpay) {
		this.irenewconttaxpay = irenewconttaxpay;
	}

	public void setInewcustrate(Integer inewcustrate) {
		this.inewcustrate = inewcustrate;
	}

	public void setInewcontrate(Integer inewcontrate) {
		this.inewcontrate = inewcontrate;
	}

	public void setIrenewcustrate(Integer irenewcustrate) {
		this.irenewcustrate = irenewcustrate;
	}

	public void setIrenewcontrate(Integer irenewcontrate) {
		this.irenewcontrate = irenewcontrate;
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
