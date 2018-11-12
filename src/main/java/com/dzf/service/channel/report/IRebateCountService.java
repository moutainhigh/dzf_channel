package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.RebateCountVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IRebateCountService {
	
	/**
	 * 查询
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<RebateCountVO> query(QryParamVO paramvo) throws DZFWarpException;
}
