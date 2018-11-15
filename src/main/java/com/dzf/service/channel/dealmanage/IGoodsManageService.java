package com.dzf.service.channel.dealmanage;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.GoodsDocVO;
import com.dzf.model.channel.dealmanage.GoodsSpecVO;
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
	public GoodsVO queryByID(GoodsVO pamvo, Integer itype) throws DZFWarpException;
	
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
	
	/**
	 * 发布、下架数据
	 * @param pamvo
	 * @param itype
	 * @return
	 * @throws DZFWarpException
	 */
	public GoodsVO updateData(GoodsVO pamvo, Integer itype) throws DZFWarpException;
	
	/**
	 * 查询商品下拉数据
	 * @return
	 * @throws DZFWarpException
	 */
	public List<GoodsBoxVO> queryComboBox() throws DZFWarpException;
	
	/**
	 * 保存商品设置
	 * @param datamap
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	public void saveSet(Map<String, GoodsSpecVO[]> datamap, String pk_corp) throws DZFWarpException;
	
	/**
	 * 查询商品设置
	 * @param pk_goods
	 * @return
	 * @throws DZFWarpException
	 */
	public List<GoodsSpecVO> queryGoodsSet(String pk_goods) throws DZFWarpException;
}
