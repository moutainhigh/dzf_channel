package com.dzf.service.channel.report;

import java.util.List;

import com.dzf.model.channel.report.LogisticRepVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface ILogisticRepService {
	
	/**
	 * 查询商品快递信息，总条数
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer qryGoodsTotal(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 *  查询商品快递信息
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<LogisticRepVO> queryGoods(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询物料快递信息，总条数
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer qryMaterielTotal(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询物料快递信息
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<LogisticRepVO> queryMateriel(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询商品datagrid，表头信息
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ComboBoxVO> qryGoodsHead() throws DZFWarpException;
	
	/**
	 * 查询物料datagrid，表头信息
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ComboBoxVO> qryMaterHead() throws DZFWarpException;
	
}
