package com.dzf.service.channel.rebate;

import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.pub.DZFWarpException;

public interface IRebateConfService {

	/**
	 * 确认/驳回更新
	 * @param data
	 * @param pk_corp
	 * @param opertype  操作类型：1：驳回修改；2：确认通过；3：取消确认；
	 * @return
	 * @throws DZFWarpException
	 */
	
	public RebateVO updateConf(RebateVO data, String pk_corp, Integer opertype) throws DZFWarpException;
}
