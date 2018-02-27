package com.dzf.service.channel.rebate;

import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.pub.DZFWarpException;

public interface IRebateConfService {

	/**
	 * 确认更新
	 * @param data
	 * @param pk_corp
	 * @param opertype  操作类型：1：确认通过；4：驳回修改；-1:
	 * @return
	 * @throws DZFWarpException
	 */
	
	public RebateVO updateConf(RebateVO data, String pk_corp, Integer opertype) throws DZFWarpException;
}
