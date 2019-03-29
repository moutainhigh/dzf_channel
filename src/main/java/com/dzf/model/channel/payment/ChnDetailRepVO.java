package com.dzf.model.channel.payment;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 扣款明细报表VO
 * @author 宗岩
 *
 */
@SuppressWarnings("rawtypes")
public class ChnDetailRepVO extends SuperVO {
	
	private static final long serialVersionUID = 2201791567465025963L;

	@FieldAlias("id")
	private String pk_detail;
	
	@FieldAlias("corpid")
	private String pk_corp;
	
	@FieldAlias("corpnm")
	private String corpname;//加盟商名称
	
	@FieldAlias("usemny")
	private DZFDouble nusedmny;//已用金额
	
	@FieldAlias("balmny")
	private DZFDouble nbalance;//余额
	
	@FieldAlias("npmny")
	private DZFDouble npaymny;//付款金额
	
	@FieldAlias("iptype")
	private Integer ipaytype;//付款类型   1：加盟费；2：预付款；3：返点；
	
	@FieldAlias("ptypenm")
	private String vpaytypename;//付款类型名称 
	
	@FieldAlias("billid")
    private String pk_bill;//业务ID
	
    @FieldAlias("memo")
    private String vmemo;// 备注
    
    @FieldAlias("cid")
    private String coperatorid;// 录入人
    
    @FieldAlias("ddate")
    private DZFDate doperatedate;// 录入日期
    
    private Integer dr;
    
    private DZFDateTime ts;
    
    @FieldAlias("opertype")
    private Integer iopertype;//操作类型  1：付款单付款；2：合同扣款；3：返点单确认；4：退款单审核；5：商品购买；
    
    @FieldAlias("opertypenm")
    private String vopertypename;//操作类型名称
    
    @FieldAlias("ntlmny")
    private DZFDouble ntotalmny; // 合同总金额
    
    @FieldAlias("propor")
    private Integer ideductpropor;//扣款比例
    
    @FieldAlias("nbmny")
    private DZFDouble nbookmny; // 账本费(展示数据)
    
    @FieldAlias("namny")
    private DZFDouble naccountmny; // 代账费(展示数据)
    
    @FieldAlias("deductype")
    private Integer ideductype;//扣款类型 3：全部返点扣款；
    
	@FieldAlias("vccode")
	private String vcontcode; // 合同编码
	
    @FieldAlias("pstatus")
    private Integer patchstatus;// 加盟商合同类型（null正常合同；1：被2补提交的原合同；2：小规模转一般人的合同；3：变更合同;4：被5补提交的原合同;5:一般人转小规模的合同）
    
	@FieldAlias("corpkid")
	private String pk_corpk; // 客户主键
    
	@FieldAlias("corpknm")
	private String corpkname;//客户名称
	
	@FieldAlias("status")
	private Integer vstatus; // 合同状态  0：待提交；1：审核通过；5:待审批；7：已驳回；8：服务到期；9：已终止（加盟商合同）；10：已作废（加盟商合同）；
	
	@FieldAlias("isncust")
	private DZFBoolean isncust;// 是否存量客户
	
	@FieldAlias("oldname")
	private String voldname;//原客户名称
	
	@FieldAlias("mname")
	private String vmanagername; // 渠道经理（只做界面展示）

	public String getVmanagername() {
		return vmanagername;
	}

	public void setVmanagername(String vmanagername) {
		this.vmanagername = vmanagername;
	}

	public String getVoldname() {
		return voldname;
	}

	public void setVoldname(String voldname) {
		this.voldname = voldname;
	}

	public DZFBoolean getIsncust() {
		return isncust;
	}

	public void setIsncust(DZFBoolean isncust) {
		this.isncust = isncust;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
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

	public Integer getPatchstatus() {
		return patchstatus;
	}

	public void setPatchstatus(Integer patchstatus) {
		this.patchstatus = patchstatus;
	}

	public String getVcontcode() {
		return vcontcode;
	}

	public void setVcontcode(String vcontcode) {
		this.vcontcode = vcontcode;
	}

	public Integer getIdeductype() {
		return ideductype;
	}

	public void setIdeductype(Integer ideductype) {
		this.ideductype = ideductype;
	}

	public String getPk_detail() {
		return pk_detail;
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

	public DZFDouble getNbalance() {
		return nbalance;
	}

	public DZFDouble getNpaymny() {
		return npaymny;
	}

	public Integer getIpaytype() {
		return ipaytype;
	}

	public String getVpaytypename() {
		return vpaytypename;
	}

	public String getPk_bill() {
		return pk_bill;
	}

	public String getVmemo() {
		return vmemo;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public Integer getDr() {
		return dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public Integer getIopertype() {
		return iopertype;
	}

	public String getVopertypename() {
		return vopertypename;
	}

	public DZFDouble getNtotalmny() {
		return ntotalmny;
	}

	public Integer getIdeductpropor() {
		return ideductpropor;
	}

	public DZFDouble getNbookmny() {
		return nbookmny;
	}

	public DZFDouble getNaccountmny() {
		return naccountmny;
	}

	public void setPk_detail(String pk_detail) {
		this.pk_detail = pk_detail;
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

	public void setNbalance(DZFDouble nbalance) {
		this.nbalance = nbalance;
	}

	public void setNpaymny(DZFDouble npaymny) {
		this.npaymny = npaymny;
	}

	public void setIpaytype(Integer ipaytype) {
		this.ipaytype = ipaytype;
	}

	public void setVpaytypename(String vpaytypename) {
		this.vpaytypename = vpaytypename;
	}

	public void setPk_bill(String pk_bill) {
		this.pk_bill = pk_bill;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public void setIopertype(Integer iopertype) {
		this.iopertype = iopertype;
	}

	public void setVopertypename(String vopertypename) {
		this.vopertypename = vopertypename;
	}

	public void setNtotalmny(DZFDouble ntotalmny) {
		this.ntotalmny = ntotalmny;
	}

	public void setIdeductpropor(Integer ideductpropor) {
		this.ideductpropor = ideductpropor;
	}

	public void setNbookmny(DZFDouble nbookmny) {
		this.nbookmny = nbookmny;
	}

	public void setNaccountmny(DZFDouble naccountmny) {
		this.naccountmny = naccountmny;
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
