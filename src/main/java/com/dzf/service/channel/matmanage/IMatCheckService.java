package com.dzf.service.channel.matmanage;

import java.util.List;
import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException; ;

public interface IMatCheckService {

	/**
	 * 查询渠道商下拉
	 * @param uservo
	 * @return
	 */
	List<ChnAreaBVO> queryComboBox(UserVO uservo)  throws DZFWarpException;

	/**
	 * 审核，反审核
	 */
	void updateStatusById(MatOrderVO vo, UserVO uservo, MatOrderBVO[] bvos)  throws DZFWarpException;

	/**
	 * 根据主键查询数据
	 * @param pk_materielbill
	 * @return
	 */
	MatOrderVO queryById(String pk_materielbill)  throws DZFWarpException;

	/**
	 * 查询申请人和审核人
	 * @param uservo
	 * @param mid
	 * @return
	 */
	MatOrderVO queryUserData(UserVO uservo, String mid);

}
