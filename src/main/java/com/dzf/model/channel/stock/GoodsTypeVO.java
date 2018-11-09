package com.dzf.model.channel.stock;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 商品分类VO
 * @author gejw
 * @time 2018年11月9日 下午1:37:34
 *
 */
public class GoodsTypeVO extends SuperVO {

    private String pk_goodstype;
    private String pk_corp;
    private String vcode;
    private String vname;
    private String coperatorid;
    private String doperatedate;

    @FieldAlias("dr")
    private Integer dr;// 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts;// 时间戳

    public String getPk_goodstype() {
        return pk_goodstype;
    }

    public void setPk_goodstype(String pk_goodstype) {
        this.pk_goodstype = pk_goodstype;
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
        return "pk_goodstype";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "cn_goodstype";
    }

}
