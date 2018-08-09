package com.dzf.service.channel.dealmanage;

import java.io.File;
import java.util.List;

import com.dzf.model.channel.dealmanage.GoodsDocVO;
import com.dzf.model.channel.dealmanage.GoodsVO;
import com.dzf.model.channel.dealmanage.MeasVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.pub.DZFWarpException;

public interface IGoodsManageService {

	/**
	 * 查询数据行数
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(GoodsVO pamvo) throws DZFWarpException;
	
	/**
	 * 查询数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<GoodsVO> query(GoodsVO pamvo) throws DZFWarpException;
	
	/**
	 * 保存
	 * @param datavo
	 * @param files
	 * @param filenames
	 * @return
	 * @throws DZFWarpException
	 */
	public GoodsVO save(GoodsVO datavo, File[] files, String[] filenames) throws DZFWarpException;
	
	/**
	 * 查询计量单位下拉值
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ComboBoxVO> queryMeasCombox(String pk_corp) throws DZFWarpException;
	
	/**
	 * 保存计量单位
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public MeasVO saveMeas(GoodsVO pamvo) throws DZFWarpException;
	
	/**
	 * 通过主键查询商品信息
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public GoodsVO queryByID(GoodsVO pamvo) throws DZFWarpException;
	
	/**
	 * 通过主键查询商品图片信息
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public GoodsDocVO[] getAttatches(GoodsVO pamvo) throws DZFWarpException;
	
	/**
	 * 删除商品信息
	 * @param pamvo
	 * @throws DZFWarpException
	 */
	public void deleteFile(GoodsDocVO pamvo) throws DZFWarpException;
	
	/**
	 * 删除商品信息
	 * @param pamvo
	 * @throws DZFWarpException
	 */
	public void delete(GoodsVO pamvo) throws DZFWarpException;
	
	/**
	 * 通过主键查询商品图片信息
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public GoodsDocVO queryGoodsDocById(GoodsDocVO pamvo) throws DZFWarpException;
}
