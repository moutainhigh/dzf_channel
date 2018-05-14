package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.Field.FieldAlias;
import com.dzf.pub.lang.DZFDouble;

/**
 * 合同查询VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class ContQryVO extends SuperVO {

	private static final long serialVersionUID = 5903842042917100639L;
	
    @FieldAlias("naccmny")
    private DZFDouble naccountmny; //合同代账费
    
    @FieldAlias("ndesummny")
    private DZFDouble ndedsummny;//扣款总金额
    
    @FieldAlias("period")
    private String vperiod;//显示期间

	public DZFDouble getNaccountmny() {
		return naccountmny;
	}

	public DZFDouble getNdedsummny() {
		return ndedsummny;
	}

	public String getVperiod() {
		return vperiod;
	}

	public void setNaccountmny(DZFDouble naccountmny) {
		this.naccountmny = naccountmny;
	}

	public void setNdedsummny(DZFDouble ndedsummny) {
		this.ndedsummny = ndedsummny;
	}

	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
