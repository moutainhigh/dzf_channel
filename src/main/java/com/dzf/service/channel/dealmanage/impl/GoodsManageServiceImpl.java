package com.dzf.service.channel.dealmanage.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.ChnPayBillVO;
import com.dzf.model.channel.dealmanage.GoodsVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.service.channel.dealmanage.IGoodsManageService;

@Service("goodsmanageser")
public class GoodsManageServiceImpl implements IGoodsManageService {
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public Integer queryTotalRow(GoodsVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(pamvo);
		return multBodyObjectBO.queryDataTotal(ChnPayBillVO.class,sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsVO> query(GoodsVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo =  getQrySqlSpm(pamvo);
		List<GoodsVO> list = (List<GoodsVO>) multBodyObjectBO.queryDataPage(GoodsVO.class, 
				sqpvo.getSql(), sqpvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		
		return list;
	}

	/**
	 * 获取查询条件
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(GoodsVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT *  \n") ;
		sql.append("  FROM cn_goods g  \n") ; 
		sql.append(" WHERE nvl(g.dr, 0) = 0  \n") ; 
		if(pamvo.getVstatus() != null && pamvo.getVstatus() != -1){
			sql.append("   AND g.vstatus = ?  \n") ; 
			spm.addParam(pamvo.getVstatus());
		}
		if(!StringUtil.isEmpty(pamvo.getVgoodscode())){
			sql.append("   AND g.vgoodscode like ?  \n") ; 
			spm.addParam(pamvo.getVgoodscode()+"%");
		}
		if(!StringUtil.isEmpty(pamvo.getVgoodsname())){
			sql.append("   AND g.vgoodsname like ?  \n") ; 
			spm.addParam(pamvo.getVgoodsname()+"%");
		}
		sql.append(" ORDER BY g.updatets DESC \n");
		return qryvo;
	}
}
