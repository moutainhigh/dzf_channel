package com.dzf.service.branch.setup;

import java.util.List;

import com.dzf.model.branch.setup.BranchInstSetupVO;
import com.dzf.model.branch.setup.ManagerSetupVO;
import com.dzf.pub.DZFWarpException;

public interface IManagerSetupService {
	
	/**
	 * 查询总行数
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(ManagerSetupVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询数据
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ManagerSetupVO> query(ManagerSetupVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询所有数据，用于用户名称或账号过滤
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ManagerSetupVO> queryAll(ManagerSetupVO pamvo) throws DZFWarpException;
	
	/**
	 * 通过主键查询数据
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public ManagerSetupVO queryById(ManagerSetupVO pamvo) throws DZFWarpException;

	/**
	 * 保存
	 * @param upvo
	 * @param pk_corp
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public void save(ManagerSetupVO upvo, String pk_corp) throws DZFWarpException;
	
	/**
	 * 查询分部机构
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BranchInstSetupVO> queryBranch() throws DZFWarpException;
}
