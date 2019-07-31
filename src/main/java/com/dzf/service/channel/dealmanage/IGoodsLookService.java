package com.dzf.service.channel.dealmanage;

import com.dzf.model.channel.dealmanage.GoodsVO;
import com.dzf.pub.DZFWarpException;

import java.util.List;


public interface IGoodsLookService {
	
	
	/**
	 * 查询数据行数
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(GoodsVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询数据
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<GoodsVO> query(GoodsVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询商品明细
	 * @param gid
	 * @return
	 * @throws DZFWarpException
	 */
	public GoodsVO queryByID(String gid) throws DZFWarpException;
	
}
