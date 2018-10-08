package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;

@SuppressWarnings("rawtypes")
public class VouvherQryVO extends SuperVO {

	private static final long serialVersionUID = 1424603880562645151L;
	
	@FieldAlias("corpkid")
	private String pk_corpk;//客户主键

	@FieldAlias("auditnum")
	private Integer iauditnum;//已审核数量
	
	@FieldAlias("sumnum")
	private Integer isumnum;//总数量
	
	@FieldAlias("period")
	private String vperiod;//期间

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
	}

	public Integer getIauditnum() {
		return iauditnum;
	}

	public void setIauditnum(Integer iauditnum) {
		this.iauditnum = iauditnum;
	}

	public Integer getIsumnum() {
		return isumnum;
	}

	public void setIsumnum(Integer isumnum) {
		this.isumnum = isumnum;
	}

	public String getVperiod() {
		return vperiod;
	}

	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
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
