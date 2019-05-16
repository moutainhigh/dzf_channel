package com.dzf.model.channel.sale;

import com.dzf.model.channel.report.DataVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

@SuppressWarnings({ "serial" })
public class AccountSetVO extends DataVO{
	
	@FieldAlias("asetid")
    private String pk_accountset; //主键
    
    @FieldAlias("corpid")
    private String pk_corp; //加盟商公司主键

    @FieldAlias("corpkid")
    private String pk_corpk; //客户主键
    
    @FieldAlias("contractid")
    private String pk_contract; //合同主键
    
    @FieldAlias("vccode")
    private String vcontcode; // 合同编码
    
    @FieldAlias("bperiod")
    private String vbeginperiod;//开始期间
    
    @FieldAlias("eperiod")
    private String vendperiod;//结束期间
    
    @FieldAlias("cperiod")
    private String vchangeperiod;//调整期间
    
    private Integer idiff;//差异月份
    
    private Integer istatus;//状态 0：启用；1：禁用
    
	@FieldAlias("coptid")
	private String coperatorid;//录入人
	
	@FieldAlias("ddate")
	private DZFDate doperatedate;//创建日期
	
    private Integer dr;//删除标记
    
    private DZFDateTime ts;//时间戳
    
    private String corpkname;//客户名称
    
	@FieldAlias("coptname")
	private String coperatname;//录入人名称

	public String getPk_accountset() {
		return pk_accountset;
	}

	public void setPk_accountset(String pk_accountset) {
		this.pk_accountset = pk_accountset;
	}

	public String getCorpkname() {
		return corpkname;
	}

	public void setCorpkname(String corpkname) {
		this.corpkname = corpkname;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getPk_contract() {
		return pk_contract;
	}

	public String getCoperatname() {
		return coperatname;
	}

	public void setCoperatname(String coperatname) {
		this.coperatname = coperatname;
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

	public void setPk_contract(String pk_contract) {
		this.pk_contract = pk_contract;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public String getVcontcode() {
		return vcontcode;
	}

	public void setVcontcode(String vcontcode) {
		this.vcontcode = vcontcode;
	}

	public String getVbeginperiod() {
		return vbeginperiod;
	}

	public void setVbeginperiod(String vbeginperiod) {
		this.vbeginperiod = vbeginperiod;
	}

	public String getVendperiod() {
		return vendperiod;
	}

	public void setVendperiod(String vendperiod) {
		this.vendperiod = vendperiod;
	}

	public String getVchangeperiod() {
		return vchangeperiod;
	}

	public void setVchangeperiod(String vchangeperiod) {
		this.vchangeperiod = vchangeperiod;
	}

	public Integer getIdiff() {
		return idiff;
	}

	public void setIdiff(Integer idiff) {
		this.idiff = idiff;
	}

	public Integer getIstatus() {
		return istatus;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
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
	
	@Override
	public String getPKFieldName() {
		return "pk_accountset";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_accountset";
	}
	
}
