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
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.report.CustCountVO;
import com.dzf.model.channel.report.DataVO;
import com.dzf.model.channel.report.FinanceDealStateRepVO;
import com.dzf.model.channel.report.FinanceDetailVO;
import com.dzf.model.jms.basicset.JMUserRoleVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
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
		    DZFDate begindate =DateUtils.getPeriodEndDate(pamvo.getPeriod());
			pamvo.setBegdate(begindate);
		}

		// 1、按照所选则的大区、省（市）、会计运营经理和当前登陆人过滤出符合条件的加盟商信息
		HashMap<String, DataVO> map = queryCorps(pamvo, FinanceDealStateRepVO.class);
		List<String> corplist = null;
		List<DataVO> corpvaluelist = null;
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
		// if (custmap == null || custmap.isEmpty()) {
		// return null;
		// }
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

	@SuppressWarnings("unchecked")
	@Override
	public List<FinanceDetailVO> queryDetail(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO spvo = getSqlSpm(pamvo);
		List<FinanceDetailVO> retlist = (List<FinanceDetailVO>) multBodyObjectBO.queryDataPage(FinanceDetailVO.class,
				spvo.getSql(), spvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		if (retlist != null && retlist.size() > 0) {
			return queryStatus(retlist, pamvo);
		}
		return null;
	}

	@Override
	public Integer queryDetailRow(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO spvo = getSqlSpm(pamvo);
		return multBodyObjectBO.queryDataTotal(QryParamVO.class, spvo.getSql(), spvo.getSpm());
	}

	/**
	 * 获取查询条件
	 * 
	 * @param pamvo
	 * @param uservo
	 * @return
	 */
	private QrySqlSpmVO getSqlSpm(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO spvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select innercode as corpkcode,  \n");
		sql.append("       unitname as corpkname,  \n");
		sql.append("       CASE chargedeptname  \n");
		sql.append("         WHEN '一般纳税人' THEN  \n");
		sql.append("          '一般人'  \n");
		sql.append("         WHEN '小规模纳税人' THEN  \n");
		sql.append("          '小规模'  \n");
		sql.append("         ELSE  \n");
		sql.append("          ''  \n");
		sql.append("       END AS chargedeptname,  \n");
		sql.append("       pk_corp as pk_corpk,  \n");
		sql.append("       ishasaccount as ishasaccount,  \n");
		sql.append("       createdate,  \n");
		sql.append("       begindate,  \n");
		sql.append("       fathercorp as pk_corp  \n");
		sql.append("  from bd_corp \n");
		sql.append(" where nvl(dr,0) = 0   \n");
		sql.append("   and fathercorp = ? \n");
		spm.addParam(pamvo.getPk_corp());
		sql.append("   AND nvl(ishasaccount,'N') = 'Y' \n");// 已建账
		sql.append("   AND nvl(isseal, 'N') = 'N'\n"); // 未封存客户
		sql.append("   AND nvl(isaccountcorp,'N') = 'N' \n");// 非分支机构

		sql.append(" order by innercode ");
		spvo.setSql(sql.toString());
		spvo.setSpm(spm);
		return spvo;
	}

	/**
	 * 状态查询
	 * 
	 * @param list
	 * @param pamvo
	 * @param uservo
	 */
	private List<FinanceDetailVO> queryStatus(List<FinanceDetailVO> list, QryParamVO pamvo) throws DZFWarpException {
		List<String> pk_corplist = new ArrayList<String>();

		List<FinanceDetailVO> retlist = null;
		if ((pamvo.getQrytype() != null && pamvo.getQrytype() != -1)
				|| (pamvo.getSeletype() != null && pamvo.getSeletype() != -1)) {
			retlist = new ArrayList<FinanceDetailVO>();
		}

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
			vo.setVperiod(pamvo.getPeriod());
		}
		String status = null;
		String inSql = SqlUtil.buildSqlConditionForIn(pk_corplist.toArray(new String[0]));
		if (StringUtil.isEmpty(inSql)) {
			return null;
		}
		int qrytype = 1;// 查询月与当前月不相等
		String nperiod = DateUtils.getPeriod(new DZFDate());
		if (nperiod.equals(pamvo.getPeriod())) {
			qrytype = 2;// 查询月与当前月相等
		}

		HashMap<String, String> map = isQjsy(inSql, pamvo.getPeriod(), qrytype);// 查询是否期间损益
		HashMap<String, String> mapV = isVoucher(inSql, pamvo.getPeriod(), qrytype);// 查询是否填制凭证
		Map<String, String> gzmap = isGz(inSql, pamvo.getPeriod());
		Map<String, String> deptmap = qryDeptMap(inSql, pk_corp);
		String deptvalue = null;
		for (FinanceDetailVO vo : list) {
			// 1、记账状态：
			status = queryJzStatus(vo.getPk_corpk(), pamvo.getPeriod(), map, mapV, qrytype);
			vo.setJzstatus(status);
			
			// 2、账务检查以查询月是否关账来统计：
			if (gzmap != null && !gzmap.isEmpty()) {
				if (!StringUtil.isEmpty(gzmap.get(vo.getPk_corpk()))) {
					vo.setVcheckstatus(pamvo.getPeriod() + "已关账");
				}
			}
			
			// 3、客户所属部门：
			if (deptmap != null && !deptmap.isEmpty()) {
				deptvalue = deptmap.get(vo.getPk_corpk());
				if (deptvalue != null && deptvalue.length() > 0) {
					vo.setVdeptname(deptvalue);
				}
			}
			
			//过滤数据
			if((pamvo.getQrytype() != null && pamvo.getQrytype() != -1)
					|| (pamvo.getSeletype() != null && pamvo.getSeletype() != -1)){
				filterData(pamvo, retlist, vo);
			}
		}
		
		if((pamvo.getQrytype() != null && pamvo.getQrytype() != -1)
				|| (pamvo.getSeletype() != null && pamvo.getSeletype() != -1)){
			return retlist;
		}else{
			return list;
		}
	}
	
	/**
	 * 过滤明细数据
	 * @param pamvo
	 * @param retlist
	 * @param vo
	 * @throws DZFWarpException
	 */
	private void filterData(QryParamVO pamvo, List<FinanceDetailVO> retlist, FinanceDetailVO vo) throws DZFWarpException {
		//记账状态过滤条件：1：未开始；2：进行中；3：已完成；
		if (pamvo.getQrytype() != null && pamvo.getQrytype() != -1) {
			if (pamvo.getQrytype() == 1 && vo.getJzstatus().indexOf("未开始") != -1) {
				//账务检查过滤状态：1：已关账；2：未关账；
				if (pamvo.getSeletype() != null && pamvo.getSeletype() != -1) {
					if (pamvo.getSeletype() == 1 && !StringUtil.isEmpty(vo.getVcheckstatus())
							&& vo.getVcheckstatus().indexOf("已关账") != -1) {
						retlist.add(vo);
					} else if (pamvo.getSeletype() == 2 && StringUtil.isEmpty(vo.getVcheckstatus())) {
						retlist.add(vo);
					}
				}else{
					retlist.add(vo);
				}
			} else if (pamvo.getQrytype() == 2 && vo.getJzstatus().indexOf("进行中") != -1) {
				//账务检查过滤状态：1：已关账；2：未关账；
				if (pamvo.getSeletype() != null && pamvo.getSeletype() != -1) {
					if (pamvo.getSeletype() == 1 && !StringUtil.isEmpty(vo.getVcheckstatus())
							&& vo.getVcheckstatus().indexOf("已关账") != -1) {
						retlist.add(vo);
					} else if (pamvo.getSeletype() == 2 && StringUtil.isEmpty(vo.getVcheckstatus())) {
						retlist.add(vo);
					}
				}else{
					retlist.add(vo);
				}
			} else if (pamvo.getQrytype() == 3 && vo.getJzstatus().indexOf("已完成") != -1) {
				//账务检查过滤状态：1：已关账；2：未关账；
				if (pamvo.getSeletype() != null && pamvo.getSeletype() != -1) {
					if (pamvo.getSeletype() == 1 && !StringUtil.isEmpty(vo.getVcheckstatus())
							&& vo.getVcheckstatus().indexOf("已关账") != -1) {
						retlist.add(vo);
					} else if (pamvo.getSeletype() == 2 && StringUtil.isEmpty(vo.getVcheckstatus())) {
						retlist.add(vo);
					}
				}else{
					retlist.add(vo);
				}
			}
		}else{
			//账务检查过滤状态：1：已关账；2：未关账；
			if (pamvo.getSeletype() != null && pamvo.getSeletype() != -1) {
				if (pamvo.getSeletype() == 1 && !StringUtil.isEmpty(vo.getVcheckstatus())
						&& vo.getVcheckstatus().indexOf("已关账") != -1) {
					retlist.add(vo);
				} else if (pamvo.getSeletype() == 2 && StringUtil.isEmpty(vo.getVcheckstatus())) {
					retlist.add(vo);
				}
			}
		}
	}

	/**
	 * 查询客户所属部门
	 * 
	 * @param inSql
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> qryDeptMap(String inSql, String pk_corp) throws DZFWarpException {
		Map<String, String> deptmap = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select distinct uc.pk_corpk as pk_corp, dept.deptname  \n");
		sql.append("  from sm_user_corp uc  \n");
		sql.append("  join sm_user su on su.cuserid = uc.cuserid  \n");
		sql.append("  join ynt_department dept on dept.pk_department = su.pk_department  \n");
		sql.append(" where uc.pk_corp = ?  \n");
		spm.addParam(pk_corp);
		sql.append("   and nvl(uc.dr, 0) = 0  \n");
		sql.append("   and nvl(su.dr, 0) = 0  \n");
		sql.append("   and uc.pk_corpk in (").append(inSql).append(")");
		List<JMUserRoleVO> collect = (List<JMUserRoleVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(JMUserRoleVO.class));
		if (collect != null && collect.size() > 0) {
			String deptname = null;
			String corpid = null;
			for (JMUserRoleVO vo : collect) {
				corpid = vo.getPk_corp();
				if (deptmap.containsKey(corpid)) {
					deptname = deptmap.get(corpid) + "," + vo.getDeptname();
					deptmap.remove(corpid);
					deptmap.put(corpid, deptname);
				} else {
					deptmap.put(corpid, vo.getDeptname());
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
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT max(period) AS vperiod, pk_corp AS pk_corpk  \n");
		sql.append("  FROM ynt_qmcl  \n");
		sql.append(" WHERE nvl(isqjsyjz, 'N') = 'Y'  \n");
		sql.append("   AND pk_corp in ( \n");
		sql.append(inSql).append(" ) \n");
		if (qrytype == 2) {
			sql.append(" AND period <= ? ");
		} else {
			sql.append(" AND period = ? ");
		}
		spm.addParam(period);
		sql.append(" GROUP BY pk_corp ");
		List<FinanceDetailVO> list = (List<FinanceDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
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
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(period);
		sql.append("SELECT max(period) AS vperiod, pk_corp AS pk_corpk  \n");
		sql.append("  FROM ynt_tzpz_h  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND pk_corp in ( \n");
		sql.append(inSql).append(" ) \n");
		if (qrytype == 2) {
			sql.append(" AND period <= ? ");
		} else {
			sql.append(" AND period = ? ");
		}
		sql.append(" GROUP BY pk_corp ");
		List<FinanceDetailVO> list = (List<FinanceDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
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
		String jzPeriod = "";
		if (map != null && !map.isEmpty()) {
			jzPeriod = map.get(pk_corp);
		}
		String vPeriod = "";
		if (mapV != null && !mapV.isEmpty()) {
			vPeriod = mapV.get(pk_corp);
		}
		CorpVO cvo = null;
		if (qrytype == 2) {
			if (StringUtil.isEmpty(jzPeriod) && StringUtil.isEmpty(vPeriod)) {
				cvo = queryCorp(pk_corp);
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
				cvo = queryCorp(pk_corp);
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
	 * 查询客户信息
	 * 
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private CorpVO queryCorp(String pk_corp) throws DZFWarpException {
		return (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
	}

	/**
	 * 查询是否关账
	 * 
	 * @param inSql
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, String> isGz(String inSql, String period) throws DZFWarpException {
		HashMap<String, String> map = new HashMap<>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT MAX(period) AS vperiod, pk_corp AS pk_corpk  \n");
		sql.append("  FROM ynt_qmcl  \n");
		sql.append(" WHERE nvl(isgz, 'N') = 'Y'  \n");
		sql.append("   AND pk_corp in ( \n");
		sql.append(inSql).append(" ) \n");
		// 只关注查询月是否关账
		sql.append(" AND period = ? ");
		spm.addParam(period);
		sql.append(" GROUP BY pk_corp ");
		List<FinanceDetailVO> list = (List<FinanceDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(FinanceDetailVO.class));
		if (list != null && list.size() > 0) {
			for (FinanceDetailVO vo : list) {
				String key = vo.getPk_corpk();
				map.put(key, vo.getVperiod());
			}
		}
		return map;
	}
}
