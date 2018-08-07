package com.dzf.service.channel.dealmanage;

import java.util.List;

import com.dzf.model.channel.dealmanage.GoodsVO;
import com.dzf.pub.DZFWarpException;

public interface IGoodsManageService {

	/**
	 * 查询数据行数
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(GoodsVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<GoodsVO> query(GoodsVO pamvo) throws DZFWarpException;
}
