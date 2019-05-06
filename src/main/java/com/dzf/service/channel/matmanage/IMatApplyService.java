package com.dzf.service.channel.matmanage;

import java.util.List;

import com.dzf.model.channel.matmanage.MatOrderBVO;
import com.dzf.model.channel.matmanage.MatOrderVO;
import com.dzf.model.channel.matmanage.MaterielFileVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;

public interface IMatApplyService {

	/**
	 * 查询数据条数
	 * @param qvo
	 * @param pamvo
	 * @param stype
	 * @return
	 */
	int queryTotalRow(QryParamVO qvo,MatOrderVO pamvo, String stype) throws DZFWarpException;

	/**
	 * 查询数据
	 * @param qvo
	 * @param pamvo
	 * @param uservo
	 * @param stype
	 * @return
	 */
	List<MatOrderVO> query(QryParamVO qvo, MatOrderVO pamvo, UserVO uservo, String stype) throws DZFWarpException;

	/**
	 * 查询申请的物料信息
	 * @param pamvo
	 * @return
	 */
	List<MatOrderBVO> queryNumber(MatOrderVO pamvo) throws DZFWarpException;

	/**
	 * 没有申请，查询所有已经启用的物料
	 * @return
	 */
	List<MaterielFileVO> queryMatFile() throws DZFWarpException;

	/**
	 * 查询所有的省份
	 * @return
	 */
	List<MatOrderVO> queryAllProvince() throws DZFWarpException;

	/**
	 * 查询省份下的市
	 * @param pid
	 * @return
	 */
	List<MatOrderVO> queryCityByProId(Integer pid) throws DZFWarpException;

	/**
	 * 查询市下的县
	 * @param cid
	 * @return
	 */
	List<MatOrderVO> queryAreaByCid(Integer cid) throws DZFWarpException;

	/**
	 * 根据加盟商查询申请单信息
	 * @param corpid
	 * @return
	 */
	MatOrderVO showDataByCorp(String corpid) throws DZFWarpException;

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
	String saveApply(MatOrderVO vo, UserVO uservo, MatOrderBVO[] bvos, String type, String stype, String kind) throws DZFWarpException;

	/**
	 * 根据主键查询数据
	 * @param vo
	 * @param id
	 * @param uservo
	 * @param type
	 * @param stype
	 * @return
	 */
	MatOrderVO queryDataById(MatOrderVO vo, String id, UserVO uservo, String type, String stype) throws DZFWarpException;

	/**
	 * 删除申请单
	 * @param param
	 */
	void delete(MatOrderVO param) throws DZFWarpException;

	/**
	 * 查询符合条件的加盟商
	 * @param uservo
	 * @return
	 */
	List<CorpVO> queryChannel(UserVO uservo) throws DZFWarpException;

	/**
	 * 查询当前登录人
	 * @param uservo
	 * @return
	 */
	MatOrderVO queryUserData(UserVO uservo);

	
	/**
	 * 修改保存
	 * @param vo
	 */
	void editSave(MatOrderVO vo);

}
