package com.dzf.service.channel.dealmanage.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.dealmanage.IStockOutInService;


@Service("outinStock")
public class IStockOutInServiceImpl implements IStockOutInService {

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public Integer queryTotalRow(StockOutInMVO qvo) {
		QrySqlSpmVO sqpvoall = getQrySqlSpmAll(qvo);
		return multBodyObjectBO.queryDataTotal(StockOutInMVO.class, sqpvoall.getSql(),sqpvoall.getSpm());
	}
	
	@Override
	public List<StockOutInMVO> query(StockOutInMVO qvo) {
		List<StockOutInMVO> totalList = queryAll(qvo);
		String idString0="";
		String idString1="";
		Integer balanceNum = null;
		DZFDouble totalmoneyb=null;
		for (StockOutInMVO stockOutInMVO : totalList) {
			if (stockOutInMVO.getVitype() == 0) { // 期初的唯一标识
				idString0 = stockOutInMVO.getPk_goods() + stockOutInMVO.getVgoodscode() + stockOutInMVO.getInvspec()
						+ stockOutInMVO.getInvtype();
				// 期初数量
				balanceNum = stockOutInMVO.getBalanceNum();
				// 期初金额
				totalmoneyb = stockOutInMVO.getTotalmoneyb();
			} else {
				String vgoodscode = stockOutInMVO.getVgoodscode();
				String invspec = stockOutInMVO.getInvspec();
				String invtype = stockOutInMVO.getInvtype();
				String pk_goods = stockOutInMVO.getPk_goods();
				idString1 = pk_goods + vgoodscode + invspec + invtype;

				if (idString0.equals(idString1) && stockOutInMVO.getVitype() == 1) {
					// 商品入库
					stockOutInMVO.setBalanceNum(balanceNum + stockOutInMVO.getNnumin());// 结存数量
					if (stockOutInMVO.getTotalmoneyin() != null) {
						stockOutInMVO.setTotalmoneyb(totalmoneyb.add(stockOutInMVO.getTotalmoneyin()));// 结存金额
					}
					if (stockOutInMVO.getBalanceNum() != 0) {// 结存单价
						stockOutInMVO
								.setBalancePrice(stockOutInMVO.getTotalmoneyb().div(stockOutInMVO.getBalanceNum()));
					}

					balanceNum = balanceNum + stockOutInMVO.getNnumin();
					if (stockOutInMVO.getTotalmoneyin() != null) {
						totalmoneyb = totalmoneyb.add(stockOutInMVO.getTotalmoneyin());
					}
				}
				if (idString0.equals(idString1) && (stockOutInMVO.getVitype() == 2 || stockOutInMVO.getVitype() == 3)) {
					// 销售出库和其他出库
					if (balanceNum != 0) {
						stockOutInMVO.setNpriceout(totalmoneyb.div(balanceNum));// 出库单价
					}
					// 出库金额
					DZFDouble npriceout = stockOutInMVO.getNpriceout();
					stockOutInMVO.setTotalmoneyout(npriceout.multiply(stockOutInMVO.getNnumout()));

					if (balanceNum == stockOutInMVO.getNnumout()) {
						// 上一次的结存数量和这次的出库数量
						stockOutInMVO.setNnumout(balanceNum);
						stockOutInMVO.setNpriceout(totalmoneyb.setScale(0, 2).div(balanceNum));
						stockOutInMVO.setTotalmoneyout(totalmoneyb);
					} else {
						stockOutInMVO.setBalanceNum(balanceNum - stockOutInMVO.getNnumout());// 结存数量
						stockOutInMVO.setTotalmoneyb(totalmoneyb.sub(stockOutInMVO.getTotalmoneyout()));// 结存金额
						if (stockOutInMVO.getBalanceNum() != 0) {// 结存单价
							stockOutInMVO
									.setBalancePrice(stockOutInMVO.getTotalmoneyb().div(stockOutInMVO.getBalanceNum()));
						}
					}
					balanceNum = balanceNum - stockOutInMVO.getNnumout();
					totalmoneyb = totalmoneyb.sub(stockOutInMVO.getTotalmoneyout());
				}
			}
		}
		return totalList;
	}

	
	/**
	 * 查询全部
	 * @param qvo
	 * @param totalList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<StockOutInMVO> queryAll(StockOutInMVO qvo) {
		QrySqlSpmVO sqpvoall = getQrySqlSpmAll(qvo);
		List<StockOutInMVO> listall = (List<StockOutInMVO>) multBodyObjectBO.queryDataPage(StockOutInMVO.class,
				sqpvoall.getSql(), sqpvoall.getSpm(), qvo.getPage(), qvo.getRows(), null);
		return listall;
	}

	/**
	 * 查询全部——获取查询条件
	 * 
	 * @param qvo
	 * @return
	 */
	private  QrySqlSpmVO getQrySqlSpmAll(StockOutInMVO qvo) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();

		sql.append(" select s.* from  ");
		sql.append("  (select cg.vgoodscode, cg.vgoodsname,cg.pk_goods, ");
		sql.append(
				"         gs.invspec, gs.invtype,gs.pk_goodsspec,si.dconfirmtime,1 vitype,si.vbillcode,nvl(sib.nnum,0) nnumin, sib.nprice npricein,");
		sql.append("        sib.ntotalcost totalmoneyin, ");
		sql.append("   0 as nnumout,");// 只作为显示
		sql.append("   0 as npriceout,");
		sql.append("   0 as totalmoneyout,");
		sql.append("        0 as balanceNum, ");
		sql.append("        0 as balancePrice,");
		sql.append("        0 as totalmoneyb ");
		sql.append("       from cn_goods cg ");
		sql.append("       left join cn_goodsspec gs on cg.pk_goods=gs.pk_goods  ");

		sql.append("     left join cn_stockin_b sib");
		sql.append("     on gs.pk_goods=sib.pk_goods");
		sql.append("     and sib.pk_goodsspec=gs.pk_goodsspec");
		sql.append("     left join cn_stockin si on si.pk_stockin=sib.pk_stockin  ");
		sql.append("  where nvl(cg.dr, 0) = 0 ");
		sql.append("    and nvl(si.dr, 0) = 0 ");
		sql.append("    and nvl(gs.dr, 0) = 0 ");
		sql.append("    and nvl(sib.dr, 0) = 0 ");
		sql.append("    and si.vstatus = 2 ");

		if (!StringUtil.isEmpty(qvo.getPk_goods())) {

			String[] strs = qvo.getPk_goods().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" AND cg.pk_goods in (").append(inSql).append(")");
		}
		if(!StringUtil.isEmpty(qvo.getPk_goodsspec())){
			sql.append("   AND gs.pk_goodsspec = ? ");
			spm.addParam(qvo.getPk_goodsspec());
		}
		
		if (qvo.getBegdate() != null) {
			sql.append("   AND substr(si.dconfirmtime,0,10) >= ? \n");
			spm.addParam(qvo.getBegdate());
		}
		if (qvo.getEnddate() != null) {
			sql.append("   AND substr(si.dconfirmtime,0,10) <= ? \n");
			spm.addParam(qvo.getEnddate());
		}
		sql.append("  union all");
		sql.append(" select ");
		sql.append(" 		cg.vgoodscode, cg.vgoodsname,cg.pk_goods, ");
		sql.append("        gs.invspec, gs.invtype,gs.pk_goodsspec, ");
		sql.append("       so.dconfirmtime,2 vitype,  so.vbillcode,");
		sql.append("   0 as nnumin,");// 只作为显示
		sql.append("   0 as npricein,");
		sql.append("   0 as totalmoneyin,");
		sql.append("   nvl(sob.nnum, 0) nnumout,");
		sql.append("   0 as npriceout,");
		sql.append("   0 as totalmoneyout,");
		sql.append("   0 as balanceNum,");
		sql.append("   0 as balancePrice,");
		sql.append("   0 as totalmoneyb");
		sql.append("    from cn_goods cg ");
		sql.append("    left join cn_goodsspec gs on cg.pk_goods=gs.pk_goods ");
		sql.append("    left join cn_stockout_b sob on sob.pk_goods= gs.pk_goods ");
		sql.append("    and sob.pk_goodsspec = gs.pk_goodsspec");
		sql.append("    left join cn_stockout so on so.pk_stockout = sob.pk_stockout ");

		sql.append("  where nvl(cg.dr, 0) = 0 ");
		sql.append("    and nvl(gs.dr, 0) = 0 ");
		sql.append("    and nvl(sob.dr, 0) = 0 ");
		sql.append("    and nvl(so.dr, 0) = 0 ");
		sql.append("    and (so.vstatus = 1 or so.vstatus = 2) ");
		sql.append("    and nvl(so.itype,0) = 0 ");

		if (!StringUtil.isEmpty(qvo.getPk_goods())) {

			String[] strs = qvo.getPk_goods().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" AND cg.pk_goods in (").append(inSql).append(")");
		}
		if(!StringUtil.isEmpty(qvo.getPk_goodsspec())){
			sql.append("   AND gs.pk_goodsspec = ? ");
			spm.addParam(qvo.getPk_goodsspec());
		}
		if (qvo.getBegdate() != null) {
			sql.append("   AND substr(so.dconfirmtime,0,10) >= ? \n");
			spm.addParam(qvo.getBegdate());
		}
		if (qvo.getEnddate() != null) {
			sql.append("   AND substr(so.dconfirmtime,0,10) <= ? \n");
			spm.addParam(qvo.getEnddate());
		}
		sql.append("  union all ");
		sql.append(" select ");
		sql.append(" 		cg.vgoodscode, cg.vgoodsname,cg.pk_goods, ");
		sql.append("        gs.invspec, gs.invtype,gs.pk_goodsspec, ");
		sql.append("       so.dconfirmtime,3 vitype,  so.vbillcode,");
		sql.append("   0 as nnumin,");// 只作为显示
		sql.append("   0 as npricein,");
		sql.append("   0 as totalmoneyin,");
		sql.append("       nvl(sob.nnum, 0) nnumout,");
		sql.append("       0 as npriceout,");
		sql.append("       0 as totalmoneyout,");
		sql.append("       0 as balanceNum,");
		sql.append("       0 as balancePrice,");
		sql.append("       0 as totalmoneyb");
		sql.append("    from cn_goods cg ");
		sql.append("    left join cn_goodsspec gs on cg.pk_goods=gs.pk_goods ");
		sql.append("    left join cn_stockout_b sob on sob.pk_goods= gs.pk_goods ");
		sql.append("    and sob.pk_goodsspec = gs.pk_goodsspec");
		sql.append("    left join cn_stockout so on so.pk_stockout = sob.pk_stockout ");

		sql.append("  where nvl(cg.dr, 0) = 0 ");
		sql.append("    and nvl(gs.dr, 0) = 0 ");
		sql.append("    and nvl(sob.dr, 0) = 0 ");
		sql.append("    and nvl(so.dr, 0) = 0 ");
		sql.append("    and (so.vstatus = 1 or so.vstatus = 2) ");
		sql.append("    and so.itype = 1");

		if (!StringUtil.isEmpty(qvo.getPk_goods())) {

			String[] strs = qvo.getPk_goods().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" AND cg.pk_goods in (").append(inSql).append(")");
		}
		if(!StringUtil.isEmpty(qvo.getPk_goodsspec())){
			sql.append("   AND gs.pk_goodsspec = ? ");
			spm.addParam(qvo.getPk_goodsspec());
		}
		if (qvo.getBegdate() != null) {
			sql.append("   AND substr(so.dconfirmtime,0,10) >= ? \n");
			spm.addParam(qvo.getBegdate());
		}
		if (qvo.getEnddate() != null) {
			sql.append("   AND substr(so.dconfirmtime,0,10) <= ? \n");
			spm.addParam(qvo.getEnddate());
		}

		sql.append(" union all ");
		sql.append("  select cg.vgoodscode, cg.vgoodsname, cg.pk_goods,  ");
		sql.append(" 		 gs.invspec, gs.invtype,gs.pk_goodsspec,");
		sql.append("   '1999-01-01' as dconfirmtime,");
		sql.append("  0 vitype,");
		sql.append("   '' as vode,");
		sql.append("   0 as nnumin,");// 只作为显示
		sql.append("   0 as npricein,");
		sql.append("   0 as totalmoneyin,");
		sql.append("       0 as nnumout,");
		sql.append("       0 as npriceout,");
		sql.append("       0 as totalmoneyout,");
		
		sql.append("         ((nvl(sib.num, 0) - nvl(sob.num, 0))) balanceNum, ");
		sql.append("         decode(((nvl(sib.num, 0) - nvl(sob.num, 0))),0,0,sib.mny /((nvl(sib.num, 0) - nvl(sob.num, 0)))) balancePrice, ");
		sql.append("          nvl(sib.mny,0) totalmoneyb");
		
		sql.append("    from cn_goods cg ");
		sql.append("    left join cn_goodsspec gs on gs.pk_goods=cg.pk_goods");
		sql.append("    left join (select sum(b.ntotalcost) mny, sum(nnum) num, ");
		sql.append("     pk_goodsspec, pk_goods, invspec, invtype");
		sql.append("     from cn_stockin_b b");
		sql.append("     left join cn_stockin si on si.pk_stockin = b.pk_stockin");
		sql.append("     where nvl(b.dr, 0) = 0 and nvl(si.dr, 0) = 0    ");
		if (qvo.getBegdate() != null) {
			sql.append("   AND substr(si.dconfirmtime,0,10) < ? \n");
			spm.addParam(qvo.getBegdate());
		}
		
		sql.append("     group by pk_goodsspec, pk_goods, invspec, invtype) sib");
		sql.append("     on sib.pk_goods = gs.pk_goods");
		sql.append("     and sib.pk_goodsspec =  gs.pk_goodsspec");
		
		sql.append("     left join (select sum(nnum) num, sum(nmny) mny,");
		sql.append("     pk_goodsspec,pk_goods,invspec,invtype");
		sql.append("     from cn_stockout_b ob");
		sql.append("     left join cn_stockout so on so.pk_stockout=ob.pk_stockout");
		sql.append("     where nvl(ob.dr, 0) = 0");
		sql.append("     and nvl(so.dr, 0) = 0");
		if (qvo.getBegdate() != null) {
			sql.append("     AND substr(so.dconfirmtime, 0, 10) < ?  \n");
			spm.addParam(qvo.getBegdate());
		}
		sql.append("     group by pk_goodsspec, pk_goods, invspec, invtype) sob on sob.pk_goods = ");
		sql.append("     gs.pk_goods");
		sql.append("     and sob.pk_goodsspec =");
		sql.append("     gs.pk_goodsspec");
		
		sql.append("     where nvl(cg.dr, 0) = 0");
		sql.append("     and nvl(gs.dr, 0) = 0");

		if (!StringUtil.isEmpty(qvo.getPk_goods())) {

			String[] strs = qvo.getPk_goods().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" AND cg.pk_goods in (").append(inSql).append(")");
		}
		if(!StringUtil.isEmpty(qvo.getPk_goodsspec())){
			sql.append("   AND gs.pk_goodsspec = ? ");
			spm.addParam(qvo.getPk_goodsspec());
		}

		sql.append(" ) s ");
		sql.append("  order by s.pk_goods,s.invspec,s.invtype,s.dconfirmtime");
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
