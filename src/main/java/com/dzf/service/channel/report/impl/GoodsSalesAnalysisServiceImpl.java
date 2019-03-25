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
import com.dzf.model.channel.report.GoodsSalesAnalysisVO;
import com.dzf.model.channel.report.GoodsSalesCountVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.IGoodsSalesAnalysisService;

@Service("goodsSalesAnalysisSer")
public class GoodsSalesAnalysisServiceImpl implements IGoodsSalesAnalysisService {

	@Autowired
	private SingleObjectBO singleObjectBO;

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
				
				retvo.setNcost(detvo.getNcost());
				retvo.setNtotalcost(detvo.getNcost().multiply(detvo.getAmount()));
				
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

			// 2、获取返回数据
			return getRetList(map);

		}
		return null;
	}
	
	/**
	 * 获取返回数据
	 * @param map
	 * @param costmap
	 * @return
	 * @throws DZFWarpException
	 */
	private List<GoodsSalesAnalysisVO> getRetList(Map<String, GoodsSalesAnalysisVO> map)
			throws DZFWarpException {
		List<GoodsSalesAnalysisVO> retlist = new ArrayList<GoodsSalesAnalysisVO>();
		if (map != null && !map.isEmpty()) {
			GoodsSalesAnalysisVO rvo = null;
			CorpVO corpvo = null;
			DZFDouble ncost = DZFDouble.ZERO_DBL;
			DZFDouble ntotalcost = DZFDouble.ZERO_DBL;
			Map<String, List<GoodsSalesAnalysisVO>> cpmap = new HashMap<String, List<GoodsSalesAnalysisVO>>();
			List<GoodsSalesAnalysisVO> newlist = null;
			List<GoodsSalesAnalysisVO> oldlist = null;
			String pk_corp = "";
			for (String key : map.keySet()) {
				rvo = map.get(key);
				// 1、计算成本和成本合计
				ntotalcost = SafeCompute.multiply(rvo.getNcost(), CommonUtil.getDZFDouble(rvo.getAmount()));
				rvo.setNtotalcost(ntotalcost);
				
				corpvo = CorpCache.getInstance().get(null, rvo.getPk_corp());
				if (corpvo != null) {
					rvo.setCorpname(corpvo.getUnitname());
				}
				//2、同一公司的商品放在一起，为实现同一公司同样的商品放在一起
				pk_corp = key.substring(0, key.indexOf('&'));
				if(!cpmap.containsKey(pk_corp)){
					newlist = new ArrayList<GoodsSalesAnalysisVO>();
					newlist.add(rvo);
					cpmap.put(pk_corp, newlist);
				}else{
					oldlist = cpmap.get(pk_corp);
					oldlist.add(rvo);
					cpmap.put(pk_corp, oldlist);
				}
			}
			
			if(cpmap != null && !cpmap.isEmpty()){
				List<GoodsSalesAnalysisVO> cplist = null;
				//同一公司的商品放在一起，并计算合计行
				for(String key : cpmap.keySet()){
					cplist = cpmap.get(key);
					if(cplist != null && cplist.size() > 0){
						Collections.sort(cplist, new Comparator<GoodsSalesAnalysisVO>() {

							@Override
							public int compare(GoodsSalesAnalysisVO o1, GoodsSalesAnalysisVO o2) {
								return o1.getPk_goods().compareTo(o2.getPk_goods());
							}
						});
						
						// 2、计算合计行
						List<String> pklist = new ArrayList<String>();
						GoodsSalesAnalysisVO sumvo = null;
						int num = 0;
						for (GoodsSalesAnalysisVO revo : cplist) {
							if (!pklist.contains(revo.getPk_corp())) {
								if (sumvo != null) {
									retlist.add(sumvo);
								}
								sumvo = new GoodsSalesAnalysisVO();
								sumvo.setPk_corp(revo.getPk_corp());
								sumvo.setCorpname(revo.getCorpname());
								sumvo.setVgoodsname("小计");
								sumvo.setNtotalcost(revo.getNtotalcost());
								sumvo.setNdeductmny(revo.getNdeductmny());
								sumvo.setNdedrebamny(revo.getNdedrebamny());
								sumvo.setNdedsummny(revo.getNdedsummny());
								retlist.add(revo);
								pklist.add(revo.getPk_corp());
							} else {
								sumvo.setNtotalcost(SafeCompute.add(sumvo.getNtotalcost(), revo.getNtotalcost()));
								sumvo.setNdeductmny(SafeCompute.add(sumvo.getNdeductmny(), revo.getNdeductmny()));
								sumvo.setNdedrebamny(SafeCompute.add(sumvo.getNdedrebamny(), revo.getNdedrebamny()));
								sumvo.setNdedsummny(SafeCompute.add(sumvo.getNdedsummny(), revo.getNdedsummny()));
								retlist.add(revo);
							}
							num++;
							if (num == cplist.size()) {
								retlist.add(sumvo);
							}
						}
					}
				}
			}

		}
		return retlist;
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
		svo.setNdedsummny(goodsmny.setScale(2, DZFDouble.ROUND_HALF_UP));
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
		sql.append("       g.vgoodsname,  \n");
		sql.append("       nvl(co.ncost,0) AS ncost  \n");
		sql.append("  FROM cn_goodsbill_b b  \n");
		sql.append("  LEFT JOIN cn_goods g ON b.pk_goods = g.pk_goods  \n");
		sql.append("  LEFT JOIN cn_goodsbill l ON b.pk_goodsbill = l.pk_goodsbill  \n");
		sql.append("  LEFT JOIN cn_goodsbill_s s ON l.pk_goodsbill = s.pk_goodsbill  \n");
		sql.append("                            AND s.vstatus = 1  \n");
		sql.append("  LEFT JOIN cn_goodsspec c ON b.pk_goodsspec = c.pk_goodsspec  \n");
		sql.append("  LEFT JOIN cn_goodscost co on b.pk_goodsspec = co.pk_goodsspec \n");
		sql.append("  							AND substr(s.doperatetime, 0, 7)=co.period \n");
		sql.append(" WHERE nvl(b.dr, 0) = 0  \n");
		sql.append("   AND nvl(g.dr, 0) = 0  \n");
		sql.append("   AND nvl(l.dr, 0) = 0  \n");
		sql.append("   AND nvl(s.dr, 0) = 0  \n");
		sql.append("   AND nvl(c.dr, 0) = 0  \n");
		sql.append("   AND nvl(co.dr, 0) = 0  \n");
		sql.append("   AND l.vstatus IN (1, 2, 3)  \n");
		if (!StringUtil.isEmpty(pamvo.getPeriod())) {
			sql.append("   AND SUBSTR(s.doperatedate, 0, 7) = ?  \n");
			spm.addParam(pamvo.getPeriod());
		}
		if (!StringUtil.isEmpty(pamvo.getPk_corp())) {
			String[] strs = pamvo.getPk_corp().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" AND l.pk_corp in (").append(inSql).append(")");
		}
		sql.append("   ORDER BY l.pk_corp  \n");
		return (List<GoodsSalesCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(GoodsSalesCountVO.class));
	}
}
