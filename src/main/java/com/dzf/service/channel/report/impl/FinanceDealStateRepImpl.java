package com.dzf.service.channel.report.impl;

import java.text.ParseException;
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
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.report.CustCountVO;
import com.dzf.model.channel.report.DataVO;
import com.dzf.model.channel.report.FinanceDealStateRepVO;
import com.dzf.model.channel.report.FinanceDetailVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.jm.CodeUtils1;
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

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Override
	public List<FinanceDealStateRepVO> query(QryParamVO pamvo) throws DZFWarpException {
		if (!StringUtil.isEmpty(pamvo.getPeriod())) {
			try {
				String begindate = ToolsUtil.getMaxMonthDate(pamvo.getPeriod() + "-01");
				pamvo.setBegdate(new DZFDate(begindate));
			} catch (ParseException e) {
				throw new BusinessException("日期格式转换错误");
			}
		}

		// 1、按照所选则的大区、省（市）、会计运营经理和当前登陆人过滤出符合条件的加盟商信息
		HashMap<String, DataVO> map = queryCorps(pamvo, FinanceDealStateRepVO.class);
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
		if (custmap == null || custmap.isEmpty()) {
			return null;
		}
		FinanceDealStateRepVO retvo = null;
		FinanceDealStateRepVO showvo = null;
		Map<String, Map<String, CustCountVO>> retmap = queryVoucher(replist, pamvo.getPeriod());
		if (replist != null && replist.size() > 0) {
			CorpVO corpvo = null;
			UserVO uservo = null;
			Map<String, CustCountVO> voumap = null;
			CustCountVO countvo = null;
			for (String pk_corp : replist) {
				retvo = custmap.get(pk_corp);
				corpvo = CorpCache.getInstance().get(null, pk_corp);
				if (corpvo != null) {
					retvo.setCorpname(corpvo.getUnitname());
					retvo.setVprovname(corpvo.getCitycounty());
					retvo.setInnercode(corpvo.getInnercode());// 加盟商编码
				}
				showvo = (FinanceDealStateRepVO) map.get(pk_corp);
				if (showvo != null) {
					retvo.setAreaname(showvo.getAreaname());// 大区
					uservo = UserCache.getInstance().get(showvo.getUserid(), pk_corp);
					if (uservo != null) {
						retvo.setUsername(uservo.getUser_name());// 区总
					}
					uservo = UserCache.getInstance().get(showvo.getCuserid(), pk_corp);
					if (uservo != null) {
						retvo.setCusername(uservo.getUser_name());// 会计运营经理
					}
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
		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");
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
		sql.append("       nvl(p.isncust, 'N') AS isncust  \n");
		sql.append("  FROM bd_corp p  \n");
		sql.append("  LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp  \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.ischannel, 'N') = 'Y'  \n");
		sql.append("   AND nvl(p.isaccountcorp, 'N') = 'N'  \n");
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

	@SuppressWarnings("unchecked")
	@Override
	public List<FinanceDetailVO> queryDetail(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO spvo = getSqlSpm(paramvo);
		List<FinanceDetailVO> retlist = (List<FinanceDetailVO>) multBodyObjectBO.queryDataPage(FinanceDetailVO.class,
				spvo.getSql(), spvo.getSpm(), paramvo.getPage(), paramvo.getRows(), null);
		if (retlist != null && retlist.size() > 0) {
			queryStatus(retlist, paramvo);
		}
		return retlist;
	}

	@Override
	public Integer queryDetailRow(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO spvo = getSqlSpm(paramvo);
		return multBodyObjectBO.queryDataTotal(QryParamVO.class, spvo.getSql(), spvo.getSpm());
	}

	/**
	 * 获取查询条件
	 * 
	 * @param paramvo
	 * @param uservo
	 * @return
	 */
	private QrySqlSpmVO getSqlSpm(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO spvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select innercode as corpkcode,  \n");
		sql.append("       unitname as corpkname,  \n");
		sql.append("       pk_corp as pk_corpk,  \n");
		sql.append("       ishasaccount as ishasaccount,  \n");
		sql.append("       begindate as period,  \n");
		sql.append("       fathercorp as pk_corp  \n");
		sql.append("  from bd_corp \n");
		sql.append(" where nvl(dr,0) = 0   \n");
		sql.append("   and nvl(isaccountcorp, 'N') = 'N'   \n");
		sql.append(" and fathercorp = ? \n");
		spm.addParam(paramvo.getPk_corp());
		sql.append(" and nvl(ishasaccount,'N') = 'Y' \n");
		sql.append(" and nvl(isseal,'N') = 'N' \n");

		sql.append(" order by innercode ");
		spvo.setSql(sql.toString());
		spvo.setSpm(spm);
		return spvo;
	}

	/**
	 * 状态查询
	 * 
	 * @param list
	 * @param paramvo
	 * @param uservo
	 */
	private void queryStatus(List<FinanceDetailVO> list, QryParamVO paramvo) throws DZFWarpException {
		List<String> pk_corplist = new ArrayList<>();
		CorpVO corpvo = null;
		String pk_corp = list.get(0).getPk_corp();
		for (FinanceDetailVO vo : list) {
			if (!pk_corplist.contains(vo.getPk_corpk())) {
				pk_corplist.add(vo.getPk_corpk());
			}
			vo.setCorpkname(CodeUtils1.deCode(vo.getCorpkname()));
			corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
			if (corpvo != null) {
				vo.setCorpname(corpvo.getUnitname());
			}
			vo.setVperiod(paramvo.getPeriod());
		}
		String status = null;
		String inSql = SqlUtil.buildSqlConditionForIn(pk_corplist.toArray(new String[0]));
		int qrytype = 1;// 查询月与当前月不相等
		String nperiod = DateUtils.getPeriod(new DZFDate());
		if (nperiod.equals(paramvo.getPeriod())) {
			qrytype = 2;// 查询月与当前月相等
		}

		HashMap<String, String> map = isQjsy(inSql, paramvo.getPeriod(), qrytype);// 查询是否期间损益
		HashMap<String, String> mapV = isVoucher(inSql, paramvo.getPeriod(), qrytype);// 查询是否填制凭证
		Map<String, String> gzmap = isGz(inSql, paramvo.getPeriod());
		Map<String, StringBuffer> deptmap = qryDeptMap(inSql, pk_corp);
		StringBuffer deptvalue = null;
		for (FinanceDetailVO vo : list) {
			//1、记账状态：
			status = queryJzStatus(vo.getPk_corpk(), paramvo.getPeriod(), map, mapV, qrytype);
			vo.setJzstatus(status);
			//2、账务检查以查询月是否关账来统计：
			if(gzmap != null && !gzmap.isEmpty()){
				if(!StringUtil.isEmpty(gzmap.get(vo.getPk_corp()))){
					vo.setIacctcheck(1);
				}else{
					vo.setIacctcheck(0);
				}
			}else{
				vo.setIacctcheck(0);
			}
			//3、客户所属部门：
			if(deptmap != null && !deptmap.isEmpty()){
				deptvalue = deptmap.get(vo.getPk_corpk());
				if(deptvalue != null && deptvalue.length() > 0){
					vo.setVdeptname(deptvalue.toString());
				}
			}
		}
	}
	
	/**
	 * 查询客户所属部门
	 * @param inSql
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, StringBuffer> qryDeptMap(String inSql, String pk_corp) throws DZFWarpException {
		Map<String, StringBuffer> deptmap = new HashMap<String, StringBuffer>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
        sql.append(" select corp.pk_corp as pk_corpk,wm_concat(distinct dept.deptname) as vdeptname ");
        sql.append(" from bd_corp corp");
        sql.append(" left join sm_user_corp uc on uc.pk_corpk = corp.pk_corp");
        sql.append(" left join sm_user su on su.cuserid = uc.cuserid");
        sql.append(" left join ynt_department dept on dept.pk_department = su.pk_department");
        sql.append(" where corp.fathercorp = ? ");
        spm.addParam(pk_corp);
		sql.append("   AND nvl(corp.dr, 0) = 0  \n");
		sql.append("   AND nvl(uc.dr, 0) = 0  \n");
		sql.append("   AND nvl(su.dr, 0) = 0  \n");
		sql.append("   AND nvl(dept.dr, 0) = 0  \n");
		if (!StringUtil.isEmpty(inSql)) {
			sql.append(" AND corp.pk_corp in (").append(inSql).append(")");
		}
		sql.append(" group by corp.pk_corp ");
		List<FinanceDetailVO> deptlist = (List<FinanceDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(FinanceDetailVO.class));
		StringBuffer value = null;
		if(deptlist != null && deptlist.size() > 0){
			for(FinanceDetailVO deptvo : deptlist){
				if(!deptmap.containsKey(deptvo.getPk_corpk())){
					value = new StringBuffer();
					value.append(deptvo.getVdeptname());
					deptmap.put(deptvo.getPk_corpk(), value);
				}else{
					value = deptmap.get(deptvo.getPk_corpk());
					if(value.indexOf(deptvo.getVdeptname()) != 0){
						value.append("，").append(deptvo.getVdeptname());
						deptmap.put(deptvo.getPk_corpk(), value);
					}
				}
			}
		}
		return deptmap;
	}

	/**
	 * 查询是否期间损益
	 * 
	 * @param inSql
	 * @param period
	 * @param qrytype
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, String> isQjsy(String inSql, String period, int qrytype) throws DZFWarpException {
		HashMap<String, String> map = new HashMap<>();
		StringBuffer qmclsql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(period);
		qmclsql.append(" select max(q.period) as vperiod,q.pk_corp as pk_corpk from ynt_qmcl q ");
		qmclsql.append(" where nvl(q.isqjsyjz,'N') = 'Y' and q.pk_corp in (").append(inSql).append(")");
		if (qrytype == 2) {
			qmclsql.append(" and q.period <= ? ");
		} else {
			qmclsql.append(" and q.period = ? ");
		}
		qmclsql.append(" group by q.pk_corp ");
		List<FinanceDetailVO> qmclvos = (List<FinanceDetailVO>) singleObjectBO.executeQuery(qmclsql.toString(), sp,
				new BeanListProcessor(FinanceDetailVO.class));
		if (qmclvos != null && qmclvos.size() > 0) {
			for (FinanceDetailVO vo : qmclvos) {
				String key = vo.getPk_corpk();
				map.put(key, vo.getVperiod());
			}
		}
		return map;
	}

	/**
	 * 查询是否填制凭证
	 * 
	 * @param inSql
	 * @param period
	 * @param qrytype
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> isVoucher(String inSql, String period, int qrytype) {
		HashMap<String, String> map = new HashMap<>();
		StringBuffer tzpzsql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(period);
		tzpzsql.append(" select max(pz.period) as vperiod,pz.pk_corp as pk_corpk from ynt_tzpz_h pz ");
		tzpzsql.append(" where nvl(pz.dr,0) =0 and pz.pk_corp in (").append(inSql).append(")");
		if (qrytype == 2) {
			tzpzsql.append(" and pz.period <= ? ");
		} else {
			tzpzsql.append(" and pz.period = ? ");
		}
		tzpzsql.append(" group by pz.pk_corp ");
		List<FinanceDetailVO> list = (List<FinanceDetailVO>) singleObjectBO.executeQuery(tzpzsql.toString(), sp,
				new BeanListProcessor(FinanceDetailVO.class));
		if (list != null && list.size() > 0) {
			for (FinanceDetailVO vo : list) {
				String key = vo.getPk_corpk();
				map.put(key, vo.getVperiod());
			}
		}
		return map;
	}

	/**
	 * 查询记账状态
	 * 
	 * @param pk_corp
	 * @param period
	 * @param map
	 *            查询是否期间损益
	 * @param mapV
	 *            填制凭证
	 * @param qrytype
	 * @return
	 */
	public String queryJzStatus(String pk_corp, String period, HashMap<String, String> map,
			HashMap<String, String> mapV, int qrytype) {
		if (period == null || pk_corp == null) {
			throw new BusinessException("查询失败");
		}
		StringBuffer strStatus = new StringBuffer();
		String jzPeriod = map.get(pk_corp);

		String vPeriod = mapV.get(pk_corp);
		CorpVO cvo = null;
		if (qrytype == 2) {
			if (StringUtil.isEmpty(jzPeriod) && StringUtil.isEmpty(vPeriod)) {
				cvo = CorpCache.getInstance().get(null, pk_corp);
				if (cvo != null && cvo.getBegindate() != null) {
					strStatus.append(DateUtils.getPeriod(cvo.getBegindate())).append("未开始");
				}
			} else if (StringUtil.isEmpty(jzPeriod) && !StringUtil.isEmpty(vPeriod)) {
				strStatus.append(vPeriod).append("进行中");
			} else if (!StringUtil.isEmpty(jzPeriod) && StringUtil.isEmpty(vPeriod)) {
				strStatus.append(jzPeriod).append("已完成");
			} else if (!StringUtil.isEmpty(jzPeriod) && !StringUtil.isEmpty(vPeriod)) {
				if (jzPeriod.compareTo(vPeriod) >= 0) {
					strStatus.append(jzPeriod).append("已完成");
				} else if (jzPeriod.compareTo(vPeriod) < 0) {
					strStatus.append(vPeriod).append("进行中");
				}
			}
		} else {
			if (StringUtil.isEmpty(jzPeriod) && StringUtil.isEmpty(vPeriod)) {
				cvo = CorpCache.getInstance().get(null, pk_corp);
				if (cvo != null && cvo.getBegindate() != null) {
					if (DateUtils.getPeriod(cvo.getBegindate()).compareTo(period) <= 0) {
						strStatus.append("未开始");
					} else {
						strStatus.append("--");
					}
				}
			} else if (StringUtil.isEmpty(jzPeriod) && !StringUtil.isEmpty(vPeriod)) {
				strStatus.append(vPeriod).append("进行中");
			} else if (!StringUtil.isEmpty(jzPeriod) && StringUtil.isEmpty(vPeriod)) {
				strStatus.append(jzPeriod).append("已完成");
			} else if (!StringUtil.isEmpty(jzPeriod) && !StringUtil.isEmpty(vPeriod)) {
				strStatus.append(jzPeriod).append("已完成");
			}
		}
		return strStatus.toString();
	}
	
	/**
	 * 查询是否关账
	 * @param inSql
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, String> isGz(String inSql, String period) throws DZFWarpException {
		HashMap<String, String> map = new HashMap<>();
		StringBuffer qmclsql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		qmclsql.append(" select max(q.period) as vperiod, q.pk_corp as pk_corpk from ynt_qmcl q ");
		qmclsql.append(" where nvl(q.isgz,'N') = 'Y' and q.pk_corp in (").append(inSql).append(")");
		//只关注查询月是否关账
		qmclsql.append(" and q.period = ? ");
		sp.addParam(period);
		qmclsql.append(" group by q.pk_corp ");
		List<FinanceDetailVO> qmclvos = (List<FinanceDetailVO>) singleObjectBO.executeQuery(qmclsql.toString(), sp,
				new BeanListProcessor(FinanceDetailVO.class));
		if (qmclvos != null && qmclvos.size() > 0) {
			for (FinanceDetailVO vo : qmclvos) {
				String key = vo.getPk_corpk();
				map.put(key, vo.getVperiod());
			}
		}
		return map;
	}
}
