package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 出入库明细表VO
 * 出入库汇总表VO
 * @author yy
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
	
	@FieldAlias("pk_goodsspec")
	private String pk_goodsspec;// 规格型号id

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
	
	@FieldAlias("numin")
	private Integer nnumin;// 入库数量
	
	@FieldAlias("pricein")
	private DZFDouble npricein;// 入库成本价
	
	@FieldAlias("moneyin")
	private  DZFDouble totalmoneyin;// 入库金额
	
	@FieldAlias("numout")
	private Integer nnumout;// 出库数量
	
	@FieldAlias("priceout")
	private DZFDouble npriceout;// 出库成本价
	
	@FieldAlias("moneyout")
	private  DZFDouble totalmoneyout;// 出库金额
	
	@FieldAlias("numb")
	private  Integer balanceNum;// 结存数量
	
	@FieldAlias("priceb")
	private  DZFDouble balancePrice;// 结存成本价
	
	@FieldAlias("moneyb")
	private  DZFDouble totalmoneyb;// 结存金额
	
	
	@FieldAlias("numstart")
	private  Integer nnumstart;// 期初数量
	
	@FieldAlias("pricestart")
	private  DZFDouble npricestart;// 期初单价
	
	@FieldAlias("moneystart")
	private  DZFDouble totalmoneys;// 期初金额
	
	@FieldAlias("numend")
	private  Integer nnumend;// 期末数量
	
	@FieldAlias("priceend")
	private  DZFDouble npriceend;//期末单价
	
	@FieldAlias("moneyend")
	private  DZFDouble totalmoneye;// 期末金额
	
	
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

	public DZFDouble getBalancePrice() {
		return balancePrice;
	}

	public Integer getNnumstart() {
		return nnumstart;
	}

	public void setNnumstart(Integer nnumstart) {
		this.nnumstart = nnumstart;
	}

	public DZFDouble getNpricestart() {
		return npricestart;
	}

	public void setNpricestart(DZFDouble npricestart) {
		this.npricestart = npricestart;
	}

	public DZFDouble getTotalmoneys() {
		return totalmoneys;
	}

	public void setTotalmoneys(DZFDouble totalmoneys) {
		this.totalmoneys = totalmoneys;
	}

	public Integer getNnumend() {
		return nnumend;
	}

	public void setNnumend(Integer nnumend) {
		this.nnumend = nnumend;
	}

	public DZFDouble getNpriceend() {
		return npriceend;
	}

	public void setNpriceend(DZFDouble npriceend) {
		this.npriceend = npriceend;
	}

	public DZFDouble getTotalmoneye() {
		return totalmoneye;
	}

	public void setTotalmoneye(DZFDouble totalmoneye) {
		this.totalmoneye = totalmoneye;
	}

	public void setBalancePrice(DZFDouble balancePrice) {
		this.balancePrice = balancePrice;
	}

	public DZFDouble getTotalmoneyb() {
		return totalmoneyb;
	}

	public void setTotalmoneyb(DZFDouble totalmoneyb) {
		this.totalmoneyb = totalmoneyb;
	}

	public DZFDouble getNpricein() {
		return npricein;
	}

	public void setNpricein(DZFDouble npricein) {
		this.npricein = npricein;
	}

	public DZFDouble getTotalmoneyin() {
		return totalmoneyin;
	}

	public void setTotalmoneyin(DZFDouble totalmoneyin) {
		this.totalmoneyin = totalmoneyin;
	}

	public DZFDouble getNpriceout() {
		return npriceout;
	}

	public void setNpriceout(DZFDouble npriceout) {
		this.npriceout = npriceout;
	}

	public DZFDouble getTotalmoneyout() {
		return totalmoneyout;
	}

	public void setTotalmoneyout(DZFDouble totalmoneyout) {
		this.totalmoneyout = totalmoneyout;
	}

	public String getPk_goodsspec() {
		return pk_goodsspec;
	}

	public void setPk_goodsspec(String pk_goodsspec) {
		this.pk_goodsspec = pk_goodsspec;
	}
	
	

	
	
}
