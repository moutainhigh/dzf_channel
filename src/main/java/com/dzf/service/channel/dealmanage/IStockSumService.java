package com.dzf.service.channel.dealmanage;

import java.util.List;

import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.channel.dealmanage.StockSumVO;

public interface IStockSumService {

	Integer queryTotalRow(StockSumVO qvo);

	List<StockSumVO> query(StockSumVO qvo);

	List<GoodsBoxVO> queryComboBox();

}
