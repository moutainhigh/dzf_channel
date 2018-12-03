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
import com.dzf.model.channel.report.AchievementVO;
import com.dzf.model.channel.report.ContQryVO;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.IChannelDataService;

@Service("channelData")
public class ChannelDataServiceImpl implements IChannelDataService{

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Override
	public AchievementVO query(QryParamVO paramvo) throws DZFWarpException{
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){//按照月度查询
			return qryChartMonthData(paramvo);
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){//按照季度查询
			return qryChartSeasonData(paramvo);
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 3){//按照年度查询
			return qryChartYearData(paramvo);
		}
		return null;
	}
	
	/**
	 * 获取柱状图-查询数据
	 * @param paramvo
	 * @param powmap
	 * @param qrylist
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, ContQryVO> getMnyMap(QryParamVO paramvo, List<String> qrylist) throws DZFWarpException {
		Map<String, ContQryVO> map = new HashMap<String, ContQryVO>();
		String qrysql = "";
		List<ContQryVO> zlist = null;
		List<ContQryVO> flist = null;
		if(paramvo.getQrytype() != null && (paramvo.getQrytype() == 1 || paramvo.getQrytype() == 2)){
			qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.deductdata, 1, 7)", qrylist.toArray(new String[0]));
			zlist = qryPositiveData(qrysql, "SUBSTR(t.deductdata, 1, 7)");// 合同审核
			qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.dchangetime, 1, 7)", qrylist.toArray(new String[0]));
			flist = qryNegativeData(qrysql, "SUBSTR(t.dchangetime, 1, 7)");//合同变更 
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 3){
			qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.deductdata, 1, 4)", qrylist.toArray(new String[0]));
			zlist = qryPositiveData(qrysql, "SUBSTR(t.deductdata, 1, 4)");// 合同审核
			qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.dchangetime, 1, 4)", qrylist.toArray(new String[0]));
			flist = qryNegativeData(qrysql, "SUBSTR(t.dchangetime, 1, 4)");//合同变更 
		}
		String season = null;
		ContQryVO getVO=null;
		if(zlist != null && zlist.size() > 0){
			for (ContQryVO zvo : zlist) {
				if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){//按照季度查询
					season = ToolsUtil.getSeason(zvo.getVperiod());
					if(!map.containsKey(season)){
						map.put(season, zvo);
					}else{
						getVO= map.get(season);
						getVO.setNdedsummny(SafeCompute.add(getVO.getNdedsummny(), zvo.getNdedsummny()));
						getVO.setNdedsummny(SafeCompute.add(getVO.getNaccountmny(), zvo.getNaccountmny()));
//						map.put(season,getVO);
					}
				}else{
					map.put(zvo.getVperiod(), zvo);
				}
			}
		}
		if(flist != null && flist.size() > 0){
			for (ContQryVO fvo : flist) {
				if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){//按照季度查询
					season = ToolsUtil.getSeason(fvo.getVperiod());
					if(!map.containsKey(season)){
						map.put(season, fvo);
					}else{
						getVO= map.get(season);
						getVO.setNdedsummny(SafeCompute.add(getVO.getNdedsummny(), fvo.getNdedsummny()));
						getVO.setNdedsummny(SafeCompute.add(getVO.getNaccountmny(), fvo.getNaccountmny()));
//						map.put(season,getVO);
					}
				}else{
					map.put(fvo.getVperiod(), fvo);
				}
			}
		}
		return map;
	}
	
	
	/**
	 * 查询合同审核时，合同的扣款金额和预付款金额（正数）
	 * @param powmap
	 * @param qrysql
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<ContQryVO> qryPositiveData(String qrysql, String qrtdate) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT ").append(qrtdate).append(" AS vperiod,  \n");
		sql.append("       SUM(nvl(t.ndeductmny, 0)) AS ndedsummny,  \n");//扣款
		sql.append("       SUM(nvl(t.ndedrebamny, 0)) AS naccountmny  \n");//预付款
		sql.append("  FROM cn_contract t  \n");
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.isncust, 'N') = 'N'  \n");
		sql.append("   AND t.vdeductstatus in (?, ?, ?)  \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		if (!StringUtil.isEmpty(qrysql)) {
			sql.append(" AND ").append(qrysql);
		}
		sql.append(" GROUP BY ").append(qrtdate).append(" \n");
		sql.append(" ORDER BY ").append(qrtdate).append(" \n");
		return (List<ContQryVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ContQryVO.class));
	}

	/**
	 * 查询合同变更时，合同的扣款金额和预付款金额（负数）
	 * @param powmap
	 * @param qrylist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<ContQryVO> qryNegativeData(String qrysql, String qrtdate) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT ").append(qrtdate).append(" AS vperiod,  \n");
		sql.append("       SUM(nvl(t.nsubdeductmny, 0)) AS ndedsummny,  \n");//扣款
		sql.append("       SUM(nvl(t.nsubdedrebamny, 0)) AS naccountmny  \n");//预付款 
		sql.append("  FROM cn_contract t  \n");
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.isncust, 'N') = 'N'  \n");
		sql.append("   AND t.vdeductstatus in (?, ?)  \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		if (!StringUtil.isEmpty(qrysql)) {
			sql.append(" AND ").append(qrysql);
		}
		sql.append(" GROUP BY ").append(qrtdate).append("  \n");
		sql.append(" ORDER BY ").append(qrtdate).append(" \n");
		return (List<ContQryVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ContQryVO.class));
	}
	
	/**
	 * 查询月度数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	private AchievementVO qryChartMonthData(QryParamVO paramvo) throws DZFWarpException {
		AchievementVO retvo = new AchievementVO();
		List<DZFDouble> ndeductmny = new ArrayList<DZFDouble>();
		List<DZFDouble> ndedrebamny = new ArrayList<DZFDouble>();
		
		if(paramvo.getBeginperiod().compareTo(paramvo.getEndperiod()) > 0){
			throw new BusinessException("开始月度不能大于结束季度");
		}
		List<String> showdate = ToolsUtil.getPeriodsBp(paramvo.getBeginperiod(), paramvo.getEndperiod());
		if(showdate != null && showdate.size() > 0){
			if(showdate.size() > 12){
				throw new BusinessException("月度查询不能大于12个月");
			}
		}else{
			throw new BusinessException("月度查询条件不能为空");
		}
		if(showdate != null && showdate.size() > 0){
			Map<String, ContQryVO> map = getMnyMap(paramvo, showdate);
			DZFDouble submny = DZFDouble.ZERO_DBL;
			String period;
			ContQryVO getVO; 
			for(int i = 0; i < showdate.size(); i++){
				period = showdate.get(i);
				getVO= map.get(period);
				if(getVO==null){
					ndeductmny.add(submny);
					ndedrebamny.add(submny);
				}else{
					ndeductmny.add(getVO.getNdedsummny());
					ndedrebamny.add(getVO.getNaccountmny());
				}
			}
			retvo.setShowdate(showdate);
			retvo.setFirst(ndeductmny);
			retvo.setSecond(ndedrebamny);
		}
		return retvo;
	}
	
	/**
	 * 查询季度数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	private AchievementVO qryChartSeasonData(QryParamVO paramvo) throws DZFWarpException {
		AchievementVO retvo = new AchievementVO();
		List<DZFDouble> ndeductmny = new ArrayList<DZFDouble>();
		List<DZFDouble> ndedrebamny = new ArrayList<DZFDouble>();
		if(paramvo.getBeginperiod().compareTo(paramvo.getEndperiod()) > 0){
			throw new BusinessException("开始季度不能大于结束季度");
		}
		List<String> showdate = ToolsUtil.getSeasonsBp(paramvo.getBeginperiod(), paramvo.getEndperiod());
		if(showdate != null && showdate.size() > 0){
			if(showdate.size() > 8){
				throw new BusinessException("季度查询不能大于8季度");
			}
		}else{
			throw new BusinessException("季度查询条件不能为空");
		}
		List<String> qrylist = ToolsUtil.getPeriodsBp(paramvo.getBeginperiod(), paramvo.getEndperiod());
		
		if(qrylist != null && qrylist.size() > 0){
			Map<String, ContQryVO> map = getMnyMap(paramvo, qrylist);
			DZFDouble submny = DZFDouble.ZERO_DBL;
			String period;
			ContQryVO getVO; 
			for(int i = 0; i < showdate.size(); i++){
				period = ToolsUtil.getSeason(showdate.get(i));
				getVO= map.get(period);
				if(getVO==null){
					ndeductmny.add(submny);
					ndedrebamny.add(submny);
				}else{
					ndeductmny.add(getVO.getNdedsummny());
					ndedrebamny.add(getVO.getNaccountmny());
				}
			}
			retvo.setShowdate(showdate);
			retvo.setFirst(ndeductmny);
			retvo.setSecond(ndedrebamny);
		}
		return retvo;
	}
	
	/**
	 * 查询年度数据
	 * @param paramvo
	 * @param powmap
	 * @return
	 * @throws DZFWarpException
	 */
	private AchievementVO qryChartYearData(QryParamVO paramvo) throws DZFWarpException {
		AchievementVO retvo = new AchievementVO();
		List<DZFDouble> ndeductmny = new ArrayList<DZFDouble>();
		List<DZFDouble> ndedrebamny = new ArrayList<DZFDouble>();
		Integer byear = Integer.parseInt(paramvo.getBeginperiod());
		Integer eyear = Integer.parseInt(paramvo.getEndperiod());
		if(byear - eyear > 0){
			throw new BusinessException("开始年度不能大于结束年度");
		}
		List<String> showdate = new ArrayList<String>();
		for(int i = byear; i <= eyear; i++){
			showdate.add(String.valueOf(i));
		}
		if(showdate != null && showdate.size() > 0){
			if(showdate.size() > 3){
				throw new BusinessException("年度查询不能大于3年");
			}
		}else{
			throw new BusinessException("年度查询条件不能为空");
		}
		if(showdate != null && showdate.size() > 0){
			Map<String, ContQryVO> map = getMnyMap(paramvo, showdate);
			DZFDouble submny = DZFDouble.ZERO_DBL;
			String period;
			ContQryVO getVO; 
			for(int i = 0; i < showdate.size(); i++){
				period = showdate.get(i);
				getVO= map.get(period);
				if(getVO==null){
					ndeductmny.add(submny);
					ndedrebamny.add(submny);
				}else{
					ndeductmny.add(getVO.getNdedsummny());
					ndedrebamny.add(getVO.getNaccountmny());
				}
			}
			retvo.setShowdate(showdate);
			retvo.setFirst(ndeductmny);
			retvo.setSecond(ndedrebamny);
		}
		return retvo;
	}
	
}
