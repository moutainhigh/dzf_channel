package com.dzf.model.channel.stock;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 商品数量明细表VO
 * 
 *
 */
@SuppressWarnings("rawtypes")
public class GoodsNumVO extends SuperVO {
    
    private String pk_goodstype;
    
    private String vname;//商品分类
    
    @FieldAlias("gcode")
    private String vgoodscode;//商品编码

    @FieldAlias("gname")
    private String vgoodsname;//商品名称
    
    @FieldAlias("spec")
    private String invspec;//规格
    
    @FieldAlias("type")
    private String invtype;//型号
    
    @FieldAlias("unit")
    private String goodsunit;//单位
    
    @FieldAlias("price")
    private String goodsprice;//单价

    @FieldAlias("stock")
    private Integer istocknum;//库存数量
    
    @FieldAlias("stockin")
    private Integer istockinnum;//累计入库数量
    
    @FieldAlias("stockmny")
    private Integer isstockmny;//累计入库成本
    
    @FieldAlias("outnum")
    private Integer ioutnum;//累计出库数量
    
    @FieldAlias("nosendnum")
    private Integer nsendnum;//待发货数量
    
    @FieldAlias("nooutnum")
    private Integer noutnum;//待出库数量
    
    @FieldAlias("lock")
    private Integer ilocknum;//锁定数量
    
    @FieldAlias("use")
    private Integer iusenum;//可用数量
    
    @FieldAlias("buy")
    private Integer ibuynum;//可购买数量
    
    @FieldAlias("nowdate")
    private Integer nowdate;//当前日期

    public Integer getNoutnum() {
		return noutnum;
	}

	public void setNoutnum(Integer noutnum) {
		this.noutnum = noutnum;
	}

	public Integer getIstockinnum() {
        return istockinnum;
    }

    public void setIstockinnum(Integer istockinnum) {
        this.istockinnum = istockinnum;
    }

    public Integer getNowdate() {
        return nowdate;
    }

    public void setNowdate(Integer nowdate) {
        this.nowdate = nowdate;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getVgoodscode() {
        return vgoodscode;
    }

    public void setVgoodscode(String vgoodscode) {
        this.vgoodscode = vgoodscode;
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

    public String getPk_goodstype() {
        return pk_goodstype;
    }

    public void setPk_goodstype(String pk_goodstype) {
        this.pk_goodstype = pk_goodstype;
    }

    public String getInvtype() {
        return invtype;
    }

    public void setInvtype(String invtype) {
        this.invtype = invtype;
    }

    public Integer getIstocknum() {
        return istocknum;
    }

    public void setIstocknum(Integer istocknum) {
        this.istocknum = istocknum;
    }

    public Integer getIlocknum() {
        return ilocknum;
    }

    public void setIlocknum(Integer ilocknum) {
        this.ilocknum = ilocknum;
    }

    public Integer getIusenum() {
        return iusenum;
    }

    public void setIusenum(Integer iusenum) {
        this.iusenum = iusenum;
    }
    
    public String getGoodsunit() {
        return goodsunit;
    }

    public void setGoodsunit(String goodsunit) {
        this.goodsunit = goodsunit;
    }

    public Integer getIsstockmny() {
        return isstockmny;
    }

    public void setIsstockmny(Integer isstockmny) {
        this.isstockmny = isstockmny;
    }

    public Integer getIoutnum() {
        return ioutnum;
    }

    public void setIoutnum(Integer ioutnum) {
        this.ioutnum = ioutnum;
    }

    public Integer getNsendnum() {
        return nsendnum;
    }

    public void setNsendnum(Integer nsendnum) {
        this.nsendnum = nsendnum;
    }

    public Integer getIbuynum() {
        return ibuynum;
    }

    public void setIbuynum(Integer ibuynum) {
        this.ibuynum = ibuynum;
    }
    
    public String getGoodsprice() {
        return goodsprice;
    }

    public void setGoodsprice(String goodsprice) {
        this.goodsprice = goodsprice;
    }

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return null;
    }

}
