package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.CustNumMoneyRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface ICustNumMoneyRep {

	/**
	 * 查询
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CustNumMoneyRepVO> query(QryParamVO paramvo) throws DZFWarpException;
}
