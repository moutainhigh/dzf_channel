package com.dzf.service.channel.sale;

import java.util.List;

import com.dzf.model.channel.sale.SaleSetVO;
import com.dzf.pub.DZFWarpException;

public interface ISaleSetService {
	
	public void save(SaleSetVO vo) throws DZFWarpException; 
	
	public SaleSetVO query(SaleSetVO vo) throws DZFWarpException;
	
	public List<SaleSetVO> queryHistory(SaleSetVO vo) throws DZFWarpException;
}
