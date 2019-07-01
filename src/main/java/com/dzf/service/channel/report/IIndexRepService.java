package com.dzf.service.channel.report;

import com.dzf.model.channel.report.MonthBusimngVO;
import com.dzf.model.channel.report.WeekBusimngVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IIndexRepService {

	/**
	 * 查询本周业务情况
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public WeekBusimngVO queryBusiByWeek(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询本月业务情况
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public MonthBusimngVO queryBusiByMonth(QryParamVO paramvo) throws DZFWarpException;
}
