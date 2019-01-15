package com.dzf.service.channel.dealmanage.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.StockInVO;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.channel.stock.StockOutVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.dealmanage.IStockOutInService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

@Service("outinStock")
public class IStockOutInServiceImpl implements IStockOutInService {

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public List<Long> queryTotalRow(StockOutInMVO qvo) {
		List<Long> total = new ArrayList<Long>();
		if (qvo.getVitype() == 1) {// 入库
			QrySqlSpmVO sqpvoin = getQrySqlSpmIn(qvo);
			long queryDataTotalIn = multBodyObjectBO.queryDataTotal(StockOutInMVO.class, sqpvoin.getSql(),
					sqpvoin.getSpm());
			total.add(queryDataTotalIn);
			return total;
		}
		if (qvo.getVitype() == 2) {// 出库
			QrySqlSpmVO sqpvoout = getQrySqlSpmOut(qvo);
			long queryDataTotalOut = multBodyObjectBO.queryDataTotal(StockOutInMVO.class, sqpvoout.getSql(),
					sqpvoout.getSpm());
			total.add(queryDataTotalOut);
			return total;
		}
		// 全部
		QrySqlSpmVO sqpvoin = getQrySqlSpmIn(qvo);
		long queryDataTotalIn = multBodyObjectBO.queryDataTotal(StockOutInMVO.class, sqpvoin.getSql(),
				sqpvoin.getSpm());
		total.add(queryDataTotalIn);
		QrySqlSpmVO sqpvoout = getQrySqlSpmOut(qvo);
		long queryDataTotalOut = multBodyObjectBO.queryDataTotal(StockOutInMVO.class, sqpvoout.getSql(),
				sqpvoout.getSpm());
		total.add(queryDataTotalOut);
		return total;

	}

	@Override
	public List<StockOutInMVO> query(StockOutInMVO qvo) {

		List<StockOutInMVO> totalList = new ArrayList<StockOutInMVO>();
		UserVO uvo = null;

		if (qvo.getVitype() == 1) {// 入库
			List<StockOutInMVO> queryIn = queryIn(qvo, totalList);
			for (StockOutInMVO stockOutInMVO : queryIn) {
				uvo = UserCache.getInstance().get(stockOutInMVO.getVconfirmid(), null);
				if (uvo != null) {
					stockOutInMVO.setVconfirmname(uvo.getUser_name());
				}
			}

			return queryIn;
		}
		if (qvo.getVitype() == 2) {// 出库
			List<StockOutInMVO> queryOut = queryOut(qvo, totalList);
			for (StockOutInMVO stockOutInMVO : queryOut) {
				uvo = UserCache.getInstance().get(stockOutInMVO.getVconfirmid(), null);
				if (uvo != null) {
					stockOutInMVO.setVconfirmname(uvo.getUser_name());
				}
			}

			return queryOut;
		}
		// 全部
		List<StockOutInMVO> queryIn = queryIn(qvo, totalList);
		List<StockOutInMVO> queryOut = queryOut(qvo, totalList);

		for (StockOutInMVO stockOutInMVO : totalList) {
			uvo = UserCache.getInstance().get(stockOutInMVO.getVconfirmid(), null);
			if (uvo != null) {
				stockOutInMVO.setVconfirmname(uvo.getUser_name());
			}
		}

		return totalList;
	}

	/**
	 * 出库查询
	 * 
	 * @param qvo
	 * @return
	 */
	private List<StockOutInMVO> queryOut(StockOutInMVO qvo, List<StockOutInMVO> totalList) {
		QrySqlSpmVO sqpvoout = getQrySqlSpmOut(qvo);
		@SuppressWarnings("unchecked")
		List<StockOutInMVO> listout = (List<StockOutInMVO>) multBodyObjectBO.queryDataPage(StockOutInMVO.class,
				sqpvoout.getSql(), sqpvoout.getSpm(), qvo.getPage(), qvo.getRows(), null);
		for (StockOutInMVO stockOutInMVO : listout) {
			totalList.add(stockOutInMVO);
		}
		return totalList;
	}

	/**
	 * 入库查询
	 * 
	 * @param qvo
	 * @return
	 */
	private List<StockOutInMVO> queryIn(StockOutInMVO qvo, List<StockOutInMVO> totalList) {
		QrySqlSpmVO sqpvoin = getQrySqlSpmIn(qvo);
		@SuppressWarnings("unchecked")
		List<StockOutInMVO> listin = (List<StockOutInMVO>) multBodyObjectBO.queryDataPage(StockOutInMVO.class,
				sqpvoin.getSql(), sqpvoin.getSpm(), qvo.getPage(), qvo.getRows(), null);
		for (StockOutInMVO stockOutInMVO : listin) {
			totalList.add(stockOutInMVO);
		}
		return totalList;
	}

	/**
	 * 出库——获取查询条件
	 * 
	 * @param qvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpmOut(StockOutInMVO qvo) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();

		sql.append(" select 2 vitype, so.vbillcode, so.vconfirmid, so.dconfirmtime, ");
		sql.append(" 		cg.vgoodscode, cg.vgoodsname, cg.pk_goods,");
		sql.append("        gs.invspec, gs.invtype, ");
		sql.append("        sib.nprice ,");
		sql.append("        nvl(sob.nnum,0) nnum, sob.nprice sprice, nvl(sob.nnum,0) * sob.nprice totalmny");
		sql.append("    from cn_goods cg ");
		sql.append("    left join cn_goodsspec gs on cg.pk_goods=gs.pk_goods ");
		sql.append("    left join cn_stockout_b sob on sob.pk_goods= gs.pk_goods ");
		sql.append("    and sob.pk_goodsspec = gs.pk_goodsspec");
		sql.append("    left join cn_stockout so on so.pk_stockout = sob.pk_stockout ");
		sql.append("    left join cn_stockin_b sib on sob.pk_goods = sib.pk_goods ");
		sql.append("    and sib.pk_goodsspec = sob.pk_goodsspec");
		sql.append("  where nvl(cg.dr, 0) = 0 ");
		sql.append("    and nvl(sib.dr, 0) = 0 ");
		sql.append("    and nvl(gs.dr, 0) = 0 ");
		sql.append("    and nvl(sob.dr, 0) = 0 ");
		sql.append("    and nvl(so.dr, 0) = 0 ");

		if (!StringUtil.isEmpty(qvo.getVbillcode())) {
			sql.append("   AND so.vbillcode like ? \n");
			spm.addParam("%" + qvo.getVbillcode() + "%");
		}

		if (!StringUtil.isEmpty(qvo.getPk_goods())) {
			
			String[] strs = qvo.getPk_goods().split(",");
			 String inSql = SqlUtil.buildSqlConditionForIn(strs);
			 sql.append(" AND cg.pk_goods in (").append(inSql).append(")");
		}
		if (qvo.getBegdate() != null) {
			sql.append("   AND substr(so.dconfirmtime,0,10) >= ? \n");
			spm.addParam(qvo.getBegdate());
		}
		if (qvo.getEnddate() != null) {
			sql.append("   AND substr(so.dconfirmtime,0,10) <= ? \n");
			spm.addParam(qvo.getEnddate());
		}
		sql.append(" ORDER BY so.vbillcode");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	/**
	 * 入库——获取查询条件
	 * 
	 * @param qvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpmIn(StockOutInMVO qvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();

		sql.append(" select 1 vitype, cg.vgoodscode, cg.vgoodsname, cg.pk_goods, ");
		sql.append(" 		si.vbillcode, si.vconfirmid, si.dconfirmtime, ");
		sql.append("        sib.invspec, sib.invtype, sib.nprice, nvl(sib.nnum,0) nnum, ");
		sql.append("        gs.nprice sprice, gs.nprice * nvl(sib.nnum,0) totalmny");

		sql.append("    from cn_goods cg ");
		sql.append("    left join cn_goodsspec gs on cg.pk_goods=gs.pk_goods ");
		sql.append("    left join cn_stockin_b sib on gs.pk_goods=sib.pk_goods");
		sql.append("    and sib.pk_goodsspec=gs.pk_goodsspec");
		sql.append("    left join cn_stockin si on si.pk_stockin=sib.pk_stockin ");
		sql.append("  where nvl(cg.dr, 0) = 0 ");
		sql.append("    and nvl(si.dr, 0) = 0 ");
		sql.append("    and nvl(sib.dr, 0) = 0 ");
		sql.append("    and nvl(gs.dr, 0) = 0 ");

		if (!StringUtil.isEmpty(qvo.getVbillcode())) {
			sql.append("   AND si.vbillcode like ? \n");
			spm.addParam("%" + qvo.getVbillcode() + "%");
		}

		if (!StringUtil.isEmpty(qvo.getPk_goods())) {
			
			String[] strs = qvo.getPk_goods().split(",");
			 String inSql = SqlUtil.buildSqlConditionForIn(strs);
			 sql.append(" AND cg.pk_goods in (").append(inSql).append(")");
		}
		if (qvo.getBegdate() != null) {
			sql.append("   AND substr(si.dconfirmtime,0,10) >= ? \n");
			spm.addParam(qvo.getBegdate());
		}
		if (qvo.getEnddate() != null) {
			sql.append("   AND substr(si.dconfirmtime,0,10) <= ? \n");
			spm.addParam(qvo.getEnddate());
		}
		sql.append(" ORDER BY si.vbillcode");
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
