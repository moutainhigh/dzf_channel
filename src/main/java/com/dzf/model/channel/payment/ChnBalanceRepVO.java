package com.dzf.model.channel.payment;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;

/**
 * 付款余额查询VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class ChnBalanceRepVO extends SuperVO{

	private static final long serialVersionUID = -2238601467643308081L;
	
	@FieldAlias("corpid")
	private String pk_corp;//机构主键
	
	@FieldAlias("corpnm")
	private String corpname;// 机构名称
	
	@FieldAlias("dreldate")
	private DZFDate drelievedate;//解约日期（不存库，取客户）
	
	@FieldAlias("incode")
	private String innercode;//机构编码
	
	@FieldAlias("usemny")
	private DZFDouble nusedmny;//已用金额
	
	@FieldAlias("npmny")
	private DZFDouble npaymny;//付款金额
	
	@FieldAlias("iptype")
	private Integer ipaytype;//付款类型   1:加盟费；2：预付款；3：返点
	
	@FieldAlias("ptypenm")
	private String vpaytypename;//付款类型名称 
	
	@FieldAlias("balmny")
	private DZFDouble nbalance;//余额
	
    @FieldAlias("initbal")
    private DZFDouble initbalance;//期初余额
    
    @FieldAlias("bail")
    private DZFDouble bail;//保证金
    
    @FieldAlias("charge")
    private DZFDouble charge;//预付款
    
    @FieldAlias("rebate")
    private DZFDouble rebate;//返点
    
    @FieldAlias("propor")
    private Integer ideductpropor;//扣款比例
    
    @FieldAlias("num")
    private Integer num;//合同数(展示数据)
    
    @FieldAlias("nbmny")
    private DZFDouble nbookmny; // 账本费(展示数据)
    
    @FieldAlias("namny")
    private DZFDouble naccountmny; // 代账费(展示数据)
    
    @FieldAlias("custnum")
    private Integer icustnum;//存量合同数
    
    @FieldAlias("zeronum")
    private Integer izeronum;//0扣款(非存量)合同数
    
    @FieldAlias("dednum")
    private Integer idednum;//非存量合同数
    
	@FieldAlias("aname")
    private String areaname;//大区名称

    @FieldAlias("provname")
	public String vprovname;//省市名称

	@FieldAlias("ovince")
	public Integer vprovince;// 省市
	
	@FieldAlias("mname")
	private String vmanagername; // 渠道经理（只做界面展示）
	
	@FieldAlias("condedmny")
    private DZFDouble ncondedmny;//合同扣款
	
	@FieldAlias("buymny")
    private DZFDouble nbuymny;//商品购买
	
	@FieldAlias("yfhtmny")
	private DZFDouble nyfhtmny;//预付合同扣款
	
	@FieldAlias("yfspmny")
	private DZFDouble nyfspmny;//预付商品扣款
	
	@FieldAlias("fdhtmny")
	private DZFDouble nfdhtmny;//返点合同扣款
	
	@FieldAlias("fdspmny")
	private DZFDouble nfdspmny;//返点商品扣款
    
	public DZFDate getDrelievedate() {
		return drelievedate;
	}

	public void setDrelievedate(DZFDate drelievedate) {
		this.drelievedate = drelievedate;
	}

	public DZFDouble getNyfhtmny() {
		return nyfhtmny;
	}

	public void setNyfhtmny(DZFDouble nyfhtmny) {
		this.nyfhtmny = nyfhtmny;
	}

	public DZFDouble getNyfspmny() {
		return nyfspmny;
	}

	public void setNyfspmny(DZFDouble nyfspmny) {
		this.nyfspmny = nyfspmny;
	}

	public DZFDouble getNfdhtmny() {
		return nfdhtmny;
	}

	public void setNfdhtmny(DZFDouble nfdhtmny) {
		this.nfdhtmny = nfdhtmny;
	}

	public DZFDouble getNfdspmny() {
		return nfdspmny;
	}

	public void setNfdspmny(DZFDouble nfdspmny) {
		this.nfdspmny = nfdspmny;
	}

	public DZFDouble getNcondedmny() {
		return ncondedmny;
	}

	public void setNcondedmny(DZFDouble ncondedmny) {
		this.ncondedmny = ncondedmny;
	}

	public DZFDouble getNbuymny() {
		return nbuymny;
	}

	public void setNbuymny(DZFDouble nbuymny) {
		this.nbuymny = nbuymny;
	}

	public String getVmanagername() {
		return vmanagername;
	}

	public void setVmanagername(String vmanagername) {
		this.vmanagername = vmanagername;
	}

	public Integer getIcustnum() {
		return icustnum;
	}

	public Integer getIzeronum() {
		return izeronum;
	}

	public Integer getIdednum() {
		return idednum;
	}

	public void setIcustnum(Integer icustnum) {
		this.icustnum = icustnum;
	}

	public void setIzeronum(Integer izeronum) {
		this.izeronum = izeronum;
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

	public void setIdednum(Integer idednum) {
		this.idednum = idednum;
	}

	public Integer getIdeductpropor() {
		return ideductpropor;
	}

	public void setIdeductpropor(Integer ideductpropor) {
		this.ideductpropor = ideductpropor;
	}

	public String getVpaytypename() {
		return vpaytypename;
	}

	public void setVpaytypename(String vpaytypename) {
		this.vpaytypename = vpaytypename;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getCorpname() {
		return corpname;
	}

	public DZFDouble getNusedmny() {
		return nusedmny;
	}

	public DZFDouble getNpaymny() {
		return npaymny;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public DZFDouble getNbookmny() {
		return nbookmny;
	}

	public void setNbookmny(DZFDouble nbookmny) {
		this.nbookmny = nbookmny;
	}

	public DZFDouble getNaccountmny() {
		return naccountmny;
	}

	public void setNaccountmny(DZFDouble naccountmny) {
		this.naccountmny = naccountmny;
	}

	public Integer getIpaytype() {
		return ipaytype;
	}

	public DZFDouble getRebate() {
		return rebate;
	}

	public void setRebate(DZFDouble rebate) {
		this.rebate = rebate;
	}

	public DZFDouble getNbalance() {
		return nbalance;
	}

	public DZFDouble getInitbalance() {
		return initbalance;
	}

	public String getInnercode() {
		return innercode;
	}

	public void setInnercode(String innercode) {
		this.innercode = innercode;
	}

	public DZFDouble getBail() {
		return bail;
	}

	public DZFDouble getCharge() {
		return charge;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public void setNusedmny(DZFDouble nusedmny) {
		this.nusedmny = nusedmny;
	}

	public void setNpaymny(DZFDouble npaymny) {
		this.npaymny = npaymny;
	}

	public void setIpaytype(Integer ipaytype) {
		this.ipaytype = ipaytype;
	}

	public void setNbalance(DZFDouble nbalance) {
		this.nbalance = nbalance;
	}

	public void setInitbalance(DZFDouble initbalance) {
		this.initbalance = initbalance;
	}

	public void setBail(DZFDouble bail) {
		this.bail = bail;
	}

	public void setCharge(DZFDouble charge) {
		this.charge = charge;
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
