package com.dzf.model.channel.stock;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 库存量VO
 * 
 * @author gejw
 * @time 2018年11月9日 下午12:00:02
 *
 */
public class StockNumVO extends SuperVO {

    private String pk_stocknum;
    
    private String pk_corp;
    
    private String pk_warehouse;
    
    private String pk_goods;
    
    private String pk_goodsspec;//规格型号ID
    
    private Integer istocknum;//库存量
    
    private Integer ioutnum;//已出数量
    
    private Integer isellnum;//已卖数量
    
    private String coperatorid;
    
    private DZFDate doperatedate;

    @FieldAlias("dr")
    private Integer dr;// 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts;// 时间戳

    public String getPk_goodsspec() {
		return pk_goodsspec;
	}

	public void setPk_goodsspec(String pk_goodsspec) {
		this.pk_goodsspec = pk_goodsspec;
	}

	public String getPk_stocknum() {
        return pk_stocknum;
    }

    public void setPk_stocknum(String pk_stocknum) {
        this.pk_stocknum = pk_stocknum;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getPk_warehouse() {
        return pk_warehouse;
    }

    public void setPk_warehouse(String pk_warehouse) {
        this.pk_warehouse = pk_warehouse;
    }

    public String getPk_goods() {
        return pk_goods;
    }

    public void setPk_goods(String pk_goods) {
        this.pk_goods = pk_goods;
    }

    public Integer getIstocknum() {
        return istocknum;
    }

    public void setIstocknum(Integer istocknum) {
        this.istocknum = istocknum;
    }

    public Integer getIoutnum() {
        return ioutnum;
    }

    public void setIoutnum(Integer ioutnum) {
        this.ioutnum = ioutnum;
    }

    public Integer getIsellnum() {
        return isellnum;
    }

    public void setIsellnum(Integer isellnum) {
        this.isellnum = isellnum;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
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
        return "pk_stocknum";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "cn_stocknum";
    }

}
