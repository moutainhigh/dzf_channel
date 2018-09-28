package com.dzf.service.channel.refund;

import java.util.List;

import com.dzf.model.channel.refund.RefundDetailVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IRefundDetailService {

	/**
	 * 查询数据
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<RefundDetailVO> query(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询明细数据
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<RefundDetailVO> queryDetail(QryParamVO pamvo) throws DZFWarpException;
}
