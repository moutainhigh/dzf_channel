package com.dzf.model.channel;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 渠道-付款单VO
 * 
 * @author dzf
 *
 */
public class ChnPayBillVO extends SuperVO {

	private static final long serialVersionUID = -4729021634998726662L;
	
	@FieldAlias("billid")
	private String pk_paybill;// 主键
	
	@FieldAlias("corpid")
	private String pk_corp;// 机构主键
	
	@FieldAlias("corpnm")
	private String corpname;// 机构名称
	
	@FieldAlias("vcode")
	private String vbillcode;// 单据号
	
	@FieldAlias("dpdate")
	private DZFDate dpaydate;// 付款日期
	
	@FieldAlias("vhid")
	private String vhandleid;// 经办人ID
	
	@FieldAlias("vhname")
	private String vhandlename;// 经办人名称
	
	@FieldAlias("vcid")
	private String vconfirmid;// 确认人ID
	
	@FieldAlias("vcname")
	private String vconfirmname;// 确认人名称
	
	@FieldAlias("dctime")
	private DZFDateTime dconfirmtime;// 确认时间
	
	@FieldAlias("memo")
	private String vmemo;// 备注
	
	@FieldAlias("cid")
	private String coperatorid;// 录入人
	
	@FieldAlias("ddate")
	private DZFDate doperatedate;// 录入日期

	private Integer dr;

	private DZFDateTime ts;
	
	@FieldAlias("npmny")
	private DZFDouble npaymny;// 付款金额
	
	@FieldAlias("ipmode")
	private Integer ipaymode;// 1:银行转账；2:支付宝；3：微信
	
	@FieldAlias("iptype")
	private Integer ipaytype;// 1:加盟费；2：预付款
	
	@FieldAlias("status")
	private Integer vstatus;// 1：待提交； 2：待确认（修改为待审核）；3：已确认；4：已驳回；5:待确认；
	
    @FieldAlias("tstp")
    private DZFDateTime tstamp;//时间戳
    
    @FieldAlias("errmsg")
    private String verrmsg;//错误信息
    
    @FieldAlias("doc_name")
    private String docName; // 附件名称(中文)

    @FieldAlias("doc_owner")
    private String docOwner; // 上传人

    @FieldAlias("doc_time")
    private DZFDateTime docTime; // 上传时间
    
    @FieldAlias("fpath")
    private String vfilepath;// 文件存储路径
    
    @FieldAlias("vbname")
    private String vbankname;//付款银行
    @FieldAlias("vbcode")
    private String vbankcode;//付款账号
    
    private String vreason;//驳回原因

	@FieldAlias("subtime")
	private DZFDateTime submitime;// 提交时间

	@FieldAlias("subid")
	private String submitid;// 提交人

	@FieldAlias("subname")
	private String submitname;// 提交人名称

	@FieldAlias("stype")
	private Integer systype;// 来源系统 1-管理端；2-在线业务系统

	@FieldAlias("aname")
	private String areaname;// 大区名称

	@FieldAlias("provname")
	public String vprovname;// 省市名称

	@FieldAlias("ovince")
	public Integer vprovince;// 省市

	@FieldAlias("vaid")
	private String vauditid;// 审核人ID

	@FieldAlias("vaname")
	private String vauditname;// 审核人名称

	@FieldAlias("datime")
	private DZFDateTime daudittime;// 审核时间

	public String getVerrmsg() {
		return verrmsg;
	}

	public void setVerrmsg(String verrmsg) {
		this.verrmsg = verrmsg;
	}

	public String getVbankname() {
		return vbankname;
	}

	public void setVbankname(String vbankname) {
		this.vbankname = vbankname;
	}

	public String getVbankcode() {
		return vbankcode;
	}

	public void setVbankcode(String vbankcode) {
		this.vbankcode = vbankcode;
	}

	public String getVreason() {
		return vreason;
	}

	public void setVreason(String vreason) {
		this.vreason = vreason;
	}

	public String getVauditid() {
		return vauditid;
	}

	public void setVauditid(String vauditid) {
		this.vauditid = vauditid;
	}

	public String getVauditname() {
		return vauditname;
	}

	public void setVauditname(String vauditname) {
		this.vauditname = vauditname;
	}

	public DZFDateTime getDaudittime() {
		return daudittime;
	}

	public void setDaudittime(DZFDateTime daudittime) {
		this.daudittime = daudittime;
	}

	public String getPk_paybill() {
		return pk_paybill;
	}

	public void setPk_paybill(String pk_paybill) {
		this.pk_paybill = pk_paybill;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getDocName() {
		return docName;
	}

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
	}

	public String getSubmitname() {
		return submitname;
	}

	public void setSubmitname(String submitname) {
		this.submitname = submitname;
	}

	public DZFDateTime getSubmitime() {
		return submitime;
	}

	public void setSubmitime(DZFDateTime submitime) {
		this.submitime = submitime;
	}

	public String getSubmitid() {
		return submitid;
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

	public void setSubmitid(String submitid) {
		this.submitid = submitid;
	}

	public Integer getSystype() {
		return systype;
	}

	public void setSystype(Integer systype) {
		this.systype = systype;
	}

	public DZFDateTime getTstamp() {
		return tstamp;
	}

	public void setTstamp(DZFDateTime tstamp) {
		this.tstamp = tstamp;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public String getDocOwner() {
		return docOwner;
	}

	public void setDocOwner(String docOwner) {
		this.docOwner = docOwner;
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

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public DZFDate getDpaydate() {
		return dpaydate;
	}

	public void setDpaydate(DZFDate dpaydate) {
		this.dpaydate = dpaydate;
	}

	public String getVhandleid() {
		return vhandleid;
	}

	public void setVhandleid(String vhandleid) {
		this.vhandleid = vhandleid;
	}

	public String getVhandlename() {
		return vhandlename;
	}

	public void setVhandlename(String vhandlename) {
		this.vhandlename = vhandlename;
	}

	public String getVconfirmid() {
		return vconfirmid;
	}

	public void setVconfirmid(String vconfirmid) {
		this.vconfirmid = vconfirmid;
	}

	public String getVconfirmname() {
		return vconfirmname;
	}

	public void setVconfirmname(String vconfirmname) {
		this.vconfirmname = vconfirmname;
	}

	public DZFDateTime getDconfirmtime() {
		return dconfirmtime;
	}

	public void setDconfirmtime(DZFDateTime dconfirmtime) {
		this.dconfirmtime = dconfirmtime;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
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

	public DZFDouble getNpaymny() {
		return npaymny;
	}

	public void setNpaymny(DZFDouble npaymny) {
		this.npaymny = npaymny;
	}

	public Integer getIpaymode() {
		return ipaymode;
	}

	public void setIpaymode(Integer ipaymode) {
		this.ipaymode = ipaymode;
	}

	public Integer getIpaytype() {
		return ipaytype;
	}

	public void setIpaytype(Integer ipaytype) {
		this.ipaytype = ipaytype;
	}

	public Integer getVstatus() {
		return vstatus;
	}

	public void setVstatus(Integer vstatus) {
		this.vstatus = vstatus;
	}

	@Override
	public String getPKFieldName() {
		return "pk_paybill";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_paybill";
	}

}
