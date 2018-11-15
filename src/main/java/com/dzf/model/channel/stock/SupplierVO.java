package com.dzf.model.channel.stock;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 供应商VO
 * 
 * @author gejw
 * @time 2018年11月9日 下午1:41:44
 *
 */
public class SupplierVO extends SuperVO {

	@FieldAlias("suid")
    private String pk_supplier;
    
	@FieldAlias("corpid")
    private String pk_corp;
    
    @FieldAlias("code")
    private String vcode;
    
    @FieldAlias("name")
    private String vname;
    
    @FieldAlias("operid")
    private String coperatorid;
    
    @FieldAlias("operdate")
    private DZFDate doperatedate;

    @FieldAlias("dr")
    private Integer dr;// 删除标记

    @FieldAlias("ts")
    private DZFDateTime ts;// 时间戳

    public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getPk_supplier() {
        return pk_supplier;
    }

    public void setPk_supplier(String pk_supplier) {
        this.pk_supplier = pk_supplier;
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
        return "pk_supplier";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "cn_supplier";
    }

}
