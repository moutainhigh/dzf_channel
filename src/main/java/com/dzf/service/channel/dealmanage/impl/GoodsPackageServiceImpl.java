package com.dzf.service.channel.dealmanage.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.dealmanage.GoodsPackageVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.lang.DZFDate;
import com.dzf.service.channel.dealmanage.IGoodsPackageService;

@Service("goodspackageser")
public class GoodsPackageServiceImpl implements IGoodsPackageService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsPackageVO> query(GoodsPackageVO pamvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT e.*,    ");
		sql.append("       s.vgoodsname || ' ' || '(' || c.invspec || c.invtype || ')' AS vgoodsname,    ");
		sql.append("       c.nprice    ");
		sql.append("  FROM cn_goodspackage e    ");
		sql.append("  LEFT JOIN cn_goods s ON e.pk_goods = s.pk_goods    ");
		sql.append("  LEFT JOIN cn_goodsspec c ON e.pk_goodsspec = c.pk_goodsspec    ");
		sql.append(" WHERE nvl(e.dr, 0) = 0    ");
		sql.append("   AND nvl(s.dr, 0) = 0    ");
		sql.append("   AND nvl(c.dr, 0) = 0    ");
		sql.append(" ORDER BY e.ts DESC    ");
		return (List<GoodsPackageVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(GoodsPackageVO.class));
	}

	@Override
	public void save(String pk_corp, GoodsPackageVO[] addData, GoodsPackageVO[] delData, GoodsPackageVO[] updData)
			throws DZFWarpException {
		if (addData != null && addData.length > 0) {
			singleObjectBO.insertVOArr(pk_corp, addData);
		}
		if (updData != null && updData.length > 0) {
			String[] strs = new String[] { "pk_goods", "pk_goodsspec", "invspec", "invtype", "vmeasname", "nnum",
					"isbuypackage" };
			singleObjectBO.updateAry(updData, strs);
		}
		if (delData != null && delData.length > 0) {
			singleObjectBO.deleteVOArray(delData);
		}
	}

	@Override
	public void delete(GoodsPackageVO data) throws DZFWarpException {
		if (data.getVstatus() == 2) {
			throw new BusinessException("已发布的套餐不允许删除");
		}
		singleObjectBO.deleteObject(data);
	}

	/**
	 * 操作数据
	 * @param data
	 * @param opertype  1：发布；2：下架； 
	 */
	@Override
	public void updateData(GoodsPackageVO data, String opertype) throws DZFWarpException {
		checkUpdateData(data, opertype);
		if("1".equals(opertype)){
			data.setDpublishdate(new DZFDate());
			data.setDoffdate(null);
			data.setVstatus(2);
		}else if("2".equals(opertype)){
			data.setDpublishdate(null);
			data.setDoffdate(new DZFDate());
			data.setVstatus(3);
		}
		String[] strs = new String[]{"dpublishdate", "doffdate", "vstatus"};
		singleObjectBO.update(data, strs);
	}
	
	/**
	 * 操作数据前校验
	 * @param data
	 * @param opertype  1：发布；2：下架；
	 * @throws DZFWarpException
	 */
	private void checkUpdateData(GoodsPackageVO data, String opertype) throws DZFWarpException {
		GoodsPackageVO oldvo = (GoodsPackageVO) singleObjectBO.queryByPrimaryKey(GoodsPackageVO.class,
				data.getPk_goodspackage());
		if(oldvo == null || (oldvo != null && oldvo.getDr() != null && oldvo.getDr() == 1)){
			throw new BusinessException("商品"+data.getVgoodsname()+"数据发生变化");
		}
		if(oldvo.getUpdatets().compareTo(data.getUpdatets()) != 0){
			throw new BusinessException("商品"+data.getVgoodsname()+"数据发生变化");
		}
		if("1".equals(opertype)){
			if(oldvo.getVstatus() == 2){
				throw new BusinessException("商品"+data.getVgoodsname()+"已经发布");
			}
		}else if("2".equals(opertype)){
			if(oldvo.getVstatus() != 2){
				throw new BusinessException("商品"+data.getVgoodsname()+"尚未发布");
			}
		}
	}

}
