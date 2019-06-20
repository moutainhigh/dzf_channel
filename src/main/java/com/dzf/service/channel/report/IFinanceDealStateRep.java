package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.FinanceDealStateRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IFinanceDealStateRep {

	/**
	 * 查询
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<FinanceDealStateRepVO> query(QryParamVO paramvo) throws  DZFWarpException;
	
}
