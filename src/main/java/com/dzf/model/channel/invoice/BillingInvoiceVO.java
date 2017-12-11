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
	
	@FieldAlias("dtotalmny")
	private DZFDouble debittotalmny;//累计扣款金额
	
    @FieldAlias("btotalmny")
    private DZFDouble billtotalmny;//累计开票金额
    
    private DZFDouble noticketmny;//未开票金额
	
    private String[] corps;//渠道商ids
    
    private String bdate;//查询开始时间

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

    public DZFDouble getDebittotalmny() {
        return debittotalmny;
    }

    public void setDebittotalmny(DZFDouble debittotalmny) {
        this.debittotalmny = debittotalmny;
    }

    public DZFDouble getBilltotalmny() {
        return billtotalmny;
    }

    public void setBilltotalmny(DZFDouble billtotalmny) {
        this.billtotalmny = billtotalmny;
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
