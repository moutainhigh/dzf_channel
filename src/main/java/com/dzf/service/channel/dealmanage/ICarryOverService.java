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
	 *   判断确认时间，是否有成本结转，有则
	 *   						type:1(不可确认)（待确认的订单、待确认的入库单、待确认的其他出库单）
	 *   						type:2(不可取消确认)（已确认的订单、已确认的入库单、已确认的其他出库单）
	 * @param confirmTime
	 * @param type
	 * @throws DZFWarpException
	 */
	public void checkIsOper(DZFDateTime confirmTime,Integer type) throws DZFWarpException;
	
	/**
	 * 获取某个时间的期初余额  按照月
	 * @param byTime(截止)
	 * @return
	 * @throws DZFWarpException
	 */
	public List<StockOutInMVO> queryBalanceMonth(String byTime,String gids) throws DZFWarpException;
	
	/**
	 * 获取某个时间的期初余额  按照条
	 * @param byTime(截止)
	 * @return
	 * @throws DZFWarpException
	 */
	public List<StockOutInMVO> queryBalanceItem(String byTime,String gids) throws DZFWarpException;
	
}
