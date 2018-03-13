package com.dzf.service.channel.report.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.report.CustCountVO;
import com.dzf.model.channel.report.CustNumMoneyRepVO;
import com.dzf.model.channel.report.FinanceDealStateRepVO;
import com.dzf.model.channel.report.ManagerVO;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.model.sys.sys_power.UserVO;
import com.dzf.pub.BusinessException;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.IDefaultValue;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.cache.UserCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.report.ICustNumMoneyRep;
import com.dzf.service.channel.report.IFinanceDealStateRep;

@Service("financedealstaterepser")
public class FinanceDealStateRepImpl implements IFinanceDealStateRep{
	
	@Autowired
	private ICustNumMoneyRep custServ;
	
    @Autowired
    private SingleObjectBO singleObjectBO;

	@Override
	public List<FinanceDealStateRepVO> query(QryParamVO paramvo) throws DZFWarpException {
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			try {
				String begindate = ToolsUtil.getMaxMonthDate(paramvo.getPeriod()+"-01");
				paramvo.setBegdate(new DZFDate(begindate));
			} catch (ParseException e) {
				throw new BusinessException("日期格式转换错误");
			}
		}
		List<FinanceDealStateRepVO> retlist = new ArrayList<FinanceDealStateRepVO>();
		List<String> corplist = new ArrayList<String>();
		List<String> countcorplist = new ArrayList<String>();
		HashMap<String, FinanceDealStateRepVO> map = queryCorps(paramvo, corplist);
		List<CustCountVO> custlist = (List<CustCountVO>) custServ.queryCustNum(paramvo,1,corplist);
		Map<String, CustNumMoneyRepVO> custmap = custServ.countCustNumByType(custlist, 1, corplist, countcorplist);
		CustNumMoneyRepVO custnumvo = null;
		FinanceDealStateRepVO retvo = null;
		Map<String, Map<String, CustCountVO>> retmap = queryVoucher(countcorplist,paramvo.getPeriod());
		Map<String, CustCountVO> voumap = null;
		CustCountVO countvo = null;
		if(corplist != null && corplist.size() > 0){
			CorpVO corpvo = null;
			UserVO uservo = null;
			for(String pk_corp : corplist){
				retvo = map.get(pk_corp);
				corpvo = CorpCache.getInstance().get(null, pk_corp);
				if(corpvo != null){
					retvo.setVcorpname(corpvo.getUnitname());
					retvo.setVprovince(corpvo.getCitycounty());
				}
				uservo=UserCache.getInstance().get(retvo.getUserid(), pk_corp);
				if(uservo!=null){
					retvo.setUsername(uservo.getUser_name());
				}
				uservo=UserCache.getInstance().get(retvo.getCuserid(), pk_corp);
				if(uservo!=null){
					retvo.setCusername(uservo.getUser_name());
				}
				custnumvo = custmap.get(pk_corp);
				if(custnumvo != null){
					retvo.setIcustsmall(custnumvo.getIstockcustsmall());
					retvo.setIcusttaxpay(custnumvo.getIstockcusttaxpay());
				}
				retvo.setIcustratesmall(getCustRate(retvo.getIcustsmall(), retvo.getIcusttaxpay()));
				retvo.setIcustratetaxpay(getCustRate(retvo.getIcusttaxpay(), retvo.getIcustsmall()));
				voumap =  retmap.get(pk_corp);
				if(voumap != null){
					countvo = voumap.get("小规模纳税人");
					if(countvo != null){
						retvo.setIvouchernummall(countvo.getNum());
					}
					countvo = voumap.get("一般纳税人");
					if(countvo != null){
						retvo.setIvouchernumtaxpay(countvo.getNum());
					}
				}
				
				retlist.add(retvo);
			}
		}
		return retlist;
	}
	
	/**
	 * 查询客户数量
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
//	@SuppressWarnings("unchecked")
//	public List<CustCountVO> queryCustNum(QryParamVO paramvo) throws DZFWarpException {
//		StringBuffer sql = new StringBuffer();
//		SQLParameter spm = new SQLParameter();
//		sql.append("SELECT p.fathercorp as pk_corp, \n");
//		sql.append("	nvl(p.chargedeptname,'小规模纳税人') as chargedeptname,  \n");
//		sql.append("	count(p.pk_corp) as num \n");
//		sql.append("  FROM bd_corp p \n");
//		sql.append("  LEFT JOIN bd_account acc ON p.fathercorp = acc.pk_corp \n");
//		sql.append(" WHERE nvl(p.dr, 0) = 0 \n");
//		sql.append("   AND nvl(acc.dr, 0) = 0 \n") ;
//		sql.append("   AND nvl(acc.ischannel, 'N') = 'Y'\n") ; 
//		sql.append("   AND nvl(p.ishasaccount,'N') = 'Y' \n");//已建账
//		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");//未封存
//		sql.append("   AND p.fathercorp NOT IN \n") ; 
//		sql.append("       (SELECT f.pk_corp \n") ; 
//		sql.append("          FROM ynt_franchisee f \n") ; 
//		sql.append("         WHERE nvl(dr, 0) = 0 \n") ; 
//		sql.append("           AND nvl(f.isreport, 'N') = 'Y') \n");
//		if(StringUtil.isEmpty(paramvo.getPeriod())){
//			sql.append("   AND SUBSTR(p.createdate, 1, 7) <= ? \n");
//			spm.addParam(paramvo.getPeriod());
//		}
//		sql.append(" GROUP BY (p.fathercorp, p.chargedeptname)");
//		return (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
//				new BeanListProcessor(CustCountVO.class));
//	}
	
	/**
	 * 客户占比计算
	 * @param num1
	 * @param num2
	 * @return
	 * @throws DZFWarpException
	 */
	private DZFDouble getCustRate(Integer num1, Integer num2) throws DZFWarpException {
		DZFDouble num3 = num1 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num1);
		DZFDouble num4 = num2 == null ? DZFDouble.ZERO_DBL : new DZFDouble(num2);
		DZFDouble num = num4.add(num3);
		return num3.div(num).multiply(100);
	}

	/**
	 * 加盟商凭证计算
	 * @param countcorplist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Map<String, CustCountVO>> queryVoucher(List<String> countcorplist,String period) throws DZFWarpException {
		Map<String, Map<String, CustCountVO>> retmap = new HashMap<String, Map<String, CustCountVO>>();
		if(countcorplist == null || countcorplist.size() == 0){
			return retmap;
		}
		Map<String, CustCountVO> voumap = null;
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT p.fathercorp as pk_corp, \n");
		sql.append("  nvl(p.chargedeptname,'小规模纳税人') AS chargedeptname,  \n");
		sql.append("  count(DISTINCT h.pk_tzpz_h) as num \n");
		sql.append("  FROM ynt_tzpz_h h \n");
		sql.append("  LEFT JOIN bd_corp p ON h.pk_corp = p.pk_corp \n");
		sql.append(" WHERE nvl(h.dr, 0) = 0 and h.period = ? \n");
		sql.append("   AND nvl(p.dr, 0) = 0 \n");
		sql.append("   AND nvl(p.isseal,'N') = 'N' \n");
		String where = SqlUtil.buildSqlForIn("p.fathercorp", countcorplist.toArray(new String[0]));
		sql.append(" AND ").append(where);
		sql.append(" GROUP BY (p.chargedeptname, p.fathercorp)");
		spm.addParam(period);
		List<CustCountVO> vouchlist = (List<CustCountVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(CustCountVO.class));
		if(vouchlist != null && vouchlist.size() > 0){
			for(CustCountVO vo : vouchlist){
				if(!StringUtil.isEmpty(vo.getPk_corp())){
					if(!retmap.containsKey(vo.getPk_corp())){
						voumap = new HashMap<String, CustCountVO>();
						if(!StringUtil.isEmpty(vo.getChargedeptname())){
							voumap.put(vo.getChargedeptname(), vo);
						}
						retmap.put(vo.getPk_corp(), voumap);
					}else{
						voumap = retmap.get(vo.getPk_corp());
						if(!StringUtil.isEmpty(vo.getChargedeptname())){
							voumap.put(vo.getChargedeptname(), vo);
						}
					}
				}
			}
		}
		return retmap;
	}
	
	public HashMap<String,FinanceDealStateRepVO> queryCorps(QryParamVO paramvo, List<String> corplist) throws DZFWarpException {
		Boolean flg=checkIsLeader(paramvo);
		List<FinanceDealStateRepVO> vos=new ArrayList<>();
		List<FinanceDealStateRepVO> other=new ArrayList<>();
		vos = qryMost(paramvo,flg);
		if(!flg){
			other =qryOther(paramvo);
		}
		if(vos!=null && vos.size()>0){
			vos.addAll(other);
		}else{
			vos=other;
		}
		HashMap<String,FinanceDealStateRepVO> map=new HashMap<>();
		for (FinanceDealStateRepVO financeDealStateRepVO : other) {
			if(!map.containsKey(financeDealStateRepVO.getPk_corp())){
				map.put(financeDealStateRepVO.getPk_corp(), financeDealStateRepVO);
				corplist.add(financeDealStateRepVO.getPk_corp());
			}
		}
		return map;
	}
	
	//查询出，三个顶级用户（全部数据进行筛选）或者是大区总经理或者是培训经理非负责人
	private List<FinanceDealStateRepVO> qryMost(QryParamVO paramvo,Boolean flg) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sql.append("select p.pk_corp ,a.areaname,a.userid,b.userid cuserid,b.vprovname,b.vprovince,p.innercode ");
		sql.append(" from bd_corp p right join cn_chnarea_b b on  p.pk_corp=b.pk_corp " );   
		sql.append(" left join cn_chnarea a on b.pk_chnarea=a.pk_chnarea " );   
		sql.append(" where nvl(b.dr,0)=0 and nvl(p.dr,0)=0 and nvl(a.dr,0)=0 and b.type=2 " );
	    sql.append(" and nvl(p.isaccountcorp,'N') = 'Y' and p.fathercorp = ? " );
	    sp.addParam(IDefaultValue.DefaultGroup);
	    if(!flg){
			sql.append("  and (a.userid=? or (b.userid=? and nvl(b.ischarge,'N')='N'))");
			sp.addParam(paramvo.getUser_name());
			sp.addParam(paramvo.getUser_name());
		}
		if (!StringUtil.isEmpty(paramvo.getAreaname())) {
			sql.append(" and a.areaname=? "); // 大区
			sp.addParam(paramvo.getAreaname());
		}
		if (paramvo.getVprovince() != null && paramvo.getVprovince() != -1) {
			sql.append(" and b.vprovince=? ");// 省市
			sp.addParam(paramvo.getVprovince());
		}
		if (!StringUtil.isEmpty(paramvo.getCuserid())) {
			sql.append(" and b.userid=? ");// 渠道经理
			sp.addParam(paramvo.getCuserid());
		}
	    List<FinanceDealStateRepVO> vos =(List<FinanceDealStateRepVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(FinanceDealStateRepVO.class));
		return vos;
	}
	//查询  负责公司为空的培训负责人，且该地区没有其它培训师负责
	private List<FinanceDealStateRepVO> qryOther(QryParamVO paramvo) {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select p.pk_corp ,a.areaname,a.userid,b.userid cuserid,b.vprovname,b.vprovince,p.innercode ");
		sql.append(" from bd_corp p right join cn_chnarea_b b on  p.vprovince=b.vprovince  " );   
		sql.append(" left join cn_chnarea a on b.pk_chnarea=a.pk_chnarea " );   
		sql.append(" where nvl(b.dr,0)=0 and nvl(p.dr,0)=0 and nvl(a.dr,0)=0 " );
	    sql.append(" and nvl(p.ischannel,'N')='Y' and nvl(p.isaccountcorp,'N') = 'Y' and p.fathercorp = ? and b.type=2 " );
	    sql.append(" and nvl(b.ischarge,'N')='Y' " );
	    sp.addParam(IDefaultValue.DefaultGroup);
	    sql.append("  and (a.userid=? or b.userid=?)");
		sp.addParam(paramvo.getUser_name());
		sp.addParam(paramvo.getUser_name());
		if (!StringUtil.isEmpty(paramvo.getAreaname())) {
			sql.append(" and a.areaname=? "); // 大区
			sp.addParam(paramvo.getAreaname());
		}
		if (paramvo.getVprovince() != null && paramvo.getVprovince() != -1) {
			sql.append(" and b.vprovince=? ");// 省市
			sp.addParam(paramvo.getVprovince());
		}
		if (!StringUtil.isEmpty(paramvo.getCuserid())) {
			sql.append(" and b.userid=? ");// 渠道经理
			sp.addParam(paramvo.getCuserid());
		}
	    List<FinanceDealStateRepVO> list =(List<FinanceDealStateRepVO>) singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(FinanceDealStateRepVO.class));
	    return list;
	}
	
	private boolean checkIsLeader(QryParamVO paramvo) {
		String sql="select vdeptuserid corpcode,vcomuserid corpname,vgroupuserid pk_corpk from cn_leaderset where nvl(dr,0)=0";
		List<QryParamVO> list =(List<QryParamVO>)singleObjectBO.executeQuery(sql, null, new BeanListProcessor(QryParamVO.class));
		if(list!=null&&list.size()>0){
			QryParamVO vo=list.get(0);
			if(paramvo.getUser_name().equals(vo.getCorpcode())||paramvo.getUser_name().equals(vo.getCorpname())||paramvo.getUser_name().equals(vo.getPk_corpk())){
				return true;
			}
		}
		return false;
	}
	
}
