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
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.dealmanage.IStockOutInService;


@Service("outinStock")
public class IStockOutInServiceImpl implements IStockOutInService {

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public List<Long> queryTotalRow(StockOutInMVO qvo) {
		List<Long> total = new ArrayList<Long>();
		
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
		
		List<StockOutInMVO> queryAll = queryAll(qvo, totalList);
		String idString0="";
		String idString1="";
		Integer balanceNum = null;
		Double totalmoneyb=null;
		for (StockOutInMVO stockOutInMVO : totalList) {
			 
              if(stockOutInMVO.getVitype()==0)
              {   //期初的唯一标识
            	  idString0=stockOutInMVO.getPk_goods()+stockOutInMVO.getVgoodscode()+
            			  stockOutInMVO.getInvspec()+stockOutInMVO.getInvtype();
            	  //期初数量
     			 balanceNum = stockOutInMVO.getBalanceNum();
     			  //期初金额
     			totalmoneyb = stockOutInMVO.getTotalmoneyb();
			 }else {
				 String vgoodscode=stockOutInMVO.getVgoodscode();
				 String invspec=stockOutInMVO.getInvspec();
				 String invtype=stockOutInMVO.getInvtype();
				 String pk_goods=stockOutInMVO.getPk_goods();
				 idString1=pk_goods+vgoodscode+invspec+invtype;
//				 
				 if(idString0.equals(idString1)&&stockOutInMVO.getVitype()==1) 
				 {
					 //商品入库
					 stockOutInMVO.setBalanceNum(balanceNum+stockOutInMVO.getNnumin());//结存数量
					 stockOutInMVO.setTotalmoneyb(totalmoneyb+stockOutInMVO.getTotalmoneyin());//结存金额
					 if( stockOutInMVO.getBalanceNum()!=0){//结存成本价
						 stockOutInMVO.setBalancePrice(stockOutInMVO.getTotalmoneyb() / stockOutInMVO.getBalanceNum());
					 }
					 
					 balanceNum=balanceNum+stockOutInMVO.getNnumin();
					 totalmoneyb=totalmoneyb+stockOutInMVO.getTotalmoneyin();
				 }
				 if(idString0.equals(idString1)&&(stockOutInMVO.getVitype()==2
						            ||stockOutInMVO.getVitype()==3)) 
				 {
					 //销售出库和其他出库
					 if(balanceNum!=0){
						 stockOutInMVO.setNpriceout(totalmoneyb / balanceNum);//出库成本价
					 }
					 stockOutInMVO.setTotalmoneyout(stockOutInMVO.getNpriceout()*stockOutInMVO.getNnumout());
					//出库金额
					 stockOutInMVO.setBalanceNum(balanceNum-stockOutInMVO.getNnumout());//结存数量
					 stockOutInMVO.setTotalmoneyb(totalmoneyb-stockOutInMVO.getTotalmoneyout());//结存金额
					 if( stockOutInMVO.getBalanceNum()!=0){//结存成本价
						 stockOutInMVO.setBalancePrice(stockOutInMVO.getTotalmoneyb() / stockOutInMVO.getBalanceNum());
					 }
					//出库金额
					 balanceNum=balanceNum-stockOutInMVO.getNnumout();
					 totalmoneyb=totalmoneyb-stockOutInMVO.getTotalmoneyout();
				 }
				 
			 }
		}
		return totalList;
	}

	
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
				"         gs.invspec, gs.invtype,si.dconfirmtime,1 vitype,si.vbillcode,nvl(sib.nnum,0) nnumin, sib.nprice npricein,");
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
		sql.append(" 		 gs.invspec, gs.invtype,");
		sql.append("   null as dconfirmtime,");
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
		sql.append("    left join cn_stocknum num on cg.pk_goods = num.pk_goods");
		sql.append("    left join cn_goodsspec gs on gs.pk_goodsspec =num.pk_goodsspec");
		sql.append("    left join (select sum(nmny) mny, sum(nnum) num, ");
		sql.append("     pk_goodsspec, pk_goods, invspec, invtype");
		sql.append("     from cn_stockin_b b");
		sql.append("     left join cn_stockin si on si.pk_stockin = b.pk_stockin");
		sql.append("     where nvl(b.dr, 0) = 0 and nvl(si.dr, 0) = 0    ");
		if (qvo.getBegdate() != null) {
			sql.append("   AND substr(si.dconfirmtime,0,10) <= ? \n");
			spm.addParam(qvo.getBegdate());
		}
		sql.append("     group by pk_goodsspec, pk_goods, invspec, invtype) sib");
		sql.append("     on sib.pk_goods = num.pk_goods");
		sql.append("     and sib.pk_goodsspec =  num.pk_goodsspec");
		
		sql.append("     left join (select sum(nnum) num, sum(nmny) mny,");
		sql.append("     pk_goodsspec,pk_goods,invspec,invtype");
		sql.append("     from cn_stockout_b ob");
		sql.append("     left join cn_stockout so on so.pk_stockout=ob.pk_stockout");
		sql.append("     where nvl(ob.dr, 0) = 0");
		sql.append("     and nvl(so.dr, 0) = 0");
		if (qvo.getBegdate() != null) {
			sql.append("     AND substr(so.dconfirmtime, 0, 10) <= ?  \n");
			spm.addParam(qvo.getBegdate());
		}
		sql.append("     group by pk_goodsspec, pk_goods, invspec, invtype) sob on sob.pk_goods = ");
		sql.append("     num.pk_goods");
		sql.append("     and sob.pk_goodsspec =");
		sql.append("     num.pk_goodsspec");
		
		sql.append("     where nvl(cg.dr, 0) = 0");
		sql.append("     and nvl(num.dr, 0) = 0");

		if (!StringUtil.isEmpty(qvo.getPk_goods())) {

			String[] strs = qvo.getPk_goods().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" AND cg.pk_goods in (").append(inSql).append(")");
		}

		sql.append(" ) s ");
		sql.append("  order by s.pk_goods,s.invspec,s.invtype,s.vitype,s.dconfirmtime");
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
