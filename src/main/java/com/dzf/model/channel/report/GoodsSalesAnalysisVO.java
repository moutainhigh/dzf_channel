package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 加盟商商品销售分析VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class GoodsSalesAnalysisVO extends SuperVO {

	private static final long serialVersionUID = 1L;
	
	@FieldAlias("corpid")
    private String pk_corp; // 所属公司
	
	@FieldAlias("gid")
	private String pk_goods;//商品主键
	
	@FieldAlias("specid")
	private String pk_goodsspec;//规格型号
	
	@FieldAlias("pname")
    private String corpname; // 公司名称
	
	@FieldAlias("gname")
	private String vgoodsname; // 商品名称
	
	@FieldAlias("spec")
	private String invspec;//规格
	
	@FieldAlias("type")
	private String invtype;//型号
	
	@FieldAlias("amount")
	private Integer amount; // 数量
	
	@FieldAlias("cost")
	private DZFDouble ncost; //成本价
	
	@FieldAlias("totalcost")
	private DZFDouble ntotalcost;//成本合计
	
	@FieldAlias("price")
	private DZFDouble nprice; // 售价

	@FieldAlias("ndemny")
	private DZFDouble ndeductmny;// 预付款

	@FieldAlias("nderebmny")
	private DZFDouble ndedrebamny;// 返点款
	
	@FieldAlias("ndesummny")
	private DZFDouble ndedsummny;// 扣款总金额

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_goods() {
		return pk_goods;
	}

	public void setPk_goods(String pk_goods) {
		this.pk_goods = pk_goods;
	}

	public String getPk_goodsspec() {
		return pk_goodsspec;
	}

	public void setPk_goodsspec(String pk_goodsspec) {
		this.pk_goodsspec = pk_goodsspec;
	}

	public DZFDouble getNprice() {
		return nprice;
	}

	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getVgoodsname() {
		return vgoodsname;
	}

	public void setVgoodsname(String vgoodsname) {
		this.vgoodsname = vgoodsname;
	}

	public String getInvspec() {
		return invspec;
	}

	public void setInvspec(String invspec) {
		this.invspec = invspec;
	}

	public String getInvtype() {
		return invtype;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public DZFDouble getNcost() {
		return ncost;
	}

	public void setNcost(DZFDouble ncost) {
		this.ncost = ncost;
	}

	public DZFDouble getNtotalcost() {
		return ntotalcost;
	}

	public void setNtotalcost(DZFDouble ntotalcost) {
		this.ntotalcost = ntotalcost;
	}

	public DZFDouble getNdeductmny() {
		return ndeductmny;
	}

	public void setNdeductmny(DZFDouble ndeductmny) {
		this.ndeductmny = ndeductmny;
	}

	public DZFDouble getNdedrebamny() {
		return ndedrebamny;
	}

	public void setNdedrebamny(DZFDouble ndedrebamny) {
		this.ndedrebamny = ndedrebamny;
	}

	public DZFDouble getNdedsummny() {
		return ndedsummny;
	}

	public void setNdedsummny(DZFDouble ndedsummny) {
		this.ndedsummny = ndedsummny;
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
