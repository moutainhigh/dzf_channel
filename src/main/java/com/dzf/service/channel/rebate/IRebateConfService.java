package com.dzf.service.channel.rebate;

import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.pub.DZFWarpException;

public interface IRebateConfService {

	/**
	 * 确认更新
	 * @param data
	 * @return
	 * @throws DZFWarpException
	 */
	public RebateVO updateConf(RebateVO data) throws DZFWarpException;
}
