package com.dzf.service.branch.reportmanage;

import java.util.List;

import com.dzf.model.branch.reportmanage.QueryContractVO;
import com.dzf.model.branch.setup.BranchInstSetupVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IBranchExpireContractService {


	/**
	 * 列表查询
	 * @param qvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	List<QueryContractVO> query(QueryContractVO qvo, UserVO uservo) throws DZFWarpException;

	/**
	 * 查询所有机构名称
	 * @param uservo 
	 * @return
	 */
	List<BranchInstSetupVO> queryComboBox(UserVO uservo);

	
}
