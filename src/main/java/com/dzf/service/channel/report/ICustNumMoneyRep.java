package com.dzf.service.channel.report;

import java.util.List;
import java.util.Map;

import com.dzf.model.channel.report.CustCountVO;
import com.dzf.model.channel.report.CustNumMoneyRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.lang.DZFDouble;

public interface ICustNumMoneyRep {

	/**
	 * 查询新增客户相关信息
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CustNumMoneyRepVO> query(QryParamVO paramvo) throws  DZFWarpException;
	
	/**
	 * 查询续费客户相关信息
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CustNumMoneyRepVO> queryRenew(QryParamVO paramvo) throws  DZFWarpException;
	
	/**
	 * 客户查询
	 * @param paramvo
	 * @param qrytype
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CustCountVO> queryCustNum(QryParamVO paramvo, Integer qrytype, List<String> corplist) throws DZFWarpException;
	
	/**
	 * 客户计算
	 * @param custlist
	 * @param qrytype
	 * @param corplist
	 * @param countcorplist
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, CustNumMoneyRepVO> countCustNumByType(List<CustCountVO> custlist, Integer qrytype,
			List<String> corplist, List<String> countcorplist) throws DZFWarpException;
	
	/**
	 * 客户占比计算
	 * @param num1
	 * @param num2
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getCustRate(Integer num1, Integer num2) throws DZFWarpException;
	
}
