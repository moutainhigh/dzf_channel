package com.dzf.service.branch.reportmanage;

import java.util.List;

import com.dzf.model.branch.reportmanage.CorpDataVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface ICorpDataService {

	/**
	 * 查询会计公司下拉
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ComboBoxVO> queryAccount(String cuserid) throws DZFWarpException;
	
	/**
	 * 查询主办会计总条数
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryPcountTotal(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询主办会计
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ComboBoxVO> queryPcount(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询所有主办会计（过滤名称使用）
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ComboBoxVO> queryAllPcount(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询数据总条数
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotal(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询数据
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CorpDataVO> query(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询所有数据
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CorpDataVO> queryAll(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 组织返回数据
	 * @param pamvo
	 * @param corpks
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	public List<CorpDataVO> getReturnData(QryParamVO pamvo, String[] corpks, String period) throws DZFWarpException;
}
