package com.dzf.model.packagedef;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 服务套餐定义子表VO（省、市多选参照）
 * @author 
 *
 */
@SuppressWarnings("rawtypes")
public class PackageDefBVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	@FieldAlias("pid")
    private String pk_packagedef; //主表主键
	
    @FieldAlias("pbid")
    private String pk_packagedef_b; //主键
    
    @FieldAlias("city")
    private Integer vcity;//市
    
    @FieldAlias("dr")
    private Integer dr; // 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts; // 时间戳
    
	public String getPk_packagedef() {
		return pk_packagedef;
	}

	public void setPk_packagedef(String pk_packagedef) {
		this.pk_packagedef = pk_packagedef;
	}

	public String getPk_packagedef_b() {
		return pk_packagedef_b;
	}

	public void setPk_packagedef_b(String pk_packagedef_b) {
		this.pk_packagedef_b = pk_packagedef_b;
	}

	public Integer getVcity() {
		return vcity;
	}

	public void setVcity(Integer vcity) {
		this.vcity = vcity;
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
		return "pk_packagedef_b";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_packagedef_b";
	}

}
