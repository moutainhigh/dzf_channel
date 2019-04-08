package com.dzf.model.channel.matmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 物料订单主表VO
 */
public class MatOrderVO extends SuperVO {

	@FieldAlias("matbillid")
	private String pk_materielbill;//主键
	
	@FieldAlias("corpid")
	private String pk_corp;//公司主键
	
	@FieldAlias("logid")
	private String pk_logistics;//快递公司id
	
	@FieldAlias("fcode")
	private String fastcode;//快递单号
	
	@FieldAlias("fcost")
	private DZFDouble fastcost;//快递费用
	
	@FieldAlias("code")
	private String vcontcode;//合同编号
	
	@FieldAlias("status")
	private Integer vstatus;//合同状态   0-全部、1-待审核、2-待发货、3-已发货、4-已驳回
	
	@FieldAlias("fcorp")
	private String fathercorp;//加盟商id
	
	private Integer vprovince;//省
	
	private Integer vcity;//市
	
	private Integer varea;//区
	
	@FieldAlias("cname")
	private String citycounty;//省市区名称
	
	@FieldAlias("address")
	private String vaddress;//地址
	
	@FieldAlias("receiver")
	private String vreceiver;//收货人
	
	private String phone;//电话
	
	@FieldAlias("memo")
	private String vmemo;//备注
	
	@FieldAlias("operid")
	private String coperatorid;//录入人
	
	@FieldAlias("operdate")
	private DZFDate doperatedate;//录入日期
	
	@FieldAlias("adate")
	private DZFDate applydate;//申请日期
	
	@FieldAlias("auid")
	private String auditerid;//审核人
	
	@FieldAlias("audate")
	private DZFDate auditdate;//审核日期
	
	@FieldAlias("deid")
	private String deliverid;//发货人
	
	@FieldAlias("dedate")
	private DZFDate deliverdate;//发货日期
	
	@FieldAlias("reason")
	private String vreason;//驳回原因
	
	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间
	
	/*******仅作展示，不存库*******/
	
	@FieldAlias("begdate")
	private String begindate;//录入开始日期
	
	private String enddate;//录入结束日期
	
	@FieldAlias("applybegin")
	private String applybegindate;//申请开始日期
	
	@FieldAlias("applyend")
	private String applyenddate;//申请结束日期
	
	@FieldAlias("aname")
    private String areaname;//大区名称
	
	@FieldAlias("mname")
	private String vmanagername; // 渠道经理
	
	private String corpname; // 渠道商名称
	
	private String proname; // 省/市
	
    private Integer outnum;//发货数量
	
	private Integer applynum;//申请数量
	
	private String logname; // 快递公司名称
	
	private Integer parid;//地区父id
	
	private Integer regid;//地区id
	
	private String pname;//省份名称
	
	private String cityname;//市名称
	
	private String countryname;//区县名称
	
	private String applyname;//申请人名称
	
	@FieldAlias("wlname")
	private String vname;//物料名称
	
	@FieldAlias("unit")
	private String vunit;//单位
	
	/*******仅作展示，不存库*******/
	//0-全部、1-待审核、2-待发货、3-已发货、4-已驳回
	public static final String VSTATUS = "vstatus";
	
	public static final Integer VSTATUS_1 = 1;
	
	public static final Integer VSTATUS_2 = 2;
	
	public static final Integer VSTATUS_3 = 3;
	
	public static final Integer VSTATUS_4 = 4;
	
	public String getPk_materielbill() {
		return pk_materielbill;
	}

	public void setPk_materielbill(String pk_materielbill) {
		this.pk_materielbill = pk_materielbill;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_logistics() {
		return pk_logistics;
	}

	public void setPk_logistics(String pk_logistics) {
		this.pk_logistics = pk_logistics;
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

	public String getVcontcode() {
		return vcontcode;
	}

	public void setVcontcode(String vcontcode) {
		this.vcontcode = vcontcode;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
	}

	public String getFathercorp() {
		return fathercorp;
	}

	public void setFathercorp(String fathercorp) {
		this.fathercorp = fathercorp;
	}

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
	}

	public Integer getVcity() {
		return vcity;
	}

	public void setVcity(Integer vcity) {
		this.vcity = vcity;
	}

	public Integer getVarea() {
		return varea;
	}

	public void setVarea(Integer varea) {
		this.varea = varea;
	}

	public String getCitycounty() {
		return citycounty;
	}

	public void setCitycounty(String citycounty) {
		this.citycounty = citycounty;
	}

	public String getVaddress() {
		return vaddress;
	}

	public void setVaddress(String vaddress) {
		this.vaddress = vaddress;
	}

	public String getVreceiver() {
		return vreceiver;
	}

	public void setVreceiver(String vreceiver) {
		this.vreceiver = vreceiver;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
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

	public DZFDate getApplydate() {
		return applydate;
	}

	public void setApplydate(DZFDate applydate) {
		this.applydate = applydate;
	}

	public String getAuditerid() {
		return auditerid;
	}

	public void setAuditerid(String auditerid) {
		this.auditerid = auditerid;
	}

	public DZFDate getAuditdate() {
		return auditdate;
	}

	public void setAuditdate(DZFDate auditdate) {
		this.auditdate = auditdate;
	}

	public String getDeliverid() {
		return deliverid;
	}

	public void setDeliverid(String deliverid) {
		this.deliverid = deliverid;
	}

	public DZFDate getDeliverdate() {
		return deliverdate;
	}

	public void setDeliverdate(DZFDate deliverdate) {
		this.deliverdate = deliverdate;
	}

	public String getVreason() {
		return vreason;
	}

	public void setVreason(String vreason) {
		this.vreason = vreason;
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
	
	public String getBegindate() {
		return begindate;
	}

	public void setBegindate(String begindate) {
		this.begindate = begindate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getApplybegindate() {
		return applybegindate;
	}

	public void setApplybegindate(String applybegindate) {
		this.applybegindate = applybegindate;
	}

	public String getApplyenddate() {
		return applyenddate;
	}

	public void setApplyenddate(String applyenddate) {
		this.applyenddate = applyenddate;
	}
	
	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getVmanagername() {
		return vmanagername;
	}

	public void setVmanagername(String vmanagername) {
		this.vmanagername = vmanagername;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getProname() {
		return proname;
	}

	public void setProname(String proname) {
		this.proname = proname;
	}

	public Integer getOutnum() {
		return outnum;
	}

	public void setOutnum(Integer outnum) {
		this.outnum = outnum;
	}

	public Integer getApplynum() {
		return applynum;
	}

	public void setApplynum(Integer applynum) {
		this.applynum = applynum;
	}

	public String getLogname() {
		return logname;
	}

	public void setLogname(String logname) {
		this.logname = logname;
	}

	public Integer getParid() {
		return parid;
	}

	public void setParid(Integer parid) {
		this.parid = parid;
	}
	
	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}
	
	public String getCityname() {
		return cityname;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}

	public String getCountryname() {
		return countryname;
	}

	public void setCountryname(String countryname) {
		this.countryname = countryname;
	}
	
	public Integer getRegid() {
		return regid;
	}

	public void setRegid(Integer regid) {
		this.regid = regid;
	}
	
	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public String getVunit() {
		return vunit;
	}

	public void setVunit(String vunit) {
		this.vunit = vunit;
	}
	
	public String getApplyname() {
		return applyname;
	}

	public void setApplyname(String applyname) {
		this.applyname = applyname;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_materielbill";
	}

	@Override
	public String getTableName() {
		return "cn_materielbill";
	}

}
