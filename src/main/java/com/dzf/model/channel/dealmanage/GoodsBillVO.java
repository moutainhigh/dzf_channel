package com.dzf.model.channel.dealmanage;

import com.dzf.model.pub.MultSuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 商品购买订单
 * @author zy
 *
 */
@SuppressWarnings({ "serial" })
public class GoodsBillVO extends MultSuperVO {
	
	@FieldAlias("billid")
	private String pk_goodsbill;//主键
	
	@FieldAlias("corpid")
    private String pk_corp; // 所属公司
	
	@FieldAlias("billcode")
	private String vbillcode;//订单号
	
	private Integer vstatus;//状态  0：待确认；1：待发货；2：已发货；3：已收货；4：已取消；
	
	@FieldAlias("rename")
	private String vreceivername;//收货人
	
	private String phone;//联系方式
	
	@FieldAlias("recode")
	private String vzipcode;//收货邮编
	
    @FieldAlias("ovince")
    public Integer vprovince;// 省
    
    @FieldAlias("city")
    public Integer vcity;// 市
    
    @FieldAlias("area")
    public Integer varea;// 区
    
    @FieldAlias("ccounty")
    public String citycounty;//所属区域
	
	@FieldAlias("readdress")
	private String vreceiveaddress;//详细地址
	
	@FieldAlias("ndesummny")
	private DZFDouble ndedsummny;// 扣款总金额

	@FieldAlias("ndemny")
	private DZFDouble ndeductmny;// 扣款金额-预付款

	@FieldAlias("nderebmny")
	private DZFDouble ndedrebamny;// 扣款金额-返点款
	
    @FieldAlias("opid")
    private String coperatorid; // 制单人

    @FieldAlias("opdate")
    private DZFDate doperatedate; // 制单日期
    
    private Integer dr; // 删除标记

    private DZFDateTime ts; // 时间戳
    
    @FieldAlias("tistatus")
    private Integer vtistatus;//开票状态 1：未开票；2：已开票；
    
    //&&&&&&&&&&&&&&&&&仅作展示用字段begin&&&&&&&&&&&&&&&
    
	@FieldAlias("pcode")
    private String corpcode; // 公司编码（仅作数据展示）
	
	@FieldAlias("pname")
    private String corpname; // 公司编码（仅作数据展示）
	
	@FieldAlias("submtime")
	private DZFDateTime dsubmittime; //提交时间（仅作数据展示）
	
	@FieldAlias("reason")
	private String vrejereason;//驳回原因（仅作数据传递）
	
	@FieldAlias("logunit")
	private String logisticsunit; //物流公司
	
	@FieldAlias("fcode")
	private String fastcode; //物流单号
	
	private GoodsBillSVO[] detail;//订单明细
	
	private GoodsBillBVO[] goods;//商品明细
	
	@FieldAlias("confdate")
	private DZFDate dconfdate; //确认日期（仅作数据展示）
	
	@FieldAlias("bdate")
	private DZFDate begdate;// 开始日期（查询使用）

	@FieldAlias("edate")
	private DZFDate enddate;// 结束日期（查询使用）
	
	@FieldAlias("bbdate") 
	private DZFDate bbegdate;// 开始日期（查询使用）

	@FieldAlias("eedate")
	private DZFDate eenddate;// 结束日期（查询使用）
	
	@FieldAlias("oid")
	private String voperater;//渠道运营id
	
	@FieldAlias("aname")
    private String areaname;//大区名称
	
    @FieldAlias("provname")
	public String vprovname;//省市名称
    
	@FieldAlias("qrysql")
	private String vqrysql;//查询语句
	
	@FieldAlias("dreldate")
    private DZFDate drelievedate;//解约日期
	
	//&&&&&&&&&&&&&&&&&仅作展示用字段end&&&&&&&&&&&&&&&

	public Integer getVtistatus() {
		return vtistatus;
	}

	public void setVtistatus(Integer vtistatus) {
		this.vtistatus = vtistatus;
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

	public DZFDate getBbegdate() {
		return bbegdate;
	}

	public void setBbegdate(DZFDate bbegdate) {
		this.bbegdate = bbegdate;
	}

	public String getVqrysql() {
		return vqrysql;
	}

	public void setVqrysql(String vqrysql) {
		this.vqrysql = vqrysql;
	}

	public String getVoperater() {
		return voperater;
	}

	public void setVoperater(String voperater) {
		this.voperater = voperater;
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

	public DZFDate getEenddate() {
		return eenddate;
	}

	public void setEenddate(DZFDate eenddate) {
		this.eenddate = eenddate;
	}

	public DZFDate getDconfdate() {
		return dconfdate;
	}

	public void setDconfdate(DZFDate dconfdate) {
		this.dconfdate = dconfdate;
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

	public String getPk_goodsbill() {
		return pk_goodsbill;
	}

	public GoodsBillSVO[] getDetail() {
		return detail;
	}

	public void setDetail(GoodsBillSVO[] detail) {
		this.detail = detail;
	}

	public GoodsBillBVO[] getGoods() {
		return goods;
	}

	public void setGoods(GoodsBillBVO[] goods) {
		this.goods = goods;
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

	public String getVrejereason() {
		return vrejereason;
	}

	public void setVrejereason(String vrejereason) {
		this.vrejereason = vrejereason;
	}

	public String getCorpcode() {
		return corpcode;
	}

	public void setCorpcode(String corpcode) {
		this.corpcode = corpcode;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public DZFDateTime getDsubmittime() {
		return dsubmittime;
	}

	public void setDsubmittime(DZFDateTime dsubmittime) {
		this.dsubmittime = dsubmittime;
	}

	public void setPk_goodsbill(String pk_goodsbill) {
		this.pk_goodsbill = pk_goodsbill;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
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

	public DZFDouble getNdedsummny() {
		return ndedsummny;
	}

	public void setNdedsummny(DZFDouble ndedsummny) {
		this.ndedsummny = ndedsummny;
	}

	public DZFDouble getNdeductmny() {
		return ndeductmny;
	}

	public void setNdeductmny(DZFDouble ndeductmny) {
		this.ndeductmny = ndeductmny;
	}

	public DZFDouble getNdedrebamny() {
		return ndedrebamny;
	}

	public void setNdedrebamny(DZFDouble ndedrebamny) {
		this.ndedrebamny = ndedrebamny;
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
	
	public DZFDate getDrelievedate() {
		return drelievedate;
	}

	public void setDrelievedate(DZFDate drelievedate) {
		this.drelievedate = drelievedate;
	}

	@Override
	public String getPKFieldName() {
		return "pk_goodsbill";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_goodsbill";
	}
	
	@Override
	public String[] getTableCodes() {
	    return new String[] { "cn_goodsbill_b", "cn_goodsbill_s" };
	}
	
}
