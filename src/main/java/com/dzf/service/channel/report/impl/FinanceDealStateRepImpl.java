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
import com.dzf.model.channel.report.DataVO;
import com.dzf.model.channel.report.FinanceDealStateRepVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.DateUtils;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.IFinanceDealStateRep;

@Service("financedealstaterepser")
public class FinanceDealStateRepImpl extends DataCommonRepImpl implements IFinanceDealStateRep {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public List<FinanceDealStateRepVO> query(QryParamVO pamvo) throws DZFWarpException {
		if (!StringUtil.isEmpty(pamvo.getPeriod())) {
		    DZFDate begindate =DateUtils.getPeriodEndDate(pamvo.getPeriod());
			pamvo.setBegdate(begindate);
		}

		// 1、按照所选则的大区、省（市）、会计运营经理和当前登陆人过滤出符合条件的加盟商信息
		HashMap<String, DataVO> map = queryCorps(pamvo, FinanceDealStateRepVO.class);
		List<String> corplist = null;
		if (map != null && !map.isEmpty()) {
			Collection<String> col = map.keySet();
			corplist = new ArrayList<String>(col);
		}

		if (corplist != null && corplist.size() > 0  ) {
			return getRetList(pamvo, corplist, map);
		}
		return null;
	}

	/**
	 * 获取返回数据列表
	 * 
	 * @param pamvo
	 * @param corplist
	 * @param map
	 * @return
	 * @throws DZFWarpException
	 */
	private List<FinanceDealStateRepVO> getRetList(QryParamVO pamvo, List<String> corplist, HashMap<String, DataVO> map)
			throws DZFWarpException {
		List<FinanceDealStateRepVO> retlist = new ArrayList<FinanceDealStateRepVO>();
		List<String> replist = new ArrayList<String>();// 统计凭证的加盟商主键
		List<CustCountVO> custlist = queryCustNum(pamvo, corplist);
		Map<String, FinanceDealStateRepVO> custmap = getRetMap(custlist, replist);
		FinanceDealStateRepVO retvo = null;
		FinanceDealStateRepVO showvo = null;
		Map<String, Map<String, CustCountVO>> retmap = queryVoucher(replist, pamvo.getPeriod());
		if (corplist != null && corplist.size() > 0) {
			CorpVO corpvo = null;
			UserVO uservo = null;
			Map<String, CustCountVO> voumap = null;
			CustCountVO countvo = null;
			for (String pk_corp : corplist) {
				retvo = custmap.get(pk_corp);
				if (retvo == null) {
					retvo = new FinanceDealStateRepVO();
					retvo.setPk_corp(pk_corp);
				}
				corpvo = CorpCache.getInstance().get(null, pk_corp);
				if (corpvo != null) {
					retvo.setCorpname(corpvo.getUnitname());
					retvo.setVprovname(corpvo.getCitycounty());
					retvo.setInnercode(corpvo.getInnercode());// 加盟商编码
				}
				showvo = (FinanceDealStateRepVO) map.get(pk_corp);
				if (showvo != null) {
					DataVO data = map.get(pk_corp);
					retvo.setChndate(showvo.getChndate());
					retvo.setAreaname(showvo.getAreaname());// 大区
					retvo.setUsername(showvo.getUsername());// 区总
					retvo.setCusername(showvo.getCusername());// 会计运营经理
					corpvo.setDrelievedate(data.getDrelievedate());
				}
				retvo.setIcustratesmall(getCustRate(retvo.getIcustsmall(), retvo.getIcusttaxpay()));
				retvo.setIcustratetaxpay(getCustRate(retvo.getIcusttaxpay(), retvo.getIcustsmall()));
				if (retmap != null && !retmap.isEmpty()) {
					voumap = retmap.get(pk_corp);
					if (voumap != null && !voumap.isEmpty()) {
						countvo = voumap.get("小规模纳税人");
						if (countvo != null) {
							retvo.setIvouchernummall(countvo.getNum());
						}
						countvo = voumap.get("一般纳税人");
						if (countvo != null) {
							retvo.setIvouchernumtaxpay(countvo.getNum());
						}
					}

				}
				retvo.setDrelievedate(corpvo.getDrelievedate());
				retlist.add(retvo);
			}
		}
		return retlist;
	}

	/**
	 * 客户占比计算
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 * @throws DZFWarpException
	 */
	private DZFDouble getCustRate(Integer num1, Integer num2) throws DZFWarpException {
		DZFDouble num3 = num1 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num1);
		DZFDouble num4 = num2 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num2);
		DZFDouble num = num4.add(num3);
		return num3.div(num).multiply(100);
	}

	/**
	 * 加盟商凭证计算
	 * 
	 * @param countcorplist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Map<String, CustCountVO>> queryVoucher(List<String> countcorplist, String period)
			throws DZFWarpException {
		Map<String, Map<String, CustCountVO>> retmap = new HashMap<String, Map<String, CustCountVO>>();
		if (countcorplist == null || countcorplist.size() == 0) {
			return retmap;
		}
		Map<String, CustCountVO> voumap = null;
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT p.fathercorp as pk_corp, \n");
		sql.append("  nvl(p.chargedeptname,'小规模纳税人') AS chargedeptname,  \n");
		sql.append("  count(DISTINCT h.pk_tzpz_h) as num \n");
		sql.append("  FROM ynt_tzpz_h h \n");
		sql.append("  LEFT JOIN bd_corp p ON h.pk_corp = p.pk_corp \n");
		sql.append(" WHERE nvl(h.dr, 0) = 0 and h.period = ? \n");
		sql.append("   AND nvl(p.dr, 0) = 0 \n");
		sql.append("   AND nvl(p.ishasaccount,'N') = 'Y' \n");// 已建账
		sql.append("   AND nvl(p.isseal, 'N') = 'N'\n"); // 未封存客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N' \n");// 非分支机构
		String where = SqlUtil.buildSqlForIn("p.fathercorp", countcorplist.toArray(new String[0]));
		sql.append(" AND ").append(where);
		sql.append(" GROUP BY (p.chargedeptname, p.fathercorp)");
		spm.addParam(period);
		List<CustCountVO> vouchlist = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if (vouchlist != null && vouchlist.size() > 0) {
			for (CustCountVO vo : vouchlist) {
				if (!StringUtil.isEmpty(vo.getPk_corp())) {
					if (!retmap.containsKey(vo.getPk_corp())) {
						voumap = new HashMap<String, CustCountVO>();
						if (!StringUtil.isEmpty(vo.getChargedeptname())) {
							voumap.put(vo.getChargedeptname(), vo);
						}
						retmap.put(vo.getPk_corp(), voumap);
					} else {
						voumap = retmap.get(vo.getPk_corp());
						if (!StringUtil.isEmpty(vo.getChargedeptname())) {
							voumap.put(vo.getChargedeptname(), vo);
						}
					}
				}
			}
		}
		return retmap;
	}

	/**
	 * 查询各类纳税人资格的新增及存量客户
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
		sql.append("       COUNT(p.pk_corp) AS num,  \n");
		sql.append("       nvl(p.isncust, 'N') AS isncust \n");
		sql.append("  FROM bd_corp p  \n");
		sql.append("  LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp  \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.ischannel, 'N') = 'Y'  \n");
		sql.append("   AND nvl(p.isaccountcorp, 'N') = 'N'  \n");
		sql.append("   AND nvl(p.ishasaccount,'N') = 'Y' \n");// 已建账
		sql.append("   AND nvl(p.isseal, 'N') = 'N'\n"); // 未封存客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N' \n");// 非分支机构
		if (!StringUtil.isEmpty(pamvo.getPeriod())) {
			sql.append("   AND substr(p.createdate,1,7) <= ? \n");
			spm.addParam(pamvo.getPeriod());
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
		sql.append("          nvl(p.chargedeptname, '小规模纳税人'),  \n");
		sql.append("          nvl(p.isncust, 'N') \n");
		return (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
	}

	/**
	 * 查询各类纳税人资格的新增及存量客户汇总
	 * 
	 * @param list
	 * @param replist
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, FinanceDealStateRepVO> getRetMap(List<CustCountVO> list, List<String> replist)
			throws DZFWarpException {
		Map<String, FinanceDealStateRepVO> map = new HashMap<String, FinanceDealStateRepVO>();
		if (list != null && list.size() > 0) {
			FinanceDealStateRepVO repvo = null;
			for (CustCountVO cvo : list) {
				if (!map.containsKey(cvo.getPk_corp())) {
					repvo = new FinanceDealStateRepVO();
					repvo.setPk_corp(cvo.getPk_corp());
					if ("小规模纳税人".equals(cvo.getChargedeptname())) {
						if (cvo.getIsncust() != null && cvo.getIsncust().booleanValue()) {// 存量
							repvo.setIstocksmall(cvo.getNum());
						} else {// 新增
							repvo.setInewsmall(cvo.getNum());
						}
						repvo.setIcustsmall(cvo.getNum());
					} else if ("一般纳税人".equals(cvo.getChargedeptname())) {
						if (cvo.getIsncust() != null && cvo.getIsncust().booleanValue()) {// 存量
							repvo.setIstocktaxpay(cvo.getNum());
						} else {// 新增
							repvo.setInewtaxpay(cvo.getNum());
						}
						repvo.setIcusttaxpay(cvo.getNum());
					}
					if (!replist.contains(cvo.getPk_corp())) {
						replist.add(cvo.getPk_corp());
					}
					map.put(cvo.getPk_corp(), repvo);
				} else {
					repvo = map.get(cvo.getPk_corp());
					if ("小规模纳税人".equals(cvo.getChargedeptname())) {
						if (cvo.getIsncust() != null && cvo.getIsncust().booleanValue()) {// 存量
							repvo.setIstocksmall(ToolsUtil.addInteger(repvo.getIstocksmall(), cvo.getNum()));
						} else {// 新增
							repvo.setInewsmall(ToolsUtil.addInteger(repvo.getInewsmall(), cvo.getNum()));
						}
						repvo.setIcustsmall(ToolsUtil.addInteger(repvo.getIcustsmall(), cvo.getNum()));
					} else if ("一般纳税人".equals(cvo.getChargedeptname())) {
						if (cvo.getIsncust() != null && cvo.getIsncust().booleanValue()) {// 存量
							repvo.setIstocktaxpay(ToolsUtil.addInteger(repvo.getIstocktaxpay(), cvo.getNum()));
						} else {// 新增
							repvo.setInewtaxpay(ToolsUtil.addInteger(repvo.getInewtaxpay(), cvo.getNum()));
						}
						repvo.setIcusttaxpay(ToolsUtil.addInteger(repvo.getIcusttaxpay(), cvo.getNum()));
					}
					map.put(cvo.getPk_corp(), repvo);
				}
			}
		}
		return map;
	}

}
