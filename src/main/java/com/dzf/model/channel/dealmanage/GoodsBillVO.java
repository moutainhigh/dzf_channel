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
	
	@FieldAlias("readdress")
	private String vreceiveaddress;//收货地址
	
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
    
    //仅作展示用字段begin
    
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
	private DZFDate dconfdate; //确认时间（仅作数据展示）
	
	//仅作展示用字段end

	public DZFDate getDconfdate() {
		return dconfdate;
	}

	public void setDconfdate(DZFDate dconfdate) {
		this.dconfdate = dconfdate;
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
