package com.dzf.service.channel.report;

import com.dzf.model.channel.report.AchievementVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

/**
 * 业绩分析接口
 * @author zy
 *
 */
public interface IAchievementService {

	/**
	 * 查询业绩环比
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public AchievementVO queryLine(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询业绩环比
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public AchievementVO queryChart(QryParamVO paramvo) throws DZFWarpException;
}
