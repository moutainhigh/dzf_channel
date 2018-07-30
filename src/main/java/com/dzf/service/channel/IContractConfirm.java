package com.dzf.service.channel;

import java.io.File;
import java.util.List;
import java.util.Map;

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
	 * @param datavo
	 * @param opertype
	 * @param cuserid
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public ContractConfrimVO updateDeductData(ContractConfrimVO datavo, Integer opertype, String cuserid,
			String pk_corp) throws DZFWarpException;

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
	 * 查询套餐属性
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, String> queryPackageMap() throws DZFWarpException;
	
	/**
	 * 批量审核-审核单个数据
	 * @param datavo
	 * @param paramvo
	 * @param opertype
	 * @param cuserid
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public ContractConfrimVO updateBathDeductData(ContractConfrimVO datavo, ContractConfrimVO paramvo,
			Integer opertype, String cuserid, String pk_corp) throws DZFWarpException;
	
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
	
}
