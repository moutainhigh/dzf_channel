package com.dzf.model.channel.sale;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDateTime;		

/**
 * 渠道区域划分从表VO(渠道经理)
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class ChnAreaBVO extends SuperVO {
	
	@FieldAlias("pk_area")
	private String pk_chnarea;// 主表主键
	
	@FieldAlias("pk_areab")
	private String pk_chnarea_b;// 主键
	
	@FieldAlias("corpid")
	private String pk_corp;// 负责加盟商主键
	
    @FieldAlias("corpnm")
    private String corpname; // 负责渠道商名称
	
	@FieldAlias("ovince")
	public Integer vprovince;// 地区
	    
	@FieldAlias("provname")
	public String vprovname;// 地区名称
	
	@FieldAlias("isCharge")
	public DZFBoolean isCharge;//是否省/市负责人
	
	public Integer stytle;//1:渠道经理；2：培训师
	
    @FieldAlias("uid")
    private String userid; // 用户主键（渠道经理）
    
    @FieldAlias("uname")
    private String username; // 用户名称（渠道经理）
	
	@FieldAlias("vmemo")
	private String vmemo;// 备注
	
	@FieldAlias("dr")
	private Integer dr;// 删除标记
	
	@FieldAlias("ts")
	private DZFDateTime ts;// 时间戳

	public String getPk_chnarea() {
		return pk_chnarea;
	}

	public void setPk_chnarea(String pk_chnarea) {
		this.pk_chnarea = pk_chnarea;
	}

	public String getPk_chnarea_b() {
		return pk_chnarea_b;
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

	public DZFBoolean getIsCharge() {
		return isCharge;
	}

	public void setIsCharge(DZFBoolean isCharge) {
		this.isCharge = isCharge;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public void setPk_chnarea_b(String pk_chnarea_b) {
		this.pk_chnarea_b = pk_chnarea_b;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
	}

	public String getVprovname() {
		return vprovname;
	}

	public void setVprovname(String vprovname) {
		this.vprovname = vprovname;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
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
		return "pk_chnarea";
	}

	@Override
	public String getPKFieldName() {
		return "pk_chnarea_b";
	}

	@Override
	public String getTableName() {
		return "cn_chnarea_b";
	}

}
