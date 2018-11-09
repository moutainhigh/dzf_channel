package com.dzf.model.channel.stock;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 仓库VO
 * 
 * @author gejw
 * @time 2018年11月9日 下午12:00:02
 *
 */
public class WareHouseVO extends SuperVO {

    private String pk_warehouse;
    private String pk_corp;
    private String vcode;
    private String vname;
    private String coperatorid;
    private String doperatedate;

    @FieldAlias("dr")
    private Integer dr;// 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts;// 时间戳

    public String getPk_warehouse() {
        return pk_warehouse;
    }

    public void setPk_warehouse(String pk_warehouse) {
        this.pk_warehouse = pk_warehouse;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getCoperatorid() {
        return coperatorid;
    }

    public void setCoperatorid(String coperatorid) {
        this.coperatorid = coperatorid;
    }

    public String getDoperatedate() {
        return doperatedate;
    }

    public void setDoperatedate(String doperatedate) {
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
    public String getPKFieldName() {
        return "pk_warehouse";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "cn_warehouse";
    }

}
