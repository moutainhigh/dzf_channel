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
import com.dzf.model.channel.report.CustNumMoneyRepVO;
import com.dzf.model.channel.report.DataVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.IDefaultValue;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.ICustNumMoneyRep;

@Service("custnummoneyrepser")
public class CustNumMoneyRepImpl extends DataCommonRepImpl implements ICustNumMoneyRep {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public List<CustNumMoneyRepVO> query(QryParamVO paramvo) throws DZFWarpException, IllegalAccessException, Exception {
		List<CustNumMoneyRepVO> retlist = new ArrayList<CustNumMoneyRepVO>();
		HashMap<String, DataVO> map = queryCorps(paramvo,CustNumMoneyRepVO.class);
		List<String> corplist = null;
		if(map!=null && !map.isEmpty()){
			Collection<String> col = map.keySet();
			corplist = new ArrayList<String>(col);
		}
		// HashMap<String, T> map11 = new HashMap<String, T>();

		if (corplist != null && corplist.size() > 0) {
			// 1.1、查询客户数量、合同金额
			Map<String, CustCountVO> stockmap = queryStockNumMny(paramvo, corplist);
			// 1.2、查询新增客户数量、合同金额
			Map<String, CustCountVO> newmap = queryNumMnyByType(paramvo, corplist, 1);
			// 1.3、查询续费客户数量、合同金额
			Map<String, CustCountVO> renewmap = queryNumMnyByType(paramvo, corplist, 2);
			String preperiod = ToolsUtil.getPreviousMonth(paramvo.getPeriod());
			paramvo.setPeriod(preperiod);
			// 1.4、查询上一个月新增客户数量、合同金额
			Map<String, CustCountVO> lastnewmap = queryNumMnyByType(paramvo, corplist, 1);
			// 1.5、查询上一个月续费客户数量、合同金额
			Map<String, CustCountVO> lastrenewmap = queryNumMnyByType(paramvo, corplist, 2);

			CorpVO corpvo = null;
			UserVO uservo = null;
			CustCountVO custvo = null;
			CustNumMoneyRepVO retvo = null;

			for (String pk_corp : corplist) {
				retvo =(CustNumMoneyRepVO)map.get(pk_corp);
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
				// 2.1 存量客户数量、合同金额赋值：
				if (stockmap != null && !stockmap.isEmpty()) {
					custvo = stockmap.get(pk_corp + "一般纳税人");
					if (custvo != null) {
						retvo.setIstockcusttaxpay(custvo.getNum());// 存量客户数量
						retvo.setIstockconttaxpay(custvo.getSummny());// 存量客户合同金额
					}
					custvo = stockmap.get(pk_corp + "小规模纳税人");
					if (custvo != null) {
						retvo.setIstockcustsmall(custvo.getNum());// 存量客户数量
						retvo.setIstockcontsmall(custvo.getSummny());// 存量客户合同金额
					}
				}
				// 2.2 新增客户数量、合同金额赋值：
				if (newmap != null && !newmap.isEmpty()) {
					custvo = newmap.get(pk_corp + "一般纳税人");
					if (custvo != null) {
						retvo.setInewcusttaxpay(custvo.getNum());// 新增客户数量
						retvo.setInewconttaxpay(custvo.getSummny());// 新增客合同金额
					}
					custvo = newmap.get(pk_corp + "小规模纳税人");
					if (custvo != null) {
						retvo.setInewcustsmall(custvo.getNum());// 新增客户数量
						retvo.setInewcontsmall(custvo.getSummny());// 新增客户合同金额
					}
				}
				// 2.3续费客户数量、合同金额赋值：
				if (renewmap != null && !renewmap.isEmpty()) {
					custvo = renewmap.get(pk_corp + "一般纳税人");
					if (custvo != null) {
						retvo.setIrenewcusttaxpay(custvo.getNum());// 续费客户数量
						retvo.setIrenewconttaxpay(custvo.getSummny());// 续费客合同金额
					}
					custvo = renewmap.get(pk_corp + "小规模纳税人");
					if (custvo != null) {
						retvo.setIrenewcustsmall(custvo.getNum());// 续费客户数量
						retvo.setIrenewcontsmall(custvo.getSummny());// 续费客户合同金额
					}
				}

				// 3.1 上月新增客户数量、合同金额赋值：
				if (lastnewmap != null && !lastnewmap.isEmpty()) {
					custvo = lastnewmap.get(pk_corp + "一般纳税人");
					if (custvo != null) {
						retvo.setIlastnewcusttaxpay(custvo.getNum());// 新增客户数量
						retvo.setIlastnewconttaxpay(custvo.getSummny());// 新增客合同金额
					}
					custvo = lastnewmap.get(pk_corp + "小规模纳税人");
					if (custvo != null) {
						retvo.setIlastnewcustsmall(custvo.getNum());// 新增客户数量
						retvo.setIlastnewcontsmall(custvo.getSummny());// 新增客户合同金额
					}
				}
				// 3.2 上月续费客户数量、合同金额赋值：
				if (lastrenewmap != null && !lastrenewmap.isEmpty()) {
					custvo = lastrenewmap.get(pk_corp + "一般纳税人");
					if (custvo != null) {
						retvo.setIlastrenewcusttaxpay(custvo.getNum());// 续费客户数量
						retvo.setIlastrenewconttaxpay(custvo.getSummny());// 续费客合同金额
					}
					custvo = lastrenewmap.get(pk_corp + "小规模纳税人");
					if (custvo != null) {
						retvo.setIlastrenewcustsmall(custvo.getNum());// 续费客户数量
						retvo.setIlastrenewcontsmall(custvo.getSummny());// 续费客户合同金额
					}
				}
				// 4.1新增客户、合同增长率
				retvo.setInewcustratesmall(getCustRate(retvo.getInewcustsmall(), retvo.getIlastnewcustsmall()));
				retvo.setInewcustratetaxpay(getCustRate(retvo.getInewcusttaxpay(), retvo.getIlastnewcusttaxpay()));
				retvo.setInewcontratesmall(getContRate(retvo.getInewcontsmall(), retvo.getIlastnewcontsmall()));
				retvo.setInewcontratetaxpay(getContRate(retvo.getInewconttaxpay(), retvo.getIlastnewconttaxpay()));
				// 4.2续费客户、合同增长率
				retvo.setIrenewcustratesmall(getCustRate(retvo.getIrenewcustsmall(), retvo.getIlastrenewcustsmall()));
				retvo.setIrenewcustratetaxpay(
						getCustRate(retvo.getIrenewcusttaxpay(), retvo.getIlastrenewcusttaxpay()));
				retvo.setIrenewcontratesmall(getContRate(retvo.getIrenewcontsmall(), retvo.getIlastrenewcontsmall()));
				retvo.setIrenewcontratetaxpay(
						getContRate(retvo.getIrenewconttaxpay(), retvo.getIlastrenewconttaxpay()));
				retlist.add(retvo);
			}
		}
		return retlist;
	}

	/**
	 * 查询非存量客户数量、合同金额
	 * 
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, CustCountVO> queryStockNumMny(QryParamVO paramvo, List<String> corplist)
			throws DZFWarpException {
		Map<String, CustCountVO> stockmap = new HashMap<String, CustCountVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT pk_corp, chargedeptname, COUNT(pk_corpk) AS num, SUM(ntotalmny) AS summny \n");
		sql.append("  FROM (SELECT NVL(ct.chargedeptname, '小规模纳税人') AS chargedeptname, \n");
		sql.append("               t.pk_corp AS pk_corp, \n");
		sql.append("               t.pk_corpk AS pk_corpk, \n");
		sql.append(
				"               CASE t.vdeductstatus WHEN 1 THEN ct.ntotalmny WHEN 9 THEN t.nchangetotalmny END AS ntotalmny \n");
		// sql.append(" t.ntotalmny AS ntotalmny \n") ;
		// sql.append(" FROM ynt_contract t \n") ;
		sql.append("          FROM cn_contract t \n");
		sql.append("         INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append("          LEFT JOIN bd_account acc ON t.pk_corp = acc.pk_corp \n");
		sql.append("         WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("           AND nvl(ct.dr, 0) = 0 \n");
		sql.append("           AND nvl(ct.isncust,'N')='N' \n");//不统计存量客户
		if (corplist != null && corplist.size() > 0) {
			String condition = SqlUtil.buildSqlForIn("t.pk_corp", corplist.toArray(new String[corplist.size()]));
			sql.append(" and ");
			sql.append(condition);
		}
		sql.append("           AND nvl(ct.patchstatus, 0) != 2 \n");// 补单合同不统计
		sql.append("           AND nvl(acc.dr, 0) = 0 \n");
		sql.append("           AND nvl(acc.ischannel, 'N') = 'Y'\n");
		sql.append("           AND (ct.vbeginperiod = ? OR ct.vendperiod = ? OR \n");
		spm.addParam(paramvo.getPeriod());
		spm.addParam(paramvo.getPeriod());
		sql.append("                (ct.vbeginperiod < ? AND ct.vendperiod > ? )) \n");
		spm.addParam(paramvo.getPeriod());
		spm.addParam(paramvo.getPeriod());
		// sql.append(" AND nvl(t.icontracttype, 1) = 2 \n") ;
		sql.append("           AND t.vdeductstatus in ( 1 , 9) \n"); // 合同状态 =
																		// 已审核 或
																		// 已终止
		sql.append("   AND t.pk_corp NOT IN \n");
		sql.append("       (SELECT f.pk_corp \n");
		sql.append("          FROM ynt_franchisee f \n");
		sql.append("         WHERE nvl(dr, 0) = 0 \n");
		// sql.append(" AND nvl(f.isreport, 'N') = 'Y') \n");
		// sql.append(" AND NVL(t.isncust, 'N') = 'Y') cu \n") ;
		sql.append("           AND nvl(f.isreport, 'N') = 'Y')) cu \n");
		sql.append(" GROUP BY pk_corp, chargedeptname");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if (list != null && list.size() > 0) {
			String key = "";
			for (CustCountVO vo : list) {
				key = vo.getPk_corp() + "" + vo.getChargedeptname();
				stockmap.put(key, vo);
			}
		}
		return stockmap;
	}

	/**
	 * 查询新增（续费）客户数量、合同金额
	 * 
	 * @param paramvo
	 * @param corplist
	 * @param qrytype
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, CustCountVO> queryNumMnyByType(QryParamVO paramvo, List<String> corplist, Integer qrytype)
			throws DZFWarpException {
		Map<String, CustCountVO> map = new HashMap<String, CustCountVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT pk_corp,\n");
		sql.append("       chargedeptname,\n");
		sql.append("       COUNT(pk_corpk) AS num,\n");
		sql.append("       SUM(ntotalmny) AS summny\n");
		sql.append("  FROM (SELECT NVL(ct.chargedeptname, '小规模纳税人') AS chargedeptname,\n");
		sql.append("               t.pk_corp AS pk_corp,\n");
		sql.append(
				"               CASE t.vdeductstatus WHEN 1 THEN ct.ntotalmny WHEN 9 THEN t.nchangetotalmny END AS ntotalmny, \n");
		// sql.append(" t.ntotalmny AS ntotalmny,\n");
		sql.append("               t.pk_corpk AS pk_corpk\n");
		sql.append("          FROM cn_contract t\n");
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		// sql.append(" FROM ynt_contract t\n");
		sql.append("          LEFT JOIN bd_account acc ON t.pk_corp = acc.pk_corp \n");
		sql.append("         WHERE nvl(t.dr, 0) = 0\n");
		sql.append("           AND nvl(ct.dr, 0) = 0\n");
		if (corplist != null && corplist.size() > 0) {
			String condition = SqlUtil.buildSqlForIn("t.pk_corp", corplist.toArray(new String[corplist.size()]));
			sql.append(" and ");
			sql.append(condition);
		}
		sql.append("           AND nvl(ct.patchstatus, 0) != 2 \n");// 补单合同不统计
		sql.append("           AND nvl(acc.dr, 0) = 0\n");
		sql.append("           AND nvl(acc.ischannel, 'N') = 'Y' \n");
		sql.append("           AND nvl(ct.isncust,'N')='N' \n");//不统计存量客户
		sql.append("   AND t.pk_corp NOT IN \n");
		sql.append("       (SELECT f.pk_corp \n");
		sql.append("          FROM ynt_franchisee f \n");
		sql.append("         WHERE nvl(dr, 0) = 0 \n");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
		sql.append("           AND SUBSTR(ct.dsigndate, 1, 7) = ? \n");
		spm.addParam(paramvo.getPeriod());
		// sql.append(" AND nvl(t.icontracttype, 1) = 2 \n");
		sql.append("           AND t.vdeductstatus in ( 1 , 9) \n");// 合同状态 =
																	// 已审核 或已终止
		// sql.append(" AND NVL(t.isncust, 'N') = 'N'\n");
		if (qrytype == 1) {// 新增客户
			sql.append("           AND t.pk_corpk NOT IN \n");
		} else if (qrytype == 2) {// 续费客户
			sql.append("           AND t.pk_corpk IN \n");
		}
		sql.append("               (SELECT t.pk_corpk AS pk_corpk\n");
		sql.append("                  FROM cn_contract t\n");
		// sql.append(" FROM ynt_contract t\n");
		sql.append("                  LEFT JOIN bd_account acc ON t.pk_corp = acc.pk_corp\n");
		sql.append("                 WHERE nvl(t.dr, 0) = 0\n");
		sql.append("                   AND nvl(acc.dr, 0) = 0\n");
		sql.append("                   AND nvl(acc.ischannel, 'N') = 'Y'\n");
		sql.append("   AND t.pk_corp NOT IN \n");
		sql.append("       (SELECT f.pk_corp \n");
		sql.append("          FROM ynt_franchisee f \n");
		sql.append("         WHERE nvl(dr, 0) = 0 \n");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
		sql.append("                   AND SUBSTR(ct.dsigndate, 1, 7) < ? \n");
		spm.addParam(paramvo.getPeriod());
		// sql.append(" AND nvl(t.icontracttype, 1) = 2\n");
		sql.append("                   AND t.vdeductstatus in ( 1 , 9))) cu\n");// 合同状态
																				// =
																				// 已审核
																				// 或已终止
		sql.append(" GROUP BY pk_corp, chargedeptname");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if (list != null && list.size() > 0) {
			String key = "";
			for (CustCountVO vo : list) {
				key = vo.getPk_corp() + "" + vo.getChargedeptname();
				map.put(key, vo);
			}
		}
		return map;
	}

	/**
	 * 整数类型增长率计算方法
	 * 
	 * @param num1
	 *            本月数据
	 * @param num2
	 *            上月数据
	 * @return
	 */
	@Override
	public DZFDouble getCustRate(Integer num1, Integer num2) throws DZFWarpException {
		DZFDouble num3 = num1 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num1);
		DZFDouble num4 = num2 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num2);
		if (DZFDouble.ZERO_DBL.compareTo(num3) == 0) {
			return DZFDouble.ZERO_DBL;
		}
		if (DZFDouble.ZERO_DBL.compareTo(num4) == 0) {
			return DZFDouble.ZERO_DBL;
		}
		DZFDouble num = num3.sub(num4);
		return num.div(num2).multiply(100);
	}

	/**
	 * DZFDouble类型增长率计算方法
	 * 
	 * @param num1
	 *            本月数据
	 * @param num2
	 *            上月数据
	 * @return
	 */
	private DZFDouble getContRate(DZFDouble num1, DZFDouble num2) throws DZFWarpException {
		num1 = num1 == null ? DZFDouble.ZERO_DBL : num1;
		num2 = num2 == null ? DZFDouble.ZERO_DBL : num2;
		if (DZFDouble.ZERO_DBL.compareTo(num1) == 0) {
			return DZFDouble.ZERO_DBL;
		}
		if (DZFDouble.ZERO_DBL.compareTo(num2) == 0) {
			return DZFDouble.ZERO_DBL;
		}
		DZFDouble num = num1.sub(num2);
		return num.div(num2).multiply(100);
	}

	/**
	 * 查询客户数量
	 * 
	 * @param paramvo
	 * @param qrytype
	 *            1:存量客户查询；2：新增客户查询；
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CustCountVO> queryCustNum(QryParamVO paramvo, Integer qrytype, List<String> corplist)
			throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT p.fathercorp as pk_corp,\n");
		sql.append("       p.chargedeptname as chargedeptname,\n");
		sql.append("       count(p.pk_corp) as num \n");
		sql.append("  FROM bd_corp p \n");
		sql.append("  LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0 \n");
		if (corplist != null && corplist.size() > 0) {
			String condition = SqlUtil.buildSqlForIn("t.pk_corp", corplist.toArray(new String[corplist.size()]));
			sql.append(" and ");
			sql.append(condition);
		}
		sql.append("   AND nvl(t.dr, 0) = 0 \n");
		sql.append("   AND nvl(t.ischannel, 'N') = 'Y'\n"); // 渠道客户
		sql.append("   AND nvl(p.isseal, 'N') = 'N'\n"); // 未封存
		sql.append("   AND nvl(p.ishasaccount,'N') = 'Y' \n");// 已建账
		sql.append("   AND p.chargedeptname is not null \n");// 纳税人性质不能为空
		sql.append("   AND p.fathercorp NOT IN \n");
		sql.append("       (SELECT f.pk_corp \n");
		sql.append("          FROM ynt_franchisee f \n");
		sql.append("         WHERE nvl(dr, 0) = 0 \n");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
		if (qrytype == 1) {
			if (paramvo.getBegdate() != null) {
				sql.append("   AND p.createdate <= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
		} else if (qrytype == 2) {
			sql.append("   AND p.createdate <= ? \n");
			spm.addParam(paramvo.getBegdate());
			sql.append("   AND p.createdate >= ? \n");
			DZFDate date = new DZFDate();
			spm.addParam(date.getYear() + "-" + date.getStrMonth() + "-01");
		}
		sql.append(" GROUP BY (p.fathercorp, p.chargedeptname)");
		return (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
	}

	/**
	 * 计算客户分类信息
	 * 
	 * @param stocklist
	 * @param corplist
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public Map<String, CustNumMoneyRepVO> countCustNumByType(List<CustCountVO> custlist, Integer qrytype,
			List<String> corplist, List<String> countcorplist) throws DZFWarpException {
		Map<String, CustNumMoneyRepVO> custmap = new HashMap<String, CustNumMoneyRepVO>();
		CustNumMoneyRepVO countvo = null;
		countcorplist.clear();
		for (CustCountVO vo : custlist) {
			if (!corplist.contains(vo.getPk_corp())) {
				corplist.add(vo.getPk_corp());
			}
			if (!countcorplist.contains(vo.getPk_corp())) {
				countcorplist.add(vo.getPk_corp());
			}
			if (!custmap.containsKey(vo.getPk_corp())) {
				countvo = new CustNumMoneyRepVO();
				countvo.setPk_corp(vo.getPk_corp());
				if ("小规模纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						countvo.setIstockcustsmall(vo.getNum());// 存量客户-小规模
					} else if (qrytype == 2) {
						countvo.setInewcustsmall(vo.getNum());// 新增客户-小规模
					}
				} else if ("一般纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						countvo.setIstockcusttaxpay(vo.getNum());// 存量客户-一般纳税人
					} else if (qrytype == 2) {
						countvo.setInewcusttaxpay(vo.getNum());// 新增客户-一般纳税人
					}
				}
				countvo.setIcustsum(vo.getNum());// 客户合计
				custmap.put(vo.getPk_corp(), countvo);
			} else {
				countvo = custmap.get(vo.getPk_corp());
				if ("小规模纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						// 存量客户-小规模
						countvo.setIstockcustsmall(ToolsUtil.addInteger(countvo.getIstockcustsmall(), vo.getNum()));
					} else if (qrytype == 2) {
						// 新增客户-小规模
						countvo.setInewcustsmall(ToolsUtil.addInteger(countvo.getIstockcustsmall(), vo.getNum()));
					}
				} else if ("一般纳税人".equals(vo.getChargedeptname())) {
					if (qrytype == 1) {
						// 存量客户-一般纳税人
						countvo.setIstockcusttaxpay(ToolsUtil.addInteger(countvo.getIstockcusttaxpay(), vo.getNum()));
					} else if (qrytype == 2) {
						// 新增客户-一般纳税人
						countvo.setInewcusttaxpay(ToolsUtil.addInteger(countvo.getIstockcusttaxpay(), vo.getNum()));
					}
				}
				countvo.setIcustsum(ToolsUtil.addInteger(countvo.getIcustsum(), vo.getNum()));// 客户合计
			}
		}
		return custmap;
	}


	// 查询 培训负责人
	private List<CustNumMoneyRepVO> qryCharge(QryParamVO paramvo, Boolean flg) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select p.pk_corp,  \n");
		sql.append("       a.areaname,  \n");
		sql.append("       a.userid,  \n");
		sql.append("       b.vprovname,  \n");
		sql.append("       b.vprovince,  \n");
		sql.append("       p.innercode,  \n");
		sql.append("       (case  \n");
		sql.append("         when b.pk_corp is null then  \n");
		sql.append("          null  \n");
		sql.append("         when b.pk_corp != p.pk_corp then  \n");
		sql.append("          null  \n");
		sql.append("         else  \n");
		sql.append("          b.userid  \n");
		sql.append("       end) cuserid  \n");
		sql.append("  from bd_account p  \n");
		sql.append(" right join cn_chnarea_b b on p.vprovince = b.vprovince  \n");
		sql.append("  left join cn_chnarea a on b.pk_chnarea = a.pk_chnarea  \n");
		sql.append(" where nvl(b.dr, 0) = 0  \n");
		sql.append("   and nvl(p.dr, 0) = 0  \n");
		sql.append("   and nvl(a.dr, 0) = 0  \n");
		sql.append("   and nvl(p.ischannel, 'N') = 'Y'  \n");
		sql.append("   and nvl(p.isaccountcorp, 'N') = 'Y'  \n");
		sql.append("   and p.fathercorp = ?  \n");
		sql.append("   and b.type = 2  \n");
		sql.append("   and nvl(b.ischarge, 'N') = 'Y'  \n");
		sql.append("   AND p.pk_corp NOT IN  \n");
		sql.append("       (SELECT f.pk_corp  \n");
		sql.append("          FROM ynt_franchisee f  \n");
		sql.append("         WHERE nvl(dr, 0) = 0  \n");
		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
		sp.addParam(IDefaultValue.DefaultGroup);
		if (!flg) {
			sql.append("  and (a.userid=? or b.userid=? )");
			sp.addParam(paramvo.getUser_name());
			sp.addParam(paramvo.getUser_name());
		}
		if (!StringUtil.isEmpty(paramvo.getAreaname())) {
			sql.append(" and a.areaname=? "); // 大区
			sp.addParam(paramvo.getAreaname());
		}
		if (paramvo.getVprovince() != null && paramvo.getVprovince() != -1) {
			sql.append(" and b.vprovince=? ");// 省市
			sp.addParam(paramvo.getVprovince());
		}
		if (!StringUtil.isEmpty(paramvo.getCuserid())) {
			sql.append(" and b.userid=? ");// 渠道经理
			sp.addParam(paramvo.getCuserid());
		}
		List<CustNumMoneyRepVO> list = (List<CustNumMoneyRepVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(CustNumMoneyRepVO.class));
		return list;
	}

}
