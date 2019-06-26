package com.dzf.service.channel.dealmanage.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.service.channel.dealmanage.ICarryOverService;
import com.dzf.service.channel.dealmanage.IStockSumService;

@Service("sumStock")
public class StockSumServiceImpl implements IStockSumService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private ICarryOverService carryover;
	
	@Override
	@SuppressWarnings("unchecked")
	public List<StockOutInMVO> query(StockOutInMVO qvo) throws DZFWarpException {
		List<StockOutInMVO> begList = carryover.queryBalanceMonth(qvo.getBegdate().toString(),qvo.getPk_goods());//开始日期的期初余额
		List<StockOutInMVO> endList = carryover.queryBalanceMonth(qvo.getEnddate().getDateAfter(1).toString(),qvo.getPk_goods());//结束日期的下一天的期初余额
		HashMap<String, StockOutInMVO> map = getStockIn(qvo);
		
		StockOutInMVO getVO = new StockOutInMVO();
		int i=0;
		String key;
		DZFDouble totalout;
		for (StockOutInMVO stockOutInMVO : begList) {
			stockOutInMVO.setNnumstart(stockOutInMVO.getBalanceNum());//0、期初余额
			stockOutInMVO.setTotalmoneys(stockOutInMVO.getTotalmoneyb());
			if(stockOutInMVO.getBalanceNum()!=0){
				stockOutInMVO.setNpricestart(stockOutInMVO.getTotalmoneyb().div(stockOutInMVO.getBalanceNum()));
			}
			
			getVO = endList.get(i);
			stockOutInMVO.setNnumend(getVO.getBalanceNum());//1、期末余额
			if(stockOutInMVO.getNnumend()==0){
				DZFDouble money = new DZFDouble(0);
				stockOutInMVO.setTotalmoneye(money);
			}else{
				stockOutInMVO.setTotalmoneye(getVO.getTotalmoneyb());
			}
			if(getVO.getBalanceNum()!=0){
				stockOutInMVO.setNpriceend(getVO.getTotalmoneyb().div(getVO.getBalanceNum()));
			}
			
			key = stockOutInMVO.getPk_goods()+stockOutInMVO.getPk_goodsspec();
			if(!map.containsKey(key)){//2、本期入库
				stockOutInMVO.setNnumin(0);
				stockOutInMVO.setTotalmoneyin(DZFDouble.ZERO_DBL);
			}else{
				getVO = map.get(key);
				stockOutInMVO.setNnumin(getVO.getNnumin());
				stockOutInMVO.setTotalmoneyin(getVO.getTotalmoneyin());
			}
			
			stockOutInMVO.setNnumout(stockOutInMVO.getNnumstart()+stockOutInMVO.getNnumin()-stockOutInMVO.getNnumend());//3、本期卖出
			
			totalout = stockOutInMVO.getTotalmoneys().add(stockOutInMVO.getTotalmoneyin()).sub(stockOutInMVO.getTotalmoneye());
			stockOutInMVO.setTotalmoneyout(totalout);
			
			i++;
		}
		return begList;
	}
	
	
	private HashMap<String, StockOutInMVO> getStockIn(StockOutInMVO qvo) throws DZFWarpException {
		HashMap<String, StockOutInMVO> map = new HashMap<>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(qvo.getBegdate());
		spm.addParam(qvo.getEnddate());
		sql.append("select sib.pk_goods, ");
		sql.append("       sib.pk_goodsspec, ");
		sql.append("       sum(nvl(sib.nnum, 0)) nnumin, ");
		sql.append("       sum(nvl(sib.ntotalcost, 0)) totalmoneyin ");
		sql.append("  from cn_stockin_b sib ");
		sql.append("  left join cn_stockin si on si.pk_stockin = sib.pk_stockin ");
		sql.append(" where nvl(si.dr, 0) = 0 ");
		sql.append("   and nvl(sib.dr, 0) = 0 ");
		sql.append("   and si.vstatus = 2 ");
		sql.append("   AND substr(si.dconfirmtime, 0, 10) >= ? ");
		sql.append("   AND substr(si.dconfirmtime, 0, 10) <= ? ");
		sql.append(" group by sib.pk_goods, sib.pk_goodsspec ");
		List<StockOutInMVO> list = (List<StockOutInMVO>)singleObjectBO.executeQuery(sql.toString(),spm,new BeanListProcessor(StockOutInMVO.class));
		String key;
		for (StockOutInMVO stockOutInMVO : list) {
			key = stockOutInMVO.getPk_goods()+stockOutInMVO.getPk_goodsspec();
			map.put(key, stockOutInMVO);
		}
		return map;
	}

}
