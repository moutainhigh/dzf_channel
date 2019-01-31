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
import com.sun.tools.javah.resources.l10n;

import oracle.net.aso.s;

@Service("outinStock")
public class IStockOutInServiceImpl implements IStockOutInService {

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public List<Long> queryTotalRow(StockOutInMVO qvo) {
		List<Long> total = new ArrayList<Long>();
		/*
		 * if (qvo.getVitype() == 1) {// 入库 QrySqlSpmVO sqpvoin = getQrySqlSpmIn(qvo);
		 * long queryDataTotalIn = multBodyObjectBO.queryDataTotal(StockOutInMVO.class,
		 * sqpvoin.getSql(), sqpvoin.getSpm()); total.add(queryDataTotalIn); return
		 * total; } if (qvo.getVitype() == 2) {// 出库 QrySqlSpmVO sqpvoout =
		 * getQrySqlSpmOut(qvo); long queryDataTotalOut =
		 * multBodyObjectBO.queryDataTotal(StockOutInMVO.class, sqpvoout.getSql(),
		 * sqpvoout.getSpm()); total.add(queryDataTotalOut); return total; }
		 */
		// 全部
		/*
		 * QrySqlSpmVO sqpvoin = getQrySqlSpmIn(qvo); long queryDataTotalIn =
		 * multBodyObjectBO.queryDataTotal(StockOutInMVO.class, sqpvoin.getSql(),
		 * sqpvoin.getSpm()); total.add(queryDataTotalIn); QrySqlSpmVO sqpvoout =
		 * getQrySqlSpmOut(qvo); long queryDataTotalOut =
		 * multBodyObjectBO.queryDataTotal(StockOutInMVO.class, sqpvoout.getSql(),
		 * sqpvoout.getSpm()); total.add(queryDataTotalOut);
		 */

		// 全部
		QrySqlSpmVO sqpvoall = getQrySqlSpmAll(qvo);
		long queryDataTotalAll = multBodyObjectBO.queryDataTotal(StockOutInMVO.class, sqpvoall.getSql(),
				sqpvoall.getSpm());
		total.add(queryDataTotalAll);
		return total;

	}

	@Override
	public List<StockOutInMVO> query(StockOutInMVO qvo) {

		List<StockOutInMVO> totalList = new ArrayList<StockOutInMVO>();
		//UserVO uvo = null;

		/*
		 * if (qvo.getVitype() == 1) {// 商品入库 List<StockOutInMVO> queryIn = queryIn(qvo,
		 * totalList); return queryIn; } if (qvo.getVitype() == 2) {// 销售出库
		 * List<StockOutInMVO> queryOut = queryOut(qvo, totalList); return queryOut; }
		 * if (qvo.getVitype() == 3) {// 其他出库 List<StockOutInMVO> queryqtOut =
		 * queryQtOut(qvo, totalList); return queryqtOut; }
		 */
		// 全部
		/*
		 * List<StockOutInMVO> queryIn = queryIn(qvo, totalList); List<StockOutInMVO>
		 * queryOut = queryOut(qvo, totalList);
		 */

		List<StockOutInMVO> queryAll = queryAll(qvo, totalList);
		String idString0="";
		String idString1="";
		Integer balanceNum=0;
		Integer totalmoneyb=0;
		for (StockOutInMVO stockOutInMVO : totalList) {
              if(stockOutInMVO.getVitype()==0)
              {
            	  idString0=stockOutInMVO.getPk_goods()+stockOutInMVO.getVgoodscode()+
            			  stockOutInMVO.getInvspec()+stockOutInMVO.getInvtype();
            	  //期初数量
				 balanceNum = stockOutInMVO.getBalanceNum();
				  //期初金额
				totalmoneyb = stockOutInMVO.getTotalmoneyb();
			 }else {
				 StockOutInMVO outInMVO = new StockOutInMVO();
				 String vgoodscode=stockOutInMVO.getVgoodscode();
				 String invspec=stockOutInMVO.getInvspec();
				 String invtype=stockOutInMVO.getInvtype();
				 String pk_goods=stockOutInMVO.getPk_goods();
				 idString1=pk_goods+vgoodscode+invspec+invtype;
				 
				 outInMVO.setVgoodscode(vgoodscode);
				 outInMVO.setPk_goods(pk_goods);
				 outInMVO.setInvspec(invspec);
				 outInMVO.setInvtype(invtype);
				 outInMVO.setVitype(0);
				 totalList.add(outInMVO);//添加显示商品的期初余额数据
				 if(idString0.equals(idString1)&&stockOutInMVO.getVitype()==1) 
				 {
					 //商品入库
					 stockOutInMVO.setBalanceNum(balanceNum+stockOutInMVO.getNnumin());//结存数量
					 stockOutInMVO.setTotalmoneyb(totalmoneyb+stockOutInMVO.getTotalmoneyin());//结存金额
				 }
				 if(idString0.equals(idString1)&&(stockOutInMVO.getVitype()==2
						            ||stockOutInMVO.getVitype()==3)) 
				 {
					 //销售出库和其他出库
					 stockOutInMVO.setBalanceNum(balanceNum-stockOutInMVO.getNnumout());//结存数量
					 stockOutInMVO.setTotalmoneyb(totalmoneyb-stockOutInMVO.getTotalmoneyout());//结存金额
				 }
				 if(!idString0.equals(idString1))//另一种商品
				 {
					    idString0="";
						idString1="";
				 }
					 
				 
			 }
		}
		return totalList;
	}

	/**
	 * 其他出库查询
	 * 
	 * @param qvo
	 * @return
	 */
	/*
	 * private List<StockOutInMVO> queryQtOut(StockOutInMVO qvo, List<StockOutInMVO>
	 * totalList) { QrySqlSpmVO sqpvoout = getQrySqlSpmQtOut(qvo);
	 * 
	 * @SuppressWarnings("unchecked") List<StockOutInMVO> listqtout =
	 * (List<StockOutInMVO>) multBodyObjectBO.queryDataPage(StockOutInMVO.class,
	 * sqpvoout.getSql(), sqpvoout.getSpm(), qvo.getPage(), qvo.getRows(), null);
	 * for (StockOutInMVO stockOutInMVO : listqtout) { totalList.add(stockOutInMVO);
	 * } return totalList; }
	 * 
	 *//**
		 * 出库查询
		 * 
		 * @param qvo
		 * @return
		 */
	/*
	 * private List<StockOutInMVO> queryOut(StockOutInMVO qvo, List<StockOutInMVO>
	 * totalList) { QrySqlSpmVO sqpvoout = getQrySqlSpmOut(qvo);
	 * 
	 * @SuppressWarnings("unchecked") List<StockOutInMVO> listout =
	 * (List<StockOutInMVO>) multBodyObjectBO.queryDataPage(StockOutInMVO.class,
	 * sqpvoout.getSql(), sqpvoout.getSpm(), qvo.getPage(), qvo.getRows(), null);
	 * for (StockOutInMVO stockOutInMVO : listout) { totalList.add(stockOutInMVO); }
	 * return totalList; }
	 * 
	 *//**
		 * 入库查询
		 * 
		 * @param qvo
		 * @return
		 *//*
			 * private List<StockOutInMVO> queryIn(StockOutInMVO qvo, List<StockOutInMVO>
			 * totalList) { QrySqlSpmVO sqpvoin = getQrySqlSpmIn(qvo);
			 * 
			 * @SuppressWarnings("unchecked") List<StockOutInMVO> listin =
			 * (List<StockOutInMVO>) multBodyObjectBO.queryDataPage(StockOutInMVO.class,
			 * sqpvoin.getSql(), sqpvoin.getSpm(), qvo.getPage(), qvo.getRows(), null); for
			 * (StockOutInMVO stockOutInMVO : listin) { totalList.add(stockOutInMVO); }
			 * return totalList; }
			 */

	/**
	 * 查询全部
	 * 
	 * @param qvo
	 * @param totalList
	 * @return
	 */
	private List<StockOutInMVO> queryAll(StockOutInMVO qvo, List<StockOutInMVO> totalList) {
		QrySqlSpmVO sqpvoall = getQrySqlSpmAll(qvo);
		@SuppressWarnings("unchecked")
		List<StockOutInMVO> listall = (List<StockOutInMVO>) multBodyObjectBO.queryDataPage(StockOutInMVO.class,
				sqpvoall.getSql(), sqpvoall.getSpm(), qvo.getPage(), qvo.getRows(), null);
		for (StockOutInMVO stockOutInMVO : listall) {
			totalList.add(stockOutInMVO);
		}
		return totalList;
	}

	/**
	 * 查询全部——获取查询条件
	 * 
	 * @param qvo
	 * @return
	 */
	private QrySqlSpmVO getQrySqlSpmAll(StockOutInMVO qvo) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();

		sql.append(" select s.* from  ");
		sql.append("  (select cg.vgoodscode, cg.vgoodsname,cg.pk_goods, ");
		sql.append(
				"         sib.invspec, sib.invtype,si.dconfirmtime,1 vitype,si.vbillcode,nvl(sib.nnum,0) nnumin, sib.nprice npricein,");
		sql.append("        sib.nprice * nvl(sib.nnum,0) totalmoneyin, ");
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
		sql.append("     left join cn_stocknum num on sib.pk_goods = num.pk_goods and");
		sql.append("     sib.pk_goodsspec =  num.pk_goodsspec");
		sql.append("  where nvl(cg.dr, 0) = 0 ");
		sql.append("    and nvl(si.dr, 0) = 0 ");
		sql.append("    and nvl(gs.dr, 0) = 0 ");
		sql.append("    and nvl(num.dr, 0) = 0 ");
		sql.append("    and nvl(sib.dr, 0) = 0 ");
		sql.append("    and si.vstatus = 2 ");
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
		sql.append("  union all");
		sql.append(" select");
		sql.append(" 		cg.vgoodscode, cg.vgoodsname,cg.pk_goods, ");
		sql.append("        gs.invspec, gs.invtype, ");
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
		sql.append("    left join cn_stockin_b sib");
		sql.append("    on sob.pk_goods = sib.pk_goods");
		sql.append("    and sib.pk_goodsspec = sob.pk_goodsspec");
		sql.append("     left join cn_stocknum num on num.pk_goods=sib.pk_goods and num.pk_goodsspec=sib.pk_goodsspec");

		sql.append("  where nvl(cg.dr, 0) = 0 ");
		sql.append("    and nvl(gs.dr, 0) = 0 ");
		sql.append("    and nvl(sob.dr, 0) = 0 ");
		sql.append("    and nvl(so.dr, 0) = 0 ");
		sql.append("    and nvl(num.dr, 0) = 0 ");
		sql.append("    and nvl(sib.dr, 0) = 0 ");
		sql.append("    and (so.vstatus = 1 or so.vstatus = 2) ");
		sql.append("    and nvl(so.itype,0) = 0 ");
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
		sql.append("  union all ");
		sql.append(" select");
		sql.append(" 		cg.vgoodscode, cg.vgoodsname,cg.pk_goods, ");
		sql.append("        gs.invspec, gs.invtype, ");
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
		sql.append("    left join cn_stockin_b sib");
		sql.append("    on sob.pk_goods = sib.pk_goods");
		sql.append("    and sib.pk_goodsspec = sob.pk_goodsspec");
		sql.append("     left join cn_stocknum num on num.pk_goods=sib.pk_goods and num.pk_goodsspec=sib.pk_goodsspec");

		sql.append("  where nvl(cg.dr, 0) = 0 ");
		sql.append("    and nvl(gs.dr, 0) = 0 ");
		sql.append("    and nvl(sob.dr, 0) = 0 ");
		sql.append("    and nvl(so.dr, 0) = 0 ");
		sql.append("    and nvl(num.dr, 0) = 0 ");
		sql.append("    and nvl(sib.dr, 0) = 0 ");
		sql.append("    and (so.vstatus = 1 or so.vstatus = 2) ");
		sql.append("    and so.itype = 1");

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

		sql.append(" union all ");
		sql.append("  select cg.vgoodscode, cg.vgoodsname, cg.pk_goods,  ");
		sql.append(" 		 sib.invspec, sib.invtype,");
		sql.append("   null as dconfirmtime,");
		sql.append("  0 vitype,");
		sql.append("   '' as vode,");
		sql.append("   0 as nnumin,");// 只作为显示
		sql.append("   0 as npricein,");
		sql.append("   0 as totalmoneyin,");
		sql.append("       0 as nnumout,");
		sql.append("       0 as npriceout,");
		sql.append("       0 as totalmoneyout,");
		sql.append("         (nvl(num.istocknum,0) - nvl(num.ioutnum,0)) balanceNum, ");
		sql.append("         decode(sib.num,0,0,sib.mny / sib.num) balancePrice, ");
		sql.append("         ((num.istocknum - num.ioutnum) * decode(sib.num,0,0,sib.mny / sib.num)) totalmoneyb");
		sql.append("    from cn_goods cg ");
		sql.append("    left join cn_stocknum num on cg.pk_goods = num.pk_goods");
		sql.append("    left join (select sum(nmny) mny, sum(nnum) num,pk_stockin, ");
		sql.append("     pk_goodsspec, pk_goods, invspec, invtype");
		sql.append("     from cn_stockin_b where nvl(dr, 0) = 0 ");
		sql.append("     group by pk_goodsspec, pk_goods, invspec, invtype,pk_stockin) sib");
		sql.append("    on sib.pk_goods = num.pk_goods");
		sql.append("     and sib.pk_goodsspec =  num.pk_goodsspec");
		sql.append("    left join cn_stockin si on si.pk_stockin = sib.pk_stockin");

		sql.append("  where nvl(cg.dr, 0) = 0 ");
		sql.append("    and nvl(num.dr, 0) = 0 ");

		if (!StringUtil.isEmpty(qvo.getPk_goods())) {

			String[] strs = qvo.getPk_goods().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" AND cg.pk_goods in (").append(inSql).append(")");
		}

		if (qvo.getBegdate() != null) {
			sql.append("   AND substr(si.dconfirmtime,0,10) <= ? \n");
			spm.addParam(qvo.getBegdate());
		}

		sql.append(" ) s ");
		sql.append("  order by s.vgoodscode,s.vgoodsname,s.invspec,s.invtype,s.vitype asc,s.dconfirmtime");
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

	/**
	 * 出库——获取查询条件
	 * 
	 * @param qvo
	 * @return
	 * @throws DZFWarpException
	 */
	/*
	 * private QrySqlSpmVO getQrySqlSpmOut(StockOutInMVO qvo) { QrySqlSpmVO qryvo =
	 * new QrySqlSpmVO(); StringBuffer sql = new StringBuffer(); SQLParameter spm =
	 * new SQLParameter();
	 * 
	 * sql.append(" select 2 vitype, so.vbillcode, so.dconfirmtime, ");
	 * sql.append(" 		cg.vgoodscode, cg.vgoodsname,cg.pk_goods, ");
	 * sql.append("         nvl(sob.nnum, 0) nnumout,");
	 * sql.append("        gs.invspec, gs.invtype, "); sql.
	 * append("         (sib.nprice * nvl(sib.nnum,0) + sib.mny) / (nvl(sib.nnum,0) + sib.num) npriceout,"
	 * ); sql.append(
	 * "         nvl(sob.nnum, 0)* ((sib.nprice * nvl(sib.nnum,0) + sib.mny) / (nvl(sib.nnum,0) + sib.num)) totalmoneyout,"
	 * ); sql.
	 * append("          (num.istocknum - num.ioutnum + nvl(sib.nnum,0) - nvl(sob.nnum, 0)) balanceNum,"
	 * ); sql.
	 * append("          (sib.nprice * nvl(sib.nnum,0) + sib.mny) / (nvl(sib.nnum,0) + sib.num) balancePrice,"
	 * ); sql.append(
	 * "         ((num.istocknum - num.ioutnum + nvl(sib.nnum,0) - nvl(sob.nnum, 0)) *((sib.nprice * nvl(sib.nnum,0) + sib.mny) / (nvl(sib.nnum,0) + sib.num))) totalmoneyb"
	 * ); sql.append("    from cn_goods cg ");
	 * sql.append("    left join cn_goodsspec gs on cg.pk_goods=gs.pk_goods ");
	 * sql.append("    left join cn_stockout_b sob on sob.pk_goods= gs.pk_goods ");
	 * sql.append("    and sob.pk_goodsspec = gs.pk_goodsspec"); sql.
	 * append("    left join cn_stockout so on so.pk_stockout = sob.pk_stockout ");
	 * sql.append(
	 * "    left join (select nvl(nnum,0) nnum,nprice,sum(nmny) mny, sum(nnum) num,pk_goodsspec,pk_goods from cn_stockin_b  "
	 * ); sql.
	 * append("    group by nvl(nnum,0),nprice,pk_goodsspec,pk_goods) sib on sob.pk_goods = sib.pk_goods"
	 * ); sql.append("    and sib.pk_goodsspec = sob.pk_goodsspec"); sql.
	 * append("     left join cn_stocknum num on num.pk_goods=sib.pk_goods and num.pk_goodsspec=sib.pk_goodsspec"
	 * );
	 * 
	 * sql.append("  where nvl(cg.dr, 0) = 0 ");
	 * sql.append("    and nvl(gs.dr, 0) = 0 ");
	 * sql.append("    and nvl(sob.dr, 0) = 0 ");
	 * sql.append("    and nvl(so.dr, 0) = 0 ");
	 * sql.append("    and nvl(sib.dr, 0) = 0 ");
	 * sql.append("    and nvl(num.dr, 0) = 0 ");
	 * sql.append("    and (so.vstatus = 1 or so.vstatus = 2) ");
	 * sql.append("    and nvl(so.itype,0) = 0 ");
	 * 
	 * if (!StringUtil.isEmpty(qvo.getVbillcode())) {
	 * sql.append("   AND so.vbillcode like ? \n"); spm.addParam("%" +
	 * qvo.getVbillcode() + "%"); }
	 * 
	 * if (!StringUtil.isEmpty(qvo.getPk_goods())) {
	 * 
	 * String[] strs = qvo.getPk_goods().split(","); String inSql =
	 * SqlUtil.buildSqlConditionForIn(strs);
	 * sql.append(" AND cg.pk_goods in (").append(inSql).append(")"); } if
	 * (qvo.getBegdate() != null) {
	 * sql.append("   AND substr(so.dconfirmtime,0,10) >= ? \n");
	 * spm.addParam(qvo.getBegdate()); } if (qvo.getEnddate() != null) {
	 * sql.append("   AND substr(so.dconfirmtime,0,10) <= ? \n");
	 * spm.addParam(qvo.getEnddate()); }
	 * sql.append(" order by cg.vgoodscode,cg.vgoodsname,gs.invspec, gs.invtype");
	 * qryvo.setSql(sql.toString()); qryvo.setSpm(spm); return qryvo; }
	 * 
	 *//**
		 * 入库——获取查询条件
		 * 
		 * @param qvo
		 * @return
		 * @throws DZFWarpException
		 */
	/*
	 * private QrySqlSpmVO getQrySqlSpmIn(StockOutInMVO qvo) throws DZFWarpException
	 * { QrySqlSpmVO qryvo = new QrySqlSpmVO(); StringBuffer sql = new
	 * StringBuffer(); SQLParameter spm = new SQLParameter();
	 * 
	 * sql.append("  select 1 vitype, cg.vgoodscode, cg.vgoodsname, cg.pk_goods,  "
	 * ); sql.append(" 		 si.vbillcode, si.dconfirmtime , "); sql.
	 * append("         sib.invspec, sib.invtype, sib.nprice npricein, nvl(sib.nnum,0) nnumin, "
	 * ); sql.append("        sib.nprice * nvl(sib.nnum,0) totalmoneyin, ");
	 * sql.append("        (nvl(sib.nnum,0) + sib.num) balanceNum, "); sql.
	 * append("        (sib.nprice * nvl(sib.nnum,0) + sib.mny) / (nvl(sib.nnum,0) + sib.num) balancePrice,"
	 * ); sql.append(
	 * "        ((nvl(sib.nnum,0) + sib.num) * ((sib.nprice * nvl(sib.nnum,0) + sib.mny) / (sib.nnum + sib.num) )) totalmoneyb "
	 * ); sql.append("       from cn_goods cg ");
	 * sql.append("       left join cn_goodsspec gs on cg.pk_goods=gs.pk_goods  ");
	 * 
	 * sql.append("     left join (select sum(nmny) mny, sum(nnum) num,  "); sql.
	 * append("     pk_goodsspec, pk_goods, invspec, invtype,nprice,pk_stockin,nvl(nnum,0) nnum"
	 * ); sql.append("     from cn_stockin_b"); sql.append(
	 * "     group by pk_goodsspec, pk_goods, invspec, invtype,nprice,nvl(nnum,0),pk_stockin,nvl(nnum,0) ) sib "
	 * ); sql.append("     on gs.pk_goods=sib.pk_goods");
	 * sql.append("     and sib.pk_goodsspec=gs.pk_goodsspec");
	 * sql.append("     left join cn_stockin si on si.pk_stockin=sib.pk_stockin  ");
	 * sql.
	 * append("     left join cn_stocknum num on sib.pk_goods = num.pk_goods and");
	 * sql.append("     sib.pk_goodsspec =  num.pk_goodsspec");
	 * sql.append("  where nvl(cg.dr, 0) = 0 ");
	 * sql.append("    and nvl(si.dr, 0) = 0 ");
	 * sql.append("    and nvl(sib.dr, 0) = 0 ");
	 * sql.append("    and nvl(gs.dr, 0) = 0 ");
	 * sql.append("    and nvl(num.dr, 0) = 0 ");
	 * sql.append("    and si.vstatus = 2 ");
	 * 
	 * if (!StringUtil.isEmpty(qvo.getVbillcode())) {
	 * sql.append("   AND si.vbillcode like ? \n"); spm.addParam("%" +
	 * qvo.getVbillcode() + "%"); }
	 * 
	 * if (!StringUtil.isEmpty(qvo.getPk_goods())) {
	 * 
	 * String[] strs = qvo.getPk_goods().split(","); String inSql =
	 * SqlUtil.buildSqlConditionForIn(strs);
	 * sql.append(" AND cg.pk_goods in (").append(inSql).append(")"); } if
	 * (qvo.getBegdate() != null) {
	 * sql.append("   AND substr(si.dconfirmtime,0,10) >= ? \n");
	 * spm.addParam(qvo.getBegdate()); } if (qvo.getEnddate() != null) {
	 * sql.append("   AND substr(si.dconfirmtime,0,10) <= ? \n");
	 * spm.addParam(qvo.getEnddate()); }
	 * sql.append(" ORDER BY cg.vgoodscode,cg.vgoodsname,sib.invspec, sib.invtype");
	 * qryvo.setSql(sql.toString()); qryvo.setSpm(spm); return qryvo; }
	 * 
	 *//**
		 * 期初余额——获取查询条件
		 * 
		 * @param qvo
		 * @return
		 * @throws DZFWarpException
		 */

	/*
	 * private QrySqlSpmVO getQrySqlSpmQc(StockOutInMVO qvo) throws DZFWarpException
	 * { QrySqlSpmVO qryvo = new QrySqlSpmVO(); StringBuffer sql = new
	 * StringBuffer(); SQLParameter spm = new SQLParameter();
	 * 
	 * sql.append("  select 0 vitype, cg.vgoodscode, cg.vgoodsname, cg.pk_goods,  "
	 * ); sql.append(" 		 sib.invspec, sib.invtype,");
	 * sql.append("         (num.istocknum - num.ioutnum) balanceNum, ");
	 * sql.append("         (sib.mny / sib.num) balancePrice, "); sql.
	 * append("         ((num.istocknum - num.ioutnum) * (sib.mny / sib.num)) totalmoneyb"
	 * ); sql.append("    from cn_goods cg ");
	 * sql.append("    left join cn_stocknum num on cg.pk_goods = num.pk_goods");
	 * sql.append("    left join (select sum(nmny) mny, sum(nnum) num, ");
	 * sql.append("     pk_goodsspec, pk_goods, invspec, invtype");
	 * sql.append("     from cn_stockin_b where nvl(sib.dr, 0) = 0 ");
	 * sql.append("     group by pk_goodsspec, pk_goods, invspec, invtype ) sib");
	 * sql.append("    on sib.pk_goods = num.pk_goods");
	 * sql.append("     and sib.pk_goodsspec =  num.pk_goodsspec");
	 * 
	 * sql.append("  where nvl(cg.dr, 0) = 0 ");
	 * sql.append("    and nvl(num.dr, 0) = 0 ");
	 * 
	 * sql.append(" ORDER BY cg.vgoodscode,cg.vgoodsname, sib.invspec, sib.invtype"
	 * );
	 * 
	 * if (!StringUtil.isEmpty(qvo.getPk_goods())) {
	 * 
	 * String[] strs = qvo.getPk_goods().split(","); String inSql =
	 * SqlUtil.buildSqlConditionForIn(strs);
	 * sql.append(" AND cg.pk_goods in (").append(inSql).append(")"); }
	 * 
	 * qryvo.setSql(sql.toString()); qryvo.setSpm(spm); return qryvo; }
	 * 
	 *//**
		 * 其他出库——获取查询条件
		 * 
		 * @param qvo
		 * @return
		 * @throws DZFWarpException
		 *//*
			 * private QrySqlSpmVO getQrySqlSpmQtOut(StockOutInMVO qvo) { QrySqlSpmVO qryvo
			 * = new QrySqlSpmVO(); StringBuffer sql = new StringBuffer(); SQLParameter spm
			 * = new SQLParameter();
			 * 
			 * sql.append(" select 3 vitype, so.vbillcode, so.dconfirmtime, ");
			 * sql.append(" 		cg.vgoodscode, cg.vgoodsname,cg.pk_goods, ");
			 * sql.append("         nvl(sob.nnum, 0) nnumout,");
			 * sql.append("        gs.invspec, gs.invtype, "); sql.
			 * append("         (sib.nprice * nvl(sib.nnum,0) + sib.mny) / (nvl(sib.nnum,0) + sib.num) npriceout,"
			 * ); sql.append(
			 * "         nvl(sob.nnum, 0)* ((sib.nprice * nvl(sib.nnum,0) + sib.mny) / (nvl(sib.nnum,0) + sib.num)) totalmoneyout,"
			 * ); sql.
			 * append("          (num.istocknum - num.ioutnum + nvl(sib.nnum,0) - nvl(sob.nnum, 0)) balanceNum,"
			 * ); sql.
			 * append("          (sib.nprice * nvl(sib.nnum,0) + sib.mny) / (nvl(sib.nnum,0) + sib.num) balancePrice,"
			 * ); sql.append(
			 * "          ((num.istocknum - num.ioutnum + nvl(sib.nnum,0) - nvl(sob.nnum, 0)) *((sib.nprice * nvl(sib.nnum,0) + sib.mny) / (nvl(sib.nnum,0) + sib.num))) totalmoneyb"
			 * ); sql.append("    from cn_goods cg ");
			 * sql.append("    left join cn_goodsspec gs on cg.pk_goods=gs.pk_goods ");
			 * sql.append("    left join cn_stockout_b sob on sob.pk_goods= gs.pk_goods ");
			 * sql.append("    and sob.pk_goodsspec = gs.pk_goodsspec"); sql.
			 * append("    left join cn_stockout so on so.pk_stockout = sob.pk_stockout ");
			 * sql.append(
			 * "    left join (select nvl(nnum,0) nnum,nprice,sum(nmny) mny, sum(nnum) num,pk_goodsspec,pk_goods from cn_stockin_b  "
			 * ); sql.
			 * append("    group by nvl(nnum,0),nprice,pk_goodsspec,pk_goods) sib on sob.pk_goods = sib.pk_goods"
			 * ); sql.append("    and sib.pk_goodsspec = sob.pk_goodsspec"); sql.
			 * append("     left join cn_stocknum num on num.pk_goods=sib.pk_goods and num.pk_goodsspec=sib.pk_goodsspec"
			 * );
			 * 
			 * sql.append("  where nvl(cg.dr, 0) = 0 ");
			 * sql.append("    and nvl(gs.dr, 0) = 0 ");
			 * sql.append("    and nvl(sob.dr, 0) = 0 ");
			 * sql.append("    and nvl(so.dr, 0) = 0 ");
			 * sql.append("    and nvl(sib.dr, 0) = 0 ");
			 * sql.append("    and nvl(num.dr, 0) = 0 ");
			 * sql.append("    and (so.vstatus = 1 or so.vstatus = 2) ");
			 * sql.append("    and so.itype = 1 ");
			 * 
			 * if (!StringUtil.isEmpty(qvo.getVbillcode())) {
			 * sql.append("   AND so.vbillcode like ? \n"); spm.addParam("%" +
			 * qvo.getVbillcode() + "%"); }
			 * 
			 * if (!StringUtil.isEmpty(qvo.getPk_goods())) {
			 * 
			 * String[] strs = qvo.getPk_goods().split(","); String inSql =
			 * SqlUtil.buildSqlConditionForIn(strs);
			 * sql.append(" AND cg.pk_goods in (").append(inSql).append(")"); } if
			 * (qvo.getBegdate() != null) {
			 * sql.append("   AND substr(so.dconfirmtime,0,10) >= ? \n");
			 * spm.addParam(qvo.getBegdate()); } if (qvo.getEnddate() != null) {
			 * sql.append("   AND substr(so.dconfirmtime,0,10) <= ? \n");
			 * spm.addParam(qvo.getEnddate()); }
			 * sql.append(" order by cg.vgoodscode,cg.vgoodsname,gs.invspec, gs.invtype,");
			 * qryvo.setSql(sql.toString()); qryvo.setSpm(spm); return qryvo; }
			 */
}
