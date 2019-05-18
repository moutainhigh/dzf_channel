package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.DataAnalysisVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

/**
 * 加盟商数据分析
 * @author zy
 *
 */
public interface IDataAnalysisService {
	
	/**
	 * 查询数据总条数
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(QryParamVO pamvo) throws DZFWarpException;

	/**
	 * 查询数据
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DataAnalysisVO> query(QryParamVO pamvo) throws DZFWarpException;
}
