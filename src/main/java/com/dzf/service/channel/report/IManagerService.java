package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.ManagerVO;
import com.dzf.pub.DZFWarpException;

public interface IManagerService {
	
	/**
	 * 明细查询(已审核)
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ManagerVO> queryDetail(ManagerVO vo) throws DZFWarpException;
	
	/**
	 * 明细查询(未审核)
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ManagerVO> queryWDetail(ManagerVO vo) throws DZFWarpException;
	
}
