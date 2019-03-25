package com.dzf.model.channel.dealmanage;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 商品成本VO(按月)
 *
 */
@SuppressWarnings("rawtypes")
public class GoodsCostVO extends SuperVO {
    
	@FieldAlias("costid")
	private String pk_goodscost;//主键 
	
	@FieldAlias("gid")
	private String pk_goods;//商品主键 
	
	@FieldAlias("specid")
	private String pk_goodsspec;//规格型号主键  
	
	private String period;//期间
	
	@FieldAlias("cost")
	private DZFDouble ncost;//成本价
	
	@FieldAlias("totalcost")
	private DZFDouble ntotalcost;//总成本
	
	@FieldAlias("num")
	private Integer nnum;//数量
	
	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间
	
	public String getPk_goodscost() {
		return pk_goodscost;
	}

	public void setPk_goodscost(String pk_goodscost) {
		this.pk_goodscost = pk_goodscost;
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

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
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

	public Integer getNnum() {
		return nnum;
	}

	public void setNnum(Integer nnum) {
		this.nnum = nnum;
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
		return "pk_goodscost";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_goods";
	}

	@Override
	public String getTableName() {
		return "cn_goodscost";
	}
	
	@Override  
	public boolean equals(Object o) {  
        if (o == this) return true;  
        if (!(o instanceof StockOutInMVO)) {  
            return false;  
        }  
        GoodsCostVO vo = (GoodsCostVO) o;  
        return new EqualsBuilder().append(pk_goods, vo.pk_goods).append(pk_goodsspec, vo.pk_goodsspec).isEquals();  
	 }  
	  
    @Override  
    public int hashCode() {  
        return new HashCodeBuilder(17, 37).append(pk_goods).append(pk_goodsspec).toHashCode();  
    } 

}
