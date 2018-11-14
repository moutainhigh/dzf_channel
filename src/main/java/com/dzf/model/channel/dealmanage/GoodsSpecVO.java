package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 商品规格型号
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class GoodsSpecVO extends SuperVO {

	private static final long serialVersionUID = 934949279471144652L;
	
	@FieldAlias("specid")
	private String pk_goodsspec;//主键  
	
	@FieldAlias("gid")
	private String pk_goods;//主键 
	
	@FieldAlias("corpid")
	private String pk_corp;//主键
	
	@FieldAlias("spec")
	private String invspec;//规格
	
	@FieldAlias("type")
	private String invtype;//型号
	
	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间

	public String getPk_goodsspec() {
		return pk_goodsspec;
	}

	public void setPk_goodsspec(String pk_goodsspec) {
		this.pk_goodsspec = pk_goodsspec;
	}

	public String getPk_goods() {
		return pk_goods;
	}

	public void setPk_goods(String pk_goods) {
		this.pk_goods = pk_goods;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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
		return "pk_goodsspec";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_goods";
	}

	@Override
	public String getTableName() {
		return "cn_goodsspec";
	}

}
