package com.dzf.model.demp.contract;

import com.dzf.model.pub.MultSuperVO;
import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 合同管理
 * 
 * @author dzf
 * 
 */
@SuppressWarnings("serial")
public class ContractVO extends MultSuperVO {

    @FieldAlias("contractid")
    private String pk_contract; // 主键

    @FieldAlias("history")
    private String pk_history; // 历史主键

    @FieldAlias("flag")
    private String isflag; // 是否最新记录

    @FieldAlias("corpid")
    private String pk_corp; // 所属分部

    @FieldAlias("corpkid")
    private String pk_corpk; // 客户主键

    @FieldAlias("corpkco")
    private String corpkcode; // 客户编码（查询用）

    @FieldAlias("corpkna")
    private String corpkname; // 客户名称查询用

    @FieldAlias("corpna")
    private String corpname; // 所属分部名称查询用

    @FieldAlias("typemax")
    private String busitypemax; // 业务大类

    @FieldAlias("typemin")
    private String busitypemin; // 业务小类

    @FieldAlias("vproductname")
    private String vproductname; // 业务大类名称查询用

    @FieldAlias("vbusitypename")
    private String vbusitypename; // 业务小类名称查询用

    @FieldAlias("bdate")
    private DZFDate dbegindate; // 开始日期

    @FieldAlias("edate")
    private DZFDate denddate; // 结束日期

    @FieldAlias("nmsmny")
    private DZFDouble nmservicemny; // 每月服务费

    @FieldAlias("chgcycle")
    private String vchargecycle; // 收款周期

    @FieldAlias("cylnum")
    private Integer icyclenum; // 周期数

    @FieldAlias("ntlmny")
    private DZFDouble ntotalmny; // 合同总金额

    @FieldAlias("nbmny")
    private DZFDouble nbookmny; // 账本费

    @FieldAlias("memo")
    private String vmemo; // 备注/说明

    @FieldAlias("days")
    private Integer idays; // 天数

    @FieldAlias("nfmny")
    private DZFDouble nfirstmny; // 第一次付款/首款

    @FieldAlias("nlmny")
    private DZFDouble nlastmny; // 尾款/剩余费用

    @FieldAlias("dldate")
    private DZFDate dlastpaydate; // 尾款付款日期

    @FieldAlias("vbcontent")
    private String vbusicontent; // 办理业务内容

    @FieldAlias("dfdate")
    private DZFDate dfirstpaydate; // 第一次付款日期

    @FieldAlias("nsmny")
    private DZFDouble nsecondmny; // 第二次付款

    @FieldAlias("dsdate")
    private DZFDate dseconddate; // 第二次付款日期

    @FieldAlias("ntmny")
    private DZFDouble nthreemny; // 第三次付款

    @FieldAlias("dtdate")
    private DZFDate dthreedate; // 第三次付款日期

    @FieldAlias("vsment")
    private String vsupplement; // 补充约定

    @FieldAlias("matter")
    private String vmatter; // 约定事项补充

    @FieldAlias("paryb")
    private String vparyb; // 乙方名称

    @FieldAlias("vctrep")
    private String vcontentrep; // 内容及要求

    @FieldAlias("finishdate")
    private DZFDate dfinishdate; // 完成时间

    @FieldAlias("drepdate")
    private DZFDate dreportdate; // 出具审计报告日期

    @FieldAlias("vaudittent")
    private String vauditcontent; // 审计内容

    @FieldAlias("vaudcope")
    private String vauditscope; // 审计范围

    @FieldAlias("vauditpose")
    private String vauditpurpose; // 审计目的

    @FieldAlias("number")
    private Integer inumber; // 著作权个数

    @FieldAlias("nagmny")
    private DZFDouble nagencymny; // 代理费

    @FieldAlias("dlxs")
    private String vproxytype; // 代理形式

    @FieldAlias("wtbl")
    private String vhandleplace; // 委托办理地点

    @FieldAlias("dlsx")
    private String vagencymatters; // 代理事项

    @FieldAlias("dlnr")
    private String vagencycontent; // 代理内容

    @FieldAlias("ldbdate")
    private String dldbegindate; // 劳动开始时间

    @FieldAlias("ldedate")
    private String dldenddate; // 劳动开始时间

    @FieldAlias("jsbdw")
    private String vpaycompany; // 代缴社保单位

    @FieldAlias("sjyrdw")
    private String vusecompany; // 实际用人单位

    @FieldAlias("fwbdate")
    private DZFDate dserbegindate; // 服务开始日期

    @FieldAlias("fwedate")
    private DZFDate dserenddate; // 服务结束日期

    @FieldAlias("operatorid")
    private String coperatorid; // 制单人

    @FieldAlias("zddate")
    private DZFDate doperatedate; // 制单日期

    @FieldAlias("bzddate")
    private DZFDate bdoperatedate; // 制单开始日期查询用

    @FieldAlias("ezddate")
    private DZFDate edoperatedate; // 制单结束日期查询用

    @FieldAlias("status")
    private Integer vstatus; // 合同状态 0：未审核；1：审核通过；2：执行中；3：终止； 4：结束；5:待审批；

    @FieldAlias("dr")
    private Integer dr; // 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts; // 时间戳

    @FieldAlias("vccode")
    private String vcontcode; // 合同编码

    @FieldAlias("istype")
    private Integer icosttype; // 合同类型(0：代理记账合同；1：增值服务合同；2：无合同应收；)

    @FieldAlias("ddate")
    private DZFDate dreceivedate; // 收款时间

    @FieldAlias("nmny")
    private DZFDouble nreceivemny; // 收款金额

    @FieldAlias("isname")
    private String icostname; // 费用名称

    @FieldAlias("contacts")
    private String vcontacts; // 联系人

    @FieldAlias("contactinfo")
    private String vcontactinfo; // 联系方式

    @FieldAlias("queryDate")
    private String queryDate; // 用于查询当月的合同

    @FieldAlias("pkconb")
    private String pk_contract_b; // 合同子表主键

    @FieldAlias("hbcm2")
    private String hthcharge; // 本次收款

    @FieldAlias("vcharge")
    private String vothercharge; // 其他费用

    @FieldAlias("othermny")
    private DZFDouble nothermny; // 其他费用金额

    @FieldAlias("register")
    private DZFBoolean isregister; // 是否业务登记

    private DZFBoolean isywfinish; // 业务流程是否完成

    @FieldAlias("isskfi")
    private DZFBoolean isskfinish; // 收款是否完成

    private DZFBoolean ys; // 应收
    private DZFBoolean ljys; // 累计收款
    private DZFBoolean qf; // 欠费

    @FieldAlias("qc")
    private DZFBoolean isqc; // 是否期初

    @FieldAlias("modify")
    private Integer ismodify; // 0 新增 1修改 2续签 3变更

    @FieldAlias("dchgdate")
    private DZFDate dchangedate; // 合同变更开始日期

    private Integer bodycount; // 表体数，合同导入用
    private DZFDouble nunyhys; // 期初已收金额，合同导入用

    @FieldAlias("hyhm2")
    private DZFDouble nyhmny; // 期初优惠金额，合同导入用

    private Integer dataSource;

    // v 2.0 6-29上线
    @FieldAlias("qm")
    private DZFBoolean isqm; // 是否期末收款

    @FieldAlias("xq")
    private DZFBoolean isxq; // 是否续签

    @FieldAlias("feetypeid")
    private String pk_feetype; // 费用类型主键

    @FieldAlias("istypename")
    private String icosttypename; // 合同类型名称

    @FieldAlias("booksy")
    private DZFDouble booksy; // 账本费实收

    @FieldAlias("inum")
    private Integer inum; // 到期提醒次数

    @FieldAlias("pk_xq")
    private String pk_xuqian; // 续签来源

    @FieldAlias("pk_order_b")
    private String pk_order_b; // 来源订单行

    @FieldAlias("pk_order")
    private String pk_order; // 来源订单

    @FieldAlias("termdate")
    private DZFDate termdate; // 终止日期

    @FieldAlias("operatorpsn")
    private String operatorpsn; // 经办人

    @FieldAlias("balancesum")
    private DZFDouble balancesum; // 余额合计

    private String jbrname; // 经办人名称

    private String lrrname; // 录入人名称

    @FieldAlias("ctype")
    private Integer chargetype; // 收款类型: 1 合同收款 9应收款 5 合同退款 10应收退款

    @FieldAlias("hxhye")
    private DZFDouble hxhyemny;// 核销后余额（代缴使用）

    @FieldAlias("contwsh")
    private DZFBoolean iscontwsh;// 是否包含未审核单据

    @FieldAlias("qrytype")
    private Integer querytype;// 查询类型

    @FieldAlias("sendmsg")
    private Integer isendmsg; // 到期消息

    @FieldAlias("lmpsnid")
    private String lastmodifypsnid; // 最后修改人

    @FieldAlias("lmpsn")
    private String lastmodifypsn; // 最后修改人名称

    @FieldAlias("lsdate")
    private DZFDate lastmodifydate; // 最后修改日期

    @FieldAlias("aupsnid")
    private String auditpsnid; // 最审核人

    @FieldAlias("aupsn")
    private String auditpsn; // 最审核人名称

    @FieldAlias("audate")
    private DZFDate auditdate; // 最后审核日期

    @FieldAlias("clinkm")
    private String custlinkman; // 客户联系人

    @FieldAlias("clinkp")
    private String custlinkphone;// 客户联系方式

    @FieldAlias("sign")
    private DZFBoolean issign;// 是否签订合同

    @FieldAlias("signdate")
    private DZFDate dsigndate;// 签订合同日期

    @FieldAlias("tstp")
    private DZFDateTime tstamp;// 时间戳

    @FieldAlias("contstp")
    private DZFDateTime contstamp;// 合同时间戳（数据库不做存储，仅做数据传递）

    private SuperVO[] bodys;

    private DZFDouble summny;// 增值服务合同的导入,做总金额校验

    private String vbilltype;// 单据类型

    @FieldAlias("preaupsnid")
    private String preauditpsnid;// 待审核人；

    private String senderman;// 送审人
    @FieldAlias("setid")
    private String pk_approveset;// 审核流程主ID
    @FieldAlias("setbid")
    private String pk_approveset_b;// 审核流程子ID

    private String pk_workflow;// 审批任务ID

    @FieldAlias("pronote")
    private String vapprovenote;// 审批批语

    @FieldAlias("reason")
    private String reason;// 合同终止或结束原因说明

    private Integer approvestatus;
    
    @FieldAlias("skstatus")
    private Integer vskstatus; // 收款状态 1：未收款；2：部分收款；3：全部收款；
    
    @FieldAlias("pid")
    private String pk_packagedef; // 套餐主键
    
    @FieldAlias("bperiod")
    private String vbeginperiod;//合同开始期间
    
    @FieldAlias("eperiod")
    private String vendperiod;//合同结束期间
    
    @FieldAlias("adviser")
    private String vadviser;//销售顾问
    
    @FieldAlias("chname")
    private String chargedeptname;//纳税人资格
    
    @FieldAlias("contcycle")
    private Integer icontractcycle;//合同周期
    
    @FieldAlias("conttype")
    private Integer icontracttype;//合同类型  1或空：普通合同；2：渠道商合同；
    
    @FieldAlias("destatus")
    private Integer vdeductstatus;//加盟商合同状态  1：待审核；2：已审核；3：已驳回；4：服务到期；
    
    @FieldAlias("confreason")
    private String vconfreason;// 驳回原因
    
    @FieldAlias("submitime")
    private DZFDateTime dsubmitime;//提交时间

	public DZFDateTime getDsubmitime() {
		return dsubmitime;
	}

	public void setDsubmitime(DZFDateTime dsubmitime) {
		this.dsubmitime = dsubmitime;
	}

	public Integer getVdeductstatus() {
		return vdeductstatus;
	}

	public void setVdeductstatus(Integer vdeductstatus) {
		this.vdeductstatus = vdeductstatus;
	}

	public String getVconfreason() {
		return vconfreason;
	}

	public void setVconfreason(String vconfreason) {
		this.vconfreason = vconfreason;
	}

	public Integer getIcontracttype() {
		return icontracttype;
	}

	public void setIcontracttype(Integer icontracttype) {
		this.icontracttype = icontracttype;
	}

	public String getPk_packagedef() {
		return pk_packagedef;
	}

	public void setPk_packagedef(String pk_packagedef) {
		this.pk_packagedef = pk_packagedef;
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

	public String getVadviser() {
		return vadviser;
	}

	public void setVadviser(String vadviser) {
		this.vadviser = vadviser;
	}

	public String getChargedeptname() {
		return chargedeptname;
	}

	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
	}

	public Integer getIcontractcycle() {
		return icontractcycle;
	}

	public void setIcontractcycle(Integer icontractcycle) {
		this.icontractcycle = icontractcycle;
	}

	public Integer getVskstatus() {
        return vskstatus;
    }

    public void setVskstatus(Integer vskstatus) {
        this.vskstatus = vskstatus;
    }

    public String getPk_approveset() {
        return pk_approveset;
    }

    public void setPk_approveset(String pk_approveset) {
        this.pk_approveset = pk_approveset;
    }

    public Integer getApprovestatus() {
        return approvestatus;
    }

    public void setApprovestatus(Integer approvestatus) {
        this.approvestatus = approvestatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPk_workflow() {
        return pk_workflow;
    }

    public void setPk_workflow(String pk_workflow) {
        this.pk_workflow = pk_workflow;
    }

    public String getVapprovenote() {
        return vapprovenote;
    }

    public void setVapprovenote(String vapprovenote) {
        this.vapprovenote = vapprovenote;
    }

    public String getPk_approveset_b() {
        return pk_approveset_b;
    }

    public void setPk_approveset_b(String pk_approveset_b) {
        this.pk_approveset_b = pk_approveset_b;
    }

    public String getSenderman() {
        return senderman;
    }

    public void setSenderman(String senderman) {
        this.senderman = senderman;
    }

    public String getPreauditpsnid() {
        return preauditpsnid;
    }

    public void setPreauditpsnid(String preauditpsnid) {
        this.preauditpsnid = preauditpsnid;
    }

    public String getVbilltype() {
        return vbilltype;
    }

    public void setVbilltype(String vbilltype) {
        this.vbilltype = vbilltype;
    }

    public DZFDouble getSummny() {
        return summny;
    }

    public void setSummny(DZFDouble summny) {
        this.summny = summny;
    }

    public SuperVO[] getBodys() {
        return bodys;
    }

    public void setBodys(SuperVO[] bodys) {
        this.bodys = bodys;
    }

    public DZFDateTime getContstamp() {
        return contstamp;
    }

    public void setContstamp(DZFDateTime contstamp) {
        this.contstamp = contstamp;
    }

    public DZFDateTime getTstamp() {
        return tstamp;
    }

    public void setTstamp(DZFDateTime tstamp) {
        this.tstamp = tstamp;
    }

    public DZFBoolean getIssign() {
        return issign;
    }

    public void setIssign(DZFBoolean issign) {
        this.issign = issign;
    }

    public DZFDate getDsigndate() {
        return dsigndate;
    }

    public void setDsigndate(DZFDate dsigndate) {
        this.dsigndate = dsigndate;
    }

    public String getCustlinkman() {
        return custlinkman;
    }

    public void setCustlinkman(String custlinkman) {
        this.custlinkman = custlinkman;
    }

    public String getCustlinkphone() {
        return custlinkphone;
    }

    public void setCustlinkphone(String custlinkphone) {
        this.custlinkphone = custlinkphone;
    }

    public String getLastmodifypsn() {
        return lastmodifypsn;
    }

    public void setLastmodifypsn(String lastmodifypsn) {
        this.lastmodifypsn = lastmodifypsn;
    }

    public String getAuditpsn() {
        return auditpsn;
    }

    public void setAuditpsn(String auditpsn) {
        this.auditpsn = auditpsn;
    }

    public String getLastmodifypsnid() {
        return lastmodifypsnid;
    }

    public void setLastmodifypsnid(String lastmodifypsnid) {
        this.lastmodifypsnid = lastmodifypsnid;
    }

    public DZFDate getLastmodifydate() {
        return lastmodifydate;
    }

    public void setLastmodifydate(DZFDate lastmodifydate) {
        this.lastmodifydate = lastmodifydate;
    }

    public String getAuditpsnid() {
        return auditpsnid;
    }

    public void setAuditpsnid(String auditpsnid) {
        this.auditpsnid = auditpsnid;
    }

    public DZFDate getAuditdate() {
        return auditdate;
    }

    public void setAuditdate(DZFDate auditdate) {
        this.auditdate = auditdate;
    }

    public Integer getIsendmsg() {
        return isendmsg;
    }

    public void setIsendmsg(Integer isendmsg) {
        this.isendmsg = isendmsg;
    }

    public String getPk_contract() {
        return pk_contract;
    }

    public void setPk_contract(String pk_contract) {
        this.pk_contract = pk_contract;
    }

    public String getPk_history() {
        return pk_history;
    }

    public void setPk_history(String pk_history) {
        this.pk_history = pk_history;
    }

    public String getIsflag() {
        return isflag;
    }

    public void setIsflag(String isflag) {
        this.isflag = isflag;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getPk_corpk() {
        return pk_corpk;
    }

    public void setPk_corpk(String pk_corpk) {
        this.pk_corpk = pk_corpk;
    }

    public String getCorpkcode() {
        return corpkcode;
    }

    public void setCorpkcode(String corpkcode) {
        this.corpkcode = corpkcode;
    }

    public String getCorpkname() {
        return corpkname;
    }

    public void setCorpkname(String corpkname) {
        this.corpkname = corpkname;
    }

    public String getCorpname() {
        return corpname;
    }

    public void setCorpname(String corpname) {
        this.corpname = corpname;
    }

    public String getBusitypemax() {
        return busitypemax;
    }

    public void setBusitypemax(String busitypemax) {
        this.busitypemax = busitypemax;
    }

    public String getBusitypemin() {
        return busitypemin;
    }

    public void setBusitypemin(String busitypemin) {
        this.busitypemin = busitypemin;
    }

    public String getVproductname() {
        return vproductname;
    }

    public void setVproductname(String vproductname) {
        this.vproductname = vproductname;
    }

    public String getVbusitypename() {
        return vbusitypename;
    }

    public void setVbusitypename(String vbusitypename) {
        this.vbusitypename = vbusitypename;
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

    public DZFDouble getNmservicemny() {
        return nmservicemny;
    }

    public void setNmservicemny(DZFDouble nmservicemny) {
        this.nmservicemny = nmservicemny;
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

    public DZFDouble getNtotalmny() {
        return ntotalmny;
    }

    public void setNtotalmny(DZFDouble ntotalmny) {
        this.ntotalmny = ntotalmny;
    }

    public DZFDouble getNbookmny() {
        return nbookmny;
    }

    public void setNbookmny(DZFDouble nbookmny) {
        this.nbookmny = nbookmny;
    }

    public String getVmemo() {
        return vmemo;
    }

    public void setVmemo(String vmemo) {
        this.vmemo = vmemo;
    }

    public Integer getIdays() {
        return idays;
    }

    public void setIdays(Integer idays) {
        this.idays = idays;
    }

    public DZFDouble getNfirstmny() {
        return nfirstmny;
    }

    public void setNfirstmny(DZFDouble nfirstmny) {
        this.nfirstmny = nfirstmny;
    }

    public DZFDouble getNlastmny() {
        return nlastmny;
    }

    public void setNlastmny(DZFDouble nlastmny) {
        this.nlastmny = nlastmny;
    }

    public DZFDate getDlastpaydate() {
        return dlastpaydate;
    }

    public void setDlastpaydate(DZFDate dlastpaydate) {
        this.dlastpaydate = dlastpaydate;
    }

    public String getVbusicontent() {
        return vbusicontent;
    }

    public void setVbusicontent(String vbusicontent) {
        this.vbusicontent = vbusicontent;
    }

    public DZFDate getDfirstpaydate() {
        return dfirstpaydate;
    }

    public void setDfirstpaydate(DZFDate dfirstpaydate) {
        this.dfirstpaydate = dfirstpaydate;
    }

    public DZFDouble getNsecondmny() {
        return nsecondmny;
    }

    public void setNsecondmny(DZFDouble nsecondmny) {
        this.nsecondmny = nsecondmny;
    }

    public DZFDate getDseconddate() {
        return dseconddate;
    }

    public void setDseconddate(DZFDate dseconddate) {
        this.dseconddate = dseconddate;
    }

    public DZFDouble getNthreemny() {
        return nthreemny;
    }

    public void setNthreemny(DZFDouble nthreemny) {
        this.nthreemny = nthreemny;
    }

    public DZFDate getDthreedate() {
        return dthreedate;
    }

    public void setDthreedate(DZFDate dthreedate) {
        this.dthreedate = dthreedate;
    }

    public String getVsupplement() {
        return vsupplement;
    }

    public void setVsupplement(String vsupplement) {
        this.vsupplement = vsupplement;
    }

    public String getVmatter() {
        return vmatter;
    }

    public void setVmatter(String vmatter) {
        this.vmatter = vmatter;
    }

    public String getVparyb() {
        return vparyb;
    }

    public void setVparyb(String vparyb) {
        this.vparyb = vparyb;
    }

    public String getVcontentrep() {
        return vcontentrep;
    }

    public void setVcontentrep(String vcontentrep) {
        this.vcontentrep = vcontentrep;
    }

    public DZFDate getDfinishdate() {
        return dfinishdate;
    }

    public void setDfinishdate(DZFDate dfinishdate) {
        this.dfinishdate = dfinishdate;
    }

    public DZFDate getDreportdate() {
        return dreportdate;
    }

    public void setDreportdate(DZFDate dreportdate) {
        this.dreportdate = dreportdate;
    }

    public String getVauditcontent() {
        return vauditcontent;
    }

    public void setVauditcontent(String vauditcontent) {
        this.vauditcontent = vauditcontent;
    }

    public String getVauditscope() {
        return vauditscope;
    }

    public void setVauditscope(String vauditscope) {
        this.vauditscope = vauditscope;
    }

    public String getVauditpurpose() {
        return vauditpurpose;
    }

    public void setVauditpurpose(String vauditpurpose) {
        this.vauditpurpose = vauditpurpose;
    }

    public Integer getInumber() {
        return inumber;
    }

    public void setInumber(Integer inumber) {
        this.inumber = inumber;
    }

    public DZFDouble getNagencymny() {
        return nagencymny;
    }

    public void setNagencymny(DZFDouble nagencymny) {
        this.nagencymny = nagencymny;
    }

    public String getVproxytype() {
        return vproxytype;
    }

    public void setVproxytype(String vproxytype) {
        this.vproxytype = vproxytype;
    }

    public String getVhandleplace() {
        return vhandleplace;
    }

    public void setVhandleplace(String vhandleplace) {
        this.vhandleplace = vhandleplace;
    }

    public String getVagencymatters() {
        return vagencymatters;
    }

    public void setVagencymatters(String vagencymatters) {
        this.vagencymatters = vagencymatters;
    }

    public String getVagencycontent() {
        return vagencycontent;
    }

    public void setVagencycontent(String vagencycontent) {
        this.vagencycontent = vagencycontent;
    }

    public String getDldbegindate() {
        return dldbegindate;
    }

    public void setDldbegindate(String dldbegindate) {
        this.dldbegindate = dldbegindate;
    }

    public String getDldenddate() {
        return dldenddate;
    }

    public void setDldenddate(String dldenddate) {
        this.dldenddate = dldenddate;
    }

    public String getVpaycompany() {
        return vpaycompany;
    }

    public void setVpaycompany(String vpaycompany) {
        this.vpaycompany = vpaycompany;
    }

    public String getVusecompany() {
        return vusecompany;
    }

    public void setVusecompany(String vusecompany) {
        this.vusecompany = vusecompany;
    }

    public DZFDate getDserbegindate() {
        return dserbegindate;
    }

    public void setDserbegindate(DZFDate dserbegindate) {
        this.dserbegindate = dserbegindate;
    }

    public DZFDate getDserenddate() {
        return dserenddate;
    }

    public void setDserenddate(DZFDate dserenddate) {
        this.dserenddate = dserenddate;
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

    public DZFDate getBdoperatedate() {
        return bdoperatedate;
    }

    public void setBdoperatedate(DZFDate bdoperatedate) {
        this.bdoperatedate = bdoperatedate;
    }

    public DZFDate getEdoperatedate() {
        return edoperatedate;
    }

    public void setEdoperatedate(DZFDate edoperatedate) {
        this.edoperatedate = edoperatedate;
    }

    public Integer getVstatus() {
        return vstatus;
    }

    public void setVstatus(Integer vstatus) {
        this.vstatus = vstatus;
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

    public String getVcontcode() {
        return vcontcode;
    }

    public void setVcontcode(String vcontcode) {
        this.vcontcode = vcontcode;
    }

    public Integer getIcosttype() {
        return icosttype;
    }

    public void setIcosttype(Integer icosttype) {
        this.icosttype = icosttype;
    }

    public DZFDate getDreceivedate() {
        return dreceivedate;
    }

    public void setDreceivedate(DZFDate dreceivedate) {
        this.dreceivedate = dreceivedate;
    }

    public DZFDouble getNreceivemny() {
        return nreceivemny;
    }

    public void setNreceivemny(DZFDouble nreceivemny) {
        this.nreceivemny = nreceivemny;
    }

    public String getIcostname() {
        return icostname;
    }

    public void setIcostname(String icostname) {
        this.icostname = icostname;
    }

    public String getVcontacts() {
        return vcontacts;
    }

    public void setVcontacts(String vcontacts) {
        this.vcontacts = vcontacts;
    }

    public String getVcontactinfo() {
        return vcontactinfo;
    }

    public void setVcontactinfo(String vcontactinfo) {
        this.vcontactinfo = vcontactinfo;
    }

    public String getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(String queryDate) {
        this.queryDate = queryDate;
    }

    public String getPk_contract_b() {
        return pk_contract_b;
    }

    public void setPk_contract_b(String pk_contract_b) {
        this.pk_contract_b = pk_contract_b;
    }

    public String getHthcharge() {
        return hthcharge;
    }

    public void setHthcharge(String hthcharge) {
        this.hthcharge = hthcharge;
    }

    public String getVothercharge() {
        return vothercharge;
    }

    public void setVothercharge(String vothercharge) {
        this.vothercharge = vothercharge;
    }

    public DZFDouble getNothermny() {
        return nothermny;
    }

    public void setNothermny(DZFDouble nothermny) {
        this.nothermny = nothermny;
    }

    public DZFBoolean getIsregister() {
        return isregister;
    }

    public void setIsregister(DZFBoolean isregister) {
        this.isregister = isregister;
    }

    public DZFBoolean getIsywfinish() {
        return isywfinish;
    }

    public void setIsywfinish(DZFBoolean isywfinish) {
        this.isywfinish = isywfinish;
    }

    public DZFBoolean getIsskfinish() {
        return isskfinish;
    }

    public void setIsskfinish(DZFBoolean isskfinish) {
        this.isskfinish = isskfinish;
    }

    public DZFBoolean getYs() {
        return ys;
    }

    public void setYs(DZFBoolean ys) {
        this.ys = ys;
    }

    public DZFBoolean getLjys() {
        return ljys;
    }

    public void setLjys(DZFBoolean ljys) {
        this.ljys = ljys;
    }

    public DZFBoolean getQf() {
        return qf;
    }

    public void setQf(DZFBoolean qf) {
        this.qf = qf;
    }

    public DZFBoolean getIsqc() {
        return isqc;
    }

    public void setIsqc(DZFBoolean isqc) {
        this.isqc = isqc;
    }

    public Integer getIsmodify() {
        return ismodify;
    }

    public void setIsmodify(Integer ismodify) {
        this.ismodify = ismodify;
    }

    public DZFDate getDchangedate() {
        return dchangedate;
    }

    public void setDchangedate(DZFDate dchangedate) {
        this.dchangedate = dchangedate;
    }

    public Integer getBodycount() {
        return bodycount;
    }

    public void setBodycount(Integer bodycount) {
        this.bodycount = bodycount;
    }

    public DZFDouble getNunyhys() {
        return nunyhys;
    }

    public void setNunyhys(DZFDouble nunyhys) {
        this.nunyhys = nunyhys;
    }

    public DZFDouble getNyhmny() {
        return nyhmny;
    }

    public void setNyhmny(DZFDouble nyhmny) {
        this.nyhmny = nyhmny;
    }

    public Integer getDataSource() {
        return dataSource;
    }

    public void setDataSource(Integer dataSource) {
        this.dataSource = dataSource;
    }

    public DZFBoolean getIsqm() {
        return isqm;
    }

    public void setIsqm(DZFBoolean isqm) {
        this.isqm = isqm;
    }

    public DZFBoolean getIsxq() {
        return isxq;
    }

    public void setIsxq(DZFBoolean isxq) {
        this.isxq = isxq;
    }

    public String getPk_feetype() {
        return pk_feetype;
    }

    public void setPk_feetype(String pk_feetype) {
        this.pk_feetype = pk_feetype;
    }

    public String getIcosttypename() {
        return icosttypename;
    }

    public void setIcosttypename(String icosttypename) {
        this.icosttypename = icosttypename;
    }

    public DZFDouble getBooksy() {
        return booksy;
    }

    public void setBooksy(DZFDouble booksy) {
        this.booksy = booksy;
    }

    public Integer getInum() {
        return inum;
    }

    public void setInum(Integer inum) {
        this.inum = inum;
    }

    public String getPk_xuqian() {
        return pk_xuqian;
    }

    public void setPk_xuqian(String pk_xuqian) {
        this.pk_xuqian = pk_xuqian;
    }

    public String getPk_order_b() {
        return pk_order_b;
    }

    public void setPk_order_b(String pk_order_b) {
        this.pk_order_b = pk_order_b;
    }

    public String getPk_order() {
        return pk_order;
    }

    public void setPk_order(String pk_order) {
        this.pk_order = pk_order;
    }

    public DZFDate getTermdate() {
        return termdate;
    }

    public void setTermdate(DZFDate termdate) {
        this.termdate = termdate;
    }

    public String getOperatorpsn() {
        return operatorpsn;
    }

    public void setOperatorpsn(String operatorpsn) {
        this.operatorpsn = operatorpsn;
    }

    public DZFDouble getBalancesum() {
        return balancesum;
    }

    public void setBalancesum(DZFDouble balancesum) {
        this.balancesum = balancesum;
    }

    public String getJbrname() {
        return jbrname;
    }

    public void setJbrname(String jbrname) {
        this.jbrname = jbrname;
    }

    public String getLrrname() {
        return lrrname;
    }

    public void setLrrname(String lrrname) {
        this.lrrname = lrrname;
    }

    public Integer getChargetype() {
        return chargetype;
    }

    public void setChargetype(Integer chargetype) {
        this.chargetype = chargetype;
    }

    public DZFDouble getHxhyemny() {
        return hxhyemny;
    }

    public void setHxhyemny(DZFDouble hxhyemny) {
        this.hxhyemny = hxhyemny;
    }

    public DZFBoolean getIscontwsh() {
        return iscontwsh;
    }

    public void setIscontwsh(DZFBoolean iscontwsh) {
        this.iscontwsh = iscontwsh;
    }

    public Integer getQuerytype() {
        return querytype;
    }

    public void setQuerytype(Integer querytype) {
        this.querytype = querytype;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_contract";
    }

    @Override
    public String getTableName() {
        return "ynt_contract";
    }

    @Override
    public String[] getTableCodes() {
        return new String[] { "ynt_contract_b", "ynt_contract_b2" };
    }
}
