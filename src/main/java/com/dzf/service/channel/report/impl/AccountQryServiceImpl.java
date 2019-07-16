package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.ArrayListProcessor;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.report.FinanceDetailVO;
import com.dzf.model.jms.basicset.JMUserRoleVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IAccountQryService;

@Service("accountqryser")
public class AccountQryServiceImpl implements IAccountQryService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@Override
	public Integer queryTotalRow(QryParamVO pamvo, UserVO uvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getCorpInfo(pamvo, uvo, true);
		return multBodyObjectBO.getDataTotal(qryvo.getSql(), qryvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FinanceDetailVO> query(QryParamVO pamvo, UserVO uvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getCorpInfo(pamvo, uvo, false);
		List<FinanceDetailVO> list = (List<FinanceDetailVO>) multBodyObjectBO.queryDataPage(FinanceDetailVO.class,
				qryvo.getSql(), qryvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		if(list != null && list.size() > 0){
			List<String> pklist = new ArrayList<String>();
			for(FinanceDetailVO avo : list){
				pklist.add(avo.getPk_corpk());
			}
			String[] pks = null;
			if(pklist != null && pklist.size() > 0){
				pks = pklist.toArray(new String[0]);
			}else{
				return null;
			}
			return getReturnData(pamvo, pks, uvo);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<FinanceDetailVO> getReturnData(QryParamVO pamvo, String[] pks, UserVO uvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		
		//1、获取查询列
		getQryColumn(sql, spm, pamvo);
		//2、查询客户相关信息
		getCorpSqlSpm(pamvo, pks, sql, spm, uvo);
		//3、查询记账状态
		getJzztSqlSpm(pamvo, pks, sql, spm, uvo);
		//4、账务检查状态
		getZwjcSqlSpm(pamvo, pks, sql, spm, uvo);
		sql.append(" ORDER BY qcorp.corpkcode ");
		List<FinanceDetailVO> rlist = (List<FinanceDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(FinanceDetailVO.class));
		if(rlist != null && rlist.size() > 0){
			return setShowName(rlist, pamvo, pks);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FinanceDetailVO> queryAllData(QryParamVO pamvo, UserVO uvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		
		//0、获取全部数据
		getAllColumn(sql);
		//1、获取查询列
		getQryColumn(sql, spm, pamvo);
		//2、查询客户相关信息
		getCorpSqlSpm(pamvo, null, sql, spm, uvo);
		//3、查询记账状态
		getJzztSqlSpm(pamvo, null, sql, spm, uvo);
		//4、账务检查状态
		getZwjcSqlSpm(pamvo, null, sql, spm, uvo);
		//5、获取全部数据过滤条件
		getAllFilter(sql, spm, pamvo);
		List<FinanceDetailVO> rlist = (List<FinanceDetailVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(FinanceDetailVO.class));
		if(rlist != null && rlist.size() > 0){
			return setShowName(rlist, pamvo, null);
		}
		return null;
	}
	
	/**
	 * 获取全部数据过滤条件
	 * @param sql
	 * @param spm
	 * @param pamvo
	 * @throws DZFWarpException
	 */
	private void getAllFilter(StringBuffer sql, SQLParameter spm, QryParamVO pamvo) throws DZFWarpException {
		sql.append(" ) d_all   ");
		sql.append(" WHERE d_all.pk_corpk is not null   ");
		//记账状态
		if(!"全部".equals(pamvo.getVmanager())){
			sql.append(" AND d_all.jzstatus like ?   ");
			spm.addParam("%"+pamvo.getVmanager()+"%");
		}
		//账务检查
		if(!"全部".equals(pamvo.getVbillcode())){
			if("已关账".equals(pamvo.getVbillcode())){
				sql.append(" AND d_all.vcheckstatus like ?   ");
				spm.addParam("%"+pamvo.getVbillcode()+"%");
			}else{
				sql.append(" AND d_all.vcheckstatus IS NULL   ");
			}
		}
		
		sql.append(" order by d_all.corpkcode ");
	}
	
	/**
	 * 获取全部数据
	 * @param sql
	 * @throws DZFWarpException
	 */
	private void getAllColumn(StringBuffer sql) throws DZFWarpException {
		sql.append("SELECT d_all.corpkcode,    ");
		sql.append("       d_all.corpkname,    ");
		sql.append("       d_all.chargedeptname,    ");
		sql.append("       d_all.pk_corp,    ");
		sql.append("       d_all.pk_corpk,    ");
		sql.append("       d_all.createdate,    ");
		sql.append("       d_all.begindate,    ");
		//记账状态
		sql.append("       d_all.jzstatus,   ");
		//账务检查
		sql.append("       d_all.vcheckstatus   ");
		sql.append(" FROM  (   ");
	}
	
	/**
	 * 设置显示名称
	 * @param rlist
	 * @param pamvo
	 * @param pks
	 * @throws DZFWarpException
	 */
	private List<FinanceDetailVO> setShowName(List<FinanceDetailVO> rlist, QryParamVO pamvo, String[] pks)
			throws DZFWarpException {
		Map<String, String> dmap = qryDeptMap(pamvo, pks);
		List<FinanceDetailVO> flist = null;
		if (!StringUtil.isEmpty(pamvo.getCorpkname())) {
			flist = new ArrayList<FinanceDetailVO>();
		}
		String dname = null;
		String corpname = "";
		CorpVO corpvo = CorpCache.getInstance().get(null, pamvo.getPk_corp());
		if (corpvo != null) {
			corpname = corpvo.getUnitname();
		}
		for (FinanceDetailVO avo : rlist) {
			QueryDeCodeUtils.decKeyUtil(new String[] { "corpkname" }, avo, 1);
			if (dmap != null && !dmap.isEmpty()) {
				dname = dmap.get(avo.getPk_corpk());
				if (!StringUtil.isEmpty(dname)) {
					avo.setVdeptname(dname);
				}
			}
			avo.setCorpname(corpname);
			avo.setVperiod(pamvo.getPeriod());
			if (!StringUtil.isEmpty(pamvo.getCorpkname())) {
				if ((!StringUtil.isEmpty(avo.getCorpkcode()) && avo.getCorpkcode().indexOf(pamvo.getCorpkname()) != -1)
						|| (!StringUtil.isEmpty(avo.getCorpkname())
								&& avo.getCorpkname().indexOf(pamvo.getCorpkname()) != -1)) {
					flist.add(avo);
				}
			}
		}
		if (!StringUtil.isEmpty(pamvo.getCorpkname())) {
			return flist;
		} else {
			return rlist;
		}
	}
	
	/**
	 * 查询客户所属部门
	 * @param pamvo
	 * @param pks
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> qryDeptMap(QryParamVO pamvo, String[] pks) throws DZFWarpException {
		Map<String, String> deptmap = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select distinct uc.pk_corpk as pk_corp, dept.deptname    ");
		sql.append("  from sm_user_corp uc    ");
		sql.append("  join sm_user su on su.cuserid = uc.cuserid    ");
		sql.append("  join ynt_department dept on dept.pk_department = su.pk_department    ");
		sql.append(" where nvl(uc.dr, 0) = 0   ");
		sql.append("   and nvl(su.dr, 0) = 0    ");
		sql.append("   and uc.pk_corp = ?    ");
		spm.addParam(pamvo.getPk_corp());
		if(pks != null && pks.length > 0){
			String where = SqlUtil.buildSqlForIn("uc.pk_corpk", pks);
			sql.append(" AND ").append(where);
		}
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
	 * 查询账务检查状态
	 * @param pamvo
	 * @param pks
	 * @param sql
	 * @param spm
	 * @param uvo
	 * @throws DZFWarpException
	 */
	private void getZwjcSqlSpm(QryParamVO pamvo, String[] pks, StringBuffer sql, SQLParameter spm, UserVO uvo)
			throws DZFWarpException {
		sql.append(" LEFT JOIN (   ");
		sql.append("SELECT q.pk_corp, q.period    ");
		sql.append("  FROM ynt_qmcl q    ");
		sql.append("  JOIN bd_corp corp on corp.pk_corp = q.pk_corp    ");
		sql.append(" WHERE q.isgz = 'Y'    ");
		sql.append("   AND q.period = ?    ");
		spm.addParam(pamvo.getPeriod());
		if (pks != null && pks.length > 0) {
			String where = SqlUtil.buildSqlForIn(" q.pk_corp ", pks);
			sql.append(" AND ").append(where);
		} else {
			// 添加过滤查询条件
			addSqlParam(pamvo, sql, spm);
		}
		sql.append(" )  zwjc   ");
		sql.append(" ON qcorp.pk_corp = zwjc.pk_corp   ");
	}
	
	/**
	 * 查询记账状态
	 * @param pamvo
	 * @param pks
	 * @param sql
	 * @param spm
	 * @param uvo
	 * @throws DZFWarpException
	 */
	private void getJzztSqlSpm(QryParamVO pamvo, String[] pks, StringBuffer sql, SQLParameter spm,
			UserVO uvo) throws DZFWarpException {
		sql.append(" LEFT JOIN (   ");
		sql.append("SELECT q.pk_corp,     ");
		sql.append(" q.period as period   ");
		sql.append("  FROM ynt_qmcl q    ");
		sql.append("  JOIN bd_corp corp ON corp.pk_corp = q.pk_corp    ");
		sql.append(" WHERE q.isqjsyjz = 'Y'   ");
		sql.append(" AND q.period = ? ");
		spm.addParam(pamvo.getPeriod());
		if (pks != null && pks.length > 0) {
			String where = SqlUtil.buildSqlForIn(" q.pk_corp ", pks);
			sql.append(" AND ").append(where);
		} else {
			// 添加过滤查询条件
			addSqlParam(pamvo, sql, spm);
		}
		sql.append("  ) jzzt   ");
		sql.append(" ON qcorp.pk_corp = jzzt.pk_corp    ");
	}
	
	/**
	 * 查询客户相关信息
	 * @param pamvo
	 * @param pks
	 * @param sql
	 * @param spm
	 * @param ischannel
	 * @throws DZFWarpException
	 */
	private void getCorpSqlSpm(QryParamVO pamvo, String[] pks, StringBuffer sql, SQLParameter spm,
			UserVO uvo) throws DZFWarpException {
		sql.append("(SELECT corp.innercode as corpkcode,    ");
		sql.append("       corp.unitname as corpkname,    ");
		sql.append("       CASE corp.chargedeptname    ");
		sql.append("         WHEN '一般纳税人' THEN    ");
		sql.append("          '一般人'    ");
		sql.append("         WHEN '小规模纳税人' THEN    ");
		sql.append("          '小规模'    ");
		sql.append("         ELSE    ");
		sql.append("          ''    ");
		sql.append("       END AS chargedeptname,    ");
		sql.append("       corp.pk_corp,    ");
		sql.append("       corp.createdate,    ");
		sql.append("       corp.begindate,    ");
		sql.append("       corp.fathercorp    ");
		sql.append("  from bd_corp corp   ");
		sql.append(" where nvl(corp.dr,0) = 0     ");
		sql.append("   and corp.fathercorp = ?   ");
		spm.addParam(pamvo.getPk_corp());
		
		if (pks != null && pks.length > 0) {
			String where = SqlUtil.buildSqlForIn(" corp.pk_corp ", pks);
			sql.append(" AND ").append(where);
		} else {
			// 添加过滤查询条件
			addSqlParam(pamvo, sql, spm);
		}
		sql.append(" ) qcorp   ");
	}
	
	/**
	 * 获取查询列
	 * @param sql
	 * @param spm
	 * @param qrytype
	 * @param pamvo
	 * @param ischannel
	 * @throws DZFWarpException
	 */
	private void getQryColumn(StringBuffer sql, SQLParameter spm, QryParamVO pamvo) throws DZFWarpException {
		sql.append("SELECT qcorp.corpkcode,    ");
		sql.append("       qcorp.corpkname,    ");
		sql.append("       qcorp.chargedeptname,    ");
		sql.append("       qcorp.pk_corp AS pk_corpk,    ");
		sql.append("       qcorp.fathercorp AS pk_corp,    ");
		sql.append("       qcorp.createdate,    ");
		sql.append("       qcorp.begindate,    ");
		//1、记账状态
		sql.append("       CASE WHEN jzzt.period IS NOT NULL THEN    ");
		sql.append("            jzzt.period || '已完成'   ");
		//查询月
		sql.append("            WHEN jzzt.period IS NULL AND substr(qcorp.begindate,0,7) <= ? THEN   ");
		spm.addParam(pamvo.getPeriod());
		sql.append("'").append(pamvo.getPeriod()).append("'").append(" || '未完成'");
		sql.append("       END AS jzstatus,   ");
		
		//2、账务检查
		sql.append("       CASE WHEN zwjc.period IS NOT NULL THEN    ");
		sql.append("            zwjc.period || '已关账'   ");
		sql.append("            ELSE '' END AS vcheckstatus   ");
		
		sql.append(" FROM   ");
	}
	
	/**
	 * 获取客户过滤条件
	 * @param pamvo
	 * @param uvo
	 * @param isqrynum
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getCorpInfo(QryParamVO pamvo, UserVO uvo, boolean isqrynum) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		if(isqrynum){
			sql.append("SELECT COUNT(pk_corp)   ");
		}else{
			sql.append("SELECT pk_corp AS pk_corpk    ") ; 
		}
		sql.append("  FROM bd_corp   ");
		sql.append(" WHERE nvl(dr,0) = 0     ");
		sql.append("   AND fathercorp = ?   ");
		spm.addParam(pamvo.getPk_corp());
		sql.append("   AND nvl(ishasaccount,'N') = 'Y'   ");// 已建账
		sql.append("   AND nvl(isseal, 'N') = 'N'  "); // 未封存客户
		sql.append("   AND nvl(isaccountcorp,'N') = 'N'   ");// 非分支机构
		sql.append(" order by innercode ");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}	
	
	/**
	 * 追加查询条件
	 * @param pamvo
	 * @param sql
	 * @param spm
	 * @throws DZFWarpException
	 */
	private void addSqlParam(QryParamVO pamvo, StringBuffer sql, SQLParameter spm) throws DZFWarpException {
        sql.append(" and nvl(corp.dr,0) = 0     ");
		sql.append("   AND nvl(corp.ishasaccount,'N') = 'Y'   ");// 已建账
		sql.append("   AND nvl(corp.isseal, 'N') = 'N'  "); // 未封存客户
		sql.append("   AND nvl(corp.isaccountcorp,'N') = 'N'   ");// 非分支机构
        sql.append(" and corp.fathercorp = ?     ");
        spm.addParam(pamvo.getPk_corp());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> queryJzMap(String[] corpks, String period) throws DZFWarpException {
		Map<String, String> jzmap = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT pk_corp,    ");
		sql.append("       CASE    ");
		sql.append("         WHEN jzperiod IS NOT NULL THEN    ");
		sql.append("          '已完成'    ");
		sql.append("         WHEN jzperiod IS NULL AND substr(begindate, 0, 7) <= ? THEN    ");
		spm.addParam(period);
		sql.append("          '未完成'    ");
		sql.append("       END AS jzstatus    ");
		sql.append("  FROM (SELECT corp.pk_corp,    ");
		sql.append("               q.period AS jzperiod,    ");
		sql.append("               corp.begindate AS begindate    ");
		sql.append("          FROM bd_corp corp    ");
		sql.append("          LEFT JOIN ynt_qmcl q ON corp.pk_corp = q.pk_corp    ");
		sql.append("                              AND q.isqjsyjz = 'Y'    ");
		sql.append("                              AND q.period = ?    ");
		spm.addParam(period);
		sql.append("         WHERE nvl(corp.dr, 0) = 0    ");
		sql.append("           AND nvl(corp.isaccountcorp, 'N') = 'N'    ");
		sql.append("           AND nvl(corp.isseal, 'N') = 'N'    ");
		if(corpks != null && corpks.length > 0){
			String where = SqlUtil.buildSqlForIn("corp.pk_corp", corpks);
			sql.append(" AND ").append(where);
		}else{
			return null;
		}
		sql.append(" )   ");
		List<Object> list = (List<Object>)singleObjectBO.executeQuery(sql.toString(), spm, new ArrayListProcessor());
		if(list != null && list.size() > 0){
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[]) list.get(i);
				if(!StringUtil.isEmpty(String.valueOf(obj[1]))){
					jzmap.put(String.valueOf(obj[0]), String.valueOf(obj[1]));
				}
			}
		}
		return jzmap;
	}

}
