package com.dzf.model.channel.contract;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 合同确认VO
 * 
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class ContractConfrimVO extends SuperVO {

	private static final long serialVersionUID = 8799494318014396487L;

//	@FieldAlias("operatorid")
//	private String coperatorid; // 审核人
//
//	@FieldAlias("chgcycle")
//	private String vchargecycle; // 收款周期 （此字段已废弃）（直接删除）
//
//	@FieldAlias("cylnum")
//	private Integer icyclenum; // 周期数（直接删除）
//
//	@FieldAlias("typemin")
//	private String busitypemin; // 业务小类（直接删除）
//
//	@FieldAlias("sourid")
//	private String pk_source;// 补提交的合同来源主键（直接删除）
	
//	@FieldAlias("conmemo")
//	private String vconmemo; // 审核备注（无用，直接删除）
	
	// 与原合同重复字段 begin*************************

	@FieldAlias("contractid")
	private String pk_contract; // 合同主键

	@FieldAlias("corpid")
	private String pk_corp; // 渠道商主键

	@FieldAlias("corpkid")
	private String pk_corpk; // 客户主键

	@FieldAlias("destatus")
	private Integer vdeductstatus;// 加盟商合同合同状态：0：待提交；1：已审核；5:待审核： 
									// 7：拒绝审核；8：服务到期；9：已终止；10：已作废；

	@FieldAlias("status")
	private Integer vstatus; // 合同状态
								// 0：待提交；1：审核通过；5:待审批；7：已驳回；8：服务到期；9：已终止（加盟商合同）；10：已作废（加盟商合同）；

	// 与原合同重复字段 end***************************

	// 取原合同或缓存字段 begin*************************

	@FieldAlias("area")
	private String varea;// 地区：取客户的省+市 （不存库，取客户缓存）

	@FieldAlias("corpnm")
	private String corpname; // 渠道商名称（不存库，取客户缓存）

	@FieldAlias("corpkna")
	private String corpkname; // 客户名称（不存库，取客户缓存）
	
	@FieldAlias("vccode")
	private String vcontcode; // 合同编码（不存库，取原合同）

	@FieldAlias("pid")
	private String pk_packagedef; // 套餐主键（不存库，取原合同）

	@FieldAlias("isncust")
	private DZFBoolean isncust;// 是否存量客户（不存库，取原合同）

	private DZFBoolean isnconfirm;// 未确定服务期限（Y是未确定）（不存库，取原合同）

	@FieldAlias("chname")
	public String chargedeptname;// 纳税人资格（不存库，取原合同）

	@FieldAlias("typeminm")
	private String vbusitypename; // 业务小类名称（不存库，取原合同）

	@FieldAlias("ntlmny")
	private DZFDouble ntotalmny; // 合同总金额（不存库，取原合同）

	@FieldAlias("nbmny")
	private DZFDouble nbookmny; // 账本费（不存库，取原合同）

	@FieldAlias("nmsmny")
	private DZFDouble nmservicemny; // 每月服务费（不存库，取原合同）

	@FieldAlias("contcycle")
	private Integer icontractcycle;// 合同周期（不存库，取原合同）

	@FieldAlias("recycle")
	private Integer ireceivcycle;// 收款周期（不存库，取原合同）

	@FieldAlias("submitime")
	private DZFDateTime dsubmitime;// 提交时间（不存库，取原合同）

	@FieldAlias("bperiod")
	private String vbeginperiod;// 合同开始期间（不存库，取原合同）

	@FieldAlias("eperiod")
	private String vendperiod;// 合同结束期间（不存库，取原合同）
	
	@FieldAlias("bdate")
	private DZFDate dbegindate; // 开始日期 （不存库，取原合同）合同变更使用

	@FieldAlias("edate")
	private DZFDate denddate; // 结束日期（不存库，取原合同）合同变更使用
	
	@FieldAlias("signdate")
	private DZFDate dsigndate;// 签订合同日期（直接删除）

	// 补单字段begin：
	@FieldAlias("cperiod")
	private String vchangeperiod;// 补提交的变更期间（不存库，取原合同）

	@FieldAlias("pstatus")
	private Integer patchstatus;// 加盟合同类型（null正常合同；1：被补提交的合同；2：补提交的合同；3：变更合同）（不存库，取原合同）
	// 补单字段end
	
	@FieldAlias("confreason")
	private String vconfreason;// 驳回原因（不存库，取原合同）
	
	@FieldAlias("adviser")
	private String vadviser;// 销售顾问 （不存库，取原合同）

	// 取原合同或缓存字段 end***************************

	// 合同历史字段 begin@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

	@FieldAlias("id")
	private String pk_confrim; // 主键

	@FieldAlias("naccmny")
	private DZFDouble naccountmny; // 合同代账费（只做界面展示）
	
	@FieldAlias("balmny")
	private DZFDouble nbalance;// 预付款余额（只做界面展示）

	@FieldAlias("rebbalmny")
	private DZFDouble nrebatebalance;// 返点余额（只做界面展示）

	@FieldAlias("salespromot")
	private String vsalespromot;// 促销活动（只做界面展示）
	
	@FieldAlias("confreasonid")
	private String vconfreasonid;// 驳回原因主键 （仅作数据传输）

	@FieldAlias("tstp")
	private DZFDateTime tstamp;// 时间戳

	// 扣款字段begin&&&&&&&&&&&&&&&&&&&&&&&&&&&&&：

	@FieldAlias("dedate")
	private DZFDate deductdata;// 扣费日期

	@FieldAlias("ductime")
	private DZFDateTime deductime;// 扣费时间

	@FieldAlias("voper")
	private String voperator; // 经办人（审核人）

	@FieldAlias("vopernm")
	private String vopername; // 经办人（审核人）姓名

	@FieldAlias("ndesummny")
	private DZFDouble ndedsummny;// 扣款总金额

	@FieldAlias("ndemny")
	private DZFDouble ndeductmny;// 扣款金额-预付款

	@FieldAlias("nderebmny")
	private DZFDouble ndedrebamny;// 扣款金额-返点款

	@FieldAlias("propor")
	private Integer ideductpropor;// 扣款比例

	// 扣款字段begin&&&&&&&&&&&&&&&&&&&&&&&&&&&&&：

	// 变更合同字段begin&&&&&&&&&&&&&&&&&&&&&&&&&&&&&：

	@FieldAlias("changetype")
	private Integer ichangetype;// 变更类型1：C端客户终止，变更合同；2：提重了，合同作废；

	@FieldAlias("changereason")
	private String vchangeraeson;// 变更原因

	@FieldAlias("changememo")
	private String vchangememo;// 变更备注

	@FieldAlias("stperiod")
	private String vstopperiod;// 终止期间

	@FieldAlias("remny")
	private DZFDouble nreturnmny;// 退回扣款总金额

	@FieldAlias("rededmny")
	private DZFDouble nretdedmny;// 退回预付款金额

	@FieldAlias("rerebmny")
	private DZFDouble nretrebmny;// 退回返点款金额

	@FieldAlias("nchtlmny")
	private DZFDouble nchangetotalmny; // 变更后合同金额

	@FieldAlias("nchsumny")
	private DZFDouble nchangesummny;// 变更后扣款总金额

	@FieldAlias("nchdemny")
	private DZFDouble nchangededutmny;// 变更后预付款扣款金额

	@FieldAlias("nchremny")
	private DZFDouble nchangerebatmny;// 变更后返点款扣款金额

	@FieldAlias("changer")
	private String vchanger; // 变更人

	@FieldAlias("changetime")
	private DZFDateTime dchangetime;// 变更时间

	@FieldAlias("changedate")
	private DZFDate dchangedate;// 变更日期（不存库，只做展示）

	@FieldAlias("subtotalmny")
	private DZFDouble nsubtotalmny;// 变更后合同总金额差额

	@FieldAlias("subdesummny")
	private DZFDouble nsubdedsummny;// 变更后扣款总金额差额

	@FieldAlias("subdeductmny")
	private DZFDouble nsubdeductmny;// 变更后预付款扣款差额

	@FieldAlias("subderebmny")
	private DZFDouble nsubdedrebamny;// 变更后返点款扣款差额

	// 变更合同字段end&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

	@FieldAlias("mname")
	private String vmanagername; // 渠道经理（只做界面展示）

	@FieldAlias("chtype")
	private Integer channeltype;// 加盟商类型 1-普通加盟商；2-金牌加盟商；

	@FieldAlias("corptp")
	private String corptype; // 加盟商类型（只做界面展示）

	@FieldAlias("canedit")
	private DZFBoolean iscanedit;// 是否允许编辑

	@FieldAlias("errmsg")
	private String verrmsg;// 错误信息

	@FieldAlias("statusname")
	private String vstatusname;// 状态名称

	@FieldAlias("dr")
	private Integer dr; // 删除标记

	@FieldAlias("ts")
	private DZFDateTime ts; // 时间戳

	// 合同历史字段 end@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	
	@FieldAlias("cts")
	private DZFDateTime checkts;//数据校验时间戳(区分原合同数据和历史数据)
	
	//查询用的
	@FieldAlias("aname")
    private String areaname;//大区名称 (运营区域)
	
	@FieldAlias("ovince")
	public Integer vprovince;// 省/市(获取加盟商的运营区域设置)

	public String getVadviser() {
		return vadviser;
	}

	public void setVadviser(String vadviser) {
		this.vadviser = vadviser;
	}

	public DZFDateTime getCheckts() {
		return checkts;
	}

	public void setCheckts(DZFDateTime checkts) {
		this.checkts = checkts;
	}

	public DZFDate getDsigndate() {
		return dsigndate;
	}

	public void setDsigndate(DZFDate dsigndate) {
		this.dsigndate = dsigndate;
	}

	public DZFBoolean getIsnconfirm() {
		return isnconfirm;
	}

	public String getVconfreasonid() {
		return vconfreasonid;
	}

	public void setVconfreasonid(String vconfreasonid) {
		this.vconfreasonid = vconfreasonid;
	}

	public DZFDate getDbegindate() {
		return dbegindate;
	}

	public DZFDate getDenddate() {
		return denddate;
	}

	public void setDbegindate(DZFDate dbegindate) {
		this.dbegindate = dbegindate;
	}

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
	}

	public void setDenddate(DZFDate denddate) {
		this.denddate = denddate;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public void setIsnconfirm(DZFBoolean isnconfirm) {
		this.isnconfirm = isnconfirm;
	}

	public String getVconfreason() {
		return vconfreason;
	}

	public void setVconfreason(String vconfreason) {
		this.vconfreason = vconfreason;
	}

	public DZFDate getDchangedate() {
		return dchangedate;
	}

	public void setDchangedate(DZFDate dchangedate) {
		this.dchangedate = dchangedate;
	}

	public DZFBoolean getIscanedit() {
		return iscanedit;
	}

	public void setIscanedit(DZFBoolean iscanedit) {
		this.iscanedit = iscanedit;
	}

	public DZFDouble getNaccountmny() {
		return naccountmny;
	}

	public void setNaccountmny(DZFDouble naccountmny) {
		this.naccountmny = naccountmny;
	}

	public Integer getChanneltype() {
		return channeltype;
	}

	public String getCorptype() {
		return corptype;
	}

	public void setChanneltype(Integer channeltype) {
		this.channeltype = channeltype;
	}

	public void setCorptype(String corptype) {
		this.corptype = corptype;
	}

	public String getVmanagername() {
		return vmanagername;
	}

	public void setVmanagername(String vmanagername) {
		this.vmanagername = vmanagername;
	}

	public String getVstatusname() {
		return vstatusname;
	}

	public void setVstatusname(String vstatusname) {
		this.vstatusname = vstatusname;
	}

	public DZFDouble getNretdedmny() {
		return nretdedmny;
	}

	public void setNretdedmny(DZFDouble nretdedmny) {
		this.nretdedmny = nretdedmny;
	}

	public DZFDouble getNretrebmny() {
		return nretrebmny;
	}

	public void setNretrebmny(DZFDouble nretrebmny) {
		this.nretrebmny = nretrebmny;
	}

	public DZFDouble getNchangesummny() {
		return nchangesummny;
	}

	public void setNchangesummny(DZFDouble nchangesummny) {
		this.nchangesummny = nchangesummny;
	}

	public DZFDouble getNchangerebatmny() {
		return nchangerebatmny;
	}

	public void setNchangerebatmny(DZFDouble nchangerebatmny) {
		this.nchangerebatmny = nchangerebatmny;
	}

	public DZFDouble getNdedsummny() {
		return ndedsummny;
	}

	public void setNdedsummny(DZFDouble ndedsummny) {
		this.ndedsummny = ndedsummny;
	}

	public DZFDouble getNdedrebamny() {
		return ndedrebamny;
	}

	public void setNdedrebamny(DZFDouble ndedrebamny) {
		this.ndedrebamny = ndedrebamny;
	}

	public DZFDouble getNrebatebalance() {
		return nrebatebalance;
	}

	public void setNrebatebalance(DZFDouble nrebatebalance) {
		this.nrebatebalance = nrebatebalance;
	}

	public DZFDouble getNsubdedsummny() {
		return nsubdedsummny;
	}

	public void setNsubdedsummny(DZFDouble nsubdedsummny) {
		this.nsubdedsummny = nsubdedsummny;
	}

	public DZFDouble getNsubdedrebamny() {
		return nsubdedrebamny;
	}

	public void setNsubdedrebamny(DZFDouble nsubdedrebamny) {
		this.nsubdedrebamny = nsubdedrebamny;
	}

	public Integer getIreceivcycle() {
		return ireceivcycle;
	}

	public void setIreceivcycle(Integer ireceivcycle) {
		this.ireceivcycle = ireceivcycle;
	}

	public DZFBoolean getIsncust() {
		return isncust;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
	}

	public DZFDouble getNsubtotalmny() {
		return nsubtotalmny;
	}

	public DZFDouble getNsubdeductmny() {
		return nsubdeductmny;
	}

	public void setNsubtotalmny(DZFDouble nsubtotalmny) {
		this.nsubtotalmny = nsubtotalmny;
	}

	public void setNsubdeductmny(DZFDouble nsubdeductmny) {
		this.nsubdeductmny = nsubdeductmny;
	}

	public String getVchangememo() {
		return vchangememo;
	}

	public void setVchangememo(String vchangememo) {
		this.vchangememo = vchangememo;
	}

	public DZFDateTime getDchangetime() {
		return dchangetime;
	}

	public void setDchangetime(DZFDateTime dchangetime) {
		this.dchangetime = dchangetime;
	}

	public String getVchangeraeson() {
		return vchangeraeson;
	}

	public void setVchangeraeson(String vchangeraeson) {
		this.vchangeraeson = vchangeraeson;
	}

	public String getVchanger() {
		return vchanger;
	}

	public void setVchanger(String vchanger) {
		this.vchanger = vchanger;
	}

	public String getVchangeperiod() {
		return vchangeperiod;
	}

	public Integer getPatchstatus() {
		return patchstatus;
	}

	public Integer getIchangetype() {
		return ichangetype;
	}

	public String getVstopperiod() {
		return vstopperiod;
	}

	public DZFDouble getNreturnmny() {
		return nreturnmny;
	}

	public DZFDouble getNchangetotalmny() {
		return nchangetotalmny;
	}

	public DZFDouble getNchangededutmny() {
		return nchangededutmny;
	}

	public void setVchangeperiod(String vchangeperiod) {
		this.vchangeperiod = vchangeperiod;
	}

	public void setPatchstatus(Integer patchstatus) {
		this.patchstatus = patchstatus;
	}

	public void setIchangetype(Integer ichangetype) {
		this.ichangetype = ichangetype;
	}

	public void setVstopperiod(String vstopperiod) {
		this.vstopperiod = vstopperiod;
	}

	public void setNreturnmny(DZFDouble nreturnmny) {
		this.nreturnmny = nreturnmny;
	}

	public void setNchangetotalmny(DZFDouble nchangetotalmny) {
		this.nchangetotalmny = nchangetotalmny;
	}

	public void setNchangededutmny(DZFDouble nchangededutmny) {
		this.nchangededutmny = nchangededutmny;
	}

	public void setIsncust(DZFBoolean isncust) {
		this.isncust = isncust;
	}

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

	public String getVarea() {
		return varea;
	}

	public void setVarea(String varea) {
		this.varea = varea;
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
