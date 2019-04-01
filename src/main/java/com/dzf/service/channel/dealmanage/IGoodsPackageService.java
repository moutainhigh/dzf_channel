package com.dzf.service.channel.dealmanage;

import java.util.List;

import com.dzf.model.channel.dealmanage.GoodsPackageVO;
import com.dzf.pub.DZFWarpException;

public interface IGoodsPackageService {
	
	/**
	 * 查询
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<GoodsPackageVO> query(GoodsPackageVO pamvo) throws DZFWarpException; 

	/**
	 * 保存
	 * @param pk_corp
	 * @param insertData
	 * @param delData
	 * @param updateData
	 * @throws DZFWarpException
	 */
	public void save(String pk_corp, GoodsPackageVO[] addData, GoodsPackageVO[] delData, GoodsPackageVO[] updData)
			throws DZFWarpException;
	
	/**
	 * 删除
	 * @param data
	 * @throws DZFWarpException
	 */
	public void delete(GoodsPackageVO data) throws DZFWarpException;
	
	/**
	 * 更新操作数据（发布、下架）
	 * @param data
	 * @param opertype
	 * @throws DZFWarpException
	 */
	public void updateData(GoodsPackageVO data, String opertype) throws DZFWarpException;
	
}
