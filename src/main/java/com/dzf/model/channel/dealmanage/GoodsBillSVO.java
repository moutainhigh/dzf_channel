package com.dzf.model.channel.dealmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 商品购买订单状态详情
 *
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class GoodsBillSVO extends SuperVO {
	
	@FieldAlias("billid")
	private String pk_goodsbill;//主表主键
	
	@FieldAlias("billid_s")
	private String pk_goodsbill_s;//主键
	
	@FieldAlias("corpid")
    private String pk_corp; // 所属公司
	
	@FieldAlias("action")
	private String vsaction;//状态动作   订单创建
	
	private Integer vstatus;//状态   0：订单创建
	
	@FieldAlias("describe")
	private String vsdescribe;//状态说明
	
	@FieldAlias("logid")
	private String pk_logistics;//物流档案id
	
	@FieldAlias("logunit")
	private String logisticsunit; //物流公司
	
	@FieldAlias("fcode")
	private String fastcode; //物流单号
	
	@FieldAlias("opid")
	private String coperatorid; //处理人
	
    @FieldAlias("opname")
	private String coperatorname; //处理人名称
	
	@FieldAlias("opdate")
	private DZFDate doperatedate; //处理日期
	
	@FieldAlias("optime")
	private DZFDateTime doperatetime; //处理时间
	
	@FieldAlias("note")
	private String vnote; //处理说明
	
    private Integer dr; // 删除标记

    private DZFDateTime ts; // 时间戳
	
	public String getPk_goodsbill() {
		return pk_goodsbill;
	}

	public void setPk_goodsbill(String pk_goodsbill) {
		this.pk_goodsbill = pk_goodsbill;
	}

	public String getPk_goodsbill_s() {
		return pk_goodsbill_s;
	}

	public void setPk_goodsbill_s(String pk_goodsbill_s) {
		this.pk_goodsbill_s = pk_goodsbill_s;
	}

	public String getPk_logistics() {
		return pk_logistics;
	}

	public void setPk_logistics(String pk_logistics) {
		this.pk_logistics = pk_logistics;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVsaction() {
		return vsaction;
	}

	public void setVsaction(String vsaction) {
		this.vsaction = vsaction;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
	}

	public String getVsdescribe() {
		return vsdescribe;
	}

	public void setVsdescribe(String vsdescribe) {
		this.vsdescribe = vsdescribe;
	}

	public String getLogisticsunit() {
		return logisticsunit;
	}

	public void setLogisticsunit(String logisticsunit) {
		this.logisticsunit = logisticsunit;
	}

	public String getFastcode() {
		return fastcode;
	}

	public void setFastcode(String fastcode) {
		this.fastcode = fastcode;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public String getCoperatorname() {
		return coperatorname;
	}

	public void setCoperatorname(String coperatorname) {
		this.coperatorname = coperatorname;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public DZFDateTime getDoperatetime() {
		return doperatetime;
	}

	public void setDoperatetime(DZFDateTime doperatetime) {
		this.doperatetime = doperatetime;
	}

	public String getVnote() {
		return vnote;
	}

	public void setVnote(String vnote) {
		this.vnote = vnote;
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
		return "pk_goodsbill_s";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_goodsbill_s";
	}
	
}
