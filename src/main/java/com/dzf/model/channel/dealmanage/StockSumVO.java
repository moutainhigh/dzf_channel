package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 出入库汇总表VO
 * 
 * @author yy
 */

/**
 * @author admin
 *
 */
@SuppressWarnings("rawtypes")
public class StockSumVO extends SuperVO {

	@FieldAlias("sid")
	private String pk_stocksum;

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

	@FieldAlias("begdate")
	private DZFDate begdate;// 开始日期

	@FieldAlias("enddate")
	private DZFDate enddate;// 结束日期
	
	@FieldAlias("numstart")
	private  Integer nnumstart;// 期初数量
	
	@FieldAlias("pricestart")
	private  DZFDouble npricestart;// 期初单价
	
	@FieldAlias("moneystart")
	private  DZFDouble totalmoneys;// 期初金额
	
	@FieldAlias("numin")
	private Integer nnumin;// 入库数量
	
	@FieldAlias("moneyin")
	private  DZFDouble totalmoneyin;// 入库金额
	
	@FieldAlias("numout")
	private Integer nnumout;// 出库数量
	
	@FieldAlias("moneyout")
	private  DZFDouble totalmoneyout;// 出库金额
	
	@FieldAlias("numend")
	private  Integer nnumend;// 期末数量
	
	@FieldAlias("priceend")
	private  DZFDouble npriceend;//期末单价
	
	@FieldAlias("moneyend")
	private  DZFDouble totalmoneye;// 期末金额
	
	/**
	 * 只作为前台显示属性
	 */
	@FieldAlias("balancestart")
	private  Integer balancestart;// 期初余额
	
	@FieldAlias("instock")
	private  Integer instock;// 本期入库
	
	@FieldAlias("outstock")
	private  Integer outstock;// 本期出库
	
	@FieldAlias("balanceend")
	private  Integer balanceend;// 期末余额

	public String getPk_stocksum() {
		return pk_stocksum;
	}

	public void setPk_stocksum(String pk_stocksum) {
		this.pk_stocksum = pk_stocksum;
	}

	public String getVgoodsname() {
		return vgoodsname;
	}

	public void setVgoodsname(String vgoodsname) {
		this.vgoodsname = vgoodsname;
	}

	public String getPk_goods() {
		return pk_goods;
	}

	public void setPk_goods(String pk_goods) {
		this.pk_goods = pk_goods;
	}

	public String getVgoodscode() {
		return vgoodscode;
	}

	public void setVgoodscode(String vgoodscode) {
		this.vgoodscode = vgoodscode;
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

	public Integer getNnumstart() {
		return nnumstart;
	}

	public void setNnumstart(Integer nnumstart) {
		this.nnumstart = nnumstart;
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

	public Integer getNnumend() {
		return nnumend;
	}

	public void setNnumend(Integer nnumend) {
		this.nnumend = nnumend;
	}

	public Integer getBalancestart() {
		return balancestart;
	}

	public void setBalancestart(Integer balancestart) {
		this.balancestart = balancestart;
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

	public Integer getBalanceend() {
		return balanceend;
	}

	public void setBalanceend(Integer balanceend) {
		this.balanceend = balanceend;
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

	public DZFDouble getTotalmoneyin() {
		return totalmoneyin;
	}

	public void setTotalmoneyin(DZFDouble totalmoneyin) {
		this.totalmoneyin = totalmoneyin;
	}

	public DZFDouble getTotalmoneyout() {
		return totalmoneyout;
	}

	public void setTotalmoneyout(DZFDouble totalmoneyout) {
		this.totalmoneyout = totalmoneyout;
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

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
