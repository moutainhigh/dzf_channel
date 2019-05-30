package com.dzf.service.branch.setup;

import java.util.List;

import com.dzf.model.branch.setup.BranchUserVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IBranchUserService {
	
	/**
	 * 查询用户
	 * @param qvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<UserVO> query(QryParamVO qvo) throws DZFWarpException;
	
	/**
	 * 查询单个用户，新增+修改用户
	 * @param loginUserid
	 * @param qryId
	 * @return
	 * @throws DZFWarpException
	 */
	public BranchUserVO queryByID(String loginUserid,String qryId) throws DZFWarpException;
	
	/**
	 * 保存用户
	 * @param uservo
	 * @throws DZFWarpException
	 */
	public void save(UserVO uservo) throws DZFWarpException;
	
	/**
	 * 修改用户
	 * @param uservo
	 * @throws DZFWarpException
	 */
	public void saveEdit(UserVO uservo) throws DZFWarpException;
	
	
	/**
	 * 更新用户，锁定状态
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void updateLock(UserVO vo) throws DZFWarpException;
	
}
