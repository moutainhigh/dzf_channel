package com.dzf.service.channel.dealmanage.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.ChnBalanceVO;
import com.dzf.model.channel.ChnDetailVO;
import com.dzf.model.channel.dealmanage.GoodsBillBVO;
import com.dzf.model.channel.dealmanage.GoodsBillSVO;
import com.dzf.model.channel.dealmanage.GoodsBillVO;
import com.dzf.model.channel.dealmanage.GoodsBillMxVO;
import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.StockInBVO;
import com.dzf.model.channel.stock.StockNumVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.dealmanage.IChannelOrderMxService;
import com.dzf.service.channel.dealmanage.IChannelOrderService;

@Service("channelmxorderser")
public class ChannelOrderMxServiceImpl implements IChannelOrderMxService {

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private SingleObjectBO singleObjectBO;

	
	@Override
	public int queryTotalRowMx(GoodsBillMxVO paramvo) {
		QrySqlSpmVO sqpvo = getQrySqlSpmMx(paramvo);
		return multBodyObjectBO.queryDataTotal(GoodsBillMxVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsBillMxVO> querymx(GoodsBillMxVO pamvo) throws DZFWarpException {
		QrySqlSpmVO sqpvo = getQrySqlSpmMx(pamvo);
		List<GoodsBillMxVO> list = (List<GoodsBillMxVO>) multBodyObjectBO.queryDataPage(GoodsBillMxVO.class, sqpvo.getSql(),
				sqpvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		if (list != null && list.size() > 0) {
			setShowValue(list);
		}
		return list;
	}

	/**
	 * 设置显示名称
	 * 
	 * @throws DZFWarpException
	 */
	private void setShowValue(List<GoodsBillMxVO> list) throws DZFWarpException {
		CorpVO corpvo = null;
		for (GoodsBillMxVO bvo : list) {
			corpvo = CorpCache.getInstance().get(null, bvo.getPk_corp());
			if (corpvo != null) {
				bvo.setCorpcode(corpvo.getInnercode());
				bvo.setCorpname(corpvo.getUnitname());
			}
		}
	}
	
	/**
	 * 获取明细查询条件
	 * 
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpmMx(GoodsBillMxVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select gill.vbillcode, gill.pk_corp, gill.ndedsummny, \n");
		sql.append("       gill.ndeductmny, gill.ndedrebamny, gbill.vgoodsname, \n");
		sql.append("       gbill.invspec, gbill.invtype,  \n");
		sql.append("       nvl(gbill.amount, 0) amount, gbill.nprice, \n");
		sql.append("       gbill.amount * gbill.nprice ntotalmny,  \n");
		sql.append("       gill.vstatus,  \n");
		sql.append("       substr(gsillq.doperatedate,0,10) dconfirmtime, \n");
		sql.append("       substr(gsillf.doperatedate,0,10) dsendtime,  \n");
		sql.append("       substr(gsillt.doperatedate,0,10) dsubmittime  \n");
		sql.append("  from cn_goodsbill_b gbill  \n");
		sql.append("  left join cn_goodsbill gill on gbill.pk_goodsbill = gill.pk_goodsbill  \n");
		sql.append("  left join cn_goodsbill_s gsillt on gill.pk_goodsbill =  \n");
		sql.append("   gsillt.pk_goodsbill  and gsillt.vstatus = 0 \n");
		sql.append("   left join cn_goodsbill_s gsillq on gill.pk_goodsbill =  \n");
		sql.append("  gsillq.pk_goodsbill  and gsillq.vstatus = 1 \n");
		sql.append("   left join cn_goodsbill_s gsillf on gill.pk_goodsbill =  \n");
		sql.append("    gsillf.pk_goodsbill  and gsillf.vstatus = 2 \n");
		sql.append("   where nvl(gill.dr,0) =0  \n");
		sql.append("  and nvl(gbill.dr,0) =0  \n");
		sql.append("  and nvl(gsillt.dr,0) =0  \n");
		if (!StringUtil.isEmpty(pamvo.getVbillcode())) {
			sql.append(" AND gill.vbillcode like ? \n");
			spm.addParam("%" + pamvo.getVbillcode() + "%");
		}
		if (!StringUtil.isEmpty(pamvo.getPk_corp())) {
			 String[] strs = pamvo.getPk_corp().split(",");
			 String inSql = SqlUtil.buildSqlConditionForIn(strs);
			 sql.append(" AND gill.pk_corp in (").append(inSql).append(")");
		}
		if (pamvo.getVstatus() != null && pamvo.getVstatus() != -1) {
			sql.append(" AND gill.vstatus = ? \n");
			spm.addParam(pamvo.getVstatus());
		}
		if (!StringUtil.isEmpty(pamvo.getPk_goodsbill())) {
			sql.append(" AND gill.pk_goodsbill = ? ");
			spm.addParam(pamvo.getPk_goodsbill());
		}
		if (pamvo.getSubmitbegin() != null) {
			sql.append("   AND substr(gsillt.doperatedate,0,10) >= ? \n");
			spm.addParam(pamvo.getSubmitbegin());
		}
		if (pamvo.getSubmitend() != null) {
			sql.append("   AND substr(gsillt.doperatedate,0,10) <= ? \n");
			spm.addParam(pamvo.getSubmitend());
		}
		if (pamvo.getKbegin() != null) {
			sql.append("   AND substr(gsillq.doperatedate,0,10) >= ? \n");
			spm.addParam(pamvo.getKbegin());
		}
		if (pamvo.getKend() != null) {
			sql.append("   AND substr(gsillq.doperatedate,0,10) <= ? \n");
			spm.addParam(pamvo.getKend());
		}
		if (!StringUtil.isEmpty(pamvo.getPk_goods())) {
			String[] strs = pamvo.getPk_goods().split(",");
			 String inSql = SqlUtil.buildSqlConditionForIn(strs);
			 sql.append(" AND gbill.pk_goods in (").append(inSql).append(")");
		}
		sql.append(" order by gsillt.doperatedate desc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GoodsBoxVO> queryComboBox() throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT pk_goods AS id, vgoodsname AS name \n");
		sql.append("  FROM cn_goods  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		return (List<GoodsBoxVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(GoodsBoxVO.class));
	}


}
