package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.CustNumMoneyRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IRenewAchieveService {

	/**
	 * 查询续费客户相关信息
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CustNumMoneyRepVO> queryRenew(QryParamVO paramvo) throws  DZFWarpException;
}
