package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.DeductAnalysisVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IDeductAnalysis {
	
	/**
	 * 查询
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DeductAnalysisVO> query(QryParamVO paramvo) throws DZFWarpException;

}
