package com.dzf.service.channel.manager;

import java.util.List;

import com.dzf.model.channel.manager.ManagerVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.pub.DZFWarpException;

public interface IManagerService {
	
	/**
	 * 列表查询
	 * @param vo
	 * @param user
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ManagerVO> query(ManagerVO vo) throws DZFWarpException;
	
	/**
	 * 查询框上的渠道经理参照查询
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ComboBoxVO> queryManager(Integer type,String cuserid)throws DZFWarpException;
	
	/**
	 * 查询框上的大区参照查询
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ComboBoxVO> queryArea()throws DZFWarpException;
	
}
