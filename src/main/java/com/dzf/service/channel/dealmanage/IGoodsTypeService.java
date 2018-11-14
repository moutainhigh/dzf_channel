package com.dzf.service.channel.dealmanage;

import java.util.List;

import com.dzf.model.channel.stock.GoodsTypeVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.pub.DZFWarpException;

public interface IGoodsTypeService {
	
	public List<GoodsTypeVO> query() throws DZFWarpException;
	
	public void save(GoodsTypeVO vo) throws DZFWarpException;
	
	public void delete(GoodsTypeVO vo) throws DZFWarpException;
	
	/**
	 * 查询商品下拉
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ComboBoxVO> queryComboBox() throws DZFWarpException;
}
