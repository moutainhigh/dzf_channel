package com.dzf.model.channel.rebate;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;

/**
 * 返点单
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class RebateVO extends SuperVO {

	private static final long serialVersionUID = 7496666964587686006L;
	
	@FieldAlias("rebid")
	private String pk_rebate;//主键
	
	@FieldAlias("corpid")
	private String pk_corp;//代账公司(加盟商)主键
	
	@FieldAlias("corpcode")
	private String corpcode;//代账公司(加盟商)编码
	
	@FieldAlias("corp")
	private String corpname;//代账公司(加盟商)名称
	
	@FieldAlias("corpfid")
	private String fathercorp;//上级机构主键
	
	@FieldAlias("vcode")
	private String vbillcode;//返点单号
	
	@FieldAlias("year")
	private String vyear;//所属季度-年
	
	@FieldAlias("season")
	private Integer iseason;//所属季度-季度
	
	@FieldAlias("period")
	private String vperiod;//返点所属期间
	
	@FieldAlias("speriod")
	private String vsperiod;//开始期间
	
	@FieldAlias("speriod")
	private String veperiod;//结束期间
	
	@FieldAlias("debitmny")
	private DZFDouble ndebitmny;//扣款金额
	
	@FieldAlias("basemny")
	private DZFDouble nbasemny;//返点基数
	
	@FieldAlias("rebatemny")
	private DZFDouble nrebatemny;//返点金额
	
	@FieldAlias("istatus")
	private Integer istatus;//状态   0：待提交；1：待确认；2：待审批；3：审批通过；4：已驳回；

	@FieldAlias("memo")
	private String vmemo;//备注
	
	@FieldAlias("operid")
	private String coperatorid;//录入人
	
	@FieldAlias("operdate")
	private DZFDate doperatedate;//录入日期
	
	@FieldAlias("confid")
	private String vconfirmid;//确认人
	
	@FieldAlias("conftime")
	private DZFDateTime tconfirmtime;//确认时间
	
	@FieldAlias("confnote")
	private String vconfirmnote;//确认说明
	
	@FieldAlias("apprid")
	private String vapproveid;//审批人
	
	@FieldAlias("apprtime")
	private DZFDateTime tapprovetime;//审批时间
	
	@FieldAlias("apprnote")
	private String vapprovenote;//审批说明
	
    @FieldAlias("dr")
    private Integer dr; // 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts; // 时间戳
    
    @FieldAlias("tstp")
    private DZFDateTime tstamp;// 时间戳 （数据发生变化时，使用）
    
    @FieldAlias("contnum")
    private Integer icontractnum;//合同数量
    
    //只展示，不存库字段begin&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    
	@FieldAlias("aname")
    private String vareaname;// 大区
	
	@FieldAlias("provname")
	public String vprovname;// 省（市）
	
    @FieldAlias("mname")
    private String vmanagername; // 渠道经理
    
    @FieldAlias("errmsg")
    private String verrmsg;//错误信息
    
	@FieldAlias("statusname")
	private String vstatusname;//状态名称
	
	@FieldAlias("opername")
	private String voperatorname;//录入人名称
	
	@FieldAlias("showdate")
	private String vshowdate;//所属季度-展示名称
	
	//只展示，不存库字段begin&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

	public String getVshowdate() {
		return vshowdate;
	}

	public Integer getIcontractnum() {
		return icontractnum;
	}

	public void setIcontractnum(Integer icontractnum) {
		this.icontractnum = icontractnum;
	}

	public void setVshowdate(String vshowdate) {
		this.vshowdate = vshowdate;
	}

	public String getVoperatorname() {
		return voperatorname;
	}

	public void setVoperatorname(String voperatorname) {
		this.voperatorname = voperatorname;
	}

	public String getVstatusname() {
		return vstatusname;
	}

	public void setVstatusname(String vstatusname) {
		this.vstatusname = vstatusname;
	}

	public String getVerrmsg() {
		return verrmsg;
	}

	public void setVerrmsg(String verrmsg) {
		this.verrmsg = verrmsg;
	}

	public String getVareaname() {
		return vareaname;
	}

	public void setVareaname(String vareaname) {
		this.vareaname = vareaname;
	}

	public String getVperiod() {
		return vperiod;
	}

	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}

	public Integer getIseason() {
		return iseason;
	}

	public void setIseason(Integer iseason) {
		this.iseason = iseason;
	}

	public String getCorpcode() {
		return corpcode;
	}

	public void setCorpcode(String corpcode) {
		this.corpcode = corpcode;
	}

	public String getVmanagername() {
		return vmanagername;
	}

	public void setVmanagername(String vmanagername) {
		this.vmanagername = vmanagername;
	}

	public String getVprovname() {
		return vprovname;
	}

	public void setVprovname(String vprovname) {
		this.vprovname = vprovname;
	}

	public DZFDateTime getTstamp() {
		return tstamp;
	}

	public void setTstamp(DZFDateTime tstamp) {
		this.tstamp = tstamp;
	}

	public String getPk_rebate() {
		return pk_rebate;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getCorpname() {
		return corpname;
	}

	public String getFathercorp() {
		return fathercorp;
	}

	public String getVbillcode() {
		return vbillcode;
	}

	public String getVyear() {
		return vyear;
	}

	public String getVsperiod() {
		return vsperiod;
	}

	public String getVeperiod() {
		return veperiod;
	}

	public DZFDouble getNdebitmny() {
		return ndebitmny;
	}

	public DZFDouble getNbasemny() {
		return nbasemny;
	}

	public DZFDouble getNrebatemny() {
		return nrebatemny;
	}

	public Integer getIstatus() {
		return istatus;
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

	public String getVconfirmid() {
		return vconfirmid;
	}

	public DZFDateTime getTconfirmtime() {
		return tconfirmtime;
	}

	public String getVconfirmnote() {
		return vconfirmnote;
	}

	public String getVapproveid() {
		return vapproveid;
	}

	public DZFDateTime getTapprovetime() {
		return tapprovetime;
	}

	public String getVapprovenote() {
		return vapprovenote;
	}

	public Integer getDr() {
		return dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setPk_rebate(String pk_rebate) {
		this.pk_rebate = pk_rebate;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public void setFathercorp(String fathercorp) {
		this.fathercorp = fathercorp;
	}

	public void setVbillcode(String vbillcode) {
		this.vbillcode = vbillcode;
	}

	public void setVyear(String vyear) {
		this.vyear = vyear;
	}

	public void setVsperiod(String vsperiod) {
		this.vsperiod = vsperiod;
	}

	public void setVeperiod(String veperiod) {
		this.veperiod = veperiod;
	}

	public void setNdebitmny(DZFDouble ndebitmny) {
		this.ndebitmny = ndebitmny;
	}

	public void setNbasemny(DZFDouble nbasemny) {
		this.nbasemny = nbasemny;
	}

	public void setNrebatemny(DZFDouble nrebatemny) {
		this.nrebatemny = nrebatemny;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
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

	public void setVconfirmid(String vconfirmid) {
		this.vconfirmid = vconfirmid;
	}

	public void setTconfirmtime(DZFDateTime tconfirmtime) {
		this.tconfirmtime = tconfirmtime;
	}

	public void setVconfirmnote(String vconfirmnote) {
		this.vconfirmnote = vconfirmnote;
	}

	public void setVapproveid(String vapproveid) {
		this.vapproveid = vapproveid;
	}

	public void setTapprovetime(DZFDateTime tapprovetime) {
		this.tapprovetime = tapprovetime;
	}

	public void setVapprovenote(String vapprovenote) {
		this.vapprovenote = vapprovenote;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getPKFieldName() {
		return "pk_rebate";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_rebate";
	}

}
