package com.dzf.model.channel.sale;

import com.dzf.model.pub.MultSuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 渠道区域划分VO(大区总经理)
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class ChnAreaVO extends MultSuperVO {

    @FieldAlias("pk_area")
    private String pk_chnarea; // 渠道区域主键
	
    @FieldAlias("corpid")
    private String pk_corp; // 渠道商主键
    
    @FieldAlias("corpnm")
    private String corpname; // 渠道商名称
    
    @FieldAlias("acode")
    private String areacode;//大区编码
    
    @FieldAlias("aname")
    private String areaname;//大区名称
    
	@FieldAlias("uid")
    private String userid; // 用户主键（大区总经理）
    
    @FieldAlias("uname")
    private String username; // 用户名称（大区总经理）
    
	@FieldAlias("provnames")
	public String vprovnames;// 地区名称(仅供查询用的)
    
	@FieldAlias("vmemo")
	private String vmemo;// 备注
    
	@FieldAlias("coptid")
	private String coperatorid;// 创建人
	
	@FieldAlias("ddate")
	private DZFDate doperatedate;// 创建日期
	
	@FieldAlias("type")
	public Integer type;//1:渠道经理；2：培训师
	
	@FieldAlias("dr")
	private Integer dr;// 删除标记
	
	@FieldAlias("ts")
	private DZFDateTime ts;// 时间戳
    
	@Override
	public String getPKFieldName() {
		return "pk_chnarea";
	}

	public String getPk_chnarea() {
		return pk_chnarea;
	}

	public void setPk_chnarea(String pk_chnarea) {
		this.pk_chnarea = pk_chnarea;
	}

	public String getVprovnames() {
		return vprovnames;
	}

	public void setVprovnames(String vprovnames) {
		this.vprovnames = vprovnames;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getAreacode() {
		return areacode;
	}

	public void setAreacode(String areacode) {
		this.areacode = areacode;
	}

	public void setUsername(String username) {
		this.username = username;
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

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_chnarea";
	}
	
	@Override
	public String[] getTableCodes() {
	    return new String[] { "cn_chnarea_b" };
	}
}
