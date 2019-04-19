package com.dzf.service.channel.matmanage;

import java.util.List;

import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.channel.matmanage.MaterielStockInVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IMatStockInService {

	/**
	 * 查询所有物料
	 * @return
	 */
	List<MaterielFileVO> queryComboBox() throws DZFWarpException;

	/**
	 * 新增入库单
	 * @param data
	 * @param uservo
	 */
	void saveStockIn(MaterielStockInVO data, UserVO uservo) throws DZFWarpException;

	/**
	 * 查询数据条数
	 * @param pamvo
	 * @return
	 */
	int queryTotalRow(MaterielStockInVO pamvo) throws DZFWarpException;

	/**
	 * 查询数据
	 * @param pamvo
	 * @param uservo
	 * @return
	 */
	List<MaterielStockInVO> query(MaterielStockInVO pamvo, UserVO uservo) throws DZFWarpException;

	/**
	 * 根据主键查询数据
	 * @param id
	 * @return
	 */
	MaterielStockInVO queryDataById(String id) throws DZFWarpException;

	/**
	 * 删除入库单
	 * @param pamvo
	 */
	void delete(MaterielStockInVO pamvo) throws DZFWarpException;

}
