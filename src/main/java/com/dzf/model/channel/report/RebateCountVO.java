package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 返点金额统计
 *
 */
@SuppressWarnings("rawtypes")
public class RebateCountVO extends SuperVO {

	private static final long serialVersionUID = 7496666964587686006L;
	
	
	@FieldAlias("corpid")
	private String pk_corp;//代账公司(加盟商)主键
	
	@FieldAlias("corpcode")
	private String corpcode;//代账公司(加盟商)编码
	
	@FieldAlias("corp")
	private String corpname;//代账公司(加盟商)名称
	
	@FieldAlias("year")
	private String vyear;//所属季度-年
	
	@FieldAlias("season")
	private Integer iseason;//所属季度-季度
	
	@FieldAlias("period")
	private String vperiod;//返点所属期间

	@FieldAlias("rebatemny")
	private DZFDouble nrebatemny;//返点金额
	
	private DZFDouble nmny1;//第一季度
	
	private DZFDouble nmny2;//第二季度
	
	private DZFDouble nmny3;//第三季度
	
	private DZFDouble nmny4;//第四季度
    
	@FieldAlias("aname")
    private String areaname;// 大区
	
	@FieldAlias("provname")
	public String vprovname;// 省（市）
	
	@FieldAlias("ovince")
	public Integer vprovince;// 省市
	
    @FieldAlias("mname")
    private String vmanagername; // 渠道经理

	public DZFDouble getNmny1() {
        return nmny1;
    }

    public void setNmny1(DZFDouble nmny1) {
        this.nmny1 = nmny1;
    }

    public DZFDouble getNmny2() {
        return nmny2;
    }

    public void setNmny2(DZFDouble nmny2) {
        this.nmny2 = nmny2;
    }

    public DZFDouble getNmny3() {
        return nmny3;
    }

    public void setNmny3(DZFDouble nmny3) {
        this.nmny3 = nmny3;
    }

    public DZFDouble getNmny4() {
        return nmny4;
    }

    public void setNmny4(DZFDouble nmny4) {
        this.nmny4 = nmny4;
    }

    public String getVperiod() {
		return vperiod;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}

	public Integer getIseason() {
		return iseason;
	}

	public void setIseason(Integer iseason) {
		this.iseason = iseason;
	}

	public String getCorpcode() {
		return corpcode;
	}

	public void setCorpcode(String corpcode) {
		this.corpcode = corpcode;
	}

	public String getVmanagername() {
		return vmanagername;
	}

	public void setVmanagername(String vmanagername) {
		this.vmanagername = vmanagername;
	}

	public String getVprovname() {
		return vprovname;
	}

	public void setVprovname(String vprovname) {
		this.vprovname = vprovname;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getCorpname() {
		return corpname;
	}
	
	public String getVyear() {
		return vyear;
	}

	public DZFDouble getNrebatemny() {
		return nrebatemny;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public void setVyear(String vyear) {
		this.vyear = vyear;
	}

	public void setNrebatemny(DZFDouble nrebatemny) {
		this.nrebatemny = nrebatemny;
	}

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
	}

	@Override
	public String getPKFieldName() {
		return "pk_rebate";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "cn_rebate";
	}

}
