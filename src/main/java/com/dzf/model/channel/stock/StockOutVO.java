package com.dzf.model.channel.stock;

import com.dzf.model.pub.MultSuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 出库单VO
 *
 */
/**
 * @author admin
 *
 */
@SuppressWarnings({ "serial" })
public class StockOutVO extends MultSuperVO {
	
	@FieldAlias("soutid")
	private String pk_stockout;
	
	@FieldAlias("corpid")
	private String pk_corp;
	
	private String fathercorp;
	
	@FieldAlias("vcode")
	private String vbillcode;
	
	@FieldAlias("nmny")
	private DZFDouble ntotalmny;//销售总金额
	
	@FieldAlias("logid")
	private String pk_logistics;//物流档案id
	
	@FieldAlias("logunit")
	private String logisticsunit; //物流公司
	
	@FieldAlias("fcode")
	private String fastcode; //物流单号
	
	@FieldAlias("fcost")
	private DZFDouble fastcost;//物流费用
	
	private Integer vstatus;//状态 0:待确认；1：待发货；2：已发货
	
	@FieldAlias("ctid")
	private String coperatorid;
	
	@FieldAlias("ctdate")
	private DZFDateTime doperatedate;
	
	@FieldAlias("conid")
	private String vconfirmid;//确认出库人id
	
	@FieldAlias("conname")
	private String vconfirmname;//确认出库人
	
	@FieldAlias("contime")
	private DZFDateTime dconfirmtime;//确认出库时间
	
	@FieldAlias("delid")
	private String vdeliverid;//发货人
	
	@FieldAlias("deltime")
	private DZFDateTime ddelivertime;//发货时间
	
	@FieldAlias("memo")
	private String vmemo;//备注
	
	
	@FieldAlias("rename")
	private String vreceivername;//收货人
	
	private String phone;//联系方式
	
	@FieldAlias("recode")
	private String vzipcode;//收货邮编
	
	@FieldAlias("readdress")
	private String vreceiveaddress;//收货地址
	
	@FieldAlias("getdate")
	private DZFDate vgetdate;//领取日期
	
	private Integer itype;//null商品购买；1：内部损耗

    private Integer dr;// 删除标记
    
    private DZFDateTime ts;// 时间戳
	
	@FieldAlias("ctname")
	private String coperatname;//录入人名称
	
	private String corpname;//加盟商名称
	
	@FieldAlias("aname")
    private String areaname;//大区名称
	
    @FieldAlias("provname")
	public String vprovname;//省市名称
	
	@FieldAlias("ovince")
	private Integer vprovince;// 地区
	
	@FieldAlias("oid")
	private String voperater;//渠道运营id

    public String getVconfirmname() {
		return vconfirmname;
	}

	public void setVconfirmname(String vconfirmname) {
		this.vconfirmname = vconfirmname;
	}

	public String getPk_stockout() {
		return pk_stockout;
	}

	public void setPk_stockout(String pk_stockout) {
		this.pk_stockout = pk_stockout;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getFathercorp() {
		return fathercorp;
	}

	public String getPk_logistics() {
		return pk_logistics;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getVprovname() {
		return vprovname;
	}

	public void setVprovname(String vprovname) {
		this.vprovname = vprovname;
	}

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
	}

	public String getVoperater() {
		return voperater;
	}

	public void setVoperater(String voperater) {
		this.voperater = voperater;
	}

	public DZFDouble getFastcost() {
		return fastcost;
	}

	public void setFastcost(DZFDouble fastcost) {
		this.fastcost = fastcost;
	}

	public void setPk_logistics(String pk_logistics) {
		this.pk_logistics = pk_logistics;
	}

	public void setFathercorp(String fathercorp) {
		this.fathercorp = fathercorp;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public String getCorpname() {
		return corpname;
	}

	public String getVreceivername() {
		return vreceivername;
	}

	public void setVreceivername(String vreceivername) {
		this.vreceivername = vreceivername;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVzipcode() {
		return vzipcode;
	}

	public void setVzipcode(String vzipcode) {
		this.vzipcode = vzipcode;
	}

	public String getVreceiveaddress() {
		return vreceiveaddress;
	}

	public void setVreceiveaddress(String vreceiveaddress) {
		this.vreceiveaddress = vreceiveaddress;
	}

	public Integer getItype() {
		return itype;
	}

	public DZFDate getVgetdate() {
		return vgetdate;
	}

	public void setVgetdate(DZFDate vgetdate) {
		this.vgetdate = vgetdate;
	}

	public void setItype(Integer itype) {
		this.itype = itype;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public DZFDouble getNtotalmny() {
		return ntotalmny;
	}

	public String getCoperatname() {
		return coperatname;
	}

	public void setCoperatname(String coperatname) {
		this.coperatname = coperatname;
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

	public void setNtotalmny(DZFDouble ntotalmny) {
		this.ntotalmny = ntotalmny;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDateTime getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDateTime doperatedate) {
		this.doperatedate = doperatedate;
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

	public String getVdeliverid() {
		return vdeliverid;
	}

	public void setVdeliverid(String vdeliverid) {
		this.vdeliverid = vdeliverid;
	}

	public DZFDateTime getDdelivertime() {
		return ddelivertime;
	}

	public void setDdelivertime(DZFDateTime ddelivertime) {
		this.ddelivertime = ddelivertime;
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
        return "pk_stockout";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "cn_stockout";
    }
    
	@Override
	public String[] getTableCodes() {
	    return new String[] { "cn_stockout_b"};
	}

}
