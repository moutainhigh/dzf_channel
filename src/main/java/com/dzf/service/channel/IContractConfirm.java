package com.dzf.service.channel;

import java.util.List;

import com.dzf.model.channel.ContractConfrimVO;
import com.dzf.model.demp.contract.ContractDocVO;
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
	
//	/**
//	 * 合同确认成功/确认失败
//	 * @param confrimVOs
//	 * @param status
//	 * @throws DZFWarpException
//	 */
//	public ContractConfrimVO updateConfStatus(ContractConfrimVO[] confrimVOs, Integer status) throws DZFWarpException;
	
	/**
	 * 查询待扣款数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public ContractConfrimVO queryDebitData(ContractConfrimVO paramvo) throws DZFWarpException;
	
	/**
	 * 扣款/驳回
	 * @param paramvo
	 * @param opertype
	 * @return
	 * @throws DZFWarpException
	 */
	public ContractConfrimVO updateDeductData(ContractConfrimVO paramvo, Integer opertype, String cuserid) throws DZFWarpException;
	
	/**
	 * 获取附件列表
	 * @param qvo
	 * @return
	 * @throws DZFWarpException
	 */
	public ContractDocVO[] getAttatches(ContractDocVO qvo) throws DZFWarpException;
	
	/**
	 * 批量审核
	 * @param confrimVOs
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ContractConfrimVO> bathconfrim(ContractConfrimVO[] confrimVOs, Integer opertype, String cuserid) throws DZFWarpException;
}
