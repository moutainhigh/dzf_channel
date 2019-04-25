package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.ManagerVO;
import com.dzf.pub.DZFWarpException;

public interface IChannelStatisService {
	
	public List<ManagerVO> query(ManagerVO vo) throws DZFWarpException;
	
	/**
	 * 明细查询
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ManagerVO> queryDetail(ManagerVO vo) throws DZFWarpException;

}
