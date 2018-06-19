package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.CustCountVO;
import com.dzf.model.channel.report.CustManageRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface ICustManageRep {
	/**
	 * 查询
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CustManageRepVO> query(QryParamVO paramvo) throws DZFWarpException, IllegalAccessException, Exception;
	
	/**
	 * 查询行业排序
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CustCountVO> queryIndustry(QryParamVO paramvo) throws DZFWarpException;

}
