package com.dzf.service.channel.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dzf.dao.bs.SingleObjectBO;
import com.dzf.dao.jdbc.framework.SQLParameter;
import com.dzf.dao.jdbc.framework.processor.BeanListProcessor;
import com.dzf.model.channel.payment.ChnBalanceRepVO;
import com.dzf.model.channel.payment.ChnDetailRepVO;
import com.dzf.model.pub.CommonUtil;
import com.dzf.model.pub.IStatusConstant;
import com.dzf.model.pub.QryParamVO;
import com.dzf.model.pub.QrySqlSpmVO;
import com.dzf.model.sys.sys_power.CorpVO;
import com.dzf.pub.DZFWarpException;
import com.dzf.pub.StringUtil;
import com.dzf.pub.cache.CorpCache;
import com.dzf.pub.lang.DZFDate;
import com.dzf.pub.lang.DZFDouble;
import com.dzf.pub.util.SafeCompute;
import com.dzf.pub.util.SqlUtil;
import com.dzf.pub.util.ToolsUtil;
import com.dzf.service.channel.IChnPayBalanceService;
import com.dzf.service.pub.IPubService;

@Service("chnpaybalanceser")
public class ChnPayBalanceServiceImpl implements IChnPayBalanceService{
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	
    @Autowired
    private IPubService pubService;
	
	@Override
	public List<ChnBalanceRepVO> query(QryParamVO paramvo) throws DZFWarpException {
		List<ChnBalanceRepVO> retlist = new ArrayList<ChnBalanceRepVO>();
		List<String> pklist = new ArrayList<String>();
		String areaname=paramvo.getAreaname();
		String condition = pubService.makeCondition(paramvo.getCuserid(),areaname);
	  	if(condition==null){
	  		return null;
    	}else if(condition.equals("flg")){
    		condition=null;
    	}
	  	paramvo.setAreaname(condition);
		Map<String, ChnBalanceRepVO> initmap = qryDataMap(paramvo, pklist, 1);// 查询期初余额
		Map<String, ChnBalanceRepVO> datamap = qryDataMap(paramvo, pklist, 2);// 查询明细金额
		if(paramvo.getQrytype() != null && (paramvo.getQrytype() == -1 	|| paramvo.getQrytype() == 2 )){//全部查询、预付款查询
			//0扣款按照预付款扣款统计
			qryNoDeductConData(paramvo, pklist);
		}
//		HashMap<String, ChnBalanceRepVO> map = new HashMap<String, ChnBalanceRepVO>();
		Map<String, ChnBalanceRepVO> posimap = null;//扣款合同信息
		Map<String, ChnBalanceRepVO> negamap = null;//退款合同信息
		ChnBalanceRepVO contvo = null;
		if (pklist != null && pklist.size() > 0) {
			//全部查询、预付款查询、返点查询
			if (paramvo.getQrytype() != null && (paramvo.getQrytype() == -1 || paramvo.getQrytype() == 2
					|| paramvo.getQrytype() == 3)) {
//				map = queryConList(paramvo, pklist);
				posimap = qryPositiveData(paramvo, pklist);
				negamap = qryNegativeData(paramvo, pklist);
			}
			ChnBalanceRepVO repvo = null;
			ChnBalanceRepVO vo = null;
			String pk_corp = "";
			CorpVO accvo = null;
			String ipaytype = null;
			Integer paytype = null;
			Map<Integer, String> areaMap = pubService.getAreaMap(areaname,3);
			for (String pk : pklist) {
				repvo = new ChnBalanceRepVO();
				ipaytype = pk.substring(pk.indexOf(",") + 1);
				pk_corp = pk.substring(0, pk.indexOf(","));
				repvo.setPk_corp(pk_corp);
				accvo = CorpCache.getInstance().get(null, pk_corp);
				if (accvo != null) {
					repvo.setCorpname(accvo.getUnitname());
					repvo.setInnercode(accvo.getInnercode());
					if(areaMap!=null && !areaMap.isEmpty()){
						repvo.setAreaname(areaMap.get(accvo.getVprovince()));
					}
				}
				if (!StringUtil.isEmpty(paramvo.getCorpname())) {
					if (repvo.getInnercode().indexOf(paramvo.getCorpname()) == -1
							&& repvo.getCorpname().indexOf(paramvo.getCorpname()) == -1) {
						continue;
					}
				}
				paytype = Integer.parseInt(ipaytype);
				repvo.setIpaytype(paytype);
				if (paytype == 1) {
					repvo.setVpaytypename("保证金");
				} else if (paytype == 2) {
					repvo.setVpaytypename("预付款");
				} else if (paytype == 3) {
					repvo.setVpaytypename("返点");
				}
//				if (map != null && !map.isEmpty() && paytype == 2 && map.containsKey(repvo.getPk_corp())) {
//					ChnBalanceRepVO balanvo = map.get(repvo.getPk_corp());
//					repvo.setNum(balanvo.getNum());
//					repvo.setNaccountmny(balanvo.getNaccountmny());
//					repvo.setNbookmny(balanvo.getNbookmny());
//				}
				//全部查询、预付款查询时，存量合同数、0扣款(非存量)合同数、非存量合同数、合同代账费、账本费显示在预付款上
				if(paramvo.getQrytype() != null && (paramvo.getQrytype() == -1 || paramvo.getQrytype() == 2)){
					if(paytype == 2){
						if(posimap != null && !posimap.isEmpty()){
							contvo = posimap.get(repvo.getPk_corp());
							if(contvo != null){
								repvo.setIcustnum(contvo.getIcustnum());
								repvo.setIzeronum(contvo.getIzeronum());
								repvo.setIdednum(contvo.getIdednum());
								repvo.setNaccountmny(contvo.getNaccountmny());
								repvo.setNbookmny(contvo.getNbookmny());
							}
						}
						if(negamap != null && !negamap.isEmpty()){
							contvo = negamap.get(repvo.getPk_corp());
							if(contvo != null){
								repvo.setIcustnum(ToolsUtil.addInteger(repvo.getIcustnum(), contvo.getIcustnum()));
								repvo.setIzeronum(ToolsUtil.addInteger(repvo.getIzeronum(), contvo.getIzeronum()));
								repvo.setIdednum(ToolsUtil.addInteger(repvo.getIdednum(), contvo.getIdednum()));
								repvo.setNaccountmny(SafeCompute.add(repvo.getNaccountmny(), contvo.getNaccountmny()));
								repvo.setNbookmny(SafeCompute.add(repvo.getNbookmny(), contvo.getNbookmny()));
							}
						}
						repvo.setNum(CommonUtil.getInteger(repvo.getIcustnum()) + CommonUtil.getInteger(repvo.getIzeronum())
								+ CommonUtil.getInteger(repvo.getIdednum()));
					}
				//返点查询时，存量合同数、0扣款(非存量)合同数、非存量合同数、合同代账费、账本费显示在返点上
				}else if(paramvo.getQrytype() != null && (paramvo.getQrytype() == 3)){
					if(paytype == 3){
						if(posimap != null && !posimap.isEmpty()){
							contvo = posimap.get(repvo.getPk_corp());
							if(contvo != null){
								repvo.setIcustnum(contvo.getIcustnum());
								repvo.setIzeronum(contvo.getIzeronum());
								repvo.setIdednum(contvo.getIdednum());
								repvo.setNaccountmny(contvo.getNaccountmny());
								repvo.setNbookmny(contvo.getNbookmny());
							}
						}
						if(negamap != null && !negamap.isEmpty()){
							contvo = negamap.get(repvo.getPk_corp());
							if(contvo != null){
								repvo.setIcustnum(ToolsUtil.addInteger(repvo.getIcustnum(), contvo.getIcustnum()));
								repvo.setIzeronum(ToolsUtil.addInteger(repvo.getIzeronum(), contvo.getIzeronum()));
								repvo.setIdednum(ToolsUtil.addInteger(repvo.getIdednum(), contvo.getIdednum()));
								repvo.setNaccountmny(SafeCompute.add(repvo.getNaccountmny(), contvo.getNaccountmny()));
								repvo.setNbookmny(SafeCompute.add(repvo.getNbookmny(), contvo.getNbookmny()));
							}
						}
						repvo.setNum(CommonUtil.getInteger(repvo.getIcustnum()) + CommonUtil.getInteger(repvo.getIzeronum())
								+ CommonUtil.getInteger(repvo.getIdednum()));
					}
				}
	
				if (initmap != null && !initmap.isEmpty()) {
					vo = initmap.get(pk);
					if (vo != null) {
						if (paytype == 1) {
							repvo.setInitbalance(vo.getBail());
						} else if (paytype == 2) {
							repvo.setInitbalance(vo.getCharge());
						} else if (paytype == 3) {
							repvo.setInitbalance(vo.getRebate());
						}
					}
				}
				if (datamap != null && !datamap.isEmpty()) {
					vo = datamap.get(pk);
					if (vo != null) {
						if (paytype == 1) {
							repvo.setNpaymny(vo.getBail());
						} else if (paytype == 2) {
							repvo.setNpaymny(vo.getNpaymny());
							repvo.setNusedmny(vo.getNusedmny());
							repvo.setIdeductpropor(vo.getIdeductpropor());
						} else if (paytype == 3) {
							repvo.setNpaymny(vo.getNpaymny());
							repvo.setNusedmny(vo.getNusedmny());
							repvo.setIdeductpropor(vo.getIdeductpropor());
						}
					}
				}
				countBalance(repvo);
				retlist.add(repvo);
			}
		}
		return retlist;
	}
	
	/**
	 * 查询零扣款合同的加盟商信息(既没有保证金，也没有预付款)
	 * @param paramvo
	 * @param pklist
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private void qryNoDeductConData(QryParamVO paramvo,List<String> pklist) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT DISTINCT t.pk_corp \n") ;
		sql.append("  FROM cn_contract t  \n") ; 
		sql.append(" LEFT JOIN bd_account ba on t.pk_corp=ba.pk_corp ");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(ba.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(t.ndedsummny, 0) = 0  \n") ; 
		sql.append("   AND t.vstatus = 1  \n") ; 
		sql.append("   AND t.pk_confrim NOT IN (SELECT l.pk_bill  \n") ; 
		sql.append("                              FROM cn_detail l  \n") ; 
		sql.append("                             WHERE nvl(l.dr, 0) = 0  \n") ; 
		sql.append("                               AND l.ipaytype = 2) \n");
		if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
	        String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
	        sql.append(" and  t.pk_corp  in (" + corpIdS + ")");
	    }
		if(!StringUtil.isEmpty(paramvo.getAreaname())){
			sql.append(paramvo.getAreaname());
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(t.deductdata,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if(!StringUtil.isEmpty(paramvo.getEndperiod())){
				sql.append(" AND substr(t.deductdata,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND t.deductdata >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append(" AND t.deductdata <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		List<ChnBalanceRepVO> list = (List<ChnBalanceRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnBalanceRepVO.class));
		if(list != null && list.size() > 0){
			String pk = "";
			for(ChnBalanceRepVO repvo : list){
				pk = repvo.getPk_corp() + ",2";
				if(!pklist.contains(pk)){
					pklist.add(pk);
				}
			}
		}
	}
	
//	/**
//	 * 查询付款单余额列表上的合同相关数据
//	 * @param paramvo
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	@SuppressWarnings("unchecked")
//	private HashMap<String,ChnBalanceRepVO> queryConList(QryParamVO paramvo,List<String> pklist) throws DZFWarpException {
//		
//		SQLParameter spm=new SQLParameter();
//		StringBuffer buf=new StringBuffer();
//		if(!StringUtil.isEmpty(paramvo.getPeriod())){
//			DZFDate start=new DZFDate(paramvo.getBeginperiod()+"-01");
//			DZFDate end=new DZFDate(paramvo.getEndperiod()+"-01");
//			Calendar cal =Calendar.getInstance();
//			cal.setTime(new Date(end.getMillis()));
//			cal.add(Calendar.MONTH, 1);
//			cal.add(Calendar.DATE, -1);
//			end=new DZFDate(cal.getTime());
//			for(int i=0;i<3*3+1;i++){
//				spm.addParam(start);
//				spm.addParam(end);
//			}
//		}else{
//			for(int i=0;i<3*3+1;i++){
//				spm.addParam(paramvo.getBegdate());
//				spm.addParam(paramvo.getEnddate());
//			}
//		}
//		
//		buf=new StringBuffer();//提单量,合同代账费,账本费
//		buf.append("  select t.pk_corp,");
//		buf.append("  sum(decode((sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
//		buf.append("  sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,1))+");
//		buf.append("  sum(decode(t.vdeductstatus,10," );
//		buf.append("  decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
//		buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,-1),");
//		buf.append("  decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
//		buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,0)))");
//		buf.append("  -sum (decode(ct.patchstatus,2,decode((sign(to_date(t.deductdata, 'yyyy-MM-dd')-to_date(?, 'yyyy-MM-dd'))*");
//		buf.append("   sign(to_date(t.deductdata, 'yyyy-MM-dd')-to_date(?, 'yyyy-MM-dd'))),1,0,1),0)");
//		buf.append("  )as num,");
//		
//		buf.append("  sum(decode((sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
//		buf.append("  sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(ct.ntotalmny,0)-nvl(ct.nbookmny,0)))+");
//		buf.append("  sum(decode(t.vdeductstatus,10," );
//		buf.append("  decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
//		buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(t.nsubtotalmny,0)+nvl(ct.nbookmny,0)),");
//		buf.append("  decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
//		buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(t.nsubtotalmny,0))");
//		buf.append("  ))as naccountmny,");
//		
//		buf.append("  sum(decode((sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
//		buf.append("  sign(to_date(t.deductdata,'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,nvl(ct.nbookmny,0)))+");
//		buf.append("  sum(decode(t.vdeductstatus,10," );
//		buf.append("  decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
//		buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,-nvl(ct.nbookmny,0)),");
//		buf.append("  decode((sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))*");
//		buf.append("  sign(to_date(substr(t.dchangetime,0,10),'yyyy-MM-dd')-to_date(?,'yyyy-MM-dd'))),1,0,0)");
//		buf.append("  ))as nbookmny");
//		
//		buf.append("  from cn_contract t INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract ");
//		buf.append("  where nvl(t.dr,0) = 0 and nvl(ct.dr,0) = 0 and (t.vdeductstatus=1 or t.vdeductstatus=9 or t.vdeductstatus=10) ");
//		if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
//	        String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
//	        buf.append(" and  t.pk_corp  in (" + corpIdS + ")");
//	    }
//		buf.append("  group by t.pk_corp");
//		List<ChnBalanceRepVO> list = (List<ChnBalanceRepVO>) singleObjectBO.executeQuery(buf.toString(), spm,
//				new BeanListProcessor(ChnBalanceRepVO.class));
//		
//		
//		HashMap<String, ChnBalanceRepVO> map = new HashMap<String, ChnBalanceRepVO>();
//		for (ChnBalanceRepVO chnBalanceRepVO : list) {
//			map.put(chnBalanceRepVO.getPk_corp(), chnBalanceRepVO);
//		}
//		return map;
//	}
	
	/**
	 * 查询扣款合同信息
	 * @param paramvo
	 * @param pklist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, ChnBalanceRepVO> qryPositiveData(QryParamVO paramvo, List<String> pklist) throws DZFWarpException {
		Map<String, ChnBalanceRepVO> posimap = new HashMap<String, ChnBalanceRepVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,  \n");
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'Y' AND nvl(ct.patchstatus, 0) != 2 THEN  \n");
		sql.append("              1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS icustnum,  \n");
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(t.ideductpropor, 0) = 0 AND  \n");
		sql.append("                  nvl(ct.patchstatus, 0) != 2 THEN  \n");
		sql.append("              1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS izeronum,  \n");
		sql.append("       SUM(CASE  \n");
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(t.ideductpropor, 0) != 0 AND  \n");
		sql.append("                  nvl(ct.patchstatus, 0) != 2 THEN  \n");
		sql.append("              1  \n");
		sql.append("             ELSE  \n");
		sql.append("              0  \n");
		sql.append("           END) AS idednum,  \n");
		sql.append("       SUM(nvl(ct.ntotalmny, 0) - nvl(ct.nbookmny, 0)) AS naccountmny,  \n");
		sql.append("       SUM(nvl(ct.nbookmny, 0)) AS nbookmny  \n");
		sql.append("  FROM cn_contract t  \n");
		sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract  \n");
		sql.append(" LEFT JOIN bd_account ba on t.pk_corp=ba.pk_corp ");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n");
		sql.append("   AND nvl(ct.dr, 0) = 0  \n");
		sql.append("   AND nvl(ba.dr, 0) = 0  \n");
		if (paramvo.getCorps() != null && paramvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
			sql.append(" and  t.pk_corp  in (" + corpIdS + ")");
		}
		if(!StringUtil.isEmpty(paramvo.getAreaname())){
			sql.append(paramvo.getAreaname());
		}
//		if(pklist != null && pklist.size() > 0){
//			String where = SqlUtil.buildSqlForIn("t.pk_corp", pklist.toArray(new String[0]));
//			sql.append(" AND ").append(where);
//		}
		if (paramvo.getQrytype() != null && paramvo.getQrytype() == 2) {//预付款扣款
			//正常和作废扣款：1、预付款扣款金额不为0；2、扣款总金额为0；
			//变更扣款：1、状态为变更，且变更后预付款扣款金额不为0；
			sql.append(" AND ( (  ( nvl(t.ndeductmny,0) != 0 OR nvl(t.ndedsummny,0) = 0 )  AND t.vstatus IN (?, ?) ) \n");
			sql.append(" OR (nvl(t.nchangededutmny,0) != 0 AND t.vstatus = ? )  )\n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 3){//返点扣款
			//正常和作废扣款：1、返点扣款金额不为0；
			//变更扣款：1、状态为变更，且变更后返点扣款金额不为0；
			sql.append(" AND (  ( nvl(t.ndedrebamny,0) != 0 AND t.vstatus IN (?, ?) )  \n");
			sql.append(" OR ( nvl(t.nchangerebatmny,0) != 0 AND t.vstatus = ? ) ) \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		}else{
			sql.append("   AND t.vstatus IN (?, ?, ?) \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}
		if (!StringUtil.isEmpty(paramvo.getPeriod())) {
			if (!StringUtil.isEmpty(paramvo.getBeginperiod())) {
				sql.append(" AND substr(t.deductdata,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if (!StringUtil.isEmpty(paramvo.getEndperiod())) {
				sql.append(" AND substr(t.deductdata,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		} else {
			if (paramvo.getBegdate() != null) {
				sql.append(" AND t.deductdata >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if (paramvo.getEnddate() != null) {
				sql.append(" AND t.deductdata <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" GROUP BY t.pk_corp \n");
		List<ChnBalanceRepVO> list = (List<ChnBalanceRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnBalanceRepVO.class));
		if(list != null && list.size() > 0){
			for(ChnBalanceRepVO repvo : list){
				posimap.put(repvo.getPk_corp(), repvo);
			}
		}
		return posimap;
	}
	
	/**
	 * 查询退款合同信息
	 * @param paramvo
	 * @param pklist
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, ChnBalanceRepVO> qryNegativeData(QryParamVO paramvo, List<String> pklist) throws DZFWarpException {
		Map<String, ChnBalanceRepVO> negamap = new HashMap<String, ChnBalanceRepVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT t.pk_corp,  \n") ;
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'Y' AND nvl(ct.patchstatus, 0) != 2 AND  \n") ; 
		sql.append("             	  t.vstatus = 10 THEN  \n") ; 
		sql.append("              -1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS icustnum,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(t.ideductpropor, 0) = 0 AND  \n") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 2 AND t.vstatus = 10 THEN  \n") ; 
		sql.append("              -1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS izeronum,  \n") ; 
		sql.append("       SUM(CASE  \n") ; 
		sql.append("             WHEN nvl(ct.isncust, 'N') = 'N' AND nvl(t.ideductpropor, 0) != 0 AND  \n") ; 
		sql.append("                  nvl(ct.patchstatus, 0) != 2 AND t.vstatus = 10 THEN  \n") ; 
		sql.append("              -1  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              0  \n") ; 
		sql.append("           END) AS idednum,  \n") ; 
		sql.append("       SUM(CASE t.vstatus  \n") ; 
		sql.append("             WHEN 9 THEN  \n") ; 
		sql.append("              nvl(t.nsubtotalmny, 0)  \n") ; 
		sql.append("             ELSE  \n") ; 
		sql.append("              nvl(t.nsubtotalmny, 0) + nvl(ct.nbookmny, 0)  \n") ; 
		sql.append("           END) AS naccountmny,  \n") ; 
		sql.append("       -abs(SUM(CASE  \n") ; 
		sql.append("                  WHEN t.vstatus = 10 THEN  \n") ; 
		sql.append("                   nvl(ct.nbookmny, 0)  \n") ; 
		sql.append("                  ELSE  \n") ; 
		sql.append("                   0  \n") ; 
		sql.append("                END)) AS nbookmny  \n") ; 
		sql.append("  FROM cn_contract t  \n") ; 
		sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract  \n") ; 
		sql.append("  LEFT JOIN bd_account ba on t.pk_corp=ba.pk_corp ");
		sql.append(" WHERE nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(ct.dr, 0) = 0  \n") ; 
		sql.append("   AND nvl(ba.dr, 0) = 0  \n") ; 
		if (paramvo.getCorps() != null && paramvo.getCorps().length > 0) {
			String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
			sql.append(" and  t.pk_corp  in (" + corpIdS + ")");
		}
		if(!StringUtil.isEmpty(paramvo.getAreaname())){
			sql.append(paramvo.getAreaname());
		}
//		if(pklist != null && pklist.size() > 0){
//			String where = SqlUtil.buildSqlForIn("t.pk_corp", pklist.toArray(new String[0]));
//			sql.append(" AND ").append(where);
//		}
		if (paramvo.getQrytype() != null && paramvo.getQrytype() == 2) {//预付款扣款
			sql.append(" AND ( (nvl(t.nchangededutmny,0) != 0 AND t.vstatus = ? )  \n");
			sql.append("  OR ( (nvl(t.ndeductmny,0) != 0 OR nvl(t.ndedsummny,0) = 0 ) AND t.vstatus = ? ) ) \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 3){//返点扣款
			sql.append(" AND ( ( nvl(t.nchangerebatmny,0) != 0 AND t.vstatus = ? )  \n");
			sql.append("  OR ( nvl(t.ndedrebamny,0) != 0 AND t.vstatus = ? ) ) \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}else{
			sql.append("   AND t.vstatus IN (?, ?) \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}
		if (!StringUtil.isEmpty(paramvo.getPeriod())) {
			if (!StringUtil.isEmpty(paramvo.getBeginperiod())) {
				sql.append(" AND substr(t.dchangetime,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if (!StringUtil.isEmpty(paramvo.getEndperiod())) {
				sql.append(" AND substr(t.dchangetime,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		} else {
			if (paramvo.getBegdate() != null) {
				sql.append(" AND substr(t.dchangetime,1,10) >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if (paramvo.getEnddate() != null) {
				sql.append(" AND substr(t.dchangetime,1,10) <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" GROUP BY t.pk_corp \n");
		List<ChnBalanceRepVO> list = (List<ChnBalanceRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnBalanceRepVO.class));
		if(list != null && list.size() > 0){
			for(ChnBalanceRepVO repvo : list){
				negamap.put(repvo.getPk_corp(), repvo);
			}
		}
		return negamap;
	}
	
	/**
	 * 计算余额
	 * @param repvo
	 */
	private void countBalance(ChnBalanceRepVO repvo){
		DZFDouble paymny = SafeCompute.add(repvo.getInitbalance(), repvo.getNpaymny());
		DZFDouble balance = SafeCompute.sub(paymny, repvo.getNusedmny());
		repvo.setNbalance(balance);
	}
	
	/**
	 * 查询金额
	 * @param paramvo
	 * @param pklist  
	 * @param qrytype  1：查询期初余额；2：查询明细金额；
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String,ChnBalanceRepVO> qryDataMap(QryParamVO paramvo, List<String> pklist, Integer qrytype) throws DZFWarpException {
		Map<String,ChnBalanceRepVO> map = new HashMap<String,ChnBalanceRepVO>();
		QrySqlSpmVO qryvo = null;
		if(qrytype == 1){
			qryvo = getInitQryParam(paramvo);
		}else if(qrytype == 2){
			qryvo = getQryParam(paramvo);
		}
		List<ChnBalanceRepVO> list = (List<ChnBalanceRepVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(ChnBalanceRepVO.class));
		if(list != null && list.size() > 0){
			String pk = "";
			for(ChnBalanceRepVO repvo : list){
				pk = repvo.getPk_corp() + "," + repvo.getIpaytype();
				if(!pklist.contains(pk)){
					pklist.add(pk);
				}
				map.put(pk, repvo);
			}
		}
		return map;
	}

	/**
	 * 获取期初数据查询条件
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getInitQryParam(QryParamVO paramvo){
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT a.pk_corp, \n") ;
		sql.append("       a.ipaytype, \n") ; 
		sql.append("       SUM(decode(a.ipaytype, 1, nvl(a.npaymny,0), 0)) AS bail, \n") ; 
		sql.append("       SUM(decode(a.ipaytype, 2, nvl(a.npaymny,0), 0)) - \n") ; 
		sql.append("       SUM(decode(a.ipaytype, 2, nvl(a.nusedmny,0), 0)) AS charge, \n") ; 
		sql.append("       SUM(decode(a.ipaytype, 3, nvl(a.npaymny,0), 0)) - \n") ; 
		sql.append("       SUM(decode(a.ipaytype, 3, nvl(a.nusedmny,0), 0)) AS rebate \n") ; 
		sql.append("  FROM cn_detail a \n") ; 
		sql.append("  LEFT JOIN bd_account ba on a.pk_corp=ba.pk_corp ");
		sql.append(" WHERE nvl(a.dr, 0) = 0 and nvl(ba.dr,0)=0 \n") ; 
		if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
	        String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
	        sql.append(" and a.pk_corp  in (" + corpIdS + ")");
	    }
		if(!StringUtil.isEmpty(paramvo.getAreaname())){
			sql.append(paramvo.getAreaname());
		}
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append(" AND a.ipaytype = ? \n");
			spm.addParam(paramvo.getQrytype());
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(a.doperatedate,1,7) < ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND a.doperatedate < ? \n");
				spm.addParam(paramvo.getBegdate());
			}
		}
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
			sql.append(" AND a.pk_corp = ? \n");
			spm.addParam(paramvo.getPk_corp());
		}
		sql.append(" GROUP BY a.pk_corp, a.ipaytype \n");
		sql.append(" ORDER BY a.pk_corp \n");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**
	 * 获取流水数据查询条件
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getQryParam(QryParamVO paramvo){
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT a.pk_corp, \n") ;
		sql.append("       a.ipaytype, \n") ; 
		sql.append("       SUM(decode(a.ipaytype, 1, nvl(a.npaymny,0), 0)) AS bail, \n") ; 
		sql.append("       SUM(decode(a.ipaytype, 2, nvl(a.npaymny,0) ,3 , nvl(a.npaymny,0),0)) AS npaymny, \n") ; 
		sql.append("       SUM(decode(a.ipaytype, 2, nvl(a.nusedmny,0),3 , nvl(a.nusedmny,0),0)) AS nusedmny, \n") ; 
		sql.append("       MIN(CASE a.ideductpropor WHEN 0 THEN NULL ELSE a.ideductpropor END) AS ideductpropor \n") ; 
		sql.append("  FROM cn_detail a \n") ; 
		sql.append("  LEFT JOIN bd_account ba on a.pk_corp=ba.pk_corp ");
		sql.append(" WHERE nvl(a.dr, 0) = 0 and nvl(ba.dr,0)=0 \n") ; 
		if( null != paramvo.getCorps() && paramvo.getCorps().length > 0){
	        String corpIdS = SqlUtil.buildSqlConditionForIn(paramvo.getCorps());
	        sql.append(" AND a.pk_corp  in (" + corpIdS + ")");
	    }
		if(!StringUtil.isEmpty(paramvo.getAreaname())){
			sql.append(paramvo.getAreaname());
		}
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append(" AND a.ipaytype = ? \n");
			spm.addParam(paramvo.getQrytype());
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(a.doperatedate,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if(!StringUtil.isEmpty(paramvo.getEndperiod())){
				sql.append(" AND substr(a.doperatedate,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND a.doperatedate >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append(" AND a.doperatedate <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" GROUP BY a.pk_corp,a.ipaytype \n");
		sql.append(" ORDER BY a.pk_corp \n");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ChnDetailRepVO> queryDetail(QryParamVO paramvo) throws DZFWarpException {
		List<ChnDetailRepVO> retlist = new ArrayList<ChnDetailRepVO>();
		List<String> pklist = new ArrayList<String>();
		Map<String, ChnBalanceRepVO> initmap = qryDataMap(paramvo, pklist, 1);
		DZFDouble coutbal = DZFDouble.ZERO_DBL;
		if (initmap != null && !initmap.isEmpty()) {
			if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){//分类查询
				ChnBalanceRepVO repvo = initmap.get(paramvo.getPk_corp() + "," + paramvo.getQrytype());
				if (repvo != null) {
					ChnDetailRepVO initvo = new ChnDetailRepVO();
					if (!StringUtil.isEmpty(paramvo.getPeriod())) {
						initvo.setDoperatedate(new DZFDate(paramvo.getBeginperiod() + "-01"));
					} else {
						initvo.setDoperatedate(paramvo.getBegdate());
					}
					initvo.setPk_corp(paramvo.getPk_corp());
					CorpVO accvo = CorpCache.getInstance().get(null, paramvo.getPk_corp());
					if (accvo != null) {
						initvo.setCorpname(accvo.getUnitname());
					}
					initvo.setVmemo("期初余额");
					if (paramvo.getQrytype() == 1) {
						initvo.setNbalance(repvo.getBail());
						coutbal = repvo.getBail();
						initvo.setVpaytypename("保证金");
					} else if (paramvo.getQrytype() == 2) {
						initvo.setNbalance(repvo.getCharge());
						coutbal = repvo.getCharge();
						initvo.setVpaytypename("预付款");
					} else if (paramvo.getQrytype() == 3) {
						initvo.setNbalance(repvo.getRebate());
						coutbal = repvo.getRebate();
						initvo.setVpaytypename("返点");
					}
					retlist.add(initvo);
				}
			}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == -1){//全部查询
				ChnDetailRepVO initvo = new ChnDetailRepVO();
				if (!StringUtil.isEmpty(paramvo.getPeriod())) {
					initvo.setDoperatedate(new DZFDate(paramvo.getBeginperiod() + "-01"));
				} else {
					initvo.setDoperatedate(paramvo.getBegdate());
				}
				initvo.setPk_corp(paramvo.getPk_corp());
				CorpVO accvo = CorpCache.getInstance().get(null, paramvo.getPk_corp());
				if (accvo != null) {
					initvo.setCorpname(accvo.getUnitname());
				}
				initvo.setVmemo("期初余额");
				ChnBalanceRepVO repvo = initmap.get(paramvo.getPk_corp() + "," + 2);//预付款期初余额
				if(repvo != null){
					initvo.setNbalance(repvo.getCharge());
					coutbal = repvo.getCharge();
				}
				repvo = initmap.get(paramvo.getPk_corp() + "," + 3);//返点期初余额
				if(repvo != null){
					initvo.setNbalance(SafeCompute.add(initvo.getNbalance(), repvo.getRebate()));
					coutbal = SafeCompute.add(coutbal,repvo.getRebate());
				}
				initvo.setVpaytypename("预付款+返点");
				retlist.add(initvo);
			}
		}

		QrySqlSpmVO sqpvo = getDetailQry(paramvo);
		List<ChnDetailRepVO> list = (List<ChnDetailRepVO>) singleObjectBO.executeQuery(sqpvo.getSql(), sqpvo.getSpm(),
				new BeanListProcessor(ChnDetailRepVO.class));
		if (paramvo.getQrytype() != null && (paramvo.getQrytype() == -1 ||paramvo.getQrytype() == 2)) {
			list = qryNoDeduction(list, paramvo);// 查询扣费为0的数据
		}
		if (list != null && list.size() > 0) {
			List<ChnDetailRepVO> oederlist = getOrderList(list);
			CorpVO accvo = null;
			DZFDouble balance = DZFDouble.ZERO_DBL;
			HashMap<String, ChnDetailRepVO> contmap = null;//合同信息
			//全部（显示在预付款上）、预付款、返点查询合同明细
			if (paramvo.getQrytype() != null && (paramvo.getQrytype() == -1 
					|| paramvo.getQrytype() == 2 || paramvo.getQrytype() == 3)) {
				contmap = queryConDetail(paramvo);
			}
			for (ChnDetailRepVO vo : oederlist) {
				accvo = CorpCache.getInstance().get(null, vo.getPk_corp());
				if (accvo != null) {
					vo.setCorpname(accvo.getUnitname());
				}
				if (vo.getIpaytype() != null) {
					switch (vo.getIpaytype()) {
					case 1:
						vo.setVpaytypename("保证金");
						break;
					case 2:
						vo.setVpaytypename("预付款");
						break;
					case 3:
						vo.setVpaytypename("返点");
						break;
					}
				}
				if (contmap != null && !contmap.isEmpty()) {
					String key = "1" + vo.getPk_bill();
					if (CommonUtil.getDZFDouble(vo.getNusedmny()).compareTo(DZFDouble.ZERO_DBL) == 0 
							&& (vo.getDr() != null && vo.getDr() == -1)) {
						key = "-1" + vo.getPk_bill();
					}
					if (CommonUtil.getDZFDouble(vo.getNusedmny()).compareTo(DZFDouble.ZERO_DBL) < 0) {
						key = "-1" + vo.getPk_bill();
					}
					if (contmap.containsKey(key)) {
						ChnDetailRepVO contvo = contmap.get(key);
						if(paramvo.getQrytype() != null && paramvo.getQrytype() == -1){//查询全部时，展示预付款和返点的合同相关金额
							if(vo.getIpaytype() == 3 && vo.getIopertype() == 2){//返点扣款或返点退款
								if(contvo.getIdeductype() != null && contvo.getIdeductype() == 3){//全部返点扣款
									vo.setNaccountmny(contvo.getNaccountmny());
									vo.setNbookmny(contvo.getNbookmny());
								}
							}else{
								vo.setNaccountmny(contvo.getNaccountmny());
								vo.setNbookmny(contvo.getNbookmny());
							}
						}else{
							vo.setNaccountmny(contvo.getNaccountmny());
							vo.setNbookmny(contvo.getNbookmny());
						}
					}
				}
				balance = SafeCompute.sub(vo.getNpaymny(), vo.getNusedmny());
				vo.setNbalance(SafeCompute.add(coutbal, balance));
				coutbal = vo.getNbalance();
				retlist.add(vo);
			}
		}
		return retlist;
	}
	
	/**
	 * 查询扣款比例为0的 cn_detail 没有统计在内
	 * @param list
	 * @param paramvo
	 * @return
	 */
	private List<ChnDetailRepVO> qryNoDeduction(List<ChnDetailRepVO> list, QryParamVO paramvo) {
		StringBuffer ids=new StringBuffer();
		for (ChnDetailRepVO ChnDetailRepVO : list) {
			ids.append("'"+ChnDetailRepVO.getPk_bill()+"',");
		}
		List<ChnDetailRepVO> vos1 = qryByDeduct(list, paramvo, ids);
		CorpVO corpvo= null;
		StringBuffer vmemo=null;
		for (ChnDetailRepVO ChnDetailRepVO : vos1) {
			corpvo = CorpCache.getInstance().get(null, ChnDetailRepVO.getPk_corp());
			vmemo=new StringBuffer();
			if(!StringUtil.isEmpty(ChnDetailRepVO.getVmemo()) &&ChnDetailRepVO.getVmemo().contains("存量客户")&& corpvo!=null){
				vmemo.append("存量客户:").append(corpvo.getUnitname()).append("、").append(ChnDetailRepVO.getVmemo().substring(5));
			}else if(corpvo!=null){
				vmemo.append(corpvo.getUnitname()).append(ChnDetailRepVO.getVmemo());
			}
			ChnDetailRepVO.setVmemo(vmemo.toString());
			ChnDetailRepVO.setDr(1);
			ChnDetailRepVO.setNusedmny(DZFDouble.ZERO_DBL);
			list.add(ChnDetailRepVO);
		}
		List<ChnDetailRepVO> vos2 = qryByChange(list, paramvo, ids);
		for (ChnDetailRepVO ChnDetailRepVO : vos2) {
			corpvo = CorpCache.getInstance().get(null, ChnDetailRepVO.getPk_corp());
			vmemo=new StringBuffer();
			if(ChnDetailRepVO.getDr() != null && ChnDetailRepVO.getDr() == 9){
				vmemo.append("合同终止：");
			}else{
				vmemo.append("合同作废：");
			}
			if(corpvo!=null){
				vmemo.append(corpvo.getUnitname()).append("、").append(ChnDetailRepVO.getVmemo());
			}
			ChnDetailRepVO.setVmemo(vmemo.toString());
			ChnDetailRepVO.setNusedmny(DZFDouble.ZERO_DBL);
			ChnDetailRepVO.setDr(-1);
			list.add(ChnDetailRepVO);
		}
		return list;
	}
	
	/**
	 * 根据变更日期，查询扣款比例为0的 cn_detail 没有统计在内
	 * @param list
	 * @param paramvo
	 * @param ids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ChnDetailRepVO> qryByChange(List<ChnDetailRepVO> list, QryParamVO paramvo, StringBuffer ids) {
		StringBuffer sql;
		SQLParameter spm;
		sql = new StringBuffer();
		spm = new SQLParameter();
		sql.append(" select t.pk_confrim as pk_bill,substr(t.dchangetime,1,10) as doperatedate,t.pk_corpk as pk_corp," );   
		sql.append(" t.vstatus as dr,2 as ipaytype,2 as iopertype,ct.vcontcode as vmemo from cn_contract t" ); 
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append(" WHERE nvl(t.dr,0) = 0 and nvl(ct.dr,0) = 0 and t.pk_corp=? and t.ideductpropor=0 and (t.vstatus=10 or t.vstatus=9) \n");
		spm.addParam(paramvo.getPk_corp());
		if(ids != null && ids.length() > 0){
			sql.append(" and t.pk_confrim not in (");
			sql.append(ids.toString().substring(0,ids.toString().length()-1));
			sql.append(" )");
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(t.dchangetime,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if(!StringUtil.isEmpty(paramvo.getEndperiod())){
				sql.append(" AND substr(t.dchangetime,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND substr(t.dchangetime,1,10) >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append(" AND substr(t.dchangetime,1,10) <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" order by t.dchangetime \n");
		return (List<ChnDetailRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnDetailRepVO.class));
	}
	
	/**
	 * 根据扣款日期，查询扣款比例为0的 cn_detail 没有统计在内
	 * @param list
	 * @param paramvo
	 * @param ids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ChnDetailRepVO> qryByDeduct(List<ChnDetailRepVO> list, QryParamVO paramvo, StringBuffer ids) {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append(" select t.pk_confrim as pk_bill,t.deductdata as doperatedate,t.pk_corpk as pk_corp," );   
		sql.append(" 2 as ipaytype,2 as iopertype," );  
		sql.append(" CONCAT(decode(nvl(ct.isncust,'N'),'Y','存量客户:',''),ct.vcontcode) as vmemo from cn_contract t" );   
		sql.append("  INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n");
		sql.append(" WHERE nvl(t.dr,0) = 0 and nvl(ct.dr,0) = 0 and t.pk_corp=? and t.ideductpropor=0  \n");
		sql.append(" and (t.vstatus=1 or t.vstatus=9 or t.vstatus=10) \n");
		spm.addParam(paramvo.getPk_corp());
		if(ids != null && ids.length() > 0){
			sql.append(" and t.pk_confrim not in (");
			sql.append(ids.toString().substring(0,ids.toString().length()-1));
			sql.append(" )");
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(t.deductdata,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if(!StringUtil.isEmpty(paramvo.getEndperiod())){
				sql.append(" AND substr(t.deductdata,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND t.deductdata >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append(" AND t.deductdata <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" order by t.deductdata \n");
		return (List<ChnDetailRepVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ChnDetailRepVO.class));
	}
	/**
	 * 查询付款单余额明细的合同相关数据
	 * @param paramvo  
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String,ChnDetailRepVO> queryConDetail(QryParamVO paramvo) throws DZFWarpException {
		DZFDate begdate = null;
		DZFDate enddate = null;
		//如果为期间查询，则把期间转换为开始日期（开始期间+'-01'），结束日期（结束期间下一个月减去1天），即开始期间月初日期、结束期间月末日期
		if (!StringUtil.isEmpty(paramvo.getPeriod())) {
			begdate = new DZFDate(paramvo.getBeginperiod() + "-01");
			enddate = new DZFDate(paramvo.getEndperiod() + "-01");
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(enddate.getMillis()));
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DATE, -1);
			enddate = new DZFDate(cal.getTime());
		} else {
			begdate = paramvo.getBegdate();
			enddate = paramvo.getEnddate();
		}
		QrySqlSpmVO qryvo = getQrySql(1, begdate, enddate, paramvo);
		List<ChnDetailRepVO> kklist = (List<ChnDetailRepVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(ChnDetailRepVO.class));
		
		qryvo = getQrySql(2, begdate, enddate, paramvo);
		List<ChnDetailRepVO> bglist = (List<ChnDetailRepVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(ChnDetailRepVO.class));
		qryvo = getQrySql(3, begdate, enddate, paramvo);
		List<ChnDetailRepVO> zflist = (List<ChnDetailRepVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(ChnDetailRepVO.class));
		
		HashMap<String, ChnDetailRepVO> map = new HashMap<String, ChnDetailRepVO>();
		for (ChnDetailRepVO ChnDetailRepVO : kklist) {
			String key = "1" + ChnDetailRepVO.getPk_bill();
			map.put(key, ChnDetailRepVO);
		}
		for (ChnDetailRepVO ChnDetailRepVO : bglist) {
			String key = "-1" + ChnDetailRepVO.getPk_bill();
			map.put(key, ChnDetailRepVO);
		}
		for (ChnDetailRepVO ChnDetailRepVO : zflist) {
			String key = "-1" + ChnDetailRepVO.getPk_bill();
			map.put(key, ChnDetailRepVO);
		}
		return map;
	}
	
	/**
	 * 获取查询语句
	 * @param qrytype  1：扣款；2：变更；3：作废；
	 * @param begdate
	 * @param enddate
	 * @param paramvo
	 * @return
	 */
	
	private QrySqlSpmVO getQrySql(int qrytype, DZFDate begdate, DZFDate enddate, QryParamVO paramvo) throws DZFWarpException{
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select t.pk_confrim as pk_bill,  \n") ;
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == -1){//查询全部时，区分该合同是否为全部返点扣款
			sql.append(" CASE WHEN nvl(t.ndeductmny,0) = 0 AND nvl(t.ndedrebamny,0) != 0  \n");
			sql.append("  THEN 3 ELSE 0 END AS ideductype, \n");
		}
		if(qrytype == 1){
			sql.append("       t.deductdata as doperatedate,  \n") ; 
			sql.append("       nvl(ct.ntotalmny, 0) - nvl(ct.nbookmny, 0) as naccountmny,  \n") ; 
			sql.append("       nvl(ct.nbookmny, 0) as nbookmny  \n") ; 
		}else if(qrytype == 2){
			sql.append("       substr(t.dchangetime, 0, 10) as doperatedate,  \n") ; 
			sql.append("       nvl(t.nsubtotalmny, 0) as naccountmny,  \n") ; 
			sql.append("       0 as nbookmny  \n") ; 
		}else if(qrytype == 3){
			sql.append("       substr(t.dchangetime, 0, 10) as doperatedate,  \n") ; 
			sql.append("       nvl(t.nsubtotalmny, 0)  + nvl(ct.nbookmny, 0) as naccountmny,  \n") ; 
			sql.append("       -abs(nvl(ct.nbookmny, 0)) as nbookmny  \n") ;
		}
		sql.append("  from cn_contract t  \n") ; 
		sql.append(" INNER JOIN ynt_contract ct ON t.pk_contract = ct.pk_contract \n") ; 
		sql.append(" where nvl(t.dr, 0) = 0  \n") ; 
		sql.append("   and nvl(ct.dr, 0) = 0  \n") ; 
		if(qrytype == 1){
			sql.append("   AND t.vstatus IN (?, ?, ?) \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_1);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}else if(qrytype == 2){
			sql.append("   AND t.vstatus = ? \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_9);
		}else if(qrytype == 3){
			sql.append("   AND t.vstatus = ? \n");
			spm.addParam(IStatusConstant.IDEDUCTSTATUS_10);
		}
		if(qrytype == 1){
			sql.append("   and t.deductdata >= ?  \n") ; 
			sql.append("   and t.deductdata <= ?  \n") ; 
		}else if(qrytype == 2 || qrytype == 3){
			sql.append("   and substr(t.dchangetime, 0, 10) >= ?  \n") ; 
			sql.append("   and substr(t.dchangetime, 0, 10) <= ?  \n") ; 
		}
		spm.addParam(begdate);
		spm.addParam(enddate);
		sql.append("   and t.pk_corp = ? \n");
		spm.addParam(paramvo.getPk_corp());
		if(paramvo.getQrytype() != null && paramvo.getQrytype() == 2){//预付款
			sql.append(" AND ( nvl(t.ndeductmny,0) != 0 OR nvl(t.ndedsummny,0) = 0 ) ");
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == 3){//返点
			sql.append(" AND nvl(t.ndedrebamny,0) != 0 ");
		}
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
	
	/**	
	 * 同一天的数据，按照收款在前，付款在后排列
	 * @param list
	 * @return
	 * @throws DZFWarpException
	 */
	private List<ChnDetailRepVO> getOrderList(List<ChnDetailRepVO> list) throws DZFWarpException{
		List<ChnDetailRepVO> relist = new ArrayList<ChnDetailRepVO> ();
		Map<DZFDate,List<ChnDetailRepVO>> map = new HashMap<DZFDate,List<ChnDetailRepVO>>();
		List<ChnDetailRepVO> newlist = null;
		List<ChnDetailRepVO> oldlist = null;
		List<DZFDate> keylist = new ArrayList<DZFDate>();
		for(ChnDetailRepVO vo : list){
			if(!map.containsKey(vo.getDoperatedate())){
				newlist = new ArrayList<ChnDetailRepVO>();
				newlist.add(vo);
				map.put(vo.getDoperatedate(), newlist);
				keylist.add(vo.getDoperatedate());
			}else{
				oldlist = map.get(vo.getDoperatedate());
				oldlist.add(vo);
				map.put(vo.getDoperatedate(), oldlist);
			}
		}
		Collections.sort(keylist, new Comparator<DZFDate>(){

			@Override
			public int compare(DZFDate o1, DZFDate o2) {
				return o1.compareTo(o2);
			}
			
		});
		for(DZFDate key : keylist){
			newlist = map.get(key);
			Collections.sort(newlist,new Comparator<ChnDetailRepVO>() {

				@Override
				public int compare(ChnDetailRepVO o1, ChnDetailRepVO o2) {
					if(o1.getIpaytype() == IStatusConstant.IPAYTYPE_3){
						return -o1.getIopertype().compareTo(o2.getIopertype());
					}else{
						return o1.getIopertype().compareTo(o2.getIopertype());
					}
				}
				
			});
			for(ChnDetailRepVO vo : newlist){
				relist.add(vo);
			}
		}
		return relist;
	}
	
	/**
	 * 获取明细查询条件
	 * @param paramvo
	 * @return
	 */
	private QrySqlSpmVO getDetailQry(QryParamVO paramvo) throws DZFWarpException{
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT * FROM cn_detail WHERE nvl(dr,0) = 0 and pk_corp=? \n");
		spm.addParam(paramvo.getPk_corp());
		if(paramvo.getQrytype() != null && paramvo.getQrytype() != -1){
			sql.append(" AND ipaytype = ? \n");
			spm.addParam(paramvo.getQrytype());
		}else if(paramvo.getQrytype() != null && paramvo.getQrytype() == -1){
			sql.append(" AND ipaytype in (2, 3) \n");
		}
		if(!StringUtil.isEmpty(paramvo.getPeriod())){
			if(!StringUtil.isEmpty(paramvo.getBeginperiod())){
				sql.append(" AND substr(doperatedate,1,7) >= ? \n");
				spm.addParam(paramvo.getBeginperiod());
			}
			if(!StringUtil.isEmpty(paramvo.getEndperiod())){
				sql.append(" AND substr(doperatedate,1,7) <= ? \n");
				spm.addParam(paramvo.getEndperiod());
			}
		}else{
			if(paramvo.getBegdate() != null){
				sql.append(" AND doperatedate >= ? \n");
				spm.addParam(paramvo.getBegdate());
			}
			if(paramvo.getEnddate() != null){
				sql.append(" AND doperatedate <= ? \n");
				spm.addParam(paramvo.getEnddate());
			}
		}
		sql.append(" order by ts asc");
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}
}
