package com.dzf.service.channel.dealmanage;

import java.util.List;

import com.dzf.model.channel.dealmanage.GoodsBillMxVO;
import com.dzf.model.channel.dealmanage.GoodsBillVO;
import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.pub.DZFWarpException;

/**
 * 加盟商订单明细
 * @author yy
 *
 */
public interface IChannelOrderMxService {

	/**
	 * 查询明细行数
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public int queryTotalRowMx(GoodsBillMxVO paramvo);

	/**
	 * 查询数据明细
	 * @param paramvo
	 * @return
	 */
	public List<GoodsBillMxVO> querymx(GoodsBillMxVO paramvo);

	/**
	 * 查询商品下拉
	 * @return
	 */
	public List<GoodsBoxVO> queryComboBox();
	
}
