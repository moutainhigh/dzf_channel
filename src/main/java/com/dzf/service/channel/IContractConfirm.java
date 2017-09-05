package com.dzf.service.channel;

import java.util.List;

import com.dzf.model.channel.ContractConfrimVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IContractConfirm {

	/**
	 * 查询数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ContractConfrimVO> query(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 合同确认成功/确认失败
	 * @param confrimVOs
	 * @param status
	 * @throws DZFWarpException
	 */
	public ContractConfrimVO updateConfStatus(ContractConfrimVO[] confrimVOs, Integer status) throws DZFWarpException;
	
	/**
	 * 查询待扣款数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public ContractConfrimVO queryDebitData(ContractConfrimVO paramvo) throws DZFWarpException;
	
	/**
	 * 收款确认/取消收款
	 * @param paramvo
	 * @param opertype
	 * @return
	 * @throws DZFWarpException
	 */
	public ContractConfrimVO updateDeductData(ContractConfrimVO paramvo, Integer opertype, String cuserid) throws DZFWarpException;
}
