package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;

@SuppressWarnings({ "serial" })
public class LogisticRepVO extends SuperVO{
	
	@FieldAlias("corpid")
	private String pk_corp;
	
	@FieldAlias("id")
	private String pk_id;
	
	private String corpname;//加盟商名称
	
	@FieldAlias("aname")
    private String areaname;//大区名称
	
	@FieldAlias("ovince")
	private Integer vprovince;// 地区
	
	@FieldAlias("mid")
	private String vmanager;//渠道经理id
	
	//////////////////////////////1、收货信息	//////////////////////////////
	
	@FieldAlias("rename")
	private String vreceivername;//收货人
	
	private String phone;//联系电话
	
	@FieldAlias("readdress")
	private String vreceiveaddress;//收货地址
	
	//////////////////////////////2、快递信息	//////////////////////////////
	
	@FieldAlias("logunit")
	private String logisticsunit; //快递公司
	
	@FieldAlias("fcode")
	private String fastcode; //物流单号
	
	@FieldAlias("fcost")
	private DZFDouble fastcost;//物流费用
	
	@FieldAlias("dedate")
	private DZFDate deliverdate;//发货日期
	
	//////////////////////////////其它//////////////////////////////
	
	@FieldAlias("memo")
	private String vmemo;//备注
	

    public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getPk_id() {
		return pk_id;
	}

	public void setPk_id(String pk_id) {
		this.pk_id = pk_id;
	}

	public String getVreceivername() {
		return vreceivername;
	}

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
	}

	public void setVreceivername(String vreceivername) {
		this.vreceivername = vreceivername;
	}

	public String getPhone() {
		return phone;
	}

	public String getVmanager() {
		return vmanager;
	}

	public void setVmanager(String vmanager) {
		this.vmanager = vmanager;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVreceiveaddress() {
		return vreceiveaddress;
	}

	public void setVreceiveaddress(String vreceiveaddress) {
		this.vreceiveaddress = vreceiveaddress;
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

	public DZFDouble getFastcost() {
		return fastcost;
	}

	public void setFastcost(DZFDouble fastcost) {
		this.fastcost = fastcost;
	}

	public DZFDate getDeliverdate() {
		return deliverdate;
	}

	public void setDeliverdate(DZFDate deliverdate) {
		this.deliverdate = deliverdate;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
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
