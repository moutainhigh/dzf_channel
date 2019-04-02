package com.dzf.model.channel.matmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

public class MaterielStockInBVO extends SuperVO {

	@FieldAlias("matinbid")
	private String pk_materielin_b;//主键
	
	@FieldAlias("matinid")
	private String pk_materielin;//主表主键
	
	@FieldAlias("matfileid")
	private String pk_materiel;//物料档案主键
	
	@FieldAlias("wlname")
	private String vname;//物料名称
	
	@FieldAlias("unit")
	private String vunit;//单位
	
	@FieldAlias("cost")
	private DZFDouble ncost;//成本价
	
	@FieldAlias("num")
	private Integer nnum;//入库数量
	
	@FieldAlias("operid")
	private String coperatorid;//录入人
	
	@FieldAlias("opertime")
	private DZFDateTime doperatetime;//录入时间
	
	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间
	
	/******不存库，展示使用***********/
	private String code;//物料编码
	
	public String getPk_materielin_b() {
		return pk_materielin_b;
	}

	public void setPk_materielin_b(String pk_materielin_b) {
		this.pk_materielin_b = pk_materielin_b;
	}

	public String getPk_materielin() {
		return pk_materielin;
	}

	public void setPk_materielin(String pk_materielin) {
		this.pk_materielin = pk_materielin;
	}

	public String getPk_materiel() {
		return pk_materiel;
	}

	public void setPk_materiel(String pk_materiel) {
		this.pk_materiel = pk_materiel;
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
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String getPKFieldName() {
		return "pk_materielin_b";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_materielin_b";
	}

}
