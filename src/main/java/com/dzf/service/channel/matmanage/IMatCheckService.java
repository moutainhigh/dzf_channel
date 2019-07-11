package com.dzf.service.channel.matmanage;

import java.util.List;

import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.pub.QryParamVO;
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

	/**
	 * 查询数据条数
	 * @param qvo
	 * @param pamvo
	 * @return
	 */
	int queryTotalRow(QryParamVO qvo, MatOrderVO pamvo);

	/**
	 * 查询数据
	 * @param qvo
	 * @param pamvo
	 * @param uservo
	 * @return
	 */
	List<MatOrderVO> query(QryParamVO qvo, MatOrderVO pamvo, UserVO uservo);

	/**
	 * 根据id查询详情
	 * @param vo
	 * @param id
	 * @param uservo
	 * @param stype
	 * @return
	 */
	MatOrderVO queryDataById(MatOrderVO vo, String id, UserVO uservo, String stype);

	/**
	 * 查询上一次驳回原因
	 * @param uservo
	 * @return
	 */
	String queryLastReason(UserVO uservo);

}
