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
import com.dzf.model.channel.sale.ChnAreaBVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.QueryUtil;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.IAchievementService;
import com.dzf.service.pub.IPubService;

@Service("achievementser")
public class AchievementServiceImpl implements IAchievementService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IPubService pubser;
	
	private String filtersql = QueryUtil.getWhereSql();

	@Override
	public AchievementVO queryLine(QryParamVO paramvo) throws DZFWarpException {
		Map<Integer, String> powmap = qryUserPower(paramvo);
		if(powmap == null || powmap.isEmpty()){
			return null;
		}
		return queryLineDate(paramvo, powmap);
	}
	
	/**
	 * 查询线状图数据
	 * @param paramvo
	 * @param powmap
	 * @return
	 * @throws DZFWarpException
	 */
	private AchievementVO queryLineDate(QryParamVO paramvo, Map<Integer, String> powmap) throws DZFWarpException {
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){//按照月度查询
			return qryLineMonthData(paramvo, powmap);
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){//按照季度查询
			return qryLineSeasonData(paramvo, powmap);
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 3){//按照年度查询
			return qryLineYearData(paramvo, powmap);
		}
		return null;
	}
	
	/**
	 * 查询线状图-年度数据
	 * @param paramvo
	 * @param powmap
	 * @return
	 * @throws DZFWarpException
	 */
	private AchievementVO qryLineYearData(QryParamVO paramvo, Map<Integer, String> powmap) throws DZFWarpException {
		AchievementVO retvo = new AchievementVO();
		List<DZFDouble> first = new ArrayList<DZFDouble>();
		List<DZFDouble> second = new ArrayList<DZFDouble>();
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
		List<String> qrylist = new ArrayList<String>();
		qrylist.add(String.valueOf(byear-1));
		qrylist.addAll(showdate);
		if(qrylist != null && qrylist.size() > 0){
			String qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.deductdata, 1, 4)", qrylist.toArray(new String[0]));
			List<ContQryVO> zlist = qryPositiveData(powmap, qrysql, "SUBSTR(t.deductdata, 1, 4)");//扣款金额
			qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.dchangetime, 1, 4)", qrylist.toArray(new String[0]));
			List<ContQryVO> flist = qryNegativeData(powmap, qrysql, "SUBSTR(t.dchangetime, 1, 4)");//退款金额
			Map<String,DZFDouble> kkmap = new HashMap<String,DZFDouble>();
			Map<String,DZFDouble> jemap = new HashMap<String,DZFDouble>();
			for(ContQryVO zvo : zlist){
				kkmap.put(zvo.getVperiod(), zvo.getNdedsummny());
				jemap.put(zvo.getVperiod(), zvo.getNaccountmny());
			}
			for(ContQryVO fvo : flist){
				kkmap.put(fvo.getVperiod(), SafeCompute.add(kkmap.get(fvo.getVperiod()), fvo.getNdedsummny()));
				jemap.put(fvo.getVperiod(), SafeCompute.add(jemap.get(fvo.getVperiod()), fvo.getNaccountmny()));
			}
			DZFDouble submny = DZFDouble.ZERO_DBL;
			String preyear = String.valueOf(byear - 1);
			//如果当月金额为0且上月金额不为0，则增长率为-100；如果当月金额为0且上月金额为0，则增长率为0；如果当月金额不为0，且上月金额为0，则增长率为100；
			for(int i = 0; i < showdate.size(); i++){
				if(CommonUtil.getDZFDouble(kkmap.get(showdate.get(i))).compareTo(DZFDouble.ZERO_DBL) == 0){
					if(i == 0){
						if(CommonUtil.getDZFDouble(kkmap.get(preyear)).compareTo(DZFDouble.ZERO_DBL) == 0){
							first.add(DZFDouble.ZERO_DBL);
						}else{
							first.add(new DZFDouble(100.00).multiply(-1));
						}
					}else{
						if(CommonUtil.getDZFDouble(kkmap.get(showdate.get(i-1))).compareTo(DZFDouble.ZERO_DBL) == 0){
							first.add(DZFDouble.ZERO_DBL);
						}else{
							first.add(new DZFDouble(100.00).multiply(-1));
						}
					}
				}else{
					if(i == 0){
						if(CommonUtil.getDZFDouble(kkmap.get(preyear)).compareTo(DZFDouble.ZERO_DBL) == 0){
							first.add(new DZFDouble(100.00));
						}else{
							submny = SafeCompute.sub(kkmap.get(showdate.get(i)), kkmap.get(preyear));
							first.add(submny.div(getAbsValue(kkmap.get(preyear))).multiply(100));
						}
					}else{
						//(当月金额 - 上一月金额) / 上一月金额
						if(CommonUtil.getDZFDouble(kkmap.get(showdate.get(i-1))).compareTo(DZFDouble.ZERO_DBL) == 0){
							first.add(new DZFDouble(100.00));
						}else{
							submny = SafeCompute.sub(kkmap.get(showdate.get(i)), kkmap.get(showdate.get(i-1)));
							first.add(submny.div(getAbsValue(kkmap.get(showdate.get(i-1)))).multiply(100));
						}
					}
				}
				
				if(CommonUtil.getDZFDouble(jemap.get(showdate.get(i))).compareTo(DZFDouble.ZERO_DBL) == 0){
					if(i == 0){
						if(CommonUtil.getDZFDouble(jemap.get(preyear)).compareTo(DZFDouble.ZERO_DBL) == 0){
							second.add(DZFDouble.ZERO_DBL);
						}else{
							second.add(new DZFDouble(100.00).multiply(-1));
						}
					}else{
						if(CommonUtil.getDZFDouble(jemap.get(showdate.get(i-1))).compareTo(DZFDouble.ZERO_DBL) == 0){
							second.add(DZFDouble.ZERO_DBL);
						}else{
							second.add(new DZFDouble(100.00).multiply(-1));
						}
					}
				}else{
					if(i == 0){
						if(CommonUtil.getDZFDouble(jemap.get(preyear)).compareTo(DZFDouble.ZERO_DBL) == 0){
							second.add(new DZFDouble(100.00));
						}else{
							submny = SafeCompute.sub(jemap.get(showdate.get(i)), jemap.get(preyear));
							second.add(submny.div(getAbsValue(jemap.get(preyear))).multiply(100));
						}
					}else{
						//(当月金额 - 上一月金额) / 上一月金额
						if(CommonUtil.getDZFDouble(jemap.get(showdate.get(i-1))).compareTo(DZFDouble.ZERO_DBL) == 0){
							second.add(new DZFDouble(100.00));
						}else{
							submny = SafeCompute.sub(jemap.get(showdate.get(i)), jemap.get(showdate.get(i-1)));
							second.add(submny.div(getAbsValue(jemap.get(showdate.get(i-1)))).multiply(100));
						}
					}
				}
			}
			retvo.setShowdate(showdate);
			retvo.setFirst(first);
			retvo.setSecond(second);
		}else{
			throw new BusinessException("年度查询条件不能为空");
		}
		return retvo;
	}
	
	/**
	 * 查询线状图-季度数据
	 * @param paramvo
	 * @param powmap
	 * @return
	 * @throws DZFWarpException
	 */
	private AchievementVO qryLineSeasonData(QryParamVO paramvo, Map<Integer, String> powmap) throws DZFWarpException {
		AchievementVO retvo = new AchievementVO();
		List<DZFDouble> first = new ArrayList<DZFDouble>();
		List<DZFDouble> second = new ArrayList<DZFDouble>();
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
		String strperiod = ToolsUtil.getPreNumsMonth(paramvo.getBeginperiod(), 3);
		String preseason = ToolsUtil.getSeason(strperiod);
		List<String> qrylist = ToolsUtil.getPeriodsBp(strperiod, paramvo.getEndperiod());
		if(qrylist != null && qrylist.size() > 0){
			String qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.deductdata, 1, 7)", qrylist.toArray(new String[0]));
			List<ContQryVO> zlist = qryPositiveData(powmap, qrysql, "SUBSTR(t.deductdata, 1, 7)");//扣款金额
			qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.dchangetime, 1, 7)", qrylist.toArray(new String[0]));
			List<ContQryVO> flist = qryNegativeData(powmap, qrysql, "SUBSTR(t.dchangetime, 1, 7)");//退款金额
			Map<String,DZFDouble> kkmap = new HashMap<String,DZFDouble>();
			Map<String,DZFDouble> jemap = new HashMap<String,DZFDouble>();
			String season = "";
			for(ContQryVO zvo : zlist){
				season = ToolsUtil.getSeason(zvo.getVperiod());
				if(!kkmap.containsKey(season)){
					kkmap.put(season, zvo.getNdedsummny());
				}else{
					kkmap.put(season, SafeCompute.add(kkmap.get(season), zvo.getNdedsummny()));
				}
				if(!jemap.containsKey(season)){
					jemap.put(season, zvo.getNaccountmny());
				}else{
					jemap.put(season, SafeCompute.add(jemap.get(season), zvo.getNaccountmny()));
				}
			}
			for(ContQryVO fvo : flist){
				season = ToolsUtil.getSeason(fvo.getVperiod());
				if(!kkmap.containsKey(season)){
					kkmap.put(season, fvo.getNdedsummny());
				}else{
					kkmap.put(season, SafeCompute.add(kkmap.get(season), fvo.getNdedsummny()));
				}
				if(!jemap.containsKey(season)){
					jemap.put(season, fvo.getNaccountmny());
				}else{
					jemap.put(season, SafeCompute.add(jemap.get(season), fvo.getNaccountmny()));
				}
			}
			DZFDouble submny = DZFDouble.ZERO_DBL;
			//如果当月金额为0且上月金额不为0，则增长率为-100；如果当月金额为0且上月金额为0，则增长率为0；如果当月金额不为0，且上月金额为0，则增长率为100；
			for(int i = 0; i < showdate.size(); i++){
				if(CommonUtil.getDZFDouble(kkmap.get(showdate.get(i))).compareTo(DZFDouble.ZERO_DBL) == 0){
					if(i == 0){
						if(CommonUtil.getDZFDouble(kkmap.get(preseason)).compareTo(DZFDouble.ZERO_DBL) == 0){
							first.add(DZFDouble.ZERO_DBL);
						}else{
							first.add(new DZFDouble(100.00).multiply(-1));
						}
					}else{
						if(CommonUtil.getDZFDouble(kkmap.get(showdate.get(i-1))).compareTo(DZFDouble.ZERO_DBL) == 0){
							first.add(DZFDouble.ZERO_DBL);
						}else{
							first.add(new DZFDouble(100.00).multiply(-1));
						}
					}
				}else{
					if(i == 0){
						if(CommonUtil.getDZFDouble(kkmap.get(preseason)).compareTo(DZFDouble.ZERO_DBL) == 0){
							first.add(new DZFDouble(100.00));
						}else{
							submny = SafeCompute.sub(kkmap.get(showdate.get(i)), kkmap.get(preseason));
							first.add(submny.div(getAbsValue(kkmap.get(preseason))).multiply(100));
						}
					}else{
						//(当季度金额 - 上一季度金额) / 上一季度金额
						if(CommonUtil.getDZFDouble(kkmap.get(showdate.get(i-1))).compareTo(DZFDouble.ZERO_DBL) == 0){
							first.add(new DZFDouble(100.00));
						}else{
							submny = SafeCompute.sub(kkmap.get(showdate.get(i)), kkmap.get(showdate.get(i-1)));
							first.add(submny.div(getAbsValue(kkmap.get(showdate.get(i-1)))).multiply(100));
						}
					}
				}
				
				if(CommonUtil.getDZFDouble(jemap.get(showdate.get(i))).compareTo(DZFDouble.ZERO_DBL) == 0){
					if(i == 0){
						if(CommonUtil.getDZFDouble(jemap.get(preseason)).compareTo(DZFDouble.ZERO_DBL) == 0){
							second.add(DZFDouble.ZERO_DBL);
						}else{
							second.add(new DZFDouble(100.00).multiply(-1));
						}
					}else{
						if(CommonUtil.getDZFDouble(jemap.get(showdate.get(i-1))).compareTo(DZFDouble.ZERO_DBL) == 0){
							second.add(DZFDouble.ZERO_DBL);
						}else{
							second.add(new DZFDouble(100.00).multiply(-1));
						}
					}
				}else{
					if(i == 0){
						if(CommonUtil.getDZFDouble(jemap.get(preseason)).compareTo(DZFDouble.ZERO_DBL) == 0){
							second.add(new DZFDouble(100.00));
						}else{
							submny = SafeCompute.sub(jemap.get(showdate.get(i)), jemap.get(preseason));
							second.add(submny.div(getAbsValue(jemap.get(preseason))).multiply(100));
						}
					}else{
						//(当月金额 - 上一月金额) / 上一月金额
						if(CommonUtil.getDZFDouble(jemap.get(showdate.get(i-1))).compareTo(DZFDouble.ZERO_DBL) == 0){
							second.add(new DZFDouble(100.00));
						}else{
							submny = SafeCompute.sub(jemap.get(showdate.get(i)), jemap.get(showdate.get(i-1)));
							second.add(submny.div(getAbsValue(jemap.get(showdate.get(i-1)))).multiply(100));
						}
					}
				}
			}
			retvo.setShowdate(showdate);
			retvo.setFirst(first);
			retvo.setSecond(second);
		}else{
			throw new BusinessException("季度查询条件不能为空");
		}
		return retvo;
	}
	
	/**
	 * 查询线状图-月度数据
	 * @return
	 * @throws DZFWarpException
	 */
	private AchievementVO qryLineMonthData(QryParamVO paramvo, Map<Integer, String> powmap) throws DZFWarpException {
		AchievementVO retvo = new AchievementVO();
		List<DZFDouble> first = new ArrayList<DZFDouble>();
		List<DZFDouble> second = new ArrayList<DZFDouble>();
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
		String preperiod = ToolsUtil.getPreviousMonth(paramvo.getBeginperiod());
		List<String> qrylist = new ArrayList<String>();
		qrylist.add(preperiod);
		qrylist.addAll(showdate);
		if(qrylist != null && qrylist.size() > 0){
			String qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.deductdata, 1, 7)", qrylist.toArray(new String[0]));
			List<ContQryVO> zlist = qryPositiveData(powmap, qrysql, "SUBSTR(t.deductdata, 1, 7)");//扣款金额
			qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.dchangetime, 1, 7)", qrylist.toArray(new String[0]));
			List<ContQryVO> flist = qryNegativeData(powmap, qrysql, "SUBSTR(t.dchangetime, 1, 7)");//退款金额
			Map<String,DZFDouble> kkmap = new HashMap<String,DZFDouble>();
			Map<String,DZFDouble> jemap = new HashMap<String,DZFDouble>();
			for(ContQryVO zvo : zlist){
				kkmap.put(zvo.getVperiod(), zvo.getNdedsummny());
				jemap.put(zvo.getVperiod(), zvo.getNaccountmny());
			}
			for(ContQryVO fvo : flist){
				kkmap.put(fvo.getVperiod(), SafeCompute.add(kkmap.get(fvo.getVperiod()), fvo.getNdedsummny()));
				jemap.put(fvo.getVperiod(), SafeCompute.add(jemap.get(fvo.getVperiod()), fvo.getNaccountmny()));
			}
			DZFDouble submny = DZFDouble.ZERO_DBL;
			//如果当月金额为0且上月金额不为0，则增长率为-100；如果当月金额为0且上月金额为0，则增长率为0；如果当月金额不为0，且上月金额为0，则增长率为100；
			for(int i = 0; i < showdate.size(); i++){
				if(CommonUtil.getDZFDouble(kkmap.get(showdate.get(i))).compareTo(DZFDouble.ZERO_DBL) == 0){
					if(i == 0){
						if(CommonUtil.getDZFDouble(kkmap.get(preperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
							first.add(DZFDouble.ZERO_DBL);
						}else{
							first.add(new DZFDouble(100.00).multiply(-1));
						}
					}else{
						if(CommonUtil.getDZFDouble(kkmap.get(showdate.get(i-1))).compareTo(DZFDouble.ZERO_DBL) == 0){
							first.add(DZFDouble.ZERO_DBL);
						}else{
							first.add(new DZFDouble(100.00).multiply(-1));
						}
					}
				}else{
					if(i == 0){
						if(CommonUtil.getDZFDouble(kkmap.get(preperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
							first.add(new DZFDouble(100.00));
						}else{
							submny = SafeCompute.sub(kkmap.get(showdate.get(i)), kkmap.get(preperiod));
							first.add(submny.div(getAbsValue(kkmap.get(preperiod))).multiply(100));
						}
					}else{
						//(当月金额 - 上一月金额) / 上一月金额
						if(CommonUtil.getDZFDouble(kkmap.get(showdate.get(i-1))).compareTo(DZFDouble.ZERO_DBL) == 0){
							first.add(new DZFDouble(100.00));
						}else{
							submny = SafeCompute.sub(kkmap.get(showdate.get(i)), kkmap.get(showdate.get(i-1)));
							first.add(submny.div(getAbsValue(kkmap.get(showdate.get(i-1)))).multiply(100));
						}
					}
				}
				
				if(CommonUtil.getDZFDouble(jemap.get(showdate.get(i))).compareTo(DZFDouble.ZERO_DBL) == 0){
					if(i == 0){
						if(CommonUtil.getDZFDouble(jemap.get(preperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
							second.add(DZFDouble.ZERO_DBL);
						}else{
							second.add(new DZFDouble(100.00).multiply(-1));
						}
					}else{
						if(CommonUtil.getDZFDouble(jemap.get(showdate.get(i-1))).compareTo(DZFDouble.ZERO_DBL) == 0){
							second.add(DZFDouble.ZERO_DBL);
						}else{
							second.add(new DZFDouble(100.00).multiply(-1));
						}
					}
				}else{
					if(i == 0){
						if(CommonUtil.getDZFDouble(jemap.get(preperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
							second.add(new DZFDouble(100.00));
						}else{
							submny = SafeCompute.sub(jemap.get(showdate.get(i)), jemap.get(preperiod));
							second.add(submny.div(getAbsValue(jemap.get(preperiod))).multiply(100));
						}
					}else{
						//(当月金额 - 上一月金额) / 上一月金额
						if(CommonUtil.getDZFDouble(jemap.get(showdate.get(i-1))).compareTo(DZFDouble.ZERO_DBL) == 0){
							second.add(new DZFDouble(100.00));
						}else{
							submny = SafeCompute.sub(jemap.get(showdate.get(i)), jemap.get(showdate.get(i-1)));
							second.add(submny.div(getAbsValue(jemap.get(showdate.get(i-1)))).multiply(100));
						}
					}
				}
			}
			retvo.setShowdate(showdate);
			retvo.setFirst(first);
			retvo.setSecond(second);
		}else{
			throw new BusinessException("月度查询条件不能为空");
		}
		return retvo;
	}
	
	/**
	 * 取金额的绝对值
	 * @param mny
	 * @return
	 * @throws DZFWarpException
	 */
	private DZFDouble getAbsValue(DZFDouble mny) throws DZFWarpException {
		if(CommonUtil.getDZFDouble(mny).compareTo(DZFDouble.ZERO_DBL) == 0){
			return DZFDouble.ZERO_DBL;
		}else if(CommonUtil.getDZFDouble(mny).compareTo(DZFDouble.ZERO_DBL) > 0){
			return mny;
		}else if(CommonUtil.getDZFDouble(mny).compareTo(DZFDouble.ZERO_DBL) < 0){
			return mny.multiply(new DZFDouble(-1));
		}
		return null;
	}
	
	/**
	 * 查询扣款数据
	 * @param powmap
	 * @param qrysql
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<ContQryVO> qryPositiveData(Map<Integer, String> powmap, String qrysql, String qrtdate)
			throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT ").append(qrtdate).append(" AS vperiod,  \n");
		sql.append("       SUM(nvl(t.ndedsummny, 0)) AS ndedsummny,  \n");
		sql.append("       SUM(nvl(ct.nchangetotalmny, 0) - nvl(ct.nbookmny, 0)) AS naccountmny  \n");
		sql.append("  FROM cn_contract t  \n");
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append("  LEFT JOIN bd_account account ON t.pk_corp = account.pk_corp  \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND nvl(account.dr, 0) = 0  \n");
		if (!StringUtil.isEmpty(powmap.get(1))) {
			sql.append(" AND ").append(powmap.get(1));
		} else if (!StringUtil.isEmpty(powmap.get(2))) {
			sql.append(" AND ").append(powmap.get(2));
		} else if (!StringUtil.isEmpty(powmap.get(3))) {
			sql.append(" AND ").append(powmap.get(3));
		}
		sql.append("   AND nvl(ct.isncust, 'N') = 'N'  \n");
		sql.append("   AND t.vdeductstatus in (?, ?, ?)  \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		if (!StringUtil.isEmpty(qrysql)) {
			sql.append(" AND ").append(qrysql);
		}
		if(!StringUtil.isEmpty(filtersql)){
			sql.append(" AND ").append(filtersql);
		}
		sql.append(" GROUP BY ").append(qrtdate).append(" \n");
		sql.append(" ORDER BY ").append(qrtdate).append(" \n");
		return (List<ContQryVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ContQryVO.class));
	}

	/**
	 * 查询退款数据
	 * 
	 * @param powmap
	 * @param qrylist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private List<ContQryVO> qryNegativeData(Map<Integer, String> powmap, String qrysql, String qrtdate)
			throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT ").append(qrtdate).append(" AS vperiod,  \n");
		sql.append("       SUM(nvl(t.nsubdedsummny, 0)) AS ndedsummny,  \n");
		sql.append("       SUM(CASE t.vstatus  \n") ; 
		sql.append("             WHEN 9 THEN  \n") ; 
		sql.append("              nvl(t.nsubtotalmny, 0)  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              nvl(t.nsubtotalmny, 0) + nvl(ct.nbookmny, 0)  \n") ; 
		sql.append("           END) AS naccountmny  \n") ; 
		sql.append("  FROM cn_contract t  \n");
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append("  LEFT JOIN bd_account account ON t.pk_corp = account.pk_corp  \n");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND nvl(account.dr, 0) = 0  \n");
		if (!StringUtil.isEmpty(powmap.get(1))) {
			sql.append(" AND ").append(powmap.get(1));
		} else if (!StringUtil.isEmpty(powmap.get(2))) {
			sql.append(" AND ").append(powmap.get(2));
		} else if (!StringUtil.isEmpty(powmap.get(3))) {
			sql.append(" AND ").append(powmap.get(3));
		}
		sql.append("   AND nvl(ct.isncust, 'N') = 'N'  \n");
		sql.append("   AND t.vdeductstatus in (?, ?)  \n");
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		if (!StringUtil.isEmpty(qrysql)) {
			sql.append(" AND ").append(qrysql);
		}
		if(!StringUtil.isEmpty(filtersql)){
			sql.append(" AND ").append(filtersql);
		}
		sql.append(" GROUP BY ").append(qrtdate).append("  \n");
		sql.append(" ORDER BY ").append(qrtdate).append(" \n");
		return (List<ContQryVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ContQryVO.class));
	}

	@Override
	public AchievementVO queryChart(QryParamVO paramvo) throws DZFWarpException {
		Map<Integer, String> powmap = qryUserPower(paramvo);
		if(powmap == null || powmap.isEmpty()){
			return null;
		}
		return queryChartDate(paramvo, powmap);
	}
	
	/**
	 * 查询柱状图数据
	 * @param paramvo
	 * @param powmap
	 * @return
	 * @throws DZFWarpException
	 */
	private AchievementVO queryChartDate(QryParamVO paramvo, Map<Integer, String> powmap) throws DZFWarpException {
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 1){//按照月度查询
			return qryChartMonthData(paramvo, powmap);
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){//按照季度查询
			return qryChartSeasonData(paramvo, powmap);
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 3){//按照年度查询
			return qryChartYearData(paramvo, powmap);
		}
		return null;
	}
	
	/**
	 * 查询柱状图-年度数据
	 * @param paramvo
	 * @param powmap
	 * @return
	 * @throws DZFWarpException
	 */
	private AchievementVO qryChartYearData(QryParamVO paramvo, Map<Integer, String> powmap) throws DZFWarpException {
		if(StringUtil.isEmpty(paramvo.getVyear())){
			throw new BusinessException("年度查询条件不能为空");
		}
		AchievementVO retvo = new AchievementVO();
		List<DZFDouble> first = new ArrayList<DZFDouble>();
		List<DZFDouble> second = new ArrayList<DZFDouble>();
		Integer year = Integer.parseInt(paramvo.getVyear());
		List<String> showdate = new ArrayList<String>();
		showdate.add(paramvo.getVyear());
		List<String> qrylist = new ArrayList<String>();
		for(int i = (year-2); i <= year; i++){
			qrylist.add(String.valueOf(i));
		}
		if(qrylist != null && qrylist.size() > 0){
			Map<String, DZFDouble> mnymap = getMnyMap(paramvo, powmap, qrylist);
			DZFDouble submny = DZFDouble.ZERO_DBL;
			//如果当期金额为0且上期金额不为0，则增长率为-100；如果当期金额为0且上期金额为0，则增长率为0；如果当期金额不为0，且上期金额为0，则增长率为100
			String preoneyear = "";
			String pretwoyear = "";
			String period = "";
			for(int i = 0; i < showdate.size(); i++){
				period = showdate.get(i);
				preoneyear = String.valueOf(Integer.parseInt(period) - 1);
				pretwoyear = String.valueOf(Integer.parseInt(period) - 2);
				//当期数据
				if(CommonUtil.getDZFDouble(mnymap.get(period)).compareTo(DZFDouble.ZERO_DBL) == 0){
					if(CommonUtil.getDZFDouble(mnymap.get(preoneyear)).compareTo(DZFDouble.ZERO_DBL) == 0){
						second.add(DZFDouble.ZERO_DBL);
					}else{
						second.add(new DZFDouble(100.00).multiply(-1));
					}
				}else{
					if(CommonUtil.getDZFDouble(mnymap.get(preoneyear)).compareTo(DZFDouble.ZERO_DBL) == 0){
						second.add(new DZFDouble(100.00));
					}else{
						submny = SafeCompute.sub(mnymap.get(showdate.get(i)), mnymap.get(preoneyear));
						second.add(submny.div(getAbsValue(mnymap.get(preoneyear))).multiply(100));
					}
				}
				//往期数据
				if(CommonUtil.getDZFDouble(mnymap.get(preoneyear)).compareTo(DZFDouble.ZERO_DBL) == 0){
					if(CommonUtil.getDZFDouble(mnymap.get(pretwoyear)).compareTo(DZFDouble.ZERO_DBL) == 0){
						first.add(DZFDouble.ZERO_DBL);
					}else{
						first.add(new DZFDouble(100.00).multiply(-1));
					}
				}else{
					if(CommonUtil.getDZFDouble(mnymap.get(pretwoyear)).compareTo(DZFDouble.ZERO_DBL) == 0){
						first.add(new DZFDouble(100.00));
					}else{
						submny = SafeCompute.sub(mnymap.get(showdate.get(i)), mnymap.get(pretwoyear));
						first.add(submny.div(getAbsValue(mnymap.get(pretwoyear))).multiply(100));
					}
				}
			}
			retvo.setShowdate(showdate);
			retvo.setFirst(first);
			retvo.setSecond(second);
		}else{
			throw new BusinessException("月度查询条件不能为空");
		}
		return retvo;
	}
	
	/**
	 * 查询柱状图-季度数据
	 * @param paramvo
	 * @param powmap
	 * @return
	 * @throws DZFWarpException
	 */
	private AchievementVO qryChartSeasonData(QryParamVO paramvo, Map<Integer, String> powmap) throws DZFWarpException {
		AchievementVO retvo = new AchievementVO();
		List<DZFDouble> first = new ArrayList<DZFDouble>();
		List<DZFDouble> second = new ArrayList<DZFDouble>();
		if(paramvo.getBeginperiod().compareTo(paramvo.getEndperiod()) > 0){
			throw new BusinessException("开始月度不能大于结束月度");
		}
		List<String> showdate = ToolsUtil.getSeasonsBp(paramvo.getBeginperiod(), paramvo.getEndperiod());
		List<String> qrylist = ToolsUtil.getPeriodsBp(paramvo.getBeginperiod(), paramvo.getEndperiod());
		String preOneBegPeriod = ToolsUtil.getPreNumsYear(paramvo.getBeginperiod(), 1);//上一期期间
		String preOneEndPeriod = ToolsUtil.getPreNumsYear(paramvo.getEndperiod(), 1);
		List<String> onedate = ToolsUtil.getPeriodsBp(preOneBegPeriod, preOneEndPeriod);
		qrylist.addAll(onedate);
		String preTwoBegPeriod = ToolsUtil.getPreNumsYear(paramvo.getBeginperiod(), 2);//上两期期间
		String preTwoEndPeriod = ToolsUtil.getPreNumsYear(paramvo.getEndperiod(), 2);
		List<String> twodate = ToolsUtil.getPeriodsBp(preTwoBegPeriod, preTwoEndPeriod);
		qrylist.addAll(twodate);
		if(qrylist != null && qrylist.size() > 0){
			Map<String, DZFDouble> mnymap = getMnyMap(paramvo, powmap, qrylist);
			DZFDouble submny = DZFDouble.ZERO_DBL;
			//如果当期金额为0且上期金额不为0，则增长率为-100；如果当期金额为0且上期金额为0，则增长率为0；如果当期金额不为0，且上期金额为0，则增长率为100
			String preoneperiod = "";
			String pretwoperiod = "";
			String period = "";
			for(int i = 0; i < showdate.size(); i++){
				period = showdate.get(i);
				preoneperiod = ToolsUtil.getPreNumsYear(period, 1);
				pretwoperiod = ToolsUtil.getPreNumsYear(period, 2);
				//当期数据
				if(CommonUtil.getDZFDouble(mnymap.get(period)).compareTo(DZFDouble.ZERO_DBL) == 0){
					if(CommonUtil.getDZFDouble(mnymap.get(preoneperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
						second.add(DZFDouble.ZERO_DBL);
					}else{
						second.add(new DZFDouble(100.00).multiply(-1));
					}
				}else{
					if(CommonUtil.getDZFDouble(mnymap.get(preoneperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
						second.add(new DZFDouble(100.00));
					}else{
						submny = SafeCompute.sub(mnymap.get(showdate.get(i)), mnymap.get(preoneperiod));
						second.add(submny.div(getAbsValue(mnymap.get(preoneperiod))).multiply(100));
					}
				}
				//往期数据
				if(CommonUtil.getDZFDouble(mnymap.get(preoneperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
					if(CommonUtil.getDZFDouble(mnymap.get(pretwoperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
						first.add(DZFDouble.ZERO_DBL);
					}else{
						first.add(new DZFDouble(100.00).multiply(-1));
					}
				}else{
					if(CommonUtil.getDZFDouble(mnymap.get(pretwoperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
						first.add(new DZFDouble(100.00));
					}else{
						submny = SafeCompute.sub(mnymap.get(showdate.get(i)), mnymap.get(pretwoperiod));
						first.add(submny.div(getAbsValue(mnymap.get(pretwoperiod))).multiply(100));
					}
				}
			}
			retvo.setShowdate(showdate);
			retvo.setFirst(first);
			retvo.setSecond(second);
		}else{
			throw new BusinessException("月度查询条件不能为空");
		}
		return retvo;
	}
	
	/**
	 * 查询柱状图-月度数据
	 * @param paramvo
	 * @param powmap
	 * @return
	 * @throws DZFWarpException
	 */
	private AchievementVO qryChartMonthData(QryParamVO paramvo, Map<Integer, String> powmap) throws DZFWarpException {
		AchievementVO retvo = new AchievementVO();
		List<DZFDouble> first = new ArrayList<DZFDouble>();
		List<DZFDouble> second = new ArrayList<DZFDouble>();
		if(paramvo.getBeginperiod().compareTo(paramvo.getEndperiod()) > 0){
			throw new BusinessException("开始月度不能大于结束月度");
		}
		List<String> qrylist = new ArrayList<String>();
		List<String> showdate = ToolsUtil.getPeriodsBp(paramvo.getBeginperiod(), paramvo.getEndperiod());
		qrylist.addAll(showdate);
		String preOneBegPeriod = ToolsUtil.getPreNumsYear(paramvo.getBeginperiod(), 1);//上一期期间
		String preOneEndPeriod = ToolsUtil.getPreNumsYear(paramvo.getEndperiod(), 1);
		List<String> onedate = ToolsUtil.getPeriodsBp(preOneBegPeriod, preOneEndPeriod);
		qrylist.addAll(onedate);
		String preTwoBegPeriod = ToolsUtil.getPreNumsYear(paramvo.getBeginperiod(), 2);//上两期期间
		String preTwoEndPeriod = ToolsUtil.getPreNumsYear(paramvo.getEndperiod(), 2);
		List<String> twodate = ToolsUtil.getPeriodsBp(preTwoBegPeriod, preTwoEndPeriod);
		qrylist.addAll(twodate);
		if(qrylist != null && qrylist.size() > 0){
			Map<String, DZFDouble> mnymap = getMnyMap(paramvo, powmap, qrylist);
			DZFDouble submny = DZFDouble.ZERO_DBL;
			//如果当期金额为0且上期金额不为0，则增长率为-100；如果当期金额为0且上期金额为0，则增长率为0；如果当期金额不为0，且上期金额为0，则增长率为100
			String preoneperiod = "";
			String pretwoperiod = "";
			String period = "";
			for(int i = 0; i < showdate.size(); i++){
				period = showdate.get(i);
				preoneperiod = ToolsUtil.getPreNumsYear(period, 1);
				pretwoperiod = ToolsUtil.getPreNumsYear(period, 2);
				//当期数据
				if(CommonUtil.getDZFDouble(mnymap.get(period)).compareTo(DZFDouble.ZERO_DBL) == 0){
					if(CommonUtil.getDZFDouble(mnymap.get(preoneperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
						second.add(DZFDouble.ZERO_DBL);
					}else{
						second.add(new DZFDouble(100.00).multiply(-1));
					}
				}else{
					if(CommonUtil.getDZFDouble(mnymap.get(preoneperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
						second.add(new DZFDouble(100.00));
					}else{
						submny = SafeCompute.sub(mnymap.get(showdate.get(i)), mnymap.get(preoneperiod));
						second.add(submny.div(getAbsValue(mnymap.get(preoneperiod))).multiply(100));
					}
				}
				//往期数据
				if(CommonUtil.getDZFDouble(mnymap.get(preoneperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
					if(CommonUtil.getDZFDouble(mnymap.get(pretwoperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
						first.add(DZFDouble.ZERO_DBL);
					}else{
						first.add(new DZFDouble(100.00).multiply(-1));
					}
				}else{
					if(CommonUtil.getDZFDouble(mnymap.get(pretwoperiod)).compareTo(DZFDouble.ZERO_DBL) == 0){
						first.add(new DZFDouble(100.00));
					}else{
						submny = SafeCompute.sub(mnymap.get(showdate.get(i)), mnymap.get(pretwoperiod));
						first.add(submny.div(getAbsValue(mnymap.get(pretwoperiod))).multiply(100));
					}
				}
			}
			retvo.setShowdate(showdate);
			retvo.setFirst(first);
			retvo.setSecond(second);
		}else{
			throw new BusinessException("月度查询条件不能为空");
		}
		return retvo;
	}
	
	/**
	 * 获取柱状图-查询数据
	 * @param paramvo
	 * @param powmap
	 * @param qrylist
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, DZFDouble> getMnyMap(QryParamVO paramvo, Map<Integer, String> powmap, List<String> qrylist)
			throws DZFWarpException {
		String qrysql = "";
		List<ContQryVO> zlist = null;
		List<ContQryVO> flist = null;
		if(paramvo.getQrytype() != null && (paramvo.getQrytype() == 1 || paramvo.getQrytype() == 2)){
			qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.deductdata, 1, 7)", qrylist.toArray(new String[0]));
			zlist = qryPositiveData(powmap, qrysql, "SUBSTR(t.deductdata, 1, 7)");// 扣款金额
			qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.dchangetime, 1, 7)", qrylist.toArray(new String[0]));
			flist = qryNegativeData(powmap, qrysql, "SUBSTR(t.dchangetime, 1, 7)");// 退款金额
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 3){
			qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.deductdata, 1, 4)", qrylist.toArray(new String[0]));
			zlist = qryPositiveData(powmap, qrysql, "SUBSTR(t.deductdata, 1, 4)");// 扣款金额
			qrysql = SqlUtil.buildSqlForIn("SUBSTR(t.dchangetime, 1, 4)", qrylist.toArray(new String[0]));
			flist = qryNegativeData(powmap, qrysql, "SUBSTR(t.dchangetime, 1, 4)");// 退款金额
		}
		
		Map<String, DZFDouble> kkmap = new HashMap<String, DZFDouble>();
		Map<String, DZFDouble> jemap = new HashMap<String, DZFDouble>();
		String season = "";
		if(zlist != null && zlist.size() > 0){
			for (ContQryVO zvo : zlist) {
				if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){//按照季度查询
					season = ToolsUtil.getSeason(zvo.getVperiod());
					if(!kkmap.containsKey(season)){
						kkmap.put(season, zvo.getNdedsummny());
					}else{
						kkmap.put(season, SafeCompute.add(kkmap.get(season), zvo.getNdedsummny()));
					}
					if(!jemap.containsKey(season)){
						jemap.put(season, zvo.getNaccountmny());
					}else{
						jemap.put(season, SafeCompute.add(jemap.get(season), zvo.getNaccountmny()));
					}
				}else{
					kkmap.put(zvo.getVperiod(), zvo.getNdedsummny());
					jemap.put(zvo.getVperiod(), zvo.getNaccountmny());
				}
			}
		}
		if(flist != null && flist.size() > 0){
			for (ContQryVO fvo : flist) {
				if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){//按照季度查询
					season = ToolsUtil.getSeason(fvo.getVperiod());
					if(!kkmap.containsKey(season)){
						kkmap.put(season, fvo.getNdedsummny());
					}else{
						kkmap.put(season, SafeCompute.add(kkmap.get(season), fvo.getNdedsummny()));
					}
					if(!jemap.containsKey(season)){
						jemap.put(season, fvo.getNaccountmny());
					}else{
						jemap.put(season, SafeCompute.add(jemap.get(season), fvo.getNaccountmny()));
					}
				}else{
					kkmap.put(fvo.getVperiod(), SafeCompute.add(kkmap.get(fvo.getVperiod()), fvo.getNdedsummny()));
					jemap.put(fvo.getVperiod(), SafeCompute.add(jemap.get(fvo.getVperiod()), fvo.getNaccountmny()));
				}
			}
		}
		if(paramvo.getIpaytype() != null && paramvo.getIpaytype() == 1){
			return kkmap;
		}else if(paramvo.getIpaytype() != null && paramvo.getIpaytype() == 2){
			return jemap;
		}
		return null;
	}
	
	/**
	 * 查询用户角色权限  
	 * 1、区域总经理；2、区域经理；3、渠道经理或渠道负责人（有可能一个渠道经理是多个地区的负责人，同时是另外多个地区的非负责人）；
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<Integer, String> qryUserPower(QryParamVO paramvo) throws DZFWarpException {
		Integer level = pubser.getDataLevel(paramvo.getCuserid());
		if(paramvo.getCorptype() != null && paramvo.getCorptype() == 1){
			if(level != null && level == 1){
				return qryUserPowerOne(paramvo);
			}
		}else if(paramvo.getCorptype() != null && paramvo.getCorptype() == 2){
			if(level != null && level <= 2){
				return qryUserPowerTwo(paramvo);
			}
		}else if(paramvo.getCorptype() != null && paramvo.getCorptype() == 3){
			if(level != null && level <= 3){
				return qryUserPowerThree(paramvo);
			}
		}
		return null;
	}
	
	/**
	 * 渠道总权限查询（查询所有的加盟商）
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<Integer, String> qryUserPowerOne(QryParamVO paramvo) throws DZFWarpException {
		Map<Integer, String> pmap = new HashMap<Integer, String>();
//		StringBuffer sql = new StringBuffer();
//		SQLParameter spm = new SQLParameter();
//		// 1、区域总经理；
//		sql.append("SELECT t.pk_leaderset  \n");
//		sql.append("  FROM cn_leaderset t  \n");
//		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
//		sql.append("   AND (t.vdeptuserid = ? OR t.vcomuserid = ? OR t.vgroupuserid = ? ) \n");
//		spm.addParam(paramvo.getCuserid());
//		spm.addParam(paramvo.getCuserid());
//		spm.addParam(paramvo.getCuserid());
//		List<ManagerVO> list = (List<ManagerVO>) singleObjectBO.executeQuery(sql.toString(), spm,
//				new BeanListProcessor(ManagerVO.class));
//		if (list != null && list.size() > 0) {
//			
//		}
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT account.vprovince  \n");
		sql.append("  FROM bd_account account \n");
		sql.append(" WHERE nvl(account.dr, 0) = 0  \n");
		sql.append("   AND nvl(account.ischannel, 'N') = 'Y' \n");
		sql.append("   AND account.vprovince is not null \n");
		if(!StringUtil.isEmpty(filtersql)){
			sql.append(" AND ").append(filtersql);
		}
		List<ChnAreaBVO> clist = (List<ChnAreaBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnAreaBVO.class));
		if(clist != null && clist.size() > 0){
			List<String> pklist = new ArrayList<String>();
			String vprov = "";
			for(ChnAreaBVO bvo : clist){
				vprov = String.valueOf(bvo.getVprovince());
				if(!pklist.contains(vprov)){
					pklist.add(vprov);
				}
			}
			if(pklist != null && pklist.size() > 0){
				String corpsql = SqlUtil.buildSqlForIn("account.vprovince", pklist.toArray(new String[0]));
				pmap.put(1, corpsql);
			}
		}
		return pmap;
	}
	
	/**
	 * 大区权限查询（只查询所负责的省份）
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<Integer, String> qryUserPowerTwo(QryParamVO paramvo) throws DZFWarpException {
		Map<Integer, String> pmap = new HashMap<Integer, String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		List<String> pklist = new ArrayList<String>();
		sql.append("SELECT DISTINCT b.vprovince  \n");
		sql.append("  FROM cn_chnarea_b b  \n");
		sql.append("  LEFT JOIN cn_chnarea a ON a.pk_chnarea = b.pk_chnarea  \n");
		sql.append("  LEFT JOIN bd_account account ON b.vprovince = account.vprovince  \n");
		sql.append(" WHERE nvl(b.dr, 0) = 0  \n");
		sql.append("   AND nvl(b.type, 0) = 1  \n");
		sql.append("   AND nvl(account.ischannel, 'N') = 'Y' \n");
		if(!StringUtil.isEmpty(filtersql)){
			sql.append(" AND ").append(filtersql);
		}
		if(paramvo.getCorptype() != null && paramvo.getCorptype() == 2){
			sql.append("   AND a.userid = ? \n");
			spm.addParam(paramvo.getCuserid());
		}
		List<ChnAreaBVO> clist = (List<ChnAreaBVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnAreaBVO.class));
		if(clist != null && clist.size() > 0){
			String vprov = "";
			for(ChnAreaBVO bvo : clist){
				vprov = String.valueOf(bvo.getVprovince());
				if(!pklist.contains(vprov)){
					pklist.add(vprov);
				}
			}
		}
		if(pklist != null && pklist.size() > 0){
			String corpsql = SqlUtil.buildSqlForIn("account.vprovince", pklist.toArray(new String[0]));
			pmap.put(2, corpsql);
		}
		return pmap;
	}
	
	/**
	 * 省权限查询
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<Integer, String> qryUserPowerThree(QryParamVO paramvo) throws DZFWarpException {
		Map<Integer, String> pmap = new HashMap<Integer, String>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
//		List<String> pklist = new ArrayList<String>();
//		// 3.1、区域负责人
//		sql.append("SELECT account.pk_corp  \n");
//		sql.append("  FROM cn_chnarea_b b  \n");
//		sql.append("  LEFT JOIN bd_account account ON b.vprovince = account.vprovince  \n");
//		sql.append(" WHERE nvl(b.dr, 0) = 0  \n");
//		sql.append("   AND nvl(account.dr, 0) = 0  \n");
//		sql.append("   AND nvl(b.type, 0) = 1  \n");
//		sql.append("   AND nvl(account.ischannel, 'N') = 'Y'  \n");
//		sql.append("   AND nvl(b.ischarge, 'N') = 'Y'  \n");
//		sql.append("   AND b.userid = ? \n");
//		spm.addParam(paramvo.getCuserid());
//		if(!StringUtil.isEmpty(filtersql)){
//			sql.append(" AND ").append(filtersql);
//		}
//		List<AccountVO> clist = (List<AccountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
//				new BeanListProcessor(AccountVO.class));
//		if(clist != null && clist.size() > 0){
//			for(AccountVO acvo : clist){
//				if(!pklist.contains(acvo.getPk_corp())){
//					pklist.add(acvo.getPk_corp());
//				}
//			}
//		}
//		//3.2、其他区域非负责人
//		sql = new StringBuffer();
//		spm = new SQLParameter();
//		sql.append("SELECT b.pk_corp  \n") ;
//		sql.append("  FROM cn_chnarea_b b  \n") ; 
//		sql.append(" WHERE nvl(b.dr, 0) = 0  \n") ; 
//		sql.append("   AND nvl(b.ischarge, 'N') = 'N'  \n") ; 
//		sql.append("   AND nvl(b.type, 0) = 1  \n");
//		sql.append("   AND b.userid = ? \n");
//		spm.addParam(paramvo.getCuserid());
//		List<AccountVO> klist = (List<AccountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
//				new BeanListProcessor(AccountVO.class));
//		if(klist != null && klist.size() > 0){
//			for(AccountVO acvo : klist){
//				if(!pklist.contains(acvo.getPk_corp())){
//					pklist.add(acvo.getPk_corp());
//				}
//			}
//		}
//		if(pklist != null && pklist.size() > 0){
//			String corpsql = SqlUtil.buildSqlForIn("account.pk_corp", pklist.toArray(new String[0]));
//			pmap.put(3, corpsql);
//		}
		
		String[] pk_corps = pubser.getManagerCorp(paramvo.getCuserid(), 1);
		if(pk_corps != null && pk_corps.length > 0){
			String corpsql = SqlUtil.buildSqlForIn("account.pk_corp", pk_corps);
			pmap.put(3, corpsql);
		}
		
		return pmap;
	}

}
