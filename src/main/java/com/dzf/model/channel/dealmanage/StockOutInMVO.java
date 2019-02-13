package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 出入库明细表VO
 * 
 * @author yy
 */

/**
 * @author admin
 *
 */
@SuppressWarnings("rawtypes")
public class StockOutInMVO extends SuperVO {

	@FieldAlias("sid")
	private String pk_stockoutin;

	@FieldAlias("vcode")
	private String vbillcode;// 出入库单号

	@FieldAlias("gname")
	private String vgoodsname;// 商品名称
	
	@FieldAlias("gid")
	private String pk_goods;// 商品名称id

	@FieldAlias("gcode")
	private String vgoodscode;// 商品编码

	@FieldAlias("spec")
	private String invspec;//规格
	
	@FieldAlias("type")
	private String invtype;//型号

	@FieldAlias("itype")
	private Integer vitype;// 业务类型

	@FieldAlias("contime")
	private DZFDateTime dconfirmtime;// 确认出入库时间

	@FieldAlias("begdate")
	private DZFDate begdate;// 开始日期

	@FieldAlias("enddate")
	private DZFDate enddate;// 结束日期
	
	@FieldAlias("numb")
	private  Integer balanceNum;// 结存数量
	
	@FieldAlias("priceb")
	private  Double balancePrice;// 结存成本价
	
	@FieldAlias("moneyb")
	private  Double totalmoneyb;// 结存金额
	
	@FieldAlias("numin")
	private Integer nnumin;// 入库数量
	
	@FieldAlias("pricein")
	private Double npricein;// 入库成本价
	
	@FieldAlias("moneyin")
	private  Double totalmoneyin;// 入库金额
	
	@FieldAlias("numout")
	private Integer nnumout;// 出库数量
	
	@FieldAlias("priceout")
	private Double npriceout;// 出库成本价
	
	@FieldAlias("moneyout")
	private  Double totalmoneyout;// 出库金额
	
	/**
	 * 只作为前台显示属性
	 */
	@FieldAlias("balance")
	private  Integer balance;// 结存
	
	@FieldAlias("instock")
	private  Integer instock;// 入库
	
	@FieldAlias("outstock")
	private  Integer outstock;// 出库
	
	
	
	
	public String getPk_goods() {
		return pk_goods;
	}

	public void setPk_goods(String pk_goods) {
		this.pk_goods = pk_goods;
	}

	public DZFDate getBegdate() {
		return begdate;
	}

	public void setBegdate(DZFDate begdate) {
		this.begdate = begdate;
	}

	public DZFDate getEnddate() {
		return enddate;
	}

	public void setEnddate(DZFDate enddate) {
		this.enddate = enddate;
	}

	public String getPk_stockoutin() {
		return pk_stockoutin;
	}

	public void setPk_stockoutin(String pk_stockoutin) {
		this.pk_stockoutin = pk_stockoutin;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public String getVgoodscode() {
		return vgoodscode;
	}

	public void setVgoodscode(String vgoodscode) {
		this.vgoodscode = vgoodscode;
	}

	public void setVitype(Integer vitype) {
		this.vitype = vitype;
	}
	
	public Integer getVitype() {
		return vitype;
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

	public DZFDateTime getDconfirmtime() {
		return dconfirmtime;
	}

	public void setDconfirmtime(DZFDateTime dconfirmtime) {
		this.dconfirmtime = dconfirmtime;
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

	public Integer getBalanceNum() {
		return balanceNum;
	}

	public void setBalanceNum(Integer balanceNum) {
		this.balanceNum = balanceNum;
	}

	
	public Integer getNnumin() {
		return nnumin;
	}

	public void setNnumin(Integer nnumin) {
		this.nnumin = nnumin;
	}

	

	public Integer getNnumout() {
		return nnumout;
	}

	public void setNnumout(Integer nnumout) {
		this.nnumout = nnumout;
	}

	

	public Integer getBalance() {
		return balance;
	}

	public void setBalance(Integer balance) {
		this.balance = balance;
	}

	public Integer getInstock() {
		return instock;
	}

	public void setInstock(Integer instock) {
		this.instock = instock;
	}

	public Integer getOutstock() {
		return outstock;
	}

	public void setOutstock(Integer outstock) {
		this.outstock = outstock;
	}

	public Double getBalancePrice() {
		return balancePrice;
	}

	public void setBalancePrice(Double balancePrice) {
		this.balancePrice = balancePrice;
	}

	public Double getTotalmoneyb() {
		return totalmoneyb;
	}

	public void setTotalmoneyb(Double totalmoneyb) {
		this.totalmoneyb = totalmoneyb;
	}

	public Double getNpricein() {
		return npricein;
	}

	public void setNpricein(Double npricein) {
		this.npricein = npricein;
	}

	public Double getTotalmoneyin() {
		return totalmoneyin;
	}

	public void setTotalmoneyin(Double totalmoneyin) {
		this.totalmoneyin = totalmoneyin;
	}

	public Double getNpriceout() {
		return npriceout;
	}

	public void setNpriceout(Double npriceout) {
		this.npriceout = npriceout;
	}

	public Double getTotalmoneyout() {
		return totalmoneyout;
	}

	public void setTotalmoneyout(Double totalmoneyout) {
		this.totalmoneyout = totalmoneyout;
	}

	
	
}
