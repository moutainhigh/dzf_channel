package com.dzf.model.channel.report;

import com.dzf.pub.SuperVO;
import com.dzf.pub.lang.DZFDouble;

/**
 * 付款余额查询VO
 * @author zy
 *
 */
@SuppressWarnings("rawtypes")
public class BalanceRepVO extends SuperVO {

	private static final long serialVersionUID = -2021886944012677846L;
	
	private DZFDouble nbzjmny;//保证金金额
	
	private DZFDouble nyfkmny;//预付款金额

	public DZFDouble getNbzjmny() {
		return nbzjmny;
	}

	public void setNbzjmny(DZFDouble nbzjmny) {
		this.nbzjmny = nbzjmny;
	}

	public DZFDouble getNyfkmny() {
		return nyfkmny;
	}

	public void setNyfkmny(DZFDouble nyfkmny) {
		this.nyfkmny = nyfkmny;
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
