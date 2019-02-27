package com.dzf.service.channel.dealmanage.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.jdbc.framework.processor.ColumnProcessor;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.channel.stock.CarryOverVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.WiseRunException;
import com.dzf.pub.lang.DZFBoolean;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDateTime;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.lock.LockUtil;
import com.dzf.pub.util.DateUtils;
import com.dzf.service.channel.dealmanage.ICarryOverService;

@Service("carryOver")
public class CarryOverServiceImpl implements ICarryOverService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Override
	public List<CarryOverVO> query(QryParamVO qvo){
		if(qvo.getBeginperiod().compareTo("2018-12")<0){
			qvo.setBeginperiod("2018-12");
		}
		List<CarryOverVO> retlist = new ArrayList<>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT c.pk_carryover,c.iscarryover,c.period,c.updatets \n") ;
		sql.append("  FROM cn_carryover c  \n") ; 
		sql.append(" WHERE nvl(c.dr, 0) = 0  \n") ; 
		sql.append(" 	AND  c.period >= ? AND c.period <=? \n") ; 
		sql.append(" ORDER BY c.period  \n");
		spm.addParam(qvo.getBeginperiod());
		spm.addParam(qvo.getEndperiod());
		List<CarryOverVO> list = (List<CarryOverVO>) singleObjectBO.executeQuery(sql.toString(),
				spm, new BeanListProcessor(CarryOverVO.class));
		HashMap<String, CarryOverVO> map = new HashMap<>();
		for (CarryOverVO carryOverVO : list) {
			map.put(carryOverVO.getPeriod(), carryOverVO);
		}
		
		DZFDate start = new DZFDate(qvo.getBeginperiod()+"-01");
		DZFDate end = new DZFDate(qvo.getEndperiod()+"-01");
		
		String period;
		long millis;
		CarryOverVO setvo = new CarryOverVO();
		while(start.compareTo(end)<=0){
			period = start.toString().substring(0, 7);
			if(!map.containsKey(period)){
				setvo = new CarryOverVO();
				setvo.setPeriod(period);;
				setvo.setIscarryover(DZFBoolean.FALSE);
				retlist.add(setvo);
			}else{
				retlist.add(map.get(period));
			}
			millis= DateUtils.getNextMonth(start.getMillis());
			start = new DZFDate(millis);
		}
		return retlist;
	}
	
	@Override
	public void save(CarryOverVO vo){
		String uuid = UUID.randomUUID().toString();
		try {
			LockUtil.getInstance().tryLockKey(vo.getTableName(), vo.getPeriod(), uuid, 60);
			boolean isNew = checkData(vo);
			checkIsSave(vo);
			if(isNew){//新增
				vo.setDoperatedate(new DZFDate());
				vo.setPk_corp("000001");
				singleObjectBO.insertVO(vo.getPk_corp(), vo);
			}else{//修改
				singleObjectBO.update(vo,new String[]{"iscarryover"});
			}
			if(vo.getIscarryover().booleanValue()){//新增价格，存储在（订单子表+其他出库单子表）
				addPrice(vo);
			}else{//清除价格
				deletePrice(vo);
			}
		} catch (Exception e) {
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		} finally {
			LockUtil.getInstance().unLock_Key(vo.getTableName(), vo.getPeriod(), uuid);
		}		
	}
	
	private void addPrice(CarryOverVO vo) throws DZFWarpException{
		//1、获取期初余额
		HashMap<String, StockOutInMVO> balMap = new HashMap<>();
		List<StockOutInMVO> balList = queryBalanceByTime(vo.getPeriod());
		String key;
		for (StockOutInMVO stockOutInMVO : balList) {
			key = stockOutInMVO.getPk_goods()+stockOutInMVO.getPk_goodsspec();
			if(stockOutInMVO.getBalanceNum()!=0){
				stockOutInMVO.setBalancePrice(stockOutInMVO.getTotalmoneyb().div(stockOutInMVO.getBalanceNum()));
			}
			balMap.put(key, stockOutInMVO);
		}
		
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(vo.getPeriod());
		spm.addParam(vo.getPeriod());
		spm.addParam(vo.getPeriod());
		sql.append("select sib.pk_goods, ");//入库单
		sql.append("       sib.pk_goodsspec, ");
		sql.append("       si.dconfirmtime, ");
		sql.append("       1 vitype, ");
		sql.append("       sib.pk_stockin_b pk_stockoutin, ");
		sql.append("       nvl(sib.nnum, 0) nnumin, ");
		sql.append("       sib.nprice npricein, ");
		sql.append("       nvl(sib.ntotalcost, 0) totalmoneyin, ");
		sql.append("       0 as nnumout, ");
		sql.append("       0 as npriceout, ");
		sql.append("       0 as totalmoneyout, ");
		sql.append("       0 as balanceNum, ");
		sql.append("       0 as balancePrice, ");
		sql.append("       0 as totalmoneyb ");
		sql.append("  from cn_stockin_b sib ");
		sql.append("  left join cn_stockin si on si.pk_stockin = sib.pk_stockin ");
		sql.append(" where nvl(si.dr, 0) = 0 ");
		sql.append("   and nvl(sib.dr, 0) = 0 ");
		sql.append("   and substr(si.dconfirmtime, 0,7) = ? ");
		sql.append("   and si.vstatus = 2 ");
		sql.append("union all ");
		sql.append("select gb.pk_goods, ");//订单
		sql.append("       gb.pk_goodsspec, ");
		sql.append("       gs.doperatetime dconfirmtime, ");
		sql.append("       2 vitype, ");
		sql.append("       gb.pk_goodsbill_b pk_stockoutin, ");
		sql.append("       0 as nnumin, ");
		sql.append("       0 as npricein, ");
		sql.append("       0 as totalmoneyin, ");
		sql.append("       nvl(gb.amount, 0) nnumout, ");
		sql.append("       nvl(gb.ncost, 0) as npriceout, ");
		sql.append("       nvl(gb.ntotalcost, 0) as totalmoneyout, ");
		sql.append("       0 as balanceNum, ");
		sql.append("       0 as balancePrice, ");
		sql.append("       0 as totalmoneyb ");
		sql.append("  from cn_goodsbill cg ");
		sql.append("  left join cn_goodsbill_b gb on cg.pk_goodsbill = gb.pk_goodsbill ");
		sql.append("  left join cn_goodsbill_s gs on cg.pk_goodsbill = gs.pk_goodsbill and gs.vstatus = 1 ");
		sql.append(" where nvl(cg.dr, 0) = 0 ");
		sql.append("   and nvl(gb.dr, 0) = 0 ");
		sql.append("   and nvl(gs.dr, 0) = 0 ");
		sql.append("   and substr(gs.doperatetime, 0,7) = ? ");
		sql.append("   and cg.vstatus != 0 ");
		sql.append("   and cg.vstatus != 4 ");
		sql.append("union all ");
		sql.append("select sob.pk_goods, ");//其他出库单
		sql.append("       sob.pk_goodsspec, ");
		sql.append("       so.dconfirmtime, ");
		sql.append("       3 vitype, ");
		sql.append("       so.vbillcode, ");
		sql.append("       0 as nnumin, ");
		sql.append("       0 as npricein, ");
		sql.append("       0 as totalmoneyin, ");
		sql.append("       nvl(sob.nnum, 0) nnumout, ");
		sql.append("       0 as npriceout, ");
		sql.append("       0 as totalmoneyout, ");
		sql.append("       0 as balanceNum, ");
		sql.append("       0 as balancePrice, ");
		sql.append("       0 as totalmoneyb ");
		sql.append("  from cn_stockout_b sob ");
		sql.append("  left join cn_stockout so on so.pk_stockout = sob.pk_stockout ");
		sql.append(" where nvl(sob.dr, 0) = 0 ");
		sql.append("   and nvl(so.dr, 0) = 0 ");
		sql.append("   and substr(so.dconfirmtime, 0,7) = ? ");
		sql.append("   and (so.vstatus = 1 or so.vstatus = 2) ");
		sql.append("   and so.itype = 1 ");
		sql.append("order by pk_goods, pk_goodsspec, dconfirmtime ");
		List<StockOutInMVO> listall = (List<StockOutInMVO>)singleObjectBO.executeQuery(sql.toString(),
				spm,new BeanListProcessor(StockOutInMVO.class));
		
		String oldId = null;
		String newId = null;
		StockOutInMVO qcVO = new StockOutInMVO();
		for (StockOutInMVO stockOutInMVO : listall) {
			newId = stockOutInMVO.getPk_goods() + stockOutInMVO.getPk_goodsspec();
			if(!newId.equals(oldId)){//新的商品
				if(!balMap.containsKey(newId)){
					qcVO = new StockOutInMVO();
					qcVO.setBalanceNum(0);
					qcVO.setBalancePrice(DZFDouble.ZERO_DBL);
					qcVO.setTotalmoneyb(DZFDouble.ZERO_DBL);
				}else{
					qcVO = balMap.get(newId);
				}
				qcVO=updateAndCalBalance(stockOutInMVO,qcVO);
				oldId=newId;
			}else{
				qcVO=updateAndCalBalance(stockOutInMVO,qcVO);
			}
		}
	}

	private StockOutInMVO updateAndCalBalance(StockOutInMVO nowVO, StockOutInMVO qcVO) throws DZFWarpException{
		if(nowVO.getVitype() == 1){// 商品入库
			qcVO.setBalanceNum(qcVO.getBalanceNum() + nowVO.getNnumin());// 结存数量
			qcVO.setTotalmoneyb(qcVO.getTotalmoneyb().add(nowVO.getTotalmoneyin()));// 结存金额
		}else{// 订单和其他出库
			nowVO.setNpriceout(qcVO.getBalancePrice());// 出库单价（上一次结存单价）
			nowVO.setTotalmoneyout(qcVO.getBalancePrice().multiply(nowVO.getNnumout()));// 出库金额（单价x数量）
			
			nowVO.setBalanceNum(qcVO.getBalanceNum() - nowVO.getNnumout());// 结存数量
			nowVO.setTotalmoneyb(qcVO.getTotalmoneyb().sub(nowVO.getTotalmoneyout()));// 结存金额
		}
		if (qcVO.getBalanceNum()>0) {
			qcVO.setBalancePrice(qcVO.getTotalmoneyb().div(qcVO.getBalanceNum()));
		}
		
		if(qcVO.getBalancePrice().compareTo(DZFDouble.ZERO_DBL)>0){
			SQLParameter spm = new SQLParameter();
			spm.addParam(nowVO.getNpriceout());
			spm.addParam(nowVO.getTotalmoneyout());
			spm.addParam(nowVO.getPk_stockoutin());
			StringBuffer sql = new StringBuffer();
			if(nowVO.getVitype() == 2){
				sql.append("update cn_stockout_b set ncost = ?, ntotalcost = ?");
				sql.append("  where nvl(dr, 0) = 0 and pk_stockout_b =?");
				singleObjectBO.executeUpdate(sql.toString(), spm);
			}else if(nowVO.getVitype() == 3){						
				sql.append("update cn_goodsbill_b set ncost = ?, ntotalcost = ?");
				sql.append("  where nvl(dr, 0) = 0 and pk_goodsbill_b =?");
				singleObjectBO.executeUpdate(sql.toString(), spm);
			}
		}
		return qcVO;
	}

	private void deletePrice(CarryOverVO vo) throws DZFWarpException{
		SQLParameter spm = new SQLParameter();
		spm.addParam(vo.getPeriod());
		StringBuffer sql = new StringBuffer();
		//1、清除其它出库单上的数据
		sql.append("update (select b.* ");
		sql.append("          from cn_stockout_b b ");
		sql.append("          left join cn_stockout o on b.pk_stockout = o.pk_stockout ");
		sql.append("         where nvl(b.dr, 0) = 0 ");
		sql.append("           and nvl(o.itype, 0) = 1 ");
//		sql.append("           and o.vstatus=1 ");//待确认
		sql.append("           and substr(o.dconfirmtime, 0, 7) =? ) ");
		sql.append("   set ncost = null, ntotalcost = null ");
		singleObjectBO.executeUpdate(sql.toString(), spm);
		//2、清除订单上的数据
		sql = new StringBuffer();
		sql.append("update cn_goodsbill_b b ");
		sql.append("   set b.ncost = null, b.ntotalcost = null ");
		sql.append(" where b.pk_goodsbill in ");
		sql.append("       (select gb.pk_goodsbill ");
		sql.append("          from cn_goodsbill_b gb ");
		sql.append("          left join cn_goodsbill_s gs on gb.pk_goodsbill = gs.pk_goodsbill ");
		sql.append("         where gs.vstatus = 1 ");
		sql.append("           and substr(gs.doperatetime, 0, 7) =? ");
		sql.append("           and nvl(gb.dr, 0) = 0 ");
		sql.append("           and nvl(gs.dr, 0) = 0) ");
		singleObjectBO.executeUpdate(sql.toString(), spm);
	}

	private void checkIsSave(CarryOverVO vo)  throws DZFWarpException{
		DZFDate date = new DZFDate(vo.getPeriod() + "-01");
		String sql = " period=? and nvl(dr,0)=0 and nvl(iscarryover,'N')='Y' ";
		if (vo.getIscarryover().booleanValue() && vo.getPeriod().compareTo("2018-12") != 0) {// 结转（看前一个月是否结转）
			long millis = DateUtils.getPreviousMonth(date.getMillis());
			date = new DZFDate(millis);
			SQLParameter spm = new SQLParameter();
			spm.addParam(date.toString().substring(0, 7));
			CarryOverVO[] vos = (CarryOverVO[]) singleObjectBO.queryByCondition(CarryOverVO.class, sql, spm);
			if(vos == null || vos.length<1){
				throw new BusinessException("上一个月的成本未结转");
			}
		} else if (!vo.getIscarryover().booleanValue()) {
			long millis = DateUtils.getNextMonth(date.getMillis());// 反结转（看下个月是否结转）提醒报错
			date = new DZFDate(millis);
			SQLParameter spm = new SQLParameter();
			spm.addParam(date.toString().substring(0, 7));
			CarryOverVO[] vos = (CarryOverVO[]) singleObjectBO.queryByCondition(CarryOverVO.class, sql, spm);
			if (vos != null && vos.length > 0) {
				throw new BusinessException("下一个月的成本未反结转");
			}
		} else {
			return;
		}
	}

	/**
	 * 是否是最新数据
	 * @param qryvo
	 */
	private boolean checkData(CarryOverVO qvo) throws DZFWarpException{
		boolean isNew;
		SQLParameter spm = new SQLParameter();
		spm.addParam(qvo.getPeriod());
		CarryOverVO[] vos =(CarryOverVO[]) singleObjectBO.queryByCondition(CarryOverVO.class," period=? and nvl(dr,0)=0 ", spm);
		if(vos == null || vos.length<1){
			isNew = true;
		}else{
			if(!vos[0].getUpdatets().equals(qvo.getUpdatets())){
				throw new BusinessException("期间为："+qvo.getPeriod()+",数据已发生变化;<br>");
			}
			if(StringUtils.isEmpty(qvo.getPk_carryover()) || qvo.getIscarryover() ==null){
				throw new BusinessException("非法操作");
			}
			isNew = false;
		}
		return isNew;
	}
	
	@Override
	public void checkIsCancel(DZFDateTime confirmTime) throws DZFWarpException{
		if(confirmTime==null){
			throw new BusinessException("非法操作!");
		}
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select count(1) as count from cn_carryover");
		sql.append(" where nvl(dr,0) = 0 and nvl(iscarryover,'N')='Y' ");
		sql.append(" and period = ?");
		sp.addParam(confirmTime.toString().substring(0, 7));
		String res = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("count")).toString();
		int num = Integer.valueOf(res);
		if(num > 0){
			throw new BusinessException("该月已成本结转，不可取消确认");
		}
	}
	
	@Override
	public List<StockOutInMVO> queryBalanceByTime(String byTime) throws DZFWarpException {
		int subLen = byTime.length();
		StringBuffer sql = new StringBuffer();
		sql.append("select cg.vgoodscode, ");
		sql.append("       cg.vgoodsname, ");
		sql.append("       cg.pk_goods, ");
		sql.append("       gs.invspec, ");
		sql.append("       gs.invtype, ");
		sql.append("       gs.pk_goodsspec, ");
		sql.append("       ((nvl(ru.nnum, 0) - nvl(ding.nnum, 0) - nvl(other.nnum, 0))) balanceNum, ");
		sql.append("       0 as balancePrice, ");
		sql.append("       (nvl(ru.ntotalcost, 0) - nvl(ding.ntotalcost, 0) - nvl(other.ntotalcost, 0)) totalmoneyb ");
		sql.append("  from cn_goods cg ");
		sql.append("  left join cn_goodsspec gs on gs.pk_goods = cg.pk_goods ");
		
		sql.append("  left join (select sum(nvl(sib.nnum, 0)) nnum, ");//汇总此时间之前，入库确认数量，入库确认总金额
		sql.append("                    sum(nvl(sib.ntotalcost, 0)) ntotalcost, ");
		sql.append("                    sib.pk_goods, ");
		sql.append("                    sib.pk_goodsspec ");
		sql.append("               from cn_stockin_b sib ");
		sql.append("               left join cn_stockin si on si.pk_stockin = sib.pk_stockin ");
		sql.append("              where nvl(si.dr, 0) = 0 ");
		sql.append("                and nvl(sib.dr, 0) = 0 ");
		sql.append("                and si.vstatus = 2 ");
		sql.append("                AND substr(si.dconfirmtime, 0, "+subLen+") < ? ");
		sql.append("              group by sib.pk_goods, sib.pk_goodsspec) ru on  gs.pk_goodsspec = ru.pk_goodsspec ");
		
		sql.append("  left join (select sum(nvl(gb.amount, 0)) nnum, ");//汇总此时间之前，订单确认数量，订单确认总金额
		sql.append("                    sum(nvl(gb.ntotalcost, 0)) ntotalcost, ");
		sql.append("                    gb.pk_goods, ");
		sql.append("                    gb.pk_goodsspec ");
		sql.append("               from cn_goodsbill cg ");
		sql.append("               left join cn_goodsbill_b gb on cg.pk_goodsbill = gb.pk_goodsbill ");
		sql.append("               left join cn_goodsbill_s gs on cg.pk_goodsbill = gs.pk_goodsbill and gs.vstatus = 1 ");
		sql.append("              where nvl(cg.dr, 0) = 0 ");
		sql.append("                and nvl(gb.dr, 0) = 0 ");
		sql.append("                and nvl(gs.dr, 0) = 0 ");
		sql.append("                and cg.vstatus != 0 ");
		sql.append("                and cg.vstatus != 4 ");
		sql.append("                AND substr(gs.doperatetime, 0,"+subLen+") < ? ");
		sql.append("              group by gb.pk_goods, gb.pk_goodsspec) ding on  gs.pk_goodsspec = ding.pk_goodsspec ");
		
		sql.append("  left join (select sum(nvl(sob.nnum, 0)) nnum, ");//汇总此时间之前，其他出库单确认数量，其他出库单确认总金额
		sql.append("                    sum(nvl(sob.ntotalcost, 0)) ntotalcost, ");
		sql.append("                    sob.pk_goods, ");
		sql.append("                    sob.pk_goodsspec ");
		sql.append("               from cn_stockout_b sob ");
		sql.append("               left join cn_stockout so on so.pk_stockout = sob.pk_stockout ");
		sql.append("              where nvl(sob.dr, 0) = 0 ");
		sql.append("                and nvl(so.dr, 0) = 0 ");
		sql.append("                and (so.vstatus = 1 or so.vstatus = 2) ");
		sql.append("                and so.itype = 1 ");
		sql.append("                AND substr(so.dconfirmtime, 0, "+subLen+") < ? ");
		sql.append("              group by sob.pk_goods, sob.pk_goodsspec) other on gs.pk_goodsspec =other.pk_goodsspec ");
		SQLParameter spm = new SQLParameter();
		spm.addParam(byTime);
		spm.addParam(byTime);
		spm.addParam(byTime);
		List<StockOutInMVO> list = (List<StockOutInMVO>)singleObjectBO.executeQuery(sql.toString(),
				spm,new BeanListProcessor(StockOutInMVO.class));
		
		return list;
	}

}
