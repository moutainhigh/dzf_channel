package com.dzf.service.channel.refund;

import java.util.List;

import com.dzf.model.channel.contract.ContractConfrimVO;
import com.dzf.model.channel.refund.RefundBillVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IRefundBillService {
	
	/**
	 * 查询数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<RefundBillVO> query(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询数据总条数
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(QryParamVO paramvo) throws DZFWarpException;

	/**
	 * 新增保存
	 * @param data
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public RefundBillVO save(RefundBillVO datavo, String logincorp) throws DZFWarpException;
	
	/**
	 * 查询退款金额
	 * @param paramvo
	 * @param type  -1：新增时查询；1：保存前校验；2：确认前校验；
	 * @return
	 * @throws DZFWarpException
	 */
	public RefundBillVO queryRefundMny(RefundBillVO paramvo, Integer type) throws DZFWarpException;
	
	/**
	 * 保存、确认前校验
	 * @param datavo
	 * @param checktype  1：保存前校验；2：确认前校验；
	 * @return
	 * @throws DZFWarpException
	 */
	public RefundBillVO checkBeforeSave(RefundBillVO datavo, Integer checktype) throws DZFWarpException;
	
	/**
	 * 删除
	 * @param datavo
	 * @return
	 * @throws DZFWarpException
	 */
	public RefundBillVO delete(RefundBillVO datavo) throws DZFWarpException;
	
	/**
	 * 确认、取消确认-更新操作数据
	 * @param datavo
	 * @param opertype
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public RefundBillVO updateOperat(RefundBillVO datavo, Integer opertype, String cuserid) throws DZFWarpException;
}
