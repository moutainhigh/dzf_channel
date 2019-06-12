package com.dzf.service.branch.reportmanage.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.branch.reportmanage.CorpDataVO;
import com.dzf.model.pub.ComboBoxVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.branch.reportmanage.ICorpDataService;

@Service("corpdataser")
public class CorpDataServiceImpl implements ICorpDataService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	@SuppressWarnings("unchecked")
	@Override
	public List<ComboBoxVO> queryAccount(String cuserid) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT DISTINCT p.pk_corp AS id, p.unitname AS name  \n");
		sql.append("  FROM br_branchcorp p  \n");
		sql.append("  LEFT JOIN br_user_branch h ON h.pk_branchset = p.pk_branchset  \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(h.dr, 0) = 0  \n");
		sql.append("   AND nvl(p.isseal, 'N') = 'N'  \n");
		sql.append("   AND h.cuserid = ?  \n");
		spm.addParam(cuserid);
		List<ComboBoxVO> list = (List<ComboBoxVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ComboBoxVO.class));
		if (list != null && list.size() > 0) {
			List<ComboBoxVO> retlist = new ArrayList<ComboBoxVO>();
			ComboBoxVO bvo = new ComboBoxVO();
			bvo.setId("pk_all");
			bvo.setName("全部");
			retlist.add(bvo);
			retlist.addAll(list);
			return retlist;
		}
		return list;
	}

	@Override
	public Integer queryPcountTotal(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getPcountQry(pamvo.getCuserid(), true);
		return multBodyObjectBO.getDataTotal(qryvo.getSql(), qryvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ComboBoxVO> queryPcount(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getPcountQry(pamvo.getCuserid(), false);
		List<ComboBoxVO> list = (List<ComboBoxVO>) multBodyObjectBO.queryDataPage(ComboBoxVO.class, qryvo.getSql(),
				qryvo.getSpm(), pamvo.getPage(), pamvo.getRows(), "r.user_code");
		if (list != null && list.size() > 0) {
			QueryDeCodeUtils.decKeyUtils(new String[] { "name" }, list, 1);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ComboBoxVO> queryAllPcount(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getPcountQry(pamvo.getCuserid(), false);
		List<ComboBoxVO> list = (List<ComboBoxVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(ComboBoxVO.class));
		List<ComboBoxVO> retlist = new ArrayList<ComboBoxVO>();
		if (list != null && list.size() > 0) {
			QueryDeCodeUtils.decKeyUtils(new String[] { "name" }, list, 1);
			for (ComboBoxVO uvo : list) {
				if ((!StringUtil.isEmpty(uvo.getCode()) && uvo.getCode().indexOf(pamvo.getUser_code()) != -1)
						|| !StringUtil.isEmpty(uvo.getName()) && uvo.getName().indexOf(pamvo.getUser_code()) != -1) {
					retlist.add(uvo);
				}
			}
		}
		return retlist;
	}

	/**
	 * * 获取主办会计查询条件
	 * 
	 * @param cuserid
	 * @param isqrynum
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getPcountQry(String cuserid, boolean isqrynum) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		if (isqrynum) {
			sql.append(" SELECT count(DISTINCT p.vsuperaccount) \n");
		} else {
			sql.append("SELECT DISTINCT p.vsuperaccount AS id,  \n");
			sql.append("       r.user_code AS code, \n");
			sql.append("       r.user_name AS name \n");
		}
		sql.append("  FROM bd_corp p  \n");
		sql.append("  LEFT JOIN sm_user r ON p.vsuperaccount = r.cuserid  \n");
		sql.append("  LEFT JOIN br_branchcorp bp ON p.fathercorp = bp.pk_corp  \n");
		sql.append("  LEFT JOIN br_user_branch h ON h.pk_branchset = bp.pk_branchset  \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(r.dr, 0) = 0  \n");
		sql.append("   AND nvl(bp.dr, 0) = 0  \n");
		sql.append("   AND nvl(h.dr, 0) = 0  \n");
		sql.append("   AND nvl(bp.isseal, 'N') = 'N'  \n");
		sql.append("   AND nvl(p.isseal, 'N') = 'N'  \n");
		sql.append("   AND nvl(p.isaccountcorp, 'N') = 'N'  \n");
		sql.append("   AND p.isformal = 'Y'  \n");
		sql.append("   AND p.vsuperaccount is not null  \n");
		sql.append("   AND r.user_name is not null  \n");
		sql.append("   AND h.cuserid = ? \n");
		spm.addParam(cuserid);
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@Override
	public Integer queryTotal(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getTotalSqlSpm(pamvo, true);
		return multBodyObjectBO.getDataTotal(qryvo.getSql(), qryvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CorpDataVO> query(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getTotalSqlSpm(pamvo, false);
		List<CorpDataVO> list = (List<CorpDataVO>) multBodyObjectBO.queryDataPage(CorpDataVO.class, qryvo.getSql(),
				qryvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		if (list != null && list.size() > 0) {
			List<String> pklist = new ArrayList<String>();
			for (CorpDataVO dvo : list) {
				pklist.add(dvo.getPk_corp());
			}
			String[] corpks = null;
			if (pklist != null && pklist.size() > 0) {
				corpks = pklist.toArray(new String[0]);
			}
			DZFDate date = new DZFDate();
			String period = date.getYear() + date.getStrMonth();
			period = ToolsUtil.getPreNumsMonth(period, 1);
			return getReturnData(pamvo, corpks, period);
		}
		return null;
	}
	
	/**
	 * 获取返回数据
	 * @param pamvo
	 * @param corpks
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CorpDataVO> getReturnData(QryParamVO pamvo, String[] corpks, String period) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		// 1、列表查询字段：
		getQryColumn(sql);
		// 2、客户相关信息：
		getCorpSqlSpm(pamvo, corpks, sql, spm, period);
		// 3、合同主表信息：
		getContSqlSpm(pamvo, corpks, sql, spm);
		// 4、合同子表信息：
		getContbSqlSpm(pamvo, corpks, sql, spm);
		List<CorpDataVO> rlist = (List<CorpDataVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CorpDataVO.class));
		if (rlist != null && rlist.size() > 0) {
			String[] strs = new String[] { "corpname", "unitname", "pcountname", "phone2" };
			Map<String, String> jzmap = qryJzMap(corpks, period);
			Map<String, String> vmap = qryVouchMap(corpks, period);
			for (CorpDataVO cvo : rlist) {
				QueryDeCodeUtils.decKeyUtil(strs, cvo, 1);
				countJzStatue(cvo, jzmap, vmap, period);
				countSerMonth(cvo, corpks, period);
			}
		}
		return rlist;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CorpDataVO> queryAll(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getTotalSqlSpm(pamvo, false);
		List<CorpDataVO> list = (List<CorpDataVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(CorpDataVO.class));
		if(list != null && list.size() > 0){
			List<String> pklist = new ArrayList<String>();
			for (CorpDataVO dvo : list) {
				QueryDeCodeUtils.decKeyUtil(new String[]{"unitname"}, dvo, 1);
				if(!StringUtil.isEmpty(dvo.getUnitname()) && dvo.getUnitname().indexOf(pamvo.getCorpkname()) != -1){
					pklist.add(dvo.getPk_corp());
				}
			}
			
			String[] corpks = null;
			if (pklist != null && pklist.size() > 0) {
				corpks = pklist.toArray(new String[0]);
				corpks = getPagedPKs(corpks, pamvo.getPage(), pamvo.getRows());
			}
			
			DZFDate date = new DZFDate();
			String period = date.getYear() + date.getStrMonth();
			period = ToolsUtil.getPreNumsMonth(period, 1);
			
			return getReturnData(pamvo, corpks, period);
		}
		return null;
	}
	
	/**
	 * 分页
	 * @param pks
	 * @param page
	 * @param rows
	 * @return
	 * @throws DZFWarpException
	 */
	public String[] getPagedPKs(String[] pks, int page, int rows) throws DZFWarpException {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= pks.length) {// 防止endIndex数组越界
			endIndex = pks.length;
		}
		pks = Arrays.copyOfRange(pks, beginIndex, endIndex);
		return pks;
	}

	/**
	 * 计算记账状态、报税状态
	 * 
	 * @param cvo
	 * @param jzmap
	 * @param vmap
	 * @throws DZFWarpException
	 */
	private void countJzStatue(CorpDataVO cvo, Map<String, String> jzmap, Map<String, String> vmap, String period)
			throws DZFWarpException {
		if(cvo.getBegindate() != null){
			String jzperid = cvo.getBegindate().getYear() + "-" + cvo.getBegindate().getStrMonth();
			//记账状态
			if(jzmap != null && !jzmap.isEmpty() && !StringUtil.isEmpty(jzmap.get(cvo.getPk_corp()))){
				cvo.setVjzstatues("已完成");
			}else{
				if(vmap != null && !vmap.isEmpty() && !StringUtil.isEmpty(vmap.get(cvo.getPk_corp()))){
					cvo.setVjzstatues("进行中");
				}else{
					if(jzperid.compareTo(period) <= 0){
						cvo.setVjzstatues("未开始");
					}
				}
			}
			if(jzperid.compareTo(period) > 0){
				cvo.setVbsstatues("");
			}
		}else{
			cvo.setVbsstatues("");
		}
	}

	/**
	 * 损益结转
	 * 
	 * @param corpks
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> qryJzMap(String[] corpks, String period) throws DZFWarpException {
		Map<String, String> jzmap = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT DISTINCT pk_corp, period  \n");
		sql.append("  FROM ynt_qmcl  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND nvl(isqjsyjz, 'N') = 'Y' \n");
		sql.append("   AND period = ?  \n");
		spm.addParam(period);
		if (corpks != null && corpks.length > 0) {
			String where = SqlUtil.buildSqlForIn("pk_corp", corpks);
			sql.append(" AND ").append(where);
		}
		List<QryParamVO> list = (List<QryParamVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(QryParamVO.class));
		if (list != null && list.size() > 0) {
			for (QryParamVO jzvo : list) {
				jzmap.put(jzvo.getPk_corp(), jzvo.getPeriod());
			}
		}
		return jzmap;
	}

	/**
	 * 凭证信息
	 * 
	 * @param corpks
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> qryVouchMap(String[] corpks, String period) throws DZFWarpException {
		Map<String, String> vmap = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT DISTINCT pk_corp, period  \n");
		sql.append("  FROM ynt_tzpz_h  \n");
		sql.append(" WHERE nvl(dr, 0) = 0  \n");
		sql.append("   AND period = ?  \n");
		spm.addParam(period);
		if (corpks != null && corpks.length > 0) {
			String where = SqlUtil.buildSqlForIn("pk_corp", corpks);
			sql.append(" AND ").append(where);
		}
		List<QryParamVO> list = (List<QryParamVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(QryParamVO.class));
		if (list != null && list.size() > 0) {
			for (QryParamVO jzvo : list) {
				vmap.put(jzvo.getPk_corp(), jzvo.getPeriod());
			}
		}
		return vmap;
	}

	/**
	 * 计算服务余额/月
	 * 
	 * @param cvo
	 * @param corpks
	 * @param period
	 * @throws DZFWarpException
	 */
	private void countSerMonth(CorpDataVO cvo, String[] corpks, String period) throws DZFWarpException {
		// 计算服务余额/月 计算逻辑：
		// [(合同优惠 + 收款金额) - 账本费已收金额] / 月服务费 - [合同开始日期到当前服务器日期的月份数] + 1
		if (cvo.getNreceivemny() != null) {
			DZFDouble countmny = SafeCompute.add(cvo.getNyhmny(), cvo.getNreceivemny());
			countmny = SafeCompute.sub(countmny, cvo.getNbookmny());
			countmny = SafeCompute.div(countmny, cvo.getNmservicemny());
			int num = (int) Math.ceil(countmny.doubleValue());//只入不舍
			int cnum = ToolsUtil.getCyclenum(cvo.getDbegindate(), new DZFDate());
			int rnum = num - cnum + 1;
			if (rnum < 0) {
				rnum = 0;
			}
			cvo.setIsurplusmonth(rnum);
		}
	}

	/**
	 * 获取总客户数量
	 * 
	 * @param pamvo
	 * @param isqrynum
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getTotalSqlSpm(QryParamVO pamvo, boolean isqrynum) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		if (isqrynum) {
			sql.append("SELECT COUNT(DISTINCT c.pk_corp)  \n");
		} else {
			sql.append("SELECT DISTINCT c.pk_corp, c.unitname  \n");
		}
		sql.append("  FROM bd_corp c  \n");
		sql.append("  LEFT JOIN br_branchcorp p ON c.fathercorp = p.pk_corp  \n");
		sql.append("  LEFT JOIN br_user_branch h ON h.pk_branchset = p.pk_branchset  \n");
		sql.append(" WHERE nvl(c.dr, 0) = 0  \n");
		sql.append("   AND nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(h.dr, 0) = 0  \n");
		sql.append("   AND nvl(p.isseal, 'N') = 'N'  \n");
		sql.append("   AND nvl(c.isseal, 'N') = 'N'  \n");
		sql.append("   AND nvl(c.isaccountcorp, 'N') = 'N'  \n");
		sql.append("   AND c.isformal = 'Y'  \n");
		sql.append("   AND h.cuserid = ?  \n");
		spm.addParam(pamvo.getCuserid());
		if (pamvo.getBegdate() != null) {
			sql.append(" AND c.createdate >= ? \n");
			spm.addParam(pamvo.getBegdate());
		}
		if (pamvo.getEnddate() != null) {
			sql.append(" AND c.createdate <= ? \n");
			spm.addParam(pamvo.getEnddate());
		}
		if (!StringUtil.isEmpty(pamvo.getPk_corp()) && !"pk_all".equals(pamvo.getPk_corp())) {
			sql.append(" AND p.pk_corp = ? \n");
			spm.addParam(pamvo.getPk_corp());
		}
		if (!StringUtil.isEmpty(pamvo.getCorpkcode())) {
			sql.append(" AND c.innercode like ? \n");
			spm.addParam("%" + pamvo.getCorpkcode() + "%");
		}
		// 销售人员
		if (!StringUtil.isEmpty(pamvo.getUser_name())) {
			sql.append(" AND c.foreignname like ? \n");
			spm.addParam("%" + pamvo.getUser_name() + "%");
		}
		// 主办会计
		if (!StringUtil.isEmpty(pamvo.getVmanager())) {
			sql.append(" AND c.vsuperaccount = ? \n");
			spm.addParam(pamvo.getVmanager());
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	/**
	 * 获取综合查询字段
	 * 
	 * @param sql
	 * @throws DZFWarpException
	 */
	private void getQryColumn(StringBuffer sql) throws DZFWarpException {
		sql.append("SELECT corp.corpname,  \n");
		sql.append("       corp.unitcode,  \n");
		sql.append("       corp.unitname,  \n");
		sql.append("       corp.linkman2,  \n");
		sql.append("       corp.phone2,  \n");
		sql.append("       corp.citycounty,  \n");
		sql.append("       corp.chargedeptname,  \n");
		sql.append("       corp.begindate,  \n");
		sql.append("       corp.pcountname,  \n");
		sql.append("       corp.foreignname,  \n");
		sql.append("       corp.createdate,  \n");
		sql.append("       corp.vbsstatues,  \n");
		sql.append("       corp.pk_corp,  \n");

		sql.append("       cont.dbegindate,  \n");
		sql.append("       cont.denddate,  \n");
		sql.append("       cont.ntotalmny,  \n");
		sql.append("       cont.nreceivemny,  \n");
		sql.append("       cont.nmservicemny, \n");

		sql.append("       contb.nbookmny, \n");
		sql.append("       contb.nyhmny \n");
		sql.append("  FROM  \n");
	}

	/**
	 * 获取客户相关信息
	 * 
	 * @param pamvo
	 * @param corpks
	 * @param sql
	 * @param spm
	 * @throws DZFWarpException
	 */
	private void getCorpSqlSpm(QryParamVO pamvo, String[] corpks, StringBuffer sql, SQLParameter spm, String period)
			throws DZFWarpException {
		sql.append("(SELECT t.unitname AS corpname,  \n");
		sql.append("       p.innercode AS unitcode,  \n");
		sql.append("       p.unitname,  \n");
		sql.append("       p.pk_corp,  \n");
		sql.append("       p.linkman2,  \n");
		sql.append("       p.phone2,  \n");
		sql.append("       a.region_name || b.region_name || c.region_name AS citycounty,  \n");
		sql.append("       CASE p.chargedeptname  \n");
		sql.append("         WHEN '小规模纳税人' THEN  \n");
		sql.append("          '小规模'  \n");
		sql.append("         WHEN '一般纳税人' THEN  \n");
		sql.append("          '一般人'  \n");
		sql.append("         ELSE  \n");
		sql.append("          ''  \n");
		sql.append("       END AS chargedeptname,  \n");
		sql.append("       p.begindate,  \n");
		sql.append("       r.user_name AS pcountname,  \n");
		sql.append("       p.foreignname,  \n");
		sql.append("       p.createdate,  \n");
		sql.append("       CASE n.taxStateFinish  \n");
		sql.append("         WHEN 1 THEN  \n");
		sql.append("          '已完成'  \n");
		sql.append("         ELSE  \n");
		sql.append("          '未完成'  \n");
		sql.append("       END AS vbsstatues  \n");
		sql.append("  FROM bd_corp p  \n");
		sql.append("  LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp  \n");
		sql.append("  LEFT JOIN sm_user r ON p.vsuperaccount = r.cuserid  \n");
		sql.append("  LEFT JOIN ynt_area a ON p.vprovince = a.region_id  \n");
		sql.append("  LEFT JOIN ynt_area b ON p.vcity = b.region_id  \n");
		sql.append("  LEFT JOIN ynt_area c ON p.varea = c.region_id  \n");
		sql.append("  LEFT JOIN nsworkbench n ON p.pk_corp = n.pk_corp  \n");
		sql.append("                         AND n.period = ?  \n");
		spm.addParam(period);
		sql.append(" WHERE nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(r.dr, 0) = 0  \n");
		sql.append("   AND nvl(a.dr, 0) = 0  \n");
		sql.append("   AND nvl(b.dr, 0) = 0  \n");
		sql.append("   AND nvl(c.dr, 0) = 0  \n");
		sql.append("   AND nvl(p.isseal, 'N') = 'N'  \n");
		sql.append("   AND nvl(p.isaccountcorp, 'N') = 'N'  \n");
		sql.append("   AND p.isformal = 'Y'  \n");
		if (corpks != null && corpks.length > 0) {
			String where = SqlUtil.buildSqlForIn("p.pk_corp", corpks);
			sql.append(" AND ").append(where);
		}
		if(!"全部".equals(pamvo.getVbillcode())){
			if("未完成".equals(pamvo.getVbillcode())){
				sql.append(" AND nvl(n.taxStateFinish,0) = 0 \n");
			}else if("已完成".equals(pamvo.getVbillcode())){
				sql.append(" AND n.taxStateFinish = 1 \n");
			}
		}
		sql.append(" ) corp \n");
	}

	/**
	 * 获取合同主表相关信息
	 * 
	 * @param pamvo
	 * @param corpks
	 * @param sql
	 * @param spm
	 * @throws DZFWarpException
	 */
	private void getContSqlSpm(QryParamVO pamvo, String[] corpks, StringBuffer sql, SQLParameter spm)
			throws DZFWarpException {
		sql.append(" LEFT JOIN \n");
		sql.append("(SELECT ct.pk_corpk AS pk_corp,  \n");
		sql.append("       ct.pk_contract,  \n");
		sql.append("       ct.dbegindate,  \n");
		sql.append("       ct.denddate,  \n");
		sql.append("       ct.ntotalmny,  \n");
		sql.append("       ct.nmservicemny,  \n");
		sql.append("       ct.nreceivemny  \n");
		sql.append("  FROM (SELECT ROW_NUMBER() OVER(PARTITION BY pk_corpk ORDER BY ts DESC) rn,  \n");
		sql.append("               t.pk_corpk,  \n");
		sql.append("               t.pk_contract,  \n");
		sql.append("               t.dbegindate,  \n");
		sql.append("               t.denddate,  \n");
		sql.append("               nvl(t.ntotalmny,0) AS ntotalmny,  \n");
		sql.append("               nvl(t.nmservicemny,0) AS nmservicemny,  \n");
		sql.append("               nvl(t.nreceivemny,0) AS nreceivemny \n");
		sql.append("          FROM ynt_contract t  \n");
		sql.append("         WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("           AND t.isflag = 'Y'  \n");
		sql.append("           AND t.vstatus IN (1, 3, 4)  \n");
		sql.append("           AND t.icosttype = 0  \n");
		sql.append("   AND t.dbegindate <= ?  \n");
		spm.addParam(new DZFDate());
		sql.append("   AND t.denddate >= ?  \n");
		spm.addParam(new DZFDate());
		if (corpks != null && corpks.length > 0) {
			String where = SqlUtil.buildSqlForIn("t.pk_corpk", corpks);
			sql.append(" AND ").append(where);
		}
		sql.append("                               ) ct  \n");
		sql.append(" where ct.rn = 1 ) cont \n");
		sql.append(" ON corp.pk_corp = cont.pk_corp \n");
	}

	/**
	 * 获取合同子表相关信息
	 * 
	 * @param pamvo
	 * @param corpks
	 * @param sql
	 * @param spm
	 * @throws DZFWarpException
	 */
	private void getContbSqlSpm(QryParamVO pamvo, String[] corpks, StringBuffer sql, SQLParameter spm)
			throws DZFWarpException {
		sql.append("LEFT JOIN \n");
		sql.append("(SELECT t.pk_contract,  \n");
		sql.append("       SUM(CASE b.icosttypecode  \n");
		sql.append("             WHEN '0102' THEN  \n");
		sql.append("              nvl(b.ysreceivemny, 0)  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS nbookmny,  \n");
		sql.append("       SUM(nvl(b.nyhmny,0)) AS nyhmny \n");
		sql.append("  FROM ynt_contract t  \n");
		sql.append("  LEFT JOIN ynt_contract_b b ON t.pk_contract = b.pk_contract  \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(b.dr, 0) = 0  \n");
		sql.append("   AND t.isflag = 'Y'  \n");
		sql.append("   AND t.vstatus IN (1, 3, 4)  \n");
		sql.append("   AND t.icosttype = 0  \n");
		sql.append("   AND t.dbegindate <= ?  \n");
		spm.addParam(new DZFDate());
		sql.append("   AND t.denddate >= ?  \n");
		spm.addParam(new DZFDate());
		if (corpks != null && corpks.length > 0) {
			String where = SqlUtil.buildSqlForIn("t.pk_corpk", corpks);
			sql.append(" AND ").append(where);
		}
		sql.append(" GROUP BY t.pk_contract) contb  \n");
		sql.append(" ON cont.pk_contract = contb.pk_contract \n");
	}
}
