package com.dzf.model.channel.sys_power;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 角色按钮权限vo
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class RoleButtonVO extends SuperVO {
	
	@FieldAlias("rbid")
    public String pk_role_button;//按钮主键
	
	@FieldAlias("rid")
	public String pk_role; //角色名称
    
    @FieldAlias("bid")
    public String pk_button; //按钮编码
    
    @FieldAlias("fid")
    public String pk_funnode;//节点主键
    
	private Integer	dr;	
	
	private DZFDateTime	ts;
	
	public String getPk_role_button() {
		return pk_role_button;
	}

	public void setPk_role_button(String pk_role_button) {
		this.pk_role_button = pk_role_button;
	}

	public String getPk_role() {
		return pk_role;
	}

	public void setPk_role(String pk_role) {
		this.pk_role = pk_role;
	}

	public String getPk_button() {
		return pk_button;
	}

	public void setPk_button(String pk_button) {
		this.pk_button = pk_button;
	}

	public String getPk_funnode() {
		return pk_funnode;
	}

	public void setPk_funnode(String pk_funnode) {
		this.pk_funnode = pk_funnode;
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
		return "pk_role_button";
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "sm_role_button";
	}

}
