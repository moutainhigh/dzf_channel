package com.dzf.service.channel.dealmanage;

import java.util.List;

import com.dzf.model.channel.dealmanage.StockInBVO;
import com.dzf.model.channel.dealmanage.StockInVO;
import com.dzf.model.channel.stock.SupplierVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IStockInService {
	
	/**
	 * 查询数据行数
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<StockInVO> query(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询供应商数据
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<SupplierVO> querySupplierRef(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 保存
	 * @param hvo
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public StockInVO save(StockInVO hvo, String pk_corp) throws DZFWarpException;
	
	/**
	 * 通过主键查询数据
	 * @param pk_stockin
	 * @param pk_corp
	 * @param qrytype  0：详情查询（包含子表）；1：修改查询（包含子表）；2：普通查询（不包含子表）；
	 * @return
	 * @throws DZFWarpException
	 */
	public StockInVO queryById(String pk_stockin, String pk_corp, Integer qrytype) throws DZFWarpException;
	
	/**
	 * 删除入库单
	 * @param pamvo
	 * @throws DZFWarpException
	 */
	public void delete(StockInVO pamvo) throws DZFWarpException;
	
	/**
	 * 确认入库
	 * @param stvo
	 * @param cuserid
	 * @throws DZFWarpException
	 */
	public StockInVO updateconfirm(StockInVO stvo, String cuserid) throws DZFWarpException;
	
	/**
	 * 保存供应商
	 * @param pamvo
	 * @throws DZFWarpException
	 */
	public void saveSupplier(StockInVO pamvo) throws DZFWarpException;

}
