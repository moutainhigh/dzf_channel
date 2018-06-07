package com.dzf.service.channel;

import java.util.List;

import com.dzf.model.channel.payment.ChnBalanceRepVO;
import com.dzf.model.channel.payment.ChnDetailRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IChnPayBalanceService {

	/**
	 * 查询数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ChnBalanceRepVO> query(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询明细数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ChnDetailRepVO> queryDetail(QryParamVO paramvo) throws DZFWarpException;
}
