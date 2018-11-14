package com.dzf.model.channel.stock;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 出库单VO
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class StockOutBVO extends SuperVO {
	
	@FieldAlias("soutbid")
	private String pk_stockout_b;
	
	@FieldAlias("soutid")
	private String pk_stockout;
	
	@FieldAlias("billid_b")
	private String pk_goodsbill_b;//订单来源主键
	
	@FieldAlias("houseid")
	private String pk_warehouse;//仓库
	
	@FieldAlias("gid")
	private String pk_goods;//商品
	
	@FieldAlias("specid")
	private String pk_goodsspec;//规格型号
	
	private String invspec;//规格
	
	private String invtype;//型号
	
	private DZFDouble nprice;//销售价
	
	private Integer nnum;//数量
	
	private DZFDouble nmny;//金额
	
	@FieldAlias("corpid")
	private String pk_corp;
	
	private String fathercorp;
	
    @FieldAlias("dr")
    private Integer dr;// 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts;// 时间戳
    
    ///////////////////查询用的///////////
    
	@FieldAlias("vcode")
	private String vbillcode;//订单编号
	
	@FieldAlias("gname")
	private String vgoodsname; // 商品名称

    public String getPk_stockout_b() {
		return pk_stockout_b;
	}

	public void setPk_stockout_b(String pk_stockout_b) {
		this.pk_stockout_b = pk_stockout_b;
	}

	public String getPk_stockout() {
		return pk_stockout;
	}

	public void setPk_stockout(String pk_stockout) {
		this.pk_stockout = pk_stockout;
	}

	public String getPk_warehouse() {
		return pk_warehouse;
	}

	public void setPk_warehouse(String pk_warehouse) {
		this.pk_warehouse = pk_warehouse;
	}

	public String getPk_goodsbill_b() {
		return pk_goodsbill_b;
	}

	public String getVgoodsname() {
		return vgoodsname;
	}

	public void setVgoodsname(String vgoodsname) {
		this.vgoodsname = vgoodsname;
	}

	public void setPk_goodsbill_b(String pk_goodsbill_b) {
		this.pk_goodsbill_b = pk_goodsbill_b;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
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

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getFathercorp() {
		return fathercorp;
	}

	public void setFathercorp(String fathercorp) {
		this.fathercorp = fathercorp;
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

	public DZFDouble getNprice() {
		return nprice;
	}

	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
	}

	public Integer getNnum() {
		return nnum;
	}

	public void setNnum(Integer nnum) {
		this.nnum = nnum;
	}

	public DZFDouble getNmny() {
		return nmny;
	}

	public void setNmny(DZFDouble nmny) {
		this.nmny = nmny;
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

	@Override
    public String getPKFieldName() {
        return "pk_stockout_b";
    }

    @Override
    public String getParentPKFieldName() {
        return "pk_stockout";
    }

    @Override
    public String getTableName() {
        return "cn_stockout_b";
    }

}
