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
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.ICustNumMoneyService;

@Service("custnummoneyrepser")
public class CustNumMoneyServiceImpl extends DataCommonRepImpl implements ICustNumMoneyService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public List<CustNumMoneyRepVO> query(QryParamVO paramvo) throws DZFWarpException {
		HashMap<String, DataVO> map = queryCorps(paramvo, CustNumMoneyRepVO.class,2);
		List<String> corplist = null;
		if (map != null && !map.isEmpty()) {
			Collection<String> col = map.keySet();
			corplist = new ArrayList<String>(col);
		}
		if (corplist != null && corplist.size() > 0) {
			boolean isqrymon = false;// 是否按月查询
			if (!StringUtil.isEmpty(paramvo.getPeriod())) {
				isqrymon = true;
			}
			// 1、查询客户总数量、合同总金额
			Map<String, Integer> custmap = queryCustNum(paramvo, corplist, null);
			Map<String, DZFDouble> conmap = queryContMny(paramvo, corplist, null);
			// 2、查询新增客户数量、合同金额
			Map<String, Integer> ncustmap = queryCustNum(paramvo, corplist, 1);
			Map<String, DZFDouble> nconmap = queryContMny(paramvo, corplist, 1);
			// 3、查询上一个月新增客户数量、合同金额
			Map<String, Integer> lncustmap = null;
			Map<String, DZFDouble> lnconmap = null;
			if (isqrymon) {
				lncustmap = queryCustNum(paramvo, corplist, 2);
				lnconmap = queryContMny(paramvo, corplist, 2);
			}
			// 4、查询合同提单量
			Map<String, Integer> cmap = null;
			if (isqrymon) {
				cmap = queryContNumByMonth(paramvo, corplist);
			} else {
				cmap = queryContNumByPeriod(paramvo, corplist);
			}
			return getReturnList(corplist, map, custmap, conmap, ncustmap, nconmap, lncustmap, lnconmap, cmap,
					isqrymon);
		}
		return null;
	}

	/**
	 * 获取返回数据
	 * 
	 * @param corplist
	 * @param map
	 * @param custmap
	 * @param conmap
	 * @param ncustmap
	 * @param nconmap
	 * @param lncustmap
	 * @param lnconmap
	 * @param cmap
	 * @return
	 * @throws DZFWarpException
	 */
	private List<CustNumMoneyRepVO> getReturnList(List<String> corplist, HashMap<String, DataVO> map,
			Map<String, Integer> custmap, Map<String, DZFDouble> conmap, Map<String, Integer> ncustmap,
			Map<String, DZFDouble> nconmap, Map<String, Integer> lncustmap, Map<String, DZFDouble> lnconmap,
			Map<String, Integer> cmap, boolean isqrymon) throws DZFWarpException {
		List<CustNumMoneyRepVO> retlist = new ArrayList<CustNumMoneyRepVO>();
		CorpVO corpvo = null;
		CustNumMoneyRepVO retvo = null;
		for (String pk_corp : corplist) {
			retvo = (CustNumMoneyRepVO) map.get(pk_corp);
			corpvo = CorpCache.getInstance().get(null, pk_corp);
			if (corpvo != null) {
				retvo.setCorpname(corpvo.getUnitname());
				retvo.setVprovname(corpvo.getCitycounty());
				retvo.setDrelievedate(corpvo.getDrelievedate());
			}
			// 1 、客户数量、合同金额：
			if (custmap != null && !custmap.isEmpty()) {
				retvo.setIstockcusttaxpay(custmap.get(pk_corp + "一般纳税人"));
				retvo.setIstockcustsmall(custmap.get(pk_corp + "小规模纳税人"));
			}
			if (conmap != null && !conmap.isEmpty()) {
				retvo.setIstockconttaxpay(conmap.get(pk_corp + "一般纳税人"));
				retvo.setIstockcontsmall(conmap.get(pk_corp + "小规模纳税人"));
			}
			// 2 、新增客户数量、合同金额：
			if (ncustmap != null && !ncustmap.isEmpty()) {
				retvo.setInewcusttaxpay(ncustmap.get(pk_corp + "一般纳税人"));
				retvo.setInewcustsmall(ncustmap.get(pk_corp + "小规模纳税人"));
			}
			if (nconmap != null && !nconmap.isEmpty()) {
				retvo.setInewconttaxpay(nconmap.get(pk_corp + "一般纳税人"));
				retvo.setInewcontsmall(nconmap.get(pk_corp + "小规模纳税人"));
			}

			// 3、 上月新增客户数量、合同金额：
			if (lncustmap != null && !lncustmap.isEmpty()) {
				retvo.setIlastnewcusttaxpay(lncustmap.get(pk_corp + "一般纳税人"));
				retvo.setIlastnewcustsmall(lncustmap.get(pk_corp + "小规模纳税人"));
			}
			if (lnconmap != null && !lnconmap.isEmpty()) {
				retvo.setIlastnewconttaxpay(lnconmap.get(pk_corp + "一般纳税人"));
				retvo.setIlastnewcontsmall(lnconmap.get(pk_corp + "小规模纳税人"));
			}
			// 4、新增客户、合同增长率
			if (isqrymon) {
				retvo.setInewcustratesmall(getCustRate(retvo.getInewcustsmall(), retvo.getIlastnewcustsmall()));
				retvo.setInewcustratetaxpay(getCustRate(retvo.getInewcusttaxpay(), retvo.getIlastnewcusttaxpay()));
				retvo.setInewcontratesmall(getContRate(retvo.getInewcontsmall(), retvo.getIlastnewcontsmall()));
				retvo.setInewcontratetaxpay(getContRate(retvo.getInewconttaxpay(), retvo.getIlastnewconttaxpay()));
			}
			// 5、合同数量
			if (cmap != null && !cmap.isEmpty()) {
				retvo.setIcontnum(cmap.get(pk_corp));
			}
			retlist.add(retvo);
		}
		return retlist;
	}

	/**
	 * 查询客户数量
	 * 
	 * @param pamvo
	 *            查询参数
	 * @param corplist
	 *            过滤后加盟商主键
	 * @param qrytype
	 *            查询类型 空：所有数据； 1：新增客户；2：新增客户（上月）；
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Integer> queryCustNum(QryParamVO pamvo, List<String> corplist, Integer qrytype)
			throws DZFWarpException {
		Map<String, Integer> nummap = new HashMap<String, Integer>();
		SQLParameter spm = new SQLParameter();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT p.fathercorp AS pk_corp,  ");
		sql.append("       NVL(p.chargedeptname, '小规模纳税人') AS chargedeptname,  ");
		sql.append("       COUNT(p.pk_corp) AS num    ");
		sql.append("  FROM bd_corp p    ");
		sql.append("  LEFT JOIN bd_account acc ON p.fathercorp = acc.pk_corp    ");
		sql.append(" WHERE nvl(p.dr, 0) = 0    ");
		sql.append("   AND nvl(acc.dr, 0) = 0    ");
		sql.append("   AND nvl(acc.ischannel, 'N') = 'Y'  ");
		sql.append("   AND nvl(p.isseal, 'N') = 'N'   ");// 非流失客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N'   ");// 非会计公司
		if(pamvo.getIsncust() != null){
			sql.append("   AND nvl(p.isncust,'N') = ?   ");
			spm.addParam(pamvo.getIsncust());
		}
		if (corplist != null && corplist.size() > 0) {
			String where = SqlUtil.buildSqlForIn("acc.pk_corp", corplist.toArray(new String[0]));
			sql.append(" AND ");
			sql.append(where);
		}
		if (qrytype != null && qrytype == 1) {// 新增客户
			if (!StringUtil.isEmpty(pamvo.getPeriod())) {
				// 按照查询月份查询
				sql.append(" AND substr(p.createdate, 1, 7) = ?   ");
				spm.addParam(pamvo.getPeriod());
			} else {
				// 按照查询期间查询
				if (pamvo.getBegdate() != null) {
					sql.append(" AND p.createdate >= ?   ");
					spm.addParam(pamvo.getBegdate());
				}
				if (pamvo.getEnddate() != null) {
					sql.append(" AND p.createdate <= ?   ");
					spm.addParam(pamvo.getEnddate());
				}
			}
		} else if (qrytype != null && qrytype == 2) {// 新增客户（上月）
			sql.append(" AND substr(p.createdate, 1, 7) = ?   ");
			String preperiod = ToolsUtil.getPreviousMonth(pamvo.getPeriod());
			spm.addParam(preperiod);
		} 
		sql.append(" GROUP BY p.fathercorp, NVL(p.chargedeptname, '小规模纳税人')   ");

		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if (list != null && list.size() > 0) {
			String key = "";
			for (CustCountVO vo : list) {
				key = vo.getPk_corp() + "" + vo.getChargedeptname();
				nummap.put(key, vo.getNum());
			}
		}
		return nummap;
	}

	/**
	 * 查询合同金额
	 * 
	 * @param qrytype
	 * @param corplist
	 * @param qrytype
	 *            查询类型 空：所有数据； 1：新增客户；2：新增客户（上月）；
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public Map<String, DZFDouble> queryContMny(QryParamVO pamvo, List<String> corplist, Integer qrytype)
			throws DZFWarpException {
		Map<String, DZFDouble> mnymap = new HashMap<String, DZFDouble>();
		// 1、扣款信息：
		List<CustCountVO> plist = qryPositiveData(pamvo, corplist, qrytype);
		String key = "";
		if (plist != null && plist.size() > 0) {
			for (CustCountVO vo : plist) {
				key = vo.getPk_corp() + "" + vo.getChargedeptname();
				mnymap.put(key, vo.getSummny());
			}

		}
		// 2、退款信息：
		List<CustCountVO> nlist = qryNegativeData(pamvo, corplist, qrytype);
		if (nlist != null && nlist.size() > 0) {
			for (CustCountVO vo : nlist) {
				key = vo.getPk_corp() + "" + vo.getChargedeptname();
				if (!mnymap.containsKey(key)) {
					mnymap.put(key, vo.getSummny());
				} else {
					DZFDouble mny = SafeCompute.add(mnymap.get(key), vo.getSummny());
					mnymap.put(key, mny);
				}
			}
		}
		return mnymap;
	}

	/**
	 * 查询扣款数据
	 * 
	 * @param pamvo
	 * @param corplist
	 * @param qrytype
	 *            查询类型 空：所有数据； 1：新增客户；2：新增客户（上月）；
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<CustCountVO> qryPositiveData(QryParamVO pamvo, List<String> corplist, Integer qrytype)
			throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,   ");
		sql.append("       NVL(p.chargedeptname, '小规模纳税人') AS chargedeptname,  ");
		sql.append("       SUM(nvl(ct.nchangetotalmny, 0)) AS summny    ");
		sql.append("  FROM cn_contract t    ");
		sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract    ");
		sql.append("  LEFT JOIN bd_account acc ON t.pk_corp = acc.pk_corp    ");
		sql.append("  LEFT JOIN bd_corp p ON t.pk_corpk = p.pk_corp    ");
		sql.append(" WHERE nvl(t.dr, 0) = 0    ");
		sql.append("   AND nvl(ct.dr, 0) = 0    ");
		sql.append("   AND nvl(acc.dr, 0) = 0    ");
		sql.append("   AND nvl(acc.ischannel, 'N') = 'Y'  ");
		sql.append("   AND nvl(p.dr, 0) = 0    ");
		sql.append("   AND nvl(p.isseal,'N') = 'N'   ");// 非封存客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N'   ");// 非会计公司
		if(pamvo.getIsncust() != null){
			sql.append("   AND nvl(p.isncust,'N') = ?   ");
			spm.addParam(pamvo.getIsncust());
		}
		if (corplist != null && corplist.size() > 0) {
			String where = SqlUtil.buildSqlForIn("acc.pk_corp", corplist.toArray(new String[0]));
			sql.append(" AND ");
			sql.append(where);
		}
		sql.append(" AND nvl(ct.isxq,'N') = 'N' ");
		if (qrytype != null && qrytype == 1) {// 新增客户
			if (!StringUtil.isEmpty(pamvo.getPeriod())) {
				// 按照查询月份查询
				sql.append(" AND substr(p.createdate, 1, 7) = ?   ");
				spm.addParam(pamvo.getPeriod());
			} else {
				// 按照查询期间查询
				if (pamvo.getBegdate() != null) {
					sql.append(" AND p.createdate >= ?   ");
					spm.addParam(pamvo.getBegdate());
				}
				if (pamvo.getEnddate() != null) {
					sql.append(" AND p.createdate <= ?   ");
					spm.addParam(pamvo.getEnddate());
				}
			}
		} else if (qrytype != null && qrytype == 2) {// 新增客户（上月）
			sql.append(" AND substr(p.createdate, 1, 7) = ?   ");
			String preperiod = ToolsUtil.getPreviousMonth(pamvo.getPeriod());
			spm.addParam(preperiod);
		}
		
		sql.append("   AND t.vdeductstatus in (?, ?, ?)    ");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		sql.append(" GROUP BY t.pk_corp, NVL(p.chargedeptname, '小规模纳税人')   ");
		return (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
	}

	/**
	 * 查询退款数据
	 * 
	 * @param pamvo
	 * @param corplist
	 * @param qrytype
	 *            查询类型 空：所有数据； 1：新增客户；2：新增客户（上月）；
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<CustCountVO> qryNegativeData(QryParamVO pamvo, List<String> corplist, Integer qrytype)
			throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,  ");
		sql.append("       NVL(p.chargedeptname, '小规模纳税人') AS chargedeptname,  ");
		sql.append("       SUM(nvl(t.nsubtotalmny, 0)) AS summny");
		sql.append("  FROM cn_contract t    ");
		sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract    ");
		sql.append("  LEFT JOIN bd_account acc ON t.pk_corp = acc.pk_corp    ");
		sql.append("  LEFT JOIN bd_corp p ON t.pk_corpk = p.pk_corp     ");
		sql.append(" WHERE nvl(t.dr, 0) = 0    ");
		sql.append("   AND nvl(ct.dr, 0) = 0    ");
		sql.append("   AND nvl(acc.dr, 0) = 0    ");
		sql.append("   AND nvl(acc.ischannel, 'N') = 'Y'  ");
		sql.append("   AND nvl(p.isseal,'N') = 'N'   ");// 非封存客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N'   ");// 非会计公司
		if(pamvo.getIsncust() != null){
			sql.append("   AND nvl(p.isncust,'N') = ?   ");
			spm.addParam(pamvo.getIsncust());
		}
		if (corplist != null && corplist.size() > 0) {
			String where = SqlUtil.buildSqlForIn("acc.pk_corp", corplist.toArray(new String[0]));
			sql.append(" AND ");
			sql.append(where);
		}
		sql.append(" AND nvl(ct.isxq,'N') = 'N' ");
		if (qrytype != null && qrytype == 1) {// 新增客户
			if (!StringUtil.isEmpty(pamvo.getPeriod())) {
				// 按照查询月份查询
				sql.append(" AND substr(p.createdate, 1, 7) = ?   ");
				spm.addParam(pamvo.getPeriod());
			} else {
				// 按照查询期间查询
				if (pamvo.getBegdate() != null) {
					sql.append(" AND p.createdate >= ?   ");
					spm.addParam(pamvo.getBegdate());
				}
				if (pamvo.getEnddate() != null) {
					sql.append(" AND p.createdate <= ?   ");
					spm.addParam(pamvo.getEnddate());
				}
			}
		} else if (qrytype != null && qrytype == 2) {// 新增客户（上月）
			sql.append(" AND substr(p.createdate, 1, 7) = ?   ");
			String preperiod = ToolsUtil.getPreviousMonth(pamvo.getPeriod());
			spm.addParam(preperiod);
		}
		
		sql.append("   AND t.vdeductstatus in (?, ?)    ");
		sql.append(" GROUP BY t.pk_corp, NVL(p.chargedeptname, '小规模纳税人')   ");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		return (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
	}

	/**
	 * 整数类型增长率计算方法
	 * 
	 * @param num1
	 *            本月数据
	 * 
	 * @param num2
	 *            上月数据
	 * 
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
	 * 查询合同提单量（按月查询）
	 * 
	 * @param pamvo
	 * @param corplist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Integer> queryContNumByMonth(QryParamVO pamvo, List<String> corplist)
			throws DZFWarpException {
		Map<String, Integer> cmap = new HashMap<String, Integer>();

		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,   ");
		
		sql.append("       SUM(CASE    ");
		sql.append("             WHEN substr(t.deductdata, 1, 7) = ?  THEN  ");
		spm.addParam(pamvo.getPeriod());
		sql.append("              1    ");
		sql.append("             ELSE    ");
		sql.append("              0    ");
		sql.append("           END) -    ");
		
		sql.append("       SUM(CASE    ");
		sql.append("             WHEN t.vdeductstatus = 10 AND substr(t.dchangetime, 1, 7) = ?  THEN   ");
		spm.addParam(pamvo.getPeriod());
		sql.append("              1    ");
		sql.append("             ELSE    ");
		sql.append("              0    ");
		sql.append("           END) AS num    ");
		
		sql.append("  FROM cn_contract t   ");
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract   ");
		sql.append("  LEFT JOIN bd_corp p ON ct.pk_corpk = p.pk_corp    ");
		sql.append(" WHERE nvl(t.dr, 0) = 0   ");
		sql.append("   AND nvl(ct.dr, 0) = 0   ");
		sql.append("   AND nvl(p.dr, 0) = 0    ");
		sql.append("   AND nvl(p.isseal,'N') = 'N'   ");// 非流失客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N'   ");// 非会计公司
		if(pamvo.getIsncust() != null){
			sql.append("   AND nvl(p.isncust,'N') = ?   ");
			spm.addParam(pamvo.getIsncust());
		}
		sql.append("   AND t.vdeductstatus in (?, ?, ?)   ");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);

		if (corplist != null && corplist.size() > 0) {
			String condition = SqlUtil.buildSqlForIn("t.pk_corp", corplist.toArray(new String[corplist.size()]));
			sql.append(" and ");
			sql.append(condition);
		}

		sql.append("   AND ( substr(t.deductdata, 1, 7) = ? OR    ");
		sql.append("         substr(t.dchangetime, 1, 7) = ? )    ");
		spm.addParam(pamvo.getPeriod());
		spm.addParam(pamvo.getPeriod());
		// 合同数量去掉补提单合同数
		sql.append("  AND nvl(ct.patchstatus,0) != 2 AND nvl(ct.patchstatus,0) != 5     ");
		sql.append("   GROUP BY t.pk_corp   ");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if (list != null && list.size() > 0) {
			for (CustCountVO cvo : list) {
				cmap.put(cvo.getPk_corp(), cvo.getNum());
			}
		}
		return cmap;
	}

	/**
	 * 查询合同提单量（按期间查询）
	 * 
	 * @param pamvo
	 * @param corplist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Integer> queryContNumByPeriod(QryParamVO pamvo, List<String> corplist)
			throws DZFWarpException {
		Map<String, Integer> cmap = new HashMap<String, Integer>();

		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,   ");
		
		// 扣款期间在查询期间，记录1
		sql.append("       SUM(CASE    ");
		sql.append("             WHEN t.deductdata >= ? AND t.deductdata <= ?  THEN   ");
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("              1    ");
		sql.append("             ELSE    ");
		sql.append("              0    ");
		sql.append("           END) -    ");
		
		// 作废日期在查询期间，记录-1
		sql.append("       SUM(CASE    ");
		sql.append("           WHEN t.vdeductstatus = 10    ");
		sql.append("            AND substr(t.dchangetime, 1, 10) >= ?   ");
		sql.append("            AND substr(t.dchangetime, 1, 10) <= ? THEN ");
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("              1    ");
		sql.append("             ELSE    ");
		sql.append("              0    ");
		sql.append("           END) AS num    ");
		
		sql.append("  FROM cn_contract t   ");
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract   ");
		sql.append("  LEFT JOIN bd_corp p ON t.pk_corpk = p.pk_corp    ");
		sql.append(" WHERE nvl(t.dr, 0) = 0   ");
		sql.append("   AND nvl(ct.dr, 0) = 0   ");
		sql.append("   AND nvl(p.dr, 0) = 0    ");
		sql.append("   AND nvl(p.isseal,'N') = 'N'   ");// 非封存客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N'   ");// 非会计公司
		if(pamvo.getIsncust() != null){
			sql.append("   AND nvl(p.isncust,'N') = ?   ");
			spm.addParam(pamvo.getIsncust());
		}
		sql.append("   AND t.vdeductstatus in (?, ?, ?)   ");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);

		if (corplist != null && corplist.size() > 0) {
			String condition = SqlUtil.buildSqlForIn("t.pk_corp", corplist.toArray(new String[corplist.size()]));
			sql.append(" and ");
			sql.append(condition);
		}

		sql.append("   AND  ( ( t.deductdata >= ? AND t.deductdata <= ? )   ");
		sql.append("       OR (substr(t.dchangetime, 1, 10) >= ? AND substr(t.dchangetime, 1, 10) <= ? ) )   ");
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		// 合同数量去掉补提单合同数
		sql.append("     AND  nvl(ct.patchstatus,0) != 2 AND nvl(ct.patchstatus,0) != 5     ");
		sql.append("   GROUP BY t.pk_corp   ");
		List<CustCountVO> list = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if (list != null && list.size() > 0) {
			for (CustCountVO cvo : list) {
				cmap.put(cvo.getPk_corp(), cvo.getNum());
			}
		}
		return cmap;
	}
}
