package com.dzf.service.channel.contract;

import java.util.List;

import com.dzf.model.channel.contract.ChangeApplyVO;
import com.dzf.model.pub.ComboBoxVO;
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
	
	/**
	 * 查询待审核人员
	 * @param pamvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ComboBoxVO> queryAuditer(ChangeApplyVO pamvo, UserVO uservo)throws DZFWarpException;
	
	/**
	 * 通过主键查询申请信息
	 * 
	 * @param pamvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	public ChangeApplyVO queryById(ChangeApplyVO pamvo, UserVO uservo) throws DZFWarpException;
	
	/**
	 * 变更保存
	 * @param datavo
	 * @param uservo
	 * @throws DZFWarpException
	 */
	public void updateChange(ChangeApplyVO datavo, UserVO uservo) throws DZFWarpException;
	
}
