package com.dzf.model.branch.reportmanage;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;

/**
 * 客户数据VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class CorpDataVO extends SuperVO {

	private static final long serialVersionUID = 1L;
	
    @FieldAlias("fcorp")
    private String fathercorp;// 会计公司主键
    
    @FieldAlias("pname")
    private String corpname;//会计公司名称
	
    @FieldAlias("pk_gs")
    private String pk_corp;// 客户主键
    
    @FieldAlias("ucode")
    private String unitcode;// 公司编码
    
    @FieldAlias("uname")
    private String unitname;// 公司名称
    
    @FieldAlias("ccounty")
    public String citycounty;//省市
    
    @FieldAlias("chname")
    private String chargedeptname;// 纳税人性质
    
    @FieldAlias("bdate")
    private DZFDate begindate;// 建账日期
    
    @FieldAlias("jzzt")
    private String vjzstatues;//记账状态
    
    @FieldAlias("bszt")
    private String vbsstatues;//报税状态
    
    @FieldAlias("cbdate")
    private DZFDate dbegindate; // 开始日期

    @FieldAlias("edate")
    private DZFDate denddate; // 结束日期
    
    @FieldAlias("ntlmny")
    private DZFDouble ntotalmny; // 合同总金额
    
    @FieldAlias("nbmny")
    private DZFDouble nbookmny; // 账本费已收金额
    
    @FieldAlias("nmsmny")
    private DZFDouble nmservicemny; // 每月服务费
    
	@FieldAlias("nyhmny")
	private DZFDouble nyhmny; // 合同优惠
    
    @FieldAlias("nmny")
    private DZFDouble nreceivemny; // 收款金额
    
    @FieldAlias("smonth")
    private Integer isurplusmonth;// 服务余额/月
    
    @FieldAlias("pcount")
    public String pcountname;// 主办会计名称
    
    @FieldAlias("pcountid")
    public String vsuperaccount;// 主办会计主键
    
    @FieldAlias("fname")
    public String foreignname;// 销售代表
    
    @FieldAlias("cdate")
    public DZFDate createdate;// 录入日期

	public DZFDouble getNmservicemny() {
		return nmservicemny;
	}

	public void setNmservicemny(DZFDouble nmservicemny) {
		this.nmservicemny = nmservicemny;
	}

	public DZFDouble getNyhmny() {
		return nyhmny;
	}

	public void setNyhmny(DZFDouble nyhmny) {
		this.nyhmny = nyhmny;
	}

	public DZFDouble getNbookmny() {
		return nbookmny;
	}

	public void setNbookmny(DZFDouble nbookmny) {
		this.nbookmny = nbookmny;
	}

	public String getFathercorp() {
		return fathercorp;
	}

	public void setFathercorp(String fathercorp) {
		this.fathercorp = fathercorp;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getCitycounty() {
		return citycounty;
	}

	public void setCitycounty(String citycounty) {
		this.citycounty = citycounty;
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	public String getChargedeptname() {
		return chargedeptname;
	}

	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
	}

	public DZFDate getBegindate() {
		return begindate;
	}

	public void setBegindate(DZFDate begindate) {
		this.begindate = begindate;
	}

	public String getVjzstatues() {
		return vjzstatues;
	}

	public void setVjzstatues(String vjzstatues) {
		this.vjzstatues = vjzstatues;
	}

	public String getVbsstatues() {
		return vbsstatues;
	}

	public void setVbsstatues(String vbsstatues) {
		this.vbsstatues = vbsstatues;
	}

	public DZFDate getDbegindate() {
		return dbegindate;
	}

	public void setDbegindate(DZFDate dbegindate) {
		this.dbegindate = dbegindate;
	}

	public DZFDate getDenddate() {
		return denddate;
	}

	public void setDenddate(DZFDate denddate) {
		this.denddate = denddate;
	}

	public DZFDouble getNtotalmny() {
		return ntotalmny;
	}

	public void setNtotalmny(DZFDouble ntotalmny) {
		this.ntotalmny = ntotalmny;
	}

	public DZFDouble getNreceivemny() {
		return nreceivemny;
	}

	public void setNreceivemny(DZFDouble nreceivemny) {
		this.nreceivemny = nreceivemny;
	}

	public Integer getIsurplusmonth() {
		return isurplusmonth;
	}

	public void setIsurplusmonth(Integer isurplusmonth) {
		this.isurplusmonth = isurplusmonth;
	}

	public String getPcountname() {
		return pcountname;
	}

	public void setPcountname(String pcountname) {
		this.pcountname = pcountname;
	}

	public String getVsuperaccount() {
		return vsuperaccount;
	}

	public void setVsuperaccount(String vsuperaccount) {
		this.vsuperaccount = vsuperaccount;
	}

	public String getForeignname() {
		return foreignname;
	}

	public void setForeignname(String foreignname) {
		this.foreignname = foreignname;
	}

	public DZFDate getCreatedate() {
		return createdate;
	}

	public void setCreatedate(DZFDate createdate) {
		this.createdate = createdate;
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
