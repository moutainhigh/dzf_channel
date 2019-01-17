package com.dzf.model.channel.dealmanage;

import com.dzf.model.pub.MultSuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 商品购买订单
 * @author yy
 *
 */
@SuppressWarnings({ "serial" })
public class GoodsBillMxVO extends MultSuperVO {
	
	@FieldAlias("billid")
	private String pk_goodsbill;//主键
	
	@FieldAlias("corpid")
    private String pk_corp; // 所属公司
	
	@FieldAlias("billcode")
	private String vbillcode;//订单号
	
	private String vstatus;//状态  0：待确认；1：待发货；2：已发货；3：已收货；4：已取消；
	
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
    
    @FieldAlias("updates")
    private DZFDateTime ts; // 时间戳
    
    //仅作展示用字段begin
    
	@FieldAlias("pcode")
    private String corpcode; // 公司编码（仅作数据展示）
	
	@FieldAlias("pname")
    private String corpname; // 公司编码（仅作数据展示）
	
	@FieldAlias("submtime")
	private DZFDate dsubmittime; //提交日期（仅作数据展示）
	
	@FieldAlias("confirmtime")
	private DZFDate dconfirmtime; //扣款日期（仅作数据展示）
	
	@FieldAlias("sendtime")
	private DZFDate dsendtime; //发货日期（仅作数据展示）
	
	@FieldAlias("reason")
	private String vrejereason;//驳回原因（仅作数据传递）
	
	@FieldAlias("logunit")
	private String logisticsunit; //物流公司
	
	@FieldAlias("fcode")
	private String fastcode; //物流单号
	
	@FieldAlias("submitbegin")
	private String submitbegin; //提交开始日期
	
	@FieldAlias("submitend")
	private String submitend; //提交结束日期
	
	@FieldAlias("kbegin")
	private String kbegin; //扣款开始日期
	
	@FieldAlias("kend")
	private String kend; //扣款结束日期
	
	@FieldAlias("gid")
	private String pk_goods;//商品主键
	
	private GoodsBillSVO[] detail;//订单明细
	
	private GoodsBillBVO[] goods;//商品明细
	
	@FieldAlias("gname")
	private String vgoodsname; // 商品名称
 
	@FieldAlias("price")
	private DZFDouble nprice; // 单价
	
	@FieldAlias("amount")
	private Integer amount; // 数量
	
	@FieldAlias("totalmny")
	private DZFDouble ntotalmny; //合计
	
	@FieldAlias("spec")
	private String invspec;//规格
	
	@FieldAlias("type")
	private String invtype;//型号
	
	//仅作展示用字段end
	
	public String getPk_goodsbill() {
		return pk_goodsbill;
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

	public String getVgoodsname() {
		return vgoodsname;
	}

	public void setVgoodsname(String vgoodsname) {
		this.vgoodsname = vgoodsname;
	}

	public DZFDouble getNprice() {
		return nprice;
	}

	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public DZFDouble getNtotalmny() {
		return ntotalmny;
	}

	public void setNtotalmny(DZFDouble ntotalmny) {
		this.ntotalmny = ntotalmny;
	}

	public String getPk_goods() {
		return pk_goods;
	}

	public void setPk_goods(String pk_goods) {
		this.pk_goods = pk_goods;
	}

	public String getSubmitbegin() {
		return submitbegin;
	}

	public void setSubmitbegin(String submitbegin) {
		this.submitbegin = submitbegin;
	}

	public String getSubmitend() {
		return submitend;
	}

	public void setSubmitend(String submitend) {
		this.submitend = submitend;
	}

	public String getKbegin() {
		return kbegin;
	}

	public void setKbegin(String kbegin) {
		this.kbegin = kbegin;
	}

	public String getKend() {
		return kend;
	}

	public void setKend(String kend) {
		this.kend = kend;
	}

	public DZFDate getDsubmittime() {
		return dsubmittime;
	}

	public void setDsubmittime(DZFDate dsubmittime) {
		this.dsubmittime = dsubmittime;
	}

	public DZFDate getDconfirmtime() {
		return dconfirmtime;
	}

	public void setDconfirmtime(DZFDate dconfirmtime) {
		this.dconfirmtime = dconfirmtime;
	}

	public DZFDate getDsendtime() {
		return dsendtime;
	}

	public void setDsendtime(DZFDate dsendtime) {
		this.dsendtime = dsendtime;
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

	public String getVstatus() {
		return vstatus;
	}

	public void setVstatus(String vstatus) {
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
