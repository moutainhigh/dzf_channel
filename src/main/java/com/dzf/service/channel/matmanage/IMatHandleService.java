package com.dzf.service.channel.matmanage;

import java.util.List;

import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IMatHandleService {

	/**
	 * 查询快递公司下拉
	 * @return
	 */
	List<MatOrderVO> queryComboBox() throws DZFWarpException;

	/**
	 * 校验加盟商是否存在
	 * @param sTmp
	 * @return
	 */
	MatOrderVO queryIsExist(String sTmp) throws DZFWarpException;

	/**
	 * 补全数据
	 * @param excelvo
	 * @param corpname
	 * @param managname 
	 * @param date 
	 * @param logname 
	 * @param uservo 
	 * @return
	 */
	MatOrderVO getFullVO(MatOrderVO excelvo, String corpname, 
			String managname, String date, String logname, UserVO uservo) throws DZFWarpException;

	/**
	 * 导入保存数据
	 * @param vos
	 * @return
	 */
	MatOrderVO[] saveImoprt(MatOrderVO[] vos) throws DZFWarpException;

	/**
	 * 查询所有物料
	 * @return
	 */
	List<String> queryAllMatName() throws DZFWarpException;

	/**
	 * 查询数据
	 * @param qvo
	 * @param pamvo
	 * @param uservo
	 * @return
	 */
	List<MatOrderVO> query(QryParamVO qvo, MatOrderVO pamvo, UserVO uservo) throws DZFWarpException;

}
