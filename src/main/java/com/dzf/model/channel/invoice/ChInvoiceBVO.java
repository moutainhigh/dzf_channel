package com.dzf.model.channel.invoice;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 订单发票子表
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class ChInvoiceBVO extends SuperVO {

	private static final long serialVersionUID = 1L;
	
	@FieldAlias("bid")
	private String pk_invoice_b;// 子表主键
	
	@FieldAlias("pid")
	private String pk_invoice;// 主表主键
	
	@FieldAlias("bspmc")
	private String bspmc;// 商品名称
	
	@FieldAlias("gg")
	private String invspec;// 规格
	
	@FieldAlias("jldw")
	private String measurename;// 单位
	
	@FieldAlias("bnum")
	private DZFDouble bnum;// 数量
	
	@FieldAlias("bdj")
	private DZFDouble bprice;// 单价（不含税）
	
	@FieldAlias("bje")
	private DZFDouble bhjje;// 金额（不含税）
	
	@FieldAlias("bsl")
	private DZFDouble bspsl;// 税率
	
	@FieldAlias("bse")
	private DZFDouble bspse;// 税额
	
	@FieldAlias("corpid")
	private String pk_corp;//公司主键
	
	@FieldAlias("sourceid")
	private String pk_source;//来源主键（订单子表主键）
	
	private Integer dr;

	private DZFDateTime ts;
	
	//*******************仅作计算使用begin
	
	@FieldAlias("tmny")
	private DZFDouble ntotalmny; //金额（含税）
	
	@FieldAlias("cmny")
	private DZFDouble ncountmny; //分配金额
	
	//*******************仅作计算使用end

	public DZFDouble getNtotalmny() {
		return ntotalmny;
	}

	public void setNtotalmny(DZFDouble ntotalmny) {
		this.ntotalmny = ntotalmny;
	}

	public DZFDouble getNcountmny() {
		return ncountmny;
	}

	public void setNcountmny(DZFDouble ncountmny) {
		this.ncountmny = ncountmny;
	}

	public String getPk_source() {
		return pk_source;
	}

	public void setPk_source(String pk_source) {
		this.pk_source = pk_source;
	}

	public String getPk_invoice_b() {
		return pk_invoice_b;
	}

	public void setPk_invoice_b(String pk_invoice_b) {
		this.pk_invoice_b = pk_invoice_b;
	}

	public String getPk_invoice() {
		return pk_invoice;
	}

	public void setPk_invoice(String pk_invoice) {
		this.pk_invoice = pk_invoice;
	}

	public String getBspmc() {
		return bspmc;
	}

	public void setBspmc(String bspmc) {
		this.bspmc = bspmc;
	}

	public String getInvspec() {
		return invspec;
	}

	public void setInvspec(String invspec) {
		this.invspec = invspec;
	}

	public String getMeasurename() {
		return measurename;
	}

	public void setMeasurename(String measurename) {
		this.measurename = measurename;
	}

	public DZFDouble getBnum() {
		return bnum;
	}

	public void setBnum(DZFDouble bnum) {
		this.bnum = bnum;
	}

	public DZFDouble getBprice() {
		return bprice;
	}

	public void setBprice(DZFDouble bprice) {
		this.bprice = bprice;
	}

	public DZFDouble getBhjje() {
		return bhjje;
	}

	public void setBhjje(DZFDouble bhjje) {
		this.bhjje = bhjje;
	}

	public DZFDouble getBspsl() {
		return bspsl;
	}

	public void setBspsl(DZFDouble bspsl) {
		this.bspsl = bspsl;
	}

	public DZFDouble getBspse() {
		return bspse;
	}

	public void setBspse(DZFDouble bspse) {
		this.bspse = bspse;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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
		return "pk_invoice_b";
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_invoice";
	}

	@Override
	public String getTableName() {
		return "cn_invoice_b";
	}

}
