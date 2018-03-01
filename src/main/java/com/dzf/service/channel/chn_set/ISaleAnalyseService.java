package com.dzf.service.channel.chn_set;

import java.util.ArrayList;
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
	
	/**
	 * 根据大区进行查询其下面的省市(目前只适用于渠道)
	 * @param name
	 * @return
	 * @throws DZFWarpException
	 */
	public ArrayList queryProvince(String name) throws DZFWarpException;
	
	
}
