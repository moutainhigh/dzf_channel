package com.dzf.service.channel.dealmanage;

import java.util.List;

import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.sys.sys_power.UserVO;

public interface IStockOutInService {

	List<Long> queryTotalRow(StockOutInMVO qvo);

	List<StockOutInMVO> query(StockOutInMVO qvo);

	List<GoodsBoxVO> queryComboBox();

}
