package com.dzf.model.demp.channel;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;

/**
 * 合同确认VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class ContractConfrimVO extends SuperVO {

	private static final long serialVersionUID = 8799494318014396487L;
	
    @FieldAlias("contractid")
    private String pk_contract; // 主键
	
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
    
    @FieldAlias("chgcycle")
    private String vchargecycle; // 收款周期

    @FieldAlias("cylnum")
    private Integer icyclenum; // 周期数
    
    @FieldAlias("dedate")
    private DZFDate deductdata;//扣款日期
    
    @FieldAlias("ndemny")
    private DZFDate ndeductmny;//扣款金额
    
    @FieldAlias("confstatus")
    private Integer vconfstatus;//合同状态
    
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

	public String getVchargecycle() {
		return vchargecycle;
	}

	public void setVchargecycle(String vchargecycle) {
		this.vchargecycle = vchargecycle;
	}

	public Integer getIcyclenum() {
		return icyclenum;
	}

	public void setIcyclenum(Integer icyclenum) {
		this.icyclenum = icyclenum;
	}

	public DZFDate getDeductdata() {
		return deductdata;
	}

	public void setDeductdata(DZFDate deductdata) {
		this.deductdata = deductdata;
	}

	public DZFDate getNdeductmny() {
		return ndeductmny;
	}

	public void setNdeductmny(DZFDate ndeductmny) {
		this.ndeductmny = ndeductmny;
	}

	public Integer getVconfstatus() {
		return vconfstatus;
	}

	public void setVconfstatus(Integer vconfstatus) {
		this.vconfstatus = vconfstatus;
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
