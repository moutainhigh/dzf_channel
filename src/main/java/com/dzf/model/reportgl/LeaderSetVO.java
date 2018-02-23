package com.dzf.model.reportgl;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;

public class LeaderSetVO extends SuperVO {

    @FieldAlias("id")
    private String pk_leaderset;
    @FieldAlias("uid1")
    private String vdeptuserid;// 地区
    @FieldAlias("uid2")
    private String vcomuserid;// 公司编码
    @FieldAlias("uid3")
    private String vgroupuserid;// 公司名称
    @FieldAlias("corpid")
    private String pk_corp;// 会计公司pk

    private Integer dr; // 删除标记

    private DZFDateTime ts; // 时间戳
    
    public String getPk_leaderset() {
        return pk_leaderset;
    }

    public void setPk_leaderset(String pk_leaderset) {
        this.pk_leaderset = pk_leaderset;
    }

    public String getVdeptuserid() {
        return vdeptuserid;
    }

    public void setVdeptuserid(String vdeptuserid) {
        this.vdeptuserid = vdeptuserid;
    }

    public String getVcomuserid() {
        return vcomuserid;
    }

    public void setVcomuserid(String vcomuserid) {
        this.vcomuserid = vcomuserid;
    }

    public String getVgroupuserid() {
        return vgroupuserid;
    }

    public void setVgroupuserid(String vgroupuserid) {
        this.vgroupuserid = vgroupuserid;
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

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }
   
    @Override
    public String getPKFieldName() {
        return "pk_leaderset";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "cn_leaderset";
    }

}
