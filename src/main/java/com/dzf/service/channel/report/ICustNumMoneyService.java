package com.dzf.service.channel.report;

import java.util.List;
import java.util.Map;

import com.dzf.model.channel.report.CustNumMoneyRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.lang.DZFDouble;

public interface ICustNumMoneyService {

	/**
	 * 查询新增客户相关信息
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CustNumMoneyRepVO> query(QryParamVO paramvo) throws  DZFWarpException;
	
	/**
	 * 客户占比计算
	 * @param num1
	 * @param num2
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getCustRate(Integer num1, Integer num2) throws DZFWarpException;
	
	/**
	 * 查询客户数量
	 * @param paramvo
	 * @param corplist
	 * @param qrytype
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, Integer> queryCustNum(QryParamVO paramvo, List<String> corplist, Integer qrytype)
			throws DZFWarpException;
	
	/**
	 * 查询合同金额
	 * @param paramvo
	 * @param corplist
	 * @param qrytype
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, DZFDouble> queryContMny(QryParamVO paramvo, List<String> corplist, Integer qrytype)
			throws DZFWarpException;
	
}
