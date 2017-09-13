package com.dzf.model.channel;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 合同确认VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class ContractConfrimVO extends SuperVO {

	private static final long serialVersionUID = 8799494318014396487L;
	
    @FieldAlias("id")
    private String pk_confrim; // 主键
	
    @FieldAlias("contractid")
    private String pk_contract; // 合同主键
    
    @FieldAlias("area")
    private String varea;//地区：取客户的省+市
	
    @FieldAlias("corpid")
    private String pk_corp; // 渠道商主键
    
    @FieldAlias("corpnm")
    private String corpname; // 渠道商名称
    
    @FieldAlias("corpkid")
    private String pk_corpk; // 客户主键
    
    @FieldAlias("corpkna")
    private String corpkname; // 客户名称
    
    @FieldAlias("chname")
    public String chargedeptname;// 纳税人资格
    
    @FieldAlias("typemin")
    private String busitypemin; // 业务小类
    
    @FieldAlias("typeminm")
    private String vbusitypename; // 业务小类名称
    
    @FieldAlias("vccode")
    private String vcontcode; // 合同编码
    
    @FieldAlias("ntlmny")
    private DZFDouble ntotalmny; // 合同总金额
    
    @FieldAlias("bdate")
    private DZFDate dbegindate; // 开始日期

    @FieldAlias("edate")
    private DZFDate denddate; // 结束日期
    
    @FieldAlias("nmsmny")
    private DZFDouble nmservicemny; // 每月服务费
    
    @FieldAlias("ndemny")
    private DZFDouble ndeductmny;//扣款金额
    
    @FieldAlias("propor")
    private Integer ideductpropor;//扣款比例
    
    @FieldAlias("destatus")
    private Integer vdeductstatus;//加盟商合同状态  1：待审核；2：已审核；3：已驳回；4：服务到期；
    
    @FieldAlias("confreason")
    private String vconfreason;// 驳回原因
    
    @FieldAlias("tstp")
    private DZFDateTime tstamp;// 时间戳
    
    @FieldAlias("dr")
    private Integer dr; // 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts; // 时间戳
    
    @FieldAlias("conmemo")
    private String vconmemo; // 备注
    
    @FieldAlias("voper")
    private String voperator; // 经办人
    
    @FieldAlias("vopernm")
    private String vopername; // 经办人姓名
    
	@FieldAlias("balmny")
	private DZFDouble nbalance;//余额
	
    @FieldAlias("operatorid")
    private String coperatorid; // 制单人
    
    @FieldAlias("contcycle")
    private Integer icontractcycle;//合同周期
    
    @FieldAlias("chgcycle")
    private String vchargecycle; // 收款周期
    
    @FieldAlias("adviser")
    private String vadviser;//销售顾问
    
    @FieldAlias("cylnum")
    private Integer icyclenum; // 周期数（查询使用，不存库）
    
    @FieldAlias("nbmny")
    private DZFDouble nbookmny; // 账本费
    
    @FieldAlias("bperiod")
    private String vbeginperiod;//合同开始期间
    
    @FieldAlias("eperiod")
    private String vendperiod;//合同结束期间
    
    @FieldAlias("submitime")
    private DZFDateTime dsubmitime;//提交时间
    
    @FieldAlias("dedate")
    private DZFDate deductdata;//扣费日期
    
    @FieldAlias("ductime")
    private DZFDateTime deductime;//扣费时间
    
    @FieldAlias("pid")
    private String pk_packagedef; // 套餐主键
    
    @FieldAlias("salespromot")
    private String vsalespromot;//促销活动（数据展示，不存库）
    
    @FieldAlias("errmsg")
    private String verrmsg;//错误信息
    
	public String getVerrmsg() {
		return verrmsg;
	}

	public void setVerrmsg(String verrmsg) {
		this.verrmsg = verrmsg;
	}

	public String getVsalespromot() {
		return vsalespromot;
	}

	public void setVsalespromot(String vsalespromot) {
		this.vsalespromot = vsalespromot;
	}

	public String getPk_packagedef() {
		return pk_packagedef;
	}

	public void setPk_packagedef(String pk_packagedef) {
		this.pk_packagedef = pk_packagedef;
	}

	public DZFDateTime getDsubmitime() {
		return dsubmitime;
	}

	public void setDsubmitime(DZFDateTime dsubmitime) {
		this.dsubmitime = dsubmitime;
	}

	public DZFDateTime getDeductime() {
		return deductime;
	}

	public void setDeductime(DZFDateTime deductime) {
		this.deductime = deductime;
	}

	public Integer getIcontractcycle() {
		return icontractcycle;
	}

	public void setIcontractcycle(Integer icontractcycle) {
		this.icontractcycle = icontractcycle;
	}

	public DZFDouble getNbookmny() {
		return nbookmny;
	}

	public void setNbookmny(DZFDouble nbookmny) {
		this.nbookmny = nbookmny;
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

	public Integer getIcyclenum() {
		return icyclenum;
	}

	public void setIcyclenum(Integer icyclenum) {
		this.icyclenum = icyclenum;
	}

	public String getVarea() {
		return varea;
	}

	public void setVarea(String varea) {
		this.varea = varea;
	}

	public String getVchargecycle() {
		return vchargecycle;
	}

	public void setVchargecycle(String vchargecycle) {
		this.vchargecycle = vchargecycle;
	}

	public String getVadviser() {
		return vadviser;
	}

	public void setVadviser(String vadviser) {
		this.vadviser = vadviser;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public String getVconmemo() {
		return vconmemo;
	}

	public void setVconmemo(String vconmemo) {
		this.vconmemo = vconmemo;
	}

	public String getVoperator() {
		return voperator;
	}

	public void setVoperator(String voperator) {
		this.voperator = voperator;
	}

	public String getVopername() {
		return vopername;
	}

	public void setVopername(String vopername) {
		this.vopername = vopername;
	}

	public DZFDouble getNbalance() {
		return nbalance;
	}

	public void setNbalance(DZFDouble nbalance) {
		this.nbalance = nbalance;
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

	public String getPk_confrim() {
		return pk_confrim;
	}

	public void setPk_confrim(String pk_confrim) {
		this.pk_confrim = pk_confrim;
	}

	public DZFDateTime getTstamp() {
		return tstamp;
	}

	public void setTstamp(DZFDateTime tstamp) {
		this.tstamp = tstamp;
	}

	public String getVconfreason() {
		return vconfreason;
	}

	public void setVconfreason(String vconfreason) {
		this.vconfreason = vconfreason;
	}

	public DZFDouble getNmservicemny() {
		return nmservicemny;
	}

	public void setNmservicemny(DZFDouble nmservicemny) {
		this.nmservicemny = nmservicemny;
	}

	public Integer getVdeductstatus() {
		return vdeductstatus;
	}

	public void setVdeductstatus(Integer vdeductstatus) {
		this.vdeductstatus = vdeductstatus;
	}

	public Integer getIdeductpropor() {
		return ideductpropor;
	}

	public void setIdeductpropor(Integer ideductpropor) {
		this.ideductpropor = ideductpropor;
	}

	public String getPk_contract() {
		return pk_contract;
	}

	public void setPk_contract(String pk_contract) {
		this.pk_contract = pk_contract;
	}

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

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public String getCorpkname() {
		return corpkname;
	}

	public void setCorpkname(String corpkname) {
		this.corpkname = corpkname;
	}

	public String getChargedeptname() {
		return chargedeptname;
	}

	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
	}

	public String getBusitypemin() {
		return busitypemin;
	}

	public void setBusitypemin(String busitypemin) {
		this.busitypemin = busitypemin;
	}

	public String getVbusitypename() {
		return vbusitypename;
	}

	public void setVbusitypename(String vbusitypename) {
		this.vbusitypename = vbusitypename;
	}

	public String getVcontcode() {
		return vcontcode;
	}

	public void setVcontcode(String vcontcode) {
		this.vcontcode = vcontcode;
	}

	public DZFDouble getNtotalmny() {
		return ntotalmny;
	}

	public void setNtotalmny(DZFDouble ntotalmny) {
		this.ntotalmny = ntotalmny;
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

	public DZFDate getDeductdata() {
		return deductdata;
	}

	public void setDeductdata(DZFDate deductdata) {
		this.deductdata = deductdata;
	}

	public DZFDouble getNdeductmny() {
		return ndeductmny;
	}

	public void setNdeductmny(DZFDouble ndeductmny) {
		this.ndeductmny = ndeductmny;
	}

	@Override
	public String getPKFieldName() {
		return "pk_confrim";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_contract";
	}

}
