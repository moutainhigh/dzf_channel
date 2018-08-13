package com.dzf.service.channel.dealmanage;

import java.util.List;

import com.dzf.model.channel.dealmanage.GoodsBillVO;
import com.dzf.pub.DZFWarpException;

/**
 * 加盟商订单
 * @author zy
 *
 */
public interface IChannelOrderService {

	/**
	 * 查询数据行数
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(GoodsBillVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<GoodsBillVO> query(GoodsBillVO pamvo) throws DZFWarpException;
	
}
