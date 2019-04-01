package com.dzf.model.channel.invoice;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 加盟商开票查询VO
 * @author dzf
 *
 */
public class BillingInvoiceVO extends SuperVO{
	
	private static final long serialVersionUID = -6057771983096487432L;

	
	@FieldAlias("corpid")
	private String pk_corp;//公司主键
	
	@FieldAlias("ccode")
    private String corpcode;//单位编码
	
	@FieldAlias("cname")
	private String corpname;//单位名称
	
//	@FieldAlias("dtotalmny")
//	private DZFDouble debittotalmny;//累计扣款金额
	
	@FieldAlias("dconmny")
	private DZFDouble debitconmny;//累计合同扣款金额
	
	@FieldAlias("dbuymny")
	private DZFDouble debitbuymny;//累计商品扣款金额
	
//    @FieldAlias("btotalmny")
//    private DZFDouble billtotalmny;//累计开票金额
    
    @FieldAlias("bconmny")
    private DZFDouble billconmny;//累计合同开票金额
    
    @FieldAlias("bbuymny")
    private DZFDouble billbuymny;//累计商品开票金额
    
    private DZFDouble noticketmny;//未开票金额 (合同扣款未开票金额)
    
    @FieldAlias("notbmny")
    private DZFDouble notbuymny;//商品购买未开票金额
	
    private String[] corps;//渠道商ids
    
    private String bdate;//查询开始时间
    
	@FieldAlias("uid")
	private String cuserid;// 用户ID
    
	@FieldAlias("aname")
    private String areaname;//大区名称

    @FieldAlias("provname")
	public String vprovname;//省市名称

	@FieldAlias("ovince")
	public Integer vprovince;// 省市
	
	@FieldAlias("mid")
	private String vmanager;//渠道经理
	
	@FieldAlias("oid")
	private String voperater;//渠道运营

	public String getVmanager() {
		return vmanager;
	}

	public void setVmanager(String vmanager) {
		this.vmanager = vmanager;
	}

	public String getVoperater() {
		return voperater;
	}

	public void setVoperater(String voperater) {
		this.voperater = voperater;
	}

	public DZFDouble getBillconmny() {
		return billconmny;
	}

	public void setBillconmny(DZFDouble billconmny) {
		this.billconmny = billconmny;
	}

	public DZFDouble getBillbuymny() {
		return billbuymny;
	}

	public void setBillbuymny(DZFDouble billbuymny) {
		this.billbuymny = billbuymny;
	}

	public DZFDouble getNotbuymny() {
		return notbuymny;
	}

	public void setNotbuymny(DZFDouble notbuymny) {
		this.notbuymny = notbuymny;
	}

	public DZFDouble getDebitconmny() {
		return debitconmny;
	}

	public void setDebitconmny(DZFDouble debitconmny) {
		this.debitconmny = debitconmny;
	}

	public DZFDouble getDebitbuymny() {
		return debitbuymny;
	}

	public void setDebitbuymny(DZFDouble debitbuymny) {
		this.debitbuymny = debitbuymny;
	}

	public String getBdate() {
        return bdate;
    }

    public void setBdate(String bdate) {
        this.bdate = bdate;
    }

    public String[] getCorps() {
        return corps;
    }

    public void setCorps(String[] corps) {
        this.corps = corps;
    }

    public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getVprovname() {
		return vprovname;
	}

	public void setVprovname(String vprovname) {
		this.vprovname = vprovname;
	}

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
	}

    public DZFDouble getNoticketmny() {
        return noticketmny;
    }

    public void setNoticketmny(DZFDouble noticketmny) {
        this.noticketmny = noticketmny;
    }

    public String getCorpcode() {
		return corpcode;
	}

	public void setCorpcode(String corpcode) {
		this.corpcode = corpcode;
	}
	
	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}


	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
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
