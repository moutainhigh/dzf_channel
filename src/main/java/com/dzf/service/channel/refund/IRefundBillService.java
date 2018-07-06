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
	 * @return
	 * @throws DZFWarpException
	 */
	public RefundBillVO queryRefundMny(RefundBillVO paramvo) throws DZFWarpException;
	
	/**
	 * 保存前校验
	 * @param datavo
	 * @return
	 * @throws DZFWarpException
	 */
	public RefundBillVO checkBeforeSave(RefundBillVO datavo) throws DZFWarpException;
}
