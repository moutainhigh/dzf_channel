package com.dzf.model.channel.sys_power;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;

/**
 * 
 */
public class URoleVO extends SuperVO {
    
    @FieldAlias("pk_id")
    private String pk_user_role;

    @FieldAlias("id")
    private String pk_role;

    @FieldAlias("text")
    private String role_name;// ---这个字段不存库

    private Boolean checked;// -----这个不存库

    @FieldAlias("ckaccarr[]")
    private String[] ckaccarr;// ---不存库

    @FieldAlias("uid")
    private String cuserid;

    private String pk_corp;

    public String getPk_user_role() {
        return pk_user_role;
    }

    public void setPk_user_role(String pk_user_role) {
        this.pk_user_role = pk_user_role;
    }

    public String getPk_role() {
        return pk_role;
    }

    public void setPk_role(String pk_role) {
        this.pk_role = pk_role;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String[] getCkaccarr() {
        return ckaccarr;
    }

    public void setCkaccarr(String[] ckaccarr) {
        this.ckaccarr = ckaccarr;
    }

    @Override
    public String getPKFieldName() {
        return "";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "";
    }
}