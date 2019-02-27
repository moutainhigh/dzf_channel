package com.dzf.model.channel.stock;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;

/**
 * 期末结转VO
 *
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class CarryOverVO extends SuperVO {

	@FieldAlias("coid")
    private String pk_carryover;
    
    private String pk_corp;
    
    private String period;
    
    @FieldAlias("isco")
    private DZFBoolean iscarryover;
    
    private String coperatorid;
    
    private DZFDate doperatedate;

    private Integer dr;// 删除标记

    private DZFDateTime ts;// 时间戳
    

    public String getPk_carryover() {
		return pk_carryover;
	}

	public void setPk_carryover(String pk_carryover) {
		this.pk_carryover = pk_carryover;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public DZFBoolean getIscarryover() {
		return iscarryover;
	}

	public void setIscarryover(DZFBoolean iscarryover) {
		this.iscarryover = iscarryover;
	}

	public String getCoperatorid() {
		return coperatorid;
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
        return "pk_carryover";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "cn_carryover";
    }

}
