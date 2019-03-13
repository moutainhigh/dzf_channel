package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.dealmanage.StockOutInMVO;
import com.dzf.model.channel.report.GoodsSalesAnalysisVO;
import com.dzf.model.channel.report.GoodsSalesCountVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.dealmanage.ICarryOverService;
import com.dzf.service.channel.report.IGoodsSalesAnalysisService;

@Service("goodsSalesAnalysisSer")
public class GoodsSalesAnalysisServiceImpl implements IGoodsSalesAnalysisService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private ICarryOverService carryover;

	private Logger log = Logger.getLogger(this.getClass());

	@Override
	public List<GoodsSalesAnalysisVO> query(QryParamVO pamvo) throws DZFWarpException {
		return getReturnData(pamvo);
	}

	/**
	 * 组装返回数据
	 * 
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private List<GoodsSalesAnalysisVO> getReturnData(QryParamVO pamvo) throws DZFWarpException {
		List<GoodsSalesAnalysisVO> retlist = new ArrayList<GoodsSalesAnalysisVO>();
		List<GoodsSalesCountVO> detlist = queryDetailData(pamvo);
		Map<String, GoodsSalesAnalysisVO> map = new HashMap<String, GoodsSalesAnalysisVO>();
		if (detlist != null && detlist.size() > 0) {
			// 1、获取商品订单明细
			GoodsSalesAnalysisVO retvo = null;
			GoodsSalesAnalysisVO oldvo = null;
			GoodsSalesCountVO countvo = null;
			StringBuffer ids = new StringBuffer();
			String pk_key = "";
			for (GoodsSalesCountVO detvo : detlist) {
				retvo = new GoodsSalesAnalysisVO();
				retvo.setPk_corp(detvo.getPk_corp());
				retvo.setPk_goods(detvo.getPk_goods());
				retvo.setPk_goodsspec(detvo.getPk_goodsspec());
				retvo.setInvspec(detvo.getInvspec());
				retvo.setInvtype(detvo.getInvtype());
				retvo.setAmount(detvo.getAmount());
				retvo.setNprice(detvo.getNprice());
				retvo.setVgoodsname(detvo.getVgoodsname());
				if (ids.indexOf(detvo.getPk_goods()) != -1) {
					ids.append(detvo.getPk_goods()).append(",");
				}
				countvo = countMny(detvo);
				if (countvo != null) {
					retvo.setNdeductmny(countvo.getNdeductmny());
					retvo.setNdedrebamny(countvo.getNdedrebamny());
					retvo.setNdedsummny(countvo.getNdedsummny());
				}
				pk_key = retvo.getPk_corp() + "&" + retvo.getPk_goodsspec();
				if (!map.containsKey(pk_key)) {
					map.put(pk_key, retvo);
				} else {
					oldvo = map.get(pk_key);
					oldvo.setNdeductmny(SafeCompute.add(oldvo.getNdeductmny(), retvo.getNdeductmny()));
					oldvo.setNdedrebamny(SafeCompute.add(oldvo.getNdedrebamny(), retvo.getNdedrebamny()));
					oldvo.setNdedsummny(SafeCompute.add(oldvo.getNdedsummny(), retvo.getNdedsummny()));
					oldvo.setAmount(ToolsUtil.addInteger(oldvo.getAmount(), retvo.getAmount()));
					map.put(pk_key, oldvo);
				}
			}

			// 2、计算商品不同规格型号的成本
			Map<String, DZFDouble> costmap = getCostMap(ids, pamvo);

			// 3、获取返回数据
			if (map != null && !map.isEmpty()) {
				List<GoodsSalesAnalysisVO> totalist = new ArrayList<GoodsSalesAnalysisVO>();
				GoodsSalesAnalysisVO rvo = null;
				CorpVO corpvo = null;
				for (String key : map.keySet()) {
					rvo = map.get(key);
					if (costmap != null && !costmap.isEmpty()) {
						rvo.setNcost(costmap.get(rvo.getPk_goodsspec()));
						rvo.setNtotalcost(
								SafeCompute.multiply(rvo.getNcost(), CommonUtil.getDZFDouble(rvo.getAmount())));
					}
					corpvo = CorpCache.getInstance().get(null, rvo.getPk_corp());
					if (corpvo != null) {
						rvo.setCorpname(corpvo.getUnitname());
					}
					totalist.add(rvo);
				}
				if (totalist != null && totalist.size() > 0) {
					Collections.sort(totalist, new Comparator<GoodsSalesAnalysisVO>() {

						@Override
						public int compare(GoodsSalesAnalysisVO o1, GoodsSalesAnalysisVO o2) {
							return o1.getPk_corp().compareTo(o2.getPk_corp());
						}

					});
					//4、计算合计行
					List<String> pklist = new ArrayList<String>();
					GoodsSalesAnalysisVO sumvo = null;
					int num = 0;
					for(GoodsSalesAnalysisVO revo : totalist){
						if(!pklist.contains(revo.getPk_corp())){
							if(sumvo != null){
								retlist.add(sumvo);
							}
							sumvo = new GoodsSalesAnalysisVO();
							sumvo.setPk_corp(revo.getPk_corp());
							sumvo.setCorpname(revo.getCorpname());
							sumvo.setVgoodsname("合计");
							sumvo.setNtotalcost(revo.getNtotalcost());
							sumvo.setNdeductmny(revo.getNdeductmny());
							sumvo.setNdedrebamny(revo.getNdedrebamny());
							sumvo.setNdedsummny(revo.getNdedsummny());
							retlist.add(revo);
							pklist.add(revo.getPk_corp());
						}else{
							sumvo.setNtotalcost(SafeCompute.add(sumvo.getNtotalcost(), revo.getNtotalcost()));
							sumvo.setNdeductmny(SafeCompute.add(sumvo.getNdeductmny(), revo.getNdeductmny()));
							sumvo.setNdedrebamny(SafeCompute.add(sumvo.getNdedrebamny(), revo.getNdedrebamny()));
							sumvo.setNdedsummny(SafeCompute.add(sumvo.getNdedsummny(), revo.getNdedsummny()));
							retlist.add(revo);
						}
						num ++;
						if(num == totalist.size()){
							retlist.add(sumvo);
						}
					}
				}
			}

		}
		return retlist;
	}

	/**
	 * 获取商品不同规格型号的成本
	 * 
	 * @param ids
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, DZFDouble> getCostMap(StringBuffer ids, QryParamVO pamvo) throws DZFWarpException {
		Map<String, DZFDouble> costmap = new HashMap<String, DZFDouble>();
		String gids = "";
		if (ids != null && ids.length() > 0) {
			gids = ids.toString().substring(0, ids.length() - 1);
		}
		String qryperiod = "";
		try {
			String qrydate = ToolsUtil.getDateAfterNum(new DZFDate(pamvo.getPeriod() + "-01"), 2);
			DZFDate date = new DZFDate(qrydate);
			qryperiod = date.getYear() + "-" + date.getStrMonth();
		} catch (Exception e) {
			log.error("查询日期转换异常：" + e.getMessage());
		}
		List<StockOutInMVO> calist = carryover.queryBalanceByTime(qryperiod, gids);
		if (calist != null && calist.size() > 0) {
			DZFDouble costmny = null;
			for (StockOutInMVO svo : calist) {
				if(!StringUtil.isEmpty(svo.getPk_goodsspec())){
					costmny = SafeCompute.div(svo.getTotalmoneyb(), CommonUtil.getDZFDouble(svo.getBalanceNum()));
					costmap.put(svo.getPk_goodsspec(), costmny);
				}
			}
		}
		return costmap;
	}

	/**
	 * 计算订单单个商品相关金额
	 * 
	 * @param detvo
	 * @return
	 * @throws DZFWarpException
	 */
	private GoodsSalesCountVO countMny(GoodsSalesCountVO detvo) throws DZFWarpException {
		GoodsSalesCountVO svo = new GoodsSalesCountVO();
		DZFDouble num = CommonUtil.getDZFDouble(detvo.getAmount());
		DZFDouble goodsmny = SafeCompute.multiply(detvo.getNbprice(), num);
		DZFDouble ducpropor = SafeCompute.div(detvo.getNdeductmny(), detvo.getNdedsummny());
		DZFDouble ndeductmny = SafeCompute.multiply(goodsmny, ducpropor);// 预付款
		DZFDouble rebpropor = SafeCompute.div(detvo.getNdedrebamny(), detvo.getNdedsummny());
		DZFDouble ndedrebamny = SafeCompute.multiply(goodsmny, rebpropor);// 返点款
		svo.setNdeductmny(ndeductmny.setScale(2, DZFDouble.ROUND_HALF_UP));
		svo.setNdedrebamny(ndedrebamny.setScale(2, DZFDouble.ROUND_HALF_UP));
		svo.setNdedsummny(goodsmny);
		return svo;
	}

	/**
	 * 查询商品订单明细数据
	 * 
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<GoodsSalesCountVO> queryDetailData(QryParamVO pamvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT l.pk_corp,  \n");
		sql.append("       l.ndedsummny,  \n");
		sql.append("       l.ndeductmny,  \n");
		sql.append("       l.ndedrebamny,  \n");
		sql.append("       b.pk_goods,  \n");
		sql.append("       b.pk_goodsspec,  \n");
		sql.append("       b.invspec,  \n");
		sql.append("       b.invtype,  \n");
		sql.append("       b.amount,  \n"); // 数量
		sql.append("       c.nprice,  \n");
		sql.append("       b.nprice AS nbprice,  \n");
		sql.append("       g.vgoodsname  \n");
		sql.append("  FROM cn_goodsbill_b b  \n");
		sql.append("  LEFT JOIN cn_goods g ON b.pk_goods = g.pk_goods  \n");
		sql.append("  LEFT JOIN cn_goodsbill l ON b.pk_goodsbill = l.pk_goodsbill  \n");
		sql.append("  LEFT JOIN cn_goodsbill_s s ON l.pk_goodsbill = s.pk_goodsbill  \n");
		sql.append("                            AND s.vstatus = 1  \n");
		sql.append("  LEFT JOIN cn_goodsspec c ON b.pk_goodsspec = c.pk_goodsspec  \n");
		sql.append(" WHERE nvl(b.dr, 0) = 0  \n");
		sql.append("   AND nvl(g.dr, 0) = 0  \n");
		sql.append("   AND nvl(l.dr, 0) = 0  \n");
		sql.append("   AND nvl(s.dr, 0) = 0  \n");
		sql.append("   AND nvl(c.dr, 0) = 0  \n");
		sql.append("   AND l.vstatus IN (1, 2, 3)  \n");
		if (!StringUtil.isEmpty(pamvo.getPeriod())) {
			sql.append("   AND SUBSTR(s.doperatedate, 0, 7) = ?  \n");
			spm.addParam(pamvo.getPeriod());
		}
		if(!StringUtil.isEmpty(pamvo.getPk_corp())){
		    String[] strs = pamvo.getPk_corp().split(",");
		    String inSql = SqlUtil.buildSqlConditionForIn(strs);
		    sql.append(" AND l.pk_corp in (").append(inSql).append(")");
		}
		sql.append("   ORDER BY l.pk_corp  \n");
		return (List<GoodsSalesCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(GoodsSalesCountVO.class));
	}
}
