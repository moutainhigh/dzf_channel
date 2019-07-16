package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
import java.util.List;

import com.dzf.pub.util.QueryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.dao.multbs.MultBodyObjectBO;
import com.dzf.model.channel.report.DataAnalysisVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.QueryDeCodeUtils;
import com.dzf.pub.StringUtil;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IDataAnalysisService;

@Service("dataanalysissser")
public class DataAnalysisServiceImpl implements IDataAnalysisService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private MultBodyObjectBO multBodyObjectBO;

	private String filtersql = QueryUtil.getWhereSql();

	@Override
	public Integer queryTotalRow(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getTotalSqlSpm(pamvo);
		return multBodyObjectBO.getDataTotal(qryvo.getSql(), qryvo.getSpm());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DataAnalysisVO> query(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO fqryvo = getFilterSql(pamvo);
		List<DataAnalysisVO> list = (List<DataAnalysisVO>) multBodyObjectBO.queryDataPage(DataAnalysisVO.class, 
				fqryvo.getSql(), fqryvo.getSpm(), pamvo.getPage(), pamvo.getRows(), null);
		if(list != null && list.size() > 0){
			List<String> pklist = new ArrayList<String>();
			for(DataAnalysisVO dvo : list){
				pklist.add(dvo.getPk_corp());
			}
			String[] corpks = null;
			if(pklist != null && pklist.size() > 0){
				corpks = pklist.toArray(new String[0]);
			}
			StringBuffer sql = new StringBuffer();
			SQLParameter spm = new SQLParameter();
			//1、列表查询字段：
			getQryColumn(sql);
			//2、会计公司相关信息：
			getAccSqlSpm(pamvo, corpks, sql, spm);
			//3、公司相关信息：
			getCorpSqlSpm(pamvo, corpks, sql, spm);
			//4、合同相关信息：
			getContSqlSpm(pamvo, corpks, sql, spm);
			//5、余额相关信息
			getPaySqlSpm(pamvo, corpks, sql, spm);
			//6、商品个购买相关信息：
			getBuySqlSpm(pamvo, corpks, sql, spm);
			//7、用户相关信息：
			getUserSqlSpm(pamvo, corpks, sql, spm);
			List<DataAnalysisVO> rlist = (List<DataAnalysisVO>) singleObjectBO.executeQuery(sql.toString(), spm,
					new BeanListProcessor(DataAnalysisVO.class));
			if(rlist != null && rlist.size() > 0){
				QueryDeCodeUtils.decKeyUtils(new String[]{"corpname"}, rlist, 1);
				return rlist;
			}
		}
		return null;
	}
	
	/**
	 * 获取总行数查询语句
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getTotalSqlSpm(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT count(DISTINCT account.pk_corp)    ");
		sql.append("  FROM bd_account account    ");
		sql.append("  LEFT JOIN cn_chnarea_b b ON account.vprovince = b.vprovince    ");
		sql.append("  LEFT JOIN cn_chnarea a ON b.pk_chnarea = a.pk_chnarea    ");
		sql.append(" WHERE ").append(filtersql);
		sql.append("   AND nvl(b.dr, 0) = 0    ");
		sql.append("   AND b.type = 1    ");
		if(!StringUtil.isEmpty(pamvo.getPk_corp())){
		    String[] strs = pamvo.getPk_corp().split(",");
		    String inSql = SqlUtil.buildSqlConditionForIn(strs);
		    sql.append(" AND account.pk_corp in (").append(inSql).append(")");
		}
		if(!StringUtil.isEmpty(pamvo.getAreaname())){
			sql.append(" AND a.areacode = ?   ");
			spm.addParam(pamvo.getAreaname());
		}
		if(!StringUtil.isEmpty(pamvo.getBeginperiod())){
			sql.append(" AND account.djoindate >= ?   ");
			spm.addParam(pamvo.getBeginperiod());
		}
		if(!StringUtil.isEmpty(pamvo.getEndperiod())){
			sql.append(" AND account.djoindate <= ?   ");
			spm.addParam(pamvo.getEndperiod());
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**
	 * 获取业务数据查询的会计公司过滤
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getFilterSql(QryParamVO pamvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT DISTINCT account.pk_corp    ");
		sql.append("  FROM bd_account account    ");
		sql.append("  LEFT JOIN cn_chnarea_b b ON account.vprovince = b.vprovince    ");
		sql.append("  LEFT JOIN cn_chnarea a ON b.pk_chnarea = a.pk_chnarea    ");
		sql.append(" WHERE ").append(filtersql);
		sql.append("   AND nvl(b.dr, 0) = 0    ");
		sql.append("   AND b.type = 1    ");
		if (!StringUtil.isEmpty(pamvo.getPk_corp())) {
			String[] strs = pamvo.getPk_corp().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" AND account.pk_corp in (").append(inSql).append(")");
		}
		if (!StringUtil.isEmpty(pamvo.getAreaname())) {
			sql.append(" AND a.areacode = ?   ");
			spm.addParam(pamvo.getAreaname());
		}
		if (!StringUtil.isEmpty(pamvo.getBeginperiod())) {
			sql.append(" AND account.djoindate >= ?   ");
			spm.addParam(pamvo.getBeginperiod());
		}
		if (!StringUtil.isEmpty(pamvo.getEndperiod())) {
			sql.append(" AND account.djoindate <= ?   ");
			spm.addParam(pamvo.getEndperiod());
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**
	 * 获取综合查询字段
	 * @param sql
	 * @throws DZFWarpException
	 */
	private void getQryColumn(StringBuffer sql) throws DZFWarpException {
		sql.append("SELECT DISTINCT acc.pk_corp,    ");
		sql.append("       acc.corpname,    ");
		sql.append("       acc.djoindate,    ");
		sql.append("       acc.drelievedate,    ");
		sql.append("       acc.citycounty AS vprovname,    ");
		sql.append("       acc.areaname,    ");
		
		sql.append("       corp.ismcustnum,    ");
		sql.append("       corp.igecustnum,    ");
		sql.append("       corp.ismstocknum,    ");
		sql.append("       corp.igestocknum,    ");
		sql.append("       corp.ismnstocknum,    ");
		sql.append("       corp.igenstocknum,    ");
		
		sql.append("       nvl(cont.istockconnum_s,0) - nvl(cont.istockconnum_b,0) AS istockconnum,    ");
		sql.append("       nvl(cont.izeroconnum_s,0) - nvl(cont.izeroconnum_b,0) AS izeroconnum,    ");
		sql.append("       nvl(cont.instockconnum_s,0) - nvl(cont.instockconnum_b,0) AS instockconnum,    ");
		//因为差额为负数，所以要加
		sql.append("       nvl(cont.ntotalmny_s,0) + nvl(cont.ntotalmny_b,0) -   ");
		sql.append("       (nvl(cont.nbookmny_s,0) - nvl(cont.nbookmny_b,0)) AS naccountmny,   ");
		sql.append("       nvl(cont.nbookmny_s,0) - nvl(cont.nbookmny_b,0) AS nbookmny,    ");
		sql.append("       nvl(cont.ndeductmny_s,0) + nvl(cont.ndeductmny_b,0) AS ndeductmny,   ");
		
		sql.append("       pay.ndepositmny,   ");
		sql.append("       pay.npaymentmny,   ");
		sql.append("       pay.nrebatemny,   ");
		
		sql.append("       buy.ngoodsbuymny,   ");
		
		sql.append("       usr.isumcustnum   ");
		sql.append("  FROM    ");
	}
	
	/**
	 * 获取会计公司相关信息查询语句
	 * @param pamvo
	 * @param corpks
	 * @param sql
	 * @param spm
	 * @throws DZFWarpException
	 */
	private void getAccSqlSpm(QryParamVO pamvo, String[] corpks, StringBuffer sql, SQLParameter spm) throws DZFWarpException {
		//1、加盟商相关信息：
		sql.append("(SELECT DISTINCT t.pk_corp,    ");
		sql.append("       t.unitname AS corpname,    ");
		sql.append("       t.djoindate,    ");
		sql.append("       t.drelievedate,    ");
		sql.append("       t.citycounty,    ");
		sql.append("       a.areaname    ");
		sql.append("  FROM bd_account t    ");
		sql.append("  LEFT JOIN cn_chnarea_b b ON t.vprovince = b.vprovince    ");
		sql.append("  LEFT JOIN cn_chnarea a ON b.pk_chnarea = a.pk_chnarea    ");
		sql.append(" WHERE nvl(t.dr, 0) = 0    ");
		sql.append("   AND nvl(b.dr, 0) = 0    ");
		sql.append("   AND b.type = 1    ");
		sql.append("   AND nvl(a.dr, 0) = 0    ");
	    String where = SqlUtil.buildSqlForIn("t.pk_corp", corpks);
	    sql.append(" AND  ").append(where);
		sql.append("   ) acc    ");
	}
	
	/**
	 * 获取公司相关信息查询语句
	 * @param pamvo
	 * @param corpks
	 * @param sql
	 * @param spm
	 * @throws DZFWarpException
	 */
	private void getCorpSqlSpm(QryParamVO pamvo, String[] corpks,StringBuffer sql, SQLParameter spm) throws DZFWarpException {
		sql.append("LEFT JOIN    ");
		sql.append("(SELECT DISTINCT p.fathercorp AS pk_corp,    ") ;
		sql.append("              SUM(CASE    ") ; 
		sql.append("             WHEN nvl(p.chargedeptname, '小规模纳税人') = '小规模纳税人' THEN    ") ; 
		sql.append("              1    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS ismcustnum,    ") ;
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN nvl(p.chargedeptname, '小规模纳税人') = '一般纳税人' THEN    ") ; 
		sql.append("              1    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS igecustnum,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN nvl(p.chargedeptname, '小规模纳税人') = '小规模纳税人' AND    ") ; 
		sql.append("                  nvl(p.isncust, 'N') = 'Y' THEN    ") ; 
		sql.append("              1    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS ismstocknum,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN nvl(p.chargedeptname, '小规模纳税人') = '一般纳税人' AND    ") ; 
		sql.append("                  nvl(p.isncust, 'N') = 'Y' THEN    ") ; 
		sql.append("              1    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS igestocknum,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN nvl(p.chargedeptname, '小规模纳税人') = '小规模纳税人' AND    ") ; 
		sql.append("                  nvl(p.isncust, 'N') = 'N' THEN    ") ; 
		sql.append("              1    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS ismnstocknum,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN nvl(p.chargedeptname, '小规模纳税人') = '一般纳税人' AND    ") ; 
		sql.append("                  nvl(p.isncust, 'N') = 'N' THEN    ") ; 
		sql.append("              1    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS igenstocknum    ") ; 
		sql.append("  FROM bd_corp p    ");
		sql.append(" WHERE nvl(p.dr, 0) = 0    ");
		sql.append("   AND nvl(p.isseal,'N') = 'N'   ");//非封存客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N'   ");//非分支机构
		String where = SqlUtil.buildSqlForIn("p.fathercorp", corpks);
	    sql.append(" AND  ").append(where);
		if(pamvo.getBegdate() != null){
			sql.append(" AND p.createdate >= ?   ");
			spm.addParam(pamvo.getBegdate());
		}
		if(pamvo.getEnddate() != null){
			sql.append(" AND p.createdate <= ?   ");
			spm.addParam(pamvo.getEnddate());
		}
		sql.append("  GROUP BY p.fathercorp) corp    ");
		sql.append("ON acc.pk_corp = corp.pk_corp    ");
	}
	
	/**
	 * 获取合同相关信息查询语句
	 * @param pamvo
	 * @param corpks
	 * @param sql
	 * @param spm
	 * @throws DZFWarpException
	 */
	private void getContSqlSpm(QryParamVO pamvo, String[] corpks,StringBuffer sql, SQLParameter spm) throws DZFWarpException {
		sql.append("LEFT JOIN    ");
		sql.append("(SELECT DISTINCT ct.pk_corp,    ") ;
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN cn.deductdata >= ? AND cn.deductdata <= ? AND    ") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("                  nvl(ct.isncust, 'N') = 'Y' AND nvl(ct.patchstatus, 0) != 2 AND    ") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 5 THEN    ") ; 
		sql.append("              1    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS istockconnum_s,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'Y' AND nvl(ct.patchstatus, 0) != 2 AND    ") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 5 AND ct.vstatus = 10 THEN    ") ; 
		sql.append("              1    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS istockconnum_b,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN cn.deductdata >= ? AND cn.deductdata <= ? AND    ") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("                  nvl(ct.isncust, 'N') = 'N' AND nvl(cn.ideductpropor, 0) = 0 AND    ") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 THEN    ") ; 
		sql.append("              1    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS izeroconnum_s,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(cn.ideductpropor, 0) = 0 AND    ") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 AND    ") ; 
		sql.append("                  ct.vstatus = 10 THEN    ") ; 
		sql.append("              1    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS izeroconnum_b,    ") ;
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN cn.deductdata >= ? AND cn.deductdata <= ? AND    ") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("                  nvl(ct.isncust, 'N') = 'N' AND nvl(cn.ideductpropor, 0) != 0 AND    ") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 THEN    ") ; 
		sql.append("              1    ") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(cn.ideductpropor, 0) != 0 AND    ") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 AND    ") ; 
		sql.append("                  ct.vstatus = 10 THEN    ") ; 
		sql.append("              -1    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS instockconnum_s,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(cn.ideductpropor, 0) != 0 AND    ") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 AND    ") ; 
		sql.append("                  ct.vstatus = 10 THEN    ") ; 
		sql.append("              1    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS instockconnum_b,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN cn.deductdata >= ? AND    ") ; 
		sql.append("                  cn.deductdata <= ? THEN    ") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("              nvl(ct.nchangetotalmny, 0)    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS ntotalmny_s,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN substr(cn.dchangetime, 0, 10) >= ? AND    ") ; 
		sql.append("                  substr(cn.dchangetime, 0, 10) <= ? THEN    ") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("              nvl(cn.nsubtotalmny, 0)    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS ntotalmny_b,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN cn.deductdata >= ? AND    ") ; 
		sql.append("                  cn.deductdata <= ? THEN    ") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("              nvl(ct.nbookmny, 0)    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS nbookmny_s,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN ct.vstatus = 10 THEN   ") ; 
		sql.append("              nvl(ct.nbookmny, 0)    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS nbookmny_b,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN cn.deductdata >= ? AND    ") ; 
		sql.append("                  cn.deductdata <= ? THEN    ") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("              nvl(cn.ndedsummny, 0)    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS ndeductmny_s,    ") ; 
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN substr(cn.dchangetime, 0, 10) >= ? AND    ") ; 
		sql.append("                  substr(cn.dchangetime, 0, 10) <= ? THEN    ") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("              nvl(cn.nsubdedsummny, 0)    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS ndeductmny_b    ") ; 
		sql.append("  FROM cn_contract cn    ") ; 
		sql.append(" INNER JOIN ynt_contract ct ON cn.pk_contract = ct.pk_contract    ") ; 
		sql.append(" WHERE nvl(cn.dr, 0) = 0    ") ; 
		sql.append("   AND nvl(ct.dr, 0) = 0    ") ; 
		sql.append("   AND ct.vstatus IN (1, 9, 10)   ") ; 
		sql.append("   AND ct.icontracttype = 2    ") ; 
		String where = SqlUtil.buildSqlForIn("ct.pk_corp", corpks);
	    sql.append(" AND  ").append(where);
		if(pamvo.getBegdate() != null){
			sql.append(" AND (cn.deductdata >= ?   ");
			spm.addParam(pamvo.getBegdate());
			sql.append("  OR substr(cn.dchangetime, 0, 10) >= ?)   ");
			spm.addParam(pamvo.getBegdate());
		}
		if(pamvo.getEnddate() != null){
			sql.append(" AND (cn.deductdata <= ?   ");
			spm.addParam(pamvo.getEnddate());
			sql.append("  OR substr(cn.dchangetime, 0, 10) <= ?)   ");
			spm.addParam(pamvo.getEnddate());
		}
		sql.append(" GROUP BY ct.pk_corp ) cont   ");
		sql.append("    ON acc.pk_corp = cont.pk_corp    ");
	}
	
	/**
	 * 获取余额相关信息查询语句
	 * @param pamvo
	 * @param corpks
	 * @param sql
	 * @param spm
	 * @throws DZFWarpException
	 */
	private void getPaySqlSpm(QryParamVO pamvo, String[] corpks, StringBuffer sql, SQLParameter spm) throws DZFWarpException {
		sql.append("LEFT JOIN    ");
		sql.append("(SELECT DISTINCT b.pk_corp,    ") ;
		sql.append("       SUM(decode(b.ipaytype, 1, nvl(b.npaymny, 0), 0)) AS ndepositmny,    ") ; 
		sql.append("       SUM(decode(b.ipaytype, 2, nvl(b.npaymny, 0), 0)) -    ") ; 
		sql.append("       SUM(decode(b.ipaytype, 2, nvl(b.nusedmny, 0), 0)) AS npaymentmny,    ") ; 
		sql.append("       SUM(decode(b.ipaytype, 3, nvl(b.npaymny, 0), 0)) -    ") ; 
		sql.append("       SUM(decode(b.ipaytype, 3, nvl(b.nusedmny, 0), 0)) AS nrebatemny    ") ; 
		sql.append("  FROM cn_balance b    ") ; 
		sql.append(" WHERE nvl(b.dr, 0) = 0    ") ; 
		String where = SqlUtil.buildSqlForIn("b.pk_corp", corpks);
	    sql.append(" AND  ").append(where);
	    sql.append(" GROUP BY b.pk_corp ) pay   ");
		sql.append("    ON acc.pk_corp = pay.pk_corp    ");
	}
	
	/**
	 * 获取商品购买相关信息查询语句
	 * @param pamvo
	 * @param corpks
	 * @param sql
	 * @param spm
	 * @return
	 * @throws DZFWarpException
	 */
	private void getBuySqlSpm(QryParamVO pamvo, String[] corpks, StringBuffer sql, SQLParameter spm) throws DZFWarpException {
		sql.append("LEFT JOIN    ");
		sql.append("(SELECT DISTINCT dl.pk_corp,    ") ;
		sql.append("       SUM(CASE    ") ; 
		sql.append("             WHEN (dl.ipaytype = 2 OR dl.ipaytype = 3) AND dl.iopertype = 5 THEN    ") ; 
		sql.append("              nvl(dl.nusedmny, 0)    ") ; 
		sql.append("             ELSE    ") ; 
		sql.append("              0    ") ; 
		sql.append("           END) AS ngoodsbuymny    ") ; 
		sql.append("  FROM cn_detail dl    ") ; 
		sql.append(" WHERE nvl(dl.dr, 0) = 0    ") ; 
		String where = SqlUtil.buildSqlForIn("dl.pk_corp", corpks);
	    sql.append(" AND  ").append(where);
		if(pamvo.getBegdate() != null){
			sql.append(" AND dl.doperatedate >= ?   ");
			spm.addParam(pamvo.getBegdate());
		}
		if(pamvo.getEnddate() != null){
			sql.append(" AND dl.doperatedate <= ?   ");
			spm.addParam(pamvo.getEnddate());
		}
		sql.append(" GROUP BY dl.pk_corp) buy   ");
		sql.append("    ON acc.pk_corp = buy.pk_corp    ");
	}
	
	/**
	 * 获取用户相关信息查询语句
	 * @param pamvo
	 * @param corpks
	 * @param sql
	 * @param spm
	 * @throws DZFWarpException
	 */
	private void getUserSqlSpm(QryParamVO pamvo, String[] corpks,StringBuffer sql, SQLParameter spm) throws DZFWarpException {
		sql.append("LEFT JOIN    ");
		sql.append("(SELECT DISTINCT r.pk_corp, COUNT(r.cuserid) AS isumcustnum   ") ;
		sql.append("  FROM sm_user r    ") ; 
		sql.append(" WHERE nvl(r.dr, 0) = 0    ") ; 
		sql.append("   AND nvl(r.locked_tag, 'N') = 'N'    ") ; 
//		if(pamvo.getBegdate() != null){
//			sql.append(" AND r.able_time >= ?   ");
//			spm.addParam(pamvo.getBegdate() );
//		}
//		if(pamvo.getEnddate() != null){
//			sql.append(" AND r.able_time <= ?   ");
//			spm.addParam(pamvo.getEnddate() );
//		}
		String where = SqlUtil.buildSqlForIn("r.pk_corp", corpks);
		sql.append(" AND  ").append(where);
		sql.append(" GROUP BY r.pk_corp) usr   ");
		sql.append("    ON acc.pk_corp = usr.pk_corp   ");
	}

}
