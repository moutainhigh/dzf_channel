package com.dzf.service.demp.channel;

import java.util.List;

import com.dzf.model.demp.channel.ChnBalanceVO;
import com.dzf.model.demp.channel.ChnDetailVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IChnPayBalanceService {

	/**
	 * 查询数据行数
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ChnBalanceVO> query(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询明细行数
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryDetailTotal(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询明细数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ChnDetailVO> queryDetail(QryParamVO paramvo) throws DZFWarpException;
}
