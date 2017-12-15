package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.DebitQueryVO;
import com.dzf.pub.DZFWarpException;

public interface IDebitQueryService {
	
	/**
	 * 查询datagrid的表头数据
	 * @param qvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DebitQueryVO> queryHeader(DebitQueryVO qvo) throws DZFWarpException;
	
	/**
	 * 列表查询
	 * @param vo
	 * @param user
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DebitQueryVO> query(DebitQueryVO vo) throws DZFWarpException;
	
	
}
