package com.dzf.service.channel.report.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.DeductAnalysisVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.service.channel.report.IDeductAnalysis;

@Service("deductanalysisser")
public class DeductAnalysisImpl implements IDeductAnalysis {
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@SuppressWarnings("unchecked")
	@Override
	public List<DeductAnalysisVO> query(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySqlSpm(paramvo, 1);
		List<DeductAnalysisVO> list = (List<DeductAnalysisVO>) singleObjectBO.executeQuery(qryvo.getSql(),
				qryvo.getSpm(), new BeanListProcessor(DeductAnalysisVO.class));
		if (list != null && list.size() > 0) {
			Map<String, DeductAnalysisVO> dedmap = queryTotalMny(paramvo, 1);//扣款金额
			Map<String, DeductAnalysisVO> retmap = queryTotalMny(paramvo, 2);//退款金额
			DeductAnalysisVO dedvo = null;
			DeductAnalysisVO retvo = null;
			CorpVO corpvo = null;
			 Map<String, Integer> custmap = queryCustNum(paramvo);
			for (DeductAnalysisVO vo : list) {
				corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if (corpvo != null) {
					vo.setCorpcode(corpvo.getInnercode());
					vo.setCorpname(corpvo.getUnitname());
				}
				if(dedmap != null && !dedmap.isEmpty()){
					dedvo = dedmap.get(vo.getPk_corp());
				}
				if(retmap != null && !retmap.isEmpty()){
					retvo = retmap.get(vo.getPk_corp());
				}
				if(retvo != null){
					vo.setIretnum(retvo.getIcorpnums());
					vo.setNretmny(retvo.getNdeducmny());
				}
				if(dedvo != null){
					vo.setIcorpnums_sum(dedvo.getIcorpnums());
					vo.setNdeductmny_sum(SafeCompute.sub(dedvo.getNdeducmny(), vo.getNretmny()));
				}
				if(custmap != null && !custmap.isEmpty()){
					vo.setIstocknum(custmap.get(vo.getPk_corp()));
				}
			}
		}
		return list;
	}
	
	/**
	 * 获取扣款金额查询语句
	 * @param paramvo
	 * @param qrytype  1：扣款金额汇总查询；2：扣款金额排序查询；
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(QryParamVO paramvo, int qrytype) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT   \n") ;
		if(qrytype == 1){
			sql.append("   t.pk_corp,  \n") ;
		}
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){//预付款
			sql.append(" nvl(t.ndeductmny,0) AS ndeducmny, \n") ;
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){//返点
			sql.append(" nvl(t.ndedrebamny,0) AS ndeducmny, \n") ;
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == -1){//全部
			sql.append(" nvl(t.ndedsummny,0) AS ndeducmny, \n") ;
		}
		sql.append(" COUNT(t.pk_confrim) AS icorpnums  \n") ;
		sql.append("  FROM cn_contract t  \n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND t.vstatus IN (?, ?) \n") ; 
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
//		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);//暂不统计作废数据
		sql.append("   AND nvl(t.isncust, 'N') = 'N' \n") ; 
		if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
			sql.append(" AND SUBSTR(t.deductdata,1,7) >= ? \n");
			spm.addParam(paramvo.getBeginperiod());
		}
		if(!StringUtil.isEmpty(paramvo.getEndperiod())){
			sql.append(" AND SUBSTR(t.deductdata,1,7) <= ? \n");
			spm.addParam(paramvo.getEndperiod());
		}
		if(paramvo.getBegdate() != null){
			sql.append(" AND t.deductdata >= ? \n");
			spm.addParam(paramvo.getBegdate());
		}
		if(paramvo.getEnddate() != null){
			sql.append(" AND t.deductdata <= ? \n");
			spm.addParam(paramvo.getEnddate());
		}
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
			String[] corps = paramvo.getPk_corp().split(",");
			String where = SqlUtil.buildSqlForIn(" t.pk_corp ", corps);
			sql.append(" AND ").append(where);
		}
//		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){
//			sql.append(" AND t.ndeductmny IS NOT NULL ");
//		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){
//			sql.append(" AND t.ndedrebamny IS NOT NULL ");
//		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == -1){
//			sql.append(" AND t.ndedsummny IS NOT NULL ");
//		}
		sql.append(" GROUP BY \n") ; 
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){
			sql.append(" nvl(t.ndeductmny,0) ");
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){
			sql.append(" nvl(t.ndedrebamny,0) ");
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == -1){
			sql.append(" nvl(t.ndedsummny,0) ");
		}
		if(qrytype == 1){
			sql.append(" ,t.pk_corp  \n") ; 
		}
		sql.append(" ORDER BY ");
		if(qrytype == 1){
			sql.append(" t.pk_corp");
		}else if(qrytype == 2){
			if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){
				sql.append(" nvl(t.ndeductmny,0) ");
			}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){
				sql.append(" nvl(t.ndedrebamny,0) ");
			}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == -1){
				sql.append(" nvl(t.ndedsummny,0) ");
			}
			sql.append(" DESC");
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**
	 * 查询金额汇总数据
	 * @param paramvo
	 * @param qrytype  1：扣款；2：退款
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String,DeductAnalysisVO> queryTotalMny(QryParamVO paramvo, int qrytype) throws DZFWarpException {
		Map<String,DeductAnalysisVO> map = new HashMap<String,DeductAnalysisVO>();
		QrySqlSpmVO qryvo = getTotalQrySqlSpm(paramvo, qrytype);
		List<DeductAnalysisVO> list = (List<DeductAnalysisVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(DeductAnalysisVO.class));
		if(list != null && list.size() > 0){
			for(DeductAnalysisVO vo : list){
				map.put(vo.getPk_corp(), vo);
			}
		}
		return map;
	}
	
	/**
	 * 获取金额汇总查询语句
	 * @param paramvo
	 * @param qrytype  1：扣款；2：退款
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getTotalQrySqlSpm(QryParamVO paramvo, int qrytype) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,  \n") ;
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){//预付款
			if(qrytype == 1){
				sql.append(" SUM(nvl(t.ndeductmny,0)) AS ndeducmny, \n") ;
			}else if(qrytype == 2){
				sql.append(" SUM(nvl(t.nretdedmny,0)) AS ndeducmny, \n") ;
			}
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){//返点
			if(qrytype == 1){
				sql.append(" SUM(nvl(t.ndedrebamny,0)) AS ndeducmny, \n") ;
			}else if(qrytype == 2){
				sql.append(" SUM(nvl(t.nretrebmny,0)) AS ndeducmny, \n") ;
			}
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == -1){//全部
			if(qrytype == 1){
				sql.append(" SUM(nvl(t.ndedsummny,0)) AS ndeducmny, \n") ;
			}else if(qrytype == 2){
				sql.append(" SUM(nvl(t.nreturnmny,0)) AS ndeducmny, \n") ;
			}
		}
		sql.append(" COUNT(t.pk_confrim) AS icorpnums  \n") ;
		sql.append("  FROM cn_contract t  \n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		if(qrytype == 1){
			sql.append("   AND t.vstatus IN (?, ?) \n") ;
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		}else if(qrytype == 2){
			sql.append("   AND t.vstatus = ? \n") ; 
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		}
//		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);//暂不统计作废数据
		sql.append("   AND nvl(t.isncust, 'N') = 'N' \n") ; 
		if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
			if(qrytype == 1){
				sql.append(" AND SUBSTR(t.deductdata,1,7) >= ? \n");
			}else if(qrytype == 2){
				sql.append(" AND SUBSTR(t.dchangetime,1,7) >= ? \n");
			}
			spm.addParam(paramvo.getBeginperiod());
		}
		if(!StringUtil.isEmpty(paramvo.getEndperiod())){
			if(qrytype == 1){
				sql.append(" AND SUBSTR(t.deductdata,1,7) <= ? \n");
			}else if(qrytype == 2){
				sql.append(" AND SUBSTR(t.dchangetime,1,7) <= ? \n");
			}
			spm.addParam(paramvo.getEndperiod());
		}
		if(paramvo.getBegdate() != null){
			if(qrytype == 1){
				sql.append(" AND t.deductdata >= ? \n");
			}else if(qrytype == 2){
				sql.append(" AND SUBSTR(t.dchangetime,1,10) >= ? \n");
			}
			spm.addParam(paramvo.getBegdate());
		}
		if(paramvo.getEnddate() != null){
			if(qrytype == 1){
				sql.append(" AND t.deductdata <= ? \n");
			}else if(qrytype == 2){
				sql.append(" AND SUBSTR(t.dchangetime,1,10) <= ? \n");
			}
			spm.addParam(paramvo.getEnddate());
		}
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
			String[] corps = paramvo.getPk_corp().split(",");
			String where = SqlUtil.buildSqlForIn(" t.pk_corp ", corps);
			sql.append(" AND ").append(where);
		}
//		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){
//			sql.append(" AND t.nretdedmny IS NOT NULL ");
//		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){
//			sql.append(" AND t.nretrebmny IS NOT NULL ");
//		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == -1){
//			sql.append(" AND t.nreturnmny IS NOT NULL ");
//		}
		sql.append(" GROUP BY \n") ; 
		sql.append(" t.pk_corp  \n") ; 
		sql.append(" ORDER BY t.pk_corp \n");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DeductAnalysisVO> queryMnyOrder(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySqlSpm(paramvo, 2);
		List<DeductAnalysisVO> list = (List<DeductAnalysisVO>) singleObjectBO.executeQuery(qryvo.getSql(),
				qryvo.getSpm(), new BeanListProcessor(DeductAnalysisVO.class));
//		if(list != null && list.size() > 0){
//			for(DeductAnalysisVO vo : list){
//				vo.setNdeducmny(CommonUtil.getDZFDouble(vo.getNdeducmny()).setScale(2, DZFDouble.ROUND_HALF_UP));
//			}
//		}
		return list;
	}
	
	/**
	 * 查询存量客户数量
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Integer> queryCustNum(QryParamVO paramvo) throws DZFWarpException {
		Map<String, Integer> custmap = new HashMap<String, Integer>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT p.fathercorp as pk_corp,\n");
		sql.append("       COUNT(DISTINCT p.pk_corp) AS istocknum \n");
		sql.append("  FROM bd_corp p \n");
		sql.append("  LEFT JOIN bd_account t ON p.fathercorp = t.pk_corp \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0 \n");
		if (!StringUtil.isEmpty(paramvo.getPk_corp())) {
			String[] corps = paramvo.getPk_corp().split(",");
			String where = SqlUtil.buildSqlForIn(" t.pk_corp ", corps);
			sql.append(" AND ").append(where);
		}
		sql.append("   AND nvl(t.dr, 0) = 0 \n");
		sql.append("   AND nvl(t.ischannel, 'N') = 'Y'\n"); // 渠道客户
		sql.append("   AND nvl(p.isseal, 'N') = 'N'\n"); // 未封存
		sql.append("   AND nvl(p.isncust,'N') = 'Y' \n"); // 存量客户
		// sql.append(" AND nvl(p.ishasaccount,'N') = 'Y' \n");//已建账
		// sql.append(" AND p.chargedeptname is not null \n");//纳税人性质不能为空
		sql.append(" GROUP BY p.fathercorp \n") ; 
		List<DeductAnalysisVO> list = (List<DeductAnalysisVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(DeductAnalysisVO.class));
		if(list != null && list.size() > 0){
			for(DeductAnalysisVO vo : list){
				custmap.put(vo.getPk_corp(), vo.getIstocknum());
			}
		}
		return custmap;
	}

}
