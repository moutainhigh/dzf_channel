package com.dzf.model.channel.contract;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 合同变更申请VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class ChangeApplyVO extends SuperVO {
	
	private static final long serialVersionUID = 1L;

	@FieldAlias("applyid")
    private String pk_changeapply; // 合同变更申请主键
	
    @FieldAlias("conid")
    private String pk_contract; // 原合同主键
    
	@FieldAlias("hisid")
	private String pk_confrim; // 合同历史主键
	
	@FieldAlias("corpid")
	private String pk_corp; // 加盟商主键

	@FieldAlias("corpkid")
	private String pk_corpk; // 客户主键

	// 合同状态  0：待提交；1：审核通过；5:待审批；7：已驳回；8：服务到期；9：已终止（加盟商合同）；10：已作废（加盟商合同）；
	@FieldAlias("status")
	private Integer vstatus; 
	
	@FieldAlias("aptime")
	private DZFDateTime applytime;//申请时间
	
	//申请状态  1：渠道待审（未处理）；2： 区总待审（处理中）；3：总经理待审（处理中）；4：运营待审（处理中）；5：已处理；6：已拒绝；
	@FieldAlias("apstatus")
	private Integer iapplystatus;
	
	@FieldAlias("changetype")
	private Integer ichangetype;// 变更类型1：合同终止；2：合同作废；3：非常规套餐；

	@FieldAlias("changereason")
	private String vchangeraeson;// 变更原因

	@FieldAlias("changememo")
	private String vchangememo;// 变更备注

	@FieldAlias("stperiod")
	private String vstopperiod;// 终止期间
	
	@FieldAlias("bcgeperiod")
	private String vbchangeperiod;// 变更后合同开始期间

	@FieldAlias("ecgeperiod")
	private String vechangeperiod;// 变更后合同结束期间
	
	@FieldAlias("nchtlmny")
	private DZFDouble nchangetotalmny; // 变更后合同总金额
	
	@FieldAlias("doc_name")
	private String docName; // 附件名称(中文)

	@FieldAlias("doc_temp")
	private String docTemp; // 附件名称(下载用 非中文)

	@FieldAlias("doc_time")
	private DZFDateTime docTime; // 上传时间
	
	@FieldAlias("fpath")
	private String vfilepath;// 文件存储路径
	
	@FieldAlias("doc_owner")
	private String docOwner; // 上传人
	
	@FieldAlias("oper")
	private String coperatorid;//录入人
	
	@FieldAlias("opdate")
	private DZFDate doperatedate;//录入日期
	
	@FieldAlias("chanid")
	private String vchannelid;//渠道经理（待审核人）
	
	@FieldAlias("areaer")
	private String vareaer;//区总（待审核人）
	
	@FieldAlias("direr")
	private String vdirector;//总经理（待审核人）
	
	// 不存只作展示 begin*************************
	
	@FieldAlias("area")
	private String varea;// 地区：取客户的省+市 （不存库，取客户缓存）
	
	@FieldAlias("mname")
	private String vmanagername; // 渠道经理（只做界面展示）

	@FieldAlias("corpnm")
	private String corpname; // 渠道商名称（不存库，取客户缓存）
	
    @FieldAlias("vccode")
    private String vcontcode; // 合同编码（不存库，取原合同）
    
    @FieldAlias("corpkna")
	private String corpkname; // 客户名称（不存库，取客户缓存）
    
    @FieldAlias("corpkcd")
	private String corpkcode; // 客户编码（不存库，取客户缓存）
    
    @FieldAlias("chname")
	public String chargedeptname;// 纳税人资格（不存库，取原合同）
    
	@FieldAlias("ntlmny")
	private DZFDouble ntotalmny; // 合同金额（不存库，取原合同）
	
	@FieldAlias("naccmny")
	private DZFDouble naccountmny; // 合同代账费（不存库，取原合同（合同金额 - 账本费））

	@FieldAlias("nbmny")
	private DZFDouble nbookmny; // 账本费（不存库，取原合同）
	
	@FieldAlias("bperiod")
	private String vbeginperiod;// 合同开始期间（不存库，取原合同）

	@FieldAlias("eperiod")
	private String vendperiod;// 合同结束期间（不存库，取原合同）
	
	@FieldAlias("signdate")
	private DZFDate dsigndate;// 签订合同日期（不存库，取原合同）
	
	@FieldAlias("contcycle")
	private Integer icontractcycle;// 合同周期（不存库，取原合同）

	@FieldAlias("recycle")
	private Integer ireceivcycle;// 收款周期（不存库，取原合同）
	
	@FieldAlias("nmsmny")
	private DZFDouble nmservicemny; // 月服务费（不存库，取原合同）
	
	@FieldAlias("submitdate")
	private DZFDate dsubmidate;// 提单日期（不存库，取原合同）
	
	@FieldAlias("ndesummny")
	private DZFDouble ndedsummny;// 扣款总金额（不存库，取历史合同）

	// 不存只作展示 end***************************

	public String getVchannelid() {
		return vchannelid;
	}

	public void setVchannelid(String vchannelid) {
		this.vchannelid = vchannelid;
	}

	public String getVareaer() {
		return vareaer;
	}

	public void setVareaer(String vareaer) {
		this.vareaer = vareaer;
	}

	public String getVdirector() {
		return vdirector;
	}

	public void setVdirector(String vdirector) {
		this.vdirector = vdirector;
	}

	public DZFDouble getNaccountmny() {
		return naccountmny;
	}

	public void setNaccountmny(DZFDouble naccountmny) {
		this.naccountmny = naccountmny;
	}

	public String getVarea() {
		return varea;
	}

	public void setVarea(String varea) {
		this.varea = varea;
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

	public String getDocOwner() {
		return docOwner;
	}

	public void setDocOwner(String docOwner) {
		this.docOwner = docOwner;
	}

	public String getVbchangeperiod() {
		return vbchangeperiod;
	}

	public void setVbchangeperiod(String vbchangeperiod) {
		this.vbchangeperiod = vbchangeperiod;
	}

	public String getVechangeperiod() {
		return vechangeperiod;
	}

	public void setVechangeperiod(String vechangeperiod) {
		this.vechangeperiod = vechangeperiod;
	}

	public DZFDouble getNdedsummny() {
		return ndedsummny;
	}

	public void setNdedsummny(DZFDouble ndedsummny) {
		this.ndedsummny = ndedsummny;
	}

	public String getCorpkcode() {
		return corpkcode;
	}

	public void setCorpkcode(String corpkcode) {
		this.corpkcode = corpkcode;
	}

	public String getPk_changeapply() {
		return pk_changeapply;
	}

	public void setPk_changeapply(String pk_changeapply) {
		this.pk_changeapply = pk_changeapply;
	}

	public String getPk_contract() {
		return pk_contract;
	}

	public void setPk_contract(String pk_contract) {
		this.pk_contract = pk_contract;
	}

	public String getPk_confrim() {
		return pk_confrim;
	}

	public void setPk_confrim(String pk_confrim) {
		this.pk_confrim = pk_confrim;
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

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
	}

	public DZFDateTime getApplytime() {
		return applytime;
	}

	public void setApplytime(DZFDateTime applytime) {
		this.applytime = applytime;
	}

	public Integer getIapplystatus() {
		return iapplystatus;
	}

	public void setIapplystatus(Integer iapplystatus) {
		this.iapplystatus = iapplystatus;
	}

	public Integer getIchangetype() {
		return ichangetype;
	}

	public void setIchangetype(Integer ichangetype) {
		this.ichangetype = ichangetype;
	}

	public String getVchangeraeson() {
		return vchangeraeson;
	}

	public void setVchangeraeson(String vchangeraeson) {
		this.vchangeraeson = vchangeraeson;
	}

	public String getVchangememo() {
		return vchangememo;
	}

	public void setVchangememo(String vchangememo) {
		this.vchangememo = vchangememo;
	}

	public String getVstopperiod() {
		return vstopperiod;
	}

	public void setVstopperiod(String vstopperiod) {
		this.vstopperiod = vstopperiod;
	}

	public DZFDouble getNchangetotalmny() {
		return nchangetotalmny;
	}

	public void setNchangetotalmny(DZFDouble nchangetotalmny) {
		this.nchangetotalmny = nchangetotalmny;
	}

	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocTemp() {
		return docTemp;
	}

	public void setDocTemp(String docTemp) {
		this.docTemp = docTemp;
	}

	public DZFDateTime getDocTime() {
		return docTime;
	}

	public void setDocTime(DZFDateTime docTime) {
		this.docTime = docTime;
	}

	public String getVfilepath() {
		return vfilepath;
	}

	public void setVfilepath(String vfilepath) {
		this.vfilepath = vfilepath;
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

	public String getVcontcode() {
		return vcontcode;
	}

	public void setVcontcode(String vcontcode) {
		this.vcontcode = vcontcode;
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

	public DZFDouble getNtotalmny() {
		return ntotalmny;
	}

	public void setNtotalmny(DZFDouble ntotalmny) {
		this.ntotalmny = ntotalmny;
	}

	public DZFDouble getNmservicemny() {
		return nmservicemny;
	}

	public void setNmservicemny(DZFDouble nmservicemny) {
		this.nmservicemny = nmservicemny;
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

	public DZFDate getDsigndate() {
		return dsigndate;
	}

	public void setDsigndate(DZFDate dsigndate) {
		this.dsigndate = dsigndate;
	}

	public Integer getIcontractcycle() {
		return icontractcycle;
	}

	public void setIcontractcycle(Integer icontractcycle) {
		this.icontractcycle = icontractcycle;
	}

	public Integer getIreceivcycle() {
		return ireceivcycle;
	}

	public void setIreceivcycle(Integer ireceivcycle) {
		this.ireceivcycle = ireceivcycle;
	}

	public DZFDate getDsubmidate() {
		return dsubmidate;
	}

	public void setDsubmidate(DZFDate dsubmidate) {
		this.dsubmidate = dsubmidate;
	}

	@Override
	public String getPKFieldName() {
		return "pk_changeapply";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_changeapply";
	}

}
