package com.dzf.service.channel.report;

import java.util.List;
import java.util.Map;

import com.dzf.model.channel.report.FinanceDetailVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IAccountQryService {

	/**
	 * 查询条数
	 * @param paramvo
	 * @param loginUserInfo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(QryParamVO pamvo, UserVO uvo) throws DZFWarpException;

	/**
	 * 查询
	 * @param paramvo
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<FinanceDetailVO> query(QryParamVO pamvo, UserVO uvo) throws DZFWarpException;
	
	/**
	 * 查询所有数据
	 * @param paramvo
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<FinanceDetailVO> queryAllData(QryParamVO pamvo, UserVO uvo) throws DZFWarpException;
	
	/**
	 * 查询记账状态月数据 （分部管理系统使用）
	 * @param corpks
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, String> queryJzMap(String[] corpks, String period) throws DZFWarpException;
	
}
