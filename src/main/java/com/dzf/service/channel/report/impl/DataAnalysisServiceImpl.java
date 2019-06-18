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
		sql.append("SELECT count(DISTINCT account.pk_corp)  \n");
		sql.append("  FROM bd_account account  \n");
		sql.append("  LEFT JOIN cn_chnarea_b b ON account.vprovince = b.vprovince  \n");
		sql.append("  LEFT JOIN cn_chnarea a ON b.pk_chnarea = a.pk_chnarea  \n");
		sql.append(" WHERE ").append(filtersql);
		sql.append("   AND nvl(b.dr, 0) = 0  \n");
		sql.append("   AND b.type = 1  \n");
		if(!StringUtil.isEmpty(pamvo.getPk_corp())){
		    String[] strs = pamvo.getPk_corp().split(",");
		    String inSql = SqlUtil.buildSqlConditionForIn(strs);
		    sql.append(" AND account.pk_corp in (").append(inSql).append(")");
		}
		if(!StringUtil.isEmpty(pamvo.getAreaname())){
			sql.append(" AND a.areacode = ? \n");
			spm.addParam(pamvo.getAreaname());
		}
		if(!StringUtil.isEmpty(pamvo.getBeginperiod())){
			sql.append(" AND account.djoindate >= ? \n");
			spm.addParam(pamvo.getBeginperiod());
		}
		if(!StringUtil.isEmpty(pamvo.getEndperiod())){
			sql.append(" AND account.djoindate <= ? \n");
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
		sql.append("SELECT DISTINCT account.pk_corp  \n");
		sql.append("  FROM bd_account account  \n");
		sql.append("  LEFT JOIN cn_chnarea_b b ON account.vprovince = b.vprovince  \n");
		sql.append("  LEFT JOIN cn_chnarea a ON b.pk_chnarea = a.pk_chnarea  \n");
		sql.append(" WHERE ").append(filtersql);
		sql.append("   AND nvl(b.dr, 0) = 0  \n");
		sql.append("   AND b.type = 1  \n");
		if (!StringUtil.isEmpty(pamvo.getPk_corp())) {
			String[] strs = pamvo.getPk_corp().split(",");
			String inSql = SqlUtil.buildSqlConditionForIn(strs);
			sql.append(" AND account.pk_corp in (").append(inSql).append(")");
		}
		if (!StringUtil.isEmpty(pamvo.getAreaname())) {
			sql.append(" AND a.areacode = ? \n");
			spm.addParam(pamvo.getAreaname());
		}
		if (!StringUtil.isEmpty(pamvo.getBeginperiod())) {
			sql.append(" AND account.djoindate >= ? \n");
			spm.addParam(pamvo.getBeginperiod());
		}
		if (!StringUtil.isEmpty(pamvo.getEndperiod())) {
			sql.append(" AND account.djoindate <= ? \n");
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
		sql.append("SELECT DISTINCT acc.pk_corp,  \n");
		sql.append("       acc.corpname,  \n");
		sql.append("       acc.djoindate,  \n");
		sql.append("       acc.drelievedate,  \n");
		sql.append("       acc.citycounty AS vprovname,  \n");
		sql.append("       acc.areaname,  \n");
		
		sql.append("       corp.ismcustnum,  \n");
		sql.append("       corp.igecustnum,  \n");
		sql.append("       corp.ismstocknum,  \n");
		sql.append("       corp.igestocknum,  \n");
		sql.append("       corp.ismnstocknum,  \n");
		sql.append("       corp.igenstocknum,  \n");
		
		sql.append("       nvl(cont.istockconnum_s,0) - nvl(cont.istockconnum_b,0) AS istockconnum,  \n");
		sql.append("       nvl(cont.izeroconnum_s,0) - nvl(cont.izeroconnum_b,0) AS izeroconnum,  \n");
		sql.append("       nvl(cont.instockconnum_s,0) - nvl(cont.instockconnum_b,0) AS instockconnum,  \n");
		//因为差额为负数，所以要加
		sql.append("       nvl(cont.ntotalmny_s,0) + nvl(cont.ntotalmny_b,0) - \n");
		sql.append("       (nvl(cont.nbookmny_s,0) - nvl(cont.nbookmny_b,0)) AS naccountmny, \n");
		sql.append("       nvl(cont.nbookmny_s,0) - nvl(cont.nbookmny_b,0) AS nbookmny,  \n");
		sql.append("       nvl(cont.ndeductmny_s,0) + nvl(cont.ndeductmny_b,0) AS ndeductmny, \n");
		
		sql.append("       pay.ndepositmny, \n");
		sql.append("       pay.npaymentmny, \n");
		sql.append("       pay.nrebatemny, \n");
		
		sql.append("       buy.ngoodsbuymny, \n");
		
		sql.append("       usr.isumcustnum \n");
		sql.append("  FROM  \n");
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
		sql.append("(SELECT DISTINCT t.pk_corp,  \n");
		sql.append("       t.unitname AS corpname,  \n");
		sql.append("       t.djoindate,  \n");
		sql.append("       t.drelievedate,  \n");
		sql.append("       t.citycounty,  \n");
		sql.append("       a.areaname  \n");
		sql.append("  FROM bd_account t  \n");
		sql.append("  LEFT JOIN cn_chnarea_b b ON t.vprovince = b.vprovince  \n");
		sql.append("  LEFT JOIN cn_chnarea a ON b.pk_chnarea = a.pk_chnarea  \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(b.dr, 0) = 0  \n");
		sql.append("   AND b.type = 1  \n");
		sql.append("   AND nvl(a.dr, 0) = 0  \n");
	    String where = SqlUtil.buildSqlForIn("t.pk_corp", corpks);
	    sql.append(" AND  ").append(where);
		sql.append("   ) acc  \n");
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
		sql.append("LEFT JOIN  \n");
		sql.append("(SELECT DISTINCT p.fathercorp AS pk_corp,  \n") ;
		sql.append("              SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(p.chargedeptname, '小规模纳税人') = '小规模纳税人' THEN  \n") ; 
		sql.append("              1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS ismcustnum,  \n") ;
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(p.chargedeptname, '小规模纳税人') = '一般纳税人' THEN  \n") ; 
		sql.append("              1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS igecustnum,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(p.chargedeptname, '小规模纳税人') = '小规模纳税人' AND  \n") ; 
		sql.append("                  nvl(p.isncust, 'N') = 'Y' THEN  \n") ; 
		sql.append("              1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS ismstocknum,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(p.chargedeptname, '小规模纳税人') = '一般纳税人' AND  \n") ; 
		sql.append("                  nvl(p.isncust, 'N') = 'Y' THEN  \n") ; 
		sql.append("              1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS igestocknum,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(p.chargedeptname, '小规模纳税人') = '小规模纳税人' AND  \n") ; 
		sql.append("                  nvl(p.isncust, 'N') = 'N' THEN  \n") ; 
		sql.append("              1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS ismnstocknum,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(p.chargedeptname, '小规模纳税人') = '一般纳税人' AND  \n") ; 
		sql.append("                  nvl(p.isncust, 'N') = 'N' THEN  \n") ; 
		sql.append("              1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS igenstocknum  \n") ; 
		sql.append("  FROM bd_corp p  \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0  \n");
		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");//非封存客户
		sql.append("   AND nvl(p.isaccountcorp,'N') = 'N' \n");//非分支机构
		String where = SqlUtil.buildSqlForIn("p.fathercorp", corpks);
	    sql.append(" AND  ").append(where);
		if(pamvo.getBegdate() != null){
			sql.append(" AND p.createdate >= ? \n");
			spm.addParam(pamvo.getBegdate());
		}
		if(pamvo.getEnddate() != null){
			sql.append(" AND p.createdate <= ? \n");
			spm.addParam(pamvo.getEnddate());
		}
		sql.append("  GROUP BY p.fathercorp) corp  \n");
		sql.append("ON acc.pk_corp = corp.pk_corp  \n");
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
		sql.append("LEFT JOIN  \n");
		sql.append("(SELECT DISTINCT ct.pk_corp,  \n") ;
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN cn.deductdata >= ? AND cn.deductdata <= ? AND  \n") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("                  nvl(ct.isncust, 'N') = 'Y' AND nvl(ct.patchstatus, 0) != 2 AND  \n") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 5 THEN  \n") ; 
		sql.append("              1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS istockconnum_s,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'Y' AND nvl(ct.patchstatus, 0) != 2 AND  \n") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 5 AND ct.vstatus = 10 THEN  \n") ; 
		sql.append("              1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS istockconnum_b,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN cn.deductdata >= ? AND cn.deductdata <= ? AND  \n") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("                  nvl(ct.isncust, 'N') = 'N' AND nvl(cn.ideductpropor, 0) = 0 AND  \n") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 THEN  \n") ; 
		sql.append("              1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS izeroconnum_s,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(cn.ideductpropor, 0) = 0 AND  \n") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 AND  \n") ; 
		sql.append("                  ct.vstatus = 10 THEN  \n") ; 
		sql.append("              1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS izeroconnum_b,  \n") ;
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN cn.deductdata >= ? AND cn.deductdata <= ? AND  \n") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("                  nvl(ct.isncust, 'N') = 'N' AND nvl(cn.ideductpropor, 0) != 0 AND  \n") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 THEN  \n") ; 
		sql.append("              1  \n") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(cn.ideductpropor, 0) != 0 AND  \n") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 AND  \n") ; 
		sql.append("                  ct.vstatus = 10 THEN  \n") ; 
		sql.append("              -1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS instockconnum_s,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(cn.ideductpropor, 0) != 0 AND  \n") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 AND  \n") ; 
		sql.append("                  ct.vstatus = 10 THEN  \n") ; 
		sql.append("              1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS instockconnum_b,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN cn.deductdata >= ? AND  \n") ; 
		sql.append("                  cn.deductdata <= ? THEN  \n") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("              nvl(ct.nchangetotalmny, 0)  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS ntotalmny_s,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN substr(cn.dchangetime, 0, 10) >= ? AND  \n") ; 
		sql.append("                  substr(cn.dchangetime, 0, 10) <= ? THEN  \n") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("              nvl(cn.nsubtotalmny, 0)  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS ntotalmny_b,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN cn.deductdata >= ? AND  \n") ; 
		sql.append("                  cn.deductdata <= ? THEN  \n") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("              nvl(ct.nbookmny, 0)  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS nbookmny_s,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN ct.vstatus = 10 THEN \n") ; 
		sql.append("              nvl(ct.nbookmny, 0)  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS nbookmny_b,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN cn.deductdata >= ? AND  \n") ; 
		sql.append("                  cn.deductdata <= ? THEN  \n") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("              nvl(cn.ndedsummny, 0)  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS ndeductmny_s,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN substr(cn.dchangetime, 0, 10) >= ? AND  \n") ; 
		sql.append("                  substr(cn.dchangetime, 0, 10) <= ? THEN  \n") ; 
		spm.addParam(pamvo.getBegdate());
		spm.addParam(pamvo.getEnddate());
		sql.append("              nvl(cn.nsubdedsummny, 0)  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS ndeductmny_b  \n") ; 
		sql.append("  FROM cn_contract cn  \n") ; 
		sql.append(" INNER JOIN ynt_contract ct ON cn.pk_contract = ct.pk_contract  \n") ; 
		sql.append(" WHERE nvl(cn.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(ct.dr, 0) = 0  \n") ; 
		sql.append("   AND ct.vstatus IN (1, 9, 10) \n") ; 
		sql.append("   AND ct.icontracttype = 2  \n") ; 
		String where = SqlUtil.buildSqlForIn("ct.pk_corp", corpks);
	    sql.append(" AND  ").append(where);
		if(pamvo.getBegdate() != null){
			sql.append(" AND (cn.deductdata >= ? \n");
			spm.addParam(pamvo.getBegdate());
			sql.append("  OR substr(cn.dchangetime, 0, 10) >= ?) \n");
			spm.addParam(pamvo.getBegdate());
		}
		if(pamvo.getEnddate() != null){
			sql.append(" AND (cn.deductdata <= ? \n");
			spm.addParam(pamvo.getEnddate());
			sql.append("  OR substr(cn.dchangetime, 0, 10) <= ?) \n");
			spm.addParam(pamvo.getEnddate());
		}
		sql.append(" GROUP BY ct.pk_corp ) cont \n");
		sql.append("    ON acc.pk_corp = cont.pk_corp  \n");
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
		sql.append("LEFT JOIN  \n");
		sql.append("(SELECT DISTINCT b.pk_corp,  \n") ;
		sql.append("       SUM(decode(b.ipaytype, 1, nvl(b.npaymny, 0), 0)) AS ndepositmny,  \n") ; 
		sql.append("       SUM(decode(b.ipaytype, 2, nvl(b.npaymny, 0), 0)) -  \n") ; 
		sql.append("       SUM(decode(b.ipaytype, 2, nvl(b.nusedmny, 0), 0)) AS npaymentmny,  \n") ; 
		sql.append("       SUM(decode(b.ipaytype, 3, nvl(b.npaymny, 0), 0)) -  \n") ; 
		sql.append("       SUM(decode(b.ipaytype, 3, nvl(b.nusedmny, 0), 0)) AS nrebatemny  \n") ; 
		sql.append("  FROM cn_balance b  \n") ; 
		sql.append(" WHERE nvl(b.dr, 0) = 0  \n") ; 
		String where = SqlUtil.buildSqlForIn("b.pk_corp", corpks);
	    sql.append(" AND  ").append(where);
	    sql.append(" GROUP BY b.pk_corp ) pay \n");
		sql.append("    ON acc.pk_corp = pay.pk_corp  \n");
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
		sql.append("LEFT JOIN  \n");
		sql.append("(SELECT DISTINCT dl.pk_corp,  \n") ;
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN (dl.ipaytype = 2 OR dl.ipaytype = 3) AND dl.iopertype = 5 THEN  \n") ; 
		sql.append("              nvl(dl.nusedmny, 0)  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS ngoodsbuymny  \n") ; 
		sql.append("  FROM cn_detail dl  \n") ; 
		sql.append(" WHERE nvl(dl.dr, 0) = 0  \n") ; 
		String where = SqlUtil.buildSqlForIn("dl.pk_corp", corpks);
	    sql.append(" AND  ").append(where);
		if(pamvo.getBegdate() != null){
			sql.append(" AND dl.doperatedate >= ? \n");
			spm.addParam(pamvo.getBegdate());
		}
		if(pamvo.getEnddate() != null){
			sql.append(" AND dl.doperatedate <= ? \n");
			spm.addParam(pamvo.getEnddate());
		}
		sql.append(" GROUP BY dl.pk_corp) buy \n");
		sql.append("    ON acc.pk_corp = buy.pk_corp  \n");
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
		sql.append("LEFT JOIN  \n");
		sql.append("(SELECT DISTINCT r.pk_corp, COUNT(r.cuserid) AS isumcustnum \n") ;
		sql.append("  FROM sm_user r  \n") ; 
		sql.append(" WHERE nvl(r.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(r.locked_tag, 'N') = 'N'  \n") ; 
//		if(pamvo.getBegdate() != null){
//			sql.append(" AND r.able_time >= ? \n");
//			spm.addParam(pamvo.getBegdate() );
//		}
//		if(pamvo.getEnddate() != null){
//			sql.append(" AND r.able_time <= ? \n");
//			spm.addParam(pamvo.getEnddate() );
//		}
		String where = SqlUtil.buildSqlForIn("r.pk_corp", corpks);
		sql.append(" AND  ").append(where);
		sql.append(" GROUP BY r.pk_corp) usr \n");
		sql.append("    ON acc.pk_corp = usr.pk_corp \n");
	}

}
