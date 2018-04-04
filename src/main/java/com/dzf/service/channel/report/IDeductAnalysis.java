package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.DeductAnalysisVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IDeductAnalysis {
	
	/**
	 * 查询金额数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DeductAnalysisVO> query(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询金额排序数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DeductAnalysisVO> queryMnyOrder(QryParamVO paramvo) throws DZFWarpException;

}
