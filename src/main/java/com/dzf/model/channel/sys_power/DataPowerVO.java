package com.dzf.model.channel.sys_power;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 数据权限
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class DataPowerVO extends SuperVO {

    @FieldAlias("id")
    private String pk_datapower; // 数据权限主键
    
	@FieldAlias("corpid")
	private String pk_corp;
	
    @FieldAlias("roleid")
    private String pk_role; // 角色主键
    
    @FieldAlias("level")
    private Integer idatalevel;//1:所有数据权限；2:大区数据权限；3:加盟商数据权限
    
	@FieldAlias("coptid")
	private String coperatorid;// 创建人
	
	@FieldAlias("ddate")
	private DZFDate doperatedate;// 创建日期
	
	@FieldAlias("dr")
	private Integer dr;// 删除标记
	
	@FieldAlias("ts")
	private DZFDateTime ts;// 时间戳
	
    public String getPk_datapower() {
		return pk_datapower;
	}

	public void setPk_datapower(String pk_datapower) {
		this.pk_datapower = pk_datapower;
	}

	public String getPk_role() {
		return pk_role;
	}

	public void setPk_role(String pk_role) {
		this.pk_role = pk_role;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public Integer getIdatalevel() {
		return idatalevel;
	}

	public void setIdatalevel(Integer idatalevel) {
		this.idatalevel = idatalevel;
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
        return "pk_datapower";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "cn_datapower";
    }
}