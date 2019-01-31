package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 入库子表VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class StockInBVO extends SuperVO {
	
	private static final long serialVersionUID = -3393610427773366550L;

	@FieldAlias("stbid")
	private String pk_stockin_b;//主键
	
	@FieldAlias("stid")
	private String pk_stockin;//主表主键
	
	@FieldAlias("corpid")
	private String pk_corp;//登录公司主键
	
	@FieldAlias("supid")
	private String pk_supplier;//供应商主键
	
	@FieldAlias("wareid")
	private String pk_warehouse;//仓库主键
	
	@FieldAlias("gid")
	private String pk_goods;//商品主键
	
	@FieldAlias("specid")
	private String pk_goodsspec;//规格型号主键  
	
	@FieldAlias("spec")
	private String invspec;//规格
	
	@FieldAlias("type")
	private String invtype;//型号
	
	@FieldAlias("price")
	private DZFDouble nprice;//成本价
	
	@FieldAlias("num")
	private Integer nnum;//数量
	
	@FieldAlias("mny")
	private DZFDouble nmny;//金额
	
	@FieldAlias("memo")
	private String vmemo;//备注
	
	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间
	
	@FieldAlias("itype")
	private Integer iinvoicetype;//发票类型  1：增值税专用发票；2：增值税普通发票；
	
	@FieldAlias("uprice")
	private DZFDouble nunitprice;//单价
	
	@FieldAlias("taxrate")
	private Integer itaxrate;//税率
	
	@FieldAlias("taxamount")
	private DZFDouble ntaxamount;//税额
	
	@FieldAlias("pricetax")
	private DZFDouble npricetax;//价税合计
	
	@FieldAlias("tmny")
	private DZFDouble ntotalmny;//采购金额
	
	@FieldAlias("tcost")
	private DZFDouble ntotalcost;//成本金额
	
	//仅作数据传递和展示使用begin*****************
	
	@FieldAlias("gname")
	private String vgoodsname;//商品名称
	
	@FieldAlias("supname")
	private String vsuppliername;//供应商名称
	
	@FieldAlias("tstp")
	private DZFDateTime tstamp;//商品最新时间戳
	
	//仅作数据传递和展示使用end*******************
	
	public DZFDouble getNpricetax() {
		return npricetax;
	}

	public void setNpricetax(DZFDouble npricetax) {
		this.npricetax = npricetax;
	}

	public DZFDouble getNtotalcost() {
		return ntotalcost;
	}

	public void setNtotalcost(DZFDouble ntotalcost) {
		this.ntotalcost = ntotalcost;
	}

	public Integer getIinvoicetype() {
		return iinvoicetype;
	}

	public void setIinvoicetype(Integer iinvoicetype) {
		this.iinvoicetype = iinvoicetype;
	}

	public DZFDouble getNunitprice() {
		return nunitprice;
	}

	public void setNunitprice(DZFDouble nunitprice) {
		this.nunitprice = nunitprice;
	}

	public Integer getItaxrate() {
		return itaxrate;
	}

	public void setItaxrate(Integer itaxrate) {
		this.itaxrate = itaxrate;
	}

	public DZFDouble getNtaxamount() {
		return ntaxamount;
	}

	public void setNtaxamount(DZFDouble ntaxamount) {
		this.ntaxamount = ntaxamount;
	}

	public DZFDouble getNtotalmny() {
		return ntotalmny;
	}

	public void setNtotalmny(DZFDouble ntotalmny) {
		this.ntotalmny = ntotalmny;
	}

	public DZFDateTime getTstamp() {
		return tstamp;
	}

	public void setTstamp(DZFDateTime tstamp) {
		this.tstamp = tstamp;
	}

	public String getVsuppliername() {
		return vsuppliername;
	}

	public void setVsuppliername(String vsuppliername) {
		this.vsuppliername = vsuppliername;
	}

	public String getPk_goodsspec() {
		return pk_goodsspec;
	}

	public void setPk_goodsspec(String pk_goodsspec) {
		this.pk_goodsspec = pk_goodsspec;
	}

	public String getVgoodsname() {
		return vgoodsname;
	}

	public void setVgoodsname(String vgoodsname) {
		this.vgoodsname = vgoodsname;
	}

	public String getPk_stockin_b() {
		return pk_stockin_b;
	}

	public void setPk_stockin_b(String pk_stockin_b) {
		this.pk_stockin_b = pk_stockin_b;
	}

	public String getPk_stockin() {
		return pk_stockin;
	}

	public void setPk_stockin(String pk_stockin) {
		this.pk_stockin = pk_stockin;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_supplier() {
		return pk_supplier;
	}

	public void setPk_supplier(String pk_supplier) {
		this.pk_supplier = pk_supplier;
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

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
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
		return "pk_stockin_b";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_stockin";
	}

	@Override
	public String getTableName() {
		return "cn_stockin_b";
	}

}
