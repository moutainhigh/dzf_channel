package com.dzf.service.channel.matmanage;

import java.util.List;
import java.util.Map;

import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
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
	MatOrderVO queryIsExist(String sTmp);

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
	MatOrderVO getFullVO(MatOrderVO excelvo, String corpname, String managname, String date, String logname, UserVO uservo);

	/**
	 * 导入保存数据
	 * @param vos
	 * @return
	 */
	MatOrderVO[] saveImoprt(MatOrderVO[] vos);

}
