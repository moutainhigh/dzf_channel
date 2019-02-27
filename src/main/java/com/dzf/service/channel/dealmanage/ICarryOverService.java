package com.dzf.service.channel.dealmanage;

import java.util.List;

import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.channel.stock.CarryOverVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.lang.DZFDateTime;

public interface ICarryOverService {
	
	public List<CarryOverVO> query(QryParamVO qvo) throws DZFWarpException;
	
	public void save(CarryOverVO vo) throws DZFWarpException;
	
	/**
	 * 判断确认时间，是否有成本结转，有则不可取消确认
	 * 				（已确认的订单、已确认的入库单、已确认的其他出库单）
	 * @param confirmTime
	 * @throws DZFWarpException
	 */
	public void checkIsCancel(DZFDateTime confirmTime) throws DZFWarpException;
	
	/**
	 * 获取某个时间的期初余额
	 * @param byTime(截止)
	 * @return
	 * @throws DZFWarpException
	 */
	public List<StockOutInMVO> queryBalanceByTime(String byTime) throws DZFWarpException;
	
}
