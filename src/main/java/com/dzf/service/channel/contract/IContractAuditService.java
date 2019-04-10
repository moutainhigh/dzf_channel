package com.dzf.service.channel.contract;

import java.util.List;

import com.dzf.model.channel.contract.ChangeApplyVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IContractAuditService {

	/**
	 * 查询数据总行数
	 * 
	 * @param pamvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalNum(ChangeApplyVO pamvo, UserVO uservo) throws DZFWarpException;

	/**
	 * 查询数据
	 * 
	 * @param pamvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ChangeApplyVO> query(ChangeApplyVO pamvo, UserVO uservo) throws DZFWarpException;
	
}
