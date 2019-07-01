package com.dzf.service.channel.matmanage;

import java.util.List;

import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IMatApplyService {

	/**
	 * 查询数据条数
	 * @param qvo
	 * @param pamvo
	 * @return
	 */
	public int queryTotalRow(QryParamVO qvo,MatOrderVO pamvo) throws DZFWarpException;

	/**
	 * 查询数据
	 * @param qvo
	 * @param pamvo
	 * @param uservo
	 * @param stype
	 * @return
	 */
	public List<MatOrderVO> query(QryParamVO qvo, MatOrderVO pamvo, UserVO uservo) throws DZFWarpException;

	/**
	 * 根据加盟商查询申请单信息
	 * @param corpid
	 * @return
	 */
	public MatOrderVO showDataByCorp(String corpid) throws DZFWarpException;

	/**
	 * 新增申请单
	 * @param vo
	 * @param uservo
	 * @param bvos
	 * @param type
	 * @param stype
	 * @param kind 
	 * @return
	 */
	public String saveApply(MatOrderVO vo, UserVO uservo, MatOrderBVO[] bvos, String stype, String kind) throws DZFWarpException;

	/**
	 * 根据主键查询数据
	 * @param vo
	 * @param id
	 * @param uservo
	 * @return
	 */
	public MatOrderVO queryDataById(MatOrderVO vo, String id, UserVO uservo) throws DZFWarpException;

	/**
	 * 删除申请单
	 * @param param
	 */
	public void delete(MatOrderVO param) throws DZFWarpException;

	/**
	 * 查询符合条件的加盟商
	 * @param uservo
	 * @return
	 */
	public List<CorpVO> queryChannel(UserVO uservo) throws DZFWarpException;

	/**
	 * 查询当前登录人
	 * @param uservo
	 * @return
	 */
	public MatOrderVO queryUserData(UserVO uservo);

	
	/**
	 * 修改保存
	 * @param vo
	 */
	public void editSave(MatOrderVO vo);

}
