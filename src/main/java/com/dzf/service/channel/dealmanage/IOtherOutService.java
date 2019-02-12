package com.dzf.service.channel.dealmanage;

import java.util.List;

import com.dzf.model.channel.stock.StockOutBVO;
import com.dzf.model.channel.stock.StockOutVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.DZFWarpException;

public interface IOtherOutService {
	
	
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
	public List<StockOutVO> query(QryParamVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询出库单明细(修改用的)
	 * @param soutid
	 * @return
	 * @throws DZFWarpException
	 */
	public StockOutVO queryByID(String soutid) throws DZFWarpException;
	
	/**
	 * 查询出库单明细(查看;打印)
	 * @param soutid
	 * @return
	 * @throws DZFWarpException
	 */
	public StockOutVO queryForLook(String soutid) throws DZFWarpException;
	
	/**
	 * 查询可购买商品（为了新增出库单）
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<StockOutBVO> queryGoodsAble()throws DZFWarpException;
	
	/**
	 * 新增保存;
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void saveNew(StockOutVO vo) throws DZFWarpException;
	
	/**
	 * 修改保存;
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void saveEdit(StockOutVO vo) throws DZFWarpException;
	
	/**
	 * 删除出库单
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void delete(StockOutVO vo) throws DZFWarpException;
	
	/**
	 * 确认出库
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void updateCommit(StockOutVO vo) throws DZFWarpException;
	
	
	/**
	 * 取消确认出库
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void updateCancel(StockOutVO vo) throws DZFWarpException;
	
}
