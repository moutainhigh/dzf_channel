package com.dzf.service.channel.dealmanage.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.model.channel.stock.GoodsTypeVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.MaxCodeVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lock.LockUtil;
import com.dzf.service.channel.dealmanage.IGoodsTypeService;
import com.dzf.service.pub.IBillCodeService;

@Service("typeGoods")
public class GoodsTypeServiceImpl implements IGoodsTypeService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IBillCodeService billCode;
	
	
	@Override
	public List<GoodsTypeVO> query(){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT g.vcode,g.vname,g.pk_goodstype    ") ;
		sql.append("  FROM cn_goodstype g    ") ; 
		sql.append(" WHERE nvl(g.dr, 0) = 0    ") ; 
		sql.append(" ORDER BY g.vcode DESC   ");
		List<GoodsTypeVO> list = (List<GoodsTypeVO>) singleObjectBO.executeQuery(sql.toString(),
				null, new BeanListProcessor(GoodsTypeVO.class));
		return list;
	}
	
	@Override
	public void save(GoodsTypeVO vo){
		vo.getVname().replaceAll(" ", "");
		checkIsOnly(vo);
		if(StringUtil.isEmpty(vo.getPk_goodstype())){//新增
			MaxCodeVO mcvo=new MaxCodeVO();
			mcvo.setTbName(vo.getTableName());
			mcvo.setFieldName("vcode");
			mcvo.setPk_corp(vo.getPk_corp());
			mcvo.setBillType("FL");
			mcvo.setCorpIdField("pk_corp");
			mcvo.setDiflen(2);
			vo.setVcode(billCode.getDefaultCode(mcvo));
			
			vo.setDoperatedate(new DZFDate());
			singleObjectBO.saveObject(vo.getPk_corp(), vo);
		}else{//修改
			singleObjectBO.update(vo, new String[]{"vname"});
		}
	}
	
	@Override
	public void delete(GoodsTypeVO vo){
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(vo.getTableName(), vo.getPk_goodstype(), uuid, 60);
			checkIsQuote(vo);
				SQLParameter spm =new SQLParameter();
				spm.addParam(vo.getPk_goodstype());
				singleObjectBO.executeUpdate("delete from cn_goodstype  where pk_goodstype=? ", spm);
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(vo.getTableName(), vo.getPk_goodstype(), uuid);
		}	
	}
	
	
	/**
	 * 校验商品分类是否唯一
	 * @param vo
	 * @return
	 */
	private void checkIsOnly(GoodsTypeVO vo) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select count(1) as count from cn_goodstype");
		sql.append(" where nvl(dr,0) = 0 and pk_corp=? ");
		sql.append(" and vname = ?");
		if(!StringUtil.isEmpty(vo.getPk_goodstype())){
			sql.append(" and pk_goodstype != ?");
			sp.addParam(vo.getPk_goodstype());
		}
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getVname());
		String res = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("count")).toString();
		int num = Integer.valueOf(res);
		if(num > 0){
			throw new BusinessException("分类名称重复,请重新输入!");
		}
	}
	
	/**
	 * 校验商品分类是否被商品引用
	 * @param vo
	 * @return
	 */
	private void checkIsQuote(GoodsTypeVO vo) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select count(1) as count from cn_goods");
		sql.append(" where nvl(dr,0) = 0 and pk_corp=? ");
		sql.append(" and pk_goodstype = ?");
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getPk_goodstype());
		String res = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("count")).toString();
		int num = Integer.valueOf(res);
		if(num > 0){
			throw new BusinessException("该商品分类已被商品引用,不可删除!");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ComboBoxVO> queryComboBox() throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT pk_goodstype AS id, vname AS name   ");
		sql.append("  FROM cn_goodstype    ");
		sql.append(" WHERE nvl(dr, 0) = 0    ");
		sql.append(" ORDER BY vcode DESC    ");
		return (List<ComboBoxVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(ComboBoxVO.class));
	}

}
