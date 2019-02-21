package com.dzf.service.channel.dealmanage.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.dealmanage.StockSumVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.dealmanage.IStockSumService;

@Service("sumStock")
public class IStockSumImpl implements IStockSumService {

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Override
	public Integer queryTotalRow(StockSumVO qvo) {
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo,false);
        return multBodyObjectBO.queryDataTotal(StockSumVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}
	
	
	@Override
	public List<StockSumVO> query(StockSumVO qvo) {
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo, true);
		List<StockSumVO> list = (List<StockSumVO>) multBodyObjectBO.queryDataPage(StockSumVO.class, sqpvo.getSql(),
				sqpvo.getSpm(), qvo.getPage(), qvo.getRows(), null);
		DZFDouble start;
		DZFDouble totalin;
		DZFDouble stockout;
		for (StockSumVO stockSumVO : list) {
			if(stockSumVO.getNnumstart()>0 && stockSumVO.getNpricestart().compareTo(DZFDouble.ZERO_DBL)>0){
				stockSumVO.setTotalmoneys(stockSumVO.getNpricestart().multiply(new DZFDouble(stockSumVO.getNnumstart())));
			}
			start=stockSumVO.getTotalmoneys()==null?DZFDouble.ZERO_DBL:stockSumVO.getTotalmoneys();
			totalin=stockSumVO.getTotalmoneyin()==null?DZFDouble.ZERO_DBL:stockSumVO.getTotalmoneyin();
			if(stockSumVO.getNnumend()>0 && stockSumVO.getNpriceend().compareTo(DZFDouble.ZERO_DBL)>0){
				stockSumVO.setTotalmoneye(stockSumVO.getNpriceend().multiply(new DZFDouble(stockSumVO.getNnumend())));
			}
			stockout=stockSumVO.getTotalmoneye()==null?DZFDouble.ZERO_DBL:stockSumVO.getTotalmoneye();
			stockSumVO.setTotalmoneyout(start.add(totalin).sub(stockout));
		}
		return list;
	}
	
	private QrySqlSpmVO getQrySqlSpm(StockSumVO qvo,boolean isDetail) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select cg.vgoodscode, ");
		sql.append("       cg.vgoodsname, ");
		sql.append("       cg.pk_goods, ");
		sql.append("       gs.invspec, ");
		sql.append("       gs.invtype, ");
		sql.append("       gs.pk_goodsspec ");
		
		if(isDetail){
			sql.append("       ,(nvl(b_sib.num, 0) - nvl(b_sob.num, 0)) nnumstart,");// 期初数量
//			sql.append("       nvl(b_sib.total, 0) totalmoneys, ");// 期初金额
			sql.append("  decode((nvl(b_sib.num, 0) - nvl(b_sob.num, 0)),0,0,b_sib.total /(nvl(b_sib.num, 0) - nvl(b_sob.num, 0))) npricestart, ");// 期初金额
			
			sql.append("       m_sib.num nnumin,m_sib.total totalmoneyin, ");
			sql.append("       m_sob.num nnumout,");
			
			sql.append("       (nvl(e_sib.num, 0) - nvl(e_sob.num, 0)) nnumend,");// 期末数量
//			sql.append("       nvl(e_sib.total, 0) totalmoneye ");// 期末金额
			sql.append("  decode((nvl(e_sib.num, 0) - nvl(e_sob.num, 0)),0,0,e_sib.total /(nvl(e_sib.num, 0) - nvl(e_sob.num, 0))) npriceend");// 期初金额
		}	
		sql.append("  from cn_goods cg ");
		sql.append("  left join cn_goodsspec gs on cg.pk_goods = gs.pk_goods ");
		if(isDetail){	
			sql.append(" left join (");
			getStockIn(spm, sql, qvo.getBegdate(), null);
			sql.append(" )b_sib on b_sib.pk_goods = gs.pk_goods and b_sib.pk_goodsspec = gs.pk_goodsspec ");
			
			sql.append(" left join (");
			getStockOut(spm, sql, qvo.getBegdate(), null);
			sql.append(" )b_sob on b_sob.pk_goods = gs.pk_goods and b_sob.pk_goodsspec = gs.pk_goodsspec ");
			
			
			sql.append(" left join (");
			getStockIn(spm, sql, qvo.getBegdate(), qvo.getEnddate());
			sql.append(" )m_sib on m_sib.pk_goods = gs.pk_goods and m_sib.pk_goodsspec = gs.pk_goodsspec ");
			
			sql.append(" left join (");
			getStockOut(spm, sql, qvo.getBegdate(), qvo.getEnddate());
			sql.append(" )m_sob on m_sob.pk_goods = gs.pk_goods and m_sob.pk_goodsspec = gs.pk_goodsspec ");
			
			sql.append(" left join (");
			getStockIn(spm, sql, qvo.getEnddate().getDateAfter(1), null);
			sql.append(" )e_sib on e_sib.pk_goods = gs.pk_goods and e_sib.pk_goodsspec = gs.pk_goodsspec ");
			
			sql.append(" left join (");
			getStockOut(spm, sql, qvo.getEnddate().getDateAfter(1), null);
			sql.append(" )e_sob on e_sob.pk_goods = gs.pk_goods and e_sob.pk_goodsspec = gs.pk_goodsspec ");
		}
		
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
		sql.append("  order by cg.vgoodscode");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	private void getStockOut(SQLParameter spm,StringBuffer sql, DZFDate start, DZFDate end){
		sql.append("select sob.pk_goods, ");
		sql.append("       sob.pk_goodsspec, ");
		sql.append("       sum(nvl(sob.nnum, 0)) num ");
		sql.append("  from cn_stockout_b sob ");
		sql.append("  left join cn_stockout so on so.pk_stockout = sob.pk_stockout ");
		sql.append(" where nvl(sob.dr, 0) = 0 ");
		sql.append("   and nvl(so.dr, 0) = 0 ");
		sql.append("   and (so.vstatus = 1 or so.vstatus = 2) ");
		if(end ==null){
			sql.append("   AND substr(so.dconfirmtime, 0, 10) < ? ");
			spm.addParam(start);
		}else{
			sql.append("   AND substr(so.dconfirmtime, 0, 10) >= ? ");
			sql.append("   AND substr(so.dconfirmtime, 0, 10) <= ? ");
			spm.addParam(start);
			spm.addParam(end);
		}
		sql.append(" group by sob.pk_goods, sob.pk_goodsspec ");
	}
	
	private void getStockIn(SQLParameter spm,StringBuffer sql, DZFDate start, DZFDate end){
		sql.append("select sib.pk_goods, ");
		sql.append("       sib.pk_goodsspec, ");
		sql.append("       sum(nvl(sib.nnum, 0)) num, ");
		sql.append("       sum(nvl(sib.ntotalcost, 0)) total ");
		sql.append("  from cn_stockin_b sib ");
		sql.append("  left join cn_stockin si on si.pk_stockin = sib.pk_stockin ");
		sql.append(" where nvl(si.dr, 0) = 0 ");
		sql.append("   and nvl(sib.dr, 0) = 0 ");
		sql.append("   and si.vstatus = 2 ");
		if(end ==null){
			sql.append("   AND substr(si.dconfirmtime, 0, 10) < ? ");
			spm.addParam(start);
		}else{
			sql.append("   AND substr(si.dconfirmtime, 0, 10) >= ? ");
			sql.append("   AND substr(si.dconfirmtime, 0, 10) <= ? ");
			spm.addParam(start);
			spm.addParam(end);
		}
		sql.append(" group by sib.pk_goods, sib.pk_goodsspec ");
	}

}
