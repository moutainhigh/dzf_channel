package com.dzf.service.channel.matmanage;

import java.util.List;

import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.channel.matmanage.MaterielStockInVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;


public interface IMatFileService {

	/**
	 * 校验物料名称唯一性
	 * @param name
	 * @return
	 */
	Boolean queryMatName(String name)  throws DZFWarpException;

	/**
	 * 新增物料
	 * @param data
	 * @param uservo
	 */
	void saveMatFile(MaterielFileVO data, UserVO uservo)  throws DZFWarpException;

	/**
	 * 查询数据条数
	 * @param pamvo
	 * @return
	 */
	int queryTotalRow(MaterielFileVO pamvo)  throws DZFWarpException;

	/**
	 * 查询数据
	 * @param pamvo
	 * @param uservo
	 * @return
	 */
	List<MaterielFileVO> query(MaterielFileVO pamvo, UserVO uservo)  throws DZFWarpException;

	/**
	 * 修改封存状态
	 * @param mvo
	 * @param type
	 */
	void updateStatus(MaterielFileVO mvo, Integer type)  throws DZFWarpException;

	/**
	 * 是否封存
	 * @param ids
	 * @return
	 */
	List<MaterielFileVO> querySsealById(String ids)  throws DZFWarpException;

	/**
	 * 根据主键查询数据
	 * @param id
	 * @return
	 */
	MaterielFileVO queryDataById(String id)  throws DZFWarpException;

	/**
	 * 判断是否已经入库
	 * @param ids
	 * @return
	 */
	//List<MaterielStockInVO> queryIsRk(String ids)  throws DZFWarpException;

	/**
	 * 删除物料档案
	 * @param mvo
	 */
	void deleteWl(MaterielFileVO mvo)  throws DZFWarpException;

	/**
	 * 下拉查询所有物料档案
	 * @param pamvo
	 * @param uservo
	 * @return
	 */
	List<MaterielFileVO> queryMatFile(MaterielFileVO pamvo, UserVO uservo)  throws DZFWarpException;

	
}
