package com.dzf.service.channel.dealmanage;

import java.util.List;

import com.dzf.model.channel.stock.GoodsNumVO;
import com.dzf.pub.DZFWarpException;

public interface IGoodsNumService {
	
	public List<GoodsNumVO> query(GoodsNumVO qvo) throws DZFWarpException;
	
	public Integer queryTotalRow(GoodsNumVO qvo) throws DZFWarpException;
	
}
