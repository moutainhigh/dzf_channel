package com.dzf.service.channel.chn_set;

import java.util.List;

import com.dzf.model.channel.sale.SaleAnalyseVO;
import com.dzf.pub.DZFWarpException;

public interface ISaleAnalyseService {
	
	/**
	 * 列表查询
	 * @param vo
	 * @param user
	 * @return
	 * @throws DZFWarpException
	 */
	public List<SaleAnalyseVO> query(SaleAnalyseVO vo) throws DZFWarpException;
	
	
}
