package com.dzf.service.channel.rebate;

import com.dzf.model.channel.rebate.RebateVO;
import com.dzf.pub.DZFWarpException;

public interface IRebateAuditService {

	/**
	 * 审批/驳回更新
	 * @param data
	 * @param pk_corp
	 * @param opertype  操作类型：1：驳回修改；4：审核通过；
	 * @return
	 * @throws DZFWarpException
	 */
	
	public RebateVO updateAudit(RebateVO data, String pk_corp, Integer opertype) throws DZFWarpException;
}
