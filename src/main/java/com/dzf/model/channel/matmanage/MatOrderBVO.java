package com.dzf.model.channel.matmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 物料订单子表VO
 *
 */
public class MatOrderBVO extends SuperVO {

	@FieldAlias("matbillid_b")
	private String pk_materielbill_b;//主键

	@FieldAlias("matbillid")
	private String pk_materielbill;//主表主键
	
	@FieldAlias("matfileid")
	private String pk_materiel;//物料档案主键
	
	@FieldAlias("wlname")
	private String vname;//物料名称
	
	@FieldAlias("unit")
	private String vunit;//单位
	
	private Integer outnum;//发货数量
	
	private Integer applynum;//申请数量
	
	private Integer succnum;//审核通过数量
	
    private Integer sumapply;//上季度申请总数量
	
	private Integer sumsucc;//上季度申请通过总数量
	
	
	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间
	
	public String getPk_materielbill_b() {
		return pk_materielbill_b;
	}

	public void setPk_materielbill_b(String pk_materielbill_b) {
		this.pk_materielbill_b = pk_materielbill_b;
	}

	public String getPk_materielbill() {
		return pk_materielbill;
	}

	public void setPk_materielbill(String pk_materielbill) {
		this.pk_materielbill = pk_materielbill;
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
	
	public Integer getSuccnum() {
		return succnum;
	}

	public void setSuccnum(Integer succnum) {
		this.succnum = succnum;
	}
	
	public Integer getSumapply() {
		return sumapply;
	}

	public void setSumapply(Integer sumapply) {
		this.sumapply = sumapply;
	}

	public Integer getSumsucc() {
		return sumsucc;
	}

	public void setSumsucc(Integer sumsucc) {
		this.sumsucc = sumsucc;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_materielbill_b";
	}

	@Override
	public String getTableName() {
		return "cn_materielbill_b";
	}

}
