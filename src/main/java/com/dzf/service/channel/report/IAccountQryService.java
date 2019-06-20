package com.dzf.service.channel.report;

import java.util.List;

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
	
}
