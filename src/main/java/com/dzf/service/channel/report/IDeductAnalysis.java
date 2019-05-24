package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.DeductAnalysisVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IDeductAnalysis {
	
	/**
	 * 查询金额数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DeductAnalysisVO> query(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 查询金额排序数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DeductAnalysisVO> queryMnyOrder(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 获取查询条件
	 * @param cuserid
	 * @param qrytype  1：渠道经理；2：培训师；3：渠道运营；
	 * @return
	 * @throws DZFWarpException
	 */
	public String getQrySql(String cuserid, Integer qrytype) throws DZFWarpException; 
	
	/**
	 * 获取展示列
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Object[] queryColumn(QryParamVO paramvo) throws DZFWarpException;
	
	/**
	 * 获取展示数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DeductAnalysisVO> queryData(QryParamVO paramvo) throws DZFWarpException;

}
