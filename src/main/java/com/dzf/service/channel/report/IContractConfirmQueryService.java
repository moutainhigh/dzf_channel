package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.contract.ContractConfrimVO;
import com.dzf.model.demp.contract.ContractDocVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IContractConfirmQueryService {

	/**
	 * 查询数据条数
	 * @param paramvo
	 * @return
	 */
	int queryTotalRow(QryParamVO paramvo) throws DZFWarpException;

	/**
	 * 查询数据
	 * @param paramvo
	 * @return
	 */
	List<ContractConfrimVO> query(QryParamVO paramvo) throws DZFWarpException;

	/**
	 * 查询合同明细
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	ContractConfrimVO queryInfoById(ContractConfrimVO paramvo) throws DZFWarpException;

	/**
	 * 获取查询条件
	 * @param cuserid
	 * @param qrytype
	 * @return
	 * @throws DZFWarpException
	 */
	String getQrySql(String cuserid, Integer qrytype) throws DZFWarpException;

	/**
	 * 获取附件列表
	 * @param qvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ContractDocVO> getAttatches(ContractDocVO qvo) throws DZFWarpException;

	
}
