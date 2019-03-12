package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.GoodsSalesAnalysisVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IGoodsSalesAnalysisService {

	/**
	 * 查询
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<GoodsSalesAnalysisVO> query(QryParamVO pamvo) throws  DZFWarpException;
}
