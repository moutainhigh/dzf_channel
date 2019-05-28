package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.ManagerVO;
import com.dzf.pub.DZFWarpException;

public interface IRegionalManService {
	
	/**
	 * 列表查询
	 * @param vo
	 * @param user
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ManagerVO> query(ManagerVO vo) throws DZFWarpException;
	
}
