package com.dzf.service.channel;

import java.io.File;
import java.util.List;

import com.dzf.model.channel.contract.ContractConfrimVO;
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
	
	/**
	 * 查询数据总条数
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询待扣款数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public ContractConfrimVO queryDebitData(ContractConfrimVO paramvo) throws DZFWarpException;
	
	/**
	 * 获取附件列表
	 * @param qvo
	 * @return
	 * @throws DZFWarpException
	 */
	public ContractDocVO[] getAttatches(ContractDocVO qvo) throws DZFWarpException;
	
	/**
	 * 变更
	 * @param paramvo
	 * @param cuserid
	 * @param files
	 * @param filenames
	 * @return
	 * @throws DZFWarpException
	 */
	public ContractConfrimVO saveChange(ContractConfrimVO paramvo, String cuserid, File[] files, String[] filenames)
			throws DZFWarpException;
	
	/**
	 * 查询合同信息
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public ContractConfrimVO queryInfoById(ContractConfrimVO paramvo) throws DZFWarpException;
	
	/**
	 * 获取渠道经理查询条件
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public String getQrySql(String cuserid) throws DZFWarpException; 
	
	/**
	 * 审核/驳回操作
	 * @param datavo  合同信息
	 * @param paramvo 参数信息（批量审核使用）
	 * @param opertype  操作类型：1：审核；2：驳回；
	 * @param cuserid
	 * @param pk_corp
	 * @param checktype 校验类型：single：单个审核；batch：批量审核；
	 * @return
	 * @throws DZFWarpException
	 */
	public ContractConfrimVO updateAuditData(ContractConfrimVO datavo, ContractConfrimVO paramvo, Integer opertype,
			String cuserid, String pk_corp, String checktype) throws DZFWarpException;
	
}
