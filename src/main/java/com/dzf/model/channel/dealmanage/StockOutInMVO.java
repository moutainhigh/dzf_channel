package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 出入库明细表VO
 * 
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

	@FieldAlias("gcode")
	private String vgoodscode;// 商品编码

	@FieldAlias("spec")
	private String invspec;//规格
	
	@FieldAlias("type")
	private String invtype;//型号

	@FieldAlias("itype")
	private Integer vitype;// 类型

	@FieldAlias("nprice")
	private DZFDouble nprice;// 成本价

	@FieldAlias("num")
	private Integer nnum;// 数量

	@FieldAlias("nmny")
	private DZFDouble totalmny;// 金额

	@FieldAlias("conid")
	private String vconfirmid;// 确认出入库人id

	@FieldAlias("conname")
	private String vconfirmname;// 确认出入库人姓名
	
	@FieldAlias("contime")
	private DZFDateTime dconfirmtime;// 确认出入库时间

	@FieldAlias("ctname")
	private String coperatname;// 录入人名称
	
	@FieldAlias("begdate")
	private DZFDate begdate;// 开始日期

	@FieldAlias("enddate")
	private DZFDate enddate;// 结束日期
	
	public String getPk_goods() {
		return pk_goods;
	}

	public void setPk_goods(String pk_goods) {
		this.pk_goods = pk_goods;
	}

	public String getVconfirmname() {
		return vconfirmname;
	}

	public void setVconfirmname(String vconfirmname) {
		this.vconfirmname = vconfirmname;
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

	public DZFDouble getTotalmny() {
		return totalmny;
	}

	public void setTotalmny(DZFDouble totalmny) {
		this.totalmny = totalmny;
	}

	public String getVconfirmid() {
		return vconfirmid;
	}

	public void setVconfirmid(String vconfirmid) {
		this.vconfirmid = vconfirmid;
	}

	public DZFDateTime getDconfirmtime() {
		return dconfirmtime;
	}

	public void setDconfirmtime(DZFDateTime dconfirmtime) {
		this.dconfirmtime = dconfirmtime;
	}
	
	public String getCoperatname() {
		return coperatname;
	}

	public void setCoperatname(String coperatname) {
		this.coperatname = coperatname;
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
