package com.dzf.service.channel;

import java.util.List;

import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IChnPayConfService {

	/**
	 * 查询数据行数
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ChnPayBillVO> query(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 通过主键查询单个VO
	 * @param cid
	 * @return
	 * @throws DZFWarpException
	 */
	public ChnPayBillVO queryByID(String cid) throws DZFWarpException;
	
	/**
	 * 确认收款、取消确认、驳回 单条数据操作
	 * @param billvo
	 * @param opertype
	 * @param cuserid
	 * @param vreason
	 * @return
	 * @throws DZFWarpException
	 */
	public ChnPayBillVO updateOperate(ChnPayBillVO billvo, Integer opertype, String cuserid,String vreason) throws DZFWarpException;
	
	/**
	 * 校验数据状态
	 * @param billvo
	 * @return
	 * @throws DZFWarpException
	 */
	public void checkBillStatus(ChnPayBillVO billvo) throws DZFWarpException;
}
