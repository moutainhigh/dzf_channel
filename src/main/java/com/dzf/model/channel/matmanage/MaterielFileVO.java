package com.dzf.model.channel.matmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 物料档案VO
 */
public class MaterielFileVO extends SuperVO {

	@FieldAlias("matfileid")
	private String pk_materiel;//主键
	
	@FieldAlias("corpid")
	private String pk_corp;//公司主键
	
	@FieldAlias("code")
	private String vcode;//物料编码
	
	@FieldAlias("wlname")
	private String vname;//物料名称
	
	@FieldAlias("unit")
	private String vunit;//单位
	
	@FieldAlias("cost")
	private DZFDouble ncost;//成本价
	
	@FieldAlias("apply")
	private Integer isappl;//是否申请  0 ：否  1 ：是
	
	@FieldAlias("sseal")
	private Integer isseal;//是否封存  0-全部  1-启用  2-封存
	
	private Integer intnum;//入库数量
	
	private Integer outnum;//出库数量（包括发货数量+删除的入库数量）
	
	@FieldAlias("operid")
	private String coperatorid;//录入人
	
	@FieldAlias("opertime")
	private DZFDateTime doperatetime;//录入时间
	
	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间
	
	public static final String ISSEAL = "isseal";
	
	public static final Integer ISSEAL_1 = 1;//启用
	
	public static final Integer ISSEAL_2 = 2;//封存
	
	/********不存库**********/
	
	private String applyreq;//申请条件
	
	private String applyname;//申请人
	
	@FieldAlias("begdate")
	private String begindate;//录入开始日期
	
	private String enddate;//录入结束日期
	
	private Integer applynum;//申请数量
	
	private Integer stocknum;//库存数量
	
	/********不存库**********/
	
	public String getPk_materiel() {
		return pk_materiel;
	}

	public void setPk_materiel(String pk_materiel) {
		this.pk_materiel = pk_materiel;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public String getVunit() {
		return vunit;
	}

	public void setVunit(String vunit) {
		this.vunit = vunit;
	}

	public DZFDouble getNcost() {
		return ncost;
	}

	public void setNcost(DZFDouble ncost) {
		this.ncost = ncost;
	}

	public Integer getIsappl() {
		return isappl;
	}

	public void setIsappl(Integer isappl) {
		this.isappl = isappl;
	}

	public Integer getIsseal() {
		return isseal;
	}

	public void setIsseal(Integer isseal) {
		this.isseal = isseal;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDateTime getDoperatetime() {
		return doperatetime;
	}

	public void setDoperatetime(DZFDateTime doperatetime) {
		this.doperatetime = doperatetime;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	
	public String getApplyreq() {
		return applyreq;
	}

	public void setApplyreq(String applyreq) {
		this.applyreq = applyreq;
	}
	
	public String getBegindate() {
		return begindate;
	}

	public void setBegindate(String begindate) {
		this.begindate = begindate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	
	public Integer getIntnum() {
		return intnum;
	}

	public void setIntnum(Integer intnum) {
		this.intnum = intnum;
	}

	public Integer getOutnum() {
		return outnum;
	}

	public void setOutnum(Integer outnum) {
		this.outnum = outnum;
	}

	public Integer getApplynum() {
		return applynum;
	}

	public void setApplynum(Integer applynum) {
		this.applynum = applynum;
	}

	public String getApplyname() {
		return applyname;
	}

	public void setApplyname(String applyname) {
		this.applyname = applyname;
	}
	
	public Integer getStocknum() {
		return stocknum;
	}

	public void setStocknum(Integer stocknum) {
		this.stocknum = stocknum;
	}

	@Override
	public String getPKFieldName() {
		return "pk_materiel";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_materiel";
	}

}
