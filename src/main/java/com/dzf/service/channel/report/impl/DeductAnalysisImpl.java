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
import com.dzf.model.channel.report.ReportDatagridColumn;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.IDeductAnalysis;
import com.dzf.service.pub.IPubService;

@Service("deductanalysisser")
public class DeductAnalysisImpl implements IDeductAnalysis {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IPubService pubser;

	/**
	 * 查询扣款明细
	 * @param pk_corp
	 * @param dedvo
	 * @param bakvo
	 * @param bakmap
	 * @param detmap
	 * @param dedmap
	 * @param custmap
	 * @param num
	 * @param connum
	 * @param icustnum
	 * @param izeronum
	 * @param idednum
	 * @param ndedsummny
	 * @param dvo
	 * @return
	 * @throws DZFWarpException
	 */
	private DeductAnalysisVO getDeductCorp(String pk_corp, DeductAnalysisVO dedvo, DeductAnalysisVO bakvo,
			Map<String, DeductAnalysisVO> bakmap, Map<String, List<DeductAnalysisVO>> detmap,
			Map<String, DeductAnalysisVO> dedmap, Map<String, Integer> custmap, int num, int connum, int icustnum,
			int izeronum, int idednum, DZFDouble ndedsummny, DeductAnalysisVO dvo) throws DZFWarpException {
		DeductAnalysisVO retvo = new DeductAnalysisVO();
		retvo.setPk_corp(pk_corp);
		CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
		if (corpvo != null) {
			retvo.setCorpcode(corpvo.getInnercode());
			retvo.setCorpname(corpvo.getUnitname());
			if(corpvo.getDrelievedate() != null){
				retvo.setDrelievedate(corpvo.getDrelievedate());
			}
		}
		retvo.setNdeducmny(dvo.getNdeducmny());
		retvo.setIcorpnums(dvo.getIcorpnums());
		if (bakmap != null && !bakmap.isEmpty()) {
			bakvo = bakmap.get(pk_corp);
			if (bakvo != null) {
				retvo.setIretnum(bakvo.getIcorpnums());// 退回合同数
				retvo.setNretmny(bakvo.getNdeducmny());// 退回金额

				retvo.setIcustnum(bakvo.getIcustnum());// 存量合同数
				retvo.setIzeronum(bakvo.getIzeronum());// 0扣款(非存量)合同数
				retvo.setIdednum(bakvo.getIdednum());// 非存量合同数
			}
		}
		if (dedmap != null && !dedmap.isEmpty()) {
			dedvo = dedmap.get(pk_corp);
			if (dedvo != null) {
				if (num == 0) {
					connum = ToolsUtil.subInteger(dedvo.getIcorpnums(), retvo.getIretnum());
					ndedsummny = SafeCompute.sub(dedvo.getNdeducmny(), retvo.getNretmny());

					icustnum = ToolsUtil.addInteger(dedvo.getIcustnum(), retvo.getIcustnum());// 存量合同数
					izeronum = ToolsUtil.addInteger(dedvo.getIzeronum(), retvo.getIzeronum());// 0扣款(非存量)合同数
					idednum = ToolsUtil.addInteger(dedvo.getIdednum(), retvo.getIdednum());// 非存量合同数
				}
				retvo.setIcorpnums_sum(connum);// 总合同数
				retvo.setNdeductmny_sum(ndedsummny);// 总扣款

				retvo.setIcustnum(icustnum);// 存量合同数
				retvo.setIzeronum(izeronum);// 0扣款(非存量)合同数
				retvo.setIdednum(idednum);// 非存量合同数
			} else {// 此公司无扣款金额，只有退款金额，但是别的公司有退款金额，计算总合同数和总扣款
				if (num == 0) {
					connum = ToolsUtil.subInteger(0, retvo.getIretnum());
					ndedsummny = SafeCompute.sub(DZFDouble.ZERO_DBL, retvo.getNretmny());
					icustnum = ToolsUtil.addInteger(0, retvo.getIcustnum());// 存量合同数
					izeronum = ToolsUtil.addInteger(0, retvo.getIzeronum());// 0扣款(非存量)合同数
					idednum = ToolsUtil.addInteger(0, retvo.getIdednum());// 非存量合同数
				}
				retvo.setIcorpnums_sum(connum);// 总合同数
				retvo.setNdeductmny_sum(ndedsummny);// 总扣款

				retvo.setIcustnum(icustnum);// 存量合同数
				retvo.setIzeronum(izeronum);// 0扣款(非存量)合同数
				retvo.setIdednum(idednum);// 非存量合同数
			}
		} else {// 无扣款金额，只有退款金额，计算总合同数和总扣款
			if (num == 0) {
				connum = ToolsUtil.subInteger(0, retvo.getIretnum());
				ndedsummny = SafeCompute.sub(DZFDouble.ZERO_DBL, retvo.getNretmny());
				icustnum = ToolsUtil.addInteger(0, retvo.getIcustnum());// 存量合同数
				izeronum = ToolsUtil.addInteger(0, retvo.getIzeronum());// 0扣款(非存量)合同数
				idednum = ToolsUtil.addInteger(0, retvo.getIdednum());// 非存量合同数
			}
			retvo.setIcorpnums_sum(connum);// 总合同数
			retvo.setNdeductmny_sum(ndedsummny);// 总扣款
			retvo.setIcustnum(icustnum);// 存量合同数
			retvo.setIzeronum(izeronum);// 0扣款(非存量)合同数
			retvo.setIdednum(idednum);// 非存量合同数
		}
		if (custmap != null && !custmap.isEmpty()) {
			retvo.setIstocknum(custmap.get(retvo.getPk_corp()));
		}
		return retvo;
	}

	/**
	 * 查询公司没有扣款明细
	 * 
	 * @param pk_corp
	 * @param dedvo
	 * @param bakvo
	 * @param bakmap
	 * @param detmap
	 * @param dedmap
	 * @param custmap
	 * @param num
	 * @param connum
	 * @param icustnum
	 * @param izeronum
	 * @param idednum
	 * @param ndedsummny
	 * @return
	 * @throws DZFWarpException
	 */
	private DeductAnalysisVO getNullDeductCorp(String pk_corp, DeductAnalysisVO dedvo, DeductAnalysisVO bakvo,
			Map<String, DeductAnalysisVO> bakmap, Map<String, List<DeductAnalysisVO>> detmap,
			Map<String, DeductAnalysisVO> dedmap, Map<String, Integer> custmap, int num, int connum, int icustnum,
			int izeronum, int idednum, DZFDouble ndedsummny) throws DZFWarpException {
		DeductAnalysisVO retvo = new DeductAnalysisVO();
		retvo.setPk_corp(pk_corp);
		CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
		if (corpvo != null) {
			retvo.setCorpcode(corpvo.getInnercode());
			retvo.setCorpname(corpvo.getUnitname());
		}
		if (bakmap != null && !bakmap.isEmpty()) {
			bakvo = bakmap.get(pk_corp);
			if (bakvo != null) {
				retvo.setIretnum(bakvo.getIcorpnums());// 退回合同数
				retvo.setNretmny(bakvo.getNdeducmny());// 退回金额

				retvo.setIcustnum(bakvo.getIcustnum());// 存量合同数
				retvo.setIzeronum(bakvo.getIzeronum());// 0扣款(非存量)合同数
				retvo.setIdednum(bakvo.getIdednum());// 非存量合同数
			}
		}
		if (dedmap != null && !dedmap.isEmpty()) {
			dedvo = dedmap.get(pk_corp);
			if (dedvo != null) {
				retvo.setIcorpnums_sum(ToolsUtil.subInteger(dedvo.getIcorpnums(), retvo.getIretnum()));// 总合同数
				retvo.setNdeductmny_sum(SafeCompute.sub(dedvo.getNdeducmny(), retvo.getNretmny()));// 总扣款

				retvo.setIcustnum(ToolsUtil.addInteger(dedvo.getIcustnum(), retvo.getIcustnum()));// 存量合同数
				retvo.setIzeronum(ToolsUtil.addInteger(dedvo.getIzeronum(), retvo.getIzeronum()));// 0扣款(非存量)合同数
				retvo.setIdednum(ToolsUtil.addInteger(dedvo.getIdednum(), retvo.getIdednum()));// 非存量合同数
			} else {// 此公司无扣款金额，只有退款金额，但是别的公司有退款金额，计算总合同数和总扣款
				if (num == 0) {
					connum = ToolsUtil.subInteger(0, retvo.getIretnum());
					ndedsummny = SafeCompute.sub(DZFDouble.ZERO_DBL, retvo.getNretmny());

					icustnum = ToolsUtil.addInteger(0, retvo.getIcustnum());// 存量合同数
					izeronum = ToolsUtil.addInteger(0, retvo.getIzeronum());// 0扣款(非存量)合同数
					idednum = ToolsUtil.addInteger(0, retvo.getIdednum());// 非存量合同数
				}
				retvo.setIcorpnums_sum(connum);// 总合同数
				retvo.setNdeductmny_sum(ndedsummny);// 总扣款
				retvo.setIcustnum(icustnum);// 存量合同数
				retvo.setIzeronum(izeronum);// 0扣款(非存量)合同数
				retvo.setIdednum(idednum);// 非存量合同数
			}
		} else {// 无扣款金额，只有退款金额，计算总合同数和总扣款
			retvo.setIcorpnums_sum(ToolsUtil.subInteger(0, retvo.getIretnum()));// 总合同数
			retvo.setNdeductmny_sum(SafeCompute.sub(DZFDouble.ZERO_DBL, retvo.getNretmny()));// 总扣款
			retvo.setIcustnum(ToolsUtil.addInteger(0, retvo.getIcustnum()));// 存量合同数
			retvo.setIzeronum(ToolsUtil.addInteger(0, retvo.getIzeronum()));// 0扣款(非存量)合同数
			retvo.setIdednum(ToolsUtil.addInteger(0, retvo.getIdednum()));// 非存量合同数
		}
		if (custmap != null && !custmap.isEmpty()) {
			retvo.setIstocknum(custmap.get(retvo.getPk_corp()));
		}

		return retvo;
	}

	/**
	 * 查询日期校验
	 * 
	 * @param paramvo
	 * @throws DZFWarpException
	 */
	private void checkQryDate(QryParamVO paramvo) throws DZFWarpException {
		if (!StringUtil.isEmpty(paramvo.getBeginperiod()) && !StringUtil.isEmpty(paramvo.getEndperiod())) {
			if (paramvo.getBeginperiod().compareTo(paramvo.getEndperiod()) > 0) {
				throw new BusinessException("开始查询期间不能大于结束查询期间");
			}
		} else if (paramvo.getBegdate() != null && paramvo.getEnddate() != null) {
			if (paramvo.getBegdate().compareTo(paramvo.getEnddate()) > 0) {
				throw new BusinessException("开始查询日期不能大于结束日期");
			}
		}

	}

	/**
	 * 查询扣款明细
	 * 
	 * @param paramvo
	 * @param pk_corplist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, List<DeductAnalysisVO>> queryDedMnyDetail(QryParamVO paramvo, List<String> pk_corplist)
			throws DZFWarpException {
		Map<String, List<DeductAnalysisVO>> detmap = new HashMap<String, List<DeductAnalysisVO>>();
		QrySqlSpmVO qryvo = getQrySqlSpm(paramvo, 1);
		List<DeductAnalysisVO> list = (List<DeductAnalysisVO>) singleObjectBO.executeQuery(qryvo.getSql(),
				qryvo.getSpm(), new BeanListProcessor(DeductAnalysisVO.class));
		if (list != null && list.size() > 0) {
			List<DeductAnalysisVO> newlist = null;
			List<DeductAnalysisVO> oldlist = null;
			for (DeductAnalysisVO detvo : list) {
				if (!pk_corplist.contains(detvo.getPk_corp())) {
					pk_corplist.add(detvo.getPk_corp());
				}
				if (!detmap.containsKey(detvo.getPk_corp())) {
					newlist = new ArrayList<DeductAnalysisVO>();
					newlist.add(detvo);
					detmap.put(detvo.getPk_corp(), newlist);
				} else {
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
	 * 
	 * @param paramvo
	 * @param qrytype
	 *            1：扣款金额汇总查询；2：扣款金额排序查询；
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(QryParamVO paramvo, int qrytype) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT   \n");
		if (qrytype == 1) {
			sql.append("   t.pk_corp,  \n");
		}
		if (paramvo.getQrytype() != null && paramvo.getQrytype() == 1) {// 预付款
			sql.append(" nvl(t.ndeductmny,0) AS ndeducmny, \n");
		} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == 2) {// 返点
			sql.append(" nvl(t.ndedrebamny,0) AS ndeducmny, \n");
		} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == -1) {// 全部
			sql.append(" nvl(t.ndedsummny,0) AS ndeducmny, \n");
		}
		sql.append(" COUNT(t.pk_confrim) AS icorpnums  \n");
		sql.append("  FROM cn_contract t  \n");
		sql.append("  LEFT JOIN bd_account account ON account.pk_corp = t.pk_corp \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND t.vstatus IN (?, ?, ?) \n");
		sql.append("   AND "+QueryUtil.getWhereSql()+" \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);// 暂不统计作废数据
		if (!StringUtil.isEmpty(paramvo.getBeginperiod())) {
			sql.append(" AND SUBSTR(t.deductdata,1,7) >= ? \n");
			spm.addParam(paramvo.getBeginperiod());
		}
		if (!StringUtil.isEmpty(paramvo.getEndperiod())) {
			sql.append(" AND SUBSTR(t.deductdata,1,7) <= ? \n");
			spm.addParam(paramvo.getEndperiod());
		}
		if (paramvo.getBegdate() != null) {
			sql.append(" AND t.deductdata >= ? \n");
			spm.addParam(paramvo.getBegdate());
		}
		if (paramvo.getEnddate() != null) {
			sql.append(" AND t.deductdata <= ? \n");
			spm.addParam(paramvo.getEnddate());
		}
		if (!StringUtil.isEmpty(paramvo.getPk_corp())) {
			String[] corps = paramvo.getPk_corp().split(",");
			String where = SqlUtil.buildSqlForIn(" t.pk_corp ", corps);
			sql.append(" AND ").append(where);
		}
		if (paramvo.getQrytype() != null && paramvo.getQrytype() == 1) {
			sql.append(" AND ( nvl(t.ndeductmny,0) != 0 OR nvl(t.ndedsummny,0) = 0 ) ");
		} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == 2) {
			sql.append(" AND nvl(t.ndedrebamny,0) != 0 ");
		}
		if (!StringUtil.isEmpty(paramvo.getVqrysql())) {
			sql.append(paramvo.getVqrysql());
		}
		sql.append(" GROUP BY \n");
		if (paramvo.getQrytype() != null && paramvo.getQrytype() == 1) {
			sql.append(" nvl(t.ndeductmny,0) ");
		} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == 2) {
			sql.append(" nvl(t.ndedrebamny,0) ");
		} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == -1) {
			sql.append(" nvl(t.ndedsummny,0) ");
		}
		if (qrytype == 1) {
			sql.append(" ,t.pk_corp  \n");
		}
		sql.append(" ORDER BY ");
		if (qrytype == 1) {
			sql.append(" t.pk_corp");
		} else if (qrytype == 2) {
			if (paramvo.getQrytype() != null && paramvo.getQrytype() == 1) {
				sql.append(" nvl(t.ndeductmny,0) ");
			} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == 2) {
				sql.append(" nvl(t.ndedrebamny,0) ");
			} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == -1) {
				sql.append(" nvl(t.ndedsummny,0) ");
			}
			sql.append(" ASC");
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	/**
	 * 查询金额汇总数据
	 * 
	 * @param paramvo
	 * @param qrytype
	 *            1：扣款；2：退款
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, DeductAnalysisVO> queryTotalMny(QryParamVO paramvo, int qrytype, List<String> pk_corplist)
			throws DZFWarpException {
		Map<String, DeductAnalysisVO> map = new HashMap<String, DeductAnalysisVO>();
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		if (qrytype == 1) {
			qryvo = getKkTotalQrySqlSpm(paramvo, qrytype);
		} else if (qrytype == 2) {
			qryvo = getTkTotalQrySqlSpm(paramvo, qrytype);
		}
		List<DeductAnalysisVO> list = (List<DeductAnalysisVO>) singleObjectBO.executeQuery(qryvo.getSql(),
				qryvo.getSpm(), new BeanListProcessor(DeductAnalysisVO.class));
		if (list != null && list.size() > 0) {
			for (DeductAnalysisVO vo : list) {
				if (!pk_corplist.contains(vo.getPk_corp())) {
					pk_corplist.add(vo.getPk_corp());
				}
				map.put(vo.getPk_corp(), vo);
			}
		}
		return map;
	}

	/**
	 * 获取扣款数据查询sql
	 * 
	 * @param paramvo
	 * @param qrytype
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getKkTotalQrySqlSpm(QryParamVO paramvo, int qrytype) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,  \n");
		if (paramvo.getQrytype() != null && paramvo.getQrytype() == 1) {// 预付款
			sql.append(" SUM(nvl(t.ndeductmny,0)) AS ndeducmny, \n");
		} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == 2) {// 返点
			sql.append(" SUM(nvl(t.ndedrebamny,0)) AS ndeducmny, \n");
		} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == -1) {// 全部
			sql.append(" SUM(nvl(t.ndedsummny,0)) AS ndeducmny, \n");
		}
		sql.append("       SUM(CASE  \n");
		sql.append(
				"             WHEN nvl(ct.isncust, 'N') = 'Y' AND nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 THEN  \n");
		sql.append("              1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS icustnum,  \n");
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(t.ideductpropor, 0) = 0 \n");
		sql.append("                  AND nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 THEN  \n");
		sql.append("              1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS izeronum,  \n");
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(t.ideductpropor, 0) != 0   \n");
		sql.append("                  AND nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 THEN  \n");
		sql.append("              1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS idednum,  \n");

		// 合同数量去掉补提单合同数
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.patchstatus,0) != 2 AND nvl(ct.patchstatus, 0) != 5 THEN  \n");
		sql.append("              1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS icorpnums  \n");// 合同数量

		sql.append("  FROM cn_contract t  \n");
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append("  LEFT JOIN bd_account account ON t.pk_corp = account.pk_corp \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append(" AND nvl(ct.dr, 0) = 0  \n");
		sql.append(" AND "+QueryUtil.getWhereSql()+" \n");
		if (!StringUtil.isEmpty(paramvo.getBeginperiod())) {
			sql.append(" AND SUBSTR(t.deductdata,1,7) >= ? \n");
			spm.addParam(paramvo.getBeginperiod());
		}
		if (!StringUtil.isEmpty(paramvo.getEndperiod())) {
			sql.append(" AND SUBSTR(t.deductdata,1,7) <= ? \n");
			spm.addParam(paramvo.getEndperiod());
		}
		if (paramvo.getBegdate() != null) {
			sql.append(" AND t.deductdata >= ? \n");
			spm.addParam(paramvo.getBegdate());
		}
		if (paramvo.getEnddate() != null) {
			sql.append(" AND t.deductdata <= ? \n");
			spm.addParam(paramvo.getEnddate());
		}
		if (!StringUtil.isEmpty(paramvo.getPk_corp())) {
			String[] corps = paramvo.getPk_corp().split(",");
			String where = SqlUtil.buildSqlForIn(" t.pk_corp ", corps);
			sql.append(" AND ").append(where);
		}
		if (paramvo.getQrytype() != null && paramvo.getQrytype() == 1) {// 预付款扣款
			// 正常和作废扣款：1、预付款扣款金额不为0；2、扣款总金额为0；
			// 变更扣款：1、状态为变更
			sql.append(
					" AND ( (  ( nvl(t.ndeductmny,0) != 0 OR nvl(t.ndedsummny,0) = 0 )  AND t.vstatus IN (?, ?) ) \n");
			sql.append(" OR t.vstatus = ?   )\n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);

		} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == 2) {// 返点扣款
			// 正常和作废扣款：1、返点扣款金额不为0；
			// 变更扣款：1、状态为变更，返点扣款金额不为0；
			sql.append(" AND ( nvl(t.ndedrebamny,0) != 0 AND t.vstatus IN (?, ?, ?) )  \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		} else {
			sql.append("   AND t.vstatus IN (?, ?, ?) \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}
		if (!StringUtil.isEmpty(paramvo.getVqrysql())) {
			sql.append(paramvo.getVqrysql());
		}
		sql.append(" GROUP BY t.pk_corp \n");
		sql.append(" ORDER BY t.pk_corp \n");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	/**
	 * 获取退款数据查询sql
	 * 
	 * @param paramvo
	 * @param qrytype
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getTkTotalQrySqlSpm(QryParamVO paramvo, int qrytype) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,  \n");
		if (paramvo.getQrytype() != null && paramvo.getQrytype() == 1) {// 预付款
			sql.append(" SUM(nvl(t.nretdedmny,0)) AS ndeducmny, \n");
		} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == 2) {// 返点
			sql.append(" SUM(nvl(t.nretrebmny,0)) AS ndeducmny, \n");
		} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == -1) {// 全部
			sql.append(" SUM(nvl(t.nreturnmny,0)) AS ndeducmny, \n");
		}
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'Y' AND nvl(ct.patchstatus, 0) != 2   \n");
		sql.append("             	  AND nvl(ct.patchstatus, 0) != 5 AND t.vstatus = 10 THEN  \n");
		sql.append("              -1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS icustnum,  \n"); // 存量合同数：不统计小规模转一般人和一般人转小规模
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(t.ideductpropor, 0) = 0   \n");
		sql.append(
				"                  AND nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 AND t.vstatus = 10 THEN  \n");
		sql.append("              -1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS izeronum,  \n"); // 0扣款(非存量)合同数：不统计小规模转一般人和一般人转小规模
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(t.ideductpropor, 0) != 0   \n");
		sql.append(
				"                  AND nvl(ct.patchstatus, 0) != 2 AND nvl(ct.patchstatus, 0) != 5 AND t.vstatus = 10 THEN  \n");
		sql.append("              -1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS idednum,  \n"); // 非存量合同数：不统计小规模转一般人和一般人转小规模
		sql.append(" SUM( ");
		sql.append("    CASE t.vstatus \n");
		sql.append("      WHEN 10 THEN \n");
		sql.append("       1 \n");
		sql.append("      ELSE \n");
		sql.append("       0 \n");
		sql.append("    END ) AS icorpnums \n");// 退款合同数量

		sql.append("  FROM cn_contract t  \n");
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append("  LEFT JOIN bd_account account ON t.pk_corp = account.pk_corp \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append(" AND nvl(ct.dr, 0) = 0  \n");
		sql.append(" AND "+QueryUtil.getWhereSql()+" \n");
		if (!StringUtil.isEmpty(paramvo.getBeginperiod())) {
			sql.append(" AND SUBSTR(t.dchangetime,1,7) >= ? \n");
			spm.addParam(paramvo.getBeginperiod());
		}
		if (!StringUtil.isEmpty(paramvo.getEndperiod())) {
			sql.append(" AND SUBSTR(t.dchangetime,1,7) <= ? \n");
			spm.addParam(paramvo.getEndperiod());
		}
		if (paramvo.getBegdate() != null) {
			sql.append(" AND SUBSTR(t.dchangetime,1,10) >= ? \n");
			spm.addParam(paramvo.getBegdate());
		}
		if (paramvo.getEnddate() != null) {
			sql.append(" AND SUBSTR(t.dchangetime,1,10) <= ? \n");
			spm.addParam(paramvo.getEnddate());
		}
		if (!StringUtil.isEmpty(paramvo.getPk_corp())) {
			String[] corps = paramvo.getPk_corp().split(",");
			String where = SqlUtil.buildSqlForIn(" t.pk_corp ", corps);
			sql.append(" AND ").append(where);
		}
		if (paramvo.getQrytype() != null && paramvo.getQrytype() == 1) {// 预付款扣款
			sql.append(" AND ( t.vstatus = ?   \n");
			sql.append("  OR ( (nvl(t.ndeductmny,0) != 0 OR nvl(t.ndedsummny,0) = 0 ) AND t.vstatus = ? ) ) \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		} else if (paramvo.getQrytype() != null && paramvo.getQrytype() == 2) {// 返点扣款
			sql.append(" AND ( t.vstatus IN (?, ?) AND nvl(t.ndedrebamny,0) != 0 )   \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		} else {
			sql.append("   AND t.vstatus IN (?, ?) \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}
		if (!StringUtil.isEmpty(paramvo.getVqrysql())) {
			sql.append(paramvo.getVqrysql());
		}
		sql.append(" GROUP BY t.pk_corp \n");
		sql.append(" ORDER BY t.pk_corp \n");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@SuppressWarnings("unchecked")
	private List<DeductAnalysisVO> queryMnyOrder(QryParamVO paramvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySqlSpm(paramvo, 2);
		List<DeductAnalysisVO> list = (List<DeductAnalysisVO>) singleObjectBO.executeQuery(qryvo.getSql(),
				qryvo.getSpm(), new BeanListProcessor(DeductAnalysisVO.class));
		return list;
	}

	/**
	 * 查询存量客户数量
	 * 
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
		sql.append("  LEFT JOIN bd_account account ON p.fathercorp = account.pk_corp \n");
		sql.append(" WHERE nvl(p.dr, 0) = 0 \n");
		if (!StringUtil.isEmpty(paramvo.getPk_corp())) {
			String[] corps = paramvo.getPk_corp().split(",");
			String where = SqlUtil.buildSqlForIn(" account.pk_corp ", corps);
			sql.append(" AND ").append(where);
		}
		//sql.append("   AND nvl(account.dr, 0) = 0 \n");
		sql.append("   AND nvl(account.ischannel, 'N') = 'Y'\n"); // 渠道客户
		sql.append("   AND nvl(p.isseal, 'N') = 'N'\n"); // 未封存
		sql.append("   AND nvl(p.isncust,'N') = 'Y' \n"); // 存量客户
		sql.append("   AND "+QueryUtil.getWhereSql()+"\n"); 
		if (!StringUtil.isEmpty(paramvo.getVqrysql())) {
			sql.append(paramvo.getVqrysql());
		}
		sql.append(" GROUP BY p.fathercorp \n");
		List<DeductAnalysisVO> list = (List<DeductAnalysisVO>) singleObjectBO.executeQuery(sql.toString(), null,
				new BeanListProcessor(DeductAnalysisVO.class));
		if (list != null && list.size() > 0) {
			for (DeductAnalysisVO vo : list) {
				custmap.put(vo.getPk_corp(), vo.getIstocknum());
			}
		}
		return custmap;
	}

	@Override
	public String getQrySql(String cuserid, Integer qrytype) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		String[] corps = pubser.getManagerCorp(cuserid, qrytype);
		if (corps != null && corps.length > 0) {
			String where = SqlUtil.buildSqlForIn(" account.pk_corp", corps);
			sql.append(" AND ").append(where);
		} else {
			sql.append(" AND t.pk_corp is null \n");
		}
		return sql.toString();
	}

	@Override
	public Object[] queryColumn(QryParamVO paramvo) throws DZFWarpException {
		Object[] objs = new Object[2];
		List<DeductAnalysisVO> clist = queryMnyOrder(paramvo);
		List<ReportDatagridColumn> hbcolist = new ArrayList<ReportDatagridColumn>();
		List<ReportDatagridColumn> colist = new ArrayList<ReportDatagridColumn>();
		if(clist != null && clist.size() > 0){
			ReportDatagridColumn hbcol = new ReportDatagridColumn();
			ReportDatagridColumn col1 = new ReportDatagridColumn();
			ReportDatagridColumn col2 = new ReportDatagridColumn();
			
			//合并列
			hbcol = new ReportDatagridColumn();
			hbcol.setTitle("退回扣款");
			hbcol.setField("ret");
			hbcol.setColspan(2);
			hbcol.setWidth(180);
			hbcolist.add(hbcol);
			//分项列1
			col1 = new ReportDatagridColumn();
			col1.setField("retnum");
			col1.setTitle("合同数");
			col1.setHalign("center");
			col1.setAlign("right");
			col1.setWidth(90);
			colist.add(col1);
			//分项列2
			col2 = new ReportDatagridColumn();
			col2.setField("retmny");
			col2.setTitle("金额");
			col2.setHalign("center");
			col2.setAlign("right");
			col2.setWidth(90);
			colist.add(col2);
			
			String mny = "";
			for(DeductAnalysisVO dvo : clist){
				mny = String.valueOf(dvo.getNdeducmny().setScale(2, DZFDouble.ROUND_HALF_UP));
				//合并列
				hbcol = new ReportDatagridColumn();
				hbcol.setTitle(mny);
				hbcol.setField(mny);
				hbcol.setColspan(2);
				hbcol.setWidth(180);
				hbcolist.add(hbcol);
				//分项列1
				col1 = new ReportDatagridColumn();
				col1.setField("num_"+mny);
				col1.setTitle("合同数");
				col1.setHalign("center");
				col1.setAlign("right");
				col1.setWidth(90);
				colist.add(col1);
				//分项列2
				col2 = new ReportDatagridColumn();
				col2.setField("mny_"+mny);
				col2.setTitle("扣款");
				col2.setHalign("center");
				col2.setAlign("right");
				col2.setWidth(90);
				colist.add(col2);
			}
			objs[0] = hbcolist;
			objs[1] = colist;
		}
		return objs;
	}

	@Override
	public List<DeductAnalysisVO> queryData(QryParamVO paramvo) throws DZFWarpException {
		// 查询日期校验
		checkQryDate(paramvo);
		// 1、客户主键
		List<String> pk_corplist = new ArrayList<String>();
		// 2、扣款金额明细
		Map<String, List<DeductAnalysisVO>> detmap = queryDedMnyDetail(paramvo, pk_corplist);
		// 3、扣款总金额
		Map<String, DeductAnalysisVO> dedmap = queryTotalMny(paramvo, 1, pk_corplist);
		// 4、退款总金额
		Map<String, DeductAnalysisVO> bakmap = queryTotalMny(paramvo, 2, pk_corplist);
		// 5、存量客户数
		Map<String, Integer> custmap = queryCustNum(paramvo);
		if (pk_corplist != null && pk_corplist.size() > 0) {
			List<DeductAnalysisVO> retlist = getRetData(pk_corplist, detmap, dedmap, bakmap, custmap);
			return retlist;
		}
		return new ArrayList<DeductAnalysisVO>();
	}
	
	/**
	 * 组装返回信息
	 * 
	 * @param pk_corplist
	 * @param detmap
	 * @param dedmap
	 * @param bakmap
	 * @param custmap
	 * @return
	 * @throws DZFWarpException
	 */
	private List<DeductAnalysisVO> getRetData(List<String> pk_corplist, Map<String, List<DeductAnalysisVO>> detmap,
			Map<String, DeductAnalysisVO> dedmap, Map<String, DeductAnalysisVO> bakmap, Map<String, Integer> custmap)
			throws DZFWarpException {
		Map<String, UserVO> marmap = pubser.getManagerMap(IStatusConstant.IQUDAO);// 渠道经理
		Map<String, UserVO> opermap = pubser.getManagerMap(IStatusConstant.IYUNYING);// 渠道运营
		
		List<DeductAnalysisVO> retlist = new ArrayList<DeductAnalysisVO>();
		DeductAnalysisVO retvo = null;
		List<DeductAnalysisVO> detlist = null;
		DeductAnalysisVO dedvo = null;
		DeductAnalysisVO bakvo = null;
		int num = 0;// 循环次数
		int connum = 0;// 合同总数量

		int icustnum = 0;// 存量合同数
		int izeronum = 0;// 0扣款(非存量)合同数
		int idednum = 0;// 非存量合同数
		
		DZFDouble ndedsummny = DZFDouble.ZERO_DBL;
		for (String pk_corp : pk_corplist) {
			num = 0;
			connum = 0;
			icustnum = 0;// 存量合同数
			izeronum = 0;// 0扣款(非存量)合同数
			idednum = 0;// 非存量合同数
			ndedsummny = DZFDouble.ZERO_DBL;
			
			DeductAnalysisVO setvo = null;
			if (detmap != null && !detmap.isEmpty()) {
				detlist = (List<DeductAnalysisVO>) detmap.get(pk_corp);
				if (detlist != null && detlist.size() > 0) {
					setvo = new DeductAnalysisVO();
					num = 0;
					for (DeductAnalysisVO dvo : detlist) {
						connum = 0;
						icustnum = 0;// 存量合同数
						izeronum = 0;// 0扣款(非存量)合同数
						idednum = 0;// 非存量合同数
						ndedsummny = DZFDouble.ZERO_DBL;

						retvo = getDeductCorp(pk_corp, dedvo, bakvo, bakmap, detmap, dedmap, custmap, num, connum,
								icustnum, izeronum, idednum, ndedsummny, dvo);
						setUserName(marmap, opermap, retvo);
						setvo = setHashValue(retvo, num, setvo);
						num++;
					}
					retlist.add(setvo);
				} else {
					// 某公司没有扣款明细
					retvo = getNullDeductCorp(pk_corp, dedvo, bakvo, bakmap, detmap, dedmap, custmap, num, connum,
							icustnum, izeronum, idednum, ndedsummny);
					setUserName(marmap, opermap, retvo);
					setvo = new DeductAnalysisVO();
					num = 0;
					setvo = setHashValue(retvo, num, setvo);
					retlist.add(setvo);
				}
			} else {
				// 查询公司没有扣款明细
				retvo = getNullDeductCorp(pk_corp, dedvo, bakvo, bakmap, detmap, dedmap, custmap, num, connum, icustnum,
						izeronum, idednum, ndedsummny);
				setUserName(marmap, opermap, retvo);
				setvo = new DeductAnalysisVO();
				num = 0;
				setvo = setHashValue(retvo, num, setvo);
				retlist.add(setvo);
			}
		}

		return retlist;
	}
	
	/**
	 * 设置显示数据
	 * @param retvo
	 * @param num
	 * @param setvo
	 * @return
	 * @throws DZFWarpException
	 */
	private DeductAnalysisVO setHashValue(DeductAnalysisVO retvo, Integer num, DeductAnalysisVO setvo) throws DZFWarpException {
		HashMap<String, Object> map = setvo.getHash();
		if(num == 0){
			map.put("corpid", retvo.getPk_corp());
			map.put("corpcode", retvo.getCorpcode());
			map.put("corpname", retvo.getCorpname());
			map.put("dreldate", retvo.getDrelievedate());
			
			if(retvo.getIretnum()!= null){
				map.put("retnum", retvo.getIretnum());
			}
			if(retvo.getNretmny() != null){
				DZFDouble retmny = SafeCompute.multiply(retvo.getNretmny(), new DZFDouble(-1)).setScale(2, DZFDouble.ROUND_HALF_UP);
				map.put("retmny", retmny);
			}
			if(retvo.getIstocknum() != null){
				map.put("stocknum", retvo.getIstocknum());
			}
			if(retvo.getIcustnum() != null){
				map.put("custnum", retvo.getIcustnum());
			}
			if(retvo.getIzeronum() != null){
				map.put("zeronum", retvo.getIzeronum());
			}
			if(retvo.getIdednum() != null){
				map.put("dednum", retvo.getIdednum());
			}
			if(!StringUtil.isEmpty(retvo.getVmanager())){
				map.put("mid", retvo.getVmanager());	
			}
			if(!StringUtil.isEmpty(retvo.getVoperater())){
				map.put("oid", retvo.getVoperater());
			}
			if(retvo.getNdeductmny_sum() != null){
				map.put("summny", retvo.getNdeductmny_sum());
			}
		}
		if(retvo.getNdeducmny() != null){
			DZFDouble ndeductmny = retvo.getNdeducmny().setScale(2, DZFDouble.ROUND_HALF_UP);
			String mny = String.valueOf(ndeductmny);
			map.put("num_"+mny, retvo.getIcorpnums());
			map.put("mny_"+mny, ndeductmny);
		}
		return setvo;
	}
	
	/**
	 * 设置人员显示名称
	 * @param marmap
	 * @param opermap
	 * @param retvo
	 * @throws DZFWarpException
	 */
	private void setUserName(Map<String, UserVO> marmap, Map<String, UserVO> opermap, DeductAnalysisVO retvo) throws DZFWarpException {
		UserVO uservo = null;
		if (marmap != null && !marmap.isEmpty()) {
			uservo = marmap.get(retvo.getPk_corp());
			if (uservo != null) {
				retvo.setVmanager(uservo.getUser_name());// 渠道经理
			}
		}
		if (opermap != null && !opermap.isEmpty()) {
			uservo = opermap.get(retvo.getPk_corp());
			if (uservo != null) {
				retvo.setVoperater(uservo.getUser_name());// 渠道运营
			}
		}
	}
	
}
