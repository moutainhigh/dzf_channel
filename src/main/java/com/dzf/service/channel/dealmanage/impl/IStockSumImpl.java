package com.dzf.service.channel.dealmanage.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.omg.Security.SecQOPPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.action.channel.dealmanage.StockOutInAction;
import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ResultSetProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.StockInVO;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.channel.dealmanage.StockSumVO;
import com.dzf.model.channel.stock.GoodsNumVO;
import com.dzf.model.channel.stock.StockNumVO;
import com.dzf.model.channel.stock.StockOutVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.dealmanage.IStockOutInService;
import com.dzf.service.channel.dealmanage.IStockSumService;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.sun.tools.classfile.StackMap_attribute.stack_map_frame;
import com.sun.tools.javah.resources.l10n;
import com.sun.xml.internal.xsom.impl.scd.Iterators.Map;

import oracle.net.aso.d;
import oracle.net.aso.s;

@Service("sumStock")
public class IStockSumImpl implements IStockSumService {

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IStockOutInService stockoutin;
	
	@Override
	public Integer queryTotalRow(StockSumVO qvo) {
		QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
        return multBodyObjectBO.queryDataTotal(StockSumVO.class, sqpvo.getSql(), sqpvo.getSpm());
	}
	
	@Override
	public List<StockSumVO> query(StockSumVO qvo) {
		 QrySqlSpmVO sqpvo = getQrySqlSpm(qvo);
	     List<StockSumVO> list = (List<StockSumVO>) multBodyObjectBO.queryDataPage(StockSumVO.class, sqpvo.getSql(),
	                sqpvo.getSpm(), qvo.getPage(), qvo.getRows(), null);
	    StockOutInMVO qvo2 = new StockOutInMVO();
	    qvo2.setPk_goods(qvo.getPk_goods());
	    qvo2.setBegdate(qvo.getBegdate());
	    qvo2.setEnddate(qvo.getEnddate());
	    //查询出所有商品出入库明细
	    List<StockOutInMVO> stockoutinList = getQrySqlSpm2(qvo2);
	   
	    String idString0="";
	    String idString1="";
	    String idString2="";
	    Integer balanceNum = null;
		DZFDouble totalmoneyb=null;
		Integer length=0;
		//LinkedHashMap<String,DZFDouble> sum= new LinkedHashMap<String,DZFDouble>();//商品出库金额总和
		DZFDouble sumpriceout=DZFDouble.ZERO_DBL;
	    List<String> listsum = new ArrayList<String>();
	    List<String> listoutin = new ArrayList<String>();
	    for (StockOutInMVO stockOutInMVO : stockoutinList) {
	    	if(stockOutInMVO.getVitype()==0){
	    		idString0=stockOutInMVO.getPk_goods()+stockOutInMVO.getVgoodscode()+
		    			stockOutInMVO.getInvspec()+stockOutInMVO.getInvtype();
	    		  //期初数量
    			 balanceNum = stockOutInMVO.getBalanceNum();
    			  //期初金额
    			 totalmoneyb = stockOutInMVO.getTotalmoneyb();
    			// sum.put(idString1, sumpriceout);
    			 
    			 for (StockSumVO stockSumVO : list) {
    				 
    		  		  idString2=stockSumVO.getPk_goods()+stockSumVO.getVgoodscode()+
    		  				  stockSumVO.getInvspec()+stockSumVO.getInvtype();
    		  		    if(idString1.equals(idString2)){
    		  		    	stockSumVO.setTotalmoneyout(sumpriceout);
    		  		    }
    				}
    			 sumpriceout=DZFDouble.ZERO_DBL;
	    	}else{
	    		 
				 String invspec=stockOutInMVO.getInvspec();
				 String invtype=stockOutInMVO.getInvtype();
				 String pk_goods=stockOutInMVO.getPk_goods();
				 String vgoodscode=stockOutInMVO.getVgoodscode();
				 idString1=stockOutInMVO.getPk_goods()+stockOutInMVO.getVgoodscode()+
			    			stockOutInMVO.getInvspec()+stockOutInMVO.getInvtype();
				 if(idString0.equals(idString1)&&stockOutInMVO.getVitype()==1) 
				 {
					 //商品入库
					 stockOutInMVO.setBalanceNum(balanceNum+stockOutInMVO.getNnumin());//结存数量
					 if(stockOutInMVO.getTotalmoneyin()!=null){
						 stockOutInMVO.setTotalmoneyb(totalmoneyb.add(stockOutInMVO.getTotalmoneyin()));//结存金额
					 }
					 if(stockOutInMVO.getBalanceNum()!=0){//结存单价
						 stockOutInMVO.setBalancePrice(stockOutInMVO.getTotalmoneyb().div(stockOutInMVO.getBalanceNum()) );
					 }
					 
					 balanceNum=balanceNum+stockOutInMVO.getNnumin();
					 if(stockOutInMVO.getTotalmoneyin()!=null){
						 totalmoneyb=totalmoneyb.add(stockOutInMVO.getTotalmoneyin());
					 }
				 }
				 
				 if(idString0.equals(idString1)&&(stockOutInMVO.getVitype()==2
				            ||stockOutInMVO.getVitype()==3)) {
					 
					//销售出库和其他出库
					 if(balanceNum!=0){
						 stockOutInMVO.setNpriceout(totalmoneyb.div(balanceNum));//出库单价
					 }
					//出库金额
					 DZFDouble npriceout=stockOutInMVO.getNpriceout();
					 stockOutInMVO.setTotalmoneyout(npriceout.setScale(0, 2).multiply(stockOutInMVO.getNnumout()));
					 sumpriceout= sumpriceout.add(stockOutInMVO.getTotalmoneyout());
					 
					 stockOutInMVO.setBalanceNum(balanceNum-stockOutInMVO.getNnumout());//结存数量
					 stockOutInMVO.setTotalmoneyb(totalmoneyb.sub(stockOutInMVO.getTotalmoneyout()));//结存金额
					 if( stockOutInMVO.getBalanceNum()!=0){//结存单价
					 stockOutInMVO.setBalancePrice(stockOutInMVO.getTotalmoneyb().div(stockOutInMVO.getBalanceNum()));
					 }
					//出库金额
					 balanceNum=balanceNum-stockOutInMVO.getNnumout();
					 totalmoneyb=totalmoneyb.sub(stockOutInMVO.getTotalmoneyout());
	    	    }
		}
	    	
   }	    
	    for (StockOutInMVO stockOutInMVO : stockoutinList) {
	    	for (StockSumVO stockSumVO : list) {
				 
		  		  idString2=stockSumVO.getPk_goods()+stockSumVO.getVgoodscode()+
		  				  stockSumVO.getInvspec()+stockSumVO.getInvtype();
		  		    if(idString1.equals(idString2)){
		  		    	stockSumVO.setTotalmoneyout(sumpriceout);
		  		    }
				}
	    }
	        return list;
}

	@Override
	public List<GoodsBoxVO> queryComboBox() {
		return null;
	}
	
	private QrySqlSpmVO getQrySqlSpm(StockSumVO qvo) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("    select cg.pk_goods, cg.vgoodscode, cg.vgoodsname, ");
		sql.append("    spec.invspec,spec.invtype, ");
		sql.append("    sib2.num nnumin,");
		sql.append("    spec.pk_goodsspec, ");
		sql.append("    sib2.total totalmoneyin, ");
		sql.append("    sob.num nnumout, null as totalmoneyout,");
		sql.append("    nvl(sib1.num,0)  nnumstart,");
		sql.append("    decode( nvl(sib1.num,0), 0, 0, sib1.mny /nvl(sib1.num,0) )  npricestart,");
		sql.append("    sib1.mny totalmoneys, ");
		sql.append("   ( (nvl(sib1.num,0)+nvl(sib2.num,0)-nvl(sob.num,0))) nnumend, ");
		sql.append("    (nvl(sib1.mny,0) + nvl(sib2.total,0) - nvl(sob.mny,0)) totalmoneye, ");
		sql.append("    decode((( nvl(sib1.num,0)+nvl(sib2.num,0)-nvl(sob.num,0) ) ),0,0,");
		sql.append("    ( (nvl(sib1.mny,0) + nvl(sib2.total,0) - nvl(sob.mny,0))   /");
		sql.append("    ( (  nvl(sib1.num,0)+nvl(sib2.num,0)-nvl(sob.num,0)) )) ) npriceend");
		
		sql.append("    from cn_goods cg");
		sql.append("    left join cn_goodsspec spec on cg.pk_goods = spec.pk_goods ");
		sql.append("    left join (select sum(nmny) mny,sum(nnum) num, ");
		sql.append("    pk_goodsspec, pk_goods ");
		sql.append("    from cn_stockin_b ib ");
		sql.append("    left join cn_stockin si on si.pk_stockin =ib.pk_stockin ");
		if (qvo.getBegdate() != null) {
			sql.append("   where substr(si.dconfirmtime,0,10) < ? \n");
			spm.addParam(qvo.getBegdate());
		}
		sql.append("     and nvl(ib.dr,0)=0");
		sql.append("    group by pk_goodsspec, pk_goods) sib1 on sib1.pk_goods =  spec.pk_goods ");
		sql.append("    and sib1.pk_goodsspec = spec.pk_goodsspec");
		
		sql.append("    left join (select sum(ib.ntotalcost) total,sum(nnum) num,pk_goodsspec,pk_goods");
		sql.append("    from cn_stockin_b ib ");
		sql.append("    left join cn_stockin si on si.pk_stockin = ib.pk_stockin ");
		
		if (qvo.getBegdate() != null) {
			sql.append("   where substr(si.dconfirmtime,0,10) >= ? \n");
			spm.addParam(qvo.getBegdate());
		}
		if (qvo.getEnddate() != null) {
			sql.append("   and  substr(si.dconfirmtime,0,10) <= ? \n");
			spm.addParam(qvo.getEnddate());
		}
		sql.append("     and nvl(ib.dr,0)=0");
		sql.append("     group by pk_goodsspec, pk_goods) sib2 on sib2.pk_goods =spec.pk_goods");
		sql.append("     and sib2.pk_goodsspec = spec.pk_goodsspec");
		sql.append("     left join (select sum(nnum) num, sum(nmny) mny, pk_goodsspec,pk_goods");
		sql.append("     from cn_stockout_b ob");
		sql.append("     left join cn_stockout so on ob.pk_stockout = so.pk_stockout");
		if (qvo.getBegdate() != null) {
			sql.append("   where substr(so.dconfirmtime,0,10) >= ? \n");
			spm.addParam(qvo.getBegdate());
		}
		if (qvo.getEnddate() != null) {
			sql.append("   and substr(so.dconfirmtime,0,10) <= ?  \n ");
			spm.addParam(qvo.getEnddate());
		}
		sql.append("     and nvl(ob.dr,0)=0");
		sql.append("     group by pk_goodsspec, pk_goods) sob on sob.pk_goods =spec.pk_goods");
		sql.append("     and sob.pk_goodsspec = ");
		sql.append("     spec.pk_goodsspec");
		sql.append("     where nvl(cg.dr, 0) = 0");
		sql.append("     and nvl(spec.dr, 0) = 0");
		
		if (!StringUtil.isEmpty(qvo.getPk_goods())) {

			String[] strs = qvo.getPk_goods().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" and cg.pk_goods in (").append(inSql).append(")");
		}
		
		
		
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		
		return qryvo;
	}

	private List<StockOutInMVO> getQrySqlSpm2(StockOutInMVO qvo) {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();

		sql.append(" select s.* from  ");
		sql.append("  (select cg.vgoodscode, cg.vgoodsname,cg.pk_goods, ");
		sql.append(
				"         gs.invspec, gs.invtype,si.dconfirmtime,1 vitype,si.vbillcode,nvl(sib.nnum,0) nnumin, sib.nprice npricein,");
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
		sql.append("    left join (select sum(nmny) mny, sum(nnum) num, ");
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

		sql.append(" ) s ");
		sql.append("  order by s.pk_goods,s.invspec,s.invtype,s.dconfirmtime");
		
		List<StockOutInMVO> outinList = (List<StockOutInMVO>) singleObjectBO.executeQuery(sql.toString(), spm, 
				new BeanListProcessor(StockOutInMVO.class));
		return outinList;
	}
}
