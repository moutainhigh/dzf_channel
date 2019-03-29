package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.CustCountVO;
import com.dzf.model.channel.report.CustManageRepVO;
import com.dzf.model.channel.report.DataVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.model.sys.sys_set.BDTradeVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.ICustManageRep;

@Service("custmanagerepser")
public class CustManageRepImpl extends DataCommonRepImpl implements ICustManageRep {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public List<CustManageRepVO> query(QryParamVO pamvo) throws DZFWarpException, IllegalAccessException, Exception {

		Map<String, DataVO> map = queryCorps(pamvo, CustManageRepVO.class);
		List<String> corplist = null;
		if (map != null && !map.isEmpty()) {
			Collection<String> col = map.keySet();
			corplist = new ArrayList<String>(col);
		}

		if (corplist != null && corplist.size() > 0) {
			return getRetList(pamvo, corplist, map);
		}
		return null;
	}

	/**
	 * 获取返回数据
	 * 
	 * @param pamvo
	 * @param corplist
	 * @param map
	 * @return
	 * @throws DZFWarpException
	 */
	private List<CustManageRepVO> getRetList(QryParamVO pamvo, List<String> corplist, Map<String, DataVO> map)
			throws DZFWarpException {
		List<CustManageRepVO> retlist = new ArrayList<CustManageRepVO>();
		List<String> replist = new ArrayList<String>();
		List<CustCountVO> list = queryCustNum(pamvo, corplist);
		Map<String, CustManageRepVO> custmap = getCustMap(list, replist);
		CustManageRepVO custnumvo = null;
		CustManageRepVO retvo = null;//

		List<String> codelist = qryIndustryCode(pamvo);// 排行前五行业主键
		List<CustCountVO> custnumlist = qryIndustryNum(pamvo);
		Map<String, CustCountVO> industmap = qryIndustryMap(custnumlist, codelist);
		codelist.add("others");
		String[] industrys = new String[] { "小规模纳税人", "一般纳税人" };

		CorpVO corpvo = null;
		UserVO uservo = null;
		DataVO data = null;//
		for (String pk_corp : corplist) {
			data = map.get(pk_corp);
			retvo = (CustManageRepVO) data;
			if (retvo == null) {
				retvo = new CustManageRepVO();
			}
			retvo.setPk_corp(pk_corp);
			corpvo = CorpCache.getInstance().get(null, pk_corp);
			if (corpvo != null) {
				retvo.setCorpname(corpvo.getUnitname());
				retvo.setVprovname(corpvo.getCitycounty());
			}
			uservo = UserCache.getInstance().get(retvo.getUserid(), pk_corp);
			if (uservo != null) {
				retvo.setUsername(uservo.getUser_name());
			}
			uservo = UserCache.getInstance().get(retvo.getCuserid(), pk_corp);
			if (uservo != null) {
				retvo.setCusername(uservo.getUser_name());
			}
			custnumvo = custmap.get(pk_corp);
			if (custnumvo != null) {
				retvo.setIcustsmall(custnumvo.getIcustsmall());
				retvo.setIcusttaxpay(custnumvo.getIcusttaxpay());
			}
			// 获取各个行业的值
			setIndustryNum(codelist, pk_corp, industrys, industmap, retvo);

			retlist.add(retvo);
		}
		return retlist;
	}

	/**
	 * 获取各个行业的值
	 * 
	 * @param codelist
	 * @param pk_corp
	 * @param industrys
	 * @param industmap
	 * @param retvo
	 * @throws DZFWarpException
	 */
	private void setIndustryNum(List<String> codelist, String pk_corp, String[] industrys,
			Map<String, CustCountVO> industmap, CustManageRepVO retvo) throws DZFWarpException {
		String key = "";
		CustCountVO industryvo = null;
		Integer countnum = null;
		Integer custsum = 0;
		DZFDouble rate = DZFDouble.ZERO_DBL;
		if (codelist != null && codelist.size() > 0) {
			for (int i = 0; i < codelist.size(); i++) {
				for (int j = 0; j < industrys.length; j++) {
					key = pk_corp + "" + codelist.get(i) + "" + industrys[j];
					industryvo = industmap.get(key);
					custsum = 0;
					custsum = addInteger(retvo.getIcustsmall(), retvo.getIcusttaxpay());
					if (industryvo != null) {
						if (j == 0) {
							retvo.setAttributeValue("icustsmall" + (i + 1), industryvo.getNum());
							countnum = CommonUtil.getInteger(retvo.getAttributeValue("icustsmall" + (i + 1)));
							rate = getCustRate(countnum, custsum);
							retvo.setAttributeValue("icustratesmall" + (i + 1), rate);
						} else if (j == 1) {
							retvo.setAttributeValue("icusttaxpay" + (i + 1), industryvo.getNum());
							countnum = CommonUtil.getInteger(retvo.getAttributeValue("icusttaxpay" + (i + 1)));
							rate = getCustRate(countnum, custsum);
							retvo.setAttributeValue("icustratetaxpay" + (i + 1), rate);
						}
					}
				}
			}
		}
	}

	/**
	 * 占比计算
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 * @throws DZFWarpException
	 */
	private DZFDouble getCustRate(Integer num1, Integer num2) throws DZFWarpException {
		DZFDouble num3 = num1 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num1);
		DZFDouble num4 = num2 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num2);
		return num3.div(num4).multiply(100);
	}

	/**
	 * 整数相加
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 * @throws DZFWarpException
	 */
	private Integer addInteger(Integer num1, Integer num2) throws DZFWarpException {
		num1 = num1 == null ? 0 : num1;
		num2 = num2 == null ? 0 : num2;
		return num1 + num2;
	}

	/**
	 * 行业分类汇总计算
	 * 
	 * @param custnumlist
	 * @param pklist
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, CustCountVO> qryIndustryMap(List<CustCountVO> custnumlist, List<String> codelist)
			throws DZFWarpException {
		Map<String, CustCountVO> retmap = new HashMap<String, CustCountVO>();
		if (custnumlist != null && custnumlist.size() > 0) {
			String key = "";
			CustCountVO retvo = null;
			for (CustCountVO vo : custnumlist) {
				if (StringUtil.isEmpty(vo.getPk_corp()) || StringUtil.isEmpty(vo.getIndustrycode())
						|| StringUtil.isEmpty(vo.getChargedeptname())) {
					continue;
				}
				if (codelist.contains(vo.getIndustrycode())) {
					key = vo.getPk_corp() + "" + vo.getIndustrycode() + "" + vo.getChargedeptname();
				} else {
					key = vo.getPk_corp() + "others" + vo.getChargedeptname();
				}
				retvo = new CustCountVO();
				if (!retmap.containsKey(key)) {
					retvo.setKey(key);
					retvo.setNum(vo.getNum());
					retmap.put(key, retvo);
				} else {
					retvo = retmap.get(key);
					retvo.setNum(CommonUtil.addInteger(retvo.getNum(), vo.getNum()));
				}
			}
		}
		return retmap;
	}

	/**
	 * 行业按照会计公司主键、行业（大类编码）、纳税人资格进行分类汇总
	 * 
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<CustCountVO> qryIndustryNum(QryParamVO paramvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT fathercorp as pk_corp, \n");
		sql.append("       chargedeptname, \n");
		sql.append("       industrycode,\n");
		sql.append("       count(pk_corp) as num \n");
		sql.append("  FROM (SELECT p.fathercorp,\n");
		sql.append("               p.pk_corp,\n");
		sql.append("               p.chargedeptname,\n");
		sql.append("               (case \n");
		sql.append("                 when instr(trade.tradecode, 'Z') > 0 then \n");
		sql.append("                  trade.tradecode \n");
		sql.append("                 else \n");
		sql.append("                  substr(trade.tradecode, 0, 2) \n");
		sql.append("               end) industrycode \n");
		sql.append("          FROM bd_corp p \n");
		sql.append("          LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp \n");
		sql.append("          LEFT JOIN ynt_bd_trade trade ON p.industry = trade.pk_trade \n");
		sql.append("         WHERE nvl(p.dr, 0) = 0 \n");
		sql.append("           AND nvl(t.dr, 0) = 0 \n");

		sql.append("   AND nvl(t.ischannel, 'N') = 'Y'  \n");
		sql.append("   AND nvl(p.isaccountcorp, 'N') = 'N'  \n");// 非分支机构
		sql.append("   AND nvl(p.isseal, 'N') = 'N'\n"); // 未封存
		sql.append("   AND nvl(p.ishasaccount,'N') = 'Y' \n");// 已建账

		sql.append("           AND p.fathercorp NOT IN \n");
		sql.append("               (SELECT f.pk_corp \n");
		sql.append("                  FROM ynt_franchisee f \n");
		sql.append("                 WHERE nvl(dr, 0) = 0 \n");
		sql.append("                   AND nvl(f.isreport, 'N') = 'Y')\n");
		sql.append("           AND p.chargedeptname IS NOT NULL \n");
		sql.append("           AND p.Industry IS NOT NULL )\n");
		sql.append(" WHERE industrycode IS NOT NULL \n");
		sql.append("   AND chargedeptname IS NOT NULL \n");
		sql.append(" GROUP BY fathercorp, chargedeptname, industrycode \n");
		sql.append(" ORDER BY num DESC");
		return (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
	}

	/**
	 * 排行前五行业主键
	 * 
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	private List<String> qryIndustryCode(QryParamVO paramvo) throws DZFWarpException {
		List<String> retlist = new ArrayList<String>();
		List<CustCountVO> countlist = queryIndustry(paramvo);
		if (countlist != null && countlist.size() > 0) {
			for (CustCountVO vo : countlist) {
				if (!StringUtil.isEmpty(vo.getIndustrycode())) {
					retlist.add(vo.getIndustrycode());
					if (retlist != null && retlist.size() == 5) {
						break;
					}
				}
			}
		}
		return retlist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CustCountVO> queryIndustry(QryParamVO paramvo) throws DZFWarpException {
		List<CustCountVO> retlist = new ArrayList<CustCountVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT industrycode, count(pk_corp) as num \n");
		sql.append("  FROM (SELECT p.pk_corp,\n");
		sql.append("               (case \n");
		sql.append("                 when instr(trade.tradecode, 'Z') > 0  then \n");
		sql.append("                  trade.tradecode \n");
		sql.append("                 else \n");
		sql.append("                  substr(trade.tradecode, 0, 2)  \n");
		sql.append("               end) industrycode \n");
		sql.append("          FROM bd_corp p \n");
		sql.append("          LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp \n");
		sql.append("          LEFT JOIN ynt_bd_trade trade ON p.industry = trade.pk_trade \n");
		sql.append("         WHERE nvl(p.dr, 0) = 0 \n");
		sql.append("           AND nvl(t.dr, 0) = 0 \n");

		sql.append("   AND nvl(t.ischannel, 'N') = 'Y'  \n");
		sql.append("   AND nvl(p.isaccountcorp, 'N') = 'N'  \n");// 非分支机构
		sql.append("   AND nvl(p.isseal, 'N') = 'N'\n"); // 未封存
		sql.append("   AND nvl(p.ishasaccount,'N') = 'Y' \n");// 已建账

		sql.append("           AND p.fathercorp NOT IN \n");
		sql.append("               (SELECT f.pk_corp \n");
		sql.append("                  FROM ynt_franchisee f \n");
		sql.append("                 WHERE nvl(dr, 0) = 0 \n");
		sql.append("                   AND nvl(f.isreport, 'N') = 'Y')\n");
		sql.append("           AND p.chargedeptname IS NOT NULL \n");
		sql.append("           AND p.Industry IS NOT NULL )\n");
		sql.append(" WHERE industrycode IS NOT NULL \n");
		sql.append(" GROUP BY industrycode \n");
		sql.append(" ORDER BY num DESC \n");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if (list != null && list.size() > 0) {
			Map<String, String> trademap = queryTrade();
			for (CustCountVO vo : list) {
				if (!StringUtil.isEmpty(vo.getIndustrycode())) {
					vo.setIndustryname(trademap.get(vo.getIndustrycode()));
					retlist.add(vo);
					if (retlist != null && retlist.size() == 5 && list.size() > 5) {
						CustCountVO countvo = new CustCountVO();
						countvo.setIndustryname("其他类");
						retlist.add(countvo);
						break;
					}
				}
			}
		}
		return retlist;
	}

	/**
	 * 查询行业信息
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, String> queryTrade() throws DZFWarpException {
		Map<String, String> map = new HashMap<String, String>();
		String sql = " nvl(dr,0) = 0 ";
		BDTradeVO[] tradeVOs = (BDTradeVO[]) singleObjectBO.queryByCondition(BDTradeVO.class, sql.toString(), null);
		if (tradeVOs != null && tradeVOs.length > 0) {
			for (BDTradeVO vo : tradeVOs) {
				if (!StringUtil.isEmpty(vo.getTradecode())) {
					if (vo.getTradecode().indexOf("Z") != -1) {
						map.put(vo.getTradecode(), vo.getTradename());
					} else {
						if (vo.getTradecode().length() == 2) {
							map.put(vo.getTradecode(), vo.getTradename());
						}
					}
				}
			}
		}
		return map;
	}

	/**
	 * 查询各类纳税人资格的客户
	 * 
	 * @param paramvo
	 * @param corplist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<CustCountVO> queryCustNum(QryParamVO pamvo, List<String> corplist) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp AS pk_corp,  \n");
		sql.append("       nvl(p.chargedeptname, '小规模纳税人') AS chargedeptname,  \n");
		sql.append("       COUNT(p.pk_corp) AS num  \n");
		sql.append("  FROM bd_corp p  \n");
		sql.append("  LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp  \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.ischannel, 'N') = 'Y'  \n");
		sql.append("   AND nvl(p.isaccountcorp, 'N') = 'N'  \n");// 非分支机构
		sql.append("   AND nvl(p.isseal, 'N') = 'N'\n"); // 未封存
		sql.append("   AND nvl(p.ishasaccount,'N') = 'Y' \n");// 已建账
		if (!StringUtil.isEmpty(pamvo.getBeginperiod())) {
			sql.append("   AND substr(p.createdate,1,7) <= ? \n");
			spm.addParam(pamvo.getBeginperiod());
		}
		if (corplist != null && corplist.size() > 0) {
			String filter = SqlUtil.buildSqlForIn("t.pk_corp", corplist.toArray(new String[0]));
			sql.append(" AND ");
			sql.append(filter);
		}
		if (pamvo.getCorps() != null && pamvo.getCorps().length > 0) {
			String filter = SqlUtil.buildSqlForIn("t.pk_corp", pamvo.getCorps());
			sql.append(" AND ");
			sql.append(filter);
		}
		sql.append(" GROUP BY t.pk_corp,  \n");
		sql.append("          nvl(p.chargedeptname, '小规模纳税人') \n");
		return (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
	}

	/**
	 * 获取按照纳税人资格汇总的客户数量
	 * 
	 * @param list
	 * @param corplist
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, CustManageRepVO> getCustMap(List<CustCountVO> list, List<String> replist)
			throws DZFWarpException {
		Map<String, CustManageRepVO> custmap = new HashMap<String, CustManageRepVO>();
		if (list != null && list.size() > 0) {
			CustManageRepVO repvo = null;
			for (CustCountVO cvo : list) {
				if (!custmap.containsKey(cvo.getPk_corp())) {
					repvo = new CustManageRepVO();
					repvo.setPk_corp(cvo.getPk_corp());
					if ("小规模纳税人".equals(cvo.getChargedeptname())) {
						repvo.setIcustsmall(cvo.getNum());
					} else if ("一般纳税人".equals(cvo.getChargedeptname())) {
						repvo.setIcusttaxpay(cvo.getNum());
					}
					if (!replist.contains(cvo.getPk_corp())) {
						replist.add(cvo.getPk_corp());
					}
					custmap.put(cvo.getPk_corp(), repvo);
				} else {
					repvo = custmap.get(cvo.getPk_corp());
					if ("小规模纳税人".equals(cvo.getChargedeptname())) {
						repvo.setIcustsmall(ToolsUtil.addInteger(repvo.getIcustsmall(), cvo.getNum()));
					} else if ("一般纳税人".equals(cvo.getChargedeptname())) {
						repvo.setIcusttaxpay(ToolsUtil.addInteger(repvo.getIcusttaxpay(), cvo.getNum()));
					}
					custmap.put(cvo.getPk_corp(), repvo);
				}
			}
		}
		return custmap;
	}

}
