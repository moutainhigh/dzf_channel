package com.dzf.model.channel.matmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 物料入库VO
 */
public class MaterielStockInVO extends SuperVO {

	@FieldAlias("matinid")
	private String pk_materielin;//主键
	
	@FieldAlias("corpid")
	private String pk_corp;//公司主键
	
	@FieldAlias("matfileid")
	private String pk_materiel;//物料档案主键
	
	@FieldAlias("code")
	private String vbillcode;//单据编码
	
	@FieldAlias("indate")
	private String stockdate;//入库日期
	
	@FieldAlias("tmny")
	private DZFDouble ntotalmny;//总金额
	
	@FieldAlias("wlname")
	private String vname;//物料名称
	
	@FieldAlias("unit")
	private String vunit;//单位
	
	@FieldAlias("cost")
	private DZFDouble ncost;//成本价
	
	@FieldAlias("num")
	private Integer nnum;//入库数量
	
	@FieldAlias("memo")
	private String vmemo;//备注
	
	@FieldAlias("operid")
	private String coperatorid;//录入人
	
	@FieldAlias("opertime")
	private DZFDateTime doperatetime;//录入时间
	
	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间
	
	/*******仅作展示，不存库*******/
	
	@FieldAlias("begdate")
	private String begindate;//录入开始日期
	
	private String enddate;//录入结束日期
	
	private String opername;//录入人名称
	
	private Integer count;//入库单的条数
	
	@FieldAlias("code")
	private String vcode;//物料编号
	
	/*******仅作展示，不存库*******/
	
	public String getPk_materielin() {
		return pk_materielin;
	}

	public void setPk_materielin(String pk_materielin) {
		this.pk_materielin = pk_materielin;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public String getStockdate() {
		return stockdate;
	}

	public void setStockdate(String stockdate) {
		this.stockdate = stockdate;
	}

	public DZFDouble getNtotalmny() {
		return ntotalmny;
	}

	public void setNtotalmny(DZFDouble ntotalmny) {
		this.ntotalmny = ntotalmny;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
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

	public Integer getNnum() {
		return nnum;
	}

	public void setNnum(Integer nnum) {
		this.nnum = nnum;
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
	
	public String getPk_materiel() {
		return pk_materiel;
	}

	public void setPk_materiel(String pk_materiel) {
		this.pk_materiel = pk_materiel;
	}

	public String getOpername() {
		return opername;
	}

	public void setOpername(String opername) {
		this.opername = opername;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}
	
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String getPKFieldName() {
		return "pk_materielin";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_materielin";
	}

}
