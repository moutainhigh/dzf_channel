package com.dzf.service.channel.report;

import com.dzf.model.channel.report.WeekBusimngVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IIndexRep {

	/**
	 * 查询本周业务情况
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public WeekBusimngVO queryThisWeek(QryParamVO paramvo) throws DZFWarpException;
}
