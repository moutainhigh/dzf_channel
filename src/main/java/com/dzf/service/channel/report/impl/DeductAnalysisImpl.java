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
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.cache.CorpCache;
import com.dzf.service.channel.report.IDeductAnalysis;

@Service("deductanalysisser")
public class DeductAnalysisImpl implements IDeductAnalysis {
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@SuppressWarnings("unchecked")
	@Override
	public List<DeductAnalysisVO> query(QryParamVO paramvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp, \n");
		sql.append("       SUM(nvl(t.ndedsummny, 0) + nvl(t.nsubdedsummny, 0)) AS ndeductmny, \n");
		sql.append("       COUNT(t.pk_corpk) AS icorpnums \n");
		sql.append("  FROM cn_contract t \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n");
		sql.append("   AND t.vstatus IN (?, ?) \n");
//		sql.append("   AND t.pk_corp = 'mx9cKz' \n");
		sql.append(" GROUP BY ndeductmny, t.pk_corp \n");
		sql.append(" ORDER BY t.pk_corp \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		List<DeductAnalysisVO> list = (List<DeductAnalysisVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(DeductAnalysisVO.class));
		if(list != null && list.size() > 0){
			Map<String,DeductAnalysisVO> map = queryTotal(paramvo);
			DeductAnalysisVO totalvo = null;
			CorpVO corpvo = null;
			for(DeductAnalysisVO vo : list){
				corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if(corpvo != null){
					vo.setCorpcode(corpvo.getInnercode());
					vo.setCorpname(corpvo.getUnitname());
				}
				totalvo = map.get(vo.getPk_corp());
				if(totalvo != null){
					vo.setIcorpnums_sum(totalvo.getIcorpnums_sum());
					vo.setNdeductmny_sum(totalvo.getNdeductmny_sum());
				}
			}
		}
		return list;
	}
	
	/**
	 * 查询汇总情况
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String,DeductAnalysisVO> queryTotal(QryParamVO paramvo) throws DZFWarpException {
		Map<String,DeductAnalysisVO> map = new HashMap<String,DeductAnalysisVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp, \n") ;
		sql.append("       SUM(nvl(t.ndedsummny, 0) + nvl(t.nsubdedsummny, 0)) AS ndeductmny_sum, \n") ; 
		sql.append("       COUNT(t.pk_corpk) AS icorpnums_sum \n") ; 
		sql.append("  FROM cn_contract t \n") ; 
		sql.append(" WHERE nvl(t.dr, 0) = 0 \n") ; 
		sql.append("   AND t.vstatus IN (?, ?)\n") ; 
		sql.append(" GROUP BY t.pk_corp \n") ; 
		sql.append(" ORDER BY t.pk_corp \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		List<DeductAnalysisVO> list = (List<DeductAnalysisVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(DeductAnalysisVO.class));
		if(list != null && list.size() > 0){
			for(DeductAnalysisVO vo : list){
				map.put(vo.getPk_corp(), vo);
			}
		}
		return map;
	}

}
