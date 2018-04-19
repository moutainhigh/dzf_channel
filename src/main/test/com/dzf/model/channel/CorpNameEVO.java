package com.dzf.model.channel;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

@SuppressWarnings({ "serial", "rawtypes" })
public class CorpNameEVO extends SuperVO {
	
	@FieldAlias("pk_id")
	private String 	pk_corpnameedit;//主键
	
	@FieldAlias("corpid")
	private String	pk_corp;//客户ID
	
    @FieldAlias("incode")
    public String innercode;//客户编码
	
    @FieldAlias("fatname")
    private String fathername; //代账机构名称
	
	@FieldAlias("fatcorp")
	private String	fathercorp;//代账机构
	
	@FieldAlias("oldname")
	private String	voldname;//原客户名称
	
	@FieldAlias("newname")
	private String	vnewname;//新客户名称
	
	@FieldAlias("url")
	private String	vurl;//附件路径
	
	@FieldAlias("statu")
	private Integer	istatus;//状态  0：待提交；1：待审核；2：已审核；3：拒绝审核；
	
	@FieldAlias("memo")
	private String	vmemo;//备注
	
	@FieldAlias("coid")
	private String	coperatorid;//	录入人
	
	@FieldAlias("coname")
	private String	coperatname;//录入人名称
	
	@FieldAlias("dodate")
	private DZFDate doperatedate;//录入日期	
	
	@FieldAlias("subid")
	private String	vsubmitorid	;//提交人
	
	@FieldAlias("subname")
	private String	vsubmitname;//提交人名称
	
	@FieldAlias("subtime")
	private DZFDateTime	vsubmittime;//提交时间	
	
	@FieldAlias("apprid")
	private String vapproveid;//审批人
	
	@FieldAlias("apprname")
	private String	vapprovename;//审批人名称
	
	@FieldAlias("apprtime")
	private DZFDateTime	tapprovetime;//审批时间
	
	@FieldAlias("apprnote")
	private String vapprovenote;//审批说明	
	
	@FieldAlias("filname")
	private String filename; // 附件名称
	
	@FieldAlias("errmsg")
	private String verrmsg;//错误信息

	private Integer	dr;	
	
	private DZFDateTime	ts;

	public String getInnercode() {
		return innercode;
	}

	public void setInnercode(String innercode) {
		this.innercode = innercode;
	}

	public String getVerrmsg() {
		return verrmsg;
	}

	public void setVerrmsg(String verrmsg) {
		this.verrmsg = verrmsg;
	}

	public String getPk_corpnameedit() {
		return pk_corpnameedit;
	}

	public void setPk_corpnameedit(String pk_corpnameedit) {
		this.pk_corpnameedit = pk_corpnameedit;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getFathercorp() {
		return fathercorp;
	}

	public void setFathercorp(String fathercorp) {
		this.fathercorp = fathercorp;
	}

	public String getFathername() {
		return fathername;
	}

	public void setFathername(String fathername) {
		this.fathername = fathername;
	}

	public String getVoldname() {
		return voldname;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setVoldname(String voldname) {
		this.voldname = voldname;
	}

	public String getVnewname() {
		return vnewname;
	}

	public void setVnewname(String vnewname) {
		this.vnewname = vnewname;
	}

	public String getVurl() {
		return vurl;
	}

	public void setVurl(String vurl) {
		this.vurl = vurl;
	}

	public Integer getIstatus() {
		return istatus;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
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

	public String getCoperatname() {
		return coperatname;
	}

	public void setCoperatname(String coperatname) {
		this.coperatname = coperatname;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getVsubmitorid() {
		return vsubmitorid;
	}

	public void setVsubmitorid(String vsubmitorid) {
		this.vsubmitorid = vsubmitorid;
	}

	public String getVsubmitname() {
		return vsubmitname;
	}

	public void setVsubmitname(String vsubmitname) {
		this.vsubmitname = vsubmitname;
	}

	public DZFDateTime getVsubmittime() {
		return vsubmittime;
	}

	public void setVsubmittime(DZFDateTime vsubmittime) {
		this.vsubmittime = vsubmittime;
	}

	public String getVapproveid() {
		return vapproveid;
	}

	public void setVapproveid(String vapproveid) {
		this.vapproveid = vapproveid;
	}

	public String getVapprovename() {
		return vapprovename;
	}

	public void setVapprovename(String vapprovename) {
		this.vapprovename = vapprovename;
	}

	public DZFDateTime getTapprovetime() {
		return tapprovetime;
	}

	public void setTapprovetime(DZFDateTime tapprovetime) {
		this.tapprovetime = tapprovetime;
	}

	public String getVapprovenote() {
		return vapprovenote;
	}

	public void setVapprovenote(String vapprovenote) {
		this.vapprovenote = vapprovenote;
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
		// TODO Auto-generated method stub
		return "pk_corpnameedit";
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "cn_corpnameedit";
	}

}
