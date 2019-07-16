package com.dzf.service.channel.dealmanage.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.dealmanage.GoodsBoxVO;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.dealmanage.ICarryOverService;
import com.dzf.service.channel.dealmanage.IStockOutInService;


@Service("outinStock")
public class StockOutInServiceImpl implements IStockOutInService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private ICarryOverService carryover;
	

	@Override
	@SuppressWarnings("unchecked")
	public List<StockOutInMVO> query(StockOutInMVO qvo) throws DZFWarpException {
		List<StockOutInMVO>  retList = new ArrayList<StockOutInMVO>();
		//1、获取期初余额
		HashMap<String, StockOutInMVO> balMap = new HashMap<>();
		List<StockOutInMVO> balList = carryover.queryBalanceMonth(qvo.getBegdate().toString(),qvo.getPk_goods());
		String key;
		for (StockOutInMVO stockOutInMVO : balList) {
			key = stockOutInMVO.getPk_goods()+stockOutInMVO.getPk_goodsspec();
			if(stockOutInMVO.getBalanceNum()!=0){
				stockOutInMVO.setBalancePrice(stockOutInMVO.getTotalmoneyb().div(stockOutInMVO.getBalanceNum()));
			}
			stockOutInMVO.setVitype(0);
			stockOutInMVO.setDconfirmtime(new DZFDateTime("1999-01-01 00:00:00"));
			balMap.put(key, stockOutInMVO);
		}
		
		QrySqlSpmVO sqpvoall = getQrySqlSpmAll(qvo);
		List<StockOutInMVO> listall = (List<StockOutInMVO>)singleObjectBO.executeQuery(sqpvoall.getSql(),
				sqpvoall.getSpm(),new BeanListProcessor(StockOutInMVO.class));
		
		String oldId = null;
		String newId = null;
		
		StockOutInMVO lastVO = new StockOutInMVO();
		for (StockOutInMVO stockOutInMVO : listall) {
			newId = stockOutInMVO.getPk_goods() + stockOutInMVO.getPk_goodsspec();
			if(!newId.equals(oldId)){//新的商品
				if(!balMap.containsKey(newId)){
					lastVO = new StockOutInMVO();
					lastVO.setBalanceNum(0);
					lastVO.setBalancePrice(DZFDouble.ZERO_DBL);
					lastVO.setTotalmoneyb(DZFDouble.ZERO_DBL);
					lastVO.setPk_goods(stockOutInMVO.getPk_goods());
					lastVO.setPk_goodsspec(stockOutInMVO.getPk_goodsspec());
					lastVO.setVgoodscode(stockOutInMVO.getVgoodscode());
					lastVO.setVgoodsname(stockOutInMVO.getVgoodsname());
					lastVO.setInvspec(stockOutInMVO.getInvspec());
					lastVO.setInvtype(stockOutInMVO.getInvtype());
					lastVO.setVitype(0);
				}else{
					lastVO = balMap.get(newId);
				}
				retList.add(lastVO);
				stockOutInMVO=calBalance(stockOutInMVO,lastVO);
				retList.add(stockOutInMVO);
				lastVO=stockOutInMVO;
				oldId=newId;
			}else{
				stockOutInMVO=calBalance(stockOutInMVO,lastVO);
				lastVO=stockOutInMVO;
				retList.add(stockOutInMVO);
			}
		}
		return retList;
	}
	
	
	private StockOutInMVO calBalance(StockOutInMVO nowVO, StockOutInMVO lastVO) throws DZFWarpException {
		if(nowVO.getVitype() == 1){// 商品入库
			if(nowVO.getTotalmoneyin().compareTo(DZFDouble.ZERO_DBL)>0){//入库单价 (本次入库金额/本次入库数量)
				nowVO.setNpricein(nowVO.getTotalmoneyin().div(nowVO.getNnumin()));
			}
			nowVO.setBalanceNum(lastVO.getBalanceNum() + nowVO.getNnumin());// 结存数量
			nowVO.setTotalmoneyb(lastVO.getTotalmoneyb().add(nowVO.getTotalmoneyin()));// 结存金额
		}else{// 销售出库和其他出库
//			nowVO.setNpriceout(lastVO.getBalancePrice());// 出库单价（上一次结存单价）
//			nowVO.setTotalmoneyout(lastVO.getBalancePrice().multiply(nowVO.getNnumout()));// 出库金额（单价x数量）
			nowVO.setTotalmoneyout(nowVO.getNpriceout().multiply(nowVO.getNnumout()));// 出库金额（单价x数量）
			Integer num = lastVO.getBalanceNum() ;
			nowVO.setBalanceNum(num - nowVO.getNnumout());// 结存数量
			DZFDouble toatal =lastVO.getTotalmoneyb();
			if(nowVO.getBalanceNum()==0){
				DZFDouble money = new DZFDouble(0);
				nowVO.setTotalmoneyb(money);
			}else{
				nowVO.setTotalmoneyb(toatal.sub(nowVO.getTotalmoneyout()));// 结存金额
			}
			
		}
		if (nowVO.getTotalmoneyb().compareTo(DZFDouble.ZERO_DBL)>0) {
			nowVO.setBalancePrice(nowVO.getTotalmoneyb().div(nowVO.getBalanceNum()).setScale(4, DZFDouble.ROUND_HALF_UP));
		}
		return nowVO;
	}

	/**
	 * 查询全部——获取查询条件
	 * @param qvo
	 * @return
	 */
	private  QrySqlSpmVO getQrySqlSpmAll(StockOutInMVO qvo)  throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select cg.vgoodscode, ");
		sql.append("       cg.vgoodsname, ");
		sql.append("       cg.pk_goods, ");
		sql.append("       gs.invspec, ");
		sql.append("       gs.invtype, ");
		sql.append("       gs.pk_goodsspec, ");
		sql.append("       hz.* ");
		sql.append("  from cn_goods cg ");//0、商品数量
		sql.append("  left join cn_goodsspec gs on cg.pk_goods = gs.pk_goods ");
		sql.append("  left join (select sib.pk_goods, ");//1、入库单
		sql.append("                    sib.pk_goodsspec, ");
		sql.append("                    si.dconfirmtime, ");
		sql.append("                    1 vitype, ");
		sql.append("                    si.vbillcode, ");
		sql.append("                    nvl(sib.nnum, 0) nnumin, ");
		sql.append("                    sib.nprice npricein, ");
		sql.append("                    nvl(sib.ntotalcost,0) totalmoneyin, ");
		sql.append("                    0 as nnumout, ");
		sql.append("                    0 as npriceout, ");
		sql.append("                    0 as totalmoneyout, ");
		sql.append("                    0 as balanceNum, ");
		sql.append("                    0 as balancePrice, ");
		sql.append("                    0 as totalmoneyb ");
		sql.append("               from cn_stockin_b sib ");
		sql.append("               left join cn_stockin si on si.pk_stockin = sib.pk_stockin ");
		sql.append("              where nvl(si.dr, 0) = 0 ");
		sql.append("                and nvl(sib.dr, 0) = 0 ");
		sql.append("                and si.vstatus = 2 ");
		sql.append("             union all ");
		sql.append("             select gb.pk_goods, ");//订单
		sql.append("                    gb.pk_goodsspec, ");
		sql.append("                    gs.doperatetime dconfirmtime,");
		sql.append("                    2 vitype, ");
		sql.append("                    cg.vbillcode, ");
		sql.append("                    0 as nnumin, ");
		sql.append("                    0 as npricein, ");
		sql.append("                    0 as totalmoneyin, ");
		sql.append("                    nvl(gb.amount, 0) nnumout, ");
//		sql.append("                    nvl(gb.ncost, 0) as npriceout, ");
		sql.append("                    nvl(co.ncost,0) as npriceout, ");
//		sql.append("                    nvl(gb.ntotalcost, 0) as totalmoneyout, ");
		sql.append("                    0 as totalmoneyout, ");
		sql.append("                    0 as balanceNum, ");
		sql.append("                    0 as balancePrice, ");
		sql.append("                    0 as totalmoneyb ");
		sql.append("  			  from cn_goodsbill cg ");
		sql.append("  			  left join cn_goodsbill_b gb on cg.pk_goodsbill = gb.pk_goodsbill ");
		sql.append("  			  left join cn_goodsbill_s gs on cg.pk_goodsbill = gs.pk_goodsbill and gs.vstatus = 1 ");
		sql.append("  			  left join cn_goodscost   co on gb.pk_goodsspec = co.pk_goodsspec and substr(gs.doperatetime, 0, 7)=co.period");
		sql.append(" 			where nvl(cg.dr, 0) = 0 ");
		sql.append("  				and nvl(gb.dr, 0) = 0 ");
		sql.append("   				and nvl(gs.dr, 0) = 0 ");
		sql.append("   				and nvl(co.dr, 0) = 0 ");
		sql.append("   				and cg.vstatus != 0 ");
		sql.append("   				and cg.vstatus != 4 ");
		sql.append("             union all ");//3、其它出库单
		sql.append("             select sob.pk_goods, ");
		sql.append("                    sob.pk_goodsspec, ");
		sql.append("                    so.dconfirmtime, ");
		sql.append("                    3 vitype, ");
		sql.append("                    so.vbillcode, ");
		sql.append("                    0 as nnumin, ");
		sql.append("                    0 as npricein, ");
		sql.append("                    0 as totalmoneyin, ");
		sql.append("                    nvl(sob.nnum, 0) nnumout, ");
//		sql.append("                    nvl(sob.ncost, 0) as npriceout, ");
		sql.append("                    nvl(co.ncost,0) as npriceout, ");
//		sql.append("                    nvl(sob.ntotalcost, 0) as totalmoneyout,");
		sql.append("                    0 as totalmoneyout,");
		sql.append("                    0 as balanceNum, ");
		sql.append("                    0 as balancePrice, ");
		sql.append("                    0 as totalmoneyb ");
		sql.append("               from cn_stockout_b sob ");
		sql.append("               left join cn_stockout so on so.pk_stockout = sob.pk_stockout ");
		sql.append("               left join cn_goodscost co on sob.pk_goodsspec = co.pk_goodsspec and substr(so.dconfirmtime, 0, 7)=co.period");
		sql.append("              where nvl(sob.dr, 0) = 0 ");
		sql.append("                and nvl(so.dr, 0) = 0 ");
		sql.append("                and (so.vstatus = 1 or so.vstatus = 2) ");
		sql.append("                and so.itype = 1) hz on hz.pk_goods = gs.pk_goods ");
		sql.append("                                    and hz.pk_goodsspec = gs.pk_goodsspec ");
		sql.append(" where nvl(cg.dr, 0) = 0 and nvl(gs.dr, 0) = 0 ");
		sql.append(" 		and substr(hz.dconfirmtime, 0, 10) >= ? ");
		sql.append(" 		and substr(hz.dconfirmtime, 0, 10) <= ? ");
		spm.addParam(qvo.getBegdate());
		spm.addParam(qvo.getEnddate());
		if (!StringUtil.isEmpty(qvo.getPk_goods())) {
			String[] strs = qvo.getPk_goods().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" 	AND cg.pk_goods in (").append(inSql).append(")");
		}
		if(!StringUtil.isEmpty(qvo.getPk_goodsspec())){
			sql.append("   AND gs.pk_goodsspec = ? ");
			spm.addParam(qvo.getPk_goodsspec());
		}
		sql.append(" order by cg.vgoodscode, gs.pk_goodsspec, hz.dconfirmtime ");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<GoodsBoxVO> queryComboBox() throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT pk_goods AS id, vgoodsname AS name   ");
		sql.append("  FROM cn_goods    ");
		sql.append(" WHERE nvl(dr, 0) = 0    ");
		return (List<GoodsBoxVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(GoodsBoxVO.class));
	}

}
