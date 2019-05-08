package com.dzf.model.packagedef;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 服务套餐定义子表VO（加盟商多选参照）
 * @author 
 *
 */
@SuppressWarnings("rawtypes")
public class PackageDefCVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	@FieldAlias("pid")
    private String pk_packagedef; //主表主键
	
    @FieldAlias("pcid")
    private String pk_packagedef_c; //主键
	
    @FieldAlias("corpid")
    private String pk_corp; //加盟商
    
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

	public String getPk_packagedef_c() {
		return pk_packagedef_c;
	}

	public void setPk_packagedef_c(String pk_packagedef_c) {
		this.pk_packagedef_c = pk_packagedef_c;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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
		return "pk_packagedef_c";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_packagedef_c";
	}

}
