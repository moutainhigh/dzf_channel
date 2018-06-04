package com.dzf.service.channel.report.impl;

import java.util.ArrayList;
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
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.IDeductAnalysis;

@Service("deductanalysisser")
public class DeductAnalysisImpl implements IDeductAnalysis {
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public List<DeductAnalysisVO> query(QryParamVO paramvo) throws DZFWarpException {
		List<DeductAnalysisVO> retlist = new ArrayList<DeductAnalysisVO>();
		//查询日期校验
		checkQryDate(paramvo);
		//1、客户主键
		List<String> pk_corplist = new ArrayList<String>();
		//2、扣款金额明细
		Map<String,List<DeductAnalysisVO>> detmap = queryDedMnyDetail(paramvo, pk_corplist);
		//3、扣款总金额
		Map<String, DeductAnalysisVO> dedmap = queryTotalMny(paramvo, 1, pk_corplist);
		//4、退款总金额
		Map<String, DeductAnalysisVO> bakmap = queryTotalMny(paramvo, 2, pk_corplist);
		//5、存量客户数
		Map<String, Integer> custmap = queryCustNum(paramvo);
		if(pk_corplist != null && pk_corplist.size() > 0){
			DeductAnalysisVO retvo = null;
			List<DeductAnalysisVO> detlist = null;
//			DeductAnalysisVO detvo = null;
			DeductAnalysisVO dedvo = null;
			DeductAnalysisVO bakvo = null;
			CorpVO corpvo = null;
			int num = 0;//循环次数
			int connum = 0;//合同最终金额
			DZFDouble ndedsummny = DZFDouble.ZERO_DBL;
			for(String pk_corp : pk_corplist){
				num = 0;
				connum = 0;
				if(detmap != null && !detmap.isEmpty()){
					detlist = (List<DeductAnalysisVO>) detmap.get(pk_corp);
					if(detlist != null && detlist.size() > 0){
						for(DeductAnalysisVO dvo : detlist){
							num = 0;
							connum = 0;
							ndedsummny = DZFDouble.ZERO_DBL;
							retvo = new DeductAnalysisVO();
							retvo.setPk_corp(pk_corp);
							corpvo = CorpCache.getInstance().get(null, pk_corp);
							if (corpvo != null) {
								retvo.setCorpcode(corpvo.getInnercode());
								retvo.setCorpname(corpvo.getUnitname());
							}
							retvo.setNdeducmny(dvo.getNdeducmny());
							retvo.setIcorpnums(dvo.getIcorpnums());
							if(bakmap != null && !bakmap.isEmpty()){
								bakvo = bakmap.get(pk_corp);
								if(bakvo != null){
									retvo.setIretnum(bakvo.getIcorpnums());//退回合同数
									retvo.setNretmny(bakvo.getNdeducmny());//退回金额
								}
							}
							if(dedmap != null && !dedmap.isEmpty()){
								dedvo = dedmap.get(pk_corp);
								if(dedvo != null){
									if(num == 0){
										connum = ToolsUtil.subInteger(dedvo.getIcorpnums(), retvo.getIretnum());
										ndedsummny = SafeCompute.sub(dedvo.getNdeducmny(), retvo.getNretmny());
									}
									retvo.setIcorpnums_sum(connum);//总合同数
									retvo.setNdeductmny_sum(ndedsummny);//总扣款
								}else{//此公司无扣款金额，只有退款金额，但是别的公司有退款金额，计算总合同数和总扣款
									if(num == 0){
										connum = ToolsUtil.subInteger(0, retvo.getIretnum());
										ndedsummny = SafeCompute.sub(DZFDouble.ZERO_DBL, retvo.getNretmny());
									}
									retvo.setIcorpnums_sum(connum);//总合同数
									retvo.setNdeductmny_sum(ndedsummny);//总扣款
								}
							}else{//无扣款金额，只有退款金额，计算总合同数和总扣款
								if(num == 0){
									connum = ToolsUtil.subInteger(0, retvo.getIretnum());
									ndedsummny = SafeCompute.sub(DZFDouble.ZERO_DBL, retvo.getNretmny());
								}
								retvo.setIcorpnums_sum(connum);//总合同数
								retvo.setNdeductmny_sum(ndedsummny);//总扣款
							}
							if(custmap != null && !custmap.isEmpty()){
								retvo.setIstocknum(custmap.get(retvo.getPk_corp()));
							}
							retlist.add(retvo);
							num++;
						}	
					}else{
						//某公司没有扣款明细
						retvo = new DeductAnalysisVO();
						retvo.setPk_corp(pk_corp);
						corpvo = CorpCache.getInstance().get(null, pk_corp);
						if (corpvo != null) {
							retvo.setCorpcode(corpvo.getInnercode());
							retvo.setCorpname(corpvo.getUnitname());
						}
						if(bakmap != null && !bakmap.isEmpty()){
							bakvo = bakmap.get(pk_corp);
							if(bakvo != null){
								retvo.setIretnum(bakvo.getIcorpnums());//退回合同数
								retvo.setNretmny(bakvo.getNdeducmny());//退回金额
							}
						}
						if(dedmap != null && !dedmap.isEmpty()){
							dedvo = dedmap.get(pk_corp);
							if(dedvo != null){
								retvo.setIcorpnums_sum(ToolsUtil.subInteger(dedvo.getIcorpnums(), retvo.getIretnum()));//总合同数
								retvo.setNdeductmny_sum(SafeCompute.sub(dedvo.getNdeducmny(), retvo.getNretmny()));//总扣款
							}else{//此公司无扣款金额，只有退款金额，但是别的公司有退款金额，计算总合同数和总扣款
								if(num == 0){
									connum = ToolsUtil.subInteger(0, retvo.getIretnum());
									ndedsummny = SafeCompute.sub(DZFDouble.ZERO_DBL, retvo.getNretmny());
								}
								retvo.setIcorpnums_sum(connum);//总合同数
								retvo.setNdeductmny_sum(ndedsummny);//总扣款
							}
						}else{//无扣款金额，只有退款金额，计算总合同数和总扣款
							retvo.setIcorpnums_sum(ToolsUtil.subInteger(0, retvo.getIretnum()));//总合同数
							retvo.setNdeductmny_sum(SafeCompute.sub(DZFDouble.ZERO_DBL, retvo.getNretmny()));//总扣款
						}
						if(custmap != null && !custmap.isEmpty()){
							retvo.setIstocknum(custmap.get(retvo.getPk_corp()));
						}
						retlist.add(retvo);
					}
					
				}else{
					//查询所有公司没有扣款明细
					retvo = new DeductAnalysisVO();
					retvo.setPk_corp(pk_corp);
					corpvo = CorpCache.getInstance().get(null, pk_corp);
					if (corpvo != null) {
						retvo.setCorpcode(corpvo.getInnercode());
						retvo.setCorpname(corpvo.getUnitname());
					}
					if(bakmap != null && !bakmap.isEmpty()){
						bakvo = bakmap.get(pk_corp);
						if(bakvo != null){
							retvo.setIretnum(bakvo.getIcorpnums());//退回合同数
							retvo.setNretmny(bakvo.getNdeducmny());//退回金额
						}
					}
					if(dedmap != null && !dedmap.isEmpty()){
						dedvo = dedmap.get(pk_corp);
						if(dedvo != null){
							retvo.setIcorpnums_sum(ToolsUtil.subInteger(dedvo.getIcorpnums(), retvo.getIretnum()));//总合同数
							retvo.setNdeductmny_sum(SafeCompute.sub(dedvo.getNdeducmny(), retvo.getNretmny()));//总扣款
						}else{//此公司无扣款金额，只有退款金额，但是别的公司有退款金额，计算总合同数和总扣款
							if(num == 0){
								connum = ToolsUtil.subInteger(0, retvo.getIretnum());
								ndedsummny = SafeCompute.sub(DZFDouble.ZERO_DBL, retvo.getNretmny());
							}
							retvo.setIcorpnums_sum(connum);//总合同数
							retvo.setNdeductmny_sum(ndedsummny);//总扣款
						}
					}else{//无扣款金额，只有退款金额，计算总合同数和总扣款
						retvo.setIcorpnums_sum(ToolsUtil.subInteger(0, retvo.getIretnum()));//总合同数
						retvo.setNdeductmny_sum(SafeCompute.sub(DZFDouble.ZERO_DBL, retvo.getNretmny()));//总扣款
					}
					if(custmap != null && !custmap.isEmpty()){
						retvo.setIstocknum(custmap.get(retvo.getPk_corp()));
					}
					retlist.add(retvo);
				}
			}
		}
		return retlist;
	}
	
	/**
	 * 查询日期校验
	 * @param paramvo
	 * @throws DZFWarpException
	 */
	private void checkQryDate(QryParamVO paramvo) throws DZFWarpException {
		if(!StringUtil.isEmpty(paramvo.getBeginperiod()) && !StringUtil.isEmpty(paramvo.getEndperiod())){
			if(paramvo.getBeginperiod().compareTo(paramvo.getEndperiod()) > 0){
				throw new BusinessException("开始查询期间不能大于结束查询期间");
			}
		}else if(paramvo.getBegdate() != null && paramvo.getEnddate() != null){
			if(paramvo.getBegdate().compareTo(paramvo.getEnddate()) > 0){
				throw new BusinessException("开始查询日期不能大于结束日期");
			}
		}
		
	}
	
	/**
	 * 查询扣款明细
	 * @param paramvo
	 * @param pk_corplist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String,List<DeductAnalysisVO>> queryDedMnyDetail(QryParamVO paramvo,List<String> pk_corplist) throws DZFWarpException {
		Map<String,List<DeductAnalysisVO>> detmap = new HashMap<String,List<DeductAnalysisVO>>();
		QrySqlSpmVO qryvo = getQrySqlSpm(paramvo, 1);
		List<DeductAnalysisVO> list = (List<DeductAnalysisVO>) singleObjectBO.executeQuery(qryvo.getSql(),
				qryvo.getSpm(), new BeanListProcessor(DeductAnalysisVO.class));
		if(list != null && list.size() > 0){
			List<DeductAnalysisVO> newlist = null;
			List<DeductAnalysisVO> oldlist = null;
			for(DeductAnalysisVO detvo : list){
				if(!pk_corplist.contains(detvo.getPk_corp())){
					pk_corplist.add(detvo.getPk_corp());
				}
				if(!detmap.containsKey(detvo.getPk_corp())){
					newlist = new ArrayList<DeductAnalysisVO>();
					newlist.add(detvo);
					detmap.put(detvo.getPk_corp(), newlist);
				}else{
					oldlist = detmap.get(detvo.getPk_corp());
					oldlist.add(detvo);
					detmap.put(detvo.getPk_corp(), oldlist);
				}
			}
		}
		return detmap;
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
		sql.append("   AND t.vstatus IN (?, ?, ?) \n") ; 
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);//暂不统计作废数据
//		sql.append("   AND nvl(t.isncust, 'N') = 'N' \n") ; //不统计存量客户
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
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){
			sql.append(" AND ( nvl(t.ndeductmny,0) != 0 OR nvl(t.ndedsummny,0) = 0 ) ");
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){
			sql.append(" AND nvl(t.ndedrebamny,0) != 0 ");
		}
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
	private Map<String,DeductAnalysisVO> queryTotalMny(QryParamVO paramvo, int qrytype, List<String> pk_corplist) throws DZFWarpException {
		Map<String,DeductAnalysisVO> map = new HashMap<String,DeductAnalysisVO>();
		QrySqlSpmVO qryvo = getTotalQrySqlSpm(paramvo, qrytype);
		List<DeductAnalysisVO> list = (List<DeductAnalysisVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(DeductAnalysisVO.class));
		if(list != null && list.size() > 0){
			for(DeductAnalysisVO vo : list){
				if(!pk_corplist.contains(vo.getPk_corp())){
					pk_corplist.add(vo.getPk_corp());
				}
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
		if(qrytype == 1){
//			sql.append("  COUNT(t.pk_confrim) AS icorpnums \n") ; 
			//合同数量去掉补提单合同数
			sql.append("       SUM(CASE  \n") ; 
			sql.append("             WHEN nvl(ct.patchstatus,0) != 2 THEN  \n") ; 
			sql.append("              1  \n") ; 
			sql.append("             ELSE  \n") ; 
			sql.append("              0  \n") ; 
			sql.append("           END) AS icorpnums  \n") ;
		}else if(qrytype == 2){
			sql.append(" SUM( ");
			sql.append("    CASE t.vstatus \n") ; 
			sql.append("      WHEN 10 THEN \n") ; 
			sql.append("       1 \n") ; 
			sql.append("      ELSE \n") ; 
			sql.append("       0 \n") ; 
			sql.append("    END ) AS icorpnums \n") ;
		}
		sql.append("  FROM cn_contract t  \n") ; 
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append(" AND nvl(ct.dr, 0) = 0  \n") ; 
		if(qrytype == 1){
			sql.append("   AND t.vstatus IN (?, ?, ?) \n") ;
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}else if(qrytype == 2){
			sql.append("   AND t.vstatus IN (?, ?) \n") ; 
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}
//		sql.append("   AND nvl(t.isncust, 'N') = 'N' \n") ; 
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
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){
			sql.append(" AND ( nvl(t.ndeductmny,0) != 0 OR nvl(t.ndedsummny,0) = 0 ) ");
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){
			sql.append(" AND nvl(t.ndedrebamny,0) != 0 ");
		}
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
	 * @param pk_corplist
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
//				if(!pk_corplist.contains(vo.getPk_corp())){
//					pk_corplist.add(vo.getPk_corp());
//				}
				custmap.put(vo.getPk_corp(), vo.getIstocknum());
			}
		}
		return custmap;
	}

}
