package com.dzf.service.channel.dealmanage;

import java.util.List;

import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.pub.DZFWarpException;

public interface IStockOutInService {


	List<StockOutInMVO> query(StockOutInMVO qvo) throws DZFWarpException;

	List<GoodsBoxVO> queryComboBox() throws DZFWarpException;
	
}
